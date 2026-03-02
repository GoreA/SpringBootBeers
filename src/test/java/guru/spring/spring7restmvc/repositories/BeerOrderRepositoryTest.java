package guru.spring.spring7restmvc.repositories;

import guru.spring.spring7restmvc.bootstrap.BootstrapData;
import guru.spring.spring7restmvc.entities.Beer;
import guru.spring.spring7restmvc.entities.BeerOrder;
import guru.spring.spring7restmvc.entities.BeerOrderShipment;
import guru.spring.spring7restmvc.entities.Customer;
import guru.spring.spring7restmvc.services.BeerCsvServiceImpl;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvServiceImpl.class})
class BeerOrderRepositoryTest {

  @Autowired
  BeerOrderRepository beerOrderRepository;

  @Autowired
  CustomerRepository customerRepository;

  @Autowired
  BeerRepository beerRepository;

  Customer testCustomer;
  Beer testBeer;

  @BeforeEach
  void setUp() {
    testCustomer = customerRepository.findAll().getFirst();
    testBeer = beerRepository.findAll().getFirst();
  }

  @Transactional
  @Test
  void findAllByBeerOrders() {
    BeerOrder beerOrder = BeerOrder.builder()
        .customerRef("Test Order")
        .beerOrderShipment(BeerOrderShipment.builder()
            .trackingNumber("8976342675726")
            .build())
        .customer(testCustomer)
        .build();

    HashMap<Integer, Beer> beers = new HashMap<>();
    BeerOrder savedOrder = beerOrderRepository.save(beerOrder);


    System.out.println(savedOrder.getCustomerRef());
  }
}