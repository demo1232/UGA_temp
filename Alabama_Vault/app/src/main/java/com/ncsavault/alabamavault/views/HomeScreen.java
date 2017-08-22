package com.ncsavault.alabamavault.views;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.bottomnavigation.BottomNavigationBar;
import com.ncsavault.alabamavault.bottomnavigation.NavigationPage;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.dto.CatagoriesTabDao;
import com.ncsavault.alabamavault.dto.PlaylistDto;
import com.ncsavault.alabamavault.dto.TabBannerDTO;
import com.ncsavault.alabamavault.dto.VideoDTO;
import com.ncsavault.alabamavault.fragments.views.BaseFragment;
import com.ncsavault.alabamavault.fragments.views.CatagoriesFragment;
import com.ncsavault.alabamavault.fragments.views.FeaturedFragment;
import com.ncsavault.alabamavault.fragments.views.HomeFragment;
import com.ncsavault.alabamavault.fragments.views.PlaylistFragment;
import com.ncsavault.alabamavault.fragments.views.ProfileFragment;
import com.ncsavault.alabamavault.fragments.views.SavedVideoFragment;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.models.BannerDataModel;
import com.ncsavault.alabamavault.models.BaseModel;
import com.ncsavault.alabamavault.service.TrendingFeaturedVideoService;
import com.ncsavault.alabamavault.service.VideoDataService;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by gauravkumar.singh on 5/16/2017.
 */

