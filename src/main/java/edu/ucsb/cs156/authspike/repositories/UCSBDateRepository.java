package edu.ucsb.cs156.authspike.repositories;

import edu.ucsb.cs156.authspike.entities.UCSBDate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.ZonedDateTime;
import java.util.List;


@PreAuthorize("hasRole('ROLE_USER')")
@RepositoryRestResource(path="dates")
public interface UCSBDateRepository extends CrudRepository<UCSBDate, Long> {
    @RestResource(exported = true)
    List<UCSBDate> findAll();

    Iterable<UCSBDate> findAllByQuarterYYYYQ(String quarterYYYYQ);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    void deleteById(Long id);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    void deleteByLocalDateTimeIsAfter(ZonedDateTime localDateTime);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    <S extends UCSBDate> S save(final S date);

}
