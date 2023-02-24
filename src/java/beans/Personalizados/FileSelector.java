/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.Personalizados;

import EstadoActual.EA;
import Util.EAc;
import java.io.File;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.stream.FileImageOutputStream;
import org.primefaces.model.DefaultStreamedContent;

import Utiles.JSF.FacesBean;
import Utiles.MetodosUtiles.Archivo;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;


//prime faces
import org.primefaces.model.file.UploadedFile;
//import org.primefaces.model.UploadedFile;

/**
 *
 * @author Rene
 */
//@Named(value = "fileSelector")
@ManagedBean(name = "FileSelector3")
@ViewScoped
public class FileSelector extends FacesBean implements Serializable {

    private UploadedFile file;

    /**
     * Creates a new instance of FileSeleltor
     */
    public FileSelector() {
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }
    @ManagedProperty("#{beanAplicacion}")
    private EA service;

    public void setService(EA service) {
        this.service = service;
    }

    public void utilizarFile() {
        try {

            System.out.println("file=" + file);
            System.out.println("type=" + file.getContentType());
            System.out.println("nombre=" + file.getFileName());
            
            //byte cont[]=file.getContent();
            byte cont[]=file.getContent();
            
            
            System.out.println("leng=" + cont.length);
            
            String extencion = file.getContentType().endsWith("jpeg") ? ".jpg" : ".png";
            
            String direccionRelativa = "resources" + File.separator + "demo"
                    + File.separator + "images" + File.separator + "crop" + File.separator + "imagenACortar" + extencion;
            System.out.println("dire rel=" + direccionRelativa);
            String newFileName = getRealPath() + direccionRelativa;
            System.out.println("newFileName=" + newFileName);

            Archivo.crearImagen(cont, newFileName);
            System.out.println("crear d");
            service.getCortador().ini(newFileName, direccionRelativa);
            System.out.println("crear creado");
            showDialog(EAc.DLG_EDITAR_FOTO_PREFIL);
        } catch (Exception ex) {
            responderException(ex);
            System.out.println("error crear");
        }
    }

}
