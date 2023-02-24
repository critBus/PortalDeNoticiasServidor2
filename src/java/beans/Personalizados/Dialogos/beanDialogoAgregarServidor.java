/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.Personalizados.Dialogos;

import Util.EAc;
import Utiles.JSF.FacesBean;
import static Utiles.JSF.FacesBean.hideDialog;
import static Utiles.JSF.FacesBean.mensajeERROR;
import static Utiles.JSF.FacesBean.mensajeINFO;
import static Utiles.JSF.FacesBean.responderException;
import beans.Superclases.FacesBDbean;
import beans.Superclases.aplicacionBean;
import javax.faces.bean.ViewScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;

/**
 *
 * @author Rene
 */
@ManagedBean(name = "beanDialgoAgregarServidor3")
@ViewScoped
public class beanDialogoAgregarServidor extends aplicacionBean implements Serializable {

    private int a = 192, b = 168, c = 43, d = 0;

    public void resetear() {
        a = 192;
        b = 168;
        c = 43;
        d = 0;
    }

    private boolean validar() throws Exception {
        int[] partes = {a, b, c, d};
        for (int i = 0; i < partes.length; i++) {
            int p = partes[i];
            if (p < 0 || p > 255) {
                mensajeERROR("Escriba las direcciones correctamente");
                return false;
            }
        }
        if (FacesBDbean.servidores.existeUsuarioInterno(getDireccionIP())) {
            mensajeERROR("Ya existe esta direccion");
            return false;
        }
        return true;
    }

    public void apretoAgregar() {
        try {
            if (validar()) {
                FacesBDbean.servidores.addServidores(getDireccionIP());
                apretoCancelar();
                // System.out.println("update="+getUpdateAceptar()+" !!!!!!!!!!!!!!!1");
                getService().actualizarDatosPrincipales();
                mensajeINFO("Se agrego con Exito");
                getService().setEsDialogoAgregarDeServidores(true);
                //irA("GestionDeServidores");
            }
            bibrarDialog(EAc.DLG_AGREGAR_SERVIDOR);
        } catch (Exception ex) {
            responderException(ex);
        }

    }

    public void apretoCancelar() {
        hideDialog(EAc.DLG_AGREGAR_SERVIDOR);
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public int getD() {
        return d;
    }

    public void setD(int d) {
        this.d = d;
    }

    private String getDireccionIP() {
        return a + "." + b + "." + c + "." + d;
    }

    public String getUpdateAceptar() {
        if (getService().esDialogoAgregarDeServidores()) {
            return ":forAddServidor :form :formEast2";
        }
        return ":forAddServidor";
    }
    public String getUpdateEliminar() {
        if (getService().esDialogoAgregarDeServidores()) {
            return ":forAddServidor :form :formEast2";
        }
        return ":forAddServidor";
    }
}
