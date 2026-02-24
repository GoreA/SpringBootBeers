package guru.spring.spring7restmvc.services;

import guru.spring.spring7restmvc.models.BeerCsvRecord;
import java.io.File;
import java.util.List;

public interface BeerCsvService {
  List<BeerCsvRecord> convertCSV(File csvFile);
}
