package com.fiap.mechanical_hub.infrastructure.http.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

/*
@WebMvcTest(CustomerController.class)
@DisplayName("CustomerController")
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateCustomerUseCase createCustomerUseCase;

    @MockBean
    private FindCustomerByIdUseCase findCustomerByIdUseCase;

    @MockBean
    private FindAllCustomersUseCase findAllCustomersUseCase;

    @MockBean
    private UpdateCustomerUseCase updateCustomerUseCase;

    @MockBean
    private DeleteCustomerUseCase deleteCustomerUseCase;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserJpaRepository userJpaRepository;

    @MockBean
    private AuthorizationUseCase authorizationUseCase;

    private UUID customerId;
    private CustomerResponse customerResponse;
    private InsertCustomerRequest upsertRequest;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();

        upsertRequest = new InsertCustomerRequest(
                "João da Silva",
                "CPF",
                "529.982.247-25",
                "(11) 99999-0000",
                "joao@email.com",
                "Rua das Flores, 123 - São Paulo/SP"
        );

        customerResponse = new CustomerResponse(
                customerId,
                "João da Silva",
                "CPF",
                "52998224725",
                "(11) 99999-0000",
                "joao@email.com",
                "Rua das Flores, 123 - São Paulo/SP",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("POST /customers")
    class Create {

        @Test
        @DisplayName("Should create customer and return 201 when request data is valid")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn201WhenDataIsValid() throws Exception {
            when(createCustomerUseCase.execute(any(CreateCustomerCommand.class))).thenReturn(customerResponse);

            mockMvc.perform(post("/customers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(customerId.toString()))
                    .andExpect(jsonPath("$.name").value("João da Silva"))
                    .andExpect(jsonPath("$.documentType").value("CPF"))
                    .andExpect(jsonPath("$.email").value("joao@email.com"));

            verify(createCustomerUseCase).execute(any(CreateCustomerCommand.class));
        }

        @Test
        @DisplayName("Should return 409 when document number is already registered")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn409WhenDocumentAlreadyExists() throws Exception {
            when(createCustomerUseCase.execute(any(CreateCustomerCommand.class)))
                    .thenThrow(new DuplicatedDocumentException("Cliente com documento 529.982.247-25 já existe"));

            mockMvc.perform(post("/customers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").value("Documento duplicado"))
                    .andExpect(jsonPath("$.message").value("Cliente com documento 529.982.247-25 já existe"));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(post("/customers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(createCustomerUseCase);
        }
    }

    @Nested
    @DisplayName("GET /customers")
    class FindAll {

        @Test
        @DisplayName("Should return 200 with customer list")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WithCustomerList() throws Exception {
            when(findAllCustomersUseCase.execute()).thenReturn(List.of(customerResponse));

            mockMvc.perform(get("/customers"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(customerId.toString()))
                    .andExpect(jsonPath("$[0].name").value("João da Silva"))
                    .andExpect(jsonPath("$[0].email").value("joao@email.com"));

            verify(findAllCustomersUseCase).execute();
        }

        @Test
        @DisplayName("Should return 200 with empty list when no customers are registered")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WithEmptyListWhenNoCustomersExist() throws Exception {
            when(findAllCustomersUseCase.execute()).thenReturn(List.of());

            mockMvc.perform(get("/customers"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/customers"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(findAllCustomersUseCase);
        }
    }

    @Nested
    @DisplayName("GET /customers/{id}")
    class FindById {

        @Test
        @DisplayName("Should return 200 with customer data when ID exists")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WhenCustomerExists() throws Exception {
            when(findCustomerByIdUseCase.execute(any(FindCustomerByIdCommand.class))).thenReturn(customerResponse);

            mockMvc.perform(get("/customers/{id}", customerId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(customerId.toString()))
                    .andExpect(jsonPath("$.name").value("João da Silva"))
                    .andExpect(jsonPath("$.email").value("joao@email.com"))
                    .andExpect(jsonPath("$.documentType").value("CPF"));

            verify(findCustomerByIdUseCase).execute(any(FindCustomerByIdCommand.class));
        }

        @Test
        @DisplayName("Should return 404 when customer is not found")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn404WhenCustomerDoesNotExist() throws Exception {
            when(findCustomerByIdUseCase.execute(any(FindCustomerByIdCommand.class)))
                    .thenThrow(new NotFoundException("Cliente não encontrado para o id: " + customerId));

            mockMvc.perform(get("/customers/{id}", customerId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/customers/{id}", customerId))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(findCustomerByIdUseCase);
        }
    }

    @Nested
    @DisplayName("PUT /customers/{id}")
    class Update {

        @Test
        @DisplayName("Should update customer and return 200 when request data is valid")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WhenDataIsValid() throws Exception {
            when(updateCustomerUseCase.execute(any(UpdateCustomerCommand.class)))
                    .thenReturn(customerResponse);

            mockMvc.perform(put("/customers/{id}", customerId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(customerId.toString()))
                    .andExpect(jsonPath("$.name").value("João da Silva"))
                    .andExpect(jsonPath("$.email").value("joao@email.com"));

            verify(updateCustomerUseCase).execute(any(UpdateCustomerCommand.class));
        }

        @Test
        @DisplayName("Should return 404 when customer is not found")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn404WhenCustomerDoesNotExist() throws Exception {
            when(updateCustomerUseCase.execute(any(UpdateCustomerCommand.class)))
                    .thenThrow(new NotFoundException("Cliente não encontrado para o id: " + customerId));

            mockMvc.perform(put("/customers/{id}", customerId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("Should return 422 when attempting to change customer document")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn422WhenDocumentIsChanged() throws Exception {
            when(updateCustomerUseCase.execute(any(UpdateCustomerCommand.class)))
                    .thenThrow(new InvalidDocumentException("Não é permitido alterar o documento do cliente"));

            mockMvc.perform(put("/customers/{id}", customerId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.error").value("Documento Inválido"))
                    .andExpect(jsonPath("$.message").value("Não é permitido alterar o documento do cliente"));
        }

        @Test
        @DisplayName("Should return 409 when document already belongs to another customer")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn409WhenDocumentBelongsToAnotherCustomer() throws Exception {
            when(updateCustomerUseCase.execute(any(UpdateCustomerCommand.class)))
                    .thenThrow(new DuplicatedDocumentException("Cliente com documento 529.982.247-25 já existe"));

            mockMvc.perform(put("/customers/{id}", customerId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").value("Documento duplicado"))
                    .andExpect(jsonPath("$.status").value(409));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(put("/customers/{id}", customerId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(updateCustomerUseCase);
        }
    }

    @Nested
    @DisplayName("DELETE /customers/{id}")
    class Delete {

        @Test
        @DisplayName("Should delete customer and return 204 when ID exists")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn204WhenCustomerExists() throws Exception {
            doNothing().when(deleteCustomerUseCase).execute(any(DeleteCustomerCommand.class));

            mockMvc.perform(delete("/customers/{id}", customerId)
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(deleteCustomerUseCase).execute(any(DeleteCustomerCommand.class));
        }

        @Test
        @DisplayName("Should return 404 when customer is not found")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn404WhenCustomerDoesNotExist() throws Exception {
            doThrow(new NotFoundException("Cliente não encontrado para o id: " + customerId))
                    .when(deleteCustomerUseCase).execute(any(DeleteCustomerCommand.class));

            mockMvc.perform(delete("/customers/{id}", customerId)
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(delete("/customers/{id}", customerId)
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(deleteCustomerUseCase);
        }
    }
}

 */