package net.yolopago.pago.db.dao.product;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import net.yolopago.pago.db.entity.Product;

@Dao
public interface ProductDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(Product product);

	@Query("DELETE FROM Product")
	void deleteAll();

	@Query("SELECT P.* FROM Product P WHERE _id = (SELECT MIN(P2._id) FROM Product P2) ")
	LiveData<Product> loadFirst();

	@Query("SELECT p.* FROM Product p WHERE p._id = (SELECT MIN(p2._id) FROM Product p2) ")
	Product findFirst();
}
