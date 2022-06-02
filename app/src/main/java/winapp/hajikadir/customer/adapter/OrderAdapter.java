package winapp.hajikadir.customer.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.model.Order;
import winapp.hajikadir.customer.util.Utils;


/**
 * Created by user on 26-Oct-16.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.RecyclerViewHolders> {
    public interface OnItemClickListener {
        public void onItemClick(int position);
    }
   // private final OnItemClickListener listener;
    private List<Order> itemList;
    private Activity activity;
    private ImageLoader mImageLoader;
    public OrderAdapter(Activity activity, List<Order> itemList) {
        Log.d("itemList","-->"+itemList.size());
        this.itemList = itemList;
        this.activity = activity;
    }
    @Override
    public OrderAdapter.RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listing_order, null);
        OrderAdapter.RecyclerViewHolders rcv = new OrderAdapter.RecyclerViewHolders(layoutView);
        layoutView.setId(viewType);
        return rcv;
    }
    @Override
    public void onBindViewHolder(OrderAdapter.RecyclerViewHolders holder, int position) {
        Order order = getItem(position);

        holder.orderNo.setText(order.getNo());
        holder.date.setText(order.getDate());
        String total = order.getTotal().toString();
        if(total!=null && !total.isEmpty()){
            double dTotal = total.equals("") ? 0 : Double.valueOf(total);
            holder.total.setText(Utils.twoDecimalPoint(dTotal));
        }else{
            holder.total.setText("0.00");
        }
        String status = order.getStatus().toString();
        if(status!=null && !status.isEmpty() && !status.equals("null")){
            holder.status.setText(order.getStatus());
        }else{
            holder.status.setText("-");
        }

    }
    public Order getItem(int position)
    {
        return itemList.get(position);
    }
    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView orderNo,date,total,status;
        public RecyclerViewHolders(View itemView) {
            super(itemView);
            orderNo = (TextView)itemView.findViewById(R.id.orderNo);
            date = (TextView)itemView.findViewById(R.id.date);
            total = (TextView)itemView.findViewById(R.id.total);
            status = (TextView)itemView.findViewById(R.id.status);


            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            //listener.onItemClick(getPosition());

        }
    }
}