package com.fiap.mechanical_hub.domain.entities;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.fiap.mechanical_hub.domain.utils.license_plate.LicensePlateFormatter.normalize;
import static com.fiap.mechanical_hub.domain.utils.license_plate.LicensePlateValidator.validateLicensePlate;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Vehicle {

	private UUID id;
	private UUID customerId;
	private String licensePlate;
	private String brand;
	private String model;
	private Integer year;
	private String color;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static Vehicle create(UUID customerId,
								 String licensePlate,
								 String brand,
								 String model,
								 Integer year,
								 String color) {
		validateLicensePlate(licensePlate);

		Vehicle vehicle = new Vehicle();
		vehicle.id = UUID.randomUUID();
		vehicle.customerId = customerId;
		vehicle.licensePlate = normalize(licensePlate);
		vehicle.brand = brand;
		vehicle.model = model;
		vehicle.year = year;
		vehicle.color = color;
		vehicle.createdAt = LocalDateTime.now();
		vehicle.updatedAt = LocalDateTime.now();

		return vehicle;
	}

	public void update(String licensePlate,
								  String brand,
								  String model,
								  Integer year,
								  String color) {
		validateLicensePlate(licensePlate);

		this.licensePlate = normalize(licensePlate);
		this.brand = brand;
		this.model = model;
		this.year = year;
		this.color = color;
		this.updatedAt = LocalDateTime.now();
	}
}

