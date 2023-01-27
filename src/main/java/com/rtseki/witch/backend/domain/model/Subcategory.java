package com.rtseki.witch.backend.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "_subcategory",
uniqueConstraints = {
		@UniqueConstraint(columnNames = "name")
})
public class Subcategory {
	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	private String name;
	
	private String description;
	
	@ManyToOne
	private Category category;
	
	public Subcategory() {
		
	}
	
	public Subcategory(Long id, String name, String description, Category category) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.category = category;
	}
}
