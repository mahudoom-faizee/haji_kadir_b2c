package winapp.hajikadir.customer.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.helper.DBHelper;
import winapp.hajikadir.customer.helper.SettingsManager;
import winapp.hajikadir.customer.model.Product;
import winapp.hajikadir.customer.util.Constants;

public class SplashActivity  extends AppCompatActivity implements Constants {
    private Intent mIntent;
    private SettingsManager mSettings;
    private DBHelper dbHelper;
    private static final int REQUEST_ID_WRITE_PERMISSION = 100;
    private boolean sentToSettings = false;
    private int currentVersion;
    public static String FACEBOOK_URL = "https://www.facebook.com/Haji-Kadir-Foods-161228651416398";
    public static String FACEBOOK_PAGE_ID = "161228651416398";
    HashMap<String, String> productvalues;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        DBHelper.init(SplashActivity.this);
        dbHelper=new DBHelper(SplashActivity.this);
        productvalues=new HashMap<>();

//        setViewID();

//        Product.setHolidayCode("");

        currentVersion = getAppVersionCode(SplashActivity.this);
        Log.d("currentVersion","vr : "+currentVersion);
        Log.d("getPackageName()","ne "+getApplicationContext().getPackageName());
        new VersionChecker().execute();
        getHoliday();
//        new GetVersionCode().execute();
    }

    private void getHoliday() {

        if (dbHelper.getHolidayCursor().getCount() == 0) {
            DBHelper.setHoliday(0,"0","0");
        }
    }


  /*  private class GetVersionCode extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {

            String newVersion = null;
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName() + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();

                return newVersion;
            } catch (Exception e) {
                e.printStackTrace();
                return newVersion;
            }
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)) {
                    //show dialog
                    playStoreUpdate();
                }else{
                    setViewID();
                }
            }else{
                setViewID();
            }
            Log.d("update", "Current version " + currentVersion + " playstore version " + onlineVersion);
        }
    }*/

    public class VersionChecker extends AsyncTask<String, Document, Document> {

        private Document document;
        String newVersion="";

        @Override
        protected Document doInBackground(String... params) {

            Log.d("getApplicationContext","-->"+getApplicationContext().getPackageName());
//                document = Jsoup.connect("https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName() +"&hl=en")
//                        .timeout(30000)
//                        .userAgent("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
//                        .get();
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName()+ "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                        .first()
                        .ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("document","-->"+document);

            return document;
        }

        @Override
        protected void onPostExecute(Document d) {
            super.onPostExecute(d);

//            Elements es =  d.body().getElementsByClass("xyOfqd").select(".hAyfc");
//            String newVersion = es.get(3).child(1).child(0).child(0).ownText();


//            Element element = document.select("div:matchesOwn(^Current Version$)").first().parent().select("span").first();
//            String newVersion = element.text();

//            String newVersion = document.select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
//                    .first()
//                    .ownText();

            Log.i("update", "newVersion==="+newVersion);

            // playStoreUpdate();

            if (newVersion != null && !newVersion.isEmpty()) {
                if (Float.valueOf(currentVersion) < Float.valueOf(newVersion)) {
                    //show dialog
                    playStoreUpdate();
                }else{
                    setViewID();
                }
            }else{
                setViewID();
            }
        }
    }

    public int getAppVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException ex) {}
        return 0;
    }

    private void setViewID() {
        //object initialization
        mIntent = new Intent();
        dbHelper = new DBHelper(SplashActivity.this);
        mSettings = new SettingsManager(SplashActivity.this);
        mSettings.putWebserviceUrl(API_SERVER_URL);
        dbHelper.deleteAllProduct();
        dbHelper.deleteAllProductModifier();
        dbHelper.deleteAllCouponCode();
        dbHelper.truncateCategoryTables();
        mSettings.setBtnPosition("0");
        mSettings.setToastShow("NOTSHOW");
        mIntent.setClass(SplashActivity.this, HomeActivity.class);
        mIntent.putExtra("Holidayalert","Holidayalert");
        mIntent.putExtra(SHOW_LOCATION,"SHOW LOCATION");
        invokeHandler(startActivity);

    }
    private boolean askPermission(int requestId, String permissionName) {
        if (Build.VERSION.SDK_INT >= 23) {
            // Check if we have permission
            int permission = ActivityCompat.checkSelfPermission(this, permissionName);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                this.requestPermissions(
                        new String[]{permissionName},
                        requestId
                );
                return false;
            }else{
                if (ActivityCompat.shouldShowRequestPermissionRationale
                        (this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showAlert();
                }
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);        //
        // Note: If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0) {
            switch (requestCode) {
                case REQUEST_ID_WRITE_PERMISSION: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startActivity(mIntent);
                        finish();
                    }else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            //Show permission explanation dialog...
                            finish();
                        }else{
                            //Never ask again selected, or device policy prohibits the app from having that permission.
                            //So, disable that feature, or fall back to another situation...
                            showAlert();

                        }


                    }
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Permission Cancelled!", Toast.LENGTH_SHORT).show();
        }
    }
    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Need Storage Permission");
        alertDialog.setMessage("This app needs storage permission.");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_ID_WRITE_PERMISSION);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
                    }
                });
        alertDialog.show();
    }
    private void invokeHandler(Runnable runnable) {
        new Handler().postDelayed(runnable, SPLASH_SCREEN_TIME);
    }

    private Runnable startActivity = new Runnable() {
        @Override
        public void run() {
            boolean canWrite = askPermission(REQUEST_ID_WRITE_PERMISSION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            Log.d("canWrite",""+canWrite);
            if (canWrite) {
                startActivity(mIntent);
                finish();
            }

        }
    };

    private void playStoreUpdate() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("App Latest Update");
        alertDialog.setMessage("Please update latest app from google play store");
        alertDialog.setCancelable(false);
       /* alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });*/
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            // getOpenFacebookIntent(SplashActivity.this);
                           /* String URL = getFacebookPageURL(SplashActivity.this);
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL)));*/
                            // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/pg/Haji-Kadir-Foods-161228651416398/comunity/?ref=page_internal")));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }

                    }
                });
        alertDialog.show();
    }

    public static Intent getOpenFacebookIntent(Context context) {

        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/<id_here>"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/<user_name_here>"));
        }
    }


    //method to get the right URL to use in the intent
    public String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {//newer versions of fb app
                Log.d("if","if");
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                Log.d("else","else");
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                startActivity(mIntent);
                finish();
            }
        }
    }


}