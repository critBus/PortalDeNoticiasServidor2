/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.Personalizados.Dialogos;

//import static Util.BDPersistence.getRol;
import Util.PersistenceUtil;
import beans.Superclases.FacesBDbean;
import beans.Superclases.aplicacionBean;
import entity.Users;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

/**
 *
 * @author Rene
 */
@ManagedBean(name = "beanDialogoInfoUsuario3")
@ViewScoped
public class beanDialogoInfoUsuario extends aplicacionBean implements Serializable {
    private Users  usuarioAdd;
    
    public void resetear(){
        try {
            String cuenta=getService().getIdUsuarioInfo();
           // System.out.println("cuenta cargada es "+cuenta);
            usuarioAdd=getUsuarioInterno(cuenta);
        } catch (Exception ex) {
            responderException(ex);
        }
    }
    public String getRolUsuarioAdd() {
        try {
            if(usuarioAdd==null){
                return "nulll";
            }
            
            return PersistenceUtil.getRolString(FacesBDbean.servidores.getRolInterno(usuarioAdd));
        } catch (Exception ex) {
            responderException(ex);
        }
        return null;
    }

    public Users getUsuarioAdd() {
        return usuarioAdd;
    }

    public void setUsuarioAdd(Users usuarioAdd) {
        this.usuarioAdd = usuarioAdd;
    }
    
    
}
