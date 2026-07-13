package kg.uluk.reference.container;


public interface Container {
  String ENTRY_MIMETYPE = "mimetype";
  String ENTRY_MANIFEST = "META-INF/manifest.xml";
  String ENTRY_SIGNATURES = "META-INF/signatures-cds-kg.xml";
  String MIMETYPE_DIGITAL_REFERENCE_CONTAINER =
      "application/vnd.kg.university.reference.container+zip";
  String ENTRY_CREATE_RESPONSE_JSON = "create-response.json";
  String ENTRY_CONFIRM_RESPONSE_JSON = "confirm-response.json";
  String ENTRY_RETRIEVE_RESPONSE_JSON = "retrieve-response.json";
  String ENTRY_PRODUCT_JSON = "product.json";
  String ENTRY_PRODUCT_XML = "product.xml";
  String ENTRY_PRODUCT_PDF = "product.pdf";
  String ENTRY_DESCRIPTION_TXT = "description.txt";
  String REFERENCE_CONTAINER_NAME_PREFIX = "reference-container-";


  enum ContainerType {
    DIGITAL_REFERENCE_CONTAINER(MIMETYPE_DIGITAL_REFERENCE_CONTAINER);

    private final String mimeType;

    ContainerType(String mimeType) {
      this.mimeType = mimeType;
    }

    public String mimeType() {
      return mimeType;
    }
  }
}
