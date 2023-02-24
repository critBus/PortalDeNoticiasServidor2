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
@Table(name = "temapublicacion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Temapublicacion.findAll", query = "SELECT t FROM Temapublicacion t")
    , @NamedQuery(name = "Temapublicacion.findById", query = "SELECT t FROM Temapublicacion t WHERE t.id = :id")})
public class Temapublicacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "noticiaid", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Publicacion noticiaid;
    @JoinColumn(name = "temaid", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Tema temaid;

    public Temapublicacion() {
    }

    public Temapublicacion(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Publicacion getNoticiaid() {
        return noticiaid;
    }

    public void setNoticiaid(Publicacion noticiaid) {
        this.noticiaid = noticiaid;
    }

    public Tema getTemaid() {
        return temaid;
    }

    public void setTemaid(Tema temaid) {
        this.temaid = temaid;
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
        if (!(object instanceof Temapublicacion)) {
            return false;
        }
        Temapublicacion other = (Temapublicacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Temapublicacion[ id=" + id + " ]";
    }
    
}
