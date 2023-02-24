/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util.ClasesDeApoyo;

//import EstadoActual.EA;
//import Util.BDPersistence;
//import beans.Personalizados.beanTodasLasPublicaciones;
//import beans.Superclases.FacesBDbean;
//import beans.Superclases.aplicacionBean;
import RMI.ConexionServidores;
import comunicacion.ComunicacionServidor2;
import entity.Comentario;
import entity.Publicacion;
import entity.Tema;
import entity.Temapublicacion;
import entity.Users;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Callback;
//import javax.annotation.ManagedBean;
//import javax.enterprise.context.SessionScoped;

import org.primefaces.model.StreamedContent;

/**
 *
 * @author Rene
 */
public class DatosDePublicacion implements Serializable {

    private int idPublicacion;
    private HashMap<String, LinkedList<String>> tiposDeTemasNombres;
    private ClasificacionTemporal clasificacionTemporal;
    //private StreamedContent imagenDeUsuario;
//    private String direccionImagenDeUsuario;
    private LinkedList<DatosDeComentario> datosDeComentario;
    private String comentarioPropio;
private String cuentaAutorPublicacion;
    public DatosDePublicacion(String cuentaAutorPublicacion,int idPublicacion, HashMap<String, LinkedList<String>> tiposDeTemasNombres, ClasificacionTemporal clasificacionTemporal, LinkedList<DatosDeComentario> datosDeComentario) {
        this.idPublicacion = idPublicacion;
        this.tiposDeTemasNombres = tiposDeTemasNombres;
        this.clasificacionTemporal = clasificacionTemporal;
//        this.direccionImagenDeUsuario = direccionImagenDeUsuario;
        this.datosDeComentario = datosDeComentario;
        this.cuentaAutorPublicacion=cuentaAutorPublicacion;
    }

    public HashMap<String, LinkedList<String>> getTiposDeTemasNombres() {
        return tiposDeTemasNombres;
    }

    public void setTiposDeTemasNombres(HashMap<String, LinkedList<String>> tiposDeTemasNombres) {
        this.tiposDeTemasNombres = tiposDeTemasNombres;
    }

    public ClasificacionTemporal getClasificacionTemporal() {
        return clasificacionTemporal;
    }

    public void setClasificacionTemporal(ClasificacionTemporal clasificacionTemporal) {
        this.clasificacionTemporal = clasificacionTemporal;
    }

    public static HashMap<String, DatosDePublicacion> getDatosDePublicaciones(final ConexionServidores servidor, Users actual, List<Publicacion> publicaciones) {
        final HashMap<String, DatosDePublicacion> datosDePublicaciones = new HashMap<>();

        publicaciones.forEach(p -> {
            try {
                String cuenta = p.getUsersusername().getUsername();
                final HashMap<String, LinkedList<String>> tp = new HashMap<>();
                List<Temapublicacion> ltp = servidor.getTemaPublicacionAll(p);
                ltp.forEach(pb -> {
//                p.getTemapublicacionCollection().forEach(pb -> {
                    Tema t = pb.getTemaid();
                    String tipo = t.getTipodetemaid().getNombre();
                    if (!tp.containsKey(tipo)) {
                        tp.put(tipo, new LinkedList<>());
                    }
                    tp.get(tipo).add(t.getNombre());
                });
                try {

                    datosDePublicaciones.put(getLlave(p), new DatosDePublicacion(p.getUsersusername().getUsername(),p.getId(), tp, new ClasificacionTemporal(servidor, actual.getUsername(), p, servidor.getClasificacion(actual, p)), obtenerListaDeComentarios(servidor, p)));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        return datosDePublicaciones;
    }

    public static LinkedList<DatosDeComentario> obtenerListaDeComentarios(ConexionServidores servidor, Publicacion p) {
        try {
            if(p==null){
            return new LinkedList<>();
            }
            //,HashMap<String, String> imagenesDeUsuarios,Callback<Object,String> crearImagen
            final LinkedList<DatosDeComentario> ld = new LinkedList<>();
//        System.out.println("p="+p.getId()+" "+p.getTitulo()+" "+p.getUsersusername().getUsername());
//        System.out.println("coll sise="+ p.getComentarioCollection().size());
            List<Comentario> lc = servidor.getComentarioAllList(p);
            if (lc == null) {
                lc = new LinkedList<>();
            }
            for (Iterator<Comentario> iterator1 = lc.iterator(); iterator1.hasNext();) {
                Comentario c = iterator1.next();
//                    System.out.println("se llama");
                //String cuentaComentario = c.getUsersusername().getUsername();
                String cuentaComentario = c.getUsenameid();
                ld.add(new DatosDeComentario(cuentaComentario, c.getContenido(), c.getFecha(), c.getId(), p.getId(),p.getUsersusername().getUsername()));
//                    System.out.println("completo");
            }
// );
            Collections.sort(ld, Collections.reverseOrder());
            return ld;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void actualizarComentariosDePublicacion(ConexionServidores servidor) throws Exception {
        datosDeComentario = obtenerListaDeComentarios(servidor, servidor.getPublicacion(cuentaAutorPublicacion,idPublicacion));
    }

    public LinkedList<DatosDeComentario> getDatosDeComentario() {
        return datosDeComentario;
    }

    public void setDatosDeComentario(LinkedList<DatosDeComentario> datosDeComentario) {
        this.datosDeComentario = datosDeComentario;
    }

    public int getIdPublicacion() {
        return idPublicacion;
    }

    public void setIdPublicacion(int idPublicacion) {
        this.idPublicacion = idPublicacion;
    }

    public String getComentarioPropio() {
        return comentarioPropio;
    }

    public void setComentarioPropio(String comentarioPropio) {
        this.comentarioPropio = comentarioPropio;
    }

    
    public static String getLlave(Publicacion p){
    return p.getId()+p.getUsersusername().getUsername();
    }
}
