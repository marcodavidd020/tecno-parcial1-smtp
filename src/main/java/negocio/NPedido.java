package negocio;

import data.DPedido;
import data.DDireccion;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Clase de lógica de negocio para pedido
 */
public class NPedido {
    
    private DPedido dPedido;
    private DDireccion dDireccion;
    
    public NPedido() {
        this.dPedido = new DPedido();
        this.dDireccion = new DDireccion();
    }
    
    /**
     * Obtiene todos los pedidos
     */
    public List<String[]> getAll() throws SQLException {
        return dPedido.getAll();
    }
    
    /**
     * Obtiene un pedido por ID
     */
    public List<String[]> getById(int id) throws SQLException {
        return dPedido.getById(id);
    }
    
    /**
     * Obtiene pedidos por estado
     */
    public List<String[]> getByEstado(String estado) throws SQLException {
        return dPedido.getByEstado(estado);
    }
    
    /**
     * Obtiene pedidos por cliente (email)
     */
    public List<String[]> getByClienteEmail(String email) throws SQLException {
        return dPedido.getByClienteEmail(email);
    }
    
    /**
     * Crea un nuevo pedido con dirección
     */
    public List<String[]> crearPedido(String nombreDireccion, Double longitud, Double latitud, String referencia, double total) throws SQLException {
        // 1. Crear la dirección
        List<String[]> direccion = dDireccion.save(nombreDireccion, longitud, latitud, referencia);
        if (direccion.isEmpty()) {
            throw new SQLException("Error al crear la dirección");
        }
        
        int direccionId = Integer.parseInt(direccion.get(0)[0]);
        
        // 2. Crear el pedido
        String fecha = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        return dPedido.save(direccionId, fecha, total, "pendiente");
    }
    
    /**
     * Crea un pedido usando una dirección existente
     */
    public List<String[]> crearPedidoConDireccionExistente(int direccionId, double total) throws SQLException {
        String fecha = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        return dPedido.save(direccionId, fecha, total, "pendiente");
    }
    
    /**
     * Crea un pedido desde una URL de Google Maps
     */
    public List<String[]> crearPedidoDesdeGoogleMaps(String nombreDireccion, String urlGoogleMaps, String referencia, double total) throws SQLException {
        // Extraer coordenadas de la URL
        double[] coordenadas = DDireccion.extraerCoordenadasDeUrl(urlGoogleMaps);
        if (coordenadas == null) {
            throw new SQLException("No se pudieron extraer coordenadas de la URL de Google Maps");
        }
        
        double latitud = coordenadas[0];
        double longitud = coordenadas[1];
        
        return crearPedido(nombreDireccion, longitud, latitud, referencia, total);
    }
    
    /**
     * Guarda un nuevo pedido
     */
    public List<String[]> save(int direccionId, String fecha, double total, String estado) throws SQLException {
        return dPedido.save(direccionId, fecha, total, estado);
    }
    
    /**
     * Actualiza el estado de un pedido
     */
    public List<String[]> updateEstado(int id, String estado) throws SQLException {
        return dPedido.updateEstado(id, estado);
    }
    
    /**
     * Elimina un pedido
     */
    public boolean delete(int id) throws SQLException {
        return dPedido.delete(id);
    }
    
    /**
     * Obtiene estadísticas de pedidos
     */
    public List<String[]> getEstadisticasPedidos() throws SQLException {
        return dPedido.getEstadisticasPedidos();
    }
    
    /**
     * Obtiene pedidos pendientes de envío
     */
    public List<String[]> getPedidosPendientesEnvio() throws SQLException {
        return dPedido.getPedidosPendientesEnvio();
    }
    
    /**
     * Procesa un pedido (cambia estado a "procesando")
     */
    public List<String[]> procesarPedido(int pedidoId) throws SQLException {
        return dPedido.updateEstado(pedidoId, "procesando");
    }
    
    /**
     * Envía un pedido (cambia estado a "enviado")
     */
    public List<String[]> enviarPedido(int pedidoId) throws SQLException {
        return dPedido.updateEstado(pedidoId, "enviado");
    }
    
    /**
     * Entrega un pedido (cambia estado a "entregado")
     */
    public List<String[]> entregarPedido(int pedidoId) throws SQLException {
        return dPedido.updateEstado(pedidoId, "entregado");
    }
    
    /**
     * Cancela un pedido (cambia estado a "cancelado")
     */
    public List<String[]> cancelarPedido(int pedidoId) throws SQLException {
        return dPedido.updateEstado(pedidoId, "cancelado");
    }
    
    /**
     * Obtiene pedidos pendientes
     */
    public List<String[]> getPedidosPendientes() throws SQLException {
        return dPedido.getByEstado("pendiente");
    }
    
    /**
     * Obtiene pedidos procesando
     */
    public List<String[]> getPedidosProcesando() throws SQLException {
        return dPedido.getByEstado("procesando");
    }
    
    /**
     * Obtiene pedidos enviados
     */
    public List<String[]> getPedidosEnviados() throws SQLException {
        return dPedido.getByEstado("enviado");
    }
    
    /**
     * Obtiene pedidos entregados
     */
    public List<String[]> getPedidosEntregados() throws SQLException {
        return dPedido.getByEstado("entregado");
    }
    
    /**
     * Obtiene pedidos cancelados
     */
    public List<String[]> getPedidosCancelados() throws SQLException {
        return dPedido.getByEstado("cancelado");
    }
    
    /**
     * Genera URL de Google Maps para un pedido
     */
    public String generarUrlGoogleMaps(int pedidoId) throws SQLException {
        List<String[]> pedido = dPedido.getById(pedidoId);
        if (pedido.isEmpty()) {
            return null;
        }
        
        String[] datos = pedido.get(0);
        int direccionId = Integer.parseInt(datos[1]); // direccion_id
        
        return dDireccion.generarUrlGoogleMaps(direccionId);
    }
    
    /**
     * Obtiene pedidos cercanos a unas coordenadas
     */
    public List<String[]> getPedidosCercanos(double latitud, double longitud, double radioKm) throws SQLException {
        return dPedido.getPedidosPendientesEnvio(); // Por simplicidad, retornamos todos los pendientes
        // En una implementación completa, filtraríamos por distancia
    }
    
    public void disconnect() {
        dPedido.disconnect();
        dDireccion.disconnect();
    }
} 