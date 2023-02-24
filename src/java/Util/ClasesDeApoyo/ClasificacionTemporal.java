/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util.ClasesDeApoyo;

//import Util.BDPersistence;
//import beans.Superclases.FacesBDbean;
//import Comunicacion.IComunicacionBD;
import RMI.ConexionServidores;
import comunicacion.ComunicacionServidor2;
import entity.Clasificacion;
import entity.Publicacion;
import entity.Users;
import java.io.Serializable;
import java.util.Arrays;

/**
 *
 * @author Rene
 */
public class ClasificacionTemporal implements Serializable {

    private String cuenta;
    private int idPublicacion;
    private boolean like, dislike;
    private int clasificacionPersonal, clasificacionGeneral, cantidadDeLikes, cantidadDeDislike, cantidadDeOpiniones;

    private String cuentaAutorPublicacion;
//    private IComunicacionBD servidor;

    public ClasificacionTemporal(ConexionServidores servidor, String cuenta, Publicacion p, Clasificacion c) throws Exception {
//      this.servidor=servidor;
        int clasificacionGeneral[] = calcularPuntuacionGeneral(servidor, p);//buscar despues
        int cal[] = calcularCantidadDeLikesYDislikes(servidor, p);
        if (c == null) {
            inicializar(cuenta, p.getUsersusername().getUsername(), p.getId(), false, false, 0, clasificacionGeneral[0], cal[0], cal[1], clasificacionGeneral[1]);
        } else {
            boolean eslikeNull = c.getLegusta() == null;
            inicializar(cuenta, p.getUsersusername().getUsername(), p.getId(), eslikeNull ? false : c.getLegusta(), eslikeNull ? false : !c.getLegusta(), c.getClasificacion() == null ? 0 : c.getClasificacion(), clasificacionGeneral[0], cal[0], cal[1], clasificacionGeneral[1]);
        }

    }

    private void inicializar(String cuenta, String cuentaAutorPublicacion, int idPublicacion, boolean like, boolean dislike, int clasificacionPersonal, int clasificacionGeneral, int cantidadDeLikes, int cantidadDeDislike, int cantidadDeOpiniones) {
        this.cuenta = cuenta;
        this.idPublicacion = idPublicacion;
        this.like = like;
        this.dislike = dislike;
        this.clasificacionPersonal = clasificacionPersonal;
        this.clasificacionGeneral = clasificacionGeneral;
        this.cantidadDeLikes = cantidadDeLikes;
        this.cantidadDeDislike = cantidadDeDislike;
        this.cantidadDeOpiniones = cantidadDeOpiniones;
        this.cuentaAutorPublicacion = cuentaAutorPublicacion;
    }

    public int[] calcularPuntuacionGeneral(ConexionServidores servidor) throws Exception {
        return calcularPuntuacionGeneral(servidor, servidor.getPublicacion(cuentaAutorPublicacion, idPublicacion));
    }

//    public int[] calcularPuntuacionGeneral(ConexionServidores servidor,int idPu) throws Exception {
//        return servidor.getPuntacionGeneralYCantidadDeOpiniones(idPu);
//    }
    public int[] calcularPuntuacionGeneral(ConexionServidores servidor, Publicacion p) throws Exception {
        if (p == null) {
            return new int[]{0, 0};
        }
        int[] res = servidor.getPuntacionGeneralYCantidadDeOpiniones(p);
        return res != null ? res : new int[]{0, 0};
    }

//    public int[] calcularCantidadDeLikesYDislikes(ComunicacionServidor2 servidor, int idPu) throws Exception {
//        int res[] = servidor.getCantidadDeLikesYDislikes(idPu);
//        return res;
//    }
    public int[] calcularCantidadDeLikesYDislikes(ConexionServidores servidor, Publicacion p) throws Exception {
        if (p == null) {
            return new int[]{0, 0};
        }
        int res[] = servidor.getCantidadDeLikesYDislikes(p);
        return res != null ? res : new int[]{0, 0};
    }

    public int[] calcularCantidadDeLikesYDislikes(ConexionServidores servidor) throws Exception {

        return calcularCantidadDeLikesYDislikes(servidor, servidor.getPublicacion(cuentaAutorPublicacion, idPublicacion));
    }

    public void actualizarCantidadDeLikesYDislikes(ConexionServidores servidor) throws Exception {
        int cal[] = calcularCantidadDeLikesYDislikes(servidor);
        cantidadDeLikes = cal[0];
        cantidadDeDislike = cal[1];
    }

