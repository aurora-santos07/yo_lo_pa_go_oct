package net.yolopago.pago.fragment;

import androidx.lifecycle.LiveData;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.wizarpos.emvsample.MainApp;
import com.wizarpos.emvsample.R;

import net.yolopago.pago.viewmodel.TxDetailViewModel;
import net.yolopago.pago.ws.dto.payment.TxDto;

/**
 * Created by dante on 3/11/18.
 */

public class FragmentRefund extends AbstractFragmentPay {
    private TxDto txDto;
    private TxDetailViewModel txDetailViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pay, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }
        setLeyends();
    }


    @Override
    protected void setLeyends() {
        Bundle bundle = getArguments();
        txDto = (TxDto) bundle.get("txDto");
        Double balance = txDto.getAmount();
        if (null != txDto.getAmountReturns()) {
            balance -= txDto.getAmountReturns();
        }
        binding.txtPayTitle.setText("Devolucion");
        binding.txtProduct.setText("Importe maximo: $" + DECIMAL_FORMAT.format(balance));
        binding.btnPay.setText("Devolver");

        FIXED_PRICE = Boolean.FALSE;
        setAmount(0.0);
        maxAmount = ((Double) (balance * 100)).longValue();
        binding.txtAmount.setText(DECIMAL_FORMAT.format(0.0));

        txDetailViewModel = new TxDetailViewModel(MainApp.getApplicationContext(getActivity()));

        binding.btnPay.setOnClickListener(v -> invokePaymentActivities());
        binding.btnCancel.setOnClickListener(v -> {
            setAmount(0.0);
            binding.txtAmount.setText(DECIMAL_FORMAT.format(0.0));
            double bal = txDto.getAmount();
            if (null != txDto.getAmountReturns()) {
                bal -= txDto.getAmountReturns();
            }
            maxAmount = ((Double) (bal * 100)).longValue();
        });
    }

   protected void invokePaymentActivities() {
        /* Double amount = getAmount();
        if (amount == 0) {
            Toast.makeText(getContext(), "Debe introducir un monto.", Toast.LENGTH_LONG).show();
            return;
        }

        binding.btnPay.setVisibility(View.GONE);
        binding.btnCancel.setVisibility(View.GONE);
        LiveData<String> liveData = txDetailViewModel.requestRefund(
                txDto.getMerchantDto().getId(),
                txDto.getTerminalDto().getId(),
                txDto.getSellerDto().getId(),
                txDto.getId(),
                amount);
        liveData.observe(getActivity(), s -> {
            if (s.equals("OK")) {
                if (txDto.getAmountReturns() == null || txDto.getAmountReturns() == 0.0) {
                    txDto.setAmountReturns(amount);
                } else {
                    txDto.setAmountReturns(txDto.getAmountReturns() + amount);
                }
                TxDetailFragment.txDto = txDto;
                Toast.makeText(getContext(), "Devolucion realizada exitosamente", Toast.LENGTH_LONG).show();
                getActivity().getSupportFragmentManager().popBackStack();
            } else {
                Toast.makeText(getContext(), "No se pudo realizar la Devolucion", Toast.LENGTH_LONG).show();
                binding.btnPay.setVisibility(View.VISIBLE);
                binding.btnCancel.setVisibility(View.VISIBLE);
            }
        });*/
    }
}
