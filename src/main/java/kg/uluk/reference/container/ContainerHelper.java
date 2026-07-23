package kg.uluk.reference.container;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ContainerHelper {
  private ContainerHelper() {}

  public static void create(
      OutputStream out, List<Document> documents, Container.ContainerType containerType)
      throws IOException {
    Objects.requireNonNull(out, "out");
    Objects.requireNonNull(documents, "documents");
    Objects.requireNonNull(containerType, "containerType");

    Map<String, String> manifestEntries = new LinkedHashMap<>();

    try (ZipOutputStream zos = new ZipOutputStream(out, StandardCharsets.UTF_8)) {
      addStoredEntry(
          zos,
          Container.ENTRY_MIMETYPE,
          containerType.mimeType().getBytes(StandardCharsets.US_ASCII));

      for (Document document : documents) {
        addDeflatedEntryFromStream(zos, document.path(), document.streamSupplier());
        manifestEntries.put(document.path(), document.mediaType());
      }

      addDeflatedEntry(
          zos,
          Container.ENTRY_MANIFEST,
          buildManifestXml(containerType, manifestEntries).getBytes(StandardCharsets.UTF_8));

      addDeflatedEntry(
          zos, Container.ENTRY_SIGNATURES, "<signatures/>".getBytes(StandardCharsets.UTF_8));

      zos.finish();
    }
  }

  private static void addStoredEntry(ZipOutputStream zos, String name, byte[] data)
      throws IOException {
    CRC32 crc = new CRC32();
    crc.update(data);

    ZipEntry entry = new ZipEntry(name);
    entry.setMethod(ZipEntry.STORED);
    entry.setSize(data.length);
    entry.setCompressedSize(data.length);
    entry.setCrc(crc.getValue());

    zos.putNextEntry(entry);
    zos.write(data);
    zos.closeEntry();
  }

  private static void addDeflatedEntry(ZipOutputStream zos, String name, byte[] data)
      throws IOException {
    ZipEntry entry = new ZipEntry(name);
    entry.setMethod(ZipEntry.DEFLATED);
    zos.putNextEntry(entry);
    zos.write(data);
    zos.closeEntry();
  }

  private static void addDeflatedEntryFromStream(
      ZipOutputStream zos, String name, Document.InputStreamSupplier supplier) throws IOException {
    ZipEntry entry = new ZipEntry(name);
    entry.setMethod(ZipEntry.DEFLATED);
    zos.putNextEntry(entry);

    byte[] buffer = new byte[8192];
    try (InputStream in = supplier.get()) {
      if (in == null) throw new IOException("InputStreamSupplier returned null for: " + name);
      int read;
      while ((read = in.read(buffer)) != -1) {
        zos.write(buffer, 0, read);
      }
    } finally {
      zos.closeEntry();
    }
  }

  private static String buildManifestXml(
      Container.ContainerType containerType, Map<String, String> entries) {
    StringBuilder sb = new StringBuilder(1024);
    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    sb.append("<manifest:manifest ")
        .append("xmlns:manifest=\"urn:oasis:names:tc:opendocument:xmlns:manifest:1.0\">\n");

    sb.append("  <manifest:file-entry manifest:full-path=\"/\" manifest:media-type=\"")
        .append(escapeXml(containerType.mimeType()))
        .append("\"/>\n");

    for (Map.Entry<String, String> e : entries.entrySet()) {
      sb.append("  <manifest:file-entry manifest:full-path=\"")
          .append(escapeXml(e.getKey()))
          .append("\" manifest:media-type=\"")
          .append(escapeXml(e.getValue()))
          .append("\"/>\n");
    }

    sb.append("</manifest:manifest>\n");
    return sb.toString();
  }

  private static String escapeXml(String s) {
    return s.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;");
  }
}
