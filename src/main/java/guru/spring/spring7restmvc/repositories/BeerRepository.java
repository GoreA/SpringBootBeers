package guru.spring.spring7restmvc.repositories;

import guru.spring.spring7restmvc.entities.Beer;
import guru.spring.spring7restmvc.models.BeerStyle;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeerRepository extends JpaRepository<Beer, UUID> {
  List<Beer> findAllByBeerNameIsLikeIgnoreCase(String beerName);

  List<Beer> findAllByBeerStyle(BeerStyle beerStyle);

  List<Beer> findAllByBeerStyleAndBeerNameIsLikeIgnoreCase(BeerStyle beerStyle, String s);
}
