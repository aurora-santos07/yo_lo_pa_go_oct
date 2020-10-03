package com.wizarpos.emvsample.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wizarpos.emvsample.R;

import net.yolopago.pago.activity.MainActivity;
import net.yolopago.pago.fragment.DetailProductFragment;
import net.yolopago.pago.fragment.FragmentResumen;
import net.yolopago.pago.fragment.TxDetailFragment;
import net.yolopago.pago.ws.dto.payment.TxDto;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class ProductAdapter extends BaseAdapter  {
    Context context;
    ArrayList<Product> productArrayList;
    FragmentResumen resumeFragment;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("$ ###,###,##0.00",new DecimalFormatSymbols(Locale.US));

    public ProductAdapter(Context activity, ArrayList<Product> productArrayList, FragmentResumen resumeFragment) {
        this.resumeFragment = resumeFragment;
        this.context = activity;
        this.productArrayList = productArrayList;
    }
    @Override
    public int getCount() {
        if(productArrayList==null) {
        return 0;
        }
        return productArrayList.size();
    }

    @Override
    public Object getItem(int i) {  return productArrayList.get(i);   }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v = view;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_products, null);
        }

        TextView productoV  = (TextView)v.findViewById(R.id.prod);
        TextView cantidadV  = (TextView)v.findViewById(R.id.cant);
        TextView precioV  = (TextView)v.findViewById(R.id.prec);
        TextView totalV  = (TextView)v.findViewById(R.id.tot);
        //TextView totalG = (TextView)v.findViewById(R.id.totalGlobal);

        productoV.setText(productArrayList.get(i).getProducto());
        cantidadV.setText(productArrayList.get(i).getCantidad());
        precioV.setText(DECIMAL_FORMAT.format(productArrayList.get(i).getPrecio()));
        totalV.setText(DECIMAL_FORMAT.format(productArrayList.get(i).getTotal()));
       // totalG.setText(DECIMAL_FORMAT.format(productArrayList.get(i).getSum()));


        return v;
    }


}