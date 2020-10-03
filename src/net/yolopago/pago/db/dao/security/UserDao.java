package net.yolopago.pago.db.dao.security;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import net.yolopago.pago.db.entity.User;

@Dao
public interface UserDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(User user);

	@Query("DELETE FROM User")
	void deleteAll();

	@Query("SELECT u.* FROM User u WHERE u._id = (SELECT MIN(u2._id) FROM User u2)")
	User findFirst();

	@Query("SELECT u.* FROM User u WHERE u._id = :idUser")
	LiveData<User> findById(Long idUser);
}
