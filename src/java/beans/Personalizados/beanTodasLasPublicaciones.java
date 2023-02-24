/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.Personalizados;

import Util.ClasesDeApoyo.ClasificacionTemporal;
import Util.ClasesDeApoyo.ComparadorDePublicaciones;
import beans.Superclases.aplicacionBean;
import Temporal.Publicacion2;
import Util.ClasesDeApoyo.SeleccionDeSubmenu;
import EstadoActual.EA;
import Util.ClasesDeApoyo.DatosDeBeanTodasLasPublicaciones;
import Util.EAc;
//import Util.BDPersistence;
import Utiles.JSF.FacesUtil;
import Utiles.MetodosUtiles.Archivo;
import Utiles.MetodosUtiles.Tempus;
import Util.ClasesDeApoyo.DatosDeComentario;
import Util.ClasesDeApoyo.DatosDePublicacion;
//import beans.ClasesDeApoyo.DatosDeUsuario;
import beans.Superclases.FacesBDbean;
import static beans.Superclases.FacesBDbean.getStyleOcultar;
import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.ToggleEvent;
import entity.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Rene
 */
@ManagedBean(name = "beanTodasLasPublicaciones3")
@ViewScoped
public class beanTodasLasPublicaciones extends aplicacionBean {
 private DatosDeBeanTodasLasPublicaciones datosDeBeanTodasLasPublicaciones;

    private boolean soloSeguidos;

    /**
     * Creates a new instance of beanTodasLasPublicaciones
     */
    public beanTodasLasPublicaciones() {

    }

    @Override
    public void ini() {
        super.ini(); //To change body of generated methods, choose Tools | Templates.
        soloSeguidos = false;
        resetear();
    }

    public void resetear() {

        filtrarPorSeguidos();

    }

