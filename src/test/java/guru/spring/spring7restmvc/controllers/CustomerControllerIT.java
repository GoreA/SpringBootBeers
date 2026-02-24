package guru.spring.spring7restmvc.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import guru.spring.spring7restmvc.entities.Customer;
import guru.spring.spring7restmvc.exceptions.NotFoundException;
import guru.spring.spring7restmvc.models.CustomerDTO;
import guru.spring.spring7restmvc.repositories.CustomerRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class CustomerControllerIT {
  @Autowired
  private CustomerController controller;
  @Autowired
  private CustomerRepository customerRepository;

  @Test
  public void testListCustomers() {
    List<CustomerDTO> customerDTOList = controller.listCustomers();
    assertThat(controller).isNotNull();
    assertThat(customerDTOList.size()).isEqualTo(3);
  }

  @Test
  public void testGetCustomerById() {
    CustomerDTO customerDTO = controller.getCustomerById(customerRepository.findAll().get(0).getId());
    assertThat(customerDTO).isNotNull();
  }

  @Test
  public void testGetCustomerByIdNotFound() {
    assertThrows(NotFoundException.class, () -> controller.getCustomerById(UUID.randomUUID()));
  }

  @Rollback
  @Transactional
  @Test
  public void testEmptyList() {
    customerRepository.deleteAll();
    List<CustomerDTO> customerDTOList = controller.listCustomers();
    assertThat(customerDTOList).isNotNull();
    assertThat(customerDTOList.size()).isEqualTo(0);
  }

  @Rollback
  @Transactional
  @Test
  public void saveCustomer() {
    CustomerDTO customerDTO = CustomerDTO.builder().name("Test").build();
    CustomerDTO savedCustomer = controller.createCustomer(customerDTO).getBody();
    assertThat(savedCustomer).isNotNull();
    assertThat(savedCustomer.getId()).isNotNull();
  }

  @Rollback
  @Transactional
  @Test
  public void updateCustomer() {
    Customer customer = customerRepository.findAll().get(0);
    CustomerDTO customerDTO = CustomerDTO.builder()
        .id(customer.getId())
        .name("Updated Name")
        .build();

    ResponseEntity<CustomerDTO> responseEntity = controller.updateCustomer(customerDTO.getId(), customerDTO);
    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(responseEntity.getBody()).isNotNull();
    assertThat(responseEntity.getBody().getName()).isEqualTo("Updated Name");
    String[] locationUUID = responseEntity.getHeaders().getLocation().getPath().split("/");
    UUID customerId = UUID.fromString(locationUUID[4]);
    Customer updatedCustomer = customerRepository.findById(customerId).get();
    assertThat(updatedCustomer).isNotNull();
    assertThat(updatedCustomer.getName()).isEqualTo("Updated Name");
   }

  @Rollback
  @Transactional
  @Test
  void testUpdateNonExistingCustomer() throws Exception {
    CustomerDTO customerDTO = CustomerDTO.builder().build();
    assertThrows(NotFoundException.class, () -> controller.updateCustomer(UUID.randomUUID(), customerDTO));
  }

  @Rollback
  @Transactional
  @Test
  void testDeleteCustomer() throws Exception {
    Customer customer = customerRepository.findAll().get(0);
    ResponseEntity responseEntity = controller.deleteCustomer(customer.getId());
    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(customerRepository.findById(customer.getId())).isEmpty();
  }

  @Rollback
  @Transactional
  @Test
  void testDeleteNonExistingCustomer() throws Exception {
    assertThrows(NotFoundException.class, () -> controller.deleteCustomer(UUID.randomUUID()));
  }
}
