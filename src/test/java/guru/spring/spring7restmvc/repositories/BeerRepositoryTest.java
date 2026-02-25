package guru.spring.spring7restmvc.repositories;

import guru.spring.spring7restmvc.bootstrap.BootstrapData;
import guru.spring.spring7restmvc.entities.Beer;
import guru.spring.spring7restmvc.models.BeerStyle;
import guru.spring.spring7restmvc.services.BeerCsvService;
import guru.spring.spring7restmvc.services.BeerCsvServiceImpl;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvServiceImpl.class})
class BeerRepositoryTest {

  @Autowired
  BeerRepository beerRepository;

  @Rollback
  @Transactional
  @Test
  void testSaveBeerTooLongName() {
    Beer savedBeer = beerRepository.save(Beer.builder()
        .beerName("My Beer 756454532346790-09876543237890-=-0987654321234567890-09876543234567890-98763212346578987678")
        .upc("123456789012")
        .beerStyle(BeerStyle.IPA)
        .version(1)
        .build());

    assertThrows(ConstraintViolationException.class, () -> beerRepository.flush());
  }

  @Rollback
  @Transactional
  @Test
  void testSaveBeer() {
    Beer savedBeer = beerRepository.save(Beer.builder()
        .beerName("My Beer").upc("123456789012").beerStyle(BeerStyle.IPA)
        .version(1)
        .build());

    beerRepository.flush();
    assertThat(savedBeer).isNotNull();
    assertThat(savedBeer.getId()).isNotNull();
  }

  @Test
  void testFindBeerByName() {
    List<Beer> beerList = beerRepository.findAllByBeerNameIsLikeIgnoreCase("%Brew%");
    assertThat(beerList).isNotEmpty();
    assertThat(beerList.size()).isEqualTo(16);
  }

  @Test
  void testFindBeerByNameAndBeerStyle() {
    List<Beer> beerList = beerRepository.findAllByBeerStyle(BeerStyle.LAGER);
    assertThat(beerList).isNotEmpty();
    assertThat(beerList.size()).isEqualTo(39);
  }

  @Test
  void testFindBeerByNameAndBeerStyleAndName() {
    List<Beer> beerList = beerRepository.findAllByBeerStyleAndBeerNameIsLikeIgnoreCase(BeerStyle.LAGER, "%Black%");
    assertThat(beerList).isNotEmpty();
    assertThat(beerList.size()).isEqualTo(2);
  }
}
