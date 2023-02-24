/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util.ClasesDeApoyo.Estadisticas;

//import Util.BDPersistence;
import Utiles.ClasesUtiles.Interfases.Funcionales.Creador;
import Utiles.ClasesUtiles.Interfases.Funcionales.Utilizar;
import Utiles.ClasesUtiles.Interfases.Funcionales.Utilizar2;
import Utiles.ClasesUtiles.Interfases.Funcionales.Utilizar3;
import Utiles.MetodosUtiles.Arreglos;
import Utiles.MetodosUtiles.Operaciones;
import Utiles.MetodosUtiles.Tempus;
import entity.Publicacion;
import entity.Subcripcion;
import entity.Tema;
import entity.Temapublicacion;
import entity.Users;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javafx.util.Callback;

/**
 *
 * @author Rene
 */
public class EstadisticasDeSubcripcion implements Serializable{

    private int[] subcricionesPrimeraSemana, subcricionesSegundaSemana, subcricionesTerceraSemana;
    private String nombresDias[];
//    private int cantidadDeLikes[];
//    private String labelLikes[];
//    private double[] evaluacionPrimeraSemana, evaluacionSegundaSemana, evaluacionTerceraSemana;
//    private String labelEvaluaciones[];

    public EstadisticasDeSubcripcion(int[] subcricionesPrimeraSemana, int[] subcricionesSegundaSemana, int[] subcricionesTerceraSemana, String[] nombresDias) {
        this.subcricionesPrimeraSemana = subcricionesPrimeraSemana;
        this.subcricionesSegundaSemana = subcricionesSegundaSemana;
        this.subcricionesTerceraSemana = subcricionesTerceraSemana;
        this.nombresDias = nombresDias;
    }
    
    
    
////    
////    public EstadisticasDeSubcripcion(Users u) {
////        iniDatosSemanas();
////        Date hoy = new Date(), principioPrimera = Tempus.restarDias(hoy, 7), principioSegunda = Tempus.restarDias(principioPrimera, 7), principioTercera = Tempus.restarDias(principioSegunda, 7);
////        int diaPrimero = principioPrimera.getDay();
////        nombresDias = new String[7];
////        labelEvaluaciones = new String[7];
////        for (int i = 0; i < 7; i++) {
////           // nombresDias[i] = Tempus.getNombreDiaDate(diaPrimero + i);
////            nombresDias[i] = Tempus.getTaim(Tempus.restarDias(hoy, i), "yyyy-MM-dd");
////            labelEvaluaciones[i]= Tempus.getNombreDiaDate(diaPrimero + i).substring(0, 3);
////        }
////        Utilizar2<Date, int[]> aumentarSub = (date, arreglo) -> {
////            arreglo[(diaPrimero + date.getDay()) % 7]++;
////        };
////
////        for (Iterator<Subcripcion> iterator = u.getSubcripcionCollection().iterator(); iterator.hasNext();) {
////            Subcripcion next = iterator.next();
////            Date fecha = next.getFecha();
////            if (Tempus.esMayorQue(fecha, principioPrimera)) {
////                aumentarSub.utilizar(fecha, subcricionesPrimeraSemana);
////                continue;
////            }
////            if (Tempus.esMayorQue(fecha, principioSegunda)) {
////                aumentarSub.utilizar(fecha, subcricionesSegundaSemana);
////                continue;
////            }
////            if (Tempus.esMayorQue(fecha, principioTercera)) {
////                aumentarSub.utilizar(fecha, subcricionesTerceraSemana);
////
////            }
////        }
////
////        final LinkedList<Publicacion>[] publicacionesDeLaPrimeraSemana=Arreglos.arregloObjectA(new LinkedList<>(), 7),
////                publicacionesDeLaSegundaSemana=Arreglos.arregloObjectA(new LinkedList<>(), 7),
////                publicacionesDeLaTerceraSemana=Arreglos.arregloObjectA(new LinkedList<>(), 7);
////        
////        
////        
////         Utilizar3<Date, LinkedList<Publicacion>[],Publicacion> addEva = (date, arreglo,publ) -> {
////             
////            arreglo[(diaPrimero + date.getDay()) % 7].add(publ);
////        };
////        
////        HashMap<Integer,Integer> hm=new HashMap<>();
////        for (Iterator<Publicacion> iterator = u.getPublicacionCollection().iterator(); iterator.hasNext();) {
////            Publicacion pu = iterator.next();
////            int likes=BDPersistence.getCantidadDeLikesYDislikes(pu)[0];
////            for (Iterator<Temapublicacion> iterator1 = pu.getTemapublicacionCollection().iterator(); iterator1.hasNext();) {
////                Temapublicacion next = iterator1.next();
////                int key=next.getTemaid().getId();
////                if(hm.containsKey(key)){
////                hm.put(key, hm.get(key)+likes);
////                    continue;
////                }
////                hm.put(key, likes);
////            }
////            
////            Date fecha = pu.getFecha();
////            if (Tempus.esMayorQue(fecha, principioPrimera)) {
////                addEva.utilizar(fecha, publicacionesDeLaPrimeraSemana,pu);
////                continue;
////            }
////            if (Tempus.esMayorQue(fecha, principioSegunda)) {
////                addEva.utilizar(fecha, publicacionesDeLaSegundaSemana,pu);
////                continue;
////            }
////            if (Tempus.esMayorQue(fecha, principioTercera)) {
////                addEva.utilizar(fecha, publicacionesDeLaTerceraSemana,pu);
////
////            }
////            
////        }
//////        int idsTemasMayores[]=Arreglos.arregloFill(-1, 5);
////        LinkedList<Integer> idsTemasMayores=new LinkedList<>(Arrays.asList(-1,-1,-1,-1,-1));
////        for (Iterator<Integer> iterator = hm.keySet().iterator(); iterator.hasNext();) {
////            Integer next = iterator.next();
////            int cant=hm.get(next);
////            for (int i = 0; i < idsTemasMayores.size(); i++) {
////                if(idsTemasMayores.get(i)==-1||cant>hm.get(idsTemasMayores.get(i))){
////                idsTemasMayores.add(i,next);
////                idsTemasMayores.removeLast();
////                break;
////                }
////            }
////        }
////        LinkedList<Integer> canti=new LinkedList<>();
////        for (int i = 0; i < idsTemasMayores.size(); i++) {
////            if(idsTemasMayores.get(i)==-1){
////            break;
////            }
////            canti.add(hm.get(idsTemasMayores.get(i)));
////        }
////        cantidadDeLikes=new int[canti.size()];
////        labelLikes=new String[canti.size()];
////        for (int i = 0; i < canti.size(); i++) {
////            cantidadDeLikes[i]=canti.get(i);
////            Tema t=BDPersistence.getTema(idsTemasMayores.get(i));
////            labelLikes[i]=t.getTipodetemaid().getNombre()+" "+t.getNombre();
////        }
////        
////        evaluacionPrimeraSemana=Arreglos.arregloFill(0.0, 7);
////        evaluacionSegundaSemana=Arreglos.arregloFill(0.0, 7);
////        evaluacionTerceraSemana=Arreglos.arregloFill(0.0, 7);
////        
////        Callback<LinkedList<Publicacion>,Double> getPromedio=v->{
////        double total = 0;
////       for (Publicacion a : v) {
////            total += BDPersistence.getPuntacionGeneralYCantidadDeOpiniones(a.getId())[0];
////        }
////        double promedio= total / v.size();
////        return promedio;
////        };
////        
////        for (int i = 0; i < evaluacionPrimeraSemana.length; i++) {
//////            Operaciones.promedio(
//////                    new LinkedList<Publicacion>()
//////                    //publicacionesDeLaPrimeraSemana[i]
//////                    , v->{
////////                BDPersistence.getPuntacionGeneralYCantidadDeOpiniones(v.getId())[0]
//////                    return 2;
//////                    });
////// double total = 0;
//////       for (Publicacion a : publicacionesDeLaPrimeraSemana[i]) {
//////            total += BDPersistence.getPuntacionGeneralYCantidadDeOpiniones(a.getId())[0];
//////        }
//////        double promedio= total / publicacionesDeLaPrimeraSemana[i].size();
//////            evaluacionPrimeraSemana[i]=promedio;
////evaluacionPrimeraSemana[i]=getPromedio.call(publicacionesDeLaPrimeraSemana[i]);
////        }
////         for (int i = 0; i < evaluacionSegundaSemana.length; i++) {
////
////evaluacionSegundaSemana[i]=getPromedio.call(publicacionesDeLaSegundaSemana[i]);
////        }
////          for (int i = 0; i < evaluacionTerceraSemana.length; i++) {
////
////evaluacionTerceraSemana[i]=getPromedio.call(publicacionesDeLaTerceraSemana[i]);
////        }
////          
//////        int resto=0;
//////        for (Iterator<Integer> iterator = hm.keySet().iterator(); iterator.hasNext();) {
//////            Integer next = iterator.next();
//////            if(!idsTemasMayores.contains(next)){
//////            int cant=hm.get(next);
//////            resto+=cant;
//////            }
//////        }
//////        for (int i = 0; i < idsTemasMayores.length; i++) {
//////            
//////        }
////        
////    }

//    public int[] getCantidadDeLikes() {
//        return cantidadDeLikes;
//    }
//
//    public void setCantidadDeLikes(int[] cantidadDeLikes) {
//        this.cantidadDeLikes = cantidadDeLikes;
//    }
//
//    public String[] getLabelLikes() {
//        return labelLikes;
//    }
//
//    public void setLabelLikes(String[] labelLikes) {
//        this.labelLikes = labelLikes;
//    }

//    private void iniDatosSemanas() {
//        Creador<int[]> c = () -> Arreglos.arregloFill(0, 7);
//        subcricionesPrimeraSemana = c.crear();
//        subcricionesSegundaSemana = c.crear();
//        subcricionesTerceraSemana = c.crear();
//    }

