/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.ClasesDeApoyo;

import entity.Users;
import java.io.Serializable;

/**
 *
 * @author Rene
 */
public class Cortador implements Serializable{
    //direccionDeImagen=
     private String  direccionImagenAcortar,direccionRelativaImagenAcortar,direccionImagenRecortada,direccionRelativaImagenRecortada;
     private boolean volverDespuesDerecortar;
     private Users usuarioAManejar; 
    public Cortador() {
//        System.out.println("int cortador *******************************");
        direccionImagenAcortar=direccionRelativaImagenAcortar=direccionImagenRecortada=direccionRelativaImagenRecortada=null;
         volverDespuesDerecortar=false;
       usuarioAManejar=null;
    }

    public Cortador(String direccionImagenAcortar, String direccionRelativaImagenAcortar) {
        ini(direccionImagenAcortar, direccionRelativaImagenAcortar);
    }
public void ini(String direccionImagenAcortar, String direccionRelativaImagenAcortar) {
        this.direccionImagenAcortar = direccionImagenAcortar;
        this.direccionRelativaImagenAcortar = direccionRelativaImagenAcortar;
        
    }
    public String getDireccionImagenAcortar() {
        return direccionImagenAcortar;
    }

    public void setDireccionImagenAcortar(String direccionImagenAcortar) {
        this.direccionImagenAcortar = direccionImagenAcortar;
    }

    public String getDireccionRelativaImagenAcortar() {
        return direccionRelativaImagenAcortar;
    }

    public void setDireccionRelativaImagenAcortar(String direccionRelativaImagenAcortar) {
        this.direccionRelativaImagenAcortar = direccionRelativaImagenAcortar;
    }

    public String getDireccionImagenRecortada() {
        return direccionImagenRecortada;
    }

    public void setDireccionImagenRecortada(String direccionImagenRecortada) {
        this.direccionImagenRecortada = direccionImagenRecortada;
    }

    public String getDireccionRelativaImagenRecortada() {
        return direccionRelativaImagenRecortada;
    }

    public void setDireccionRelativaImagenRecortada(String direccionRelativaImagenRecortada) {
        this.direccionRelativaImagenRecortada = direccionRelativaImagenRecortada;
    }

    public boolean isVolverDespuesDerecortar() {
        return volverDespuesDerecortar;
    }

    public void setVolverDespuesDerecortar(boolean volverDespuesDerecortar) {
        System.out.println("*************** seteado = "+volverDespuesDerecortar);
        this.volverDespuesDerecortar = volverDespuesDerecortar;
    }

    public Users getUsuarioAManejar() {
        return usuarioAManejar;
    }

    public void setUsuarioAManejar(Users usuarioAManejar) {
        this.usuarioAManejar = usuarioAManejar;
    }
     
}
