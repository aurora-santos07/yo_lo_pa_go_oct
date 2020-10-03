package net.yolopago.pago.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import net.yolopago.pago.db.dao.payment.TransactionDao;
import net.yolopago.pago.db.entity.TransactionEnt;

@Database(entities = {
		TransactionEnt.class

		}, version = 1, exportSchema = false)
public abstract class PaymentDatabase extends RoomDatabase {
	private static PaymentDatabase theInstance;

	public abstract TransactionDao transactionDao();

	public static synchronized PaymentDatabase getDatabase(final Context context) {
		if (theInstance == null) {
					theInstance = Room.databaseBuilder(context.getApplicationContext(),
							PaymentDatabase.class, "Payment_Database")
							.fallbackToDestructiveMigration()
							.build();

		}
		return theInstance;
	}
}
