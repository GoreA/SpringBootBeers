package guru.spring.spring7restmvc.repositories;

import guru.spring.spring7restmvc.entities.Category;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
