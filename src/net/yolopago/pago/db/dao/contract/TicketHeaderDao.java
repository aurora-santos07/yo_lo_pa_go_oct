package net.yolopago.pago.db.dao.contract;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import net.yolopago.pago.db.entity.TicketHeader;

@Dao
public interface TicketHeaderDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(TicketHeader ticketHeader);

	@Query("DELETE FROM TicketHeader")
	void deleteAll();
}
