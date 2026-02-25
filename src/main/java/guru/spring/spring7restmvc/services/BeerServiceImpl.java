package guru.spring.spring7restmvc.services;

import guru.spring.spring7restmvc.entities.Beer;
import guru.spring.spring7restmvc.mappers.BeerMapper;
import guru.spring.spring7restmvc.models.BeerDTO;
import guru.spring.spring7restmvc.models.BeerStyle;
import guru.spring.spring7restmvc.repositories.BeerRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BeerServiceImpl implements BeerService {

  private final BeerRepository beerRepository;

  private final BeerMapper beerMapper;

  @Override
  public List<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory) {
    List<Beer> beerList;
     if (StringUtils.isNotBlank(beerName) && Objects.isNull(beerStyle)) {
      beerList = listBeersByName(beerName);
    } else if (!StringUtils.isNotBlank(beerName) && Objects.nonNull(beerStyle)) {
       beerList = listBeersByBeerStyle(beerStyle);
    } else if (StringUtils.isNotBlank(beerName) && Objects.nonNull(beerStyle)) {
       beerList = listBeersByNameAdBeerStyle(beerName, beerStyle);
     } else {
      beerList = beerRepository.findAll();
    }
    if (showInventory == null || !showInventory) {
      beerList.forEach(beer -> beer.setQuantityOnHand(null));
    }
    return beerList.stream().map(beerMapper::beertoBeerDTO).toList();
  }

  private List<Beer> listBeersByNameAdBeerStyle(String beerName, BeerStyle beerStyle) {
    return beerRepository.findAllByBeerStyleAndBeerNameIsLikeIgnoreCase(beerStyle, "%" + beerName + "%");
  }

  public List<Beer> listBeersByName(String beerName) {
    return beerRepository.findAllByBeerNameIsLikeIgnoreCase("%" + beerName + "%");
  }

  public List<Beer> listBeersByBeerStyle(BeerStyle beerStyle) {
    return beerRepository.findAllByBeerStyle(beerStyle);
  }

  @Override
  public Optional<BeerDTO> getBeerById(UUID id) {
    log.info("from Service getBeerById({})", id);
    Beer beer = beerRepository.findById(id).orElse(null);
    return Optional.ofNullable(beerMapper.beertoBeerDTO(beer));
  }

  @Override
  public BeerDTO saveBeer(BeerDTO beerDTO) {
    Beer newBeer = beerMapper.beerDTOtoBeer(beerDTO);
    return beerMapper.beertoBeerDTO(beerRepository.save(newBeer));
  }

  @Override
  public Optional<BeerDTO> updateBeer(UUID uuid, BeerDTO beerDTO) {

    AtomicReference<Optional<BeerDTO>> beerDTORef = new AtomicReference<>();

    beerRepository.findById(uuid).ifPresentOrElse(foundBeer -> {
          foundBeer.setBeerName(beerDTO.getBeerName().isEmpty()
              ? foundBeer.getBeerName() : beerDTO.getBeerName());
          foundBeer.setBeerStyle(beerDTO.getBeerStyle() == null
              ? foundBeer.getBeerStyle() : beerDTO.getBeerStyle());
          foundBeer.setPrice(beerDTO.getPrice() == null
              || beerDTO.getPrice().equals(BigDecimal.valueOf(0.0))
              ? foundBeer.getPrice() : beerDTO.getPrice());
          foundBeer.setQuantityOnHand(beerDTO.getQuantityOnHand() == null
              || beerDTO.getQuantityOnHand() == 0
              ? foundBeer.getQuantityOnHand() : beerDTO.getQuantityOnHand());
          foundBeer.setUpc(beerDTO.getUpc() == null
              || beerDTO.getUpc().isEmpty()
              ? foundBeer.getUpc() : beerDTO.getUpc());
          foundBeer.setUpdateDate(LocalDateTime.now());
          beerDTORef.set(Optional.ofNullable(beerMapper
              .beertoBeerDTO(beerRepository.save(foundBeer))));
        }, () -> beerDTORef.set(Optional.empty())
    );
    return beerDTORef.get();
  }

  @Override
  public boolean deleteBeer(UUID uuid) {
    if (beerRepository.existsById(uuid)) {
      beerRepository.deleteById(uuid);
      return true;
    }
    return  false;
  }

  @Override
  public Optional<BeerDTO> patchBeer(UUID uuid, BeerDTO beerDTO) {
    return updateBeer(uuid, beerDTO);
  }
}
