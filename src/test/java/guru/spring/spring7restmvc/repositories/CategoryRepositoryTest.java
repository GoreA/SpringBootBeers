package guru.spring.spring7restmvc.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import guru.spring.spring7restmvc.bootstrap.BootstrapData;
import guru.spring.spring7restmvc.entities.Beer;
import guru.spring.spring7restmvc.entities.Category;
import guru.spring.spring7restmvc.services.BeerCsvServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvServiceImpl.class})
public class CategoryRepositoryTest {
  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  BeerRepository beerRepository;

  Beer testBeer;

  @BeforeEach
  void setUp() {
    testBeer = beerRepository.findAll().getFirst();
  }

  @Transactional
  @Test
  void testAddCategory() {
    Category savedCategory = categoryRepository.saveAndFlush(
        Category.builder()
            .description("Ales")
            .build()
    );

    testBeer.addCategory(savedCategory);
    Beer savedBeer = beerRepository.saveAndFlush(testBeer);

    assertThat(savedBeer.getCategories().size()).isEqualTo(1);
    assertThat(savedCategory.getBeers().size()).isEqualTo(1);
  }

}
