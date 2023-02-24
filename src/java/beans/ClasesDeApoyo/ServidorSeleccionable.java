/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.ClasesDeApoyo;
import entity.Servidores;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
/**
 *
 * @author Rene
 */
public class ServidorSeleccionable {
    private Servidores servidor;
    public boolean selected = false;

    public ServidorSeleccionable(Servidores servidor) {
        this.servidor = servidor;
    }

    public ServidorSeleccionable() {
    }

    public Servidores getServidor() {
        return servidor;
    }

    public void setServidor(Servidores servidor) {
        this.servidor = servidor;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
     public static LinkedList<ServidorSeleccionable> getLista(List<Servidores> ls) {
        LinkedList<ServidorSeleccionable> lss = new LinkedList<>();
        for (Iterator<Servidores> iterator = ls.iterator(); iterator.hasNext();) {
            Servidores next = iterator.next();
            lss.add(new ServidorSeleccionable(next));
        }
        return lss;
    }
}
