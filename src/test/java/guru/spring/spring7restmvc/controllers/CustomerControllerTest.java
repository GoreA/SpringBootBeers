package guru.spring.spring7restmvc.controllers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import guru.spring.spring7restmvc.mappers.CustomerMapper;
import guru.spring.spring7restmvc.models.CustomerDTO;
import guru.spring.spring7restmvc.repositories.CustomerRepository;
import guru.spring.spring7restmvc.services.CustomerServceImpl;
import guru.spring.spring7restmvc.services.CustomerService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {
  public static final String CUSTOMER_BASE_PATH = "/api/v1/customer";
  public static final String CUSTOMER_BASE_PATH_ID = "/api/v1/customer/{id}";
  @MockitoBean
  private CustomerService customerService;

  @Autowired
  ObjectMapper objectMapper;
  @Autowired
  MockMvc mockMvc;
  @MockitoBean
  private CustomerRepository customerRepository;
  @MockitoBean
  private CustomerMapper customerMapper;

  CustomerServceImpl customerServiceMock = new CustomerServceImpl(customerRepository, customerMapper);

  @Test
  void listCustomers() throws Exception {
    given(customerService.getCustomers()).willReturn(getCustomers());
    mockMvc.perform(MockMvcRequestBuilders.get(CUSTOMER_BASE_PATH)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()").value(3));
  }

  @Test
  void getCustomer() throws Exception {
    var testCustomer = getCustomers().get(0);
    given(customerService.getCustomer(any(UUID.class))).willReturn(Optional.ofNullable(testCustomer));
    mockMvc.perform(MockMvcRequestBuilders.get(CUSTOMER_BASE_PATH_ID, UUID.randomUUID())
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(testCustomer.getId().toString()))
        .andExpect(jsonPath("$.name").value(testCustomer.getName()));
  }

  @Test
  void createCustomer() throws Exception {
    var testCustomer = getCustomers().get(0);
    testCustomer.setId(UUID.randomUUID());
    testCustomer.setVersion(null);
    given(customerService.saveNewCustomer(any())).willReturn(testCustomer);
    mockMvc.perform(MockMvcRequestBuilders.post(CUSTOMER_BASE_PATH)
            .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(testCustomer)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(header().exists("Location"));
  }

  @Test
  void updateCustomer() throws Exception {
    var testCustomer = getCustomers().get(0);
    given(customerService.updateCustomer(any(UUID.class), any(CustomerDTO.class)))
        .willReturn(Optional.of(testCustomer));
    mockMvc.perform(MockMvcRequestBuilders.put(CUSTOMER_BASE_PATH_ID, UUID.randomUUID())
            .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(testCustomer)))
        .andExpect(status().isOk());

    verify(customerService).updateCustomer(any(UUID.class), any(CustomerDTO.class));
  }

  @Test
  void deleteCustomer() throws Exception {
    CustomerDTO testCustomerDTO = getCustomers().get(0);
    given(customerService.deleteCustomer(any(UUID.class))).willReturn(true);
    mockMvc.perform(MockMvcRequestBuilders.delete(CUSTOMER_BASE_PATH_ID, testCustomerDTO.getId())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
    verify(customerService).deleteCustomer(captor.capture());
    assertThat(captor.getValue()).isEqualTo(testCustomerDTO.getId());
  }

  private List<CustomerDTO> getCustomers() {
    return List.of(
        CustomerDTO.builder().id(UUID.randomUUID()).name("Customer 1").build(),
        CustomerDTO.builder().id(UUID.randomUUID()).name("Customer 2").build(),
        CustomerDTO.builder().id(UUID.randomUUID()).name("Customer 3").build()
    );
  }

}
