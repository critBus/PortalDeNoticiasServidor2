/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util.ClasesDeApoyo;

import RMI.ConexionServidores;
import Util.EAc;
import comunicacion.ComunicacionServidor2;
//import Util.EAc;
//import EstadoActual.EA;
//import static Utiles.JSF.FacesBean.responderException;
//import beans.Personalizados.beanPrincipal;
//import beans.Superclases.FacesBDbean;
//import static beans.Superclases.FacesBDbean.getUsuarioAllList;
import entity.Tema;
import entity.Tipodetema;
import entity.Users;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

/**
 *
 * @author Rene
 */
public class DatosDeBeanPrincipal implements Serializable {

    //int bean principal *********************************************************
//    private MenuModel model;
    private DatosDeMenuModel model;

    private LinkedList<DatosDeUsuario> todosLosUsuarios;//usuariosActuales
    private String filtroDeUsuarios;

    private ComparadorDeDatosDeUsuario comparadorDeDatosDeUsuario;

    public DatosDeBeanPrincipal(ConexionServidores servidor, Users actual) {
        resetear(servidor, actual);
    }

    public void resetear(ConexionServidores servidor, Users actual) {
        try {
           // System.out.println("resetear datos Principal ***************************************************************");
            model = crearDatosDeMenuModel(servidor, actual);
            if (actual != null) {
                todosLosUsuarios = DatosDeUsuario.getListMenosUsuarioActual(servidor, actual, servidor.getUsuarioAllListInterno_Y_Externo());

                LinkedList<String> usariosSeguidos = servidor.getUsernamesSubscritosAllList(actual);
                if (usariosSeguidos == null) {
                    usariosSeguidos = new LinkedList<>();
                }
                comparadorDeDatosDeUsuario = new ComparadorDeDatosDeUsuario();
                for (int i = 0; i < usariosSeguidos.size(); i++) {
                    comparadorDeDatosDeUsuario.addCuentaSeguida(usariosSeguidos.get(i));
                }
                sort(todosLosUsuarios);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public ComparadorDeDatosDeUsuario getComparadorDeDatosDeUsuario() {
        return comparadorDeDatosDeUsuario;
    }

    public void setComparadorDeDatosDeUsuario(ComparadorDeDatosDeUsuario comparadorDeDatosDeUsuario) {
        this.comparadorDeDatosDeUsuario = comparadorDeDatosDeUsuario;
    }

    public static void addItem(DatosDeSubMenu sm, String nombre, String hrefNombre, String icono) {
        sm.getItems().add(new DatosDeMenuItem(nombre, hrefNombre, icono));
    }

    public static DatosDeMenuModel crearDatosDeMenuModel(ConexionServidores servidor, Users actual) throws Exception {
        DatosDeMenuModel model = new DatosDeMenuModel();
        DatosDeSubMenu sm = new DatosDeSubMenu(EAc.SUBMENU_GENERAL);
        sm.setIcon("fas fa-cogs");
        addItem(sm, "Inicio", "Inicio", "fas fa-home");
        addItem(sm, "Crear Publicacion", "CrearPublicacion", "fas fa-edit");
        addItem(sm, EAc.MI_VER_SEGUIDOS, EAc.PW_VER_PUBLICACIONES, "fas fa-star");
        addItem(sm, EAc.MI_VER_ULTIMAS, EAc.PW_VER_PUBLICACIONES, "fas fa-hourglass-end");
        addItem(sm, EAc.MI_MIS_PUBLICACIONES, EAc.PW_VER_PUBLICACIONES, "fas fa-sign-in-alt");
        sm.setExpanded(true);
        model.getSubMenus().add(sm);
        List<Tipodetema> l = servidor.getTipoDeTemaAllList();
        for (int i = 0; i < l.size(); i++) {

            sm = new DatosDeSubMenu(l.get(i).getNombre());
            List<Tema> te = servidor.getTemaAllList(l.get(i).getNombre());
            for (int j = 0; j < te.size(); j++) {
                addItem(sm, te.get(j).getNombre(), EAc.PW_VER_PUBLICACIONES, "fas fa-indent");
            }
            model.getSubMenus().add(sm);
            String icono = "";
            switch (l.get(i).getNombre()) {
                case "Tecnologia":
                    icono = "fas fa-dna";
                    break;
                case "Politica":
                    icono = "fas fa-globe";
                    break;
                case "Cine":
                    icono = "fas fa-tv";
                    break;
                case "Cultura":
                    icono = "fas fa-graduation-cap";
                    break;
                case "Musica":
                    icono = "fas fa-music";//fas fa-microphone  fas fa-file-audio
                    break;
            }
            if (!icono.isEmpty()) {
                sm.setIcon(icono);
            }

        }

        return model;
    }

    public static MenuModel crearMenuModel(String nombreDeBeanPrincipal, DatosDeMenuModel d) {
        MenuModel model = new DefaultMenuModel();
        for (int i = 0; i < d.getSubMenus().size(); i++) {
            DatosDeSubMenu sub = d.getSubMenus().get(i);
            DefaultSubMenu sm = new DefaultSubMenu(sub.getNombre());
            sm.setExpanded(sub.isExpanded());
            sm.setIcon(sub.getIcon());
            for (int j = 0; j < sub.getItems().size(); j++) {
                DatosDeMenuItem itm = sub.getItems().get(j);
                addItem(nombreDeBeanPrincipal, sm, itm.getNombre(), itm.getHrefNombre(), itm.getIcono());
            }
            model.addElement(sm);
        }
        return model;
    }

    public static void addItem(String nombreBeanPrincipal, DefaultSubMenu sm, String nombre, String hrefNombre, String icono) {

        DefaultMenuItem item = new DefaultMenuItem();
        item.setIcon("ui-icon-search");
        String inforamcionAPasar = "dire(" + hrefNombre + ") submenu(" + sm.getLabel() + ") item(" + nombre + ")";
        item.setCommand("#{" + nombreBeanPrincipal + ".apretoMenuPrincipal}");

        item.setTitle(inforamcionAPasar);
        item.setValue(nombre);
        item.setIcon(icono);
        sm.getElements().add(item);
    }

    public DatosDeMenuModel getModel() {
        return model;
    }

    public void setModel(DatosDeMenuModel model) {
        this.model = model;
    }

    public LinkedList<DatosDeUsuario> getTodosLosUsuarios() {
        return todosLosUsuarios;
    }

    public void setTodosLosUsuarios(LinkedList<DatosDeUsuario> todosLosUsuarios) {
        this.todosLosUsuarios = todosLosUsuarios;
    }

    public String getFiltroDeUsuarios() {
        return filtroDeUsuarios;
    }

    public void setFiltroDeUsuarios(String filtroDeUsuarios) {
        this.filtroDeUsuarios = filtroDeUsuarios;
    }

    public void sort(List<DatosDeUsuario> l) {
        Collections.sort(l, comparadorDeDatosDeUsuario);
    }

}
