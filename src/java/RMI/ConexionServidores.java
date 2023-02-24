/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RMI;

//import EstadoActual.EA;
import Util.ClasesDeApoyo.ClasificacionTemporal;
import Util.ClasesDeApoyo.ComparadorDeMensajes;
import Util.ClasesDeApoyo.ComparadorDePublicaciones;
import Util.ClasesDeApoyo.DatosDeBeanPrincipal;
import Util.ClasesDeApoyo.DatosDeBeanTodasLasPublicaciones;
import Util.ClasesDeApoyo.DatosDeComentario;
import Util.ClasesDeApoyo.DatosDePublicacion;
import Util.ClasesDeApoyo.DialogoChat;
import Util.ClasesDeApoyo.Estadisticas.EstadisticasDeUsuario;
import Util.ClasesDeApoyo.SeleccionDeSubmenu;
import Util.DatosCliente;
import Util.IEstadoActual;
import Utiles.ClasesUtiles.Interfases.Funcionales.Creador;
import Utiles.ClasesUtiles.Interfases.Funcionales.Creador1ConException;
import Utiles.ClasesUtiles.Interfases.Funcionales.CreadorConException;
import Utiles.ClasesUtiles.Interfases.Funcionales.RealizarConException;
import Utiles.ClasesUtiles.Interfases.Funcionales.UtilizarConException;
import Utiles.ClasesUtiles.Servidores.ProcesoCliente;
import Utiles.ClasesUtiles.Servidores.Serializable.ProcesoServidorClienteRMI;
import comunicacion.ComunicacionCliente2;
import comunicacion.ComunicacionServidor2;
import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;
import entity.Clasificacion;
import entity.Comentario;
import entity.Publicacion;
import entity.Tema;
import entity.Tipodetema;
import entity.Users;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javafx.util.Callback;
import entity.Mensaje;
import entity.Servidores;
import entity.Subcripcion;
import entity.Temapublicacion;
import java.util.Collections;
import java.util.Iterator;

/**
 *
 * @author Rene
 */
public class ConexionServidores {

    private ComunicacionServidor2 servidorInterno;
    private ComunicacionCliente2 clienteInterno;
    private String[] ips;
    //private ComunicacionServidor2[] servidoresExternos;
    private LinkedList<ComunicacionServidor2> servidoresExternos;

    public ConexionServidores conectarAServidor(IEstadoActual ea) throws Exception {
        int port = new Random().nextInt(2000) + 4000;
        String nombreObjeto = "cliente" + port;
        CreadorConException<DatosCliente> c = () -> {
            return new DatosCliente(ea, "localhost", port, nombreObjeto);

        };
        ProcesoServidorClienteRMI<DatosCliente, ComunicacionServidor2> p = new ProcesoServidorClienteRMI<>(
                c, ComunicacionCliente2.class, port, nombreObjeto,
                ComunicacionCliente2.NOMBRE_SERVIDOR, ComunicacionCliente2.PUERTO, ComunicacionCliente2.NOMBRE_OBJETO
        );
        servidorInterno = p.getServidor();
        clienteInterno = p.getCliente();
        inicializarDireccionesIps();
        servidoresExternos = new LinkedList<>();
        return this;
    }

    public void inicializarDireccionesIps() throws Exception {
        ips = servidorInterno.getDireccionesIps();
    }

    private void inicializarServidoresExternos() throws Exception {
        LinkedList<ComunicacionServidor2> lse = new LinkedList<>();
        for (int i = 0; i < ips.length; i++) {
            try {
                ProcesoCliente<ComunicacionServidor2> p = new ProcesoCliente(ips[i], ComunicacionCliente2.PUERTO, ComunicacionCliente2.NOMBRE_OBJETO);
                lse.add(p.getServidor());
            } catch (Exception ex) {

            }
        }
        //servidoresExternos = lse.toArray(new ComunicacionServidor2[0]);
        servidoresExternos = lse;
    }

    private <E> E buscarUnResultado(Creador1ConException<ComunicacionServidor2, E> c) throws Exception {
        E interno = c.crear(servidorInterno);
        if (interno != null) {
            return interno;
        }
        for (int i = 0; i < servidoresExternos.size(); i++) {
            if (removerSiNoEstaDisponible(i)) {
                E externo = c.crear(servidoresExternos.get(i));
                //  System.out.println("externo=" + externo);
                if (externo != null) {
                    return externo;
                }
            } else {
                i--;
            }
        }
        return null;
    }

