package winapp.hajikadir.customer.adapter;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.theartofdev.fastimageloader.FastImageLoader;
import com.theartofdev.fastimageloader.ImageLoadSpec;
import com.theartofdev.fastimageloader.target.TargetImageView;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.helper.HajikadirApp;
import winapp.hajikadir.customer.helper.Specs;


public class BannerAdapter extends PagerAdapter {

    private  Context context;
   private  ArrayList<String> mBannerImage;
    private ImageLoader mImageLoader;
    public BannerAdapter(Context context, ArrayList<String> bannerImage) {
        this.context = context;
        this.mBannerImage = bannerImage;
        mImageLoader = HajikadirApp.getImageLoader();
        if (HajikadirApp.mPrefetchImages) {
            for (String item : bannerImage) {
                FastImageLoader.prefetchImage(item, Specs.IMG_IX_IMAGE);
            }
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View viewItem = null;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        try {
            viewItem = inflater.inflate(R.layout.item_banner, container, false);
            TargetImageView imageView = (TargetImageView) viewItem
                    .findViewById(R.id.image_display);
            String banner = mBannerImage.get(position);
            Log.d("mBannerImage","-->"+banner);
            ImageLoadSpec spec = FastImageLoader.getSpec(Specs.IMG_IX_IMAGE);
            imageView.loadImage(banner, spec.getKey());
            mImageLoader.displayImage(banner,imageView);

        } catch (Exception e) {
            e.printStackTrace();
        }
        ((ViewPager) container).addView(viewItem);

        return viewItem;
    }

    @Override
    public int getCount() {
        return mBannerImage.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

}