package com.wizarpos.emvsample.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.wizarpos.emvsample.R;

public class HeaderTicketView extends View {
    Path path;
    Paint paint, canvasPaint;
    public static TextView textView;
    LinearLayout linearLayout;
    public  static Bitmap canvasBitmap;
    public static Canvas drawCanvas;
    View view;
    Rect button = new Rect(); // Define the dimensions of the button here
    private static final String TAG = "HeaderTicketView";

    public HeaderTicketView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    @SuppressLint({"ResourceAsColor", "WrongThread"})
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#E4E9ED"));
        paint.setStrokeWidth(this.getPaddingBottom()*3f/2f);
        canvas.drawLine(this.getPaddingLeft()*3f/4f,this.getHeight(),this.getWidth()-(this.getPaddingRight()*3f/4f),this.getHeight(),paint);
        canvas.drawCircle(this.getPaddingLeft()*3f/4f,this.getHeight(),this.getPaddingBottom()*3f/2f,paint);
        canvas.drawCircle(this.getWidth()-(this.getPaddingRight()*3f/4f),this.getHeight(),this.getPaddingBottom()*3f/2f,paint);
        paint.setColor(Color.parseColor("#b0b0b0"));
        paint.setStrokeWidth(this.getPaddingBottom()*2f/3f);
        canvas.drawLine(this.getPaddingLeft()*3f/4f,this.getHeight()-(this.getPaddingBottom()*2f/3f),this.getWidth()-(this.getPaddingRight()*3f/4f),this.getHeight()-(this.getPaddingBottom()*2f/3f),paint);
        canvas.drawCircle(this.getPaddingLeft()*3f/4f,this.getHeight()-(this.getPaddingBottom()*2f/3f),this.getPaddingBottom()*1f/3f,paint);
        canvas.drawCircle(this.getWidth()-this.getPaddingLeft()*3f/4f,this.getHeight()-(this.getPaddingBottom()*2f/3f),this.getPaddingBottom()*1f/3f,paint);
        // canvas.drawPath(path, paint);
        paint.setStyle(Paint.Style.FILL);
        //paint.setFlags(paint.getFlags() & ~paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(2f);
        paint.setColor(Color.parseColor("#15000000"));
        drawSpikes(canvas,6f,2f, paint);
        paint.setColor(Color.parseColor("#10000000"));
        drawSpikes(canvas,8f,2f,  paint);
        paint.setColor(Color.parseColor("#0A000000"));
        drawSpikes(canvas,10f,2f,  paint);
        paint.setColor(Color.parseColor("#05000000"));
        drawSpikes(canvas,12f,2f,  paint);
        paint.setColor(Color.parseColor("#F8FBFF"));
        drawSpikes(canvas,0f,2f,  paint);
        paint.setStyle(Paint.Style.STROKE);

        paint.setColor(Color.parseColor("#E4E9ED"));
        drawSpikes(canvas,0f,2f,  paint);
        /*linearLayout = new LinearLayout(getContext());
       // textView = new TextView(getContext());
        //textView.setBackgroundColor(Color.parseColor("#f9fcff"));

        //linearLayout.addView(textView);
        //linearLayout.setBackgroundColor(Color.parseColor("#f9fcff"));
        linearLayout.measure(canvas.getWidth(), canvas.getHeight());
        linearLayout.layout(0, 0, canvas.getWidth(),canvas.getHeight());
        linearLayout.draw(canvas);*/
        //this.draw(canvas);
    }

    private void drawSpikes(Canvas canvas,float desX,float desY, Paint paint) {
        float sizeStroke=paint.getStrokeWidth();


        int spikes=22;
        int i=0;
        float margin =  this.getPaddingLeft();
        float int_spikes= margin;
        float space_spikes= (canvas.getWidth()-(margin*2))/spikes;

        boolean up=true;
        int stratY=0,endY=0;
        Path path = new Path();
       // path.addRect(int_spikes,canvas.getHeight(),canvas.getWidth()-int_spikes,canvas.getHeight(), Path.Direction.CW);
        path.moveTo(int_spikes+desX,canvas.getHeight()+desY-sizeStroke-(getPaddingBottom()*2f/3f));
        path.lineTo(int_spikes+desX, 0+desY+sizeStroke);
       // canvas.drawLine(int_spikes, canvas.getHeight(), int_spikes, 0, paint);


        for(i=0;i<spikes;i++) {
            if(up) {
                stratY=0;
                endY=this.getPaddingTop();
                up=false;
            }else {
                stratY=this.getPaddingTop();
                endY=0;
                up=true;
            }
            path.lineTo(int_spikes+space_spikes+desX, endY+desY+sizeStroke);
            //canvas.drawLine(int_spikes, stratY, int_spikes+space_spikes, endY, paint);
            int_spikes=int_spikes+space_spikes;
        }
        path.lineTo(int_spikes,canvas.getHeight()+desY-sizeStroke-(getPaddingBottom()*2f/3f));
        path.lineTo(margin+desX,canvas.getHeight()+desY-sizeStroke-(getPaddingBottom()*2f/3f));

       // canvas.drawLine(int_spikes, canvas.getHeight(), int_spikes, 0, paint);
        canvas.drawPath(path,paint);
    }


    public void setupDrawing(){
        path = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(5);
        canvasPaint = new Paint(Paint.DITHER_FLAG);;
    }


    @Override
    protected  void  onSizeChanged(int w, int h, int oldw, int ildh){
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }
}
