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

import net.yolopago.pago.db.entity.Merchant;
import net.yolopago.pago.viewmodel.MerchantViewModel;
import net.yolopago.pago.ws.dto.security.UserDto;

import com.wizarpos.emvsample.databinding.FragmentPagoServiciosBinding;


public class FragmentPagoServicios extends AbstractFragment implements View.OnClickListener {

    private FragmentPagoServiciosBinding binding;
    private UserDto userDto = new UserDto();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pagos_servicios, container, false);


        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentProductos fragmentProductos = new FragmentProductos();
                setFragment(fragmentProductos);
            }
        });


        return binding.getRoot();
    }


    @Override
    public void onClick(View v) {
        logout();
    }
}
