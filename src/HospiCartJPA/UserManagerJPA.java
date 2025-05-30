package HospiCartJPA;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.*;

import HospiCartPOJOs.Role;
import HospiCartPOJOs.User;
import Utilities.Encryption;
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
			Role supplier = new Role("supplier");
			this.createRole(doctor);
			this.createRole(nurse);
			this.createRole(supplier);
		}
	}
	/**
	 * Method that closes the entity manager. This method should be called after having worked with the database.
	 */
	public void close() {
		if (em != null && em.isOpen()) {
	        // I make sure any active transaction is committed or rolled back
	        if (em.getTransaction().isActive()) {
	            try {
	                em.getTransaction().commit();
	            } catch (Exception e) {
	                em.getTransaction().rollback();
	            }
	        }
	        em.close();
	    }	
	}

	@Override
	public void register(User user, int roleOption) throws SQLException {
		User existentUser = getUserByEmail(user.getEmail());
		em.getTransaction().begin();
		if(existentUser == null) {
			Role role = em.find(Role.class, roleOption);
			assignRole(user, role);
			//Persist is used for creating in JPA.
			em.persist(user); 
		}
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
		user.setRole(role);
		role.addUser(user);
	}

	@Override
	public User getUserByEmail(String email) {
		try {
			Query q = em.createNativeQuery("SELECT * FROM users WHERE email = ? ", User.class);
			q.setParameter(1, email);
			User user = (User) q.getSingleResult();
			return user;
		} catch (NoResultException e) {
			return null;
		}
	}
	
	@Override
	public User getUser(String email, String password) {
		try {
			String encriptedPassword = Encryption.encryptPasswordMD5(password);
			Query q = em.createNativeQuery("SELECT * FROM users WHERE email = ? AND password = ?", User.class);
			q.setParameter(1, email);
			q.setParameter(2, encriptedPassword);
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
		Query q = em.createNativeQuery("SELECT * FROM roles WHERE name = ?", Role.class);
		q.setParameter(1, name);
		Role r = (Role) q.getSingleResult();
		return r;
	}
	
	@Override
	public void updatePassword(String email, String password, boolean closeEM){
	    try {
	    	em.getTransaction().begin(); // Start the transaction
	        Query q = em.createNativeQuery("UPDATE users SET password = ? WHERE email = ?");
	        String encryptedPassword = Encryption.encryptPasswordMD5(password);
	        q.setParameter(1, encryptedPassword);
	        q.setParameter(2, email);
	        q.executeUpdate(); // Execute the update query
	        em.getTransaction().commit(); // Commit the transaction
	        //I only close the entity manager if I need to --> I need to close them after resetting the password in the actors menu because if they want to update any other
	        //information (such as the company name in the case of the supplier menu), these methods use JDBC and I won't be able to update these values if the em is not closed in this method.
	        if(closeEM) {
	            close();
	        }
	    } catch (Exception e) {
	        if (em.getTransaction().isActive()) {
	        	em.getTransaction().rollback(); //I rollback only if a transaction is active
	        }
	        throw new RuntimeException("Error updating user's password: " + e.getMessage(), e);
	    }
	}
}
