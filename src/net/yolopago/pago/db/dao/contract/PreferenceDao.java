package net.yolopago.pago.db.dao.contract;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import net.yolopago.pago.db.entity.Preference;

@Dao
public interface PreferenceDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(Preference preference);

	@Query("DELETE FROM Preference")
	void deleteAll();

	@Query("SELECT COUNT(*) FROM Preference WHERE _id = :id ")
	Long countById(Long id);

	@Query("SELECT * FROM Preference ")
	Preference[] findAll();
}
