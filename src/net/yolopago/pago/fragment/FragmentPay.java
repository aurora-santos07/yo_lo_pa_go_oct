package net.yolopago.pago.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;

import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.activity.IdleActivity;
import com.wizarpos.emvsample.activity.Product;

import net.yolopago.pago.viewmodel.ContractViewModel;
import net.yolopago.pago.viewmodel.PaymentViewModel;
import net.yolopago.pago.ws.dto.ticket.TicketLineDto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by dante on 3/11/18.
 */

public class FragmentPay extends AbstractFragmentPay {
    private PaymentViewModel paymentViewModel;
    Product product = new Product();
    ArrayList<Product> pay;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("$ ###,###,##0.00",new DecimalFormatSymbols(Locale.US));
    public interface FragmentPayListeer {
        void onInputPaySent(CharSequence input);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pay, container, false);
        Bundle getArgs;
        getArgs = this.getArguments();
        if(getArgs != null) {
            pay = (ArrayList<Product>) getArgs.getSerializable("pay");
            for (int i = 0; i < pay.size(); i++) {
                String data1 = pay.get(i).getProducto();
                String data2 = pay.get(i).getCantidad();
                Integer c = Integer.parseInt(data2);
                Double data3 = pay.get(i).getPrecio();
                Double sum = pay.get(i).getSum();
                //Log.i("Information", "lista payment: " + data1 + ", " + c + ", " + data3 + ", " + sum);
            }
        }

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String myvalue = bundle.getString("totalventa");
            Double b = Double.parseDouble(myvalue);
            BigDecimal bd = BigDecimal.valueOf(b);
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            //Log.i("Information", "totalPagar: " + bd);
            Handler handler = new Handler();
            BigDecimal finalBd = bd;
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    binding.txtAmount.setText(DECIMAL_FORMAT.format(finalBd));
                }
            };
            handler.postDelayed(r, 500);

        } else {
            //Log.i("Information", "Nulo: ");
        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setLeyends();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_0:
                            break;
                        case KeyEvent.KEYCODE_1:
                            break;
                        case KeyEvent.KEYCODE_2:
                            break;
                        case KeyEvent.KEYCODE_3:
                            break;
                        case KeyEvent.KEYCODE_4:
                            break;
                        case KeyEvent.KEYCODE_5:
                            break;
                        case KeyEvent.KEYCODE_6:
                            break;
                        case KeyEvent.KEYCODE_7:
                            break;
                        case KeyEvent.KEYCODE_8:
                            break;
                        case KeyEvent.KEYCODE_9:
                            break;
                        case KeyEvent.KEYCODE_DEL:
                            break;
                        case KeyEvent.KEYCODE_ENTER:
                            //invokePaymentActivities();
                            break;
                        case KeyEvent.KEYCODE_ESCAPE:
                            setAmount(0.0);
                            binding.txtAmount.setText(DECIMAL_FORMAT.format(0.0));
                            FragmentProductos fragmentProductos = new FragmentProductos();
                            setFragment(fragmentProductos);
                            break;
                        case KeyEvent.KEYCODE_BACK:
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void setLeyends() {
       // binding.txtPayTitle.setText("Cobro");
       // binding.txtAmount.setText(" 0.00");
      //  binding.btnPay.setText("Cobrar");

        binding.btnPay.setOnClickListener(v -> invokePaymentActivities());
        binding.btnCancel.setOnClickListener(v -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            // Configura el titulo.
            alertDialogBuilder.setTitle("¡Cancelar!");
            // Configura el mensaje.
            alertDialogBuilder
                    .setMessage("¿Desea cancelar la compra?")
                    .setCancelable(false)
                    .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (FIXED_PRICE) {
                                return;
                            }
                            setAmount(0.0);
                            binding.txtAmount.setText(DECIMAL_FORMAT.format(0.0));
                            Toast.makeText(getContext(), "¡Compra cancelada!", Toast.LENGTH_LONG).show();
                            FragmentProductos fragmentProductos = new FragmentProductos();
                            setFragment(fragmentProductos);
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).create().show();
        });
    }

    protected void invokePaymentActivities() {
        if(isConnected()) {
            //           Intent intent = new Intent(getActivity().getApplicationContext(), FirmaActivity.class);
            //           startActivityForResult(intent, 0);
            if (binding.txtAmount.getText().toString().equals("0.00")) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                // Configura el titulo.
                alertDialogBuilder.setTitle("¡Error!");
                // Configura el mensaje.
                alertDialogBuilder
                        .setMessage("¡No se puede cobrar si la cantidad es $0.00!")
                        .setCancelable(false)
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        }).create().show();
            } else {
                Bundle bundle = this.getArguments();
                if (bundle != null) {
                    String myvalue = bundle.getString("totalventa");
                    float b = Float.parseFloat(myvalue);
                    BigDecimal bd = BigDecimal.valueOf(b);
                    bd = bd.setScale(2, RoundingMode.HALF_UP);
                    //Log.i("Information", "totalPagar: " + bd);
                    Handler handler = new Handler();
                    BigDecimal finalBd = bd;
                    binding.txtAmount.setText("" + finalBd);
                    String monto = binding.txtAmount.getText().toString();
                    Intent intent = new Intent(getActivity().getApplicationContext(), IdleActivity.class);
                    intent.putExtra("amount", monto);
                    intent.putExtra("idle", (ArrayList<Product>) pay);
                    startActivityForResult(intent, 0);

                } else {
                    //Log.i("Information", "Nulo: ");
                }
            }
        }else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            // Configura el titulo.
            alertDialogBuilder.setTitle("¡Sin acceso a internet!");
            // Configura el mensaje.
            alertDialogBuilder
                    .setMessage("¡Favor de verificar la terminal!")
                    .setCancelable(false)
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    }).create().show();
        }
    }

    public boolean isConnected(){
        boolean connected = false;
        try{
            ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        }catch (Exception e){
            //Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }




}