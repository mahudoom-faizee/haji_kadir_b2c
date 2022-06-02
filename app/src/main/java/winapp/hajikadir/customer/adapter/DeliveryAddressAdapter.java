package winapp.hajikadir.customer.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.helper.SettingsManager;
import winapp.hajikadir.customer.model.Address;

/**
 * Created by user on 11-Nov-17.
 */

public class DeliveryAddressAdapter extends RecyclerView.Adapter<DeliveryAddressAdapter.CustomerAddressViewHolder> {
    public interface onEditClickListener{
        public void onCompleted(Address address);
    }
    public onEditClickListener editClickListener;
    public void setOnEditClickListener(onEditClickListener editClickListener){
        this.editClickListener = editClickListener;
    }
    public interface onDeleteClickListener{
        public void onCompleted(Address address,int position);
    }
    public onDeleteClickListener deleteClickListener;
    public void setOnDeleteClickListener(onDeleteClickListener deleteClickListener){
        this.deleteClickListener = deleteClickListener;
    }
    private ArrayList<Address> mCustomerAddressArr;
    private Activity activity;
    private SettingsManager mSettings;
    public DeliveryAddressAdapter(Activity activity, ArrayList<Address> mCustomerAddressArr){
     this.mSettings = new SettingsManager(activity);

     this.activity = activity;
     this.mCustomerAddressArr = mCustomerAddressArr;
    }
    @Override
    public CustomerAddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address,parent,false);
        return new CustomerAddressViewHolder(item);
    }
    public Address getItem(int position){
        return mCustomerAddressArr.get(position);
    }
    @Override
    public void onBindViewHolder(CustomerAddressViewHolder holder, int position) {
        Address address = getItem(position);
        String firstName = address.getFirstName();
        String lastName = address.getLastName();
        String address1 = address.getAddress1();
        String address2 = address.getAddress2();
        String postalCode = address.getPostalCode();
        String unitNo=address.getUnitNo();

        String mFirstName = "", mLastName = "";
        if(firstName.matches("null")){
            firstName = "";
        }
        if(lastName.matches("null")){
            lastName = "";
        }
        if(firstName!=null && !firstName.isEmpty()){
            mFirstName = firstName;
        }
        if(lastName!=null && !lastName.isEmpty()){
            mLastName = lastName;
        }
        holder.mName.setText(mFirstName+" "+mLastName);

        if(address1!=null && !address1.isEmpty()){
            holder.mAddress1.setText(address1);
        }else{
            holder.mAddress1.setVisibility(View.GONE);
        }
        if(address2!=null && !address2.isEmpty()){
            holder.mAddress2.setText(address2);
        }else{
            holder.mAddress2.setVisibility(View.GONE);
        }

        String unitNum = unitNo.replaceAll("\\s","");
        Log.d("UnitNumber",""+unitNum);
        if(unitNum!=null && !unitNum.isEmpty()){
            if(unitNum.matches("null")){
                holder.mUnitNo.setVisibility(View.GONE);
            }else{
                holder.mUnitNo.setText(unitNum);
            }

        } else {

            holder.mUnitNo.setVisibility(View.GONE);

        }

        holder.mPostalCode.setText(postalCode);
        holder.mRadioButton.setChecked(address.isChecked());
        holder.mRadioButton.setId(position);
        holder.mRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = v.getId();
                Address address = getItem(position);
                uncheckedAll();
                address.setChecked(true);
                notifyDataSetChanged();
                mSettings.updateCustomerDetail(address);
                Log.d("setBtnPosition",""+position);
                String value=String.valueOf(position);
                mSettings.setBtnPosition(value);
            }
        });
        holder.mAddressLayout.setId(position);
        holder.mAddressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = v.getId();
                Address address = getItem(position);
                uncheckedAll();
                address.setChecked(true);
                notifyDataSetChanged();
                mSettings.updateCustomerDetail(address);
                Log.d("setBtnPosition",""+position);
                String value=String.valueOf(position);
                mSettings.setBtnPosition(value);
            }
        });
        holder.mEditImg.setId(position);
        holder.mEditImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int position = v.getId();
                    Address address = getItem(position);
                    if (editClickListener != null) {
                        editClickListener.onCompleted(address);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        holder.mDeleteImg.setId(position);
        holder.mDeleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int position = v.getId();
                    Address address = getItem(position);
                    if (deleteClickListener != null) {
                        deleteClickListener.onCompleted(address,position);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCustomerAddressArr.size();
    }

    public class CustomerAddressViewHolder extends RecyclerView.ViewHolder{
        private TextView mName,mAddress1,mAddress2,mPostalCode,mUnitNo;
        private RadioButton mRadioButton;
        private LinearLayout mAddressLayout;
        private ImageView mEditImg,mDeleteImg;
        public CustomerAddressViewHolder(View itemView){
         super(itemView);
            mName = (TextView) itemView.findViewById(R.id.name);
            mAddress1 = (TextView) itemView.findViewById(R.id.address1);
            mAddress2 = (TextView) itemView.findViewById(R.id.address2);
            mPostalCode = (TextView) itemView.findViewById(R.id.postalCode);
            mRadioButton = (RadioButton) itemView.findViewById(R.id.radioButton);
            mAddressLayout = (LinearLayout) itemView.findViewById(R.id.addressLayout);
            mEditImg = (ImageView) itemView.findViewById(R.id.editImg);
            mDeleteImg = (ImageView) itemView.findViewById(R.id.deleteImg);
            mUnitNo=(TextView) itemView.findViewById(R.id.unitNo);
        }
    }
    private void uncheckedAll(){
        for(Address address : mCustomerAddressArr){
            address.setChecked(false);
        }

    }
    public void remove(int position){
        mCustomerAddressArr.remove(position);
        notifyDataSetChanged();
    }


}
