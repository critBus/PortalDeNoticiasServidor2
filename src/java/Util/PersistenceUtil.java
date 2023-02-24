/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import entity.Users;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Rene
 */
public abstract class PersistenceUtil {

    public final static String ROLE_ADMIN = "ROLE_ADMIN", ROLE_USER = "ROLE_USER", ROLE_ADMIN_STRING = "Administrador", ROLE_USER_STRING = "Usuario";

    public static Users inicializarBasicosUsersAenB(Users a, Users b) {
        a.setApellido1(b.getApellido1());
        a.setApellido2(b.getApellido2());
        a.setDescription(b.getDescription());
        a.setEmail(b.getEmail());
        a.setEnabled(b.getEnabled());
        a.setIdentificacion(b.getIdentificacion());
        a.setNombre(b.getNombre());
        a.setPassword(b.getPassword());
        a.setUsername(b.getUsername());

        a.setImagen(b.getImagen());
        a.setFormatodeimagen(b.getFormatodeimagen());
        return a;
    }

    public static boolean esAdministrador(String rol) {
        return rol.equals(ROLE_ADMIN);
    }

    public static String getRolString(String rolReal) {
        return rolReal.equals(ROLE_ADMIN) ? ROLE_ADMIN_STRING : ROLE_USER_STRING;
    }

    public static String getRolReal(String rolReal) {
        return rolReal.equals(ROLE_ADMIN_STRING) ? ROLE_ADMIN : ROLE_USER;
    }

    public static List<String> getRolesString() {
        return Arrays.asList(new String[]{ROLE_USER_STRING,ROLE_ADMIN_STRING });
    }
}
