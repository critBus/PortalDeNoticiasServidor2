/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.Personalizados.Dialogos;

import EstadoActual.EA;
import Util.EAc;
import beans.Superclases.aplicacionBean;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Rene
 */
@ManagedBean(name = "beanDialogoVerContraseña3")
@ViewScoped
public class beanDialogoVerContraseña extends aplicacionBean implements Serializable {
    private String contraseña;
public void resetear() {

        contraseña=getService().getVerContrase();

    }
    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }
 public void apretoAceptar(){
     hideDialog(EAc.DLG_VER_CONTRASEÑA_LOGUIN);
 }   
}
