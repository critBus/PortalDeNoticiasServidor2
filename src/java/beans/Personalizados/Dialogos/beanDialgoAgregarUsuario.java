/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.Personalizados.Dialogos;

import EstadoActual.EA;
import Util.EAc;
import Util.PersistenceUtil;
import beans.ClasesDeApoyo.Prefil;
//import Util.BDPersistence;
import static Utiles.JSF.FacesBean.getRealPath;
import static Utiles.JSF.FacesBean.mensajeERROR;
import static Utiles.JSF.FacesBean.responderException;
import static Utiles.JSF.FacesBean.showDialog;
import Utiles.MetodosUtiles.Archivo;
import Utiles.MetodosUtiles.BD;
import Utiles.MetodosUtiles.MetodosUtiles;
import beans.ClasesDeApoyo.Cortador;
import beans.Superclases.FacesBDbean;
import beans.Superclases.aplicacionBean;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.inject.Named;
//import javax.faces.view.ViewScoped;
import entity.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ViewScoped;
//import org.primefaces.model.file.UploadedFile;

/**
 *
 * @author Rene
 */
//@Named(value = "beanDialgoAgreagar")
@ManagedBean(name = "beanDialgoAgregarUsuario3")
@ViewScoped
public class beanDialgoAgregarUsuario extends aplicacionBean implements Serializable {

    private Users usuario;
    private String reafirmarContra;
    private List<String> roles;
    private String rol;//,nombreModificada;
  /**
     * Creates a new instance of beanDialgoAgreagar
     */
    public beanDialgoAgregarUsuario() {

    }

    @Override
    public void ini() {
        try {
            super.ini(); //To change body of generated methods, choose Tools | Templates.
           //roles = getService().getRoles();
           roles =PersistenceUtil.getRolesString();
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public boolean agregarUsuario() {
        try {
            if (validarUsuarioAdd()) {
                usuario.setPassword(encriptar(usuario.getPassword()));
                //Users agregado = addUsuario(usuario, getService().isDialogoDeLogin() ? false : esAdministrador(rol));
                Users agregado = addUsuario(usuario, getService().isDialogoDeLogin() ? false : esAdministrador(PersistenceUtil.getRolReal(rol)));
                boolean irAinicio = !getService().isDialogoAgregarDeGestion();
                apretoCancelar();
               // seAgregoUnaCuenta(agregado.getUsername()); pq ya se envio la notificacion
                mensajeINFO("Se agrego con Exito");
               
                if (irAinicio) {
                    getService().setPrefilActual(new Prefil(agregado));
                    getService().resetear();
                    irA("Inicio");
                }
                else{
                 getService().getDatosDeBeanPrincipalCliente().agregarCuenta(agregado.getUsername());
                }
                return true;
            }
            bibrarDialog(EAc.DLG_AGREGAR_USUARIO);

        } catch (Exception ex) {
            responderException(ex);
        }
        return false;
    }

    public void apretoCancelar() {
        if (getService().isDialogoDeLogin()) {
            getService().setDialogoDeLogin(false);
            hideDialog(EAc.DLG_AGREGAR_USUARIO_LOGUIN);
            return;
        }
        if (getService().isDialogoAgregarDeGestion()) {
            getService().setDialogoAgregarDeGestion(false);
            hideDialog(EAc.DLG_AGREGAR_USUARIO);
            return;
        }
        hideDialog(EAc.DLG_AGREGAR_USUARIO);
    }

    private boolean validarUsuarioAdd() throws Exception {
        boolean modificar=false;
        boolean noVaciosNoModificar = modificar ? true : !isEmptyOR(usuario.getUsername(), usuario.getIdentificacion(), usuario.getPassword(), reafirmarContra);
        if (noVaciosNoModificar && !isEmptyOR(usuario.getDescription(), usuario.getApellido1(), usuario.getApellido2(), usuario.getEmail(), usuario.getNombre())) {
//            System.out.println("usuarioAdd.getContraseña()="+usuarioAdd.getContraseña());
//            System.out.println("reafirmarContra="+reafirmarContra);
//            System.out.println("(!isEmpty(usuarioAdd.getContraseña(), reafirmarContra)="+(!isEmpty(usuarioAdd.getContraseña(), reafirmarContra)));
//            System.out.println("(!modificar)="+(!modificar));
//            System.out.println("((!modificar) || (!isEmpty(usuarioAdd.getContraseña(), reafirmarContra)))="+((!modificar) || (!isEmpty(usuarioAdd.getContraseña(), reafirmarContra))));
            if (!MetodosUtiles.maximoDeCaracteres(EAc.MAX_CARATERES_PALABRA, usuario.getUsername(), usuario.getIdentificacion(), usuario.getApellido1(), usuario.getApellido2())) {
                mensajeERROR("Las palabras no pueden exeder los " + EAc.MAX_CARATERES_PALABRA + " caracteres");
                return false;
            }
            if (usuario.getDescription().length() > EAc.MAX_CARATERES_TEXTO) {
                mensajeERROR("La descripcion no puede exeder los " + EAc.MAX_CARATERES_TEXTO + " caracteres");
                return false;
            }
            if (((!modificar) || (!isEmptyAll(usuario.getPassword(), reafirmarContra)))) {
                if (usuario.getPassword().equals(getReafirmarContra())) {
                    if (usuario.getPassword().length() > 7) {

//                        if (modificar && usuario.getPassword().equals(usuario.getPassword())) {
//                            mensajeERROR("La contraseña nueva tiene que ser diferente a la anterior ");
//                            return false;
//                        }

                    } else {
                        mensajeERROR("Las Contraseña tienen que tener minimo 8 caracteres");
                        return false;
                    }
                } else {
                    mensajeERROR("Las Contraseñas tienen que ser iguales");
                    return false;
                }
            }
            if ((!modificar) && existeUsuarioInterno_Y_Externo(usuario.getUsername())) {
                mensajeERROR("Ya existe otro usario con este nombre de cuenta ");//2
                return false;
            }
            String validarDatos = EA.esValidoDatosBasicos(usuario, true);
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
        if (getService() != null && getService().getServiceRMI()!= null&& getService().getServiceRMI().getServidorRMI()!= null && getService().getRoles() != null) {
            try {
                usuario = getNewUser();
                reafirmarContra = "";
          } catch (Exception ex) {
                responderException(ex);
            }

        }

    }

    public String ocultarSiNoEsAdministrador() {
        return getStyleOcultar(!esAdministrador());
    }

    public void apretoAceptar() {

        agregarUsuario();

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

//    public void vario() {
//        System.out.println("siiiiiiiiiiiiiiii");
//    }
//
//    public void vario2() {
//        System.out.println("ooooooooooooooooooooooooooo");
//    }
//
//    public void vario3() {
//        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
//    }

    public String getUpdateAceptar() {
        if (getService().isDialogoAgregarDeGestion()) {
            return ":form :formEast2";
        }
        return ":formTop:tolbarPrincipal";
    }

}
