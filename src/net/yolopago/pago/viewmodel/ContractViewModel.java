package net.yolopago.pago.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.yolopago.pago.db.entity.Merchant;
import net.yolopago.pago.repository.ContractRepository;
import net.yolopago.pago.ws.dto.contract.MerchantDto;
import net.yolopago.pago.ws.dto.security.PrincipalDto;

public class ContractViewModel  extends AndroidViewModel {
    private MutableLiveData<String> msg;
    private MutableLiveData<String> errorMsg;
    private ContractRepository contractRepository;

    public ContractViewModel(@NonNull Application application) {
        super(application);
        msg = new MutableLiveData<>();
        errorMsg = new MutableLiveData<>();

        contractRepository = ContractRepository.getInstance(application);
        contractRepository.setMsg(msg);
        contractRepository.setErrorMsg(msg);
    }

    public LiveData<String> canTransac(String model, String serialNumber ) {
        return contractRepository.canTransac( model,  serialNumber  );
    }

    public LiveData<MerchantDto> getMerchant() {
        return contractRepository.getMerchant();
    }

    public LiveData<PrincipalDto> getContractSesion(String user, String password,String terminal) {
        return contractRepository.getContractSesion(user,password, terminal);
    }

    public LiveData<String> logOut() {
        return contractRepository.logOut();
    }


}
