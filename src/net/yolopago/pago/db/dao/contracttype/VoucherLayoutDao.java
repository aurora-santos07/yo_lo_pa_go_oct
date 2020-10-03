package net.yolopago.pago.db.dao.contracttype;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import net.yolopago.pago.db.entity.VoucherLayout;

@Dao
public interface VoucherLayoutDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(VoucherLayout voucherLayout);

	@Query("DELETE FROM VoucherLayout")
	void deleteAll();

}
