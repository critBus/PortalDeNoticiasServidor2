/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.Superclases;

import EstadoActual.EA;
import Util.EAc;
//import Util.BDPersistence;
import Utiles.JSF.FacesBean;
import static Utiles.JSF.FacesBean.responderException;
import Utiles.JSF.FacesUtil;
import Util.ClasesDeApoyo.SeleccionDeSubmenu;
import static beans.Superclases.FacesBDbean.irA;
import controller.exceptions.NonexistentEntityException;
import entity.Clasificacion;
import entity.Comentario;
import entity.Mensaje;
import entity.Publicacion;
import entity.Subcripcion;
import entity.Tema;
import entity.Users;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Rene
 */
public class aplicacionBean extends FacesBDbean {
//direccionDeImagen=
    //resetear datos Principal ***************************************************************
@ManagedProperty("#{estadoActual3}")
    private EA service;

    public void setService(EA service) {
        this.service = service;
    }

    public EA getService() {
        return service;
    }

    public void verSusPublicaciones(String cuenta) throws Exception{
    //verSusPublicaciones(getUsuario(cuenta));
    getService().setSeleccionDeSubmenuPrincipal(new SeleccionDeSubmenu(EAc.SUBMENU_GENERAL, cuenta));
            try {
//                System.out.println("va a saltar");
                irA(EAc.PW_VER_PUBLICACIONES);
            } catch (IOException ex) {
//                System.out.println("error uno");
                responderException(ex);
            }
    }
public void verSusPublicaciones(Users u){
getService().setSeleccionDeSubmenuPrincipal(new SeleccionDeSubmenu(EAc.SUBMENU_GENERAL, u.getUsername()));
            try {
//                System.out.println("va a saltar");
                irA(EAc.PW_VER_PUBLICACIONES);
            } catch (IOException ex) {
//                System.out.println("error uno");
                responderException(ex);
            }
}
   
    
    public Publicacion addPublicacion(String contenido, String titulo, Tema... T) throws Exception {
       
        return FacesBDbean.servidores.addPublicacion(service.getPrefilActual().getUsuarioActual(), contenido, titulo, T);
    }

    public Clasificacion getClasificacion(Publicacion p) {
        try {
            return FacesBDbean.getClasificacion(service.getPrefilActual().getUsuarioActual(), p);
        } catch (Exception ex) {
            responderException(ex);
        }
        return null;
    }

    public boolean esAdministrador() {
        try {
            return FacesBDbean.esAdministradorInterno(service.getPrefilActual().getUsuarioActual());
        } catch (Exception ex) {
            responderException(ex);
        }
        return false;
    }

    public StreamedContent getStreamedContent() {
        return FacesUtil.getStreamedContent(service.getPrefilActual().getUsuarioActual().getImagen());
    }

    public Comentario addComentario(Publicacion p, String contenido) throws Exception {
        return FacesBDbean.addComentario(service.getPrefilActual().getUsuarioActual(), p, contenido);
    }
   public  List<Mensaje> getMensajeAllListCon(Users destinatario) {
        try {
            return service.getMensajeAllListCon(destinatario);
//   return getMensajeAllListEntreAmbosSort(service.getPrefilActual().getUsuarioActual(), destinatario);
        } catch (Exception ex) {
            responderException(ex);
        }
        return null;
   }
   
    public String getDireccionImagenUsuarioActualYcrearDeSerNecesario() {
    return imagenesDeUsuarios.getDireccionImagenYcrearDeSerNecesario(service.getPrefilActual().getUsuarioActual());
    }
    public  Mensaje addMensaje(Users destino,String contenido) throws Exception {
    return addMensaje(service.getPrefilActual().getUsuarioActual(),destino, contenido);
    }
    public  Subcripcion addSubcripcion(String propietario) throws Exception {
    return addSubcripcion(getUsuario(propietario));
    }
    public  Subcripcion addSubcripcion(Users propietario) throws Exception {
        return addSubcripcion(propietario, service.getPrefilActual().getUsuarioActual());
    }
    public  void eliminarSubcripcion(String propietario) throws NonexistentEntityException, Exception{
       eliminarSubcripcion(getUsuario(propietario));
    }
    public  void eliminarSubcripcion(Users propietario) throws NonexistentEntityException, Exception{
        eliminarSubcripcion(propietario, service.getPrefilActual().getUsuarioActual());
    }
    public  boolean estaSubscritoElUsuarioActualA(String cuenta){
        try {
            return getService().getDatosDeBeanPrincipalCliente().esSeguido(cuenta);
//            return estaSubscritoElUsuarioActualA(getUsuario(cuenta));
        } catch (Exception ex) {
            responderException(ex);
        }
        return false;
    }
    public  boolean estaSubscritoElUsuarioActualA(Users propietario){
        try {
             return getService().getDatosDeBeanPrincipalCliente().esSeguido(propietario.getUsername());
//            return existeSubcripcion(propietario, service.getPrefilActual().getUsuarioActual());
        } catch (Exception ex) {
            responderException(ex);
        }
        return false;
    }
}
