package winapp.hajikadir.customer.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import winapp.hajikadir.customer.adapter.DeliveryAddressAdapter;
import winapp.hajikadir.customer.model.Address;
import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.helper.DBHelper;
import winapp.hajikadir.customer.helper.ProgressDialog;
import winapp.hajikadir.customer.helper.RestClientAcessTask;
import winapp.hajikadir.customer.helper.SessionManager;
import winapp.hajikadir.customer.helper.SettingsManager;
import winapp.hajikadir.customer.helper.UIHelper;
import winapp.hajikadir.customer.util.Constants;
import winapp.hajikadir.customer.util.Utils;

public class DeliveryAddressActivity extends AppCompatActivity implements DialogInterface.OnCancelListener,Constants {
    private TextView mlblCartNoOfItem,mlblTitle;
    private Intent mIntent;
    private SettingsManager mSettings;
    private SessionManager mSession;
    public DBHelper mDBHelper;
    private ActionBar mActionBar;
    private Thread thrdGetPostal,thrdSaveNewAddress,thrdGetCustomerAddress,thrdDeleteNewAddress;
    private EditText mFirstName, mLastName, mPostalCode, mAddress, mUnitNo, mBuildingName;
    private Button mPostalCodeBtn,mSaveNewAddressBtn;
    private String customerId = "";
    private RecyclerView mAddressRecyclerView;
    private ProgressDialog mProgressDialog;
    private UIHelper mUIHelper;
    private Animation mAnimFadeIn;
    private ArrayList<Address> mAddressArr;
    private DeliveryAddressAdapter mDeliveryAddressAdapter;
    private Dialog dialog;
    private Boolean checkPostalCode=true;
    Geocoder geocoder = null;
    List<android.location.Address> addressList;
    String address="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_address);

        //Customize the ActionBar
        mActionBar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.custom_abs_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.LEFT);

        mlblTitle= (TextView) viewActionBar.findViewById(R.id.title);
        mlblCartNoOfItem= (TextView) viewActionBar.findViewById(R.id.item);

        mAddressRecyclerView = (RecyclerView) findViewById(R.id.addressRecyclerView);
        mAddressRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mlblCartNoOfItem.setVisibility(View.GONE);
        mlblTitle.setText(getResources().getString(R.string.title_activity_delivery));
        mActionBar.setCustomView(viewActionBar, params);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        //abar.setIcon(R.color.transparent);
        mActionBar.setHomeButtonEnabled(true);

        mIntent = new Intent();
        mSettings = new SettingsManager(this);
        mSession = new SessionManager(this);
        mUIHelper = new UIHelper(this);

        mAddressArr = new ArrayList<>();
        geocoder =  new Geocoder(this, Locale.getDefault());
        addressList= new ArrayList<>();

        HashMap<String, String> userDetails = mSettings.getUserInfo();
        customerId = userDetails.get(PREF_CUSTOMER_ID);
