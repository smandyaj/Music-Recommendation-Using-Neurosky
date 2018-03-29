package com.example.naren.brainsensor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.neurosky.thinkgear.*;


import android.app.Activity;
import android.bluetooth.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.neurosky.thinkgear.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static android.content.Context.MODE_PRIVATE;

public class MainActivity extends Activity {

    private static final String TAG = "HelloEEG";

    BluetoothAdapter            bluetoothAdapter;
    TGDevice                    device;

    final boolean               rawEnabled = true;

    ScrollView                  sv;
    TextView                    tv;
    Button                      b;

    private SQLiteDatabase db = null;
    private static String tableName = "brainSensor";
    private static String DB_PATH = "Group26.db";
    private SQLiteDatabase.CursorFactory mFactory;
    private int data1,data2,data3,data4;
    private MyDatabaseHelper mdh;


    public void MainActivity(Context context){

        mdh = new MyDatabaseHelper(context);
    }
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main );
        //db = SQLiteDatabase.openOrCreateDatabase(DB_PATH,Context.MODE_PRIVATE,null);
        sv = (ScrollView)findViewById( R.id.scrollView1 );
        tv = (TextView)findViewById( R.id.textView1 );
        tv.setText( "" );
        tv.append( "Android version: " + Integer.valueOf(android.os.Build.VERSION.SDK) + "\n" );
        mdh = new MyDatabaseHelper(this.getApplicationContext());



        // Check if Bluetooth is available on the Android device
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if( bluetoothAdapter == null ) {

            // Alert user that Bluetooth is not available
            Toast.makeText( this, "Bluetooth not available", Toast.LENGTH_LONG ).show();
            //finish();
            return;

        } else {

            // create the TGDevice
            device = new TGDevice(bluetoothAdapter, handler);
        }

        tv.append("NeuroSky: " + TGDevice.version + " " + TGDevice.build_title);
        tv.append("\n" );

    }
	/* end onCreate() */

    //turn off app when touch return button of phone
    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event)
    {
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0)
        {
            device.close();
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        //if (!bluetoothAdapter.isEnabled()) {
        //  Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        //startActivityForResult(enableIntent, 1);
        //}
    }

    @Override
    public void onPause() {
        // device.close();
        super.onPause();
    }

    @Override
    public void onStop() {
        device.close();
        super.onStop();

    }

    @Override
    public void onDestroy() {
        //device.close();
        super.onDestroy();
    }

    /**
     * Handles messages from TGDevice
     */
    final Handler handler = new Handler() {
        @Override
        public void handleMessage( Message msg ) {
            //tv.append("MESSAGE:" +msg.what);
            switch( msg.what ) {
                case TGDevice.MSG_STATE_CHANGE:

                    switch( msg.arg1 ) {
                        case TGDevice.STATE_IDLE:
                            break;
                        case TGDevice.STATE_CONNECTING:
                            tv.append( "Connecting...\n" );
                            break;
                        case TGDevice.STATE_CONNECTED:
                            tv.append( "Connected.\n" );
                            device.start();
                            break;
                        case TGDevice.STATE_NOT_FOUND:
                            tv.append( "Could not connect any of the paired BT devices.  Turn them on and try again.\n" );
                            break;
                        case TGDevice.STATE_ERR_NO_DEVICE:
                            tv.append( "No Bluetooth devices paired.  Pair your device and try again.\n" );
                            break;
                        case TGDevice.STATE_ERR_BT_OFF:
                            tv.append( "Bluetooth is off.  Turn on Bluetooth and try again." );
                            break;

                        case TGDevice.STATE_DISCONNECTED:
                            tv.append( "Disconnected.\n" );
                    } /* end switch on msg.arg1 */

                    break;

                case TGDevice.MSG_POOR_SIGNAL:
                    tv.append( "PoorSignal: " + msg.arg1 + "\n" );
                    data1 = msg.arg1;
                    break;

                case TGDevice.MSG_HEART_RATE:
                    tv.append( "Heart rate: " + msg.arg1 + "\n" );
                    break;

                case TGDevice.MSG_ATTENTION:
                    tv.append( "Attention: " + msg.arg1 + "\n" );
                    data4=msg.arg1;
                    break;

                case TGDevice.MSG_MEDITATION:
                    tv.append( "Meditation: " + msg.arg1 + "\n" );
                    data2=msg.arg2;
                    break;

                case TGDevice.MSG_BLINK:
                    tv.append( "Blink: " + msg.arg1 + "\n" );
                    break;

                case TGDevice.MSG_DIFFICULTY:
                    tv.append( "Difficulty: " + msg.arg1 + "\n" );
                    break;

                case TGDevice.MSG_RAW_MULTI:
                    tv.append( "Positivity: " + msg.arg1 + "\n" );
                    data3= msg.arg1;
                    break;

                case TGDevice.MSG_POSITIVITY:
                    tv.append( "Positivity: " + msg.arg1 + "\n" );
                    data3= msg.arg1;
                    break;

                case TGDevice.MSG_THINKCAP_RAW:
                    tv.append( "THINKCAP RAW: " + msg.arg1 + "\n" );
                    break;


                case TGDevice.MSG_FAMILIARITY:
                    tv.append( "FAMILIARITY: " + msg.arg1 + "\n" );
                    break;

                case TGDevice.MSG_RELAXATION:
                    tv.append( "RELAXATION: " + msg.arg1 + "\n" );
                    break;

                case TGDevice.MSG_RESPIRATION:
                    tv.append( "RESPIRATION: " + msg.arg1 + "\n" );
                    break;

                case TGDevice.MSG_SLEEP_STAGE:
                    tv.append( "SLEEP STAGE: " + msg.arg1 + "\n" );
                    break;

                default:
                    break;



            } /* end switch on msg.what */







            try{
               File directory = new File(Environment.getDataDirectory()
                        + "/Brainsensor/");
                if (!directory.exists()) {
                    directory.mkdir();
                }

                //db =SQLiteDatabase.openOrCreateDatabase(directory.toString() + File.separator +DB_PATH,mFactory,null);
                String temp = directory+ File.separator+DB_PATH;
                Toast.makeText(getApplicationContext(), temp,Toast.LENGTH_SHORT).show();


                db = mdh.getReadableDatabase();
                db.execSQL("INSERT INTO "+
                tableName + "(data1,data2,data3,data4)"
                + " values("+ data1 +","+ data2
                +","+ data3 + "," + data4 + " ); "
                );

            }catch (Exception e){
                System.out.println(e.toString());
                Toast.makeText(getApplicationContext(), "CATCH" + e.toString(),Toast.LENGTH_SHORT).show();

            }finally {
                Toast.makeText(getApplicationContext(), "FINALLY",Toast.LENGTH_SHORT).show();

                if (db != null) {
                    db.close();
                }
            }
            sv.fullScroll( View.FOCUS_DOWN );


            Toast.makeText(getApplicationContext(),"END",Toast.LENGTH_SHORT).show();

        } /* end handleMessage() */

    }; /* end Handler */

    /**
     * This method is called when the user clicks on the "Connect" button.
     *
     * @param view
     */
    public void doStuff(View view) {
        if( device.getState() != TGDevice.STATE_CONNECTING && device.getState() != TGDevice.STATE_CONNECTED ) {

            device.connect( rawEnabled );
        }

    } /* end doStuff() */

} /* end HelloEEGActivity() */
