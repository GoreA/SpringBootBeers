package guru.spring.spring7restmvc.controllers;

import guru.spring.spring7restmvc.exceptions.NotFoundException;
import guru.spring.spring7restmvc.models.CustomerDTO;
import guru.spring.spring7restmvc.services.CustomerService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

  private final CustomerService customerService;

  @RequestMapping(method = RequestMethod.GET)
  public List<CustomerDTO> listCustomers() {
    return customerService.getCustomers();
  }

  @GetMapping(path = "{id}")
  public CustomerDTO getCustomerById(@PathVariable("id") UUID id) {
    log.info("Get customer by id {}", id);
    return customerService.getCustomer(id).orElseThrow(NotFoundException::new);
  }

  @PostMapping
  public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerDTO) {
    log.info("Create new customer {}", customerDTO.getName());
    CustomerDTO savedCustomerDTO = customerService.saveNewCustomer(customerDTO);
    HttpHeaders headers = new HttpHeaders();
    headers.add("Location", "/api/v1/customer/" + savedCustomerDTO.getId().toString());
    return ResponseEntity.ok().headers(headers).body(savedCustomerDTO);
  }

  @PutMapping(path = "{id}")
  public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable("id") UUID id, @RequestBody CustomerDTO customerDTO) {
    log.info("Update customer with id {}", id);
    Optional<CustomerDTO> updatedCustomerDTOOptional = customerService.updateCustomer(id, customerDTO);
    CustomerDTO updatedCustomerDTO = updatedCustomerDTOOptional
        .orElseThrow(NotFoundException::new);
    HttpHeaders headers = new HttpHeaders();
    headers.add("Location", "/api/v1/customer/" + updatedCustomerDTO.getId().toString());
    return ResponseEntity.ok().headers(headers).body(updatedCustomerDTO);
  }

  @DeleteMapping(path = "{id}")
  public ResponseEntity<Void> deleteCustomer(@PathVariable("id") UUID id) {
    log.info("Delete customer with id {}", id);
    if(!customerService.deleteCustomer(id))
      throw new NotFoundException();
    return ResponseEntity.noContent().build();
  }
}
