package guru.spring.spring7restmvc.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import tools.jackson.databind.annotation.JsonDeserialize;

@Builder
@Data
@AllArgsConstructor
@JsonDeserialize(builder = BeerDTO.BeerDTOBuilder.class)
public class BeerDTO {
  @JsonProperty("id")
  private UUID id;

  @JsonProperty("version")
  private Integer version;

  @JsonProperty("beerName")
  @NotBlank
  @NotNull
  @Size(min = 1, max = 50, message = "size must be between 1 and 50")
  private String beerName;

  @NotNull
  @JsonProperty("beerStyle")
  private BeerStyle beerStyle;

  @NotBlank
  @NotNull
  @JsonProperty("upc")
  @Size(min = 1, max = 255)
  private String upc;

  @JsonProperty("quantityOnHand")
  private Integer quantityOnHand;

  @JsonProperty("price")
  private BigDecimal price;

  private LocalDateTime createdDate;
  private LocalDateTime updateDate;
}
