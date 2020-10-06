package net.yolopago.pago.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.scottyab.rootbeer.RootBeer;
import com.wizarpos.emvsample.MainApp;
import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.activity.IdleActivity;
import com.wizarpos.emvsample.activity.Product;
import com.wizarpos.emvsample.activity.TransResultActivity;
import com.wizarpos.emvsample.activity.cancelacionPrint;
import com.wizarpos.emvsample.constant.Constant;
import com.wizarpos.emvsample.constant.ConstantYLP;

import net.yolopago.pago.db.ConfigDatabase;
import net.yolopago.pago.db.PaymentDatabase;
import net.yolopago.pago.db.dao.contract.PreferenceDao;
import net.yolopago.pago.db.dao.payment.TransactionDao;
import net.yolopago.pago.db.entity.Preference;
import net.yolopago.pago.db.entity.TransactionEnt;
import net.yolopago.pago.utilities.LEDDeviceHM;
import net.yolopago.pago.utilities.PrinterHM;
import net.yolopago.pago.fragment.AbstractFragment;
import net.yolopago.pago.fragment.DetailProductFragment;
import net.yolopago.pago.fragment.FragmentLogin;
import net.yolopago.pago.fragment.FragmentMerchant;
import net.yolopago.pago.fragment.FragmentNotification;
import net.yolopago.pago.fragment.FragmentPagoServicios;
import net.yolopago.pago.fragment.FragmentPay;
import net.yolopago.pago.fragment.FragmentProductos;
import net.yolopago.pago.fragment.FragmentResumen;
import net.yolopago.pago.fragment.FragmentRefund;
import net.yolopago.pago.fragment.FragmentSelectMerchant;
import net.yolopago.pago.fragment.FragmentSplash;
import net.yolopago.pago.fragment.FragmentTxTicket;
import net.yolopago.pago.fragment.FragmentTxVoucher;
import net.yolopago.pago.fragment.PrincipalFragment;
import net.yolopago.pago.fragment.TxDetailFragment;
import net.yolopago.pago.fragment.TxListFragment;
import net.yolopago.pago.listener.InternetConnectionListener;
import net.yolopago.pago.recyclerview.BadgeDrawable;
import net.yolopago.pago.viewmodel.ContractViewModel;
import net.yolopago.pago.viewmodel.PaymentViewModel;
import net.yolopago.pago.ws.dto.ticket.TicketLineDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.io.Serializable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements InternetConnectionListener {
	private static Fragment theFragment;
	private MutableLiveData<String> mutableLiveData;
	private static final long Start_Time = 900000; //milisegundos en los que expira la sesión
	private CountDownTimer countDownTimer;
	private boolean TimerRunning;
	private ArrayList<Product> listMain;
	private long TimeLeft = Start_Time;
	private FragmentManager fragmentManager;
	private FrameLayout fragmentContainer;
	private LinearLayout navigationBar;
	private ImageButton btn_1,btn_2,btn_3,btn_4;
	private ArrayList<Product> productsList = new ArrayList<>();
	//private AppBarConfiguration mAppBarConfiguration;
	//private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
	private static final String TAG = "MainActivity";
	private boolean logOutInBackGround=false;
	private PaymentViewModel paymentViewModel;

	//ASANTOS Variables que se cargan con el valor de concepto y monto
	private String concept;
	private String amount;

	public MainActivity() {
		MainApp.mainActivity = this;
		MainApp.theActivity = this;
		mutableLiveData = new MutableLiveData<>();
		mutableLiveData.observe(this, s -> {
			if (theFragment instanceof FragmentSplash ||
					theFragment instanceof FragmentLogin ||
					theFragment instanceof FragmentNotification ||
					theFragment instanceof FragmentSelectMerchant) {
				logout();

			}
		});

	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//ASANTOS Si la app fue llamada por otra aplicación, traerá dentro del inten que la llama el monto y el concepto
		if(getIntent().getStringExtra(ConstantYLP.EXTRA_CONCEPT) != null){
			concept = getIntent().getStringExtra(ConstantYLP.EXTRA_CONCEPT);
			amount = getIntent().getStringExtra(ConstantYLP.EXTRA_AMOUNT);
		}
		Log.d("main activity", "--" + amount);
		//ASANTOS Se elimina uso de timer
		//startTimer();

		fragmentManager = getSupportFragmentManager();
		setContentView(R.layout.activity_nav);
		if (savedInstanceState == null) {

			fragmentContainer=(FrameLayout)this.findViewById(R.id.fragment_container);
			navigationBar =(LinearLayout) this.findViewById(R.id.navigation);

			RootBeer rootBeer = new RootBeer(this.getApplicationContext());
			if (rootBeer.isRooted()) {
				Fragment fragment;
				Log.d(TAG, "onCreate: Loadind Splash");
					clearProductList();
					fragment = new FragmentSplash();
					//ASANTOS paso de parámetros
				if (amount != null) {
						Log.d("main activity", "maount not null");
						Bundle args = new Bundle();
						args.putString(ConstantYLP.EXTRA_AMOUNT, amount);
						args.putString(ConstantYLP.EXTRA_CONCEPT, concept);
						fragment.setArguments(args);
					}//Termina aqui
					setFragment(fragment);
			}else {

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
				alertDialogBuilder
						.setMessage("EL DISPOSITIVO SE ENCUENTRA ROOTEADO Y NO SE PUEDE OPERAR")
						.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								finish();
								System.exit(0);
							}
						}).create().show();

			}



			btn_1=(ImageButton)this.findViewById(R.id.navigation_cobro);
			btn_1.setOnClickListener(this::clickListener);
			btn_2=(ImageButton)this.findViewById(R.id.navigation_trans);
			btn_2.setOnClickListener(this::clickListener);
			btn_3=(ImageButton)this.findViewById(R.id.navigation_servicios);
			btn_3.setOnClickListener(this::clickListener);
			btn_4=(ImageButton)this.findViewById(R.id.navigation_shop);
			btn_4.setOnClickListener(this::clickListener);
			setBadgeCount();
		}


	}

	private void clickListener(View v) {
		Fragment fragment;
		btn_1.setSelected(false);
		btn_2.setSelected(false);
		btn_3.setSelected(false);
		btn_4.setSelected(false);
		v.setSelected(true);

		switch (v.getId()) {
			case R.id.navigation_cobro:
				fragment = new FragmentProductos();
				setFragment(fragment);
				break;
			case R.id.navigation_trans:
				//PrinterHM print =new PrinterHM(getResources());
				//print.print(0);
				fragment = new TxListFragment();
				setFragment(fragment);
				break;
			case R.id.navigation_shop:
				Log.e("error", "theFragment:" + theFragment.getClass().getName());
				Bundle b = new Bundle();
				if(theFragment instanceof FragmentProductos ) {
					b.putSerializable("key", (Serializable) ((FragmentProductos)theFragment).productsArrayList);
				}
				fragment = new FragmentResumen();
				fragment.setArguments(b);
				setFragment(fragment);
				break;
			case R.id.navigation_servicios:
				fragment = new FragmentPagoServicios();
				setFragment(fragment);
				break;
		}
	}

	public void setSelectedButton(Fragment fragment){
		//String setFrag
		btn_1.setSelected(false);
		btn_2.setSelected(false);
		btn_3.setSelected(false);
		btn_4.setSelected(false);

		if (fragment instanceof FragmentResumen){
			btn_4.setSelected(true);
		}else if (fragment instanceof FragmentProductos){
			setBadgeCount();
			btn_1.setSelected(true);
		}else if (fragment instanceof FragmentPay) {
		}else if (fragment instanceof TxListFragment){
			btn_2.setSelected(true);
		}else if (fragment instanceof FragmentMerchant ){
		}else if (fragment instanceof PrincipalFragment ){
		}else if (fragment instanceof TxDetailFragment){
			btn_2.setSelected(true);
		}else if (fragment instanceof DetailProductFragment ){
			btn_4.setSelected(true);
		}else if (fragment instanceof FragmentPagoServicios){
			btn_3.setSelected(true);
		}



	}



	@Override
	protected void onStart() {
		super.onStart();
	}

	public void loadFragment(Fragment fragment) {
		getSupportFragmentManager().
				beginTransaction().
				setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).
				replace(R.id.fragment_container, fragment, fragment.getClass().getName()).
				addToBackStack("p").
				commit();
	}

	public void setFragment(Fragment fragment) {
		theFragment = fragment;

		if (fragment instanceof FragmentResumen || fragment instanceof FragmentProductos || fragment instanceof FragmentPay ||
			fragment instanceof TxListFragment || fragment instanceof FragmentMerchant ||fragment instanceof  PrincipalFragment ||
			fragment instanceof TxDetailFragment|| fragment instanceof DetailProductFragment || fragment instanceof FragmentPagoServicios ||
		    fragment instanceof FragmentTxVoucher || fragment instanceof FragmentTxTicket) {
			//setContentView(R.layout.activity_nav);
			setSelectedButton(fragment);
			int px = (int) (45 * this.getApplicationContext().getResources().getDisplayMetrics().density);
			navigationBar.setVisibility(View.VISIBLE);
			ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fragmentContainer.getLayoutParams();

			params.setMargins(0, 0, 0, px);
			fragmentContainer.setLayoutParams(params);

		} else {
			navigationBar.setVisibility(View.INVISIBLE);
			ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fragmentContainer.getLayoutParams();
			params.setMargins(0, 0, 0, 0);
			fragmentContainer.setLayoutParams(params);
		}
		getSupportFragmentManager().
				beginTransaction().
				replace(R.id.fragment_container, fragment, "login").
				commit();

		checkReversos();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Fragment fragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
		if (keyCode == KeyEvent.KEYCODE_BACK && (
			fragment instanceof TxDetailFragment ||
			fragment instanceof FragmentRefund
				)) {
			fragmentManager.popBackStack();
		}
		return true;
	}

	public void onInternetUnavailable() {
		onUnauthorizedAccess();
	}

	public void onUnauthorizedAccess() {
		//mutableLiveData.postValue("onInternetUnavailable");
		this.runOnUiThread(new Runnable() {
			public void run() {
				logout();
				//Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
			}
		});
		//logout();
	}

	public void logout() {
		clearProductList();
		if(logOutInBackGround==false) {
			if(!(theFragment instanceof FragmentLogin)) {
				Context context = MainApp.getInstance().getApplicationContext();
				Toast.makeText(context, "La sesión ha expirado.", Toast.LENGTH_LONG).show();
				((AbstractFragment) theFragment).logout();
			}
		}else {
			((AbstractFragment) theFragment).exit=true;
		}
	}

	@Override
	public void onUserInteraction(){
		super.onUserInteraction();
		//ASANTOS Se elimina uso de timer
		//pauseTimer();
		//resetTimer();
		//startTimer();
	}

	private void startTimer(){
		countDownTimer = new CountDownTimer(TimeLeft, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				TimeLeft = millisUntilFinished;
				updateCountDownText();
			}
			@Override
			public void onFinish() {
				TimerRunning = false;
				logout();
			}
		}.start();
		TimerRunning = true;
	}

	private void resetTimer(){
		TimeLeft = Start_Time;
		updateCountDownText();
	}

	private void pauseTimer(){
		countDownTimer.cancel();
		TimerRunning = false;
	}

	private void updateCountDownText(){
		//formato de la visualización del timer de logout 00:00
		int minutes = (int) TimeLeft / 1000 / 60;
		int seconds = (int) TimeLeft / 1000 % 60;


	}

	public  void setBadgeCount() {

		BadgeDrawable badge;

		// Reuse drawable if possible
		LayerDrawable icon=((LayerDrawable)btn_4.getDrawable());
		Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
		if (reuse != null && reuse instanceof BadgeDrawable) {
			badge = (BadgeDrawable) reuse;
		} else {
			//Log.d("(TAG, "setBadgeCount: context");
			badge = new BadgeDrawable(this.getApplicationContext());
		}
		int count =getConutProducList();
		badge.setCount(""+getConutProducList());
		icon.mutate();
		icon.setDrawableByLayerId(R.id.ic_badge, badge);


		//Log.d("(TAG, "setBadgeCount: "+badge);
	}
	public ArrayList<Product> getProducList(){
		if(productsList==null){
			productsList=new ArrayList<Product>();
		}
		//Log.d("(TAG, "getProducList: PoductSize"+productsList.size());
		return productsList;
	}
	public int getConutProducList(){
		ArrayList<Product> productos=getProducList();
		int total=0;
		for (int i = 0; i < productos.size(); i++) {
			total+=Integer.parseInt(productos.get(i).getCantidad());
		}
		return total;
	}

	public void clearProductList() {
		productsList = new ArrayList<Product>();
	}

	@Override
	public void onPostResume(){
		super.onPostResume();
        logOutInBackGround=false;
		if(theFragment!=null && ((AbstractFragment)theFragment).exit==true){
			logout();
		}
	}
	@Override
	public void onPause(){
		super.onPause();
        logOutInBackGround=true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult( requestCode,  resultCode,  data);

		//Log.d("(TAG, "onActivityResult requestCode:"+requestCode+" resultCode:"+resultCode);
		//ASANTOS elimina valores para evitar cicladops en la app
		if (amount != null && requestCode == 0 ){
			amount = null;
			concept = null;
			finish(); //ASANTOS se cierra esta app para ver la app anterior
		}
		if(resultCode== TransResultActivity.END_OK || resultCode== TransResultActivity.END_FAIL_NO) {
			clearProductList();
			if (theFragment instanceof FragmentResumen){
				Fragment fragment = new FragmentProductos();
				setFragment(fragment);
			}else if (theFragment instanceof FragmentProductos){
				((FragmentProductos)theFragment).refresh();
				//((FragmentProductos)theFragment).enableBtns();
			}


		}else if(resultCode== TransResultActivity.END_FAIL_SI) {
			if (theFragment instanceof FragmentResumen){

				((FragmentResumen)theFragment).makePay();
			}else if (theFragment instanceof FragmentProductos){
				((FragmentProductos)theFragment).makePay();
			}
		}
		if(requestCode == FragmentResumen.IDLE_ACTIVITY_CODE && resultCode== Activity.RESULT_CANCELED){
			if (theFragment instanceof FragmentResumen){
				((FragmentResumen)theFragment).enableBtns();
			}
			if (theFragment instanceof FragmentProductos){
				//((FragmentProductos)theFragment).enableBtns();
			}
		}
		if(resultCode== cancelacionPrint.END_OK ){
			/*if (theFragment instanceof FragmentResumen){
				((FragmentResumen)theFragment).enableBtns();
			}*/
			setFragment(new TxListFragment());
		}

		LEDDeviceHM.get().clear();
	}

	@Override
	protected void onResume() {
		super.onResume();
		 checkReversos();
	}

	public void checkReversos(){
		//Log.d(TAG, "checkReversos: "+theFragment.getClass().getName()+ "" +FragmentLogin.class.getName());

		if(theFragment!=null && !theFragment.getClass().isAssignableFrom(FragmentLogin.class) &&
			!theFragment.getClass().isAssignableFrom(FragmentSplash.class) ){
			Disposable db = Observable.just("DB")
					.subscribeOn(Schedulers.computation())
					.flatMap((String s) -> {
						PaymentDatabase paymentDatabase = PaymentDatabase.getDatabase(this.getApplication());
						TransactionDao transactionDao = paymentDatabase.transactionDao();
						if (paymentViewModel == null) {
							paymentViewModel = new PaymentViewModel(this.getApplication());
						}
						Log.d(TAG, "Compra: LAST"+transactionDao.getAllLast().length);
						if (transactionDao.getAllLast().length > 0) {
							paymentViewModel.getReverseByIdTicket(transactionDao.getAllLast()[0].getIdTicket());//transactionDao.getAllLast()[0].getIdTicket());
						}
						return Observable.just("");
					}).observeOn(AndroidSchedulers.mainThread())
					.subscribe();
		}
	}


	/*ASANTOS Método que setea el activity de pagos pasando al intent los valores de monto y  concepto */
	private void callPayment(){
		Intent intent = new Intent(getApplicationContext(), IdleActivity.class);
		intent.putExtra("amount", amount);
		ArrayList<Product>pay =  new ArrayList<Product>();
		pay.add(new Product(concept, "1", Double.parseDouble(amount), Double.parseDouble(amount), Double.parseDouble(amount)));
		intent.putExtra("idle",(ArrayList<Product>) pay );
		startActivityForResult(intent, 0);
	}


	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		//ASANTOS Validación para llamar a fragment de pago si la sesión está iniciada
		amount = intent.getStringExtra(ConstantYLP.EXTRA_AMOUNT);
		concept = intent.getStringExtra(ConstantYLP.EXTRA_CONCEPT);
		Log.d("main activity", "--" + amount);
		if (amount != null){
			Log.d("main activity", "amount not null newintent");
			if(!(theFragment instanceof FragmentSplash) && !(theFragment instanceof FragmentLogin)) {
				Log.d("main activity", "call payment");
				callPayment();
			}else{
				//llama al fragmaent login para agregar los argumentos
				Fragment fragment = new FragmentLogin();
				Bundle args = new Bundle();
				args.putString(ConstantYLP.EXTRA_AMOUNT, amount);
				args.putString(ConstantYLP.EXTRA_CONCEPT, concept);
				fragment.setArguments(args);
				setFragment(fragment);
			}
		}//ASANTOS Aquì termina
	}
}