    private <E> E buscarUnResultado_SiExisteUsuario(String username, Creador1ConException<ComunicacionServidor2, E> c) throws Exception {
        if (servidorInterno.existeUsuario(username)) {
            return c.crear(servidorInterno);
        }

        for (int i = 0; i < servidoresExternos.size(); i++) {
            if (removerSiNoEstaDisponible(i)) {
                if (servidoresExternos.get(i).existeUsuario(username)) {
                    return c.crear(servidoresExternos.get(i));
                }

            } else {
                i--;
            }
        }
        return null;
    }

    private void realizar_SiExisteUsuario(String username, UtilizarConException<ComunicacionServidor2> c) throws Exception {
        if (servidorInterno.existeUsuario(username)) {
            c.utilizar(servidorInterno);
            return;
        }

        for (int i = 0; i < servidoresExternos.size(); i++) {
            //  System.out.println("recorre");
            if (removerSiNoEstaDisponible(i)) {
                if (servidoresExternos.get(i).existeUsuario(username)) {
                    c.utilizar(servidoresExternos.get(i));
                    return;
                }

            } else {
                i--;
            }
        }

    }

    private void realizarExternos(UtilizarConException<ComunicacionServidor2> c) throws Exception {
        for (int i = 0; i < servidoresExternos.size(); i++) {
            if (removerSiNoEstaDisponible(i)) {
                c.utilizar(servidoresExternos.get(i));

            } else {
                i--;
            }
        }
    }

    private void realizarTodos(UtilizarConException<ComunicacionServidor2> c) throws Exception {
        c.utilizar(servidorInterno);
        realizarExternos(c);
    }

    private boolean buscarUnResultadoBool(Creador1ConException<ComunicacionServidor2, Boolean> c) throws Exception {
        boolean interno = c.crear(servidorInterno);
        if (interno) {
            return interno;
        }
        for (int i = 0; i < servidoresExternos.size(); i++) {
            if (removerSiNoEstaDisponible(i)) {
                boolean externo = c.crear(servidoresExternos.get(i));
                if (externo) {
                    return externo;
                }
            } else {
                i--;
            }
        }
        return false;
    }

    private boolean buscarUnResultadoBool_SiExisteUsuario(String username, Creador1ConException<ComunicacionServidor2, Boolean> c) throws Exception {
        if (servidorInterno.existeUsuario(username)) {
            return c.crear(servidorInterno);
        }

        for (int i = 0; i < servidoresExternos.size(); i++) {
            if (removerSiNoEstaDisponible(i)) {
                if (servidoresExternos.get(i).existeUsuario(username)) {
                    return c.crear(servidoresExternos.get(i));
                }
            } else {
                i--;
            }
        }
        return false;
    }

    private <E> List<E> buscarListas(Creador1ConException<ComunicacionServidor2, List<E>> c) throws Exception {
        List<E> interno = c.crear(servidorInterno);
        if (interno == null) {
            interno = new LinkedList<>();
        }
        for (int i = 0; i < servidoresExternos.size(); i++) {
            if (removerSiNoEstaDisponible(i)) {
                List<E> externo = c.crear(servidoresExternos.get(i));
                if (externo != null) {
                    interno.addAll(externo);
                }
            } else {
                i--;
            }
        }
        return interno;
    }

    private <E> LinkedList<E> buscarListasLnkL(Creador1ConException<ComunicacionServidor2, List<E>> c) throws Exception {
        List<E> l = buscarListas(c);
        if (l instanceof LinkedList) {
            return (LinkedList) l;
        }
        return new LinkedList<E>(l);
    }

    private boolean removerSiNoEstaDisponible(int i) {
        try {
            return servidoresExternos.get(i).estaDisponible();
        } catch (Exception ex) {
            //ex.printStackTrace();
            servidoresExternos.remove(i);
            return false;
        }

    }

    private <E> E obtener_SiEsValidoUsuario(Users u1, Users u2, CreadorConException<E> c) throws Exception {
        if (u1 != null && u2 != null) {
            return c.crear();
        }
        return null;
    }

    private <E> E obtener_SiEsValidoUsuario(Users u, CreadorConException<E> c) throws Exception {
        if (u != null) {
            return c.crear();
        }
        return null;
    }

    private <E> void realizar_SiEsValidoUsuario(Users u, RealizarConException c) throws Exception {
        if (u != null) {
            c.realizar();
        }

    }

    private boolean obtenerBool_SiEsValidoUsuario(Users u, CreadorConException<Boolean> c) throws Exception {
        if (u != null) {
            return c.crear();
        }
        return false;
    }

