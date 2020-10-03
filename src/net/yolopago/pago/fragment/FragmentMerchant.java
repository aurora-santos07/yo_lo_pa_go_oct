package net.yolopago.pago.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.databinding.FragmentMerchantBinding;

import net.yolopago.pago.db.entity.Merchant;
import net.yolopago.pago.viewmodel.MerchantViewModel;
import net.yolopago.pago.ws.dto.security.UserDto;

public class FragmentMerchant extends AbstractFragment implements View.OnClickListener {
    private FragmentMerchantBinding binding;
    private MerchantViewModel merchantViewModel;
    private UserDto userDto = new UserDto();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        merchantViewModel = ViewModelProviders.of(this).get(MerchantViewModel.class);
        merchantViewModel.getMerchant().observe(this, merchant -> {

            binding.idContractTextView.setText(merchant.getIdContract() + "");
            binding.merchantTextView.setText(merchant.getName());
            binding.addressTextView.setText(formatAddress(merchant));
            binding.phoneTextView.setText(merchant.getPhone());
            //binding.EmailTextView.setText("");
            sessionViewModel.getSession().observe(this, session -> {
                sessionViewModel.getUser(session.getIdUser()).observe(this, user -> {
                    binding.userTextView.setText(user.getName());
                    binding.EmailTextView.setText(user.getEmail());
                });
            });
            binding.terminalTextView.setText(Build.SERIAL);
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_merchant, container, false);
        return binding.getRoot();
    }

    private String formatAddress(Merchant merchant) {
        StringBuffer stringBuffer = new StringBuffer(merchant.getStreet());
        stringBuffer.append(" ").append(merchant.getExternal());
        stringBuffer.append(" ").append(merchant.getInternal());
        return stringBuffer.toString();
    }

    @Override
    public void onClick(View v) {
        logout();
    }
}
