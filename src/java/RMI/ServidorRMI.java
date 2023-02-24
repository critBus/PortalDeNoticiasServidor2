/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RMI;

import Util.BDPersitence;
import Utiles.ClasesUtiles.Servidores.Serializable.ProcesoServidorRMI;
//import static Utiles.JSF.FacesBean.responderException;
import comunicacion.ComunicacionCliente2;
import comunicacion.ComunicacionServidor2;
import java.rmi.RemoteException;

/**
 *
 * @author Rene
 */
public class ServidorRMI {

    public static ProcesoServidorRMI<BDPersitence> procesoServidor;
    private  byte[] img;
public ServidorRMI iniciarServidor() throws Exception {
    return iniciarServidor(null);
}
    public ServidorRMI iniciarServidor(byte[] img) throws Exception {
this.img=img;
        procesoServidor = new ProcesoServidorRMI<>(
                () -> {
//                    try {
                        return new BDPersitence("localhost", ComunicacionCliente2.PUERTO, ComunicacionCliente2.NOMBRE_OBJETO,this.img);
//                    } catch (RemoteException ex) {
//                        ex.printStackTrace();
//                    }
//                    return null;
                }, ComunicacionServidor2.class, ComunicacionCliente2.PUERTO, ComunicacionCliente2.NOMBRE_OBJETO
        );
//procesoServidor=new ProcesoServidorRMI(() -> new BDPersistence( ), IComunicacionBD.class,EAc.PUERTO_SERVIDOR, EAc.NOMBRE_OBJETO_SERVIDOR);

        return this;
    }

    public ServidorRMI detenerServidor() throws Exception {

        procesoServidor.stop();
        return this;

    }
}
