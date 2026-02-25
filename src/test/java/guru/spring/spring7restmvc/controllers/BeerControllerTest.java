package guru.spring.spring7restmvc.controllers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import guru.spring.spring7restmvc.models.BeerDTO;
import guru.spring.spring7restmvc.models.BeerStyle;
import guru.spring.spring7restmvc.services.BeerService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(BeerController.class)
class BeerControllerTest {

  public static final String BEER_BASE_PATH = "/api/v1/beer";
  public static final String BEER_BASE_PATH_ID = "/api/v1/beer/{id}";

  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  private BeerService beerService;

  @Autowired
  ObjectMapper objectMapper;

  @Captor
  ArgumentCaptor<UUID> uuidArgumentCaptor = ArgumentCaptor.forClass(UUID.class);

  @Captor
  ArgumentCaptor<BeerDTO> beerArgumentCaptor = ArgumentCaptor.forClass(BeerDTO.class);

  @Test
  void listBeers() throws Exception {

    given(beerService.listBeers(any(), any(), any())).willReturn(getBeers());
    mockMvc.perform(MockMvcRequestBuilders.get(BEER_BASE_PATH)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$.[0].quantityOnHand").value(IsNull.notNullValue()));
  }


  @Test
  void getBeerById() throws Exception {
    BeerDTO testBear = getBeers().get(0);
    given(beerService.getBeerById(any(UUID.class))).willReturn(Optional.of(testBear));
    mockMvc.perform(MockMvcRequestBuilders.get(BEER_BASE_PATH_ID, UUID.randomUUID())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(testBear.getId().toString()))
        .andExpect(jsonPath("$.beerName").value(testBear.getBeerName()));
  }

  @Test
  void testCreateBeer() throws Exception {
    BeerDTO testBear = new BeerDTO(UUID.randomUUID(),
        null,
        "Hop Hooligans",
        BeerStyle.GOSE,
        "873465777",
        20,
        new BigDecimal("20.00"),
        LocalDateTime.now(),
        LocalDateTime.now());

    given(beerService.saveBeer(any(BeerDTO.class))).willReturn(testBear);
    mockMvc.perform(MockMvcRequestBuilders.post(BEER_BASE_PATH)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testBear)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(header().exists("Location"));
  }

  @Test
  void testUpdateBeer() throws Exception {
    BeerDTO testBear = BeerDTO.builder()
        .id(UUID.randomUUID())
        .beerName("Hop Hooligans")
        .beerStyle(BeerStyle.GOSE)
        .upc("899234777")
        .quantityOnHand(20)
        .build();

    given(beerService.updateBeer(any(UUID.class), any(BeerDTO.class))).willReturn(Optional.of(testBear));
    mockMvc.perform(MockMvcRequestBuilders.put(BEER_BASE_PATH_ID, testBear.getId())
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(testBear)))
        .andExpect(status().isOk());

    verify(beerService).updateBeer(any(UUID.class), any(BeerDTO.class));
  }

  @Test
  void testPatchBeer() throws Exception {
    BeerDTO testBear = getBeers().get(0);

    Map<String, Object> beerMap = new HashMap<>();
    beerMap.put("beerName", "New Name");

    given(beerService.patchBeer(any(UUID.class), any(BeerDTO.class))).willReturn(Optional.of(testBear));
    mockMvc.perform(MockMvcRequestBuilders.patch(BEER_BASE_PATH_ID, testBear.getId())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(beerMap)))
        .andExpect(status().isOk());

    verify(beerService).patchBeer(uuidArgumentCaptor.capture(), beerArgumentCaptor.capture());
    assertThat(uuidArgumentCaptor.getValue()).isEqualTo(testBear.getId());
    assertThat(beerMap.get("beerName")).isEqualTo(beerArgumentCaptor.getValue().getBeerName());
  }

  @Test
  void testDeleteBeer() throws Exception {
    BeerDTO testBear = getBeers().get(0);

    given(beerService.deleteBeer(any(UUID.class))).willReturn(true);
    mockMvc.perform(MockMvcRequestBuilders.delete(BEER_BASE_PATH_ID, testBear.getId())
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    verify(beerService).deleteBeer(uuidArgumentCaptor.capture());
    assertThat(uuidArgumentCaptor.getValue()).isEqualTo(testBear.getId());
  }

  @Test
  public void testErrorHandling() throws Exception {
    given(beerService.getBeerById(any(UUID.class))).willReturn(Optional.empty());
    mockMvc.perform(MockMvcRequestBuilders.get(BEER_BASE_PATH_ID, UUID.randomUUID()))
        .andExpect(status().isNotFound());
  }

  @Test
  void testCreateBeerNullBeerName() throws Exception {

    BeerDTO beerDTO = BeerDTO.builder().build();

    given(beerService.saveBeer(any(BeerDTO.class))).willReturn(getBeers().get(1));

    MvcResult mvcResult = mockMvc.perform(post(BEER_BASE_PATH)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(beerDTO)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.length()", is(5)))
        .andReturn();

    System.out.println(mvcResult.getResponse().getContentAsString());
  }

  @Test
  void testUpdateBeerNullBeerName() throws Exception {

    BeerDTO beerDTO = BeerDTO.builder().build();

    given(beerService.updateBeer(any(UUID.class), any(BeerDTO.class))).willReturn(Optional.of(getBeers().get(1)));

    MvcResult mvcResult = mockMvc.perform(post(BEER_BASE_PATH)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(beerDTO)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.length()", is(5)))
        .andReturn();

    System.out.println(mvcResult.getResponse().getContentAsString());
  }

  public List<BeerDTO> getBeers () {
    return List.of(
        BeerDTO.builder()
            .id(UUID.randomUUID())
            .beerName("Galaxy Cat")
            .beerStyle(BeerStyle.PALE_ALE)
            .upc("12356")
            .price(new BigDecimal("12.99"))
            .quantityOnHand(122)
            .build(),
        BeerDTO.builder()
            .id(UUID.randomUUID())
            .beerName("Crank")
            .beerStyle(BeerStyle.PALE_ALE)
            .upc("12356222")
            .price(new BigDecimal("11.99"))
            .quantityOnHand(392)
            .build(),
        BeerDTO.builder()
            .id(UUID.randomUUID())
            .beerName("Sunshine City")
            .beerStyle(BeerStyle.IPA)
            .upc("12354")
            .price(new BigDecimal("13.99"))
            .quantityOnHand(144)
            .build()
    );
  }
}