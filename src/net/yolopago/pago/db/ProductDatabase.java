package net.yolopago.pago.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import net.yolopago.pago.db.dao.product.PriceDao;
import net.yolopago.pago.db.dao.product.ProductDao;
import net.yolopago.pago.db.dao.product.ProductTaxDao;
import net.yolopago.pago.db.dao.product.TaxDao;
import net.yolopago.pago.db.entity.Price;
import net.yolopago.pago.db.entity.Product;
import net.yolopago.pago.db.entity.ProductTax;
import net.yolopago.pago.db.entity.Tax;

@Database(entities = {
		Product.class,
		Price.class,
		ProductTax.class,
		Tax.class,
		}, version = 1, exportSchema = false)
public abstract class ProductDatabase extends RoomDatabase {
	private static ProductDatabase theInstance;

	public abstract ProductDao productDao();
	public abstract PriceDao priceDao();
	public abstract ProductTaxDao productTaxDao();
	public abstract TaxDao taxDao();

	public static synchronized ProductDatabase getDatabase(final Context context) {
		if (theInstance == null) {
					theInstance = Room.databaseBuilder(context.getApplicationContext(),
							ProductDatabase.class, "Product_Database")
							.fallbackToDestructiveMigration()
							.build();

		}
		return theInstance;
	}
}
