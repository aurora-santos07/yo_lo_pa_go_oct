package net.yolopago.pago.db.dao.contract;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import net.yolopago.pago.db.entity.Merchant;

@Dao
public interface MerchantDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(Merchant merchant);

	@Query("DELETE FROM Merchant")
	void deleteAll();

	@Query("SELECT m.* FROM Merchant m WHERE m._id = (SELECT MIN(m2._id) FROM Merchant m2)")
	Merchant getFirst();

	@Query("SELECT m.* FROM Merchant m WHERE m._id = (SELECT MIN(m2._id) FROM Merchant m2)")
	LiveData<Merchant> findFirst();
}
