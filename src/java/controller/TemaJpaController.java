/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;
import controller.exceptions.PreexistingEntityException;
import entity.Tema;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entity.Tipodetema;
import entity.Temapublicacion;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Rene
 */
public class TemaJpaController implements Serializable {

    public TemaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Tema tema) throws PreexistingEntityException, Exception {
        if (tema.getTemapublicacionCollection() == null) {
            tema.setTemapublicacionCollection(new ArrayList<Temapublicacion>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tipodetema tipodetemaid = tema.getTipodetemaid();
            if (tipodetemaid != null) {
                tipodetemaid = em.getReference(tipodetemaid.getClass(), tipodetemaid.getId());
                tema.setTipodetemaid(tipodetemaid);
            }
            Collection<Temapublicacion> attachedTemapublicacionCollection = new ArrayList<Temapublicacion>();
            for (Temapublicacion temapublicacionCollectionTemapublicacionToAttach : tema.getTemapublicacionCollection()) {
                temapublicacionCollectionTemapublicacionToAttach = em.getReference(temapublicacionCollectionTemapublicacionToAttach.getClass(), temapublicacionCollectionTemapublicacionToAttach.getId());
                attachedTemapublicacionCollection.add(temapublicacionCollectionTemapublicacionToAttach);
            }
            tema.setTemapublicacionCollection(attachedTemapublicacionCollection);
            em.persist(tema);
            if (tipodetemaid != null) {
                tipodetemaid.getTemaCollection().add(tema);
                tipodetemaid = em.merge(tipodetemaid);
            }
            for (Temapublicacion temapublicacionCollectionTemapublicacion : tema.getTemapublicacionCollection()) {
                Tema oldTemaidOfTemapublicacionCollectionTemapublicacion = temapublicacionCollectionTemapublicacion.getTemaid();
                temapublicacionCollectionTemapublicacion.setTemaid(tema);
                temapublicacionCollectionTemapublicacion = em.merge(temapublicacionCollectionTemapublicacion);
                if (oldTemaidOfTemapublicacionCollectionTemapublicacion != null) {
                    oldTemaidOfTemapublicacionCollectionTemapublicacion.getTemapublicacionCollection().remove(temapublicacionCollectionTemapublicacion);
                    oldTemaidOfTemapublicacionCollectionTemapublicacion = em.merge(oldTemaidOfTemapublicacionCollectionTemapublicacion);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTema(tema.getId()) != null) {
                throw new PreexistingEntityException("Tema " + tema + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Tema tema) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tema persistentTema = em.find(Tema.class, tema.getId());
            Tipodetema tipodetemaidOld = persistentTema.getTipodetemaid();
            Tipodetema tipodetemaidNew = tema.getTipodetemaid();
            Collection<Temapublicacion> temapublicacionCollectionOld = persistentTema.getTemapublicacionCollection();
            Collection<Temapublicacion> temapublicacionCollectionNew = tema.getTemapublicacionCollection();
            List<String> illegalOrphanMessages = null;
            for (Temapublicacion temapublicacionCollectionOldTemapublicacion : temapublicacionCollectionOld) {
                if (!temapublicacionCollectionNew.contains(temapublicacionCollectionOldTemapublicacion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Temapublicacion " + temapublicacionCollectionOldTemapublicacion + " since its temaid field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (tipodetemaidNew != null) {
                tipodetemaidNew = em.getReference(tipodetemaidNew.getClass(), tipodetemaidNew.getId());
                tema.setTipodetemaid(tipodetemaidNew);
            }
            Collection<Temapublicacion> attachedTemapublicacionCollectionNew = new ArrayList<Temapublicacion>();
            for (Temapublicacion temapublicacionCollectionNewTemapublicacionToAttach : temapublicacionCollectionNew) {
                temapublicacionCollectionNewTemapublicacionToAttach = em.getReference(temapublicacionCollectionNewTemapublicacionToAttach.getClass(), temapublicacionCollectionNewTemapublicacionToAttach.getId());
                attachedTemapublicacionCollectionNew.add(temapublicacionCollectionNewTemapublicacionToAttach);
            }
            temapublicacionCollectionNew = attachedTemapublicacionCollectionNew;
            tema.setTemapublicacionCollection(temapublicacionCollectionNew);
            tema = em.merge(tema);
            if (tipodetemaidOld != null && !tipodetemaidOld.equals(tipodetemaidNew)) {
                tipodetemaidOld.getTemaCollection().remove(tema);
                tipodetemaidOld = em.merge(tipodetemaidOld);
            }
            if (tipodetemaidNew != null && !tipodetemaidNew.equals(tipodetemaidOld)) {
                tipodetemaidNew.getTemaCollection().add(tema);
                tipodetemaidNew = em.merge(tipodetemaidNew);
            }
            for (Temapublicacion temapublicacionCollectionNewTemapublicacion : temapublicacionCollectionNew) {
                if (!temapublicacionCollectionOld.contains(temapublicacionCollectionNewTemapublicacion)) {
                    Tema oldTemaidOfTemapublicacionCollectionNewTemapublicacion = temapublicacionCollectionNewTemapublicacion.getTemaid();
                    temapublicacionCollectionNewTemapublicacion.setTemaid(tema);
                    temapublicacionCollectionNewTemapublicacion = em.merge(temapublicacionCollectionNewTemapublicacion);
                    if (oldTemaidOfTemapublicacionCollectionNewTemapublicacion != null && !oldTemaidOfTemapublicacionCollectionNewTemapublicacion.equals(tema)) {
                        oldTemaidOfTemapublicacionCollectionNewTemapublicacion.getTemapublicacionCollection().remove(temapublicacionCollectionNewTemapublicacion);
                        oldTemaidOfTemapublicacionCollectionNewTemapublicacion = em.merge(oldTemaidOfTemapublicacionCollectionNewTemapublicacion);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tema.getId();
                if (findTema(id) == null) {
                    throw new NonexistentEntityException("The tema with id " + id + " no longer exists.");
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
            Tema tema;
            try {
                tema = em.getReference(Tema.class, id);
                tema.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tema with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Temapublicacion> temapublicacionCollectionOrphanCheck = tema.getTemapublicacionCollection();
            for (Temapublicacion temapublicacionCollectionOrphanCheckTemapublicacion : temapublicacionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Tema (" + tema + ") cannot be destroyed since the Temapublicacion " + temapublicacionCollectionOrphanCheckTemapublicacion + " in its temapublicacionCollection field has a non-nullable temaid field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Tipodetema tipodetemaid = tema.getTipodetemaid();
            if (tipodetemaid != null) {
                tipodetemaid.getTemaCollection().remove(tema);
                tipodetemaid = em.merge(tipodetemaid);
            }
            em.remove(tema);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Tema> findTemaEntities() {
        return findTemaEntities(true, -1, -1);
    }

    public List<Tema> findTemaEntities(int maxResults, int firstResult) {
        return findTemaEntities(false, maxResults, firstResult);
    }

    private List<Tema> findTemaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Tema.class));
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

    public Tema findTema(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Tema.class, id);
        } finally {
            em.close();
        }
    }

    public int getTemaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Tema> rt = cq.from(Tema.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
