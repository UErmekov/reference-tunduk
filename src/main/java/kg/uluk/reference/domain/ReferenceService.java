package kg.uluk.reference.domain;

import jakarta.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import kg.uluk.reference.container.Container;
import kg.uluk.reference.container.ContainerHelper;
import kg.uluk.reference.container.Document;
import kg.uluk.reference.dto.response.ReferenceCancelResponse;
import kg.uluk.reference.dto.response.ReferenceConfirmResponse;
import kg.uluk.reference.dto.response.ReferenceCreateResponse;
import kg.uluk.reference.dto.response.ReferenceRetrieveResponse;
import kg.uluk.reference.service.CountryInfoService;
import kg.uluk.reference.service.dto.CountryResponse;
import tools.jackson.databind.ObjectMapper;

public class ReferenceService {
  private final ReferenceRepository repository;
  private final CountryInfoService countryInfoService;
  private final ObjectMapper objectMapper;

  public ReferenceService(
      ReferenceRepository repository,
      CountryInfoService countryInfoService,
      ObjectMapper objectMapper) {
    this.repository = repository;
    this.countryInfoService = countryInfoService;
    this.objectMapper = objectMapper;
  }

  @Transactional
  public ReferenceCreateResponse create(
      String ticketId, String owner, Map<String, String> params, BigDecimal price, Long duration)
      throws IOException {
    Optional<Reference> existing = repository.findByTicketId(ticketId);

    if (existing.isPresent()) {
      return toCreateResponse(existing.get());
    }

    CountryResponse.Country country = countryInfoService.fetchCountryInfo(params);

    byte[] jsonBytes = objectMapper.writeValueAsBytes(country);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Document jsonDoc =
        Document.ofBytes(
            UUID.randomUUID().toString(),
            Container.ENTRY_PRODUCT_JSON,
            "application/json",
            null,
            null,
            null,
            jsonBytes);
    ContainerHelper.create(
        out, List.of(jsonDoc), Container.ContainerType.DIGITAL_REFERENCE_CONTAINER);

    byte[] content = out.toByteArray();

    Reference reference = new Reference();
    reference.setTicketId(ticketId);
    reference.setOwner(owner);
    reference.setContent(content);
    reference.setPrice(price);
    reference.setDuration(duration);
    reference.setContentType(Container.MIMETYPE_DIGITAL_REFERENCE_CONTAINER);
    reference.setSize((long) content.length);
    reference.setState(ReferenceState.CREATED);

    Reference saved = repository.save(reference);

    return toCreateResponse(saved);
  }

  private ReferenceCreateResponse toCreateResponse(Reference reference) {
    return new ReferenceCreateResponse(
        reference.getTicketId(),
        reference.getDuration(),
        reference.getPrice(),
        reference.getContent(),
        reference.getSize(),
        reference.getState());
  }

  @Transactional
  public ReferenceConfirmResponse confirm(String ticketId) {
    Reference existing =
        repository
            .findByTicketId(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("Ticket id does not exist"));

    if (existing.getState() == ReferenceState.CONFIRMED) {
      return toConfirmResponse(existing);
    }

    if (existing.getState() != ReferenceState.CREATED) {
      throw new IllegalStateException("Cannot confirm from state: " + existing.getState());
    }

    existing.setState(ReferenceState.CONFIRMED);
    existing.setDeliveryTime(OffsetDateTime.now().plusSeconds(existing.getDuration()));
    Reference saved = repository.save(existing);

    return toConfirmResponse(saved);
  }

  private ReferenceConfirmResponse toConfirmResponse(Reference existing) {
    return new ReferenceConfirmResponse(
        existing.getContent(), existing.getSize(), existing.getDeliveryTime());
  }

  @Transactional
  public ReferenceCancelResponse cancel(String ticketId) {
    Reference existing =
        repository
            .findByTicketId(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("Ticket id does not exist"));

    if (existing.getState() == ReferenceState.CANCELED) {
      return toCancelResponse(existing);
    }

    if (existing.getState() != ReferenceState.CREATED) {
      throw new IllegalStateException("Cannot cancel from state: " + existing.getState());
    }

    existing.setState(ReferenceState.CANCELED);
    Reference saved = repository.save(existing);

    return toCancelResponse(saved);
  }

  private ReferenceCancelResponse toCancelResponse(Reference existing) {
    return new ReferenceCancelResponse(existing.getContent(), existing.getSize());
  }

  @Transactional
  public ReferenceRetrieveResponse retrieve(String ticketId) {
    Reference existing =
        repository
            .findByTicketId(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("Ticket id does not exist"));

    if (existing.getState() == ReferenceState.RETRIEVED) {
      return toRetrieveResponse(existing);
    }

    if (existing.getState() != ReferenceState.CONFIRMED) {
      throw new IllegalStateException("Cannot retrieve from state: " + existing.getState());
    }

    existing.setState(ReferenceState.RETRIEVED);
    Reference saved = repository.save(existing);

    return toRetrieveResponse(saved);
  }

  private ReferenceRetrieveResponse toRetrieveResponse(Reference existing) {
    return new ReferenceRetrieveResponse(existing.getContent(), existing.getSize());
  }
}