    private boolean obtenerBool_SiEsValidoUsuario(Users u1, Users u2, CreadorConException<Boolean> c) throws Exception {
        if (u1 != null && u2 != null) {
            return c.crear();
        }
        return false;
    }

    public Clasificacion getClasificacion(Users u, final Publicacion p) throws Exception {
        //return obtener_SiExisteUsuario(u, ()->{});
        return obtener_SiEsValidoUsuario(u, () -> {
            inicializarServidoresExternos();
            return buscarUnResultado_SiExisteUsuario(p.getUsersusername().getUsername(), v -> v.getClasificacion(u, p));
        });

    }

    public Publicacion agregarPublicacion(Publicacion u) throws Exception {
        return servidorInterno.addPublicacion(u);
    }

    public Users getNewUser() throws Exception {
        return servidorInterno.getNewUser();
    }

    public Users getUsuario(String Cuenta, String contraseña) throws Exception {
        inicializarServidoresExternos();
        return buscarUnResultado(v -> v.getUsuario(Cuenta, contraseña));
    }

    public Users getUsuarioInterno(String Cuenta) throws Exception {
        return servidorInterno.getUsuario(Cuenta);
    }

    public Users getUsuarioInterno(String Cuenta, String contraseña) throws Exception {
        return servidorInterno.getUsuario(Cuenta, contraseña);
    }

    public Users getUsuario(String Cuenta) throws Exception {
        inicializarServidoresExternos();
        return buscarUnResultado(v -> v.getUsuario(Cuenta));

    }

    public LinkedList<Publicacion> getPublicacionAllList() throws Exception {
        inicializarServidoresExternos();
        return buscarListasLnkL(v -> v.getPublicacionAllList());
//        LinkedList<Publicacion> publicaciones = servidorInterno.getPublicacionAllList();
//        for (int i = 0; i < servidoresExternos.length; i++) {
//            publicaciones.addAll(servidoresExternos[i].getPublicacionAllList());
//        }
//        return publicaciones;
    }

    public LinkedList<Publicacion> getPublicacionAllList_Limite_Sort(int limite) throws Exception {
        inicializarServidoresExternos();
        LinkedList<Publicacion> lp = buscarListasLnkL(v -> v.getPublicacionAllList_Limite_Sort(limite));
        if (lp.isEmpty()) {
            return lp;
        }
        Collections.sort(lp, new ComparadorDePublicaciones().reversed());
        return new LinkedList<>(lp.subList(0, limite > lp.size() ? lp.size() : limite));

    }

    public LinkedList<Tema> getTemaAllList() throws Exception {
        return servidorInterno.getTemaAllList();
        //return buscarListasLnkL(v->v.getTemaAllList());
    }

    public Tema getTema(int id) throws Exception {
        return servidorInterno.getTema(id);
    }

    public LinkedList<Tipodetema> getTipoDeTemaAllList() throws Exception {
        return servidorInterno.getTipoDeTemaAllList();
    }

    public LinkedList<Publicacion> getPublicacionAllList(String tipoDeTema, String tema) throws Exception {
        inicializarServidoresExternos();
        return buscarListasLnkL(v -> v.getPublicacionAllList(tipoDeTema, tema));
    }

    public List<Publicacion> getPublicacionAllList(Users u) throws Exception {

        return obtener_SiEsValidoUsuario(u, () -> {
            inicializarServidoresExternos();
            //return buscarListasLnkL(v -> v.getPublicacionAllList(u));
            return buscarUnResultado_SiExisteUsuario(u.getUsername(), v -> v.getPublicacionAllList(u));
        });

    }

    public List<Publicacion> getPublicacionAllList_Limite_Sort(Users u, int limite) throws Exception {
        return obtener_SiEsValidoUsuario(u, () -> {
            inicializarServidoresExternos();
            //return buscarListasLnkL(v -> v.getPublicacionAllList(u));
            return buscarUnResultado_SiExisteUsuario(u.getUsername(), v -> v.getPublicacionAllList_Limite_Sort(u, limite));
        });

    }

    public Tipodetema getNewTipodetema(String nombre) throws Exception {
        return servidorInterno.getNewTipodetema(nombre);
    }

    public Tema getNewTema(String nombre, Tipodetema ti) throws Exception {
        return servidorInterno.getNewTema(nombre, ti);
    }

    public List<String> getRolesUsuario() throws Exception {
        return servidorInterno.getRolesUsuario();
    }

