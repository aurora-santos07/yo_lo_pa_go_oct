package net.yolopago.pago.db.dao.contracttype;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import net.yolopago.pago.db.entity.ContractType;

@Dao
public interface ContractTypeDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(ContractType contractType);

	@Query("DELETE FROM ContractType")
	void deleteAll();

	@Query("SELECT c.* FROM ContractType c WHERE c._id = (SELECT MIN(c2._id) FROM ContractType c2) ")
	ContractType findFirst();
}
