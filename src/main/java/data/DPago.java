package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

/**
 * Clase de acceso a datos para la tabla pago
 */
public class DPago {
    
    public static final String[] HEADERS = {"id", "nota_venta_id", "tipo_pago_id", "fechapago", "estado", "pago_facil_id", "created_at", "updated_at"};
    
    private final SqlConnection connection;
    
    public DPago() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }
    
    /**
     * Obtiene todos los pagos con información de la nota de venta
     */
    public List<String[]> getAll() throws SQLException {
        List<String[]> pagos = new ArrayList<>();
        String query = "SELECT p.id, p.nota_venta_id, p.tipo_pago_id, p.fechapago, p.estado, p.pago_facil_id, " +
                      "p.created_at, p.updated_at, " +
                      "tp.tipo_pago, nv.total as total_nota, nv.estado as estado_nota " +
                      "FROM pagos p " +
                      "INNER JOIN tipo_pago tp ON p.tipo_pago_id = tp.id " +
                      "INNER JOIN nota_venta nv ON p.nota_venta_id = nv.id " +
                      "ORDER BY p.fechapago DESC";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                pagos.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    String.valueOf(rs.getInt("nota_venta_id")),
                    String.valueOf(rs.getInt("tipo_pago_id")),
                    rs.getString("fechapago"),
                    rs.getString("estado"),
                    rs.getString("pago_facil_id"),
                    rs.getString("created_at"),
                    rs.getString("updated_at"),
                    rs.getString("tipo_pago"),
                    String.valueOf(rs.getBigDecimal("total_nota")),
                    rs.getString("estado_nota")
                });
            }
        }
        
        return pagos;
    }
    
    /**
     * Obtiene un pago por ID
     */
    public List<String[]> getById(int id) throws SQLException {
        List<String[]> pagos = new ArrayList<>();
        String query = "SELECT p.id, p.nota_venta_id, p.tipo_pago_id, p.fechapago, p.estado, p.pago_facil_id, " +
                      "p.created_at, p.updated_at, " +
                      "tp.tipo_pago, nv.total as total_nota, nv.estado as estado_nota " +
                      "FROM pagos p " +
                      "INNER JOIN tipo_pago tp ON p.tipo_pago_id = tp.id " +
                      "INNER JOIN nota_venta nv ON p.nota_venta_id = nv.id " +
                      "WHERE p.id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pagos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("nota_venta_id")),
                        String.valueOf(rs.getInt("tipo_pago_id")),
                        rs.getString("fechapago"),
                        rs.getString("estado"),
                        rs.getString("pago_facil_id"),
                        rs.getString("created_at"),
                        rs.getString("updated_at"),
                        rs.getString("tipo_pago"),
                        String.valueOf(rs.getBigDecimal("total_nota")),
                        rs.getString("estado_nota")
                    });
                }
            }
        }
        
        return pagos;
    }
    
    /**
     * Obtiene pagos por nota de venta
     */
    public List<String[]> getByNotaVentaId(int notaVentaId) throws SQLException {
        List<String[]> pagos = new ArrayList<>();
        String query = "SELECT p.id, p.nota_venta_id, p.tipo_pago_id, p.fechapago, p.estado, p.pago_facil_id, " +
                      "p.created_at, p.updated_at, " +
                      "tp.tipo_pago, nv.total as total_nota, nv.estado as estado_nota " +
                      "FROM pagos p " +
                      "INNER JOIN tipo_pago tp ON p.tipo_pago_id = tp.id " +
                      "INNER JOIN nota_venta nv ON p.nota_venta_id = nv.id " +
                      "WHERE p.nota_venta_id = ? " +
                      "ORDER BY p.fechapago DESC";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, notaVentaId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pagos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("nota_venta_id")),
                        String.valueOf(rs.getInt("tipo_pago_id")),
                        rs.getString("fechapago"),
                        rs.getString("estado"),
                        rs.getString("pago_facil_id"),
                        rs.getString("created_at"),
                        rs.getString("updated_at"),
                        rs.getString("tipo_pago"),
                        String.valueOf(rs.getBigDecimal("total_nota")),
                        rs.getString("estado_nota")
                    });
                }
            }
        }
        
        return pagos;
    }
    
    /**
     * Guarda un nuevo pago
     */
    public List<String[]> save(int notaVentaId, double monto, String metodoPago, String estado, String referencia) throws SQLException {
        List<String[]> pagos = new ArrayList<>();
        
        // Primero obtener el tipo_pago_id basado en el método de pago
        int tipoPagoId = obtenerTipoPagoId(metodoPago);
        
        String query = "INSERT INTO pagos (nota_venta_id, tipo_pago_id, fechapago, estado, pago_facil_id) " +
                      "VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?) " +
                      "RETURNING id, nota_venta_id, tipo_pago_id, fechapago, estado, pago_facil_id, created_at, updated_at";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, notaVentaId);
            ps.setInt(2, tipoPagoId);
            ps.setString(3, estado);
            ps.setString(4, referencia); // Usar referencia como pago_facil_id
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pagos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("nota_venta_id")),
                        String.valueOf(rs.getInt("tipo_pago_id")),
                        rs.getString("fechapago"),
                        rs.getString("estado"),
                        rs.getString("pago_facil_id"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                    });
                }
            }
        }
        
        return pagos;
    }
    
    /**
     * Obtiene el tipo_pago_id basado en el método de pago
     */
    private int obtenerTipoPagoId(String metodoPago) throws SQLException {
        String query = "SELECT id FROM tipo_pago WHERE tipo_pago = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setString(1, metodoPago);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    // Si no existe, crear el tipo de pago
                    return crearTipoPago(metodoPago);
                }
            }
        }
    }
    
    /**
     * Crea un nuevo tipo de pago si no existe
     */
    private int crearTipoPago(String metodoPago) throws SQLException {
        String query = "INSERT INTO tipo_pago (tipo_pago) VALUES (?) RETURNING id";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setString(1, metodoPago);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("No se pudo crear el tipo de pago: " + metodoPago);
                }
            }
        }
    }
    
    /**
     * Actualiza el estado de un pago
     */
    public List<String[]> updateEstado(int id, String estado) throws SQLException {
        List<String[]> pagos = new ArrayList<>();
        String query = "UPDATE pagos SET estado = ?, updated_at = CURRENT_TIMESTAMP " +
                      "WHERE id = ? " +
                      "RETURNING id, nota_venta_id, tipo_pago_id, fechapago, estado, pago_facil_id, created_at, updated_at";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, estado);
            ps.setInt(2, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pagos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("nota_venta_id")),
                        String.valueOf(rs.getInt("tipo_pago_id")),
                        rs.getString("fechapago"),
                        rs.getString("estado"),
                        rs.getString("pago_facil_id"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                    });
                }
            }
        }
        
        return pagos;
    }
    
    /**
     * Elimina un pago por ID
     */
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM pagos WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Obtiene estadísticas de pagos
     */
    public List<String[]> getEstadisticasPagos() throws SQLException {
        List<String[]> estadisticas = new ArrayList<>();
        String query = "SELECT " +
                      "COUNT(*) as total_pagos, " +
                      "COUNT(CASE WHEN estado = 'pagado' THEN 1 END) as pagos_completados, " +
                      "COUNT(CASE WHEN estado = 'pendiente' THEN 1 END) as pagos_pendientes, " +
                      "COUNT(CASE WHEN estado = 'fallido' THEN 1 END) as pagos_fallidos, " +
                      "COUNT(CASE WHEN estado = 'reembolsado' THEN 1 END) as pagos_reembolsados " +
                      "FROM pagos";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                estadisticas.add(new String[]{
                    String.valueOf(rs.getInt("total_pagos")),
                    String.valueOf(rs.getInt("pagos_completados")),
                    String.valueOf(rs.getInt("pagos_pendientes")),
                    String.valueOf(rs.getInt("pagos_fallidos")),
                    String.valueOf(rs.getInt("pagos_reembolsados"))
                });
            }
        }
        
        return estadisticas;
    }
    
    /**
     * Obtiene pagos por método de pago
     */
    public List<String[]> getPagosPorMetodo() throws SQLException {
        List<String[]> pagosPorMetodo = new ArrayList<>();
        String query = "SELECT tp.tipo_pago, " +
                      "COUNT(*) as cantidad_pagos " +
                      "FROM pagos p " +
                      "JOIN tipo_pago tp ON p.tipo_pago_id = tp.id " +
                      "GROUP BY tp.tipo_pago " +
                      "ORDER BY cantidad_pagos DESC";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                pagosPorMetodo.add(new String[]{
                    rs.getString("tipo_pago"),
                    String.valueOf(rs.getInt("cantidad_pagos"))
                });
            }
        }
        
        return pagosPorMetodo;
    }
    
    /**
     * Procesa un pago (simula procesamiento de pago)
     */
    public List<String[]> procesarPago(int notaVentaId, double monto, String metodoPago, String referencia) throws SQLException {
        // Simular procesamiento de pago (en un sistema real, aquí iría la integración con pasarela de pagos)
        String estado = "pagado"; // Por defecto exitoso (usando el enum de Laravel)
        
        // Validar que el monto sea positivo
        if (monto <= 0) {
            throw new SQLException("El monto debe ser mayor a 0");
        }
        
        // Validar método de pago
        if (metodoPago == null || metodoPago.trim().isEmpty()) {
            throw new SQLException("El método de pago es requerido");
        }
        
        return save(notaVentaId, monto, metodoPago, estado, referencia);
    }
    
    public void disconnect() {
        // La conexión se cierra automáticamente con try-with-resources
    }
}
