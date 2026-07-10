package kg.uluk.reference.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "refs")
public class Reference {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(name = "ticket_id")
  private String ticketId;

  private String owner;

  @Enumerated(EnumType.STRING)
  private ReferenceState state;

  @Lob private byte[] content;

  @Column(name = "content_type")
  private String contentType;

  private Long size;
  private BigDecimal price;
  private Long duration;

  @Column(name = "delivery_time")
  private OffsetDateTime deliveryTime;

  @Column(name = "created_at")
  private OffsetDateTime createdAt;

  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = OffsetDateTime.now();
    updatedAt = OffsetDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = OffsetDateTime.now();
  }
}
