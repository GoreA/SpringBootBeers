package guru.spring.spring7restmvc.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
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
public class Customer {
  @Id
  @GeneratedValue(generator = "UUID")
  @UuidGenerator //from SpringBoot 3.4, no need to define the GenericGenerator
  @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID id;

  private String name;

  @Column(length = 255, unique = true)
  private String email;

  @Version
  private Integer version;

  @CreationTimestamp
  private LocalDateTime createdDate;

  @UpdateTimestamp
  private LocalDateTime lastModifiedDate;

  // added to set the bi-directional relationship
  // initialized by default so that we don't get NullPointerException when trying to add a BeerOrder to the Set
  @Builder.Default
  @OneToMany(mappedBy = "customer")
  private Set<BeerOrder> beerOrders = new HashSet<>();
}
