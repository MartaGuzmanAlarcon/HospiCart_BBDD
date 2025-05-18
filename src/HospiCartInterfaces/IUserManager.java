package HospiCartInterfaces;
import java.sql.SQLException;
import java.util.List;

import HospiCartPOJOs.Role;
import HospiCartPOJOs.User;

// TODO RW interface
public interface IUserManager {
	

		public void register(User user, int roleOption) throws SQLException;
		public void createRole(Role role);
		public Role getRole(String name);
		public List<Role> getRoles();
		public void assignRole(User user, Role role);
		// If user doesn't exist return null
		public User getUser(String name, String password);
		public User getUserByEmail(String name);
		public void close();
		void updatePassword(String email, String password);
	}


