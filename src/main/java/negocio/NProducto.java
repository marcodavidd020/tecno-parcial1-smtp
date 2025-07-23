package negocio;

import data.DProducto;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase de negocio para la gestión de productos
 */
public class NProducto {
    
    private final DProducto dProducto;
    
    public NProducto() {
        this.dProducto = new DProducto();
    }
    
    /**
     * Obtiene todos los productos
     */
    public List<String[]> getAll() throws SQLException {
        return dProducto.getAll();
    }
    
    /**
     * Obtiene un producto por ID
     */
    public List<String[]> getById(int id) throws SQLException {
        return dProducto.getById(id);
    }
    
    /**
     * Guarda un nuevo producto
     */
    public List<String[]> save(String codProducto, String nombre, double precioCompra, double precioVenta, String imagen, String descripcion, int categoriaId) throws SQLException {
        return dProducto.save(codProducto, nombre, precioCompra, precioVenta, imagen, descripcion, categoriaId);
    }
    
    /**
     * Actualiza un producto existente
     */
    public List<String[]> update(int id, String codProducto, String nombre, double precioCompra, double precioVenta, String imagen, String descripcion, int categoriaId) throws SQLException {
        return dProducto.update(id, codProducto, nombre, precioCompra, precioVenta, imagen, descripcion, categoriaId);
    }
    
    /**
     * Elimina un producto por ID
     */
    public boolean delete(int id) throws SQLException {
        return dProducto.delete(id);
    }
    
    /**
     * Obtiene productos por categoría
     */
    public List<String[]> getByCategoria(int categoriaId) throws SQLException {
        return dProducto.getByCategoria(categoriaId);
    }
    
    public void disconnect() {
        dProducto.disconnect();
    }
} 