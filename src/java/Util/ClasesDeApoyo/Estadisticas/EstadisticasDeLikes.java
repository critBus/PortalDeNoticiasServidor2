/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util.ClasesDeApoyo.Estadisticas;

import java.io.Serializable;

/**
 *
 * @author Rene
 */
public class EstadisticasDeLikes implements Serializable{
    private int cantidadDeLikes[];
    private String labelLikes[];

    public EstadisticasDeLikes(int[] cantidadDeLikes, String[] labelLikes) {
        this.cantidadDeLikes = cantidadDeLikes;
        this.labelLikes = labelLikes;
    }

    public int[] getCantidadDeLikes() {
        return cantidadDeLikes;
    }

    public void setCantidadDeLikes(int[] cantidadDeLikes) {
        this.cantidadDeLikes = cantidadDeLikes;
    }

    public String[] getLabelLikes() {
        return labelLikes;
    }

    public void setLabelLikes(String[] labelLikes) {
        this.labelLikes = labelLikes;
    }
    
}
