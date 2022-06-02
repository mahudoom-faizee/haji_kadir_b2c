package winapp.hajikadir.customer.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import winapp.hajikadir.customer.R;
import winapp.hajikadir.customer.activity.CartActivity;
import winapp.hajikadir.customer.activity.ProductActivity;
import winapp.hajikadir.customer.activity.ProductDetailActivity;
import winapp.hajikadir.customer.model.Modifier;
import winapp.hajikadir.customer.util.Utils;

/**
 * Created by user on 01-Apr-17.
 */

public class ModifierAdapter extends BaseAdapter {

    private ArrayList<Modifier> modifierList;
    private Activity activity;
    private LayoutInflater lInflater;
    private ViewHolder holder = null;
    private int resourceId;
    private TextView text;
    private String price="0";
    public ModifierAdapter() {
    }
    public ModifierAdapter(Activity activity,int resourceId,ArrayList<Modifier> modifierList) {
        this.resourceId = resourceId;
        this.activity = activity;
        this.modifierList = new ArrayList<Modifier>();
        this.modifierList = modifierList;
        lInflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return modifierList.size();
    }

    @Override
    public Object getItem(int position) {
        return modifierList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public Modifier getModifier(int position) {
        return ((Modifier) getItem(position));
    }
    private class ViewHolder {
        CheckBox name;
        public ViewHolder(View convertView) {
            name = (CheckBox) convertView.findViewById(R.id.checkBox);
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = lInflater.inflate(resourceId, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        Modifier modifier = getModifier(position);

        String modiferName = modifier.getName();
        String priceStr = modifier.getPrice();

        double dprice =0;
        if(priceStr!=null && !priceStr.isEmpty()){
            dprice = Double.valueOf(priceStr);
            priceStr = Utils.twoDecimalPoint(dprice);
        }
        String modiferPrice = "<font color='#299111'>"+" $"+priceStr+"</font>";
        String nullPrice = "<font color='#299111'>"+" $"+0.00+"</font>";

        if(dprice>0){
            holder.name.setText(Html.fromHtml(modiferName + modiferPrice));
        }else{
//            holder.name.setText(Html.fromHtml(modiferName));
            holder.name.setText(Html.fromHtml(modiferName+nullPrice));

        }

       // holder.name.setText(modifier.getName() +modiferPrice);
        holder.name.setOnCheckedChangeListener(myCheckChangList);
        holder.name.setTag(position);
        holder.name.setId(position);
        holder.name.setChecked(modifier.isSelected());
        /*if(activity instanceof CartActivity){
            holder.name.setBackgroundColor(activity.getResources().getColor(android.R.color.transparent));
        }*/

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity instanceof ProductDetailActivity || activity instanceof ProductActivity){
                     double dPrice = price.equals("") ? 0 : Double.valueOf(price);
                    double dModifierPrice =  getSelectedModifierPrice();
                    double totalPrice = dPrice + dModifierPrice;
                    text.setText(Utils.twoDecimalPoint(totalPrice));
                }
            }
        });
        return convertView;
    }
    public void setPrice(String price,TextView text){
        this.price = price;
        this.text = text;
    }
    public ArrayList<Modifier> getSelectedModifier() {
        ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
        if(modifierList.size()>0)
        for (Modifier m : modifierList) {
            if (m.isSelected())
                modifiers.add(m);
        }
        return modifiers;
    }
    public ArrayList<Modifier> getAllModifier() {
        ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
        if(modifierList.size()>0)
            for (Modifier m : modifierList) {
                    modifiers.add(m);
            }
        return modifiers;
    }
    public boolean isSelected(){
        if(modifierList.size()>0) {
            for (Modifier m : modifierList) {
                if (m.isSelected()) {
                    return true;
                }
            }
        }
        return false;
    }
    public void unCheckedAll(){
        try {
            if (modifierList.size() > 0){
                for (Modifier m : modifierList) {
                    m.setSelected(false);
                }
                notifyDataSetChanged();
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public double getSelectedModifierPrice() {
        double modifiers = 0.00;
       try{
           if(modifierList.size()>0)
               for (Modifier m : modifierList) {
                   if (m.isSelected()) {
                       String price = m.getPrice();
                       if (price != null && !price.isEmpty() && !price.equalsIgnoreCase("null")) {
                           modifiers += Double.valueOf(price);
                       }
                   }
                   }
       }catch (Exception e){
           e.printStackTrace();
       }
       return modifiers;
    }

    CompoundButton.OnCheckedChangeListener myCheckChangList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            getModifier((Integer) buttonView.getTag()).selected = isChecked;
        }
    };
}