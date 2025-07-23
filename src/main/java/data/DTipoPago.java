package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

/**
 * Clase de acceso a datos para la tabla tipo_pago
 */
public class DTipoPago {
    
    public static final String[] HEADERS = {"id", "tipo_pago"};
    
    private final SqlConnection connection;
    
    public DTipoPago() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }
    
    /**
     * Obtiene todos los tipos de pago
     */
    public List<String[]> getAll() throws SQLException {
        List<String[]> tiposPago = new ArrayList<>();
        String query = "SELECT id, tipo_pago FROM tipo_pago ORDER BY tipo_pago";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                tiposPago.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("tipo_pago")
                });
            }
        }
        
        return tiposPago;
    }
    
    /**
     * Obtiene un tipo de pago por ID
     */
    public List<String[]> getById(int id) throws SQLException {
        List<String[]> tiposPago = new ArrayList<>();
        String query = "SELECT id, tipo_pago FROM tipo_pago WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tiposPago.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("tipo_pago")
                    });
                }
            }
        }
        
        return tiposPago;
    }
    
    /**
     * Guarda un nuevo tipo de pago
     */
    public List<String[]> save(String tipoPago) throws SQLException {
        List<String[]> tiposPago = new ArrayList<>();
        String query = "INSERT INTO tipo_pago (tipo_pago) VALUES (?) RETURNING id, tipo_pago";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, tipoPago);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tiposPago.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("tipo_pago")
                    });
                }
            }
        }
        
        return tiposPago;
    }
    
    /**
     * Actualiza un tipo de pago existente
     */
    public List<String[]> update(int id, String tipoPago) throws SQLException {
        List<String[]> tiposPago = new ArrayList<>();
        String query = "UPDATE tipo_pago SET tipo_pago = ? WHERE id = ? RETURNING id, tipo_pago";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, tipoPago);
            ps.setInt(2, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tiposPago.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("tipo_pago")
                    });
                }
            }
        }
        
        return tiposPago;
    }
    
    /**
     * Elimina un tipo de pago por ID
     */
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM tipo_pago WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Obtiene el nombre del tipo de pago por ID
     */
    public String getTipoPagoById(int id) throws SQLException {
        String query = "SELECT tipo_pago FROM tipo_pago WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("tipo_pago");
                }
            }
        }
        
        return null;
    }
    
    public void disconnect() {
        connection.closeConnection();
    }
} 