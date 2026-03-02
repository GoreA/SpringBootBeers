package guru.spring.spring7restmvc.repositories;

import guru.spring.spring7restmvc.entities.BeerOrder;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeerOrderRepository extends JpaRepository<BeerOrder, UUID> {
}
