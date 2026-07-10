package kg.uluk.reference.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountryResponse {
  private Data data;

  @Getter
  @Setter
  public static class Data {
    @JsonProperty("objects")
    private List<Country> country;
  }

  @Getter
  @Setter
  public static class Country {
    private Names names;
    private List<Capital> capitals;
    private long population;
    private Area area;
    private String region;
    private String subregion;
    private List<Language> languages;
    private List<Currency> currencies;

    @JsonProperty("government_type")
    private String governmentType;

    private List<String> timezones;
    private List<String> borders;
    private boolean landlocked;
  }

  @Getter
  @Setter
  public static class Names {
    private String common;
    private String official;

    @JsonProperty("native")
    private Map<String, NativeName> nativeNames;
  }

  @Getter
  @Setter
  public static class NativeName {
    private String common;
    private String official;
  }

  @Getter
  @Setter
  public static class Capital {
    private String name;
  }

  @Getter
  @Setter
  public static class Area {
    private double kilometers;
  }

  @Getter
  @Setter
  public static class Language {
    private String name;

    @JsonProperty("native_name")
    private String nativeName;
  }

  @Getter
  @Setter
  public static class Currency {
    private String code;
    private String name;
    private String symbol;
  }
}
