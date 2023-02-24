/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util.ClasesDeApoyo;

import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 * @author Rene
 */
public class DatosDeMenuModel implements Serializable{
    private LinkedList<DatosDeSubMenu> subMenus; 

    public DatosDeMenuModel() {
        subMenus=new LinkedList<>();
    }

    public LinkedList<DatosDeSubMenu> getSubMenus() {
        return subMenus;
    }

    public void setSubMenus(LinkedList<DatosDeSubMenu> subMenus) {
        this.subMenus = subMenus;
    }
    
    
}
