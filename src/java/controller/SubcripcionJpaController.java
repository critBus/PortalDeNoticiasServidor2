/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.NonexistentEntityException;
import controller.exceptions.PreexistingEntityException;
import entity.Subcripcion;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entity.Users;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Rene
 */
public class SubcripcionJpaController implements Serializable {

    public SubcripcionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Subcripcion subcripcion) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Users usersusernamepropietario = subcripcion.getUsersusernamepropietario();
            if (usersusernamepropietario != null) {
                usersusernamepropietario = em.getReference(usersusernamepropietario.getClass(), usersusernamepropietario.getUsername());
                subcripcion.setUsersusernamepropietario(usersusernamepropietario);
            }
            em.persist(subcripcion);
            if (usersusernamepropietario != null) {
                usersusernamepropietario.getSubcripcionCollection().add(subcripcion);
                usersusernamepropietario = em.merge(usersusernamepropietario);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findSubcripcion(subcripcion.getId()) != null) {
                throw new PreexistingEntityException("Subcripcion " + subcripcion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Subcripcion subcripcion) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Subcripcion persistentSubcripcion = em.find(Subcripcion.class, subcripcion.getId());
            Users usersusernamepropietarioOld = persistentSubcripcion.getUsersusernamepropietario();
            Users usersusernamepropietarioNew = subcripcion.getUsersusernamepropietario();
            if (usersusernamepropietarioNew != null) {
                usersusernamepropietarioNew = em.getReference(usersusernamepropietarioNew.getClass(), usersusernamepropietarioNew.getUsername());
                subcripcion.setUsersusernamepropietario(usersusernamepropietarioNew);
            }
            subcripcion = em.merge(subcripcion);
            if (usersusernamepropietarioOld != null && !usersusernamepropietarioOld.equals(usersusernamepropietarioNew)) {
                usersusernamepropietarioOld.getSubcripcionCollection().remove(subcripcion);
                usersusernamepropietarioOld = em.merge(usersusernamepropietarioOld);
            }
            if (usersusernamepropietarioNew != null && !usersusernamepropietarioNew.equals(usersusernamepropietarioOld)) {
                usersusernamepropietarioNew.getSubcripcionCollection().add(subcripcion);
                usersusernamepropietarioNew = em.merge(usersusernamepropietarioNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = subcripcion.getId();
                if (findSubcripcion(id) == null) {
                    throw new NonexistentEntityException("The subcripcion with id " + id + " no longer exists.");
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
            Subcripcion subcripcion;
            try {
                subcripcion = em.getReference(Subcripcion.class, id);
                subcripcion.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The subcripcion with id " + id + " no longer exists.", enfe);
            }
            Users usersusernamepropietario = subcripcion.getUsersusernamepropietario();
            if (usersusernamepropietario != null) {
                usersusernamepropietario.getSubcripcionCollection().remove(subcripcion);
                usersusernamepropietario = em.merge(usersusernamepropietario);
            }
            em.remove(subcripcion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Subcripcion> findSubcripcionEntities() {
        return findSubcripcionEntities(true, -1, -1);
    }

    public List<Subcripcion> findSubcripcionEntities(int maxResults, int firstResult) {
        return findSubcripcionEntities(false, maxResults, firstResult);
    }

    private List<Subcripcion> findSubcripcionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Subcripcion.class));
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

    public Subcripcion findSubcripcion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Subcripcion.class, id);
        } finally {
            em.close();
        }
    }

    public int getSubcripcionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Subcripcion> rt = cq.from(Subcripcion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
