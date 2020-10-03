package net.yolopago.pago.db.dao.contracttype;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import net.yolopago.pago.db.entity.CAPK;

@Dao
public interface CAPKDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(CAPK capk);

	@Query("DELETE FROM CAPK")
	void deleteAll();

}
