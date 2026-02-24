package guru.spring.spring7restmvc.services;

import guru.spring.spring7restmvc.entities.Customer;
import guru.spring.spring7restmvc.mappers.CustomerMapper;
import guru.spring.spring7restmvc.models.CustomerDTO;
import guru.spring.spring7restmvc.repositories.CustomerRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServceImpl implements CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;

  @Override
  public List<CustomerDTO> getCustomers() {
    List<Customer> customers = customerRepository.findAll();
    List<CustomerDTO> customerDTOs = customers.stream()
        .map(customerMapper::customertoCustomerDTO).toList();
    return customerDTOs;
  }

  @Override
  public Optional<CustomerDTO> getCustomer(UUID id) {

    Customer customer = customerRepository.findById(id).orElse(null);
    return Optional.ofNullable(customerMapper.customertoCustomerDTO(customer));
  }

  @Override
  public CustomerDTO saveNewCustomer(CustomerDTO customerDTO) {
    Customer customer = customerMapper.customerDTOtoCustomer(customerDTO);

    customerRepository.save(customer);
    CustomerDTO newCustomerDTO = customerMapper.customertoCustomerDTO(customer);
    return newCustomerDTO;
  }

  @Override
  public Optional<CustomerDTO> updateCustomer(UUID id, CustomerDTO customerDTO) {
    AtomicReference<Optional<CustomerDTO>> customerOptional = new AtomicReference<>();
    customerRepository.findById(id).ifPresentOrElse(foundCustomer -> {
      foundCustomer.setName(customerDTO.getName() == null
          || customerDTO.getName().isEmpty()
          ? foundCustomer.getName() : customerDTO.getName());
      foundCustomer.setLastModifiedDate(LocalDateTime.now());
      customerRepository.save(foundCustomer);
      customerOptional.set(Optional.of(customerMapper.customertoCustomerDTO(foundCustomer)));
    }, () -> {
      customerOptional.set(Optional.empty());
    });
    return customerOptional.get();
  }

  @Override
  public Boolean deleteCustomer(UUID id) {
    if (customerRepository.existsById(id)) {
      customerRepository.deleteById(id);
      return true;
    }
    return false;
  }
}
