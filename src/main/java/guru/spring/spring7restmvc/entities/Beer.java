package guru.spring.spring7restmvc.entities;

import guru.spring.spring7restmvc.models.BeerStyle;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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

  @OneToMany(mappedBy = "beer")
  private Set<BeerOrderLine> beerOrderLines = new HashSet<>();

  @Builder.Default // initialize the Set to avoid NullPointerException when trying to add a Category to the Set
  @ManyToMany(mappedBy = "beers")
  //  @JoinTable(name = "beer_category",
  //      joinColumns = @JoinColumn(name = "beer_id"),
  //      inverseJoinColumns = @JoinColumn(name = "category_id"))
  // no need to define the JoinTable here since it's already defined in the Category entity,
  // and this is the inverse side of the relationship
  private Set<Category> categories = new HashSet<>();

  public void addCategory(Category category) {
    this.categories.add(category);
    category.getBeers().add(this);
  }

  public void removeCategory(Category category) {
    this.categories.remove(category);
    category.getBeers().remove(this);
  }

}
