/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.Personalizados;

import EstadoActual.EA;
import Util.EAc;
//import Util.BDPersistence;
import beans.ClasesDeApoyo.Prefil;
import beans.Superclases.FacesBDbean;
import static beans.Superclases.FacesBDbean.getUsuario;
import static beans.Superclases.FacesBDbean.irA;
import beans.Superclases.aplicacionBean;
import entity.Mensaje;
import entity.Users;
//import extra.beanUno;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
//import org.primefaces.context.PrimeRequestContext;

/**
 *
 * @author Rene
 */
@ManagedBean(name = "beanLogin3")
@ViewScoped
public class beanLogin extends aplicacionBean {
 
    private String cuenta, contraseña;//= "12345678";= "uno"
    
    public void salioDelLoginTrue(){
    getService().setSalioDelLogin(true);
    }

    public void resetear() {
        cuenta = contraseña = "";
    }
 public void apretoLogin() {
      //  System.out.println("login ******************************");
getService().nombreUsuario=cuenta;
getService().password=contraseña;
        execute("logear();");
 }

    public void apretoCrearCuenta() {
        getService().setDialogoDeLogin(true);
        showDialog(EAc.DLG_AGREGAR_USUARIO_LOGUIN);
    }

    public void apretoRecordarContraseña() {
        showDialog(EAc.DLG_RECORDAR_CONTRASEÑA_LOGUIN);
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public void mostrarMensaje() {
        mensajeINFO("llego el mensaje");
    }

    public void iniEA() {
        System.out.println("llame a iniciar EA 000000000000000000000000000000000000");
        getService().iniciarEA();
    }

//    public void moverRaton() {
////    getService().setPri(PrimeRequestContext.getCurrentInstance());
////    getService().setFacs(FacesContext.getCurrentInstance());
//    }
}
