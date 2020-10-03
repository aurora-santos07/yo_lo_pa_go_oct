package net.yolopago.pago.db.dao.product;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import net.yolopago.pago.db.entity.Tax;

@Dao
public interface TaxDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(Tax tax);

	@Query("DELETE FROM Tax")
	void deleteAll();
}
