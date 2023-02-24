/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.Personalizados;

import Util.ClasesDeApoyo.DatosDeBeanPrincipalCliente;
import Util.EAc;
import static Utiles.JSF.FacesBean.hideDialog;
import static Utiles.JSF.FacesBean.mensajeINFO;
import static Utiles.JSF.FacesBean.responderException;
import static Utiles.JSF.FacesBean.showDialog;
import beans.ClasesDeApoyo.ServidorSeleccionable;
import beans.ClasesDeApoyo.UsersSeleccionable;
import beans.Superclases.FacesBDbean;
import static beans.Superclases.FacesBDbean.getUsuarioAllListInterno;
import static beans.Superclases.FacesBDbean.servidores;
import beans.Superclases.aplicacionBean;
import entity.Servidores;
//import entity.Users;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
//import javax.faces.event.ActionListener;
import javax.faces.view.ViewScoped;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author Rene
 */
@ManagedBean(name = "beanGestionDeServidores3")
@ViewScoped
public class beanGestionDeServidores extends aplicacionBean   { //implements ActionListener
    
    private LinkedList<ServidorSeleccionable> lista;
    private Servidores servidorEnLista, servidorTemporal;
    
    @Override
    public void ini() {
        super.ini(); //To change body of generated methods, choose Tools | Templates.
        try {
            super.ini(); //To change body of generated methods, choose Tools | Templates.
//        System.out.println("ini *******************************");
            lista = ServidorSeleccionable.getLista(FacesBDbean.servidores.getServidoresAllListInterno());
            
        } catch (Exception ex) {
            responderException(ex);
        }
    }
    
    public void resetear() {
        super.ini(); //To change body of generated methods, choose Tools | Templates.
        try {
            super.ini(); //To change body of generated methods, choose Tools | Templates.
//        System.out.println("ini *******************************");
            lista = ServidorSeleccionable.getLista(FacesBDbean.servidores.getServidoresAllListInterno());
            servidorTemporal = FacesBDbean.servidores.getNewServidoresInterno();
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public void actualizar(RowEditEvent e) {
        try {
            Servidores t = (Servidores) e.getObject();
            FacesBDbean.servidores.updateServidorInterno(t);
            for (int i = 0; i < lista.size(); i++) {
                Servidores s = lista.get(i).getServidor();
                if ((int)s.getId() == (int)t.getId()) {
                    s.setIp(t.getIp());
                    break;
                }
            }
            mensajeINFO("Se  Modifico");
        } catch (Exception ex) {
            responderException(ex);
        }
    }
     public void cancelar(RowEditEvent row) {

    }
     public void eliminar(Servidores u) {
        try {
            
            for (int i = 0; i < lista.size(); i++) {
                if ((int)lista.get(i).getServidor().getId()==(int)u.getId()) {
                    FacesBDbean.servidores.eliminarServidorInterno(lista.remove(i).getServidor());
                    getService().actualizarDatosPrincipales();
                    mensajeINFO("Se elimino");
                    break;

                }
            }
            
            

        } catch (Exception ex) {
            responderException(ex);
        }
    }
     
     
     
     public void eliminar() {
        try {
            boolean elimino = false;
            LinkedList<Integer> idsAEliminar=new LinkedList<>();
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).isSelected()) {
                    elimino = true;
                    idsAEliminar.add(lista.remove(i--).getServidor().getId());
                }
            }
            if (elimino) {
                for (int i = 0; i < idsAEliminar.size(); i++) {
                    int id=idsAEliminar.get(i);
                    FacesBDbean.servidores.eliminarServidorInterno(id);
                }
                
               getService().actualizarDatosPrincipales();
                mensajeINFO("Se eliminaron");
            } else {
                mensajeINFO("No hay seleccionados");
            }

        } catch (Exception ex) {
            System.out.println("errrrrrrrrrrrrrrrrrrrrrrrrrorrrrrrrrr");
            responderException(ex);
        }
      
    }
     
     
     public void showDialogAdd() {
        showDialog(EAc.DLG_AGREGAR_SERVIDOR);
    }

    public void hideDialogAdd() {
        hideDialog(EAc.DLG_AGREGAR_SERVIDOR);
    }
    public LinkedList<ServidorSeleccionable> getLista() {
        return lista;
    }
    
    public void setLista(LinkedList<ServidorSeleccionable> lista) {
        this.lista = lista;
    }
    
    public Servidores getServidorEnLista() {
        return servidorEnLista;
    }
    
    public void setServidorEnLista(Servidores servidorEnLista) {
        this.servidorEnLista = servidorEnLista;
    }
    
    public Servidores getServidorTemporal() {
        return servidorTemporal;
    }
    
    public void setServidorTemporal(Servidores servidorTemporal) {
        this.servidorTemporal = servidorTemporal;
    }
    
   public void setEsDialogoAgregarDeServidoresTrue() {
        getService().setEsDialogoAgregarDeServidores(true);
    }
// @Override
//    public void processAction(ActionEvent ae) throws AbortProcessingException {
//        try {
//            System.out.println("Se llamo!!!!!!!!!!");
//           
//            List<Servidores> ls=FacesBDbean.servidores.getServidoresAllListInterno();
//            For1:
//            for (int i = 0; i < ls.size(); i++) {
//                for (int j = 0; j < lista.size(); j++) {
//                    if((int)lista.get(i).getServidor().getId()==(int)ls.get(i).getId()){
//                    continue For1;
//                    }
//                }
//                System.out.println("se agrego "+ls.get(i));
//                lista.addLast(new ServidorSeleccionable(ls.get(i)));
//                break;
//            }
//            
//            
//            //lista = ServidorSeleccionable.getLista();
//        } catch (Exception ex) {
//            responderException(ex);
//        }
//    }
}
