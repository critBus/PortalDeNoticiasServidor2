/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
@Table(name = "clasificacion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Clasificacion.findAll", query = "SELECT c FROM Clasificacion c")
    , @NamedQuery(name = "Clasificacion.findById", query = "SELECT c FROM Clasificacion c WHERE c.id = :id")
    , @NamedQuery(name = "Clasificacion.findByClasificacion", query = "SELECT c FROM Clasificacion c WHERE c.clasificacion = :clasificacion")
    , @NamedQuery(name = "Clasificacion.findByLegusta", query = "SELECT c FROM Clasificacion c WHERE c.legusta = :legusta")
    , @NamedQuery(name = "Clasificacion.findByUsernameid", query = "SELECT c FROM Clasificacion c WHERE c.usernameid = :usernameid")})
public class Clasificacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "clasificacion")
    private Integer clasificacion;
    @Column(name = "legusta")
    private Boolean legusta;
    @Basic(optional = false)
    @Column(name = "usernameid")
    private String usernameid;
    @JoinColumn(name = "publicacionid", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Publicacion publicacionid;

    public Clasificacion() {
    }

    public Clasificacion(Integer id) {
        this.id = id;
    }

    public Clasificacion(Integer id, String usernameid) {
        this.id = id;
        this.usernameid = usernameid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(Integer clasificacion) {
        this.clasificacion = clasificacion;
    }

    public Boolean getLegusta() {
        return legusta;
    }

    public void setLegusta(Boolean legusta) {
        this.legusta = legusta;
    }

    public String getUsernameid() {
        return usernameid;
    }

    public void setUsernameid(String usernameid) {
        this.usernameid = usernameid;
    }

    public Publicacion getPublicacionid() {
        return publicacionid;
    }

    public void setPublicacionid(Publicacion publicacionid) {
        this.publicacionid = publicacionid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Clasificacion)) {
            return false;
        }
        Clasificacion other = (Clasificacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Clasificacion[ id=" + id + " ]";
    }
    
}
