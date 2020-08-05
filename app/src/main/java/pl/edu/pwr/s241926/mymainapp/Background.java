package pl.edu.pwr.s241926.mymainapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class Background extends View
{
    Paint p;
    Refresh refresh;
    String napis="napis";

    public Background(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        refresh = new Refresh();
        refresh.start();
        p = new Paint();

    }
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawText(napis,100,100,p);
    }
    class Refresh extends Thread
    {
        @Override
        public void run()
        {
            while (true)
            {
                try {
                    Thread.sleep(500);
                    invalidate();
                }catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
