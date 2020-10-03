package net.yolopago.pago.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import com.wizarpos.emvsample.MainApp;
import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.activity.IdleActivity;
import com.wizarpos.emvsample.activity.Product;
import com.wizarpos.emvsample.constant.ConstantYLP;
import com.wizarpos.emvsample.databinding.FragmentLoginBinding;


import net.yolopago.pago.ws.dto.contract.MerchantDto;
import net.yolopago.pago.ws.dto.security.PrincipalDto;

import java.util.ArrayList;


public class FragmentLogin extends AbstractFragment {
    public FragmentLoginBinding binding;
    public static String usuario, terminal,taxIdMerchant;
    public static Context context;
    protected static MainApp appState = null;

    public static Long idTerminal, idSeller,idUser,idMerchant;
    private static final int pswdIterations = 10;
    private static final int keySize = 128;

    //ASANTOS Variables donde se cargará el valor de monto y concepto provenientes de la otra app si la sesión se encuentra cerrada a la hora de ser llamada
    private String concept;
    private String amount;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);

        //ASANTOS hardcode para hacer pruebas más rápido
        binding.editTextEmail.setText("rogerg@yolopago.com.mx");
        binding.editTextPassword.setText("Hola1234$");
        //ASANTOS se obtienen los valores de monto y concepto
        if(getArguments()!= null){Log.d("getArguments", "notnull");
            if (getArguments().getString(ConstantYLP.EXTRA_AMOUNT) != null) {

                amount = getArguments().getString(ConstantYLP.EXTRA_AMOUNT);
                concept = getArguments().getString(ConstantYLP.EXTRA_CONCEPT);
            }
        }//ASANTOS Aquì termina
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }

        binding.btnLogin.setOnClickListener(v -> validateLogin());
        binding.btnOlvide.setOnClickListener(v-> {
            AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(getContext());
            // Configura el titulo.
            alertDialogBuilder1.setMessage("¿Has olvidado tu contraseña?\n\n Por favor, inicia el proceso de recuperación de contraseña desde el portal web de YoloPago.\n\nwww.yolopago.com.mx")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            sessionViewModel.deleteAll();
                        }
                    }).create().show();
        });
    }

    private void validate(PrincipalDto pricipalDto){
        idTerminal = pricipalDto.getIdTerminal();
        idSeller = pricipalDto.getIdSeller();
        idUser = pricipalDto.getId();
        idMerchant = pricipalDto.getIdMerchant();
        taxIdMerchant = pricipalDto.getTaxIdMerchant();
        String msgAlert="";
        if(pricipalDto.isTerminalActiva()){
            if( pricipalDto.isSellerEnable()){// || pricipalDto.isSellerAcces()) {
                LiveData<PrincipalDto> liveData = paymentViewModel.getPaymentSession(binding.editTextEmail.getText().toString(), binding.editTextPassword.getText().toString(),terminal);
                liveData.observe(this, s -> {
                    liveData.removeObservers(this);
                    if(s!=null) {

                        LiveData<PrincipalDto> liveDataTk = ticketViewModel.getTicketSession(binding.editTextEmail.getText().toString(), binding.editTextPassword.getText().toString(), terminal);
                        liveDataTk.observe(this, s2 -> {

                            if (s2 != null) {

                                liveDataTk.removeObservers(this);
                                usuario = binding.editTextEmail.getText().toString();
                                SharedPreferences sp = getContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("username", usuario);
                                editor.putLong("userId", idUser);
                                editor.putLong("terminalId", idTerminal);
                                editor.putLong("sellerid", idSeller);
                                editor.putLong("merchantid", idMerchant);
                                editor.putString("merchanttaxid", taxIdMerchant);
                                editor.commit();

                                LiveData<MerchantDto> liveDataMr = contractViewModel.getMerchant();
                                liveDataMr.observe(this, sc -> {
                                    liveDataMr.removeObservers(this);
                                    //Log.d("getMerchant", "validate: "+sc);
                                    if (sc != null) {
                                        sessionViewModel.saveMerchant(sc);
                                    }
                                    FragmentNotification fragmentNotification = new FragmentNotification();
                                    setFragment(fragmentNotification);

                                });
                                //ASANTOS Se valida que el concepto no venga nulo para pasar directamente al pago
                                if(concept != null){
                                    callPayment();
                                }

                            } else {
                                AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(getContext());
                                // Configura el titulo.
                                alertDialogBuilder1.setMessage("Error al autenticarte")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                sessionViewModel.deleteAll();
                                            }
                                        }).create().show();
                            }

                        });
                    }else{
                        AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(getContext());
                        // Configura el titulo.
                        alertDialogBuilder1.setMessage("Error al autenticarte")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        sessionViewModel.deleteAll();
                                    }
                                }).create().show();
                    }



                });


            }else if(!pricipalDto.isSellerAcces()){
                msgAlert="Usuario sin acceso";
            }else {
                msgAlert="Usuario inactivo";
            }
        }else{
         msgAlert="Terminal Inactiva";
        }

        if(!msgAlert.equals("")) {
            AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(getContext());
            // Configura el titulo.
            alertDialogBuilder1.setMessage(msgAlert)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            sessionViewModel.deleteAll();
                        }
                    }).create().show();
        }
    }

    private void validateLogin() {

       if(isConnected()) {

           Log.e("Error", "validateLogin");
           terminal = Build.SERIAL;
           binding.loginProgressBar.setVisibility(View.VISIBLE);
           binding.loginProgressBar.setProgress(25);
           binding.btnLogin.setEnabled(false);
           binding.txtMsgError.setText("");

           LiveData<PrincipalDto> liveData = contractViewModel.getContractSesion(binding.editTextEmail.getText().toString(), binding.editTextPassword.getText().toString(),terminal);
           liveData.observe(this, s -> {
               binding.loginProgressBar.setProgress(100);
               binding.loginProgressBar.setVisibility(View.INVISIBLE);
               liveData.removeObservers(this);
               Log.w("Error", "s: " + s);
               if (s == null) {
                   AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(getContext());
                   // Configura el titulo.
                   alertDialogBuilder1.setMessage("Error al autenticarte")
                           .setCancelable(false)
                           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int id) {
                                   sessionViewModel.deleteAll();
                               }
                           }).create().show();
               } else if (s != null) {
                   validate(s);
               } else  {
                   FragmentSelectMerchant fragmentSelectMerchant = new FragmentSelectMerchant();
                   setFragment(fragmentSelectMerchant);
               }

               binding.btnLogin.setEnabled(true);
           });
       }else{
           Toast.makeText(getContext(), "Sin Conexión", Toast.LENGTH_SHORT).show();
       }
    }
    public static String getTerminalId(){
        return  terminal;
    }


    public boolean isConnected(){
        boolean connected = false;
        try{
            ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        }catch (Exception e){
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    /*ASANTOS Método que setea el fragment de pagos pasando al intent los valores de monto y  concepto */
    private void callPayment(){
        Intent intent = new Intent(getActivity().getApplicationContext(), IdleActivity.class);
        intent.putExtra("amount", amount);
        ArrayList<Product>pay =  new ArrayList<Product>();
        pay.add(new Product(concept, "1", Double.parseDouble(amount), Double.parseDouble(amount), Double.parseDouble(amount)));
        intent.putExtra("idle",(ArrayList<Product>) pay );
        getActivity().startActivityForResult(intent, 0);
    }
}
