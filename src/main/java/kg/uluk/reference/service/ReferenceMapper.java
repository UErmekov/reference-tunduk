package kg.uluk.reference.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;
import kg.uluk.reference.domain.ReferenceService;
import kg.uluk.reference.dto.response.ReferenceCancelResponse;
import kg.uluk.reference.dto.response.ReferenceConfirmResponse;
import kg.uluk.reference.dto.response.ReferenceCreateResponse;
import kg.uluk.reference.dto.response.ReferenceRetrieveResponse;
import kg.university.hub.xsd.producer.v1.ParamType;
import kg.university.hub.xsd.producer.v1.TicketCancelResponseType;
import kg.university.hub.xsd.producer.v1.TicketConfirmResponseType;
import kg.university.hub.xsd.producer.v1.TicketCreateRequestType;
import kg.university.hub.xsd.producer.v1.TicketCreateResponseType;
import kg.university.hub.xsd.producer.v1.TicketRetrieveResponseType;

public class ReferenceMapper {
  private static final BigDecimal PRICE = BigDecimal.valueOf(160.0);
  private static final Long DURATION = 60L;

  private final ReferenceService referenceService;

  public ReferenceMapper(ReferenceService referenceService) {
    this.referenceService = referenceService;
  }

  public TicketCreateResponseType create(TicketCreateRequestType request) {
    if (request.getParams().getParam() == null) {
      throw new IllegalArgumentException("Params is required");
    }

    Map<String, String> params =
        request.getParams().getParam().stream()
            .collect(Collectors.toMap(ParamType::getKey, ParamType::getValue));

    ReferenceCreateResponse response =
        referenceService.create(request.getTicketId(), request.getOwner(), params, PRICE, DURATION);

    TicketCreateResponseType result = new TicketCreateResponseType();
    result.setContent(response.content());
    result.setSize(response.size());
    result.setPrice(response.price());
    result.setDuration(response.duration());
    return result;
  }

  public TicketConfirmResponseType confirm(String ticketId) {
    ReferenceConfirmResponse response = referenceService.confirm(ticketId);

    TicketConfirmResponseType result = new TicketConfirmResponseType();
    result.setContent(response.content());
    result.setSize(response.size());
    result.setDeliveryTime(response.deliveryTime());
    return result;
  }

  public TicketRetrieveResponseType retrieve(String ticketId) {
    ReferenceRetrieveResponse response = referenceService.retrieve(ticketId);

    TicketRetrieveResponseType result = new TicketRetrieveResponseType();
    result.setContent(response.content());
    result.setSize(response.size());
    return result;
  }

  public TicketCancelResponseType cancel(String ticketId) {
    ReferenceCancelResponse response = referenceService.cancel(ticketId);

    TicketCancelResponseType result = new TicketCancelResponseType();
    result.setContent(response.content());
    result.setSize(response.size());
    return result;
  }
}
