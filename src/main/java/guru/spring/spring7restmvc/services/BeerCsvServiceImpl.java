package guru.spring.spring7restmvc.services;

import com.opencsv.bean.CsvToBeanBuilder;
import guru.spring.spring7restmvc.models.BeerCsvRecord;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BeerCsvServiceImpl implements BeerCsvService {

  @Override
  public List<BeerCsvRecord> convertCSV(File csvFile) {
    try (Reader reader = new FileReader(csvFile)) {
      List<BeerCsvRecord> beerCsvRecords = new CsvToBeanBuilder<BeerCsvRecord>(reader)
          .withType(BeerCsvRecord.class)
          .build()
          .parse();
      return beerCsvRecords;
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
