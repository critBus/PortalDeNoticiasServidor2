/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util.ClasesDeApoyo;

import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 * @author Rene
 */
public class DatosDeSubMenu implements Serializable{
    private String nombre,icon;
    private boolean expanded;
    private LinkedList<DatosDeMenuItem> items;

    public DatosDeSubMenu() {
        this("");
    }

    public DatosDeSubMenu(String nombre) {
        this(nombre,"",false,new LinkedList());
    }

    public DatosDeSubMenu(String nombre, String icon, boolean expanded, LinkedList<DatosDeMenuItem> items) {
        this.nombre = nombre;
        this.icon = icon;
        this.expanded = expanded;
        this.items = items;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LinkedList<DatosDeMenuItem> getItems() {
        return items;
    }

    public void setItems(LinkedList<DatosDeMenuItem> items) {
        this.items = items;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

   
    
}
