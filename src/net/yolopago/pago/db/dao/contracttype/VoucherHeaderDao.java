package net.yolopago.pago.db.dao.contracttype;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import net.yolopago.pago.db.entity.VoucherHeader;

@Dao
public interface VoucherHeaderDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(VoucherHeader voucherHeader);

	@Query("DELETE FROM VoucherHeader")
	void deleteAll();

}
