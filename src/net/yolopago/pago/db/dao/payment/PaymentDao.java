package net.yolopago.pago.db.dao.payment;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import net.yolopago.pago.db.entity.Payment;

@Dao
public interface PaymentDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(Payment payment);

	@Update
	void updatePayment(Payment payment);

	@Query("SELECT p.* FROM Payment p WHERE p._id= (SELECT Min(p2._id) FROM Payment p2)")
	Payment findLast();

	//@Query("SELECT p.* FROM Payment p WHERE p.idTicket = :idTicket")
	//PaymentDao findById(Long idTicket);


	@Query("DELETE FROM Payment")
	void deleteAll();
}
