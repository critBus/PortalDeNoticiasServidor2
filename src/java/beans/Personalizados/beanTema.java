/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.Personalizados;

import beans.Superclases.aplicacionBean;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

/**
 *
 * @author Rene
 */
@ManagedBean(name = "beanTema3")
@ViewScoped
public class beanTema extends aplicacionBean{
     private String tema;
     @Override
    public void ini() {
       // System.out.println("int bean principal *********************************************************");
        if(getService()!=null&&getService().getTema()!=null&&!getService().getTema().isEmpty()){
        tema = getService().getTema();
        }else{
                
                
       tema = "black-tie";}
    }
    public void ponerTema() {
        //setTema("start");
        // System.out.println("*************** tema="+tema);
        //System.out.println("set tema 2--------------------------" + tema);
        getService().setTema(tema);
    }

    public String getTema() {
        return tema;
    }
    
    public void setTema(String tema) {
        this.tema = tema;
       // System.out.println("set tema --------------------------" + tema);
        getService().setTema(tema);
    }
}
