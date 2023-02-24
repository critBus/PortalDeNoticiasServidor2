/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.ClasesDeApoyo;

import entity.Users;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Rene
 */
public class UsersSeleccionable implements Serializable{

    Users users;

    public boolean selected = false;

    public UsersSeleccionable() {
    }

    public UsersSeleccionable(Users users) {
        this.users = users;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public static LinkedList<UsersSeleccionable> getLista(List<Users> l) {
        LinkedList<UsersSeleccionable> lista = new LinkedList<>();
        for (int i = 0; i < l.size(); i++) {
            lista.add(new UsersSeleccionable(l.get(i)));
        }
        return lista;
    }
}
