package net.yolopago.pago.db.dao.payment;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import net.yolopago.pago.db.entity.TransactionEnt;

@Dao
public interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TransactionEnt payment);

    @Update
    void updateTransactionDto(TransactionEnt transactionEnt);

    @Query("SELECT t.* FROM TransactionEnt t WHERE t._id= (SELECT Min(t2._id) FROM TransactionEnt t2)")
    LiveData<TransactionEnt> findLast();

    @Query("SELECT t.* FROM TransactionEnt t WHERE t._id= (SELECT Min(t2._id) FROM TransactionEnt t2)")
    TransactionEnt getLast();

    @Query("SELECT t.* FROM TransactionEnt t")
    TransactionEnt[] getAllLast();

    //@Query("SELECT p.* FROM Payment p WHERE p.idTicket = :idTicket")
    //PaymentDao findById(Long idTicket);


    @Query("DELETE FROM TransactionEnt")
    void deleteAll();
}