//        String userId = userDetails.get(PREF_USER_ID);
//        String firstName = userDetails.get(PREF_FIRST_NAME);
//        String lastName = userDetails.get(PREF_LAST_NAME);
//        String address1 = userDetails.get(PREF_ADDRESS1);
//        String address2 = userDetails.get(PREF_ADDRESS2);
//        String phoneNo = userDetails.get(PREF_PHONENO);
//        String postalCode = userDetails.get(PREF_PINCODE);

      /*  ((TextView) findViewById(R.id.name)).setText(firstName);

        if(address1!=null && !address1.isEmpty()){
            ((TextView) findViewById(R.id.address1)).setText(address1);
        }else{
            findViewById(R.id.address1).setVisibility(View.GONE);
        }
        if(address2!=null && !address2.isEmpty()){
            ((TextView) findViewById(R.id.address2)).setText(address2);
        }else{
            findViewById(R.id.address2).setVisibility(View.GONE);
        }

        if(postalCode!=null && !postalCode.isEmpty()){
            ((TextView) findViewById(R.id.postalCode)).setText(postalCode);
        }else{
            findViewById(R.id.postalCode).setVisibility(View.GONE);
        }

        if(phoneNo!=null && !phoneNo.isEmpty()){
            ((TextView) findViewById(R.id.phoneNo)).setText(phoneNo);
        }else{
            findViewById(R.id.phoneNo).setVisibility(View.GONE);
        }*/

        findViewById(R.id.btnDeliver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIntent.setClass(DeliveryAddressActivity.this, DeliveryMethodActivity.class);
                startActivity(mIntent);
            }
        });

        findViewById(R.id.addNewAddress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddNewAddressDialog("Add",null);
            }
        });

        mDeliveryAddressAdapter = new DeliveryAddressAdapter(DeliveryAddressActivity.this,mAddressArr);
        mAddressRecyclerView.setAdapter(mDeliveryAddressAdapter);
        mDeliveryAddressAdapter.setOnEditClickListener(new DeliveryAddressAdapter.onEditClickListener() {
            @Override
            public void onCompleted(Address address) {
                showAddNewAddressDialog("Edit",address);
            }
        });

        mDeliveryAddressAdapter.setOnDeleteClickListener(new DeliveryAddressAdapter.onDeleteClickListener() {
            @Override
            public void onCompleted(final Address address, final int position) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                dialog.dismiss();
                                mDeleteAddress(address.getAddressId(),position);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder ab = new AlertDialog.Builder(DeliveryAddressActivity.this);
                ab.setMessage("Are you sure, do you want to delete this address?").setPositiveButton("Yes", dialogClickListener)  .setNegativeButton("No", dialogClickListener).show();


            }
        });

        Log.d("customerId","-->"+customerId);

        showProgress();
        getCustomerAddress();
    }

    public void showAddNewAddressDialog(final String flag, final Address deliveryAddress) {
        String adddressId = "";
        dialog = new Dialog(DeliveryAddressActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_new_address);
        dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.setCancelable(false);
        ImageView close = (ImageView) dialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();

        mFirstName = (EditText) dialog.findViewById(R.id.firstName);
        mLastName = (EditText) dialog.findViewById(R.id.lastName);
        mPostalCode = (EditText) dialog.findViewById(R.id.postalCode);
        mAddress = (EditText) dialog.findViewById(R.id.address);
        mUnitNo = (EditText) dialog.findViewById(R.id.unitNo);
        mBuildingName = (EditText) dialog.findViewById(R.id.buildingName);

        mPostalCodeBtn = (Button) dialog.findViewById(R.id.postalCodeBtn);

        mSaveNewAddressBtn = (Button) dialog.findViewById(R.id.saveNewAddress);

        if(flag.matches("Edit")){
            if(deliveryAddress!=null){
                 adddressId =  deliveryAddress.getAddressId();
                mFirstName.setText(deliveryAddress.getFirstName());
                mLastName.setText(deliveryAddress.getLastName());
                mPostalCode.setText(deliveryAddress.getPostalCode());
                mAddress.setText(deliveryAddress.getAddress1());
                mUnitNo.setText(deliveryAddress.getUnitNo());
                mBuildingName.setText(deliveryAddress.getAddress2());
                mSaveNewAddressBtn.setText("UPDATE");
            }

        }/*else {
            HashMap<String, String> userDetails = mSettings.getUserInfo();
            String firstName = userDetails.get(PREF_FIRST_NAME);
            String lastName = userDetails.get(PREF_LAST_NAME);
            mFirstName.setText(firstName);
            mLastName.setText(lastName);
            mSaveNewAddressBtn.setText("SAVE");
        }*/

        mPostalCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postalCode  = mPostalCode.getText().toString();
                if(postalCode!=null && !postalCode.isEmpty()){
                    address="Singapore"+" "+postalCode;
                    mGetPostalCode(postalCode);
                }else{
                    Toast.makeText(DeliveryAddressActivity.this,"Please enter postal code",Toast.LENGTH_SHORT).show();
                }
            }
        });

        final String finalAdddressId = adddressId;
        mSaveNewAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String firstName = mFirstName.getText().toString();
                    String lastName = mLastName.getText().toString();
                    String postalCode = mPostalCode.getText().toString();
                    String unitNo = mUnitNo.getText().toString();
                    String address = mAddress.getText().toString();
                    String buildingName = mBuildingName.getText().toString();

                    if (firstName != null && !firstName.isEmpty()) {
//                        if (lastName != null && !lastName.isEmpty()) {
                            if (postalCode != null && !postalCode.isEmpty()) {
                                if (address != null && !address.isEmpty()) {
//                                    if (buildingName != null && !buildingName.isEmpty()) {
                                        if (unitNo != null && !unitNo.isEmpty()) {
                                     //   mSaveNewAddress(flag, finalAdddressId, firstName, lastName, postalCode, address, unitNo, buildingName);

                                        if(unitNo.matches("#")){
                                            Toast.makeText(DeliveryAddressActivity.this, "Please enter unit no", Toast.LENGTH_SHORT).show();
                                        }else{
                                            mSaveNewAddress(flag, finalAdddressId, firstName, lastName, postalCode, address, unitNo, buildingName);
                                           /* if(checkPostalCode==false){

                                                checkPostalCode=true;
                                            }else
                                            {Toast.makeText(getApplicationContext(),"Please Validate your postal Code using search button",Toast.LENGTH_LONG).show();
                                            }*/
                                        }


                                        } else {
                                            Toast.makeText(DeliveryAddressActivity.this, "Please enter unit no", Toast.LENGTH_SHORT).show();
                                        }

//                                    } else {
//                                        Toast.makeText(DeliveryAddressActivity.this, "Please enter building name", Toast.LENGTH_SHORT).show();
//                                    }
                                } else {
                                    Toast.makeText(DeliveryAddressActivity.this, "Please enter address", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(DeliveryAddressActivity.this, "Please enter postal code", Toast.LENGTH_SHORT).show();
                            }
//                        } else {
//                            Toast.makeText(DeliveryAddressActivity.this, "Please enter last Name", Toast.LENGTH_SHORT).show();
//                        }
                    } else {
                        Toast.makeText(DeliveryAddressActivity.this, "Please enter first Name", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


    }
    public void showProgress(){
        mProgressDialog = ProgressDialog.show(DeliveryAddressActivity.this,"Loading...", true,true,this);
        mProgressDialog.setCancelable(false);
        mAnimFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in2);
        mAnimFadeIn = new AlphaAnimation(1.0f, 0.0f);
        mAnimFadeIn.setDuration(1000);
        mAnimFadeIn.setStartOffset(3000);
    }
    public void mGetPostalCode(final String postalCode){

        thrdGetPostal = new Thread() {
            @Override
            public void run() {

                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;
                try {
                    RestClientAcessTask client = new RestClientAcessTask(DeliveryAddressActivity.this, FUNC_GET_POSTAL);
                    client.addParam("postal", postalCode);
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
                ctgPostalProgressHandler.sendMessage(msg);
            }
        };
        thrdGetPostal.start();
    }
    private Handler ctgPostalProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                thrdGetPostal.interrupt();
            } catch (Exception e) {
            }
            String sResponseCode = msg.getData().getString("CODE");
            String sResponseMsg = msg.getData().getString("DESC");

            Log.d("sResponseCode", "-->s" + sResponseCode);
            if (sResponseCode.equals("SUCCESS")) {
                try {
                    JSONObject jsonObject = new JSONObject(sResponseMsg);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        int len = jsonArray.length();
                        Log.d("jsonArray", "-->s" + jsonArray);
                        if(len>0){
//                            checkPostalCode=false;

                            JSONObject object = jsonArray.getJSONObject(0);
                            String street = object.getString("street");
                            String building = object.getString("building");
                            String range = object.getString("range");

                            if(street!=null && !street.isEmpty()){
                                if(range!=null && !range.isEmpty()){
                                    mAddress.setText(range+" "+street);
                                }else{
                                    mAddress.setText(street);
                                }
                            }
                            if(building!=null && !building.isEmpty()){
                                mBuildingName.setText(building);
                            }
                        }else{
                            try {
                                Log.d("addressshow",address);
                                addressList = geocoder.getFromLocationName(address, 5);
                                if (addressList != null && addressList.size() > 0) {
                                    double lat = (double) (addressList.get(0).getLatitude());
                                    double lng = (double) (addressList.get(0).getLongitude());
                                    Log.d("LatitudeLongitudeValues",""+lat+lng);

                                    addressList = geocoder.getFromLocation(lat, lng, 1);
                                    String address = addressList.get(0). getAddressLine(0);// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                    String[] separated = address.split(",");
                                    String city = addressList.get(0).getLocality();
                                    String state = addressList.get(0).getAdminArea();
                                    String country = addressList.get(0).getCountryName();
                                    String postalCode=addressList.get(0).getPostalCode();
                                    String knownName = addressList.get(0).getFeatureName();
                                    Log.d("Address",""+address);
                                    Log.d("city",""+city);
                                    Log.d("state",""+state);
                                    Log.d("country",""+country);
                                    Log.d("PostalCode",""+postalCode);
                                    Log.d("knownName",""+knownName);

                                    mAddress.setText(separated[0]+separated[1]);
                                    mBuildingName.setText(separated[1]);

                                }

                            }catch (Exception e){

                            }
//                            checkPostalCode=true;
//                            Toast.makeText(DeliveryAddressActivity.this,"Invalid Postal Code",Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                }

            } else if (sResponseCode.equals("ERROR")) {
            }
        }
    };

    public void mSaveNewAddress(final String flag,final String addressId,final String firstName,final String lastName,final String postalCode,final String address,final String unitNo,final String buildingName){
        thrdSaveNewAddress = new Thread() {
            @Override
            public void run() {

                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;
                RestClientAcessTask client = null;
                try {
                    Log.d("flag","-->"+flag);
                    Log.d("addressId","-->"+addressId);
                    if(flag.matches("Edit")){
                      client = new RestClientAcessTask(DeliveryAddressActivity.this, FUNC_UPDATE_DELIVERY_ADDRESS);
                      client.addParam("address_id", addressId);
                    }else{
                        client = new RestClientAcessTask(DeliveryAddressActivity.this, FUNC_SAVE_ADDRESS);
                    }
                    client.addParam("customer_id", customerId);
                    client.addParam("firstname", firstName);
                    client.addParam("lastname", lastName);
                    client.addParam("postcode", postalCode);
                    client.addParam("address_1", address);
                    client.addParam("unitno", unitNo);
                    client.addParam("address_2", buildingName);
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
                ctgSaveNewAddressProgressHandler.sendMessage(msg);
            }
        };
        thrdSaveNewAddress.start();
    }
    private Handler ctgSaveNewAddressProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                thrdSaveNewAddress.interrupt();
            } catch (Exception e) {
            }
            String sResponseCode = msg.getData().getString("CODE");
            String sResponseMsg = msg.getData().getString("DESC");

            Log.d("sResponseCode", "-->s" + sResponseCode);
            if (sResponseCode.equals("SUCCESS")) {
                try {
                    Log.d("sResponseCode", "-<<" + sResponseCode);
                    JSONObject jsonObject = new JSONObject(sResponseMsg);
                    Log.d("jsonObject", "-<<" + jsonObject.toString());
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        String result =  jsonObject.getString("data");
                      //  JSONArray jsonArray = jsonObject.getJSONArray("data");
                        Log.d("result", "-->s" + result);
                        if(!result.equalsIgnoreCase("false")){
                            dialog.dismiss();
                            showProgress();
                            getCustomerAddress();
                        }else{
                            Toast.makeText(DeliveryAddressActivity.this,"Unable to save address. please try again later",Toast.LENGTH_SHORT).show();
                        }

                    }

                } catch (Exception e) {
                   Toast.makeText(DeliveryAddressActivity.this,"Unable to save address. please try again later",Toast.LENGTH_SHORT).show();
                }



            } else if (sResponseCode.equals("ERROR")) {
                Toast.makeText(DeliveryAddressActivity.this,"Unable to save address. please try again later",Toast.LENGTH_SHORT).show();
            }
        }
    };
    public void getCustomerAddress(){
        mAddressArr.clear();
        thrdGetCustomerAddress = new Thread() {
            @Override
            public void run() {

                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;
                try {
                    RestClientAcessTask client = new RestClientAcessTask(DeliveryAddressActivity.this, FUNC_GET_CUSTOMER_ADDRESS);
                    client.addParam("customer_id", customerId);
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
                ctgCustomerAddressProgressHandler.sendMessage(msg);
            }
        };
        thrdGetCustomerAddress.start();
    }
    /**
     * Handling the message while progressing
     */
    private Handler ctgCustomerAddressProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                thrdGetCustomerAddress.interrupt();
            } catch (Exception e) {
            }
            String mStatus = null;
            String sResponseCode = msg.getData().getString("CODE");
            String sResponseMsg = msg.getData().getString("DESC");
            mProgressDialog.setMessage("Loading...");
            if (sResponseCode.equals("SUCCESS")) {
                mStatus =sResponseCode;
                try {
                    JSONObject jsonObject = new JSONObject(sResponseMsg);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        Log.d("customer jsonArray", "-->" + jsonArray);
                        int len = jsonArray.length();
                        if(len>0){
                            for(int i=0;i<len;i++){
                                Address address = new Address();
                                JSONObject object = jsonArray.getJSONObject(i);

                                String firstname = object.getString("firstname");
                                String lastname = object.getString("lastname");
                                String address_id = object.getString("address_id");
                                String address_1 = object.getString("address_1");
                                String address_2 = object.getString("address_2");
                                String postcode = object.getString("postcode");
                                String unitno = object.getString("unitno");
                                String city = object.getString("city");
                                String country_id = object.getString("country_id");
                                String zone_id = object.getString("zone_id");
                                address.setFirstName(firstname);
                                address.setLastName(lastname);
                                address.setAddress1(address_1);
                                address.setAddress2(address_2);
                                address.setPostalCode(postcode);
                                address.setUnitNo(unitno);
                                address.setCity(city);
                                address.setAddressId(address_id);
                                mAddressArr.add(address);
                            }

                            //Sort By SortOrder
                            Collections.sort(mAddressArr, new Comparator<Address>(){
                                public int compare(Address o1, Address o2)
                                {
                                    return (o2.getAddressId().compareTo(o1.getAddressId()));
                                }
                            });
                            Log.d("Radio_position",""+ mSettings.getBtnPosition());
                            int value=Integer.parseInt(mSettings.getBtnPosition());
                            for(int i=0;i<mAddressArr.size();i++){
                                Log.d("ivalue",""+i);
                                if(i==value){
                                    Log.d("ivalue",""+i);
                                    mAddressArr.get(i).setChecked(true);
                                }else{
                                    mAddressArr.get(i).setChecked(false);
                                }
                            }
                            mDeliveryAddressAdapter.notifyDataSetChanged();

                            Address address = mAddressArr.get(value);
                            mSettings.updateCustomerDetail(address);
                        }
                        mProgressDialog.dismiss();
                    }


                } catch (Exception e) {
                    mProgressDialog.dismiss();
                    mUIHelper.showErrorDialog(e.getMessage());
                }

            } else if (sResponseCode.equals("ERROR")) {
                mProgressDialog.dismiss();
                mUIHelper.showLongToast(R.string.unable_to_get_address);
            }

        }
    };


    public void mDeleteAddress(final String addressId, final int position){
        try {
            showProgress();
            thrdDeleteNewAddress = new Thread() {
                @Override
                public void run() {

                    Message msg = new Message();
                    Bundle bndle = new Bundle();
                    String sCode = null, sDesc = null;
                    try {
                        RestClientAcessTask client = new RestClientAcessTask(DeliveryAddressActivity.this, FUNC_DELETE_ADDRESS);
                        client.addParam("address_id", addressId);
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
                    bndle.putInt("POSITION",position);
                    msg.setData(bndle);
                    ctgDeleteAddressProgressHandler.sendMessage(msg);
                }
            };
            thrdDeleteNewAddress.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private Handler ctgDeleteAddressProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                thrdSaveNewAddress.interrupt();
            } catch (Exception e) {
            }
            String sResponseCode = msg.getData().getString("CODE");
            String sResponseMsg = msg.getData().getString("DESC");
            int position = msg.getData().getInt("POSITION");

            Log.d("sResponseCode", "-->s" + sResponseCode);
            if (sResponseCode.equals("SUCCESS")) {
                try {
                    Log.d("sResponseCode", "-<<" + sResponseCode);
                    JSONObject jsonObject = new JSONObject(sResponseMsg);
                    Log.d("jsonObject", "-<<" + jsonObject.toString());
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        String result =  jsonObject.getString("data");
                        //  JSONArray jsonArray = jsonObject.getJSONArray("data");
                        Log.d("result", "-->s" + result);
                        if(result.equalsIgnoreCase("true")){
                            mDeliveryAddressAdapter.remove(position);
                            Toast.makeText(DeliveryAddressActivity.this,"Address deleted Successfully",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(DeliveryAddressActivity.this,"Unable to delete address. please try again later",Toast.LENGTH_SHORT).show();
                        }
                        mProgressDialog.dismiss();
                    }
                } catch (Exception e) {
                    mProgressDialog.dismiss();
                    Toast.makeText(DeliveryAddressActivity.this,"Unable to delete address. please try again later",Toast.LENGTH_SHORT).show();
                }
            } else if (sResponseCode.equals("ERROR")) {
                mProgressDialog.dismiss();
                Toast.makeText(DeliveryAddressActivity.this,"Unable to delete address. please try again later",Toast.LENGTH_SHORT).show();
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
