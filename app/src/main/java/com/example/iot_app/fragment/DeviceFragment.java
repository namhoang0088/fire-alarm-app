package com.example.iot_app.fragment;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.cardview.widget.CardView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;

import com.example.iot_app.R;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class DeviceFragment extends Fragment {
    @Nullable
    private HttpHelper httpHelper;
    private boolean isPowerOn = true;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_management, container, false);
        LinearLayout lightControl = view.findViewById(R.id.light_control);
        CardView buttonLight = view.findViewById(R.id.button_light);

        //startMQTT();
        lightControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý sự kiện khi LinearLayout được click
                openLightcontrol(Gravity.CENTER);
            }
        });

        buttonLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPowerOn = !isPowerOn;
                if (isPowerOn) {
                    //sendDataMQTT("temperature", "25.5");
                    buttonLight.setCardBackgroundColor(Color.parseColor("#00CC66")); // Màu nền khi bật
                } else {
                    //sendDataMQTT("humidity", "90.0");
                    buttonLight.setCardBackgroundColor(Color.parseColor("#F44336")); // Màu nền khi tắt
                }
            }
        });

        return view;
    }

    private void openLightcontrol(int gravity){
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.control_light_layout);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);


        if(Gravity.CENTER == gravity){
            dialog.setCancelable(true);
        } else{
            dialog.setCancelable(false);
        }

//        Hiển thị thông số seekbar lên texview
        TextView tv;
        SeekBar sbar;
        ImageView picture;
        tv = dialog.findViewById(R.id.status_light_seekbar);
        sbar = dialog.findViewById(R.id.seekBar_light);
        picture = dialog.findViewById(R.id.light_picture);

        sbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tv.setText(String.valueOf(i));
                if( i > 20){
                    picture.setImageResource(R.drawable.light_turnon_image);
                } else{
                    picture.setImageResource(R.drawable.light_image);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
//------------------------------------


        dialog.show();
    }

//    public void startMQTT(){
//        mqttHelper = new MQTTHelper_Device(this);
//        mqttHelper.setCallback(new MqttCallbackExtended() {
//            @Override
//            public void connectComplete(boolean reconnect, String serverURI) {
//                if (reconnect) {
//                    Log.d("MQTT", "Reconnected to: " + serverURI);
//                } else {
//                    Log.d("MQTT", "Connected to: " + serverURI);
//                }
//            }
//
//            @Override
//            public void connectionLost(Throwable cause) {
//                Log.d("MQTT", "Connection lost. Cause: " + cause.getMessage());
//            }
//
//            @Override
//            public void messageArrived(String topic, MqttMessage message) throws Exception {
//                // Xử lý khi có thông điệp mới đến từ ThingsBoard (nếu cần)
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken token) {
//                // Xử lý khi việc gửi message hoàn tất (nếu cần)
//            }
//        });
//    }


//    public void sendDataMQTT(String key, String value) {
//        try {
//            JSONObject telemetryData = new JSONObject();
//            telemetryData.put(key, value);
//
//            MqttMessage msg = new MqttMessage();
//            msg.setId(1234);
//            msg.setQos(0);
//            msg.setRetained(false);
//
//            byte[] b = telemetryData.toString().getBytes(Charset.forName("UTF-8"));
//            msg.setPayload(b);
//
//            mqttHelper.mqttAndroidClient.publish("v1/devices/me/telemetry", msg);
//
//        } catch (JSONException | MqttException e) {
//            e.printStackTrace();
//        }
//    }

}
