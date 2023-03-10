package com.rtseki.witch.backend.domain.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;
	
	@OneToMany(mappedBy = "subcategory", fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	private List<Product> products = new ArrayList<>();
	
	public Subcategory() {
		
	}
	
	public Subcategory(Long id, String name, String description, Category category) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.category = category;
	}
	
	public Subcategory(Long id, String name, String description, Category category, List<Product> products) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.category = category;
		this.products = products;
	}
}
