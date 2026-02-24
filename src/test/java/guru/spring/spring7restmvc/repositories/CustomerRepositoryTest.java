package guru.spring.spring7restmvc.repositories;

import guru.spring.spring7restmvc.entities.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRepositoryTest {

  @Autowired
  CustomerRepository customerRepository;

  @Test
  void testSaveCustomer() {
    Customer customer = customerRepository.save(Customer.builder()
        .name("New Name")
        .version(1)
        .build());

    assertThat(customer.getId()).isNotNull();

  }
}
