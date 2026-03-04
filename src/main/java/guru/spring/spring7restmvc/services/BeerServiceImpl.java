package guru.spring.spring7restmvc.services;

import guru.spring.spring7restmvc.entities.Beer;
import guru.spring.spring7restmvc.mappers.BeerMapper;
import guru.spring.spring7restmvc.models.BeerDTO;
import guru.spring.spring7restmvc.models.BeerStyle;
import guru.spring.spring7restmvc.repositories.BeerRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BeerServiceImpl implements BeerService {

  private final BeerRepository beerRepository;

  private final BeerMapper beerMapper;

  private static final int DEFAULT_PAGE = 0;
  private static final int DEFAULT_PAGE_SIZE = 25;

  @Override
  public Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber,
                                 Integer pageSize) {

    PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);
    Page<Beer> beerPage;
     if (StringUtils.isNotBlank(beerName) && Objects.isNull(beerStyle)) {
       beerPage = listBeersByName(beerName, pageRequest);
    } else if (!StringUtils.isNotBlank(beerName) && Objects.nonNull(beerStyle)) {
       beerPage = listBeersByBeerStyle(beerStyle, pageRequest);
    } else if (StringUtils.isNotBlank(beerName) && Objects.nonNull(beerStyle)) {
       beerPage = listBeersByNameAdBeerStyle(beerName, beerStyle, pageRequest);
     } else {
       beerPage = beerRepository.findAll(pageRequest);
    }
    if (showInventory == null || !showInventory) {
      beerPage.forEach(beer -> beer.setQuantityOnHand(null));
    }
    return beerPage.map(beerMapper::beerToBeerDTO);
  }

  public PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
    int queryPageNumber;
    int queryPageSize;

    if (pageNumber != null && pageNumber > 0) {
      queryPageNumber = pageNumber - 1;
    } else {
      queryPageNumber = DEFAULT_PAGE;
    }

    if (pageSize == null) {
      queryPageSize = DEFAULT_PAGE_SIZE;
    } else {
      if (pageSize > 1000) {
        queryPageSize = 1000;
      } else {
        queryPageSize = pageSize;
      }
    }
    Sort sort = Sort.by(Sort.Order.asc("beerName"));

    return PageRequest.of(queryPageNumber, queryPageSize, sort);
  }

  private Page<Beer> listBeersByNameAdBeerStyle(String beerName, BeerStyle beerStyle, Pageable pageable) {
    return beerRepository.findAllByBeerStyleAndBeerNameIsLikeIgnoreCase(beerStyle, "%" + beerName + "%", pageable);
  }

  public Page<Beer> listBeersByName(String beerName, Pageable pageable) {
    return beerRepository.findAllByBeerNameIsLikeIgnoreCase("%" + beerName + "%", pageable);
  }

  public Page<Beer> listBeersByBeerStyle(BeerStyle beerStyle, Pageable pageable) {
    return beerRepository.findAllByBeerStyle(beerStyle, pageable);
  }

  @Override
  public Optional<BeerDTO> getBeerById(UUID id) {
    log.info("from Service getBeerById({})", id);
    Beer beer = beerRepository.findById(id).orElse(null);
    return Optional.ofNullable(beerMapper.beerToBeerDTO(beer));
  }

  @Override
  public BeerDTO saveBeer(BeerDTO beerDTO) {
    Beer newBeer = beerMapper.beerDTOToBeer(beerDTO);
    return beerMapper.beerToBeerDTO(beerRepository.save(newBeer));
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
          foundBeer.setVersion(Objects.isNull(beerDTO.getVersion())
              || beerDTO.getVersion() == 0
              ? foundBeer.getVersion() + 1 : beerDTO.getVersion());
          foundBeer.setUpdateDate(LocalDateTime.now());
          beerDTORef.set(Optional.ofNullable(beerMapper
              .beerToBeerDTO(beerRepository.save(foundBeer))));
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
