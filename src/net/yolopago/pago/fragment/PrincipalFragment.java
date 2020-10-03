package net.yolopago.pago.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.activity.FuncActivity;
import com.wizarpos.emvsample.activity.IdleActivity;
import com.wizarpos.emvsample.activity.Product;
import com.wizarpos.emvsample.activity.ProductAdapter;
import com.wizarpos.emvsample.activity.TransResultActivity;
import com.wizarpos.emvsample.databinding.FragmentPricipalBinding;

import net.yolopago.pago.db.entity.Session;

import java.io.Serializable;
import java.util.ArrayList;

public class PrincipalFragment extends AbstractFragment{
    ArrayList<Product> productsArrayListP = new ArrayList<>();
    private FragmentPricipalBinding binding;
    Session session;
    FuncActivity funcActivity = new FuncActivity();
    ProductAdapter adapter;
    public static String userPrinicpal, passPrincipal;
    public static Long idtemrinalPrincipal, idSellerPrincipal;

    public PrincipalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_principal, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }

        SharedPreferences sp = getContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        userPrinicpal = sp.getString("username", "");
      //  passPrincipal = sp.getString("password", "");
       // idtemrinalPrincipal = sp.getLong("terminalId", 0L);
        idSellerPrincipal = sp.getLong("sellerid", 0L);

        Log.e("Error", "validand datos del shared preferences: " +  userPrinicpal + " " + idtemrinalPrincipal + " " +
                idSellerPrincipal);



        String getOtroMetodo = TransResultActivity.changePay() + binding.modventaP.getText().toString();
        Log.e("FatalError", "viendo valor: " + getOtroMetodo);
        if(getOtroMetodo.equals("cambiarMetodoPago")){
            productsArrayListP = IdleActivity.listIdle();
            for (int i = 0; i < productsArrayListP.size(); i++) {
                String data1 = productsArrayListP.get(i).getProducto();
                String data2 = productsArrayListP.get(i).getCantidad();
                Integer c = Integer.parseInt(data2);
                Double data3 = productsArrayListP.get(i).getPrecio();
                //Double dou = Double.parseDouble(data3);
                //Log.i("Information", "Regresa Productos Metodo pago: " + data1 + ", " + c + ", " + data3);
            }
            Bundle b = new Bundle();
            b.putSerializable("key", (Serializable) productsArrayListP);
            FragmentResumen fragmentResumen = new FragmentResumen();
            fragmentResumen.setArguments(b);
            setFragment(fragmentResumen);
        }else if(getOtroMetodo.equals("cambiarMetodoPagoBloqueo")){
            productsArrayListP = IdleActivity.listIdle();
            for (int i = 0; i < productsArrayListP.size(); i++) {
                String data1 = productsArrayListP.get(i).getProducto();
                String data2 = productsArrayListP.get(i).getCantidad();
                Integer c = Integer.parseInt(data2);
                Double data3 = productsArrayListP.get(i).getPrecio();
                //Double dou = Double.parseDouble(data3);
                //Log.i("Information", "Regresa Productos Metodo pago: " + data1 + ", " + c + ", " + data3);
            }
            Bundle b = new Bundle();
            b.putSerializable("key", (Serializable) productsArrayListP);
            FragmentResumen fragmentResumen = new FragmentResumen();
            fragmentResumen.setArguments(b);
            setFragment(fragmentResumen);
        }else if(getOtroMetodo.equals("cambiarMetodoPagoMalInsert")){
            productsArrayListP = IdleActivity.listIdle();
            for (int i = 0; i < productsArrayListP.size(); i++) {
                String data1 = productsArrayListP.get(i).getProducto();
                String data2 = productsArrayListP.get(i).getCantidad();
                Integer c = Integer.parseInt(data2);
                Double data3 = productsArrayListP.get(i).getPrecio();
                //Double dou = Double.parseDouble(data3);
                //Log.i("Information", "Regresa Productos Metodo pago: " + data1 + ", " + c + ", " + data3);
            }
            Bundle b = new Bundle();
            b.putSerializable("key", (Serializable) productsArrayListP);
            FragmentResumen fragmentResumen = new FragmentResumen();
            fragmentResumen.setArguments(b);
            setFragment(fragmentResumen);
        }
        binding.logout.setOnClickListener(v -> salir());

    }

    public void salir(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        // Configura el titulo.
       // alertDialogBuilder.setTitle("Salir");

        // Configura el mensaje.
        alertDialogBuilder
                .setMessage("¿Deseas cerrar la sesión?")
                .setCancelable(false)
                .setPositiveButton("Si",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Toast.makeText(getContext(), "¡Sesión finalizada!", Toast.LENGTH_LONG ).show();
                        logout();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                }).create().show();
    }


    public static String getUsuarioP(){
        return  userPrinicpal;
    }

    public static Long getIdSellerP(){
        return  idSellerPrincipal;
    }

}
