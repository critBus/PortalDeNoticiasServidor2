/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.ClasesDeApoyo;

import entity.Mensaje;
import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author Rene
 */
public class ComparadorDeMensajes implements Comparator<Mensaje> , Serializable{

    @Override
    public int compare(Mensaje o1, Mensaje o2) {
       return o1.getFecha().compareTo(o2.getFecha());
    }
    
}
