package net.yolopago.pago.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.jniinterface.PrinterInterface;
import com.cloudpos.printer.PrinterDevice;
import com.wizarpos.emvsample.MainApp;
import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.activity.FirmaActivity;
import com.wizarpos.emvsample.activity.ProcessOnlineActivity;
import com.wizarpos.emvsample.activity.Product;
import com.wizarpos.emvsample.constant.Constant;
import com.wizarpos.emvsample.constant.ConstantYLP;
import com.wizarpos.util.AppUtil;

import net.yolopago.pago.viewmodel.PaymentViewModel;
import net.yolopago.pago.viewmodel.TicketViewModel;
import net.yolopago.pago.ws.dto.ticket.PdfDto;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class PrinterHM {
    private static final String TAG = "PrinterHM";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("$ ###,###,##0.00",new DecimalFormatSymbols(Locale.US));
    final byte SWIPE_ENTRY       = 2;
    final byte INSERT_ENTRY      = (byte)0x80;
    final byte CONTACTLESS_ENTRY = 6;
   private Paint paint;
   private Canvas canvas;
   private Resources resources;
   private float posX=0;
   private float posY=0;
   private int left=0;
   private int rigth=0;
    private int width=0;
   private int height=0;
   private int blockSize=0;
    private int blockBitmap=254;
   private Bitmap bitmap;
   private Activity app;
   PdfDto pdf= new PdfDto();
    private static PrinterHM _instance;
    public static Handler handler = new Handler();
    private Context ctx;
    private PrinterDevice printerDevice;



    public PrinterHM(Resources r){
        resources=r;

    }
    public void init(){
        posX=0;
        posY=0;
        width=371;
        height=2412;
        blockSize=width;
        left=0;
        rigth=0;
        paint= new Paint();
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        bitmap.eraseColor(Color.WHITE);
        canvas = new Canvas(bitmap);

    }

    synchronized public static PrinterHM getInstance(Context ctx)
    {
        if (null == _instance){
            _instance = new PrinterHM(ctx.getResources());
            _instance.printerDevice = (PrinterDevice) POSTerminal.getInstance(ctx).getDevice("cloudpos.device.printer");
        }
        _instance.init();

        return _instance;
    }

    public void  print( MainApp appState,int typePrint,boolean print,boolean save){

        try {
            init();
            byte[] signatures=null;
            Drawable image = resources.getDrawable(R.drawable.ylp_logo_yolo);
            image.setBounds(142,0,242,53);//385-100=285/2=142.5

            paint.setStyle(Paint.Style.STROKE);
            paint.setFlags(paint.getFlags() & ~paint.ANTI_ALIAS_FLAG);
            image.draw(canvas);

            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(1);
            paint.setTextSize(15);
            paint.setFakeBoldText(true);

            posY=70;

            if(appState.typePrintRecive== ConstantYLP.RECIBO_CANCELADO) {
                drawText("CANCELACION",'C',20);addLine();
            }else {
                drawText("VENTA",'C',20);addLine();
            }

            drawText(appState.trans.getMerchantName(),'C',20);addLine();
            drawText(appState.trans.getDir(),'C');addLine();
            drawText("TEL:",'C');addLine();
            drawText("AFILIACION:"+appState.terminalConfig.getMID(),'L');addLine();
            drawText("TERMINAL:"+appState.trans.getTerminalId(),'L');addLine();
            drawText("CONTROL:"+appState.trans.getControlNumber(),'L');addLine();
            String masked_pan="";
            if(appState.trans.getPAN()!=null){
                masked_pan=appState.trans.getPAN();
                if(masked_pan.length()>3) {
                    masked_pan = masked_pan.substring(masked_pan.length() - 4);
                }
            }
            drawText("**** **** ****"+masked_pan,'L');addLine();
            drawText( appState.trans.getCardBrandName()+"/"+ appState.trans.getBankName()+"/"+(appState.trans.getCardTypeName()==null?"":appState.trans.getCardTypeName()) ,'L');addLine();addLine();
            switch(appState.typePrintRecive){
                case ConstantYLP.RECIBO_DECLINADO:drawText("DECLINADA",'L',20);addLine();break;
                case ConstantYLP.RECIBO_RECHAZADO:drawText("RECHAZADA",'L',20);addLine();break;
                case ConstantYLP.RECIBO_TIMEOUT:drawText("TIEMPO EXCEDIDO",'L',20);addLine();break;
                case ConstantYLP.RECIBO_REVERSADO:drawText("REVERSADA",'L',20);addLine();break;
                case ConstantYLP.RECIBO_ACEPTADO:drawText("APROBADA",'L',20);addLine();break;
                case ConstantYLP.RECIBO_CONEXION:drawText("ERROR CONEXION",'L',20);addLine();break;
                case ConstantYLP.RECIBO_CANCELADO:drawText("APROBADA",'L',20);addLine();break;

            }

            drawText("AUT:"+appState.trans.getCodeAut(),'L');addLine();
            drawText("REF:"+appState.trans.getReference(),'L');addLine();
            if(appState.typePrintRecive== ConstantYLP.RECIBO_CANCELADO) {
                drawText("REF ORIGINAL:"+appState.trans.getOriginalReference(),'L');addLine();
            }

            if(appState.trans.getCardEntryMode() != Constant.SWIPE_ENTRY){
                drawText("AID:"+appState.trans.getAID(),'L');addLine();
                drawText("TVR:"+appState.trans.getTVR(),'L');addLine();
                drawText("TSI:"+appState.trans.getTSI(),'L');addLine();addLine();
            }

            drawText("IMPORTE M.N",'L',20);

            drawText( DECIMAL_FORMAT.format((appState.trans.getTransAmount()/100d)),'R',20);addLine();
            if(appState.typePrintRecive == ConstantYLP.RECIBO_ACEPTADO) {
                if (appState.trans.getNeedSignature() == 1) {
                    Bitmap sig = FirmaActivity.getBitmapSignature();
                    drawText("FIRMA", 'C');
                    addLine();
                    canvas.drawBitmap(sig, 0, posY, paint);
                    posY += sig.getHeight();
                    drawText("--------------------", 'C');
                    addLine();
                    drawText(appState.trans.getCountName(), 'C');
                    addLine();
                } else {
                    if (appState.trans.getCardEntryMode() == CONTACTLESS_ENTRY) {
                        drawText("AUTORIZADO SIN FIRMA", 'C');
                        addLine();
                    } else if (appState.trans.getCardEntryMode() == INSERT_ENTRY) {
                        drawText("AUTORIZADO CON  FIRMA ELECTRONICA", 'C');
                        addLine();
                    }
                    drawText("--------------------", 'C');
                    addLine();
                    drawText(appState.trans.getCountName(), 'C');
                    addLine();
                }
            }

            drawText(( ""
                    + " " + appState.trans.getTransDate().substring(0, 4)
                    + "/" + appState.trans.getTransDate().substring(4, 6)
                    + "/" + appState.trans.getTransDate().substring(6, 8)),'C');addLine();
            drawText(( ""
                    + " " + appState.trans.getTransTime().substring(0, 2)
                    + ":" + appState.trans.getTransTime().substring(2, 4)
                    + ":" + appState.trans.getTransTime().substring(4, 6) ),'C');addLine();addLine();

            if(appState.typePrintRecive!= ConstantYLP.RECIBO_CANCELADO) {
                drawText("RECUERDA QUE EN TU ESTADO DE CUENTA  EL CARGO APARECERA A NOMBRE DE YOLOPAGO",'C');addLine();addLine();addLine();
            }
            canvas.save();

            if (save){

                Bitmap printbpmSave =Bitmap.createBitmap(bitmap,0,0,width,(int)Math.ceil(posY));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                printbpmSave.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                printbpmSave.recycle();

                PaymentViewModel model=new PaymentViewModel(appState);
                model.addFilePayment(appState.trans.getPaymentId(),Base64.encodeToString(byteArray,Base64.DEFAULT));
            }

            if(print) {

                //TPD PrinterInterface.open();
                //TPD PrinterInterface.begin();
                //TPD printerWrite(PrinterCommand.init());


                boolean keepPrinting=true;
                int initPoint=0;
                int indexArray=0;
                posY=(float)Math.ceil(posY);
                int size=(int)Math.ceil(posY/blockBitmap);
                byte[][] toPrint=new byte[size][0];
                int faltante=blockBitmap>posY?(int)posY:blockBitmap;

                printBitmap(Bitmap.createBitmap(bitmap, 0, 0, width, (int)Math.ceil(posY)));

                /*while(keepPrinting) {
                    Log.e("Si hay bitmap", "int:"+initPoint+" falt:"+faltante);
                    Bitmap printbpm =Bitmap.createBitmap(bitmap,0,initPoint,width,faltante);
                    toPrint[indexArray++] = Utils.decodeBitmap(printbpm);
                    posY-=blockBitmap;
                    initPoint+=blockBitmap;
                    if(posY<=0){
                        keepPrinting=false;
                    }else if(posY<blockBitmap) {
                     faltante=(int)(posY);
                    }else{
                        faltante=blockBitmap;
                    }
                }
                for(int i=0;i<indexArray;i++) {
                    printerWrite(toPrint[i]);

                }
                printerWrite(PrinterCommand.feedLine(1));*/
            }




        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //PrinterInterface.end();
            //PrinterInterface.close();
        }
    }

    public void printReceipt( MainApp appState, ArrayList<Product> items,boolean print,boolean save){

        try {
            init();
            byte[] signatures=null;
            Drawable image = resources.getDrawable(R.drawable.ylp_logo_yolo);
            image.setBounds(20,0,120,53);//385-100=285/2=142.5
            paint.setStyle(Paint.Style.STROKE);
            paint.setFlags(paint.getFlags() & ~paint.ANTI_ALIAS_FLAG);
            image.draw(canvas);

            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(1);
            paint.setTextSize(15);
            paint.setFakeBoldText(true);

            posY=50;

            blockSize =270;

            /*float line=383;
            int n=5;
            canvas.drawLine(0,0f,line,0,paint);
            canvas.drawLine(line,0f,line,line-20,paint);
            canvas.drawLine(1,0,1,line-20,paint);
            canvas.drawLine(0,line-20,line,line-20,paint);

            line=371;
            canvas.drawLine(line,0f,line,line-300,paint);*/
            drawText(appState.trans.getMerchantName(),'R',20);addLine();
            drawText(appState.trans.getDir(),'R');addLine();
            drawText("TEL:",'R');addLine();
            drawText("RFC:"+appState.trans.getRfc(),'R');addLine();
            drawText("Operador:"+appState.trans.getOperador(),'R');addLine();
            blockSize =width;
            drawText("--------------------------------------------",'C');addLine();
            int cantidadNum=0;
            ArrayList<Product> list = items==null?ProcessOnlineActivity.list():items;
            float tempProducPosY=0;
            float maxProducPosY=0;
            float maxCantidadSize=0;
            float total=0;
            for (int i = 0; i < list.size(); i++) {
                Double precio = list.get(i).getTotal();
                maxCantidadSize=Math.max(maxCantidadSize,paint.measureText(DECIMAL_FORMAT.format(precio)));
            }
            for (int i = 0; i < list.size(); i++) {
                String producto = list.get(i).getProducto();
                String cantidad = list.get(i).getCantidad();
                cantidadNum += Integer.parseInt(cantidad);
                Double precio = list.get(i).getTotal();
                drawText(cantidad,'L');
                left=65;
                blockSize=(int)(width-maxCantidadSize-left);
                tempProducPosY=posY;
                drawText(producto,'L');
                addLine();
                maxProducPosY=posY;
                posY=tempProducPosY;
                blockSize=(int)maxCantidadSize;
                drawText(DECIMAL_FORMAT.format(precio),'R');
                posY=maxProducPosY;
                blockSize=width;
                left=0;
            }
            drawText("--------------------------------------------",'C');addLine();

            rigth=(int)paint.measureText(AppUtil.formatAmount(appState.trans.getTransAmount()))+10;
            float temp=posY;
            drawText("IVA 16%:",'R');addLine();
            drawText("TOTAL:",'R');addLine();
            drawText("EFECTIVO:",'R');addLine();
            drawText("CAMBIO:",'R');addLine();
            drawText("TOTAL ARTICULOS:",'R');addLine();
            rigth=0;
            posY=temp;
            drawText("$0.00",'R');addLine();
            drawText(DECIMAL_FORMAT.format((appState.trans.getTransAmount()/100d)),'R');addLine();
            drawText("$0.00",'R');addLine();
            drawText("$0.00",'R');addLine();
            drawText(""+cantidadNum,'R');addLine();

            drawText( ""
                    + appState.trans.getTransDate().substring(0, 4)
                    + "/" + appState.trans.getTransDate().substring(4, 6)
                    + "/" + appState.trans.getTransDate().substring(6, 8)
                    +" "
                    + appState.trans.getTransTime().substring(0, 2)
                    + ":" + appState.trans.getTransTime().substring(2, 4)
                    + ":" + appState.trans.getTransTime().substring(4, 6) ,'C');addLine();addLine();

            drawText("ID TICKET:"+appState.trans.getTicketId(),'C');addLine();
            drawText("¡GRACIAS POR SU COMPRA!",'C',paint.getTextSize(),true);addLine();addLine();addLine();

            canvas.save();
            if (save){

                this.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        Bitmap printbpmSave =Bitmap.createBitmap(bitmap,0,0,width,(int)Math.ceil(posY));
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        printbpmSave.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        printbpmSave.recycle();

                        TicketViewModel model=new TicketViewModel(appState);
                        model.addFileTicket(appState.trans.getTicketId(),Base64.encodeToString(byteArray,Base64.DEFAULT));
                    }
                });

            }

            if(print) {

               //TPD PrinterInterface.open();
                //TPD PrinterInterface.begin();
                //TPD  printerWrite(PrinterCommand.init());
               // printerWrite(PrinterCommand.setHeatTime(100));

                boolean keepPrinting = true;
                int initPoint = 0;
                int indexArray = 0;
                posY = (float) Math.ceil(posY);
                int size = (int) Math.ceil(posY / blockBitmap);
                byte[][] toPrint = new byte[size][0];
                int faltante = blockBitmap;
                printBitmap(Bitmap.createBitmap(bitmap, 0, 0, width,(int)Math.ceil(posY)));
              /*  while (keepPrinting) {
                    Log.e("Si hay bitmap", "int:" + initPoint + " falt:" + faltante);
                    Bitmap printbpm = Bitmap.createBitmap(bitmap, 0, initPoint, width, faltante);
                    toPrint[indexArray++] = Utils.decodeBitmap(printbpm);
                    posY -= blockBitmap;
                    initPoint += blockBitmap;
                    if (posY <= 0) {
                        keepPrinting = false;
                    } else if (posY < blockBitmap) {
                        faltante = (int) (posY);
                    } else {
                        faltante = blockBitmap;
                    }
                }
                for (int i = 0; i < indexArray; i++) {
                    printerWrite(toPrint[i]);
                }
                printerWrite(PrinterCommand.feedLine(1));*/
            }
            if (save){
                this.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        Bitmap printbpmSave =Bitmap.createBitmap(bitmap,0,0,width,(int)Math.ceil(posY));
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        printbpmSave.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        printbpmSave.recycle();

                        TicketViewModel model=new TicketViewModel(appState);
                        model.addFileTicket(appState.trans.getTicketId(),Base64.encodeToString(byteArray,Base64.DEFAULT));
                    }
                });

            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //PrinterInterface.end();
            //PrinterInterface.close();
        }
    }


    public void printFromData(String data){
        init();
        byte[] dataArray= Base64.decode(data,Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(dataArray, 0, dataArray.length);
        //PrinterInterface.open();
        //PrinterInterface.begin();
        //printerWrite(PrinterCommand.init());
       // printerWrite(PrinterCommand.setHeatTime(300));

        posY=bitmap.getHeight();
        boolean keepPrinting = true;
        int initPoint = 0;
        int indexArray = 0;
        posY = (float) Math.ceil(posY);
        int size = (int) Math.ceil(posY / blockBitmap);
        byte[][] toPrint = new byte[size][0];
        int faltante = blockBitmap;

        printBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight()));
        /*while (keepPrinting) {
            Log.e("Si hay bitmap", "int:" + initPoint + " falt:" + faltante);
            Bitmap printbpm = Bitmap.createBitmap(bitmap, 0, initPoint, width, faltante);
            toPrint[indexArray++] = Utils.decodeBitmap(printbpm);
            posY -= blockBitmap;
            initPoint += blockBitmap;
            if (posY <= 0) {
                keepPrinting = false;
            } else if (posY < blockBitmap) {
                faltante = (int) (posY);
            } else {
                faltante = blockBitmap;
            }
        }
        for (int i = 0; i < indexArray; i++) {
            printerWrite(toPrint[i]);
        }
        printerWrite(PrinterCommand.feedLine(1));*/
    }

    public void drawText(String text,char aling,float sizeText,boolean bold){
        boolean currentBold=paint.isFakeBoldText();
        paint.setFakeBoldText(bold);
        drawText(text,aling,sizeText);
        paint.setFakeBoldText(currentBold);
    }
    public void drawText(String text,char aling,float sizeText){
        float currentSize=paint.getTextSize();
        paint.setTextSize(sizeText);
        drawText(text,aling);
        paint.setTextSize(currentSize);
    }
    public void drawText(String text,char aling){
        switch(aling) {
            case 'L':
                float m2=paint.measureText(text);
                if(m2>blockSize) {
                    ArrayList<String> textA=getLinesText(text);
                    for(int i=0;i<textA.size();i++){
                        canvas.drawText(textA.get(i),left,posY,paint);
                        if((i+1)<textA.size()) {
                            addLine();
                        }
                    }
                }else{
                    canvas.drawText(text,left,posY,paint);
                }
                break;
            case 'C':
                float m=paint.measureText(text);
                if(m>blockSize) {
                   ArrayList<String> textA=getLinesText(text);
                   for(int i=0;i<textA.size();i++){
                       float m3=paint.measureText(textA.get(i));
                       canvas.drawText(textA.get(i),((blockSize-m3)/2)+(width-blockSize)+left,posY,paint);
                       if((i+1)<textA.size()) {
                           addLine();
                       }
                   }
                }else{
                    canvas.drawText(text,((blockSize-m)/2)+(width-blockSize)+left,posY,paint);
                }
                break;
            case 'R':
                float m4=paint.measureText(text);
                if(m4>blockSize) {
                    ArrayList<String> textA=getLinesText(text);
                    for(int i=0;i<textA.size();i++){
                        float m44=paint.measureText(textA.get(i));

                        canvas.drawText(textA.get(i),blockSize-m44+(width-blockSize)-rigth,posY,paint);
                        if((i+1)<textA.size()) {
                            addLine();
                        }
                    }
                }else{

                    canvas.drawText(text,blockSize-m4+(width-blockSize)-rigth,posY,paint);
                }
                break;

        }
        posX=posX+paint.measureText(text);
    }

    private ArrayList<String> getLinesText(String text) {
        ArrayList<String> ret_temp = new ArrayList<String>();
        ArrayList<String> ret = new ArrayList<String>();
        ArrayList<String> lines=getLinesText(text,"\n");
        for (int i = 0; i < lines.size(); i++) {

            if (paint.measureText(lines.get(i)) > blockSize) {
                ret_temp.addAll(getLinesText(lines.get(i)," "));
            } else {
                ret_temp.add(lines.get(i));
            }
        }
        for (int i = 0; i < ret_temp.size(); i++) {

            if (paint.measureText(ret_temp.get(i)) > blockSize) {
                ret.addAll(getLinesText(ret_temp.get(i),""));
            } else {
                ret.add(ret_temp.get(i));
            }
        }

        return ret;
    }
    private ArrayList<String> getLinesText(String text,String split) {

        ArrayList<String> ret = new ArrayList<String>();
        String[] textA = text.split(split);
        String sumText = "";
        for (int i = 0; i < textA.length; i++) {

            if (paint.measureText(sumText + " " + textA[i]) > blockSize) {
                if(!sumText.equals("")) {
                    ret.add(sumText.trim());
                }
                sumText = textA[i];
            } else {
                sumText = sumText + " " + textA[i];
            }
        }
        if (!sumText.isEmpty()){
            ret.add(sumText.trim());
        }
        return ret;
    }

    public void drawText(String text){
        canvas.drawText(text, posX, posY, paint);
        posX=posX+paint.measureText(text);
    }
    public void addLine(){
        posY+=paint.getTextSize()+1;
        posX=0;
    }


    public void printerWrite(byte[] data)
    {
        int s= PrinterInterface.queryStatus();
        Log.d(TAG, "printerWrite: "+s);
        PrinterInterface.write(data, data.length);

    }
    public void printBitmap(Bitmap bitmap)
    {

        try {
            printerDevice.open();

        } catch (DeviceException e) {
            e.printStackTrace();
        }
        try {
            printerDevice.printBitmap(bitmap);
        } catch (DeviceException e) {
            e.printStackTrace();
        }
        try {
            printerDevice.close();
        } catch (DeviceException e) {
            e.printStackTrace();
        }

    }
    public int hasPaper()
    {
            int ret=0;
        try {
            printerDevice.open();

        } catch (DeviceException e) {
            e.printStackTrace();
        }
        try {
            ret=printerDevice.queryStatus();
        } catch (DeviceException e) {
            e.printStackTrace();

        }
        try {
            printerDevice.close();
        } catch (DeviceException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
