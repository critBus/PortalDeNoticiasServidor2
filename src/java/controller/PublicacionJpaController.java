/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;
import controller.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entity.Users;
import entity.Temapublicacion;
import java.util.ArrayList;
import java.util.Collection;
import entity.Clasificacion;
import entity.Comentario;
import entity.Publicacion;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Rene
 */
public class PublicacionJpaController implements Serializable {

    public PublicacionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Publicacion publicacion) throws PreexistingEntityException, Exception {
        if (publicacion.getTemapublicacionCollection() == null) {
            publicacion.setTemapublicacionCollection(new ArrayList<Temapublicacion>());
        }
        if (publicacion.getClasificacionCollection() == null) {
            publicacion.setClasificacionCollection(new ArrayList<Clasificacion>());
        }
        if (publicacion.getComentarioCollection() == null) {
            publicacion.setComentarioCollection(new ArrayList<Comentario>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Users usersusername = publicacion.getUsersusername();
            if (usersusername != null) {
                usersusername = em.getReference(usersusername.getClass(), usersusername.getUsername());
                publicacion.setUsersusername(usersusername);
            }
            Collection<Temapublicacion> attachedTemapublicacionCollection = new ArrayList<Temapublicacion>();
            for (Temapublicacion temapublicacionCollectionTemapublicacionToAttach : publicacion.getTemapublicacionCollection()) {
                temapublicacionCollectionTemapublicacionToAttach = em.getReference(temapublicacionCollectionTemapublicacionToAttach.getClass(), temapublicacionCollectionTemapublicacionToAttach.getId());
                attachedTemapublicacionCollection.add(temapublicacionCollectionTemapublicacionToAttach);
            }
            publicacion.setTemapublicacionCollection(attachedTemapublicacionCollection);
            Collection<Clasificacion> attachedClasificacionCollection = new ArrayList<Clasificacion>();
            for (Clasificacion clasificacionCollectionClasificacionToAttach : publicacion.getClasificacionCollection()) {
                clasificacionCollectionClasificacionToAttach = em.getReference(clasificacionCollectionClasificacionToAttach.getClass(), clasificacionCollectionClasificacionToAttach.getId());
                attachedClasificacionCollection.add(clasificacionCollectionClasificacionToAttach);
            }
            publicacion.setClasificacionCollection(attachedClasificacionCollection);
            Collection<Comentario> attachedComentarioCollection = new ArrayList<Comentario>();
            for (Comentario comentarioCollectionComentarioToAttach : publicacion.getComentarioCollection()) {
                comentarioCollectionComentarioToAttach = em.getReference(comentarioCollectionComentarioToAttach.getClass(), comentarioCollectionComentarioToAttach.getId());
                attachedComentarioCollection.add(comentarioCollectionComentarioToAttach);
            }
            publicacion.setComentarioCollection(attachedComentarioCollection);
            em.persist(publicacion);
            if (usersusername != null) {
                usersusername.getPublicacionCollection().add(publicacion);
                usersusername = em.merge(usersusername);
            }
            for (Temapublicacion temapublicacionCollectionTemapublicacion : publicacion.getTemapublicacionCollection()) {
                Publicacion oldNoticiaidOfTemapublicacionCollectionTemapublicacion = temapublicacionCollectionTemapublicacion.getNoticiaid();
                temapublicacionCollectionTemapublicacion.setNoticiaid(publicacion);
                temapublicacionCollectionTemapublicacion = em.merge(temapublicacionCollectionTemapublicacion);
                if (oldNoticiaidOfTemapublicacionCollectionTemapublicacion != null) {
                    oldNoticiaidOfTemapublicacionCollectionTemapublicacion.getTemapublicacionCollection().remove(temapublicacionCollectionTemapublicacion);
                    oldNoticiaidOfTemapublicacionCollectionTemapublicacion = em.merge(oldNoticiaidOfTemapublicacionCollectionTemapublicacion);
                }
            }
            for (Clasificacion clasificacionCollectionClasificacion : publicacion.getClasificacionCollection()) {
                Publicacion oldPublicacionidOfClasificacionCollectionClasificacion = clasificacionCollectionClasificacion.getPublicacionid();
                clasificacionCollectionClasificacion.setPublicacionid(publicacion);
                clasificacionCollectionClasificacion = em.merge(clasificacionCollectionClasificacion);
                if (oldPublicacionidOfClasificacionCollectionClasificacion != null) {
                    oldPublicacionidOfClasificacionCollectionClasificacion.getClasificacionCollection().remove(clasificacionCollectionClasificacion);
                    oldPublicacionidOfClasificacionCollectionClasificacion = em.merge(oldPublicacionidOfClasificacionCollectionClasificacion);
                }
            }
            for (Comentario comentarioCollectionComentario : publicacion.getComentarioCollection()) {
                Publicacion oldPublicacionidOfComentarioCollectionComentario = comentarioCollectionComentario.getPublicacionid();
                comentarioCollectionComentario.setPublicacionid(publicacion);
                comentarioCollectionComentario = em.merge(comentarioCollectionComentario);
                if (oldPublicacionidOfComentarioCollectionComentario != null) {
                    oldPublicacionidOfComentarioCollectionComentario.getComentarioCollection().remove(comentarioCollectionComentario);
                    oldPublicacionidOfComentarioCollectionComentario = em.merge(oldPublicacionidOfComentarioCollectionComentario);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPublicacion(publicacion.getId()) != null) {
                throw new PreexistingEntityException("Publicacion " + publicacion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Publicacion publicacion) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Publicacion persistentPublicacion = em.find(Publicacion.class, publicacion.getId());
            Users usersusernameOld = persistentPublicacion.getUsersusername();
            Users usersusernameNew = publicacion.getUsersusername();
            Collection<Temapublicacion> temapublicacionCollectionOld = persistentPublicacion.getTemapublicacionCollection();
            Collection<Temapublicacion> temapublicacionCollectionNew = publicacion.getTemapublicacionCollection();
            Collection<Clasificacion> clasificacionCollectionOld = persistentPublicacion.getClasificacionCollection();
            Collection<Clasificacion> clasificacionCollectionNew = publicacion.getClasificacionCollection();
            Collection<Comentario> comentarioCollectionOld = persistentPublicacion.getComentarioCollection();
            Collection<Comentario> comentarioCollectionNew = publicacion.getComentarioCollection();
            List<String> illegalOrphanMessages = null;
            for (Temapublicacion temapublicacionCollectionOldTemapublicacion : temapublicacionCollectionOld) {
                if (!temapublicacionCollectionNew.contains(temapublicacionCollectionOldTemapublicacion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Temapublicacion " + temapublicacionCollectionOldTemapublicacion + " since its noticiaid field is not nullable.");
                }
            }
            for (Clasificacion clasificacionCollectionOldClasificacion : clasificacionCollectionOld) {
                if (!clasificacionCollectionNew.contains(clasificacionCollectionOldClasificacion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Clasificacion " + clasificacionCollectionOldClasificacion + " since its publicacionid field is not nullable.");
                }
            }
            for (Comentario comentarioCollectionOldComentario : comentarioCollectionOld) {
                if (!comentarioCollectionNew.contains(comentarioCollectionOldComentario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Comentario " + comentarioCollectionOldComentario + " since its publicacionid field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (usersusernameNew != null) {
                usersusernameNew = em.getReference(usersusernameNew.getClass(), usersusernameNew.getUsername());
                publicacion.setUsersusername(usersusernameNew);
            }
            Collection<Temapublicacion> attachedTemapublicacionCollectionNew = new ArrayList<Temapublicacion>();
            for (Temapublicacion temapublicacionCollectionNewTemapublicacionToAttach : temapublicacionCollectionNew) {
                temapublicacionCollectionNewTemapublicacionToAttach = em.getReference(temapublicacionCollectionNewTemapublicacionToAttach.getClass(), temapublicacionCollectionNewTemapublicacionToAttach.getId());
                attachedTemapublicacionCollectionNew.add(temapublicacionCollectionNewTemapublicacionToAttach);
            }
            temapublicacionCollectionNew = attachedTemapublicacionCollectionNew;
            publicacion.setTemapublicacionCollection(temapublicacionCollectionNew);
            Collection<Clasificacion> attachedClasificacionCollectionNew = new ArrayList<Clasificacion>();
            for (Clasificacion clasificacionCollectionNewClasificacionToAttach : clasificacionCollectionNew) {
                clasificacionCollectionNewClasificacionToAttach = em.getReference(clasificacionCollectionNewClasificacionToAttach.getClass(), clasificacionCollectionNewClasificacionToAttach.getId());
                attachedClasificacionCollectionNew.add(clasificacionCollectionNewClasificacionToAttach);
            }
            clasificacionCollectionNew = attachedClasificacionCollectionNew;
            publicacion.setClasificacionCollection(clasificacionCollectionNew);
            Collection<Comentario> attachedComentarioCollectionNew = new ArrayList<Comentario>();
            for (Comentario comentarioCollectionNewComentarioToAttach : comentarioCollectionNew) {
                comentarioCollectionNewComentarioToAttach = em.getReference(comentarioCollectionNewComentarioToAttach.getClass(), comentarioCollectionNewComentarioToAttach.getId());
                attachedComentarioCollectionNew.add(comentarioCollectionNewComentarioToAttach);
            }
            comentarioCollectionNew = attachedComentarioCollectionNew;
            publicacion.setComentarioCollection(comentarioCollectionNew);
            publicacion = em.merge(publicacion);
            if (usersusernameOld != null && !usersusernameOld.equals(usersusernameNew)) {
                usersusernameOld.getPublicacionCollection().remove(publicacion);
                usersusernameOld = em.merge(usersusernameOld);
            }
            if (usersusernameNew != null && !usersusernameNew.equals(usersusernameOld)) {
                usersusernameNew.getPublicacionCollection().add(publicacion);
                usersusernameNew = em.merge(usersusernameNew);
            }
            for (Temapublicacion temapublicacionCollectionNewTemapublicacion : temapublicacionCollectionNew) {
                if (!temapublicacionCollectionOld.contains(temapublicacionCollectionNewTemapublicacion)) {
                    Publicacion oldNoticiaidOfTemapublicacionCollectionNewTemapublicacion = temapublicacionCollectionNewTemapublicacion.getNoticiaid();
                    temapublicacionCollectionNewTemapublicacion.setNoticiaid(publicacion);
                    temapublicacionCollectionNewTemapublicacion = em.merge(temapublicacionCollectionNewTemapublicacion);
                    if (oldNoticiaidOfTemapublicacionCollectionNewTemapublicacion != null && !oldNoticiaidOfTemapublicacionCollectionNewTemapublicacion.equals(publicacion)) {
                        oldNoticiaidOfTemapublicacionCollectionNewTemapublicacion.getTemapublicacionCollection().remove(temapublicacionCollectionNewTemapublicacion);
                        oldNoticiaidOfTemapublicacionCollectionNewTemapublicacion = em.merge(oldNoticiaidOfTemapublicacionCollectionNewTemapublicacion);
                    }
                }
            }
            for (Clasificacion clasificacionCollectionNewClasificacion : clasificacionCollectionNew) {
                if (!clasificacionCollectionOld.contains(clasificacionCollectionNewClasificacion)) {
                    Publicacion oldPublicacionidOfClasificacionCollectionNewClasificacion = clasificacionCollectionNewClasificacion.getPublicacionid();
                    clasificacionCollectionNewClasificacion.setPublicacionid(publicacion);
                    clasificacionCollectionNewClasificacion = em.merge(clasificacionCollectionNewClasificacion);
                    if (oldPublicacionidOfClasificacionCollectionNewClasificacion != null && !oldPublicacionidOfClasificacionCollectionNewClasificacion.equals(publicacion)) {
                        oldPublicacionidOfClasificacionCollectionNewClasificacion.getClasificacionCollection().remove(clasificacionCollectionNewClasificacion);
                        oldPublicacionidOfClasificacionCollectionNewClasificacion = em.merge(oldPublicacionidOfClasificacionCollectionNewClasificacion);
                    }
                }
            }
            for (Comentario comentarioCollectionNewComentario : comentarioCollectionNew) {
                if (!comentarioCollectionOld.contains(comentarioCollectionNewComentario)) {
                    Publicacion oldPublicacionidOfComentarioCollectionNewComentario = comentarioCollectionNewComentario.getPublicacionid();
                    comentarioCollectionNewComentario.setPublicacionid(publicacion);
                    comentarioCollectionNewComentario = em.merge(comentarioCollectionNewComentario);
                    if (oldPublicacionidOfComentarioCollectionNewComentario != null && !oldPublicacionidOfComentarioCollectionNewComentario.equals(publicacion)) {
                        oldPublicacionidOfComentarioCollectionNewComentario.getComentarioCollection().remove(comentarioCollectionNewComentario);
                        oldPublicacionidOfComentarioCollectionNewComentario = em.merge(oldPublicacionidOfComentarioCollectionNewComentario);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = publicacion.getId();
                if (findPublicacion(id) == null) {
                    throw new NonexistentEntityException("The publicacion with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Publicacion publicacion;
            try {
                publicacion = em.getReference(Publicacion.class, id);
                publicacion.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The publicacion with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Temapublicacion> temapublicacionCollectionOrphanCheck = publicacion.getTemapublicacionCollection();
            for (Temapublicacion temapublicacionCollectionOrphanCheckTemapublicacion : temapublicacionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Publicacion (" + publicacion + ") cannot be destroyed since the Temapublicacion " + temapublicacionCollectionOrphanCheckTemapublicacion + " in its temapublicacionCollection field has a non-nullable noticiaid field.");
            }
            Collection<Clasificacion> clasificacionCollectionOrphanCheck = publicacion.getClasificacionCollection();
            for (Clasificacion clasificacionCollectionOrphanCheckClasificacion : clasificacionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Publicacion (" + publicacion + ") cannot be destroyed since the Clasificacion " + clasificacionCollectionOrphanCheckClasificacion + " in its clasificacionCollection field has a non-nullable publicacionid field.");
            }
            Collection<Comentario> comentarioCollectionOrphanCheck = publicacion.getComentarioCollection();
            for (Comentario comentarioCollectionOrphanCheckComentario : comentarioCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Publicacion (" + publicacion + ") cannot be destroyed since the Comentario " + comentarioCollectionOrphanCheckComentario + " in its comentarioCollection field has a non-nullable publicacionid field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Users usersusername = publicacion.getUsersusername();
            if (usersusername != null) {
                usersusername.getPublicacionCollection().remove(publicacion);
                usersusername = em.merge(usersusername);
            }
            em.remove(publicacion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Publicacion> findPublicacionEntities() {
        return findPublicacionEntities(true, -1, -1);
    }

    public List<Publicacion> findPublicacionEntities(int maxResults, int firstResult) {
        return findPublicacionEntities(false, maxResults, firstResult);
    }

    private List<Publicacion> findPublicacionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Publicacion.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Publicacion findPublicacion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Publicacion.class, id);
        } finally {
            em.close();
        }
    }

    public int getPublicacionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Publicacion> rt = cq.from(Publicacion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
