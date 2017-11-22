package com.ncsavault.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import applicationId.R;
import com.ncsavault.fragments.views.ItemFragment;
import com.ncsavault.utils.CarouselLinearLayout;
import com.ncsavault.views.HomeScreen;

/**
 * This class mainly used for creating Horizontal pager Adapter on top of the Home screen.
 */
public class CarouselPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

    public final static float BIG_SCALE = 1.4f;
    private final static float SMALL_SCALE = 0.5f;
    private final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
    private final HomeScreen context;
    private final FragmentManager fragmentManager;
    private float scale;
    private final ViewPager pager;

    /**
     *  Constructor
     * @param context Get the access of Home screen
     * @param fm Get the access of FragmentManager
     * @param viewPager Get the access of ViewPager here
     */
    public CarouselPagerAdapter(HomeScreen context, FragmentManager fm, ViewPager viewPager) {
        super(fm);
        this.pager = viewPager;
        this.fragmentManager = fm;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        // make the first pager bigger than others
        try {
            if (position == HomeScreen.FIRST_PAGE)
                scale = BIG_SCALE;
            else
                scale = SMALL_SCALE;

            position = position % HomeScreen.count;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ItemFragment.newInstance(context, position, scale);
    }

    @Override
    public int getCount() {
        int count = 0;
        try {
            count = HomeScreen.count * HomeScreen.LOOPS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        try {
            if (positionOffset >= 0f && positionOffset <= 1f) {
                CarouselLinearLayout cur = getRootView(position);
                CarouselLinearLayout next = getRootView(position + 1);

                cur.setScaleBoth(BIG_SCALE - DIFF_SCALE * positionOffset);
                next.setScaleBoth(SMALL_SCALE + DIFF_SCALE * positionOffset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @SuppressWarnings("ConstantConditions")
    private CarouselLinearLayout getRootView(int position) {
        return (CarouselLinearLayout) fragmentManager.findFragmentByTag(this.getFragmentTag(position))
                .getView().findViewById(R.id.root_container);
    }

    /**
     * Method we have used for tag the fragment using the position of the fragment.
     * @param position Set the position to tag the particular fragment.
     * @return The tag Fragment.
     */
    private String getFragmentTag(int position) {
        return "android:switcher:" + pager.getId() + ":" + position;
    }
}