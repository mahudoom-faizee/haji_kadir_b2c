package winapp.hajikadir.customer.helper;

import android.content.Context;
import android.util.Log;

import java.io.File;

/**
 * Created by user on 31-May-17.
 */

public class FileCache {
    private File cacheDir;

    public FileCache(Context context) {
        // Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            cacheDir = new File(
                    android.os.Environment.getExternalStorageDirectory(),
                    "HajiKadirImageCache");
        else
            cacheDir = context.getCacheDir();
        if (!cacheDir.exists())
            Log.d("Directory","Created");
            cacheDir.mkdirs();
    }

    public File getFile(String url) {
        String filename = String.valueOf(url.hashCode());
        // String filename = URLEncoder.encode(url);
        File f = new File(cacheDir, filename);
        return f;

    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();
    }

}
