package guru.spring.spring7restmvc.mappers;

import guru.spring.spring7restmvc.entities.Customer;
import guru.spring.spring7restmvc.models.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {
  Customer customerDTOtoCustomer(CustomerDTO customerDTO);
  CustomerDTO customertoCustomerDTO(Customer customer);
}
