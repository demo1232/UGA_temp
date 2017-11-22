package com.ncsavault.bottomnavigation;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;

/**
 * A base class that holds the title, icon and an instance of the fragment to be shown as each
 * Navigation page
 *
 */

public class NavigationPage {

    private final String title;
    private final Drawable icon;
    private final Fragment fragment;

    public NavigationPage(String title, Drawable icon, Fragment fragment) {
        this.title = title;
        this.icon = icon;
        this.fragment = fragment;
    }

    public String getTitle() {
        return title;
    }

    public Drawable getIcon() {
        return icon;
    }

    public Fragment getFragment() {
        return fragment;
    }
}
