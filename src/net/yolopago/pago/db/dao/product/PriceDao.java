package net.yolopago.pago.db.dao.product;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import net.yolopago.pago.db.entity.Price;

@Dao
public interface PriceDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(Price price);

	@Query("DELETE FROM Price")
	void deleteAll();

	@Query("SELECT P.* FROM Price P WHERE _id = (SELECT MIN(P2._id) FROM Product P2) ")
	LiveData<Price> loadFirst();

	@Query("SELECT p.* FROM Price p WHERE p._id = (SELECT MIN(p2._id) FROM Product p2) ")
	Price findFirst();

	@Query("SELECT p.* FROM Price p WHERE p._id = :id ")
	LiveData<Price> loadById(Long id);
}
