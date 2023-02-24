/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util.ClasesDeApoyo.Estadisticas;

//import Util.BDPersistence;
import RMI.ConexionServidores;
import comunicacion.ComunicacionServidor2;
import Utiles.ClasesUtiles.Interfases.Funcionales.Creador;
import Utiles.ClasesUtiles.Interfases.Funcionales.Creador2;
import Utiles.ClasesUtiles.Interfases.Funcionales.Creador3;
import Utiles.ClasesUtiles.Interfases.Funcionales.Utilizar2;
import Utiles.ClasesUtiles.Interfases.Funcionales.Utilizar3;
import Utiles.MetodosUtiles.Arreglos;
import Utiles.MetodosUtiles.MetodosUtiles;
import Utiles.MetodosUtiles.Tempus;
//import beans.Superclases.FacesBDbean;
//import beans.Superclases.aplicacionBean;
import entity.Clasificacion;
import entity.Publicacion;
import entity.Subcripcion;
import entity.Tema;
import entity.Temapublicacion;
import entity.Users;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Callback;

/**
 *
 * @author Rene
 */
public class EstadisticasDeUsuario implements Serializable {

    private double porsentageDeAumentoDeSubcriptores;
    private int cantidadDeSubcritores;
    private int cantidadDeLikesDeEstaSemana;
    private double porsentageDeAumentoDeLikes;
    private int cantidadDePublicacionesDeEstaSemana;
    private int cantidadDePublicaciones;
    private double porsentageDeAumentoPublicaciones;
    private EstadisticasDeSubcripcion estadisticasDeSubcripcion;
    private EstadisticasDeLikes estadisticasDeLikes;
    private EstadisticasEvaluacion estadisticasEvaluacion;

