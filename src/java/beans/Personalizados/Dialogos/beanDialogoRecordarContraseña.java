/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.Personalizados.Dialogos;

import EstadoActual.EA;
import Util.EAc;
//import Util.BDPersistence;
import static Utiles.JSF.FacesBean.bibrarDialog;
import static Utiles.JSF.FacesBean.hideDialog;
import static Utiles.JSF.FacesBean.responderException;
import beans.Superclases.FacesBDbean;
import beans.Superclases.aplicacionBean;
import entity.Users;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Rene
 */
@ManagedBean(name = "beanDialogoRecordarContraseña3")
@ViewScoped
public class beanDialogoRecordarContraseña extends aplicacionBean implements Serializable {

    private Users usuario;

    private boolean validarUsuario() {
        if (!isEmptyOR(usuario.getUsername(), usuario.getIdentificacion(),  usuario.getApellido1(), usuario.getApellido2(), usuario.getEmail(), usuario.getNombre())) {
//            System.out.println("usuarioAdd.getContraseña()="+usuarioAdd.getContraseña());
//            System.out.println("reafirmarContra="+reafirmarContra);
//            System.out.println("(!isEmpty(usuarioAdd.getContraseña(), reafirmarContra)="+(!isEmpty(usuarioAdd.getContraseña(), reafirmarContra)));
//            System.out.println("(!modificar)="+(!modificar));
//            System.out.println("((!modificar) || (!isEmpty(usuarioAdd.getContraseña(), reafirmarContra)))="+((!modificar) || (!isEmpty(usuarioAdd.getContraseña(), reafirmarContra))));

            return true;

        } else {
            mensajeERROR("No puede tener campos vacios");
        }

        return false;
    }

    public void resetearUsuario() {
if(FacesBDbean.servidores!=null){
try {
            usuario = getNewUser();
        } catch (Exception ex) {
            responderException(ex);
        }
}
        

    }

    public void apretoRecordar() {
        try {
            if (validarUsuario()) {
                Users u = FacesBDbean.servidores.getUsuarioInterno(usuario.getUsername());
                if (u != null) {
                    if (u.getApellido1().equals(usuario.getApellido1())&&
                         u.getApellido2().equals(usuario.getApellido2())&&
                            u.getEmail().equals(usuario.getEmail())&&
                            u.getNombre().equals(usuario.getNombre())) {
                    }
                  //  System.out.println("u.getPassword()="+u.getPassword());
                    getService().setVerContrase(u.getPassword());
                    System.out.println(" intenta ver ");
                    showDialog(EAc.DLG_VER_CONTRASEÑA_LOGUIN);
                    return;
                }

            }
             bibrarDialog(EAc.DLG_RECORDAR_CONTRASEÑA_LOGUIN);
             showDialog("Datos Erroneos");
            

        } catch (Exception ex) {
             bibrarDialog(EAc.DLG_RECORDAR_CONTRASEÑA_LOGUIN);
            responderException(ex);
        }
       
     }

    public void apretoCancelar() {

        hideDialog(EAc.DLG_RECORDAR_CONTRASEÑA_LOGUIN);
    }

    public Users getUsuario() {
        return usuario;
    }

    public void setUsuario(Users usuario) {
        this.usuario = usuario;
    }

}