    public int[] getSubcricionesPrimeraSemana() {
        return subcricionesPrimeraSemana;
    }

    public void setSubcricionesPrimeraSemana(int[] subcricionesPrimeraSemana) {
        this.subcricionesPrimeraSemana = subcricionesPrimeraSemana;
    }

    public int[] getSubcricionesSegundaSemana() {
        return subcricionesSegundaSemana;
    }

    public void setSubcricionesSegundaSemana(int[] subcricionesSegundaSemana) {
        this.subcricionesSegundaSemana = subcricionesSegundaSemana;
    }

    public int[] getSubcricionesTerceraSemana() {
        return subcricionesTerceraSemana;
    }

    public void setSubcricionesTerceraSemana(int[] subcricionesTerceraSemana) {
        this.subcricionesTerceraSemana = subcricionesTerceraSemana;
    }

    public String[] getNombresDias() {
        return nombresDias;
    }

    public void setNombresDias(String[] nombresDias) {
        this.nombresDias = nombresDias;
    }

    public int[][] crearArregloValores() {
        int valores[][] = new int[7][];
        switch (cantAIni()) {
            case 1:
                for (int i = 0; i < 7; i++) {
                    valores[i]=new int[1];
                    valores[i][0] = subcricionesPrimeraSemana[i];
                   
//                     valores[0]=new int[7];
//                    valores[1]=new int[7];
//                    valores[2]=new int[7];
//                    
//                    valores[0][i] = subcricionesPrimeraSemana[i];
                }
//        return new int[][]{subcricionesPrimeraSemana};
            case 2:
                for (int i = 0; i < 7; i++) {
                    valores[i]=new int[2];
                    valores[i][0] = subcricionesPrimeraSemana[i];
                    valores[i][1] = subcricionesSegundaSemana[i];
                    
//                     valores[0]=new int[7];
//                    valores[1]=new int[7];
//                    
//                    
//                    valores[0][i] = subcricionesPrimeraSemana[i];
//                    valores[1][i] = subcricionesSegundaSemana[i];
                }
//        return new int[][]{subcricionesPrimeraSemana,subcricionesSegundaSemana};
            case 3:
                for (int i = 0; i < 7; i++) {
                    valores[i]=new int[3];
                    valores[i][0] = subcricionesPrimeraSemana[i];
                    valores[i][1] = subcricionesSegundaSemana[i];
                    valores[i][2] = subcricionesTerceraSemana[i];
//                    
//                    
//                    valores[0][i] = subcricionesPrimeraSemana[i];
//                    valores[1][i] = subcricionesSegundaSemana[i];
//                    valores[2][i] = subcricionesTerceraSemana[i];
                }
//        return new int[][]{subcricionesPrimeraSemana,subcricionesSegundaSemana,subcricionesTerceraSemana};

        }
        return valores;
        //return new int[][]{subcricionesPrimeraSemana,subcricionesSegundaSemana,subcricionesTerceraSemana};
    }

    public String[] getLabels() {
        String labels[] = {"Primera", "Segunda", "Tercera"};
        switch (cantAIni()) {
            case 1:
                return new String[]{labels[0]};
            case 2:
                return new String[]{labels[0], labels[1]};
            case 3:
                return new String[]{labels[0], labels[1], labels[2]};

        }
        return null;
    }

    private int cantAIni() {
        if (!estaVacio(subcricionesTerceraSemana)) {
            return 3;
        }
        if (!estaVacio(subcricionesSegundaSemana)) {
            return 2;
        }
        return 1;
    }

    private boolean estaVacio(int[] A) {
        for (int i : A) {
            if (i > 0) {
                return false;
            }
        }
        return true;

    }
}
