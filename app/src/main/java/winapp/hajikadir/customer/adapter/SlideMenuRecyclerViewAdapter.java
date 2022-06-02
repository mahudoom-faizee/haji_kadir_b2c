package winapp.hajikadir.customer.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.helper.HajikadirApp;
import winapp.hajikadir.customer.helper.UrlImageLoader;
import winapp.hajikadir.customer.model.Category;

/**
 * Created by user on 27-Oct-16.
 */

public class SlideMenuRecyclerViewAdapter extends RecyclerView.Adapter<SlideMenuRecyclerViewAdapter.RecyclerViewHolders> {
    /*public interface OnItemClickListener {
        public void onItemClick(int position);
    }*/
    private SlideMenuRecyclerViewAdapter.OnItemClickListener mItemClickListener = null;
    // private final OnItemClickListener listener;
    private List<Category> itemList;
    private Activity activity;
    //private ImageLoader mImageLoader;
    private Typeface mTruenoRg;
    private UrlImageLoader mUrlImageLoader;
    public SlideMenuRecyclerViewAdapter() {
    }
    public SlideMenuRecyclerViewAdapter(Activity activity, List<Category> mItemList/*,OnItemClickListener listener*/) {
      //  mImageLoader = HajikadirApp.getImageLoader();
        itemList = new ArrayList<>();
        this.itemList = mItemList;
        this.activity = activity;
        mUrlImageLoader = new UrlImageLoader(activity);
        //this.listener = listener;
    }
    @Override
    public SlideMenuRecyclerViewAdapter.RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_slide_recycler_view, null);
        SlideMenuRecyclerViewAdapter.RecyclerViewHolders rcv = new SlideMenuRecyclerViewAdapter.RecyclerViewHolders(layoutView);
        layoutView.setId(viewType);
        return rcv;
    }
    @Override
    public void onBindViewHolder(SlideMenuRecyclerViewAdapter.RecyclerViewHolders holder, int position) {
        Category category = getItem(position);
        if(category.getImage()!=null && !category.getImage().isEmpty()){
            mUrlImageLoader.displayImage(category.getImage(), holder.menuIcon);
        }else{
            holder.menuIcon.setImageResource(R.mipmap.no_img_available);
        }
        holder.menuName.setText(category.getName());
         }
    public Category getItem(int position)
    {
        return itemList.get(position);
    }
    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView menuName;
        public ImageView menuIcon;
        public RecyclerViewHolders(View itemView) {
            super(itemView);
            menuName = (TextView)itemView.findViewById(R.id.menu_name);
            menuIcon = (ImageView)itemView.findViewById(R.id.menu_icon);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            // listener.onItemClick(getPosition());

            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view,getAdapterPosition());
            }

        }
    }
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final SlideMenuRecyclerViewAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}