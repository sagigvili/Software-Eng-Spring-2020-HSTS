package Hibernate.Entities;

import javax.persistence.Entity;

@Entity
public class Principal extends User {

	public Principal() {

	}

	public Principal(String userName, String password, String firstName, String lastName, String identityNumber) {
		super(userName, password, firstName, lastName, identityNumber);
	}
}
