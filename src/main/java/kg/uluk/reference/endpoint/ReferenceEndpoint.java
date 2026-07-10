package kg.uluk.reference.endpoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import kg.uluk.reference.service.ReferenceMapper;
import kg.university.hub.xsd.producer.v1.TicketCancelResponseType;
import kg.university.hub.xsd.producer.v1.TicketConfirmResponseType;
import kg.university.hub.xsd.producer.v1.TicketCreateRequestType;
import kg.university.hub.xsd.producer.v1.TicketCreateResponseType;
import kg.university.hub.xsd.producer.v1.TicketRetrieveResponseType;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpServletConnection;

@Endpoint
public class ReferenceEndpoint {
  private static final String NAMESPACE = "urn:kg:university:hub:xsd:producer:v1";

  private final ReferenceMapper referenceMapper;

  public ReferenceEndpoint(ReferenceMapper referenceMapper) {
    this.referenceMapper = referenceMapper;
  }

  @PayloadRoot(namespace = NAMESPACE, localPart = "TicketRequest")
  @ResponsePayload
  public JAXBElement<?> handle(@RequestPayload TicketCreateRequestType request) {
    String action = extractSoapAction();

    return switch (action) {
      case "create" -> new JAXBElement<>(
          new QName(NAMESPACE, "TicketCreateResponse"),
          TicketCreateResponseType.class,
          referenceMapper.create(request));
      case "confirm" -> new JAXBElement<>(
          new QName(NAMESPACE, "TicketConfirmResponse"),
          TicketConfirmResponseType.class,
          referenceMapper.confirm(request.getTicketId()));
      case "retrieve" -> new JAXBElement<>(
          new QName(NAMESPACE, "TicketRetrieveResponse"),
          TicketRetrieveResponseType.class,
          referenceMapper.retrieve(request.getTicketId()));
      case "cancel" -> new JAXBElement<>(
          new QName(NAMESPACE, "TicketCancelResponse"),
          TicketCancelResponseType.class,
          referenceMapper.cancel(request.getTicketId()));
      default -> throw new IllegalArgumentException("Unknown SOAP action: " + action);
    };
  }

  private String extractSoapAction() {
    var transportContext = TransportContextHolder.getTransportContext();
    if (transportContext != null
        && transportContext.getConnection() instanceof HttpServletConnection httpConnection) {
      HttpServletRequest httpRequest = httpConnection.getHttpServletRequest();
      String action = httpRequest.getHeader("SOAPAction");
      if (action != null) {
        return action.replace("\"", "").trim();
      }
    }
    return "";
  }
}
