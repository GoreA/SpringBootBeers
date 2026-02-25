package guru.spring.spring7restmvc.services;

import guru.spring.spring7restmvc.models.BeerDTO;
import guru.spring.spring7restmvc.models.BeerStyle;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeerService {
  List<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory);

  public Optional<BeerDTO> getBeerById(UUID id);

  BeerDTO saveBeer(BeerDTO beerDTO);

  Optional<BeerDTO> updateBeer(UUID uuid, BeerDTO beerDTO);

  boolean deleteBeer(UUID uuid);

  Optional<BeerDTO> patchBeer(UUID uuid, BeerDTO beerDTO);
}
