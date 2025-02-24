package edu.ucsb.cs156.authspike.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Installation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String installationId;
    private String orgName;
}
