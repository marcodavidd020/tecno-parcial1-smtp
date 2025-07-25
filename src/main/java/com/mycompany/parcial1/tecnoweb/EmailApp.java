package com.mycompany.parcial1.tecnoweb;

import data.*;
import interfaces.ICasoUsoListener;
import interfaces.IEmailListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import librerias.Email;
import librerias.HtmlRes;
import librerias.Interpreter;
import librerias.ParamsAction;
import librerias.analex.Token;
import negocio.*;
import postgresConecction.EmailReceipt;
import postgresConecction.EmailSend;
import postgresConecction.SqlConnection;
import postgresConecction.DBConnection;

public class EmailApp implements ICasoUsoListener, IEmailListener {

    private static final int CONSTRAINTS_ERROR = -2;
    private static final int NUMBER_FORMAT_ERROR = -3;
    private static final int INDEX_OUT_OF_BOUND_ERROR = -4;
    private static final int PARSE_ERROR = -5;
    private static final int AUTHORIZATION_ERROR = -6;

    private EmailReceipt emailReceipt;
    private NUsuario nUsuario;
    private NPromocion nPromocion;
    private NCategoria nCategoria;
    private NProducto nProducto;
    private NTipoPago nTipoPago;
    private NCliente nCliente;
    private NCarrito nCarrito;
    private NNotaVenta nNotaVenta;
    private NPedido nPedido;
    private NDireccion nDireccion;

    public EmailApp() {
        this.emailReceipt = new EmailReceipt();
        this.emailReceipt.setEmailListener(this);
        this.nUsuario = new NUsuario();
        this.nPromocion = new NPromocion();
        this.nCategoria = new NCategoria();
        this.nProducto = new NProducto();
        this.nTipoPago = new NTipoPago();
        this.nCliente = new NCliente();
        this.nCarrito = new NCarrito();
        this.nNotaVenta = new NNotaVenta();
        this.nPedido = new NPedido();
        this.nDireccion = new NDireccion();
    }

    public void start() {
        Thread thread = new Thread(emailReceipt);
        thread.setName("Mail Receipt");
        thread.start();
    }

