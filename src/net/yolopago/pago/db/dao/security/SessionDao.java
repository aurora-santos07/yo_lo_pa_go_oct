package net.yolopago.pago.db.dao.security;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import net.yolopago.pago.db.entity.Session;

@Dao
public interface SessionDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(Session session);

	@Query("SELECT * FROM Session WHERE _id = (SELECT MIN(S2._id) FROM Session S2) ")
	LiveData<Session> loadFirst();

	@Query("SELECT * FROM Session WHERE _id = (SELECT MIN(S2._id) FROM Session S2) ")
	Session getFirst();

	@Query("DELETE FROM Session")
	void deleteAll();

}
