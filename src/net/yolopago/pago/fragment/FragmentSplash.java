package net.yolopago.pago.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.constant.ConstantYLP;
import com.wizarpos.emvsample.databinding.FragmentSplashBinding;
import net.yolopago.pago.db.entity.Session;

/**
 * Created by dante on 3/11/18.
 */

public class FragmentSplash extends AbstractFragment {
    private FragmentSplashBinding binding;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
;        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false);
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
                .into(binding.imgSlash);
        //ASANTOS Se agregan argumentos al fragment login
        FragmentLogin fragmentLogin = new FragmentLogin();
        if (getArguments() != null) {
            Bundle args = new Bundle();
            args.putString(ConstantYLP.EXTRA_AMOUNT, getArguments().getString(ConstantYLP.EXTRA_AMOUNT));
            args.putString(ConstantYLP.EXTRA_CONCEPT, getArguments().getString(ConstantYLP.EXTRA_CONCEPT));
            fragmentLogin.setArguments(args);
        }//Termina aquí
        setFragment(fragmentLogin);

    }
}
