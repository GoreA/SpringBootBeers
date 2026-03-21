package guru.spring.spring7restmvc.controllers;

import guru.spring.spring7restmvc.exceptions.NotFoundException;
import guru.spring.spring7restmvc.models.BeerDTO;
import guru.spring.spring7restmvc.models.BeerStyle;
import guru.spring.spring7restmvc.services.BeerService;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/beer")
public class BeerController {
  private final BeerService beerService;

  @GetMapping
  public Page<BeerDTO> listBeers(@RequestParam(required = false) String beerName,
                                 @RequestParam(required = false) BeerStyle beerStyle,
                                 @RequestParam(required = false) Boolean showInventory,
                                 @RequestParam(required = false) Integer pageNumber,
                                 @RequestParam(required = false) Integer pageSize){
    return beerService.listBeers(beerName, beerStyle, showInventory, pageNumber, pageSize);
  }

  @RequestMapping(path = "{id}", method = RequestMethod.GET)
  public BeerDTO getBeerById(@PathVariable("id") UUID uuid) {
    log.info("From Controller getBeerById({})", uuid);
    return beerService.getBeerById(uuid)
        .orElseThrow(NotFoundException::new);
  }

  @PostMapping
  public ResponseEntity<BeerDTO> createBeer(@Validated @RequestBody BeerDTO beerDTO) {
    BeerDTO savedBeerDTO = beerService.saveBeer(beerDTO);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Location", "/api/v1/beer/" + savedBeerDTO.getId().toString());
    return ResponseEntity.ok().headers(headers).body(savedBeerDTO);
  }

  @PutMapping(path = "{id}")
  public ResponseEntity<BeerDTO> updateBeer(@PathVariable("id") UUID uuid, @Validated @RequestBody BeerDTO beerDTO) {
    log.info("From Controller updateBeer({})", uuid);
    Optional<BeerDTO> updatedBeerDTOOptional = beerService.updateBeer(uuid, beerDTO);
    BeerDTO updatedBeerDTO = updatedBeerDTOOptional
        .orElseThrow(NotFoundException::new);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Location", "/api/v1/beer/" + updatedBeerDTO.getId().toString());
    return ResponseEntity.ok().headers(headers).body(updatedBeerDTO);
  }

  @DeleteMapping(path = "{id}")
  public ResponseEntity<Void> deleteBeer(@PathVariable("id") UUID uuid) {
    log.info("From Controller deleteBeer({})", uuid);
   if(!beerService.deleteBeer(uuid))
     throw new NotFoundException();
    return ResponseEntity.noContent().build();
  }

  @PatchMapping(path = "{id}")
  public ResponseEntity<BeerDTO> patchBeer(@PathVariable("id") UUID uuid, @RequestBody BeerDTO beerDTO) {
    log.info("From Controller patchBeer({})", uuid);
    Optional<BeerDTO> updatedBeerDTOOptional = beerService.patchBeer(uuid, beerDTO);
    BeerDTO updatedBeerDTO = updatedBeerDTOOptional
        .orElseThrow(NotFoundException::new);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Location", "/api/v1/beer/" + updatedBeerDTO.getId().toString());
    return ResponseEntity.ok().headers(headers).body(updatedBeerDTO);
  }
}
