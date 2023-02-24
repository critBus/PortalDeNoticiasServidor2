/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Rene
 */
@Entity
@Table(name = "tipodetema")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tipodetema.findAll", query = "SELECT t FROM Tipodetema t")
    , @NamedQuery(name = "Tipodetema.findById", query = "SELECT t FROM Tipodetema t WHERE t.id = :id")
    , @NamedQuery(name = "Tipodetema.findByNombre", query = "SELECT t FROM Tipodetema t WHERE t.nombre = :nombre")})
public class Tipodetema implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "nombre")
    private String nombre;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tipodetemaid")
    private Collection<Tema> temaCollection;

    public Tipodetema() {
    }

    public Tipodetema(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @XmlTransient
    public Collection<Tema> getTemaCollection() {
        return temaCollection;
    }

    public void setTemaCollection(Collection<Tema> temaCollection) {
        this.temaCollection = temaCollection;
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
        if (!(object instanceof Tipodetema)) {
            return false;
        }
        Tipodetema other = (Tipodetema) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Tipodetema[ id=" + id + " ]";
    }
    
}
