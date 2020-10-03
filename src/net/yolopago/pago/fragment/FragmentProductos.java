package net.yolopago.pago.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.activity.FuncActivity;
import com.wizarpos.emvsample.activity.IdleActivity;
import com.wizarpos.emvsample.activity.Product;
import com.wizarpos.emvsample.activity.ProductAdapter;
import com.wizarpos.emvsample.databinding.FragmentProductosBinding;

import net.yolopago.pago.activity.MainActivity;
import net.yolopago.pago.db.entity.Session;
import net.yolopago.pago.viewmodel.ContractViewModel;
import net.yolopago.pago.viewmodel.TicketViewModel;
import net.yolopago.pago.ws.dto.ticket.TicketDto;
import net.yolopago.pago.ws.dto.ticket.TicketLineDto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentProductos extends AbstractFragment{
    private static final String TAG = "FragmentProductos";
    public ArrayList<Product> productsArrayList = new ArrayList<>();
    private FragmentProductosBinding binding;
    private static int PRECIO_INPUT=0;
    private static int CANTIDAD_INPUT=1;
    private int inputType=0;
    Session session;
    FuncActivity funcActivity = new FuncActivity();
    //ProductAdapter adapter;
    public static String userPrinicpal,taxIdMerchant;
    public static Long idtemrinalPrincipal, idSellerPrincipal,idMerchant;
    public static final int IDLE_ACTIVITY_CODE=1;
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("$ ###,###,##0.00",new DecimalFormatSymbols(Locale.US));
    private static final DecimalFormat PRECIO_FORMAT = new DecimalFormat("###,###,##0.00",new DecimalFormatSymbols(Locale.US));
    TicketDto ticketDto = new TicketDto();


    public FragmentProductos() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_productos, container, false);
        binding.precio.setFilters(new InputFilter[]{new DecimalDigitInputFilters(14,2)});
        //binding.agregarProducto.setVisibility(View.INVISIBLE);
        binding.precio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            //    binding.agregarProducto.setVisibility(View.VISIBLE);
            }
        });
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
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    switch (keyCode) {

                        case KeyEvent.KEYCODE_0:
                            changeValue(0,"");
                            break;
                        case KeyEvent.KEYCODE_1:
                        case KeyEvent.KEYCODE_2:
                        case KeyEvent.KEYCODE_3:
                        case KeyEvent.KEYCODE_4:
                        case KeyEvent.KEYCODE_5:
                        case KeyEvent.KEYCODE_6:
                        case KeyEvent.KEYCODE_7:
                        case KeyEvent.KEYCODE_8:
                        case KeyEvent.KEYCODE_9:
                            changeValue(keyCode-7,"");
                            break;
                        case KeyEvent.KEYCODE_DEL:
                            changeValue(-1,"");
                            break;
                        case KeyEvent.KEYCODE_ENTER:
                            AgregarProductos();
                            break;
                        case KeyEvent.KEYCODE_ESCAPE:
                            cancelarProducto();
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }
        SharedPreferences sp = getContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        userPrinicpal = sp.getString("username", "");
        //passPrincipal = sp.getString("password", "");
        idtemrinalPrincipal = sp.getLong("terminalId", 0L);
        idSellerPrincipal = sp.getLong("sellerid", 0L);
        idMerchant = sp.getLong("merchantid", 0L);
        taxIdMerchant = sp.getString("merchanttaxid", "");
        productsArrayList =((MainActivity)getActivity()).getProducList();
        setCobrarText();
        productsArrayList =((MainActivity)getActivity()).getProducList();
        binding.backPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getContext(), FirmaActivity.class);
                //startActivity(intent);
                PrincipalFragment principalFragment = new PrincipalFragment();
                setFragment(principalFragment);
            }
        });
        binding.precio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputType=PRECIO_INPUT;
            }
        });
        binding.cantidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputType=CANTIDAD_INPUT;
            }
        });
        binding.upCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cantidadAdd(1);
            }
        });
        binding.downCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cantidadAdd(-1);
            }
        });
        binding.producto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(binding.producto.getWindowToken(), 0);
                    binding.producto.clearFocus();
                    return true;
                }
                return false;
            }
        });



        binding.btnDigit0.setOnClickListener(this::clickListener);
        binding.btnDigit1.setOnClickListener(this::clickListener);
        binding.btnDigit2.setOnClickListener(this::clickListener);
        binding.btnDigit3.setOnClickListener(this::clickListener);
        binding.btnDigit4.setOnClickListener(this::clickListener);
        binding.btnDigit5.setOnClickListener(this::clickListener);
        binding.btnDigit6.setOnClickListener(this::clickListener);
        binding.btnDigit7.setOnClickListener(this::clickListener);
        binding.btnDigit8.setOnClickListener(this::clickListener);
        binding.btnDigit9.setOnClickListener(this::clickListener);
        binding.btnDigitDel.setOnClickListener(this::clickListener);


      //  binding.CancelarProducto.setOnClickListener(v -> cancelarProducto());
        binding.btnDigitCobrar.setOnClickListener(v -> VerResumen());
        binding.btnDigitAddcar.setOnClickListener(v -> AgregarProductos());
        binding.backPrincipal.setOnClickListener(v -> salir());

    }

    private void clickListener(View v) {
        switch (v.getId()){
            case R.id.btn_digit_0:changeValue(0,"");break;
            case R.id.btn_digit_1:changeValue(1,"");break;
            case R.id.btn_digit_2:changeValue(2,"");break;
            case R.id.btn_digit_3:changeValue(3,"");break;
            case R.id.btn_digit_4:changeValue(4,"");break;
            case R.id.btn_digit_5:changeValue(5,"");break;
            case R.id.btn_digit_6:changeValue(6,"");break;
            case R.id.btn_digit_7:changeValue(7,"");break;
            case R.id.btn_digit_8:changeValue(8,"");break;
            case R.id.btn_digit_9:changeValue(9,"");break;
            case R.id.btn_digit_del:changeValue(-1,"");break;

        }
    }

    private void changeValue(int number,String dot) {
        if(inputType==PRECIO_INPUT) {
            int maxDigit = 10;
            NumberFormat format = NumberFormat.getInstance(Locale.US);
            Number numberParse = null;
            try {
                numberParse = format.parse(binding.precio.getText().toString());
            } catch (ParseException e) {
                numberParse=0;
            }
            Double preciod = numberParse.doubleValue();
            String cantidad = "";
            if (binding.precio.getText().toString().length() <= maxDigit && number != -1) {
                preciod = (preciod * 10) + (number / 100f);
                cantidad = String.format(Locale.US,"%.2f", preciod);
                binding.precio.setText(PRECIO_FORMAT.format(preciod));
            } else if (number == -1) {
                preciod = (Math.floor(preciod * 10f) / 100f);
                cantidad = String.format(Locale.US,"%.2f", preciod);
                binding.precio.setText(PRECIO_FORMAT.format(preciod));
            }
        }else {
            int maxDigit = 6;
            Integer preciod = binding.cantidad.getText().toString().equals("")?0:Integer.parseInt(binding.cantidad.getText().toString());
            String cantidad = "";
            if (binding.cantidad.getText().toString().length() <= maxDigit && number != -1) {
                preciod = (preciod * 10)+number;
                cantidad = String.format(Locale.US,"%d", preciod);
                binding.cantidad.setText(cantidad);
            } else if (number == -1) {
                preciod = (preciod  / 10);
                if(preciod!=0) {
                    cantidad = String.format(Locale.US,"%d", preciod);
                }
                binding.cantidad.setText(cantidad);
            }
        }

    }
    private void cantidadAdd(int number) {

            int maxDigit = 6;
            Integer preciod = binding.cantidad.getText().toString().equals("")?0:Integer.parseInt(binding.cantidad.getText().toString());
            String cantidad = "";
            if (binding.cantidad.getText().toString().length() <= maxDigit && preciod >= 0 ) {
                preciod = preciod +number;
                preciod = (preciod<0)?0:(preciod>=Math.pow(10,maxDigit))?(int) (Math.pow(10,maxDigit)-1):preciod;
                if(preciod!=0) {
                    cantidad = String.format(Locale.US,"%d", preciod);
                }
                binding.cantidad.setText(cantidad);
            }
    }

    public void cancelarProducto(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        // Configura el titulo.
        alertDialogBuilder.setTitle("Cancelar producto");

        // Configura el mensaje.
        alertDialogBuilder
                .setMessage("¿Desea cancelar el producto?")
                .setCancelable(false)
                .setPositiveButton("SI",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        binding.producto.setText("");
                        binding.cantidad.setText("");
                        binding.precio.setText("0.00");
                      //  binding.precioU.setText("");
                       // binding.vertotal.setText("");
                    }
                })
                .setNegativeButton("NO",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                }).create().show();
    }

    public void AgregarProductos(){
            if (binding.producto.getText().toString().equals("")  || binding.precio.getText().toString().equals("")) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                // Configura el titulo.
               // alertDialogBuilder.setTitle("Error Producto");
                alertDialogBuilder
                        .setMessage("Ingresa la información del producto")
                        .setCancelable(false)
                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .create().show();
            } else  if(binding.precio.getText().toString().equals(".") || binding.precio.getText().toString().equals("0.00") ||
                    binding.precio.getText().toString().equals("0.0") || binding.precio.getText().toString().equals("0")){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder
                        .setMessage("Ingresa el precio para el producto")
                        .setCancelable(false)
                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .create().show();
            }else if(binding.cantidad.getText().toString().equals("")|| binding.cantidad.getText().toString().equals("0") || binding.cantidad.getText().toString().equals("00")
            || binding.cantidad.getText().toString().equals("000") || binding.cantidad.getText().toString().equals("0000")
            || binding.cantidad.getText().toString().equals("00000") ){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder
                        .setMessage("Ingresa la cantidad para el producto")
                        .setCancelable(false)
                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .create().show();
            }else{
                //totalVenta();
                String productoTabla = binding.producto.getText().toString();
                String cantidadTabla = binding.cantidad.getText().toString();
                NumberFormat format = NumberFormat.getInstance(Locale.US);
                Number numberParse = null;
                try {
                    numberParse = format.parse(binding.precio.getText().toString());
                } catch (ParseException e) {
                    numberParse=0;
                }
                Double precioTablas = numberParse.doubleValue();
                Double cantidad = Double.parseDouble(binding.cantidad.getText().toString());
                Double totalTablas = precioTablas*cantidad;
                Double sum = totalTablas;

                if (productoTabla != null && cantidadTabla != null ) {
                    Product product = new Product(productoTabla, cantidadTabla, precioTablas, totalTablas, sum);

                    if(totalAmount(product)<10000000) {
                        productsArrayList.add(product);
                        //adapter.notifyDataSetChanged();
                        binding.producto.setText("");
                        binding.cantidad.setText("");
                        binding.precio.setText("0.00");
                        setCobrarText();
                        ((MainActivity) getActivity()).setBadgeCount();
                    }else{
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                        alertDialogBuilder
                                .setMessage("El monto total no puede ser mayor o igual a 10M")
                                .setCancelable(false)
                                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                                .create().show();
                    }
                }
            }

    }

    private void setCobrarText() {

        Double total=0d;
        if(productsArrayList!=null) {
            for (int i = 0; i < productsArrayList.size(); i++) {
                Double data3 = productsArrayList.get(i).getTotal();
                total +=(data3);
            }
        }
       // String cantidad = String.format("%.2f", total);
        binding.btnDigitCobrar.setText("Cobrar "+MONEY_FORMAT.format(total));
    }
    public Double totalAmount(Product product) {
        Double total=0d;
        if(productsArrayList!=null) {
            for (int i = 0; i < productsArrayList.size(); i++) {
                Double data3 = productsArrayList.get(i).getTotal();
                total +=(data3);
            }
        }
        total+=product.getTotal();
        return total;
    }

    public void totalVenta(){

        BigDecimal bda = new BigDecimal(binding.precio.getText().toString());
        bda = bda.setScale(2, RoundingMode.HALF_UP);
        float a = Float.parseFloat(String.valueOf(bda));


        BigDecimal bdb = new BigDecimal(binding.cantidad.getText().toString());
        bdb = bdb.setScale(2, RoundingMode.HALF_UP);
        float b = Float.parseFloat(String.valueOf(bdb));

        Float x = a * b;

    }
    public void VerResumen(){

        makePay();
    }

    public void refresh() {
        productsArrayList=((MainActivity)getActivity()).getProducList();
        setCobrarText();
        ((MainActivity)getActivity()).setBadgeCount();
    }

    public void salir(){
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getContext());
        // Configura el titulo.
         // Configura el mensaje.
        alertDialogBuilder
                .setMessage("¿Deseas cerrar la sesión?")
                .setCancelable(false)
                .setPositiveButton("SI",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Toast.makeText(getContext(), "¡Sesión finalizada!", Toast.LENGTH_LONG ).show();
                        ((MainActivity)getActivity()).clearProductList();
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
    //public static String getContraseñaP(){return  passPrincipal;   }
    public static Long getTerminalP(){  return  idtemrinalPrincipal;    }
    public static Long getIdSellerP(){   return  idSellerPrincipal;    }
    public static Long getIdMerchant(){    return  idMerchant;    }
    public static String getTaxIdMerchant(){  return  taxIdMerchant;    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d("(TAG, "onDestroy: Mureiendo");
    }

    public void makePay(){
        ((MainActivity)getActivity()).checkReversos();
        binding.btnDigitCobrar.setEnabled(false);
        SharedPreferences sp = getContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        Long idSellerPrincipal = sp.getLong("sellerid", 0L);
        Long idUser = sp.getLong("userId", 0L);
        ContractViewModel contractViewModel= new ContractViewModel(this.getActivity().getApplication());
        TicketViewModel ticketViewModel= ViewModelProviders.of(this).get(TicketViewModel.class);
        //Toast.makeText(getContext(), "Validando Terminal", Toast.LENGTH_LONG).show();

        LiveData<String> liveDatasContrac = contractViewModel.canTransac(getModel(),getSerialNumber());
        liveDatasContrac.observe(getActivity(), s -> {
            // liveDatasContrac.removeObservers(this);
            if (s.equals("OK")) {

                if (productsArrayList.size() > 0) {
                    ArrayList<TicketLineDto> listProduct = new ArrayList<TicketLineDto>();
                    for (int i = 0; i < productsArrayList.size(); i++) {
                        String data1 = productsArrayList.get(i).getProducto();
                        String data2 = productsArrayList.get(i).getCantidad();
                        Integer c = Integer.parseInt(data2);
                        Double data3 = productsArrayList.get(i).getPrecio();
                        //Double b = Double.parseDouble(data3);
                        listProduct.add(new TicketLineDto(data1, data3, c));
                        int size = listProduct.size();
                        //Log.i("Information", "Size: " + size);
                    }

                    ticketDto.setTicketLines(listProduct);
                    LiveData<String> liveData = ticketViewModel.crearTicket(ticketDto);
                    liveData.observe(getActivity(), ss -> {
                        if (ss.equals("OK")) {
                            //Toast.makeText(getContext(), "Productos agregados correctamente", Toast.LENGTH_LONG).show();
                            Long IdTicket = ticketViewModel.idTicket(idUser);//
                           // Log.e("ver error", "ejecutando servicio getIdTicket: " + IdTicket+ " ");

                            Double totalInfo = 0d;
                            int size = productsArrayList.size();
                            for (int i = 0; i < size; i++) {
                                totalInfo = totalInfo + productsArrayList.get(i).getSum();
                            }
                            BigDecimal bdSuma = BigDecimal.valueOf(totalInfo);
                            bdSuma = bdSuma.setScale(2, RoundingMode.HALF_UP);

                            Bundle b = new Bundle();
                            b.putString("totalventa", ""+totalInfo);
                            b.putSerializable("pay", (Serializable) productsArrayList);


                            if(!this.exit) {
                                Intent intent = new Intent(getActivity().getApplicationContext(), IdleActivity.class);
                                intent.putExtra("amount", "" + totalInfo);
                                intent.putExtra("idle", (ArrayList<Product>) productsArrayList);
                                getActivity().startActivityForResult(intent, IDLE_ACTIVITY_CODE);
                            }


                        } else {
                            Toast.makeText(getContext(), "No se agregaron productos", Toast.LENGTH_LONG).show();
                        }
                        binding.btnDigitCobrar.setEnabled(true);

                    });


                } else {
                    if(!this.exit) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                        // Configura el titulo.
                        alertDialogBuilder.setTitle("¡Error!");
                        // Configura el mensaje.
                        alertDialogBuilder
                                .setMessage("¡No se puede cobrar con lista de productos vacía!")
                                .setCancelable(false)
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                }).create().show();
                    }
                }
                binding.btnDigitCobrar.setEnabled(true);


            } else {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder
                        .setMessage(s)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        }).create().show();

				   /* Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
					MainActivity mainActivity = (MainActivity) FragmentResumen.this.getActivity();
					FragmentLogin loginFragment = new FragmentLogin();
					mainActivity.setFragment(loginFragment);*/

            }

            binding.btnDigitCobrar.setEnabled(true);

        });


    }

    private String getModel() {
        String model = Build.MODEL;
        model = model.replaceAll("-", "_");
        model = model.replaceAll(" ", "_");
        return model;
    }

    private String getSerialNumber() {
        String serial = Build.SERIAL;
        return serial;
    }
}
