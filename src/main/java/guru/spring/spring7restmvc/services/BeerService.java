package guru.spring.spring7restmvc.services;

import guru.spring.spring7restmvc.models.BeerDTO;
import guru.spring.spring7restmvc.models.BeerStyle;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface BeerService {
  Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber,
                          Integer pageSize);

  public Optional<BeerDTO> getBeerById(UUID id);

  BeerDTO saveBeer(BeerDTO beerDTO);

  Optional<BeerDTO> updateBeer(UUID uuid, BeerDTO beerDTO);

  boolean deleteBeer(UUID uuid);

  Optional<BeerDTO> patchBeer(UUID uuid, BeerDTO beerDTO);
}