    // Implementacion para cada caso de uso específico
    @Override
    public void usuario(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    // nUsuario.save(event.getParams());
                    List<String[]> userDataSaved = nUsuario.save(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Usuario guardado correctamente");
                    tableNotifySuccess(event.getSender(), "Usuario guardado correctamente", DUsuario.HEADERS, (ArrayList<String[]>) userDataSaved, event.getCommand());
                    break;
                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // Si hay parámetros, se asume que es una solicitud de usuario por ID
                        String idParam = event.getParams().get(0); // Supone que el ID es el primer parámetro
                        try {
                            int id = Integer.parseInt(idParam);
                            List<String[]> userData = nUsuario.get(id);
                            if (userData != null) {
                                //simpleNotifySuccess(event.getSender(), "Usuario encontrado: " + Arrays.toString(userData));
                                tableNotifySuccess(event.getSender(), "Usuario encontrado", DUsuario.HEADERS, (ArrayList<String[]>) userData, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "Error", "Usuario no encontrado.");
                            }
                        } catch (NumberFormatException e) {
                            simpleNotify(event.getSender(), "Error", "ID inválido.");
                        }
                    } else {
                        // Si no hay parámetros, se asume que es una solicitud de todos los usuarios
                        tableNotifySuccess(event.getSender(), "Lista de Usuarios", DUsuario.HEADERS, nUsuario.list());
                    }
                    break;
                case Token.MODIFY:
                    // nUsuario.update(event.getParams());
                    List<String[]> userDataUpdated = nUsuario.update(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Usuario actualizado correctamente");
                    tableNotifySuccess(event.getSender(), "Usuario actualizado correctamente", DUsuario.HEADERS, (ArrayList<String[]>) userDataUpdated, event.getCommand());
                    break;
                case Token.DELETE:
                    // nUsuario.delete(event.getParams());
                    List<String[]> userDataDeleted = nUsuario.delete(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Usuario eliminado correctamente");
                    tableNotifySuccess(event.getSender(), "Usuario eliminado correctamente", DUsuario.HEADERS, (ArrayList<String[]>) userDataDeleted, event.getCommand());
                    break;
            }
        } catch (SQLException ex) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), Collections.singletonList("Error SQL: " + ex.getMessage()));
        } catch (IndexOutOfBoundsException ex) {
            handleError(INDEX_OUT_OF_BOUND_ERROR, event.getSender(), Collections.singletonList("Error de índice: " + ex.getMessage()));
        }
    }










    @Override
    public void promocion(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.GET:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        String param = event.getParams().get(0);
                        
                        // Intentar como ID numérico primero
                        try {
                            int id = Integer.parseInt(param);
                            List<String[]> promocionData = nPromocion.getById(id);
                            
                            if (!promocionData.isEmpty()) {
                                String[] enhancedHeaders = {"ID", "Nombre", "Fecha Inicio", "Fecha Fin", "Descuento", "Producto ID", "Producto", "Precio Venta"};
                                tableNotifySuccess(event.getSender(), "Promoción encontrada", enhancedHeaders, (ArrayList<String[]>) promocionData, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "Promoción no encontrada", 
                                    "❌ **No se encontró la promoción con ID: " + id + "**");
                            }
                        } catch (NumberFormatException e) {
                            // Si no es número, intentar como producto_id
                            int productoId = Integer.parseInt(param);
                            List<String[]> promocionesData = nPromocion.getByProductoId(productoId);
                            
                            if (!promocionesData.isEmpty()) {
                                String[] enhancedHeaders = {"ID", "Nombre", "Fecha Inicio", "Fecha Fin", "Descuento", "Producto ID", "Producto", "Precio Venta"};
                                tableNotifySuccess(event.getSender(), "Promociones del Producto", enhancedHeaders, (ArrayList<String[]>) promocionesData, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "No hay promociones", 
                                    "📝 **No hay promociones para el producto con ID: " + productoId + "**");
                            }
                        }
                    } else {
                        // Comando: promocion get (todas las promociones)
                        List<String[]> promocionesData = nPromocion.getAll();
                        
                        if (!promocionesData.isEmpty()) {
                            String[] enhancedHeaders = {"ID", "Nombre", "Fecha Inicio", "Fecha Fin", "Descuento", "Producto ID", "Producto", "Precio Venta"};
                            tableNotifySuccess(event.getSender(), "Lista de Promociones", enhancedHeaders, (ArrayList<String[]>) promocionesData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "No hay promociones", 
                                "📝 **No hay promociones registradas en el sistema.**");
                        }
                    }
                    break;
                    
                case Token.ADD:
                    if (event.getParams() != null && event.getParams().size() >= 4) {
                        // Comando: promocion add <nombre, fecha_inicio, fecha_fin, descuento, producto_id>
                        String nombre = event.getParams().get(0);
                        String fechaInicio = event.getParams().get(1);
                        String fechaFin = event.getParams().get(2);
                        String descuento = event.getParams().get(3);
                        Integer productoId = null;
                        
                        if (event.getParams().size() >= 5) {
                            productoId = Integer.parseInt(event.getParams().get(4));
                        }
                        
                        List<String[]> promocionData = nPromocion.save(nombre, fechaInicio, fechaFin, descuento, productoId);
                        tableNotifySuccess(event.getSender(), "Promoción guardada correctamente", DPromocion.HEADERS, (ArrayList<String[]>) promocionData, event.getCommand());
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "promocion add &lt;nombre, fecha_inicio, fecha_fin, descuento, producto_id&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "promocion add Descuento Verano, 2024-06-01, 2024-08-31, 20%, 1");
                    }
                    break;
                    
                case Token.MODIFY:
                    if (event.getParams() != null && event.getParams().size() >= 5) {
                        // Comando: promocion modify <id, nombre, fecha_inicio, fecha_fin, descuento, producto_id>
                        int id = Integer.parseInt(event.getParams().get(0));
                        String nombre = event.getParams().get(1);
                        String fechaInicio = event.getParams().get(2);
                        String fechaFin = event.getParams().get(3);
                        String descuento = event.getParams().get(4);
                        Integer productoId = null;
                        
                        if (event.getParams().size() >= 6) {
                            productoId = Integer.parseInt(event.getParams().get(5));
                        }
                        
                        List<String[]> promocionData = nPromocion.update(id, nombre, fechaInicio, fechaFin, descuento, productoId);
                        tableNotifySuccess(event.getSender(), "Promoción modificada correctamente", DPromocion.HEADERS, (ArrayList<String[]>) promocionData, event.getCommand());
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "promocion modify &lt;id, nombre, fecha_inicio, fecha_fin, descuento, producto_id&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "promocion modify 1, Descuento Verano, 2024-06-01, 2024-08-31, 25%, 1");
                    }
                    break;
                    
                case Token.DELETE:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        // Comando: promocion delete <id>
                        int id = Integer.parseInt(event.getParams().get(0));
                        
                        boolean success = nPromocion.delete(id);
                        if (success) {
                            simpleNotifySuccess(event.getSender(), 
                                "✅ **Promoción eliminada exitosamente**\n\n" +
                                "🗑️ **ID eliminado:** " + id);
                        } else {
                            simpleNotify(event.getSender(), "Error al eliminar", 
                                "❌ **No se pudo eliminar la promoción con ID: " + id + "**\n\n" +
                                "📝 **Verifique que la promoción existe.**");
                        }
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "promocion delete &lt;id&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "promocion delete 1");
                    }
                    break;
                    
                default:
                    simpleNotify(event.getSender(), "Comando no reconocido", 
                        "❌ **Comando de promoción no reconocido.**\n\n" +
                        "📋 **Comandos disponibles:**\n" +
                        "• promocion get\n" +
                        "• promocion get &lt;id&gt;\n" +
                        "• promocion get &lt;producto_id&gt;\n" +
                        "• promocion add &lt;nombre, fecha_inicio, fecha_fin, descuento, producto_id&gt;\n" +
                        "• promocion modify &lt;id, nombre, fecha_inicio, fecha_fin, descuento, producto_id&gt;\n" +
                        "• promocion delete &lt;id&gt;");
                    break;
            }
        } catch (SQLException e) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, "Error en método promocion para usuario: " + event.getSender(), e);
            simpleNotify(event.getSender(), "Error de Sistema", 
                "❌ **Se ha producido un error al procesar su solicitud.**\n\n" +
                "🔧 **Error en promocion:** " + e.getMessage());
        }
    }











    @Override
    public void error(ParamsAction event) {
        System.out.println("=== ERROR HANDLER ===");
        System.out.println("Sender: " + event.getSender());
        System.out.println("Command: " + event.getCommand());
        System.out.println("Action: " + event.getAction());
        System.out.println("Params: " + event.getParams());
        
        handleError(event.getAction(), event.getSender(), event.getParams());
    }

    @Override
    public void help(ParamsAction event) {
        try {
            if (event.getAction() == Token.GET) {
                String userEmail = event.getSender();
                
                // Log para debugging
                Logger.getLogger(EmailApp.class.getName()).info("Help solicitado por: " + userEmail);
                System.out.println("=== HELP SOLICITADO ===");
                System.out.println("Usuario: " + userEmail);
                System.out.println("Action: " + event.getAction());
                
                // Verificar si el usuario existe en la base de datos
                if (!nUsuario.emailExists(userEmail)) {
                    System.out.println("=== USUARIO NO REGISTRADO ===");
                    // El usuario no existe en la base de datos
                    simpleNotify(userEmail, "Acceso Denegado", 
                        "❌ **Su correo electrónico no está registrado en nuestro sistema.**\n\n" +
                        "🔐 **Para acceder a los comandos del sistema, debe registrarse primero.**\n\n" +
                        "📧 **Use el siguiente comando para registrarse:**\n" +
                        "**register user &lt;nombre, celular, email, genero, password, nit&gt;**\n\n" +
                        "📋 **Ejemplo:**\n" +
                        "register user Juan Pérez, 70012345, juan@email.com, masculino, miPassword123, 1234567890\n\n" +
                        "📧 **O contacte al administrador:**\n" +
                        "• Email: admin@tecnoweb.org.bo\n" +
                        "• Teléfono: +591-2-1234567\n\n" +
                        "✅ **Una vez registrado, podrá acceder a todos los comandos del sistema.**");
                    return;
                }
                
                // El usuario existe, verificar si tiene registro de cliente
                List<String[]> userData = nUsuario.findByEmail(userEmail);
                if (!userData.isEmpty()) {
                    int userId = Integer.parseInt(userData.get(0)[0]); // Obtener el ID del usuario
                    
                    if (!nUsuario.isCliente(userId)) {
                        System.out.println("=== USUARIO SIN REGISTRO DE CLIENTE ===");
                        // El usuario existe pero no tiene registro de cliente
                        simpleNotify(userEmail, "Registro de Cliente Requerido", 
                            "⚠️ **Su cuenta de usuario existe pero no tiene registro de cliente.**\n\n" +
                            "🔐 **Para acceder a los comandos del sistema, debe completar su registro como cliente.**\n\n" +
                            "📧 **Use el siguiente comando para registrarse como cliente:**\n" +
                            "**register cliente &lt;nit&gt;**\n\n" +
                            "📋 **Ejemplo:**\n" +
                            "register cliente 1234567890\n\n" +
                            "📧 **O contacte al administrador:**\n" +
                            "• Email: admin@tecnoweb.org.bo\n" +
                            "• Teléfono: +591-2-1234567\n\n" +
                            "✅ **Una vez completado el registro como cliente, podrá acceder a todos los comandos.**");
                        return;
                    }
                }
                
                System.out.println("=== USUARIO AUTORIZADO - MOSTRANDO AYUDA ===");
                // El usuario existe y tiene registro de cliente, mostrar ayuda completa
                String[] headers = {"Categoría", "Comando", "Descripción"};
                ArrayList<String[]> data = new ArrayList<>();

                // Comandos de Registro
                data.add(new String[]{"Registro", "register user &lt;nombre, celular, email, genero, password, nit&gt;", "Registra un nuevo usuario y cliente"});

                // Usuarios
                data.add(new String[]{"Usuarios", "usuario get", "Obtiene todos los usuarios"});

                // Promociones
                data.add(new String[]{"Promociones", "promocion get", "Obtener todas las promociones"});

        // Comandos de Categorías
        data.add(new String[]{"Categorías", "categoria get", "Obtiene todas las categorías"});
        
        // Comandos de Productos
        data.add(new String[]{"Productos", "producto get", "Obtiene todos los productos"});
        
        // Comandos de Tipos de Pago
        data.add(new String[]{"Tipos de Pago", "tipopago get", "Obtiene todos los tipos de pago"});
        
        // Comandos de Clientes
        data.add(new String[]{"Clientes", "cliente get", "Obtiene todos los clientes"});
        
        // Comandos de Carrito
        data.add(new String[]{"Carrito", "carrito get", "Obtiene tu carrito activo con productos"});
        data.add(new String[]{"Carrito", "carrito add &lt;producto_id, cantidad&gt;", "Agrega producto al carrito"});
        data.add(new String[]{"Carrito", "carrito modify &lt;detalle_id, cantidad&gt;", "Modifica cantidad de producto"});
        data.add(new String[]{"Carrito", "carrito delete &lt;detalle_id&gt;", "Elimina producto del carrito"});

        // Sistema de Ventas
        data.add(new String[]{"Nota de Venta", "notaventa get", "Obtiene mis notas de venta"});

        data.add(new String[]{"Pedido", "pedido get", "Obtiene mis pedidos"});

        data.add(new String[]{"Dirección", "direccion get", "Obtiene todas las direcciones"});

        // Comando de Compra Completa
                        data.add(new String[]{"Compra", "comprar &lt;tipo_pago_id, url_google_maps&gt;", "Realiza compra completa desde carrito"});

                System.out.println("=== ENVIANDO RESPUESTA HELP ===");
                System.out.println("Filas de datos: " + data.size());
                System.out.println("Headers: " + headers.length);
                
                // Mostrar todos los comandos disponibles de manera organizada
                tableNotifySuccess(event.getSender(), "✅ **Comandos disponibles** - Acceso autorizado", headers, data);
                
                System.out.println("=== HELP ENVIADO EXITOSAMENTE ===");
            }
        } catch (Exception ex) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, 
                "Error en método help para usuario: " + event.getSender(), ex);
            System.err.println("=== ERROR EN HELP ===");
            System.err.println("Usuario: " + event.getSender());
            System.err.println("Error: " + ex.getMessage());
            ex.printStackTrace();
            handleError(CONSTRAINTS_ERROR, event.getSender(), Collections.singletonList("Error en help: " + ex.getMessage()));
        }
    }

    @Override
    public void register(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                case Token.USER:
                    if (event.getParams() != null && event.getParams().size() >= 6) {
                        // Comando: register user <nombre, celular, email, genero, password, nit>
                        String nombre = event.getParams().get(0);
                        String celular = event.getParams().get(1);
                        String email = event.getParams().get(2);
                        String genero = event.getParams().get(3).toLowerCase(); // Normalizar a minúsculas
                        String password = event.getParams().get(4);
                        String nit = event.getParams().get(5);
                        
                        // Verificar que el email no exista ya
                        if (nUsuario.emailExists(email)) {
                            simpleNotify(event.getSender(), "Error de Registro", 
                                "❌ **El email ya está registrado en el sistema.**\n\n" +
                                "🔐 **Si ya tiene una cuenta, use el comando:**\n" +
                                "help get\n\n" +
                                "📧 **Si olvidó su contraseña, contacte al administrador:**\n" +
                                "• Email: admin@tecnoweb.org.bo");
                            return;
                        }
                        
                        // Registrar el usuario y cliente en una sola transacción
                        List<String[]> userData = nUsuario.registerUserAndCliente(nombre, celular, email, genero, password, nit);
                        
                        simpleNotifySuccess(event.getSender(), 
                            "✅ **Usuario y Cliente registrados exitosamente**\n\n" +
                            "📋 **Datos registrados:**\n" +
                            "• Nombre: " + nombre + "\n" +
                            "• Email: " + email + "\n" +
                            "• Celular: " + celular + "\n" +
                            "• Género: " + genero + " (normalizado)\n" +
                            "• NIT: " + nit + "\n\n" +
                            "🔐 **Ahora tiene acceso completo a todos los comandos del sistema.**\n\n" +
                            "📧 **Use el comando para ver todos los comandos disponibles:**\n" +
                            "help get");
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Registro", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "register user &lt;nombre, celular, email, genero, password, nit&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "register user Juan Pérez, 70012345, juan@email.com, masculino, miPassword123, 1234567890\n\n" +
                            "📝 **Nota:** El género debe ser en minúsculas: masculino, femenino, otro");
                    }
                    break;
                    
                case Token.MODIFY:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        // Comando: register cliente <nit>
                        String nit = event.getParams().get(0);
                        String userEmail = event.getSender();
                        
                        // Verificar que el usuario existe
                        if (!nUsuario.emailExists(userEmail)) {
                            simpleNotify(event.getSender(), "Error de Registro", 
                                "❌ **Su email no está registrado en el sistema.**\n\n" +
                                "🔐 **Primero debe registrarse como usuario:**\n" +
                                "register user &lt;nombre, celular, email, genero, password, nit&gt;");
                            return;
                        }
                        
                        // Obtener el ID del usuario
                        List<String[]> userData = nUsuario.findByEmail(userEmail);
                        if (userData.isEmpty()) {
                            simpleNotify(event.getSender(), "Error de Registro", 
                                "❌ **Error al obtener datos del usuario.**");
                            return;
                        }
                        
                        int userId = Integer.parseInt(userData.get(0)[0]);
                        
                        // Verificar que no sea ya cliente
                        if (nUsuario.isCliente(userId)) {
                            simpleNotify(event.getSender(), "Error de Registro", 
                                "⚠️ **Ya está registrado como cliente.**\n\n" +
                                "🔐 **Use el comando para ver todos los comandos disponibles:**\n" +
                                "help get");
                            return;
                        }
                        
                        // Registrar como cliente
                        boolean success = nUsuario.registerCliente(userId, nit);
                        
                        if (success) {
                            simpleNotifySuccess(event.getSender(), 
                                "✅ **Cliente registrado exitosamente**\n\n" +
                                "📋 **Datos registrados:**\n" +
                                "• NIT: " + nit + "\n" +
                                "• Email: " + userEmail + "\n\n" +
                                "🔐 **Ahora tiene acceso completo a todos los comandos.**\n\n" +
                                "📧 **Use el comando para ver todos los comandos disponibles:**\n" +
                                "help get");
                        } else {
                            simpleNotify(event.getSender(), "Error de Registro", 
                                "❌ **Error al registrar como cliente.**\n\n" +
                                "📧 **Contacte al administrador:**\n" +
                                "• Email: admin@tecnoweb.org.bo");
                        }
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Registro", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "register cliente &lt;nit&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "register cliente 1234567890");
                    }
                    break;
                    
                default:
                    simpleNotify(event.getSender(), "Error de Registro", 
                        "❌ **Comando de registro no reconocido.**\n\n" +
                        "📋 **Comandos disponibles:**\n" +
                        "• register user &lt;nombre, celular, email, genero, password, nit&gt;\n" +
                        "• register cliente &lt;nit&gt;");
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, 
                "Error en método register para usuario: " + event.getSender(), ex);
            handleError(CONSTRAINTS_ERROR, event.getSender(), Collections.singletonList("Error en registro: " + ex.getMessage()));
        } catch (Exception ex) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, 
                "Error inesperado en método register para usuario: " + event.getSender(), ex);
            handleError(CONSTRAINTS_ERROR, event.getSender(), Collections.singletonList("Error inesperado en registro: " + ex.getMessage()));
        }
    }

    @Override
    public void categoria(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.GET:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        // Comando: categoria get <id>
                        int id = Integer.parseInt(event.getParams().get(0));
                        List<String[]> categoriaData = nCategoria.getById(id);
                        
                        if (!categoriaData.isEmpty()) {
                            tableNotifySuccess(event.getSender(), "Categoría encontrada", DCategoria.HEADERS, (ArrayList<String[]>) categoriaData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Categoría no encontrada", 
                                "❌ **No se encontró la categoría con ID: " + id + "**");
                        }
                    } else {
                        // Comando: categoria get (todas las categorías)
                        List<String[]> categoriasData = nCategoria.getAll();
                        
                        if (!categoriasData.isEmpty()) {
                            tableNotifySuccess(event.getSender(), "Lista de Categorías", DCategoria.HEADERS, (ArrayList<String[]>) categoriasData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "No hay categorías", 
                                "📝 **No hay categorías registradas en el sistema.**");
                        }
                    }
                    break;
                    
                case Token.ADD:
                    if (event.getParams() != null && event.getParams().size() >= 2) {
                        // Comando: categoria add <nombre, descripcion>
                        String nombre = event.getParams().get(0);
                        String descripcion = event.getParams().get(1);
                        
                        List<String[]> categoriaData = nCategoria.save(nombre, descripcion);
                        tableNotifySuccess(event.getSender(), "Categoría guardada correctamente", DCategoria.HEADERS, (ArrayList<String[]>) categoriaData, event.getCommand());
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "categoria add &lt;nombre, descripcion&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "categoria add Electrónicos, Productos electrónicos y tecnología");
                    }
                    break;
                    
                case Token.MODIFY:
                    if (event.getParams() != null && event.getParams().size() >= 3) {
                        // Comando: categoria modify <id, nombre, descripcion>
                        int id = Integer.parseInt(event.getParams().get(0));
                        String nombre = event.getParams().get(1);
                        String descripcion = event.getParams().get(2);
                        
                        List<String[]> categoriaData = nCategoria.update(id, nombre, descripcion);
                        tableNotifySuccess(event.getSender(), "Categoría modificada correctamente", DCategoria.HEADERS, (ArrayList<String[]>) categoriaData, event.getCommand());
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "categoria modify &lt;id, nombre, descripcion&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "categoria modify 1, Electrónicos, Productos electrónicos y tecnología actualizada");
                    }
                    break;
                    
                case Token.DELETE:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        // Comando: categoria delete <id>
                        int id = Integer.parseInt(event.getParams().get(0));
                        
                        boolean success = nCategoria.delete(id);
                        if (success) {
                            simpleNotifySuccess(event.getSender(), 
                                "✅ **Categoría eliminada exitosamente**\n\n" +
                                "🗑️ **ID eliminado:** " + id);
                        } else {
                            simpleNotify(event.getSender(), "Error al eliminar", 
                                "❌ **No se pudo eliminar la categoría con ID: " + id + "**\n\n" +
                                "📝 **Verifique que la categoría existe y no tiene productos asociados.**");
                        }
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "categoria delete &lt;id&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "categoria delete 1");
                    }
                    break;
                    
                default:
                    simpleNotify(event.getSender(), "Comando no reconocido", 
                        "❌ **Comando de categoría no reconocido.**\n\n" +
                        "📋 **Comandos disponibles:**\n" +
                        "• categoria get\n" +
                        "• categoria get &lt;id&gt;\n" +
                        "• categoria add &lt;nombre, descripcion&gt;\n" +
                        "• categoria modify &lt;id, nombre, descripcion&gt;\n" +
                        "• categoria delete &lt;id&gt;");
                    break;
            }
        } catch (SQLException e) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, "Error en método categoria para usuario: " + event.getSender(), e);
            simpleNotify(event.getSender(), "Error de Sistema", 
                "❌ **Se ha producido un error al procesar su solicitud.**\n\n" +
                "🔧 **Error en categoría:** " + e.getMessage());
        }
    }

    @Override
    public void producto(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.GET:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        // Comando: producto get <id>
                        int id = Integer.parseInt(event.getParams().get(0));
                        List<String[]> productoData = nProducto.getById(id);
                        
                        if (!productoData.isEmpty()) {
                            tableNotifySuccess(event.getSender(), "Producto encontrado", DProducto.HEADERS, (ArrayList<String[]>) productoData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Producto no encontrado", 
                                "❌ **No se encontró el producto con ID: " + id + "**");
                        }
                    } else {
                        // Comando: producto get (todos los productos)
                        List<String[]> productosData = nProducto.getAll();
                        
                        if (!productosData.isEmpty()) {
                            tableNotifySuccess(event.getSender(), "Lista de Productos", DProducto.HEADERS, (ArrayList<String[]>) productosData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "No hay productos", 
                                "📝 **No hay productos registrados en el sistema.**");
                        }
                    }
                    break;
                    
                case Token.ADD:
                    if (event.getParams() != null && event.getParams().size() >= 7) {
                        // Comando: producto add <cod_producto, nombre, precio_compra, precio_venta, imagen, descripcion, categoria_id>
                        String codProducto = event.getParams().get(0);
                        String nombre = event.getParams().get(1);
                        double precioCompra = Double.parseDouble(event.getParams().get(2));
                        double precioVenta = Double.parseDouble(event.getParams().get(3));
                        String imagen = event.getParams().get(4);
                        String descripcion = event.getParams().get(5);
                        int categoriaId = Integer.parseInt(event.getParams().get(6));
                        
                        List<String[]> productoData = nProducto.save(codProducto, nombre, precioCompra, precioVenta, imagen, descripcion, categoriaId);
                        tableNotifySuccess(event.getSender(), "Producto guardado correctamente", DProducto.HEADERS, (ArrayList<String[]>) productoData, event.getCommand());
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "producto add &lt;cod_producto, nombre, precio_compra, precio_venta, imagen, descripcion, categoria_id&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "producto add PROD001, Laptop HP, 800.00, 1200.00, laptop.jpg, Laptop HP Pavilion, 1");
                    }
                    break;
                    
                case Token.MODIFY:
                    if (event.getParams() != null && event.getParams().size() >= 8) {
                        // Comando: producto modify <id, cod_producto, nombre, precio_compra, precio_venta, imagen, descripcion, categoria_id>
                        int id = Integer.parseInt(event.getParams().get(0));
                        String codProducto = event.getParams().get(1);
                        String nombre = event.getParams().get(2);
                        double precioCompra = Double.parseDouble(event.getParams().get(3));
                        double precioVenta = Double.parseDouble(event.getParams().get(4));
                        String imagen = event.getParams().get(5);
                        String descripcion = event.getParams().get(6);
                        int categoriaId = Integer.parseInt(event.getParams().get(7));
                        
                        List<String[]> productoData = nProducto.update(id, codProducto, nombre, precioCompra, precioVenta, imagen, descripcion, categoriaId);
                        tableNotifySuccess(event.getSender(), "Producto modificado correctamente", DProducto.HEADERS, (ArrayList<String[]>) productoData, event.getCommand());
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "producto modify &lt;id, cod_producto, nombre, precio_compra, precio_venta, imagen, descripcion, categoria_id&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "producto modify 1, PROD001, Laptop HP Actualizada, 850.00, 1250.00, laptop_new.jpg, Laptop HP Pavilion actualizada, 1");
                    }
                    break;
                    
                case Token.DELETE:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        // Comando: producto delete <id>
                        int id = Integer.parseInt(event.getParams().get(0));
                        
                        boolean success = nProducto.delete(id);
                        if (success) {
                            simpleNotifySuccess(event.getSender(), 
                                "✅ **Producto eliminado exitosamente**\n\n" +
                                "🗑️ **ID eliminado:** " + id);
                        } else {
                            simpleNotify(event.getSender(), "Error al eliminar", 
                                "❌ **No se pudo eliminar el producto con ID: " + id + "**\n\n" +
                                "📝 **Verifique que el producto existe.**");
                        }
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "producto delete &lt;id&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "producto delete 1");
                    }
                    break;
                    
                default:
                    simpleNotify(event.getSender(), "Comando no reconocido", 
                        "❌ **Comando de producto no reconocido.**\n\n" +
                        "📋 **Comandos disponibles:**\n" +
                        "• producto get\n" +
                        "• producto get &lt;id&gt;\n" +
                        "• producto add &lt;cod_producto, nombre, precio_compra, precio_venta, imagen, descripcion, categoria_id&gt;\n" +
                        "• producto modify &lt;id, cod_producto, nombre, precio_compra, precio_venta, imagen, descripcion, categoria_id&gt;\n" +
                        "• producto delete &lt;id&gt;");
                    break;
            }
        } catch (SQLException e) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, "Error en método producto para usuario: " + event.getSender(), e);
            simpleNotify(event.getSender(), "Error de Sistema", 
                "❌ **Se ha producido un error al procesar su solicitud.**\n\n" +
                "🔧 **Error en producto:** " + e.getMessage());
        }
    }

    @Override
    public void tipopago(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.GET:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        // Comando: tipopago get <id>
                        int id = Integer.parseInt(event.getParams().get(0));
                        List<String[]> tipopagoData = nTipoPago.getById(id);
                        
                        if (!tipopagoData.isEmpty()) {
                            tableNotifySuccess(event.getSender(), "Tipo de Pago encontrado", DTipoPago.HEADERS, (ArrayList<String[]>) tipopagoData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Tipo de Pago no encontrado", 
                                "❌ **No se encontró el tipo de pago con ID: " + id + "**");
                        }
                    } else {
                        // Comando: tipopago get (todos los tipos de pago)
                        List<String[]> tipopagosData = nTipoPago.getAll();
                        
                        if (!tipopagosData.isEmpty()) {
                            tableNotifySuccess(event.getSender(), "Lista de Tipos de Pago", DTipoPago.HEADERS, (ArrayList<String[]>) tipopagosData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "No hay tipos de pago", 
                                "📝 **No hay tipos de pago registrados en el sistema.**");
                        }
                    }
                    break;
                    
                case Token.ADD:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        // Comando: tipopago add <tipo_pago>
                        String tipoPago = event.getParams().get(0);
                        
                        List<String[]> tipopagoData = nTipoPago.save(tipoPago);
                        tableNotifySuccess(event.getSender(), "Tipo de Pago guardado correctamente", DTipoPago.HEADERS, (ArrayList<String[]>) tipopagoData, event.getCommand());
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "tipopago add &lt;tipo_pago&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "tipopago add Efectivo");
                    }
                    break;
                    
                case Token.MODIFY:
                    if (event.getParams() != null && event.getParams().size() >= 2) {
                        // Comando: tipopago modify <id, tipo_pago>
                        int id = Integer.parseInt(event.getParams().get(0));
                        String tipoPago = event.getParams().get(1);
                        
                        List<String[]> tipopagoData = nTipoPago.update(id, tipoPago);
                        tableNotifySuccess(event.getSender(), "Tipo de Pago modificado correctamente", DTipoPago.HEADERS, (ArrayList<String[]>) tipopagoData, event.getCommand());
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "tipopago modify &lt;id, tipo_pago&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "tipopago modify 1, Efectivo");
                    }
                    break;
                    
                case Token.DELETE:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        // Comando: tipopago delete <id>
                        int id = Integer.parseInt(event.getParams().get(0));
                        
                        boolean success = nTipoPago.delete(id);
                        if (success) {
                            simpleNotifySuccess(event.getSender(), 
                                "✅ **Tipo de Pago eliminado exitosamente**\n\n" +
                                "🗑️ **ID eliminado:** " + id);
                        } else {
                            simpleNotify(event.getSender(), "Error al eliminar", 
                                "❌ **No se pudo eliminar el tipo de pago con ID: " + id + "**\n\n" +
                                "📝 **Verifique que el tipo de pago existe.**");
                        }
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "tipopago delete &lt;id&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "tipopago delete 1");
                    }
                    break;
                    
                default:
                    simpleNotify(event.getSender(), "Comando no reconocido", 
                        "❌ **Comando de tipopago no reconocido.**\n\n" +
                        "📋 **Comandos disponibles:**\n" +
                        "• tipopago get\n" +
                        "• tipopago get &lt;id&gt;\n" +
                        "• tipopago add &lt;nombre, descripcion&gt;\n" +
                        "• tipopago modify &lt;id, nombre, descripcion&gt;\n" +
                        "• tipopago delete &lt;id&gt;");
                    break;
            }
        } catch (SQLException e) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, "Error en método tipopago para usuario: " + event.getSender(), e);
            simpleNotify(event.getSender(), "Error de Sistema", 
                "❌ **Se ha producido un error al procesar su solicitud.**\n\n" +
                "🔧 **Error en tipopago:** " + e.getMessage());
        }
    }


    @Override
    public void onReceiptEmail(List<Email> emails) {
        for (Email email : emails) {
            System.out.println("=== PROCESANDO EMAIL ===");
            System.out.println("From: " + email.getFrom());
            System.out.println("Subject: " + email.getSubject());
            System.out.println("Message: " + email.getMessage());
            
            Interpreter interpreter = new Interpreter(email.getSubject(), email.getFrom());
            interpreter.setCasoUsoListener(this);
            Thread thread = new Thread(interpreter);
            thread.start();
        }
    }

    private void handleError(int type, String email, List<String> args) {
        Email emailObject = new Email(email, "❌ Error de Sistema",
                HtmlRes.generateErrorMessage("Error de Sistema", 
                    "Se ha producido un error al procesar su solicitud." + 
                    (args != null && !args.isEmpty() ? "\n\n" + args.get(0) : "")));
        sendEmail(emailObject);
    }

    private void simpleNotifySuccess(String email, String message) {
        Email emailObject = new Email(email, "✅ Operación Exitosa",
                HtmlRes.generateSuccessMessage("Operación Exitosa", message));
        sendEmail(emailObject);
    }

    private void tableNotifySuccess(String email, String title, String[] headers, ArrayList<String[]> data) {
        System.out.println("=== TABLE NOTIFY SUCCESS ===");
        System.out.println("Email: " + email);
        System.out.println("Title: " + title);
        System.out.println("Headers count: " + headers.length);
        System.out.println("Data rows: " + data.size());
        
        try {
            Email emailObject = new Email(email, Email.SUBJECT,
                    HtmlRes.generateTable(title, headers, data));
            System.out.println("Email object created successfully");
            sendEmail(emailObject);
            System.out.println("Email sent successfully");
        } catch (Exception ex) {
            System.err.println("Error in tableNotifySuccess: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void tableNotifySuccess(String email, String title, String[] headers, ArrayList<String[]> data, String command) {
        System.out.println("=== TABLE NOTIFY SUCCESS (WITH COMMAND) ===");
        System.out.println("Email: " + email);
        System.out.println("Title: " + title);
        System.out.println("Command: " + command);
        System.out.println("Headers count: " + headers.length);
        System.out.println("Data rows: " + data.size());
        
        try {
            Email emailObject = new Email(email, command,
                    HtmlRes.generateTable(title, headers, data));
            System.out.println("Email object created successfully");
            sendEmail(emailObject);
            System.out.println("Email sent successfully");
        } catch (Exception ex) {
            System.err.println("Error in tableNotifySuccess with command: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void sendEmail(Email email) {
        EmailSend sendEmail = new EmailSend(email);
        Thread thread = new Thread(sendEmail);
        thread.start();
    }

    private void simpleNotify(String email, String title, String message) {
        Email emailObject = new Email(email, title,
                HtmlRes.generateText(new String[]{
                        title,
                        message
                }));
        sendEmail(emailObject);
    }

    @Override
    public void cliente(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.GET:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        String param = event.getParams().get(0);
                        
                        // Intentar como ID numérico primero
                        try {
                            int id = Integer.parseInt(param);
                            List<String[]> clienteData = nCliente.getById(id);
                            
                            if (!clienteData.isEmpty()) {
                                String[] enhancedHeaders = {"ID", "NIT", "User ID", "Nombre", "Email", "Celular", "Género", "Creado", "Actualizado"};
                                tableNotifySuccess(event.getSender(), "Cliente encontrado", enhancedHeaders, (ArrayList<String[]>) clienteData, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "Cliente no encontrado", 
                                    "❌ **No se encontró el cliente con ID: " + id + "**");
                            }
                        } catch (NumberFormatException e) {
                            // Si no es número, intentar como NIT
                            List<String[]> clienteData = nCliente.getByNit(param);
                            
                            if (!clienteData.isEmpty()) {
                                String[] enhancedHeaders = {"ID", "NIT", "User ID", "Nombre", "Email", "Celular", "Género", "Creado", "Actualizado"};
                                tableNotifySuccess(event.getSender(), "Cliente encontrado por NIT", enhancedHeaders, (ArrayList<String[]>) clienteData, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "Cliente no encontrado", 
                                    "❌ **No se encontró el cliente con NIT: " + param + "**");
                            }
                        }
                    } else {
                        // Comando: cliente get (todos los clientes)
                        List<String[]> clientesData = nCliente.getAll();
                        
                        if (!clientesData.isEmpty()) {
                            String[] enhancedHeaders = {"ID", "NIT", "User ID", "Nombre", "Email", "Celular", "Género", "Creado", "Actualizado"};
                            tableNotifySuccess(event.getSender(), "Lista de Clientes", enhancedHeaders, (ArrayList<String[]>) clientesData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "No hay clientes", 
                                "📝 **No hay clientes registrados en el sistema.**");
                        }
                    }
                    break;
                    
                case Token.ADD:
                    if (event.getParams() != null && event.getParams().size() >= 2) {
                        // Comando: cliente add <nit, user_id>
                        String nit = event.getParams().get(0);
                        int userId = Integer.parseInt(event.getParams().get(1));
                        
                        List<String[]> clienteData = nCliente.save(nit, userId);
                        tableNotifySuccess(event.getSender(), "Cliente guardado correctamente", DCliente.HEADERS, (ArrayList<String[]>) clienteData, event.getCommand());
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "cliente add &lt;nit, user_id&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "cliente add 1234567890, 1");
                    }
                    break;
                    
                case Token.MODIFY:
                    if (event.getParams() != null && event.getParams().size() >= 2) {
                        // Comando: cliente modify <id, nit>
                        int id = Integer.parseInt(event.getParams().get(0));
                        String nit = event.getParams().get(1);
                        
                        List<String[]> clienteData = nCliente.update(id, nit);
                        tableNotifySuccess(event.getSender(), "Cliente modificado correctamente", DCliente.HEADERS, (ArrayList<String[]>) clienteData, event.getCommand());
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "cliente modify &lt;id, nit&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "cliente modify 1, 9876543210");
                    }
                    break;
                    
                case Token.DELETE:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        // Comando: cliente delete <id>
                        int id = Integer.parseInt(event.getParams().get(0));
                        
                        boolean success = nCliente.delete(id);
                        if (success) {
                            simpleNotifySuccess(event.getSender(), 
                                "✅ **Cliente eliminado exitosamente**\n\n" +
                                "🗑️ **ID eliminado:** " + id);
                        } else {
                            simpleNotify(event.getSender(), "Error al eliminar", 
                                "❌ **No se pudo eliminar el cliente con ID: " + id + "**\n\n" +
                                "📝 **Verifique que el cliente existe.**");
                        }
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "cliente delete &lt;id&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "cliente delete 1");
                    }
                    break;
                    
                default:
                    simpleNotify(event.getSender(), "Comando no reconocido", 
                        "❌ **Comando de cliente no reconocido.**\n\n" +
                        "📋 **Comandos disponibles:**\n" +
                        "• cliente get\n" +
                        "• cliente get &lt;id&gt;\n" +
                        "• cliente get &lt;nit&gt;\n" +
                        "• cliente add &lt;nit, user_id&gt;\n" +
                        "• cliente modify &lt;id, nit&gt;\n" +
                        "• cliente delete &lt;id&gt;");
                    break;
            }
        } catch (SQLException e) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, "Error en método cliente para usuario: " + event.getSender(), e);
            simpleNotify(event.getSender(), "Error de Sistema", 
                "❌ **Se ha producido un error al procesar su solicitud.**\n\n" +
                "🔧 **Error en cliente:** " + e.getMessage());
        }
    }

    @Override
    public void notaVenta(ParamsAction event) {
        try {
            System.out.println("=== PROCESANDO NOTA VENTA PARA USUARIO ===");
            System.out.println("Usuario: " + event.getSender());
            System.out.println("Action: " + event.getAction());
            System.out.println("Params: " + event.getParams());
            
            switch (event.getAction()) {
                case Token.GET:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        String param = event.getParams().get(0);
                        
                        // Intentar como ID numérico primero
                        try {
                            int id = Integer.parseInt(param);
                            List<String[]> notaVentaData = nNotaVenta.getById(id);
                            
                            if (!notaVentaData.isEmpty()) {
                                String[] enhancedHeaders = {"ID", "Cliente ID", "Pedido ID", "Fecha", "Total", "Estado", "Observaciones", "NIT", "Nombre", "Email", "Creado", "Actualizado"};
                                tableNotifySuccess(event.getSender(), "Nota de Venta encontrada", enhancedHeaders, (ArrayList<String[]>) notaVentaData, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "Nota de Venta no encontrada", 
                                    "❌ **No se encontró la nota de venta con ID: " + id + "**");
                            }
                        } catch (NumberFormatException e) {
                            simpleNotify(event.getSender(), "Error de formato", 
                                "❌ **El ID debe ser un número válido**");
                        }
                    } else {
                        // Obtener todas las notas de venta del usuario
                        List<String[]> notasVenta = nNotaVenta.getByClienteEmail(event.getSender());
                        
                        if (!notasVenta.isEmpty()) {
                            String[] enhancedHeaders = {"ID", "Cliente ID", "Pedido ID", "Fecha", "Total", "Estado", "Observaciones", "NIT", "Nombre", "Email", "Creado", "Actualizado"};
                            tableNotifySuccess(event.getSender(), "Mis Notas de Venta", enhancedHeaders, (ArrayList<String[]>) notasVenta, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Sin notas de venta", 
                                "📋 **No tienes notas de venta registradas**");
                        }
                    }
                    break;
                    
                case Token.ADD:
                    // Crear nota de venta desde carrito
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        String observaciones = event.getParams().get(0);
                        Integer pedidoId = null;
                        
                        if (event.getParams().size() >= 2) {
                            try {
                                pedidoId = Integer.parseInt(event.getParams().get(1));
                            } catch (NumberFormatException e) {
                                // Si no es número, ignorar
                            }
                        }
                        
                        List<String[]> notaVenta = nNotaVenta.crearNotaVentaDesdeCarrito(event.getSender(), pedidoId, observaciones);
                        
                        if (!notaVenta.isEmpty()) {
                            String[] enhancedHeaders = {"ID", "Cliente ID", "Pedido ID", "Fecha", "Total", "Estado", "Observaciones", "Creado", "Actualizado"};
                            tableNotifySuccess(event.getSender(), "✅ Nota de Venta Creada", enhancedHeaders, (ArrayList<String[]>) notaVenta, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Error al crear nota de venta", 
                                "❌ **No se pudo crear la nota de venta**");
                        }
                    } else {
                        simpleNotify(event.getSender(), "Parámetros insuficientes", 
                            "❌ **Uso: notaventa add <observaciones> [pedido_id]**");
                    }
                    break;
                    
                case Token.MODIFY:
                    if (event.getParams() != null && event.getParams().size() >= 3) {
                        try {
                            int id = Integer.parseInt(event.getParams().get(0));
                            String estado = event.getParams().get(1);
                            String observaciones = event.getParams().get(2);
                            
                            List<String[]> notaVenta = nNotaVenta.update(id, estado, observaciones);
                            
                            if (!notaVenta.isEmpty()) {
                                String[] enhancedHeaders = {"ID", "Cliente ID", "Pedido ID", "Fecha", "Total", "Estado", "Observaciones", "Creado", "Actualizado"};
                                tableNotifySuccess(event.getSender(), "✅ Nota de Venta Actualizada", enhancedHeaders, (ArrayList<String[]>) notaVenta, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "Error al actualizar", 
                                    "❌ **No se pudo actualizar la nota de venta**");
                            }
                        } catch (NumberFormatException e) {
                            simpleNotify(event.getSender(), "Error de formato", 
                                "❌ **El ID debe ser un número válido**");
                        }
                    } else {
                        simpleNotify(event.getSender(), "Parámetros insuficientes", 
                            "❌ **Uso: notaventa modify <id, estado, observaciones>**");
                    }
                    break;
                    
                case Token.DELETE:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        try {
                            int id = Integer.parseInt(event.getParams().get(0));
                            
                            boolean deleted = nNotaVenta.delete(id);
                            
                            if (deleted) {
                                simpleNotifySuccess(event.getSender(), "✅ Nota de Venta eliminada exitosamente");
                            } else {
                                simpleNotify(event.getSender(), "Error al eliminar", 
                                    "❌ **No se pudo eliminar la nota de venta**");
                            }
                        } catch (NumberFormatException e) {
                            simpleNotify(event.getSender(), "Error de formato", 
                                "❌ **El ID debe ser un número válido**");
                        }
                    } else {
                        simpleNotify(event.getSender(), "Parámetros insuficientes", 
                            "❌ **Uso: notaventa delete <id>**");
                    }
                    break;
                    
                default:
                    simpleNotify(event.getSender(), "Acción no válida", 
                        "❌ **Acción no válida para nota de venta**\n\n" +
                        "📋 **Comandos disponibles:**\n" +
                        "• notaventa get - Obtener mis notas de venta\n" +
                        "• notaventa get <id> - Obtener nota de venta por ID\n" +
                        "• notaventa add <observaciones> [pedido_id] - Crear desde carrito\n" +
                        "• notaventa modify <id, estado, observaciones> - Actualizar\n" +
                        "• notaventa delete <id> - Eliminar");
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, 
                "Error SQL en notaVenta. Usuario: " + event.getSender() + 
                ", Action: " + event.getAction() + 
                ", Params: " + event.getParams(), ex);
            simpleNotify(event.getSender(), "Error de Base de Datos", 
                "❌ **Error al procesar la solicitud: " + ex.getMessage() + "**");
        } catch (Exception ex) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, 
                "Error inesperado en notaVenta. Usuario: " + event.getSender() + 
                ", Action: " + event.getAction() + 
                ", Params: " + event.getParams(), ex);
            simpleNotify(event.getSender(), "Error del Sistema", 
                "❌ **Error inesperado: " + ex.getMessage() + "**");
        }
    }

    @Override
    public void pedido(ParamsAction event) {
        try {
            System.out.println("=== PROCESANDO PEDIDO PARA USUARIO ===");
            System.out.println("Usuario: " + event.getSender());
            System.out.println("Action: " + event.getAction());
            System.out.println("Params: " + event.getParams());
            
            switch (event.getAction()) {
                case Token.GET:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        String param = event.getParams().get(0);
                        
                        // Intentar como ID numérico primero
                        try {
                            int id = Integer.parseInt(param);
                            List<String[]> pedidoData = nPedido.getById(id);
                            
                            if (!pedidoData.isEmpty()) {
                                String[] enhancedHeaders = {"ID", "Dirección ID", "Fecha", "Total", "Estado", "Fecha Envío", "Fecha Entrega", "Nombre Dirección", "Longitud", "Latitud", "Referencia", "Creado", "Actualizado"};
                                tableNotifySuccess(event.getSender(), "Pedido encontrado", enhancedHeaders, (ArrayList<String[]>) pedidoData, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "Pedido no encontrado", 
                                    "❌ **No se encontró el pedido con ID: " + id + "**");
                            }
                        } catch (NumberFormatException e) {
                            simpleNotify(event.getSender(), "Error de formato", 
                                "❌ **El ID debe ser un número válido**");
                        }
                    } else {
                        // Obtener pedidos del usuario autenticado
                        List<String[]> pedidos = nPedido.getByClienteEmail(event.getSender());
                        
                        if (!pedidos.isEmpty()) {
                            String[] enhancedHeaders = {"ID", "Dirección ID", "Fecha", "Total", "Estado", "Fecha Envío", "Fecha Entrega", "Nombre Dirección", "Longitud", "Latitud", "Referencia", "Creado", "Actualizado"};
                            tableNotifySuccess(event.getSender(), "Mis Pedidos", enhancedHeaders, (ArrayList<String[]>) pedidos, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Sin pedidos", 
                                "📋 **No tienes pedidos registrados**");
                        }
                    }
                    break;
                    
                case Token.ADD:
                    // Crear pedido con dirección
                    if (event.getParams() != null && event.getParams().size() >= 4) {
                        String nombreDireccion = event.getParams().get(0);
                        String urlGoogleMaps = event.getParams().get(1);
                        String referencia = event.getParams().get(2);
                        double total = Double.parseDouble(event.getParams().get(3));
                        
                        List<String[]> pedido = nPedido.crearPedidoDesdeGoogleMaps(nombreDireccion, urlGoogleMaps, referencia, total);
                        
                        if (!pedido.isEmpty()) {
                            String[] enhancedHeaders = {"ID", "Dirección ID", "Fecha", "Total", "Estado", "Fecha Envío", "Fecha Entrega", "Creado", "Actualizado"};
                            tableNotifySuccess(event.getSender(), "✅ Pedido Creado", enhancedHeaders, (ArrayList<String[]>) pedido, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Error al crear pedido", 
                                "❌ **No se pudo crear el pedido**");
                        }
                    } else {
                        simpleNotify(event.getSender(), "Parámetros insuficientes", 
                            "❌ **Uso: pedido add <nombre_direccion, url_google_maps, referencia, total>**");
                    }
                    break;
                    
                case Token.MODIFY:
                    if (event.getParams() != null && event.getParams().size() >= 2) {
                        try {
                            int id = Integer.parseInt(event.getParams().get(0));
                            String estado = event.getParams().get(1);
                            
                            List<String[]> pedido = nPedido.updateEstado(id, estado);
                            
                            if (!pedido.isEmpty()) {
                                String[] enhancedHeaders = {"ID", "Dirección ID", "Fecha", "Total", "Estado", "Fecha Envío", "Fecha Entrega", "Creado", "Actualizado"};
                                tableNotifySuccess(event.getSender(), "✅ Pedido Actualizado", enhancedHeaders, (ArrayList<String[]>) pedido, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "Error al actualizar", 
                                    "❌ **No se pudo actualizar el pedido**");
                            }
                        } catch (NumberFormatException e) {
                            simpleNotify(event.getSender(), "Error de formato", 
                                "❌ **El ID debe ser un número válido**");
                        }
                    } else {
                        simpleNotify(event.getSender(), "Parámetros insuficientes", 
                            "❌ **Uso: pedido modify <id, estado>**");
                    }
                    break;
                    
                case Token.DELETE:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        try {
                            int id = Integer.parseInt(event.getParams().get(0));
                            
                            boolean deleted = nPedido.delete(id);
                            
                            if (deleted) {
                                simpleNotifySuccess(event.getSender(), "✅ Pedido eliminado exitosamente");
                            } else {
                                simpleNotify(event.getSender(), "Error al eliminar", 
                                    "❌ **No se pudo eliminar el pedido**");
                            }
                        } catch (NumberFormatException e) {
                            simpleNotify(event.getSender(), "Error de formato", 
                                "❌ **El ID debe ser un número válido**");
                        }
                    } else {
                        simpleNotify(event.getSender(), "Parámetros insuficientes", 
                            "❌ **Uso: pedido delete <id>**");
                    }
                    break;
                    
                default:
                    simpleNotify(event.getSender(), "Acción no válida", 
                        "❌ **Acción no válida para pedido**\n\n" +
                        "📋 **Comandos disponibles:**\n" +
                        "• pedido get - Obtener todos los pedidos\n" +
                        "• pedido get <id> - Obtener pedido por ID\n" +
                        "• pedido add <nombre_direccion, url_google_maps, referencia, total> - Crear pedido\n" +
                        "• pedido modify <id, estado> - Actualizar estado\n" +
                        "• pedido delete <id> - Eliminar");
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, 
                "Error SQL en pedido. Usuario: " + event.getSender() + 
                ", Action: " + event.getAction() + 
                ", Params: " + event.getParams(), ex);
            simpleNotify(event.getSender(), "Error de Base de Datos", 
                "❌ **Error al procesar la solicitud: " + ex.getMessage() + "**");
        } catch (Exception ex) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, 
                "Error inesperado en pedido. Usuario: " + event.getSender() + 
                ", Action: " + event.getAction() + 
                ", Params: " + event.getParams(), ex);
            simpleNotify(event.getSender(), "Error del Sistema", 
                "❌ **Error inesperado: " + ex.getMessage() + "**");
        }
    }

    @Override
    public void direccion(ParamsAction event) {
        try {
            System.out.println("=== PROCESANDO DIRECCION PARA USUARIO ===");
            System.out.println("Usuario: " + event.getSender());
            System.out.println("Action: " + event.getAction());
            System.out.println("Params: " + event.getParams());
            
            switch (event.getAction()) {
                case Token.GET:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        String param = event.getParams().get(0);
                        
                        // Intentar como ID numérico primero
                        try {
                            int id = Integer.parseInt(param);
                            List<String[]> direccionData = nDireccion.getById(id);
                            
                            if (!direccionData.isEmpty()) {
                                String[] enhancedHeaders = {"ID", "Nombre", "Longitud", "Latitud", "Referencia", "Creado", "Actualizado"};
                                tableNotifySuccess(event.getSender(), "Dirección encontrada", enhancedHeaders, (ArrayList<String[]>) direccionData, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "Dirección no encontrada", 
                                    "❌ **No se encontró la dirección con ID: " + id + "**");
                            }
                        } catch (NumberFormatException e) {
                            // Si no es número, buscar por nombre
                            List<String[]> direcciones = nDireccion.getByNombre(param);
                            
                            if (!direcciones.isEmpty()) {
                                String[] enhancedHeaders = {"ID", "Nombre", "Longitud", "Latitud", "Referencia", "Creado", "Actualizado"};
                                tableNotifySuccess(event.getSender(), "Direcciones encontradas", enhancedHeaders, (ArrayList<String[]>) direcciones, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "Dirección no encontrada", 
                                    "❌ **No se encontró la dirección: " + param + "**");
                            }
                        }
                    } else {
                        // Obtener todas las direcciones
                        List<String[]> direcciones = nDireccion.getAll();
                        
                        if (!direcciones.isEmpty()) {
                            String[] enhancedHeaders = {"ID", "Nombre", "Longitud", "Latitud", "Referencia", "Creado", "Actualizado"};
                            tableNotifySuccess(event.getSender(), "Todas las Direcciones", enhancedHeaders, (ArrayList<String[]>) direcciones, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Sin direcciones", 
                                "📋 **No hay direcciones registradas**");
                        }
                    }
                    break;
                    
                case Token.ADD:
                    // Crear dirección desde Google Maps
                    if (event.getParams() != null && event.getParams().size() >= 3) {
                        String nombre = event.getParams().get(0);
                        String urlGoogleMaps = event.getParams().get(1);
                        String referencia = event.getParams().get(2);
                        
                        List<String[]> direccion = nDireccion.crearDesdeGoogleMaps(nombre, urlGoogleMaps, referencia);
                        
                        if (!direccion.isEmpty()) {
                            String[] enhancedHeaders = {"ID", "Nombre", "Longitud", "Latitud", "Referencia", "Creado", "Actualizado"};
                            tableNotifySuccess(event.getSender(), "✅ Dirección Creada", enhancedHeaders, (ArrayList<String[]>) direccion, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Error al crear dirección", 
                                "❌ **No se pudo crear la dirección**");
                        }
                    } else {
                        simpleNotify(event.getSender(), "Parámetros insuficientes", 
                            "❌ **Uso: direccion add <nombre, url_google_maps, referencia>**");
                    }
                    break;
                    
                case Token.MODIFY:
                    if (event.getParams() != null && event.getParams().size() >= 4) {
                        try {
                            int id = Integer.parseInt(event.getParams().get(0));
                            String nombre = event.getParams().get(1);
                            String urlGoogleMaps = event.getParams().get(2);
                            String referencia = event.getParams().get(3);
                            
                            // Extraer coordenadas de la URL
                            double[] coordenadas = NDireccion.extraerCoordenadasDeUrl(urlGoogleMaps);
                            Double longitud = null;
                            Double latitud = null;
                            
                            if (coordenadas != null) {
                                latitud = coordenadas[0];
                                longitud = coordenadas[1];
                            }
                            
                            List<String[]> direccion = nDireccion.update(id, nombre, longitud, latitud, referencia);
                            
                            if (!direccion.isEmpty()) {
                                String[] enhancedHeaders = {"ID", "Nombre", "Longitud", "Latitud", "Referencia", "Creado", "Actualizado"};
                                tableNotifySuccess(event.getSender(), "✅ Dirección Actualizada", enhancedHeaders, (ArrayList<String[]>) direccion, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "Error al actualizar", 
                                    "❌ **No se pudo actualizar la dirección**");
                            }
                        } catch (NumberFormatException e) {
                            simpleNotify(event.getSender(), "Error de formato", 
                                "❌ **El ID debe ser un número válido**");
                        }
                    } else {
                        simpleNotify(event.getSender(), "Parámetros insuficientes", 
                            "❌ **Uso: direccion modify <id, nombre, url_google_maps, referencia>**");
                    }
                    break;
                    
                case Token.DELETE:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        try {
                            int id = Integer.parseInt(event.getParams().get(0));
                            
                            boolean deleted = nDireccion.delete(id);
                            
                            if (deleted) {
                                simpleNotifySuccess(event.getSender(), "✅ Dirección eliminada exitosamente");
                            } else {
                                simpleNotify(event.getSender(), "Error al eliminar", 
                                    "❌ **No se pudo eliminar la dirección**");
                            }
                        } catch (NumberFormatException e) {
                            simpleNotify(event.getSender(), "Error de formato", 
                                "❌ **El ID debe ser un número válido**");
                        }
                    } else {
                        simpleNotify(event.getSender(), "Parámetros insuficientes", 
                            "❌ **Uso: direccion delete <id>**");
                    }
                    break;
                    
                default:
                    simpleNotify(event.getSender(), "Acción no válida", 
                        "❌ **Acción no válida para dirección**\n\n" +
                        "📋 **Comandos disponibles:**\n" +
                        "• direccion get - Obtener todas las direcciones\n" +
                        "• direccion get <id> - Obtener dirección por ID\n" +
                        "• direccion get <nombre> - Buscar dirección por nombre\n" +
                        "• direccion add <nombre, url_google_maps, referencia> - Crear dirección\n" +
                        "• direccion modify <id, nombre, url_google_maps, referencia> - Actualizar\n" +
                        "• direccion delete <id> - Eliminar");
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, 
                "Error SQL en direccion. Usuario: " + event.getSender() + 
                ", Action: " + event.getAction() + 
                ", Params: " + event.getParams(), ex);
            simpleNotify(event.getSender(), "Error de Base de Datos", 
                "❌ **Error al procesar la solicitud: " + ex.getMessage() + "**");
        } catch (Exception ex) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, 
                "Error inesperado en direccion. Usuario: " + event.getSender() + 
                ", Action: " + event.getAction() + 
                ", Params: " + event.getParams(), ex);
            simpleNotify(event.getSender(), "Error del Sistema", 
                "❌ **Error inesperado: " + ex.getMessage() + "**");
        }
    }

    @Override
    public void comprar(ParamsAction event) {
        try {
            System.out.println("=== PROCESANDO COMPRA COMPLETA PARA USUARIO ===");
            System.out.println("Usuario: " + event.getSender());
            System.out.println("Action: " + event.getAction());
            System.out.println("Params: " + event.getParams());
            
            // El comando comprar no necesita action, es un comando único
            // Formato: comprar <tipo_pago_id, url_google_maps>
            
            if (event.getParams() == null || event.getParams().size() < 2) {
                simpleNotify(event.getSender(), "Parámetros insuficientes", 
                    "❌ **Uso: comprar <tipo_pago_id, url_google_maps>**\n\n" +
                    "📋 **Ejemplo:**\n" +
                    "comprar <1, https://www.google.com/maps/@-17.8521448,-63.167395,16z?entry=ttu>\n\n" +
                    "💳 **Para ver los tipos de pago disponibles use:**\n" +
                    "tipopago get");
                return;
            }
            
            // Obtener el ID del tipo de pago
            int tipoPagoId;
            try {
                tipoPagoId = Integer.parseInt(event.getParams().get(0));
            } catch (NumberFormatException e) {
                simpleNotify(event.getSender(), "ID de tipo de pago inválido", 
                    "❌ **El primer parámetro debe ser un número (ID del tipo de pago).**\n\n" +
                    "💳 **Para ver los tipos de pago disponibles use:**\n" +
                    "tipopago get");
                return;
            }
            
            // Obtener el tipo de pago de la base de datos
            List<String[]> tipoPagoData = nTipoPago.getById(tipoPagoId);
            if (tipoPagoData.isEmpty()) {
                simpleNotify(event.getSender(), "Tipo de pago no encontrado", 
                    "❌ **No se encontró el tipo de pago con ID: " + tipoPagoId + "**\n\n" +
                    "💳 **Para ver los tipos de pago disponibles use:**\n" +
                    "tipopago get");
                return;
            }
            
            String metodoPago = tipoPagoData.get(0)[1]; // El nombre del método de pago
            
            // Reconstruir la URL de Google Maps si se dividió en múltiples parámetros
            String urlGoogleMaps = event.getParams().get(1);
            if (event.getParams().size() > 2) {
                // Si hay más parámetros, reconstruir la URL completa
                StringBuilder urlBuilder = new StringBuilder(urlGoogleMaps);
                for (int i = 2; i < event.getParams().size(); i++) {
                    urlBuilder.append(",").append(event.getParams().get(i));
                }
                urlGoogleMaps = urlBuilder.toString();
            }
            
            System.out.println("URL reconstruida: " + urlGoogleMaps);
            
            // Generar nombre de dirección automáticamente
            String nombreDireccion = "Dirección de Entrega";
            String referencia = "Entrega a domicilio";
            String observaciones = "Compra realizada desde el carrito";
            
            // Validar método de pago
            if (!esMetodoPagoValido(metodoPago)) {
                simpleNotify(event.getSender(), "Método de pago inválido", 
                    "❌ **Método de pago no soportado: " + metodoPago + "**\n\n" +
                    "💳 **Métodos de pago soportados:**\n" +
                    "• efectivo\n" +
                    "• tarjeta\n" +
                    "• transferencia\n" +
                    "• pago_movil\n" +
                    "• qr\n" +
                    "• paypal\n" +
                    "• bitcoin");
                return;
            }
            
            // 1. Verificar que el carrito tenga productos
            // Primero obtener el cliente por email
            List<String[]> clienteData = nCliente.getByUserId(getUserIdByEmail(event.getSender()));
            if (clienteData.isEmpty()) {
                simpleNotify(event.getSender(), "Cliente no encontrado", 
                    "❌ **No se encontró su información de cliente. Regístrese primero.**");
                return;
            }
            
            int clienteId = Integer.parseInt(clienteData.get(0)[0]);
            System.out.println("🔍 DEBUG: Cliente ID: " + clienteId);
            
            List<String[]> carritoData = nCarrito.getCarritoActivo(clienteId);
            System.out.println("🔍 DEBUG: Carrito obtenido - ID: " + (carritoData.isEmpty() ? "NONE" : carritoData.get(0)[0]) + 
                             ", Estado: " + (carritoData.isEmpty() ? "NONE" : carritoData.get(0)[4]) + 
                             ", Total: " + (carritoData.isEmpty() ? "NONE" : carritoData.get(0)[3]));
            
            if (carritoData.isEmpty()) {
                simpleNotify(event.getSender(), "Carrito vacío", 
                    "❌ **Su carrito está vacío. Agregue productos antes de comprar.**\n\n" +
                    "🛒 **Use el comando:**\n" +
                    "carrito add <producto_id, cantidad>");
                return;
            }
            
            int carritoId = Integer.parseInt(carritoData.get(0)[0]);
            System.out.println("🔍 DEBUG: Usando carrito ID: " + carritoId);
            
            List<String[]> detallesCarrito = nCarrito.getDetallesCarrito(carritoId);
            System.out.println("🔍 DEBUG: Detalles del carrito encontrados: " + detallesCarrito.size());
            
            if (detallesCarrito.isEmpty()) {
                simpleNotify(event.getSender(), "Carrito sin productos", 
                    "❌ **Su carrito no tiene productos. Agregue productos antes de comprar.**\n\n" +
                    "🛒 **Use el comando:**\n" +
                    "carrito add <producto_id, cantidad>");
                return;
            }
            
            // Calcular total del carrito
            double totalCarrito = 0.0;
            for (String[] detalle : detallesCarrito) {
                totalCarrito += Double.parseDouble(detalle[4]); // precio_total
            }
            
            // 2. Crear dirección
            List<String[]> direccion = nDireccion.crearDesdeGoogleMaps(nombreDireccion, urlGoogleMaps, referencia);
            if (direccion.isEmpty()) {
                simpleNotify(event.getSender(), "Error al crear dirección", 
                    "❌ **No se pudo crear la dirección. Verifique la URL de Google Maps.**");
                return;
            }
            
            int direccionId = Integer.parseInt(direccion.get(0)[0]);
            
            // 3. Crear pedido
            List<String[]> pedido = nPedido.crearPedidoConDireccionExistente(direccionId, totalCarrito);
            if (pedido.isEmpty()) {
                simpleNotify(event.getSender(), "Error al crear pedido", 
                    "❌ **No se pudo crear el pedido.**");
                return;
            }
            
            int pedidoId = Integer.parseInt(pedido.get(0)[0]);
            
            // 4. Crear nota de venta desde carrito
            List<String[]> notaVenta = nNotaVenta.crearNotaVentaDesdeCarrito(event.getSender(), pedidoId, observaciones);
            if (notaVenta.isEmpty()) {
                simpleNotify(event.getSender(), "Error al crear nota de venta", 
                    "❌ **No se pudo crear la nota de venta.**");
                return;
            }
            
            int notaVentaId = Integer.parseInt(notaVenta.get(0)[0]);
            
            // 5. Procesar pago (simulado)
            System.out.println("🔍 DEBUG COMPRA: Procesando pago - NotaVenta ID: " + notaVentaId + ", Total: " + totalCarrito + ", Método: " + metodoPago);
            // El pago se procesa automáticamente al crear la nota de venta
            
            // 6. Cambiar estado del carrito a "procesado"
            boolean carritoActualizado = nCarrito.cambiarEstadoCarrito(carritoId, "procesado");
            if (!carritoActualizado) {
                System.out.println("⚠️ Advertencia: No se pudo cambiar el estado del carrito a procesado");
            }
            
            // 6. Generar resumen de la compra
            StringBuilder resumen = new StringBuilder();
            resumen.append("🎉 **¡Compra realizada exitosamente!**\n\n");
            resumen.append("📋 **Resumen de la compra:**\n");
            resumen.append("• **Nota de Venta ID:** ").append(notaVentaId).append("\n");
            resumen.append("• **Pedido ID:** ").append(pedidoId).append("\n");
            resumen.append("• **Dirección ID:** ").append(direccionId).append("\n");
            resumen.append("• **Total:** Bs").append(String.format("%.2f", totalCarrito)).append("\n");
            resumen.append("• **Método de pago:** ").append(metodoPago).append("\n");
            resumen.append("• **Estado:** Completada ✅\n\n");
            
            resumen.append("📍 **Dirección de entrega:**\n");
            resumen.append("• **Nombre:** ").append(nombreDireccion).append("\n");
            resumen.append("• **Referencia:** ").append(referencia).append("\n");
            resumen.append("• **Google Maps:** ").append(urlGoogleMaps).append("\n\n");
            
            resumen.append("🛒 **Productos comprados:**\n");
            for (String[] detalle : detallesCarrito) {
                resumen.append("• ").append(detalle[5]).append(" x").append(detalle[3]).append(" = Bs").append(detalle[4]).append("\n");
            }
            resumen.append("\n");
            
            resumen.append("📝 **Observaciones:** ").append(observaciones).append("\n\n");
            resumen.append("🚚 **Su pedido será procesado y enviado pronto.**\n");
            resumen.append("📧 **Recibirá actualizaciones por email.**");
            
            simpleNotifySuccess(event.getSender(), resumen.toString());
            
        } catch (SQLException ex) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, 
                "Error SQL en comprar. Usuario: " + event.getSender() + 
                ", Params: " + event.getParams(), ex);
            simpleNotify(event.getSender(), "Error de Base de Datos", 
                "❌ **Error al procesar la compra: " + ex.getMessage() + "**");
        } catch (Exception ex) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, 
                "Error inesperado en comprar. Usuario: " + event.getSender() + 
                ", Params: " + event.getParams(), ex);
            simpleNotify(event.getSender(), "Error del Sistema", 
                "❌ **Error inesperado: " + ex.getMessage() + "**");
        }
    }

    @Override
    public void carrito(ParamsAction event) {
        try {
            // Primero, obtener el cliente_id del usuario que hace la petición
            String userEmail = event.getSender();
            System.out.println("=== PROCESANDO CARRITO PARA USUARIO: " + userEmail + " ===");
            
            List<String[]> userData = nUsuario.findByEmail(userEmail);
            System.out.println("Usuario encontrado: " + !userData.isEmpty());
            
            if (userData.isEmpty()) {
                System.out.println("❌ Usuario no encontrado: " + userEmail);
                simpleNotify(userEmail, "Usuario no encontrado", 
                    "❌ **Su correo electrónico no está registrado en nuestro sistema.**\n\n" +
                    "🔐 **Para acceder al carrito, debe registrarse primero usando:**\n" +
                    "register &lt;nombre, email, password&gt;");
                return;
            }
            
            int userId = Integer.parseInt(userData.get(0)[0]);
            System.out.println("User ID encontrado: " + userId);
            
            List<String[]> clienteData = nCliente.getByUserId(userId);
            System.out.println("Cliente encontrado: " + !clienteData.isEmpty());
            
            if (clienteData.isEmpty()) {
                System.out.println("❌ Cliente no encontrado para user_id: " + userId);
                simpleNotify(userEmail, "Cliente no encontrado", 
                    "❌ **Su cuenta de usuario no tiene registro de cliente.**\n\n" +
                    "🔐 **Para acceder al carrito, debe completar su registro como cliente usando:**\n" +
                    "cliente add &lt;nit, user_id&gt;\n\n" +
                    "📋 **Su user_id es:** " + userId + "\n\n" +
                    "📧 **Ejemplo:**\n" +
                    "cliente add Nit123, " + userId);
                return;
            }
            
            int clienteId = Integer.parseInt(clienteData.get(0)[0]);
            System.out.println("Cliente ID encontrado: " + clienteId);
            System.out.println("Acción solicitada: " + event.getAction());
            
            switch (event.getAction()) {
                case Token.GET:
                    // Obtener el carrito activo del cliente (con lógica inteligente)
                    List<String[]> carritoData = nCarrito.getCarritoActivo(clienteId);
                    
                    if (!carritoData.isEmpty()) {
                        int carritoId = Integer.parseInt(carritoData.get(0)[0]);
                        String estado = carritoData.get(0)[4];
                        
                        // Obtener los detalles del carrito con productos
                        List<String[]> detallesData = nCarrito.getDetallesCarrito(carritoId);
                        
                        // Mostrar información del carrito
                        String[] carritoHeaders = {"ID", "Cliente ID", "Fecha", "Total", "Estado", "NIT", "Nombre", "Email", "Creado", "Actualizado"};
                        tableNotifySuccess(event.getSender(), "🛒 **Tu Carrito de Compras**", carritoHeaders, (ArrayList<String[]>) carritoData, event.getCommand());
                        
                        // Mostrar productos del carrito
                        if (!detallesData.isEmpty()) {
                            String[] detallesHeaders = {"ID", "Carrito ID", "Producto ID", "Cantidad", "Precio Unit.", "Subtotal", "Producto", "Descripción", "Stock", "Precio Venta", "Creado", "Actualizado"};
                            tableNotifySuccess(event.getSender(), "📦 **Productos en tu Carrito**", detallesHeaders, (ArrayList<String[]>) detallesData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Carrito vacío", 
                                "🛒 **Tu carrito está vacío.**\n\n" +
                                "📦 **Agrega productos usando:**\n" +
                                "carrito add &lt;producto_id, cantidad&gt;");
                        }
                        
                        // Mensaje informativo sobre el estado
                        if ("abandonado".equals(estado)) {
                            simpleNotify(event.getSender(), "Carrito reactivado", 
                                "✅ **Tu carrito abandonado ha sido reactivado automáticamente.**");
                        } else if ("procesado".equals(estado)) {
                            simpleNotify(event.getSender(), "Nuevo carrito creado", 
                                "🆕 **Se ha creado un nuevo carrito para ti.**");
                        }
                        
                    } else {
                        simpleNotify(event.getSender(), "Error al obtener carrito", 
                            "❌ **No se pudo obtener o crear tu carrito.**\n\n" +
                            "🔧 **Contacte al administrador si el problema persiste.**");
                    }
                    break;
                    
                case Token.ADD:
                    System.out.println("=== PROCESANDO ADD AL CARRITO ===");
                    System.out.println("Parámetros recibidos: " + event.getParams());
                    
                    if (event.getParams() != null && event.getParams().size() >= 2) {
                        // Comando: carrito add <producto_id, cantidad>
                        int productoId = Integer.parseInt(event.getParams().get(0));
                        int cantidad = Integer.parseInt(event.getParams().get(1));
                        
                        System.out.println("Producto ID: " + productoId);
                        System.out.println("Cantidad: " + cantidad);
                        
                        // Obtener el carrito activo
                        System.out.println("Obteniendo carrito activo para cliente: " + clienteId);
                        List<String[]> carritoActivo = nCarrito.getCarritoActivo(clienteId);
                        System.out.println("Carrito activo encontrado: " + !carritoActivo.isEmpty());
                        
                        if (!carritoActivo.isEmpty()) {
                            int carritoId = Integer.parseInt(carritoActivo.get(0)[0]);
                            System.out.println("Carrito ID: " + carritoId);
                            
                            System.out.println("Agregando producto al carrito...");
                            List<String[]> resultado = nCarrito.agregarProducto(carritoId, productoId, cantidad);
                            System.out.println("Producto agregado exitosamente");
                            tableNotifySuccess(event.getSender(), "Producto agregado al carrito", DDetalleCarrito.HEADERS, (ArrayList<String[]>) resultado, event.getCommand());
                            
                        } else {
                            System.out.println("❌ No se pudo obtener carrito activo");
                            simpleNotify(event.getSender(), "Error al agregar producto", 
                                "❌ **No se pudo obtener tu carrito activo.**");
                        }
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "carrito add &lt;producto_id, cantidad&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "carrito add 1, 2");
                    }
                    break;
                    
                case Token.MODIFY:
                    if (event.getParams() != null && event.getParams().size() >= 2) {
                        // Comando: carrito modify <detalle_id, cantidad>
                        int detalleId = Integer.parseInt(event.getParams().get(0));
                        int cantidad = Integer.parseInt(event.getParams().get(1));
                        
                        List<String[]> resultado = nCarrito.actualizarCantidad(detalleId, cantidad);
                        tableNotifySuccess(event.getSender(), "Cantidad actualizada", DDetalleCarrito.HEADERS, (ArrayList<String[]>) resultado, event.getCommand());
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "carrito modify &lt;detalle_id, cantidad&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "carrito modify 1, 3");
                    }
                    break;
                    
                case Token.DELETE:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        // Comando: carrito delete <detalle_id>
                        int detalleId = Integer.parseInt(event.getParams().get(0));
                        
                        boolean success = nCarrito.eliminarProducto(detalleId);
                        if (success) {
                            simpleNotifySuccess(event.getSender(), 
                                "✅ **Producto eliminado del carrito exitosamente**\n\n" +
                                "🗑️ **Detalle eliminado:** " + detalleId);
                        } else {
                            simpleNotify(event.getSender(), "Error al eliminar", 
                                "❌ **No se pudo eliminar el producto del carrito.**\n\n" +
                                "📝 **Verifique que el detalle existe.**");
                        }
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "carrito delete &lt;detalle_id&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "carrito delete 1");
                    }
                    break;
                    
                default:
                    simpleNotify(event.getSender(), "Comando no reconocido", 
                        "❌ **Comando de carrito no reconocido.**\n\n" +
                        "📋 **Comandos disponibles:**\n" +
                        "• carrito get\n" +
                        "• carrito add &lt;producto_id, cantidad&gt;\n" +
                        "• carrito modify &lt;detalle_id, cantidad&gt;\n" +
                        "• carrito delete &lt;detalle_id&gt;");
                    break;
            }
        } catch (SQLException e) {
            System.err.println("=== ERROR SQL EN CARRITO ===");
            System.err.println("Usuario: " + event.getSender());
            System.err.println("Comando: " + event.getCommand());
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, "Error en método carrito para usuario: " + event.getSender(), e);
            simpleNotify(event.getSender(), "Error de Sistema", 
                "❌ **Se ha producido un error al procesar su solicitud.**\n\n" +
                "🔧 **Error en carrito:** " + e.getMessage() + "\n\n" +
                "📋 **Comando ejecutado:** " + event.getCommand());
        } catch (Exception e) {
            System.err.println("=== ERROR INESPERADO EN CARRITO ===");
            System.err.println("Usuario: " + event.getSender());
            System.err.println("Comando: " + event.getCommand());
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, "Error inesperado en método carrito para usuario: " + event.getSender(), e);
            simpleNotify(event.getSender(), "Error de Sistema", 
                "❌ **Se ha producido un error inesperado al procesar su solicitud.**\n\n" +
                "🔧 **Error:** " + e.getMessage() + "\n\n" +
                "📋 **Comando ejecutado:** " + event.getCommand());
        }
    }

    /**
     * Obtiene el ID del usuario por email
     */
    private int getUserIdByEmail(String email) throws SQLException {
        String query = "SELECT id FROM \"user\" WHERE email = ?";
        SqlConnection sqlConnection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
        
        try (var connection = sqlConnection.connect();
             var ps = connection.prepareStatement(query)) {
            
            ps.setString(1, email);
            var rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id");
            }
            
            throw new SQLException("Usuario no encontrado para el email: " + email);
        }
    }
    
    /**
     * Valida si el método de pago es válido
     */
    private boolean esMetodoPagoValido(String metodoPago) {
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

}
