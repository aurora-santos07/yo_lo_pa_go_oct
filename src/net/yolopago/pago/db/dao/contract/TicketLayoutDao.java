package net.yolopago.pago.db.dao.contract;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import net.yolopago.pago.db.entity.TicketLayout;

@Dao
public interface TicketLayoutDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(TicketLayout ticketLayout);

	@Query("DELETE FROM TicketLayout")
	void deleteAll();
}
