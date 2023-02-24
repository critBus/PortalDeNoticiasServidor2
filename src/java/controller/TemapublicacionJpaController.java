/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.NonexistentEntityException;
import controller.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entity.Publicacion;
import entity.Tema;
import entity.Temapublicacion;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Rene
 */
public class TemapublicacionJpaController implements Serializable {

    public TemapublicacionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Temapublicacion temapublicacion) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Publicacion noticiaid = temapublicacion.getNoticiaid();
            if (noticiaid != null) {
                noticiaid = em.getReference(noticiaid.getClass(), noticiaid.getId());
                temapublicacion.setNoticiaid(noticiaid);
            }
            Tema temaid = temapublicacion.getTemaid();
            if (temaid != null) {
                temaid = em.getReference(temaid.getClass(), temaid.getId());
                temapublicacion.setTemaid(temaid);
            }
            em.persist(temapublicacion);
            if (noticiaid != null) {
                noticiaid.getTemapublicacionCollection().add(temapublicacion);
                noticiaid = em.merge(noticiaid);
            }
            if (temaid != null) {
                temaid.getTemapublicacionCollection().add(temapublicacion);
                temaid = em.merge(temaid);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTemapublicacion(temapublicacion.getId()) != null) {
                throw new PreexistingEntityException("Temapublicacion " + temapublicacion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Temapublicacion temapublicacion) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Temapublicacion persistentTemapublicacion = em.find(Temapublicacion.class, temapublicacion.getId());
            Publicacion noticiaidOld = persistentTemapublicacion.getNoticiaid();
            Publicacion noticiaidNew = temapublicacion.getNoticiaid();
            Tema temaidOld = persistentTemapublicacion.getTemaid();
            Tema temaidNew = temapublicacion.getTemaid();
            if (noticiaidNew != null) {
                noticiaidNew = em.getReference(noticiaidNew.getClass(), noticiaidNew.getId());
                temapublicacion.setNoticiaid(noticiaidNew);
            }
            if (temaidNew != null) {
                temaidNew = em.getReference(temaidNew.getClass(), temaidNew.getId());
                temapublicacion.setTemaid(temaidNew);
            }
            temapublicacion = em.merge(temapublicacion);
            if (noticiaidOld != null && !noticiaidOld.equals(noticiaidNew)) {
                noticiaidOld.getTemapublicacionCollection().remove(temapublicacion);
                noticiaidOld = em.merge(noticiaidOld);
            }
            if (noticiaidNew != null && !noticiaidNew.equals(noticiaidOld)) {
                noticiaidNew.getTemapublicacionCollection().add(temapublicacion);
                noticiaidNew = em.merge(noticiaidNew);
            }
            if (temaidOld != null && !temaidOld.equals(temaidNew)) {
                temaidOld.getTemapublicacionCollection().remove(temapublicacion);
                temaidOld = em.merge(temaidOld);
            }
            if (temaidNew != null && !temaidNew.equals(temaidOld)) {
                temaidNew.getTemapublicacionCollection().add(temapublicacion);
                temaidNew = em.merge(temaidNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = temapublicacion.getId();
                if (findTemapublicacion(id) == null) {
                    throw new NonexistentEntityException("The temapublicacion with id " + id + " no longer exists.");
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
            Temapublicacion temapublicacion;
            try {
                temapublicacion = em.getReference(Temapublicacion.class, id);
                temapublicacion.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The temapublicacion with id " + id + " no longer exists.", enfe);
            }
            Publicacion noticiaid = temapublicacion.getNoticiaid();
            if (noticiaid != null) {
                noticiaid.getTemapublicacionCollection().remove(temapublicacion);
                noticiaid = em.merge(noticiaid);
            }
            Tema temaid = temapublicacion.getTemaid();
            if (temaid != null) {
                temaid.getTemapublicacionCollection().remove(temapublicacion);
                temaid = em.merge(temaid);
            }
            em.remove(temapublicacion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Temapublicacion> findTemapublicacionEntities() {
        return findTemapublicacionEntities(true, -1, -1);
    }

    public List<Temapublicacion> findTemapublicacionEntities(int maxResults, int firstResult) {
        return findTemapublicacionEntities(false, maxResults, firstResult);
    }

    private List<Temapublicacion> findTemapublicacionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Temapublicacion.class));
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

    public Temapublicacion findTemapublicacion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Temapublicacion.class, id);
        } finally {
            em.close();
        }
    }

    public int getTemapublicacionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Temapublicacion> rt = cq.from(Temapublicacion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
