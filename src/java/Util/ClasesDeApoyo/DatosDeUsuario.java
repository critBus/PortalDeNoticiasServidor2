/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util.ClasesDeApoyo;

//import beans.Superclases.FacesBDbean;
import RMI.ConexionServidores;
import comunicacion.ComunicacionServidor2;
import entity.Mensaje;
import entity.Users;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rene
 */
public class DatosDeUsuario implements Serializable {

    //direccionDeImagen=
    private String cuenta;
    private int cantidadDeMensajesNoVistos;
    private Date dateUltimoMensaje;

    public DatosDeUsuario(ConexionServidores servidor, Users u, Users actual) throws Exception {
//        System.out.println("direccionDeImagen="+direccionDeImagen);
        cuenta = u.getUsername();
                List<Mensaje> mensajesNoVistos = servidor.getMensajeNoVistoAllList(u.getUsername(), actual.getUsername());
       // List<Mensaje> mensajesNoVistos = servidor.getMensajeNoVistoAllList(u, actual);
        if (mensajesNoVistos == null) {
            mensajesNoVistos = new LinkedList<>();
        }
        //cantidadDeMensajesNoVistos=servidor.getCantidadDeMensajeNoVisto(u, actual);
        cantidadDeMensajesNoVistos = mensajesNoVistos.size();
        if (cantidadDeMensajesNoVistos > 0) {
            dateUltimoMensaje = mensajesNoVistos.get(mensajesNoVistos.size() - 1).getFecha();
        }
        mensajesNoVistos = null;
    }

    public DatosDeUsuario(String cuenta, int cantidadDeMensajesNoVistos, Date ultimoMensaje) {
        this.cuenta = cuenta;
        this.cantidadDeMensajesNoVistos = cantidadDeMensajesNoVistos;
        this.dateUltimoMensaje = ultimoMensaje;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public int getCantidadDeMensajesNoVistos() {
        return cantidadDeMensajesNoVistos;
    }

    public void setCantidadDeMensajesNoVistos(int cantidadDeMensajesNoVistos) {
        this.cantidadDeMensajesNoVistos = cantidadDeMensajesNoVistos;
        if (cantidadDeMensajesNoVistos < 1) {
            dateUltimoMensaje = null;
        }
    }

    public Date getDateUltimoMensaje() {
        return dateUltimoMensaje;
    }

    public void setDateUltimoMensaje(Date dateUltimoMensaje) {
        this.dateUltimoMensaje = dateUltimoMensaje;
    }

    public static LinkedList<DatosDeUsuario> getListMenosUsuarioActual(ConexionServidores servidor, Users actual, LinkedList<Users> l) {
        LinkedList<DatosDeUsuario> sal = new LinkedList<>();
        l.forEach(v -> {
            if (!v.getUsername().equals(actual.getUsername())) {
                try {
                    sal.add(new DatosDeUsuario(servidor, v, actual));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        return sal;
    }
//    public static List<DatosDeUsuario> getList(LinkedList<Users> l){
//    LinkedList<DatosDeUsuario> sal=new LinkedList<>();
//    l.forEach(v->sal.add(new DatosDeUsuario(v)));
//    return sal;
//    }
}
