package com.wizarpos.emvsample.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wizarpos.emvsample.R;

import net.yolopago.pago.ws.dto.ticket.TicketLineDto;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class ResumenCompraAdapter extends BaseAdapter {
    Context context;
    ArrayList<TicketLineDto> lineArrayList;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("$ ###,###,##0.00",new DecimalFormatSymbols(Locale.US));

    public ResumenCompraAdapter(Context context, ArrayList<TicketLineDto> lineArrayList) {
        this.context = context;
        this.lineArrayList = lineArrayList;
    }
    @Override
    public int getCount() {
        return lineArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View vi = view;

        if (vi == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.list_products, null);
        }

        TextView productoV  = (TextView)vi.findViewById(R.id.prod);
        TextView cantidadV  = (TextView)vi.findViewById(R.id.cant);
        TextView precioV  = (TextView)vi.findViewById(R.id.prec);
        TextView totV  = (TextView)vi.findViewById(R.id.tot);

        productoV.setText(lineArrayList.get(i).getProduct());
        cantidadV.setText("" + lineArrayList.get(i).getItems());
        precioV.setText(DECIMAL_FORMAT.format(lineArrayList.get(i).getPrice()));
        totV.setText(DECIMAL_FORMAT.format(lineArrayList.get(i).getPrice()*lineArrayList.get(i).getItems()));

        return vi;
    }
}