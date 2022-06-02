package winapp.hajikadir.customer.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.config.StringUtils;

/**
 * Created by user on 06-Feb-17.
 */

public class ContactUsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private LinearLayout mPhoneNoLayout;
    // Google Map
    private TextView mPhoneNo;
    private Intent mIntent;;
    private GoogleMap mMap;
    private double mLatitude = 0, mLongitude = 0,dLatitude=0,dLongitude=0;
    private MarkerOptions marker1;
    private MarkerOptions marker2;
    private ActionBar mActionBar;
    private TextView mlblTitle,mlblCartNoOfItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        mActionBar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.custom_abs_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.LEFT);

        mlblTitle= (TextView) viewActionBar.findViewById(R.id.title);
        mlblCartNoOfItem= (TextView) viewActionBar.findViewById(R.id.item);
        mlblTitle.setText(getResources().getString(R.string.title_activity_contact_us));
        mlblCartNoOfItem.setVisibility(View.GONE);
        mActionBar.setCustomView(viewActionBar, params);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        //abar.setIcon(R.color.transparent);
        mActionBar.setHomeButtonEnabled(true);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mIntent = new Intent();
        /*mPhoneNoLayout = (LinearLayout) findViewById(R.id.phoneno_layout);
        mPhoneNo = (TextView) findViewById(R.id.textView3);
        mPhoneNo.setText("+65123456");
        mPhoneNoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phonenum = mPhoneNo.getText().toString();
                dialContactPhone(phonenum);
            }
        });*/


    }

    private void dialContactPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
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
        menu.findItem(R.id.AboutUs).setVisible(true);
        menu.findItem(R.id.ContactUs).setVisible(false);
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
            case R.id.PrivacyPolicy:
                mIntent.setClass(this, PrivacyPolicyActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        try {
            mMap = googleMap;
            mLatitude = 1.303133;
            mLongitude = 103.863853;


            dLatitude = 1.347934;
            dLongitude = 103.936138;

            // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
            // Changing map type
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //  googleMap.setMyLocationEnabled(true);
            } else {
                //  Toast.makeText(ContactUsActivity.this, "You have to accept to enjoy all app's services!", Toast.LENGTH_LONG).show();
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    //    googleMap.setMyLocationEnabled(true);
                }
            }
            // Showing / hiding your current location
            googleMap.setMyLocationEnabled(false);

            // Enable / Disable zooming controls
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            // Enable / Disable my location button
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            // Enable / Disable Compass icon
            googleMap.getUiSettings().setCompassEnabled(true);

            // Enable / Disable Rotate gesture
            googleMap.getUiSettings().setRotateGesturesEnabled(true);

            // Enable / Disable zooming functionality
            googleMap.getUiSettings().setZoomGesturesEnabled(true);

            //Disable Map Toolbar:
            googleMap.getUiSettings().setMapToolbarEnabled(false);

            // Adding a marker
            marker1 = new MarkerOptions().position(
                    new LatLng(mLatitude, mLongitude))
                    .title("HAJI KADIR FOOD CHAIN PTE LTD - GOLDEN MILE FOOD");
            marker1.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));


            marker2 = new MarkerOptions().position(
                    new LatLng(dLatitude, dLongitude))
                    .title("HAJI KADIR FOOD CHAIN PTE LTD- TAMPINES");
            marker2.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_RED));


            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {

                    googleMap.addMarker(marker1).showInfoWindow();
                    googleMap.addMarker(marker2).showInfoWindow();


                }
            });

            CameraPosition camPos = new CameraPosition.Builder()
                    .target(getCenterCoordinate())
                    .zoom(11)
                    .build();
            CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
            mMap.animateCamera(camUpd3);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LatLng getCenterCoordinate() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(mLatitude, mLongitude));
        LatLngBounds bounds = builder.build();
        return bounds.getCenter();
    }
}