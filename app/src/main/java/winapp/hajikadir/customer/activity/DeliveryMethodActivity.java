package winapp.hajikadir.customer.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.config.StringUtils;
import winapp.hajikadir.customer.helper.DBHelper;
import winapp.hajikadir.customer.helper.ProgressDialog;
import winapp.hajikadir.customer.helper.RestClientAcessTask;
import winapp.hajikadir.customer.helper.SessionManager;
import winapp.hajikadir.customer.helper.SettingsManager;
import winapp.hajikadir.customer.helper.UIHelper;
import winapp.hajikadir.customer.model.Branch;
import winapp.hajikadir.customer.util.Constants;
import winapp.hajikadir.customer.util.Utils;

public class DeliveryMethodActivity extends AppCompatActivity implements DialogInterface.OnCancelListener,Constants {
    private TextView mlblCartNoOfItem,mlblTitle;
    private Intent mIntent;
    private SettingsManager mSettings;
    private SessionManager mSession;
    public DBHelper mDBHelper;
    private ActionBar mActionBar;
    private Thread thrdGetDistance,thrdGetThirdPartyDeliveryCharge,thrdLateNightCharge;
    private UIHelper mUIHelper;
    private ProgressDialog mProgressDialog;
    private Animation mAnimFadeIn;
    private RadioButton mDeliveryChargeRdBtn,mPickUpFromShopRdBtn;
    private double branch_lat;
    private double branch_lon;
    private String mPostalCode="",mBranchId="",mFlag = "", deliveryCharges="",cus_address1="",cus_address2="",address="",customerId="",cus_city="",
            cus_postCode="";
    Geocoder geocoder = null;
    List<Address> addressList;
    private Calendar cal;
    DateFormat timeFormat;
    String currentTime="",lateCharge ="",value ="",delivery_amt="",shop="";
    double late_charge,deli_amt;
    Cursor holidayCursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_method);

        //Customize the ActionBar
        mActionBar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.custom_abs_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.LEFT);

        mlblTitle= (TextView) viewActionBar.findViewById(R.id.title);
        mlblCartNoOfItem= (TextView) viewActionBar.findViewById(R.id.item);


        mlblCartNoOfItem.setVisibility(View.GONE);
        mlblTitle.setText(getResources().getString(R.string.title_activity_delivery_method));
        mActionBar.setCustomView(viewActionBar, params);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        //abar.setIcon(R.color.transparent);
        mActionBar.setHomeButtonEnabled(true);
        cal = Calendar.getInstance();
        timeFormat = new SimpleDateFormat("hh:mm a");
        mDBHelper =new DBHelper(DeliveryMethodActivity.this);
        mDeliveryChargeRdBtn = (RadioButton) findViewById(R.id.radioButton1);
        mPickUpFromShopRdBtn = (RadioButton) findViewById(R.id.radioButton2);


        mIntent = new Intent();
        mUIHelper = new UIHelper(DeliveryMethodActivity.this);
        mSession = new SessionManager(DeliveryMethodActivity.this);
        mSettings = new SettingsManager(DeliveryMethodActivity.this);
        mProgressDialog = ProgressDialog.show(DeliveryMethodActivity.this, "Loading...", true, true, this);
        mAnimFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in2);
        mAnimFadeIn = new AlphaAnimation(1.0f, 0.0f);
        mAnimFadeIn.setDuration(1000);
        mAnimFadeIn.setStartOffset(3000);
        geocoder = new Geocoder(this);
        addressList=new ArrayList<Address>();


                HashMap<String, String> userDetails = mSettings.getUserInfo();

        Log.d("firstName",""+userDetails.get(PREF_FIRST_NAME));
        Log.d("lastName",""+userDetails.get(PREF_LAST_NAME));
        Log.d("address1",""+userDetails.get(PREF_ADDRESS1));
        Log.d("address2",""+userDetails.get(PREF_ADDRESS2));
        Log.d("phoneNo",""+userDetails.get(PREF_PHONENO));
        Log.d("postalCode",""+userDetails.get(PREF_PINCODE));




        HashMap<String, String> branchDetails = mSession.geBranchInfo();
        String branchId = branchDetails.get(PREF_BRANCH_ID);
        String branchName = branchDetails.get(PREF_BRANCH_NAME);
        String address1 = branchDetails.get(PREF_BRANCH_ADDRESS1);
        String address2 = branchDetails.get(PREF_BRANCH_ADDRESS2);
        String phoneNo = branchDetails.get(PREF_BRANCH_PHONENO);
        String pincode = branchDetails.get(PREF_BRANCH_PINCODE);
        String country = branchDetails.get(PREF_BRANCH_COUNTRY);
        String latitude = branchDetails.get(PREF_BRANCH_LATITUDE);
        String longitude = branchDetails.get(PREF_BRANCH_LONGITUDE);
        branch_lat=Double.parseDouble(latitude);
        branch_lon=Double.parseDouble(longitude);

        Log.d("BRANCH_LATITUDE","-->"+branch_lat);
        Log.d("BRANCH_LATITUDE","-->"+branch_lon);

        ((TextView) findViewById(R.id.name)).setText(branchName);
        ((TextView) findViewById(R.id.address1)).setText(address1);
       // ((TextView) findViewById(R.id.address2)).setText(address2);
        ((TextView) findViewById(R.id.phoneNo)).setText(phoneNo);

        ((TextView) findViewById(R.id.country)).setText(country);

        ((TextView) findViewById(R.id.postalcode)).setText(pincode);


        mBranchId = mSession.getBranchId();
        mPostalCode = mSettings.getPostalCode();


        Log.d("mBranchId","-->"+mBranchId);
        Log.d("mPostalCode","-->"+mPostalCode);
        mDeliveryChargeRdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFlag = "Delivery Charges";
                mDeliveryChargeRdBtn.setChecked(true);
                mPickUpFromShopRdBtn.setChecked(false);
            }
        });
        mPickUpFromShopRdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFlag = "Pickup from Store";
                mDeliveryChargeRdBtn.setChecked(false);
                mPickUpFromShopRdBtn.setChecked(true);
            }
        });

        findViewById(R.id.btnContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDeliveryChargeRdBtn.isChecked()|| mPickUpFromShopRdBtn.isChecked()){
                    mIntent.setClass(DeliveryMethodActivity.this, PaymentMethodActivity.class);
                    mIntent.putExtra(StringUtils.EXTRA_LABEL,mFlag);
                   if(mDeliveryChargeRdBtn.isChecked()){
                       mIntent.putExtra(StringUtils.EXTRA_VALUE,deliveryCharges);
                   }else if(mPickUpFromShopRdBtn.isChecked()){
                       mIntent.putExtra(StringUtils.EXTRA_VALUE,"0.00");
                   }
                    startActivity(mIntent);
                }else{
                    Toast.makeText(DeliveryMethodActivity.this,"Please Select Delivery Method",Toast.LENGTH_SHORT).show();
                }

            }
        });


        findViewById(R.id.deliveryCharges_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDeliveryChargeRdBtn.setChecked(true);
                mPickUpFromShopRdBtn.setChecked(false);
                mFlag = "Delivery Charges";
            }
        });




        findViewById(R.id.pickup_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDeliveryChargeRdBtn.setChecked(false);
                mPickUpFromShopRdBtn.setChecked(true);
                mFlag = "Pickup from Store";
            }
        });

        holidayCursor=mDBHelper.getHolidayCursor();
        delivery_amt =holidayCursor.getString(holidayCursor.getColumnIndex(COL_DELIVERY));
        shop=holidayCursor.getString(holidayCursor.getColumnIndex(COL_SHOP));
