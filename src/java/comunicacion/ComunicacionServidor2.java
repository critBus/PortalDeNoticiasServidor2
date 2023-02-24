/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comunicacion;
//import Util.ClasesDeApoyo.*;
//import Util.ClasesDeApoyo.Estadisticas.EstadisticasDeUsuario;
import Utiles.ClasesUtiles.Interfases.Servidores.ObjetoRemoteServidor;
import entity.*;
import java.util.LinkedList;
import java.util.List;
/**
 *
 * @author Rene
 */
public interface ComunicacionServidor2 extends ObjetoRemoteServidor{
     public abstract void clearBD() throws Exception;

    public abstract boolean existeSubcripcion(Users propietario, Users subscriptor) throws Exception;

    public abstract void eliminarSubcripcion(Users propietario, Users subscriptor)throws Exception;

    public abstract Subcripcion addSubcripcion(Users propietario, Users subscriptor) throws Exception;

    public abstract Subcripcion getNewSubcripcion(Users propietario, Users subscriptor) throws Exception;

    public abstract Mensaje getNewMensaje(Users origen, Users destino, String contenido)throws Exception;

    public abstract Mensaje addMensaje(String origen, String destino, String contenido) throws Exception;

    public abstract Mensaje addMensaje(Users origen, Users destino, String contenido)throws Exception;

    public abstract Comentario addComentario(Users u, Publicacion p, String contenido) throws Exception;

    public abstract Comentario getNewComentario(Users u, Publicacion p, String contenido) throws Exception;

//    public abstract boolean esAdministrador(String rol) throws Exception;

    public abstract boolean esAdministrador(Users u) throws Exception;
    public abstract List<String> getRolesUsuario()throws Exception;

    /**
     * [0] cantidad de likes <br/>
     * [1] cantidad de dislikes
     *
     * @param p
     * @return
     */
    public abstract int[] getCantidadDeLikesYDislikes(final Publicacion p)throws Exception;
    /**
     * [0] cantidad de likes <br/>
     * [1] cantidad de dislikes
     *
     * @param pid
     * @return
     */
    public abstract int[] getCantidadDeLikesYDislikes(final int pid)throws Exception;

    /**
     * [0] promedio si no hay es 0 <br/>
     * [1] cantidad
     *
     * @param pid
     * @return
     */
    public abstract int[] getPuntacionGeneralYCantidadDeOpiniones(int pid) throws Exception;

    //public abstract Clasificacion getNewClasificacion(String cuenta, int publicacionID) throws Exception;

    public abstract Clasificacion getNewClasificacion(Users u, Publicacion p)throws Exception;

    public abstract Temapublicacion getNewTemaPublicacion(Tema t, Publicacion p) throws Exception;
    public abstract Tipodetema getNewTipodetema(String nombre) throws Exception;

    public abstract Tema getNewTema(String nombre, Tipodetema ti) throws Exception;

    public abstract Publicacion addPublicacion(String cuenta, String tipoDeTema, String tema, String contenido, String titulo) throws Exception;

    public abstract Publicacion addPublicacion(Users u, String contenido, String titulo, Tema... t) throws Exception;

    public abstract Publicacion getNewPublicacion(Users u, String contenido, String titulo) throws Exception;

    public abstract Users getNewUser(String username, String identificacion, String nombre, String apellido1, String apellido2, String email, String password, boolean enabled) throws Exception;

    public abstract Users getNewUser() throws Exception;

    public abstract void eliminarUsuarioAll()throws Exception;
    public abstract void eliminarUsuario(Users u) throws Exception;

    public abstract void eliminarSubcripcionAll(Users propietario) throws Exception;

    public abstract void eliminarSubcripcion(Subcripcion s) throws Exception;

    public abstract void eliminarMensaje(Mensaje m) throws Exception;

