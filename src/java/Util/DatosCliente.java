/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import comunicacion.ComunicacionCliente2;
import Utiles.ClasesUtiles.Servidores.Serializable.ObjetoRemoteClienteImp;
import Utiles.MetodosUtiles.ServidorUtil;
import java.rmi.RemoteException;
//import javax.faces.context.FacesContext;
//import javax.faces.view.facelets.FaceletContext;
//import org.primefaces.PrimeFaces;


/**
 *
 * @author Rene
 */
public class DatosCliente extends ObjetoRemoteClienteImp implements ComunicacionCliente2{

    private IEstadoActual estadoActual; 

    public DatosCliente(IEstadoActual estadoActual,String host, int port, String nombreObjetoRemoto) throws Exception {
        super(host, port, nombreObjetoRemoto);
        this.estadoActual=estadoActual;
    }
    
    @Override
    public String getCuentaActual() throws Exception {
//        if(estadoActual.getPrefilActual()==null){
//        return "";//return "sin cuenta actual";
//        }
//       return estadoActual.getPrefilActual().getUsuarioActual().getUsername();
return estadoActual.getCuentaActual();
    }

    @Override
    public boolean estaConectado() throws Exception {
        estadoActual.getCuentaActual();
//        estadoActual.getPrefilActual();
        return super.estaConectado(); //To change body of generated methods, choose Tools | Templates.
    }
     public String enviarMensaje(String mensaje) throws Exception{


     return "mesaje recibido";
     }
    
    @Override
    public void envioMensaje(String cuenta) throws Exception{
        System.out.println("recibiendo mensaje +++++++++++++++++++++++++++++");
        estadoActual.getNotificaciones().add("Mensaje de ("+cuenta+")");
    }
    
    public void vioLosMensajes(String cuenta) throws Exception{
        System.out.println("recibiendo ver los mensajes +++++++++++++++++++++++");
        estadoActual.getNotificaciones().add("Vio los mensajes ("+cuenta+")");
    }
    //String cuentasEliminadas eliminoCuentas
    public void estaCuentaFueEliminada(){
        System.out.println("recibiendo aviso de cuenta eliminada ++++++++++++++++++++++++");
    estadoActual.getNotificaciones().add("Esta cuenta fue eliminada");
    
    }
    
    public void seEliminaronCuentas(String ... cuentasEliminadas){
          System.out.println("recibiendo aviso de cuentas eliminadas -- ++++++++++++++++++++++++");
        for (int i = 0; i < cuentasEliminadas.length; i++) {
            estadoActual.getNotificaciones().add("Cuenta eliminada ("+cuentasEliminadas[i]+")");
        }
    }
    
    public void seAgregoUnaCuenta(String cuenta){
      System.out.println("recibiendo aviso de una cuenta agregada  ++++++++++++++++++++++++");
       estadoActual.getNotificaciones().add("Cuenta agreagada ("+cuenta+")");
    }

    @Override
        public String llamarCliente(String a) throws Exception {
            System.out.println("se llama del lado del cliente");
            return "respuesta del cliente"+a;
        }
}
