package kg.uluk.reference.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import kg.uluk.reference.service.dto.CountryResponse;
import org.springframework.web.client.RestClient;

public class CountryInfoService {
  private final RestClient client;
  private final String apiKey;

  public CountryInfoService(RestClient client, String apiKey) {
    this.client = client;
    this.apiKey = apiKey;
  }

  public CountryResponse.Country fetchCountryInfo(Map<String, String> parameters) {
    String country = parameters.get("country");

    CountryResponse response =
        client
            .get()
            .uri("https://api.restcountries.com/countries/v5?q={country}", country)
            .header("Authorization", "Bearer " + apiKey)
            .retrieve()
            .body(CountryResponse.class);

    List<CountryResponse.Country> countries =
        Objects.requireNonNull(response).getData().getCountry();

    if (countries.isEmpty()) {
      throw new IllegalArgumentException("Country not found: " + country);
    }

    return countries.get(0);
  }
}
