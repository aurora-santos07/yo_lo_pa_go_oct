package net.yolopago.pago.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.databinding.FragmentNotificationBinding;

import net.yolopago.pago.utilities.CipherHM;
import net.yolopago.pago.viewmodel.NotificationViewModel;
import net.yolopago.pago.viewmodel.PaymentViewModel;


/**
 * Created by dante on 3/11/18.
 */

public class FragmentNotification extends AbstractFragment {
    private static final String TAG = "FragmentNotification";
    private NotificationViewModel notificationViewModel;
    private FragmentNotificationBinding binding;
    private PaymentViewModel paymentViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }

        Glide.with(this.getContext())
                .load(R.drawable.yolopagogif) // or url
                .into(binding.imgNotification);
                SharedPreferences spe = getContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
                String keyterminal=spe.getString("key","");

                if(keyterminal.equals("")) {

                    String passkey = CipherHM.randomAlphaNumeric(16);
                    String numeroSerie = ("26262626" + "                                 ").substring(0, 16);
                    String crc32 = CipherHM.CRC32(passkey.getBytes());

                   // Log.d(TAG, "onActivityCreated: passkey:" + passkey + " numeroSerie:" + numeroSerie + " crc32:" + crc32);


                    paymentViewModel = ViewModelProviders.of(this).get(PaymentViewModel.class);
                    paymentViewModel.solicitaLLave(passkey, numeroSerie, crc32).observe(this, response -> {

                        //Log.d("(TAG, "onActivityCreated: response:" + response);
                        if (!response.equals("") && !response.equals("Error")) {
                            String ss="";
                          /*  String subPayworks = response.substring(0, 64);

                            String Selectorsub = CipherHM.getSelector(passkey, numeroSerie, crc32).substring(0, 64);

                            byte[] keyP = CipherHM.hexToBytes(Selectorsub);
                            byte[] ivP = {00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00};
                            byte[] dataP = CipherHM.hexToBytes(subPayworks);

                           ss = CipherHM.dencrypt(keyP, ivP, CipherHM.bytesToHex(dataP).toUpperCase());

                            String crcPayworks = response.substring(64, response.length());
                            String crc = CipherHM.CRC32(CipherHM.hexToBytes(ss));*/

                            if (true){//crcPayworks.equals(crc)) {
                                ss=response;
                                //Log.d("(TAG, "onActivityCreated: OK CRC");
                                SharedPreferences sp = getContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("key", ss);
                                editor.commit();
                                paymentViewModel.paymentRepository.validaKey();

                                FragmentProductos fragmentProductos = new FragmentProductos();
                                setFragment(fragmentProductos);


                            } else {
                                //Log.d("(TAG, "onActivityCreated: NOK CRC");
                                FragmentLogin fragmentlogin = new FragmentLogin();
                                setFragment(fragmentlogin);
                            }


                        } else {
                            Log.d(TAG, "onActivityCreated: ERROR RESPONSE");
                            FragmentLogin fragmentlogin = new FragmentLogin();
                            setFragment(fragmentlogin);
                        }
                    });


                }else{
                    FragmentProductos fragmentProductos = new FragmentProductos();
                    setFragment(fragmentProductos);
                }


    }
}
