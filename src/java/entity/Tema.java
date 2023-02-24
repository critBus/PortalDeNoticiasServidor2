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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "tema")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tema.findAll", query = "SELECT t FROM Tema t")
    , @NamedQuery(name = "Tema.findById", query = "SELECT t FROM Tema t WHERE t.id = :id")
    , @NamedQuery(name = "Tema.findByNombre", query = "SELECT t FROM Tema t WHERE t.nombre = :nombre")})
public class Tema implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "nombre")
    private String nombre;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "temaid")
    private Collection<Temapublicacion> temapublicacionCollection;
    @JoinColumn(name = "tipodetemaid", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Tipodetema tipodetemaid;

    public Tema() {
    }

    public Tema(Integer id) {
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
    public Collection<Temapublicacion> getTemapublicacionCollection() {
        return temapublicacionCollection;
    }

    public void setTemapublicacionCollection(Collection<Temapublicacion> temapublicacionCollection) {
        this.temapublicacionCollection = temapublicacionCollection;
    }

    public Tipodetema getTipodetemaid() {
        return tipodetemaid;
    }

    public void setTipodetemaid(Tipodetema tipodetemaid) {
        this.tipodetemaid = tipodetemaid;
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
        if (!(object instanceof Tema)) {
            return false;
        }
        Tema other = (Tema) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Tema[ id=" + id + " ]";
    }
    
}
