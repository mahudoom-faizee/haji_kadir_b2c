package winapp.hajikadir.customer.helper;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;


import winapp.hajikadir.customer.model.NameValuePair;
import winapp.hajikadir.customer.util.Constants;

/**
 * Created by user on 13-Jul-16.
 */
public class RestClientAcessTask implements Constants {
    private Activity activity = null;
    private Context context = null;
    private ArrayList<NameValuePair> params;
    private Exception api_exception;
    private int connectionTimeOut = 60000;
    private String response = "",sCmd="", errorMsg = "",sUrl="", errorStackMsg = "";

    private Status status;
    public enum Status {
        NONE, SUCCESS, FAILED, ERROR
    };

    public RestClientAcessTask(Activity activity) {
        this.activity = activity;
        params = new ArrayList<NameValuePair>();
    }

    public RestClientAcessTask(Activity activity, String sCmd) {
        this.activity = activity;
        this.sCmd = sCmd;
        params = new ArrayList<NameValuePair>();
    }
    public RestClientAcessTask(Context context, String sCmd) {
        this.context = context;
        this.sCmd = sCmd;
        params = new ArrayList<NameValuePair>();
    }
    public void setCommand(String sCommand) {
        this.sCmd = sCommand;
    }

    public void addParam(String name, String value) {
        params.add(new NameValuePair(name, value));
    }

    public Status processAndFetchResponseWithoutParameter() {
        this.status = Status.NONE;
        if (activity != null)
            context = activity;
        NetworkInfo info = ((ConnectivityManager) context .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            this.status = Status.ERROR;
            this.errorMsg = "No Internet Connection!";
            return this.status;
        }
        HttpURLConnection urlConnection = null;
        try {
            String sURL = API_SERVER_URL + API_SERVICE_POINT + "/" + sCmd;
            Log.d("sURL","--->sURL"+sURL);
            URL url = new URL(sURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { //success
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                response = bufferedReader.readLine();
                Log.d("RESPONSE:","-->"+response);
                this.status = Status.SUCCESS;
            }else{ //Failed
                this.status = Status.FAILED;
            }
            Log.d("RESPONSE:","-->"+response);
        }
        catch (UnknownHostException uhEx) {
            this.status = Status.ERROR;
            this.errorMsg = "No Internet Connection!";
        }catch (SocketTimeoutException exTO) {
            this.status = Status.ERROR;
            this.errorMsg = Messages.ERROR_PRECEDE + exTO.getLocalizedMessage();
        } catch (Exception ex) {
            this.status = Status.ERROR;
            this.errorMsg = Messages.ERROR_PRECEDE + ex.getLocalizedMessage();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        Log.d("status = " ,""+ status);
        return status;

    }
    public void clearParam() {
        params.clear();
    }
    public Status processAndFetchResponseWithParameter() {
        this.status = Status.NONE;
        if (activity != null)
            context = activity;
        NetworkInfo info = ((ConnectivityManager) context .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            this.status = Status.ERROR;
            this.errorMsg = "No Internet Connection!";
            return this.status;
        }
        HttpURLConnection con = null;
        try {
            String sURL = API_SERVER_URL + API_SERVICE_POINT + "/" + sCmd;
            Log.d("sURL","--->sURL"+sURL);

            String combinedParams = "";
            if (params != null && !params.isEmpty()) {
                for (NameValuePair p : params) {
                    String name = p.getName();
                    String value = p.getValue();
                    Log.d("name",""+name);
                    Log.d("value",""+value);
                    combinedParams += "&" + name + "="
                            + URLEncoder.encode(value, "UTF-8");

                }
            }
            URL url = new URL(sURL);
            con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(CONNECTION_TIMEOUT);
            con.setConnectTimeout(CONNECTION_TIMEOUT);
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(combinedParams);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer mStringBuffer = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                mStringBuffer.append(inputLine);
            }
            in.close();
              response = mStringBuffer.toString();
                this.status = Status.SUCCESS;
            } else { //Failed
                this.status = Status.FAILED;
            }
            Log.d("RESPONSE:","-->"+response);
        }
        catch (UnknownHostException uhEx) {
            this.status = Status.ERROR;
            this.errorMsg = "No Internet Connection!";
        }catch (SocketTimeoutException exTO) {
            this.status = Status.ERROR;
            this.errorMsg = Messages.ERROR_PRECEDE + exTO.getLocalizedMessage();
        } catch (Exception ex) {
            this.status = Status.ERROR;
            this.errorMsg = Messages.ERROR_PRECEDE + ex.getLocalizedMessage();
        }
        finally {
            if (con != null) {
                con.disconnect();
            }
        }
        Log.d("status = " ,""+ status);
        return status;


    }

    public Exception getException() {
        return api_exception;
    }
    public String getResponse() {
        return response;
    }
    public Status getStatus() {
        return status;
    }
    public String getErrorStack() {
        return errorStackMsg;
    }

    public String getErrorMessage() {
        return errorMsg;
    }

}

