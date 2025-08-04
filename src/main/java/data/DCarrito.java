package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

/**
 * Clase de acceso a datos para la tabla carrito
 */
public class DCarrito {
    
    public static final String[] HEADERS = {"id", "cliente_id", "fecha", "total", "estado", "nit", "nombre", "email"};
    
    private final SqlConnection connection;
    
    public DCarrito() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }
    
    /**
     * Obtiene el carrito activo del cliente, o crea uno nuevo si no existe o está procesado
     */
    public List<String[]> getCarritoActivo(int clienteId) throws SQLException {
        List<String[]> carritos = new ArrayList<>();
        
        // Primero, buscar un carrito activo que tenga productos
        String query = "SELECT c.id, c.cliente_id, c.fecha, c.total, c.estado, " +
                      "cl.nit, u.nombre, u.email, " +
                      "COUNT(dc.id) as productos_count " +
                      "FROM carrito c " +
                      "INNER JOIN cliente cl ON c.cliente_id = cl.id " +
                      "INNER JOIN \"user\" u ON cl.user_id = u.id " +
                      "LEFT JOIN detalle_carrito dc ON c.id = dc.carrito_id " +
                      "WHERE c.cliente_id = ? AND c.estado = 'activo' " +
                      "GROUP BY c.id, c.cliente_id, c.fecha, c.total, c.estado, " +
                      "cl.nit, u.nombre, u.email " +
                      "HAVING COUNT(dc.id) > 0 " +
                      "ORDER BY c.id DESC " +
                      "LIMIT 1";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, clienteId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Encontramos un carrito activo con productos
                    carritos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("cliente_id")),
                        rs.getString("fecha"),
                        String.valueOf(rs.getBigDecimal("total")),
                        rs.getString("estado"),
                        rs.getString("nit"),
                        rs.getString("nombre"),
                        rs.getString("email")
                    });
                    return carritos;
                }
            }
        }
        
        // Si no hay carrito activo con productos, buscar el último carrito del cliente
        query = "SELECT c.id, c.cliente_id, c.fecha, c.total, c.estado, " +
                "cl.nit, u.nombre, u.email " +
                "FROM carrito c " +
                "INNER JOIN cliente cl ON c.cliente_id = cl.id " +
                "INNER JOIN \"user\" u ON cl.user_id = u.id " +
                "WHERE c.cliente_id = ? " +
                "ORDER BY c.id DESC " +
                "LIMIT 1";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, clienteId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String estado = rs.getString("estado");
                    
                    if ("abandonado".equals(estado)) {
                        // Si está abandonado, cambiar a activo
                        activarCarrito(rs.getInt("id"));
                        estado = "activo";
                    } else if ("procesado".equals(estado)) {
                        // Si está procesado, crear uno nuevo
                        return crearNuevoCarrito(clienteId);
                    }
                    
                    carritos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("cliente_id")),
                        rs.getString("fecha"),
                        String.valueOf(rs.getBigDecimal("total")),
                        estado,
                        rs.getString("nit"),
                        rs.getString("nombre"),
                        rs.getString("email")
                    });
                } else {
                    // No existe carrito, crear uno nuevo
                    return crearNuevoCarrito(clienteId);
                }
            }
        }
        
        return carritos;
    }
    
    /**
     * Obtiene un carrito por ID con información del cliente
     */
    public List<String[]> getById(int id) throws SQLException {
        List<String[]> carritos = new ArrayList<>();
        String query = "SELECT c.id, c.cliente_id, c.fecha, c.total, c.estado, " +
                      "cl.nit, u.nombre, u.email " +
                      "FROM carrito c " +
                      "INNER JOIN cliente cl ON c.cliente_id = cl.id " +
                      "INNER JOIN \"user\" u ON cl.user_id = u.id " +
                      "WHERE c.id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    carritos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("cliente_id")),
                        rs.getString("fecha"),
                        String.valueOf(rs.getBigDecimal("total")),
                        rs.getString("estado"),
                        rs.getString("nit"),
                        rs.getString("nombre"),
                        rs.getString("email")
                    });
                }
            }
        }
        
        return carritos;
    }
    
    /**
     * Obtiene todos los carritos de un cliente
     */
    public List<String[]> getByClienteId(int clienteId) throws SQLException {
        List<String[]> carritos = new ArrayList<>();
        String query = "SELECT c.id, c.cliente_id, c.fecha, c.total, c.estado, " +
                      "cl.nit, u.nombre, u.email " +
                      "FROM carrito c " +
                      "INNER JOIN cliente cl ON c.cliente_id = cl.id " +
                      "INNER JOIN \"user\" u ON cl.user_id = u.id " +
                      "WHERE c.cliente_id = ? " +
                      "ORDER BY c.id DESC";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, clienteId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    carritos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("cliente_id")),
                        rs.getString("fecha"),
                        String.valueOf(rs.getBigDecimal("total")),
                        rs.getString("estado"),
                        rs.getString("nit"),
                        rs.getString("nombre"),
                        rs.getString("email")
                    });
                }
            }
        }
        
        return carritos;
    }
    
    /**
     * Crea un nuevo carrito para el cliente
     */
    private List<String[]> crearNuevoCarrito(int clienteId) throws SQLException {
        List<String[]> carritos = new ArrayList<>();
        String query = "INSERT INTO carrito (cliente_id, fecha, total, estado) VALUES (?, CURRENT_DATE, 0, 'activo') " +
                      "RETURNING id";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, clienteId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Obtener información completa del carrito creado
                    return getById(rs.getInt("id"));
                }
            }
        }
        
        return carritos;
    }
    
    /**
     * Activa un carrito abandonado
     */
    private void activarCarrito(int carritoId) throws SQLException {
        String query = "UPDATE carrito SET estado = 'activo' WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, carritoId);
            ps.executeUpdate();
        }
    }
    
    /**
     * Actualiza el total del carrito
     */
    public List<String[]> updateTotal(int carritoId, double total) throws SQLException {
        List<String[]> carritos = new ArrayList<>();
        String query = "UPDATE carrito SET total = ? WHERE id = ? RETURNING id, cliente_id, fecha, total, estado";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setBigDecimal(1, java.math.BigDecimal.valueOf(total));
            ps.setInt(2, carritoId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    carritos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("cliente_id")),
                        rs.getString("fecha"),
                        String.valueOf(rs.getBigDecimal("total")),
                        rs.getString("estado")
                    });
                }
            }
        }
        
        return carritos;
    }
    
    /**
     * Cambia el estado del carrito
     */
    public List<String[]> updateEstado(int carritoId, String estado) throws SQLException {
        List<String[]> carritos = new ArrayList<>();
        String query = "UPDATE carrito SET estado = ? WHERE id = ? RETURNING id, cliente_id, fecha, total, estado";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, estado);
            ps.setInt(2, carritoId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    carritos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("cliente_id")),
                        rs.getString("fecha"),
                        String.valueOf(rs.getBigDecimal("total")),
                        rs.getString("estado")
                    });
                }
            }
        }
        
        return carritos;
    }
    
    /**
     * Elimina un carrito por ID
     */
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM carrito WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Verifica si existe un carrito por ID
     */
    public boolean existsById(int id) throws SQLException {
        String query = "SELECT COUNT(*) FROM carrito WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    public void disconnect() {
        connection.closeConnection();
    }
} 