/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 */
package org.itech.labSampleTracker.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
* Domain class for entity "Ride"
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
@Table(name = "ride")
public class Ride implements Serializable {
 
private static final long serialVersionUID = 1L;

	@Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id ;

    @Column(name="app_user_id", nullable=false)
    private Integer appUserId ;

    @Column(name="circuit_id", nullable=false)
    private Integer circuitId ;

    @Column(name="ride_number", length=100)
    private String rideNumber ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="start_date", nullable=false)
    private Date startDate ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="end_date")
    private Date endDate ;

    @Column(name="start_mileage")
    private Integer startMileage ;

    @Column(name="end_mileage")
    private Integer endMileage ;


        @ManyToOne
    @JoinColumn(name="circuit_id", referencedColumnName="id", insertable=false, updatable=false)
    private Circuit    circuit ; 

    @ManyToOne
    @JoinColumn(name="app_user_id", referencedColumnName="id", insertable=false, updatable=false)
    private AppUser    appUser ; 

    @OneToMany(mappedBy="ride")
    private List<SampleRetrieving> listOfSampleRetrieving ; 


    public String toString() { 
        StringBuilder sb = new StringBuilder(); 
        sb.append(id);
        sb.append("|");
        sb.append(appUserId);
        sb.append("|");
        sb.append(circuitId);
        sb.append("|");
        sb.append(rideNumber);
        sb.append("|");
        sb.append(startDate);
        sb.append("|");
        sb.append(endDate);
        sb.append("|");
        sb.append(startMileage);
        sb.append("|");
        sb.append(endMileage);
        return sb.toString(); 
    } 

}



