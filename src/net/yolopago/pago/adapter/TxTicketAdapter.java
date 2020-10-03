package net.yolopago.pago.adapter;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TxTicketAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<TicketLineDto> items;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###,##0.00",new DecimalFormatSymbols(Locale.US));


    public TxTicketAdapter(Context context, int layout, ArrayList<TicketLineDto> items){
        this.context = context;
        this.layout = layout;
        this.items = items;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override

    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View v = convertView;

        LayoutInflater layoutInflater = LayoutInflater.from(this.context);

        v= layoutInflater.inflate(R.layout.list_tx_ticket, null);
        TicketLineDto currentItem  = items.get(position);

        TextView textView = (TextView) v.findViewById(R.id.item_producto);
        TextView cantidadView = (TextView) v.findViewById(R.id.item_cantidad);
        TextView totalView = (TextView) v.findViewById(R.id.item_total);

        textView.setText(currentItem.getProduct());
        totalView.setText(DECIMAL_FORMAT.format(currentItem.getPrice()));
        cantidadView.setText(""+currentItem.getItems());

        return v;
    }

    public void updateData(ArrayList items) {
        this.items=items;
        notifyDataSetChanged();
    }
}
