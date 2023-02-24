/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.Personalizados;

//import Util.BDPersistence;
import Utiles.ClasesUtiles.Interfases.Funcionales.Creador;
import Utiles.MetodosUtiles.Arreglos;
import Utiles.MetodosUtiles.Tempus;
import Util.ClasesDeApoyo.Estadisticas.EstadisticasDeUsuario;
import beans.Superclases.FacesBDbean;
import beans.Superclases.aplicacionBean;
import entity.Subcripcion;
import entity.Users;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Callback;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author Rene
 */
@ManagedBean(name = "beanInicio3")
@ViewScoped
public class beanInicio extends aplicacionBean {
//direccionDeImagen=
    //resetear datos Principal ***************************************************************
    /**
     * Creates a new instance of beanInicio
     */
    public beanInicio() {
    }

//    private PieChartModel pieModel1;
//    private LineChartModel lineModel1;
//    private LineChartModel lineModel2;
//    private LineChartModel animatedModel1;
//    private BarChartModel animatedModel2;

    private EstadisticasDeUsuario estadisticasDeUsuario;

    @Override
    public void ini() {
//        System.out.println("inicia incio ****************************8");
        try {
            super.ini(); //To change body of generated methods, choose Tools | Templates.
            if(getService().isSalioDelLogin()){ 
                getService().setSalioDelLogin(false);
            getService().procesoLogin();
            
            }
//System.out.println("FacesBDbean.servidor="+FacesBDbean.servidor);
//            System.out.println("getService()="+getService());
//            System.out.println("getService().getPrefilActual()="+getService().getPrefilActual());
//            System.out.println("getService().getPrefilActual().getUsuarioActual()="+getService().getPrefilActual().getUsuarioActual());
//            System.out.println("getService().getPrefilActual().getUsuarioActual().getUsername()="+getService().getPrefilActual().getUsuarioActual().getUsername());
//            
estadisticasDeUsuario =FacesBDbean.servidores.getEstadisticasDeUsuarioInterno(getService().getPrefilActual().getUsuarioActual().getUsername());
        } catch (Exception ex) {
            responderException(ex);
        }
    }

    public String getSignoEstadisticaAumentoPublicaciones() {
        return estadisticasDeUsuario.esPositivoElPorsentageDeAumentoDePublicaciones() ? "+" : "";
    }

    public String getColorEstadisticaAumentoPublicaciones() {
        return getColorEstadistica(estadisticasDeUsuario.esPositivoElPorsentageDeAumentoDePublicaciones());
    }

    public String getSignoEstadisticaAumentoDesubcritores() {
        return estadisticasDeUsuario.esPositivoElPorsentageDeAumentoDeSubcriptores() ? "+" : "";
    }

    public String getColorEstadisticaAumentoDesubcritores() {
        return getColorEstadistica(estadisticasDeUsuario.esPositivoElPorsentageDeAumentoDeSubcriptores());
    }

    public String getSignoEstadisticaAumentoLikes() {
        return estadisticasDeUsuario.esPositivoElPorsentageDeAumentoDeLikes() ? "+" : "";
    }

    public String getColorEstadisticaAumentoDeLikes() {
        return getColorEstadistica(estadisticasDeUsuario.esPositivoElPorsentageDeAumentoDeLikes());
    }

    private String getColorEstadistica(boolean aumenta) {
        return aumenta ? "background-color: #5f76e8 !important;" : "background-color: #ff4f70 !important;";
    }

    public EstadisticasDeUsuario getEstadisticasDeUsuario() {
        return estadisticasDeUsuario;
    }

    public void setEstadisticasDeUsuario(EstadisticasDeUsuario estadisticasDeUsuario) {
        this.estadisticasDeUsuario = estadisticasDeUsuario;
    }

    public String getJSGraficoLineas() {
       String[] yLabels = estadisticasDeUsuario.getEstadisticasDeSubcripcion().getNombresDias();
//         System.out.println("yLabels="+Arrays.toString(yLabels));
        int valores[][] = estadisticasDeUsuario.getEstadisticasDeSubcripcion().crearArregloValores();
////         Arreglos.MostrarMatriz(valores);
        String labels[] = estadisticasDeUsuario.getEstadisticasDeSubcripcion().getLabels();
//        System.out.println("labels="+Arrays.toString(labels));
        return getGraficoLineas_Morris_Area(yLabels, valores, labels);
    }

    public String getJSGraficoBarras() {
        String labels[] = estadisticasDeUsuario.getEstadisticasEvaluacion().getLabelEvaluaciones();
        double valores[][] = estadisticasDeUsuario.getEstadisticasEvaluacion().crearArregloValores();
        return getGraficoChartist_Bar(labels, valores);
    }

    public String getJSGraficoDonut() {
        String labels[] = estadisticasDeUsuario.getEstadisticasDeLikes().getLabelLikes();
        int valores[] = estadisticasDeUsuario.getEstadisticasDeLikes().getCantidadDeLikes();
        return getGraficoDonut_C3(labels, valores);
    }

