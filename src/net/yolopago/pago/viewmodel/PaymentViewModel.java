package net.yolopago.pago.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import net.yolopago.pago.db.entity.Price;
import net.yolopago.pago.db.entity.Product;
import net.yolopago.pago.repository.PaymentRepository;
import net.yolopago.pago.ws.dto.payment.TransactionDto;
import net.yolopago.pago.ws.dto.payment.UserDto;
import net.yolopago.pago.ws.dto.security.PrincipalDto;

import com.wizarpos.emvsample.MainApp;


public class 	PaymentViewModel extends AndroidViewModel {
	private MutableLiveData<String> msg;
	private MutableLiveData<String> errorMsg;
	public PaymentRepository paymentRepository;


	public PaymentViewModel(@NonNull Application application) {
		super(application);
		msg = new MutableLiveData<>();
		errorMsg = new MutableLiveData<>();

		paymentRepository = PaymentRepository.getInstance(application);
		paymentRepository.setMsg(msg);
		paymentRepository.setErrorMsg(msg);
	}

	public LiveData<String> compra(TransactionDto transactionDto) {
		return paymentRepository.compra(transactionDto);
	}
	public LiveData<String> getPaymentByIdTicket(Long idTicket) {
		return paymentRepository.getPaymentByIdTicket(idTicket);
	}
	public LiveData<String> getReverseByIdTicket(Long idTicket) {
		return paymentRepository.getReverseByIdTicket(idTicket);
	}
	public LiveData<String> addFilePayment(Long idTicket, String data) {
		return paymentRepository.addFilePayment(idTicket, data);
	}
    public LiveData<String> solicitaLLave(String passkey,String numeroSerie,String crc32) {
        return paymentRepository.solicitaLLave(passkey,numeroSerie,crc32);
    }

	public void setAppState(MainApp appstate){ paymentRepository.setAppState(appstate);}

	public MainApp getAppState(MainApp appstate){ return paymentRepository.getAppState();}

	public LiveData<Product> getProduct() {
		return paymentRepository.getProduct();
	}

	public LiveData<Price> getPrice(Long idPrice) {
		return paymentRepository.getPrice(idPrice);
	}

	public LiveData<String> IdPayment(Long idpayment){
		return paymentRepository.getTicketPayment(idpayment);
	}

	public LiveData<PrincipalDto> getPaymentSession(String user, String password,String terminal){
		return paymentRepository.getPaymentSession(user,  password, terminal);
	}

	public LiveData<String> logOut() {
		return paymentRepository.logOut();
	}
}