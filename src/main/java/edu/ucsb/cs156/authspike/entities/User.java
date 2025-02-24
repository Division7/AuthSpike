package edu.ucsb.cs156.authspike.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {

    @Id
    private String sub;

    private String name;

    private String email;

    private boolean admin;

    private boolean moderator;

    private int githubId;

    /*private Collection<Installation> installations;*/
}
