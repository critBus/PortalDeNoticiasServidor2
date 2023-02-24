/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import controller.AuthoritiesJpaController;
import controller.*;


import entity.Mensaje;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Rene
 */
public class control {
    static EntityManagerFactory em=Persistence.createEntityManagerFactory("spring_testPU");
    public static UsersJpaController usersJpaController=new UsersJpaController(em);
    public static AuthoritiesJpaController authoritiesJpaController =new AuthoritiesJpaController(em);
    public static PublicacionJpaController publicacionJpaController=new PublicacionJpaController(em);
     public static TemaJpaController temaJpaController=new TemaJpaController(em);
     public static TemapublicacionJpaController temapublicacionJpaController=new TemapublicacionJpaController(em);
     public static TipodetemaJpaController tipodetemaJpaController=new TipodetemaJpaController(em);
     public static ClasificacionJpaController clasificacionJpaController=new ClasificacionJpaController(em);
       public static ComentarioJpaController comentarioJpaController=new ComentarioJpaController(em);
       public static MensajeJpaController mensajeJpaController=new MensajeJpaController(em);
        public static SubcripcionJpaController subcripcionJpaController=new SubcripcionJpaController(em);
         public static ServidoresJpaController servidoresJpaController=new ServidoresJpaController(em);
}
 