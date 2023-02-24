/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.Personalizados;

import Util.ClasesDeApoyo.SeleccionDeSubmenu;
import beans.Superclases.aplicacionBean;
import EstadoActual.EA;
import beans.ClasesDeApoyo.Prefil;
import static Utiles.JSF.FacesBean.showDialog;
import Utiles.MetodosUtiles.MetodosUtiles;
import Util.ClasesDeApoyo.DatosDeBeanPrincipal;
import Util.ClasesDeApoyo.DatosDeBeanPrincipalCliente;
import Util.ClasesDeApoyo.DatosDeUsuario;
import Util.ClasesDeApoyo.DialogoChat;
import Util.EAc;
import Utiles.JSF.FacesUtil;
import beans.Superclases.FacesBDbean;
import static beans.Superclases.FacesBDbean.getStyleOcultar;
import static beans.Superclases.FacesBDbean.irA;
import com.sun.faces.application.ActionListenerImpl;
import entity.Mensaje;
import entity.Publicacion;
import entity.Tema;
import entity.Tipodetema;
import entity.Users;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;

import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIColumn;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlForm;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.FacesEvent;
import javax.faces.view.ViewScoped;
import org.primefaces.component.panelmenu.PanelMenu;
import org.primefaces.event.MenuActionEvent;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.model.menu.MenuModel;
//import org.primefaces.util.SerializableFunction;

/**
 *
 * @author Rene
 */
@ManagedBean(name = "beanPrincipal3")
@ViewScoped
public class beanPrincipal extends aplicacionBean {

    private MenuModel model;
    private String cuentaSuperiorSeleccionada;

    /**
     * Creates a new instance of beanPrincipal
     */
    public beanPrincipal() {

    }

    @Override
    public void ini() {
        resetear();
    }

    public void resetear() {
        if (getService().isSalioDelLogin()) {
            getService().procesoLogin();
        } //***************************************
        DatosDeBeanPrincipalCliente d = getService().getDatosDeBeanPrincipalCliente();
        model = d.getModel();
        
        //setEsDialogoAgregarDeServidoresFalse();
    }

    public List<DatosDeUsuario> completeText(String query) {
        List<DatosDeUsuario> results = new ArrayList();
        LinkedList<DatosDeUsuario> todosLosUsuarios = getService().getDatosDeBeanPrincipalCliente().getTodosLosUsuarios();
        for (int i = 0; i < todosLosUsuarios.size(); i++) {
            if (todosLosUsuarios.get(i).getCuenta().toLowerCase().contains(query.trim().toLowerCase())) {
                results.add(todosLosUsuarios.get(i));
            }

        }

        return results;
    }

    public String ocultarSiNoEsAdministrador() {
        return getStyleOcultar(!esAdministrador());
    }

