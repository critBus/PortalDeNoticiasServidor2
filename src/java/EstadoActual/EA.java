/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EstadoActual;

import comunicacion.ComunicacionServidor2;
import comunicacion.ComunicacionCliente2;
import Util.IEstadoActual;
import Util.ClasesDeApoyo.SeleccionDeSubmenu;
import beans.ClasesDeApoyo.Cortador;
import beans.ClasesDeApoyo.Prefil;
import Temporal.Publicacion2;
import Util.BDPersitence;
import Util.ClasesDeApoyo.DatosDeBeanPrincipalCliente;
//import Util.BDPersistence;
import Utiles.ClasesUtiles.Interfases.Funcionales.Creador;
import Utiles.ClasesUtiles.Servidores.ProcesoCliente;
import Utiles.JSF.FacesBean;
import Utiles.MetodosUtiles.BD;
import Utiles.MetodosUtiles.MetodosUtiles;
import Util.ClasesDeApoyo.DatosDeBeanPrincipal;
import Util.ClasesDeApoyo.DialogoChat;
import Util.EAc;
import Utiles.ClasesUtiles.Interfases.Funcionales.CreadorConException;
import Utiles.ClasesUtiles.Servidores.Serializable.ProcesoServidorRMI;
import static Utiles.JSF.FacesBean.responderException;
import beans.Superclases.FacesBDbean;
import com.sun.faces.application.NavigationHandlerImpl;
import entity.*;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import org.primefaces.application.DialogNavigationHandler;
import RMI.ServidorRMI;
import Utiles.JSF.FacesUtil;
import Utiles.MetodosUtiles.Archivo;
import beans.Personalizados.beanServidor;
import static beans.Superclases.FacesBDbean.servidores;
import beans.Superclases.aplicacionBean;
import java.io.File;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
//import org.primefaces.context.PrimeRequestContext;

/**
 *
 * @author Rene
 */
@ManagedBean(name = "estadoActual3", eager = true)
@SessionScoped
public class EA extends FacesBDbean implements Serializable, IEstadoActual {
//start

    @ManagedProperty("#{beanServidor3}")
    private beanServidor serviceRMI;

    public beanServidor getServiceRMI() {
        return serviceRMI;
    }

    public void setServiceRMI(beanServidor serviceRMI) {
        this.serviceRMI = serviceRMI;
    }

    private Cortador cortador;
    private Prefil prefilActual;
    private List<Tipodetema> tiposDeTemas;
    private SeleccionDeSubmenu seleccionDeSubmenuPrincipal;
    private HashMap<String, DialogoChat> dialogosChat;

    private boolean dialogoDeLogin;
    private boolean dialogoAgregarDeGestion;
    private boolean esDialogoAgregarDeServidores;

    private String idDLGEditar;
    private String idUsuarioAEditar;
    private String idUsuarioInfo;
    private String verContrase;
    private String tema;
    private DatosDeBeanPrincipalCliente DatosDeBeanPrincipalCliente;
    private List<String> roles;
//   private String leras;

    private LinkedList<String> notificaciones;

    public String nombreUsuario = "", password = "";

    public boolean salioDelLogin = false;

    public boolean isSalioDelLogin() {
        return salioDelLogin;
    }

    public void setSalioDelLogin(boolean a) {
        salioDelLogin = a;
    }