//        shop ="1";
//        delivery_amt ="1.25";
        Log.d("delivery_amt","--"+delivery_amt+shop);


        mGetDistance();

    }




    public void mGetDistance(){

        thrdGetDistance = new Thread() {
            @Override
            public void run() {

                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;
                try {

                    RestClientAcessTask client = new RestClientAcessTask(DeliveryMethodActivity.this, FUNC_GET_DISTANCE);
                    client.addParam("branchid", mBranchId);
                    client.addParam("postalcode", mPostalCode);
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
                ctgDistanceProgressHandler.sendMessage(msg);
            }
        };
        thrdGetDistance.start();
    }
    /**
     * Handling the message while progressing
     */
    private Handler ctgDistanceProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                thrdGetDistance.interrupt();
            } catch (Exception e) {
            }
            String sResponseCode = msg.getData().getString("CODE");
            String sResponseMsg = msg.getData().getString("DESC");

            if (sResponseCode.equals("SUCCESS")) {
                try {
                    JSONObject jsonObject = new JSONObject(sResponseMsg);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        String totalDistance =  jsonObject.getString("data").split("\\,")[0];
                        Log.d("totalDistance", "--" + totalDistance);

                        if(totalDistance.matches("NaN")){
                            totalDistance="0";
                        }else if(totalDistance.matches("12")){

                            HashMap<String, String> userDetails = mSettings.getUserInfo();
                            customerId = userDetails.get(PREF_CUSTOMER_ID);
                            cus_address1=userDetails.get(PREF_ADDRESS1);
                            cus_address2=userDetails.get(PREF_ADDRESS2);
                            cus_city=userDetails.get(PREF_CITY);
                            cus_postCode=userDetails.get(PREF_PINCODE);
                            address=cus_address1+cus_address2+cus_city+cus_postCode;

                            Log.d("customerId",""+customerId);
                            Log.d("cus_address1",""+cus_address1);
                            Log.d("cus_address2",""+cus_address2);
                            Log.d("cus_postCode",""+cus_postCode);
                            Log.d("address",""+address);


                            try {
                                addressList = geocoder.getFromLocationName(address, 5);
                                if (addressList != null && addressList.size() > 0) {
                                    double lat = (double) (addressList.get(0).getLatitude());
                                    double lng = (double) (addressList.get(0).getLongitude());
                                    distance(branch_lat,branch_lon,lat,lng);
                                    Log.d("LatitudeNan",""+lat);
                                    Log.d("LongitudeNan",""+lng);
                                    Log.d("DistanceCalculation",""+ distance(branch_lat,branch_lon,lat,lng));
                                    totalDistance = String.valueOf(Math.round(distance(branch_lat,branch_lon,lat,lng)));

                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else{
                            Log.d("totalDistance", "--" + totalDistance);

                        }
                        mGetThirdPartyDeliveryCharge(totalDistance);
                        ((TextView) findViewById(R.id.totalDistance)).setText(totalDistance +" KM");
                      //  ((TextView) findViewById(R.id.deliveryCharges)).setText("$ "+deliveryCharges);
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



    public double distance(double lat1, double lon1, double lat2, double lon2) {
        double Rad = 6372.8; //Earth's Radius In kilometers
        // TODO Auto-generated method stub
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double haverdistanceKM = Rad * c;

        return haverdistanceKM;

    }


    public void mGetThirdPartyDeliveryCharge(final String kilometer){

        thrdGetThirdPartyDeliveryCharge = new Thread() {
            @Override
            public void run() {

                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;
                try {

                    RestClientAcessTask client = new RestClientAcessTask(DeliveryMethodActivity.this, FUNC_GET_THIRD);
                    client.addParam("kilometer", kilometer);
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
                ctgThirdPartyDeliveryChargeProgressHandler.sendMessage(msg);
            }
        };
        thrdGetThirdPartyDeliveryCharge.start();
    }


    /**
     * Handling the message while progressing
     */
    private Handler ctgThirdPartyDeliveryChargeProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                thrdGetThirdPartyDeliveryCharge.interrupt();
            } catch (Exception e) {
            }
            String sResponseCode = msg.getData().getString("CODE");
            String sResponseMsg = msg.getData().getString("DESC");

            if (sResponseCode.equals("SUCCESS")) {
                try {
                    JSONObject jsonObject = new JSONObject(sResponseMsg);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        String thirdPartyDeliveryCharge =  jsonObject.getString("data");
                        deliveryCharges = thirdPartyDeliveryCharge;
                        Log.d("deliveryCharges", "--" + deliveryCharges);
                        if(thirdPartyDeliveryCharge.matches("NAN")){
                            thirdPartyDeliveryCharge = "0";
                        }
                        deliveryCharges = String.valueOf(Math.round(Double.valueOf(thirdPartyDeliveryCharge)));

                        Log.d("deliveryCharges","-->"+deliveryCharges);

                        cal.add(Calendar.HOUR,1);
                        String time = timeFormat.format(cal.getTime());
                        SimpleDateFormat date12Format = new SimpleDateFormat("hh:mm a");
                        SimpleDateFormat date24Format = new SimpleDateFormat("HH:mm");
                        try {
                            currentTime =date24Format.format(date12Format.parse(time));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Log.d("currentTime",""+currentTime);
                        getLateNightAmount(currentTime);

//                     ((TextView) findViewById(R.id.deliveryCharges)).setText("$ "+deliveryCharges);


                    }
                    mProgressDialog.setMessage("Loading...");
                    initDataLoadCompleted();

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

    public void initDataLoadCompleted() {

        ctgDistanceProgressHandler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                mProgressDialog.dismiss();
            }
        }, 3000);
    }


    private void getLateNightAmount(final String currentTime) {
        thrdLateNightCharge=  new Thread() {
            @Override
            public void run() {

                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;

                try {

                    RestClientAcessTask client = new RestClientAcessTask(DeliveryMethodActivity.this, FUNC_LATE_NIGHT_CHARGE);
                    client.addParam("Time", currentTime);
                    RestClientAcessTask.Status status = client.processAndFetchResponseWithParameter();
                    if (status == RestClientAcessTask.Status.SUCCESS) {
                        String sResponse = client.getResponse();
                        Log.d("ResponseStatus", "" + sResponse);
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
                ctgLateNightProgressHandler.sendMessage(msg);
            }
        };
        thrdLateNightCharge.start();
    }
    private Handler ctgLateNightProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                thrdLateNightCharge.interrupt();
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
                            for (int i = 0; i < len; i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                lateCharge=object.getString("latecharge");
                                late_charge=Double.parseDouble(lateCharge);
                            }
                            Log.d("LateCharge","--->"+late_charge+"   "+lateCharge);
                            if (shop.equals("1")) {

                                Log.d("delivery_amt","-->"+delivery_amt);

                                if(mSettings.getToastShow().equals("NOTSHOW")){
                                    mSettings.setToastShow("SHOW");
                                    deli_amt = Double.parseDouble(delivery_amt);
                                    Toast.makeText(getApplicationContext(), "Public Holiday delivery charge:"+delivery_amt+"$ is Added!!!", Toast.LENGTH_SHORT).show();
                                }

                            }

                            Log.d("deli_amt","--->"+deli_amt);
                            if(deli_amt==0.00){
                                value = Utils.twoDecimalPoint(Double.valueOf(deliveryCharges) * late_charge);
                            }else {
                                value = Utils.twoDecimalPoint(Double.valueOf(deliveryCharges) * late_charge * deli_amt);
                            }


                            ((TextView) findViewById(R.id.deliveryCharges)).setText("$ "+value);
                            Log.d("deliveryValue1",""+value);



                        } else{
                            if (shop.equals("1")) {

                                Log.d("delivery_amt","-->"+delivery_amt +mSettings.getToastShow());

                                if(mSettings.getToastShow().equals("NOTSHOW")){
                                    mSettings.setToastShow("SHOW");
                                    Toast.makeText(getApplicationContext(), "Public Holiday delivery charge:"+delivery_amt+"$ is Added!!!", Toast.LENGTH_SHORT).show();
                                    deli_amt = Double.parseDouble(delivery_amt);
                                }
                            }
                            Log.d("deli_amt","--->"+deli_amt);
                            if(deli_amt==0.00){
                                value = Utils.twoDecimalPoint(Double.valueOf(deliveryCharges));
                            }else{
                                value = Utils.twoDecimalPoint(Double.valueOf(deliveryCharges)* deli_amt);
                            }


                            ((TextView) findViewById(R.id.deliveryCharges)).setText("$ "+value);
                            Log.d("deliveryValue2",""+value);

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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
        menu.findItem(R.id.MyCart).setVisible(false);
        menu.findItem(R.id.NewRegistartion).setVisible(false);
        menu.findItem(R.id.Login).setVisible(false);
        menu.findItem(R.id.SignOut).setVisible(false);
        menu.findItem(R.id.UserName).setVisible(false);
        menu.findItem(R.id.Search).setVisible(false);

        menu.findItem(R.id.Home).setVisible(false);
        menu.findItem(R.id.MyOrders).setVisible(false);
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
    @Override
    public void onCancel(DialogInterface dialog) {
        mProgressDialog.dismiss();
    }
}
