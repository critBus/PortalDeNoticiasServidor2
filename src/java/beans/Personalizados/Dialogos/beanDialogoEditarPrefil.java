/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.Personalizados.Dialogos;

import beans.Superclases.aplicacionBean;
import EstadoActual.EA;
//import static EstadoActual.EA.DLG_EDITAR_PREFIL;
import Util.EAc;
//import Util.BDPersistence;
import static Utiles.JSF.FacesBean.getRealPath;
import static Utiles.JSF.FacesBean.hideDialog;
import static Utiles.JSF.FacesBean.responderException;
import static Utiles.JSF.FacesBean.showDialog;
import Utiles.MetodosUtiles.Archivo;
import beans.ClasesDeApoyo.Cortador;
import beans.Superclases.FacesBDbean;
import entity.Users;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

//Prime faces
//import org.primefaces.model.UploadedFile;
import org.primefaces.model.file.UploadedFile;

/**
 *
 * @author Rene
 */
@ManagedBean(name = "beanDialogoEditarPrefil3")
@ViewScoped
public class beanDialogoEditarPrefil extends aplicacionBean {

    private UploadedFile file;

    /**
     * Creates a new instance of beanDialogoEditarPrefil
     */
    public beanDialogoEditarPrefil() {
//        resetear();
    }

    public void resetear() {
//        System.out.println("resetear +++++++++++++++++++++++++++++++++++++++++++");
//        System.out.println("getService().getCortador().isVolverDespuesDerecortar()=" + getService().getCortador().isVolverDespuesDerecortar());
        if (getService().getCortador().isVolverDespuesDerecortar()) {
//            System.out.println("Es volver despues de Recortar");
            Cortador c = getService().getCortador();
            c.setVolverDespuesDerecortar(false);
           c.setUsuarioAManejar(null);
            return;
        } else {

        }
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public void apretoModificarFoto() {
        try {
//            System.out.println("apreto modificar Foto ----------------");
            String extencion;
            byte contenido[];
            if (file != null && file.getContent().length != 0) {
//                System.out.println("file=" + file);
//                System.out.println("type=" + file.getContentType());
//                System.out.println("nombre=" + file.getFileName());
//
//                System.out.println("leng=" + file.getContent().length);
                if (!Archivo.esImagen(file.getFileName())) {
                    mensajeERROR("No es una imagen");
                    return;
                }
                extencion = Archivo.getExtencion(file.getFileName());
                contenido = file.getContent();
            } else {
//                System.out.println("Fue file null");
                Cortador c = getService().getCortador();
//            System.out.println("c.getDireccionImagenRecortada()="+c.getDireccionImagenRecortada());
                if (c.getDireccionImagenRecortada() != null && !c.getDireccionImagenRecortada().isEmpty()) {
//                    System.out.println("entro con modificada");
                    contenido = Archivo.getBytesDeImg(c.getDireccionImagenRecortada());
                    extencion = Archivo.getExtencion(c.getDireccionImagenRecortada());

                } else {
                    Users u = getService().getPrefilActual().getUsuarioActual();
                    extencion = u.getFormatodeimagen();
                    contenido = u.getImagen();
                }
            }

            String direccionRelativa = "resources" + File.separator + "demo"
                    + File.separator + "images" + File.separator + "crop" + File.separator + "imagenACortar" + extencion;
//            System.out.println("dire rel=" + direccionRelativa);
            String newFileName = getRealPath() + direccionRelativa;
//            System.out.println("newFileName=" + newFileName);

            Archivo.crearImagenYBorrarSiExiste(contenido, newFileName);
//            System.out.println("crear d");
            getService().getCortador().ini(newFileName, direccionRelativa);
            getService().getCortador().setUsuarioAManejar(getService().getPrefilActual().getUsuarioActual());
//            System.out.println("crear creado");
            showDialog(EAc.DLG_EDITAR_FOTO_PREFIL);
        } catch (Exception ex) {
            responderException(ex);
            System.out.println("error crear");
        }
    }
  public void apretoAceptar() {
        try {
//            System.out.println("apreto mf aceptar");
            boolean modifico = false;
            Users usuario = getService().getPrefilActual().getUsuarioActual();
            Cortador c = getService().getCortador();
//            System.out.println("c.getDireccionImagenRecortada()="+c.getDireccionImagenRecortada());
            if (c.getDireccionImagenRecortada() != null && !c.getDireccionImagenRecortada().isEmpty()) {
//                System.out.println("entro 1");
                usuario.setImagen(Archivo.getBytesDeImg(c.getDireccionImagenRecortada()));
                usuario.setFormatodeimagen(Archivo.getExtencion(c.getDireccionImagenRecortada()));
                c.setDireccionImagenRecortada(null);
                c.setDireccionRelativaImagenRecortada(null);
                modifico = true;
            } else {
//                System.out.println("2 file="+file);
                if (file != null && file.getContent().length != 0) {
//                    System.out.println("entro 2");
                    String extencion = file.getContentType().endsWith("jpeg") ? ".jpg" : ".png";
                    usuario.setImagen(file.getContent());
                    usuario.setFormatodeimagen(extencion);
//                    System.out.println("impuesta");
                    modifico = true;
                }
            }
//            System.out.println("modifico="+modifico);
            if (modifico) {
//                System.out.println("modifica");
                usuario = FacesBDbean.servidores.updateUsuarioBasicosInterno(usuario);
                getService().getPrefilActual().setUsuarioActual(usuario);
                imagenesDeUsuarios.resetear();
                imagenesDeUsuarios.crearImagenYBorrarSiExiste(usuario);
            }
            apretoCancelar();
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    private boolean hayImagenRecortada() {
        Cortador c = getService().getCortador();
        boolean res = c.getDireccionRelativaImagenRecortada() != null && !c.getDireccionRelativaImagenRecortada().isEmpty();
       return res;

    }

    public String getDireccionImagenRecortada() {
        String res = getService().getCortador().getDireccionRelativaImagenRecortada();
//        System.out.println("imagen a ver ="+res);
        return res;
    }
//public void cancelarEditarPrefil(){

    public void apretoCancelar() {
        Cortador c = getService().getCortador();
        c.setDireccionImagenRecortada(null);
        c.setDireccionRelativaImagenRecortada(null);
        hideDialog(EAc.DLG_EDITAR_PREFIL);
    }

    public String ocultarImagenRecortadaSiNoExiste() {
        String res = getStyleOcultar(!hayImagenRecortada());
//        System.out.println("salida ocultar=" + res);
        return res;
    }
}
