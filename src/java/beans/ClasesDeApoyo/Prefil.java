/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.ClasesDeApoyo;

import beans.Superclases.FacesBDbean;
import entity.Users;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author Rene
 */
public class Prefil implements Serializable{
//    private String direccionRelativaImagen;
 private Users  usuarioActual;
    
    public Prefil(Users u) throws IOException {
        usuarioActual=u;
//      direccionRelativaImagen=  FacesBDbean.crearImagenDeUsuario(u);
    }

    public String getDireccionRelativaImagen() {
        String res=FacesBDbean.imagenesDeUsuarios.getDireccionImagenYcrearDeSerNecesario(usuarioActual);
//        System.out.println("******************** imagen top="+res);
        return res;
//        return direccionRelativaImagen;
    }
//
//    public void setDireccionRelativaImagen(String direccionRelativaImagen) {
//        this.direccionRelativaImagen = direccionRelativaImagen;
//    }

    

    public Users getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(Users usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

   
    
}
