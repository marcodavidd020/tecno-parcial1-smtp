package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

/**
 * Clase de acceso a datos para la tabla producto
 */
public class DProducto {
    
    public static final String[] HEADERS = {"id", "cod_producto", "nombre", "precio_compra", "precio_venta", "imagen", "descripcion", "categoria"};
    
    private final SqlConnection connection;
    private final DCategoria dCategoria;
    
    public DProducto() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
        this.dCategoria = new DCategoria();
    }
    
    /**
     * Obtiene todos los productos con nombre de categoría
     */
    public List<String[]> getAll() throws SQLException {
        List<String[]> productos = new ArrayList<>();
        String query = "SELECT p.id, p.cod_producto, p.nombre, p.precio_compra, p.precio_venta, p.imagen, p.descripcion, p.categoria_id " +
                      "FROM producto p ORDER BY p.nombre";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                int categoriaId = rs.getInt("categoria_id");
                String nombreCategoria = dCategoria.getNombreById(categoriaId);
                
                productos.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("cod_producto"),
                    rs.getString("nombre"),
                    String.valueOf(rs.getBigDecimal("precio_compra")),
                    String.valueOf(rs.getBigDecimal("precio_venta")),
                    rs.getString("imagen"),
                    rs.getString("descripcion"),
                    nombreCategoria != null ? nombreCategoria : "Sin categoría"
                });
            }
        }
        
        return productos;
    }
    
    /**
     * Obtiene un producto por ID con nombre de categoría
     */
    public List<String[]> getById(int id) throws SQLException {
        List<String[]> productos = new ArrayList<>();
        String query = "SELECT p.id, p.cod_producto, p.nombre, p.precio_compra, p.precio_venta, p.imagen, p.descripcion, p.categoria_id " +
                      "FROM producto p WHERE p.id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int categoriaId = rs.getInt("categoria_id");
                    String nombreCategoria = dCategoria.getNombreById(categoriaId);
                    
                    productos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("cod_producto"),
                        rs.getString("nombre"),
                        String.valueOf(rs.getBigDecimal("precio_compra")),
                        String.valueOf(rs.getBigDecimal("precio_venta")),
                        rs.getString("imagen"),
                        rs.getString("descripcion"),
                        nombreCategoria != null ? nombreCategoria : "Sin categoría"
                    });
                }
            }
        }
        
        return productos;
    }
    
    /**
     * Guarda un nuevo producto
     */
    public List<String[]> save(String codProducto, String nombre, double precioCompra, double precioVenta, String imagen, String descripcion, int categoriaId) throws SQLException {
        List<String[]> productos = new ArrayList<>();
        String query = "INSERT INTO producto (cod_producto, nombre, precio_compra, precio_venta, imagen, descripcion, categoria_id) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id, cod_producto, nombre, precio_compra, precio_venta, imagen, descripcion, categoria_id";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, codProducto);
            ps.setString(2, nombre);
            ps.setDouble(3, precioCompra);
            ps.setDouble(4, precioVenta);
            ps.setString(5, imagen);
            ps.setString(6, descripcion);
            ps.setInt(7, categoriaId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nombreCategoria = dCategoria.getNombreById(categoriaId);
                    
                    productos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("cod_producto"),
                        rs.getString("nombre"),
                        String.valueOf(rs.getBigDecimal("precio_compra")),
                        String.valueOf(rs.getBigDecimal("precio_venta")),
                        rs.getString("imagen"),
                        rs.getString("descripcion"),
                        nombreCategoria != null ? nombreCategoria : "Sin categoría"
                    });
                }
            }
        }
        
        return productos;
    }
    
    /**
     * Actualiza un producto existente
     */
    public List<String[]> update(int id, String codProducto, String nombre, double precioCompra, double precioVenta, String imagen, String descripcion, int categoriaId) throws SQLException {
        List<String[]> productos = new ArrayList<>();
        String query = "UPDATE producto SET cod_producto = ?, nombre = ?, precio_compra = ?, precio_venta = ?, imagen = ?, descripcion = ?, categoria_id = ? " +
                      "WHERE id = ? RETURNING id, cod_producto, nombre, precio_compra, precio_venta, imagen, descripcion, categoria_id";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, codProducto);
            ps.setString(2, nombre);
            ps.setDouble(3, precioCompra);
            ps.setDouble(4, precioVenta);
            ps.setString(5, imagen);
            ps.setString(6, descripcion);
            ps.setInt(7, categoriaId);
            ps.setInt(8, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nombreCategoria = dCategoria.getNombreById(categoriaId);
                    
                    productos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("cod_producto"),
                        rs.getString("nombre"),
                        String.valueOf(rs.getBigDecimal("precio_compra")),
                        String.valueOf(rs.getBigDecimal("precio_venta")),
                        rs.getString("imagen"),
                        rs.getString("descripcion"),
                        nombreCategoria != null ? nombreCategoria : "Sin categoría"
                    });
                }
            }
        }
        
        return productos;
    }
    
    /**
     * Elimina un producto por ID
     */
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM producto WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Obtiene productos por categoría
     */
    public List<String[]> getByCategoria(int categoriaId) throws SQLException {
        List<String[]> productos = new ArrayList<>();
        String query = "SELECT p.id, p.cod_producto, p.nombre, p.precio_compra, p.precio_venta, p.imagen, p.descripcion, p.categoria_id " +
                      "FROM producto p WHERE p.categoria_id = ? ORDER BY p.nombre";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, categoriaId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nombreCategoria = dCategoria.getNombreById(categoriaId);
                    
                    productos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("cod_producto"),
                        rs.getString("nombre"),
                        String.valueOf(rs.getBigDecimal("precio_compra")),
                        String.valueOf(rs.getBigDecimal("precio_venta")),
                        rs.getString("imagen"),
                        rs.getString("descripcion"),
                        nombreCategoria != null ? nombreCategoria : "Sin categoría"
                    });
                }
            }
        }
        
        return productos;
    }
    
    public void disconnect() {
        connection.closeConnection();
        dCategoria.disconnect();
    }
} 