/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pruebarmi;

import RMI.ConexionServidores;
import RMI.ServidorRMI;
import Util.ClasesDeApoyo.ClasificacionTemporal;
import Util.ClasesDeApoyo.DatosDeBeanPrincipal;
import Util.ClasesDeApoyo.DatosDeBeanTodasLasPublicaciones;
import Util.ClasesDeApoyo.DatosDeComentario;
import Util.ClasesDeApoyo.DatosDePublicacion;
import Util.ClasesDeApoyo.DatosDeUsuario;
import Util.ClasesDeApoyo.DialogoChat;
import Util.ClasesDeApoyo.Estadisticas.EstadisticasDeUsuario;
import Util.ClasesDeApoyo.SeleccionDeSubmenu;
import Util.DefaultBD;
import Util.EAc;
import Utiles.ClasesUtiles.BasesDeDatos.BDConect;
import Utiles.MetodosUtiles.Archivo;
import comunicacion.ComunicacionServidor2;
import entity.*;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Rene
 */
public class PruebaRMI {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        //DefaultBD.crearConfiguracionInicialYClearBD("Rene");
        System.out.println(new File( "src\\recursos\\img\\u9.png").exists());
         File fimg = new File("src/java/recursos/img/u9.png").getAbsoluteFile();
                    //  System.out.println("fimg=" + fimg + "  " + fimg.exists());
                byte[]    img = Archivo.getBytesDeImg(fimg);
        ServidorRMI servidor = new ServidorRMI().iniciarServidor(img);
        ConexionServidores clienteWeb1 = new ConexionServidores().conectarAServidor(new EAImple());
        //clienteWeb1.addUsuario(clienteWeb1.getNewUser("dos", "123", "dosNombre", "apelli1", "apelli2", "dos@gmail.com", "12345678", true), true);
        //clienteWeb1.addUsuario(clienteWeb1.getNewUser("uno", "123", "unoNombre", "apelli1", "apelli2", "uno@gmail.com", "12345678", true), true);
        Users u3 = clienteWeb1.getUsuario("tresLeyen");
        Users u1 = clienteWeb1.getUsuario("unoRene");
        System.out.println("u1=" + u1 + " u3=" + u3);
        //DefaultBD.crearTemasDafault(clienteWeb1.getServidorInterno());