    public EstadisticasDeUsuario(ConexionServidores servidor, Users u) throws Exception {

        Date hoy = new Date(), principioPrimera = Tempus.restarDias(hoy, 7), principioSegunda = Tempus.restarDias(principioPrimera, 7), principioTercera = Tempus.restarDias(principioSegunda, 7);
        int diaPrimero = principioPrimera.getDay();

        Creador<int[]> getArregloInt0_7 = () -> Arreglos.arregloFill(0, 7);
        Creador<double[]> getArregloDou0_7 = () -> Arreglos.arregloFill(0.0, 7);
        Creador<LinkedList<Publicacion>[]> getArregloLinkedPubl0_7 = () -> {
            LinkedList<Publicacion>[] arr = Arreglos.arregloObjectA(new LinkedList<>(), 7);
            for (int i = 0; i < arr.length; i++) {
                arr[i] = new LinkedList<>();
            }
            return arr;
        };
        final Creador2<Double, Double, Double> getPorcentage = (primera, segunda) -> {
            if (primera <= 0) {

                return segunda == 0 ? new Double(0) : new Double(-100);
//                return segunda * -100;
            }
            if (segunda <= 0) {
//                System.out.println("segunda == 0 resul primera="+primera);
                return primera * 100;
            }
            return MetodosUtiles.redondearADouble(MetodosUtiles.redondearADouble((primera / segunda), 2) * 100, 2);
        };
        Creador3<Double, Double, Double, Double> getPorcentageTresSemanas = (primera, segunda, tercera) -> {
            double a = getPorcentage.crear(primera, segunda);
//            System.out.println("a="+a);
            double b = getPorcentage.crear(segunda, tercera);
//            System.out.println("b="+b);
            double c = getPorcentage.crear(a, b);
//            System.out.println("c="+c);
            return c;
        };
        final Callback<Date, Integer> getIndiceDiaCorrespondiente = date -> (diaPrimero + date.getDay()) % 7;
        Callback<LinkedList<Publicacion>, Double> getPromedio = v -> {
            double total = 0;
            for (Publicacion a : v) {
                try {
                    int res[] = servidor.getPuntacionGeneralYCantidadDeOpiniones(a);
                    if (res == null) {
                        res = new int[]{0, 0};
                    }
                    total += res[0];
                } catch (Exception ex) {
                   ex.printStackTrace();
                }
            }
            double promedio = total == 0 || v.size() == 0 ? 0 : MetodosUtiles.redondearADouble((total / v.size()), 2);//total / v.size()
            return promedio;
        };

        Utilizar2<Date, int[]> aumentarSub = (date, arreglo) -> {
            arreglo[getIndiceDiaCorrespondiente.call(date)]++;
        };
        Utilizar3<Date, LinkedList<Publicacion>[], Publicacion> addEva = (date, arreglo, publ) -> {
          arreglo[getIndiceDiaCorrespondiente.call(date)].add(publ);
        };

        int[] subcricionesPrimeraSemana = getArregloInt0_7.crear(),
                subcricionesSegundaSemana = getArregloInt0_7.crear(),
                subcricionesTerceraSemana = getArregloInt0_7.crear();

        double[] evaluacionPrimeraSemana = getArregloDou0_7.crear(),
                evaluacionSegundaSemana = getArregloDou0_7.crear(),
                evaluacionTerceraSemana = getArregloDou0_7.crear();

        final LinkedList<Publicacion>[] publicacionesDeLaPrimeraSemana = getArregloLinkedPubl0_7.crear(),
                publicacionesDeLaSegundaSemana = getArregloLinkedPubl0_7.crear(),
                publicacionesDeLaTerceraSemana = getArregloLinkedPubl0_7.crear();

        String[] nombresDias = new String[7],
                labelEvaluaciones = new String[7];

        for (int i = 0; i < 7; i++) {
            nombresDias[i] = Tempus.getTaim(Tempus.restarDias(hoy, i), "yyyy-MM-dd");
            labelEvaluaciones[i] = Tempus.getNombreDiaDate(diaPrimero + i).substring(0, 3);
        }

        List<Subcripcion> subcripcionesDeSubcriptores = servidor.getSubcripcionDeSubscriptoresAllList(u);

        for (Iterator<Subcripcion> iterator = subcripcionesDeSubcriptores.iterator(); iterator.hasNext();) {
            // for (Iterator<Subcripcion> iterator = u.getSubcripcionCollection().iterator(); iterator.hasNext();) {
            Subcripcion next = iterator.next();
            Date fecha = next.getFecha();
            if (Tempus.esMayorQue(fecha, principioPrimera)) {
                aumentarSub.utilizar(fecha, subcricionesPrimeraSemana);
                continue;
            }
            if (Tempus.esMayorQue(fecha, principioSegunda)) {
                aumentarSub.utilizar(fecha, subcricionesSegundaSemana);
                continue;
            }
            if (Tempus.esMayorQue(fecha, principioTercera)) {
                aumentarSub.utilizar(fecha, subcricionesTerceraSemana);

            }
        }
        double primera = 0, segunda = 0, tercera = 0, primeraP = 0, segundaP = 0, terceraP = 0;
        HashMap<Integer, Integer> hm = new HashMap<>();
//        Collection<Publicacion> publ=u.getPublicacionCollection();
        Collection<Publicacion> publ = servidor.getPublicacionAllList(u);
        for (Iterator<Publicacion> iterator = publ.iterator(); iterator.hasNext();) {
            Publicacion pu = iterator.next();
            int likes = servidor.getCantidadDeLikesYDislikes(pu)[0];
            List<Temapublicacion> ltp = servidor.getTemaPublicacionAll(pu);
            for (Iterator<Temapublicacion> iterator1 = ltp.iterator(); iterator1.hasNext();) {
                Temapublicacion next = iterator1.next();
                int key = next.getTemaid().getId();
                if (hm.containsKey(key)) {
                    hm.put(key, hm.get(key) + likes);
                    continue;
                }
                hm.put(key, likes);
            }

            Date fecha = pu.getFecha();
            if (Tempus.esMayorQue(fecha, principioPrimera)) {
                addEva.utilizar(fecha, publicacionesDeLaPrimeraSemana, pu);
//                System.out.println("likes="+likes);
                primera += likes;
//                System.out.println("primera="+primera);
                primeraP++;
                continue;
            }
            if (Tempus.esMayorQue(fecha, principioSegunda)) {
                addEva.utilizar(fecha, publicacionesDeLaSegundaSemana, pu);
                segunda += likes;
                segundaP++;
                continue;
            }
            if (Tempus.esMayorQue(fecha, principioTercera)) {
                addEva.utilizar(fecha, publicacionesDeLaTerceraSemana, pu);
                tercera += likes;
                terceraP++;
            }

        }
//        System.out.println("primera="+primera);
//        System.out.println("segunda="+segunda);
//        System.out.println("tercera="+tercera);
        porsentageDeAumentoDeLikes = getPorcentageTresSemanas.crear(primera, segunda, tercera);
//        System.out.println("porsentageDeAumentoDeLikes="+porsentageDeAumentoDeLikes);
        cantidadDeLikesDeEstaSemana = (int) primera;
        porsentageDeAumentoPublicaciones = getPorcentageTresSemanas.crear(primeraP, segundaP, terceraP);
        cantidadDePublicacionesDeEstaSemana = (int) primeraP;
        cantidadDePublicaciones = publ.size();
//        int idsTemasMayores[]=Arreglos.arregloFill(-1, 5);
        LinkedList<Integer> idsTemasMayores = new LinkedList<>(Arrays.asList(-1, -1, -1, -1, -1));
        for (Iterator<Integer> iterator = hm.keySet().iterator(); iterator.hasNext();) {
            Integer next = iterator.next();
            int cant = hm.get(next);
            for (int i = 0; i < idsTemasMayores.size(); i++) {
                if (idsTemasMayores.get(i) == -1 || cant > hm.get(idsTemasMayores.get(i))) {
                    idsTemasMayores.add(i, next);
                    idsTemasMayores.removeLast();
                    break;
                }
            }
        }
        LinkedList<Integer> canti = new LinkedList<>();
        for (int i = 0; i < idsTemasMayores.size(); i++) {
            if (idsTemasMayores.get(i) == -1) {
                break;
            }
            canti.add(hm.get(idsTemasMayores.get(i)));
        }
        int cantidadDeLikes[] = new int[canti.size()];
        String[] labelLikes = new String[canti.size()];
        for (int i = 0; i < canti.size(); i++) {
            cantidadDeLikes[i] = canti.get(i);
            Tema t = servidor.getTema(idsTemasMayores.get(i));
            labelLikes[i] = t.getTipodetemaid().getNombre() + " " + t.getNombre();
        }

        for (int i = 0; i < evaluacionPrimeraSemana.length; i++) {
            evaluacionPrimeraSemana[i] = getPromedio.call(publicacionesDeLaPrimeraSemana[i]);
            evaluacionSegundaSemana[i] = getPromedio.call(publicacionesDeLaSegundaSemana[i]);
            evaluacionTerceraSemana[i] = getPromedio.call(publicacionesDeLaTerceraSemana[i]);
        }
        estadisticasDeSubcripcion = new EstadisticasDeSubcripcion(subcricionesPrimeraSemana, subcricionesSegundaSemana, subcricionesTerceraSemana, nombresDias);
        estadisticasDeLikes = new EstadisticasDeLikes(cantidadDeLikes, labelLikes);
        estadisticasEvaluacion = new EstadisticasEvaluacion(evaluacionPrimeraSemana, evaluacionSegundaSemana, evaluacionTerceraSemana, labelEvaluaciones);

        cantidadDeSubcritores = subcripcionesDeSubcriptores.size();
      primera = segunda = tercera = 0;
        for (int i = 0; i < subcricionesPrimeraSemana.length; i++) {
            primera += subcricionesPrimeraSemana[i];
            segunda += subcricionesSegundaSemana[i];
            tercera += subcricionesTerceraSemana[i];
        }
        porsentageDeAumentoDeSubcriptores = getPorcentageTresSemanas.crear(primera, segunda, tercera);

    }

