package com.wizarpos.emvsample.activity;

import android.app.Activity;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wizarpos.emvsample.MainApp;
import com.wizarpos.emvsample.R;
import com.wizarpos.util.AppUtil;
import com.wizarpos.util.StringUtil;

import java.io.ByteArrayOutputStream;


public class FirmaActivity extends AppCompatActivity {//extends FuncActivity {
    private static final String TAG = "FirmaActivity";
    public static DrawingView drawingView;
    Canvas canvas;
    public static String data;
    public static byte[] signature;
    Button btnContinuar,btnBorrar;
    TextView textMonto,textData;

    protected static MainApp appState = null;



    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         setContentView(R.layout.activity_firma);
        drawingView = (DrawingView)findViewById(R.id.draw_sing);

        textMonto =(TextView) this.findViewById(R.id.text_monto);
        textData =(TextView) this.findViewById(R.id.text_data);
        appState = ((MainApp)getApplicationContext());
        textMonto.setText("$" + AppUtil.formatAmount(appState.trans.getTransAmount()));

        String masked_pan="";
        if(appState.trans.getPAN()!=null){
            masked_pan=appState.trans.getPAN();
            if(masked_pan.length()>3) {
                masked_pan = masked_pan.substring(masked_pan.length() - 4);
            }
        }

        textData.setText("No. Control:"+appState.trans.getControlNumber()+" No. Aut:"+appState.trans.getCodeAut()+" Afilación:"+appState.terminalConfig.getMID()+" Tarjeta:****"+masked_pan+" Vigencia:"+appState.trans.getExpiry());

        btnContinuar =(Button) this.findViewById(R.id.btn_continuar);
        btnContinuar.setOnClickListener(v-> verificar());

        btnBorrar =(Button) this.findViewById(R.id.btn_borrar);
        btnBorrar.setOnClickListener(v-> drawingView.clearCanvas());

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        Log.i(TAG ,"onKeyDown:"+keyCode);
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
               // onBack();
                break;
            case KeyEvent.KEYCODE_ESCAPE:
                onCancel();
                break;
            case KeyEvent.KEYCODE_DEL:
              //  onDel();
                break;
            case KeyEvent.KEYCODE_ENTER:
                onEnter();
                break;
            case 232://'.'
               // onKeyCode('.');
                break;
            default:
                //onKeyCode((char) ('0'+(keyCode-KeyEvent.KEYCODE_0)));
                break;
        }
        return true;
    }


    protected void onEnter()
    {

        verificar();
    }


    protected void onCancel()
    {
        drawingView.clearCanvas();
        Toast.makeText(getApplicationContext(), "Borrado", Toast.LENGTH_LONG).show();
    }

    public static String getData(){
        data = "prueba";
        return  data;
    }

    public static byte[] getSignature(){
        drawingView.setDrawingCacheEnabled(true);
        drawingView.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(drawingView.getDrawingCache());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG,20,stream);
        byte[] byteArray = stream.toByteArray();
        //Log.e("Error", "tamaño: " + byteArray.length);

        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        Bitmap resized = Bitmap.createScaledBitmap(compressedBitmap,(int)(compressedBitmap.getWidth()*0.5), (int)(compressedBitmap.getHeight()*0.5), true);
        if(resized!=null){
            //Log.e("Si hay bitmap", "Se imprime la firma.");
            signature = Utils.decodeBitmap(resized);
        }else{
            //Log.e("Error", "Bitmap vacio");
        }
        return signature;
    }
    public static Bitmap getBitmapSignature(){

        drawingView.setDrawingCacheEnabled(true);
        drawingView.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(drawingView.getDrawingCache());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG,20,stream);
        byte[] byteArray = stream.toByteArray();

        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        Bitmap resized = Bitmap.createScaledBitmap(compressedBitmap,(int)(compressedBitmap.getWidth()*0.5), (int)(compressedBitmap.getHeight()*0.5), true);

        return resized;
    }
    public void verificar(){
        String data = "1";
        drawingView.setDrawingCacheEnabled(true);
        drawingView.buildDrawingCache(true);
        Bitmap c = Bitmap.createBitmap( drawingView.getDrawingCache());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        c.compress(Bitmap.CompressFormat.JPEG,80,stream);
        byte[] byteArray = stream.toByteArray();
        //Log.e("Error", "verificando bitmap button: " + stream.size());

        if(drawingView.isEmpty()){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FirmaActivity.this);
            alertDialogBuilder
                    .setMessage("¡La firma no puede estar vacía!")
                    .setCancelable(false)
                    .setNegativeButton("OK",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {

                        }
                    }).create().show();
        }else {
            setResult(Activity.RESULT_OK);
            finish();
        }
    }
}
