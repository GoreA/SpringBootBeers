package guru.spring.spring7restmvc.entities;

import jakarta.persistence.*;
import java.util.Set;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
//@AllArgsConstructor
@Builder
public class BeerOrder {

  public BeerOrder(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate, String customerRef,
                   Customer customer, Set<BeerOrderLine> beerOrderLines) {
    this.id = id;
    this.version = version;
    this.createdDate = createdDate;
    this.lastModifiedDate = lastModifiedDate;
    this.customerRef = customerRef;
    setCustomer(customer); //added to set the bi-directional relationship
    this.beerOrderLines = beerOrderLines;
  }

  @Id
  @GeneratedValue(generator = "UUID")
  @UuidGenerator
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false )
  private UUID id;

  @Version
  private Long version;

  @CreationTimestamp
  @Column(updatable = false)
  private Timestamp createdDate;

  @UpdateTimestamp
  private Timestamp lastModifiedDate;

  public boolean isNew() {
    return this.id == null;
  }

  private String customerRef;

  // added to set the bi-directional relationship
  public void setCustomer(Customer customer) {
    this.customer = customer;
    customer.getBeerOrders().add(this);
  }

  @ManyToOne
  private Customer customer;

  @OneToMany(mappedBy = "beerOrder")
  private Set<BeerOrderLine> beerOrderLines;
}
