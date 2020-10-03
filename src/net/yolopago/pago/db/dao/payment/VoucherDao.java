package net.yolopago.pago.db.dao.payment;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import net.yolopago.pago.db.entity.Voucher;

@Dao
public interface VoucherDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(Voucher voucher);

	@Query("DELETE FROM Voucher")
	void deleteAll();
}
