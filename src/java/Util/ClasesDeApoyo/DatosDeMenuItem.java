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
public class DatosDeMenuItem implements Serializable{
     private String nombre,  hrefNombre,  icono;

    public DatosDeMenuItem(String nombre, String hrefNombre, String icono) {
//        System.out.println("**** nombre="+nombre+" hrefNombre="+hrefNombre+" icono="+icono);
        this.nombre = nombre;
        this.hrefNombre = hrefNombre;
        this.icono = icono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHrefNombre() {
        return hrefNombre;
    }

    public void setHrefNombre(String hrefNombre) {
        this.hrefNombre = hrefNombre;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }
     
     
}
