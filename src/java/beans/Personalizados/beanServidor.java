/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.Personalizados;

import RMI.ServidorRMI;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import Utiles.JSF.FacesUtil;
import Utiles.MetodosUtiles.Archivo;
import java.io.File;
import javax.annotation.PostConstruct;

/**
 *
 * @author Rene
 */
@ManagedBean(name = "beanServidor3", eager = true)
@ApplicationScoped
public class beanServidor {

    private ServidorRMI servidorRMI;

    @PostConstruct
    public void init() {
        try {
            if (servidorRMI == null) {
              iniciarServidor();
            }
        } catch (Exception ex) {
            // responderException(ex);
            ex.printStackTrace();
        }
    }

    public void iniciarServidor() {
         if (servidorRMI == null) {
        try {
            //  System.out.println("FacesUtil.getRealPath()=" + FacesUtil.getRealPath());
            File fimg = new File(FacesUtil.getRealPath() + "resources\\img\\u9.png");
            //    System.out.println("file imagen=" + fimg + "  " + fimg.exists());
            byte im[] = Archivo.getBytesDeImg(fimg);
            //  System.out.println("im=" + im);

            servidorRMI = new ServidorRMI();
            servidorRMI.iniciarServidor(im);
        } catch (Exception ex) {
            // responderException(ex);
            ex.printStackTrace();
        }
         }
    }

    public void detenerServidor() {
        try {
            servidorRMI.detenerServidor();
            //procesoServidor.stop();
        } catch (Exception ex) {
            // responderException(ex);
            ex.printStackTrace();
        }

    }

    public ServidorRMI getServidorRMI() {
        return servidorRMI;
    }

}
