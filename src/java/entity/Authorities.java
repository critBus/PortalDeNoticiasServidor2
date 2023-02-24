/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Rene
 */
@Entity
@Table(name = "authorities")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Authorities.findAll", query = "SELECT a FROM Authorities a")
    , @NamedQuery(name = "Authorities.findByAuthority", query = "SELECT a FROM Authorities a WHERE a.authoritiesPK.authority = :authority")
    , @NamedQuery(name = "Authorities.findByUsername", query = "SELECT a FROM Authorities a WHERE a.authoritiesPK.username = :username")})
public class Authorities implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected AuthoritiesPK authoritiesPK;
    @JoinColumn(name = "username", referencedColumnName = "username", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Users users;

    public Authorities() {
    }

    public Authorities(AuthoritiesPK authoritiesPK) {
        this.authoritiesPK = authoritiesPK;
    }

    public Authorities(String authority, String username) {
        this.authoritiesPK = new AuthoritiesPK(authority, username);
    }

    public AuthoritiesPK getAuthoritiesPK() {
        return authoritiesPK;
    }

    public void setAuthoritiesPK(AuthoritiesPK authoritiesPK) {
        this.authoritiesPK = authoritiesPK;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (authoritiesPK != null ? authoritiesPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Authorities)) {
            return false;
        }
        Authorities other = (Authorities) object;
        if ((this.authoritiesPK == null && other.authoritiesPK != null) || (this.authoritiesPK != null && !this.authoritiesPK.equals(other.authoritiesPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Authorities[ authoritiesPK=" + authoritiesPK + " ]";
    }
    
}
