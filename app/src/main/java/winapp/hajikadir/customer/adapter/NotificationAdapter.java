package winapp.hajikadir.customer.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.helper.DBHelper;
import winapp.hajikadir.customer.model.Notification;

/**
 * Created by user on 12-Oct-17.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<Notification> notificationList;
    private DBHelper mDBHelper;
    public NotificationAdapter(Activity activity, List<Notification> notificationList){
        this.mDBHelper = new DBHelper(activity);
        this.notificationList = notificationList;
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification,parent,false);
        return new NotificationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        try {
            Notification notification = notificationList.get(position);

            // displaying text view data
            holder.title.setText(notification.getTitle());
            holder.message.setText(notification.getMessage());
            String date = notification.getTimestamp().split("\\ ")[0];
            String time = notification.getTimestamp().split("\\ ")[1];

            DateFormat f1 = new SimpleDateFormat("HH:mm:ss"); //HH for hour of the day (0 - 23)
            Date d = f1.parse(time);
            DateFormat f2 = new SimpleDateFormat("h:mm a");
           // f2.format(d).toLowerCase(); // "12:18am"

            holder.timestamp.setText(f2.format(d).toLowerCase());

            holder.iconText.setText(date);
            holder.close.setId(position);
            holder.close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = v.getId();
                    removeItem(position);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder{
        public TextView title, message, iconText, timestamp;
        public ImageView close, imgProfile;
        public LinearLayout messageContainer;
        public RelativeLayout iconContainer, iconBack, iconFront;

        public NotificationViewHolder(View view){
            super(view);
            title = (TextView) view.findViewById(R.id.textName);
            message = (TextView) view.findViewById(R.id.textSize);
            timestamp = (TextView) view.findViewById(R.id.textPrice);
            iconText = (TextView) view.findViewById(R.id.textType);
            close = (ImageView) view.findViewById(R.id.close);

        }
    }
    private void removeItem(int position){
        Notification notification = notificationList.get(position);
        notificationList.remove(notification);
        mDBHelper.deleteNotification(notification.getId());
        notifyDataSetChanged();
    }
}
