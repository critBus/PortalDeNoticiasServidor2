/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.Superclases;

import comunicacion.ComunicacionServidor2;
import comunicacion.ComunicacionCliente2;
import Util.DatosCliente;
import EstadoActual.EA;
import RMI.ConexionServidores;

import Util.EAc;
import Util.PersistenceUtil;
import Utiles.ClasesUtiles.Interfases.Funcionales.CreadorConException;
import Utiles.ClasesUtiles.Servidores.ProcesoCliente;
import Utiles.ClasesUtiles.Servidores.Serializable.ProcesoServidorClienteRMI;
import Utiles.JSF.FacesBean;
import entity.Users;
import entity.Publicacion;
import java.util.Date;
import java.util.LinkedList;
import Utiles.JSF.FacesUtil;
import Utiles.MetodosUtiles.Archivo;
import beans.ClasesDeApoyo.ImagenesDeUsuarios;
import controller.exceptions.NonexistentEntityException;
import entity.Clasificacion;
import entity.Comentario;
import entity.Mensaje;
import entity.Subcripcion;
import entity.Tema;
import entity.Tipodetema;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Callback;
import javax.faces.context.FacesContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import org.primefaces.model.StreamedContent;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author Rene
 */
public class FacesBDbean extends FacesBean {
//direccionDeImagen=
    //int bean principal *********************************************************
//resetear datos Principal ***************************************************************
//    public static ComunicacionServidor2 servidor;
//    public static ComunicacionCliente2 cliente;

    public static ConexionServidores servidores;

    public static ImagenesDeUsuarios imagenesDeUsuarios = new ImagenesDeUsuarios();

    public static Clasificacion getClasificacion(Users u, Publicacion p) throws Exception {
        return servidores.getClasificacion(u, p);
    }

//    public static Clasificacion getClasificacion(int id) throws Exception {
//        return servidor.getClasificacion(id);
//    }
    public static Publicacion agregarPublicacion(Publicacion u) throws Exception {
        return servidores.agregarPublicacion(u);
    }

    public static Users getNewUser() throws Exception {
        return servidores.getNewUser();
    }

    public static Users getUsuario(String Cuenta) throws Exception {
        return servidores.getUsuario(Cuenta);
    }

    public static Users getUsuarioInterno(String Cuenta) throws Exception {
        return servidores.getUsuarioInterno(Cuenta);
    }

    public static LinkedList<Publicacion> getPublicacionAllList() throws Exception {
        return servidores.getPublicacionAllList();
    }

    public static LinkedList<Tema> getTemaAllList() throws Exception {
        return servidores.getTemaAllList();
    }

    public static Tema getTema(int id) throws Exception {
        return servidores.getTema(id);
    }

    public static LinkedList<Tipodetema> getTipoDeTemaAllList() throws Exception {
        return servidores.getTipoDeTemaAllList();
    }

    public static LinkedList<Publicacion> getPublicacionAllList(String tipoDeTema, String tema) throws Exception {
        return servidores.getPublicacionAllList(tipoDeTema, tema);
    }

    public static List<Publicacion> getPublicacionAllList(Users u) throws Exception {
        return servidores.getPublicacionAllList(u);
    }

    public static Tipodetema getNewTipodetema(String nombre) throws Exception {
        return servidores.getNewTipodetema(nombre);
    }

    public static Tema getNewTema(String nombre, Tipodetema ti) throws Exception {
        return servidores.getNewTema(nombre, ti);
    }

    public static void irA(String direccion) throws IOException {
        FacesBean.irA(direccion + ".faces");
    }

    public static List<String> getRolesUsuario() throws Exception {
        return servidores.getRolesUsuario();
    }

    public static boolean existeUsuarioInterno(String Cuenta, String contraseña) throws Exception {
        return servidores.existeUsuarioInterno(Cuenta, contraseña);
    }

    public static boolean existeUsuarioInterno_Y_Externo(String Cuenta, String contraseña) throws Exception {
        return servidores.existeUsuarioInterno_Y_Externo(Cuenta, contraseña);
    }

    public static boolean existeUsuarioInterno(String Cuenta) throws Exception {
        return servidores.existeUsuarioInterno(Cuenta);
    }

    public static boolean existeUsuarioInterno_Y_Externo(String Cuenta) throws Exception {
        return servidores.existeUsuarioInterno_Y_Externo(Cuenta);
    }

    public static Users addUsuario(Users u, boolean esAdministrador) throws Exception {
        return servidores.addUsuario(u, esAdministrador);
    }

    public static boolean esAdministrador(String rol) throws Exception {
        return PersistenceUtil.esAdministrador(rol);
    }

    public static boolean esAdministradorInterno(Users u) throws Exception {
        return servidores.esAdministradorInterno(u);
    }

    public static boolean esAdministrador(Users u) throws Exception {
        return servidores.esAdministrador(u);
    }

    public static String getStyleOcultar(boolean a) {
        return Utiles.PW.UtilitesJavaScripts.getStyleOcultar(a);
    }

    public static StreamedContent getStreamedContent(Publicacion p) {
        return FacesUtil.getStreamedContent(p.getUsersusername().getImagen());
    }

