package winapp.hajikadir.customer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.adapter.BannerAdapter;
import winapp.hajikadir.customer.adapter.MenuRecyclerViewAdapter;
import winapp.hajikadir.customer.adapter.SlideMenuRecyclerViewAdapter;
import winapp.hajikadir.customer.config.StringUtils;
import winapp.hajikadir.customer.helper.DBHelper;
import winapp.hajikadir.customer.helper.HajikadirApp;
import winapp.hajikadir.customer.helper.ProgressDialog;
import winapp.hajikadir.customer.helper.RestClientAcessTask;
import winapp.hajikadir.customer.helper.SessionManager;
import winapp.hajikadir.customer.helper.SettingsManager;
import winapp.hajikadir.customer.helper.UIHelper;
import winapp.hajikadir.customer.model.Branch;
import winapp.hajikadir.customer.model.Category;
import winapp.hajikadir.customer.util.CirclePageIndicator;
import winapp.hajikadir.customer.util.RecyclerItemClickListener;
import winapp.hajikadir.customer.util.Utils;

public class HomeActivity extends BaseDrawerActivity implements DialogInterface.OnCancelListener {
    private Handler mHandler;
    private int currentimageindex = 0, delay = 500, period = 2000;
    private Timer timer;
    private Thread thrdGetCategories,thrdGetBanner,thrdGetLocation,thrdGetHoliday;
    private ArrayList<Category> mCategoryMenuArr,mCategoryArr, mCatSubArr, mSubCategoryArr;
    private ArrayList<String> mBannerArr;
    private GridLayoutManager mGridLayoutManager;
    private RecyclerView mRecyclerView;
    private MenuRecyclerViewAdapter mMenuRecyclerViewAdapter;
   // private LinearLayout mLoadinglayout;
  //  private TextView mLoadingStatusTxtV;
   // private ProgressBar mProgressBar;
    public ArrayList<Branch> mBranchArr;
    private Animation mAnimFadeIn;
    private UIHelper mUIHelper;
    private Intent mIntent;
    private ProgressDialog mProgressDialog;
    private ImageLoader mImageLoader;
    private ViewPager mViewPager;
    private CirclePageIndicator mIndicator;
    private Runnable animateViewPager;
    private boolean stopSliding = false;
    private static final int SCROLL_DIRECTION_UP = -1;
    private NestedScrollView mNestedScrollView;
    private Activity activity;
    private boolean isLoading;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String regTokenId ="",showLocation="",alert="",shop="",delivery="";
    private SettingsManager mSettingsManager;
    private  int strDate;
    String holidayCode="";
    HashMap<String, String> productvalues;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        DBHelper.init(HomeActivity.this);
        dbHelper=new DBHelper(HomeActivity.this);
        //set Title
        mTitle.setText(getResources().getString(R.string.title_activity_home));
        //View ID
      //  mLoadingStatusTxtV = (TextView) findViewById(R.id.lblLoadingStatus);
       // mLoadinglayout = (LinearLayout) findViewById(R.id.lytLoading);
      //  mProgressBar = (ProgressBar) findViewById(R.id.prgLoading);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mNestedScrollView  = (NestedScrollView) findViewById(R.id.nestedScrollView);

        mSettingsManager = new SettingsManager(HomeActivity.this);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //Object Initializtaion
        mHandler = new Handler();
        mIntent = new Intent();
        mBranchArr = new ArrayList<>();
        mUIHelper = new UIHelper(HomeActivity.this);
        mCategoryArr = new ArrayList<>();
        mCategoryMenuArr = new ArrayList<>();
        mCatSubArr = new ArrayList<>();
        mSubCategoryArr = new ArrayList<>();
        mBannerArr = new ArrayList<>();
        productvalues=new HashMap<>();

        mImageLoader = HajikadirApp.getImageLoader();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        mProgressDialog = ProgressDialog.show(HomeActivity.this,"Loading...", true,true,this);

        mAnimFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in2);
        mAnimFadeIn = new AlphaAnimation(1.0f, 0.0f);
        mAnimFadeIn.setDuration(1000);
        mAnimFadeIn.setStartOffset(3000);

        Bundle b = getIntent().getExtras();
        if(b!=null){
            showLocation = b.getString(SHOW_LOCATION);
            alert=b.getString("Holidayalert");
        }

        Log.d("showLocation","-->"+showLocation);
      /*  if (mSession.getBranchId() != null && !mSession.getBranchId().isEmpty()) {

        }else{*/

    //    }

        mGetBanner();

       // appBarLayout.setExpanded(false, true);
        HashMap<String, String> userDetails = mSession.getUserDetails();
        final String userId = userDetails.get(PREF_SESSION_USER_ID);
        final String password = userDetails.get(PREF_SESSION_PASSWORD);

        Log.d("userId","-->"+userId);
        Log.d("password","-->"+password);

        mGridLayoutManager = new GridLayoutManager(HomeActivity.this, 3);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mIndicator.setOnPageChangeListener(new PageChangeListener());
        //mViewPager.setOnPageChangeListener(new PageChangeListener());
        mViewPager.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction()) {

                    case MotionEvent.ACTION_CANCEL:
                        break;

                    case MotionEvent.ACTION_UP:
                        // calls when touch release on ViewPager
                        if (mBannerArr != null && mBannerArr.size() != 0) {
                            stopSliding = false;
                            runnable(mBannerArr.size());
                            mHandler.postDelayed(animateViewPager,
                                    ANIM_VIEWPAGER_DELAY_USER_VIEW);
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        // calls when ViewPager touch
                        if (mHandler != null && stopSliding == false) {
                            stopSliding = true;
                            mHandler.removeCallbacks(animateViewPager);
                        }
                        break;
                }
                return false;
            }
        });

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        mSubCategoryArr.clear();
                        String categoryId = mCategoryArr.get(position).getId();

                        mGetSubCategory(categoryId);
                    }
                })
        );

        findViewById(R.id.product_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIntent.setClass(HomeActivity.this, ProductActivity.class);
                mIntent.putExtra(StringUtils.EXTRA_CATEGORY_ID,"");
                mIntent.putExtra(StringUtils.EXTRA_PRODUCT, "Product");
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
            }
        });
        findViewById(R.id.myorder_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(userId!=null && !userId.isEmpty() && password!=null && !password.isEmpty()) {
                    mIntent.setClass(HomeActivity.this, MyOrderActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mIntent);

                }else{
                    mIntent.setClass(HomeActivity.this, LoginActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mIntent);

                }

            }
        });
        findViewById(R.id.myaccount_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(userId!=null && !userId.isEmpty() && password!=null && !password.isEmpty()) {
                    mIntent.setClass(HomeActivity.this, MyAccountActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mIntent);

                }else{
                    mIntent.setClass(HomeActivity.this, LoginActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mIntent);

                }
            }
        });
        findViewById(R.id.registration_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIntent.setClass(HomeActivity.this, RegistrationActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
            }
        });
        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIntent.setClass(HomeActivity.this, SearchActivity.class);
                startActivity(mIntent);


            }
        });

        ((ImageView)findViewById(R.id.bottom_home_img)).setImageResource(R.mipmap.ic_action_home_active);
        ((TextView)findViewById(R.id.bottom_home_txt)).setTextColor(getResources().getColor(android.R.color.white));


//        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
//                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
//                    displayFirebaseRegId();
//                }
//            }
//        };

        //subscribeToPushService();
    }
