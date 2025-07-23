package negocio;

import data.DTipoPago;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase de negocio para la gestión de tipos de pago
 */
public class NTipoPago {
    
    private final DTipoPago dTipoPago;
    
    public NTipoPago() {
        this.dTipoPago = new DTipoPago();
    }
    
    /**
     * Obtiene todos los tipos de pago
     */
    public List<String[]> getAll() throws SQLException {
        return dTipoPago.getAll();
    }
    
    /**
     * Obtiene un tipo de pago por ID
     */
    public List<String[]> getById(int id) throws SQLException {
        return dTipoPago.getById(id);
    }
    
    /**
     * Guarda un nuevo tipo de pago
     */
    public List<String[]> save(String tipoPago) throws SQLException {
        return dTipoPago.save(tipoPago);
    }
    
    /**
     * Actualiza un tipo de pago existente
     */
    public List<String[]> update(int id, String tipoPago) throws SQLException {
        return dTipoPago.update(id, tipoPago);
    }
    
    /**
     * Elimina un tipo de pago por ID
     */
    public boolean delete(int id) throws SQLException {
        return dTipoPago.delete(id);
    }
    
    /**
     * Obtiene el nombre del tipo de pago por ID
     */
    public String getTipoPagoById(int id) throws SQLException {
        return dTipoPago.getTipoPagoById(id);
    }
    
    public void disconnect() {
        dTipoPago.disconnect();
    }
} 