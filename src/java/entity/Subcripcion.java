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
@Table(name = "subcripcion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Subcripcion.findAll", query = "SELECT s FROM Subcripcion s")
    , @NamedQuery(name = "Subcripcion.findById", query = "SELECT s FROM Subcripcion s WHERE s.id = :id")
    , @NamedQuery(name = "Subcripcion.findByFecha", query = "SELECT s FROM Subcripcion s WHERE s.fecha = :fecha")
    , @NamedQuery(name = "Subcripcion.findByUsersusernamesubscriptor", query = "SELECT s FROM Subcripcion s WHERE s.usersusernamesubscriptor = :usersusernamesubscriptor")})
public class Subcripcion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Basic(optional = false)
    @Column(name = "usersusernamesubscriptor")
    private String usersusernamesubscriptor;
    @JoinColumn(name = "usersusernamepropietario", referencedColumnName = "username")
    @ManyToOne(optional = false)
    private Users usersusernamepropietario;

    public Subcripcion() {
    }

    public Subcripcion(Integer id) {
        this.id = id;
    }

    public Subcripcion(Integer id, String usersusernamesubscriptor) {
        this.id = id;
        this.usersusernamesubscriptor = usersusernamesubscriptor;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getUsersusernamesubscriptor() {
        return usersusernamesubscriptor;
    }

    public void setUsersusernamesubscriptor(String usersusernamesubscriptor) {
        this.usersusernamesubscriptor = usersusernamesubscriptor;
    }

    public Users getUsersusernamepropietario() {
        return usersusernamepropietario;
    }

    public void setUsersusernamepropietario(Users usersusernamepropietario) {
        this.usersusernamepropietario = usersusernamepropietario;
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
        if (!(object instanceof Subcripcion)) {
            return false;
        }
        Subcripcion other = (Subcripcion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Subcripcion[ id=" + id + " ]";
    }
    
}
