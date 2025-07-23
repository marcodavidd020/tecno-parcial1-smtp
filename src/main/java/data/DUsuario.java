package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

public class DUsuario {

    public static final String[] HEADERS = {"id", "nombre", "apellido", "telefono", "genero", "email", "password", "rol_id"};

    private final SqlConnection connection;

    public DUsuario() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }

    public List<String[]> get(int id) throws SQLException {
        List<String[]> usuario = new ArrayList<>();
        String query = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet set = ps.executeQuery();
            if (set.next()) {
                usuario.add(new String[]{
                        String.valueOf(set.getInt("id")),
                        set.getString("nombre"),
                        set.getString("apellido"),
                        set.getString("telefono"),
                        set.getString("genero"),
                        set.getString("email"),
                        set.getString("password"),
                        String.valueOf(set.getInt("rol_id"))
                });
            } else {
                throw new SQLException("Usuario no encontrado.");
            }
        } catch (SQLException e) {
            System.err.println("Error de conexión o consulta SQL: " + e.getMessage());
            throw e;
        }
        return usuario;
    }


    public List<String[]> save(String nombre, String apellido, String telefono, String genero, String email, String password, int rol_id) throws SQLException {
        // Encripta la contraseña antes de guardarla
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        String query = "INSERT INTO usuarios (nombre, apellido, telefono, genero, email, password, rol_id) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setString(3, telefono);
            ps.setString(4, genero);
            ps.setString(5, email);
            ps.setString(6, hashedPassword);
            ps.setInt(7, rol_id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                return get(id);  // Utiliza el método 'get' para recuperar el usuario recién insertado.
            } else {
                throw new SQLException("Error al insertar usuario. No se pudo recuperar el ID del usuario.");
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar el usuario: " + e.getMessage());
            throw e;
        }
    }

    public List<String[]> update(int id, String nombre, String apellido, String telefono, String genero, String email) throws SQLException {
        String query = "UPDATE usuarios SET nombre = ?, apellido = ?, telefono = ?, genero = ?, email = ? WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setString(1, nombre);
        ps.setString(2, apellido);
        ps.setString(3, telefono);
        ps.setString(4, genero);
        ps.setString(5, email);
        ps.setInt(6, id);

        if(ps.executeUpdate() == 0) {
            System.err.println("Error al modificar el usuario");
            throw new SQLException();
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        String query = "DELETE FROM usuarios WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, id);
        if(ps.executeUpdate() == 0) {
            System.err.println("Error al eliminar usuario");
            throw new SQLException();
        }
        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> usuarios = new ArrayList<>();
        String query = "SELECT * FROM usuarios";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet set = ps.executeQuery();
            while (set.next()) {
                usuarios.add(new String[]{
                        String.valueOf(set.getInt("id")),
                        set.getString("nombre"),
                        set.getString("apellido"),
                        set.getString("telefono"),
                        set.getString("genero"),
                        set.getString("email"),
                        set.getString("password"),
                        String.valueOf(set.getInt("rol_id"))
                });
            }
        } catch (SQLException e) {
            System.err.println("Error de conexión al intentar listar usuarios.");
            throw e;
        }
        return usuarios;
    }

    public void disconnect() {
        connection.closeConnection();
    }

    /**
     * Busca un usuario por su email
     * @param email Email del usuario a buscar
     * @return Lista con los datos del usuario si existe, lista vacía si no existe
     * @throws SQLException
     */
    public List<String[]> findByEmail(String email) throws SQLException {
        List<String[]> result = new ArrayList<>();
        String sql = "SELECT * FROM \"user\" WHERE email = ?";
        
        try (Connection conn = connection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String[] row = new String[8]; // id, nombre, celular, email, genero, password, estado, created_at
                row[0] = String.valueOf(rs.getInt("id"));
                row[1] = rs.getString("nombre");
                row[2] = rs.getString("celular");
                row[3] = rs.getString("email");
                row[4] = rs.getString("genero");
                row[5] = rs.getString("password");
                row[6] = rs.getString("estado");
                row[7] = rs.getString("created_at");
                result.add(row);
            }
        }
        
        return result;
    }
    
    public boolean isCliente(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cliente WHERE user_id = ?";
        
        try (Connection conn = connection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        
        return false;
    }
    
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM \"user\" WHERE email = ?";
        
        try (Connection conn = connection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        
        return false;
    }

    /**
     * Registra un nuevo usuario y cliente en una sola transacción
     * @param nombre Nombre del usuario
     * @param celular Número de celular
     * @param email Email del usuario
     * @param genero Género (masculino, femenino, otro)
     * @param password Contraseña
     * @param nit Número de Identificación Tributaria
     * @return Lista con los datos del usuario registrado
     * @throws SQLException
     */
    public List<String[]> registerUserAndCliente(String nombre, String celular, String email, String genero, String password, String nit) throws SQLException {
        // Encripta la contraseña antes de guardarla
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        Connection conn = null;
        try {
            conn = connection.connect();
            conn.setAutoCommit(false); // Iniciar transacción
            
            // 1. Insertar usuario
            String userQuery = "INSERT INTO \"user\" (nombre, celular, email, genero, password, estado) VALUES (?, ?, ?, ?, ?, 'activo') RETURNING id";
            int userId;
            
            try (PreparedStatement ps = conn.prepareStatement(userQuery)) {
                ps.setString(1, nombre);
                ps.setString(2, celular);
                ps.setString(3, email);
                ps.setString(4, genero);
                ps.setString(5, hashedPassword);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    userId = rs.getInt(1);
                } else {
                    throw new SQLException("Error al insertar usuario. No se pudo recuperar el ID del usuario.");
                }
            }
            
            // 2. Insertar cliente
            String clienteQuery = "INSERT INTO cliente (user_id, nit) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(clienteQuery)) {
                ps.setInt(1, userId);
                ps.setString(2, nit);
                
                int result = ps.executeUpdate();
                if (result == 0) {
                    throw new SQLException("Error al insertar cliente.");
                }
            }
            
            // Confirmar transacción
            conn.commit();
            
            // Retornar datos del usuario registrado
            return findByEmail(email);
            
        } catch (SQLException e) {
            // Revertir transacción en caso de error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error al hacer rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Error al registrar usuario y cliente: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Registra un nuevo cliente asociado a un usuario existente
     * @param userId ID del usuario
     * @param nit Número de Identificación Tributaria
     * @return true si se registró correctamente, false en caso contrario
     * @throws SQLException
     */
    public boolean registerCliente(int userId, String nit) throws SQLException {
        String query = "INSERT INTO cliente (user_id, nit) VALUES (?, ?)";
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setString(2, nit);
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar cliente: " + e.getMessage());
            throw e;
        }
    }
}
