package kg.uluk.reference.dto.response;

import java.math.BigDecimal;
import kg.uluk.reference.domain.ReferenceState;

public record ReferenceCreateResponse(
    String ticketId,
    Long duration,
    BigDecimal price,
    byte[] content,
    Long size,
    ReferenceState state) {}
