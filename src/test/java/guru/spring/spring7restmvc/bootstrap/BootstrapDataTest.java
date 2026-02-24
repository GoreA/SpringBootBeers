package guru.spring.spring7restmvc.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import guru.spring.spring7restmvc.repositories.BeerRepository;
import guru.spring.spring7restmvc.repositories.CustomerRepository;
import guru.spring.spring7restmvc.services.BeerCsvService;
import guru.spring.spring7restmvc.services.BeerCsvServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(BeerCsvServiceImpl.class)
class BootstrapDataTest {

  @Autowired
  BeerRepository beerRepository;

  @Autowired
  CustomerRepository customerRepository;

  @Autowired
  BeerCsvService beerCsvService;

  BootstrapData bootstrapData;

  @BeforeEach
  void setUp() {
    bootstrapData = new BootstrapData(beerRepository, customerRepository, beerCsvService);
  }

  @Test
  void testBootstrapData() throws Exception {
    bootstrapData.run(null);

    assertThat(beerRepository.count()).isEqualTo(2413);
    assertThat(customerRepository.count()).isEqualTo(3);
  }

}