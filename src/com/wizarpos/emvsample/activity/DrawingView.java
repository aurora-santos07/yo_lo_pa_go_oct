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
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.wizarpos.emvsample.R;

public class DrawingView extends View {
    Path path,path2;
    Paint paint, canvasPaint;
    public static TextView textView;
    LinearLayout linearLayout;
    public  static Bitmap canvasBitmap;
    public static Canvas drawCanvas;
    View view;
    Rect button = new Rect(); // Define the dimensions of the button here
    boolean empty =true;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    @SuppressLint({"ResourceAsColor", "WrongThread"})
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);

        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, paint);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(ContextCompat.getColor(this.getContext().getApplicationContext(), R.color.blueYLP));
        canvas.drawPath(path2, paint);
        paint.setStrokeWidth(1);
        paint.setColor(Color.BLACK);
        canvas.drawLine(30f, canvas.getHeight()-30f, canvas.getWidth()-30f, canvas.getHeight()-30f, paint);
        paint.setStrokeWidth(4f);
        paint.setColor(ContextCompat.getColor(this.getContext().getApplicationContext(), R.color.blueYLP));
        linearLayout = new LinearLayout(getContext());
        textView = new TextView(getContext());
        textView.setVisibility(view.VISIBLE);
        //textView.setText("    FAVOR DE AGREGAR SU FIRMA     ");
        textView.setBackgroundColor(Color.BLUE);
        textView.setTextSize(20);
        textView.setTextColor(Color.WHITE);
        linearLayout.addView(textView);
        linearLayout.measure(canvas.getWidth(), canvas.getHeight());
        linearLayout.layout(0, 0, canvas.getWidth(),canvas.getHeight());
        linearLayout.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        textView.setVisibility(INVISIBLE);
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                path.moveTo(touchX, touchY);
                path2.moveTo(touchX, touchY);
                path2.addCircle(touchX, touchY,1f, Path.Direction.CW);
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(touchX,touchY);
                break;
            case  MotionEvent.ACTION_UP:
                empty=false;
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void setupDrawing(){
        path = new Path();
        path2 = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(5);
        canvasPaint = new Paint(Paint.DITHER_FLAG);;
    }

    public void clearCanvas(){
        empty=true;
        path.reset();
        path2.reset();
        invalidate();
    }

    public void change(){
        textView.setBackgroundColor(Color.WHITE);
    }

    public boolean isEmpty(){
       return empty;
    }

    @Override
    protected  void  onSizeChanged(int w, int h, int oldw, int ildh){
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        canvasBitmap.eraseColor(Color.WHITE);
    }
}
