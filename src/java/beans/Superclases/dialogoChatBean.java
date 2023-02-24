/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.Superclases;

import EstadoActual.EA;
import Utiles.MetodosUtiles.Tempus;
import Util.ClasesDeApoyo.DatosDeUsuario;
import Util.ClasesDeApoyo.DialogoChat;
import Util.EAc;
import static beans.Superclases.FacesBDbean.getStyleOcultar;
import controller.exceptions.NonexistentEntityException;
import entity.Mensaje;
import entity.Publicacion;
import entity.Users;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rene
 */
public class dialogoChatBean extends aplicacionBean {

    private String idFormDilogContent,position;
    
//    public DialogoChat getDialogoChat(String algo) {
//    return getDialogoChat();
//    }
    public DialogoChat getDialogoChat() {
        if(idFormDilogContent==null){
        return null;
        }
        return getService().getDialogosChat().get(idFormDilogContent);
    }

    public void seMostro() {
        getDialogoChat().setHover(true);
//         System.out.println("se mostro");//alert('algo');
scrollToEnd();
    }
public void scrollToEnd(){
String idCom=getService().getIDFormDLG(idFormDilogContent);
//System.out.println("getService().getIDFormDLG(idFormDilogContent)="+idCom);
        execute(" document.getElementById(\"" + idCom + "\").scrollTo(0,document.getElementById(\"" + idCom + "\").\n"
                + "scrollHeight);  ");//alert('hola');
}
    public void seOculto() {
//         System.out.println("se oculto");
        getDialogoChat().setHover(false);
        getService().hideDialogoChat(idFormDilogContent);
    }
    public String getDireccionImagenDestinatarioYcrearDeSerNecesario() {
        if(getDialogoChat()==null){return "";}
        Users u=getDialogoChat().getDestinatario();
    return u==null?"": imagenesDeUsuarios.getDireccionImagenYcrearDeSerNecesario(u);
    }
    
    public String getDireccionImagenUsuarioActualYcrearDeSerNecesario(String a) {
    return getDireccionImagenUsuarioActualYcrearDeSerNecesario();
    }
//public String getDireccionImagenUsuarioActual() {
//return 
//}
    public String getDireccionImagen(Mensaje m) {
//        String res=imagenesDeUsuarios.getDireccionImagenYcrearDeSerNecesario(m, esUsuarioActual(m));
String res=imagenesDeUsuarios.getDireccionImagenYcrearDeSerNecesario(m);
//        System.out.println("direccion en respuesta="+res);
        return res;
    }

    public String getBay(Mensaje m) {
        return esUsuarioActual(m) ? "by-me" : "by-other";
    }

    private String getPull(boolean left) {
        return left ? "pull-left" : "pull-right";
    }

    public String getPullImagen(Mensaje m) {
        return getPull(esUsuarioActual(m));
    }

    public String getPullChat(Mensaje m) {
        return getPull(!esUsuarioActual(m));
    }

    private boolean esUsuarioActual(Mensaje m) {
        boolean res=getService().getPrefilActual().getUsuarioActual().getUsername().equals(m.getUsersusernameorigen().getUsername());
//        System.out.println("******************************88 m="+m);
//        System.out.println("cont="+m.getContenido());
//        System.out.println("getService().getPrefilActual().getUsuarioActual().getUsername()="+getService().getPrefilActual().getUsuarioActual().getUsername());
//        System.out.println("m.getUsersusernameorigen().getUsername()="+m.getUsersusernameorigen().getUsername());
//        System.out.println("es de us actual="+res);
        return res;
    }

    public String getIdFormDilogContent() {
        return idFormDilogContent;
    }

    public void setIdFormDilogContent(String idFormDilogContent) {
        this.idFormDilogContent = idFormDilogContent;
    }
 public String getDateConFormato(Mensaje p) {
        return Tempus.getFechaYHoraConSegundos(p.getFecha());
    }
public void enviarMensaje(){
        try {
          Mensaje m=addMensaje(getDialogoChat().getDestinatario(), getDialogoChat().getNuevoMensaje());
           getDialogoChat().getMensajes().add(m);
//          getDialogoChat().setMensajes(getMensajeAllListCon(getDialogoChat().getDestinatario()));
            getDialogoChat().setNuevoMensaje("");
            scrollToEnd();
            //FacesBDbean.servidores.notificacionPublicoMensajes(m);
        } catch (Exception ex) {
            responderException(ex);
        }
}
 public String getUpdate(){
     switch(idFormDilogContent){
         case EAc.DLG_CHAT_DERECHO:
              return  ":"+EAc.FORM_CHAT_DERECHO+" "+":"+EAc.FORM_CHAT_DERECHO_FOOTER;
            // return  ":"+EA.FORM_CHAT_DERECHO+":"+EA.ID_CHAT_DERECHO;
             case EAc.DLG_CHAT_CENTRO:
                    return  ":"+EAc.FORM_CHAT_CENTRO+" "+":"+EAc.FORM_CHAT_CENTRO_FOOTER;
          //   return  ":"+EA.FORM_CHAT_CENTRO+":"+EA.ID_CHAT_CENTRO;
             case EAc.DLG_CHAT_ISQUIERDO:
                  return  ":"+EAc.FORM_CHAT_ISQUIERDO+" "+":"+EAc.FORM_CHAT_ISQUIERDO_FOOTER;
            // return  ":"+EA.FORM_CHAT_ISQUIERDO+":"+EA.ID_CHAT_ISQUIERDO;
     }
       
    return null;
    }
 public void verSusPublicacionesOrigen(Mensaje m){
        verSusPublicaciones(m.getUsersusernameorigen());
    }
 public String  getCuentaDestinatario(){
     if(getDialogoChat()==null){return "";}
     Users u=getDialogoChat().getDestinatario();
 return u==null?"": u.getUsername();
 }
// public String  getCuentaPropietario(){
//     if(getDialogoChat()==null){return "";}
//     Users u=getDialogoChat().getDestinatario();
// return u==null?"": u.getUsername();
// }
  public String  getCuentaUsuarioActual(String a){
  return getService().getPrefilActual().getUsuarioActual().getUsername();
  }
public boolean getRender(String a){
    boolean render=getDialogoChat()!=null;
//    System.out.println("reder="+render+" "+idFormDilogContent);
return render;
}
public String OcultarSiNoEsDeUsuarioActual(Mensaje m) {
        return getStyleOcultar(!esUsuarioActual(m));
    }
public String OcultarSiEsDeUsuarioActual(Mensaje m) {
        return getStyleOcultar(esUsuarioActual(m));
    }

public String ocultarSiNoEsDePropierarioYNoASidoVisto(Mensaje m){
//    System.out.println(m.getUsersusernameorigen().getUsername()+" a "+m.getUsersusernamedestino().getUsername()+" "+m.getContenido());
//    System.out.println("!m.getVisto()="+!m.getVisto());
//    System.out.println("!esUsuarioActual(m)="+!esUsuarioActual(m));
//    System.out.println("(!esUsuarioActual(m))||!m.getVisto()="+((!esUsuarioActual(m))||!m.getVisto()));
return getStyleOcultar((!esUsuarioActual(m))||!m.getVisto());
}
public  void eliminarEsteMensaje(Mensaje m) {
        try {
            FacesBDbean.eliminarMensaje(m);
       getDialogoChat().getMensajes().remove(m);
//            System.out.println("bprrpr="+borro);
        } catch (Exception ex) {
            responderException(ex);
        }
}
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
  
}
