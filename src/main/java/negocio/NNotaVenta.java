package negocio;

import data.DNotaVenta;
import data.DDetalleVenta;
import data.DCarrito;
import data.DDetalleCarrito;
import data.DCliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

/**
 * Clase de lógica de negocio para nota_venta
 */
public class NNotaVenta {
    
    private DNotaVenta dNotaVenta;
    private DDetalleVenta dDetalleVenta;
    private DCarrito dCarrito;
    private DDetalleCarrito dDetalleCarrito;
    private DCliente dCliente;
    
    public NNotaVenta() {
        this.dNotaVenta = new DNotaVenta();
        this.dDetalleVenta = new DDetalleVenta();
        this.dCarrito = new DCarrito();
        this.dDetalleCarrito = new DDetalleCarrito();
        this.dCliente = new DCliente();
    }
    
    /**
     * Obtiene todas las notas de venta
     */
    public List<String[]> getAll() throws SQLException {
        return dNotaVenta.getAll();
    }
    
    /**
     * Obtiene una nota de venta por ID
     */
    public List<String[]> getById(int id) throws SQLException {
        return dNotaVenta.getById(id);
    }
    
    /**
     * Obtiene notas de venta por cliente
     */
    public List<String[]> getByClienteId(int clienteId) throws SQLException {
        return dNotaVenta.getByClienteId(clienteId);
    }
    
    /**
     * Obtiene notas de venta por email del cliente
     */
    public List<String[]> getByClienteEmail(String email) throws SQLException {
        // Primero obtener el cliente por email
        List<String[]> clienteData = dCliente.getByUserId(getUserIdByEmail(email));
        if (clienteData.isEmpty()) {
            return new ArrayList<>();
        }
        
        int clienteId = Integer.parseInt(clienteData.get(0)[0]);
        return dNotaVenta.getByClienteId(clienteId);
    }
    
    /**
     * Crea una nota de venta desde el carrito
     */
    public List<String[]> crearNotaVentaDesdeCarrito(String email, Integer pedidoId, String observaciones) throws SQLException {
        // 1. Obtener el cliente por email
        List<String[]> clienteData = dCliente.getByUserId(getUserIdByEmail(email));
        if (clienteData.isEmpty()) {
            throw new SQLException("Cliente no encontrado para el email: " + email);
        }
        
        int clienteId = Integer.parseInt(clienteData.get(0)[0]);
        
        // 2. Obtener el carrito activo del cliente
        List<String[]> carritoData = dCarrito.getCarritoActivo(clienteId);
        if (carritoData.isEmpty()) {
            throw new SQLException("No hay carrito activo para el cliente");
        }
        
        int carritoId = Integer.parseInt(carritoData.get(0)[0]);
        System.out.println("🔍 DEBUG NOTA VENTA: Usando carrito ID: " + carritoId + " para cliente: " + clienteId);
        
        // 3. Obtener los detalles del carrito
        List<String[]> detallesCarrito = dDetalleCarrito.getByCarritoId(carritoId);
        System.out.println("🔍 DEBUG NOTA VENTA: Detalles del carrito encontrados: " + detallesCarrito.size());
        
        if (detallesCarrito.isEmpty()) {
            throw new SQLException("El carrito está vacío");
        }
        
        // 4. Calcular el total
        double total = 0.0;
        for (String[] detalle : detallesCarrito) {
            double subtotal = Double.parseDouble(detalle[5]); // subtotal (precio_unitario × cantidad)
            System.out.println("🔍 DEBUG NOTA VENTA: Producto " + detalle[6] + 
                " - Cantidad: " + detalle[3] + 
                " - Precio Unit: " + detalle[4] + 
                " - Subtotal: " + detalle[5]);
            total += subtotal;
        }
        System.out.println("🔍 DEBUG NOTA VENTA: Total calculado: " + total);
        
        // 5. Crear la nota de venta
        String fecha = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        List<String[]> notaVenta = dNotaVenta.save(clienteId, pedidoId, fecha, total, "pendiente", observaciones);
        
        if (notaVenta.isEmpty()) {
            throw new SQLException("Error al crear la nota de venta");
        }
        
        int notaVentaId = Integer.parseInt(notaVenta.get(0)[0]);
        
        // 6. Crear los detalles de venta
        for (String[] detalle : detallesCarrito) {
            int productoAlmacenId = Integer.parseInt(detalle[2]);
            int cantidad = Integer.parseInt(detalle[3]);
            double precioTotal = Double.parseDouble(detalle[5]); // subtotal correcto
            
            dDetalleVenta.save(notaVentaId, productoAlmacenId, cantidad, precioTotal);
        }
        
        // 7. Actualizar el estado del carrito a "procesado"
        dCarrito.updateEstado(carritoId, "procesado");
        
        return notaVenta;
    }
    
