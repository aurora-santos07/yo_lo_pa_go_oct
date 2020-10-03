package net.yolopago.pago.db.dao.contracttype;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import net.yolopago.pago.db.entity.ExceptionPAN;

@Dao
public interface ExceptionPANDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(ExceptionPAN exceptionPAN);

	@Query("DELETE FROM ExceptionPAN")
	void deleteAll();

}
