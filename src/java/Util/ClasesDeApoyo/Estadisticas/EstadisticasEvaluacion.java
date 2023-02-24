/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util.ClasesDeApoyo.Estadisticas;

import java.io.Serializable;

/**
 *
 * @author Rene
 */
public class EstadisticasEvaluacion implements Serializable{
     private double[] evaluacionPrimeraSemana, evaluacionSegundaSemana, evaluacionTerceraSemana;
    private String labelEvaluaciones[];

    public EstadisticasEvaluacion(double[] evaluacionPrimeraSemana, double[] evaluacionSegundaSemana, double[] evaluacionTerceraSemana, String[] labelEvaluaciones) {
        this.evaluacionPrimeraSemana = evaluacionPrimeraSemana;
        this.evaluacionSegundaSemana = evaluacionSegundaSemana;
        this.evaluacionTerceraSemana = evaluacionTerceraSemana;
        this.labelEvaluaciones = labelEvaluaciones;
    }

    public double[] getEvaluacionPrimeraSemana() {
        return evaluacionPrimeraSemana;
    }

    public void setEvaluacionPrimeraSemana(double[] evaluacionPrimeraSemana) {
        this.evaluacionPrimeraSemana = evaluacionPrimeraSemana;
    }

    public double[] getEvaluacionSegundaSemana() {
        return evaluacionSegundaSemana;
    }

    public void setEvaluacionSegundaSemana(double[] evaluacionSegundaSemana) {
        this.evaluacionSegundaSemana = evaluacionSegundaSemana;
    }

    public double[] getEvaluacionTerceraSemana() {
        return evaluacionTerceraSemana;
    }

    public void setEvaluacionTerceraSemana(double[] evaluacionTerceraSemana) {
        this.evaluacionTerceraSemana = evaluacionTerceraSemana;
    }

    public String[] getLabelEvaluaciones() {
        return labelEvaluaciones;
    }

    public void setLabelEvaluaciones(String[] labelEvaluaciones) {
        this.labelEvaluaciones = labelEvaluaciones;
    }
    public double[][] crearArregloValores() {
    switch(cantAIni()){
        case 1:
        return new double[][]{evaluacionPrimeraSemana};
         case 2:
        return new double[][]{evaluacionPrimeraSemana,evaluacionSegundaSemana};
         case 3:
        return new double[][]{evaluacionPrimeraSemana,evaluacionSegundaSemana,evaluacionTerceraSemana};
    }
    return null;
    }
    private int cantAIni() {
        if (!estaVacio(evaluacionTerceraSemana)) {
            return 3;
        }
        if (!estaVacio(evaluacionSegundaSemana)) {
            return 2;
        }
        return 1;
    }

    private boolean estaVacio(double[] A) {
        for (double i : A) {
            if (i > 0) {
                return false;
            }
        }
        return true;

    }
    
}