    public boolean esPositivoElPorsentageDeAumentoDePublicaciones() {
        return porsentageDeAumentoPublicaciones > 0;
    }

    public boolean esPositivoElPorsentageDeAumentoDeSubcriptores() {
        return porsentageDeAumentoDeSubcriptores > 0;
    }

    public boolean esPositivoElPorsentageDeAumentoDeLikes() {
        return porsentageDeAumentoDeLikes > 0;
    }

    public double getPorsentageDeAumentoDeSubcriptores() {
        return porsentageDeAumentoDeSubcriptores;
    }

    public void setPorsentageDeAumentoDeSubcriptores(double porsentageDeAumentoDeSubcriptores) {
        this.porsentageDeAumentoDeSubcriptores = porsentageDeAumentoDeSubcriptores;
    }

    public int getCantidadDeSubcritores() {
        return cantidadDeSubcritores;
    }

    public void setCantidadDeSubcritores(int cantidadDeSubcritores) {
        this.cantidadDeSubcritores = cantidadDeSubcritores;
    }

    public int getCantidadDeLikesDeEstaSemana() {
        return cantidadDeLikesDeEstaSemana;
    }

    public void setCantidadDeLikesDeEstaSemana(int cantidadDeLikesDeEstaSemana) {
        this.cantidadDeLikesDeEstaSemana = cantidadDeLikesDeEstaSemana;
    }

