/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import Utiles.ClasesUtiles.Interfases.Funcionales.Creador;
import Utiles.ClasesUtiles.Servidores.Serializable.ProcesoServidorRMI;
import Utiles.MetodosUtiles.Tempus;
import comunicacion.ComunicacionServidor2;
import entity.Clasificacion;
import entity.Comentario;
import entity.Mensaje;
import entity.Publicacion;
import entity.Subcripcion;
import entity.Users;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javafx.util.Callback;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author Rene
 */
public class DefaultBD {

    static String temas[][][] = {{{"Tecnologia"}, {"Robotica", "Informatica", "Medicina", "Fisica"}},
    {{"Politica"}, {"Nacinales", "Internacionales"}},
    {{"Cine"}, {"Series", "Peliculas", "Animados", "Cortos"}},
    {{"Cultura"}, {"Eventos Artisticos", "Exposiciones", "Obras de Teatro"}},
    {{"Musica"}, {"Extrenos", "Discos", "Conciertos"}}

    };
    static int lengt;

    static {
        lengt = 0;
        for (int i = 0; i < temas.length; i++) {
            lengt += temas[i][1].length;
        }
    }

    public static void crearTemasDafault(ComunicacionServidor2 servidor) throws Exception {
        System.out.println("creando temas default...");
        for (int i = 0; i < temas.length; i++) {
            for (int j = 0; j < temas[i][1].length; j++) {
                if (!servidor.existeTema(temas[i][0][0], temas[i][1][j])) {
                    
                    servidor.addTema(temas[i][0][0], temas[i][1][j]);
                }
            }

        }
        System.out.println("se crearon los temas default");
    }
    
    public static void crearConfiguracionInicialYClearBD(String nombreServidor)throws Exception{
        ProcesoServidorRMI<BDPersitence> procesoServidor=new ProcesoServidorRMI(() -> new BDPersitence("localhost",8889,"ServidorRMI" ), ComunicacionServidor2.class,8889, "ServidorRMI");
         ComunicacionServidor2 servidor=procesoServidor.getServidor();
         servidor.clearBD();
         System.out.println("se clearBD");
         crearConfiguracionInicial(servidor,nombreServidor);
         System.out.println("se inicio y va detener");
         procesoServidor.stop();
         System.out.println("se detuvo el servidor");
    }
//
    private static void crearConfiguracionInicial() throws Exception {
         ProcesoServidorRMI<BDPersitence> procesoServidor=new ProcesoServidorRMI(() -> new BDPersitence("localhost",8889,"ServidorRMI" ), ComunicacionServidor2.class,8889, "ServidorRMI");
         ComunicacionServidor2 servidor=procesoServidor.getServidor();
         crearConfiguracionInicial(servidor,"");
         procesoServidor.stop();
    }
    private static String[] temaCorrespondiente(int indice) {

        int l = (indice + 1) % lengt;

        for (int i = 0; i < temas.length; i++) {
            if (l < temas[i][1].length) {
                return new String[]{temas[i][0][0], temas[i][1][l]};
            }
            l -= temas[i][1].length;

        }

        return null;
//return temas[(i+1)%temas.length];
    }
    private static void crearConfiguracionInicial(ComunicacionServidor2 servidor,String nombreServidor) throws Exception {
       
        for (int i = 0; i < temas.length; i++) {
            for (int j = 0; j < temas[i][1].length; j++) {
                if (!servidor.existeTema(temas[i][0][0],temas[i][1][j])) {
                    System.out.println("se creo");
                    servidor.addTema(temas[i][0][0],temas[i][1][j]);
                }
            }

        }
//        crearTemas();
        final Date hoy=new Date();
        final Random r=new Random();
       final Creador<Date> getFechaA=()->Tempus.restarDias(hoy,r.nextInt(21) );
        Callback<Date,Date> getFecha=v->{
           Date fecha=null;  do{fecha=getFechaA.crear();}while(v!=null&&Tempus.esMenorQue(fecha, v));
        return fecha;
        };
        String us[] = {"uno", "dos", "tres", "asd","cuatro","cinco","seis","siete","ocho","nueve"};
        for (int i = 0; i < us.length; i++) {
            us[i]+=nombreServidor;
        }
        
        for (int i = 0; i < us.length; i++) {
            if (!servidor.existeUsuario(us[i])) {
                agregarUsers(servidor,us[i], i);
                 
                for (int j = 0; j < us.length; j++) {
                    String tem[]=temaCorrespondiente(j);
                    System.out.println(Arrays.toString(tem));
                    boolean existe=servidor.existeTema(tem[0],tem[1]);
                    if(!existe){
                    throw  new Exception("no existe");
                    }
                 Publicacion p=   servidor.addPublicacion(us[i],tem[0],tem[1] , us[i] + " excribe " + us[j], "De " + us[i] + " " + j);
                    p.setFecha(getFechaA.crear());
                    servidor.updatePublicacion(p);
                }
            }
           

        }
        String men[]={"como estas?","respondeme!!","como estas tu?","oye!","yo bien y tu?","que haces","cuando nos vemos"," que bien","no me ignores","acaba de ablar","ok","dale"};
         String com[]={"escribe otro","no sirve","es una mierda","vaya porqueria","estaba bueno","estoy deacuerdo","esfuersate"," retpite otro","no estoy deacuerdo","me cuadra","esta bien","mejora la redaccion"};
        int iMen=0,iCom=0;
        for (int i = 0; i < us.length; i++) {
            Users actual=servidor.getUsuario(us[i]);
            for (int j = 0; j < us.length; j++) {
                if(i!=j){
                Mensaje m= servidor.addMensaje(us[i], us[j],men[(iMen++%men.length)] );
                 m.setFecha(getFechaA.crear());
                 servidor.updateMensaje(m);
                Users autor=servidor.getUsuario(us[j]);
                List<Publicacion> pl=servidor.getPublicacionAllList(autor);
                int can=r.nextInt(8);
                    for (int k = 0; k < pl.size()&&k<can; k++) {
                        Clasificacion c=servidor.getNewClasificacion(actual, pl.get(k));
                        c.setClasificacion(r.nextInt(10));
                        int lik=r.nextInt(3);
                        c.setLegusta(lik==0?null:lik==1);
                        servidor.addClasificacion(c);
                        int cantComk=r.nextInt(3);
                        for (int l = 1; l < cantComk; l++) {
                        Comentario co=    servidor.addComentario(actual, pl.get(k), com[iCom++%com.length]);
                        co.setFecha(getFecha.call(pl.get(k).getFecha()));
                        servidor.updateComentario(co);
                        }
                    }
                    if(r.nextBoolean()){
                    Subcripcion s= servidor.addSubcripcion(autor, actual);
                    s.setFecha(getFechaA.crear());
                    servidor.updateSubcripcion(s);
                    }
                
                }
                
            }
            
        }
        System.out.println("termino de crear la configuracion inicial");
    }
//
    private static Users agregarUsers(ComunicacionServidor2 servidor,String cuenta, int i) throws Exception {
        return agregarUsers(servidor,cuenta, cuenta + "Nom", i % 2 == 0);
    }

    private static Users agregarUsers(ComunicacionServidor2 servidor,String cuenta, String nombre, boolean admin) throws Exception {

        Users u = servidor.addUsuario(servidor.getNewUser(cuenta, "12314", nombre, "ape1", "ap2", "eam", encriptar("12345678"), true));
        servidor.setAuthorities(u, admin);
        return servidor.getUsuario(cuenta);
    }
    private static String encriptar(String contraseña) {
        
        return DigestUtils.sha1Hex(contraseña);
    }
}
