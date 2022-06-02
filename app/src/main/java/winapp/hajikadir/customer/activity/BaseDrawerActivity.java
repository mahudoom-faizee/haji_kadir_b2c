package winapp.hajikadir.customer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.LayoutRes;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.support.design.widget.NavigationView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.adapter.SlideMenuRecyclerViewAdapter;
import winapp.hajikadir.customer.config.StringUtils;
import winapp.hajikadir.customer.helper.DBHelper;
import winapp.hajikadir.customer.helper.SessionManager;
import winapp.hajikadir.customer.helper.SettingsManager;
import winapp.hajikadir.customer.model.Category;
import winapp.hajikadir.customer.model.Notification;
import winapp.hajikadir.customer.notification.NotificationActivity;
import winapp.hajikadir.customer.util.Constants;
import winapp.hajikadir.customer.util.RecyclerItemClickListener;
import winapp.hajikadir.customer.util.Utils;

/**
 * Created by user on 14-Jul-16.
 */
public class BaseDrawerActivity extends AppCompatActivity implements /*NavigationView.OnNavigationItemSelectedListener,*/ Constants {
    private NavigationView navigationView;
    private DrawerLayout fullLayout;
    public Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private int selectedNavItemId;
    private Intent mIntent;
    public TextView mTitle;
    public AppBarLayout appBarLayout;
    public RecyclerView mBaseRecyclerView;
    public HomeActivity mHomeActivity;
    public DBHelper dbHelper;
    private SettingsManager mSettings;
    public SessionManager mSession;
    public ArrayList<Category> mCategoryArr,mSubCategoryArr,mCategoryAllArr;
    public SlideMenuRecyclerViewAdapter mSlideMenuRecyclerViewAdapter;
    public static MenuItem item;
    public  AlertDialog alertDialog;
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        /**
         * This is going to be our actual root layout.
         */
        fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base_drawer, null);
        /**
         * {@link FrameLayout} to inflate the child's view. We could also use a {@link android.view.ViewStub}
         */
        FrameLayout activityContainer = (FrameLayout) fullLayout.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        mBaseRecyclerView = (RecyclerView) fullLayout.findViewById(R.id.recycler_view);
        /**
         * Note that we don't pass the child's layoutId to the parent,
         * instead we pass it our inflated layout.
         */
        super.setContentView(fullLayout);

        //Object Initializaion
        mIntent = new Intent();
        mHomeActivity = new HomeActivity();
        mSettings = new SettingsManager(this);
        mSession = new SessionManager(this);
        dbHelper = new DBHelper(this);
        mCategoryArr = new ArrayList<>();
        mSubCategoryArr = new ArrayList<>();
        mCategoryAllArr = new ArrayList<>();
        mSlideMenuRecyclerViewAdapter = new SlideMenuRecyclerViewAdapter();
         appBarLayout = (AppBarLayout)findViewById(R.id.appBarLayout);
       if(this instanceof HomeActivity){
             toolbar = (Toolbar) findViewById(R.id.toolbar1);
             mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title1);
            showViews(false,R.id.toolbar);
            showViews(true,R.id.toolbar1);
            showViews(false,R.id.toolbar_title);
            showViews(true,R.id.toolbar_title1);
        }else{
            toolbar = (Toolbar) findViewById(R.id.toolbar);
             mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            showViews(true,R.id.toolbar);
            showViews(false,R.id.toolbar1);
           showViews(true,R.id.toolbar_title);
            showViews(false,R.id.toolbar_title1);
       }
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        appBarLayout.setExpanded(false, true);

        if (useToolbar()) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        } else {
           // toolbar.setVisibility(View.GONE);
            showViews(false,toolbar);
        }

        setUpNavView();
        toolbar.setNavigationIcon(R.mipmap.ic_action_hamburger);

        navigationView.setItemIconTintList(null);

        RecyclerView.LayoutManager mLayoutManager3 = new LinearLayoutManager(getApplicationContext());
        mBaseRecyclerView.setLayoutManager(mLayoutManager3);
        mBaseRecyclerView.setHasFixedSize(true);

        mBaseRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mCategoryArr.clear();
        mCategoryAllArr.clear();
        mCategoryArr = dbHelper.category();
        mCategoryAllArr = dbHelper.categoryAll();
        mSlideMenuRecyclerViewAdapter = new SlideMenuRecyclerViewAdapter(this,mCategoryArr);
        mBaseRecyclerView.setAdapter(mSlideMenuRecyclerViewAdapter);
        //  mBaseRecyclerView.setAdapter(mMenuRecyclerViewAdapter);
        //  mMenuRecyclerViewAdapter.setOnItemClickListener(this);
        //  mSlideMenuRecyclerViewAdapter.setOnItemClickListener(this);


        Log.d("mCategoryArr","-bs->"+mCategoryArr.size());
        mBaseRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        try {
                            mCategoryArr.clear();
                            mCategoryArr = dbHelper.category();
                            fullLayout.closeDrawer(GravityCompat.START);
                            mSubCategoryArr.clear();
                            String categoryId = mCategoryArr.get(position).getId();
                            mGetSubCategory(categoryId);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                })
        );
    }

    /**
     * Helper method that can be used by child classes to
     * specify that they don't want a {@link Toolbar}
     *
     * @return true
     */
    protected boolean useToolbar() {
        return true;
    }

    protected void setUpNavView() {
      //  navigationView.setNavigationItemSelectedListener(this);
        if (useDrawerToggle()) { // use the hamburger menu
            drawerToggle = new ActionBarDrawerToggle(this, fullLayout, toolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);

            fullLayout.setDrawerListener(drawerToggle);
            drawerToggle.syncState();
        } else if (useToolbar() && getSupportActionBar() != null) {
            // Use home/back button instead
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(getResources()
                    .getDrawable(R.drawable.abc_ic_ab_back_material));
        }
    }

    /**
     * Helper method to allow child classes to opt-out of having the
     * hamburger menu.
     *
     * @return
     */
    protected boolean useDrawerToggle() {
        return true;
    }


    public boolean onNavigationItemSelected(MenuItem menuItem) {
        fullLayout.closeDrawer(GravityCompat.START);
        selectedNavItemId = menuItem.getItemId();
        switch (selectedNavItemId) {
            case R.id.nav_menu:
                mIntent.setClass(this, HomeActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
                finish();
                return true;




        }
        return onOptionsItemSelected(menuItem);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
        if(this instanceof HomeActivity){
            menu.findItem(R.id.Location).setVisible(true);
        }
        if(this instanceof ProductActivity){
            menu.findItem(R.id.Products).setVisible(false);
        }
        item = menu.findItem(R.id.MyCart);
        menu.findItem(R.id.Home).setVisible(false);
        menu.findItem(R.id.MyOrders).setVisible(false);
        Cursor cursor = dbHelper.getProductCursor();
        int cartItemCount =cursor.getCount();
        LayerDrawable icon = (LayerDrawable) item.getIcon();
        Utils.setBadgeCount(this, icon, cartItemCount);
        menu.findItem(R.id.NewRegistartion).setVisible(false);
        HashMap<String, String> userDetails = mSession.getUserDetails();
        String userName = userDetails.get(PREF_SESSION_USER_NAME);
        Log.d("userName","-->"+userName);
        if(userName!=null && !userName.isEmpty()){
            menu.findItem(R.id.Login).setVisible(false);
            menu.findItem(R.id.SignOut).setVisible(true);
            menu.findItem(R.id.UserName).setVisible(true);
            menu.findItem(R.id.UserName).setTitle(userName);
        }else{
            menu.findItem(R.id.Login).setVisible(true);
            menu.findItem(R.id.SignOut).setVisible(false);
            menu.findItem(R.id.UserName).setVisible(false);

        }
        item.setVisible(true);
        if(this instanceof HomeActivity){
            menu.findItem(R.id.Search).setVisible(false);
        }else{
            menu.findItem(R.id.Search).setVisible(true);
        }

        if(this instanceof NotificationActivity){
            menu.findItem(R.id.Search).setVisible(false);
            menu.findItem(R.id.MyCart).setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.UserName:
                // TODO Something when menu item selected
                mIntent.setClass(this, MyAccountActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
                //  finish();
                return true;
            case R.id.Location:
                mHomeActivity.mGetLocation(this,true);
                return true;

            case R.id.Login:
                // TODO Something when menu item selected
                mIntent.setClass(this, LoginActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
                return true;
            case R.id.MyCart:
                // TODO Something when menu item selected
                HashMap<String, String> userDetails = mSession.getUserDetails();
                String userId = userDetails.get(PREF_SESSION_USER_ID);
                String password = userDetails.get(PREF_SESSION_PASSWORD);
                Log.d("userId","-->"+userId);
                Log.d("password","-->"+password);
               if(userId!=null && !userId.isEmpty() && password!=null && !password.isEmpty()) {
                    mIntent.setClass(this, CartActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mIntent);
                }else{
                    mIntent.setClass(this, LoginActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mIntent);

               }
                return true;
            case R.id.Products:
                mIntent.setClass(this, ProductActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mIntent.putExtra(StringUtils.EXTRA_CATEGORY_ID,"");
                mIntent.putExtra(StringUtils.EXTRA_PRODUCT, "Product");
                startActivity(mIntent);
                return true;
            case R.id.Search:
                // TODO Something when menu item selected
                mIntent.setClass(this, SearchActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
                return true;
            case R.id.Notification:
                // TODO Something when menu item selected
                mIntent.setClass(this, NotificationActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
                return true;
            case R.id.Promotions:
                // TODO Something when menu item selected
                return true;

            case R.id.AboutUs:
                // TODO Something when menu item selected
                mIntent.setClass(this, AboutUsActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
                return true;
            case R.id.ContactUs:
                // TODO Something when menu item selected
                mIntent.setClass(this, ContactUsActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);

                return true;
            case R.id.PrivacyPolicy:
                mIntent.setClass(this, PrivacyPolicyActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
                return true;
            case R.id.SignOut:
                // TODO Something when menu item selected
                mSession.clearLoginSession();
                mSettings.clearLoginSession();
                dbHelper.deleteAllProduct();
                startActivity(getIntent());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void showViews(boolean show, View... views) {
        int visibility = show ? View.VISIBLE : View.GONE;
        for (View view : views) {
            view.setVisibility(visibility);
        }
    }
    public  void inVisibleViews(boolean show, View... views) {
        int visibility = show ? View.VISIBLE : View.INVISIBLE;
        for (View view : views) {
            view.setVisibility(visibility);
        }
    }
    public void showViews(boolean show, int... resId) {
        int visibility = show ? View.VISIBLE : View.GONE;
        for (int id : resId) {
            findViewById(id).setVisibility(visibility);
        }
    }
    public void mGetSubCategory(String categoryId) {
        Log.d("categoryId","-bd->"+categoryId);
        if(categoryId.equals("All Products")){
            mIntent.setClass(this, ProductActivity.class);
            mIntent.putExtra(StringUtils.EXTRA_CATEGORY_ID,"");
            mIntent.putExtra(StringUtils.EXTRA_PRODUCT, "Product");
            startActivity(mIntent);
            finish();
        }else {
            if (mCategoryAllArr.size() > 0) {
                for (int i = 0; i < mCategoryAllArr.size(); i++) {
                    Category mCat = mCategoryAllArr.get(i);
                    String parentId = mCat.getParentId();
                    Log.d("parentIdiiii", "-bd->" + parentId);
                    if (categoryId.matches(parentId)) {
                        Log.d("parentIdiiii", "bf-matches->" + parentId);
                        mSubCategoryArr.add(mCat);
                    }
                }
                Log.d("mSubCategoryArr", "-bd->" + mSubCategoryArr.size());
                mIntent.setClass(this, ProductActivity.class);
                mIntent.putExtra(StringUtils.EXTRA_CATEGORY_ID, categoryId);
                mIntent.putParcelableArrayListExtra(StringUtils.EXTRA_CATEGORY, mSubCategoryArr);
                startActivity(mIntent);
                finish();


            }
        }
    }
    /*public void onResume(){
        super.onResume();
        try{
            Cursor cursor = dbHelper.getProductCursor();
            int cartItemCount = cursor.getCount();
             if(item!=null){
            LayerDrawable icon = (LayerDrawable) item.getIcon();
            Utils.setBadgeCount(this, icon, cartItemCount);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
*/
  /*  public void CreateAlertDialogWithRadioButtonGroup(){
        final int currentChoice = mSession.getCurrentChoice();
        CharSequence[] values = {" First Item "," Second Item "};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Location");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(values, currentChoice, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                switch(item)
                {
                    case 0:
                        mSession.putCurrentChoice(0);
                        Toast.makeText(getApplicationContext(), "First Item Clicked", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        mSession.putCurrentChoice(1);
                        Toast.makeText(getApplicationContext(), "Second Item Clicked", Toast.LENGTH_LONG).show();
                        break;
                }
                alertDialog.dismiss();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();

    }*/
}