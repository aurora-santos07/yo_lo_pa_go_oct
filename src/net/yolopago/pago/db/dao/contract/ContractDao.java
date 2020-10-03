package net.yolopago.pago.db.dao.contract;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import net.yolopago.pago.db.entity.Contract;

@Dao
public interface ContractDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(Contract contract);

	@Query("DELETE FROM Contract")
	void deleteAll();

	@Query("SELECT c.* FROM Contract c WHERE c._id = (SELECT MIN(c2._id) FROM Contract c2) ")
	Contract findFirst();
}
