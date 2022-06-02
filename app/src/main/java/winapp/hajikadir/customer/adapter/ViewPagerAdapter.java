package winapp.hajikadir.customer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 15-Jul-16.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mFragmentTitleList = new ArrayList<>();
    private Map<Integer, String> mFragmentTags;
    private FragmentManager mFragmentManager;
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        this.mFragmentManager = fm;
        this.mFragmentTags = new HashMap<Integer,String>();
    }
    public ViewPagerAdapter(FragmentManager fm, List<Fragment> mFragmentList) {
        super(fm);
        this.mFragmentManager = fm;
        this.mFragmentList = mFragmentList;
        this.mFragmentTags = new HashMap<Integer,String>();
    }
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFrag(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
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