/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 * @author Rene
 */
public interface IEstadoActual extends Serializable {
    public String getCuentaActual();
    public LinkedList<String> getNotificaciones();
}
