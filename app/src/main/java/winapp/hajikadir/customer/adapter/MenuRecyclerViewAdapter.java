package winapp.hajikadir.customer.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.theartofdev.fastimageloader.FastImageLoader;
import com.theartofdev.fastimageloader.ImageLoadSpec;
import com.theartofdev.fastimageloader.target.TargetImageView;

import java.util.List;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.helper.HajikadirApp;
import winapp.hajikadir.customer.helper.Specs;
import winapp.hajikadir.customer.helper.UrlImageLoader;
import winapp.hajikadir.customer.model.Category;

/**
 * Created by user on 14-Jul-16.
 */
public class MenuRecyclerViewAdapter extends RecyclerView.Adapter<MenuRecyclerViewAdapter.RecyclerViewHolders> {
    public interface OnItemClickListener {
        public void onItemClick(int position);
    }
   // private final OnItemClickListener listener;
    private List<Category> itemList;
    private Activity activity;
    private ImageLoader mImageLoader;
    private Typeface mTruenoRg;
    private UrlImageLoader mUrlImageLoader;
    public MenuRecyclerViewAdapter(Activity activity, List<Category> itemList/*,OnItemClickListener listener*/) {
       mImageLoader = HajikadirApp.getImageLoader();
        this.itemList = itemList;
        this.activity = activity;
      //  this.listener = listener;
      //  this.mUrlImageLoader = new UrlImageLoader(activity);
    }
    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_category_recycler_view, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        layoutView.setId(viewType);
        return rcv;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, int position) {
        Category category = getItem(position);

        if(category.getImage()!=null && !category.getImage().isEmpty()){
           mImageLoader.displayImage(category.getImage(), holder.menuIcon);
          //  mUrlImageLoader.displayImage(category.getImage(), holder.menuIcon);
          //  ImageLoadSpec spec = FastImageLoader.getSpec(Specs.IMG_IX_IMAGE);
          // holder.menuIcon.loadImage(category.getImage(), spec.getKey());
        }else{
            holder.menuIcon.setImageResource(R.mipmap.no_img_available);
        }

        holder.menuName.setText(category.getName());
        //Fonts
        mTruenoRg = Typeface.createFromAsset(activity.getAssets(), "fonts/TruenoSBd.otf");
        int[] color = {Color.parseColor("#8d8d8d"), Color.parseColor("#c9c9c9")};
        Shader.TileMode tile_mode1 = Shader.TileMode.REPEAT;// or TileMode.REPEAT;
        RadialGradient rad_grad = new RadialGradient(0, 3, 5, color[0], color[1], tile_mode1);
        Shader shader_gradient1 = rad_grad;
        holder.menuName.getPaint().setShader(shader_gradient1);
        holder.menuName.setTypeface(mTruenoRg);
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

        }
    }
}