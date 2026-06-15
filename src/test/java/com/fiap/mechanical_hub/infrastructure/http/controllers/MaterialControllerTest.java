package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.command.material.CreateMaterialCommand;
import com.fiap.mechanical_hub.application.command.material.UpdateMaterialCommand;
import com.fiap.mechanical_hub.application.dto.material.InsertMaterialRequest;
import com.fiap.mechanical_hub.application.dto.material.MaterialResponse;
import com.fiap.mechanical_hub.application.dto.material.UpdateMaterialRequest;
import com.fiap.mechanical_hub.application.mappers.MaterialMapper;
import com.fiap.mechanical_hub.application.usecases.material.CreateMaterialUseCase;
import com.fiap.mechanical_hub.application.usecases.material.DeleteMaterialUseCase;
import com.fiap.mechanical_hub.application.usecases.material.FindAllMaterialsUseCase;
import com.fiap.mechanical_hub.application.usecases.material.FindMaterialByIdUseCase;
import com.fiap.mechanical_hub.application.usecases.material.UpdateMaterialUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MaterialControllerTest {

    private static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final MaterialMapper mapper = mock(MaterialMapper.class);
    private final CreateMaterialUseCase createMaterialUseCase = mock(CreateMaterialUseCase.class);
    private final FindMaterialByIdUseCase findMaterialByIdUseCase = mock(FindMaterialByIdUseCase.class);
    private final FindAllMaterialsUseCase findAllMaterialsUseCase = mock(FindAllMaterialsUseCase.class);
    private final UpdateMaterialUseCase updateMaterialUseCase = mock(UpdateMaterialUseCase.class);
    private final DeleteMaterialUseCase deleteMaterialUseCase = mock(DeleteMaterialUseCase.class);

    private final MaterialController controller = new MaterialController(
            mapper, createMaterialUseCase, findMaterialByIdUseCase,
            findAllMaterialsUseCase, updateMaterialUseCase, deleteMaterialUseCase
    );

    private MaterialResponse buildMaterialResponse() {
        return new MaterialResponse(
                MATERIAL_ID, "Filtro de óleo", "Filtro para motor", BigDecimal.valueOf(25.00),
                5, LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    void shouldReturnCreated_whenMaterialIsCreated() {
        InsertMaterialRequest request = new InsertMaterialRequest("Filtro de óleo", "desc", BigDecimal.valueOf(25.00), 5);
        when(mapper.toCreateCommand(any())).thenReturn(mock(CreateMaterialCommand.class));
        when(createMaterialUseCase.execute(any())).thenReturn(buildMaterialResponse());

        ResponseEntity<MaterialResponse> response = controller.create(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldDelegateToCreateUseCase_whenCreatingMaterial() {
        InsertMaterialRequest request = new InsertMaterialRequest("Filtro de óleo", "desc", BigDecimal.valueOf(25.00), 5);
        when(mapper.toCreateCommand(any())).thenReturn(mock(CreateMaterialCommand.class));
        when(createMaterialUseCase.execute(any())).thenReturn(buildMaterialResponse());

        controller.create(request);

        verify(createMaterialUseCase).execute(any());
    }

    @Test
    void shouldReturnOk_whenFindingAllMaterials() {
        when(findAllMaterialsUseCase.execute()).thenReturn(List.of(buildMaterialResponse()));

        ResponseEntity<List<MaterialResponse>> response = controller.findAll();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void shouldReturnOk_whenFindingMaterialById() {
        when(findMaterialByIdUseCase.execute(MATERIAL_ID)).thenReturn(buildMaterialResponse());

        ResponseEntity<MaterialResponse> response = controller.findById(MATERIAL_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldReturnOk_whenUpdatingMaterial() {
        UpdateMaterialRequest request = new UpdateMaterialRequest("Filtro Updated", "desc", BigDecimal.valueOf(30.00), 10);
        when(mapper.toUpdateCommand(any(), any())).thenReturn(mock(UpdateMaterialCommand.class));
        when(updateMaterialUseCase.execute(any())).thenReturn(buildMaterialResponse());

        ResponseEntity<MaterialResponse> response = controller.update(MATERIAL_ID, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldReturnNoContent_whenDeletingMaterial() {
        ResponseEntity<Void> response = controller.delete(MATERIAL_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(deleteMaterialUseCase).execute(MATERIAL_ID);
    }
}