    public abstract void eliminarComentario(Comentario c)throws Exception;
public void eliminarComentario(int id) throws Exception ;
    public abstract void eliminarClasificacion(Clasificacion c) throws Exception;
    public abstract void eliminarTemaPublicacion(Temapublicacion t) throws Exception;

    public abstract void eliminarTemaPublicacionAll(Tema t) throws Exception;

    public abstract void eliminarTemaPublicacionAll(Publicacion p) throws Exception;

    public abstract void eliminarTipoDeTemaAll() throws Exception;

    public abstract void eliminarTipoDeTema(Tipodetema t)throws Exception;

    public abstract void eliminarTemaAll(Tipodetema t)throws Exception;

    public abstract void eliminarTema(Tema t) throws Exception;
    public abstract void eliminarPublicacion(Publicacion p) throws Exception;

    public abstract void eliminarPublicacionAll(Users propietario) throws Exception;
    public abstract void eliminarClasificacionsAll(Publicacion p) throws Exception;

    public abstract void eliminarClasificacionsAll(Users p) throws Exception;

    public abstract void eliminarMensajeAll(Users p) throws Exception;
    public abstract void eliminarComentariosAll(Publicacion p) throws Exception;

    public abstract void eliminarComentariosAll(Users p) throws Exception;
    public abstract boolean getEsAdministrador(Users u) throws Exception;
    public abstract String getRol(Users u) throws Exception;
    public abstract void setAuthorities(Users u, boolean administrador) throws Exception;
    public abstract Publicacion addPublicacion(Publicacion u) throws Exception;

    //public abstract Clasificacion addClasificacion(String cuenta, int publicacionID)throws Exception;

    public abstract Clasificacion addClasificacion(Clasificacion c) throws Exception;

    public abstract Subcripcion addSubcripcion(Subcripcion s) throws Exception;
    public abstract Mensaje addMensaje(Mensaje c)throws Exception;



    public abstract Publicacion addPublicacion(Publicacion nueva, Tema... T) throws Exception;

    public abstract Tema addTema(String tipoDeTema, String nombre) throws Exception;

    public abstract Tema addTema(Tema u)throws Exception;

    public abstract Tipodetema addTipoDeTema(String tipoDeTema) throws Exception;
    public abstract Tipodetema addTipoDeTema(Tipodetema u) throws Exception;
    public abstract Comentario addComentario(Comentario u) throws Exception;

    public abstract Temapublicacion addTemaPublicacion(Temapublicacion u)throws Exception;

    public abstract Authorities addAuthorities(Authorities u) throws Exception;

    public abstract void eliminarAuthorities(Users u) throws Exception;

    public abstract void eliminarAuthorities(Authorities a) throws Exception;
    public abstract Authorities getAuthorities(Users u) throws Exception;
    public abstract Authorities getAuthorities(String username) throws Exception;

    public abstract Publicacion getPublicacion(int id) throws Exception;

    public abstract Subcripcion getSubcripcion(int id) throws Exception;

    public abstract Comentario getComentario(int id) throws Exception;

    public abstract Tema getTema(int id) throws Exception;

    public abstract Tipodetema getTipodetema(int id) throws Exception;

    public abstract Temapublicacion getTemaPublicacion(int id) throws Exception;

    public abstract List<Temapublicacion> getTemaPublicacionAll(Tema t) throws Exception;

    public abstract void eliminarUsuario(String  ...  cuenta) throws Exception;

    public abstract boolean existeUsuario(String Cuenta, String contraseña) throws Exception;

    public abstract boolean existeUsuario(String Cuenta) throws Exception;

    public abstract boolean existeTema(String tipoDeTema, String tema) throws Exception;
    public abstract boolean existeTipoDeTema(String tipoDeTema) throws Exception;
    public abstract Users getUsuario(String Cuenta, String contraseña)throws Exception;

    public abstract Users getUsuario(String Cuenta) throws Exception;

    public abstract Tipodetema getTipoDeTema(String nombre) throws Exception;

    public abstract Tema getTema(String tipo, String nombre) throws Exception;

