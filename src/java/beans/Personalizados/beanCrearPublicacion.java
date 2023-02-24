/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.Personalizados;

import beans.Superclases.aplicacionBean;
import Temporal.Publicacion2;
import beans.Superclases.FacesBDbean;
//import Util.BDPersistence;
import entity.Publicacion;
import entity.Tema;
import entity.Tipodetema;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

/**
 *
 * @author Rene
 */
@ManagedBean(name = "beanCrearPublicacion3")
@ViewScoped
public class beanCrearPublicacion extends aplicacionBean {

    private String text, titulo, temasSeleccionados[];
    private List<SelectItem> temasTodos;

    //@ManagedProperty("#{beanAplicacion}")
    //private beanAplicacion aplicacion;
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String[] getTemasSeleccionados() {
        return temasSeleccionados;
    }

    public void setTemasSeleccionados(String[] temasSeleccionados) {
        this.temasSeleccionados = temasSeleccionados;
    }

    /**
     * Creates a new instance of beanCrearPublicacion
     */
    public beanCrearPublicacion() {

    }

    @PostConstruct
    public void ini() {
        temasTodos = new ArrayList<SelectItem>();
        List<Tipodetema> l = getService().getTiposDeTemas();
        for (int i = 0; i < l.size(); i++) {
            try {
                SelectItemGroup g3 = new SelectItemGroup(l.get(i).getNombre());
//            List<Tema> te = new LinkedList(l.get(i).getTemaCollection());;
                List<Tema> te = FacesBDbean.servidores.getTemaAllList(l.get(i).getNombre());
                SelectItem se[] = new SelectItem[te.size()];
                for (int j = 0; j < te.size(); j++) {
                    //se[j]=new SelectItem(te.get(j), te.get(j).getNombre(), te.get(j).getNombre());
                    se[j] = new SelectItem(te.get(j).getId() + "", te.get(j).getNombre());
                }
                g3.setSelectItems(se);
                temasTodos.add(g3);
            } catch (Exception ex) {
                responderException(ex);
            }
        }
    }

    public void guardarPublicacion() {
       // System.out.println("enviar mensaje !!!!!!!!!!!!!!!!!!");
        //System.out.println("text=" + text);
        try {
            if (esValidoParaPublicar()) {
              int idTemasSeleccionados[] = new int[temasSeleccionados.length];
                for (int i = 0; i < idTemasSeleccionados.length; i++) {
                    idTemasSeleccionados[i] = inT(temasSeleccionados[i]);
                }
                FacesBDbean.servidores.addPublicacion(getService().getPrefilActual().getUsuarioActual(), text.replace("<img", "<img style=\"max-width: 100%;\""), titulo, idTemasSeleccionados);
                mensajeINFO("Se publico");
            }

        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public boolean esValidoParaPublicar() {
        try {
            if (titulo.trim().isEmpty()) {
                mensajeERROR("el titulo no puede estar vacio");
                return false;
            }
             int maxDeCaracteres=100;
            if(titulo.trim().length()>(maxDeCaracteres)){
             mensajeERROR("el titulo no puede contener mas de "+maxDeCaracteres+" caracteres");
                return false;
            }
            Publicacion p = FacesBDbean.servidores.getPublicacion(getService().getPrefilActual().getUsuarioActual(), titulo);
            if (p != null) {
                mensajeERROR("ya tienes una publicacion con este nombre");
                return false;
            }
            if (temasSeleccionados.length == 0) {
                mensajeERROR("tiene que selecionar algun tema");
                return false;
            }
            if (text.trim().isEmpty()) {
                mensajeERROR("el contenido no puede estar vacio");
                return false;
            }
            return true;
        } catch (Exception ex) {
            responderException(ex);
        }
        return false;
    }

    public List<SelectItem> getTemasTodos() {
        return temasTodos;
    }

    public void setTemasTodos(List<SelectItem> temasTodos) {
        this.temasTodos = temasTodos;
    }

}
