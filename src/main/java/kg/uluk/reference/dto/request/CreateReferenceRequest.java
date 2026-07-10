package kg.uluk.reference.dto.request;

import java.math.BigDecimal;
import java.util.Map;

public record CreateReferenceRequest(
    String ticketId, BigDecimal price, String owner, Map<String, String> params, Long duration) {}