    public boolean existeUsuarioInterno(String Cuenta, String contraseña) throws Exception {
        return servidorInterno.existeUsuario(Cuenta, contraseña);
    }

    public boolean existeUsuarioInterno_Y_Externo(String Cuenta, String contraseña) throws Exception {
        inicializarServidoresExternos();
        return buscarUnResultadoBool(v -> v.existeUsuario(Cuenta, contraseña));
    }

    public boolean existeUsuarioInterno(String Cuenta) throws Exception {
        return servidorInterno.existeUsuario(Cuenta);
    }

    public boolean existeUsuarioInterno_Y_Externo(String Cuenta) throws Exception {
        inicializarServidoresExternos();
        return buscarUnResultadoBool(v -> v.existeUsuario(Cuenta));
    }

    public Users addUsuario(Users u, boolean esAdministrador) throws Exception {

        return obtener_SiEsValidoUsuario(u, () -> {
            inicializarServidoresExternos();
            Users agregado = servidorInterno.addUsuario(u, esAdministrador);
            notificacionSeAgregoUnaCuenta(agregado.getUsername());
            return agregado;
        });

    }

    public boolean esAdministradorInterno(Users u) throws Exception {
        return obtenerBool_SiEsValidoUsuario(u, () -> {

            return servidorInterno.esAdministrador(u);
        });
    }

    public boolean esAdministrador(Users u) throws Exception {
        return obtenerBool_SiEsValidoUsuario(u, () -> {
            inicializarServidoresExternos();
            return buscarUnResultadoBool_SiExisteUsuario(u.getUsername(), v -> v.esAdministrador(u));
        });
    }

    public Comentario addComentario(Users u, Publicacion p, String contenido) throws Exception {
        return obtener_SiEsValidoUsuario(u, () -> {
            inicializarServidoresExternos();
            return buscarUnResultado_SiExisteUsuario(p.getUsersusername().getUsername(), v -> v.addComentario(u, p, contenido));
        });
    }

    public LinkedList<Users> getUsuarioAllListInterno() throws Exception {
        return servidorInterno.getUsuarioAllList();
    }

    public LinkedList<Users> getUsuarioAllListInterno_Y_Externo() throws Exception {
        inicializarServidoresExternos();
        return buscarListasLnkL(v -> v.getUsuarioAllList());
    }

    public List<Mensaje> getMensajeAllListEntreAmbosSort(Users uno, Users dos) throws Exception {
        return obtener_SiEsValidoUsuario(uno, dos, () -> {
            inicializarServidoresExternos();
            List<Mensaje> lm = buscarUnResultado_SiExisteUsuario(uno.getUsername(), v -> v.getMensajeAllList(uno, dos));
            if (lm == null) {
                lm = new LinkedList<>();
            }
            List<Mensaje> lm2 = buscarUnResultado_SiExisteUsuario(dos.getUsername(), v -> v.getMensajeAllList(dos, uno));
            if (lm2 == null) {
                lm2 = new LinkedList<>();
            }
            lm.addAll(lm2);
            Collections.sort(lm, new ComparadorDeMensajes());
            return lm;
        });

    }

    public Mensaje addMensaje(Users origen, Users destino, String contenido) throws Exception {

        return obtener_SiEsValidoUsuario(origen, destino, () -> {
            inicializarServidoresExternos();
            Mensaje m = servidorInterno.addMensaje(origen, destino, contenido);
            // System.out.println("suelta la noficacion");
            notificacionPublicoMensajes(m);
            //System.out.println("va a salir de los mensajes");
            return m;
        });

    }

    public Subcripcion addSubcripcion(Users propietario, Users subscriptor) throws Exception {
        return obtener_SiEsValidoUsuario(propietario, subscriptor, () -> {
            inicializarServidoresExternos();
            return buscarUnResultado_SiExisteUsuario(propietario.getUsername(), v -> v.addSubcripcion(propietario, subscriptor));
        });

    }

    public void eliminarSubcripcion(Users propietario, Users subscriptor) throws NonexistentEntityException, Exception {
        realizar_SiEsValidoUsuario(propietario, () -> {
            inicializarServidoresExternos();
            realizar_SiExisteUsuario(propietario.getUsername(), v -> v.eliminarSubcripcion(propietario, subscriptor));
        });

    }

    public boolean existeSubcripcion(Users propietario, Users subscriptor) throws Exception {
        return obtenerBool_SiEsValidoUsuario(propietario, subscriptor, () -> {
            inicializarServidoresExternos();
            return buscarUnResultado_SiExisteUsuario(propietario.getUsername(), v -> v.existeSubcripcion(propietario, subscriptor));
        });

    }

