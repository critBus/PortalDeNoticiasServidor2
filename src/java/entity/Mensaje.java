/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Rene
 */
@Entity
@Table(name = "mensaje")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Mensaje.findAll", query = "SELECT m FROM Mensaje m")
    , @NamedQuery(name = "Mensaje.findById", query = "SELECT m FROM Mensaje m WHERE m.id = :id")
    , @NamedQuery(name = "Mensaje.findByContenido", query = "SELECT m FROM Mensaje m WHERE m.contenido = :contenido")
    , @NamedQuery(name = "Mensaje.findByFecha", query = "SELECT m FROM Mensaje m WHERE m.fecha = :fecha")
    , @NamedQuery(name = "Mensaje.findByVisto", query = "SELECT m FROM Mensaje m WHERE m.visto = :visto")
    , @NamedQuery(name = "Mensaje.findByUsernamedestino", query = "SELECT m FROM Mensaje m WHERE m.usernamedestino = :usernamedestino")})
public class Mensaje implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "contenido")
    private String contenido;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Column(name = "visto")
    private Boolean visto;
    @Basic(optional = false)
    @Column(name = "usernamedestino")
    private String usernamedestino;
    @JoinColumn(name = "usersusernameorigen", referencedColumnName = "username")
    @ManyToOne(optional = false)
    private Users usersusernameorigen;

    public Mensaje() {
    }

    public Mensaje(Integer id) {
        this.id = id;
    }

    public Mensaje(Integer id, String usernamedestino) {
        this.id = id;
        this.usernamedestino = usernamedestino;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Boolean getVisto() {
        return visto;
    }

    public void setVisto(Boolean visto) {
        this.visto = visto;
    }

    public String getUsernamedestino() {
        return usernamedestino;
    }

    public void setUsernamedestino(String usernamedestino) {
        this.usernamedestino = usernamedestino;
    }

    public Users getUsersusernameorigen() {
        return usersusernameorigen;
    }

    public void setUsersusernameorigen(Users usersusernameorigen) {
        this.usersusernameorigen = usersusernameorigen;
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
        if (!(object instanceof Mensaje)) {
            return false;
        }
        Mensaje other = (Mensaje) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Mensaje[ id=" + id + " ]";
    }
    
}
