package winapp.hajikadir.customer.activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.adapter.CartAdapter;
import winapp.hajikadir.customer.config.StringUtils;
import winapp.hajikadir.customer.helper.DBHelper;
import winapp.hajikadir.customer.helper.SessionManager;
import winapp.hajikadir.customer.helper.SettingsManager;
import winapp.hajikadir.customer.model.Product;
import winapp.hajikadir.customer.util.Constants;
import winapp.hajikadir.customer.util.Utils;

public class CartActivity extends AppCompatActivity implements Constants {
    private CartAdapter dataAdapter = null;
    private Double orderTotal = 0.00;
    private ArrayList<Product> productList;
    private TextView mlblGrandTotal,mlblSubTotal,mlblServiceCharge,mlblCartNoOfItem,mlblTitle;
    private Intent mIntent;
    private SettingsManager mSettings;
    private SessionManager mSession;
    private  ListView listView;
    public DBHelper mDBHelper;
    public static MenuItem item;
    private ActionBar mActionBar;
    private Double dServiceTax = 0.00;
    public CartActivity(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

       // getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        //Customize the ActionBar
        mActionBar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.custom_abs_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.LEFT);

        mlblTitle= (TextView) viewActionBar.findViewById(R.id.title);
        mlblCartNoOfItem= (TextView) viewActionBar.findViewById(R.id.item);


        mlblTitle.setText(getResources().getString(R.string.title_activity_my_cart));
        mActionBar.setCustomView(viewActionBar, params);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        //abar.setIcon(R.color.transparent);
        mActionBar.setHomeButtonEnabled(true);


       // getSupportActionBar().setCustomView(R.layout.custom_abs_layout);
      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mlblServiceCharge = (TextView) findViewById(R.id.lblServiceCharge);
        mlblSubTotal = (TextView) findViewById(R.id.lblSubTotal);
        mlblGrandTotal = (TextView) findViewById(R.id.lblGrandTotal);
        listView = (ListView) findViewById(R.id.listView1);
        mDBHelper = new DBHelper(CartActivity.this);
        productList = new ArrayList<Product>();
        productList = mDBHelper.products(null);
        mIntent = new Intent();
        mSettings = new SettingsManager(this);
        mSession = new SessionManager(this);


        Cursor cursor = mDBHelper.getProductCursor();
        Log.d("cartSize 1 ", cursor.getCount() + "");
        mlblCartNoOfItem.setText(""+cursor.getCount());

       // dServiceTax = mDBHelper.getServiceTexTotal();

      //  mlblServiceCharge.setText(Utils.twoDecimalPoint(dServiceTax));
       // cartBadgeCount(CartActivity.this);
        //create an ArrayAdaptar from the String Array
        dataAdapter = new CartAdapter(CartActivity.this, R.layout.item_list_cart, productList,mlblCartNoOfItem,mlblSubTotal,mlblServiceCharge,mlblGrandTotal);

        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);

        findViewById(R.id.btnCheckOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int item = mDBHelper.getCount();
                if(item>0){
                   mIntent.setClass(CartActivity.this, DeliveryAddressActivity.class);
                   startActivity(mIntent);
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
        menu.findItem(R.id.MyCart).setVisible(false);
        menu.findItem(R.id.NewRegistartion).setVisible(false);
        menu.findItem(R.id.Login).setVisible(false);
        menu.findItem(R.id.SignOut).setVisible(false);
        menu.findItem(R.id.UserName).setVisible(false);
        menu.findItem(R.id.Search).setVisible(false);

        menu.findItem(R.id.Home).setVisible(true);
        menu.findItem(R.id.MyOrders).setVisible(false);
        menu.findItem(R.id.Products).setVisible(true);
        menu.findItem(R.id.Promotions).setVisible(false);
        menu.findItem(R.id.AboutUs).setVisible(true);
        menu.findItem(R.id.ContactUs).setVisible(true);
        menu.findItem(R.id.PrivacyPolicy).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.Home:
                mIntent.setClass(this, HomeActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
                return true;
            case R.id.Products:
                mIntent.setClass(this, ProductActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mIntent.putExtra(StringUtils.EXTRA_CATEGORY_ID,"");
                mIntent.putExtra(StringUtils.EXTRA_PRODUCT, "Product");
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

/*    private Drawable buildCounterDrawable(int count, int backgroundImageId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.counter_menuitem_layout, null);
        view.setBackgroundResource(backgroundImageId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setText("" + count);
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }*/
}
