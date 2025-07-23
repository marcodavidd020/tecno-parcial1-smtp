package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

/**
 * Clase de acceso a datos para la tabla nota_venta
 */
public class DNotaVenta {
    
    public static final String[] HEADERS = {"id", "cliente_id", "pedido_id", "fecha", "total", "estado", "observaciones", "created_at", "updated_at"};
    
    private final SqlConnection connection;
    
    public DNotaVenta() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }
    
    /**
     * Obtiene todas las notas de venta con información del cliente
     */
    public List<String[]> getAll() throws SQLException {
        List<String[]> notasVenta = new ArrayList<>();
        String query = "SELECT nv.id, nv.cliente_id, nv.pedido_id, nv.fecha, nv.total, nv.estado, nv.observaciones, " +
                      "nv.created_at, nv.updated_at, " +
                      "c.nit, u.nombre, u.email " +
                      "FROM nota_venta nv " +
                      "INNER JOIN cliente c ON nv.cliente_id = c.id " +
                      "INNER JOIN \"user\" u ON c.user_id = u.id " +
                      "ORDER BY nv.fecha DESC";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                notasVenta.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    String.valueOf(rs.getInt("cliente_id")),
                    rs.getString("pedido_id"),
                    rs.getString("fecha"),
                    String.valueOf(rs.getBigDecimal("total")),
                    rs.getString("estado"),
                    rs.getString("observaciones"),
                    rs.getString("nit"),
                    rs.getString("nombre"),
                    rs.getString("email"),
                    rs.getString("created_at"),
                    rs.getString("updated_at")
                });
            }
        }
        
        return notasVenta;
    }
    
    /**
     * Obtiene una nota de venta por ID con información del cliente
     */
    public List<String[]> getById(int id) throws SQLException {
        List<String[]> notasVenta = new ArrayList<>();
        String query = "SELECT nv.id, nv.cliente_id, nv.pedido_id, nv.fecha, nv.total, nv.estado, nv.observaciones, " +
                      "nv.created_at, nv.updated_at, " +
                      "c.nit, u.nombre, u.email " +
                      "FROM nota_venta nv " +
                      "INNER JOIN cliente c ON nv.cliente_id = c.id " +
                      "INNER JOIN \"user\" u ON c.user_id = u.id " +
                      "WHERE nv.id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    notasVenta.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("cliente_id")),
                        rs.getString("pedido_id"),
                        rs.getString("fecha"),
                        String.valueOf(rs.getBigDecimal("total")),
                        rs.getString("estado"),
                        rs.getString("observaciones"),
                        rs.getString("nit"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                    });
                }
            }
        }
        
        return notasVenta;
    }
    
    /**
     * Obtiene notas de venta por cliente
     */
    public List<String[]> getByClienteId(int clienteId) throws SQLException {
        List<String[]> notasVenta = new ArrayList<>();
        String query = "SELECT nv.id, nv.cliente_id, nv.pedido_id, nv.fecha, nv.total, nv.estado, nv.observaciones, " +
                      "nv.created_at, nv.updated_at, " +
                      "c.nit, u.nombre, u.email " +
                      "FROM nota_venta nv " +
                      "INNER JOIN cliente c ON nv.cliente_id = c.id " +
                      "INNER JOIN \"user\" u ON c.user_id = u.id " +
                      "WHERE nv.cliente_id = ? " +
                      "ORDER BY nv.fecha DESC";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, clienteId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notasVenta.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("cliente_id")),
                        rs.getString("pedido_id"),
                        rs.getString("fecha"),
                        String.valueOf(rs.getBigDecimal("total")),
                        rs.getString("estado"),
                        rs.getString("observaciones"),
                        rs.getString("nit"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                    });
                }
            }
        }
        
        return notasVenta;
    }
    
    /**
     * Guarda una nueva nota de venta
     */
    public List<String[]> save(int clienteId, Integer pedidoId, String fecha, double total, String estado, String observaciones) throws SQLException {
        List<String[]> notasVenta = new ArrayList<>();
        String query = "INSERT INTO nota_venta (cliente_id, pedido_id, fecha, total, estado, observaciones) " +
                      "VALUES (?, ?, ?, ?, ?, ?) " +
                      "RETURNING id, cliente_id, pedido_id, fecha, total, estado, observaciones, created_at, updated_at";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, clienteId);
            if (pedidoId != null) {
                ps.setInt(2, pedidoId);
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            // Convertir String a Date para la columna fecha
            if (fecha != null && !fecha.isEmpty()) {
                ps.setDate(3, java.sql.Date.valueOf(fecha));
            } else {
                ps.setDate(3, new java.sql.Date(System.currentTimeMillis()));
            }
            ps.setDouble(4, total);
            ps.setString(5, estado);
            ps.setString(6, observaciones);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    notasVenta.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("cliente_id")),
                        rs.getString("pedido_id"),
                        rs.getString("fecha"),
                        String.valueOf(rs.getBigDecimal("total")),
                        rs.getString("estado"),
                        rs.getString("observaciones"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                    });
                }
            }
        }
        
        return notasVenta;
    }
    
    /**
     * Actualiza una nota de venta existente
     */
    public List<String[]> update(int id, String estado, String observaciones) throws SQLException {
        List<String[]> notasVenta = new ArrayList<>();
        String query = "UPDATE nota_venta SET estado = ?, observaciones = ?, updated_at = CURRENT_TIMESTAMP " +
                      "WHERE id = ? " +
                      "RETURNING id, cliente_id, pedido_id, fecha, total, estado, observaciones, created_at, updated_at";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, estado);
            ps.setString(2, observaciones);
            ps.setInt(3, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    notasVenta.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("cliente_id")),
                        rs.getString("pedido_id"),
                        rs.getString("fecha"),
                        String.valueOf(rs.getBigDecimal("total")),
                        rs.getString("estado"),
                        rs.getString("observaciones"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                    });
                }
            }
        }
        
        return notasVenta;
    }
    
    /**
     * Elimina una nota de venta por ID
     */
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM nota_venta WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Obtiene el total de ventas por cliente
     */
    public double getTotalVentasByCliente(int clienteId) throws SQLException {
        String query = "SELECT COALESCE(SUM(total), 0) as total_ventas FROM nota_venta WHERE cliente_id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, clienteId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total_ventas");
                }
            }
        }
        
        return 0.0;
    }
    
    /**
     * Obtiene estadísticas de ventas
     */
    public List<String[]> getEstadisticasVentas() throws SQLException {
        List<String[]> estadisticas = new ArrayList<>();
        String query = "SELECT " +
                      "COUNT(*) as total_ventas, " +
                      "COALESCE(SUM(total), 0) as monto_total, " +
                      "COALESCE(AVG(total), 0) as promedio_venta, " +
                      "COUNT(CASE WHEN estado = 'completada' THEN 1 END) as ventas_completadas, " +
                      "COUNT(CASE WHEN estado = 'pendiente' THEN 1 END) as ventas_pendientes " +
                      "FROM nota_venta";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                estadisticas.add(new String[]{
                    String.valueOf(rs.getInt("total_ventas")),
                    String.valueOf(rs.getBigDecimal("monto_total")),
                    String.valueOf(rs.getBigDecimal("promedio_venta")),
                    String.valueOf(rs.getInt("ventas_completadas")),
                    String.valueOf(rs.getInt("ventas_pendientes"))
                });
            }
        }
        
        return estadisticas;
    }
    
    public void disconnect() {
        // La conexión se cierra automáticamente con try-with-resources
    }
} 