    public void procesoLogin() {
        if (!nombreUsuario.isEmpty() && !password.isEmpty()) {
            try {
//            System.out.println("entro en login cuenta="+nombreUsuario+" contraseña="+password);
                if (password != null && !password.isEmpty() && nombreUsuario != null && !nombreUsuario.isEmpty()) {
//                System.out.println("entro 1");
//                System.out.println("FacesBDbean.servidor="+FacesBDbean.servidor);//no encuentra el metodo
                    //FacesBDbean.servidor.getUsuarioAllList();
                    Users u = FacesBDbean.servidores.getUsuarioInterno(nombreUsuario, encriptar(password));
//                System.out.println("entro 2 u="+u);
                    if (u != null) {
                        setPrefilActual(new Prefil(u));
//                    System.out.println("entro 4");
                        resetear();
//                    System.out.println("entro 5");
                        //irA("Inicio");
                        salioDelLogin = false;
                        return;
                    }
                }
                // mensajeERROR("Cuenta o Contraseña Incorrecta");
                //System.out.println("se redirige al");
                System.out.println("!!!!!!!!!!!!! usuario o contraseña incorrectas !!!!!!!!!!!!!!!!!");
            } catch (Exception ex) {
                System.out.println(" dio error en el logeo !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                responderException(ex);
            }
            nombreUsuario = password = "";
        } else {
            System.out.println("alguno fue vacio 0000000000000000000000000000000");
            System.out.println("en login cuenta=" + nombreUsuario + " contraseña=" + password);
        }
    }

    @PostConstruct
    public void init() {
//        arrBl=new ArrayBlockingQueue<>(1);
        iniciarEA();
//        leras = "vacio";
    }

    public void iniciarEA() {
        if (getServiceRMI() != null) {
            if (getServiceRMI().getServidorRMI() == null) {
                getServiceRMI().iniciarServidor();
            }
//        if (servidorRMI == null) {
//            iniciarServidor();
//        }
            if (servidores == null) {
                try {
                    notificaciones = new LinkedList<>();
                    conectarAServidor(this);

                    if (tema == null) {
                        tema = "black-tie";
                    }
                    roles = getRolesUsuario();

                } catch (Exception ex) {
                    responderException(ex);
                }
            }
        }

    }

    public void resetear() {
        try {
            roles = getRolesUsuario();
            if (tema == null) {
                tema = "black-tie";
            }

            tiposDeTemas = getTipoDeTemaAllList();
            cortador = new Cortador();
            dialogosChat = new HashMap<>();
            dialogoDeLogin = false;
            verContrase = "";
            dialogoAgregarDeGestion = false;
            idDLGEditar = EAc.DLG_EDITAR_USUARIO;
            idUsuarioAEditar = "";
            idUsuarioInfo = "";
            DatosDeBeanPrincipalCliente = new DatosDeBeanPrincipalCliente("beanPrincipal3", servidores.getDatosDeBeanPrincipal(getPrefilActual().getUsuarioActual().getUsername()));
        } catch (Exception ex) {
            System.out.println("di eror al resetear EA en loogin 00000000000000000000000000000000");
            responderException(ex);
        }
    }

    public void abrirChat(String cuenta) throws Exception {
        abrirChat(getUsuario(cuenta));

    }

    public void abrirChat(Users u) throws Exception {
        //System.out.println("abrir chat u="+u);
        //if(){}
        CreadorConException<DialogoChat> c = () -> servidores.getDialogoChat(getPrefilActual().getUsuarioActual(), u);
        servidores.notificacionVioLosMensajes(u.getUsername(), getPrefilActual().getUsuarioActual().getUsername());
        String diDLG = "";
        for (int i = 0; i < 3; i++) {
            switch (i) {
                case 0:
                    diDLG = EAc.DLG_CHAT_DERECHO;
                    break;
                case 1:
                    diDLG = EAc.DLG_CHAT_CENTRO;
                    break;
                case 2:
                    diDLG = EAc.DLG_CHAT_ISQUIERDO;
                    break;
            }
            if (dialogosChat.get(diDLG) == null) {
                dialogosChat.put(diDLG, c.crear());
//                System.out.println("intenta abrir " + diDLG);
                showDialog(diDLG);
                return;
            } else {
                if (dialogosChat.get(diDLG).getDestinatario().getUsername().equals(u.getUsername())) {
                    return;
                }
            }
        }

        dialogosChat.put(EAc.DLG_CHAT_ISQUIERDO, dialogosChat.get(EAc.DLG_CHAT_CENTRO));
        dialogosChat.put(EAc.DLG_CHAT_CENTRO, dialogosChat.get(EAc.DLG_CHAT_DERECHO));
        dialogosChat.put(EAc.DLG_CHAT_DERECHO, c.crear());

    }

    public void hideDialogoChat(String idDLG) {
        dialogosChat.remove(idDLG);
    }

    public String getIDFormDLG(String idDLG) {//form5:dial1_content
        String idForm = "", idDialogo = "";
        switch (idDLG) {
            case EAc.DLG_CHAT_DERECHO:
                idForm = EAc.FORM_CHAT_DERECHO;
                idDialogo = EAc.ID_CHAT_DERECHO;
                break;
            case EAc.DLG_CHAT_ISQUIERDO:
                idForm = EAc.FORM_CHAT_ISQUIERDO;
                idDialogo = EAc.ID_CHAT_ISQUIERDO;
                break;
            case EAc.DLG_CHAT_CENTRO:
                idForm = EAc.FORM_CHAT_CENTRO;
                idDialogo = EAc.ID_CHAT_CENTRO;
                break;
        }
        //    return idForm+":"+idDialogo+"_content";
        return idDialogo + "_content";
    }

    public SeleccionDeSubmenu getSeleccionDeSubmenuPrincipal() {
        return seleccionDeSubmenuPrincipal;
    }

    public void setSeleccionDeSubmenuPrincipal(SeleccionDeSubmenu seleccionDeSubmenuPrincipal) {
        this.seleccionDeSubmenuPrincipal = seleccionDeSubmenuPrincipal;
    }

    public List<Tipodetema> getTiposDeTemas() {
        return tiposDeTemas;
    }

    public void setTiposDeTemas(List<Tipodetema> tiposDeTemas) {
        this.tiposDeTemas = tiposDeTemas;
    }

    public Prefil getPrefilActual() {
        return prefilActual;
    }

    public void setPrefilActual(Prefil prefilActual) {
        this.prefilActual = prefilActual;
    }

    public Cortador getCortador() {
        return cortador;
    }

    public void setCortador(Cortador cortador) {
        this.cortador = cortador;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public List<Mensaje> getMensajeAllListCon(Users destinatario) throws Exception {
        List<Mensaje> lm = getMensajeAllListEntreAmbosSort(prefilActual.getUsuarioActual(), destinatario);
        if (lm == null) {
            lm = new LinkedList<>();
        }
        return lm;
    }

    public HashMap<String, DialogoChat> getDialogosChat() {
        return dialogosChat;
    }

    public void setDialogosChat(HashMap<String, DialogoChat> dialogosChat) {
        this.dialogosChat = dialogosChat;
    }

    public boolean isDialogoDeLogin() {
        return dialogoDeLogin;
    }

    public void setDialogoDeLogin(boolean dialogoDeLogin) {
        this.dialogoDeLogin = dialogoDeLogin;
    }

    public String getVerContrase() {
        return verContrase;
    }

    public void setVerContrase(String verContrase) {
        this.verContrase = verContrase;
    }

    public static boolean esValidoIdentificacion(String a) {
        return MetodosUtiles.esNatural(a);
    }

    /**
     * si es valido retorna "";
     *
     * @param usuario
     * @return
     */
    public static String esValidoDatosBasicos(Users usuario, boolean validarCuenta) {
        if (validarCuenta && !BD.esCuentaValida(usuario.getUsername())) {
            return "la cuenta no cumple con los requisitos";
        }
        if (!MetodosUtiles.esValidoPrimerNombre(usuario.getApellido1())) {
            return "el primer apellido no cumple con los requisitos";
        }
        if (!MetodosUtiles.esValidoPrimerNombre(usuario.getApellido2())) {
            return "el segundo apellido no cumple con los requisitos";
        }
        if (!MetodosUtiles.esValidoPrimerNombre(usuario.getNombre())) {
            return "el nombre no cumple con los requisitos";
        }
        String validarEm = MetodosUtiles.validarEmail(usuario.getEmail());
        if (!validarEm.isEmpty()) {
            return "el email es erroneo: " + validarEm;
//            return "";
        }
        if (!EA.esValidoIdentificacion(usuario.getIdentificacion())) {
            return "la identificacion no cumple con los requisitos";
        }
        return "";
    }

    public boolean isDialogoAgregarDeGestion() {
        return dialogoAgregarDeGestion;
    }

    public void setDialogoAgregarDeGestion(boolean dialogoAgregarDeGestion) {
        this.dialogoAgregarDeGestion = dialogoAgregarDeGestion;
    }

    public String getIdDLGEditar() {
        return idDLGEditar;
    }

    public void setIdDLGEditar(String idDLGEditar) {
        this.idDLGEditar = idDLGEditar;
    }

    public String getIdUsuarioAEditar() {
        return idUsuarioAEditar;
    }

    public void setIdUsuarioAEditar(String idUsuarioAEditar) {
        this.idUsuarioAEditar = idUsuarioAEditar;
    }

    public String getIdUsuarioInfo() {
        return idUsuarioInfo;
    }

    public void setIdUsuarioInfo(String idUsuarioInfo) {
        this.idUsuarioInfo = idUsuarioInfo;
    }

    public DatosDeBeanPrincipalCliente getDatosDeBeanPrincipalCliente() {
        return DatosDeBeanPrincipalCliente;
    }

    public void setDatosDeBeanPrincipalCliente(DatosDeBeanPrincipalCliente DatosDeBeanPrincipalCliente) {
        this.DatosDeBeanPrincipalCliente = DatosDeBeanPrincipalCliente;
    }

    public List<String> getRoles() {
        return roles;
    }

    @Override
    public LinkedList<String> getNotificaciones() {
        return notificaciones;
    }

    public void setNotificaciones(LinkedList<String> notificaciones) {
        this.notificaciones = notificaciones;
    }

    public void scrollToEnd(String idFormDilogContent) {
        String idCom = getIDFormDLG(idFormDilogContent);
        String resAejcutar = " document.getElementById(\"" + idCom + "\").scrollTo(0,document.getElementById(\"" + idCom + "\").\n"
                + "scrollHeight);  ";// alert('se va ejecutar');
//    System.out.println("resAejcutar="+resAejcutar);
        execute(resAejcutar);//alert('hola');
    }

    public String getScrollToEnd(String idFormDilogContent) {
        String idCom = getIDFormDLG(idFormDilogContent);
//System.out.println("getService().getIDFormDLG(idFormDilogContent)="+idCom);
        String resAejcutar = " document.getElementById(\"" + idCom + "\").scrollTo(0,document.getElementById(\"" + idCom + "\").\n"
                + "scrollHeight);  ";
        return resAejcutar;
    }

    public void sortDatosDeUsuariosAdd(String cuenta) {
        getDatosDeBeanPrincipalCliente().getComparadorDeDatosDeUsuario().addCuentaSeguida(cuenta);
        sortDatosDeUsuarios();
    }

    public void sortDatosDeUsuariosRemove(String cuenta) {
        getDatosDeBeanPrincipalCliente().getComparadorDeDatosDeUsuario().removeCuentaSeguida(cuenta);
        sortDatosDeUsuarios();
    }

    public void sortDatosDeUsuarios() {
        getDatosDeBeanPrincipalCliente().sortTodosLosUsuarios();
        getDatosDeBeanPrincipalCliente().sortUsuariosActuales();
//        getService().getDatosDeBeanPrincipalCliente().sort(usuariosActuales);

    }

    @Override
    public String getCuentaActual() {
        if (getPrefilActual() == null) {
            return "";//return "sin cuenta actual";
        }
        return getPrefilActual().getUsuarioActual().getUsername();
    }

//    public ServidorRMI getServidorRMI() {
//        return servidorRMI;
//    }
    public boolean esDialogoAgregarDeServidores() {
        return esDialogoAgregarDeServidores;
    }

    public void setEsDialogoAgregarDeServidores(boolean esDialogoAgregarDeServidores) {
        this.esDialogoAgregarDeServidores = esDialogoAgregarDeServidores;
    }

    public void actualizarDatosPrincipales() throws Exception {
         servidores.inicializarDireccionesIps();
        setDatosDeBeanPrincipalCliente(new DatosDeBeanPrincipalCliente("beanPrincipal3", servidores.getDatosDeBeanPrincipal(getPrefilActual().getUsuarioActual().getUsername())));
       
    }

}
