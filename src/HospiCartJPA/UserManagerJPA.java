package HospiCartJPA;

import java.util.List;

import javax.persistence.*;

import HospiCartPOJOs.Role;
import HospiCartPOJOs.User;
import HospiCartInterfaces.IUserManager;


public class UserManagerJPA implements IUserManager {
	//We create an object of entity manager in order to be able to work with JPA (entity manager fulfills the same role as a connection in JDBC)
	EntityManager em;

	public UserManagerJPA() {
		em = Persistence.createEntityManagerFactory("hospiCart-provider").createEntityManager();
		//We include the following 3 lines to enable support for foreign key constraints, as we are working with SQLite.
		// IMPORTANT: transactions are used with operations that modify the database state (create, update and delete)
		em.getTransaction().begin();
		em.createNativeQuery("PRAGMA foreign_keys=ON").executeUpdate();
		em.getTransaction().commit();
		
		// Create the needed roles
		if (this.getRoles().isEmpty()) {
			Role doctor = new Role("doctor");
			Role nurse = new Role("nurse");
			this.createRole(doctor);
			this.createRole(nurse);
			//TODO SEE IF WE HAVE TO INCLUDE SUPPLIER AS A ROLE 
		}
	}
	/**
	 * Method that closes the entity manager. This method should be called after having worked with the database.
	 */
	public void close() {
		em.close();
	}

	@Override
	public void register(User user) {
		em.getTransaction().begin();
		//Persist is used for creating in JPA.
		em.persist(user); 
		em.getTransaction().commit();
	}

	@Override
	public void createRole(Role role) {
		em.getTransaction().begin();
		em.persist(role);
		em.getTransaction().commit();
	}

	@Override
	public void assignRole(User user, Role role) {
		em.getTransaction().begin();
		user.setRole(role);
		role.addUser(user);
		em.getTransaction().commit();
	}

	@Override
	public User getUser(String name, String password) {
		try {
			Query q = em.createNativeQuery("SELECT * FROM users WHERE username = ? AND password = ?", User.class);
			q.setParameter(1, name);
			q.setParameter(2, password);
			User user = (User) q.getSingleResult();
			return user;
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<Role> getRoles() {
		Query q = em.createNativeQuery("SELECT * FROM roles", Role.class);
		List<Role> roles = (List<Role>) q.getResultList();
		return roles;
	}

	@Override
	public Role getRole(String name) {
		Query q = em.createNativeQuery("SELECT * FROM roles WHERE name LIKE ?", Role.class);
		q.setParameter(1, name);
		Role r = (Role) q.getSingleResult();
		return r;
	}

}
