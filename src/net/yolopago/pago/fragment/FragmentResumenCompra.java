package net.yolopago.pago.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.activity.ResumenCompraAdapter;
import com.wizarpos.emvsample.databinding.FragmentResumenCompraBinding;
import net.yolopago.pago.ws.dto.ticket.TicketLineDto;

import java.util.ArrayList;

public class FragmentResumenCompra extends AbstractFragment {

    ResumenCompraAdapter adapter;
    public static ArrayList<TicketLineDto> listProductCompra = new ArrayList<>();
    private FragmentResumenCompraBinding binding;

    public FragmentResumenCompra() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_resumen_compra, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }

        Bundle getArgs;
        getArgs = this.getArguments();
        if (getArgs != null) {
            listProductCompra = (ArrayList<TicketLineDto>) getArgs.getSerializable("ResumenCompra");
            for (int i = 0; i < listProductCompra.size(); i++) {
                String data1 = listProductCompra.get(i).getProduct();
                Integer data2 = listProductCompra.get(i).getItems();
                Double data3 = listProductCompra.get(i).getPrice();
                //Log.i("Information", "lista resumen compra: " + data1 + ", " + data2 + ", " + data3);
            }
        }

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String myvalue = bundle.getString("totalventa");
            binding.totalVenta.setText(myvalue);
        }

        adapter = new ResumenCompraAdapter(getActivity(), listProductCompra);
            binding.resumenlistcompra.setAdapter(adapter);


            binding.regresarResumen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TxDetailFragment txDetailFragment = new TxDetailFragment();
                    setFragment(txDetailFragment);
                }
            });
        }
}
