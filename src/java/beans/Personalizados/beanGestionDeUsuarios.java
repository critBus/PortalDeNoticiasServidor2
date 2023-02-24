/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.Personalizados;

import EstadoActual.EA;
import Util.EAc;
//import static Util.BDPersistence.eliminarUsuario;
//import static Util.BDPersistence.updateUsuario;
import beans.ClasesDeApoyo.UsersSeleccionable;
import beans.Superclases.FacesBDbean;
import beans.Superclases.aplicacionBean;
import entity.Users;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import org.primefaces.event.RowEditEvent;
//import javax.faces.event.ActionListener;
/**
 *
 * @author Rene
 */
@ManagedBean(name = "beanGestionDeUsuarios3")
@ViewScoped
public class beanGestionDeUsuarios extends aplicacionBean{

    private LinkedList<UsersSeleccionable> lista;// = new LinkedList<>()
    private LinkedList<UsersSeleccionable> listaFiltro;
    private Users usuarioEnLista, usuarioTemporal;

    @Override
    public void ini() {
        try {
            super.ini(); //To change body of generated methods, choose Tools | Templates.
//        System.out.println("ini *******************************");
lista = UsersSeleccionable.getLista(getUsuarioAllListInterno());
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public void resetear() {
        try {
//            System.out.println("resetear ges **********************************8");
            lista = UsersSeleccionable.getLista(getUsuarioAllListInterno());
//            System.out.println("lista.size()=" + lista.size());
// usuarioEnLista = getNewUser();
            usuarioTemporal = getNewUser();
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public void actualizar(RowEditEvent e) {
        try {
            Users t = (Users) e.getObject();
            FacesBDbean.servidores.updateUsuarioInterno(t);
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getUsers().getUsername().equals(t.getUsername())) {
                    // lista.get(i).getUsers().inicializar(t);
                    inicializarBasicosUsersAenB(lista.get(i).getUsers(), t);
                    break;
                }
            }
//            System.out.println("*************************             actualizo");
            mensajeINFO("Se  Modifico");
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public void cancelar(RowEditEvent row) {

    }

    public String getColorEstado(boolean estado) {
        return estado ? "green" : "red";
    }

    public void eliminar(Users u) {
        try {
            String cuentaAeliminar=u.getUsername();
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getUsers().getUsername().equals(u.getUsername())) {
                    FacesBDbean.servidores.eliminarUsuarioInterno(lista.remove(i).getUsers());
                    mensajeINFO("Se elimino");
                    break;

                }
            }
            
            getService().getDatosDeBeanPrincipalCliente().eliminarCuenta(cuentaAeliminar);

//eliminoCuenta(cuentaAeliminar); pq ya realize la notificacion
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public void eliminar() {
        try {
            boolean elimino = false;
            LinkedList<String> idsAEliminar=new LinkedList<>();
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).isSelected()) {
                    elimino = true;
                    idsAEliminar.add(lista.remove(i--).getUsers().getUsername());
                }
            }
            if (elimino) {
                String cuentasEliminadas[]=idsAEliminar.toArray(new String[0]);
                getService().getDatosDeBeanPrincipalCliente().eliminarCuenta(cuentasEliminadas);
                 FacesBDbean.servidores.eliminarUsuarioInterno(cuentasEliminadas);
//eliminaronCuentas(cuentasEliminadas); pq ya se notifico
                mensajeINFO("Se eliminaron");
            } else {
                mensajeINFO("No hay seleccionados");
            }

        } catch (Exception ex) {
            System.out.println("errrrrrrrrrrrrrrrrrrrrrrrrrorrrrrrrrr");
            responderException(ex);
        }
        // return "TablaUsuarios";
    }

   
    public void showDialogAdd() {
        getService().setDialogoAgregarDeGestion(true);
        showDialog(EAc.DLG_AGREGAR_USUARIO);
    }

    public void hideDialogAdd() {
        hideDialog(EAc.DLG_AGREGAR_USUARIO);
    }

    public void showDialogMod() {
        showDialog(EAc.DLG_EDITAR_USUARIO);
    }

    public void hideDialogMod() {
        //  System.out.println("hideeeeeeeeeeeeeeeeeeeeeeeeeee");
        hideDialog(EAc.DLG_EDITAR_USUARIO);
        //  mensajeINFO("intenta mii");
    }

    public void apretoVerInf(String cuenta) {
        //System.out.println("cuenta=" + cuenta);
        getService().setIdUsuarioInfo(cuenta);
        showDialog(EAc.DLG_INFO_USUARIO_GESTION);
    }
    public void apretoEditar(String cuenta) {
        getService().setIdUsuarioAEditar(cuenta);
        getService().setIdDLGEditar(EAc.DLG_EDITAR_USUARIO_GESTION);
        showDialog(EAc.DLG_EDITAR_USUARIO);
    }

    public LinkedList<UsersSeleccionable> getLista() {
        return lista;
    }

    public void setLista(LinkedList<UsersSeleccionable> lista) {
        this.lista = lista;
    }

    public Users getUsuarioEnLista() {
        return usuarioEnLista;
    }

    public void setUsuarioEnLista(Users usuarioEnLista) {
        this.usuarioEnLista = usuarioEnLista;
    }

    public Users getUsuarioTemporal() {
        return usuarioTemporal;
    }

    public void setUsuarioTemporal(Users usuarioTemporal) {
        this.usuarioTemporal = usuarioTemporal;
    }

    public LinkedList<UsersSeleccionable> getListaFiltro() {
        return listaFiltro;
    }

    public void setListaFiltro(LinkedList<UsersSeleccionable> listaFiltro) {
        this.listaFiltro = listaFiltro;
    }

   

}
