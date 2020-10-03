package net.yolopago.pago.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import net.yolopago.pago.db.dao.contract.MerchantDao;
import net.yolopago.pago.db.entity.Merchant;

public class MerchantRepository extends AbstractRepository {
	private static MerchantRepository instance;
	private MerchantDao merchantDao;

	public static MerchantRepository getInstance(Application application) {
		if (instance == null) {
			instance = new MerchantRepository(application);
		}
		return instance;
	}

	private MerchantRepository(Application application) {
		super(application);
		merchantDao = configDatabase.merchantDao();
	}

	public LiveData<Merchant> getMerchant() {
		return merchantDao.findFirst();
	}
}
