package guru.spring.spring7restmvc.repositories;

import guru.spring.spring7restmvc.entities.Beer;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeerRepository extends JpaRepository<Beer, UUID> {
}