//    public void displayFirebaseRegId(){
//        regTokenId= mSettingsManager.getRegisterTokenId();
//
//        Log.d("Firebase Reg Id","-->"+ regTokenId);
//
//    }

    public void runnable(final int size) {
        animateViewPager = new Runnable() {
            public void run() {
                if (!stopSliding) {
                    if (mViewPager.getCurrentItem() == size - 1) {
                        mViewPager.setCurrentItem(0);
                    } else {
                        mViewPager.setCurrentItem(
                                mViewPager.getCurrentItem() + 1, true);
                    }
                    mHandler.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);
                }
            }
        };
    }
    private class PageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {

            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
        }
    }
    public void mGetLocation(final Activity activity, boolean isLoading){

        if(isLoading) {
            this.activity = activity;
            this.isLoading = isLoading;
            mUIHelper = new UIHelper(activity);
            mSession = new SessionManager(activity);
            mBranchArr = new ArrayList<>();
            mProgressDialog = ProgressDialog.show(activity, "Loading...", true, true, this);
            mAnimFadeIn = AnimationUtils.loadAnimation(activity, R.anim.fade_in2);
            mAnimFadeIn = new AlphaAnimation(1.0f, 0.0f);
            mAnimFadeIn.setDuration(1000);
            mAnimFadeIn.setStartOffset(3000);
        }

        thrdGetLocation = new Thread() {
            @Override
            public void run() {

                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;
                try {

                    RestClientAcessTask client = new RestClientAcessTask(activity, FUNC_GET_LOCATION);
                    client.addParam("branchid", "");
                    RestClientAcessTask.Status status = client.processAndFetchResponseWithParameter();
                    if (status == RestClientAcessTask.Status.SUCCESS) {
                        String sResponse = client.getResponse();
                        if (!Utils.isEmpty(sResponse)) {
                            sCode = "SUCCESS";
                            sDesc = sResponse;
                        } else {
                            sCode = "ERROR";
                            sDesc = "Error occured while loading data. No response!";
                        }
                        sCode = "SUCCESS";
                        sDesc = sResponse;
                    } else {
                        sCode = "ERROR";
                        sDesc = "Error occured while loading data."
                                + client.getErrorMessage();
                    }

                } catch (Exception e) {
                    sCode = "ERROR";
                    sDesc = "Error occured while loading data!";
                }

                bndle.putString("CODE", sCode);
                bndle.putString("DESC", sDesc);
                msg.setData(bndle);
                ctgLocationProgressHandler.sendMessage(msg);
            }
        };
        thrdGetLocation.start();
    }
    /**
     * Handling the message while progressing
     */
    private Handler ctgLocationProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                thrdGetLocation.interrupt();
            } catch (Exception e) {
            }
            String sResponseCode = msg.getData().getString("CODE");
            String sResponseMsg = msg.getData().getString("DESC");

            if (sResponseCode.equals("SUCCESS")) {
                try {
                    JSONObject jsonObject = new JSONObject(sResponseMsg);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        int len = jsonArray.length();
                        Log.d("jsonArray", "" + jsonArray);
                        for (int i = 0; i < len; i++) {
                            Branch mBranch = new Branch();
                            JSONObject object = jsonArray.getJSONObject(i);
                            String location_id = object.getString("location_id");
                            String name = object.getString("name");
                            String dname = object.getString("dname");
                            String postcode = object.getString("postcode");
                            String address = object.getString("address");
                            String address_2 = object.getString("address_2");
                            String country = object.getString("country");
                            String telephone = object.getString("telephone");
                            String latitude=object.getString("latitude");
                            String longitude=object.getString("longitude");

                            mBranch.setBranchCode(location_id);
                            mBranch.setBranchName(name);
                            mBranch.setBranchDisplayName(dname);
                            mBranch.setBranchPostalCode(postcode);
                            mBranch.setBranchAddress1(address);
                            mBranch.setBranchAddress2(address_2);
                            mBranch.setBranchCountry(country);
                            mBranch.setBranchPhoneNo(telephone);
                            mBranch.setBranchlatitude(latitude);
                            mBranch.setBranchlongitude(longitude);
                            mBranchArr.add(mBranch);
                        }
                    }  Log.d("mBranchArr","-->"+mBranchArr.size());
                    mProgressDialog.setMessage("Loading...");
                    initLocationLoadCompleted();

                } catch (Exception e) {
                  //  e.printStackTrace();
                    mUIHelper.showErrorDialog(e.getMessage());
                }

            } else if (sResponseCode.equals("ERROR")) {
                mUIHelper.showErrorDialog(sResponseMsg);
                mProgressDialog.dismiss();
            }

        }
    };

    private void mGetBanner(){

        thrdGetBanner = new Thread() {
            @Override
            public void run() {

                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;
                try {

                    RestClientAcessTask client = new RestClientAcessTask(HomeActivity.this, FUNC_GET_BANNER);
                    client.addParam("banner", "");
                    RestClientAcessTask.Status status = client.processAndFetchResponseWithParameter();
                    if (status == RestClientAcessTask.Status.SUCCESS) {
                        String sResponse = client.getResponse();
                        if (!Utils.isEmpty(sResponse)) {
                            sCode = "SUCCESS";
                            sDesc = sResponse;
                        } else {
                            sCode = "ERROR";
                            sDesc = "Error occured while loading data. No response!";
                        }
                        sCode = "SUCCESS";
                        sDesc = sResponse;
                    } else {
                        sCode = "ERROR";
                        sDesc = "Error occured while loading data."
                                + client.getErrorMessage();
                    }

                } catch (Exception e) {
                    sCode = "ERROR";
                    sDesc = "Error occured while loading data!";
                }

                bndle.putString("CODE", sCode);
                bndle.putString("DESC", sDesc);
                msg.setData(bndle);
                ctgBannerProgressHandler.sendMessage(msg);
            }
        };
        thrdGetBanner.start();
    }
    /**
     * Handling the message while progressing
     */
    private Handler ctgBannerProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                thrdGetBanner.interrupt();
            } catch (Exception e) {
            }
            String sResponseCode = msg.getData().getString("CODE");
            String sResponseMsg = msg.getData().getString("DESC");

            if (sResponseCode.equals("SUCCESS")) {
                try {
                    JSONObject jsonObject = new JSONObject(sResponseMsg);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        int len = jsonArray.length();
                        Log.d("jsonArray", "" + jsonArray);
                        for (int i = 0; i < len; i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String image = object.getString("image");
                            String bannerImg = API_SERVER_URL+"image/"+image;
                            Log.d("bannerImg","-->"+bannerImg);
                            mBannerArr.add(bannerImg);
                        }
                    }
                    mProgressDialog.setMessage("Loading...");
                    initBannerLoadCompleted();

                } catch (Exception e) {
                    mUIHelper.showErrorDialog(e.getMessage());
                }

            } else if (sResponseCode.equals("ERROR")) {
                mUIHelper.showErrorDialog(sResponseMsg);
                mProgressDialog.dismiss();
            }
            mGetCategories();

        }
    };
    public void initBannerLoadCompleted() {
        mViewPager.setAdapter(new BannerAdapter(HomeActivity.this,mBannerArr));
        mIndicator.setViewPager(mViewPager);
        runnable(mBannerArr.size());
        //Re-run callback
        mHandler.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);
    }

    private void mGetCategories() {
        thrdGetCategories = new Thread() {
            @Override
            public void run() {

                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;
                try {

                    RestClientAcessTask client = new RestClientAcessTask(HomeActivity.this, FUNC_GET_CATEGORY);
                    client.addParam("category", "");
                    RestClientAcessTask.Status status = client.processAndFetchResponseWithParameter();
                    if (status == RestClientAcessTask.Status.SUCCESS) {
                        String sResponse = client.getResponse();
                        if (!Utils.isEmpty(sResponse)) {
                            sCode = "SUCCESS";
                            sDesc = sResponse;
                        } else {
                            sCode = "ERROR";
                            sDesc = "Error occured while loading data. No response!";
                        }
                        sCode = "SUCCESS";
                        sDesc = sResponse;
                    } else {
                        sCode = "ERROR";
                        sDesc = "Error occured while loading data."
                                + client.getErrorMessage();
                    }

                } catch (Exception e) {
                    sCode = "ERROR";
                    sDesc = "Error occured while loading data!";
                }

                bndle.putString("CODE", sCode);
                bndle.putString("DESC", sDesc);
                msg.setData(bndle);
                ctgCategoryProgressHandler.sendMessage(msg);
            }
        };
        thrdGetCategories.start();
    }

    /**
     * Handling the message while progressing
     */
    private Handler ctgCategoryProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                thrdGetCategories.interrupt();
            } catch (Exception e) {
            }
            String sResponseCode = msg.getData().getString("CODE");
            String sResponseMsg = msg.getData().getString("DESC");

            if (sResponseCode.equals("SUCCESS")) {
                try {
                    Category mCategory = new Category();

                    mCategory.setId("All Products");
                    mCategory.setParentId("All Products");
                    mCategory.setName("All Products");
                    mCategory.setImage("");
                    mCategoryMenuArr.add(mCategory);
                    JSONObject jsonObject = new JSONObject(sResponseMsg);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        int len = jsonArray.length();
                        Log.d("jsonArray", "" + jsonArray);
                        for (int i = 0; i < len; i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            HashMap<String, String> productvalues = new HashMap<String, String>();
                            mCategory = new Category();
                            String cId = object.getString("category_id");
                            String parentId = object.getString("parent_id");
                            String cName = object.getString("name");
                            String cImgUrl = object.getString("image");
                            //String cImageUrl = "";
                            if(cImgUrl!=null && !cImgUrl.isEmpty()){
                                cImgUrl = API_SERVER_URL + "image/" + cImgUrl;
                                cImgUrl = cImgUrl.replaceAll(" ", "%20");
                            }

                            Log.d("cImageUrl","-->"+cImgUrl);
                          //  String cImageUrl = API_SERVER_URL + "image/" + cImgUrl;
                            mCategory.setId(cId);
                            mCategory.setParentId(parentId);
                            mCategory.setName(cName);
                            mCategory.setImage(cImgUrl);
                            productvalues.put(KEY_CATEGORY_ID,cId);
                            productvalues.put(KEY_PARENT_ID,parentId);
                            productvalues.put(KEY_NAME,cName);
                            productvalues.put(KEY_PRODUCTIMAGE,cImgUrl);
                            dbHelper.insertCategory(productvalues);
                            if (parentId.matches("0")) {
                                mCategoryArr.add(mCategory);
                                mCategoryMenuArr.add(mCategory);
                            }
                            mCatSubArr.add(mCategory);

                        }
                    }

                   /* mMenuRecyclerViewAdapter = new MenuRecyclerViewAdapter(HomeActivity.this, mCategoryArr, new MenuRecyclerViewAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            mSubCategoryArr.clear();
                            String categoryId = mCategoryArr.get(position).getId();
                            mGetSubCategory(categoryId);
                        }
                    });
                    mRecyclerView.setAdapter(mMenuRecyclerViewAdapter);*/

                    mMenuRecyclerViewAdapter = new MenuRecyclerViewAdapter(HomeActivity.this,mCategoryArr);
                    mRecyclerView.setAdapter(mMenuRecyclerViewAdapter);

                    mSlideMenuRecyclerViewAdapter = new SlideMenuRecyclerViewAdapter(HomeActivity.this,mCategoryMenuArr);
                    mBaseRecyclerView.setAdapter(mSlideMenuRecyclerViewAdapter);


                    mProgressDialog.setMessage("Loading...");

                    mProgressDialog.setMessage("Loading...");



//                    Cursor cursor = dbHelper.getHolidayCursor();
//                    int cartItemCount =cursor.getCount();
//                    Log.d("getHolidayCursor",""+cartItemCount);
//
//                    if (cursor != null && cursor.getCount() > 0) {
//                        Log.d("getCountValues",""+cursor.getCount());
//                        if (cursor.moveToFirst()) {
//                            do {
//                                holidayCode = cursor.getString(cursor
//                                        .getColumnIndex(DBHelper.COL_HOLIDAY));
//                                Log.d("holidayCode",""+holidayCode);
//                            } while (cursor.moveToNext());
//                        }
//
//                    }




                        holidayCode=dbHelper.getHoliday();

//                    if(holidayCode.equalsIgnoreCase("1")){
//                        initDataLoadCompleted("Inital data loaded successfully!");
//                    }else{
                        Log.d("getHolidayCursor",""+holidayCode);
                        getHoliday();
//                    }
                    //initDataLoadCompleted("Inital data loaded successfully!");

                } catch (Exception e) {
                    mUIHelper.showErrorDialog(e.getMessage());
                }

            } else if (sResponseCode.equals("ERROR")) {
                mUIHelper.showErrorDialog(sResponseMsg);
                mProgressDialog.dismiss();
            }


        }
    };



    private void getHoliday() {

        thrdGetHoliday = new Thread() {
            @Override
            public void run() {

                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;
//
                Date today = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String dateToStr = dateFormat.format(today);
                String[] items1 = dateToStr.split("-");
                String date=items1[0];
                strDate=Integer.parseInt(date);
                Log.d("dateToStr",""+date);
                Log.d("currentdate",dateToStr);

                try {

                    RestClientAcessTask client = new RestClientAcessTask(HomeActivity.this,FUNC_GET_HOLIDAY);
                    client.addParam("Date",dateToStr);
                    RestClientAcessTask.Status status = client.processAndFetchResponseWithParameter();
                    if (status == RestClientAcessTask.Status.SUCCESS) {
                        String sResponse = client.getResponse();
                        Log.d("ResponseStatus","" +sResponse);
                        if (!Utils.isEmpty(sResponse)) {
                            sCode = "SUCCESS";
                            sDesc = sResponse;
                        } else {
                            sCode = "ERROR";
                            sDesc = "Error occured while loading data. No response!";
                        }
                        sCode = "SUCCESS";
                        sDesc = sResponse;
                    } else {
                        sCode = "ERROR";
                        sDesc = "Error occured while loading data."
                                + client.getErrorMessage();
                    }

                } catch (Exception e) {
                    sCode = "ERROR";
                    sDesc = "Error occured while loading data!";
                }

                bndle.putString("CODE", sCode);
                bndle.putString("DESC", sDesc);
                msg.setData(bndle);
                ctgHolidayProgressHandler.sendMessage(msg);
            }
        };
        thrdGetHoliday.start();

    }

    private Handler ctgHolidayProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                thrdGetHoliday.interrupt();
            } catch (Exception e) {
            }
            String sResponseCode = msg.getData().getString("CODE");
            String sResponseMsg = msg.getData().getString("DESC");

            if (sResponseCode.equals("SUCCESS")) {
                try {
                    JSONObject jsonObject = new JSONObject(sResponseMsg);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        int len = jsonArray.length();
                        Log.d("jsonArrayLength", "" + len +jsonArray);
                        if(len>0){

                            for(int i=0;i<len;i++){
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                shop =jsonObject1.getString("shop");
                                delivery =jsonObject1.getString("delivery");
                            }

                            Log.d("ShopDelivery",""+shop +" "+delivery);

                            DBHelper.updateHoliday(1,shop,delivery);
                            Log.d("Holidayalert",""+alert);
                            if(alert.matches("Holidayalert")){
                                if(shop.equals("0")){
                                    createAlertForHoliday();
                                }else{
                                    initDataLoadCompleted("Inital data loaded successfully!");
                                }

                            }else{
                                ctgCategoryProgressHandler.postDelayed(new Runnable() {
                                    public void run() {
                                        // Actions to do after 10 seconds
                                        mProgressDialog.dismiss();
                                    }
                                }, 3000);
                            }

//
//                            for (int i = 0; i < len; i++) {
//                                JSONObject object = jsonArray.getJSONObject(i);
//                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//                                String fromDate=object.getString("fromdate");
//                                String[] items1 = fromDate.split("-");
//                                String dateFormat=items1[2];
//                                int date=Integer.parseInt(dateFormat);
//                                String toDate=object.getString("todate");
//                                String[] items2 = toDate.split("-");
//                                String dateFormats=items2[2];
//                                int date1=Integer.parseInt(dateFormats);
//                                Log.d("dateToStr",""+date1+date);
//                            }

                        } else{
//                            HashMap<String, String> productvalues=new HashMap<>();
                            DBHelper.updateHoliday(0, "0", "0");
                            initDataLoadCompleted("Inital data loaded successfully!");
//                            productvalues.put(KEY_HOLIDAY, "0");
//                            dbHelper.insertHoliday(productvalues);

                        }


                    }
                    mProgressDialog.setMessage("Loading...");


                } catch (Exception e) {
                    //  e.printStackTrace();
                    mUIHelper.showErrorDialog(e.getMessage());
                }

            } else if (sResponseCode.equals("ERROR")) {
                mUIHelper.showErrorDialog(sResponseMsg);
                mProgressDialog.dismiss();
            }

        }
    };

    private void createAlertForHoliday() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Holiday Alert!")
                .setMessage("Today Leave For Us....")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        initDataLoadCompleted("Inital data loaded successfully!");
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();


    }

