package kg.uluk.reference.dto.response;

import java.time.OffsetDateTime;

public record ReferenceConfirmResponse(byte[] content, Long size, OffsetDateTime deliveryTime) {}
