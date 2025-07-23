package negocio;

import data.DCarrito;
import data.DDetalleCarrito;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase de negocio para la gestión de carritos
 */
public class NCarrito {
    
    private final DCarrito dCarrito;
    private final DDetalleCarrito dDetalleCarrito;
    
    public NCarrito() {
        this.dCarrito = new DCarrito();
        this.dDetalleCarrito = new DDetalleCarrito();
    }
    
    /**
     * Obtiene el carrito activo del cliente con lógica inteligente
     */
    public List<String[]> getCarritoActivo(int clienteId) throws SQLException {
        return dCarrito.getCarritoActivo(clienteId);
    }
    
    /**
     * Obtiene un carrito por ID con información del cliente
     */
    public List<String[]> getById(int id) throws SQLException {
        return dCarrito.getById(id);
    }
    
    /**
     * Obtiene todos los carritos de un cliente
     */
    public List<String[]> getByClienteId(int clienteId) throws SQLException {
        return dCarrito.getByClienteId(clienteId);
    }
    
    /**
     * Actualiza el total del carrito
     */
    public List<String[]> updateTotal(int carritoId, double total) throws SQLException {
        return dCarrito.updateTotal(carritoId, total);
    }
    
    /**
     * Cambia el estado del carrito
     */
    public List<String[]> updateEstado(int carritoId, String estado) throws SQLException {
        return dCarrito.updateEstado(carritoId, estado);
    }
    
    /**
     * Cambia el estado del carrito (método simplificado que retorna boolean)
     */
    public boolean cambiarEstadoCarrito(int carritoId, String estado) {
        try {
            List<String[]> resultado = dCarrito.updateEstado(carritoId, estado);
            return !resultado.isEmpty();
        } catch (SQLException e) {
            System.err.println("Error cambiando estado del carrito: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Elimina un carrito por ID
     */
    public boolean delete(int id) throws SQLException {
        return dCarrito.delete(id);
    }
    
    /**
     * Verifica si existe un carrito por ID
     */
    public boolean existsById(int id) throws SQLException {
        return dCarrito.existsById(id);
    }
    
    /**
     * Obtiene los detalles de un carrito con información de productos
     */
    public List<String[]> getDetallesCarrito(int carritoId) throws SQLException {
        return dDetalleCarrito.getByCarritoId(carritoId);
    }
    
    /**
     * Agrega un producto al carrito usando producto_id y cantidad
     */
    public List<String[]> agregarProducto(int carritoId, int productoId, int cantidad) throws SQLException {
        // Primero obtener el producto_almacen_id y precio del producto
        List<String[]> productoInfo = dDetalleCarrito.getProductoInfo(productoId);
        
        if (productoInfo.isEmpty()) {
            throw new SQLException("Producto no encontrado con ID: " + productoId);
        }
        
        int productoAlmacenId = Integer.parseInt(productoInfo.get(0)[0]);
        double precioUnitario = Double.parseDouble(productoInfo.get(0)[1]);
        
        List<String[]> resultado = dDetalleCarrito.save(carritoId, productoAlmacenId, cantidad, precioUnitario);
        
        // Actualizar el total del carrito
        double nuevoTotal = dDetalleCarrito.calcularTotalCarrito(carritoId);
        dCarrito.updateTotal(carritoId, nuevoTotal);
        
        return resultado;
    }
    
    /**
     * Actualiza la cantidad de un producto en el carrito
     */
    public List<String[]> actualizarCantidad(int detalleId, int cantidad) throws SQLException {
        List<String[]> resultado = dDetalleCarrito.updateCantidad(detalleId, cantidad);
        
        // Obtener el carrito_id del detalle
        if (!resultado.isEmpty()) {
            int carritoId = Integer.parseInt(resultado.get(0)[1]);
            double nuevoTotal = dDetalleCarrito.calcularTotalCarrito(carritoId);
            dCarrito.updateTotal(carritoId, nuevoTotal);
        }
        
        return resultado;
    }
    
    /**
     * Elimina un producto del carrito
     */
    public boolean eliminarProducto(int detalleId) throws SQLException {
        // Obtener el carrito_id antes de eliminar
        List<String[]> detalle = dDetalleCarrito.getById(detalleId);
        int carritoId = 0;
        
        if (!detalle.isEmpty()) {
            carritoId = Integer.parseInt(detalle.get(0)[1]);
        }
        
        boolean resultado = dDetalleCarrito.delete(detalleId);
        
        // Actualizar el total del carrito
        if (resultado && carritoId > 0) {
            double nuevoTotal = dDetalleCarrito.calcularTotalCarrito(carritoId);
            dCarrito.updateTotal(carritoId, nuevoTotal);
        }
        
        return resultado;
    }
    
    /**
     * Vacía un carrito (elimina todos los productos)
     */
    public boolean vaciarCarrito(int carritoId) throws SQLException {
        boolean resultado = dDetalleCarrito.deleteByCarritoId(carritoId);
        
        // Actualizar el total del carrito a 0
        if (resultado) {
            dCarrito.updateTotal(carritoId, 0.0);
        }
        
        return resultado;
    }
    
    /**
     * Calcula el total de un carrito
     */
    public double calcularTotalCarrito(int carritoId) throws SQLException {
        return dDetalleCarrito.calcularTotalCarrito(carritoId);
    }
    
    public void disconnect() {
        dCarrito.disconnect();
        dDetalleCarrito.disconnect();
    }
} 