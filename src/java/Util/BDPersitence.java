/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import Util.ClasesDeApoyo.*;
import Util.PersistenceUtil;
import static Util.PersistenceUtil.ROLE_ADMIN;
import static Util.PersistenceUtil.ROLE_USER;
import static Util.PersistenceUtil.inicializarBasicosUsersAenB;
import static Util.control.authoritiesJpaController;
import static Util.control.clasificacionJpaController;
import static Util.control.comentarioJpaController;
import static Util.control.mensajeJpaController;
import static Util.control.publicacionJpaController;
import static Util.control.subcripcionJpaController;
import static Util.control.temaJpaController;
import static Util.control.temapublicacionJpaController;
import static Util.control.tipodetemaJpaController;
import static Util.control.usersJpaController;
import Utiles.ClasesUtiles.BasesDeDatos.BDConexionController;
import Utiles.ClasesUtiles.Servidores.Serializable.ObjetoRemoteServidorImp;
import Utiles.MetodosUtiles.Archivo;
import Util.ClasesDeApoyo.ComparadorDeMensajes;
import Util.ClasesDeApoyo.Estadisticas.EstadisticasDeUsuario;
import static Util.control.servidoresJpaController;
import Utiles.ClasesUtiles.BasesDeDatos.BDConect;
import Utiles.JSF.FacesUtil;
//import comunicacion.ComunicacionCliente1;
import comunicacion.ComunicacionCliente2;
//import comunicacion.ComunicacionServidor1;
import comunicacion.ComunicacionServidor2;
import controller.ServidoresJpaController;
import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;
import entity.Authorities;
import entity.AuthoritiesPK;
import entity.Clasificacion;
import entity.Comentario;
import entity.Mensaje;
import entity.Publicacion;
import entity.Servidores;
import entity.Subcripcion;
import entity.Tema;
import entity.Temapublicacion;
import entity.Tipodetema;
import entity.Users;
//import entity.Usuario_;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Callback;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author Rene
 */
public class BDPersitence extends ObjetoRemoteServidorImp<ComunicacionCliente2> implements ComunicacionServidor2 {
//fue null por empty

    private final static String TABLA_PUBLICACION = "publicacion", TABLA_TEMA = "tema",
            TABLA_TEMA_PUBLICACION = "temapublicacion", TABLA_TIPO_DE_TEMA = "tipodetema",
            TABLA_CLASIFICACION = "clasificacion", TABLA_COMENTARIO = "comentario",
            TABLA_MENSAJE = "mensaje", TABLA_SUBSCRIPCION = "subcripcion", TABLA_SERVIDORES = "servidores";

    private static byte[] img;

    public BDPersitence(String host, int port, String nombreObjetoRemoto) throws RemoteException {
        this(host, port, nombreObjetoRemoto, null);
    }

