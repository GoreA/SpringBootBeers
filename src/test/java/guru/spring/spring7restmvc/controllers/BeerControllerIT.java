package guru.spring.spring7restmvc.controllers;

import static guru.spring.spring7restmvc.controllers.BeerControllerTest.BEER_BASE_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import guru.spring.spring7restmvc.entities.Beer;
import guru.spring.spring7restmvc.exceptions.NotFoundException;
import guru.spring.spring7restmvc.mappers.BeerMapper;
import guru.spring.spring7restmvc.models.BeerDTO;
import guru.spring.spring7restmvc.models.BeerStyle;
import guru.spring.spring7restmvc.repositories.BeerRepository;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
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

  @Autowired
  BeerMapper beerMapper;

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
    Page<BeerDTO> beers = beerController.listBeers(null, null, false, 0, 25);
    assertThat(beers).isNotNull();
    assertThat(beers.getContent().size()).isEqualTo(25);
  }

  @Rollback
  @Transactional
  @Test
  void testEmptyList() throws Exception {
    beerRepository.deleteAll();
    Page<BeerDTO> beers = beerController.listBeers(null, null, false, 0, 25);
    assertThat(beers).isNotNull();
    assertThat(beers.getContent().size()).isEqualTo(0);
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

  @Test
  void testListBeerByName() throws Exception {
    mockMvc.perform(get(BEER_BASE_PATH)
        .queryParam("beerName", "Brew"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.size()", is(16)));
  }

  @Test
  void testListBeerByStyleAndName() throws Exception {
    mockMvc.perform(get(BEER_BASE_PATH)
            .queryParam("beerName", "Black")
            .queryParam("beerStyle", BeerStyle.LAGER.name()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.size()", is(2)));
  }

  @Test
  void testListBeerByStyle() throws Exception {
    mockMvc.perform(get(BEER_BASE_PATH)
            .queryParam("beerStyle", BeerStyle.LAGER.name())
            .queryParam("pageSize", "50")
            .queryParam("pageNumber", "0"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.size()", is(39)));
  }

  @Test
  void testListBeerByStyleAndNameTruePage() throws Exception {
    mockMvc.perform(get(BEER_BASE_PATH)
            .queryParam("beerName", "IPA")
            .queryParam("beerStyle", BeerStyle.IPA.name())
            .queryParam("showInventoryOnHand", "true")
            .queryParam("pageNumber", "2")
            .queryParam("pageSize", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.size()", is(20)))
        .andExpect(jsonPath("$.totalElements", is(310)));
  }

  @Test
  void testListMaxPageSize() throws Exception {
    mockMvc.perform(get(BEER_BASE_PATH)
            .queryParam("pageNumber", "2")
            .queryParam("pageSize", "2000"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.size()", is(1000)))
        .andExpect(jsonPath("$.totalElements", is(2413)));
  }

  //@Disabled // just for demo purposes
  @Test
  void testUpdateBeerBadVersion() throws Exception {
    Beer beer = beerRepository.findAll().get(0);

    BeerDTO beerDTO = beerMapper.beerToBeerDTO(beer);

    beerDTO.setBeerName("Updated Name");

    MvcResult result = mockMvc.perform(put(BEER_BASE_PATH_ID, beer.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(beerDTO)))
        .andExpect(status().isNoContent())
        .andReturn();

    System.out.println(result.getResponse().getContentAsString());

    beerDTO.setBeerName("Updated Name 2");

    MvcResult result2 = mockMvc.perform(put(BEER_BASE_PATH_ID, beer.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(beerDTO)))
        .andExpect(status().isNoContent())
        .andReturn();

    System.out.println(result2.getResponse().getStatus());
  }
}
