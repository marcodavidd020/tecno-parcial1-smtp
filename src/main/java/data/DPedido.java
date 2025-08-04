package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

/**
 * Clase de acceso a datos para la tabla pedido
 */
public class DPedido {
    
    public static final String[] HEADERS = {"id", "direccion_id", "fecha", "total", "estado", "fecha_hora_envio", "fecha_hora_entrega", "direccion_nombre", "longitud", "latitud", "referencia"};
    
    private final SqlConnection connection;
    
    public DPedido() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }
    
    /**
     * Obtiene todos los pedidos con información de la dirección
     */
    public List<String[]> getAll() throws SQLException {
        List<String[]> pedidos = new ArrayList<>();
        String query = "SELECT p.id, p.direccion_id, p.fecha, p.total, p.estado, " +
                      "p.fecha_hora_envio, p.fecha_hora_entrega, " +
                      "d.nombre as direccion_nombre, d.longitud, d.latitud, d.referencia " +
                      "FROM pedido p " +
                      "INNER JOIN direccion d ON p.direccion_id = d.id " +
                      "ORDER BY p.fecha DESC";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                pedidos.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    String.valueOf(rs.getInt("direccion_id")),
                    rs.getString("fecha"),
                    String.valueOf(rs.getBigDecimal("total")),
                    rs.getString("estado"),
                    rs.getString("fecha_hora_envio"),
                    rs.getString("fecha_hora_entrega"),
                    rs.getString("direccion_nombre"),
                    rs.getString("longitud"),
                    rs.getString("latitud"),
                    rs.getString("referencia")
                });
            }
        }
        
        return pedidos;
    }
    
    /**
     * Obtiene un pedido por ID con información de la dirección
     */
    public List<String[]> getById(int id) throws SQLException {
        List<String[]> pedidos = new ArrayList<>();
        String query = "SELECT p.id, p.direccion_id, p.fecha, p.total, p.estado, " +
                      "p.fecha_hora_envio, p.fecha_hora_entrega, " +
                      "d.nombre as direccion_nombre, d.longitud, d.latitud, d.referencia " +
                      "FROM pedido p " +
                      "INNER JOIN direccion d ON p.direccion_id = d.id " +
                      "WHERE p.id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pedidos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("direccion_id")),
                        rs.getString("fecha"),
                        String.valueOf(rs.getBigDecimal("total")),
                        rs.getString("estado"),
                        rs.getString("fecha_hora_envio"),
                        rs.getString("fecha_hora_entrega"),
                        rs.getString("direccion_nombre"),
                        rs.getString("longitud"),
                        rs.getString("latitud"),
                        rs.getString("referencia")
                    });
                }
            }
        }
        
        return pedidos;
    }
    
    /**
     * Obtiene pedidos por estado
     */
    public List<String[]> getByEstado(String estado) throws SQLException {
        List<String[]> pedidos = new ArrayList<>();
        String query = "SELECT p.id, p.direccion_id, p.fecha, p.total, p.estado, " +
                      "p.fecha_hora_envio, p.fecha_hora_entrega, " +
                      "d.nombre as direccion_nombre, d.longitud, d.latitud, d.referencia " +
                      "FROM pedido p " +
                      "INNER JOIN direccion d ON p.direccion_id = d.id " +
                      "WHERE p.estado = ? " +
                      "ORDER BY p.fecha DESC";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, estado);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("direccion_id")),
                        rs.getString("fecha"),
                        String.valueOf(rs.getBigDecimal("total")),
                        rs.getString("estado"),
                        rs.getString("fecha_hora_envio"),
                        rs.getString("fecha_hora_entrega"),
                        rs.getString("direccion_nombre"),
                        rs.getString("longitud"),
                        rs.getString("latitud"),
                        rs.getString("referencia")
                    });
                }
            }
        }
        
        return pedidos;
    }
    
    /**
     * Obtiene pedidos por cliente (email)
     */
    public List<String[]> getByClienteEmail(String email) throws SQLException {
        List<String[]> pedidos = new ArrayList<>();
        String query = "SELECT p.id, p.direccion_id, p.fecha, p.total, p.estado, " +
                      "p.fecha_hora_envio, p.fecha_hora_entrega, " +
                      "d.nombre as direccion_nombre, d.longitud, d.latitud, d.referencia " +
                      "FROM pedido p " +
                      "INNER JOIN direccion d ON p.direccion_id = d.id " +
                      "INNER JOIN nota_venta nv ON p.id = nv.pedido_id " +
                      "INNER JOIN cliente c ON nv.cliente_id = c.id " +
                      "INNER JOIN \"user\" u ON c.user_id = u.id " +
                      "WHERE u.email = ? " +
                      "ORDER BY p.fecha DESC";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, email);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("direccion_id")),
                        rs.getString("fecha"),
                        String.valueOf(rs.getBigDecimal("total")),
                        rs.getString("estado"),
                        rs.getString("fecha_hora_envio"),
                        rs.getString("fecha_hora_entrega"),
                        rs.getString("direccion_nombre"),
                        rs.getString("longitud"),
                        rs.getString("latitud"),
                        rs.getString("referencia")
                    });
                }
            }
        }
        
        return pedidos;
    }
    
    /**
     * Guarda un nuevo pedido
     */
    public List<String[]> save(int direccionId, String fecha, double total, String estado) throws SQLException {
        List<String[]> pedidos = new ArrayList<>();
        String query = "INSERT INTO pedido (direccion_id, fecha, total, estado) " +
                      "VALUES (?, ?, ?, ?) " +
                      "RETURNING id, direccion_id, fecha, total, estado, fecha_hora_envio, fecha_hora_entrega";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, direccionId);
            // Convertir String a Date para la columna fecha
            if (fecha != null && !fecha.isEmpty()) {
                ps.setDate(2, java.sql.Date.valueOf(fecha));
            } else {
                ps.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            }
            ps.setDouble(3, total);
            ps.setString(4, estado);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pedidos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("direccion_id")),
                        rs.getString("fecha"),
                        String.valueOf(rs.getBigDecimal("total")),
                        rs.getString("estado"),
                        rs.getString("fecha_hora_envio"),
                        rs.getString("fecha_hora_entrega")
                    });
                }
            }
        }
        
        return pedidos;
    }
    
    /**
     * Actualiza el estado de un pedido
     */
    public List<String[]> updateEstado(int id, String estado) throws SQLException {
        List<String[]> pedidos = new ArrayList<>();
        String query = "UPDATE pedido SET estado = ? ";
        
        // Agregar fecha de envío o entrega según el estado
        if ("enviado".equals(estado)) {
            query += ", fecha_hora_envio = CURRENT_TIMESTAMP ";
        } else if ("entregado".equals(estado)) {
            query += ", fecha_hora_entrega = CURRENT_TIMESTAMP ";
        }
        
        query += "WHERE id = ? " +
                "RETURNING id, direccion_id, fecha, total, estado, fecha_hora_envio, fecha_hora_entrega";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, estado);
            ps.setInt(2, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pedidos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("direccion_id")),
                        rs.getString("fecha"),
                        String.valueOf(rs.getBigDecimal("total")),
                        rs.getString("estado"),
                        rs.getString("fecha_hora_envio"),
                        rs.getString("fecha_hora_entrega")
                    });
                }
            }
        }
        
        return pedidos;
    }
    
    /**
     * Elimina un pedido por ID
     */
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM pedido WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Obtiene estadísticas de pedidos
     */
    public List<String[]> getEstadisticasPedidos() throws SQLException {
        List<String[]> estadisticas = new ArrayList<>();
        String query = "SELECT " +
                      "COUNT(*) as total_pedidos, " +
                      "COALESCE(SUM(total), 0) as monto_total, " +
                      "COALESCE(AVG(total), 0) as promedio_pedido, " +
                      "COUNT(CASE WHEN estado = 'pendiente' THEN 1 END) as pedidos_pendientes, " +
                      "COUNT(CASE WHEN estado = 'procesando' THEN 1 END) as pedidos_procesando, " +
                      "COUNT(CASE WHEN estado = 'enviado' THEN 1 END) as pedidos_enviados, " +
                      "COUNT(CASE WHEN estado = 'entregado' THEN 1 END) as pedidos_entregados, " +
                      "COUNT(CASE WHEN estado = 'cancelado' THEN 1 END) as pedidos_cancelados " +
                      "FROM pedido";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                estadisticas.add(new String[]{
                    String.valueOf(rs.getInt("total_pedidos")),
                    String.valueOf(rs.getBigDecimal("monto_total")),
                    String.valueOf(rs.getBigDecimal("promedio_pedido")),
                    String.valueOf(rs.getInt("pedidos_pendientes")),
                    String.valueOf(rs.getInt("pedidos_procesando")),
                    String.valueOf(rs.getInt("pedidos_enviados")),
                    String.valueOf(rs.getInt("pedidos_entregados")),
                    String.valueOf(rs.getInt("pedidos_cancelados"))
                });
            }
        }
        
        return estadisticas;
    }
    
    /**
     * Obtiene pedidos pendientes de envío
     */
    public List<String[]> getPedidosPendientesEnvio() throws SQLException {
        List<String[]> pedidos = new ArrayList<>();
        String query = "SELECT p.id, p.direccion_id, p.fecha, p.total, p.estado, " +
                      "p.fecha_hora_envio, p.fecha_hora_entrega, " +
                      "d.nombre as direccion_nombre, d.longitud, d.latitud, d.referencia " +
                      "FROM pedido p " +
                      "INNER JOIN direccion d ON p.direccion_id = d.id " +
                      "WHERE p.estado IN ('pendiente', 'procesando') " +
                      "ORDER BY p.fecha ASC";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                pedidos.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    String.valueOf(rs.getInt("direccion_id")),
                    rs.getString("fecha"),
                    String.valueOf(rs.getBigDecimal("total")),
                    rs.getString("estado"),
                    rs.getString("fecha_hora_envio"),
                    rs.getString("fecha_hora_entrega"),
                    rs.getString("direccion_nombre"),
                    rs.getString("longitud"),
                    rs.getString("latitud"),
                    rs.getString("referencia")
                });
            }
        }
        
        return pedidos;
    }
    
    public void disconnect() {
        // La conexión se cierra automáticamente con try-with-resources
    }
} 