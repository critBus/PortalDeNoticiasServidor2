/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util.ClasesDeApoyo;

import java.io.Serializable;

/**
 *
 * @author Rene
 */
public class SeleccionDeSubmenu implements Serializable{
    private String submenuSeleccionado,itemSeleccionado;

    public SeleccionDeSubmenu(String submenuSeleccionado, String itemSeleccionado) {
        this.submenuSeleccionado = submenuSeleccionado;
        this.itemSeleccionado = itemSeleccionado;
    }

    public String getSubmenuSeleccionado() {
        return submenuSeleccionado;
    }

    public void setSubmenuSeleccionado(String submenuSeleccionado) {
        this.submenuSeleccionado = submenuSeleccionado;
    }

    public String getItemSeleccionado() {
        return itemSeleccionado;
    }

    public void setItemSeleccionado(String itemSeleccionado) {
        this.itemSeleccionado = itemSeleccionado;
    }
    
}
