package net.yolopago.pago.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import net.yolopago.pago.db.dao.contract.ContractDao;
import net.yolopago.pago.db.dao.contract.MerchantDao;
import net.yolopago.pago.db.dao.contract.PreferenceDao;
import net.yolopago.pago.db.dao.contract.TicketFooterDao;
import net.yolopago.pago.db.dao.contract.TicketHeaderDao;
import net.yolopago.pago.db.dao.contract.TicketLayoutDao;
import net.yolopago.pago.db.dao.contracttype.AIDDao;
import net.yolopago.pago.db.dao.contracttype.CAPKDao;
import net.yolopago.pago.db.dao.contracttype.ContractTypeDao;
import net.yolopago.pago.db.dao.contracttype.ExceptionPANDao;
import net.yolopago.pago.db.dao.contracttype.RevokedCAPKDao;
import net.yolopago.pago.db.dao.contracttype.VoucherFooterDao;
import net.yolopago.pago.db.dao.contracttype.VoucherHeaderDao;
import net.yolopago.pago.db.dao.contracttype.VoucherLayoutDao;
import net.yolopago.pago.db.dao.notification.NotificationDao;
import net.yolopago.pago.db.dao.security.SessionDao;
import net.yolopago.pago.db.dao.security.UserDao;
import net.yolopago.pago.db.entity.AID;
import net.yolopago.pago.db.entity.CAPK;
import net.yolopago.pago.db.entity.Contract;
import net.yolopago.pago.db.entity.ContractType;
import net.yolopago.pago.db.entity.ExceptionPAN;
import net.yolopago.pago.db.entity.Merchant;
import net.yolopago.pago.db.entity.Notification;
import net.yolopago.pago.db.entity.Preference;
import net.yolopago.pago.db.entity.RevokedCAPK;
import net.yolopago.pago.db.entity.Session;
import net.yolopago.pago.db.entity.TicketFooter;
import net.yolopago.pago.db.entity.TicketHeader;
import net.yolopago.pago.db.entity.TicketLayout;
import net.yolopago.pago.db.entity.User;
import net.yolopago.pago.db.entity.VoucherFooter;
import net.yolopago.pago.db.entity.VoucherHeader;
import net.yolopago.pago.db.entity.VoucherLayout;

@Database(entities = {
		Session.class,
		User.class,

		Contract.class,
		Merchant.class,
		Preference.class,
		TicketFooter.class,
		TicketHeader.class,
		TicketLayout.class,

		ContractType.class,
		AID.class,
		CAPK.class,
		RevokedCAPK.class,
		ExceptionPAN.class,
		VoucherFooter.class,
		VoucherHeader.class,
		VoucherLayout.class,

		Notification.class
		}, version = 1, exportSchema = false)
public abstract class ConfigDatabase extends RoomDatabase {
	private static ConfigDatabase theInstance;

	public abstract SessionDao sessionDao();
	public abstract UserDao userDao();

	public abstract ContractDao contractDao();
	public abstract MerchantDao merchantDao();
	public abstract PreferenceDao preferenceDao();
	public abstract TicketFooterDao ticketFooterDao();
	public abstract TicketHeaderDao ticketHeaderDao();
	public abstract TicketLayoutDao ticketLayoutDao();

	public abstract ContractTypeDao contractTypeDao();
	public abstract AIDDao aidDao();
	public abstract CAPKDao capkDao();
	public abstract RevokedCAPKDao revokedCAPKDao();
	public abstract ExceptionPANDao exceptionPANDao();
	public abstract VoucherFooterDao voucherFooterDao();
	public abstract VoucherHeaderDao voucherHeaderDao();
	public abstract VoucherLayoutDao voucherLayoutDao();

	public abstract NotificationDao notificationDao();

	public static synchronized  ConfigDatabase getDatabase(final Context context) {
				if (theInstance == null) {
					theInstance = Room.databaseBuilder(context.getApplicationContext(),
							ConfigDatabase.class, "Config_Database")
							.fallbackToDestructiveMigration()
							.build();
				}
		return theInstance;
	}
}
