package net.yolopago.pago.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.yolopago.pago.activity.MainActivity;
import net.yolopago.pago.viewmodel.ContractViewModel;
import net.yolopago.pago.viewmodel.PaymentViewModel;
import net.yolopago.pago.viewmodel.SessionViewModel;
import net.yolopago.pago.viewmodel.TicketViewModel;

import java.util.HashSet;

/**
 * Created by dante on 3/11/18.
 */

public abstract class AbstractFragment extends Fragment {
    protected static SessionViewModel sessionViewModel;
    protected static ContractViewModel contractViewModel;
    protected static PaymentViewModel paymentViewModel;
    protected static TicketViewModel ticketViewModel;
    public boolean exit=false;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sessionViewModel = ViewModelProviders.of(this).get(SessionViewModel.class);
        sessionViewModel = ViewModelProviders.of(this).get(SessionViewModel.class);
        contractViewModel = ViewModelProviders.of(this).get(ContractViewModel.class);
        paymentViewModel= ViewModelProviders.of(this).get(PaymentViewModel.class);
        ticketViewModel= ViewModelProviders.of(this).get(TicketViewModel.class);
    }

//    protected void loadFragment(Fragment fragment) {
//        MainActivity activity = (MainActivity) getActivity();
//        activity.loadFragment(fragment);
//    }

    protected void setFragment(Fragment fragment) {
        MainActivity activity = (MainActivity) getActivity();
        activity.setFragment(fragment);
    }

    public void logout() {
        sessionViewModel.deleteAll();
        LiveData<String> liveDataCT= contractViewModel.logOut();
        liveDataCT.observe(this, sa -> {
            LiveData<String> liveDataPM = paymentViewModel.logOut();
            liveDataPM.observe(this, sb -> {
                LiveData<String> liveDataTK = ticketViewModel.logOut();
                liveDataTK.observe(this, sc -> {
                    SharedPreferences sp = getContext().getApplicationContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor memes = sp.edit();
                    memes.putStringSet("COOKIEYLP", new HashSet<String>()).apply();
                    memes.commit();
                    Fragment fragment = new FragmentSplash();
                    setFragment(fragment);
                });

            });

        });

    }


}