    public void apretoBuscarSuperior() {
        try {
            //        System.out.println("entra a buscar");
            Users u = getUsuario(cuentaSuperiorSeleccionada);
//        System.out.println("cuentaSuperiorSeleccionada=" + cuentaSuperiorSeleccionada);
            if (u != null) {
//            System.out.println("exite");
                verSusPublicaciones(u);
                return;
            }
//        System.out.println("no existe");
            mensajeERROR("Esta Cuenta No Existe");
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public void abrirChat(DatosDeUsuario d) {
        try {
            String filtro = getService().getDatosDeBeanPrincipalCliente().getFiltroDeUsuarios();
//            System.out.println("filtro=" + filtro);
            LinkedList<DatosDeUsuario> todosLosUsuarios = getService().getDatosDeBeanPrincipalCliente().getTodosLosUsuarios();
            if (filtro != null) {
                if (!filtro.trim().isEmpty()) {
                    getService().getDatosDeBeanPrincipalCliente().resetearUsuariosActuales();

                    todosLosUsuarios.forEach(v -> {
//                System.out.println("v.getCuenta()="+v.getCuenta());
                        if (v.getCuenta().toLowerCase().contains(filtro.trim().toLowerCase())) {
//                            usuariosActuales.add(v);
                            getService().getDatosDeBeanPrincipalCliente().getUsuariosActuales().add(v);
                        } else {
//                    System.out.println("no add");
                        }
                    });
                }
            } else {
                System.out.println("filtro es nulllllllllllllll");
            }

//            int indice = 0;
//            for (int i = 0; i < todosLosUsuarios.size(); i++) {
//                if (d.getCuenta().equals(todosLosUsuarios.get(i).getCuenta())) {
//                    indice = i;
//                    break;
//                }
//            }
            int indice = 0;
            LinkedList<DatosDeUsuario> usuariosActuales = getService().getDatosDeBeanPrincipalCliente().getUsuariosActuales();
            for (int i = 0; i < usuariosActuales.size(); i++) {
                if (d.getCuenta().equals(usuariosActuales.get(i).getCuenta())) {
                    indice = i;
                    break;

                }
            }

            usuariosActuales.get(indice).setCantidadDeMensajesNoVistos(0);
            getService().abrirChat(usuariosActuales.get(indice).getCuenta());

        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public void filtrarUsuariosActuales() {
//        System.out.println("intenta filtrar");
//        System.out.println("filtroDeUsuariosActuales="+filtroDeUsuariosActuales);
        String filtroDeUsuariosActuales = getService().getDatosDeBeanPrincipalCliente().getFiltroDeUsuarios();
        if (!filtroDeUsuariosActuales.trim().isEmpty()) {
            getService().getDatosDeBeanPrincipalCliente().resetearUsuariosActuales();
//            getService().getDatosDeBeanPrincipalCliente().setUsuariosActuales(new LinkedList());
//            usuariosActuales = new LinkedList();
            getService().getDatosDeBeanPrincipalCliente().getTodosLosUsuarios().forEach(v -> {
//            todosLosUsuarios.forEach(v -> {
//                System.out.println("v.getCuenta()="+v.getCuenta());
                if (v.getCuenta().toLowerCase().contains(filtroDeUsuariosActuales.trim().toLowerCase())) {
//                    usuariosActuales.add(v);
                    getService().getDatosDeBeanPrincipalCliente().getUsuariosActuales().add(v);
                } else {
//                    System.out.println("no add");
                }
            });
        } else {
            if (getService().getDatosDeBeanPrincipalCliente().getUsuariosActuales().size()
                    != getService().getDatosDeBeanPrincipalCliente().getTodosLosUsuarios().size()) {
                getService().getDatosDeBeanPrincipalCliente().resetearUsuariosActuales();
                getService().getDatosDeBeanPrincipalCliente().getUsuariosActuales().addAll(getService().getDatosDeBeanPrincipalCliente().getTodosLosUsuarios());

            }
        }
    }

    public void addSubcripcion(DatosDeUsuario d) {
        try {
            addSubcripcion(d.getCuenta());
            getService().sortDatosDeUsuariosAdd(d.getCuenta());

            mensajeINFO("Te subcriste a " + d.getCuenta());
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public void verSusPublicaciones(DatosDeUsuario d) {
        try {
            verSusPublicaciones(getUsuario(d.getCuenta()));
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public String OcultarSiNoEstaSubscrito(DatosDeUsuario d) {
        return getStyleOcultar(!estaSubscritoElUsuarioActualA(d));
    }

    public String OcultarSiEstaSubscrito(DatosDeUsuario d) {
        return getStyleOcultar(estaSubscritoElUsuarioActualA(d));
    }

    public boolean estaSubscritoElUsuarioActualA(DatosDeUsuario d) {
        return estaSubscritoElUsuarioActualA(d.getCuenta());
    }

    public void eliminarSubcripcion(DatosDeUsuario d) {
        try {
            eliminarSubcripcion(d.getCuenta());
            getService().sortDatosDeUsuariosRemove(d.getCuenta());
            mensajeINFO("Te desubcriste de " + d.getCuenta());
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public List<DatosDeUsuario> getUsuariosActuales2() {
//        return usuariosActuales;
        return getService().getDatosDeBeanPrincipalCliente().getUsuariosActuales();
    }

    public String getFiltroDeUsuariosActuales() {
//        return filtroDeUsuariosActuales;
        return getService().getDatosDeBeanPrincipalCliente().getFiltroDeUsuarios();
    }

    public void setFiltroDeUsuariosActuales(String filtroDeUsuariosActuales) {
        getService().getDatosDeBeanPrincipalCliente().setFiltroDeUsuarios(filtroDeUsuariosActuales);
//        this.filtroDeUsuariosActuales = filtroDeUsuariosActuales;
    }

    public MenuModel getModel() {

//        return getService().getDatosDeBeanPrincipalCliente().getModel();
        return model;
    }

    public void setModel(MenuModel model) {
        this.model = model;
//getService().getDatosDeBeanPrincipalCliente().setModel(model);
    }

    public void apretoMenuPrincipal(ActionEvent a) {
        MenuActionEvent ma = (MenuActionEvent) a;
//        System.out.println("ma.getMenuItem().getTitle()="+ma.getMenuItem().getTitle());
        String informacion[] = MetodosUtiles.getContenidosDeParentesis(ma.getMenuItem().getTitle());
        //if (informacion[0].equals(beanAplicacion.PW_VER_PUBLICACIONES)) {
//        System.out.println("informacion[1]="+informacion[1]+" "+"informacion[2]="+informacion[2]);
        getService().setSeleccionDeSubmenuPrincipal(new SeleccionDeSubmenu(informacion[1], informacion[2]));
        //}
//        System.out.println("aaaaaaaaaaaa sssssssssss a=" + ma.getMenuItem().getTitle() + " " + ma.getMenuItem().getValue());
        try {
            irA(informacion[0]);
        } catch (IOException ex) {
            responderException(ex);
        }
    }

    public void editarPrefil() {
//        System.out.println("intenta editar");
        showDialog(EAc.DLG_EDITAR_PREFIL);
    }

    public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void mostrarDialogoAgregarUsuario() {
        showDialog(EAc.DLG_AGREGAR_USUARIO);
    }

    public void apretoGestionarServidores() {
        try {
            getService().setEsDialogoAgregarDeServidores(true);
            irA("GestionDeServidores");
        } catch (IOException ex) {
            responderException(ex);
        }
    }

    public void setEsDialogoAgregarDeServidoresFalse() {
        getService().setEsDialogoAgregarDeServidores(false);
    }

    public void apretoGestionarUsuarios() {
        try {
            irA("GestionDeUsuarios");
        } catch (IOException ex) {
            responderException(ex);
        }
    }

    public void apretoCambiarCuenta(String dire) {//
        try {
            System.out.println("dire="+dire+"  ++++++++++++++++++++++++++++");
            FacesUtil.irA(dire);
            //irAInicio();
           // irA("login");
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public Prefil getPrefilActual() {
        return getService().getPrefilActual();
    }

    public String getUpdatesChat() {
//        String upd=":"+EA.FORM_CHAT_CENTRO+":"+EA.ID_CHAT_CENTRO+" "+
//            ":"+EA.FORM_CHAT_DERECHO+":"+EA.ID_CHAT_DERECHO+" "+
//            ":"+EA.FORM_CHAT_ISQUIERDO+":"+EA.ID_CHAT_ISQUIERDO;
        String upd = ":" + EAc.FORM_CHAT_CENTRO + " " + ":" + EAc.FORM_CHAT_CENTRO_HEADER + " " + ":" + EAc.FORM_CHAT_CENTRO_FOOTER + " "
                + ":" + EAc.FORM_CHAT_DERECHO + " " + ":" + EAc.FORM_CHAT_DERECHO_HEADER + " " + ":" + EAc.FORM_CHAT_DERECHO_FOOTER + " "
                + ":" + EAc.FORM_CHAT_ISQUIERDO + " " + ":" + EAc.FORM_CHAT_ISQUIERDO_HEADER + ":" + " " + EAc.FORM_CHAT_ISQUIERDO_FOOTER + " ";
//        System.out.println("upd="+upd);
        return upd;
    }

    public String getFuntionChat(DatosDeUsuario d) {

        return "fun" + d.getCuenta();
    }

    public String getCuentaSuperiorSeleccionada() {
        return cuentaSuperiorSeleccionada;
    }

    public void setCuentaSuperiorSeleccionada(String cuentaSuperiorSeleccionada) {
        this.cuentaSuperiorSeleccionada = cuentaSuperiorSeleccionada;
    }

    public void apretoEditarPrefil() {
        showDialog(EAc.DLG_EDITAR_USUARIO);
    }

    public void apretoCrearPublicacion() {
        try {
            irA("CrearPublicacion");
        } catch (IOException ex) {
            responderException(ex);
        }
    }

    public void irAInicio() {
        try {
            //irA("login");
            irA("Inicio");
        } catch (IOException ex) {
            responderException(ex);
        }
    }

    public void apretoVerUltimasPublicaciones() {
        irAPublicaciones(EAc.MI_VER_ULTIMAS);
    }

    public void apretoVerMisPublicaciones() {
        irAPublicaciones(EAc.MI_MIS_PUBLICACIONES);
    }

    public void apretoVerSeguidosPublicaciones() {
        irAPublicaciones(EAc.MI_VER_SEGUIDOS);
    }

    private void irAPublicaciones(String ver) {
        irAPublicaciones(EAc.SUBMENU_GENERAL, ver);
    }

    private void irAPublicaciones(String sub, String ver) {
        try {
            getService().setSeleccionDeSubmenuPrincipal(new SeleccionDeSubmenu(sub, ver));
            irA("TodasLasPublicaciones");
        } catch (IOException ex) {
            responderException(ex);
        }

    }

    public void actualizarVisual() {
        try {
            //actualizarFormEast2
//        System.out.println("actu 1");
            if (getService()!=null&&getService().getNotificaciones()!=null&&!getService().getNotificaciones().isEmpty()) {
//        System.out.println("no es empty");
                Set<String> jqAejecutar = new HashSet();
//boolean actualizarUsuariosActuales=false;
                HashMap<String, Integer> mensajes = new HashMap<>();
                Set<String> vioLosMensajes = new HashSet();
                LinkedList<String> notificaciones = getService().getNotificaciones();
                Set<String> cuentasEliminadas = new HashSet();
                Set<String> cuentasAgregadas = new HashSet();
//                boolean estaCuentaFueEliminada = false;
                for (int i = 0; i < notificaciones.size(); i++) {
                    String contenido = notificaciones.get(i);
                    if (contenido.startsWith("Mensaje de")) {
                        String cuenta = MetodosUtiles.contenidoDeParentesis(contenido, contenido.indexOf("("));
                        if (mensajes.containsKey(cuenta)) {
                            mensajes.put(cuenta, ((Integer) mensajes.get(cuenta)) + 1);//.intValue()
                        } else {
                            mensajes.put(cuenta, 1);
                        }

                        notificaciones.remove(i--);
                        continue;
                    }
                    if (contenido.startsWith("Vio los mensajes")) {
                        String cuenta = MetodosUtiles.contenidoDeParentesis(contenido, contenido.indexOf("("));
                        vioLosMensajes.add(cuenta);
                        notificaciones.remove(i--);
                        continue;
                    }
                    if (contenido.startsWith("Cuenta eliminada")) {
                        System.out.println("vio una cuenta eliminada");
                        String cuenta = MetodosUtiles.contenidoDeParentesis(contenido, contenido.indexOf("("));
                        cuentasEliminadas.add(cuenta);
                        notificaciones.remove(i--);
                        continue;
                    }
                    if (contenido.startsWith("Cuenta agreagada")) {
                        System.out.println("vio una cuenta agregada");
                        String cuenta = MetodosUtiles.contenidoDeParentesis(contenido, contenido.indexOf("("));
                        cuentasAgregadas.add(cuenta);
                        notificaciones.remove(i--);
                        continue;
                    }
                    if (contenido.startsWith("Esta cuenta fue eliminada")) {
//                        estaCuentaFueEliminada = true;
                        irA("index");
                        notificaciones.clear();
                        continue;
                    }
                }
                //Actualizar visual
                if (!mensajes.isEmpty()) {
                    For1:
                    for (Iterator<String> iterator = getService().getDialogosChat().keySet().iterator(); iterator.hasNext();) {
                        String idDlgChat = iterator.next();
                        DialogoChat dialogoChat = getService().getDialogosChat().get(idDlgChat);
                        for (Iterator<String> iterator1 = mensajes.keySet().iterator(); iterator1.hasNext();) {
                            String cuentaMensaje = iterator1.next();
//                    System.out.println("cuentaMensje="+cuentaMensje);
//                    System.out.println("dialogoChat.getDestinatario().getUsername()="+dialogoChat.getDestinatario().getUsername());
//                    System.out.println("dialogoChat.getDestinatario().getUsername().equals(cuentaMensje)="+dialogoChat.getDestinatario().getUsername().equals(cuentaMensje));
                            if (dialogoChat.getDestinatario().getUsername().equals(cuentaMensaje)) {

                                //                         System.out.println("ejecuta "+"actualizar"+idDlgChat);
                                //***********************************88
                                dialogoChat = FacesBDbean.servidores.getDialogoChat(getService().getPrefilActual().getUsuarioActual(), cuentaMensaje);
                                getService().getDialogosChat().put(idDlgChat, dialogoChat);
                                //getService().scrollToEnd(idDlgChat);//fue el ultimo que desactive

//                               ***********************************************
//System.out.println("comsumio en dialogo");
//                                execute("actualizar" + idDlgChat + "();");
                                jqAejecutar.add("actualizar" + idDlgChat + "();");
                                FacesBDbean.servidores.notificacionVioLosMensajes(cuentaMensaje, getService().getPrefilActual().getUsuarioActual().getUsername());
//                                getService().getScrollToEnd(idDlgChat)
                                mensajes.remove(cuentaMensaje);
                                //  getService().scrollToEnd(idDlgChat);//fue el ultimo que desactive
                                jqAejecutar.add(getService().getScrollToEnd(idDlgChat));
                                continue For1;

                            }
                        }

                        // String cuentaChat=getService().getDialogosChat().get(next);
                    }
                    if (!mensajes.isEmpty()) {
                        LinkedList<DatosDeUsuario> usuariosActuales = getService().getDatosDeBeanPrincipalCliente().getUsuariosActuales();
//                            System.out.println("va a analizar  el mensaje ");
                        for (Iterator<String> iterator1 = mensajes.keySet().iterator(); iterator1.hasNext();) {
                            String cuentaMensaje = iterator1.next();
                            for (int i = 0; i < usuariosActuales.size(); i++) {
                                if (usuariosActuales.get(i).getCuenta().equals(cuentaMensaje)) {
//                                         System.out.println("usuariosActuales.get(i).getCantidadDeMensajesNoVistos()="+usuariosActuales.get(i).getCantidadDeMensajesNoVistos());
//                                         System.out.println("mensajes.get(cuentaMensaje)="+mensajes.get(cuentaMensaje));
                                    int cant = usuariosActuales.get(i).getCantidadDeMensajesNoVistos() + mensajes.get(cuentaMensaje);
//                                         System.out.println("can="+cant);
                                    usuariosActuales.get(i).setCantidadDeMensajesNoVistos(cant);
//                                         System.out.println("va a ejecutar");
//                                    execute("actualizarFormEast2();");
                                    jqAejecutar.add("actualizarFormEast2();");
//actualizarUsuariosActuales=true;
                                    mensajes.remove(cuentaMensaje);
                                    break;
                                }
                            }
                        }

//                             usuariosActuales.get(indice).setCantidadDeMensajesNoVistos(0);
                        //System.out.println("aun no esta vacio");
                        getService().sortDatosDeUsuarios();

                    }
                }

                if (!vioLosMensajes.isEmpty()) {
                    //System.out.println("vio los mensajes");
                    For2:
                    for (Iterator<String> iterator = getService().getDialogosChat().keySet().iterator(); iterator.hasNext();) {
                        String idDlgChat = iterator.next();
                        DialogoChat dialogoChat = getService().getDialogosChat().get(idDlgChat);
                        if (dialogoChat.isHover()) {
                            for (Iterator<String> iterator1 = vioLosMensajes.iterator(); iterator1.hasNext();) {
                                String cuentaMensaje = iterator1.next();
                                if (dialogoChat.getDestinatario().getUsername().equals(cuentaMensaje)) {
                                    //System.out.println("consumio vio los mensajes");
                                    dialogoChat = FacesBDbean.servidores.getDialogoChat(getService().getPrefilActual().getUsuarioActual(), cuentaMensaje);
                                    getService().getDialogosChat().put(idDlgChat, dialogoChat);
                                    jqAejecutar.add("actualizar2" + idDlgChat + "();");
//                                    execute("actualizar2" + idDlgChat + "();");
                                    FacesBDbean.servidores.notificacionVioLosMensajes(cuentaMensaje, getService().getPrefilActual().getUsuarioActual().getUsername());
                                    vioLosMensajes.remove(cuentaMensaje);
//                                getService().scrollToEnd(idDlgChat);
                                    continue For2;

                                }
                            }
                        }

                        // String cuentaChat=getService().getDialogosChat().get(next);
                    }
                }
                if (!cuentasEliminadas.isEmpty()) {
                    for (Iterator<String> iterator1 = cuentasEliminadas.iterator(); iterator1.hasNext();) {
                        String cuenta = iterator1.next();
                        //System.out.println("va procesar la eliminacion de la cuenta " + cuenta);
                        getService().getDatosDeBeanPrincipalCliente().eliminarCuenta(cuenta);
                        jqAejecutar.add("actualizarFormEast2();");
                        cuentasEliminadas.remove(cuenta);
                    }
                }

                if (!cuentasAgregadas.isEmpty()) {
                    for (Iterator<String> iterator1 = cuentasAgregadas.iterator(); iterator1.hasNext();) {
                        String cuenta = iterator1.next();
                        //System.out.println("va procesar la cuenta agregada " + cuenta);
                        getService().getDatosDeBeanPrincipalCliente().agregarCuentaSolo(cuenta);
                        jqAejecutar.add("actualizarFormEast2();");
                        cuentasAgregadas.remove(cuenta);
                    }
                    getService().getDatosDeBeanPrincipalCliente().accionDespuesDeAgreagarCuentas();
                }

                if (!jqAejecutar.isEmpty()) {
                    for (Iterator<String> iterator = jqAejecutar.iterator(); iterator.hasNext();) {
                        String next = iterator.next();
                        //System.out.println("se realizan las ejecuciones para actualizar");
                        execute(next);
                    }

                }
            }
        } catch (Exception ex) {
            responderException(ex);
        }
    }

//  
    public void comprobarActualizar() {
        System.out.println("se ejecuto");
    }

    public void scrollToEndDerecho2() {
        execute("document.getElementById(\"idChatDerecho_content\").scrollTo(0,document.getElementById(\"idChatDerecho_content\").\n"
                + "scrollHeight);");
    }

    public void scrollToEndDerecho() {
        try {
            DialogoChat dialogoChat = getService().getDialogosChat().get(EAc.DLG_CHAT_DERECHO);
            dialogoChat = FacesBDbean.servidores.getDialogoChat(getService().getPrefilActual().getUsuarioActual(), dialogoChat.getDestinatario().getUsername());
            getService().getDialogosChat().put(EAc.DLG_CHAT_DERECHO, dialogoChat);
            getService().scrollToEnd(EAc.DLG_CHAT_DERECHO);
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public void scrollToEndCentro() {
        try {
            DialogoChat dialogoChat = getService().getDialogosChat().get(EAc.DLG_CHAT_CENTRO);
            dialogoChat = FacesBDbean.servidores.getDialogoChat(getService().getPrefilActual().getUsuarioActual(), dialogoChat.getDestinatario().getUsername());
            getService().getDialogosChat().put(EAc.DLG_CHAT_CENTRO, dialogoChat);
            getService().scrollToEnd(EAc.DLG_CHAT_CENTRO);
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public void scrollToEndIsquierdo() {
        try {
            DialogoChat dialogoChat = getService().getDialogosChat().get(EAc.DLG_CHAT_ISQUIERDO);
            dialogoChat = FacesBDbean.servidores.getDialogoChat(getService().getPrefilActual().getUsuarioActual(), dialogoChat.getDestinatario().getUsername());
            getService().getDialogosChat().put(EAc.DLG_CHAT_ISQUIERDO, dialogoChat);
            getService().scrollToEnd(EAc.DLG_CHAT_ISQUIERDO);
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public String mensajeDeNoVisto(DatosDeUsuario d) {
//        System.out.println(" d.getCantidadDeMensajesNoVistos()="+ d.getCantidadDeMensajesNoVistos());
        return d.getCantidadDeMensajesNoVistos() == 0 ? "" : "Men(" + d.getCantidadDeMensajesNoVistos() + ")";
    }

    public String getDireccionDeImagen(DatosDeUsuario d) {
        return imagenesDeUsuarios.getDireccionImagen(d.getCuenta());
    }
}
