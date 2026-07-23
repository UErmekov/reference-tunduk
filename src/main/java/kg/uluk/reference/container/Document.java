package kg.uluk.reference.container;

import static kg.uluk.reference.container.Container.ENTRY_MANIFEST;
import static kg.uluk.reference.container.Container.ENTRY_MIMETYPE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class Document {
  private final String path;
  private final String mediaType;
  private final InputStreamSupplier streamSupplier;

  public Document(
      String id,
      String path,
      String mediaType,
      String digestAlg,
      byte[] digestValue,
      byte[] signature,
      InputStreamSupplier streamSupplier) {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("ID could not be null");
    }

    if (digestAlg != null && !digestAlg.trim().isBlank()) {
      if (digestValue == null) {
        throw new IllegalArgumentException("Digest value could not be null");
      }
      if (signature == null) {
        throw new IllegalArgumentException("Signature could not be null");
      }
    }

    if (path == null || path.isBlank()) {
      throw new IllegalArgumentException("Path could not be null");
    }

    if (streamSupplier == null) {
      throw new IllegalArgumentException("Stream supplier could not be null");
    }

    this.path = normalizePath(path);
    validatePayloadPath(this.path);

    this.mediaType =
        (mediaType == null || mediaType.isBlank()) ? "application/octet-stream" : mediaType;

    this.streamSupplier = streamSupplier;
  }

  public static Document ofBytes(
      String id,
      String path,
      String mediaType,
      String digestAlg,
      byte[] digestValue,
      byte[] signature,
      byte[] content) {
    Objects.requireNonNull(content, "content");
    return new Document(
        id,
        path,
        mediaType,
        digestAlg,
        digestValue,
        signature,
        () -> new ByteArrayInputStream(content));
  }

  public String path() {
    return path;
  }

  public String mediaType() {
    return mediaType;
  }

  public InputStreamSupplier streamSupplier() {
    return streamSupplier;
  }

  private String normalizePath(String path) {
    String p = path.replace('\\', '/');
    while (p.startsWith("/")) p = p.substring(1);
    return p;
  }

  private void validatePayloadPath(String path) {
    if (path.isBlank()) throw new IllegalArgumentException("Empty payload path");
    if (path.contains(".."))
      throw new IllegalArgumentException("Path traversal is not allowed: " + path);
    if (ENTRY_MIMETYPE.equals(path) || ENTRY_MANIFEST.equals(path)) {
      throw new IllegalArgumentException("Reserved entry name: " + path);
    }
  }

  @FunctionalInterface
  public interface InputStreamSupplier {
    InputStream get() throws IOException;
  }
}
