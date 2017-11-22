package com.ncsavault.fragments.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import applicationId.R;
import com.ncsavault.utils.CarouselLinearLayout;
import com.ncsavault.views.HomeScreen;

/**
 * Class used for show the Horizontal list on Home Fragment.
 */
public class ItemFragment extends Fragment {

    private static final String POSITION = "position";
    private static final String SCALE = "scale";
    private int screenWidth;
    private int screenHeight;

    /**
     * Method used for create a new instance of Fragment.
     * @param context Reference of Context.
     * @param pos Particular fragment position.
     * @param scale scale
     * @return New fragment instance.
     */
    public static Fragment newInstance(HomeScreen context, int pos, float scale) {
        Bundle b = new Bundle();
        b.putInt(POSITION, pos);
        b.putFloat(SCALE, scale);

        return Fragment.instantiate(context, ItemFragment.class.getName(), b);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWidthAndHeight();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        final int position = this.getArguments().getInt(POSITION);
        float scale = this.getArguments().getFloat(SCALE);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(screenWidth / 2, screenHeight / 2);
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_image, container, false);

        TextView textView = linearLayout.findViewById(R.id.text);
        CarouselLinearLayout root = linearLayout.findViewById(R.id.root_container);
        ImageView imageView = linearLayout.findViewById(R.id.pagerImg);

        textView.setText("Carousel item: " + position);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageResource(HomeScreen.listItems[position]);

        root.setScaleBoth(scale);

        return linearLayout;
    }

    /**
     * Get device screen width and height
     */
    private void getWidthAndHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;
    }
}
