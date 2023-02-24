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
import entity.Subcripcion;
import java.util.ArrayList;
import java.util.Collection;
import entity.Mensaje;
import entity.Authorities;
import entity.Publicacion;
import entity.Users;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Rene
 */
public class UsersJpaController implements Serializable {

    public UsersJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Users users) throws PreexistingEntityException, Exception {
        if (users.getSubcripcionCollection() == null) {
            users.setSubcripcionCollection(new ArrayList<Subcripcion>());
        }
        if (users.getMensajeCollection() == null) {
            users.setMensajeCollection(new ArrayList<Mensaje>());
        }
        if (users.getAuthoritiesCollection() == null) {
            users.setAuthoritiesCollection(new ArrayList<Authorities>());
        }
        if (users.getPublicacionCollection() == null) {
            users.setPublicacionCollection(new ArrayList<Publicacion>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Subcripcion> attachedSubcripcionCollection = new ArrayList<Subcripcion>();
            for (Subcripcion subcripcionCollectionSubcripcionToAttach : users.getSubcripcionCollection()) {
                subcripcionCollectionSubcripcionToAttach = em.getReference(subcripcionCollectionSubcripcionToAttach.getClass(), subcripcionCollectionSubcripcionToAttach.getId());
                attachedSubcripcionCollection.add(subcripcionCollectionSubcripcionToAttach);
            }
            users.setSubcripcionCollection(attachedSubcripcionCollection);
            Collection<Mensaje> attachedMensajeCollection = new ArrayList<Mensaje>();
            for (Mensaje mensajeCollectionMensajeToAttach : users.getMensajeCollection()) {
                mensajeCollectionMensajeToAttach = em.getReference(mensajeCollectionMensajeToAttach.getClass(), mensajeCollectionMensajeToAttach.getId());
                attachedMensajeCollection.add(mensajeCollectionMensajeToAttach);
            }
            users.setMensajeCollection(attachedMensajeCollection);
            Collection<Authorities> attachedAuthoritiesCollection = new ArrayList<Authorities>();
            for (Authorities authoritiesCollectionAuthoritiesToAttach : users.getAuthoritiesCollection()) {
                authoritiesCollectionAuthoritiesToAttach = em.getReference(authoritiesCollectionAuthoritiesToAttach.getClass(), authoritiesCollectionAuthoritiesToAttach.getAuthoritiesPK());
                attachedAuthoritiesCollection.add(authoritiesCollectionAuthoritiesToAttach);
            }
            users.setAuthoritiesCollection(attachedAuthoritiesCollection);
            Collection<Publicacion> attachedPublicacionCollection = new ArrayList<Publicacion>();
            for (Publicacion publicacionCollectionPublicacionToAttach : users.getPublicacionCollection()) {
                publicacionCollectionPublicacionToAttach = em.getReference(publicacionCollectionPublicacionToAttach.getClass(), publicacionCollectionPublicacionToAttach.getId());
                attachedPublicacionCollection.add(publicacionCollectionPublicacionToAttach);
            }
            users.setPublicacionCollection(attachedPublicacionCollection);
            em.persist(users);
            for (Subcripcion subcripcionCollectionSubcripcion : users.getSubcripcionCollection()) {
                Users oldUsersusernamepropietarioOfSubcripcionCollectionSubcripcion = subcripcionCollectionSubcripcion.getUsersusernamepropietario();
                subcripcionCollectionSubcripcion.setUsersusernamepropietario(users);
                subcripcionCollectionSubcripcion = em.merge(subcripcionCollectionSubcripcion);
                if (oldUsersusernamepropietarioOfSubcripcionCollectionSubcripcion != null) {
                    oldUsersusernamepropietarioOfSubcripcionCollectionSubcripcion.getSubcripcionCollection().remove(subcripcionCollectionSubcripcion);
                    oldUsersusernamepropietarioOfSubcripcionCollectionSubcripcion = em.merge(oldUsersusernamepropietarioOfSubcripcionCollectionSubcripcion);
                }
            }
            for (Mensaje mensajeCollectionMensaje : users.getMensajeCollection()) {
                Users oldUsersusernameorigenOfMensajeCollectionMensaje = mensajeCollectionMensaje.getUsersusernameorigen();
                mensajeCollectionMensaje.setUsersusernameorigen(users);
                mensajeCollectionMensaje = em.merge(mensajeCollectionMensaje);
                if (oldUsersusernameorigenOfMensajeCollectionMensaje != null) {
                    oldUsersusernameorigenOfMensajeCollectionMensaje.getMensajeCollection().remove(mensajeCollectionMensaje);
                    oldUsersusernameorigenOfMensajeCollectionMensaje = em.merge(oldUsersusernameorigenOfMensajeCollectionMensaje);
                }
            }
            for (Authorities authoritiesCollectionAuthorities : users.getAuthoritiesCollection()) {
                Users oldUsersOfAuthoritiesCollectionAuthorities = authoritiesCollectionAuthorities.getUsers();
                authoritiesCollectionAuthorities.setUsers(users);
                authoritiesCollectionAuthorities = em.merge(authoritiesCollectionAuthorities);
                if (oldUsersOfAuthoritiesCollectionAuthorities != null) {
                    oldUsersOfAuthoritiesCollectionAuthorities.getAuthoritiesCollection().remove(authoritiesCollectionAuthorities);
                    oldUsersOfAuthoritiesCollectionAuthorities = em.merge(oldUsersOfAuthoritiesCollectionAuthorities);
                }
            }
            for (Publicacion publicacionCollectionPublicacion : users.getPublicacionCollection()) {
                Users oldUsersusernameOfPublicacionCollectionPublicacion = publicacionCollectionPublicacion.getUsersusername();
                publicacionCollectionPublicacion.setUsersusername(users);
                publicacionCollectionPublicacion = em.merge(publicacionCollectionPublicacion);
                if (oldUsersusernameOfPublicacionCollectionPublicacion != null) {
                    oldUsersusernameOfPublicacionCollectionPublicacion.getPublicacionCollection().remove(publicacionCollectionPublicacion);
                    oldUsersusernameOfPublicacionCollectionPublicacion = em.merge(oldUsersusernameOfPublicacionCollectionPublicacion);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUsers(users.getUsername()) != null) {
                throw new PreexistingEntityException("Users " + users + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Users users) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Users persistentUsers = em.find(Users.class, users.getUsername());
            Collection<Subcripcion> subcripcionCollectionOld = persistentUsers.getSubcripcionCollection();
            Collection<Subcripcion> subcripcionCollectionNew = users.getSubcripcionCollection();
            Collection<Mensaje> mensajeCollectionOld = persistentUsers.getMensajeCollection();
            Collection<Mensaje> mensajeCollectionNew = users.getMensajeCollection();
            Collection<Authorities> authoritiesCollectionOld = persistentUsers.getAuthoritiesCollection();
            Collection<Authorities> authoritiesCollectionNew = users.getAuthoritiesCollection();
            Collection<Publicacion> publicacionCollectionOld = persistentUsers.getPublicacionCollection();
            Collection<Publicacion> publicacionCollectionNew = users.getPublicacionCollection();
            List<String> illegalOrphanMessages = null;
            for (Subcripcion subcripcionCollectionOldSubcripcion : subcripcionCollectionOld) {
                if (!subcripcionCollectionNew.contains(subcripcionCollectionOldSubcripcion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Subcripcion " + subcripcionCollectionOldSubcripcion + " since its usersusernamepropietario field is not nullable.");
                }
            }
            for (Mensaje mensajeCollectionOldMensaje : mensajeCollectionOld) {
                if (!mensajeCollectionNew.contains(mensajeCollectionOldMensaje)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Mensaje " + mensajeCollectionOldMensaje + " since its usersusernameorigen field is not nullable.");
                }
            }
            for (Authorities authoritiesCollectionOldAuthorities : authoritiesCollectionOld) {
                if (!authoritiesCollectionNew.contains(authoritiesCollectionOldAuthorities)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Authorities " + authoritiesCollectionOldAuthorities + " since its users field is not nullable.");
                }
            }
            for (Publicacion publicacionCollectionOldPublicacion : publicacionCollectionOld) {
                if (!publicacionCollectionNew.contains(publicacionCollectionOldPublicacion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Publicacion " + publicacionCollectionOldPublicacion + " since its usersusername field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Subcripcion> attachedSubcripcionCollectionNew = new ArrayList<Subcripcion>();
            for (Subcripcion subcripcionCollectionNewSubcripcionToAttach : subcripcionCollectionNew) {
                subcripcionCollectionNewSubcripcionToAttach = em.getReference(subcripcionCollectionNewSubcripcionToAttach.getClass(), subcripcionCollectionNewSubcripcionToAttach.getId());
                attachedSubcripcionCollectionNew.add(subcripcionCollectionNewSubcripcionToAttach);
            }
            subcripcionCollectionNew = attachedSubcripcionCollectionNew;
            users.setSubcripcionCollection(subcripcionCollectionNew);
            Collection<Mensaje> attachedMensajeCollectionNew = new ArrayList<Mensaje>();
            for (Mensaje mensajeCollectionNewMensajeToAttach : mensajeCollectionNew) {
                mensajeCollectionNewMensajeToAttach = em.getReference(mensajeCollectionNewMensajeToAttach.getClass(), mensajeCollectionNewMensajeToAttach.getId());
                attachedMensajeCollectionNew.add(mensajeCollectionNewMensajeToAttach);
            }
            mensajeCollectionNew = attachedMensajeCollectionNew;
            users.setMensajeCollection(mensajeCollectionNew);
            Collection<Authorities> attachedAuthoritiesCollectionNew = new ArrayList<Authorities>();
            for (Authorities authoritiesCollectionNewAuthoritiesToAttach : authoritiesCollectionNew) {
                authoritiesCollectionNewAuthoritiesToAttach = em.getReference(authoritiesCollectionNewAuthoritiesToAttach.getClass(), authoritiesCollectionNewAuthoritiesToAttach.getAuthoritiesPK());
                attachedAuthoritiesCollectionNew.add(authoritiesCollectionNewAuthoritiesToAttach);
            }
            authoritiesCollectionNew = attachedAuthoritiesCollectionNew;
            users.setAuthoritiesCollection(authoritiesCollectionNew);
            Collection<Publicacion> attachedPublicacionCollectionNew = new ArrayList<Publicacion>();
            for (Publicacion publicacionCollectionNewPublicacionToAttach : publicacionCollectionNew) {
                publicacionCollectionNewPublicacionToAttach = em.getReference(publicacionCollectionNewPublicacionToAttach.getClass(), publicacionCollectionNewPublicacionToAttach.getId());
                attachedPublicacionCollectionNew.add(publicacionCollectionNewPublicacionToAttach);
            }
            publicacionCollectionNew = attachedPublicacionCollectionNew;
            users.setPublicacionCollection(publicacionCollectionNew);
            users = em.merge(users);
            for (Subcripcion subcripcionCollectionNewSubcripcion : subcripcionCollectionNew) {
                if (!subcripcionCollectionOld.contains(subcripcionCollectionNewSubcripcion)) {
                    Users oldUsersusernamepropietarioOfSubcripcionCollectionNewSubcripcion = subcripcionCollectionNewSubcripcion.getUsersusernamepropietario();
                    subcripcionCollectionNewSubcripcion.setUsersusernamepropietario(users);
                    subcripcionCollectionNewSubcripcion = em.merge(subcripcionCollectionNewSubcripcion);
                    if (oldUsersusernamepropietarioOfSubcripcionCollectionNewSubcripcion != null && !oldUsersusernamepropietarioOfSubcripcionCollectionNewSubcripcion.equals(users)) {
                        oldUsersusernamepropietarioOfSubcripcionCollectionNewSubcripcion.getSubcripcionCollection().remove(subcripcionCollectionNewSubcripcion);
                        oldUsersusernamepropietarioOfSubcripcionCollectionNewSubcripcion = em.merge(oldUsersusernamepropietarioOfSubcripcionCollectionNewSubcripcion);
                    }
                }
            }
            for (Mensaje mensajeCollectionNewMensaje : mensajeCollectionNew) {
                if (!mensajeCollectionOld.contains(mensajeCollectionNewMensaje)) {
                    Users oldUsersusernameorigenOfMensajeCollectionNewMensaje = mensajeCollectionNewMensaje.getUsersusernameorigen();
                    mensajeCollectionNewMensaje.setUsersusernameorigen(users);
                    mensajeCollectionNewMensaje = em.merge(mensajeCollectionNewMensaje);
                    if (oldUsersusernameorigenOfMensajeCollectionNewMensaje != null && !oldUsersusernameorigenOfMensajeCollectionNewMensaje.equals(users)) {
                        oldUsersusernameorigenOfMensajeCollectionNewMensaje.getMensajeCollection().remove(mensajeCollectionNewMensaje);
                        oldUsersusernameorigenOfMensajeCollectionNewMensaje = em.merge(oldUsersusernameorigenOfMensajeCollectionNewMensaje);
                    }
                }
            }
            for (Authorities authoritiesCollectionNewAuthorities : authoritiesCollectionNew) {
                if (!authoritiesCollectionOld.contains(authoritiesCollectionNewAuthorities)) {
                    Users oldUsersOfAuthoritiesCollectionNewAuthorities = authoritiesCollectionNewAuthorities.getUsers();
                    authoritiesCollectionNewAuthorities.setUsers(users);
                    authoritiesCollectionNewAuthorities = em.merge(authoritiesCollectionNewAuthorities);
                    if (oldUsersOfAuthoritiesCollectionNewAuthorities != null && !oldUsersOfAuthoritiesCollectionNewAuthorities.equals(users)) {
                        oldUsersOfAuthoritiesCollectionNewAuthorities.getAuthoritiesCollection().remove(authoritiesCollectionNewAuthorities);
                        oldUsersOfAuthoritiesCollectionNewAuthorities = em.merge(oldUsersOfAuthoritiesCollectionNewAuthorities);
                    }
                }
            }
            for (Publicacion publicacionCollectionNewPublicacion : publicacionCollectionNew) {
                if (!publicacionCollectionOld.contains(publicacionCollectionNewPublicacion)) {
                    Users oldUsersusernameOfPublicacionCollectionNewPublicacion = publicacionCollectionNewPublicacion.getUsersusername();
                    publicacionCollectionNewPublicacion.setUsersusername(users);
                    publicacionCollectionNewPublicacion = em.merge(publicacionCollectionNewPublicacion);
                    if (oldUsersusernameOfPublicacionCollectionNewPublicacion != null && !oldUsersusernameOfPublicacionCollectionNewPublicacion.equals(users)) {
                        oldUsersusernameOfPublicacionCollectionNewPublicacion.getPublicacionCollection().remove(publicacionCollectionNewPublicacion);
                        oldUsersusernameOfPublicacionCollectionNewPublicacion = em.merge(oldUsersusernameOfPublicacionCollectionNewPublicacion);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = users.getUsername();
                if (findUsers(id) == null) {
                    throw new NonexistentEntityException("The users with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Users users;
            try {
                users = em.getReference(Users.class, id);
                users.getUsername();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The users with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Subcripcion> subcripcionCollectionOrphanCheck = users.getSubcripcionCollection();
            for (Subcripcion subcripcionCollectionOrphanCheckSubcripcion : subcripcionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Users (" + users + ") cannot be destroyed since the Subcripcion " + subcripcionCollectionOrphanCheckSubcripcion + " in its subcripcionCollection field has a non-nullable usersusernamepropietario field.");
            }
            Collection<Mensaje> mensajeCollectionOrphanCheck = users.getMensajeCollection();
            for (Mensaje mensajeCollectionOrphanCheckMensaje : mensajeCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Users (" + users + ") cannot be destroyed since the Mensaje " + mensajeCollectionOrphanCheckMensaje + " in its mensajeCollection field has a non-nullable usersusernameorigen field.");
            }
            Collection<Authorities> authoritiesCollectionOrphanCheck = users.getAuthoritiesCollection();
            for (Authorities authoritiesCollectionOrphanCheckAuthorities : authoritiesCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Users (" + users + ") cannot be destroyed since the Authorities " + authoritiesCollectionOrphanCheckAuthorities + " in its authoritiesCollection field has a non-nullable users field.");
            }
            Collection<Publicacion> publicacionCollectionOrphanCheck = users.getPublicacionCollection();
            for (Publicacion publicacionCollectionOrphanCheckPublicacion : publicacionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Users (" + users + ") cannot be destroyed since the Publicacion " + publicacionCollectionOrphanCheckPublicacion + " in its publicacionCollection field has a non-nullable usersusername field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(users);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Users> findUsersEntities() {
        return findUsersEntities(true, -1, -1);
    }

    public List<Users> findUsersEntities(int maxResults, int firstResult) {
        return findUsersEntities(false, maxResults, firstResult);
    }

    private List<Users> findUsersEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Users.class));
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

    public Users findUsers(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Users.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsersCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Users> rt = cq.from(Users.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
