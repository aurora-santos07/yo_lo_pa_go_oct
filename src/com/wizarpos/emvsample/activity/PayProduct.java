package com.wizarpos.emvsample.activity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.wizarpos.emvsample.R;

import net.yolopago.pago.fragment.FragmentMerchant;
import net.yolopago.pago.fragment.FragmentPay;
import net.yolopago.pago.fragment.TxListFragment;

import java.util.ArrayList;

public class PayProduct extends AppCompatActivity implements FragmentPay.FragmentPayListeer{
    private FragmentPay fragmentPay;
    private TxListFragment txListFragment;
    private FragmentMerchant fragmentMerchant;
    private static Fragment theFragment;

    String data1;
    Integer data2;
    Double Data3;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_product);


        Bundle b = getIntent().getExtras();
        String verTotal = b.getString("pagartotal");
        ArrayList<Product> Pay = (ArrayList<Product>) b.getSerializable("productos");

        for (int i = 0; i < Pay.size(); i++) {
            String data1 = Pay.get(i).getProducto();
            String data2 = Pay.get(i).getCantidad();
            Integer c = Integer.parseInt(data2);
            Double data3 = Pay.get(i).getPrecio();
            //Double dou = Double.parseDouble(data3);
            //TicketLineDto ticketLineDto =
            //Log.i("Information", "lista: " + data1  +", " + c + ", " + data3);
        }

        fragmentPay = new FragmentPay();
        Bundle bundle = new Bundle();
        bundle.putString("message", verTotal);
        bundle.putSerializable("productospay", Pay);
        FragmentPay fragmentPay = new FragmentPay();
        fragmentPay.setArguments(bundle);


        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.containerPago, fragmentPay)
                .commit();


        mOnNavigationItemSelectedListener = item -> {
            switch (item.getItemId()) {
                case R.id.navigation_pay:
                    Toast.makeText(getApplicationContext(), "Pagos", Toast.LENGTH_LONG).show();
                    break;
                case R.id.navigation_voucher:
                    Toast.makeText(getApplicationContext(), "lista", Toast.LENGTH_LONG).show();
                    break;
                case R.id.navigation_merchant:
                    Toast.makeText(getApplicationContext(), "merchant", Toast.LENGTH_LONG).show();
                    break;

            }
            return true;
        };
    }

    // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_bottom_navigation, menu);
        return true;
    }

    @Override
    public void onInputPaySent(CharSequence input) {
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_1:
                onOne();
                break;
        }
        return true;
    }

    public void onOne(){

    }

}
