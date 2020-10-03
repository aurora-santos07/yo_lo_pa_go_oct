package com.wizarpos.emvsample.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.cloudpos.jniinterface.PrinterInterface;
import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.constant.ConstantYLP;

import net.yolopago.pago.utilities.PrinterHM;

public class cancelacionPrint extends FuncActivity {
    Button ok;
    Button reimprimir;
    TextView message;
    public static int END_FAIL_SI=2001;
    public static int END_FAIL_NO=2002;
    public static int END_OK=2003;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelacion_print);
        ok = findViewById(R.id.okButton);
        reimprimir = findViewById(R.id.reimprimir);
        message = findViewById(R.id.tTransResult_Line1);
        message.setText("Cancelando transacción");
        printReceiptCancel(true,true);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              // Intent intent = new Intent(cancelacionPrint.this, MainActivity.class);
              //  startActivity(intent);
                setResult(END_OK);
                exit();
            }
        });
        reimprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printReceiptCancel(true,false);
            }
        });
    }


    private void printReceiptCancel(boolean print,boolean save) {
            appState.typePrintRecive= ConstantYLP.RECIBO_CANCELADO;
        PrinterHM printerHM=PrinterHM.getInstance(cancelacionPrint.this.getApplicationContext());
        if(printerHM.hasPaper() != 1){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(cancelacionPrint.this);
            alertDialogBuilder
                    .setMessage("¡Sin papel en la impresora!")
                    .setCancelable(true)
                    .setNegativeButton("Continuar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            printerHM.print(appState, appState.printReceipt,print,save);
                        }
                    }).create().show();

        }else {

            printerHM.print(appState, appState.printReceipt,print,save);

        }

    }
}