    public abstract Subcripcion getSubcripcion(Users propietario, Users subscriptor) throws Exception;

    public abstract List<Subcripcion> getSubcripcionDeSubscriptorAllList(Users subscriptor) throws Exception;
    public abstract List<Subcripcion> getSubcripcionDeSubscriptoresAllList(Users propetraio)throws Exception;
    //public abstract LinkedList<Users> getUsuariosSubscritosAllList(Users propetraio) throws Exception;

    public abstract LinkedList<Users> getUsuariosSeguidosAllList(Users subscriptor) throws Exception;

    public abstract Clasificacion updateClasificacion(Clasificacion c)throws Exception;

    //public abstract Clasificacion getClasificacion(String cuenta, int publicacionID) throws Exception;
    public abstract Clasificacion getClasificacion(Users u, Publicacion p) throws Exception;

    public abstract Mensaje getMensaje(int id) throws Exception;

    public abstract Clasificacion getClasificacion(int id) throws Exception;

    public abstract Users[] getUsuarioAll() throws Exception;

    public abstract LinkedList<Users> getUsuarioAllList() throws Exception;

    public abstract LinkedList<Mensaje> getMensajeAllList() throws Exception;

    public abstract List<Mensaje> getMensajeAllListEntreAmbosSort(Users uno, Users dos) throws Exception;

    public abstract List<Mensaje> getMensajeAllList(Users u) throws Exception;

    public abstract List<Mensaje> getMensajeEnviadoAllList(Users u)throws Exception;

    public abstract List<Mensaje> getMensajeRecividoAllList(Users u) throws Exception;

    public abstract List<Mensaje> getMensajeAllList(Users origen, Users destinatario) throws Exception;

    public abstract LinkedList<Tema> getTemaAllList() throws Exception;
    public abstract LinkedList<Publicacion> getPublicacionAllList() throws Exception;
    public abstract LinkedList<Tipodetema> getTipoDeTemaAllList() throws Exception;

    //public abstract LinkedList<Publicacion> getPublicacionSeguidosAllList(Users u) throws Exception;

    public abstract LinkedList<Publicacion> getPublicacionAllList(Users u) throws Exception;

    public abstract Publicacion getPublicacion(Users u, String titulo) throws Exception;
    public abstract LinkedList<Publicacion> getPublicacionAllList(String tipoDeTema, String tema)throws Exception;

    public abstract LinkedList<Publicacion> getPublicacionAllList(Tema t)throws Exception;

    public abstract Users updateUsuario(Users u)throws Exception;

    public abstract Publicacion updatePublicacion(Publicacion p) throws Exception;

    public abstract Mensaje updateMensaje(Mensaje m) throws Exception;

    public abstract Comentario updateComentario(Comentario c)throws Exception;

    public abstract Subcripcion updateSubcripcion(Subcripcion s) throws Exception;

    public abstract Users addUsuario(Users u) throws Exception;

    public  abstract Users addUsuario(Users u, boolean esAdministrador)throws Exception;
    
    public  abstract LinkedList<Tema> getTemaAllList(String tipo) throws Exception ;
    public  List<Temapublicacion> getTemaPublicacionAll(Publicacion p) throws Exception;
    public  List<Comentario> getComentarioAllList(Publicacion p)throws Exception;
    
    public  Users updateUsuarioBasicos(Users u) throws Exception ;
    
    public boolean estaConectado(String cuenta) throws Exception;
    
   
    
    public Publicacion addPublicacion(Users u, String contenido, String titulo, int... idTemas) throws Exception ;
    
    
    public String[] getDireccionesIps() throws Exception;
    public Servidores addServidores(Servidores c) throws Exception ;
    public Servidores getServidore(int id) throws Exception;
    public Servidores getNewServidores(String direccionIp) throws Exception;
    public Servidores addServidores(String direccionIP) throws Exception ;
    