    public void eliminarMensaje(Mensaje m) throws NonexistentEntityException, Exception {
        servidorInterno.eliminarMensaje(m);
    }

    public void notificacionEliminaronCuentas(String cuentasEliminadas[]) throws Exception {
        realizarTodos(v -> v.seEliminaronCuentas(clienteInterno.getHost(), clienteInterno.getPort(), clienteInterno.getNombreObjetoRemoto(), cuentasEliminadas));
        // servidorInterno.seEliminaronCuentas(clienteInterno.getHost(), clienteInterno.getPort(), clienteInterno.getNombreObjetoRemoto(), cuentasEliminadas);
    }

    public void notificacionVioLosMensajes(String propietario, String destino) throws Exception {
        //realizar_SiExisteUsuario(destino, v -> v.vioLosMensajes(propietario, destino));
        realizar_SiExisteUsuario(propietario, v -> v.vioLosMensajes(propietario, destino));
    }

    public void notificacionPublicoMensajes(Mensaje m) throws Exception {
        realizar_SiExisteUsuario(m.getUsernamedestino(), v -> v.publicoMensaje(m));
        //realizarTodos(v -> v.publicoMensaje(m));
    }

    public void notificacionEliminoCuenta(String... cuentaEliminada) throws Exception {
        realizarTodos(v -> v.seEliminaronCuentas(clienteInterno.getHost(), clienteInterno.getPort(), clienteInterno.getNombreObjetoRemoto(), cuentaEliminada));
        // servidorInterno.seEliminaronCuentas(clienteInterno.getHost(), clienteInterno.getPort(), clienteInterno.getNombreObjetoRemoto(), cuentaEliminada);
    }

    public void notificacionSeAgregoUnaCuenta(String cuentaAgregada) throws Exception {
        realizarTodos(v -> v.seAgregoUnaCuenta(clienteInterno.getHost(), clienteInterno.getPort(), clienteInterno.getNombreObjetoRemoto(), cuentaAgregada));
        //servidorInterno.seAgregoUnaCuenta(clienteInterno.getHost(), clienteInterno.getPort(), clienteInterno.getNombreObjetoRemoto(), cuentaAgregada);
    }

    public Users getNewUser(String username, String identificacion, String nombre, String apellido1, String apellido2, String email, String password, boolean enabled) throws Exception {
        return servidorInterno.getNewUser(username, identificacion, nombre, apellido1, apellido2, email, password, enabled);
    }

    public Servidores addServidores(String direccionIP) throws Exception {
        return servidorInterno.addServidores(direccionIP);
    }

    public ComunicacionServidor2 getServidorInterno() {
        return servidorInterno;
    }

    public Publicacion addPublicacion(Users u, String contenido, String titulo, int... idTemas) throws Exception {
        return servidorInterno.addPublicacion(u, contenido, titulo, idTemas);
    }

    public Publicacion addPublicacion(String cuenta, String tipoDeTema, String tema, String contenido, String titulo) throws Exception {
        return servidorInterno.addPublicacion(cuenta, tipoDeTema, tema, contenido, titulo);
    }

    public Publicacion addPublicacion(Users u, String contenido, String titulo, Tema... t) throws Exception {
        return servidorInterno.addPublicacion(u, contenido, titulo, t);
    }

    public List<Temapublicacion> getTemaPublicacionAll(Publicacion p) throws Exception {
        inicializarServidoresExternos();
        return buscarUnResultado_SiExisteUsuario(p.getUsersusername().getUsername(), v -> v.getTemaPublicacionAll(p));
    }

    public Publicacion getPublicacion(String cuentaAutor, String titulo) throws Exception {
        return getPublicacion(getUsuario(cuentaAutor), titulo);
    }

    public Publicacion getPublicacion(Users u, String titulo) throws Exception {
        return obtener_SiEsValidoUsuario(u, () -> {
            inicializarServidoresExternos();
            return buscarUnResultado_SiExisteUsuario(u.getUsername(), v -> v.getPublicacion(u, titulo));
        });

    }

    public Publicacion getPublicacion(String cuentaAutor, int idPublicacion) throws Exception {
        return getPublicacion(getUsuario(cuentaAutor), idPublicacion);
    }

    public Publicacion getPublicacion(Users u, int idPublicacion) throws Exception {
        return obtener_SiEsValidoUsuario(u, () -> {
            inicializarServidoresExternos();
            return buscarUnResultado_SiExisteUsuario(u.getUsername(), v -> v.getPublicacion(idPublicacion));
        });

    }

