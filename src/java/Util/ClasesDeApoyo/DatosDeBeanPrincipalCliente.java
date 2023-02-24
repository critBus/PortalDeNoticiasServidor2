/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util.ClasesDeApoyo;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.primefaces.model.menu.MenuModel;

/**
 *
 * @author Rene
 */
public class DatosDeBeanPrincipalCliente implements Serializable {

    private MenuModel model;

    private LinkedList<DatosDeUsuario> todosLosUsuarios, usuariosActuales;
    private String filtroDeUsuarios;

    private ComparadorDeDatosDeUsuario comparadorDeDatosDeUsuario;

    public DatosDeBeanPrincipalCliente(String nombreDeBeanPrincipal,DatosDeBeanPrincipal d) {
        this(DatosDeBeanPrincipal.crearMenuModel(nombreDeBeanPrincipal,d.getModel()), d.getTodosLosUsuarios(), d.getFiltroDeUsuarios(), d.getComparadorDeDatosDeUsuario());
    }

    public DatosDeBeanPrincipalCliente(MenuModel model, LinkedList<DatosDeUsuario> todosLosUsuarios, String filtroDeUsuarios, ComparadorDeDatosDeUsuario comparadorDeDatosDeUsuario) {
        this.model = model;
        this.todosLosUsuarios = todosLosUsuarios;
        this.filtroDeUsuarios = filtroDeUsuarios;
        this.comparadorDeDatosDeUsuario = comparadorDeDatosDeUsuario;
        usuariosActuales = new LinkedList<>(todosLosUsuarios);
    }

    public MenuModel getModel() {
        return model;
    }

    public void setModel(MenuModel model) {
        this.model = model;
    }

    public LinkedList<DatosDeUsuario> getTodosLosUsuarios() {
        return todosLosUsuarios;
    }

    public void setTodosLosUsuarios(LinkedList<DatosDeUsuario> todosLosUsuarios) {
        this.todosLosUsuarios = todosLosUsuarios;
    }

    public String getFiltroDeUsuarios() {
        return filtroDeUsuarios;
    }

    public void setFiltroDeUsuarios(String filtroDeUsuarios) {
        this.filtroDeUsuarios = filtroDeUsuarios;
    }

    public ComparadorDeDatosDeUsuario getComparadorDeDatosDeUsuario() {
        return comparadorDeDatosDeUsuario;
    }

    public void setComparadorDeDatosDeUsuario(ComparadorDeDatosDeUsuario comparadorDeDatosDeUsuario) {
        this.comparadorDeDatosDeUsuario = comparadorDeDatosDeUsuario;
    }

    public void sort(List<DatosDeUsuario> l) {
//        System.out.println("l="+l);
//        System.out.println("comparadorDeDatosDeUsuario="+comparadorDeDatosDeUsuario);
        Collections.sort(l, comparadorDeDatosDeUsuario);
    }

    public void sortTodosLosUsuarios() {
        sort(todosLosUsuarios);
    }

    public void sortUsuariosActuales() {
        sort(usuariosActuales);
    }

    public void sortUsuarios() {
        sortTodosLosUsuarios();
        sortUsuariosActuales();
    }

    public LinkedList<DatosDeUsuario> getUsuariosActuales() {
        return usuariosActuales;
    }

    public void resetearUsuariosActuales() {
        usuariosActuales = new LinkedList<>();
    }

    public void setUsuariosActuales(LinkedList<DatosDeUsuario> usuariosActuales) {
        this.usuariosActuales = usuariosActuales;
    }

    public boolean esSeguido(String cuenta) {
        return comparadorDeDatosDeUsuario.getCuentasSeguidas().contains(cuenta);
    }

    public void eliminarCuenta(String... cuentas) {
        for (String cuenta : cuentas) {
            for (int i = 0; i < usuariosActuales.size(); i++) {
                if (usuariosActuales.get(i).getCuenta().equals(cuenta)) {
                    usuariosActuales.remove(i);
                }
            }
            for (int i = 0; i < todosLosUsuarios.size(); i++) {
                if (todosLosUsuarios.get(i).getCuenta().equals(cuenta)) {
                    todosLosUsuarios.remove(i);
                }
            }
        }

    }

    public void filtrarUsuariosActuales() {
        if (filtroDeUsuarios!=null&&!filtroDeUsuarios.trim().isEmpty()) {
            resetearUsuariosActuales();
            todosLosUsuarios.forEach(v -> {
                if (v.getCuenta().toLowerCase().contains(filtroDeUsuarios.trim().toLowerCase())) {
                    getUsuariosActuales().add(v);
                }
            });
        }else{
            System.out.println("filtro ="+filtroDeUsuarios);
        usuariosActuales = new LinkedList<>(todosLosUsuarios);
        }
    }

    public void agregarCuentaSolo(String cuenta) {
    todosLosUsuarios.add(new DatosDeUsuario(cuenta, 0,null));
    }
    public void accionDespuesDeAgreagarCuentas(){
    sort(todosLosUsuarios);
        filtrarUsuariosActuales();
    }
    public void agregarCuenta(String cuenta) {
        todosLosUsuarios.add(new DatosDeUsuario(cuenta, 0,null));
        sort(todosLosUsuarios);
        filtrarUsuariosActuales();
    }
}
