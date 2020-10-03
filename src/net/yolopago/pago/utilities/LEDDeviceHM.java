package net.yolopago.pago.utilities;

import android.content.Context;
import android.util.Log;

import com.cloudpos.Device;
import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.jniinterface.LEDInterface;
import com.cloudpos.led.LEDDevice;
import com.cloudpos.led.LEDDeviceSpec;
import com.cloudpos.sdk.impl.AbstractDevice;
import com.cloudpos.sdk.led.impl.LEDDeviceImpl;
import com.cloudpos.sdk.led.impl.LEDDeviceSpecImpl;


public class LEDDeviceHM {

    private static final String TAG = "LEDDeviceHM";
    private static Context androidContext;
    private static LEDDevice led;
    private static LEDDeviceHM self;
    private static LEDInterface lepInterface;

    public static LEDDeviceHM get(){ if(self==null){self=new LEDDeviceHM();}return self;}

    public void setContext(Context context){
        androidContext=context;
        led = (LEDDevice) POSTerminal.getInstance(androidContext).getDevice("cloudpos.device.led");

    }

    private LEDDeviceHM(){
       // lepInterface=new LEDInterface();
       // lepInterface.open();
    }

    public static LEDDeviceHM clear() {
        self.offLED1();
        self.offLED2();
        self.offLED3();
        self.offLED4();
        return self;
    }



    public void blinkLED1() { blinkLed(1); }
    public void blinkLED2() { blinkLed(2); }
    public void blinkLED3() { blinkLed(3); }
    public void blinkLED4() { blinkLed(4); }

    public  void onLED1() { onOffLed(1,true); }
    public void onLED2() { onOffLed(2,true); }
    public void onLED3() { onOffLed(3,true); }
    public void onLED4() { onOffLed(4,true); }

    public void offLED1() { onOffLed(1,false); }
    public void offLED2() { onOffLed(2,false); }
    public void offLED3() { onOffLed(3,false); }
    public void offLED4() { onOffLed(4,false); }


    void  onOffLed(int i,boolean onOff){

      //  if(onOff){lepInterface.turnOn(i-1);}else{lepInterface.turnOff(i-1);}

    /*    try {
            try {led.close();}catch (DeviceException ex) {}
            switch (i){
                case 1:led.open(0);break;
                case 2:led.open(1);break;
                case 3:led.open(2);break;
                case 4:led.open(3);break;
            }
            if(onOff){led.turnOn();}else{led.turnOff();led.close();}

        } catch (DeviceException e) {
            e.printStackTrace();
            try {led.close();}catch (DeviceException ex) {}
        }*/

    }
    public void blinkLed(int i){

        //onOffLed(i,true);
       /* try {
            switch (i){
                case 1:led.open(0);break;
                case 2:led.open(1);;break;
                case 3:led.open(2);;break;
                case 4:led.open(3);;break;
            }
            led.blink((byte)0x01,100,100,5);
            led.close();
        } catch (DeviceException e) {
            e.printStackTrace();
            try {led.close();}catch (DeviceException ex) {}
        }*/
    }



}
