/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util.ClasesDeApoyo;

//import EstadoActual.EA;
//import beans.Superclases.FacesBDbean;
import RMI.ConexionServidores;
import comunicacion.ComunicacionServidor2;
import entity.Mensaje;
import entity.Users;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
//import 

/**
 *
 * @author Rene
 */
public class DialogoChat implements Serializable {

    private List<Mensaje> mensajes;
    private String nuevoMensaje="";
    private Users destinatario;
    private boolean hover;

//    public dialogoChat(String idFormDilogContent) {
//        this.idFormDilogContent = idFormDilogContent;
//    }
    public DialogoChat(ConexionServidores servidor, Users propetario, Users destinatario) throws Exception {
        resetear(servidor, propetario, destinatario);
        hover = true;
    }

    public void resetear(ConexionServidores servidor, Users propetario, Users destinatario) throws Exception {
        this.destinatario = destinatario;
        mensajes = servidor.getMensajeAllListEntreAmbosSort(propetario, destinatario);
        if (mensajes == null) {
            mensajes = new LinkedList<>();
        }
        nuevoMensaje = "";

    }

    public List<Mensaje> getMensajes() {
        return mensajes;
    }

    public void setMensajes(List<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }

    public String getNuevoMensaje() {
        return nuevoMensaje;
    }

    public void setNuevoMensaje(String nuevoMensaje) {
        this.nuevoMensaje = nuevoMensaje;
    }

    public Users getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Users destinatario) {
        this.destinatario = destinatario;
    }

    public boolean isHover() {
        return hover;
    }

    public void setHover(boolean hover) {
        this.hover = hover;
    }

}
