/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:03 )
 */
package org.itech.labSampleTracker.entities;

import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;

 import java.util.List;


/**
* Domain class for entity "AppRole"
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
@Table(name = "app_role")
public class AppRole implements Serializable {
 
private static final long serialVersionUID = 1L;

	@Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id ;

    @Column(name="name", nullable=false, length=100)
    private String name ;

    @Column(name="is_active", nullable=false)
    private Boolean isActive ;


        @OneToMany(mappedBy="appRole")
    private List<AppUserHasRole> listOfAppUserHasRole ; 


    public String toString() { 
        StringBuilder sb = new StringBuilder(); 
        sb.append(id);
        sb.append("|");
        sb.append(name);
        sb.append("|");
        sb.append(isActive);
        return sb.toString(); 
    } 

}



