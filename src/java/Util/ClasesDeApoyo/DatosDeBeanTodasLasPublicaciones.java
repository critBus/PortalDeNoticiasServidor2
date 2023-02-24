/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util.ClasesDeApoyo;

import RMI.ConexionServidores;
import comunicacion.ComunicacionServidor2;
import Util.EAc;
import entity.Publicacion;
import entity.Users;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rene
 */
public class DatosDeBeanTodasLasPublicaciones implements Serializable {

    private LinkedList<Publicacion> publicaciones;
    private HashMap<String, DatosDePublicacion> datosDePublicaciones;

    public DatosDeBeanTodasLasPublicaciones(SeleccionDeSubmenu sel, ConexionServidores servidor, Users actual) {
        inicializarPublicaciones(sel, servidor, actual);
        Collections.sort(publicaciones, new ComparadorDePublicaciones().reversed());
        datosDePublicaciones = DatosDePublicacion.getDatosDePublicaciones(servidor, actual, publicaciones);
    }

    private void inicializarPublicaciones(SeleccionDeSubmenu sel, ConexionServidores servidor, Users actual) {
        try {
//            SeleccionDeSubmenu sel = getService().getSeleccionDeSubmenuPrincipal();
            if (sel.getSubmenuSeleccionado().equals(EAc.SUBMENU_GENERAL)) {
//                System.out.println("sel.getItemSeleccionado()="+sel.getItemSeleccionado());
                int limite=10;
                switch (sel.getItemSeleccionado()) {
                    case EAc.MI_VER_SEGUIDOS: {

                        publicaciones = servidor.getPublicacionSeguidosAllList_Limite_Sort(actual,limite);
                        if (publicaciones == null) {
                            publicaciones = new LinkedList<>();
                        }
                    }
                    return;
                    case EAc.MI_VER_ULTIMAS:
                        publicaciones = servidor.getPublicacionAllList_Limite_Sort(limite);
                        if (publicaciones == null) {
                            publicaciones = new LinkedList<>();
                        }
                        return;
                    case EAc.MI_MIS_PUBLICACIONES:
                        publicaciones = new LinkedList<>();
                        List<Publicacion> lp = servidor.getPublicacionAllList(actual);
                        if (lp != null) {
                            publicaciones.addAll(lp);
                        }
                        return;
                    default:
                        publicaciones = new LinkedList<>();
                        List<Publicacion> lp2 = servidor.getPublicacionAllList(servidor.getUsuario(sel.getItemSeleccionado()));
                        if (lp2 != null) {
                            publicaciones.addAll(lp2);
                        }
                        return;
                }
            }
//            System.out.println("ejecuta por otro menu");
//            System.out.println("sel.getSubmenuSeleccionado()="+sel.getSubmenuSeleccionado());
//            System.out.println("sel.getItemSeleccionado()="+sel.getItemSeleccionado());
            publicaciones = servidor.getPublicacionAllList(sel.getSubmenuSeleccionado(), sel.getItemSeleccionado());
        } catch (Exception ex) {
            System.out.println("erro al iniciar publ");
            ex.printStackTrace();
        }
    }

    public DatosDeBeanTodasLasPublicaciones(LinkedList<Publicacion> publicaciones, HashMap<String, DatosDePublicacion> datosDePublicaciones) {
        this.publicaciones = publicaciones;
        this.datosDePublicaciones = datosDePublicaciones;
    }

    public LinkedList<Publicacion> getPublicaciones() {
        return publicaciones;
    }

    public void setPublicaciones(LinkedList<Publicacion> publicaciones) {
        this.publicaciones = publicaciones;
    }

    public HashMap<String, DatosDePublicacion> getDatosDePublicaciones() {
        return datosDePublicaciones;
    }

    public void setDatosDePublicaciones(HashMap<String, DatosDePublicacion> datosDePublicaciones) {
        this.datosDePublicaciones = datosDePublicaciones;
    }

   

//    public void putDatosDePublicacion(int id, DatosDePublicacion da) {
//        datosDePublicaciones.put(id, da);
//    }

//    public void putDatosDeComentario(String cuentaAutorPublicacion,int id, LinkedList<DatosDeComentario> datosDeComentario) {
//        datosDePublicaciones.get(id).setDatosDeComentario(datosDeComentario);
//    }
      public void putDatosDeComentario(Publicacion p, LinkedList<DatosDeComentario> datosDeComentario) {
        datosDePublicaciones.get(DatosDePublicacion.getLlave(p)).setDatosDeComentario(datosDeComentario);
    }
}
