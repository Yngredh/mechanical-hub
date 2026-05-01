principles:

  - ALWAYS use Rich Domain Model
  - ALWAYS enforce business rules inside domain
  - NEVER place business logic in controllers
  - ALWAYS protect aggregate invariants
  - ALWAYS use intention-revealing methods

  - Prefer simplicity over flexibility (MVP first)
  - Avoid overengineering
  - Implement only what is required

  - Code must be readable, cohesive and maintainable

project structure:
src/
├── domain/
│   ├── entities/
│   ├── value-objects/
│   ├── enums/
│   ├── repositories/
│   ├── serviceData/
│   └── exceptions/
│
├── application/
│   ├── use-cases/
│   ├── dto/
│   ├── mappers/
│   └── interfaces/
│
├── infrastructure/
│   ├── database/
│   │   ├── migrations/
│   │   ├── repositories/
│   │   └── models/
│   │
│   ├── http/
│   │   ├── controllers/
│   │   ├── routes/
│   │   └── middlewares/
│   │
│   ├── integrations/
│   │   ├── email/
│   │   ├── whatsapp/
│   │   └── notifications/
│   │
│   └── config/
│
├── shared/
│   ├── utils/
│   ├── types/
│   └── constants/
│
└── main/
    └── Application.java

contexts:

  serviceData-order:
    type: core

  inventory:
    type: supporting

  administration:
    type: supporting

  auth:
    type: generic

  notification:
    type: generic

architecture:

  layers:

    domain:
      contains:
        - entities
        - value_objects
        - domain_services
        - domain_events

    application:
      contains:
        - use_cases

    infrastructure:
      contains:
        - database
        - external_services

    interface:
      contains:
        - controllers
        - DTOs

rules:

  - domain must not depend on any other layer
  - application depends only on domain
  - infrastructure depends on domain and application
  - controllers must be thin (no business logic)

domain_model:

  - entities MUST contain behavior

  - DO NOT use setters for business logic:
      invalid: order.setStatus("FINISHED")
      valid: order.finish()

  - aggregate roots MUST:
      - enforce invariants
      - validate state transitions
      - expose intention methods

  - entities must be persistence-agnostic

  - use value objects for:
      - Budget
      - Document
      - LicensePlate

business_rules:

  - cannot_execute_order_with_pending_stock

  - cannot_finish_order_if_any_service_not_finished

  - adding_service_must:
      - validate_stock
      - deduct_stock_if_available
      - mark_pending_if_not

  - rejecting_order_must_restore_stock

  - starting_service_sets_order_to_in_progress

state_machine:

  RECEIVED:
    can_transition_to: [IN_DIAGNOSIS]

  IN_DIAGNOSIS:
    can_transition_to: [WAITING_APPROVAL]

  WAITING_APPROVAL:
    can_transition_to: [APPROVED, REJECTED]

  APPROVED:
    conditions:
      - has_stock_pending == false
    can_transition_to: [IN_PROGRESS]

  IN_PROGRESS:
    conditions:
      - all_services_finished == true
    can_transition_to: [FINISHED]

  FINISHED:
    can_transition_to: [DELIVERED]

use_cases:

  - create_service_order

  - add_service_to_order

  - update_order_status

  - start_service

  - finish_service

  - replenish_stock

patterns:

  required:
    - Repository
    - Factory
    - DomainService
    - ValueObject

  recommended:
    - Strategy
    - Specification
    - EventDispatcher

  forbidden:
    - GodClass
    - FatController
    - BusinessLogicInDTO

integration:

  - external systems MUST be isolated

  - use adapters for:
      - whatsapp
      - email

  - NEVER call external serviceData from domain

  - use domain events for integrations

testing:

  focus:
    - business rules
    - state transitions
    - edge cases

  required_tests:

    - cannot_execute_with_pending_stock

    - cannot_finish_with_pending_services

    - stock_restored_on_rejection

    - correct_budget_calculation

clean_code:

  naming:
    - use ubiquitous language
    - avoid abbreviations

  functions:
    - small
    - single responsibility

  classes:
    - high cohesion
    - low coupling

  errors:
    - use explicit exceptions
    - never fail silently

ai_execution:

  before_generating_code:

    - read system context

    - identify aggregate root

    - identify business rules involved

    - validate state transitions

  while_generating_code:

    - enforce rules inside domain

    - use intention-revealing methods

    - avoid anemic model

    - keep controllers thin

  after_generating_code:

    - validate invariants are protected

    - validate no business logic outside domain

    - validate transaction safety

anti_patterns:

  - anemic_domain_model
  - business_logic_in_controller
  - direct_db_access_without_repository
  - bypassing_domain_rules
  - shared_mutable_state