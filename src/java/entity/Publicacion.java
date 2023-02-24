/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Rene
 */
@Entity
@Table(name = "publicacion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Publicacion.findAll", query = "SELECT p FROM Publicacion p")
    , @NamedQuery(name = "Publicacion.findById", query = "SELECT p FROM Publicacion p WHERE p.id = :id")
    , @NamedQuery(name = "Publicacion.findByFecha", query = "SELECT p FROM Publicacion p WHERE p.fecha = :fecha")
    , @NamedQuery(name = "Publicacion.findByTitulo", query = "SELECT p FROM Publicacion p WHERE p.titulo = :titulo")})
public class Publicacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Lob
    @Column(name = "contenido")
    private byte[] contenido;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Column(name = "titulo")
    private String titulo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "noticiaid")
    private Collection<Temapublicacion> temapublicacionCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "publicacionid")
    private Collection<Clasificacion> clasificacionCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "publicacionid")
    private Collection<Comentario> comentarioCollection;
    @JoinColumn(name = "usersusername", referencedColumnName = "username")
    @ManyToOne(optional = false)
    private Users usersusername;

    public Publicacion() {
    }

    public Publicacion(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public byte[] getContenido() {
        return contenido;
    }

    public void setContenido(byte[] contenido) {
        this.contenido = contenido;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    @XmlTransient
    public Collection<Temapublicacion> getTemapublicacionCollection() {
        return temapublicacionCollection;
    }

    public void setTemapublicacionCollection(Collection<Temapublicacion> temapublicacionCollection) {
        this.temapublicacionCollection = temapublicacionCollection;
    }

    @XmlTransient
    public Collection<Clasificacion> getClasificacionCollection() {
        return clasificacionCollection;
    }

    public void setClasificacionCollection(Collection<Clasificacion> clasificacionCollection) {
        this.clasificacionCollection = clasificacionCollection;
    }

    @XmlTransient
    public Collection<Comentario> getComentarioCollection() {
        return comentarioCollection;
    }

    public void setComentarioCollection(Collection<Comentario> comentarioCollection) {
        this.comentarioCollection = comentarioCollection;
    }

    public Users getUsersusername() {
        return usersusername;
    }

    public void setUsersusername(Users usersusername) {
        this.usersusername = usersusername;
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
        if (!(object instanceof Publicacion)) {
            return false;
        }
        Publicacion other = (Publicacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Publicacion[ id=" + id + " ]";
    }
    
}
