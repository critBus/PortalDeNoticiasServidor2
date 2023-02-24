/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.Personalizados.Dialogos;

import EstadoActual.EA;
import Util.EAc;
import Util.PersistenceUtil;
//import Util.BDPersistence;
import Utiles.ClasesUtiles.EducacionFisica.EducacionFisicaUtiles;
import static Utiles.JSF.FacesBean.hideDialog;
import static Utiles.JSF.FacesBean.mensajeERROR;
import Utiles.MetodosUtiles.MetodosUtiles;
import beans.Superclases.FacesBDbean;
//import static beans.Superclases.FacesBDbean.existeUsuario;
import static beans.Superclases.FacesBDbean.getNewUser;
import static beans.Superclases.FacesBDbean.getStyleOcultar;
import beans.Superclases.aplicacionBean;
import entity.Users;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.hibernate.mapping.Collection;

/**
 *
 * @author Rene
 */
@ManagedBean(name = "beanDialogoEditarUsuario3")
@ViewScoped
public class beanDialogoEditarUsuario extends aplicacionBean implements Serializable {

    private Users usuario;
    private String reafirmarContra, contraseñaAnterior;
    private List<String> roles;
    private String rol;

    private boolean modificarContraseña;

    @Override
    public void ini() {
        try {
            super.ini(); //To change body of generated methods, choose Tools | Templates.
            //roles = getRolesUsuario();
            roles = PersistenceUtil.getRolesString();
            if (FacesBDbean.servidores.esAdministradorInterno(getActual())) {
                Collections.reverse(roles);
            }
//         resetearUsuario();
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    private boolean validarUsuario() throws Exception {
        Users actual = getActual();
//        System.out.println("modificarContraseña=" + modificarContraseña);
        boolean noVaciosNoModificar = !modificarContraseña ? true : !isEmptyOR(usuario.getUsername(), usuario.getIdentificacion(), usuario.getPassword(), reafirmarContra);
//        System.out.println("noVaciosNoModificar=" + noVaciosNoModificar);
//        System.out.println("!isEmptyOR(usuario.getDescription(), usuario.getApellido1(), usuario.getApellido2(), usuario.getEmail(), usuario.getNombre())=" + !isEmptyOR(usuario.getDescription(), usuario.getApellido1(), usuario.getApellido2(), usuario.getEmail(), usuario.getNombre()));
        if (noVaciosNoModificar && !isEmptyOR(usuario.getDescription(), usuario.getApellido1(), usuario.getApellido2(), usuario.getEmail(), usuario.getNombre())) {
            if (!MetodosUtiles.maximoDeCaracteres(EAc.MAX_CARATERES_PALABRA, usuario.getIdentificacion(), usuario.getApellido1(), usuario.getApellido2())) {
                mensajeERROR("Las palabras no pueden exeder los " + EAc.MAX_CARATERES_PALABRA + " caracteres");
                return false;
            }
            if (usuario.getDescription().length() > EAc.MAX_CARATERES_TEXTO) {
                mensajeERROR("La descripcion no puede exeder los " + EAc.MAX_CARATERES_TEXTO + " caracteres");
                return false;
            }
            if (modificarContraseña) {
                if (actual.getPassword().equals(encriptar(contraseñaAnterior))) {
                    if (usuario.getPassword().equals(getReafirmarContra())) {
                        if (usuario.getPassword().length() > 7) {

                            if (modificarContraseña && encriptar(usuario.getPassword()).equals(actual.getPassword())) {
                                mensajeERROR("La contraseña nueva tiene que ser diferente a la anterior ");
                                return false;
                            }

                        } else {
                            mensajeERROR("Las Contraseña tienen que tener minimo 8 caracteres");
                            return false;
                        }
                    } else {
                        mensajeERROR("Las Contraseñas tienen que ser iguales");
                        return false;
                    }
                } else {
                    mensajeERROR("Las Contraseñas anterior es erronea");
                    return false;
                }

            }
            String validarDatos = EA.esValidoDatosBasicos(usuario, false);
            if (!validarDatos.isEmpty()) {
                mensajeERROR(validarDatos);
                return false;
            }

            return true;

        } else {
            mensajeERROR("No puede tener campos vacios");
        }

        return false;
    }

    public void resetearUsuario() {
        try {
            usuario = getNewUser();
            Users actual = getActual();
            usuario.setNombre(actual.getNombre());
            usuario.setApellido1(actual.getApellido1());
            usuario.setApellido2(actual.getApellido2());
            usuario.setDescription(actual.getDescription());
            usuario.setEmail(actual.getEmail());
            usuario.setIdentificacion(actual.getIdentificacion());
            usuario.setUsername(actual.getUsername());

            
            usuario.setEnabled(actual.getEnabled());
            usuario.setImagen(actual.getImagen());
            usuario.setFormatodeimagen(actual.getFormatodeimagen());
            reafirmarContra = "";
            modificarContraseña = false;
            contraseñaAnterior = "";
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    private Users getActual() {
        try {
            return getService().getIdDLGEditar().equals(EAc.DLG_EDITAR_USUARIO_GESTION) ? getUsuarioInterno(getService().getIdUsuarioAEditar()) : getService().getPrefilActual().getUsuarioActual();
        } catch (Exception ex) {
            responderException(ex);
        }
        return null;
    }

    public String getContraseñaAnterior() {
        return contraseñaAnterior;
    }

    public void setContraseñaAnterior(String contraseñaAnterior) {
        this.contraseñaAnterior = contraseñaAnterior;
    }

    public boolean getModificarContraseña() {
        return modificarContraseña;
    }

    public void setModificarContraseña(boolean modificarContraseña) {
        this.modificarContraseña = modificarContraseña;
    }

    public String ocultarSiNoEsAdministrador() {
        return getStyleOcultar(!esAdministrador());
    }

    public String ocultarSiNoModificarContraseña() {
        //System.out.println("en ocl modificarContraseña="+modificarContraseña);
        return getStyleOcultar(!modificarContraseña);
    }

    public void apretoAceptar() {
        try {
            if (validarUsuario()) {
                Users actual = getActual();
                actual.setNombre(usuario.getNombre());
                actual.setApellido1(usuario.getApellido1());
                actual.setApellido2(usuario.getApellido2());
                actual.setDescription(usuario.getDescription());
                actual.setEmail(usuario.getEmail());
                actual.setIdentificacion(usuario.getIdentificacion());
                if (modificarContraseña) {
                    actual.setPassword(encriptar(usuario.getPassword()));
                }

                if (actual.getImagen() == null) {
                    actual.setImagen(usuario.getImagen());
                    actual.setFormatodeimagen(usuario.getFormatodeimagen());
                }

                //System.out.println("comienza a editar");
                actual = FacesBDbean.servidores.updateUsuarioBasicosInterno(actual);

                
                if (FacesBDbean.servidores.esAdministradorInterno(getService().getPrefilActual().getUsuarioActual())) {
                    boolean esAdministrador = FacesBDbean.servidores.esAdministradorInterno(actual);
                    boolean esAdministradorSeleccionado = PersistenceUtil.esAdministrador(PersistenceUtil.getRolReal(rol));
                    if (esAdministrador != esAdministradorSeleccionado) {
                        FacesBDbean.servidores.setAuthoritiesInterno(actual, esAdministradorSeleccionado);
                    }
                }

                if (!getService().getIdDLGEditar().equals(EAc.DLG_EDITAR_USUARIO_GESTION)) {
                    getService().getPrefilActual().setUsuarioActual(actual);
                }
                //System.out.println("se edito");
                mensajeINFO("Se edito con Exito");
                apretoCancelar();
//                if(esAdministrador()){
//                actual.setEnabled(usuario.getEnabled());
//                }

            }
        } catch (Exception ex) {
            responderException(ex);
        }
        bibrarDialog(EAc.DLG_EDITAR_USUARIO);
    }

    public void apretoCancelar() {
        String id = getService().getIdDLGEditar();
        if (id.equals(EAc.DLG_EDITAR_USUARIO_GESTION)) {
            getService().setIdDLGEditar(EAc.DLG_EDITAR_USUARIO);
        }
        // System.out.println("hide editar");
        hideDialog(EAc.DLG_EDITAR_USUARIO);
    }

    public Users getUsuario() {
        return usuario;
    }

    public void setUsuario(Users usuario) {
        this.usuario = usuario;
    }

    public String getReafirmarContra() {
        return reafirmarContra;
    }

    public void setReafirmarContra(String reafirmarContra) {
        this.reafirmarContra = reafirmarContra;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getUpdateDLGAceptarEditarUsuario() {
        if (getService().getIdDLGEditar().equals(EAc.DLG_EDITAR_USUARIO_GESTION)) {
            return ":form";
        }
        return ":formTop:tolbarPrincipal :formEdit:msgEdit";
    }
}