/*    public void mGetSubCategory(String categoryId) {
       // Log.d("categoryId","--->"+categoryId);
      //  Log.d("mCatSubArr", "---" + mCatSubArr.size());
        if(mCatSubArr.size()>0){
        for (int i = 0; i < mCatSubArr.size(); i++) {
            Category mCat = mCatSubArr.get(i);
            String parentId = mCat.getParentId();
           // Log.d("parentId","--->"+categoryId);
            if (categoryId.matches(parentId)) {
                mSubCategoryArr.add(mCat);
            }
        }
          //  Log.d("mSubCategoryArr", "---" + mSubCategoryArr.size());
        mIntent.setClass(HomeActivity.this, ProductActivity.class);
        mIntent.putExtra(StringUtils.EXTRA_CATEGORY_ID,categoryId);
        mIntent.putParcelableArrayListExtra(StringUtils.EXTRA_CATEGORY, mSubCategoryArr);
        startActivity(mIntent);
        }
    }*/

    public void initDataLoadCompleted(String sMsg) {

      /*  ctgCategoryProgressHandler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                mProgressDialog.dismiss();
            }
        }, 3000);*/


        if(showLocation!=null && !showLocation.isEmpty()){
            mGetLocation(HomeActivity.this,false);
        }else{
            ctgCategoryProgressHandler.postDelayed(new Runnable() {
                public void run() {
                    // Actions to do after 10 seconds
                    mProgressDialog.dismiss();
                }
            }, 3000);
        }
    }

    public void initLocationLoadCompleted() {


        ctgLocationProgressHandler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                mProgressDialog.dismiss();
                if(isLoading){
                    CreateAlertDialogWithRadioButtonGroup(activity);
                }else{
                    CreateAlertDialogWithRadioButtonGroup(HomeActivity.this);
                }

            }
        }, 3000);


    }
    @Override
    public void onCancel(DialogInterface dialog) {
        mProgressDialog.dismiss();
    }
    public void mGetSubCategory(String categoryId) {
        Log.d("categoryId","-ha->"+categoryId);
        if(categoryId.equals("All Products")){
            mIntent.setClass(this, ProductActivity.class);
            mIntent.putExtra(StringUtils.EXTRA_CATEGORY_ID,"");
            mIntent.putExtra(StringUtils.EXTRA_PRODUCT, "Product");
            startActivity(mIntent);
        }else{
            if(mCatSubArr.size()>0){
                for (int i = 0; i < mCatSubArr.size(); i++) {
                    Category mCat = mCatSubArr.get(i);
                    String parentId = mCat.getParentId();
                    Log.d("parentIdiiii","-ff->"+parentId);
                    if (categoryId.matches(parentId)) {
                        Log.d("parentIdiiii","-matches->"+parentId);
                        mSubCategoryArr.add(mCat);
                    }
                }
                Log.d("mSubCategoryArr","-ac->"+mSubCategoryArr.size());
                mIntent.setClass(HomeActivity.this, ProductActivity.class);
                mIntent.putExtra(StringUtils.EXTRA_CATEGORY_ID,categoryId);
                mIntent.putParcelableArrayListExtra(StringUtils.EXTRA_CATEGORY, mSubCategoryArr);
                startActivity(mIntent);
            }
        }

    }
    public void CreateAlertDialogWithRadioButtonGroup(final Activity activity) {
        try {
            Log.d("mBranchArr", "-->" + mBranchArr.size());
            final String currentChoice = mSession.getCurrentChoice();
            int position = 0;
            if (currentChoice != null && !currentChoice.isEmpty()) {
                position = Integer.valueOf(currentChoice);
            } else {
                position = -1;
            }

            if (mBranchArr.size() > 0) {
                final CharSequence values[] = new CharSequence[mBranchArr.size()];
                for (int i = 0; i < mBranchArr.size(); i++) {
                    String branchDisplayName = mBranchArr.get(i).getBranchDisplayName().toString();
                    values[i] = branchDisplayName;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Select Location");
                builder.setCancelable(false);
                builder.setSingleChoiceItems(values, position, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                mSession.putCurrentChoice("0");
                                mSession.putBranchDisplayName(values[item].toString());
                                setSessionBranchDetails();
                                // Toast.makeText(activity, values[item].toString(), Toast.LENGTH_LONG).show();
                                break;
                            case 1:
                                mSession.putCurrentChoice("1");
                                mSession.putBranchDisplayName(values[item].toString());
                                // Toast.makeText(activity, values[item], Toast.LENGTH_LONG).show();
                                setSessionBranchDetails();
                                break;
                        }
                        alertDialog.dismiss();
                    }
                });

                alertDialog = builder.create();
                alertDialog.show();

                //final int currentChoice = mSession.getCurrentChoice();
                // Log.d("Current Choice","-->"+currentChoice);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void setSessionBranchDetails(){
        for (int i = 0; i < mBranchArr.size(); i++) {
            String sessionbranchdisplayname = mSession.getBranchDisplayName();
            String branchDisplayName = mBranchArr.get(i).getBranchDisplayName().toString();
            if (sessionbranchdisplayname != null && !sessionbranchdisplayname.isEmpty()) {
                if (sessionbranchdisplayname.equalsIgnoreCase(branchDisplayName)) {
                    String branchName = mBranchArr.get(i).getBranchName().toString();
                    String branchId = mBranchArr.get(i).getBranchCode().toString();
                    String branchPincode = mBranchArr.get(i).getBranchPostalCode().toString();
                    String branchPhoneNo = mBranchArr.get(i).getBranchPhoneNo().toString();
                    String branchCountry = mBranchArr.get(i).getBranchCountry().toString();
                    String branchAddress1 = mBranchArr.get(i).getBranchAddress1().toString();
                    String branchAddress2 = mBranchArr.get(i).getBranchAddress2().toString();
                    String branchLatitude = mBranchArr.get(i).getBranchlatitude().toString();
                    String branchLongitude= mBranchArr.get(i).getBranchlongitude().toString();
                    mSession.putBranch(branchId, branchName,branchAddress1,branchAddress2,branchPhoneNo,branchCountry, branchPincode,branchLatitude,branchLongitude);
                   /* mSession.putBranch(branchId, branchName,branchAddress1,branchAddress2,branchPhoneNo,branchCountry, branchPincode);*/



                }
            }

        }
    }
//    public void onResume(){
//
//        super.onResume();
//    }

//    @Override
//    protected  void onStart(){
//        super.onStart();
//        Cursor cursor = dbHelper.getHolidayCursor();
//        int cartItemCount =cursor.getCount();
//        Log.d("getHolidayCursors",""+cartItemCount);
//
//        if (cursor != null && cursor.getCount() > 0) {
//            Log.d("getCountValues",""+cursor.getCount());
//            if (cursor.moveToFirst()) {
//                do {
//                    holidayCode = cursor.getString(cursor
//                            .getColumnIndex(DBHelper.COL_HOLIDAY));
//                    Log.d("holidayCode",""+holidayCode);
//
//
//
//                } while (cursor.moveToNext());
//            }
//
//
//        }
//
//        dbHelper.deleteHoliday(holidayCode);
//    }


    @Override
    protected void onResume() {
        super.onResume();


        // register GCM registration complete receiver
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        //  LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
        //        new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        // NotificationUtils.clearNotifications(getApplicationContext());
        invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
    // LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

}
