package kg.uluk.reference.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReferenceRepository extends JpaRepository<Reference, String> {
  Optional<Reference> findByTicketId(String ticketId);
}
