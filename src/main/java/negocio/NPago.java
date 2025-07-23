package negocio;

import data.DPago;
import data.DNotaVenta;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de lógica de negocio para pago
 */
public class NPago {
    
    private DPago dPago;
    private DNotaVenta dNotaVenta;
    
    public NPago() {
        this.dPago = new DPago();
        this.dNotaVenta = new DNotaVenta();
    }
    
    /**
     * Obtiene todos los pagos
     */
    public List<String[]> getAll() throws SQLException {
        return dPago.getAll();
    }
    
    /**
     * Obtiene un pago por ID
     */
    public List<String[]> getById(int id) throws SQLException {
        return dPago.getById(id);
    }
    
    /**
     * Obtiene pagos por nota de venta
     */
    public List<String[]> getByNotaVentaId(int notaVentaId) throws SQLException {
        return dPago.getByNotaVentaId(notaVentaId);
    }
    
    /**
     * Guarda un nuevo pago
     */
    public List<String[]> save(int notaVentaId, double monto, String metodoPago, String estado, String referencia) throws SQLException {
        return dPago.save(notaVentaId, monto, metodoPago, estado, referencia);
    }
    
    /**
     * Actualiza el estado de un pago
     */
    public List<String[]> updateEstado(int id, String estado) throws SQLException {
        return dPago.updateEstado(id, estado);
    }
    
    /**
     * Elimina un pago
     */
    public boolean delete(int id) throws SQLException {
        return dPago.delete(id);
    }
    
    /**
     * Obtiene estadísticas de pagos
     */
    public List<String[]> getEstadisticasPagos() throws SQLException {
        return dPago.getEstadisticasPagos();
    }
    
    /**
     * Obtiene pagos por método de pago
     */
    public List<String[]> getPagosPorMetodo() throws SQLException {
        return dPago.getPagosPorMetodo();
    }
    
    /**
     * Procesa un pago y actualiza el estado de la nota de venta
     */
    public List<String[]> procesarPagoCompleto(int notaVentaId, double monto, String metodoPago, String referencia) throws SQLException {
        // 1. Validar que la nota de venta existe y está pendiente
        List<String[]> notaVenta = dNotaVenta.getById(notaVentaId);
        if (notaVenta.isEmpty()) {
            throw new SQLException("La nota de venta no existe");
        }
        
        String estadoNota = notaVenta.get(0)[5]; // índice 5 es el estado
        if (!"pendiente".equals(estadoNota)) {
            throw new SQLException("La nota de venta no está pendiente de pago");
        }
        
        // 2. Validar que el monto coincida con el total de la nota de venta
        double totalNota = Double.parseDouble(notaVenta.get(0)[4]); // índice 4 es el total
        System.out.println("🔍 DEBUG PAGO: Monto recibido: " + monto + ", Total nota venta: " + totalNota + ", Diferencia: " + Math.abs(monto - totalNota));
        
        if (Math.abs(monto - totalNota) > 0.01) { // Tolerancia de 1 centavo
            throw new SQLException("El monto del pago no coincide con el total de la nota de venta. Monto: " + monto + ", Total: " + totalNota);
        }
        
        // 3. Procesar el pago
        List<String[]> pago = dPago.procesarPago(notaVentaId, monto, metodoPago, referencia);
        
        // 4. Actualizar el estado de la nota de venta a "completada"
        dNotaVenta.update(notaVentaId, "completada", "Pago procesado exitosamente");
        
        return pago;
    }
    
    /**
     * Obtiene pagos completados
     */
    public List<String[]> getPagosCompletados() throws SQLException {
        List<String[]> todosLosPagos = dPago.getAll();
        List<String[]> pagosCompletados = new ArrayList<>();
        
        for (String[] pago : todosLosPagos) {
            if ("pagado".equals(pago[4])) { // índice 4 es el estado
                pagosCompletados.add(pago);
            }
        }
        
        return pagosCompletados;
    }
    
    /**
     * Obtiene pagos pendientes
     */
    public List<String[]> getPagosPendientes() throws SQLException {
        List<String[]> todosLosPagos = dPago.getAll();
        List<String[]> pagosPendientes = new ArrayList<>();
        
        for (String[] pago : todosLosPagos) {
            if ("pendiente".equals(pago[4])) { // índice 4 es el estado
                pagosPendientes.add(pago);
            }
        }
        
        return pagosPendientes;
    }
    
    /**
     * Obtiene pagos fallidos
     */
    public List<String[]> getPagosFallidos() throws SQLException {
        List<String[]> todosLosPagos = dPago.getAll();
        List<String[]> pagosFallidos = new ArrayList<>();
        
        for (String[] pago : todosLosPagos) {
            if ("fallido".equals(pago[4])) { // índice 4 es el estado
                pagosFallidos.add(pago);
            }
        }
        
        return pagosFallidos;
    }
    
    /**
     * Valida métodos de pago soportados
     */
    public static boolean esMetodoPagoValido(String metodoPago) {
        if (metodoPago == null || metodoPago.trim().isEmpty()) {
            return false;
        }
        
        String metodo = metodoPago.toLowerCase().trim();
        return metodo.equals("efectivo") || 
               metodo.equals("tarjeta") || 
               metodo.equals("transferencia") || 
               metodo.equals("pago_movil") ||
               metodo.equals("qr") ||
               metodo.equals("paypal") ||
               metodo.equals("bitcoin");
    }
    
    /**
     * Obtiene métodos de pago soportados
     */
    public static String[] getMetodosPagoSoportados() {
        return new String[]{
            "efectivo",
            "tarjeta", 
            "transferencia",
            "pago_movil",
            "qr",
            "paypal",
            "bitcoin"
        };
    }
    
    /**
     * Cancela un pago
     */
    public List<String[]> cancelarPago(int pagoId) throws SQLException {
        // Actualizar el estado del pago a "fallido"
        List<String[]> pago = dPago.updateEstado(pagoId, "fallido");
        
        if (!pago.isEmpty()) {
            // Obtener la nota de venta asociada
            int notaVentaId = Integer.parseInt(pago.get(0)[1]); // índice 1 es nota_venta_id
            
            // Actualizar el estado de la nota de venta a "cancelada"
            dNotaVenta.update(notaVentaId, "cancelada", "Pago cancelado");
        }
        
        return pago;
    }
    
    /**
     * Reembolsa un pago
     */
    public List<String[]> reembolsarPago(int pagoId) throws SQLException {
        // Actualizar el estado del pago a "reembolsado"
        List<String[]> pago = dPago.updateEstado(pagoId, "reembolsado");
        
        if (!pago.isEmpty()) {
            // Obtener la nota de venta asociada
            int notaVentaId = Integer.parseInt(pago.get(0)[1]); // índice 1 es nota_venta_id
            
            // Actualizar el estado de la nota de venta a "cancelada"
            dNotaVenta.update(notaVentaId, "cancelada", "Pago reembolsado");
        }
        
        return pago;
    }
    
    public void disconnect() {
        dPago.disconnect();
        dNotaVenta.disconnect();
    }
}
