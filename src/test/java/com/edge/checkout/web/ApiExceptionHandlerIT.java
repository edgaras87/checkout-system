package com.edge.checkout.web;

import com.edge.checkout.testsupport.AbstractWebSmokeIT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(ApiExceptionHandlerIT.TestApiErrorProbeConfig.class)
class ApiExceptionHandlerIT extends AbstractWebSmokeIT {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void body_validation_error_returns_problem_detail_with_errors() throws Exception {
    ResponseEntity<String> response =
      http.postForEntity(
        "/test/error-probe/validate-body",
        new ProbeRequest(""),
        String.class
      );

    JsonNode body = readBody(response);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    assertThat(body.get("title").asText()).isEqualTo("Validation failed");
    assertThat(body.get("status").asInt()).isEqualTo(400);
    assertThat(body.get("detail").asText()).isEqualTo("Request body validation failed");

    assertThat(body.has("errors")).isTrue();
    assertThat(body.get("errors").get("name").asText()).isEqualTo("must not be blank");
  }

  @Test
  void request_parameter_validation_error_returns_problem_detail_with_errors() throws Exception {
    ResponseEntity<String> response =
      http.getForEntity(
        "/test/error-probe/validate-parameter?name=",
        String.class
      );

    JsonNode body = readBody(response);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    assertThat(body.get("title").asText()).isEqualTo("Validation failed");
    assertThat(body.get("status").asInt()).isEqualTo(400);
    assertThat(body.get("detail").asText()).isEqualTo("Request parameter validation failed");

    assertThat(body.has("errors")).isTrue();
    assertThat(body.get("errors").size()).isGreaterThan(0);
    assertThat(errorMessages(body)).contains("must not be blank");
  }

  @Test
  void malformed_json_returns_problem_detail() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> request = new HttpEntity<>("{ invalid-json", headers);

    ResponseEntity<String> response =
      http.postForEntity(
        "/test/error-probe/validate-body",
        request,
        String.class
      );

    JsonNode body = readBody(response);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    assertThat(body.get("title").asText()).isEqualTo("Malformed JSON");
    assertThat(body.get("status").asInt()).isEqualTo(400);
    assertThat(body.get("detail").asText()).isEqualTo("Request body is not valid JSON");

    assertThat(body.has("errors")).isFalse();
  }

  @Test
  void illegal_argument_returns_bad_request_problem_detail() throws Exception {
    ResponseEntity<String> response =
      http.getForEntity(
        "/test/error-probe/bad-request",
        String.class
      );

    JsonNode body = readBody(response);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    assertThat(body.get("title").asText()).isEqualTo("Bad request");
    assertThat(body.get("status").asInt()).isEqualTo(400);
    assertThat(body.get("detail").asText()).isEqualTo("bad probe request");

    assertThat(body.has("errors")).isFalse();
  }

  @Test
  void invariant_violation_returns_internal_server_error_problem_detail() throws Exception {
    ResponseEntity<String> response =
      http.getForEntity(
        "/test/error-probe/invariant",
        String.class
      );

    JsonNode body = readBody(response);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

    assertThat(body.get("title").asText()).isEqualTo("Invariant violated");
    assertThat(body.get("status").asInt()).isEqualTo(500);
    assertThat(body.get("detail").asText())
      .isEqualTo("A server-side invariant was violated. See logs for details.");

    assertThat(body.has("errors")).isFalse();
  }

  private JsonNode readBody(ResponseEntity<String> response) throws Exception {
    assertThat(response.getBody()).isNotNull();
    return objectMapper.readTree(response.getBody());
  }

  private List<String> errorMessages(JsonNode body) {
    List<String> messages = new ArrayList<>();
    body.get("errors").properties().forEach(entry -> messages.add(entry.getValue().asText()));
    return messages;
  }

  record ProbeRequest(@NotBlank String name) {
  }

  @TestConfiguration
  static class TestApiErrorProbeConfig {

    @Bean
    TestApiErrorProbeController testApiErrorProbeController() {
      return new TestApiErrorProbeController();
    }
  }

  @RestController
  static class TestApiErrorProbeController {

    @PostMapping("/test/error-probe/validate-body")
    void validateBody(@Valid @RequestBody ProbeRequest request) {
      // Validation happens before the method body matters.
    }

    @GetMapping("/test/error-probe/validate-parameter")
    void validateParameter(@RequestParam @NotBlank String name) {
      // Validation happens before the method body matters.
    }

    @GetMapping("/test/error-probe/bad-request")
    void badRequest() {
      throw new IllegalArgumentException("bad probe request");
    }

    @GetMapping("/test/error-probe/invariant")
    void invariant() {
      throw new IllegalStateException("probe invariant violation");
    }
  }
}
