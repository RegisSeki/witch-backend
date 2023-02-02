package com.rtseki.witch.backend.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "_product",
uniqueConstraints = {
		@UniqueConstraint(columnNames = "barcode")},
indexes = {
		@Index(columnList = "barcode"),
		@Index(columnList = "name") 
})
public class Product {
	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@EqualsAndHashCode.Include
	private String barcode;
	
	@NotBlank
	@EqualsAndHashCode.Include
	private String name;
	
	private String description;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subcategory_id")
	private Subcategory subcategory;
	
	public Product() {
		
	}
	
	public Product(Long id, String barcode, String name, String description, Subcategory subcategory) {
		this.id = id;
		this.barcode = barcode;
		this.name = name;
		this.description = description;
		this.subcategory = subcategory;
	}
}