    public List<Comentario> getComentarioAllList(Publicacion p) throws Exception {
        inicializarServidoresExternos();
        return buscarUnResultado_SiExisteUsuario(p.getUsersusername().getUsername(), v -> v.getComentarioAllList(p));
    }

    public Clasificacion addClasificacion(Users autorClasificacion, Publicacion p) throws Exception {
        return obtener_SiEsValidoUsuario(autorClasificacion, () -> {
            inicializarServidoresExternos();
            return buscarUnResultado_SiExisteUsuario(p.getUsersusername().getUsername(), v -> v.addClasificacion(autorClasificacion, p));
        });

    }

    public Clasificacion addClasificacion(Clasificacion c) throws Exception {
        inicializarServidoresExternos();
        return buscarUnResultado_SiExisteUsuario(c.getPublicacionid().getUsersusername().getUsername(), v -> v.addClasificacion(c));
    }

    public Clasificacion updateClasificacion(Clasificacion c) throws Exception {
        inicializarServidoresExternos();
        return buscarUnResultado_SiExisteUsuario(c.getPublicacionid().getUsersusername().getUsername(), v -> v.updateClasificacion(c));
    }

    public Clasificacion addClasificacion(Users autorComentario, Publicacion p, boolean like, int clasificacion) throws Exception {
        return obtener_SiEsValidoUsuario(autorComentario, () -> {
            inicializarServidoresExternos();
            if (clasificacion >= 0 && clasificacion <= 10) {
                //System.out.println("la puso");
                return buscarUnResultado_SiExisteUsuario(p.getUsersusername().getUsername(), v -> {
                    Clasificacion c = v.getNewClasificacion(autorComentario, p);
                    c.setLegusta(like);
                    c.setClasificacion(clasificacion);
                    return v.addClasificacion(c);
                });
            }
            return null;
        });

    }

    public List<Clasificacion> getClasificacionAllList(Publicacion p) throws Exception {
        inicializarServidoresExternos();
        return buscarUnResultado_SiExisteUsuario(p.getUsersusername().getUsername(), v -> v.getClasificacionAllList(p));
    }

    public List<Subcripcion> getSubcripcionDeSubscriptoresAllList(Users propietario) throws Exception {
        return obtener_SiEsValidoUsuario(propietario, () -> {
            inicializarServidoresExternos();
            return buscarUnResultado_SiExisteUsuario(propietario.getUsername(), v -> v.getSubcripcionDeSubscriptoresAllList(propietario));

        });
    }

    public void eliminarComentario(Comentario c) throws Exception {
        inicializarServidoresExternos();
        realizar_SiExisteUsuario(c.getPublicacionid().getUsersusername().getUsername(), v -> v.eliminarComentario(c));

    }

    public Comentario getComentario(Publicacion p, int id) throws Exception {
        inicializarServidoresExternos();
        return buscarUnResultado_SiExisteUsuario(p.getUsersusername().getUsername(), v -> v.getComentario(id));
    }

    public void eliminarComentario(Publicacion p, int id) throws Exception {
        inicializarServidoresExternos();
        realizar_SiExisteUsuario(p.getUsersusername().getUsername(), v -> v.eliminarComentario(id));
    }

    public void eliminarPublicacionInterna(Publicacion p) throws Exception {
        inicializarServidoresExternos();
        //realizar_SiExisteUsuario(p.getUsersusername().getUsername(), v -> v.eliminarPublicacion(p));
        servidorInterno.eliminarPublicacion(p);
        realizarExternos(v -> v.seEliminoPublicacion(p.getUsersusername().getUsername(), p.getId()));
    }

    public void eliminarUsuarioInterno(Users u) throws Exception {
        realizar_SiEsValidoUsuario(u, () -> {
            inicializarServidoresExternos();
            String cuenta = u.getUsername();
            servidorInterno.eliminarUsuario(u);
            eliminarRastrosUsuarioAExternos(cuenta);
            notificacionEliminoCuenta(cuenta);
        });

    }

    public void eliminarUsuarioInterno(String... cuentas) throws Exception {
        inicializarServidoresExternos();
        servidorInterno.eliminarUsuario(cuentas);
        eliminarRastrosUsuarioAExternos(cuentas);
        notificacionEliminoCuenta(cuentas);
    }

