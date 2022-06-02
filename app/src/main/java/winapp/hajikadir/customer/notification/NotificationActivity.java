package winapp.hajikadir.customer.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;



import java.util.ArrayList;
import java.util.List;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.activity.BaseDrawerActivity;
import winapp.hajikadir.customer.adapter.NotificationAdapter;
import winapp.hajikadir.customer.helper.DBHelper;
import winapp.hajikadir.customer.model.Notification;

public class NotificationActivity extends BaseDrawerActivity {
    private DBHelper mDBHelper;
    private List<Notification> notificationListList;
    private RecyclerView mRecyclerView;
    private TextView mEmpty;
    private NotificationAdapter mNotificationAdapter;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_notification);
            mTitle.setText(getResources().getString(R.string.title_notification));
            mEmpty = (TextView) findViewById(R.id.empty);
            mDBHelper = new DBHelper(NotificationActivity.this);
            notificationListList = new ArrayList<>();
            mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


            notificationListList = mDBHelper.readNotification();// Reading all notification
            if (notificationListList.size() > 0) {
                mNotificationAdapter = new NotificationAdapter(NotificationActivity.this, notificationListList);
                mRecyclerView.setAdapter(mNotificationAdapter);
                mRecyclerView.setVisibility(View.VISIBLE);
                mEmpty.setVisibility(View.GONE);
            } else {
                mRecyclerView.setVisibility(View.GONE);
                mEmpty.setVisibility(View.VISIBLE);
            }
            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                        String message = intent.getStringExtra("message");
                        Log.d("message","-->"+message);
                        notificationListList.clear();
                        notificationListList = mDBHelper.readNotification();// Reading all notification
                        mNotificationAdapter = new NotificationAdapter(NotificationActivity.this, notificationListList);
                        mRecyclerView.setAdapter(mNotificationAdapter);
                        mNotificationAdapter.notifyDataSetChanged();
                    }
                }
            };
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

}
