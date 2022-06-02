package winapp.hajikadir.customer.adapter;

/**
 * Created by user on 14-Jul-16.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import winapp.hajikadir.customer.fragment.ProductFragment;
import winapp.hajikadir.customer.model.Product;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int numOfTabs=0;
    private ArrayList<Product> mProductArrList;
    private Map<Integer, String> mFragmentTags;
    private FragmentManager mFragmentManager;
    public PagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.mFragmentManager = fm;
        this.numOfTabs = numOfTabs;
        this.mFragmentTags = new HashMap<Integer,String>();
    }
        @Override
    public Fragment getItem(int position) {

            Log.d("position","-->"+position);
       /* Bundle args = new Bundle();
        args.putSerializable(StringUtils.EXTRA_PRODUCT, mProductArrList);
        ProductFragment f = new ProductFragment();
        f.setArguments(args);
        return f;*/
            //ProductFragment f = new ProductFragment();
            //return f;

          /*  switch (position){
                case 0:
                    return new ProductFragment();
                default:
                    break;
            }*/
           // return new ProductFragment();
            ProductFragment mProductFragment = new ProductFragment();
            Bundle data = new Bundle();
            data.putInt("current_page",position);
            mProductFragment.setArguments(data);
            return mProductFragment;

    }
    @Override
    public int getItemPosition(Object object) {
        // TODO Auto-generated method stub
        return POSITION_NONE;
    }
    @Override
    public int getCount() {
        return numOfTabs;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);
        if (object instanceof Fragment) {
            Fragment fragment = (Fragment) object;
            String tag = fragment.getTag();
            mFragmentTags.put(position, tag);
        }
        return object;
    }
    public Fragment getFragment(int position) {
        Fragment fragment = null;
        String tag = mFragmentTags.get(position);
        if (tag != null) {
            fragment = mFragmentManager.findFragmentByTag(tag);
        }
        return fragment;
    }
}