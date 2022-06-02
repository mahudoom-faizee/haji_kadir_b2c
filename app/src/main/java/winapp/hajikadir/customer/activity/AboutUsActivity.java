package winapp.hajikadir.customer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.config.StringUtils;


public class AboutUsActivity extends AppCompatActivity {
    private ActionBar mActionBar;
    private TextView mlblTitle,mlblCartNoOfItem;
    private String aboutUs = "haji kadir food chains was very famous in golden mile food centre for its succulent taste of the secialized soup tulang. the attractive fiery-red colour makes you wanted more and customers enjoy sucking the bone marrow so much. most of the food magazines even elected us as the best soup tulang in singapore. our soup tulang was originated by my father abdul kadir in the 50's. until today, i still follows closely to my father’s recipe. customers love our soup tulang because the exterior meat and tendon are soft and aromatic while the interior bone marrow is tender and smooth. eating soup tulang actually require some skills, otherwise it will dirty your clothes and putting yourself like a clown. but this is the best integration into the local food culture as it was the best self experience. soup tulang was so popular because people think it helps in nourishing both male and female reproductive system. apart from the soup tulang, other famous dishes are mutton chop and mutton bistik, both taste absolutely good, you really got to try all";
    private Intent mIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        mActionBar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.custom_abs_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.LEFT);

        mlblTitle= (TextView) viewActionBar.findViewById(R.id.title);
        mlblCartNoOfItem= (TextView) viewActionBar.findViewById(R.id.item);
        mlblTitle.setText(getResources().getString(R.string.title_activity_about_us));
        mlblCartNoOfItem.setVisibility(View.GONE);
        mActionBar.setCustomView(viewActionBar, params);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        //abar.setIcon(R.color.transparent);
        mActionBar.setHomeButtonEnabled(true);


        ((TextView) findViewById(R.id.aboutUs)).setText(aboutUs);

        mIntent = new Intent();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
        menu.findItem(R.id.NewRegistartion).setVisible(false);
        menu.findItem(R.id.Login).setVisible(false);
        menu.findItem(R.id.SignOut).setVisible(false);
        menu.findItem(R.id.UserName).setVisible(false);
        menu.findItem(R.id.Search).setVisible(false);
        menu.findItem(R.id.MyCart).setVisible(false);
        menu.findItem(R.id.MyOrders).setVisible(false);
        menu.findItem(R.id.Products).setVisible(true);
        menu.findItem(R.id.Promotions).setVisible(false);
        menu.findItem(R.id.AboutUs).setVisible(false);
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
}
