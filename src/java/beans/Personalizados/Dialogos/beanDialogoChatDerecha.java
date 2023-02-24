/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.Personalizados.Dialogos;

import EstadoActual.EA;
import Util.EAc;
import beans.Superclases.dialogoChatBean;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Rene
 */
@ManagedBean(name = "beanDialogoChatDerecha3")
@ViewScoped
public class beanDialogoChatDerecha extends dialogoChatBean{

    @Override
    public void ini() {
        super.ini(); //To change body of generated methods, choose Tools | Templates.
        setIdFormDilogContent(EAc.DLG_CHAT_DERECHO);
        setPosition("left");
    }
    
}
