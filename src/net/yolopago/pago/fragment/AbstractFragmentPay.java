package net.yolopago.pago.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.wizarpos.emvsample.databinding.FragmentPayBinding;
import com.wizarpos.util.AppUtil;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Created by dante on 3/11/18.
 */

public abstract class AbstractFragmentPay extends AbstractFragment {
    protected static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(" ###,###,##0.00",new DecimalFormatSymbols(Locale.US));

    protected FragmentPayBinding binding;
    protected long amount = 0l;
    protected long maxAmount = 1000000;
    protected Boolean FIXED_PRICE = Boolean.FALSE;

    protected abstract void setLeyends();

    protected abstract void invokePaymentActivities();

    public void setAmount(Double amount) {
        this.amount = ((Double) (amount * 10)).longValue();
    }

    public double getAmount() {
        return Double.parseDouble(DECIMAL_FORMAT.format(amount / 100.00));
    }

    private void setTextAmount(int digital) {
        if((amount * 10 + digital) <= maxAmount) {
            amount = amount * 10 + digital;
            binding.txtAmount.setText(DECIMAL_FORMAT.format(amount/ 100.00));
        } else {
            Toast.makeText(getContext(), "Importe no puede ser mayor a " + DECIMAL_FORMAT.format(maxAmount / 100.00), Toast.LENGTH_SHORT).show();
        }
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
                            Toast.makeText(getContext(), "Prueba3", Toast.LENGTH_SHORT).show();
                            break;
                        case KeyEvent.KEYCODE_ENTER:
                            break;
                        case KeyEvent.KEYCODE_ESCAPE:
                            Toast.makeText(getContext(), "Prueba1", Toast.LENGTH_SHORT).show();
                            break;
                        case KeyEvent.KEYCODE_BACK:
                            Toast.makeText(getContext(), "Prueba", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }
}