    public void actualizarPuntuacionGeneral(ConexionServidores servidor) throws Exception {
        int cal[] = calcularPuntuacionGeneral(servidor);
        clasificacionGeneral = cal[0];
        cantidadDeOpiniones = cal[1];
    }

//public Clasificacion ponerClasificacionPersonal(int puntuacion) throws Exception{
//  Clasificacion c = getClasificacionYCrearSiNoExiste();
//  c.setClasificacion(puntuacion);
//  c= BDPersistence.updateClasificacion(c);
// actualizarPuntuacionGeneral();
//   return c;
//}
    public Clasificacion ponerClasificacionPersonal(ConexionServidores servidor) throws Exception {
        Clasificacion c = getClasificacionYCrearSiNoExiste(servidor);
        if (c == null) {
            return null;
        }
//  c.setClasificacion(puntuacion);
        c = servidor.updateClasificacion(c);
        if (c == null) {
            return null;
        }
        actualizarPuntuacionGeneral(servidor);
        return c;
    }

    public Clasificacion apretoClasificacionPersonal(ConexionServidores servidor) throws Exception {
        return ponerClasificacionPersonal(servidor);
    }

    public Clasificacion apretoLike(ConexionServidores servidor) throws Exception {
        return ponerLike(servidor, like ? like : null);
    }

    public Clasificacion apretoDislike(ConexionServidores servidor) throws Exception {
        return ponerLike(servidor, dislike ? false : null);
    }

    public Clasificacion ponerLike(ConexionServidores servidor, Boolean b) throws Exception {
        Clasificacion c = getClasificacionYCrearSiNoExiste(servidor);
        if (c == null) {
            return null;
        }
        //System.out.println("b=" + b);
        if (b != null) {
            dislike = !(like = b);
        } else {
            like = dislike = false;
        }
        //System.out.println("like=" + like + " dislike=" + dislike);
        c.setLegusta(b);

        c = servidor.updateClasificacion(c);
        actualizarCantidadDeLikesYDislikes(servidor);

        actualizarPuntuacionGeneral(servidor);

        return c;
    }

    public Clasificacion getClasificacionYCrearSiNoExiste(ConexionServidores servidor) throws Exception {

//        System.out.println("cuenta="+cuenta);
//        System.out.println("idPublicacion="+idPublicacion);
        Users u = servidor.getUsuario(cuenta);
        if (u == null) {
            return null;
        }
        Publicacion p = servidor.getPublicacion(cuentaAutorPublicacion, idPublicacion);
        if (p == null) {
            return null;
        }
        Clasificacion c = servidor.getClasificacion(u, p);

        if (c == null) {
            c = servidor.getNewClasificacion(u, p);
            if (c == null) {
                return null;
            }
            c.setClasificacion(clasificacionPersonal);
            c = servidor.addClasificacion(c);
            if (c == null) {
                return null;
            }
        } else {
            c.setClasificacion(clasificacionPersonal);
        }
        return c;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public boolean isDislike() {
        return dislike;
    }

    public void setDislike(boolean dislike) {
        this.dislike = dislike;
    }

    public String getCuenta() {
        return cuenta;
    }

    public int getClasificacionPersonal() {
        return clasificacionPersonal;
    }

    public int getCantidadDeOpiniones() {
        return cantidadDeOpiniones;
    }

    public void setCantidadDeOpiniones(int cantidadDeOpiniones) {
        this.cantidadDeOpiniones = cantidadDeOpiniones;
    }

    public void setClasificacionPersonal(int clasificacionPersonal) {
        this.clasificacionPersonal = clasificacionPersonal;
    }

    public int getClasificacionGeneral() {
        return clasificacionGeneral;
    }

    public void setClasificacionGeneral(int clasificacionGeneral) {
        this.clasificacionGeneral = clasificacionGeneral;
    }

    public int getIdPublicacion() {
        return idPublicacion;
    }

    public int getCantidadDeLikes() {
        return cantidadDeLikes;
    }

    public void setCantidadDeLikes(int cantidadDeLikes) {
        this.cantidadDeLikes = cantidadDeLikes;
    }

    public int getCantidadDeDislike() {
        return cantidadDeDislike;
    }

    public void setCantidadDeDislike(int cantidadDeDislike) {
        this.cantidadDeDislike = cantidadDeDislike;
    }

}