    public List<Clasificacion> getClasificacionAllList(Users u)throws Exception  ;
    public List<Comentario> getComentarioAllList(Users s) throws Exception;
    public LinkedList<String> getUsernamesSubscritosAllList(Users propetraio) throws Exception;
    public Clasificacion addClasificacion(Users u, Publicacion p)throws Exception  ;
    public List<Clasificacion> getClasificacionAllList(Publicacion p) throws Exception ;
    
     public List<Mensaje> getMensajeNoVistoAllList(String cuentaOrigen, String cuentaDestino)throws Exception;
    public int getCantidadDeMensajeNoVisto(String origen, String destinatario)throws Exception;
//     public List<Mensaje> getMensajeNoVistoAllList(Users origen, Users destinatario)throws Exception;
//    public int getCantidadDeMensajeNoVisto(Users origen, Users destinatario)throws Exception;
    
public LinkedList<Publicacion> getPublicacionAllList_Limite_Sort(Users u,int limite) throws Exception;
public LinkedList<Publicacion> getPublicacionAllList_Limite_Sort(int limite) throws Exception;
public LinkedList<Servidores> getServidoresAllList() throws Exception  ;
public Servidores getNewServidores() throws Exception ;
 public Servidores updateServidores(Servidores u) throws Exception ;
 public void eliminarServidores(Servidores s) throws Exception ;
 public void eliminarServidores(int id) throws Exception ;
 public boolean existeServidor(String direccion) throws Exception ;
 public Servidores getServidor(String direccion) throws Exception ;

// creacion de objetos *****************************88
//    public DialogoChat getDialogoChat(Users propetario,String destinatario) throws Exception;
//    public DialogoChat getDialogoChat(Users propetario,Users destinatario) throws Exception;
//     public DatosDeBeanPrincipal getDatosDeBeanPrincipal(String cuenta)throws Exception;
//     public EstadisticasDeUsuario getEstadisticasDeUsuario(String cuenta) throws Exception;
//     public DatosDeBeanTodasLasPublicaciones getDatosDeBeanTodasLasPublicaciones(SeleccionDeSubmenu sel,String actual) throws Exception;
//      public LinkedList<DatosDeComentario> addComentarioYgetLista(String cuenta, int idPublicacion, String contenido)throws Exception;
//      public ClasificacionTemporal apretoPuntuacionPersonal(ClasificacionTemporal c)throws Exception;
//      public ClasificacionTemporal apretoLike(ClasificacionTemporal c)throws Exception;
//      public ClasificacionTemporal apretoDislike(ClasificacionTemporal c)throws Exception;
      public List<Mensaje> getMensajeRecividoAllList(String cuentaDestino) throws Exception;
      public void eliminarMensajesDestino(String cuentaDestino)throws Exception;
      public List<Subcripcion> getSubcripcionDeSubscriptorAllList(String cuentaSubscriptor)throws Exception ;
      public void eliminarSubcripcionesDeSubcriptor(String cuentaSubscriptor)throws Exception;
      public List<Comentario> getComentarioAllList(String cuenta) throws Exception ;
      public void eliminarComentariosAll(String cuenta) throws Exception ;
      public List<Clasificacion> getClasificacionAllList(String cuenta) throws Exception ;
      public void eliminarClasificacionsAll(String cuenta) throws Exception ;
    // comunicacion **************************8888
    public void publicoMensaje(Mensaje m) throws Exception ;
    public void vioLosMensajes(String propietario, String destino) throws Exception ;
    public void seEliminaronCuentas(String host, int port, String nombreObjetoRemoto, final String... cuentasEliminadas) throws Exception ;
    public void seAgregoUnaCuenta(String host, int port, String nombreObjetoRemoto, String cuenta) throws Exception;
    public void seEliminoPublicacion(String cuenta,int idPublicacion) throws Exception;
    //+++++++++++++++++++++++++++++++++++++====
    public String llamarServidor(String a)throws Exception;
    public boolean estaDisponible()throws Exception;
}
