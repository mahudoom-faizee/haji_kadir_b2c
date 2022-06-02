package winapp.hajikadir.customer.activity;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.LayerDrawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.adapter.PagerAdapter;
import winapp.hajikadir.customer.adapter.ViewPagerAdapter;
import winapp.hajikadir.customer.config.StringUtils;
import winapp.hajikadir.customer.fragment.ProductFragment;
import winapp.hajikadir.customer.helper.DBHelper;
import winapp.hajikadir.customer.helper.ProgressDialog;
import winapp.hajikadir.customer.helper.UIHelper;
import winapp.hajikadir.customer.model.Category;
import winapp.hajikadir.customer.model.Parent;
import winapp.hajikadir.customer.model.Product;
import winapp.hajikadir.customer.util.Utils;

public class ProductActivity extends BaseDrawerActivity {
    private  int numOfPages = 0;
    private  ArrayList<Category> mCategory;
    private  TabLayout mTabLayout;
    private  ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private TextView mEmptyTxtV;
    private  Thread mThreadProducts;
    private ArrayList<Product> mProductArr,mFilterProdutArr;
    private UIHelper mUIHelper;
    private String mSubCategoryIdStr="",mProductStr="";
    private Bundle mBundle ;
    private ViewPagerAdapter adapter;
    private ProgressDialog mProgressDialog;
    private ProductFragment mProductFragment;
    private List<Fragment> mFragmentList;
    private Intent mIntent;
    public ProductActivity(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        //View ID
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mEmptyTxtV = (TextView) findViewById(R.id.empty);


        //Object Initialization
        mCategory = new ArrayList<>();
        mProductArr = new ArrayList<>();
        mFilterProdutArr = new ArrayList<>();
        mFragmentList = new ArrayList<>();
        mBundle = new Bundle();
        mIntent = new Intent();
        mUIHelper = new UIHelper(ProductActivity.this);
        //set Title
        mTitle.setText(getResources().getString(R.string.title_activity_Product));

        mBundle = getIntent().getExtras();
        if (mBundle != null){
            //Extract the data…
            mCategory = (ArrayList<Category>) mBundle.getSerializable(StringUtils.EXTRA_CATEGORY);
            mSubCategoryIdStr = mBundle.getString(StringUtils.EXTRA_CATEGORY_ID);
            if (mBundle.containsKey(StringUtils.EXTRA_PRODUCT)){
                mProductStr = mBundle.getString(StringUtils.EXTRA_PRODUCT);
            }
        }
        HashMap<String, String> userDetails = mSession.getUserDetails();
        final String userId = userDetails.get(PREF_SESSION_USER_ID);
        final String password = userDetails.get(PREF_SESSION_PASSWORD);
       // Log.v("mCategory","--->"+mCategory.size());
        //Log.v("mSubCategoryIdStr","--->"+mSubCategoryIdStr);
     /*  if(mCategory.size()>0){
           String categoryId = mCategory.get(0).getId();
           Parent.setCategoryId(categoryId);
           Parent.setSubCategoryId(mSubCategoryIdStr);
           setupViewPager(mViewPager);
           mTabLayout.setupWithViewPager(mViewPager);
           showViews(true,mTabLayout);
           showViews(false,mEmptyTxtV);
        }else{
           showViews(false,mEmptyTxtV);
           showViews(false,mTabLayout);
           Parent.setCategoryId(mSubCategoryIdStr);
           Parent.setSubCategoryId("");
           mProductFragment = new ProductFragment();
           mFragmentList.add(mProductFragment);
           adapter = new ViewPagerAdapter(getSupportFragmentManager(),mFragmentList);
           mViewPager.setAdapter(adapter);
       }*/
        Log.v("mProductStr","--->"+mProductStr);
        if (mProductStr != null && !mProductStr.isEmpty()) {
            showViews(false, mEmptyTxtV);
            showViews(false, mTabLayout);
            Parent.setCategoryId("");
            Parent.setSubCategoryId("");
            mProductFragment = new ProductFragment();
            mFragmentList.add(mProductFragment);
            adapter = new ViewPagerAdapter(getSupportFragmentManager(), mFragmentList);
            mViewPager.setAdapter(adapter);
        } else {
            Log.v("mCategory", "--->" + mCategory.size());
            Log.v("mSubCategoryIdStr", "--->" + mSubCategoryIdStr);
            if (mCategory.size() > 0) {
                String categoryId = mCategory.get(0).getId();
                Parent.setCategoryId(categoryId);
                Parent.setSubCategoryId(mSubCategoryIdStr);
                setupViewPager(mViewPager);
                mTabLayout.setupWithViewPager(mViewPager);
                showViews(true, mTabLayout);
                showViews(false, mEmptyTxtV);
            } else {
                showViews(false, mEmptyTxtV);
                showViews(false, mTabLayout);
                Parent.setCategoryId(mSubCategoryIdStr);
                Parent.setSubCategoryId("");
                mProductFragment = new ProductFragment();
                mFragmentList.add(mProductFragment);
                adapter = new ViewPagerAdapter(getSupportFragmentManager(), mFragmentList);
                mViewPager.setAdapter(adapter);
            }
        }

        ((ImageView)findViewById(R.id.bottom_product_img)).setImageResource(R.mipmap.ic_action_product_active);
        ((TextView)findViewById(R.id.bottom_product_txt)).setTextColor(getResources().getColor(android.R.color.white));


        findViewById(R.id.home_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIntent.setClass(ProductActivity.this, HomeActivity.class);               ;
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
            }
        });
        findViewById(R.id.myorder_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(userId!=null && !userId.isEmpty() && password!=null && !password.isEmpty()) {
                    mIntent.setClass(ProductActivity.this, MyOrderActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mIntent);

                }else{
                    mIntent.setClass(ProductActivity.this, LoginActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mIntent);

                }

            }
        });
        findViewById(R.id.myaccount_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(userId!=null && !userId.isEmpty() && password!=null && !password.isEmpty()) {
                    mIntent.setClass(ProductActivity.this, MyAccountActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mIntent);

                }else{
                    mIntent.setClass(ProductActivity.this, LoginActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mIntent);

                }
            }
        });
        findViewById(R.id.registration_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIntent.setClass(ProductActivity.this, RegistrationActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
            }
        });

    }
    private void setupViewPager(final ViewPager mViewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        for (int i = 0; i < mCategory.size(); i++) {
            Category category = mCategory.get(i);
            String categoryName = category.getName();
            adapter.addFrag(new ProductFragment(), categoryName);
        }
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(0);
        mViewPager.setOffscreenPageLimit(mCategory.size());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                Fragment fragment = adapter.getFragment(position);
                if (fragment != null) {
                    mViewPager.setCurrentItem(position);
                    String categoryId = mCategory.get(position).getId();
                    Parent.setCategoryId(categoryId);
                    Parent.setSubCategoryId(mSubCategoryIdStr);
                    String category = Parent.getCategoryId();
                    String subCategory = Parent.getSubCategoryId();
                    Log.v("category","onResume-->"+category);
                    Log.v("subCategory","onResume--->"+subCategory);
                    Parent.setIsLoading(true);
                    Log.d("onResume","onResume");
                    fragment.onResume();
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }
    public void cartBadgeCount(Activity activity){
        try {
            DBHelper dbHelper = new DBHelper(activity);
            Cursor cursor = dbHelper.getProductCursor();
            int cartItemCount =cursor.getCount();
            Log.d("cartSize", cartItemCount + "");
            LayerDrawable icon = (LayerDrawable) item.getIcon();
            Utils.setBadgeCount(activity, icon, cartItemCount);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void onResume(){
        super.onResume();
        cartBadgeCount(ProductActivity.this);
    }
}