    private void eliminarRastrosUsuarioAExternos(String... cuentas) throws Exception {
        realizarExternos(v -> {
            for (int i = 0; i < cuentas.length; i++) {
                String cuenta = cuentas[i];
                try {
                    v.eliminarMensajesDestino(cuenta);
                } catch (Exception ex) {
                    System.out.println("mensaje ya eliminado");
                    // System.out.println("error 1 ini");
                    //  ex.printStackTrace();
                    //System.out.println("fin error 1");
                }
                try {
                    v.eliminarSubcripcionesDeSubcriptor(cuenta);
                } catch (Exception ex) {
                    System.out.println("error 2 ini");
                    ex.printStackTrace();
                    System.out.println("fin error 2");
                }
                try {
                    v.eliminarComentariosAll(cuenta);
                } catch (Exception ex) {
                    System.out.println("error 3 ini");
                    ex.printStackTrace();
                    System.out.println("fin error 3");
                }
                try {
                    v.eliminarClasificacionsAll(cuenta);
                } catch (Exception ex) {
                    System.out.println("error 4 ini");
                    ex.printStackTrace();
                    System.out.println("fin error 4");
                }
            }

        });

    }

    public LinkedList<Tema> getTemaAllList(String tipo) throws Exception {
        return servidorInterno.getTemaAllList(tipo);
    }

//    public List<Mensaje> getMensajeNoVistoAllList(Users origen, Users destinatario) throws Exception {
//        return obtener_SiEsValidoUsuario(origen, destinatario, () -> {
//            inicializarServidoresExternos();
//            return buscarUnResultado_SiExisteUsuario(origen.getUsername(), v -> v.getMensajeNoVistoAllList(origen, destinatario));
//        });
//    }
    public List<Mensaje> getMensajeNoVistoAllList(String origen, String destinatario) throws Exception {
        inicializarServidoresExternos();
        return buscarUnResultado_SiExisteUsuario(origen, v -> v.getMensajeNoVistoAllList(origen, destinatario));

    }

    public LinkedList<String> getUsernamesSubscritosAllList(Users propietario) throws Exception {
        return obtener_SiEsValidoUsuario(propietario, () -> {
            inicializarServidoresExternos();
            return buscarUnResultado_SiExisteUsuario(propietario.getUsername(), v -> v.getUsernamesSubscritosAllList(propietario));
        });
    }

    public int[] getPuntacionGeneralYCantidadDeOpiniones(Publicacion p) throws Exception {
        inicializarServidoresExternos();
        return buscarUnResultado_SiExisteUsuario(p.getUsersusername().getUsername(), v -> v.getPuntacionGeneralYCantidadDeOpiniones(p.getId()));
    }

    public int[] getCantidadDeLikesYDislikes(Publicacion p) throws Exception {
        inicializarServidoresExternos();
        return buscarUnResultado_SiExisteUsuario(p.getUsersusername().getUsername(), v -> v.getCantidadDeLikesYDislikes(p.getId()));
    }

    public List<Subcripcion> getSubcripcionDeSubscriptorAllList(Users subscriptor) throws Exception {
        return obtener_SiEsValidoUsuario(subscriptor, () -> {
            inicializarServidoresExternos();
            return buscarListas(v -> v.getSubcripcionDeSubscriptorAllList(subscriptor));

        });
    }

    public LinkedList<Publicacion> getPublicacionSeguidosAllList(Users u) throws Exception {

//        List<Users> l = getUsuariosSeguidosAllList(u);
//        LinkedList<Publicacion> pub = new LinkedList<Publicacion>();
//        for (int i = 0; i < l.size(); i++) {
//            pub.addAll(l.get(i).getPublicacionCollection());
//        }
        // return pub;
        return obtener_SiEsValidoUsuario(u, () -> {
            inicializarServidoresExternos();
            List<Subcripcion> ls = getSubcripcionDeSubscriptorAllList(u);
            LinkedList<Publicacion> pub2 = new LinkedList<Publicacion>();
            for (Iterator<Subcripcion> iterator = ls.iterator(); iterator.hasNext();) {
                Subcripcion next = iterator.next();
                pub2.addAll(getPublicacionAllList(next.getUsersusernamepropietario()));
            }
            return pub2;
        });
    }

