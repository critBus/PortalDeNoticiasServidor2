/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util.ClasesDeApoyo;

import entity.Publicacion;
import java.io.Serializable;

/**
 *
 * @author Rene
 */
import java.util.Comparator;
public class ComparadorDePublicaciones implements Comparator<Publicacion> , Serializable{

    @Override
    public int compare(Publicacion o1, Publicacion o2) {
       return o1.getFecha().compareTo(o2.getFecha());
    }

    
    
}
