package guru.spring.spring7restmvc.models;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class CustomerDTO {
  private UUID id;
  private String name;
  private String version;
  private LocalDateTime createdDate;
  private LocalDateTime lastModifiedDate;
}
