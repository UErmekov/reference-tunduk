package kg.uluk.reference.config;

import kg.uluk.reference.domain.ReferenceRepository;
import kg.uluk.reference.domain.ReferenceService;
import kg.uluk.reference.service.CountryInfoService;
import kg.uluk.reference.service.ReferenceMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class CommonConfiguration {

  @Bean
  public RestClient restClient() {
    return RestClient.create();
  }

  @Bean
  public CountryInfoService countryInfoService(
      RestClient restClient, @Value("${services.apikey.key}") String apiKey) {
    return new CountryInfoService(restClient, apiKey);
  }

  @Bean
  public ReferenceService referenceService(
      ReferenceRepository referenceRepository,
      CountryInfoService countryInfoService,
      ObjectMapper objectMapper) {
    return new ReferenceService(referenceRepository, countryInfoService, objectMapper);
  }

  @Bean
  public ReferenceMapper referenceMapper(ReferenceService referenceService) {
    return new ReferenceMapper(referenceService);
  }
}
