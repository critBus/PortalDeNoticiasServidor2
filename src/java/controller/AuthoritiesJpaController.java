/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.NonexistentEntityException;
import controller.exceptions.PreexistingEntityException;
import entity.Authorities;
import entity.AuthoritiesPK;
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
public class AuthoritiesJpaController implements Serializable {

    public AuthoritiesJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Authorities authorities) throws PreexistingEntityException, Exception {
        if (authorities.getAuthoritiesPK() == null) {
            authorities.setAuthoritiesPK(new AuthoritiesPK());
        }
        authorities.getAuthoritiesPK().setUsername(authorities.getUsers().getUsername());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Users users = authorities.getUsers();
            if (users != null) {
                users = em.getReference(users.getClass(), users.getUsername());
                authorities.setUsers(users);
            }
            em.persist(authorities);
            if (users != null) {
                users.getAuthoritiesCollection().add(authorities);
                users = em.merge(users);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findAuthorities(authorities.getAuthoritiesPK()) != null) {
                throw new PreexistingEntityException("Authorities " + authorities + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Authorities authorities) throws NonexistentEntityException, Exception {
        authorities.getAuthoritiesPK().setUsername(authorities.getUsers().getUsername());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Authorities persistentAuthorities = em.find(Authorities.class, authorities.getAuthoritiesPK());
            Users usersOld = persistentAuthorities.getUsers();
            Users usersNew = authorities.getUsers();
            if (usersNew != null) {
                usersNew = em.getReference(usersNew.getClass(), usersNew.getUsername());
                authorities.setUsers(usersNew);
            }
            authorities = em.merge(authorities);
            if (usersOld != null && !usersOld.equals(usersNew)) {
                usersOld.getAuthoritiesCollection().remove(authorities);
                usersOld = em.merge(usersOld);
            }
            if (usersNew != null && !usersNew.equals(usersOld)) {
                usersNew.getAuthoritiesCollection().add(authorities);
                usersNew = em.merge(usersNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                AuthoritiesPK id = authorities.getAuthoritiesPK();
                if (findAuthorities(id) == null) {
                    throw new NonexistentEntityException("The authorities with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(AuthoritiesPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Authorities authorities;
            try {
                authorities = em.getReference(Authorities.class, id);
                authorities.getAuthoritiesPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The authorities with id " + id + " no longer exists.", enfe);
            }
            Users users = authorities.getUsers();
            if (users != null) {
                users.getAuthoritiesCollection().remove(authorities);
                users = em.merge(users);
            }
            em.remove(authorities);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Authorities> findAuthoritiesEntities() {
        return findAuthoritiesEntities(true, -1, -1);
    }

    public List<Authorities> findAuthoritiesEntities(int maxResults, int firstResult) {
        return findAuthoritiesEntities(false, maxResults, firstResult);
    }

    private List<Authorities> findAuthoritiesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Authorities.class));
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

    public Authorities findAuthorities(AuthoritiesPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Authorities.class, id);
        } finally {
            em.close();
        }
    }

    public int getAuthoritiesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Authorities> rt = cq.from(Authorities.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
