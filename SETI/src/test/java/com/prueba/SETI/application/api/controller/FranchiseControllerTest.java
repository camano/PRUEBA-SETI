package com.prueba.SETI.application.api.controller;

import com.prueba.SETI.application.api.request.CreateFranchiseRequest;
import com.prueba.SETI.application.api.request.UpdateNameRequest;
import com.prueba.SETI.application.service.FranchiseService;
import com.prueba.SETI.domain.model.Franchise;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = FranchiseController.class)
class FranchiseControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private FranchiseService franchiseService;

    @Test
    void shouldCreateFranchiseSuccessfully() {
        // given
        CreateFranchiseRequest request =
                new CreateFranchiseRequest("Franquicia Test");

        Franchise franchise =
                new Franchise("F-001", "Franquicia Test");

        when(franchiseService.execute(Mockito.any(CreateFranchiseRequest.class)))
                .thenReturn(Mono.just(franchise));

        // when & then
        webTestClient.post()
                .uri("/api/franchise/add")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo("F-001")
                .jsonPath("$.name").isEqualTo("Franquicia Test");
    }

    @Test
    void shouldUpdateFranchiseNameSuccessfully() {
        String franchiseId = "F-001";
        UpdateNameRequest request = new UpdateNameRequest("Nuevo Nombre");

        Franchise updated = new Franchise(franchiseId, "Nuevo Nombre");

        when(franchiseService.updateFranchiseName(eq(franchiseId), eq("Nuevo Nombre")))
                .thenReturn(Mono.just(updated));

        webTestClient.patch()
                .uri("/api/franchise/{id}/name", franchiseId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(franchiseId)
                .jsonPath("$.name").isEqualTo("Nuevo Nombre");
    }

}