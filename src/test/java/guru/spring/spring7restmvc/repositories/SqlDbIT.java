package guru.spring.spring7restmvc.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import guru.spring.spring7restmvc.entities.Beer;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@ActiveProfiles("localmysql")
public class SqlDbIT {
  @Container
  @ServiceConnection
  static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:9.2");

  //  covered by @ServiceConnection, no need to set the properties manually
  //  @DynamicPropertySource
  //  static void mySqlProperties(DynamicPropertyRegistry registry) {
  //    registry.add("spring.datasource.username", mySQLContainer::getUsername);
  //    registry.add("spring.datasource.password", mySQLContainer::getPassword);
  //    registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
  //  }

  @Autowired
  DataSource dataSource;

  @Autowired
  BeerRepository beerRepository;

  @Test
  void testListBeers() {
    List<Beer> beers = beerRepository.findAll();

    assertThat(beers.size()).isEqualTo(2413);
  }
}
