package winapp.hajikadir.customer.helper;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.theartofdev.fastimageloader.FastImageLoader;
import com.theartofdev.fastimageloader.adapter.IdentityAdapter;
import com.theartofdev.fastimageloader.adapter.ImgIXAdapter;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.util.CustomFonts;

/**
 * Created by user on 15-Jul-16.
 */
public class HajikadirApp extends Application {
    public static ImageLoader imageLoader;
    public static DisplayImageOptions displayOptions;
   public static final int INSTAGRAM_IMAGE_SIZE = 440;
    public static final int INSTAGRAM_AVATAR_SIZE = 150;

    public static boolean mPrefetchImages;
    public HajikadirApp() {
    }

    private static Context mContext;
    private static Activity activity;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

      /*  ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                mContext).threadPriority(Thread.NORM_PRIORITY - 1)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(5 * 1024 * 1024).writeDebugLogs().build();

        displayOptions = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);*/
        DisplayImageOptions displayimageOptions = new DisplayImageOptions.Builder()
               /* .cacheInMemory(true).cacheOnDisc(true)
                .showStubImage(R.mipmap.ic_empty_image)
                .showImageForEmptyUri(R.mipmap.ic_empty_image)
                .showImageOnFail(R.mipmap.ic_empty_image).build();
                */
                .resetViewBeforeLoading(true)
                .showImageOnLoading(R.mipmap.img_loading) // resource or drawable
                .showImageForEmptyUri(R.mipmap.no_img_found) // resource or drawable
                .showImageOnFail(R.mipmap.no_img_found) // resource or drawable
                .resetViewBeforeLoading(false)  // default
                .delayBeforeLoading(0)
                .cacheInMemory(false) // default
                .cacheOnDisk(false) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                mContext).defaultDisplayImageOptions(displayimageOptions)
                .build();
        imageLoader = ImageLoader.getInstance();
        ImageLoader.getInstance().init(config);

        CustomFonts.setDefaultFont(this, "DEFAULT", "fonts/TruenoLt.otf");
        CustomFonts.setDefaultFont(this, "MONOSPACE", "fonts/TruenoRg.otf");
        CustomFonts.setDefaultFont(this, "SERIF", "fonts/TruenoBlk.otf");
        CustomFonts.setDefaultFont(this, "SANS_SERIF", "fonts/TruenoSBd.otf");



       mPrefetchImages = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("prefetch", true);

        FastImageLoader
                .init(this)
                .setDefaultImageServiceAdapter(new ImgIXAdapter())
              //  .setWriteLogsToLogcat(true)
           //     .setLogLevel(Log.DEBUG)
                .setDebugIndicator(false);

        FastImageLoader.buildSpec(Specs.IMG_IX_UNBOUNDED)
                .setUnboundDimension()
             .setPixelConfig(Bitmap.Config.RGB_565)
                .build();

        FastImageLoader.buildSpec(Specs.IMG_IX_IMAGE)
              .setDimensionByDisplay()
                .setHeightByResource(R.dimen.image_height)
             .setPixelConfig(Bitmap.Config.RGB_565)
                .build();

       IdentityAdapter identityUriEnhancer = new IdentityAdapter();
        FastImageLoader.buildSpec(Specs.INSTA_AVATAR)
                .setDimension(INSTAGRAM_AVATAR_SIZE)
                .setImageServiceAdapter(identityUriEnhancer)
                .build();

        FastImageLoader.buildSpec(Specs.INSTA_IMAGE)
                .setDimension(INSTAGRAM_IMAGE_SIZE)
              .setPixelConfig(Bitmap.Config.RGB_565)
                .setImageServiceAdapter(identityUriEnhancer)
                .build();

        FastImageLoader.buildSpec(Specs.UNBOUNDED_MAX)
                .setUnboundDimension()
              .setMaxDensity()
                .build();

    }

    public static Context getContext() {
        return mContext;
    }

    public static void setActivity(Activity appActivity) {
        activity = appActivity;
    }

    public static Activity getActivity() {
        return activity;
    }

    public static ImageLoader getImageLoader() {
        return imageLoader;
    }

    public static DisplayImageOptions getImageLoaderDispOpts() {
        return displayOptions;
    }

}
