package net.yolopago.pago.db.dao.contract;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import net.yolopago.pago.db.entity.TicketFooter;

@Dao
public interface TicketFooterDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(TicketFooter ticketFooter);

	@Query("DELETE FROM TicketFooter")
	void deleteAll();

}
