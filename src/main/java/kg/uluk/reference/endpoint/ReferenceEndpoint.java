package kg.uluk.reference.endpoint;

import java.io.IOException;
import kg.uluk.reference.service.ReferenceMapper;
import kg.university.hub.xsd.producer.v1.*;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class ReferenceEndpoint {
  private static final String NAMESPACE = "urn:kg:university:hub:xsd:producer:v1";

  private final ReferenceMapper referenceMapper;

  public ReferenceEndpoint(ReferenceMapper referenceMapper) {
    this.referenceMapper = referenceMapper;
  }

  @PayloadRoot(namespace = NAMESPACE, localPart = "TicketRequest")
  @ResponsePayload
  public TicketCreateResponseType create(@RequestPayload TicketCreateRequestType request)
      throws IOException {
    return referenceMapper.create(request);
  }

  @PayloadRoot(namespace = NAMESPACE, localPart = "TicketRequest")
  @ResponsePayload
  public TicketConfirmResponseType confirm(@RequestPayload TicketConfirmRequestType request) {
    return referenceMapper.confirm(request.getTicketId());
  }

  @PayloadRoot(namespace = NAMESPACE, localPart = "TicketRequest")
  @ResponsePayload
  public TicketRetrieveResponseType retrieve(@RequestPayload TicketRetrieveRequestType request) {
    return referenceMapper.retrieve(request.getTicketId());
  }

  @PayloadRoot(namespace = NAMESPACE, localPart = "TicketRequest")
  @ResponsePayload
  public TicketCancelResponseType cancel(@RequestPayload TicketCancelRequestType request) {
    return referenceMapper.cancel(request.getTicketId());
  }
}
