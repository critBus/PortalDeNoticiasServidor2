/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.ClasesDeApoyo;

import static Utiles.JSF.FacesBean.responderException;
import beans.Superclases.FacesBDbean;
import static beans.Superclases.FacesBDbean.crearImagenDeUsuario;
import entity.Comentario;
import entity.Mensaje;
import entity.Publicacion;
import entity.Users;
import java.util.HashMap;
import javafx.util.Callback;

/**
 *
 * @author Rene
 */
public class ImagenesDeUsuarios {
//direccionDeImagen=

    private HashMap<String, String> imagenesDeUsuarios;
    private static final Callback<Object, String> crearImagenYBorrarSiExiste = v -> {
        try {
            if (v instanceof String) {
                return crearImagenDeUsuario((String) v);
            }
            if (v instanceof Users) {
                return crearImagenDeUsuario((Users) v);
            }
            if (v instanceof Publicacion) {
                return crearImagenDeUsuario((Publicacion) v);
            }
            if (v instanceof Comentario) {
                return crearImagenDeUsuario((Comentario) v);
            }
        } catch (Exception ex) {
            responderException(ex);
        }
        return null;
    };
    
    public ImagenesDeUsuarios() {
        resetear();
    }
    
    public void resetear() {
        imagenesDeUsuarios = new HashMap();
    }

    public void crearImagenYBorrarSiExiste(Users u) {
        ImagenesDeUsuarios.this.crearImagenYBorrarSiExiste(u.getUsername(), u);
    }

    public void crearImagenDeSerNecesario(Users u) {
        crearImagenDeSerNecesarioGeneral(u.getUsername(), u);
    }
    
    public void crearImagenDeSerNecesario(String cuenta, Publicacion p) {
        crearImagenDeSerNecesarioGeneral(cuenta, p);
    }
    
    public void crearImagenDeSerNecesario(String cuenta, Comentario p) {
        crearImagenDeSerNecesarioGeneral(cuenta, p);
    }

    public void crearImagenDeSerNecesario(String cuenta) {
        crearImagenDeSerNecesarioGeneral(cuenta, cuenta);
    }

    private void crearImagenYBorrarSiExiste(String cuenta, Object p) {
        imagenesDeUsuarios.put(cuenta, crearImagenYBorrarSiExiste.call(p));
    }

    private void crearImagenDeSerNecesarioGeneral(String cuenta, Object p) {
        if (!imagenesDeUsuarios.containsKey(cuenta)) {
//            imagenesDeUsuarios.put(cuenta, crearImagen.call(p));
            ImagenesDeUsuarios.this.crearImagenYBorrarSiExiste(cuenta, p);
        }
    }

    public String getDireccionImagenYcrearDeSerNecesario(Mensaje u) {
        return getDireccionImagenYcrearDeSerNecesario(u, true);
    }

    public String getDireccionImagenYcrearDeSerNecesario(Mensaje u, boolean origen) {
//        System.out.println("m de "+u.getUsersusernameorigen().getUsername()+" para "+u.getUsersusernamedestino().getUsername());
//        System.out.println("origen="+origen);
        String s = origen ? u.getUsersusernameorigen().getUsername() : u.getUsernamedestino();
//        System.out.println("pasar u="+s.getUsername());
        return getDireccionImagenYcrearDeSerNecesario(s);
    }

    public String getDireccionImagenYcrearDeSerNecesario(Users u) {
//        System.out.println("obte u="+u.getUsername());
        crearImagenDeSerNecesario(u);
        return getDireccionImagen(u);
    }
 public String getDireccionImagenYcrearDeSerNecesario(String u) {
//        System.out.println("obte u="+u.getUsername());
        crearImagenDeSerNecesario(u);
        return getDireccionImagen(u);
    }
    public String getDireccionImagen(Users u) {
        return getDireccionImagen(u.getUsername());
    }

    public String getDireccionImagen(String cuenta) {
        crearImagenDeSerNecesario(cuenta);
        return imagenesDeUsuarios.get(cuenta);
    }
}