        //clienteWeb1.addServidores("192.168.43.82");
        // pruebaPublicacion(clienteWeb1);
        //pruebaDatosDeBeanTodasLasPublicacionesLeer(clienteWeb1);
        //pruebaEliminarUsuario(clienteWeb1);
        // clienteWeb1.addMensaje(clienteWeb1.getUsuario("dos"), clienteWeb1.getUsuario("uno"), "3de dos a uno");
        System.out.println("termino");
        // System.exit(0);

//        BDConect.getPOSTGRESConexionLocal5432("postgres", "rene", "Noticias2").csl().select_Columna_Str("servidores","ip");
    }

    public static void pruebaDatosDeBeanTodasLasPublicaciones(ConexionServidores clienteWeb1) throws Exception {
        Publicacion p = clienteWeb1.getPublicacion("sieteRene", 75);
        Users u = clienteWeb1.getUsuario("unoRene");
        //clienteWeb1.addClasificacion(u, p, true, 3);
        Clasificacion cf = clienteWeb1.getClasificacion(u, p);
        System.out.println("cf=" + cf);

    }

    public static void pruebaDatosDeBeanTodasLasPublicacionesLeer(ConexionServidores clienteWeb1) throws Exception {
        SeleccionDeSubmenu sm = new SeleccionDeSubmenu(EAc.SUBMENU_GENERAL, EAc.MI_VER_ULTIMAS);
        DatosDeBeanTodasLasPublicaciones db = clienteWeb1.getDatosDeBeanTodasLasPublicaciones(sm, "unoRene");
        LinkedList<Publicacion> lp = db.getPublicaciones();
        for (int i = 0; i < lp.size(); i++) {
            Publicacion p = lp.get(i);
            int id = p.getId();
            verPublicacion(clienteWeb1, p);
            System.out.println("id=" + id);
            DatosDePublicacion dp = db.getDatosDePublicaciones().get(DatosDePublicacion.getLlave(p));

            HashMap<String, LinkedList<String>> temas = dp.getTiposDeTemasNombres();
            for (Map.Entry<String, LinkedList<String>> entry : temas.entrySet()) {
                String key = entry.getKey();
                LinkedList<String> value = entry.getValue();
                System.out.println(key + ": " + value);
            }
            ClasificacionTemporal ct = dp.getClasificacionTemporal();
            System.out.println("cant de likes: " + ct.getCantidadDeLikes());
            System.out.println("cant de Dislikes: " + ct.getCantidadDeDislike());
            System.out.println("cant de opiniones: " + ct.getCantidadDeOpiniones());
            System.out.println("cant de calsificacion general: " + ct.getClasificacionGeneral());
            System.out.println("calsificacion personal: " + ct.getClasificacionPersonal());
            System.out.println("isDislike: " + ct.isDislike());
            System.out.println("Like: " + ct.isLike());
            System.out.println("+++");
            System.out.println("cuenta="+ct.getCuenta());
            LinkedList<DatosDeComentario> dt = dp.getDatosDeComentario();
            for (Iterator<DatosDeComentario> iterator = dt.iterator(); iterator.hasNext();) {
                DatosDeComentario n = iterator.next();
                System.out.println("Autor Comentario: " + n.getCuenta());
                System.out.println("Contenido: " + n.getContenido());
                System.out.println("Fecha: " + n.getFecha());

            }
            System.out.println("");
        }

    }

    public static void pruebaEstadisticasDeUsuario(ConexionServidores clienteWeb1) throws Exception {

        EstadisticasDeUsuario est = clienteWeb1.getEstadisticasDeUsuario("unoRene");
        System.out.println("likesEstaSemana: " + est.getCantidadDeLikesDeEstaSemana());
        System.out.println("Cant de Publicaciones: " + est.getCantidadDePublicaciones());
        System.out.println("Cant de Publicaciones esta Semana: " + est.getCantidadDePublicacionesDeEstaSemana());
        System.out.println("Cantidad de Subcriptores: " + est.getCantidadDeSubcritores());
        System.out.println("Porsentage de aumanto de likes: " + est.getPorsentageDeAumentoDeLikes());
        System.out.println("Porsentage de aumanto de Subcriptores: " + est.getPorsentageDeAumentoDeSubcriptores());
        System.out.println("Porsentage de aumanto de Publicaciones: " + est.getPorsentageDeAumentoPublicaciones());

    }

    public static void pruebaDatosBeanPrincipal(ConexionServidores clienteWeb1) throws Exception {
        DatosDeBeanPrincipal dbp = new DatosDeBeanPrincipal(clienteWeb1, clienteWeb1.getUsuario("unoRene"));
        LinkedList<DatosDeUsuario> ld = dbp.getTodosLosUsuarios();
        verDatosDeUsuario(clienteWeb1, ld);

    }

    public static void pruebaDialogoChat(ConexionServidores clienteWeb1) throws Exception {
        clienteWeb1.addMensaje(clienteWeb1.getUsuario("unoRene"), clienteWeb1.getUsuario("dosLeyen"), "hola");
        DialogoChat dch = clienteWeb1.getDialogoChat(clienteWeb1.getUsuario("unoRene"), "dosLeyen");
        List<Mensaje> lm = dch.getMensajes();
        System.out.println("Mensajes entre unoRene y dosLeyen");
        verMensajes(clienteWeb1, lm);
        dch = clienteWeb1.getDialogoChat(clienteWeb1.getUsuario("unoLeyen"), "tresLeyen");
        lm = dch.getMensajes();
        System.out.println("Mensajes entre unoLeyen y tresLeyen");
        verMensajes(clienteWeb1, lm);
    }

    public static void pruebaCrearMiniBD(ConexionServidores clienteWeb1) throws Exception {
        Users uno = clienteWeb1.addUsuario(clienteWeb1.getNewUser("uno", "123", "unoNombre", "apelli1", "apelli2", "uno@gmail.com", "12345678", true), true);
        Users dos = clienteWeb1.addUsuario(clienteWeb1.getNewUser("dos", "123", "dosNombre", "apelli1", "apelli2", "dos@gmail.com", "12345678", true), false);
        Publicacion pUno = clienteWeb1.addPublicacion("uno", "Tecnologia", "Robotica", "esta es una publicacion nueva uno", "De uno");
        Publicacion pDos = clienteWeb1.addPublicacion("dos", "Tecnologia", "Robotica", "esta es una publicacion nueva dos", "De dos");
        clienteWeb1.addClasificacion(uno, pDos, true, 6);
        clienteWeb1.addClasificacion(dos, pUno, false, 3);
        clienteWeb1.addComentario(uno, pDos, "me gusto de uno");
        clienteWeb1.addComentario(dos, pUno, "me gusto de dos");
        clienteWeb1.addSubcripcion(dos, uno);
        clienteWeb1.addSubcripcion(uno, dos);
        clienteWeb1.addMensaje(uno, dos, "de uno a dos");
        clienteWeb1.addMensaje(dos, uno, "de dos a uno");
        System.out.println("sale de la mini creacion");
    }

    public static void pruebaCrearMiniBDcomunicacionDe2(ConexionServidores clienteWeb1) throws Exception {
        Users uno = clienteWeb1.getUsuario("uno");
        Users tres = clienteWeb1.getUsuario("tres");
        Publicacion pUno = clienteWeb1.getPublicacion(uno, "De uno");
        clienteWeb1.addClasificacion(tres, pUno, false, 2);
        clienteWeb1.addComentario(tres, pUno, "me gusto de tres");
        //clienteWeb1.addSubcripcion(tres, uno);
        clienteWeb1.addSubcripcion(uno, tres);
        //clienteWeb1.addMensaje(uno, tres, "de uno a tres");
        clienteWeb1.addMensaje(tres, uno, "de tres a uno");
    }

    public static void pruebaEliminarUsuario(ConexionServidores clienteWeb1) throws Exception {
        clienteWeb1.eliminarUsuarioInterno(clienteWeb1.getUsuario("dos"));

    }

    public static void pruebaEliminarSubcripcion(ConexionServidores clienteWeb1) throws Exception {
        clienteWeb1.eliminarSubcripcion(clienteWeb1.getUsuario("tres"), clienteWeb1.getUsuario("uno"));
        clienteWeb1.eliminarSubcripcion(clienteWeb1.getUsuario("dos"), clienteWeb1.getUsuario("cuatro"));

    }

    public static void pruebaSubcripcion(ConexionServidores clienteWeb1) throws Exception {
        clienteWeb1.addSubcripcion(clienteWeb1.getUsuario("tres"), clienteWeb1.getUsuario("uno"));
        clienteWeb1.addSubcripcion(clienteWeb1.getUsuario("dos"), clienteWeb1.getUsuario("cuatro"));

    }

    public static void pruebaSubcripcionLeer(ConexionServidores clienteWeb1) throws Exception {
        System.out.println("subcritores de tres:");
        List<Subcripcion> ls = clienteWeb1.getSubcripcionDeSubscriptoresAllList(clienteWeb1.getUsuario("tres"));
        verSubcripciones(clienteWeb1, ls);
        System.out.println("subcritores de dos:");
        ls = clienteWeb1.getSubcripcionDeSubscriptoresAllList(clienteWeb1.getUsuario("dos"));
        verSubcripciones(clienteWeb1, ls);
        System.out.println("esta subcrito uno a tres " + clienteWeb1.existeSubcripcion(clienteWeb1.getUsuario("tres"), clienteWeb1.getUsuario("uno")));
        System.out.println("esta subcrito tres a uno " + clienteWeb1.existeSubcripcion(clienteWeb1.getUsuario("uno"), clienteWeb1.getUsuario("tres")));
        System.out.println("esta subcrito dos a cuatro " + clienteWeb1.existeSubcripcion(clienteWeb1.getUsuario("cuatro"), clienteWeb1.getUsuario("dos")));
        System.out.println("esta subcrito cuatro a dos " + clienteWeb1.existeSubcripcion(clienteWeb1.getUsuario("dos"), clienteWeb1.getUsuario("cuatro")));
    }

    public static void pruebaClasificacion(ConexionServidores clienteWeb1) throws Exception {
        Publicacion p = clienteWeb1.getPublicacion(clienteWeb1.getUsuario("tres"), "De tres");
        clienteWeb1.addClasificacion(clienteWeb1.getUsuario("uno"), p, true, 3);
        clienteWeb1.addClasificacion(clienteWeb1.getUsuario("dos"), p, false, 1);
    }

    public static void pruebaClasificacionLeer(ConexionServidores clienteWeb1) throws Exception {
        Publicacion p = clienteWeb1.getPublicacion(clienteWeb1.getUsuario("tres"), "De tres");
        List<Clasificacion> lc = clienteWeb1.getClasificacionAllList(p);
        System.out.println("lc=" + lc.size());
        verClasificaciones(clienteWeb1, lc);
    }

    public static void pruebaEliminarComentario(ConexionServidores clienteWeb1) throws Exception {
        Publicacion p = clienteWeb1.getPublicacion(clienteWeb1.getUsuario("tres"), "De tres");
        List<Comentario> lc = clienteWeb1.getComentarioAllList(p);
        for (int i = 0; i < lc.size(); i++) {
            Comentario c = lc.get(i);
            if (c.getUsenameid().equals("uno")) {
                clienteWeb1.eliminarComentario(c);
                continue;
            }
            if (c.getUsenameid().equals("cuatro")) {
                clienteWeb1.eliminarComentario(p, c.getId());
            }
        }
    }

    public static void pruebaComentario(ConexionServidores clienteWeb1) throws Exception {
        Publicacion p = clienteWeb1.getPublicacion(clienteWeb1.getUsuario("tres"), "De tres");
        //verPublicacion(clienteWeb1, p);
        clienteWeb1.addComentario(clienteWeb1.getUsuario("uno"), p, "comentario dejado por uno");
        //clienteWeb1.addComentario(clienteWeb1.getUsuario("uno"),clienteWeb1.getP , contenido)
    }

    public static void pruebaComentario2(ConexionServidores clienteWeb1) throws Exception {
        clienteWeb1.addUsuario(clienteWeb1.getNewUser("cuatro", "123", "ccuatroNombre", "apelli1", "apelli2", "uno@gmail.com", "12345678", true), true);
        Publicacion p = clienteWeb1.getPublicacion(clienteWeb1.getUsuario("tres"), "De tres");
        //verPublicacion(clienteWeb1, p);
        clienteWeb1.addComentario(clienteWeb1.getUsuario("cuatro"), p, "comentario dejado por cuatro");
        //clienteWeb1.addComentario(clienteWeb1.getUsuario("uno"),clienteWeb1.getP , contenido)
    }

    public static void pruebaComentarioLeer(ConexionServidores clienteWeb1) throws Exception {
        Publicacion p = clienteWeb1.getPublicacion(clienteWeb1.getUsuario("tres"), "De tres");
        List<Comentario> lp = clienteWeb1.getComentarioAllList(p);
        System.out.println("lp sise=" + lp.size());
        verComentarios(clienteWeb1, lp);
    }

    public static void pruebaPublicacion(ConexionServidores clienteWeb1) throws Exception {
        clienteWeb1.addPublicacion("uno", "Tecnologia", "Robotica", "esta es una publicacion nueva", "De uno");
    }

    public static void pruebaEliminarPublicacion(ConexionServidores clienteWeb1) throws Exception {
        Publicacion p = clienteWeb1.getPublicacion(clienteWeb1.getUsuario("uno"), "De uno");
        clienteWeb1.eliminarPublicacionInterna(p);
    }

    public static void pruebaPublicacionLeer(ConexionServidores clienteWeb1) throws Exception {
        //clienteWeb1.addPublicacion("tres", "Tecnologia", "Informatica", "esta es una publicacion creada por tres", "De tres");
        System.out.println("publicaciones todas:");
        LinkedList<Publicacion> lp = clienteWeb1.getPublicacionAllList();
        verPublicaciones(clienteWeb1, lp);
        System.out.println("publicaciones de uno:");
        List<Publicacion> lp2 = clienteWeb1.getPublicacionAllList(clienteWeb1.getUsuario("uno"));
        verPublicaciones(clienteWeb1, lp2);
        System.out.println("publicaciones de tres:");
        lp2 = clienteWeb1.getPublicacionAllList(clienteWeb1.getUsuario("tres"));
        verPublicaciones(clienteWeb1, lp2);
        System.out.println("publicaciones de Robotica:");
        lp2 = clienteWeb1.getPublicacionAllList("Tecnologia", "Robotica");
        verPublicaciones(clienteWeb1, lp2);
        System.out.println("publicaciones de Informatica:");
        lp2 = clienteWeb1.getPublicacionAllList("Tecnologia", "Informatica");
        verPublicaciones(clienteWeb1, lp2);
    }

    public static void pruebaMensajes(ConexionServidores clienteWeb1) throws Exception {
        clienteWeb1.addMensaje(clienteWeb1.getUsuario("uno"), clienteWeb1.getUsuario("tres"), "hola");
    }

    public static void verDatosDeUsuario(ConexionServidores clienteWeb1, LinkedList<DatosDeUsuario> lm) throws Exception {
        for (Iterator<DatosDeUsuario> iterator = lm.iterator(); iterator.hasNext();) {
            DatosDeUsuario next = iterator.next();
            System.out.println("Cuenta: " + next.getCuenta());
            System.out.println("Mensajes No vistos: " + next.getCantidadDeMensajesNoVistos());
            System.out.println("Ultimo Mensaje date: " + next.getDateUltimoMensaje());
            System.out.println("Lo sigue:" + clienteWeb1.existeSubcripcion(clienteWeb1.getUsuario(next.getCuenta()), clienteWeb1.getUsuario("unoRene")));
        }
    }

    public static void verMensajes(ConexionServidores clienteWeb1, List<Mensaje> lm) {

        for (Iterator<Mensaje> iterator = lm.iterator(); iterator.hasNext();) {
            Mensaje next = iterator.next();
            System.out.println("Autor: " + next.getUsersusernameorigen().getUsername());
            System.out.println("contenido: " + next.getContenido());

        }
    }

    public static void verSubcripciones(ConexionServidores clienteWeb1, List<Subcripcion> ls) throws Exception {

        for (Iterator<Subcripcion> iterator = ls.iterator(); iterator.hasNext();) {
            Subcripcion next = iterator.next();
            System.out.println(next.getUsersusernamesubscriptor() + " esta subcrito a " + next.getUsersusernamepropietario().getUsername());

        }
    }

    public static void verClasificaciones(ConexionServidores clienteWeb1, List<Clasificacion> lp) throws Exception {
        for (int i = 0; i < lp.size(); i++) {
            Clasificacion p = lp.get(i);
            verClasificacion(clienteWeb1, p);
        }
    }

    public static void verClasificacion(ConexionServidores clienteWeb1, Clasificacion c) throws Exception {
        System.out.println("Dada por: " + c.getUsernameid());
        System.out.println("Like: " + c.getLegusta());
        System.out.println("Puntacion: " + c.getClasificacion());
    }

    public static void verComentarios(ConexionServidores clienteWeb1, List<Comentario> lp) throws Exception {
        for (int i = 0; i < lp.size(); i++) {
            Comentario p = lp.get(i);
            verComentario(clienteWeb1, p);
        }
    }

    public static void verComentario(ConexionServidores clienteWeb1, Comentario c) throws Exception {
        System.out.println("Autor: " + c.getUsenameid());
        System.out.println("Contenido: " + c.getContenido());
    }

    public static void verPublicaciones(ConexionServidores clienteWeb1, List<Publicacion> lp) throws Exception {
        for (int i = 0; i < lp.size(); i++) {
            Publicacion p = lp.get(i);
            verPublicacion(clienteWeb1, p);
        }
    }

    public static void verPublicacion(ConexionServidores clienteWeb1, Publicacion p) throws Exception {
        System.out.println("Autor: " + p.getUsersusername().getUsername());
        System.out.println("Titulo: " + p.getTitulo());
        // System.out.println(getTemasString(clienteWeb1, p));
        System.out.println("Contenido: " + new String(p.getContenido()));
        //System.out.println("");
    }

    public static String getTemasString(ConexionServidores clienteWeb1, Publicacion p) throws Exception {
        ComunicacionServidor2 servidor = clienteWeb1.getServidorInterno();
        String temas = "";
        final HashMap<String, LinkedList<String>> tp = new HashMap<>();
        //Collection<Temapublicacion> ctp = p.getTemapublicacionCollection(); //error 11111111111111111
        Collection<Temapublicacion> ctp = clienteWeb1.getTemaPublicacionAll(p); //error 11111111111111111
        for (Iterator<Temapublicacion> iterator = ctp.iterator(); iterator.hasNext();) {
            Temapublicacion next = iterator.next();

            Tema t = next.getTemaid();
            String tipo = t.getTipodetemaid().getNombre();
            if (!tp.containsKey(tipo)) {
                tp.put(tipo, new LinkedList<>());
            }
            tp.get(tipo).add(t.getNombre());

        }

        for (Map.Entry<String, LinkedList<String>> entry : tp.entrySet()) {
            String key = entry.getKey();
            LinkedList<String> value = entry.getValue();
            temas += key + ": ";
            for (Iterator<String> iterator = value.iterator(); iterator.hasNext();) {
                String next = iterator.next();
                temas += " " + next + " ";
            }
            temas += "";
        }
        return temas;

    }
}
