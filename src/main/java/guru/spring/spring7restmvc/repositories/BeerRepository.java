package guru.spring.spring7restmvc.repositories;

import guru.spring.spring7restmvc.entities.Beer;
import guru.spring.spring7restmvc.models.BeerStyle;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeerRepository extends JpaRepository<Beer, UUID> {
  Page<Beer> findAllByBeerNameIsLikeIgnoreCase(String beerName, Pageable pageable);

  Page<Beer> findAllByBeerStyle(BeerStyle beerStyle, Pageable pageable);

  Page<Beer> findAllByBeerStyleAndBeerNameIsLikeIgnoreCase(BeerStyle beerStyle, String s, Pageable pageable);
}
