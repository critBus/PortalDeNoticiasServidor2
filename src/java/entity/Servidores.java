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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Rene
 */
@Entity
@Table(name = "servidores")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Servidores.findAll", query = "SELECT s FROM Servidores s")
    , @NamedQuery(name = "Servidores.findById", query = "SELECT s FROM Servidores s WHERE s.id = :id")
    , @NamedQuery(name = "Servidores.findByIp", query = "SELECT s FROM Servidores s WHERE s.ip = :ip")})
public class Servidores implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ip")
    private String ip;

    public Servidores() {
    }

    public Servidores(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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
        if (!(object instanceof Servidores)) {
            return false;
        }
        Servidores other = (Servidores) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Servidores[ id=" + id + " ]";
    }
    
}
