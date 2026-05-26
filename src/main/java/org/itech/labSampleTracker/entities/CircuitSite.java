/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:03 )
 */
package org.itech.labSampleTracker.entities;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;





/**
* Domain class for entity "CircuitSite"
*
* @author Pascal
*
*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@ToString
@Entity
@Table(name = "circuit_site")
public class CircuitSite implements Serializable {
 
private static final long serialVersionUID = 1L;

	@Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id ;

    @Column(name="circuit_id", nullable=false)
    private Integer circuitId ;

    @Column(name="site_id", nullable=false)
    private Integer siteId ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_at")
    private Date createdAt = new Date() ;

    @Column(name="is_active", nullable=false)
    private Boolean isActive = true;


        @ManyToOne
    @JoinColumn(name="site_id", referencedColumnName="id", insertable=false, updatable=false)
    private Site       site ; 

    @ManyToOne
    @JoinColumn(name="circuit_id", referencedColumnName="id", insertable=false, updatable=false)
    private Circuit    circuit ; 


    public String toString() { 
        StringBuilder sb = new StringBuilder(); 
        sb.append(id);
        sb.append("|");
        sb.append(circuitId);
        sb.append("|");
        sb.append(siteId);
        sb.append("|");
        sb.append(createdAt);
        sb.append("|");
        sb.append(isActive);
        return sb.toString(); 
    } 

}