    public BDPersitence(String host, int port, String nombreObjetoRemoto, byte[] imge) throws RemoteException {
        super(host, port, nombreObjetoRemoto);
        if (imge != null) {
            img = imge;
        } else {
            if (FacesContext.getCurrentInstance() == null) {
                try {
                    File fimg = new File("src/java/recursos/img/u9.png").getAbsoluteFile();
                    //  System.out.println("fimg=" + fimg + "  " + fimg.exists());
                    img = Archivo.getBytesDeImg(fimg);
                    // System.out.println("img=" + img + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    // img = Archivo.getBytesDeImg(new File("src\\recursos\\img\\u9.png").getAbsoluteFile());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public Servidores addServidores(String direccionIP) throws Exception {
        return addServidores(getNewServidores(direccionIP));
    }

    public Servidores addServidores(Servidores c) throws Exception {

        if (c.getId() == null) {
            c.setId(getIDComentarioCorrespondiente());
        }
        servidoresJpaController.create(c);
        return getServidore(c.getId());
    }

    public Servidores getNewServidores() throws Exception {
        return getNewServidores("");
    }

    public Servidores getNewServidores(String direccionIp) throws Exception {
        Servidores s = new Servidores();
        s.setIp(direccionIp);

        return s;
    }

    public void clearBD() throws Exception {
        eliminarUsuarioAll();
        eliminarTipoDeTemaAll();
    }

    public boolean existeSubcripcion(Users propietario, Users subscriptor) throws Exception {
        return getSubcripcion(propietario, subscriptor) != null;
    }

    public void eliminarSubcripcion(Users propietario, Users subscriptor) throws Exception {
        eliminarSubcripcion(getSubcripcion(propietario, subscriptor));
    }

    public Subcripcion addSubcripcion(Users propietario, Users subscriptor) throws Exception {
        return addSubcripcion(getNewSubcripcion(propietario, subscriptor));
    }

    public Subcripcion getNewSubcripcion(Users propietario, Users subscriptor) {
        Subcripcion s = new Subcripcion();
        s.setFecha(new Date());
        s.setUsersusernamepropietario(propietario);
        //s.setUsersusernamesubscriptor(subscriptor);

        s.setUsersusernamesubscriptor(subscriptor.getUsername());
        return s;
    }

    public Mensaje getNewMensaje(Users origen, Users destino, String contenido) {
        Mensaje m = new Mensaje();
        m.setContenido(contenido);
        m.setFecha(new Date());
        m.setUsernamedestino(destino.getUsername());
        //m.setUsersusernamedestino(destino.getUsername());
        m.setUsersusernameorigen(origen);

        m.setVisto(false);
        return m;
    }

    public Mensaje addMensaje(String origen, String destino, String contenido) throws Exception {
        return addMensaje(getUsuario(origen), getUsuario(destino), contenido);
    }

    public Mensaje addMensaje(Users origen, Users destino, String contenido) throws Exception {
        return addMensaje(getNewMensaje(origen, destino, contenido));
    }

    public Comentario addComentario(Users u, Publicacion p, String contenido) throws Exception {
        return addComentario(getNewComentario(u, p, contenido));
    }

    public Comentario getNewComentario(Users u, Publicacion p, String contenido) {
        Comentario c = new Comentario();
        c.setContenido(contenido);
        c.setFecha(new Date());

        c.setPublicacionid(p);
        //c.setUsersusername(u);
        c.setUsenameid(u.getUsername());
        return c;
    }

//    public boolean esAdministrador(String rol) {
//        return rol.equals(ROLE_ADMIN);
//    }
    public boolean esAdministrador(Users u) {
        return PersistenceUtil.esAdministrador(getUsuario(u.getUsername()).getAuthoritiesCollection().iterator().next().getAuthoritiesPK().getAuthority());
    }

    public List<String> getRolesUsuario() {
        return Arrays.asList(ROLE_ADMIN, ROLE_USER);
    }

    /**
     * [0] cantidad de likes <br/>
     * [1] cantidad de dislikes
     *
     * @param p
     * @return
     */
    public int[] getCantidadDeLikesYDislikes(final Publicacion p) {
        Callback<Boolean, Integer> c = v -> clasificacionJpaController.getEntityManager().createQuery("SELECT c FROM Clasificacion c WHERE   c.publicacionid.id=" + p.getId() + " AND c.legusta= " + v).getResultList().size();

        return new int[]{c.call(true), c.call(false)};
    }

    /**
     * [0] cantidad de likes <br/>
     * [1] cantidad de dislikes
     *
     * @param pid
     * @return
     */
    public int[] getCantidadDeLikesYDislikes(final int pid) {
        Callback<Boolean, Integer> c = v -> clasificacionJpaController.getEntityManager().createQuery("SELECT c FROM Clasificacion c WHERE   c.publicacionid.id=" + pid + " AND c.legusta= " + v).getResultList().size();

        return new int[]{c.call(true), c.call(false)};
    }

    /**
     * [0] promedio si no hay es 0 <br/>
     * [1] cantidad
     *
     * @param pid
     * @return
     */
    public int[] getPuntacionGeneralYCantidadDeOpiniones(int pid) {
        Query q = clasificacionJpaController.getEntityManager().createQuery("SELECT AVG(c.clasificacion) FROM Clasificacion c WHERE  c.publicacionid.id=" + pid);
        boolean paso = q != null && q.getResultList() != null && !q.getResultList().isEmpty() && q.getResultList().get(0) != null;

        int promedio = 0, cantidad = 0;
        if (paso) {
            promedio = (int) ((Double) q.getSingleResult()).intValue();
            cantidad = clasificacionJpaController.getEntityManager().createQuery("SELECT c FROM Clasificacion c WHERE  c.publicacionid.id=" + pid).getResultList().size();
        }
        return new int[]{promedio, cantidad};
    }

//    public Clasificacion getNewClasificacion(String cuenta, int publicacionID) {
//        return getNewClasificacion(getUsuario(cuenta), getPublicacion(publicacionID));
//    }
    @Override
    public Clasificacion getNewClasificacion(Users u, Publicacion p) {
        Clasificacion c = new Clasificacion();

        c.setPublicacionid(p);
        // c.setUsersusername(u);
        c.setUsernameid(u.getUsername());
        return c;
    }

    public Temapublicacion getNewTemaPublicacion(Tema t, Publicacion p) {
        Temapublicacion tp = new Temapublicacion();
        tp.setNoticiaid(p);
        tp.setTemaid(t);

        return tp;
    }

    public Tipodetema getNewTipodetema(String nombre) {
        Tipodetema t = new Tipodetema();
        t.setNombre(nombre);

        t.setTemaCollection(new LinkedList());

        return t;
    }

    public Tema getNewTema(String nombre, Tipodetema ti) {
        Tema t = new Tema();
        t.setNombre(nombre);
        t.setTipodetemaid(ti);

        t.setTemapublicacionCollection(new LinkedList());

        return t;
    }

    public Publicacion addPublicacion(String cuenta, String tipoDeTema, String tema, String contenido, String titulo) throws Exception {
        return addPublicacion(getUsuario(cuenta), contenido, titulo, getTema(tipoDeTema, tema));
    }

    public Publicacion addPublicacion(Users u, String contenido, String titulo, int... idTemas) throws Exception {
        Tema temasSele[] = new Tema[idTemas.length];
        for (int i = 0; i < temasSele.length; i++) {
            temasSele[i] = getTema(idTemas[i]);
        }
        return addPublicacion(u, contenido, titulo, temasSele);
    }

    public Publicacion addPublicacion(Users u, String contenido, String titulo, Tema... t) throws Exception {
        return addPublicacion(getNewPublicacion(u, contenido, titulo), t);
    }

    public Publicacion getNewPublicacion(Users u, String contenido, String titulo) {
        Publicacion p = new Publicacion();

        p.setContenido(contenido.getBytes());
        p.setFecha(new Date());
        p.setTitulo(titulo);
        p.setUsersusername(u);

        p.setComentarioCollection(new LinkedList());
        p.setTemapublicacionCollection(new LinkedList());
        p.setClasificacionCollection(new LinkedList());
        return p;
    }

    public Users getNewUser(String username, String identificacion, String nombre, String apellido1, String apellido2, String email, String password, boolean enabled) {
        Users u = new Users();
        u.setApellido1(apellido1);
        u.setApellido2(apellido2);
        u.setDescription(identificacion);
        u.setEmail(email);
        u.setEnabled(enabled);
        u.setIdentificacion(identificacion);
        u.setNombre(nombre);
        u.setPassword(password);
        u.setUsername(username);

        u.setAuthoritiesCollection(new LinkedList());
//        u.setComentarioCollection(new LinkedList());
        u.setMensajeCollection(new LinkedList());
        //      u.setMensajeCollection1(new LinkedList());
        u.setPublicacionCollection(new LinkedList());
        u.setSubcripcionCollection(new LinkedList());
        //    u.setSubcripcionCollection1(new LinkedList());
        //  u.setClasificacionCollection(new LinkedList());
        return u;
    }

    public Users getNewUser() {
        return getNewUser("", "", "", "", "", "", "", true);
//        Users u = new Users();
//        u.setApellido1("");
//        u.setApellido2("");
//        u.setDescription("");
//        u.setEmail("");
//        u.setEnabled(true);
//        u.setIdentificacion("");
//        u.setNombre("");
//        u.setPassword("");
//        u.setUsername("");
//
//        u.setAuthoritiesCollection(new LinkedList());
//        u.setComentarioCollection(new LinkedList());
//        u.setMensajeCollection(new LinkedList());
//        u.setMensajeCollection1(new LinkedList());
//        u.setPublicacionCollection(new LinkedList());
//        u.setSubcripcionCollection(new LinkedList());
//        u.setSubcripcionCollection1(new LinkedList());
//        return u;

    }

    public void eliminarUsuarioAll() throws Exception {
        List<Users> l = getUsuarioAllList();
        for (int i = 0; i < l.size(); i++) {
            eliminarUsuario(l.remove(i--));
        }
    }

    public void eliminarUsuario(Users u) throws Exception {
        eliminarAuthorities(u);
        eliminarSubcripcionAll(getUsuario(u.getUsername()));
        eliminarMensajeAll(getUsuario(u.getUsername()));
        eliminarClasificacionsAll(getUsuario(u.getUsername()));
        eliminarComentariosAll(getUsuario(u.getUsername()));
        eliminarPublicacionAll(getUsuario(u.getUsername()));

        usersJpaController.destroy(u.getUsername());

    }

    public void eliminarSubcripcionAll(Users propietario) throws NonexistentEntityException {
        List<Subcripcion> l = getSubcripcionDeSubscriptoresAllList(propietario);
        for (int i = 0; i < l.size(); i++) {
            eliminarSubcripcion(l.remove(i--));
        }
        l = getSubcripcionDeSubscriptorAllList(propietario);
        for (int i = 0; i < l.size(); i++) {
            eliminarSubcripcion(l.remove(i--));
        }
    }

    public void eliminarServidores(int id) throws Exception {
        servidoresJpaController.destroy(id);
    }

    public void eliminarServidores(Servidores s) throws Exception {
        servidoresJpaController.destroy(s.getId());
    }

    public void eliminarSubcripcion(Subcripcion s) throws NonexistentEntityException {
        subcripcionJpaController.destroy(s.getId());
    }

    public void eliminarMensaje(Mensaje m) throws NonexistentEntityException {
        mensajeJpaController.destroy(m.getId());
    }

    public void eliminarComentario(int id) throws NonexistentEntityException {
        comentarioJpaController.destroy(id);
    }

    public void eliminarComentario(Comentario c) throws NonexistentEntityException {
        comentarioJpaController.destroy(c.getId());
    }

    public void eliminarClasificacion(Clasificacion c) throws NonexistentEntityException {
        clasificacionJpaController.destroy(c.getId());
    }

    public void eliminarTemaPublicacion(Temapublicacion t) throws NonexistentEntityException {
        temapublicacionJpaController.destroy(t.getId());
    }

    public void eliminarTemaPublicacionAll(Tema t) throws NonexistentEntityException {
        List<Temapublicacion> ltp = getTemaPublicacionAll(t);
        for (Iterator<Temapublicacion> iterator = ltp.iterator(); iterator.hasNext();) {
            Temapublicacion next = iterator.next();
            eliminarTemaPublicacion(next);
        }
    }

    public void eliminarTemaPublicacionAll(Publicacion p) throws NonexistentEntityException {
        List<Temapublicacion> ltp = getTemaPublicacionAll(p);
        for (Iterator<Temapublicacion> iterator = ltp.iterator(); iterator.hasNext();) {
            Temapublicacion next = iterator.next();
            eliminarTemaPublicacion(next);
        }
    }

    public void eliminarTipoDeTemaAll() throws NonexistentEntityException, IllegalOrphanException {
        List<Tipodetema> l = getTipoDeTemaAllList();
        for (int i = 0; i < l.size(); i++) {
            eliminarTipoDeTema(l.remove(i--));
        }
    }

    public void eliminarTipoDeTema(Tipodetema t) throws NonexistentEntityException, IllegalOrphanException {
        eliminarTemaAll(t);
        tipodetemaJpaController.destroy(t.getId());
    }

    public void eliminarTemaAll(Tipodetema t) throws NonexistentEntityException, IllegalOrphanException {
        for (Iterator<Tema> iterator = t.getTemaCollection().iterator(); iterator.hasNext();) {
            Tema next = iterator.next();
            eliminarTema(next);
        }

    }

    public void eliminarTema(Tema t) throws NonexistentEntityException, IllegalOrphanException {
        eliminarTemaPublicacionAll(t);
        temaJpaController.destroy(t.getId());
    }

    public void eliminarPublicacion(Publicacion p) throws Exception {
        eliminarTemaPublicacionAll(p);
        eliminarComentariosAll(p);
        eliminarClasificacionsAll(p);
        publicacionJpaController.destroy(p.getId());
    }

    public void eliminarPublicacionAll(Users propietario) throws Exception {
        List<Publicacion> l = getPublicacionAllList(propietario);
        for (int i = 0; i < l.size(); i++) {
            eliminarPublicacion(l.remove(i--));
        }

    }

    public void eliminarClasificacionsAll(Publicacion p) throws Exception {
        List<Clasificacion> lc = getClasificacionAllList(p);
        for (Iterator<Clasificacion> iterator = lc.iterator(); iterator.hasNext();) {
            Clasificacion next = iterator.next();
            eliminarClasificacion(next);
        }
    }

    public void eliminarClasificacionsAll(Users p) throws Exception {
        List<Clasificacion> lc = getClasificacionAllList(p);
        for (Iterator<Clasificacion> iterator = lc.iterator(); iterator.hasNext();) {
            Clasificacion next = iterator.next();
            eliminarClasificacion(next);
        }
    }

    public void eliminarMensajeAll(Users p) throws NonexistentEntityException {
        List<Mensaje> l = getMensajeAllList(p);
        for (int i = 0; i < l.size(); i++) {
            eliminarMensaje(l.remove(i--));
        }
//    for (Iterator<Mensaje> iterator = p.getMensajeCollection().iterator(); iterator.hasNext();) {
//        Mensaje next = iterator.next();
//        eliminarMensaje(next);
//    }
//    for (Iterator<Mensaje> iterator = p.getMensajeCollection1().iterator(); iterator.hasNext();) {
//        Mensaje next = iterator.next();
//        eliminarMensaje(next);
//    }
    }

    public void eliminarComentariosAll(Publicacion p) throws NonexistentEntityException {
        List<Comentario> lc = getComentarioAllList(p);
        for (Iterator<Comentario> iterator = lc.iterator(); iterator.hasNext();) {
            Comentario next = iterator.next();
            eliminarComentario(next);
        }
    }

    public void eliminarComentariosAll(Users p) throws Exception {
        List<Comentario> lc = getComentarioAllList(p);
        for (Iterator<Comentario> iterator = lc.iterator(); iterator.hasNext();) {
            Comentario next = iterator.next();
            eliminarComentario(next);
        }
    }

    public boolean getEsAdministrador(Users u) {
        Authorities a = getAuthorities(u);
        return a != null && a.getAuthoritiesPK().getAuthority().equals(ROLE_ADMIN);
    }

    public String getRol(Users u) {
        //System.out.println("prueba 1 ********: u="+u);
        Authorities a = getAuthorities(u);
        //  System.out.println("prueba 1 ********: a="+a);
        AuthoritiesPK apk = a.getAuthoritiesPK();
        // System.out.println("prueba 1 ********: apk="+apk);
        String au = apk.getAuthority();
        // System.out.println("prueba 1 ********: au="+au);
        return au;
        //return getAuthorities(u).getAuthoritiesPK().getAuthority();
    }

    public void setAuthorities(Users u, boolean administrador) throws NonexistentEntityException, Exception {
        Authorities a = getAuthorities(u);
        String rol = administrador ? ROLE_ADMIN : ROLE_USER;
        if (a != null) {
//            Users u2 = getUsuario(u.getUsername());
//            authoritiesJpaController.destroy(getAuthorities(u.getUsername()).getAuthoritiesPK());
//            u.getAuthoritiesCollection().clear();
//            
//            Authorities a2 = new Authorities(rol, u.getUsername());
//            a2.setUsers(u);
//            authoritiesJpaController.create(a2);
            Users u2 = getUsuario(u.getUsername());
            authoritiesJpaController.destroy(getAuthorities(u2.getUsername()).getAuthoritiesPK());
            //u.getAuthoritiesCollection().clear();
            u2 = getUsuario(u2.getUsername());
            Authorities a2 = new Authorities(rol, u2.getUsername());
            a2.setUsers(u2);
            authoritiesJpaController.create(a2);
        } else {
            // a = new Authorities(u.getUsername(), rol);
            a = new Authorities(rol, u.getUsername());
            a.setUsers(u);
            addAuthorities(a);
        }

    }

    public Publicacion addPublicacion(Publicacion u) throws Exception {
        if (u.getId() == null) {
            u.setId(getIDPublicacionCorrespondiente());//a
        } else {
            System.out.println("no fue null !!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        publicacionJpaController.create(u);
        return getPublicacion(u.getId());

    }

//     public Clasificacion addClasificacion(String cuenta, int publicacionID) throws Exception {
//        return addClasificacion(getNewClasificacion(cuenta, publicacionID));
//    }
    public Clasificacion addClasificacion(Users u, Publicacion p) throws Exception {
        return addClasificacion(getNewClasificacion(u, p));
    }

    public Clasificacion addClasificacion(Clasificacion c) throws Exception {
        if (c.getId() == null) {
            c.setId(getIDClasificacionCorrespondiente());//a
        }
        clasificacionJpaController.create(c);
        return getClasificacion(c.getId());
    }

    public Subcripcion addSubcripcion(Subcripcion s) throws Exception {
        if (s.getId() == null) {
            s.setId(getIDSubcripcionCorrespondiente());//a
        }
        subcripcionJpaController.create(s);
        return getSubcripcion(s.getId());
    }

    public Mensaje addMensaje(Mensaje c) throws Exception {
        if (c.getId() == null) {
            c.setId(getIDMensajeCorrespondiente());//a
        }
        mensajeJpaController.create(c);
        return getMensaje(c.getId());
    }

    private static void comprobarExistenciaDeTemas(Tema... T) throws Exception {
        if (T.length == 0) {
            throw new Exception("No hay temas seleccionados");
        }
    }

    public Publicacion addPublicacion(Publicacion nueva, Tema... T) throws Exception {
        comprobarExistenciaDeTemas(T);
        Publicacion p = addPublicacion(nueva);
        for (int i = 0; i < T.length; i++) {
            addTemaPublicacion(getNewTemaPublicacion(T[i], p));
        }

        return getPublicacion(nueva.getId());

    }

    public Tema addTema(String tipoDeTema, String nombre) throws Exception {
        Tipodetema ti = getTipoDeTema(tipoDeTema);
        if (ti == null) {
            ti = addTipoDeTema(tipoDeTema);
        }
        //Tema t=
        return addTema(getNewTema(nombre, ti));
    }

    public Tema addTema(Tema u) throws Exception {
        if (u.getId() == null) {
            u.setId(getIDTemaCorrespondiente());
        }
        temaJpaController.create(u);
        return getTema(u.getId());

    }

    public Tipodetema addTipoDeTema(String tipoDeTema) throws Exception {
        return addTipoDeTema(getNewTipodetema(tipoDeTema));
    }

    public Tipodetema addTipoDeTema(Tipodetema u) throws Exception {
        if (u.getId() == null) {
            u.setId(getIDTipoDeTemaCorrespondiente());
        }
        tipodetemaJpaController.create(u);
        return getTipodetema(u.getId());

    }

    public Comentario addComentario(Comentario u) throws Exception {
        if (u.getId() == null) {
            u.setId(getIDComentarioCorrespondiente());
        }
        comentarioJpaController.create(u);
        return getComentario(u.getId());

    }

    public Temapublicacion addTemaPublicacion(Temapublicacion u) throws Exception {
        if (u.getId() == null) {
            u.setId(getIDTemaPublicacionCorrespondiente());
        }
        temapublicacionJpaController.create(u);
        return getTemaPublicacion(u.getId());

    }

    public Authorities addAuthorities(Authorities u) throws Exception {
        authoritiesJpaController.create(u);
        return getAuthorities(u.getUsers());

    }

    public void eliminarAuthorities(Users u) throws NonexistentEntityException {
        eliminarAuthorities(getAuthorities(u));

    }

    public void eliminarAuthorities(Authorities a) throws NonexistentEntityException {
        if (a != null) {
            authoritiesJpaController.destroy(a.getAuthoritiesPK());
        }

    }

//    public static Authorities getAuthorities(Users u) {
//        System.out.println("u="+u);
//        if(u!=null){
//            System.out.println("u.getAuthoritiesCollection()="+u.getAuthoritiesCollection().size());
//            if(!u.getAuthoritiesCollection().isEmpty()){
//            Authorities a=u.getAuthoritiesCollection().iterator().next();
//                System.out.println("u.getAuthoritiesCollection().iterator().next()="+a);
//                return a;
//            
//            }
//        }
//        System.out.println("return null llllllllllllllll");
//        return null;
//      // return (u != null&&!u.getAuthoritiesCollection().isEmpty())?u.getAuthoritiesCollection().iterator().next():null;
//    }
//
//    public static Authorities getAuthorities(String username) {
//       return getAuthorities(getUsuario(username));
//    }
    public Authorities getAuthorities(Users u) {
        return u != null ? getAuthorities(u.getUsername()) : null;
        //  return (u != null&&!u.getAuthoritiesCollection().isEmpty())?u.getAuthoritiesCollection().iterator().next():null;
    }

    public Authorities getAuthorities(String username) {
        // Authorities a = authoritiesJpaController.findAuthorities(new AuthoritiesPK(username, ROLE_USER));
        Authorities a = authoritiesJpaController.findAuthorities(new AuthoritiesPK(ROLE_USER, username));
        if (a == null) {
            a = authoritiesJpaController.findAuthorities(new AuthoritiesPK(ROLE_ADMIN, username));
        }
        return a;
        // return getAuthorities(getUsuario(username));
    }

    public Publicacion getPublicacion(int id) {
        return publicacionJpaController.findPublicacion(id);
    }

    public Subcripcion getSubcripcion(int id) {
        return subcripcionJpaController.findSubcripcion(id);
    }

    public Comentario getComentario(int id) {
        return comentarioJpaController.findComentario(id);
    }

    public Servidores getServidore(int id) {
        return servidoresJpaController.findServidores(id);
    }

//    public Comentario getComentario(Publicacion p,int id) {
//    
//    }
    public List<Comentario> getComentarioAllList(Publicacion p) {

        return comentarioJpaController.getEntityManager().createQuery("SELECT c FROM Comentario c WHERE  c.publicacionid.id = \"" + p.getId() + "\"").getResultList();
    }

    public List<Comentario> getComentarioAllList(Users s) throws Exception {

        return comentarioJpaController.getEntityManager().createQuery("SELECT c FROM Comentario c WHERE  c.usenameid = \"" + s.getUsername() + "\"").getResultList();
    }

    public Tema getTema(int id) {
        return temaJpaController.findTema(id);
    }

    public Tipodetema getTipodetema(int id) {
        return tipodetemaJpaController.findTipodetema(id);
    }

    public Temapublicacion getTemaPublicacion(int id) {
        return temapublicacionJpaController.findTemapublicacion(id);
    }

    public List<Temapublicacion> getTemaPublicacionAll(Tema t) {

        return temapublicacionJpaController.getEntityManager().createQuery("SELECT t FROM Temapublicacion t WHERE t.temaid.id = \"" + t.getId() + "\"").getResultList();
    }

    public List<Temapublicacion> getTemaPublicacionAll(Publicacion p) {

        return temapublicacionJpaController.getEntityManager().createQuery("SELECT t FROM Temapublicacion t WHERE t.noticiaid.id = \"" + p.getId() + "\"").getResultList();
    }

    public void eliminarUsuario(String... cuentas) throws Exception {
        for (String cuenta : cuentas) {
            eliminarUsuario(getUsuario(cuenta));
        }

    }

    public boolean existeUsuario(String Cuenta, String contraseña) throws Exception {
        return getUsuario(Cuenta, contraseña) != null;
    }

    public boolean existeUsuario(String Cuenta) throws Exception {
        return getUsuario(Cuenta) != null;
    }

    public boolean existeServidor(String direccion) throws Exception {
        return getServidor(direccion) != null;
    }

    public Servidores getServidor(String direccion) throws Exception {
        Query q = servidoresJpaController.getEntityManager().createQuery("SELECT p FROM Servidores  p WHERE  p.ip = \"" + direccion + "\"");
        return q.getResultList().isEmpty() ? null : (Servidores) q.getSingleResult();
    }

    public boolean existeTema(String tipoDeTema, String tema) throws Exception {
        return getTema(tipoDeTema, tema) != null;
    }

    public boolean existeTipoDeTema(String tipoDeTema) throws Exception {
        return getTipoDeTema(tipoDeTema) != null;
    }

    public Users getUsuario(String Cuenta, String contraseña) throws Exception {
        Users u = getUsuario(Cuenta);
        return u == null || !u.getPassword().equals(contraseña) ? null : u;
    }

    public Users getUsuario(String Cuenta) {
        return usersJpaController.findUsers(Cuenta);
    }

    public Tipodetema getTipoDeTema(String nombre) {
        Query q = temaJpaController.getEntityManager().createQuery("SELECT t FROM Tipodetema t WHERE t.nombre = \"" + nombre + "\"");
        return q.getResultList().isEmpty() ? null : (Tipodetema) q.getSingleResult();

    }

    public Tema getTema(String tipo, String nombre) {
        //Query q = temaJpaController.getEntityManager().createQuery("SELECT t FROM Tema t WHERE t.nombre = \"" + nombre + "\"");
        System.out.println("tipo =" + tipo + " nombre=" + nombre);
        Tipodetema t = getTipoDeTema(tipo);
        if (t == null) {
            System.out.println("fue null por tipo");
            return null;
        }
//        int idTipoDeTema=getTipoDeTema(tipo).getId();

        Query q = temaJpaController.getEntityManager().createQuery("SELECT t FROM Tema t WHERE t.nombre = \"" + nombre + "\" AND t.tipodetemaid.id = " + t.getId() + "");
//        if (q.getResultList().isEmpty()) {
//            System.out.println("fue null por empty");
//        }

        return q.getResultList().isEmpty() ? null : (Tema) q.getSingleResult();
    }

    public Subcripcion getSubcripcion(Users propietario, Users subscriptor) {
        Query q = subcripcionJpaController.getEntityManager().createQuery("SELECT s FROM Subcripcion s WHERE s.usersusernamepropietario.username  = \"" + propietario.getUsername() + "\" AND s.usersusernamesubscriptor = \"" + subscriptor.getUsername() + "\"");
        return q.getResultList().isEmpty() ? null : (Subcripcion) q.getResultList().get(0);
    }

    public List<Subcripcion> getSubcripcionDeSubscriptorAllList(Users subscriptor) {
        return subcripcionJpaController.getEntityManager().createQuery("SELECT s FROM Subcripcion s WHERE  s.usersusernamesubscriptor = \"" + subscriptor.getUsername() + "\"").getResultList();
    }

    public List<Subcripcion> getSubcripcionDeSubscriptoresAllList(Users propetraio) {
        return subcripcionJpaController.getEntityManager().createQuery("SELECT s FROM Subcripcion s WHERE  s.usersusernamepropietario.username = \"" + propetraio.getUsername() + "\"").getResultList();
    }

//    public LinkedList<Users> getUsuariosSubscritosAllList(Users propetraio) {
//        LinkedList<Users> res = new LinkedList<Users>();
//        List<Subcripcion> l = getSubcripcionDeSubscriptoresAllList(propetraio);
//        for (int i = 0; i < l.size(); i++) {
//            res.add(l.get(i).getUsersusernamesubscriptor());
//        }
//        return res;
//    }
    public LinkedList<String> getUsernamesSubscritosAllList(Users propietario) throws Exception {
        LinkedList<String> res = new LinkedList<String>();
        List<Subcripcion> l = getSubcripcionDeSubscriptoresAllList(propietario);
        for (int i = 0; i < l.size(); i++) {
            res.add(l.get(i).getUsersusernamesubscriptor());
        }
        return res;
    }

    public LinkedList<Users> getUsuariosSeguidosAllList(Users subscriptor) {
        LinkedList<Users> res = new LinkedList<Users>();
        List<Subcripcion> l = getSubcripcionDeSubscriptorAllList(subscriptor);
        for (int i = 0; i < l.size(); i++) {
            res.add(l.get(i).getUsersusernamepropietario());
        }
        return res;
    }

    public Clasificacion updateClasificacion(Clasificacion c) throws Exception {
        clasificacionJpaController.edit(c);
        return getClasificacion(c.getId());
    }

//    public Clasificacion getClasificacion(String cuenta, int publicacionID) {
//        return getClasificacion(getUsuario(cuenta), getPublicacion(publicacionID));
//    }
    public List<Clasificacion> getClasificacionAllList(Publicacion p) throws Exception {
        return clasificacionJpaController.getEntityManager().createQuery("SELECT c FROM Clasificacion c WHERE c.publicacionid.id = \"" + p.getId() + "\"").getResultList();
    }

    public List<Clasificacion> getClasificacionAllList(Users u) throws Exception {
        return clasificacionJpaController.getEntityManager().createQuery("SELECT c FROM Clasificacion c WHERE c.usernameid = \"" + u.getUsername() + "\"").getResultList();
    }

    public Clasificacion getClasificacion(Users u, Publicacion p) {
        Query q = clasificacionJpaController.getEntityManager().createQuery("SELECT c FROM Clasificacion c WHERE c.usernameid = \"" + u.getUsername() + "\" AND c.publicacionid.id=" + p.getId());
        return q.getResultList().isEmpty() ? null : (Clasificacion) q.getSingleResult();
    }

    public Mensaje getMensaje(int id) {
        return mensajeJpaController.findMensaje(id);
    }

    public Clasificacion getClasificacion(int id) {
        return clasificacionJpaController.findClasificacion(id);
    }

    public Users[] getUsuarioAll() throws Exception {
        return getUsuarioAllList().toArray(new Users[0]);
    }

    public LinkedList<Users> getUsuarioAllList() {

        final LinkedList<Users> lista = new LinkedList<>(usersJpaController.findUsersEntities());
        return lista;
    }

    public LinkedList<Mensaje> getMensajeAllList() {

        final LinkedList<Mensaje> lista = new LinkedList<>(mensajeJpaController.findMensajeEntities());
        return lista;
    }

    public List<Mensaje> getMensajeAllListEntreAmbosSort(Users uno, Users dos) {
        List<Mensaje> l = getMensajeAllList(uno, dos);
        l.addAll(getMensajeAllList(dos, uno));
        Collections.sort(l, new ComparadorDeMensajes());
        return l;
    }

    public List<Mensaje> getMensajeAllList(Users u) {
        List<Mensaje> l = getMensajeEnviadoAllList(u);
        l.addAll(getMensajeRecividoAllList(u));
        return l;
    }

    public List<Mensaje> getMensajeEnviadoAllList(Users u) {
//    List<Mensaje> l=
        return mensajeJpaController.getEntityManager().createQuery("SELECT m FROM Mensaje m WHERE m.usersusernameorigen.username = \"" + u.getUsername() + "\"").getResultList();
    }

    public List<Mensaje> getMensajeRecividoAllList(Users u) {
//    List<Mensaje> l=
        return mensajeJpaController.getEntityManager().createQuery("SELECT m FROM Mensaje m WHERE m.usernamedestino = \"" + u.getUsername() + "\"").getResultList();
    }
//     public List<Mensaje> getMensajeRecividoAllList(Users u) {//NoVisto
////    List<Mensaje> l=
//        return mensajeJpaController.getEntityManager().createQuery("SELECT m FROM Mensaje m WHERE m.usersusernamedestino.username = \"" + u.getUsername() + "\"").getResultList();
//    }

    public List<Mensaje> getMensajeAllList(Users origen, Users destinatario) {
        return mensajeJpaController.getEntityManager().createQuery("SELECT m FROM Mensaje m WHERE m.usersusernameorigen.username = \"" + origen.getUsername() + "\" AND m.usernamedestino = \"" + destinatario.getUsername() + "\"").getResultList();
    }

    public List<Mensaje> getMensajeNoVistoAllList(String cuentaOrigen, String cuentaDestino) {
        //public List<Mensaje> getMensajeNoVistoAllList(Users origen, Users destinatario) {
//        String cuentaOrigen=origen.getUsername();
//        String cuentaDestino=destinatario.getUsername();
        return mensajeJpaController.getEntityManager().createQuery("SELECT m FROM Mensaje m WHERE  m.usersusernameorigen.username = \"" + cuentaOrigen + "\" AND m.usernamedestino = \"" + cuentaDestino + "\" AND m.visto= \"" + false + "\"").getResultList();
        // return mensajeJpaController.getEntityManager().createQuery("SELECT m FROM Mensaje m WHERE  m.usersusernameorigen.username = \"" + origen.getUsername() + "\" AND m.usernamedestino = \"" + destinatario.getUsername() + "\" AND m.visto= \"" + false + "\"").getResultList();
    }

    public int getCantidadDeMensajeNoVisto(String origen, String destinatario) {
        //public int getCantidadDeMensajeNoVisto(Users origen, Users destinatario) {
        return getMensajeNoVistoAllList(origen, destinatario).size();
    }

    public LinkedList<Tema> getTemaAllList() {
        return new LinkedList<>(temaJpaController.findTemaEntities());
    }

    public LinkedList<Tema> getTemaAllList(String tipo) {
        //Query q = temaJpaController.getEntityManager().createQuery("SELECT t FROM Tema t WHERE t.nombre = \"" + nombre + "\"");
//        System.out.println("tipo =" + tipo + " nombre=" + nombre);
        Tipodetema t = getTipoDeTema(tipo);
        if (t == null) {
            System.out.println("fue null por tipo");
            return null;
        }
        LinkedList<Tema> lt = new LinkedList<>();
        for (Iterator<Tema> iterator = t.getTemaCollection().iterator(); iterator.hasNext();) {
            Tema next = iterator.next();
            lt.add(next);
        }
        return lt;
//        int idTipoDeTema=getTipoDeTema(tipo).getId();

//        Query q = temaJpaController.getEntityManager().createQuery("SELECT t FROM Tema t WHERE t.nombre = \"" + nombre + "\" AND t.tipodetemaid.id = " + t.getId() + "");
//        if (q.getResultList().isEmpty()) {
//            System.out.println("fue null por empty");
//        }
//
//        return q.getResultList().isEmpty() ? null : (Tema) q.getSingleResult();
    }

    public LinkedList<Servidores> getServidoresAllList() throws Exception {
        return new LinkedList<>(servidoresJpaController.findServidoresEntities());
    }

    public LinkedList<Publicacion> getPublicacionAllList() {
        return new LinkedList<>(publicacionJpaController.findPublicacionEntities());
    }

    public LinkedList<Tipodetema> getTipoDeTemaAllList() {
        return new LinkedList<>(tipodetemaJpaController.findTipodetemaEntities());
    }

    public LinkedList<Publicacion> getPublicacionAllList_Limite_Sort(int limite) throws Exception {
        List<Publicacion> lp = publicacionJpaController.findPublicacionEntities();
        if (!lp.isEmpty()) {
            Collections.sort(lp, new ComparadorDePublicaciones().reversed());
            return new LinkedList<>(lp.subList(0, limite > lp.size() ? lp.size() : limite));
        }

        return new LinkedList<>(lp);
    }

//    public LinkedList<Publicacion> getPublicacionSeguidosAllList(Users u) {
//        List<Users> l = getUsuariosSeguidosAllList(u);
//        LinkedList<Publicacion> pub = new LinkedList<Publicacion>();
//        for (int i = 0; i < l.size(); i++) {
//            pub.addAll(l.get(i).getPublicacionCollection());
//        }
//        return pub;
//    }
    public LinkedList<Publicacion> getPublicacionAllList(Users u) {
        return new LinkedList<Publicacion>(publicacionJpaController.getEntityManager().createQuery("SELECT p FROM Publicacion p WHERE p.usersusername.username = \"" + u.getUsername() + "\"").getResultList());
//        return ;
//        return q.getResultList().isEmpty() ? null : (Tipodetema) q.getSingleResult();
        //  return new LinkedList<>(u.getPublicacionCollection());
    }

    public LinkedList<Publicacion> getPublicacionAllList_Limite_Sort(Users u, int limite) throws Exception {
        LinkedList<Publicacion> lp = getPublicacionAllList(u);
        if (lp.isEmpty()) {
            return lp;
        }
        Collections.sort(lp, new ComparadorDePublicaciones().reversed());
        return new LinkedList<>(lp.subList(0, limite > lp.size() ? lp.size() : limite));
    }

    public Publicacion getPublicacion(Users u, String titulo) {
        Query q = publicacionJpaController.getEntityManager().createQuery("SELECT p FROM Publicacion p WHERE  p.usersusername.username = \"" + u.getUsername() + "\" AND p.titulo = \"" + titulo + "\"");
        return q.getResultList().isEmpty() ? null : (Publicacion) q.getSingleResult();
    }

    public LinkedList<Publicacion> getPublicacionAllList(String tipoDeTema, String tema) {
        return getPublicacionAllList(getTema(tipoDeTema, tema));
    }

    public LinkedList<Publicacion> getPublicacionAllList(Tema t) {
        List<Temapublicacion> ltp = getTemaPublicacionAll(t);
        LinkedList<Publicacion> lp = new LinkedList<>();
        for (int i = 0; i < ltp.size(); i++) {
            lp.add(ltp.get(i).getNoticiaid());
        }

//        for (Iterator<Temapublicacion> it = t.getTemapublicacionCollection().iterator(); it.hasNext();) {
//            lp.add(it.next().getNoticiaid());
//        }
//        asd
        return lp;
    }

    public Users updateUsuarioBasicos(Users u) throws Exception {
        return updateUsuario(inicializarBasicosUsersAenB(getUsuario(u.getUsername()), u));
    }

    public Servidores updateServidores(Servidores u) throws Exception {
        servidoresJpaController.edit(u);
        return getServidore(u.getId());
    }

    public Users updateUsuario(Users u) throws Exception {
        //usersJpaController.edit(getUsuario(u.getUsername()));
        usersJpaController.edit(u);
        return getUsuario(u.getUsername());
    }

    public Publicacion updatePublicacion(Publicacion p) throws Exception {
        publicacionJpaController.edit(p);
        return getPublicacion(p.getId());
    }

    public Mensaje updateMensaje(Mensaje m) throws Exception {
        mensajeJpaController.edit(m);
        return getMensaje(m.getId());
    }

    public Comentario updateComentario(Comentario c) throws Exception {
        comentarioJpaController.edit(c);
        return getComentario(c.getId());
    }

    public Subcripcion updateSubcripcion(Subcripcion s) throws Exception {
        subcripcionJpaController.edit(s);
        return getSubcripcion(s.getId());
    }

    public Users addUsuario(Users u) throws Exception {
        // System.out.println("Intenta poner imagen");
        //System.out.println("u="+u+"  u.getImagen()="+u.getImagen());
        if (u.getImagen() == null || u.getImagen().length == 0) {

            if (FacesContext.getCurrentInstance() == null) {//new File("src/java/recursos/img")
                // System.out.println("faces context fue null !!!!!!!!!!!!!!!!!!!!!!!!!!");
                //if (true) {//new File("src/java/recursos/img")
                // File f=new File("src/java/recursos/img/u9.png").getAbsoluteFile();
                // File f=new File("src/java/recursos/img/u9.png");
                // System.out.println("existe="+f.exists()+" f="+f);
//                asd
                // u.setImagen(Archivo.getBytesDeImg(f)); 
//                 File fimg = new File("src/java/recursos/img/u9.png").getAbsoluteFile();
//                System.out.println("fimg=" + fimg + "  " + fimg.exists());
//                img = Archivo.getBytesDeImg(fimg);
                //  System.out.println("img=" + img + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                u.setImagen(img);
            } else {

                //System.out.println("FacesUtil.getRealPath()=" + FacesUtil.getRealPath());
                File fimg = new File(FacesUtil.getRealPath() + "resources\\img\\u9.png");
                //  System.out.println("file imagen=" + fimg + "  " + fimg.exists());
                byte im[] = Archivo.getBytesDeImg(fimg);
                //  System.out.println("im=" + im);
                u.setImagen(im);
                //  u.setImagen(Archivo.getBytesDeImg(new File("src\\recursos\\img\\u9.png")));
//                asd
            }
            u.setFormatodeimagen(".png");
        }
        usersJpaController.create(u);
        return getUsuario(u.getUsername());

    }

    public Users addUsuario(Users u, boolean esAdministrador) throws Exception {
        Users agregado = addUsuario(u);
        setAuthorities(agregado, esAdministrador);
        return getUsuario(u.getUsername());
    }

    public static int getIDTemaCorrespondiente() throws Exception {

        return getIDCorrespondiente(TABLA_TEMA);
    }

    public static int getIDTemaPublicacionCorrespondiente() throws Exception {
        return getIDCorrespondiente(TABLA_TEMA_PUBLICACION);
        //return getConexionManual().idCorresPondiente("\"Column\"",TABLA_TEMA_PUBLICACION);
    }

    public static int getIDPublicacionCorrespondiente() throws Exception {
        return getIDCorrespondiente(TABLA_PUBLICACION);
    }

    public static int getIDTipoDeTemaCorrespondiente() throws Exception {
        return getIDCorrespondiente(TABLA_TIPO_DE_TEMA);
    }

    public static int getIDClasificacionCorrespondiente() throws Exception {
        return getIDCorrespondiente(TABLA_CLASIFICACION);
    }

    public static int getIDComentarioCorrespondiente() throws Exception {
        return getIDCorrespondiente(TABLA_COMENTARIO);
    }

    public static int getIDServidorCorrespondiente() throws Exception {
        return getIDCorrespondiente(TABLA_SERVIDORES);
    }

    public static int getIDMensajeCorrespondiente() throws Exception {
        return getIDCorrespondiente(TABLA_MENSAJE);
    }

    public static int getIDSubcripcionCorrespondiente() throws Exception {
        return getIDCorrespondiente(TABLA_SUBSCRIPCION);
    }

    public static BDConexionController getConexionManual() {
        return BDConexionController.getPOSTGRESConexionLocal5432("postgres", "rene", "Noticias2");
    }

    public static int getIDCorrespondiente(String nombreTabla) throws Exception {
        return getConsultaIDcorrespondiente(getEntityManagerCorrespondiente(nombreTabla), nombreTabla);
//        return getConexionManual().idCorresPondienteClose(nombreTabla);
    }
//"SELECT MAX(" + nombreTabla + ".\""+nombreID+"\") FROM " + nombreTabla

    public static EntityManager getEntityManagerCorrespondiente(String nombreTabla) {
        switch (nombreTabla) {
            case TABLA_CLASIFICACION:
                return clasificacionJpaController.getEntityManager();
            case TABLA_COMENTARIO:
                return comentarioJpaController.getEntityManager();
            case TABLA_MENSAJE:
                return mensajeJpaController.getEntityManager();
            case TABLA_PUBLICACION:
                return publicacionJpaController.getEntityManager();
            case TABLA_SUBSCRIPCION:
                return subcripcionJpaController.getEntityManager();
            case TABLA_TEMA:
                return temaJpaController.getEntityManager();
            case TABLA_TEMA_PUBLICACION:
                return temapublicacionJpaController.getEntityManager();
            case TABLA_TIPO_DE_TEMA:
                return tipodetemaJpaController.getEntityManager();
        }
        return null;
    }

    public static int getConsultaIDcorrespondiente(EntityManager em, String nombreTabla) {
        String nombreTablaMayuscula = nombreTabla.substring(0, 1).toUpperCase() + nombreTabla.substring(1);
//        System.out.println("nombreTablaMayuscula="+nombreTablaMayuscula);
        String consulta = "SELECT MAX(p.id) FROM " + nombreTablaMayuscula + " p ";
//Query q = publicacionJpaController.getEntityManager().createQuery("SELECT p FROM Publicacion p WHERE  p.usersusername.username = \"" + u.getUsername() + "\" AND p.titulo = \"" + titulo + "\"");
//Query q = clasificacionJpaController.getEntityManager().createQuery("SELECT AVG(c.clasificacion) FROM Clasificacion c WHERE  c.publicacionid.id=" + pid);
        Query q = em.createQuery(consulta);
        boolean paso = q != null && q.getResultList() != null && !q.getResultList().isEmpty() && q.getResultList().get(0) != null;

        int id = 0;
        if (paso) {
            id = (int) ((Integer) q.getSingleResult()).intValue();
//            cantidad = clasificacionJpaController.getEntityManager().createQuery("SELECT c FROM Clasificacion c WHERE  c.publicacionid.id=" + pid).getResultList().size();
        }
        return ++id;
    }

//    @Override
//    public boolean estaConectado(String cuenta) throws Exception {
//        final boolean conectado[] = new boolean[1];
//        //recorrerClientesYremoveDesconectadosBol((v, i) -> !(conectado[0] = v.getCuentaActual().equals(cuenta)));
//        recorrerClientesYremoveDesconectados((a,e)->{});
//
//        return conectado[0];
//    }
    public boolean estaConectado(String cuenta) throws Exception {
        final boolean conectado[] = new boolean[1];
//        conectado[0]=false;
        recorrerClientesYremoveDesconectadosBol((v, i) -> !(conectado[0] = v.getCuentaActual().equals(cuenta)));
//        recorrerClientesYremoveDesconectadosBol((v,i)->{
//        if(v.getCuentaActual().equals(cuenta)){
//        conectado[0]=true;
//        }
//            return !conectado[0];
//        });
        return conectado[0];
    }

//++++++++++Creacion de objetos++++++++++++++++
//    public DialogoChat getDialogoChat(Users propetario, Users destinatario) throws Exception {
//        return new DialogoChat(this, getUsuario(propetario.getUsername()), destinatario);
//    }
//
//    public DialogoChat getDialogoChat(Users propetario, String destinatario) throws Exception {
//        return new DialogoChat(this, propetario, getUsuario(destinatario));
//    }
//    public DatosDeBeanPrincipal getDatosDeBeanPrincipal(String cuenta) {
//        return new DatosDeBeanPrincipal(this, getUsuario(cuenta));
//    }
//    public EstadisticasDeUsuario getEstadisticasDeUsuario(String cuenta) throws Exception {
//        return new EstadisticasDeUsuario(this, getUsuario(cuenta));
//    }
//    public DatosDeBeanTodasLasPublicaciones getDatosDeBeanTodasLasPublicaciones(SeleccionDeSubmenu sel, String actual) {
//        return new DatosDeBeanTodasLasPublicaciones(sel, this, getUsuario(actual));
//    }
//    public LinkedList<DatosDeComentario> addComentarioYgetLista(String cuenta, int idPublicacion, String contenido) throws Exception {
//        Publicacion p = getPublicacion(idPublicacion);
//        Users actual = getUsuario(cuenta);
//        addComentario(actual, p, contenido);
//        return DatosDePublicacion.obtenerListaDeComentarios(this, p);
//    }
//
//    public ClasificacionTemporal apretoPuntuacionPersonal(ClasificacionTemporal c) throws Exception {
//        c.apretoClasificacionPersonal(this);
//        return c;
//    }
//
//    public ClasificacionTemporal apretoLike(ClasificacionTemporal c) throws Exception {
//        c.apretoLike(this);
//        return c;
//    }
//
//    public ClasificacionTemporal apretoDislike(ClasificacionTemporal c) throws Exception {
//        c.apretoDislike(this);
//        return c;
//    }
    //++++++++++++++++++++++++++++++++++++++
    public void publicoMensaje(Mensaje m) throws Exception {
        ExecutorService ex = Executors.newCachedThreadPool();
        //  for (int i = 0; i < size; i++) {
        ex.execute(() -> {
            System.out.println("se envia la notificacion");
            try {
                recorrerClientesYremoveDesconectados((v, i) -> {
                    String cuentaActual = v.getCuentaActual();
                    //if (cuentaActual.equals(m.getUsersusernamedestino().getUsername())) {
                    if (cuentaActual.equals(m.getUsernamedestino())) {
//                        System.out.println("lo va a recivir la cuenta "+cuentaActual);
//                        System.out.println("v.envioMensaje(m.getUsersusernamedestino().getUsername()) ="+m.getUsersusernamedestino().getUsername());
                        v.envioMensaje(m.getUsersusernameorigen().getUsername());
                    }
                });
            } catch (Exception ex1) {
                ex1.printStackTrace();
            }
        });

    }

    public void vioLosMensajes(String propietario, String destino) throws Exception {
        ExecutorService ex = Executors.newCachedThreadPool();
        //  for (int i = 0; i < size; i++) {
        ex.execute(() -> {
            try {
                List<Mensaje> lm = getMensajeNoVistoAllList(propietario, destino);
                //List<Mensaje> lm = getMensajeNoVistoAllList(getUsuario(propietario), getUsuario(destino));

                if (!lm.isEmpty()) {
                    for (int i = 0; i < lm.size(); i++) {

                        lm.get(i).setVisto(true);
                        updateMensaje(lm.get(i));

                    }
                    recorrerClientesYremoveDesconectados((v, i) -> {
                        if (v.getCuentaActual().equals(propietario)) {
                            v.vioLosMensajes(destino);
                        }
                    });
                }

            } catch (Exception ex1) {
                ex1.printStackTrace();
            }
        });

    }

    //--------------------------------------
    public int men = 0;

    public void enviarMensajes() throws Exception {
        ExecutorService ex = Executors.newCachedThreadPool();
        //  for (int i = 0; i < size; i++) {
        ex.execute(() -> {
            try {
                System.out.println("intenta enviar mensajes");
                recorrerClientesYremoveDesconectados((v, i) -> System.out.println(v.enviarMensaje("mensaje enviado " + men++)));

            } catch (Exception ex1) {
                ex1.printStackTrace();
            }

        });
    }

    public void seEliminaronCuentas(String host, int port, String nombreObjetoRemoto, final String... cuentasEliminadas) throws Exception {
        ExecutorService ex = Executors.newCachedThreadPool();
        //  for (int i = 0; i < size; i++) {
        ex.execute(() -> {
            try {
                System.out.println("intena enviar notificacion de cuenta eliminada");
                recorrerClientesYremoveDesconectados((v, k) -> {
                    if (!sonIguales(host, port, nombreObjetoRemoto, v)) {
                        String cuenta = v.getCuentaActual();
                        boolean estaFueEliminada = false;
                        for (int i = 0; i < cuentasEliminadas.length; i++) {
                            if (cuenta.equals(cuentasEliminadas[i])) {
                                System.out.println("envia notificacion de esta cuenta eliminada a " + cuenta);
                                v.estaCuentaFueEliminada();
                                estaFueEliminada = true;
                                break;
                            }
                        }
                        if (!estaFueEliminada) {
                            System.out.println("envia notificacion de que cuentas eliminaron a " + cuenta);
                            v.seEliminaronCuentas(cuentasEliminadas);
                        }
                    }

                });
            } catch (Exception ex1) {
                ex1.printStackTrace();
            }

        });

    }

    public void seAgregoUnaCuenta(String host, int port, String nombreObjetoRemoto, String cuenta) {
        ExecutorService ex = Executors.newCachedThreadPool();
        //  for (int i = 0; i < size; i++) {
        ex.execute(() -> {
            try {
                recorrerClientesYremoveDesconectados((v, k) -> {
                    if (!sonIguales(host, port, nombreObjetoRemoto, v)) {
//                     String cuentaActual = v.getCuentaActual(); 
                        v.seAgregoUnaCuenta(cuenta);
                    }
                });
            } catch (Exception ex1) {
                ex1.printStackTrace();
            }

        });
    }

    private boolean sonIguales(ComunicacionCliente2 a, ComunicacionCliente2 b) throws Exception {
        return a.getHost().equals(b.getHost()) && a.getNombreObjetoRemoto().equals(b.getNombreObjetoRemoto()) && a.getPort() == b.getPort();
    }

    private boolean sonIguales(String host, int port, String nombreObjetoRemoto, ComunicacionCliente2 b) throws Exception {
        return host.equals(b.getHost()) && nombreObjetoRemoto.equals(b.getNombreObjetoRemoto()) && port == b.getPort();
    }

    //++++++++++++++++++++++++++++++++++++++++++++=
    @Override
    public String llamarServidor(String a) throws Exception {
        System.out.println("Se recibio en el servidor");
        System.out.println("se va a poner notificacion");
        recorrerClientesYremoveDesconectados((c, i) -> c.envioMensaje("tres"));
        return "res del servidor dos " + a;
    }

    @Override
    public boolean estaDisponible() throws Exception {
        return true;
    }

    @Override
    public String[] getDireccionesIps() throws Exception {
//         System.out.println("a");

        List<Servidores> ls = control.servidoresJpaController.findServidoresEntities();
//        System.out.println("b");
        LinkedList<String> ips = new LinkedList<>();
        for (int i = 0; i < ls.size(); i++) {
            ips.add(ls.get(i).getIp());
        }
//        System.out.println("c");
        return ips.toArray(new String[0]);
//String res[]=BDConect.getPOSTGRESConexionLocal5432("postgres", "rene", "Noticias2").csl().select_Columna_Str("servidores","ip");
//return res;
    }

    public List<Mensaje> getMensajeRecividoAllList(String cuentaDestino) throws Exception {
//    List<Mensaje> l=
        return mensajeJpaController.getEntityManager().createQuery("SELECT m FROM Mensaje m WHERE m.usernamedestino = \"" + cuentaDestino + "\"").getResultList();
    }

    public void eliminarMensajesDestino(String cuentaDestino) throws Exception {
        List<Mensaje> lm = getMensajeRecividoAllList(cuentaDestino);
        for (int i = 0; i < lm.size(); i++) {
            eliminarMensaje(lm.get(i--));
        }

    }

    public List<Subcripcion> getSubcripcionDeSubscriptorAllList(String cuentaSubscriptor) throws Exception {
        return subcripcionJpaController.getEntityManager().createQuery("SELECT s FROM Subcripcion s WHERE  s.usersusernamesubscriptor = \"" + cuentaSubscriptor + "\"").getResultList();
    }
    public void eliminarSubcripcionesDeSubcriptor(String cuentaSubscriptor) throws Exception {
       // if (existeUsuario(cuentaSubscriptor)) {
            List<Subcripcion> lm = getSubcripcionDeSubscriptorAllList(cuentaSubscriptor);
            for (int i = 0; i < lm.size(); i++) {
               // eliminarSubcripcion(lm.get(i--));
                  eliminarSubcripcion(lm.get(i));
            }
      //  }

    }

    public List<Comentario> getComentarioAllList(String cuenta) throws Exception {

        return comentarioJpaController.getEntityManager().createQuery("SELECT c FROM Comentario c WHERE  c.usenameid = \"" + cuenta + "\"").getResultList();
    }

    public void eliminarComentariosAll(String cuenta) throws Exception {
        List<Comentario> lc = getComentarioAllList(cuenta);
        for (Iterator<Comentario> iterator = lc.iterator(); iterator.hasNext();) {
            Comentario next = iterator.next();
            eliminarComentario(next);
        }
    }

    public List<Clasificacion> getClasificacionAllList(String cuenta) throws Exception {
        return clasificacionJpaController.getEntityManager().createQuery("SELECT c FROM Clasificacion c WHERE c.usernameid = \"" + cuenta + "\"").getResultList();
    }

    public void eliminarClasificacionsAll(String cuenta) throws Exception {
        List<Clasificacion> lc = getClasificacionAllList(cuenta);
        for (Iterator<Clasificacion> iterator = lc.iterator(); iterator.hasNext();) {
            Clasificacion next = iterator.next();
            eliminarClasificacion(next);
        }
    }

    @Override
    public void seEliminoPublicacion(String cuenta, int idPublicacion) throws Exception {
        System.out.println("No esta implementado la notificacion de eliminacion de publicaciones");
    }

//    @Override
//    public List<Mensaje> getMensajeNoVistoAllList(Users origen, Users destinatario) throws Exception {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public int getCantidadDeMensajeNoVisto(Users origen, Users destinatario) throws Exception {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
}
