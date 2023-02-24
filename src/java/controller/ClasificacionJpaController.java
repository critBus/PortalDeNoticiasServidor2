/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.NonexistentEntityException;
import controller.exceptions.PreexistingEntityException;
import entity.Clasificacion;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entity.Publicacion;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Rene
 */
public class ClasificacionJpaController implements Serializable {

    public ClasificacionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Clasificacion clasificacion) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Publicacion publicacionid = clasificacion.getPublicacionid();
            if (publicacionid != null) {
                publicacionid = em.getReference(publicacionid.getClass(), publicacionid.getId());
                clasificacion.setPublicacionid(publicacionid);
            }
            em.persist(clasificacion);
            if (publicacionid != null) {
                publicacionid.getClasificacionCollection().add(clasificacion);
                publicacionid = em.merge(publicacionid);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findClasificacion(clasificacion.getId()) != null) {
                throw new PreexistingEntityException("Clasificacion " + clasificacion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Clasificacion clasificacion) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Clasificacion persistentClasificacion = em.find(Clasificacion.class, clasificacion.getId());
            Publicacion publicacionidOld = persistentClasificacion.getPublicacionid();
            Publicacion publicacionidNew = clasificacion.getPublicacionid();
            if (publicacionidNew != null) {
                publicacionidNew = em.getReference(publicacionidNew.getClass(), publicacionidNew.getId());
                clasificacion.setPublicacionid(publicacionidNew);
            }
            clasificacion = em.merge(clasificacion);
            if (publicacionidOld != null && !publicacionidOld.equals(publicacionidNew)) {
                publicacionidOld.getClasificacionCollection().remove(clasificacion);
                publicacionidOld = em.merge(publicacionidOld);
            }
            if (publicacionidNew != null && !publicacionidNew.equals(publicacionidOld)) {
                publicacionidNew.getClasificacionCollection().add(clasificacion);
                publicacionidNew = em.merge(publicacionidNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = clasificacion.getId();
                if (findClasificacion(id) == null) {
                    throw new NonexistentEntityException("The clasificacion with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Clasificacion clasificacion;
            try {
                clasificacion = em.getReference(Clasificacion.class, id);
                clasificacion.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The clasificacion with id " + id + " no longer exists.", enfe);
            }
            Publicacion publicacionid = clasificacion.getPublicacionid();
            if (publicacionid != null) {
                publicacionid.getClasificacionCollection().remove(clasificacion);
                publicacionid = em.merge(publicacionid);
            }
            em.remove(clasificacion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Clasificacion> findClasificacionEntities() {
        return findClasificacionEntities(true, -1, -1);
    }

    public List<Clasificacion> findClasificacionEntities(int maxResults, int firstResult) {
        return findClasificacionEntities(false, maxResults, firstResult);
    }

    private List<Clasificacion> findClasificacionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Clasificacion.class));
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

    public Clasificacion findClasificacion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Clasificacion.class, id);
        } finally {
            em.close();
        }
    }

    public int getClasificacionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Clasificacion> rt = cq.from(Clasificacion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
