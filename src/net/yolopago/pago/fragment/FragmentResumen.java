package net.yolopago.pago.fragment;


import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.wizarpos.emvsample.MainApp;
import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.activity.IdleActivity;
import com.wizarpos.emvsample.activity.Product;
import com.wizarpos.emvsample.activity.ProductAdapter;
import com.wizarpos.emvsample.activity.TransResultActivity;
import com.wizarpos.emvsample.databinding.FragmentResumenBinding;

import net.yolopago.pago.activity.MainActivity;
import net.yolopago.pago.db.entity.Session;
import net.yolopago.pago.viewmodel.ContractViewModel;
import net.yolopago.pago.viewmodel.MerchantViewModel;
import net.yolopago.pago.viewmodel.TicketViewModel;
import net.yolopago.pago.ws.dto.ticket.PdfDto;
import net.yolopago.pago.ws.dto.ticket.TicketDto;
import net.yolopago.pago.ws.dto.ticket.TicketLineDto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class FragmentResumen extends AbstractFragment {

    private static MerchantViewModel merchantViewModel;
    private static PdfDto pdfDto;
    ProductAdapter adapter;
    TicketDto ticketDto = new TicketDto();
    public static ArrayList<Product> q;
    public static List<TicketLineDto> listProduct;
    TicketViewModel ticketViewModel;
    ContractViewModel contractViewModel;
    protected static MainApp appState;
    public String a;
    private Session session;
    long idSellerPrincipal,idUser;
    String x = null;
    int ids = -1;
    private FragmentResumenBinding binding;
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("$ ###,###,##0.00",new DecimalFormatSymbols(Locale.US));
    public static final int IDLE_ACTIVITY_CODE=1;

    public FragmentResumen() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


       SharedPreferences sp = getContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        idSellerPrincipal = sp.getLong("sellerid", 0L);
        idUser = sp.getLong("userId", 0L);
        contractViewModel= new ContractViewModel(this.getActivity().getApplication());
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_resumen, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }

        session = new Session();


        listProduct = new ArrayList<TicketLineDto>();
        ticketViewModel = ViewModelProviders.of(this).get(TicketViewModel.class);

        q = ((MainActivity)getActivity()).getProducList();

        if (q.size()>0){binding.legend.setVisibility(View.INVISIBLE);}

            if(q!=null) {
                for (int i = 0; i < q.size(); i++) {
                    String data2 = q.get(i).getCantidad();
                }

                adapter = new ProductAdapter(getActivity(), q,this);
                binding.resumenlist1.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                setCobrarValue();

            }


            binding.cancelarProductos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                    alertDialogBuilder.setMessage("¿Deseas vaciar el carrito de compra?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ((MainActivity)getActivity()).clearProductList();
                            q = ((MainActivity)getActivity()).getProducList();
                            adapter = new ProductAdapter(getActivity(), q,FragmentResumen.this);
                            binding.resumenlist1.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            setCobrarValue();
                            ((MainActivity)getActivity()).setBadgeCount();
                            if (q.size()>0){binding.legend.setVisibility(View.INVISIBLE);}else{binding.legend.setVisibility(View.VISIBLE);}
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }).create().show();


                }
            });


            binding.resumenlist1.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                    switch (index) {
                        case 1:

                            break;
                        case 0:
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                            alertDialogBuilder
                                    .setMessage("¿Desea quitar el producto del carrito de compra?")
                                    .setCancelable(false)
                                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            q.remove(position); // remove item at index in list datasource
                                            adapter.notifyDataSetChanged(); // call it for refresh ListView
                                             setCobrarValue();
                                            ((MainActivity)getActivity()).setBadgeCount();
                                            if (q.size()>0){binding.legend.setVisibility(View.INVISIBLE);}else{binding.legend.setVisibility(View.VISIBLE);}
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    }).create().show();
                            break;
                    }
                    // false : close the menu; true : not close the menu
                    return false;
                }
            });

            binding.back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        FragmentProductos fragmentProductos = new FragmentProductos();
                        setFragment(fragmentProductos);
                }
            });

            binding.btnVender.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    makePay();
                }
            });
            SwipeMenuCreator creator = new SwipeMenuCreator() {

                @Override
                public void create(SwipeMenu menu) {
                    SwipeMenuItem openItem = new SwipeMenuItem( getContext());
                    SwipeMenuItem deleteItem = new SwipeMenuItem(  getContext());
                    deleteItem.setBackground(null);
                    deleteItem.setWidth(100);
                    deleteItem.setBackground(R.drawable.bg_delete_button_swipe);
                    menu.addMenuItem(deleteItem);
                }
            };
            binding.resumenlist1.setMenuCreator(creator);
        //}

        binding.resumenlist1.setOnItemClickListener(new SwipeMenuListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               SwipeMenuListView adt = (SwipeMenuListView)adapterView;
                Product product=(Product)adt.getAdapter().getItem(i);
                //Log.i("Information", "Item: " + product.getProducto());
                MainActivity mainActivity = (MainActivity) FragmentResumen.this.getActivity();
                DetailProductFragment detailProductFragment = new DetailProductFragment();
                detailProductFragment.setProduct(product,i);
                mainActivity.setFragment(detailProductFragment);
            }
        });
    }

    public void makePay() {

        binding.btnVender.setEnabled(false);
        ((MainActivity)getActivity()).checkReversos();
        //Toast.makeText(getContext(), "Validando Terminal", Toast.LENGTH_LONG).show();
        LiveData<String> liveDatasContrac = contractViewModel.canTransac(getModel(),getSerialNumber());
        liveDatasContrac.observe(getActivity(), s -> {
            // liveDatasContrac.removeObservers(this);
            if (s.equals("OK")) {

                if (q.size() > 0) {
                    listProduct = new ArrayList<TicketLineDto>();
                    for (int i = 0; i < q.size(); i++) {
                        String data1 = q.get(i).getProducto();
                        String data2 = q.get(i).getCantidad();
                        Integer c = Integer.parseInt(data2);
                        Double data3 = q.get(i).getPrecio();
                        //Double b = Double.parseDouble(data3);
                        listProduct.add(new TicketLineDto(data1, data3, c));
                        int size = listProduct.size();
                        //Log.i("Information", "Size: " + size);
                    }

                    ticketDto.setTicketLines(listProduct);
                    LiveData<String> liveData = ticketViewModel.crearTicket(ticketDto);
                    liveData.observe(getActivity(), ss -> {
                        if (ss.equals("OK")) {
                            // Toast.makeText(getContext(), "Productos agregados correctamente", Toast.LENGTH_LONG).show();
                            Long IdTicket = ticketViewModel.idTicket(idUser);//
                            Log.e("ver error", "ejecutando servicio getIdTicket: " + IdTicket);

                            Double totalInfo = 0d;
                            int size = q.size();
                            for (int i = 0; i < size; i++) {
                                totalInfo = totalInfo + q.get(i).getSum();
                            }
                            BigDecimal bdSuma = BigDecimal.valueOf(totalInfo);
                            bdSuma = bdSuma.setScale(2, RoundingMode.HALF_UP);

                            Bundle b = new Bundle();
                            b.putString("totalventa", ""+totalInfo);
                            b.putSerializable("pay", (Serializable) q);



                               /* FragmentPay fragmentPay = new FragmentPay();
                                fragmentPay.setArguments(b);
                                setFragment(fragmentPay);*/

                            Intent intent = new Intent(getActivity().getApplicationContext(), IdleActivity.class);
                            intent.putExtra("amount",""+totalInfo);
                            intent.putExtra("idle", (ArrayList<Product>) q);
                            getActivity().startActivityForResult(intent, IDLE_ACTIVITY_CODE);


                        } else {
                            Toast.makeText(getContext(), "No se agregaron productos", Toast.LENGTH_LONG).show();
                        }
                        binding.btnVender.setEnabled(true);

                    });


                } else {
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
                binding.btnVender.setEnabled(true);


            } else {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder
                        .setMessage(s)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        }).create().show();
            }

            binding.btnVender.setEnabled(true);

        });
    }

    public void setCobrarValue(){
        Double totalInfo = 0d;
        int size = q.size();
        for (int i = 0; i < size; i++) {
            totalInfo = totalInfo + q.get(i).getTotal();
        }

         binding.btnVender.setText("Cobrar " + MONEY_FORMAT.format(totalInfo));
    }
    public void refresh() {
        q=((MainActivity)getActivity()).getProducList();
        adapter = new ProductAdapter(getActivity(), q,this);
        binding.resumenlist1.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        setCobrarValue();
        ((MainActivity)getActivity()).setBadgeCount();
    }

    public void enableBtns() {
        binding.btnVender.setEnabled(true);
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
