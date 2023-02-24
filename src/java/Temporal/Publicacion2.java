/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Temporal;

import java.io.Serializable;

/**
 *
 * @author Rene
 */
public class Publicacion2  implements Serializable {
    private String contenido;

    public Publicacion2(String contenido) {
        this.contenido = contenido;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
    
}
