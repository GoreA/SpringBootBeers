package guru.spring.spring7restmvc.services;

import static org.assertj.core.api.Assertions.assertThat;

import guru.spring.spring7restmvc.models.BeerCsvRecord;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

public class BeerCsvServiceImplTest {
  BeerCsvService beerCsvService = new BeerCsvServiceImpl();

  @Test
  void convertCSV() throws FileNotFoundException {
    File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");
    List<BeerCsvRecord> recs = beerCsvService.convertCSV(file);
    System.out.println(recs.size());
    assertThat(recs.size()).isGreaterThan(0);
  }
}