    public static String getGraficoChartist_Bar(String labelsX[], double valores[][]) {
        String res = "var data = {\n"
                + "        labels: [";
        for (int i = 0; i < labelsX.length; i++) {
            res += (i != 0 ? " , " : "") + "'" + labelsX[i] + "'";
        }
        res += "],\n"
                + "        series: [\n";
        for (int i = 0; i < valores.length; i++) {
            res += (i != 0 ? " , " : "") + "[";
            for (int j = 0; j < labelsX.length; j++) {
                res += (j != 0 ? " , " : "") + "'" + valores[i][j] + "'";
            }
            res += "]";
        }

        res += "]\n"
                + "    };\n"
                + "\n"
                + "    var options = {\n"
                + "        axisX: {\n"
                + "            showGrid: false\n"
                + "        },\n"
                + "        seriesBarDistance: 5,\n"
                + "        chartPadding: {\n"
                + "            top: 15,\n"
                + "            right: 15,\n"
                + "            bottom: 5,\n"
                + "            left: 0\n"
                + "        },\n"
                + "        plugins: [\n"
                + "            Chartist.plugins.tooltip()\n"
                + "        ],\n"
                + "        width: '100%'\n"
                + "    };\n"
                + "\n"
                + "    var responsiveOptions = [\n"
                + "        ['screen and (max-width: 640px)', {\n"
                + "            seriesBarDistance: 5,\n"
                + "            axisX: {\n"
                + "                labelInterpolationFnc: function (value) {\n"
                + "                    return value[0];\n"
                + "                }\n"
                + "            }\n"
                + "        }]\n"
                + "    ];\n"
                + "    new Chartist.Bar('.net-income', data, options, responsiveOptions);";
//        System.out.println(res);
        return res;
    }

    public static String getGraficoDonut_C3(String labels[], int valores[]) {
        String res = "var chart1 = c3.generate({\n"
                + "        bindto: '#campaign-v2',\n"
                + "        data: {\n"
                + "            columns: [";
        for (int i = 0; i < labels.length; i++) {
            res += (i != 0 ? " , " : "") + "['" + labels[i] + "'," + valores[i] + "]";
        }
        res += " ],\n"
                + "\n"
                + "            type: 'donut',\n"
                + "            tooltip: {\n"
                + "                show: true\n"
                + "            }\n"
                + "        },\n"
                + "        donut: {\n"
                + "            label: {\n"
                + "                show: false\n"
                + "            },\n"
                + "            title: 'Likes',\n"
                + "            width: 18\n"
                + "        },\n"
                + "\n"
                + "        legend: {\n"
                + "            hide: false\n"
                + "        },\n"
                + "        color: {\n"
                + "            pattern: [";
        String colors[] = {"red", "#5f76e8", "#ff4f70", "#01caf1", "#edf2f6"};
        for (int i = 0; i < labels.length; i++) {
            res += (i != 0 ? " , " : "") + "'" + colors[i] + "'";
        }
        res += " ]\n"
                + "        }\n"
                + "    });\n"
                + "\n"
                + "    d3.select('#campaign-v2 .c3-chart-arcs-title').style('font-family', 'Rubik');";
//         System.out.println("res="+res);
        return res;
    }

    public static String getGraficoLineas_Morris_Area(String[] yLabels, int valores[][], String[] labels) {
      String res = "Morris.Area({\n"
                + "        element: 'morris-area-chart',\n"
                + "        data: [\n";
        for (int i = 0; i < yLabels.length; i++) {
            res += (i != 0 ? " , " : "") + "{ period:'" + yLabels[i] + "'";
            for (int j = 0; j < valores[i].length; j++) {
                res += " , " + " a" + j + " : " + valores[i][j] + " ";
            }
           res += " }\n";
        }
        res += "],\n"
                + "        xkey: 'period',\n"
                + "        ykeys: [";
        for (int i = 0; i < labels.length; i++) {
            res += (i != 0 ? " , " : " ") + " '" + "a" + i + "' ";
        }
        res += "],\n"
                + "        labels: [";
        for (int i = 0; i < labels.length; i++) {
            res += (i != 0 ? " , " : "") + "'" + labels[i] + "'";
        }
        res += " ],\npointSize: 3,\n"
                + "        fillOpacity: 0,\n"
                + "        pointStrokeColors:[";
        String colores[] = {"#5f76e8", "#01caf1", "red"};
        for (int i = 0; i < labels.length; i++) {
            res += (i != 0 ? " , " : "") + "'" + colores[i] + "'";
        }
        res += "],\n"
                + "        behaveLikeLine: true,\n"
                + "        gridLineColor: '#e0e0e0',\n"
                + "        lineWidth: 3,\n"
                + "        hideHover: 'auto',\n"
                + "        lineColors: [";
        for (int i = 0; i < labels.length; i++) {
            res += (i != 0 ? " , " : "") + "'" + colores[i] + "'";
        }
        res += "],\n"
                + "        resize: true\n"
                + "        \n"
                + "    });";
//        System.out.println("res="+res);
        return res;
    }

}
