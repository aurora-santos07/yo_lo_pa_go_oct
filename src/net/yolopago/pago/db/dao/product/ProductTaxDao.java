package net.yolopago.pago.db.dao.product;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import net.yolopago.pago.db.entity.ProductTax;

@Dao
public interface ProductTaxDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(ProductTax productTax);

	@Query("DELETE FROM ProductTax")
	void deleteAll();
}
