package net.yolopago.pago.db.dao.notification;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import net.yolopago.pago.db.entity.Notification;

@Dao
public interface NotificationDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(Notification notification);

	@Query("DELETE FROM Notification")
	void deleteAll();
}