    public static StreamedContent getStreamedContentDeCuenta(String cuenta) throws Exception {
        Users u = getUsuario(cuenta);
        byte[] img = null;
        if (u == null) {
            img = getImagenDeUsuarioDesconectado(cuenta);
        } else {
            img = u.getImagen();
        }

       // return FacesUtil.getStreamedContent(getUsuario(cuenta).getImagen());
        return FacesUtil.getStreamedContent(img);
    }

    public static String crearImagenDeUsuario(Comentario p) throws Exception {
        return crearImagenDeUsuario(p.getUsenameid());
    }

    public static String crearImagenDeUsuario(Publicacion p) throws IOException {
        return crearImagenDeUsuario(p.getUsersusername());
    }

    public static String crearImagenDeUsuario(String cuenta) throws Exception {
        Users u = getUsuario(cuenta);
        if (u == null) {
            return crearImagenDeUsuarioDesconectado(cuenta);
        }
        return crearImagenDeUsuario(u);
    }

    public static byte[] getImagenDeUsuarioDesconectado(String cuenta) throws IOException {
        byte[] img = null;

        if (FacesContext.getCurrentInstance() == null) {//new File("src/java/recursos/img")
            img = Archivo.getBytesDeImg(new File("src\\recursos\\img\\u9.png").getAbsoluteFile());

        } else {
            img = Archivo.getBytesDeImg(new File(FacesUtil.getRealPath() + "resources\\img\\u9.png"));
            //u.setImagen(Archivo.getBytesDeImg(new File("src\\recursos\\img\\u9.png")));

        }
        return img;
    }

    public static String crearImagenDeUsuarioDesconectado(String cuenta) throws IOException {
        byte[] img = getImagenDeUsuarioDesconectado(cuenta);
        return crearImagenYBorrarSiExiste(img, "resources/img/imgDeUsuarios/Foto de Prefil de" + cuenta + ".png");
    }

    public static String crearImagenDeUsuario(Users u) throws IOException {

        return crearImagenYBorrarSiExiste(u.getImagen(), "resources/img/imgDeUsuarios/Foto de Prefil de" + u.getUsername() + u.getFormatodeimagen());
    }

    public static Comentario addComentario(Users u, Publicacion p, String contenido) throws Exception {
        return servidores.addComentario(u, p, contenido);
    }

    public static LinkedList<Users> getUsuarioAllListInterno() throws Exception {
        return servidores.getUsuarioAllListInterno();
    }

    public static LinkedList<Users> getUsuarioAllListInterno_Y_Externo() throws Exception {
        return servidores.getUsuarioAllListInterno_Y_Externo();
    }

    public static List<Mensaje> getMensajeAllListEntreAmbosSort(Users uno, Users dos) throws Exception {
        return servidores.getMensajeAllListEntreAmbosSort(uno, dos);
    }

    public static Mensaje addMensaje(Users origen, Users destino, String contenido) throws Exception {
        return servidores.addMensaje(origen, destino, contenido);
    }

    public static Subcripcion addSubcripcion(Users propietario, Users subscriptor) throws Exception {
        return servidores.addSubcripcion(propietario, subscriptor);
    }

    public static void eliminarSubcripcion(Users propietario, Users subscriptor) throws NonexistentEntityException, Exception {
        servidores.eliminarSubcripcion(propietario, subscriptor);
    }

    public static boolean existeSubcripcion(Users propietario, Users subscriptor) throws Exception {
        return servidores.existeSubcripcion(propietario, subscriptor);
    }

    public static void eliminarMensaje(Mensaje m) throws NonexistentEntityException, Exception {
        servidores.eliminarMensaje(m);
    }

    public static Users inicializarBasicosUsersAenB(Users a, Users b) {
        return PersistenceUtil.inicializarBasicosUsersAenB(a, b);

    }

    public void eliminaronCuentas(String cuentasEliminadas[]) throws Exception {
        servidores.notificacionEliminaronCuentas(cuentasEliminadas);
        //servidor.seEliminaronCuentas(cliente.getHost(), cliente.getPort(), cliente.getNombreObjetoRemoto(), cuentasEliminadas);
    }

    public void eliminoCuenta(String cuentaEliminada) throws Exception {
        servidores.notificacionEliminoCuenta(cuentaEliminada);
        //servidor.seEliminaronCuentas(cliente.getHost(), cliente.getPort(), cliente.getNombreObjetoRemoto(), cuentaEliminada);
    }

    public void seAgregoUnaCuenta(String cuentaAgregada) throws Exception {
        servidores.notificacionSeAgregoUnaCuenta(cuentaAgregada);
        //servidor.seAgregoUnaCuenta(cliente.getHost(), cliente.getPort(), cliente.getNombreObjetoRemoto(), cuentaAgregada);
    }

    public String encriptar(String contraseña) {

        return DigestUtils.sha1Hex(contraseña);
    }

    public void conectarAServidor(EA ea) throws Exception {
        servidores = new ConexionServidores().conectarAServidor(ea);
    }
}
