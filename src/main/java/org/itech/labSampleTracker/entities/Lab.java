/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:03 )
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
* Domain class for entity "Lab"
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
@Table(name = "lab")
public class Lab implements Serializable {
 
private static final long serialVersionUID = 1L;

	@Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id ;

    @Column(name="lab_name", nullable=false, length=100)
    private String labName ;

    @Column(name="lab_phone", length=50)
    private String labPhone ;

    @Column(name="lab_mail", length=50)
    private String labMail ;

    @Column(name="lab_type", nullable=false, length=100)
    private String labType ;

    @Column(name="district_id")
    private Integer districtId ;

    @Column(name="longitude")
    private Double longitude ;

    @Column(name="latitude")
    private Double latitude ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_at", nullable=false)
    private Date createdAt ;

    @Column(name="is_active", nullable=false)
    private Boolean isActive ;


        @OneToMany(mappedBy="lab")
    private List<SampleAtLab> listOfSampleAtLab ; 

    @ManyToOne
    @JoinColumn(name="district_id", referencedColumnName="id", insertable=false, updatable=false)
    private District   district ; 


    public String toString() { 
        StringBuilder sb = new StringBuilder(); 
        sb.append(id);
        sb.append("|");
        sb.append(labName);
        sb.append("|");
        sb.append(labPhone);
        sb.append("|");
        sb.append(labMail);
        sb.append("|");
        sb.append(labType);
        sb.append("|");
        sb.append(districtId);
        sb.append("|");
        sb.append(longitude);
        sb.append("|");
        sb.append(latitude);
        sb.append("|");
        sb.append(createdAt);
        sb.append("|");
        sb.append(isActive);
        return sb.toString(); 
    } 

}



