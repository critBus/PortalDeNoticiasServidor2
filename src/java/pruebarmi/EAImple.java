/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pruebarmi;

import Util.IEstadoActual;
import java.util.LinkedList;

/**
 *
 * @author Rene
 */
public class EAImple implements IEstadoActual{
LinkedList<String> notificaciones=new LinkedList<>();
    @Override
    public String getCuentaActual() {
       return "uno" ;
    }

    @Override
    public LinkedList<String> getNotificaciones() {
        return notificaciones;
    }
    
}