    public LinkedList<Publicacion> getPublicacionSeguidosAllList_Limite_Sort(Users u, int limite) throws Exception {

        return obtener_SiEsValidoUsuario(u, () -> {
            inicializarServidoresExternos();
            List<Subcripcion> ls = getSubcripcionDeSubscriptorAllList(u);
            LinkedList<Publicacion> pub2 = new LinkedList<Publicacion>();
            for (Iterator<Subcripcion> iterator = ls.iterator(); iterator.hasNext();) {
                Subcripcion next = iterator.next();
                pub2.addAll(getPublicacionAllList_Limite_Sort(next.getUsersusernamepropietario(), limite));
            }
            if (pub2.isEmpty()) {
                return pub2;
            }
            Collections.sort(pub2, new ComparadorDePublicaciones().reversed());
            return new LinkedList<>(pub2.subList(0, limite > pub2.size() ? pub2.size() : limite));
            // return pub2;
        });
    }

    public Clasificacion getNewClasificacion(Users u, Publicacion p) throws Exception {
        return obtener_SiEsValidoUsuario(u, () -> {
            return buscarUnResultado_SiExisteUsuario(p.getUsersusername().getUsername(), v -> v.getNewClasificacion(u, p));
        });
    }

    public Users updateUsuarioInterno(Users u) throws Exception {
        return obtener_SiEsValidoUsuario(u, () -> servidorInterno.updateUsuario(u));
    }

    public Users updateUsuarioBasicosInterno(Users u) throws Exception {
        return obtener_SiEsValidoUsuario(u, () -> servidorInterno.updateUsuarioBasicos(u));
    }

    public String getRolInterno(Users u) throws Exception {
        return obtener_SiEsValidoUsuario(u, () -> servidorInterno.getRol(u));
    }

    public void setAuthoritiesInterno(Users u, boolean administrador) throws NonexistentEntityException, Exception {
        realizar_SiEsValidoUsuario(u, () -> servidorInterno.setAuthorities(u, administrador));
    }

    public LinkedList<Servidores> getServidoresAllListInterno() throws Exception {
        return servidorInterno.getServidoresAllList();
    }
    public Servidores getNewServidoresInterno() throws Exception {
     return servidorInterno.getNewServidores();
    }
     public Servidores updateServidorInterno(Servidores u) throws Exception {
      return servidorInterno.updateServidores(u);
     }
     public void eliminarServidorInterno(Servidores s) throws Exception {
       servidorInterno.eliminarServidores(s);
     }
     public void eliminarServidorInterno(int id) throws Exception {
       servidorInterno.eliminarServidores(id);
     }
      public boolean existeServidorInterno(String direccion) throws Exception {
      return servidorInterno.existeServidor(direccion);
      }

    //++++++++++Creacion de objetos++++++++++++++++
    public DialogoChat getDialogoChat(Users propetario, Users destinatario) throws Exception {
        return new DialogoChat(this, getUsuario(propetario.getUsername()), destinatario);
    }

    public DialogoChat getDialogoChat(Users propetario, String destinatario) throws Exception {
        return new DialogoChat(this, propetario, getUsuario(destinatario));
    }

    public DatosDeBeanPrincipal getDatosDeBeanPrincipal(String cuenta) throws Exception {
        return new DatosDeBeanPrincipal(this, getUsuario(cuenta));
    }

    public EstadisticasDeUsuario getEstadisticasDeUsuario(String cuenta) throws Exception {
        return new EstadisticasDeUsuario(this, getUsuario(cuenta));
    }

    public EstadisticasDeUsuario getEstadisticasDeUsuarioInterno(String cuenta) throws Exception {
        return new EstadisticasDeUsuario(this, getUsuarioInterno(cuenta));
    }

    public DatosDeBeanTodasLasPublicaciones getDatosDeBeanTodasLasPublicaciones(SeleccionDeSubmenu sel, String actual) throws Exception {
        return new DatosDeBeanTodasLasPublicaciones(sel, this, getUsuario(actual));
    }

    //todavia
    public LinkedList<DatosDeComentario> addComentarioYgetLista(String cuenta, String autorPublicacion, int idPublicacion, String contenido) throws Exception {
        Publicacion p = getPublicacion(autorPublicacion, idPublicacion);
        Users actual = getUsuario(cuenta);
        addComentario(actual, p, contenido);
        return DatosDePublicacion.obtenerListaDeComentarios(this, p);
//return null;
    }

    public ClasificacionTemporal apretoPuntuacionPersonal(ClasificacionTemporal c) throws Exception {
        c.apretoClasificacionPersonal(this);
        return c;
    }

    public ClasificacionTemporal apretoLike(ClasificacionTemporal c) throws Exception {
        c.apretoLike(this);
        return c;
    }

    public ClasificacionTemporal apretoDislike(ClasificacionTemporal c) throws Exception {
        c.apretoDislike(this);
        return c;
    }

}
