/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.Personalizados.Dialogos;

import EstadoActual.EA;
import beans.Superclases.aplicacionBean;
//import static EstadoActual.EA.DLG_EDITAR_FOTO_PREFIL;
import Util.EAc;
import static Utiles.JSF.FacesBean.getRealPath;
import static Utiles.JSF.FacesBean.hideDialog;
import static Utiles.JSF.FacesBean.mensajeERROR;
import static Utiles.JSF.FacesBean.mensajeINFO;
import static Utiles.JSF.FacesBean.responderException;
import Utiles.MetodosUtiles.Archivo;
import java.io.File;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;
import org.primefaces.model.CroppedImage;

/**
 *
 * @author Rene
 */
@ManagedBean(name = "beanDialogoEditarFotoPrefil3")
@ViewScoped
public class beanDialogoEditarFotoPrefil extends aplicacionBean {

    private CroppedImage croppedImage;
  private String direccionRelativaImagenACortar;

    /**
     * Creates a new instance of beanDialogoEditarFotoPrefil
     */
    public beanDialogoEditarFotoPrefil() {
    }

    @Override
    public void ini() {
        super.ini(); //To change body of generated methods, choose Tools | Templates.
        iniciar();
    }

    public void iniciar() {
        direccionRelativaImagenACortar = getService().getCortador().getDireccionRelativaImagenAcortar();
//        System.out.println("direccionRelativaImagenACortar=" + direccionRelativaImagenACortar);
    }

    public CroppedImage getCroppedImage() {
        return croppedImage;

    }
 public void setCroppedImage(CroppedImage croppedImage) {
        this.croppedImage = croppedImage;
    }

    public void crop() {

        if (croppedImage == null) {
            System.out.println("nullllllllllllllllllll");
            mensajeERROR("es null la imagen");
            return;
        }

        String extencion = getService().getCortador().getDireccionImagenAcortar().endsWith("png") ? ".png" : ".jpg";
        String direccionRelativaNewImagen = "resources" + File.separator + "demo"
                + File.separator + "images" + File.separator + "crop" + File.separator + "ImagenARecortada" + extencion;
//        System.out.println("dir rla2=" + direccionRelativaNewImagen);
        String direccionNewImagen = getRealPath() + direccionRelativaNewImagen;
//        System.out.println("newFffileName=" + direccionNewImagen);

        try {

            Archivo.crearImagenYBorrarSiExiste(croppedImage.getBytes(), direccionNewImagen);
            
            Archivo.crearImagenYBorrarSiExiste(croppedImage.getBytes(), getService().getCortador().getDireccionImagenAcortar());
            
            direccionRelativaImagenACortar = direccionRelativaNewImagen;
            getService().getCortador().setDireccionRelativaImagenAcortar(direccionRelativaImagenACortar);
            getService().getCortador().setDireccionImagenAcortar(direccionNewImagen);

aceptarRecortarImagenPrefil();
        } catch (Exception e) {
            responderException(e);

        }//:formTop:messages
    }


    public void aceptarRecortarImagenPrefil() {
        getService().getCortador().setDireccionRelativaImagenRecortada(direccionRelativaImagenACortar);
        getService().getCortador().setDireccionImagenRecortada(getRealPath() + direccionRelativaImagenACortar);
       cancelarRecortarImagen();
    }

    public void cancelarRecortarImagen() {
        getService().getCortador().setVolverDespuesDerecortar(true);
        hideDialog(EAc.DLG_EDITAR_FOTO_PREFIL);
        showDialog(EAc.DLG_EDITAR_PREFIL);
    }

    private String getRandomImageName() {
        int i = (int) (Math.random() * 100000);

        return String.valueOf(i);
    }

    public String getDireccionRelativaImagenACortar() {
        return direccionRelativaImagenACortar;
    }

    public void setDireccionRelativaImagenACortar(String direccionRelativaImagenACortar) {
        this.direccionRelativaImagenACortar = direccionRelativaImagenACortar;
    }

}
