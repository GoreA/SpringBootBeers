package guru.spring.spring7restmvc.bootstrap;

import guru.spring.spring7restmvc.entities.Beer;
import guru.spring.spring7restmvc.entities.Customer;
import guru.spring.spring7restmvc.models.BeerCsvRecord;
import guru.spring.spring7restmvc.models.BeerStyle;
import guru.spring.spring7restmvc.repositories.BeerRepository;
import guru.spring.spring7restmvc.repositories.CustomerRepository;
import guru.spring.spring7restmvc.services.BeerCsvService;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

  private final BeerRepository beerRepository;
  private final CustomerRepository customerRepository;
  private final BeerCsvService beerCsvService;

  @Transactional
  @Override
  public void run(String... args) throws Exception {
    loadBeerData();
    loadCsvData();
    loadCustomerData();
  }

  private void loadCsvData() throws FileNotFoundException {
    if (beerRepository.count() < 10) {
      File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");
      List<BeerCsvRecord> recs = beerCsvService.convertCSV(file);

      recs.forEach(beerCsvRecord -> {
        BeerStyle style = switch (beerCsvRecord.getStyle()) {
          case "American Pale Lager" -> BeerStyle.LAGER;
          case "American Pale Ale (APA)",
               "American Black Ale",
               "Belgian Dark Ale",
               "American Blonde Ale" -> BeerStyle.ALE;
          case "American IPA",
               "American Double / Imperial IPA",
               "Belgian IPA" -> BeerStyle.IPA;
          case "American Porter" -> BeerStyle.PORTER;
          case "Oatmeal Stout",
               "American Stout" -> BeerStyle.STOUT;
          case "Saison / Farmhouse Ale" -> BeerStyle.SAISON;
          case "Fruit / Vegetable Beer",
               "Winter Warmer",
               "Berliner Weissbier" -> BeerStyle.WHEAT;
          case "English Pale Ale" -> BeerStyle.PALE_ALE;
          default -> BeerStyle.PILSNER;
        };
        beerRepository.save(Beer.builder()
            .beerName(StringUtils.abbreviate(beerCsvRecord.getBeer(), 50))
            .beerStyle(style)
            .upc(beerCsvRecord.getId().toString())
            .price(new BigDecimal("9.99"))
            .quantityOnHand(beerCsvRecord.getCount())
            .build());
      });
    }
  }

  private void loadCustomerData() {
    if (customerRepository.count() == 0) {
      Customer customer1 = Customer.builder()
          .name("Maria")
          .build();

      Customer customer2 = Customer.builder()
          .name("Daniel")
          .build();

      Customer customer3 = Customer.builder()
          .name("Jose")
          .build();

      customerRepository.saveAll(Arrays.asList(customer1, customer2, customer3));
    }
  }

  private void loadBeerData() {
    if (beerRepository.count() == 0) {
      Beer beer1 = Beer.builder()
          .beerName("Galaxy Cat")
          .beerStyle(BeerStyle.PALE_ALE)
          .upc("12356")
          .price(new BigDecimal("12.99"))
          .quantityOnHand(122)
          .build();

      Beer beer2 = Beer.builder()
          .beerName("Crank")
          .beerStyle(BeerStyle.PALE_ALE)
          .upc("12356222")
          .price(new BigDecimal("11.99"))
          .quantityOnHand(392)
          .build();

      Beer beer3 = Beer.builder()
          .beerName("Sunshine City")
          .beerStyle(BeerStyle.IPA)
          .upc("12354")
          .price(new BigDecimal("13.99"))
          .quantityOnHand(144)
          .build();

      beerRepository.saveAll(Arrays.asList(beer1, beer2, beer3));

    }
  }
}
