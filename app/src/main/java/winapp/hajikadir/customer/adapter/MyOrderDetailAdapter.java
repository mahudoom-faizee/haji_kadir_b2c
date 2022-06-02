package winapp.hajikadir.customer.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.activity.ProductActivity;
import winapp.hajikadir.customer.helper.HajikadirApp;
import winapp.hajikadir.customer.helper.UrlImageLoader;
import winapp.hajikadir.customer.model.Modifier;
import winapp.hajikadir.customer.model.Product;
import winapp.hajikadir.customer.util.Constants;
import winapp.hajikadir.customer.util.Utils;
import winapp.hajikadir.customer.view.ExpandableHeightGridView;


/**
 * Created by user on 05-Nov-16.
 */

public class MyOrderDetailAdapter extends RecyclerView.Adapter<MyOrderDetailAdapter.RecyclerViewHolders> implements Constants {
    private List<Product> mItemList;
    private List<Modifier> mItemModifierList;
    private Activity activity;
  //  private ImageLoader mImageLoader;
    private UrlImageLoader mUrlImageLoader;
    private ProductActivity mProductActivity;
    private ModifierAdapter mModifierAdapter;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public MyOrderDetailAdapter(Activity activity, List<Product> itemList,List<Modifier> modifierList) {
        this.mItemList = itemList;
        this.mItemModifierList = modifierList;
        this.activity = activity;
        mProductActivity = new ProductActivity();
      //  mImageLoader = HajikadirApp.getImageLoader();
        mUrlImageLoader = new UrlImageLoader(activity);
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_order_detail_recycler_view, null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MyOrderDetailAdapter.RecyclerViewHolders rcv = new MyOrderDetailAdapter.RecyclerViewHolders(layoutView);
        layoutView.setId(viewType);

    /*    View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_order_detail_recycler_view, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rootView.setLayoutParams(lp);
        return new RecyclerViewHolder(rootView);*/
        return rcv;
    }

    @Override
    public void onBindViewHolder(MyOrderDetailAdapter.RecyclerViewHolders holder, int position) {
        Product product = getItem(position);
        String imageUrl =  product.getImage();
        if(imageUrl!=null && !imageUrl.isEmpty()){
            mUrlImageLoader.displayImage(imageUrl, holder.mImage);
        }else{
            holder.mImage.setImageResource(R.mipmap.no_img_available);
        }
        holder.mName.setText(product.getName());
        String sPrice = product.getPrice();
        if(sPrice!=null && !sPrice.isEmpty()){
            sPrice = Utils.twoDecimalPoint(Double.valueOf(sPrice));
        }
        holder.mPrice.setText(sPrice);
        holder.mQuantity.setText(""+ product.getQuantity());
        holder.mTotal.setText(""+ product.getTotal());
        ArrayList<Modifier> modifierList = getProductModifier(product.getId());
        Log.d("modifierList","--"+modifierList.size());
       // Double dPrice = 0.00;
        if(modifierList.size()>0){

            Log.d("modifierList","M-"+modifierList.size());
            mModifierAdapter = new ModifierAdapter(activity,R.layout.item_cart_modifier,modifierList);
            holder.mExpandableHeightGridView.setAdapter(mModifierAdapter);
            holder.mExpandableHeightGridView.setVisibility(View.VISIBLE);
            holder.mExpandableHeightGridView.setExpanded(true);
         //   dPrice = product.getModifierPrice().equals("") ? 0:Double.valueOf(product.getModifierPrice());
        }else{
            holder.mExpandableHeightGridView.setVisibility(View.GONE);
          //  dPrice = product.getPrice().equals("") ? 0:Double.valueOf(product.getPrice());

        }
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

        public TextView mName,mPrice,mQuantity,mTotal;
        public ImageView mImage;
        private ExpandableHeightGridView mExpandableHeightGridView;
        public RecyclerViewHolders(View itemView) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(R.id.image);
            mName = (TextView)itemView.findViewById(R.id.textView1);
            mQuantity = (TextView)itemView.findViewById(R.id.textView2);
            mPrice = (TextView)itemView.findViewById(R.id.textView3);
            mTotal = (TextView)itemView.findViewById(R.id.textView4);
            mExpandableHeightGridView = (ExpandableHeightGridView) itemView.findViewById(R.id.gridView);
        }

    }
    public  ArrayList<Modifier> getProductModifier(String productId) {
        ArrayList<Modifier> modifierList = new ArrayList<Modifier>();
        if(mItemModifierList.size()>0){
            for (Modifier modifier : mItemModifierList){
                if(productId.equalsIgnoreCase(modifier.getId())){
                    modifierList.add(modifier);
                }
            }
        }
        return modifierList;

    }

}