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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
* Domain class for entity "SampleType"
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
@Table(name = "sample_type")
public class SampleType implements Serializable {
 
private static final long serialVersionUID = 1L;

	@Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id ;

    @Column(name="name", nullable=false, length=50)
    private String name ;

    @Column(name="description", length=100)
    private String description ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_at", nullable=false)
    private Date createdAt ;

    @Column(name="is_active", nullable=false)
    private Boolean isActive ;


        @OneToMany(mappedBy="sampleType")
    private List<Sample> listOfSample ; 


    public String toString() { 
        StringBuilder sb = new StringBuilder(); 
        sb.append(id);
        sb.append("|");
        sb.append(name);
        sb.append("|");
        sb.append(description);
        sb.append("|");
        sb.append(createdAt);
        sb.append("|");
        sb.append(isActive);
        return sb.toString(); 
    } 

}



