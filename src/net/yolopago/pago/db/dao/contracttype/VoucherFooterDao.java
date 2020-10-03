package net.yolopago.pago.db.dao.contracttype;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import net.yolopago.pago.db.entity.VoucherFooter;

@Dao
public interface VoucherFooterDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(VoucherFooter voucherFooter);

	@Query("DELETE FROM VoucherFooter")
	void deleteAll();

}
