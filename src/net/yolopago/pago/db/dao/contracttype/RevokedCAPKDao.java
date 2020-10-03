package net.yolopago.pago.db.dao.contracttype;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import net.yolopago.pago.db.entity.RevokedCAPK;

@Dao
public interface RevokedCAPKDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(RevokedCAPK revokedCAPK);

	@Query("DELETE FROM RevokedCAPK")
	void deleteAll();

}
