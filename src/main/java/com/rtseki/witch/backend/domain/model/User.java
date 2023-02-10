package com.rtseki.witch.backend.domain.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
@Table(name = "_users",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = "email"),
				@UniqueConstraint(columnNames = "userId")
		})

public class User implements UserDetails {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	private String userId;
	
	@NotBlank
	@Size(min = 3, max = 50)
	private String firstname;
	
	@NotBlank
	@Size(min = 3, max = 50)
	private String lastname;
	
	@NotBlank
	@Size(max = 120)
	@Email
	private String email;
	
	@NotBlank
	private String password;

	@Enumerated(EnumType.STRING)
	private Role role;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.name()));
	}
	
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	private List<ShopList> shopLists = new ArrayList<>();
	
	public User() {
		super();
	}

	public User(Long id, @NotBlank String userId,
			@NotBlank @Size(min = 3, max = 50) String firstname,
			@NotBlank @Size(min = 3, max = 50) String lastname,
			@NotBlank @Size(max = 120) @Email String email,
			@NotBlank String password, Role role) {
		super();
		this.id = id;
		this.userId = userId;
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.password = password;
		this.role = role;
	}

	public User(@NotBlank String userId,
			@NotBlank @Size(min = 3, max = 50) String firstname,
			@NotBlank @Size(min = 3, max = 50) String lastname,
			@NotBlank @Size(max = 120) @Email String email) {
		super();
		this.userId = userId;
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
	}

	public List<ShopList> getShopLists() {
		return shopLists;
	}
	
	@Override
	public String getPassword() {
		return password;
	}
	
	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
