package winapp.hajikadir.customer.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.config.StringUtils;
import winapp.hajikadir.customer.helper.ProgressDialog;
import winapp.hajikadir.customer.helper.RestClientAcessTask;
import winapp.hajikadir.customer.helper.SessionManager;
import winapp.hajikadir.customer.helper.SettingsManager;
import winapp.hajikadir.customer.helper.UIHelper;
import winapp.hajikadir.customer.notification.Config;
import winapp.hajikadir.customer.util.Constants;
import winapp.hajikadir.customer.util.Utils;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements DialogInterface.OnCancelListener,Constants {

    private EditText mUserNameEdView, mPasswordEdView;
    private Button mSignInBtn,mSignUpBtn;
    private ProgressDialog mProgressDialog;
    private String userId="",password="",fireBaseTokenId="";
    private Animation mAnimFadeIn;
    private Thread thrdGetSignIn,thrdGetCustomer,thrdSendFirebaseIdToServer;
    private UIHelper mUIHelper;
    private Intent mIntent;
    private CheckBox mRememberMe;
    private SettingsManager mSettings;
    private SessionManager mSession;
    private ActionBar mActionBar;
    private TextView mlblTitle,mlblCartNoOfItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mActionBar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.custom_abs_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.LEFT);

        mlblTitle= (TextView) viewActionBar.findViewById(R.id.title);
        mlblCartNoOfItem= (TextView) viewActionBar.findViewById(R.id.item);
        mlblTitle.setText(getResources().getString(R.string.title_activity_login));
        mlblCartNoOfItem.setVisibility(View.GONE);
        mActionBar.setCustomView(viewActionBar, params);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        //abar.setIcon(R.color.transparent);
        mActionBar.setHomeButtonEnabled(true);

        mUIHelper = new UIHelper(LoginActivity.this);
        mIntent = new Intent();
        mSettings = new SettingsManager(LoginActivity.this);
        mSession =  new SessionManager(LoginActivity.this);

        mUserNameEdView = (EditText) findViewById(R.id.username);
        mUserNameEdView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_ACTION_NEXT) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mPasswordEdView = (EditText) findViewById(R.id.password);
        mPasswordEdView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mSignInBtn = (Button) findViewById(R.id.sign_in_button);
        mSignInBtn.setVisibility(View.VISIBLE);
        mSignInBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
             //   mDBHelper.deleteAllProduct();
            }
        });
        mRememberMe = (CheckBox) findViewById(R.id.rememberMe);
       // if (mSession.isLoggedIn()) {
            HashMap<String, String> userDetails = mSession.getUserDetails();
            mUserNameEdView.setText(userDetails.get(PREF_SESSION_USER_ID));
            mPasswordEdView.setText(userDetails.get(PREF_SESSION_PASSWORD));
          //  mRememberMe.setChecked(true);
            mUserNameEdView.setSelection(mUserNameEdView.getText().length());
        //}else{
       //     mRememberMe.setChecked(false);
       //     mSession.clearLoginSession();
       // }
        mSignUpBtn = (Button) findViewById(R.id.sign_up_button);
        mSignUpBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mIntent.setClass(LoginActivity.this, RegistrationActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
                finish();
            }
        });

        displayFirebaseRegId();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mUserNameEdView.setError(null);
        mPasswordEdView.setError(null);

        // Store values at the time of the login attempt.
        userId = mUserNameEdView.getText().toString();
        password = mPasswordEdView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid User Name.
        if (Utils.isEmpty(userId)) {
            mUserNameEdView.setError(getString(R.string.error_field_required));
            focusView = mUserNameEdView;
            cancel = true;
        } else if (!Utils.validate(userId)) {
            mUserNameEdView.setError(getString(R.string.error_invalid_username));
            focusView = mUserNameEdView;
            cancel = true;
        }
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !Utils.isPasswordValid(password)) {
            mPasswordEdView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordEdView;
            cancel = true;
        }else if (TextUtils.isEmpty(password)) {
            if(!TextUtils.isEmpty(userId)){
                mPasswordEdView.setError(getString(R.string.error_field_required));
                focusView = mPasswordEdView;
                cancel = true;
            }
        }
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
            Log.d("userId", "" + userId);
            Log.d("password", "" + password);
           /* params.clear();
            params.add(newPropertyInfo(KEY_USERID, username));
            params.add(newPropertyInfo(KEY_PASSWORD, password));
            new SoapAccessTask(LoginActivity.this, FUNC_LOGIN, params,
                    new UserLoginTask()).execute();*/
            userLoginTask();

        }
    }
    private void displayFirebaseRegId() {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

            //Log.d("AndroidBash", "Subscribed");
          //  Toast.makeText(LoginActivity.this, "Subscribed", Toast.LENGTH_SHORT).show();

            fireBaseTokenId = FirebaseInstanceId.getInstance().getToken();

            // Log and toast
            Log.d("FireBaseTokenId", fireBaseTokenId);
           // Toast.makeText(LoginActivity.this, token, Toast.LENGTH_SHORT).show();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
   public void showProgress(){
       mProgressDialog = ProgressDialog.show(LoginActivity.this,"Loading...", true,true,this);
       mProgressDialog.setCancelable(false);
       mAnimFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in2);
       mAnimFadeIn = new AlphaAnimation(1.0f, 0.0f);
       mAnimFadeIn.setDuration(1000);
       mAnimFadeIn.setStartOffset(3000);
   }

    public void userLoginTask(){
        thrdGetSignIn = new Thread() {
            @Override
            public void run() {

                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;
                try {
                    RestClientAcessTask client = new RestClientAcessTask(LoginActivity.this, FUNC_GET_LOGIN);
                    client.addParam("email", userId);
                    client.addParam("password", password);
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
                ctgSignInProgressHandler.sendMessage(msg);
            }
        };
        thrdGetSignIn.start();
    }
    /**
     * Handling the message while progressing
     */
    private Handler ctgSignInProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                thrdGetSignIn.interrupt();
            } catch (Exception e) {
            }
            String mStatus = null;
            String sResponseCode = msg.getData().getString("CODE");
            String sResponseMsg = msg.getData().getString("DESC");
            mProgressDialog.setMessage("Loading...");
            if (sResponseCode.equals("SUCCESS")) {
                try {
                    JSONObject jsonObject = new JSONObject(sResponseMsg);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        Log.d("Login jsonArray", "-->" + jsonArray);
                        int len = jsonArray.length();
                        if(len>0){
                            JSONObject object = jsonArray.getJSONObject(0);
                            String status = object.getString("status");
                            String approved = object.getString("approved");
                            String firstname = object.getString("firstname");

                            String emailId = object.getString("email");
                            String lastname = object.getString("lastname");
                            String telephone = object.getString("telephone");
                            String customer_id = object.getString("customer_id");
                            String customer_group_id = object.getString("customer_group_id");




                            if(status.equals("1")&& approved.equals("1")){
                               // if (mRememberMe.isChecked()) {
                                    mSession.createLoginSession(emailId, password,firstname);
                               // } else {
                               //     mSession.clearLoginSession();
                                //}
                               // mSettings.createUserDetail(userId,firstname);
                               /* mSettings.createUserDetail(emailId,firstname,lastname,"","",telephone,"",customer_id,customer_group_id,"",
                                        "","","","");*/
                                mUIHelper.showLongToast(R.string.successfully_login);
                                mStatus = "SUCCESS";
                                getCustomerDetail(emailId);
                               // initDataLoadCompleted(mStatus);
                            }
                           else{
                                mUIHelper.showLongToast(R.string.invalid_username_or_password);
                                mStatus = "FAILED";
                                initDataLoadCompleted(mStatus);

                            }

                        }else{
                            mUIHelper.showLongToast(R.string.invalid_username_or_password);
                            mStatus = "FAILED";
                            initDataLoadCompleted(mStatus);
                        }
                    }else{
                        mUIHelper.showLongToast(R.string.invalid_username_or_password);
                        mStatus = "FAILED";
                        initDataLoadCompleted(mStatus);
                    }


                } catch (Exception e) {
                    mProgressDialog.dismiss();
                    mUIHelper.showErrorDialog(e.getMessage());
                }

            } else if (sResponseCode.equals("ERROR")) {
                mProgressDialog.dismiss();
                mUIHelper.showLongToast(R.string.unable_to_login);
            }

        }
    };

    public void getCustomerDetail(final String emailId){
        thrdGetCustomer = new Thread() {
            @Override
            public void run() {

                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;
                try {
                    RestClientAcessTask client = new RestClientAcessTask(LoginActivity.this, FUNC_GET_CUSTOMER);
                    client.addParam("email", emailId);
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
                ctgCustomerDetailProgressHandler.sendMessage(msg);
            }
        };
        thrdGetCustomer.start();
    }
    /**
     * Handling the message while progressing
     */
    private Handler ctgCustomerDetailProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                thrdGetCustomer.interrupt();
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

                            mSettings.createUserDetail(emailId,firstname,lastname,address_1,address_2,telephone,postcode,customer_id,customer_group_id,fax,
                                    city,country_id,zone_id,company);

                            sendFirebaseIdToServer(customer_id,emailId);

                        }
                    }


                } catch (Exception e) {
                    mProgressDialog.dismiss();
                    mUIHelper.showErrorDialog(e.getMessage());
                }

            } else if (sResponseCode.equals("ERROR")) {
                mProgressDialog.dismiss();
                mUIHelper.showLongToast(R.string.unable_to_login);
            }

        }
    };


    public void sendFirebaseIdToServer(final String customerId,final String emailId){
        thrdSendFirebaseIdToServer = new Thread() {
            @Override
            public void run() {

                Message msg = new Message();
                Bundle bndle = new Bundle();
                String sCode = null, sDesc = null;
                try {
                    RestClientAcessTask client = new RestClientAcessTask(LoginActivity.this, FUNC_PUSH);
                    client.addParam("customer_id", customerId);
                    client.addParam("email", emailId);
                    client.addParam("firebase_reg_id", fireBaseTokenId);
                    Log.d("customer_id","-->"+customerId);
                    Log.d("email","-->"+emailId);
                    Log.d("firebase_reg_id","-->"+fireBaseTokenId);
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
                ctgSendFirebaseIdToServerProgressHandler.sendMessage(msg);
            }
        };
        thrdSendFirebaseIdToServer.start();
    }
    private Handler ctgSendFirebaseIdToServerProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                thrdSendFirebaseIdToServer.interrupt();
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
                        String result = jsonObject.getString("data");
                        //JSONArray jsonArray = jsonObject.getJSONArray("data");
                        Log.d("FirebaseIdToServer", "-->" + result);
                        initDataLoadCompleted(mStatus);
                    }

                } catch (Exception e) {
                    mProgressDialog.dismiss();
                    mUIHelper.showErrorDialog(e.getMessage());
                }

            } else if (sResponseCode.equals("ERROR")) {
                mProgressDialog.dismiss();
                mUIHelper.showLongToast(R.string.unable_to_send_reg_id_to_server);
            }

        }
    };
    public void initDataLoadCompleted(final String status) {
        ctgSendFirebaseIdToServerProgressHandler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                mProgressDialog.dismiss();
                if(status.equals("SUCCESS")){
                    mIntent.setClass(LoginActivity.this, HomeActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mIntent);
                    finish();
                }
            }
        }, 1000);

    }
/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
        menu.findItem(R.id.ok).setVisible(true);
        menu.findItem(R.id.Login).setVisible(false);
        menu.findItem(R.id.SignOut).setVisible(false);
        menu.findItem(R.id.UserName).setVisible(false);
        menu.findItem(R.id.Search).setVisible(false);
        menu.findItem(R.id.Products).setVisible(false);
        menu.findItem(R.id.Promotions).setVisible(false);
        menu.findItem(R.id.MyCart).setVisible(false);
        menu.findItem(R.id.AboutUs).setVisible(false);
        menu.findItem(R.id.ContactUs).setVisible(false);
        menu.findItem(R.id.PrivacyPolicy).setVisible(false);
        menu.findItem(R.id.SignOut).setVisible(false);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ok:
                // TODO Something when menu item selected
                attemptLogin();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/
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
    protected void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    @Override
    public void onCancel(DialogInterface dialog) {
        mProgressDialog.dismiss();
    }
}


