package at.ac.univie.bachelorarbeit.ss18.calltranscriber.recorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * Will start the recording service in the class RecordService whenever the user calls someone or gets called.
 */
public class CallBroadcastReceiver extends BroadcastReceiver {

    /**
     * Starts the recording service when the call is off-hook and stops it when the call is being hung up.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(final Context context, Intent intent) {

        PhoneStateListener phoneStateListener = new PhoneStateListener() {

            @Override
            public void onCallStateChanged (int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        context.stopService(new Intent(context, RecordService.class));
                        break;

                    case TelephonyManager.CALL_STATE_RINGING:
                        break;

                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        context.startService(new Intent(context, RecordService.class));
                        break;
                }

            }
        };

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

    }
}