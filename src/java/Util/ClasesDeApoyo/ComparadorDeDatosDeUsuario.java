/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util.ClasesDeApoyo;

import Utiles.MetodosUtiles.MetodosUtiles;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author Rene
 */
public class ComparadorDeDatosDeUsuario implements  Serializable , Comparator<DatosDeUsuario> {

    private  final int O1PRIMERO = -1, O1SEGUNDO = 1;
    private Set<String> cuentasSeguidas;

    public ComparadorDeDatosDeUsuario() {
        cuentasSeguidas = new HashSet<>();
    }

    public void addCuentaSeguida(String cuenta) {
        cuentasSeguidas.add(cuenta);
    }

    public void removeCuentaSeguida(String cuenta) {
        cuentasSeguidas.remove(cuenta);
    }

    @Override
    public int compare(DatosDeUsuario o1, DatosDeUsuario o2) {
        boolean esSeguidoO1 = cuentasSeguidas.contains(o1.getCuenta());
        boolean esSeguidoO2 = cuentasSeguidas.contains(o2.getCuenta());
        boolean tieneMensajesO1 = o1.getCantidadDeMensajesNoVistos() > 0;
        boolean tieneMensajesO2 = o2.getCantidadDeMensajesNoVistos() > 0;
        if (tieneMensajesO1 && !tieneMensajesO2) {
            return O1PRIMERO;
        }
        if (tieneMensajesO2 && !tieneMensajesO1) {
            return O1SEGUNDO;
        }
        if(tieneMensajesO1&&tieneMensajesO2
                &&o1.getDateUltimoMensaje()!=null&&o2.getDateUltimoMensaje()!=null){
        return o1.getDateUltimoMensaje().compareTo(o2.getDateUltimoMensaje());
        }

//        if(tieneMensajesO1&&tieneMensajesO2){
        if (!esSeguidoO1 && esSeguidoO2) {
            return O1SEGUNDO;
        }
        if (!esSeguidoO2 && esSeguidoO1) {
            return O1PRIMERO;
        }

        return compararPorUsername(o1, o2);
//        }

    }

    public static int compararPorUsername(DatosDeUsuario o1, DatosDeUsuario o2) {
        return MetodosUtiles.compararStringAConStringB(o1.getCuenta(), o2.getCuenta(), true);
    }

    public Set<String> getCuentasSeguidas() {
        return cuentasSeguidas;
    }

    public void setCuentasSeguidas(Set<String> cuentasSeguidas) {
        this.cuentasSeguidas = cuentasSeguidas;
    }
    
}
