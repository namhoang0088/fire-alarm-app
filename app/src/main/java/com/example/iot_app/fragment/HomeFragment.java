package com.example.iot_app.fragment;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.iot_app.Notification;
import com.example.iot_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {
    private Timer timer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
//        Button btnSendNotification = view.findViewById(R.id.notification);
//        btnSendNotification.setOnClickListener(v -> sendNotification());
        LinearLayout linearLayout = view.findViewById(R.id.container_card);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Calendar endTime = Calendar.getInstance();
                Calendar startTime = (Calendar) endTime.clone();
                startTime.add(Calendar.MINUTE, -5);

                // Convert timestamps to milliseconds
                long startTs = startTime.getTimeInMillis();
                long endTs = endTime.getTimeInMillis();

                // Bearer token và các thông số cần thiết
                String thingsboardHost = "https://demo.thingsboard.io";
                int port = 80;
                String deviceId = "9ae20180-6818-11ed-81ab-ffa1a15f3161";
                String jtw = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJodXlxdWFuZzAwODFAZ21haWwuY29tIiwidXNlcklkIjoiY2JhMmQ0YTAtNWI1NC0xMWVkLWIyMzYtNTVhNmNkOTVjN2E2Iiwic2NvcGVzIjpbIlRFTkFOVF9BRE1JTiJdLCJzZXNzaW9uSWQiOiJlMmI5ZDUxMi1mN2RlLTQxMTMtYjRkMC1iOTE2M2I1YTA1MjUiLCJpc3MiOiJ0aGluZ3Nib2FyZC5pbyIsImlhdCI6MTcxNDYwNjg5NywiZXhwIjoxNzE2NDA2ODk3LCJmaXJzdE5hbWUiOiJIYXkiLCJsYXN0TmFtZSI6IkRheSIsImVuYWJsZWQiOnRydWUsInByaXZhY3lQb2xpY3lBY2NlcHRlZCI6dHJ1ZSwiaXNQdWJsaWMiOmZhbHNlLCJ0ZW5hbnRJZCI6ImMwMmUzZTcwLTViNTQtMTFlZC1iMjM2LTU1YTZjZDk1YzdhNiIsImN1c3RvbWVySWQiOiIxMzgxNDAwMC0xZGQyLTExYjItODA4MC04MDgwODA4MDgwODAifQ.mwquqnGGRyvZGjisFOL5CvcyFcMa_K6ObtngV-dn3Kuau2NKhzs9TMFpev8Kt_s_JNKV9zIS5nz7PROHrvONpw";

                HttpHelper httpHelper = new HttpHelper();
                httpHelper.retrieveTelemetryData(thingsboardHost, port, deviceId, jtw, startTs, endTs, new HttpHelper.DataCallback() {
                    @Override
                    public void onDataReceived(JSONObject jsonData) {
                        try {
                            // Lấy danh sách "smoke" từ đối tượng JSON
                            JSONArray smokeArray = jsonData.getJSONArray("FLAME");

                            // Kiểm tra từng giá trị "smoke"
                            for (int i = 0; i < smokeArray.length(); i++) {
                                JSONObject smokeObject = smokeArray.getJSONObject(i);

                                // Lấy giá trị "ts" và "value" từ đối tượng smoke
                                long timestamp = smokeObject.getLong("ts");
                                int smokeValue = smokeObject.getInt("value");

                                // Kiểm tra nếu giá trị "smoke" lớn hơn 400
                                if (smokeValue > 1000) {
                                    requireActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Thực hiện các thay đổi giao diện người dùng ở đây
                                            CardView cardView = new CardView(requireContext());
                                            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                            );

                                            cardView.setLayoutParams(cardParams);

                                            // Gọi layout của card từ resource
                                            getLayoutInflater().inflate(R.layout.card_notification, cardView);

                                            // Lấy giá trị thời gian ("ts")
                                            String formattedTime = convertTimestampToTime(timestamp);
                                            String formattedDate = convertTimestampToDate(timestamp);

                                            // Thay đổi giá trị của hour và date trong cardView
                                            TextView hourTextView = cardView.findViewById(R.id.hour);
                                            TextView dateTextView = cardView.findViewById(R.id.date);

                                            hourTextView.setText(formattedTime);
                                            dateTextView.setText(formattedDate);

                                            // Thêm cardView vào linearLayout (hoặc layout tương ứng)
                                            linearLayout.addView(cardView);
                                        }
                                    });

                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Xử lý ngoại lệ nếu có lỗi khi xử lý JSON
                        }
                    }
                });

            }
        }, 0, 30000);  // Lên lịch để chạy sau 0 ms, và lặp lại mỗi 60000 ms (1 phút)

        return view;
    }

    // Hàm chuyển đổi timestamp thành định dạng giờ
    private String convertTimestampToTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    // Hàm chuyển đổi timestamp thành định dạng ngày
    private String convertTimestampToDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }



 //notification---------------------------------------------


//    private void sendNotification() {
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//
//        // Kiểm tra xem channel đã được tạo hay chưa
//        createNotificationChannel(requireContext());
//
//        // Tạo NotificationCompat.Builder
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), Notification.CHANNEL_ID)
//                .setContentTitle("Tiêu đề thông báo")
//                .setContentText("Nội dung thông báo")
//                .setSmallIcon(R.drawable.fire__icon)
//                .setLargeIcon(bitmap)
//                .setColor(getResources().getColor(R.color.black))
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//        // Lấy NotificationManagerCompat
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
//
//        // Hiển thị thông báo
//        notificationManager.notify(getNotificationId(), builder.build());
//    }
//
//    // Hàm để tạo Notification Channel (phải thực hiện trước khi hiển thị thông báo)
//    private void createNotificationChannel(Context context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = context.getString(R.string.channel_name);
//            String description = context.getString(R.string.channel_description);
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(Notification.CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//
//            // Đăng ký channel với hệ thống
//            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
//            if (notificationManager != null) {
//                notificationManager.createNotificationChannel(channel);
//            }
//        }
//    }
//
//    private int getNotificationId() {
//        return (int) new Date().getTime();
//    }
}
