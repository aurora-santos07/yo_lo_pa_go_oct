package net.yolopago.pago.db.dao.contracttype;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import net.yolopago.pago.db.entity.AID;

@Dao
public interface AIDDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(AID aid);

	@Query("DELETE FROM AID")
	void deleteAll();

}
