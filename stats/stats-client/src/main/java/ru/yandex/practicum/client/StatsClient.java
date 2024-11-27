package ru.yandex.practicum.client;

import jakarta.annotation.Nullable;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Component
public class StatsClient {
   // String statsServer = "http://stats-server:9090";
    private final RestTemplate restTemplate;

    public StatsClient(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory("http://stats-server:9090"))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }

    /*public void save(ParamHitDto newStat) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ParamHitDto> requestEntity = new HttpEntity<>(newStat, httpHeaders);
        restTemplate.exchange(statsServer + "/hit", HttpMethod.POST, requestEntity, ParamHitDto.class);
    }*/

    public <T> ResponseEntity<Object> save(T body) {
        return makeAndSendRequest(HttpMethod.POST, "/hit", null, body);
    }

    public ResponseEntity<Object> getStat(String start, String end, List<String> uris, boolean unique) {
   /*     HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> requestEntity = new HttpEntity<>(httpHeaders);

        Map<String, Object> uriVariables = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique);

        String uri = statsServer + "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
        return restTemplate.exchange(uri, HttpMethod.GET, requestEntity,
                new ParameterizedTypeReference<List<StatDto>>() {
                },
                uriVariables).getBody();*/
        String urlTemplate = UriComponentsBuilder.fromPath("/stats")
                .queryParam("start", "{start}")
                .queryParam("end", "{end}")
                .queryParam("uris", "{uris}")
                .queryParam("unique", "{unique}")
                .encode()
                .toUriString();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start);
        parameters.put("end", end);
        parameters.put("uris", uris);
        parameters.put("unique", unique);

        return makeAndSendRequest(HttpMethod.GET, urlTemplate, parameters, null);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method,
                                                          String path,
                                                          @Nullable Map<String, Object> parameters,
                                                          @Nullable T body) {
        HttpEntity<T> requestEntity = null;
        if (body != null) {
            requestEntity = new HttpEntity<>(body);
        }

        ResponseEntity<Object> statsServerResponse;
        try {
            if (parameters != null) {
                statsServerResponse = restTemplate.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                statsServerResponse = restTemplate.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(statsServerResponse);
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        return responseBuilder.build();
    }
}
