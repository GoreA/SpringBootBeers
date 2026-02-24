package guru.spring.spring7restmvc.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import guru.spring.spring7restmvc.entities.Beer;
import guru.spring.spring7restmvc.exceptions.NotFoundException;
import guru.spring.spring7restmvc.models.BeerDTO;
import guru.spring.spring7restmvc.models.BeerStyle;
import guru.spring.spring7restmvc.repositories.BeerRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
public class BeerControllerIT {
  @Autowired
  BeerController beerController;

  @Autowired
  BeerRepository beerRepository;

  @Autowired
  ObjectMapper objectMapper;

  public static final String BEER_BASE_PATH_ID = "/api/v1/beer/{id}";

  @Autowired
  WebApplicationContext wac;

  MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
  }

  @Test
  void testListBeers() throws Exception {
    List<BeerDTO> beers = beerController.listBeers();
    assertThat(beers).isNotNull();
    assertThat(beers.size()).isEqualTo(2413);
  }

  @Rollback
  @Transactional
  @Test
  void testEmptyList() throws Exception {
    beerRepository.deleteAll();
    List<BeerDTO> beers = beerController.listBeers();
    assertThat(beers).isNotNull();
    assertThat(beers.size()).isEqualTo(0);
  }

  @Test
  void testGetBeerById() throws Exception {
    Beer beer = beerRepository.findAll().get(0);
    BeerDTO beerDTO = beerController.getBeerById(beer.getId());
    assertThat(beerDTO).isNotNull();
  }

  @Test
  void testBeerIdNotFound(){
    assertThrows(NotFoundException.class, () -> beerController.getBeerById(UUID.randomUUID()));
  }

  @Rollback
  @Transactional
  @Test
  void testSaveNewBeer() throws Exception {
    BeerDTO beerDTO = BeerDTO.builder()
        .beerName("New Beer")
        .beerStyle(BeerStyle.LAGER)
        .quantityOnHand(100)
        .upc("87632864")
        .price(new BigDecimal(8.07))
        .build();

    ResponseEntity responseEntity = beerController.createBeer(beerDTO);
    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseEntity.getHeaders().getLocation()).isNotNull();
    String[] locationUUID = responseEntity.getHeaders().getLocation().getPath().split("/");
    UUID beerId = UUID.fromString(locationUUID[4]);
    Beer beer = beerRepository.findById(beerId).get();
    assertThat(beer).isNotNull();
  }

  @Rollback
  @Transactional
  @Test
  void testUpdateExistingBeer() throws Exception {
    Beer beer = beerRepository.findAll().get(0);
    BeerDTO beerDTO = BeerDTO.builder()
        .beerName("Updated Beer")
        .beerStyle(BeerStyle.IPA)
        .quantityOnHand(200)
        .upc("87632864")
        .price(new BigDecimal(8.07))
        .build();

    ResponseEntity responseEntity = beerController.updateBeer(beer.getId(), beerDTO);
    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseEntity.getHeaders().getLocation()).isNotNull();
    String[] locationUUID = responseEntity.getHeaders().getLocation().getPath().split("/");
    UUID beerId = UUID.fromString(locationUUID[4]);
    Beer updatedBeer = beerRepository.findById(beerId).get();
    assertThat(updatedBeer).isNotNull();
    assertThat(updatedBeer.getBeerName()).isEqualTo("Updated Beer");
    assertThat(updatedBeer.getBeerStyle()).isEqualTo(BeerStyle.IPA);
    assertThat(updatedBeer.getQuantityOnHand()).isEqualTo(200);
  }

  @Rollback
  @Transactional
  @Test
  void testUpdateNonExistingBeer() throws Exception {
    BeerDTO beerDTO = BeerDTO.builder().build();
    assertThrows(NotFoundException.class, () -> beerController.updateBeer(UUID.randomUUID(), beerDTO));
  }

  @Rollback
  @Transactional
  @Test
  void testDeleteBeer() throws Exception {
    Beer beer = beerRepository.findAll().get(0);
    ResponseEntity responseEntity = beerController.deleteBeer(beer.getId());
    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(beerRepository.findById(beer.getId())).isEmpty();
  }

  @Rollback
  @Transactional
  @Test
  void testDeleteNonExistingBeer() {
    assertThrows(NotFoundException.class, () -> beerController.deleteBeer(UUID.randomUUID()));
  }

  @Test
  void testPatchBeerLongName() throws Exception {
    Beer beer = beerRepository.findAll().get(0);
    Map<String, Object> beerMap = Map.of("beerName",
        "My Beer 756454532346790-09876543237890-=-0987654321234567890-09876543234567890-98763212346578987678");
    MvcResult result = mockMvc.perform(patch(BEER_BASE_PATH_ID, beer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(beerMap)))
        .andExpect(status().isBadRequest())
        .andReturn();

    String responseBody = result.getResponse().getContentAsString();
    System.out.println(responseBody);
  }
}
