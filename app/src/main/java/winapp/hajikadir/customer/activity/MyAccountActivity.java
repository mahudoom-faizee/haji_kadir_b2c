package winapp.hajikadir.customer.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.config.StringUtils;
import winapp.hajikadir.customer.helper.SessionManager;
import winapp.hajikadir.customer.helper.SettingsManager;
import winapp.hajikadir.customer.util.Constants;


public class MyAccountActivity extends AppCompatActivity implements Constants {
    private Intent mIntent;
    private SessionManager mSession;
    private SettingsManager mSettings;
    private Toolbar toolbar;
    private ActionBar mActionBar;
    private TextView mlblTitle,mlblCartNoOfItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
           // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mActionBar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.custom_abs_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.LEFT);

        mlblTitle= (TextView) viewActionBar.findViewById(R.id.title);
        mlblCartNoOfItem= (TextView) viewActionBar.findViewById(R.id.item);

        mlblCartNoOfItem.setVisibility(View.GONE);
        mlblTitle.setText(getResources().getString(R.string.title_activity_my_account));
        mActionBar.setCustomView(viewActionBar, params);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        //abar.setIcon(R.color.transparent);
        mActionBar.setHomeButtonEnabled(true);


        mSession = new SessionManager(MyAccountActivity.this);
        mSettings = new SettingsManager(MyAccountActivity.this);
        mIntent = new Intent();

        HashMap<String, String> userDetails = mSettings.getUserInfo();
        String userId = userDetails.get(PREF_USER_ID);
        String firstName = userDetails.get(PREF_FIRST_NAME);
        String lastName = userDetails.get(PREF_LAST_NAME);
        String address1 = userDetails.get(PREF_ADDRESS1);
        String address2 = userDetails.get(PREF_ADDRESS2);
        String phoneNo = userDetails.get(PREF_PHONENO);
        String city = userDetails.get(PREF_CITY);
        String pincode = userDetails.get(PREF_PINCODE);

        ((TextView) findViewById(R.id.name)).setText(firstName);
        ((TextView) findViewById(R.id.emailId)).setText(userId);
        ((TextView) findViewById(R.id.phoneNo)).setText(phoneNo);


/*

        if (address1 != null && !address1.isEmpty() && address2 != null
                && !address2.isEmpty()) {
            ((TextView) findViewById(R.id.address)).setText(address1 + "\n" + address2 + "\n" + pincode
                   );
        }

        else if(address2 != null && !address2.isEmpty()

                && (address1 == null || address1.trim().equals(""))){
            ((TextView) findViewById(R.id.address)).setText(address2 + "\n" + pincode);
        }
        else if(address1 != null && !address1.isEmpty()

                && (address2 == null || address2.trim().equals(""))){
            ((TextView) findViewById(R.id.address)).setText(address1 + "\n" + pincode);
        }
        else if (address1 != null && !address1.isEmpty()
                && (address2 == null || address2.trim().equals(""))
               ) {
            ((TextView) findViewById(R.id.address)).setText(address1 + "\n" + pincode);
        } else if (address2 != null && !address2.isEmpty()
                && (address1 == null || address1.trim().equals(""))
              ) {
            ((TextView) findViewById(R.id.address)).setText(address2 + "\n" + pincode);
        }*/
        ((TextView) findViewById(R.id.address1)).setText(address1);
        ((TextView) findViewById(R.id.address2)).setText(address2);
        ((TextView) findViewById(R.id.postalcode)).setText(pincode);

        ((ImageView)findViewById(R.id.bottom_myaccount_img)).setImageResource(R.mipmap.ic_action_myorder_active);
        ((TextView)findViewById(R.id.bottom_myaccount_txt)).setTextColor(getResources().getColor(android.R.color.white));


        findViewById(R.id.home_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIntent.setClass(MyAccountActivity.this, HomeActivity.class);               ;
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
            }
        });
    findViewById(R.id.product_layout).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mIntent.setClass(MyAccountActivity.this, ProductActivity.class);
            mIntent.putExtra(StringUtils.EXTRA_CATEGORY_ID,"");
            mIntent.putExtra(StringUtils.EXTRA_PRODUCT, "Product");
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mIntent);
        }
    });
    findViewById(R.id.myorder_layout).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mIntent.setClass(MyAccountActivity.this, MyOrderActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mIntent);
        }
    });

    findViewById(R.id.registration_layout).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mIntent.setClass(MyAccountActivity.this, RegistrationActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mIntent);
        }
    });



findViewById(R.id.viewALlOrder).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
            mIntent.setClass(MyAccountActivity.this, MyOrderActivity.class);
            startActivity(mIntent);

    }
});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
        menu.findItem(R.id.NewRegistartion).setVisible(false);
        menu.findItem(R.id.Home).setVisible(false);
        menu.findItem(R.id.MyOrders).setVisible(false);
        menu.findItem(R.id.Login).setVisible(false);
        menu.findItem(R.id.SignOut).setVisible(false);
        menu.findItem(R.id.UserName).setVisible(false);
        menu.findItem(R.id.Search).setVisible(false);
        menu.findItem(R.id.MyCart).setVisible(false);
        menu.findItem(R.id.Products).setVisible(false);
        menu.findItem(R.id.Promotions).setVisible(false);
        menu.findItem(R.id.AboutUs).setVisible(false);
        menu.findItem(R.id.ContactUs).setVisible(false);
        menu.findItem(R.id.PrivacyPolicy).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void onBackPressed() {
            finish(); // finish activity

    }
}
