/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util.ClasesDeApoyo;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Rene
 */
public class DatosDeComentario implements Comparable<DatosDeComentario>, Serializable{
 private String cuenta,contenido,cuentaAutorPublicacion;
 private Date fecha;
 private int idComentario,idPublicacion;
 
 
 
    public DatosDeComentario( String cuenta, String contenido, Date fecha,int idComentario,int idPublicacion ,String cuentaAutorPublicacion) {
//        this.direccionImagen = direccionImagen; 
        this.cuenta = cuenta;
        this.contenido = contenido;
        this.fecha = fecha;
        this.idComentario=idComentario;
        this.idPublicacion=idPublicacion;
        this.cuentaAutorPublicacion=cuentaAutorPublicacion;
    }

    public int getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(int idComentario) {
        this.idComentario = idComentario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

   
//    public String getDireccionImagen() {
//        return direccionImagen;
//    }
//
//    public void setDireccionImagen(String direccionImagen) {
//        this.direccionImagen = direccionImagen;
//    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    @Override
    public int compareTo(DatosDeComentario o) {
      return  fecha.compareTo(o.fecha);
    }

    public int getIdPublicacion() {
        return idPublicacion;
    }

    public void setIdPublicacion(int idPublicacion) {
        this.idPublicacion = idPublicacion;
    }

    public String getCuentaAutorPublicacion() {
        return cuentaAutorPublicacion;
    }

    public void setCuentaAutorPublicacion(String cuentaAutorPublicacion) {
        this.cuentaAutorPublicacion = cuentaAutorPublicacion;
    }
 
         
}
