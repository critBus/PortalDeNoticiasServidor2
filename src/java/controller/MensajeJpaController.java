/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.NonexistentEntityException;
import controller.exceptions.PreexistingEntityException;
import entity.Mensaje;
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
public class MensajeJpaController implements Serializable {

    public MensajeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Mensaje mensaje) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Users usersusernameorigen = mensaje.getUsersusernameorigen();
            if (usersusernameorigen != null) {
                usersusernameorigen = em.getReference(usersusernameorigen.getClass(), usersusernameorigen.getUsername());
                mensaje.setUsersusernameorigen(usersusernameorigen);
            }
            em.persist(mensaje);
            if (usersusernameorigen != null) {
                usersusernameorigen.getMensajeCollection().add(mensaje);
                usersusernameorigen = em.merge(usersusernameorigen);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMensaje(mensaje.getId()) != null) {
                throw new PreexistingEntityException("Mensaje " + mensaje + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Mensaje mensaje) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Mensaje persistentMensaje = em.find(Mensaje.class, mensaje.getId());
            Users usersusernameorigenOld = persistentMensaje.getUsersusernameorigen();
            Users usersusernameorigenNew = mensaje.getUsersusernameorigen();
            if (usersusernameorigenNew != null) {
                usersusernameorigenNew = em.getReference(usersusernameorigenNew.getClass(), usersusernameorigenNew.getUsername());
                mensaje.setUsersusernameorigen(usersusernameorigenNew);
            }
            mensaje = em.merge(mensaje);
            if (usersusernameorigenOld != null && !usersusernameorigenOld.equals(usersusernameorigenNew)) {
                usersusernameorigenOld.getMensajeCollection().remove(mensaje);
                usersusernameorigenOld = em.merge(usersusernameorigenOld);
            }
            if (usersusernameorigenNew != null && !usersusernameorigenNew.equals(usersusernameorigenOld)) {
                usersusernameorigenNew.getMensajeCollection().add(mensaje);
                usersusernameorigenNew = em.merge(usersusernameorigenNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = mensaje.getId();
                if (findMensaje(id) == null) {
                    throw new NonexistentEntityException("The mensaje with id " + id + " no longer exists.");
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
            Mensaje mensaje;
            try {
                mensaje = em.getReference(Mensaje.class, id);
                mensaje.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The mensaje with id " + id + " no longer exists.", enfe);
            }
            Users usersusernameorigen = mensaje.getUsersusernameorigen();
            if (usersusernameorigen != null) {
                usersusernameorigen.getMensajeCollection().remove(mensaje);
                usersusernameorigen = em.merge(usersusernameorigen);
            }
            em.remove(mensaje);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Mensaje> findMensajeEntities() {
        return findMensajeEntities(true, -1, -1);
    }

    public List<Mensaje> findMensajeEntities(int maxResults, int firstResult) {
        return findMensajeEntities(false, maxResults, firstResult);
    }

    private List<Mensaje> findMensajeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Mensaje.class));
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

    public Mensaje findMensaje(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Mensaje.class, id);
        } finally {
            em.close();
        }
    }

    public int getMensajeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Mensaje> rt = cq.from(Mensaje.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
