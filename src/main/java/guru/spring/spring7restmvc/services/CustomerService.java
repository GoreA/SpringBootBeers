package guru.spring.spring7restmvc.services;

import guru.spring.spring7restmvc.models.CustomerDTO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {
  List<CustomerDTO> getCustomers();
  Optional<CustomerDTO> getCustomer(UUID id);

  CustomerDTO saveNewCustomer(CustomerDTO customerDTO);

  Optional<CustomerDTO> updateCustomer(UUID id, CustomerDTO customerDTO);

  Boolean deleteCustomer(UUID id);
}
