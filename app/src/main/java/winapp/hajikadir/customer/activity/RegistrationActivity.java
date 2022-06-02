package winapp.hajikadir.customer.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.helper.ProgressDialog;
import winapp.hajikadir.customer.helper.RestClientAcessTask;
import winapp.hajikadir.customer.helper.SessionManager;
import winapp.hajikadir.customer.helper.SettingsManager;
import winapp.hajikadir.customer.helper.UIHelper;
import winapp.hajikadir.customer.util.Constants;
import winapp.hajikadir.customer.util.Utils;


public class RegistrationActivity extends AppCompatActivity implements DialogInterface.OnCancelListener,Constants {
    private Button mSignUpBtn;
   // private GridView mBottomSheet;
   // private BottomSheetAdapter bottomSheetAdapter;
    //private BottomSheetBehavior sheetBehavior;
    private UIHelper mUIHelper;
    private ProgressDialog mProgressDialog;
    private Animation mAnimFadeIn;
    private Intent mIntent;
    private TextView mlblTitle,mlblCartNoOfItem;
    private SessionManager mSession;
    private SettingsManager mSettings;
    private Thread thrdNewRegistration,thrdGetPostal,thrdGetCustomer;
    Geocoder geocoder = null;
    String address="";
    String name="",password="",confirmPassword="",email="",phone="",company="",unitno = "",address1="",address2="",postalCode="";
    private EditText mNameEdt,mPasswordEdt,mConfirmPasswordEdt,mEmailEdt,mPhoneEdt,mCompanyEdt,mUnitno,mAddress1Edt,mAddress2Edt,mPostalCodeEdt;
   /* Menu menu_data [] = new Menu[]
            {
                    new Menu(R.mipmap.ic_order_icon, "Home",true),
                    new Menu(R.mipmap.ic_products_icon, "Products",false),
                    new Menu(R.mipmap.ic_order_icon, "MyOrder",false),
                    new Menu(R.mipmap.ic_contact_selected_icon, "Registration",true),
                    new Menu(R.mipmap.ic_account_icon, "Exit",false)
            };
*/
   private ActionBar mActionBar;
   private boolean postalFlag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


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
        mlblTitle.setText(getResources().getString(R.string.title_activity_registrataion));
        mActionBar.setCustomView(viewActionBar, params);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        //abar.setIcon(R.color.transparent);
        mActionBar.setHomeButtonEnabled(true);
        mNameEdt = (EditText) findViewById(R.id.name);
        mPasswordEdt = (EditText) findViewById(R.id.password);
        mConfirmPasswordEdt = (EditText) findViewById(R.id.confirm_password);
        mEmailEdt = (EditText) findViewById(R.id.email);
        mPhoneEdt = (EditText) findViewById(R.id.phone);
        mCompanyEdt = (EditText) findViewById(R.id.company);
        mUnitno = (EditText) findViewById(R.id.unitno);
        mAddress1Edt = (EditText) findViewById(R.id.address1);
        mAddress2Edt = (EditText) findViewById(R.id.address2);
        mPostalCodeEdt = (EditText) findViewById(R.id.postalcode);

        mSession = new SessionManager(RegistrationActivity.this);
        mSettings = new SettingsManager(RegistrationActivity.this);

        geocoder =  new Geocoder(this, Locale.getDefault());


        //Object Initializtaion
        mIntent = new Intent();
        mUIHelper = new UIHelper(RegistrationActivity.this);

        findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postalCode = mPostalCodeEdt.getText().toString();
                if(postalCode!=null && !postalCode.isEmpty()){
                    showProgress();
                    address="Singapore"+" "+postalCode;
                    mGetPostalCode();
                }else {
                    Toast.makeText(RegistrationActivity.this,"Please enter the postal coe",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    private void attemptNewRegistration(){
        // Reset errors.
        mNameEdt.setError(null);
        mPasswordEdt.setError(null);
        mConfirmPasswordEdt.setError(null);
        mEmailEdt.setError(null);
        mPhoneEdt.setError(null);
        mAddress1Edt.setError(null);
        mAddress2Edt.setError(null);
        mPostalCodeEdt.setError(null);


        // Store values at the time of the login attempt.
        name = mNameEdt.getText().toString();
        password = mPasswordEdt.getText().toString();

        confirmPassword = mConfirmPasswordEdt.getText().toString();
        email = mEmailEdt.getText().toString();
        phone = mPhoneEdt.getText().toString();
        company = mCompanyEdt.getText().toString();
        unitno = mUnitno.getText().toString();
        address1 = mAddress1Edt.getText().toString();
        address2 = mAddress2Edt.getText().toString();
        postalCode = mPostalCodeEdt.getText().toString();
        boolean cancel = false;
        View focusView = null;

        // Check for a valid User Name.
        if (Utils.isEmpty(name)) {
            mNameEdt.setError(getString(R.string.error_field_required));
            focusView = mNameEdt;
            cancel = true;
        }else if (Utils.isEmpty(password)) {
            mPasswordEdt.setError(getString(R.string.error_field_required));
            focusView = mPasswordEdt;
            cancel = true;
        }else if (Utils.isEmpty(confirmPassword)) {
            mConfirmPasswordEdt.setError(getString(R.string.error_field_required));
            focusView = mConfirmPasswordEdt;
            cancel = true;
        }
        else if (!Utils.validate(email)) {
            mEmailEdt.setError(getString(R.string.error_invalid_email));
            focusView = mEmailEdt;
            cancel = true;
        } else if (Utils.isEmpty(phone)) {
            mPhoneEdt.setError(getString(R.string.error_field_required));
            focusView = mPhoneEdt;
            cancel = true;
        }
        /*else if (Utils.isEmpty(address1)) {
            mAddress1Edt.setError(getString(R.string.error_field_required));
            focusView = mAddress1Edt;
            cancel = true;
        }
        else if (Utils.isEmpty(address2)) {
            mAddress2Edt.setError(getString(R.string.error_field_required));
            focusView = mAddress2Edt;
            cancel = true;
        }*/
        else if (Utils.isEmpty(postalCode)) {
            mPostalCodeEdt.setError(getString(R.string.error_field_required));
            focusView = mPostalCodeEdt;
            cancel = true;
        }
        else if (Utils.isEmpty(unitno)) {
            mUnitno.setError(getString(R.string.error_field_required));
            focusView = mUnitno;
            cancel = true;
        }
        else if (!password.equals(confirmPassword)) {
            mPasswordEdt.setError(getString(R.string.password_is_not_same_as_confirm_password));
            mConfirmPasswordEdt.setError(getString(R.string.password_is_not_same_as_confirm_password));
            focusView = mPasswordEdt;
            cancel = true;
        }
        /*else if(postalFlag==false){
            mPostalCodeEdt.setError(getString(R.string.postalcode_error));
            focusView = mPostalCodeEdt;
            cancel = true;
        }*/

        //     Log.d("cancel","-->"+cancel);
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            hideKeyboard();
            showProgress();
            Log.d("name", "" + name);
            Log.d("password", "" + password);
            Log.d("confirmPassword", "" + confirmPassword);
            Log.d("email", "" + email);
            Log.d("phone", "" + phone);
            Log.d("address1", "" + address1);
            Log.d("address2", "" + address2);
            Log.d("postalCode", "" + postalCode);

            checkExistingCustomer();
            //;

        }



    }
    public void showProgress(){
        mProgressDialog = ProgressDialog.show(RegistrationActivity.this,"Loading...", true,true,this);
        mProgressDialog.setCancelable(false);
        mAnimFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in2);
        mAnimFadeIn = new AlphaAnimation(1.0f, 0.0f);
        mAnimFadeIn.setDuration(1000);
        mAnimFadeIn.setStartOffset(3000);
    }
        public void checkExistingCustomer(){
        thrdGetCustomer = new Thread() {
            @Override
            public void run() {

                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;
                try {
                    RestClientAcessTask client = new RestClientAcessTask(RegistrationActivity.this, FUNC_GET_CUSTOMER);
                    client.addParam("email", email);
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
                ctgCustomerProgressHandler.sendMessage(msg);
            }
        };
        thrdGetCustomer.start();
    }
    /**
     * Handling the message while progressing
     */
    private Handler ctgCustomerProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                thrdGetCustomer.interrupt();
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
                        Log.d("jsonArray", "-->s" + jsonArray);
                        if(len>0){
                            mProgressDialog.setMessage("Loading...");
                            JSONObject object = jsonArray.getJSONObject(0);
                            String emailId = object.getString("email");
                            if(emailId.equals(email)){
                                mProgressDialog.dismiss();
                                Toast.makeText(RegistrationActivity.this,"Email is already exist.Please Enter new email id",Toast.LENGTH_SHORT).show();
                            }/*else{
                                newRegistrationTask();
                            }*/
                         }else{
                            newRegistrationTask();
                         }
                     }
                } catch (Exception e) {
                    mProgressDialog.dismiss();
                    mUIHelper.showErrorDialog(e.getMessage());
                }

            } else if (sResponseCode.equals("ERROR")) {
                mProgressDialog.dismiss();
            }

        }
    };

    public void newRegistrationTask(){
        mProgressDialog.setMessage("Loading...");
        thrdNewRegistration = new Thread() {
            @Override
            public void run() {

                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;
                try {
                    RestClientAcessTask client = new RestClientAcessTask(RegistrationActivity.this, FUNC_GET_REGISTER);
                    Log.d("name", "" + name);
                    Log.d("password", "" + password);
                    Log.d("confirmPassword", "" + confirmPassword);
                    Log.d("email", "" + email);
                    Log.d("phone", "" + phone);
                    Log.d("address1", "" + address1);
                    Log.d("address2", "" + address2);
                    Log.d("postalCode", "" + postalCode);
                    Log.d("unitno", "" + unitno);

                    client.addParam("firstname", name);
                    client.addParam("password", password);
                    client.addParam("confirm", confirmPassword);
                    client.addParam("email", email);
                    client.addParam("telephone", phone);
                    client.addParam("company", company);
                    client.addParam("address_1", address1);
                    client.addParam("address_2", address2);
                    client.addParam("postcode", postalCode);
                    client.addParam("unitno", unitno);

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
                ctgNewRegistrationProgressHandler.sendMessage(msg);
            }
        };
        thrdNewRegistration.start();
    }
    /**
     * Handling the message while progressing
     */
    private Handler ctgNewRegistrationProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                thrdNewRegistration.interrupt();
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
                        Log.d("jsonArray", "-->s" + jsonArray);
                        if(len>0){
                            JSONObject object = jsonArray.getJSONObject(0);
                            String emailId = object.getString("email");
                            String firstname = object.getString("firstname");
                            String lastname = object.getString("lastname");
                            String telephone = object.getString("telephone");
                            String address_1 = object.getString("address_1");
                            String address_2 = object.getString("address_2");
                            String postcode = object.getString("postcode");
                            String customer_id = object.getString("customer_id");
                            String customer_group_id = object.getString("customer_group_id");
                            String fax = object.getString("fax");
                            String city = object.getString("city");
                            String country_id = object.getString("country_id");
                            String zone_id = object.getString("zone_id");
                            String company = object.getString("company");
                            String unitno = object.getString("unitno");
                            if(emailId.equals(email)){
                                mSession.createLoginSession(emailId,password,firstname);
                                mSettings.createUserDetail(emailId,firstname,lastname,address_1,address_2,telephone,postcode,customer_id,customer_group_id,fax,
                                        city,country_id,zone_id,company);
                                Toast.makeText(RegistrationActivity.this,"Successfully Registered",Toast.LENGTH_SHORT).show();
                                mIntent.setClass(RegistrationActivity.this, HomeActivity.class);
                                startActivity(mIntent);
                                finish();
                            }
                        }
                    }

                    mProgressDialog.dismiss();

                } catch (Exception e) {
                    mProgressDialog.dismiss();
                    mUIHelper.showErrorDialog(e.getMessage());
                }

            } else if (sResponseCode.equals("ERROR")) {
                mProgressDialog.dismiss();
            }

        }
    };
    public void mGetPostalCode(){

        thrdGetPostal = new Thread() {
            @Override
            public void run() {

                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;
                try {
                    RestClientAcessTask client = new RestClientAcessTask(RegistrationActivity.this, FUNC_GET_POSTAL);
                    client.addParam("postal", mPostalCodeEdt.getText().toString());
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
//                            postalFlag = true;
                            JSONObject object = jsonArray.getJSONObject(0);
                            String street = object.getString("street");
                            String building = object.getString("building");
                            if(street!=null && !street.isEmpty()){
                                mAddress1Edt.setText(street);
                            }
                            if(building!=null && !building.isEmpty()){
                                mAddress2Edt.setText(building);
                            }
                        }else{
                            try {
                                Log.d("addressshow",address);
                                List<Address> addressList = null;
                                List<Address> addressLv = null;
                                addressList = geocoder.getFromLocationName(address, 5);
                                if (addressList != null && addressList.size() > 0) {
                                    double lat = (double) (addressList.get(0).getLatitude());
                                    double lng = (double) (addressList.get(0).getLongitude());
                                    Log.d("LatitudeLongitudeValues",""+lat+lng);
                                    Log.d("addressList","check"+addressList.size());

                                    addressLv = geocoder.getFromLocation(lat, lng, 1);
                                    String address = addressLv.get(0). getAddressLine(0);// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                    String[] separated = address.split(",");
                                    String city = addressLv.get(0).getLocality();
                                    String state = addressLv.get(0).getAdminArea();
                                    String country = addressLv.get(0).getCountryName();
                                    String postalCode=addressLv.get(0).getPostalCode();
                                    String knownName = addressLv.get(0).getFeatureName();
                                    Log.d("Address",""+address);
                                    Log.d("city",""+city);
                                    Log.d("state",""+state);
                                    Log.d("country",""+country);
                                    Log.d("PostalCode",""+postalCode);
                                    Log.d("knownName",""+knownName);

                                    mAddress1Edt.setText(separated[0]+separated[1]);
                                    mAddress2Edt.setText(separated[1]);

                                }
                            }catch (Exception e){

                            }
                        }
                    }
                    mProgressDialog.dismiss();

                } catch (Exception e) {
                    mProgressDialog.dismiss();
                    mUIHelper.showErrorDialog(e.getMessage());
                }

            } else if (sResponseCode.equals("ERROR")) {
                mProgressDialog.dismiss();
            }
        }
    };
    @Override
    public void onCancel(DialogInterface dialog) {
        mProgressDialog.dismiss();
    }
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
        menu.findItem(R.id.Home).setVisible(false);
        menu.findItem(R.id.MyOrders).setVisible(false);
        menu.findItem(R.id.Search).setVisible(false);
        menu.findItem(R.id.Login).setVisible(false);
        menu.findItem(R.id.SignOut).setVisible(false);
        menu.findItem(R.id.UserName).setVisible(false);
        menu.findItem(R.id.MyCart).setVisible(false);
        menu.findItem(R.id.Products).setVisible(false);
        menu.findItem(R.id.Promotions).setVisible(false);
        menu.findItem(R.id.AboutUs).setVisible(false);
        menu.findItem(R.id.ContactUs).setVisible(false);
        menu.findItem(R.id.PrivacyPolicy).setVisible(false);
        menu.findItem(R.id.SignOut).setVisible(false);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.NewRegistartion:
                // TODO Something when menu item selected
                attemptNewRegistration();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    protected void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