public class HomeScreen extends AppCompatActivity implements BottomNavigationBar.BottomNavigationMenuClickListener, AbstractView
        , OnFragmentToucheded {
    private static final String SELECTED_ITEM = "arg_selected_item";

    private int mSelectedItem;
    public static int count = 10; //ViewPager items size
    // helper class for handling UI and click events of bottom-nav-bar
    private BottomNavigationBar mBottomNav;

    // list of Navigation pages to be shown
    private List<NavigationPage> mNavigationPageList = new ArrayList<>();
    /**
     * You shouldn't define first page = 0.
     * Let define firstpage = 'number viewpager size' to make endless carousel
     */
    public static int FIRST_PAGE = 10;
    public final static int LOOPS = 1000;

    public static Activity activity;
    public static int[] listItems = {R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4,
            R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4,
            R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4};

    private String navigationPageArray[] = {"Home", "Catagories", "Saved", "Settings"};

    private Fragment navigationPageFragment[] = {HomeFragment.newInstance(this), CatagoriesFragment.newInstance(this),
            SavedVideoFragment.newInstance(this), ProfileFragment.newInstance(this, 20, 20)};
    public SearchView searchView;

    private int[] bottomTabIcons = {R.drawable.home_icon, R.drawable.categories, R.drawable.video_save_bottom,
            R.drawable.user_profile};

    public static Toolbar mToolbar;
    Animation animation;

    public ImageView imageViewSearch;
    public ImageView imageViewBackNavigation;
    public EditText editTextSearch;
    public ImageView imageViewLogo;
    public TextView textViewEdit;

    public static ProgressBar autoRefreshProgressBar;
    private BannerDataModel mBannerDataModel;
    Handler autoRefreshHandler = new Handler();



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_layout);

        activity = this;
        loadUniversalImageLoader();

        MobileAds.initialize(this, GlobalConstants.ADMOB_APP_ID);

        initializeToolbarIcons();

        View autoRefreshView = findViewById(R.id.auto_refresh_progress_main);
        autoRefreshProgressBar = (ProgressBar) autoRefreshView.findViewById(R.id.auto_refresh_progress_bar);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            autoRefreshProgressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.circle_progress_bar_lower));
        } else {
            autoRefreshProgressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.progress_large_material, null));
        }

        loadBottomNavigationItems();

        AppController.getInstance().setCurrentActivity(activity);
        autoRefresh();
    }

    private void initializeToolbarIcons() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBar);
        appBarLayout.setExpanded(false, true);

        // mToolbar.setTitle("UGAVAULT");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //toolbar.setNavigationIcon(R.drawable.ic_toolbar);
        mToolbar.setTitle("");
        mToolbar.setSubtitle("");

        imageViewSearch = (ImageView) mToolbar.findViewById(R.id.imageview_search);
        editTextSearch = (EditText) mToolbar.findViewById(R.id.editText_search);
        imageViewLogo = (ImageView) mToolbar.findViewById(R.id.imageview_logo);
        textViewEdit = (TextView) findViewById(R.id.textview_edit);
        imageViewBackNavigation = (ImageView) findViewById(R.id.imageview_back);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    public void autoRefresh() {
        autoRefreshHandler.postDelayed(autoRefreshRunnable, GlobalConstants.AUTO_REFRESH_INTERVAL);

    }

    private Runnable autoRefreshRunnable = new Runnable() {
        @Override
        public void run() {

            System.out.println("auto refresh time : " + Calendar.getInstance().getTime());
            loadAutoRefreshData();
        }
    };

    ArrayList<String> listUrl = new ArrayList<>();

    public void loadAutoRefreshData() {
        try {
            if (autoRefreshProgressBar != null) {

                if (autoRefreshProgressBar.isShown()) {

                    return;
                }
                autoRefreshProgressBar.setVisibility(View.VISIBLE);
            }

            stopService(new Intent(HomeScreen.this, TrendingFeaturedVideoService.class));

            listUrl.add(GlobalConstants.CATEGORIES_TAB_URL);
            listUrl.add(GlobalConstants.CATEGORIES_PLAYLIST_URL);
            listUrl.add(GlobalConstants.PLAYLIST_VIDEO_URL);

            AutoRefreshData autoRefreshData = new AutoRefreshData();
            autoRefreshData.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ArrayList<VideoDTO> arrayListFeatured = new ArrayList<>();
    ArrayList<PlaylistDto> playlistDtoArrayList = new ArrayList<>();
    ArrayList<VideoDTO> arrayListVideos = new ArrayList<>();

    private class AutoRefreshData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<TabBannerDTO> arrayListBanner = new ArrayList<TabBannerDTO>();
            Intent broadCastIntent = new Intent();
            try {
                arrayListBanner.addAll(AppController.getInstance().getServiceManager().getVaultService().getAllTabBannerData());
                File imageFile;
                for (TabBannerDTO bDTO : arrayListBanner) {
                    TabBannerDTO localBannerData = VaultDatabaseHelper.getInstance(getApplicationContext())
                            .getLocalTabBannerDataByTabId(bDTO.getTabId());
                    if (bDTO.getTabName().toLowerCase().contains((GlobalConstants.FEATURED).toLowerCase())) {
                        AppController.getInstance().getModelFacade().getLocalModel().setTabId(bDTO.getTabId());
                    }
                    if (localBannerData != null) {
                        if ((localBannerData.getBannerModified() != bDTO.getBannerModified()) ||
                                (localBannerData.getBannerCreated() != bDTO.getBannerCreated())) {
                            VaultDatabaseHelper.getInstance(getApplicationContext()).updateBannerData(bDTO);
                        }

                        if (localBannerData.getTabDataModified() != bDTO.getTabDataModified()) {
                            VaultDatabaseHelper.getInstance(getApplicationContext()).updateTabData(bDTO);

                            String url = GlobalConstants.FEATURED_API_URL + "userId=" + AppController.getInstance().
                                    getModelFacade().getLocalModel().getUserId();
                            try {
                                arrayListFeatured.clear();
                                arrayListFeatured.addAll(AppController.getInstance().getServiceManager().getVaultService().
                                        getVideosListFromServer(url));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            VaultDatabaseHelper.getInstance(getApplicationContext()).insertVideosInDatabase(arrayListVideos);

                            url = GlobalConstants.GET_TRENDING_PLAYLIST_URL + "userId=" + AppController.getInstance().
                                    getModelFacade().getLocalModel().getUserId();
                            ArrayList<VideoDTO> trendingArraylist = new ArrayList<>();
                            trendingArraylist.clear();
                            trendingArraylist.addAll(AppController.getInstance().getServiceManager().getVaultService().
                                    getVideosListFromServer(url));
                            VaultDatabaseHelper.getInstance(getApplicationContext()).
                                    insertTrendingVideosInDatabase(trendingArraylist);

                            imageFile = ImageLoader.getInstance().getDiscCache().get(localBannerData.getBannerURL());
                            if (imageFile.exists()) {
                                imageFile.delete();
                            }
                            MemoryCacheUtils.removeFromCache(localBannerData.getBannerURL(),
                                    ImageLoader.getInstance().getMemoryCache());
                            broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                            arrayListVideos.clear();
                        }

                    } else {
                        VaultDatabaseHelper.getInstance(getApplicationContext()).insertTabBannerData(bDTO);
                    }
                }

                broadCastIntent.setAction(HomeFragment.HomeResponseReceiver.ACTION_RESP);
                broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                sendBroadcast(broadCastIntent);

                SharedPreferences pref = AppController.getInstance().getApplicationContext().
                        getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
                long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);

                ArrayList<CatagoriesTabDao> catagoriesListData = new ArrayList<>();
                String categoriesUrl = GlobalConstants.CATEGORIES_TAB_URL + "userid=" + userId;

                catagoriesListData.addAll(AppController.getInstance().getServiceManager()
                        .getVaultService().getCategoriesData(categoriesUrl));

                for (CatagoriesTabDao catagoriesTabDao : catagoriesListData) {
                    CatagoriesTabDao localCatoriesData = VaultDatabaseHelper.getInstance(getApplicationContext())
                            .getLocalCategoriesDataByCategoriesId(catagoriesTabDao.getCategoriesId());
                    if (localCatoriesData != null) {
                        if (localCatoriesData.getCategories_modified() != catagoriesTabDao.getCategories_modified()) {
                            VaultDatabaseHelper.getInstance(getApplicationContext()).updateCategoriesData(catagoriesTabDao);
                            try {

                                String videoUrl = GlobalConstants.VIDEO_LIST_BY_CATEGORIES_ID_URL + "userid=" + userId +
                                        "&nav_tab_id=" + catagoriesTabDao.getCategoriesId();

                                arrayListVideos.clear();
                                arrayListVideos.addAll(AppController.getInstance().getServiceManager().
                                        getVaultService().getNewVideoData(videoUrl));

                                            VaultDatabaseHelper.getInstance(getApplicationContext()).
                                                    insertVideosInDatabase(arrayListVideos);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    String url = GlobalConstants.CATEGORIES_PLAYLIST_URL + "userid=" + userId + "&nav_tab_id="
                            + catagoriesTabDao.getCategoriesId();

                                playlistDtoArrayList.clear();
                                playlistDtoArrayList.addAll(AppController.getInstance().getServiceManager().
                                        getVaultService().getPlaylistData(url));

                                for (PlaylistDto playlistDto : playlistDtoArrayList) {
                                    PlaylistDto localPlaylistDto = VaultDatabaseHelper.getInstance(getApplicationContext())
                                            .getLocalPlaylistDataByPlaylistId(playlistDto.getPlaylistId());

                                    if (localPlaylistDto != null) {
                                        if (localPlaylistDto.getPlaylist_modified() != playlistDto.getPlaylist_modified()) {
                                            VaultDatabaseHelper.getInstance(getApplicationContext()).
                                                    insertPlaylistTabData(playlistDtoArrayList, catagoriesTabDao.getCategoriesId());


                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    }
               }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (autoRefreshProgressBar != null) {
                autoRefreshProgressBar.setVisibility(View.GONE);
            }
            autoRefreshHandler.postDelayed(autoRefreshRunnable, GlobalConstants.AUTO_REFRESH_INTERVAL);

        }
    }


    @Override
    public void update() {

        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mBannerDataModel != null && mBannerDataModel.getState() == BaseModel.STATE_SUCCESS) {
                        mBannerDataModel.unRegisterView(HomeScreen.this);
                        if (autoRefreshProgressBar != null) {
                            autoRefreshProgressBar.setVisibility(View.GONE);
                        }
                        autoRefreshHandler.postDelayed(autoRefreshRunnable, GlobalConstants.AUTO_REFRESH_INTERVAL);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onFragmentTouched(Fragment fragment, float x, float y) {


        if (fragment instanceof BaseFragment) {

            final BaseFragment theFragment = (BaseFragment) fragment;

            Animator unreveal = theFragment.prepareUnrevealAnimator(x, y);

            unreveal.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    // remove the fragment only when the animation finishes
                    //  getFragmentManager().beginTransaction().remove(theFragment).commit();
                    //to prevent flashing the fragment before removing it, execute pending transactions inmediately
                    getFragmentManager().executePendingTransactions();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            unreveal.start();
        }


    }


    private void loadUniversalImageLoader() {
        File cacheDir = StorageUtils.getCacheDirectory(HomeScreen.this);
        ImageLoaderConfiguration config;
        config = new ImageLoaderConfiguration.Builder(HomeScreen.this)
                .threadPoolSize(3) // default
                .denyCacheImageMultipleSizesInMemory()
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCache(new UnlimitedDiscCache(cacheDir)) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init(config);
    }


    /**
     * Method used to load all the bottom navigation pages..
     */
    private void loadBottomNavigationItems() {
        List<NavigationPage> navigationPages = new ArrayList<>();
        for (int i = 0; i < navigationPageArray.length; i++) {
            NavigationPage bottomNavagationPage = new NavigationPage(navigationPageArray[i],
                    ContextCompat.getDrawable(this, bottomTabIcons[i]),
                    navigationPageFragment[i]);
            navigationPages.add(bottomNavagationPage);
        }
        setupBottomBarHolderActivity(navigationPages);
    }

    /**
     * initializes the BottomBarHolderActivity with sent list of Navigation pages
     *
     * @param pages
     */
    public void setupBottomBarHolderActivity(List<NavigationPage> pages) {
        // throw error if pages does not have 4 elements
        if (pages.size() != 4) {
            throw new RuntimeException("List of NavigationPage must contain 5 members.");
        } else {
            mNavigationPageList = pages;
            mBottomNav = new BottomNavigationBar(this, pages, this);
            setupFragments();
        }

    }

    /**
     * sets up the fragments with initial view
     */
    private void setupFragments() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, mNavigationPageList.get(0).getFragment(), mNavigationPageList.get(0).getFragment().getClass().getName());
        fragmentTransaction.commit();
    }

    /**
     * handling onclick events of bar items
     *
     * @param menuType
     */
    @Override
    public void onClickedOnBottomNavigationMenu(int menuType) {

        // finding the selected fragment
        Fragment fragment = null;
        switch (menuType) {
            case BottomNavigationBar.MENU_BAR_1:
                fragment = mNavigationPageList.get(0).getFragment();
                break;
            case BottomNavigationBar.MENU_BAR_2:
                fragment = mNavigationPageList.get(1).getFragment();

                break;
            case BottomNavigationBar.MENU_BAR_3:
                fragment = mNavigationPageList.get(2).getFragment();

                break;
            case BottomNavigationBar.MENU_BAR_4:
                fragment = mNavigationPageList.get(3).getFragment();
                break;

        }

        // replacing fragment with the current one
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment, fragment.getClass().getName());
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_ITEM, mSelectedItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//            getSupportFragmentManager().beginTransaction().remove(getCurrentFragment()).commit();
            getSupportFragmentManager().popBackStack();
            /*View view = findViewById(R.id.bottom_navigation);
            Fragment fragment = getCurrentFragment();

            if (fragment instanceof HomeFragment) {
                LinearLayout ll1 = (LinearLayout) view.findViewById(R.id.linearLayoutBar1);
                mBottomNav.setView(ll1);
            } else if (fragment instanceof CatagoriesFragment) {
                LinearLayout ll2 = (LinearLayout) view.findViewById(R.id.linearLayoutBar2);
                mBottomNav.setView(ll2);

            } else if (fragment instanceof SavedVideoFragment) {
                LinearLayout ll3 = (LinearLayout) view.findViewById(R.id.linearLayoutBar3);
                mBottomNav.setView(ll3);

            } else if (fragment instanceof ProfileFragment) {
                LinearLayout ll4 = (LinearLayout) view.findViewById(R.id.linearLayoutBar2);
                mBottomNav.setView(ll4);
            }*/


        } else {
            super.onBackPressed();
        }
    }

    private Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
        Fragment currentFragment = fragmentManager.findFragmentByTag(fragmentTag);
        return currentFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    public void showToastMessage(String message) {
        View includedLayout = findViewById(R.id.llToast);

        final TextView text = (TextView) includedLayout.findViewById(R.id.tv_toast_message);
        text.setText(message);

        animation = AnimationUtils.loadAnimation(this,
                R.anim.abc_fade_in);

        text.setAnimation(animation);
        text.setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animation = AnimationUtils.loadAnimation(HomeScreen.this,
                        R.anim.abc_fade_out);

                text.setAnimation(animation);
                text.setVisibility(View.GONE);
            }
        }, 2000);
    }


}
