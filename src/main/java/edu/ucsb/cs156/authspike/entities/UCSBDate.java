package edu.ucsb.cs156.authspike.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "ucsbdates")
public class UCSBDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String quarterYYYYQ;
    private String name;
    private ZonedDateTime localDateTime;
}
