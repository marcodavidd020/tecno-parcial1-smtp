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

public class EmailApp implements ICasoUsoListener, IEmailListener {

    private static final int CONSTRAINTS_ERROR = -2;
    private static final int NUMBER_FORMAT_ERROR = -3;
    private static final int INDEX_OUT_OF_BOUND_ERROR = -4;
    private static final int PARSE_ERROR = -5;
    private static final int AUTHORIZATION_ERROR = -6;

    private EmailReceipt emailReceipt;
    private NUsuario nUsuario;
    private NEvento nEvento;
    private NReserva nReserva;
    private NPago nPago;
    private NProveedor nProveedor;
    private NPromocion nPromocion;
    private NPatrocinador nPatrocinador;
    private NPatrocinio nPatrocinio;
    private NRol nRol;
    private NServicio nServicio;
    private NDetalleEvento nDetalleEvento;
    private NCategoria nCategoria;
    private NProducto nProducto;
    private NTipoPago nTipoPago;
    private NCliente nCliente;
    private NCarrito nCarrito;

    public EmailApp() {
        this.emailReceipt = new EmailReceipt();
        this.emailReceipt.setEmailListener(this);
        this.nUsuario = new NUsuario();
        this.nEvento = new NEvento();
        this.nReserva = new NReserva();
        this.nPago = new NPago();
        this.nProveedor = new NProveedor();
        this.nPromocion = new NPromocion();
        this.nPatrocinador = new NPatrocinador();
        this.nPatrocinio = new NPatrocinio();
        this.nRol = new NRol();
        this.nServicio = new NServicio();
        this.nDetalleEvento = new NDetalleEvento();
        this.nCategoria = new NCategoria();
        this.nProducto = new NProducto();
        this.nTipoPago = new NTipoPago();
        this.nCliente = new NCliente();
        this.nCarrito = new NCarrito();
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
    public void evento(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    // nEvento.save(event.getParams());
                    List<String[]> eventoDataSaved = nEvento.save(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Evento guardado correctamente");
                    tableNotifySuccess(event.getSender(), "Evento guardado correctamente", DEvento.HEADERS, (ArrayList<String[]>) eventoDataSaved, event.getCommand());
                    break;
                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // Si hay parámetros, se asume que es una solicitud de evento por ID
                        int id = Integer.parseInt(event.getParams().get(0));
                        List<String[]> eventoData = nEvento.get(id);
                        if (!eventoData.isEmpty()) {
                            tableNotifySuccess(event.getSender(), "Detalles del Evento", DEvento.HEADERS, (ArrayList<String[]>) eventoData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Error", "Evento no encontrado.");
                        }
                    } else {
                        // Si no hay parámetros, se asume que es una solicitud de todos los eventos
                        tableNotifySuccess(event.getSender(), "Lista de Eventos", DEvento.HEADERS, nEvento.list(), event.getCommand());
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> eventoDataUpdated = nEvento.update(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Evento actualizado correctamente");
                    tableNotifySuccess(event.getSender(), "Evento actualizado correctamente", DEvento.HEADERS, (ArrayList<String[]>) eventoDataUpdated, event.getCommand());
                    break;
                case Token.DELETE:
                    // nEvento.delete(event.getParams());
                    List<String[]> eventoDataDeleted = nEvento.delete(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Evento eliminado correctamente");
                    tableNotifySuccess(event.getSender(), "Evento eliminado correctamente", DEvento.HEADERS, (ArrayList<String[]>) eventoDataDeleted, event.getCommand());
                    break;
            }
        } catch (SQLException ex) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), Collections.singletonList("Error SQL: " + ex.getMessage()));
        } catch (IndexOutOfBoundsException ex) {
            handleError(INDEX_OUT_OF_BOUND_ERROR, event.getSender(), Collections.singletonList("Error de índice: " + ex.getMessage()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    // Continuación de los métodos dentro de la clase EmailApp
    @Override
    public void reserva(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    // nReserva.save(event.getParams());
                    List<String[]> reservaDataSaved = nReserva.save(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Reserva creada correctamente");
                    tableNotifySuccess(event.getSender(), "Reserva creada correctamente", DReserva.HEADERS, (ArrayList<String[]>) reservaDataSaved, event.getCommand());
                    break;
                case Token.GET:
                    // tableNotifySuccess(event.getSender(), "Lista de Reservas", DReserva.HEADERS, nReserva.list());
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // Si hay parámetros, se asume que es una solicitud de reserva por ID
                        int id = Integer.parseInt(event.getParams().get(0));
                        List<String[]> reservaData = nReserva.get(id);
                        if (!reservaData.isEmpty()) {
                            tableNotifySuccess(event.getSender(), "Detalles de la Reserva", DReserva.HEADERS, (ArrayList<String[]>) reservaData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Error", "Reserva no encontrada.");
                        }
                    } else {
                        // Si no hay parámetros, se asume que es una solicitud de todas las reservas
                        tableNotifySuccess(event.getSender(), "Lista de Reservas", DReserva.HEADERS, nReserva.list(), event.getCommand());
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> reservaDataUpdated = nReserva.update(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Reserva actualizada correctamente");
                    tableNotifySuccess(event.getSender(), "Reserva actualizada correctamente", DReserva.HEADERS, (ArrayList<String[]>) reservaDataUpdated, event.getCommand());
                    break;
                case Token.DELETE:
                    // nReserva.delete(event.getParams());
                    List<String[]> reservaDataDeleted = nReserva.delete(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Reserva eliminada correctamente");
                    tableNotifySuccess(event.getSender(), "Reserva eliminada correctamente", DReserva.HEADERS, (ArrayList<String[]>) reservaDataDeleted, event.getCommand());
                    break;
            }
        } catch (SQLException ex) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), Collections.singletonList("Error SQL: " + ex.getMessage()));
        } catch (IndexOutOfBoundsException ex) {
            handleError(INDEX_OUT_OF_BOUND_ERROR, event.getSender(), Collections.singletonList("Error de índice: " + ex.getMessage()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void pago(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    // nPago.save(event.getParams());
                    List<String[]> pagoDataSaved = nPago.save(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Pago procesado correctamente");
                    tableNotifySuccess(event.getSender(), "Pago procesado correctamente", DPago.HEADERS, (ArrayList<String[]>) pagoDataSaved, event.getCommand());
                    break;
                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // Si hay parámetros, se asume que es una solicitud de pago por ID
                        int id = Integer.parseInt(event.getParams().get(0));
                        List<String[]> pagoData = nPago.get(id);
                        if (!pagoData.isEmpty()) {
                            tableNotifySuccess(event.getSender(), "Detalles del Pago", DPago.HEADERS, (ArrayList<String[]>) pagoData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Error", "Pago no encontrado.");
                        }
                    } else {
                        // Si no hay parámetros, se asume que es una solicitud de todos los pagos
                        tableNotifySuccess(event.getSender(), "Lista de Pagos", DPago.HEADERS, nPago.list(), event.getCommand());
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> pagoDataUpdated = nPago.update(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Pago actualizado correctamente");
                    tableNotifySuccess(event.getSender(), "Pago actualizado correctamente", DPago.HEADERS, (ArrayList<String[]>) pagoDataUpdated, event.getCommand());
                    break;
                case Token.DELETE:
                    // nPago.delete(event.getParams());
                    List<String[]> pagoDataDeleted = nPago.delete(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Pago eliminado correctamente");
                    tableNotifySuccess(event.getSender(), "Pago eliminado correctamente", DPago.HEADERS, (ArrayList<String[]>) pagoDataDeleted, event.getCommand());
                    break;
            }
        } catch (SQLException ex) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), Collections.singletonList("Error SQL: " + ex.getMessage()));
        } catch (IndexOutOfBoundsException ex) {
            handleError(INDEX_OUT_OF_BOUND_ERROR, event.getSender(), Collections.singletonList("Error de índice: " + ex.getMessage()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void proveedor(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    // nProveedor.save(event.getParams());
                    List<String[]> proveedorDataSaved = nProveedor.save(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Proveedor agregado correctamente");
                    tableNotifySuccess(event.getSender(), "Proveedor agregado correctamente", DProveedor.HEADERS, (ArrayList<String[]>) proveedorDataSaved, event.getCommand());
                    break;
                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // Si hay parámetros, se asume que es una solicitud de proveedor por ID
                        int id = Integer.parseInt(event.getParams().get(0));
                        List<String[]> proveedorData = nProveedor.get(id);
                        if (!proveedorData.isEmpty()) {
                            tableNotifySuccess(event.getSender(), "Detalles del Proveedor", DProveedor.HEADERS, (ArrayList<String[]>) proveedorData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Error", "Proveedor no encontrado.");
                        }
                    } else {
                        // Si no hay parámetros, se asume que es una solicitud de todos los proveedores
                        tableNotifySuccess(event.getSender(), "Lista de Proveedores", DProveedor.HEADERS, nProveedor.list(), event.getCommand());
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> proveedorDataUpdated = nProveedor.update(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Proveedor actualizado correctamente");
                    tableNotifySuccess(event.getSender(), "Proveedor actualizado correctamente", DProveedor.HEADERS, (ArrayList<String[]>) proveedorDataUpdated, event.getCommand());
                    break;
                case Token.DELETE:
                    // nProveedor.delete(event.getParams());
                    List<String[]> proveedorDataDeleted = nProveedor.delete(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Proveedor eliminado correctamente");
                    tableNotifySuccess(event.getSender(), "Proveedor eliminado correctamente", DProveedor.HEADERS, (ArrayList<String[]>) proveedorDataDeleted, event.getCommand());
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
    public void patrocinador(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    // nPatrocinador.save(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Patrocinador agregado correctamente");
                    List<String[]> patrocinadorDataSaved = nPatrocinador.save(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Patrocinador agregado correctamente: " + Arrays.toString(patrocinadorDataSaved));
                    tableNotifySuccess(event.getSender(), "Patrocinador agregado correctamente", DPatrocinador.HEADERS, (ArrayList<String[]>) patrocinadorDataSaved);
                    break;
                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // Si hay parámetros, se asume que es una solicitud de patrocinador por ID
                        int id = Integer.parseInt(event.getParams().get(0));
                        List<String[]> patrocinadorData = nPatrocinador.get(id);
                        if (!patrocinadorData.isEmpty()) {
                            tableNotifySuccess(event.getSender(), "Detalles del Patrocinador", DPatrocinador.HEADERS, (ArrayList<String[]>) patrocinadorData);
                        } else {
                            simpleNotify(event.getSender(), "Error", "Patrocinador no encontrado.");
                        }
                    } else {
                        // Si no hay parámetros, se asume que es una solicitud de todos los patrocinadores
                        tableNotifySuccess(event.getSender(), "Lista de Patrocinadores", DPatrocinador.HEADERS, nPatrocinador.list(), event.getCommand());
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> patrocinadorDataUpdated = nPatrocinador.update(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Patrocinador actualizado correctamente: " + Arrays.toString(patrocinadorDataUpdated));
                    tableNotifySuccess(event.getSender(), "Patrocinador actualizado correctamente", DPatrocinador.HEADERS, (ArrayList<String[]>) patrocinadorDataUpdated);
                    break;
                case Token.DELETE:
//                    nPatrocinador.delete(event.getParams());
//                    simpleNotifySuccess(event.getSender(), "Patrocinador eliminado correctamente");
                    List<String[]> patrocinadorDataDeleted = nPatrocinador.delete(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Patrocinador eliminado correctamente: " + Arrays.toString(patrocinadorDataDeleted));
                    tableNotifySuccess(event.getSender(), "Patrocinador eliminado correctamente", DPatrocinador.HEADERS, (ArrayList<String[]>) patrocinadorDataDeleted);
                    break;
            }
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            handleError(CONSTRAINTS_ERROR, event.getSender(), ex.getMessage() != null ? Collections.singletonList("Error: " + ex.getMessage()) : null);
        }
    }

    @Override
    public void patrocinio(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
//                    nPatrocinio.save(event.getParams());
                    List<String[]> patrocinioDataSaved = nPatrocinio.save(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Patrocinio registrado correctamente");
                    tableNotifySuccess(event.getSender(), "Patrocinio registrado correctamente", DPatrocinio.HEADERS, (ArrayList<String[]>) patrocinioDataSaved, event.getCommand());
                    break;
                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // Si hay parámetros, se asume que es una solicitud de patrocinio por ID
                        int id = Integer.parseInt(event.getParams().get(0));
                        List<String[]> patrocinioData = nPatrocinio.get(id);
                        if (!patrocinioData.isEmpty()) {
                            tableNotifySuccess(event.getSender(), "Detalles del Patrocinio", DPatrocinio.HEADERS, (ArrayList<String[]>) patrocinioData);
                        } else {
                            simpleNotify(event.getSender(), "Error", "Patrocinio no encontrado.");
                        }
                    } else {
                        // Si no hay parámetros, se asume que es una solicitud de todos los patrocinios
                        tableNotifySuccess(event.getSender(), "Lista de Patrocinios", DPatrocinio.HEADERS, nPatrocinio.list());
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> patrocinioDataUpdated = nPatrocinio.update(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Patrocinio actualizado correctamente");
                    tableNotifySuccess(event.getSender(), "Patrocinio actualizado correctamente", DPatrocinio.HEADERS, (ArrayList<String[]>) patrocinioDataUpdated, event.getCommand());
                    break;
                case Token.DELETE:
                    // nPatrocinio.delete(event.getParams());
                    List<String[]> patrocinioDataDeleted = nPatrocinio.delete(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Patrocinio eliminado correctamente");
                    tableNotifySuccess(event.getSender(), "Patrocinio eliminado correctamente", DPatrocinio.HEADERS, (ArrayList<String[]>) patrocinioDataDeleted, event.getCommand());
                    break;
            }
        } catch (SQLException ex) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), Collections.singletonList("Error SQL: " + ex.getMessage()));
        } catch (IndexOutOfBoundsException ex) {
            handleError(INDEX_OUT_OF_BOUND_ERROR, event.getSender(), Collections.singletonList("Error de índice: " + ex.getMessage()));
        }
    }

    @Override
    public void rol(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
//                    nRol.save(event.getParams());
                    List<String[]> rolDataAdd = nRol.save(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Rol guardado correctamente");
                    tableNotifySuccess(event.getSender(), "Rol guardado correctamente", DRol.HEADERS, (ArrayList<String[]>) rolDataAdd, event.getCommand());
                    break;
                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // Si hay parámetros, se asume que es una solicitud de rol por ID
                        String idParam = event.getParams().get(0); // Supone que el ID es el primer parámetro
                        try {
                            int id = Integer.parseInt(idParam);
                            List<String[]> rolData = nRol.get(id);
                            if (rolData != null) {
                                // simpleNotifySuccess(event.getSender(), "Rol encontrado: " + Arrays.toString(rolData));
                                tableNotifySuccess(event.getSender(), "Rol encontrado", DRol.HEADERS, (ArrayList<String[]>) rolData, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "Error", "Rol no encontrado.");
                            }
                        } catch (NumberFormatException e) {
                            simpleNotify(event.getSender(), "Error", "ID inválido.");
                        }
                    } else {
                        // Si no hay parámetros, se asume que es una solicitud de todos los roles
                        tableNotifySuccess(event.getSender(), "Lista de Roles", DRol.HEADERS, nRol.list(), event.getCommand());
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> rolDataUpdate = nRol.update(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Rol actualizado correctamente");
                    tableNotifySuccess(event.getSender(), "Rol actualizado correctamente", DRol.HEADERS, (ArrayList<String[]>) rolDataUpdate, event.getCommand());
                    break;
                case Token.DELETE:
                    // nRol.delete(event.getParams());
                    List<String[]> rolDataDelete = nRol.delete(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Rol eliminado correctamente");
                    tableNotifySuccess(event.getSender(), "Rol eliminado correctamente", DRol.HEADERS, (ArrayList<String[]>) rolDataDelete, event.getCommand());
                    break;
            }
        } catch (SQLException ex) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), Collections.singletonList("Error SQL: " + ex.getMessage()));
        } catch (IndexOutOfBoundsException ex) {
            handleError(INDEX_OUT_OF_BOUND_ERROR, event.getSender(), Collections.singletonList("Error de índice: " + ex.getMessage()));
        }
    }

    @Override
    public void servicio(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    // nServicio.save(event.getParams());
                    List<String[]> servicioDataAdd = nServicio.save(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Servicio guardado correctamente");
                    tableNotifySuccess(event.getSender(), "Servicio guardado correctamente", DServicio.HEADERS, (ArrayList<String[]>) servicioDataAdd, event.getCommand());
                    break;
                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // Si hay parámetros, se asume que es una solicitud de servicio por ID
                        String idParam = event.getParams().get(0); // Supone que el ID es el primer parámetro
                        try {
                            int id = Integer.parseInt(idParam);
                            List<String[]> servicioData = nServicio.get(id);
                            if (servicioData != null) {
                                // simpleNotifySuccess(event.getSender(), "Servicio encontrado: " + Arrays.toString(servicioData));
                                tableNotifySuccess(event.getSender(), "Servicio encontrado", DServicio.HEADERS, (ArrayList<String[]>) servicioData, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "Error", "Servicio no encontrado.");
                            }
                        } catch (NumberFormatException e) {
                            simpleNotify(event.getSender(), "Error", "ID inválido.");
                        }
                    } else {
                        // Si no hay parámetros, se asume que es una solicitud de todos los servicios
                        tableNotifySuccess(event.getSender(), "Lista de Servicios", DServicio.HEADERS, nServicio.list());
                    }
                    break;
                    case Token.MODIFY:
                    List<String[]> servicioDataUpdate = nServicio.update(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Servicio actualizado correctamente");
                    tableNotifySuccess(event.getSender(), "Servicio actualizado correctamente", DServicio.HEADERS, (ArrayList<String[]>) servicioDataUpdate, event.getCommand());
                    break;
                    case Token.DELETE:
                    // nServicio.delete(event.getParams());
                    List<String[]> servicioDataDelete = nServicio.delete(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Servicio eliminado correctamente");
                    tableNotifySuccess(event.getSender(), "Servicio eliminado correctamente", DServicio.HEADERS, (ArrayList<String[]>) servicioDataDelete, event.getCommand());
                    break;
            }
        } catch (NumberFormatException | SQLException | IndexOutOfBoundsException ex) {
            handleError(NUMBER_FORMAT_ERROR, event.getSender(), null);
        }
    }

    @Override
    public void detalleEvento(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    // nDetalleEvento.save(event.getParams());
                    List<String[]> detalleEventoDataAdd = nDetalleEvento.save(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Detalle de Evento guardado correctamente");
                    tableNotifySuccess(event.getSender(), "Detalle de Evento guardado correctamente", DDetalleEvento.HEADERS, (ArrayList<String[]>) detalleEventoDataAdd, event.getCommand());
                    break;
                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // Si hay parámetros, se asume que es una solicitud de detalle de evento por ID
                        int evento_id = Integer.parseInt(event.getParams().get(0));
                        int servicio_id = Integer.parseInt(event.getParams().get(1));
                        List<String[]> detalleEventoData = nDetalleEvento.get(evento_id, servicio_id);
                        if (detalleEventoData != null) {
                            tableNotifySuccess(event.getSender(), "Detalles del Detalle de Evento", DDetalleEvento.HEADERS, (ArrayList<String[]>) detalleEventoData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Error", "Detalle de Evento no encontrado.");
                        }
                    } else {
                        // Si no hay parámetros, se asume que es una solicitud de todos los detalles de eventos
                        tableNotifySuccess(event.getSender(), "Lista de Detalles de Eventos", DDetalleEvento.HEADERS, nDetalleEvento.list(), event.getCommand());
                    }
                    break;
                case Token.MODIFY:
                    nDetalleEvento.update(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Detalle de Evento actualizado correctamente");
                    List<String[]> detalleEventoDataUpdate = nDetalleEvento.update(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Detalle de Evento actualizado correctamente: " + Arrays.toString(detalleEventoDataUpdate));
                    tableNotifySuccess(event.getSender(), "Detalle de Evento actualizado correctamente", DDetalleEvento.HEADERS, (ArrayList<String[]>) detalleEventoDataUpdate, event.getCommand());
                    break;
                case Token.DELETE:
                    // nDetalleEvento.delete(event.getParams());
                    List<String[]> detalleEventoDataDelete = nDetalleEvento.delete(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Detalle de Evento eliminado
                    tableNotifySuccess(event.getSender(), "Detalle de Evento eliminado correctamente", DDetalleEvento.HEADERS, (ArrayList<String[]>) detalleEventoDataDelete, event.getCommand());
                    break;
            }
        } catch (SQLException ex) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), Collections.singletonList("Error SQL: " + ex.getMessage()));
        } catch (IndexOutOfBoundsException ex) {
            handleError(INDEX_OUT_OF_BOUND_ERROR, event.getSender(), Collections.singletonList("Error de índice: " + ex.getMessage()));
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
                data.add(new String[]{"Registro", "register cliente &lt;nit&gt;", "Registra un usuario existente como cliente"});

                // Usuarios
                data.add(new String[]{"Usuarios", "usuario get", "Obtiene todos los usuarios"});
                data.add(new String[]{"Usuarios", "usuario get &lt;id&gt;", "Obtiene usuario por ID"});
                data.add(new String[]{"Usuarios", "usuario add &lt;nombre, apellido, telefono, genero, email, password, rol_id&gt;", "Crea un usuario"});
                data.add(new String[]{"Usuarios", "usuario modify &lt;id, nombre, apellido, telefono, genero, email&gt;", "Modifica un usuario por ID"});
                data.add(new String[]{"Usuarios", "usuario delete &lt;id&gt;", "Elimina un usuario por ID"});

                // Eventos
                data.add(new String[]{"Eventos", "evento get", "Obtiene todos los eventos"});
                data.add(new String[]{"Eventos", "evento get &lt;id&gt;", "Obtiene evento por ID"});
                data.add(new String[]{"Eventos", "evento add &lt;nombre, descripcion, capacidad, precio_entrada, fecha, hora, ubicacion, estado, imagen, servicio_id&gt;", "Crea un evento"});
                data.add(new String[]{"Eventos", "evento modify &lt;id, nombre, descripcion, capacidad, precio_entrada&gt;", "Modifica un evento por ID"});
                data.add(new String[]{"Eventos", "evento delete &lt;id&gt;", "Elimina un evento por ID"});

                // Patrocinadores y Patrocinios
                data.add(new String[]{"Patrocinadores", "patrocinador get", "Obtener todos los patrocinadores"});
                data.add(new String[]{"Patrocinadores", "patrocinador get &lt;id&gt;", "Obtener patrocinador por ID"});
                data.add(new String[]{"Patrocinadores", "patrocinador add &lt;nombre, descripcion, email, telefono&gt;", "Crea un patrocinador"});
                data.add(new String[]{"Patrocinadores", "patrocinador modify &lt;id, nombre, descripcion, email, telefono&gt;", "Modifica un patrocinador por ID"});
                data.add(new String[]{"Patrocinadores", "patrocinador delete &lt;id&gt;", "Elimina un patrocinador por ID"});

                data.add(new String[]{"Patrocinios", "patrocinio get", "Obtiene todos los patrocinios"});
                data.add(new String[]{"Patrocinios", "patrocinio get &lt;id&gt;", "Obtiene patrocinio por ID"});
                data.add(new String[]{"Patrocinios", "patrocinio add &lt;aporte, patrocinador_id, evento_id&gt;", "Crea un patrocinio"});
                data.add(new String[]{"Patrocinios", "patrocinio modify &lt;id, aporte&gt;", "Modifica un patrocinio por ID"});
                data.add(new String[]{"Patrocinios", "patrocinio delete &lt;id&gt;", "Elimina un patrocinio por ID"});

                // Roles
                data.add(new String[]{"Roles", "rol get", "Obtiene todos los roles"});
                data.add(new String[]{"Roles", "rol get &lt;id&gt;", "Obtiene rol por ID"});
                data.add(new String[]{"Roles", "rol add &lt;nombre&gt;", "Crear un rol"});
                data.add(new String[]{"Roles", "rol modify &lt;id, nombre&gt;", "Modifica un rol por ID"});
                data.add(new String[]{"Roles", "rol delete &lt;id&gt;", "Elimina un rol por ID"});

                // Servicios
                data.add(new String[]{"Servicios", "servicio get", "Obtiene todos los servicios"});
                data.add(new String[]{"Servicios", "servicio get &lt;id&gt;", "Obtiene servicio por ID"});
                data.add(new String[]{"Servicios", "servicio add &lt;nombre, descripcion&gt;", "Crea un servicio"});
                data.add(new String[]{"Servicios", "servicio modify &lt;id, nombre, descripcion&gt;", "Modifica un servicio por ID"});
                data.add(new String[]{"Servicios", "servicio delete &lt;id&gt;", "Elimina un servicio por ID"});

                // Reservas
                data.add(new String[]{"Reservas", "reserva get", "Obtiene todas las reservas"});
                data.add(new String[]{"Reservas", "reserva get &lt;id&gt;", "Obtiene reserva por ID"});
                data.add(new String[]{"Reservas", "reserva add &lt;codigo, fecha, costo_entrada, cantidad, costo_total, estado, usuario_id, evento_id&gt;", "Crea una reserva"});
                data.add(new String[]{"Reservas", "reserva modify &lt;id, estado&gt;", "Modifica una reserva por ID"});
                data.add(new String[]{"Reservas", "reserva delete &lt;id&gt;", "Elimina una reserva por ID"});

                // Proveedores
                data.add(new String[]{"Proveedores", "proveedor get", "Obtener todos los proveedores"});
                data.add(new String[]{"Proveedores", "proveedor get &lt;id&gt;", "Obtiene proveedor por ID"});
                data.add(new String[]{"Proveedores", "proveedor add &lt;nombre, telefono, email, direccion&gt;", "Crea un proveedor"});
                data.add(new String[]{"Proveedores", "proveedor modify &lt;id, nombre, telefono, email, direccion&gt;", "Modifica un proveedor por ID"});
                data.add(new String[]{"Proveedores", "proveedor delete &lt;id&gt;", "Elimina un proveedor por ID"});

                // Promociones
                data.add(new String[]{"Promociones", "promocion get", "Obtener todas las promociones"});
                data.add(new String[]{"Promociones", "promocion get &lt;id&gt;", "Obtiene promoción por ID"});
                data.add(new String[]{"Promociones", "promocion get &lt;producto_id&gt;", "Obtiene promociones por producto"});
                data.add(new String[]{"Promociones", "promocion add &lt;nombre, fecha_inicio, fecha_fin, descuento, producto_id&gt;", "Crea una promoción"});
                data.add(new String[]{"Promociones", "promocion modify &lt;id, nombre, fecha_inicio, fecha_fin, descuento, producto_id&gt;", "Modifica una promoción"});
                data.add(new String[]{"Promociones", "promocion delete &lt;id&gt;", "Elimina una promoción"});

                // Pagos
                data.add(new String[]{"Pagos", "pago get", "Obtiene todos los pagos"});
                data.add(new String[]{"Pagos", "pago get &lt;id&gt;", "Obtiene el pago por ID"});
                data.add(new String[]{"Pagos", "pago add &lt;monto, fecha, metodo_pago, reserva_id&gt;", "Crea un pago"});
                data.add(new String[]{"Pagos", "pago modify &lt;id, monto&gt;", "Modifica un pago"});
                data.add(new String[]{"Pagos", "pago delete &lt;id&gt;", "Elimina un pago"});

                // Detalles de Evento
                        data.add(new String[]{"Detalles de Evento", "detalleEvento get", "Obtiene todas los detalleevento"});
        data.add(new String[]{"Detalles de Evento", "detalleEvento get &lt;id&gt;", "Obtiene el detalleevento por ID"});
        data.add(new String[]{"Detalles de Evento", "detalleEvento add &lt;evento_id, servicio_id, costo_servicio&gt;", "Crea un detalleevento"});
        data.add(new String[]{"Detalles de Evento", "detalleEvento modify &lt;evento_id, servicio_id, costo_servicio&gt;", "Modifica un detalleevento"});
        data.add(new String[]{"Detalles de Evento", "detalleEvento delete &lt;id&gt;", "Elimina un detalleevento"});
        
        // Comandos de Categorías
        data.add(new String[]{"Categorías", "categoria get", "Obtiene todas las categorías"});
        data.add(new String[]{"Categorías", "categoria get &lt;id&gt;", "Obtiene categoría por ID"});
        data.add(new String[]{"Categorías", "categoria add &lt;nombre, descripcion&gt;", "Crea una categoría"});
        data.add(new String[]{"Categorías", "categoria modify &lt;id, nombre, descripcion&gt;", "Modifica una categoría"});
        data.add(new String[]{"Categorías", "categoria delete &lt;id&gt;", "Elimina una categoría"});
        
        // Comandos de Productos
        data.add(new String[]{"Productos", "producto get", "Obtiene todos los productos"});
        data.add(new String[]{"Productos", "producto get &lt;id&gt;", "Obtiene producto por ID"});
        data.add(new String[]{"Productos", "producto add &lt;cod_producto, nombre, precio_compra, precio_venta, imagen, descripcion, categoria_id&gt;", "Crea un producto"});
        data.add(new String[]{"Productos", "producto modify &lt;id, cod_producto, nombre, precio_compra, precio_venta, imagen, descripcion, categoria_id&gt;", "Modifica un producto"});
        data.add(new String[]{"Productos", "producto delete &lt;id&gt;", "Elimina un producto"});
        
        // Comandos de Tipos de Pago
        data.add(new String[]{"Tipos de Pago", "tipopago get", "Obtiene todos los tipos de pago"});
        data.add(new String[]{"Tipos de Pago", "tipopago get &lt;id&gt;", "Obtiene tipo de pago por ID"});
        data.add(new String[]{"Tipos de Pago", "tipopago add &lt;tipo_pago&gt;", "Crea un tipo de pago"});
        data.add(new String[]{"Tipos de Pago", "tipopago modify &lt;id, tipo_pago&gt;", "Modifica un tipo de pago"});
        data.add(new String[]{"Tipos de Pago", "tipopago delete &lt;id&gt;", "Elimina un tipo de pago"});
        
        // Comandos de Clientes
        data.add(new String[]{"Clientes", "cliente get", "Obtiene todos los clientes"});
        data.add(new String[]{"Clientes", "cliente get &lt;id&gt;", "Obtiene cliente por ID"});
        data.add(new String[]{"Clientes", "cliente get &lt;nit&gt;", "Obtiene cliente por NIT"});
        data.add(new String[]{"Clientes", "cliente add &lt;nit, user_id&gt;", "Crea un cliente"});
        data.add(new String[]{"Clientes", "cliente modify &lt;id, nit&gt;", "Modifica un cliente"});
        data.add(new String[]{"Clientes", "cliente delete &lt;id&gt;", "Elimina un cliente"});
        
        // Comandos de Carrito
        data.add(new String[]{"Carrito", "carrito get", "Obtiene tu carrito activo con productos"});
        data.add(new String[]{"Carrito", "carrito add &lt;producto_id, cantidad&gt;", "Agrega producto al carrito"});
        data.add(new String[]{"Carrito", "carrito modify &lt;detalle_id, cantidad&gt;", "Modifica cantidad de producto"});
        data.add(new String[]{"Carrito", "carrito delete &lt;detalle_id&gt;", "Elimina producto del carrito"});

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

}
