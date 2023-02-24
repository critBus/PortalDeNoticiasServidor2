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
import entity.Tema;
import entity.Tipodetema;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Rene
 */
public class TipodetemaJpaController implements Serializable {

    public TipodetemaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Tipodetema tipodetema) throws PreexistingEntityException, Exception {
        if (tipodetema.getTemaCollection() == null) {
            tipodetema.setTemaCollection(new ArrayList<Tema>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Tema> attachedTemaCollection = new ArrayList<Tema>();
            for (Tema temaCollectionTemaToAttach : tipodetema.getTemaCollection()) {
                temaCollectionTemaToAttach = em.getReference(temaCollectionTemaToAttach.getClass(), temaCollectionTemaToAttach.getId());
                attachedTemaCollection.add(temaCollectionTemaToAttach);
            }
            tipodetema.setTemaCollection(attachedTemaCollection);
            em.persist(tipodetema);
            for (Tema temaCollectionTema : tipodetema.getTemaCollection()) {
                Tipodetema oldTipodetemaidOfTemaCollectionTema = temaCollectionTema.getTipodetemaid();
                temaCollectionTema.setTipodetemaid(tipodetema);
                temaCollectionTema = em.merge(temaCollectionTema);
                if (oldTipodetemaidOfTemaCollectionTema != null) {
                    oldTipodetemaidOfTemaCollectionTema.getTemaCollection().remove(temaCollectionTema);
                    oldTipodetemaidOfTemaCollectionTema = em.merge(oldTipodetemaidOfTemaCollectionTema);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTipodetema(tipodetema.getId()) != null) {
                throw new PreexistingEntityException("Tipodetema " + tipodetema + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Tipodetema tipodetema) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tipodetema persistentTipodetema = em.find(Tipodetema.class, tipodetema.getId());
            Collection<Tema> temaCollectionOld = persistentTipodetema.getTemaCollection();
            Collection<Tema> temaCollectionNew = tipodetema.getTemaCollection();
            List<String> illegalOrphanMessages = null;
            for (Tema temaCollectionOldTema : temaCollectionOld) {
                if (!temaCollectionNew.contains(temaCollectionOldTema)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Tema " + temaCollectionOldTema + " since its tipodetemaid field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Tema> attachedTemaCollectionNew = new ArrayList<Tema>();
            for (Tema temaCollectionNewTemaToAttach : temaCollectionNew) {
                temaCollectionNewTemaToAttach = em.getReference(temaCollectionNewTemaToAttach.getClass(), temaCollectionNewTemaToAttach.getId());
                attachedTemaCollectionNew.add(temaCollectionNewTemaToAttach);
            }
            temaCollectionNew = attachedTemaCollectionNew;
            tipodetema.setTemaCollection(temaCollectionNew);
            tipodetema = em.merge(tipodetema);
            for (Tema temaCollectionNewTema : temaCollectionNew) {
                if (!temaCollectionOld.contains(temaCollectionNewTema)) {
                    Tipodetema oldTipodetemaidOfTemaCollectionNewTema = temaCollectionNewTema.getTipodetemaid();
                    temaCollectionNewTema.setTipodetemaid(tipodetema);
                    temaCollectionNewTema = em.merge(temaCollectionNewTema);
                    if (oldTipodetemaidOfTemaCollectionNewTema != null && !oldTipodetemaidOfTemaCollectionNewTema.equals(tipodetema)) {
                        oldTipodetemaidOfTemaCollectionNewTema.getTemaCollection().remove(temaCollectionNewTema);
                        oldTipodetemaidOfTemaCollectionNewTema = em.merge(oldTipodetemaidOfTemaCollectionNewTema);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tipodetema.getId();
                if (findTipodetema(id) == null) {
                    throw new NonexistentEntityException("The tipodetema with id " + id + " no longer exists.");
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
            Tipodetema tipodetema;
            try {
                tipodetema = em.getReference(Tipodetema.class, id);
                tipodetema.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tipodetema with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Tema> temaCollectionOrphanCheck = tipodetema.getTemaCollection();
            for (Tema temaCollectionOrphanCheckTema : temaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Tipodetema (" + tipodetema + ") cannot be destroyed since the Tema " + temaCollectionOrphanCheckTema + " in its temaCollection field has a non-nullable tipodetemaid field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(tipodetema);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Tipodetema> findTipodetemaEntities() {
        return findTipodetemaEntities(true, -1, -1);
    }

    public List<Tipodetema> findTipodetemaEntities(int maxResults, int firstResult) {
        return findTipodetemaEntities(false, maxResults, firstResult);
    }

    private List<Tipodetema> findTipodetemaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Tipodetema.class));
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

    public Tipodetema findTipodetema(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Tipodetema.class, id);
        } finally {
            em.close();
        }
    }

    public int getTipodetemaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Tipodetema> rt = cq.from(Tipodetema.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
