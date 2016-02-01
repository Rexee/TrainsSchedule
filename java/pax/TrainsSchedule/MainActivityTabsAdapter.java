package pax.TrainsSchedule;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pax.TrainsSchedule.Model.Stop;

public class MainActivityTabsAdapter extends FragmentPagerAdapter {

    private final List<MainActivityTabFragment> mFragments = new ArrayList<>();

    public MainActivityTabsAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public void addFragment(String title) {
        addFragment(title, 0, false);
    }
    public void addFragment(String title, int resourceId, boolean isFav) {
        MainActivityTabFragment fragment = new MainActivityTabFragment();

        fragment.mTitle = title;
        fragment.mImage = resourceId;
        fragment.mIsFav = isFav;
        mFragments.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        MainActivityTabFragment curFragment = mFragments.get(position);
        return curFragment.mTitle;
    }

    public void newSchedule(Stop.List stopsList, boolean hasNextPage) {
        MainActivityTabFragment curFragment = mFragments.get(0);
        curFragment.newSchedule(stopsList, hasNextPage);
    }

    public void appendSchedule(Stop.List stopsList, boolean hasNextPage) {
        MainActivityTabFragment curFragment = mFragments.get(0);
        curFragment.appendSchedule(stopsList, hasNextPage);
    }

    public void onTabClick(int curTab) {
        MainActivityTabFragment curFragment = mFragments.get(curTab);
        curFragment.onSelect();
    }

    public void updateImages(Context context, TabLayout tabLayout) {

        for (MainActivityTabFragment fragment : mFragments) {
            if (fragment.mImage != 0) {
                LinearLayout customTabLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.main_activity_tab_layout, null);
                TextView t = (TextView) customTabLayout.findViewById(R.id.tabText);
                t.setText(fragment.mTitle);

                Drawable d = context.getResources().getDrawable(fragment.mImage);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                ImageView i = (ImageView) customTabLayout.findViewById(R.id.tabImage);
                i.setImageDrawable(d);

                tabLayout.getTabAt(mFragments.size() - 1).setCustomView(customTabLayout);
            }
        }
    }

    public void setTitle(int position, String dateRepresentation) {
        MainActivityTabFragment fragment = mFragments.get(position);
        fragment.mTitle = dateRepresentation;
    }

    public void updateFavorites() {
        MainActivityTabFragment fragment = mFragments.get(1);
        fragment.updateFavorites();
    }
}