    public double getPorsentageDeAumentoDeLikes() {
        return porsentageDeAumentoDeLikes;
    }

    public void setPorsentageDeAumentoDeLikes(double porsentageDeAumentoDeLikes) {
        this.porsentageDeAumentoDeLikes = porsentageDeAumentoDeLikes;
    }

    public int getCantidadDePublicacionesDeEstaSemana() {
        return cantidadDePublicacionesDeEstaSemana;
    }

    public void setCantidadDePublicacionesDeEstaSemana(int cantidadDePublicacionesDeEstaSemana) {
        this.cantidadDePublicacionesDeEstaSemana = cantidadDePublicacionesDeEstaSemana;
    }

    public int getCantidadDePublicaciones() {
        return cantidadDePublicaciones;
    }

    public void setCantidadDePublicaciones(int cantidadDePublicaciones) {
        this.cantidadDePublicaciones = cantidadDePublicaciones;
    }

    public double getPorsentageDeAumentoPublicaciones() {
        return porsentageDeAumentoPublicaciones;
    }

    public void setPorsentageDeAumentoPublicaciones(double porsentageDeAumentoPublicaciones) {
        this.porsentageDeAumentoPublicaciones = porsentageDeAumentoPublicaciones;
    }

    public EstadisticasDeSubcripcion getEstadisticasDeSubcripcion() {
        return estadisticasDeSubcripcion;
    }

    public void setEstadisticasDeSubcripcion(EstadisticasDeSubcripcion estadisticasDeSubcripcion) {
        this.estadisticasDeSubcripcion = estadisticasDeSubcripcion;
    }

    public EstadisticasDeLikes getEstadisticasDeLikes() {
        return estadisticasDeLikes;
    }

    public void setEstadisticasDeLikes(EstadisticasDeLikes estadisticasDeLikes) {
        this.estadisticasDeLikes = estadisticasDeLikes;
    }

    public EstadisticasEvaluacion getEstadisticasEvaluacion() {
        return estadisticasEvaluacion;
    }

    public void setEstadisticasEvaluacion(EstadisticasEvaluacion estadisticasEvaluacion) {
        this.estadisticasEvaluacion = estadisticasEvaluacion;
    }

}
