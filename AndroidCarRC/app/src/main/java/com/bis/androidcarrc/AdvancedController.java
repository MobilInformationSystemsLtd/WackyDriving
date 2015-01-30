package com.bis.androidcarrc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;

import java.io.IOException;

/**
 * Created by derrickmilford on 29/01/15.
 */
public class AdvancedController extends Activity implements View.OnTouchListener{

    private final static String TAG = AdvancedController.class.getSimpleName();

    private View touchView;
    private static int _motorMax = 255;

    private float originX = 0;
    private float originY = 0;

    private boolean isTouching = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);
        touchView = findViewById(R.id.touchView);
        touchView.setOnTouchListener(this);
    }

    public int convertPxToDp(float pixel){
        float scale = getResources().getDisplayMetrics().density;
        int returnValue = (int) (pixel / scale + 0.5f);
        return returnValue;
    }

    public void moveImageToPoint(){

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            originX = x;
            originY = y;
            isTouching = true;

        }else if (event.getAction() == MotionEvent.ACTION_MOVE){
            if(isTouching){

                // Get the difference of movement from Origin
                float diffY = originY-y;
                float diffX = x - originX;

                if(diffX > 30){ // Right -> scale by X2
                    sendMsg("TWR "+calculateMotorValue(convertPxToDp(diffX)*2));
                }else if(diffX < -30){ // Left -> scale by X2
                    sendMsg("TWL "+calculateMotorValue(convertPxToDp(diffX)*2));
                }else {
                    sendMsg("TWR 0");
                }

                if(diffY > 30){ // Forward
                    sendMsg("MVF "+calculateMotorValue(convertPxToDp(diffY)));
                }else if(diffY < -30){ // Backwards
                    sendMsg("MVB "+calculateMotorValue(convertPxToDp(diffY)));
                }else {
                    sendMsg("MVF 0");
                }


                // Send cmd to Device
            }
        }else if(event.getAction() == MotionEvent.ACTION_UP){
            originX = 0;
            originY = 0;
            isTouching = false;
            sendMsg("TWR 0");
            sendMsg("MVF 0");
        }
        return true;
    }

    private int calculateMotorValue(int progress) {

        // Get the Positive Int of progress
        if(progress < 0)
            progress = progress * -1;

        if (progress > 30){


            if (progress > 285)
                return _motorMax;
            else {
                return (int) (progress -30);
            }
        }else
            return 0;
    }

    public void sendMsg(String msg){
        Log.d(TAG, "Sending " + msg);
        msg = msg + "\n";
        byte[] msgBuffer = msg.getBytes();
        try{
            BTConSingleton.getInstance().outStream.write(msgBuffer);
        } catch (IOException e) {
            Log.d(TAG, "Unable to get in/out stream");
            e.printStackTrace();
        }
    }
}