    public void filtrarPorSeguidos() {
//        System.out.println("intenta filtrar soloSeguidos=" + soloSeguidos);
        try {
            if (soloSeguidos) {

//                            System.out.println("Comienza a filtrar");
//            System.out.println("res sise="+res.size()+" "+res);
           
                List<Publicacion> publicaciones = datosDeBeanTodasLasPublicaciones.getPublicaciones();
//                 System.out.println("pub sise="+publicaciones.size());
                HashMap<String, DatosDePublicacion> datosDePublicaciones = datosDeBeanTodasLasPublicaciones.getDatosDePublicaciones();
                For1:
                for (int i = 0; i < publicaciones.size(); i++) {
                    if(getService().getDatosDeBeanPrincipalCliente().esSeguido(publicaciones.get(i).getUsersusername().getUsername())){
                    continue;
                    }
                 if (datosDePublicaciones != null) {
                         datosDePublicaciones.remove(DatosDePublicacion.getLlave(publicaciones.get(i)));
                    }
                    publicaciones.remove(i--);
                }

            } else {
            datosDeBeanTodasLasPublicaciones=    FacesBDbean.servidores.getDatosDeBeanTodasLasPublicaciones(getService().getSeleccionDeSubmenuPrincipal(), getService().getPrefilActual().getUsuarioActual().getUsername());
            }
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public boolean isSoloSeguidos() {
        return soloSeguidos;
    }

    public void setSoloSeguidos(boolean soloSeguidos) {
        this.soloSeguidos = soloSeguidos;
    }

    public void publicarComentario(Publicacion p) {
        try {
            
            DatosDePublicacion d = datosDeBeanTodasLasPublicaciones.getDatosDePublicaciones().get(DatosDePublicacion.getLlave(p));
           if (d.getComentarioPropio().isEmpty()) {
                mensajeERROR("El comentario no puede estar vacio");
                return;

            }
            if (d.getComentarioPropio().length() > EAc.MAX_CARATERES_TEXTO) {
                mensajeERROR("El comentario no puede exeder los " + EAc.MAX_CARATERES_TEXTO + " caracteres");
                return;
            }

            LinkedList<DatosDeComentario> comentariosActualizados=FacesBDbean.servidores.addComentarioYgetLista(getService().getPrefilActual().getUsuarioActual().getUsername(),p.getUsersusername().getUsername(), p.getId(), d.getComentarioPropio());
            datosDeBeanTodasLasPublicaciones.putDatosDeComentario(p, comentariosActualizados);
             d.setComentarioPropio("");
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public boolean tieneComentarios(Publicacion p) {
        return !datosDeBeanTodasLasPublicaciones.getDatosDePublicaciones().get(DatosDePublicacion.getLlave(p)).getDatosDeComentario().isEmpty();
    }

    public List<DatosDeComentario> getComentariosDePublicacion(Publicacion p) {
        return datosDeBeanTodasLasPublicaciones.getDatosDePublicaciones().get(DatosDePublicacion.getLlave(p)).getDatosDeComentario();
    }
    
    public String getDireccionImagenDeUsuarioDatosDeComentario(DatosDeComentario d){
    return imagenesDeUsuarios.getDireccionImagen(d.getCuenta());
    }

    public String getCantidadDeComentarios(Publicacion p) {
        try {
int cant=datosDeBeanTodasLasPublicaciones.getDatosDePublicaciones().get(DatosDePublicacion.getLlave(p)).getDatosDeComentario().size();
            return cant == 0 ? "" : "Comentarios:" + cant + " ";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String getDateConFormato(Publicacion p) {
        return Tempus.getFechaYHoraConSegundos(p.getFecha());
    }

    public String getDateConFormato(DatosDeComentario p) {
        return Tempus.getFechaYHoraConSegundos(p.getFecha());
    }

    public Set<String> getListaDeTiposDeTemas(Publicacion p) {
//        System.out.println("p="+p);
//        System.out.println("datosDePublicaciones="+datosDePublicaciones);
//        System.out.println("datosDePublicaciones.get(p.getId())="+datosDePublicaciones.get(p.getId()));
//        System.out.println("datosDePublicaciones.get(p.getId()).getTiposDeTemasNombres()="+datosDePublicaciones.get(p.getId()).getTiposDeTemasNombres());
//        System.out.println("datosDePublicaciones.get(p.getId()).getTiposDeTemasNombres().keySet()="+datosDePublicaciones.get(p.getId()).getTiposDeTemasNombres().keySet());
        return datosDeBeanTodasLasPublicaciones.getDatosDePublicaciones().get(DatosDePublicacion.getLlave(p)).getTiposDeTemasNombres().keySet();
//        return tiposDeTemasNombres.get(p.getId()).keySet();
    }

    public void apretoPuntuacionPersonal(Publicacion p) {
        try {
            ClasificacionTemporal c=FacesBDbean.servidores.apretoPuntuacionPersonal(datosDeBeanTodasLasPublicaciones.getDatosDePublicaciones().get(DatosDePublicacion.getLlave(p)).getClasificacionTemporal());
            datosDeBeanTodasLasPublicaciones.getDatosDePublicaciones().get(DatosDePublicacion.getLlave(p)).setClasificacionTemporal(c);
//            datosDeBeanTodasLasPublicaciones.getDatosDePublicaciones().get(p.getId()).getClasificacionTemporal().apretoClasificacionPersonal();
       } catch (Exception ex) {
            responderException(ex);
        }

    }

    public String marginTopDeTime(Publicacion p) {
        return perteneceAUsuarioActual(p) ? "0" : "10";
    }

    public String OcultarSiperteneceAUsuarioActual(DatosDeComentario p) {
        return getStyleOcultar(perteneceAUsuarioActual(p));
    }

    public String OcultarSiNoPerteneceAUsuarioActual(DatosDeComentario p) {
        return getStyleOcultar(!perteneceAUsuarioActual(p));
    }

    public String OcultarSiperteneceAUsuarioActual(Comentario p) {
        return getStyleOcultar(perteneceAUsuarioActual(p));
    }

    public String OcultarSiperteneceAUsuarioActual(Publicacion p) {
       return getStyleOcultar(perteneceAUsuarioActual(p));
    }

    public String OcultarSiNoTieneComentarios(Publicacion p) {
        boolean ocultar = !tieneComentarios(p);
        return getStyleOcultar(ocultar);
    }

    public String OcultarCBSoloSeguidos() {
//        System.out.println("estyle ocul");
        String res = getStyleOcultar(getService().getSeleccionDeSubmenuPrincipal().getItemSeleccionado().equals(EAc.MI_VER_SEGUIDOS));
//        System.out.println("res="+res);
        return res;

    }

    public String OcultarSiNoEstaSubscrito(Publicacion d) {
        return getStyleOcultar((perteneceAUsuarioActual(d)) || (!estaSubscritoElUsuarioActualA(d)));
    }

    public String OcultarSiEstaSubscrito(Publicacion d) {
        return getStyleOcultar((perteneceAUsuarioActual(d)) || estaSubscritoElUsuarioActualA(d));
    }

    public boolean estaSubscritoElUsuarioActualA(Publicacion d) {
        return estaSubscritoElUsuarioActualA(d.getUsersusername());
    }

    public String OcultarSiNoEstaSubscrito(Comentario d) {
        return getStyleOcultar((perteneceAUsuarioActual(d)) || !estaSubscritoElUsuarioActualA(d));
    }

    public String OcultarSiEstaSubscrito(Comentario d) {
        return getStyleOcultar((perteneceAUsuarioActual(d)) || estaSubscritoElUsuarioActualA(d));
    }

    public boolean estaSubscritoElUsuarioActualA(Comentario d) {
        return estaSubscritoElUsuarioActualA(d.getUsenameid());
    }

    public String OcultarSiNoEstaSubscrito(DatosDeComentario d) {
        return getStyleOcultar((perteneceAUsuarioActual(d)) || !estaSubscritoElUsuarioActualA(d));
    }

    public String OcultarSiEstaSubscrito(DatosDeComentario d) {
        return getStyleOcultar((perteneceAUsuarioActual(d)) || estaSubscritoElUsuarioActualA(d));
    }

    public boolean estaSubscritoElUsuarioActualA(DatosDeComentario d) {
        return estaSubscritoElUsuarioActualA(d.getCuenta());
    }

    public void verSusPublicaciones(Publicacion d) {
        verSusPublicaciones(d.getUsersusername());
    }

    public void verSusPublicaciones(Comentario d)throws Exception {
        
        verSusPublicaciones(d.getUsenameid());
    }

    public void verSusPublicaciones(DatosDeComentario d) {
        try {
            verSusPublicaciones(d.getCuenta());
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public void EliminarPublicacion(Publicacion p) {
        try {
            FacesBDbean.servidores.eliminarPublicacionInterna(p);
            datosDeBeanTodasLasPublicaciones.getDatosDePublicaciones().remove(DatosDePublicacion.getLlave(p));
            for (int i = 0; i < datosDeBeanTodasLasPublicaciones.getPublicaciones().size(); i++) {
                if (datosDeBeanTodasLasPublicaciones.getPublicaciones().get(i).getId().intValue() == p.getId().intValue()) {
                    datosDeBeanTodasLasPublicaciones.getPublicaciones().remove(i);
                }
            }
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public void EliminarMensaje(Mensaje p) {
        try {
            FacesBDbean.servidores.eliminarMensaje(p);
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public void EliminarComentario(Comentario p) {
        try {
            FacesBDbean.servidores.eliminarComentario(p);
            List<DatosDeComentario> l = datosDeBeanTodasLasPublicaciones.getDatosDePublicaciones().get(DatosDePublicacion.getLlave(p.getPublicacionid())).getDatosDeComentario();
            for (int i = 0; i < l.size(); i++) {
                if (l.get(i).getIdComentario() == p.getId().intValue()) {
                    l.remove(i);
                    break;
                }
            }

        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public void addSubcripcion(Comentario d) {
        try {
            System.out.println("intenta subscribir comentario=" + d);
            addSubcripcion(d.getUsenameid());
            getService().sortDatosDeUsuariosAdd(d.getUsenameid());
            System.out.println("va a actualizar al add");
            execute("actualizarFormEast2();");
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public void EliminarComentario(DatosDeComentario p) {
        try {
          
            Publicacion pu=FacesBDbean.servidores.getPublicacion(p.getCuentaAutorPublicacion(), p.getIdPublicacion());
            FacesBDbean.servidores.eliminarComentario(pu,p.getIdComentario());
            List<DatosDeComentario> l = datosDeBeanTodasLasPublicaciones.getDatosDePublicaciones().get(DatosDePublicacion.getLlave(pu)).getDatosDeComentario();
            for (int i = 0; i < l.size(); i++) {
                if (l.get(i).getIdComentario() == p.getIdComentario()) {
                    l.remove(i);
                    break;
                }
            }

        } catch (Exception ex) {
            responderException(ex);
        }

    }

    public void addSubcripcion(DatosDeComentario d) {
        try {
//            System.out.println("intenta subscribir DatosDeComentario="+d);
            addSubcripcion(d.getCuenta());
            getService().sortDatosDeUsuariosAdd(d.getCuenta());
            execute("actualizarFormEast2();");
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public void eliminarSubcripcion(DatosDeComentario d) {
        try {
//            System.out.println("intenta desubscribir DatosDeComentario="+d);
            eliminarSubcripcion(d.getCuenta());
            getService().sortDatosDeUsuariosRemove(d.getCuenta());
//            System.out.println("va a actualizar al eliminar");
            execute("actualizarFormEast2();");
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public void eliminarSubcripcionPublicacion(Publicacion d) {
        try {
//            System.out.println("intenta desubscribir publicacion="+d);
            eliminarSubcripcion(d.getUsersusername());
//            System.out.println("va a ordenar publicacion");
            getService().sortDatosDeUsuariosRemove(d.getUsersusername().getUsername());
//            System.out.println("ordeno publicacion");
//            System.out.println("va a actualizar al eliminar publicacion");
            execute("actualizarFormEast2();");
////            System.out.println("actualizo al elimiar publicacion");
        } catch (Exception ex) {
            System.out.println("error al leminar la subcripcion publicacion");
            responderException(ex);
        }
    }

    public void addSubcripcionPublicacion(Publicacion d) {
        try {
          //  System.out.println("intenta subscribir publicacion=" + d);
            addSubcripcion(d.getUsersusername());
          //  System.out.println("va a ordenar publicacion");
            getService().sortDatosDeUsuariosAdd(d.getUsersusername().getUsername());
          //  System.out.println("ordeno publicacion");
         //   System.out.println("va a actualizar al add publicacion");
            execute("actualizarFormEast2();");
         //   System.out.println("actualizo al add publicacion");
        } catch (Exception ex) {
            System.out.println("error al add la subcripcion publicacion");
            responderException(ex);
        }
    }

    public void abrirChat(DatosDeComentario p) {
        try {
            //        System.out.println("se llama abrir");
            getService().abrirChat(p.getCuenta());
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public void abrirChat(Comentario p) {
        try {
            //        System.out.println("se llama abrir");
            getService().abrirChat(p.getUsenameid());
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public void abrirChat(Publicacion p) {
        try {
            //        System.out.println("se llama abrir");
            getService().abrirChat(p.getUsersusername());
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public String OcultarSiNoPerteneceAUsuarioActual(Publicacion p) {
        return getStyleOcultar(!perteneceAUsuarioActual(p));
    }

    private boolean perteneceAUsuarioActual(Publicacion p) {
        return getService().getPrefilActual().getUsuarioActual().getUsername().equals(p.getUsersusername().getUsername());
    }

    private boolean perteneceAUsuarioActual(DatosDeComentario p) {
        return getService().getPrefilActual().getUsuarioActual().getUsername().equals(p.getCuenta());
    }

    private boolean perteneceAUsuarioActual(Comentario p) {
        return getService().getPrefilActual().getUsuarioActual().getUsername().equals(p.getUsenameid());
    }

    public void apretoLike(Publicacion p) {
        try {
          ClasificacionTemporal c=  FacesBDbean.servidores.apretoLike(datosDeBeanTodasLasPublicaciones.getDatosDePublicaciones().get(DatosDePublicacion.getLlave(p)).getClasificacionTemporal());
             datosDeBeanTodasLasPublicaciones.getDatosDePublicaciones().get(DatosDePublicacion.getLlave(p)).setClasificacionTemporal(c);
//            datosDeBeanTodasLasPublicaciones.getDatosDePublicaciones().get(p.getId()).getClasificacionTemporal().apretoLike();
       } catch (Exception ex) {
            responderException(ex);
        }

    }

    public void apretoDislike(Publicacion p) {
        try {
                  ClasificacionTemporal c=  FacesBDbean.servidores.apretoDislike(datosDeBeanTodasLasPublicaciones.getDatosDePublicaciones().get(DatosDePublicacion.getLlave(p)).getClasificacionTemporal());
             datosDeBeanTodasLasPublicaciones.getDatosDePublicaciones().get(DatosDePublicacion.getLlave(p)).setClasificacionTemporal(c);

//            datosDePublicaciones.get(p.getId()).getClasificacionTemporal().apretoDislike();
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public String getTemasDelTipo(Publicacion p, String tipo) {
        String res = tipo + ":";
//        System.out.println("p=" + p + "  tipo=" + tipo);
        HashMap<String, LinkedList<String>> map = datosDeBeanTodasLasPublicaciones.getDatosDePublicaciones().get(DatosDePublicacion.getLlave(p)).getTiposDeTemasNombres();
//        HashMap<String, LinkedList<String>> map = tiposDeTemasNombres.get(p.getId());
        if (map != null) {
//            System.out.println("tiposDeTemasNombres.get(p.getTitulo())=" + map);
            LinkedList<String> l = map.get(tipo);
//            System.out.println("tipo l =" + l);
            if (l != null) {
                for (int i = 0; i < l.size(); i++) {
                    res += l.get(i) + (i != l.size() - 1 ? " , " : "");
                }

            }

        }

        return res;
    }

    public String getContenido(Publicacion p) {
        return new String(p.getContenido());
    }
public List<Publicacion> getPublicacionesSeleccionadas() {
return datosDeBeanTodasLasPublicaciones.getPublicaciones();
}

public String getDireccionImagenDeUsuarioPublicacion(Publicacion p){
return imagenesDeUsuarios.getDireccionImagenYcrearDeSerNecesario(p.getUsersusername());
}

    public void onClose(CloseEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Panel Closed", "Closed panel id:'" + event.getComponent().getId() + "'");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void onToggle(ToggleEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, event.getComponent().getId() + " toggled", "Status:" + event.getVisibility().name());
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public String getUpdateAddSubcripcionEnPublicacion(Object o) {
        System.out.println("------------------------- o=" + o);
        int indice = ((Integer) o);
        return "form1:todasLasPublicaiones:" + indice + ":MISubcribirse  form1:todasLasPublicaiones:" + indice + ":MIDesubcribirse";
    }

    
    public DatosDePublicacion getDatosDePublicacion(Publicacion p) {
        return datosDeBeanTodasLasPublicaciones.getDatosDePublicaciones().get(DatosDePublicacion.getLlave(p));
    }

    public ClasificacionTemporal getClasificacionTemporal(Publicacion p) {
        return datosDeBeanTodasLasPublicaciones.getDatosDePublicaciones().get(DatosDePublicacion.getLlave(p)).getClasificacionTemporal();
//        return clasificacionesTemporales.get(p.getId());
    }
    
}