    /**
     * Guarda una nueva nota de venta
     */
    public List<String[]> save(int clienteId, Integer pedidoId, String fecha, double total, String estado, String observaciones) throws SQLException {
        return dNotaVenta.save(clienteId, pedidoId, fecha, total, estado, observaciones);
    }
    
    /**
     * Actualiza una nota de venta
     */
    public List<String[]> update(int id, String estado, String observaciones) throws SQLException {
        return dNotaVenta.update(id, estado, observaciones);
    }
    
    /**
     * Elimina una nota de venta
     */
    public boolean delete(int id) throws SQLException {
        return dNotaVenta.delete(id);
    }
    
    /**
     * Obtiene el total de ventas por cliente
     */
    public double getTotalVentasByCliente(int clienteId) throws SQLException {
        return dNotaVenta.getTotalVentasByCliente(clienteId);
    }
    
    /**
     * Obtiene estadísticas de ventas
     */
    public List<String[]> getEstadisticasVentas() throws SQLException {
        return dNotaVenta.getEstadisticasVentas();
    }
    
    /**
     * Obtiene una nota de venta completa con detalles
     */
    public List<String[]> getNotaVentaCompleta(int notaVentaId) throws SQLException {
        List<String[]> notaVenta = dNotaVenta.getById(notaVentaId);
        if (notaVenta.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Agregar los detalles de venta
        List<String[]> detallesVenta = dDetalleVenta.getByNotaVentaId(notaVentaId);
        
        // Combinar la información
        List<String[]> resultado = new ArrayList<>();
        resultado.addAll(notaVenta);
        
        // Agregar los detalles como filas adicionales
        for (String[] detalle : detallesVenta) {
            resultado.add(detalle);
        }
        
        return resultado;
    }
    
    /**
     * Obtiene el ID del usuario por email
     */
    private int getUserIdByEmail(String email) throws SQLException {
        // Esta función debería estar en NUsuario, pero la implementamos aquí por simplicidad
        // En una implementación real, deberías inyectar NUsuario como dependencia
        String query = "SELECT id FROM \"user\" WHERE email = ?";
        SqlConnection sqlConnection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
        
        try (Connection connection = sqlConnection.connect();
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id");
            }
            
            throw new SQLException("Usuario no encontrado para el email: " + email);
        }
    }
    
    /**
     * Procesa el pago de una nota de venta
     */
    public List<String[]> procesarPago(int notaVentaId) throws SQLException {
        // Actualizar el estado a "completada"
        return dNotaVenta.update(notaVentaId, "completada", "Pago procesado exitosamente");
    }
    
    /**
     * Cancela una nota de venta
     */
    public List<String[]> cancelarNotaVenta(int notaVentaId) throws SQLException {
        // Actualizar el estado a "cancelada"
        return dNotaVenta.update(notaVentaId, "cancelada", "Nota de venta cancelada");
    }
    
    /**
     * Obtiene notas de venta por estado
     */
    public List<String[]> getByEstado(String estado) throws SQLException {
        List<String[]> todasLasVentas = dNotaVenta.getAll();
        List<String[]> ventasFiltradas = new ArrayList<>();
        
        for (String[] venta : todasLasVentas) {
            if (venta[5].equals(estado)) { // índice 5 es el estado
                ventasFiltradas.add(venta);
            }
        }
        
        return ventasFiltradas;
    }
    
    /**
     * Obtiene ventas pendientes de pago
     */
    public List<String[]> getVentasPendientes() throws SQLException {
        return getByEstado("pendiente");
    }
    
    /**
     * Obtiene ventas completadas
     */
    public List<String[]> getVentasCompletadas() throws SQLException {
        return getByEstado("completada");
    }
    
    /**
     * Obtiene ventas canceladas
     */
    public List<String[]> getVentasCanceladas() throws SQLException {
        return getByEstado("cancelada");
    }
    
    public void disconnect() {
        dNotaVenta.disconnect();
        dDetalleVenta.disconnect();
        dCarrito.disconnect();
        dDetalleCarrito.disconnect();
        dCliente.disconnect();
    }
} 