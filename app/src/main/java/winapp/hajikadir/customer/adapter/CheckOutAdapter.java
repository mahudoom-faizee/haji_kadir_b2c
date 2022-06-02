package winapp.hajikadir.customer.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.helper.DBHelper;
import winapp.hajikadir.customer.helper.HajikadirApp;
import winapp.hajikadir.customer.helper.UrlImageLoader;
import winapp.hajikadir.customer.model.Modifier;
import winapp.hajikadir.customer.model.Product;
import winapp.hajikadir.customer.util.Constants;
import winapp.hajikadir.customer.view.ExpandableHeightGridView;


/**
 * Created by user on 14-Nov-16.
 */

public class CheckOutAdapter extends RecyclerView.Adapter<CheckOutAdapter.RecyclerViewHolders> implements Constants {
    private List<Product> mItemList;
    private Activity activity;
   // private ImageLoader mImageLoader;
    private UrlImageLoader mUrlImageLoader;
    private DBHelper mDBHelper;
    private ModifierAdapter mModifierAdapter;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public CheckOutAdapter(Activity activity, List<Product> itemList) {
        this.mItemList = itemList;
        this.activity = activity;
        this.mDBHelper = new DBHelper(activity);
     //   this.mImageLoader = HajikadirApp.getImageLoader();
        this.mUrlImageLoader = new UrlImageLoader(activity);
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_order_detail_recycler_view, null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        layoutView.setId(viewType);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, int position) {
        DecimalFormat df = new DecimalFormat("0.00##");
        Product product = getItem(position);
        holder.mNameTxtV .setText(product.getName());
        holder.mQtyTxtV.setText(String.valueOf(product.getQuantity()));
        String sno = product.getSno();
        ArrayList<Modifier> modifierList = mDBHelper.getProductModifier(sno);
        Log.d("modifierList","--"+modifierList.size());
        Double dPrice = 0.00;
        if(modifierList.size()>0){

            Log.d("modifierList","M-"+modifierList.size());
            mModifierAdapter = new ModifierAdapter(activity,R.layout.item_cart_modifier,modifierList);
            holder.mExpandableHeightGridView.setAdapter(mModifierAdapter);
            holder.mExpandableHeightGridView.setVisibility(View.VISIBLE);
            holder.mExpandableHeightGridView.setExpanded(true);
            dPrice = product.getModifierPrice().equals("") ? 0:Double.valueOf(product.getModifierPrice());
        }else{
            holder.mExpandableHeightGridView.setVisibility(View.GONE);
            dPrice = product.getPrice().equals("") ? 0:Double.valueOf(product.getPrice());

        }

       // Double dPrice = product.getPrice().equals("") ? 0:Double.valueOf(product.getPrice());
        holder.mPriceTxtV.setText("$" + df.format(dPrice));
        holder.mTotalTxtV.setText("$" + df.format(product.getTotal()));
        mUrlImageLoader.displayImage(product.getImage(), holder.mProductImgV);

    }
    public Product getItem(int position)
    {
        return mItemList.get(position);
    }
    @Override
    public int getItemCount() {
        return mItemList.size();
    }
    public class RecyclerViewHolders extends RecyclerView.ViewHolder {

        private TextView mQtyTxtV,mNameTxtV,mPriceTxtV,mTotalTxtV;
        private ImageView mProductImgV;
        private ExpandableHeightGridView mExpandableHeightGridView;
        public RecyclerViewHolders(View view) {
            super(view);
            mNameTxtV = (TextView) view.findViewById(R.id.textView1);
            mPriceTxtV = (TextView) view.findViewById(R.id.textView3);
            mTotalTxtV = (TextView) view.findViewById(R.id.textView4);
            mQtyTxtV= (TextView) view.findViewById(R.id.textView2);
            mProductImgV = (ImageView) view.findViewById(R.id.image);
            mExpandableHeightGridView = (ExpandableHeightGridView) view.findViewById(R.id.gridView);
        }
    }
}