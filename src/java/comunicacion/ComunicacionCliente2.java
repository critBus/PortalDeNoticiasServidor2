/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comunicacion;

import Utiles.ClasesUtiles.Interfases.Servidores.DatosRemote;

/**
 *
 * @author Rene
 */
public interface ComunicacionCliente2 extends DatosRemote {
     public static String NOMBRE_SERVIDOR="localhost",NOMBRE_OBJETO="servidor";//"192.168.43.8"
   public static int PUERTO=8888;
    public String getCuentaActual() throws Exception;
    public String enviarMensaje(String mensaje) throws Exception;
    
    public void envioMensaje(String cuenta) throws Exception;
    public void vioLosMensajes(String cuenta) throws Exception;
    public void estaCuentaFueEliminada() throws Exception;
    public void seEliminaronCuentas(String ... cuentasEliminadas)throws Exception;
    public void seAgregoUnaCuenta(String cuenta)throws Exception;
   //*****************************888888
    public String llamarCliente(String a) throws Exception;
}
