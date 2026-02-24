package guru.spring.spring7restmvc.entities;

import guru.spring.spring7restmvc.models.BeerStyle;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Beer {
  @Id
  @GeneratedValue(generator = "UUID")
//  @GenericGenerator(name="UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @UuidGenerator //from SpringBoot 3.4, no need to define the GenericGenerator
  @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID id;

  @Version
  private Integer version;

  @NotNull
  @NonNull
  @NotBlank
  @Size(min = 1, max = 50)
  @Column(length = 50)
  private String beerName;

  @NotNull
  @JdbcTypeCode(SqlTypes.TINYINT)
  private BeerStyle beerStyle;

  @NotNull
  @NotBlank
  @Size(min = 1, max = 255)
  private String upc;

  private Integer quantityOnHand;
  private BigDecimal price;

  @CreationTimestamp
  private LocalDateTime createdDate;

  @UpdateTimestamp
  private LocalDateTime updateDate;
}
