package com.ncsavault.alabamavault.views;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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
import android.widget.Button;
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
import com.ncsavault.alabamavault.fragments.views.HomeFragment;
import com.ncsavault.alabamavault.fragments.views.PlaylistFragment;
import com.ncsavault.alabamavault.fragments.views.ProfileFragment;
import com.ncsavault.alabamavault.fragments.views.SavedVideoFragment;
import com.ncsavault.alabamavault.fragments.views.VideoDetailFragment;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.models.BannerDataModel;
import com.ncsavault.alabamavault.models.BaseModel;
import com.ncsavault.alabamavault.service.TrendingFeaturedVideoService;
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
         {
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

    private int[] bottomTabIcons = {R.drawable.tab_home, R.drawable.tab_category, R.drawable.tab_saved,
            R.drawable.tab_profile};

    public static Toolbar mToolbar;
    Animation animation;

    public ImageView imageViewSearch;
    public ImageView imageViewBackNavigation;
    public EditText editTextSearch;
    public ImageView imageViewLogo;
    public TextView textViewEdit;
    public LinearLayout linearLayoutToolbarText;
    TextView textViewToolbatText1;
    TextView textViewToolbatText2;

    public static ProgressBar autoRefreshProgressBar;
    private BannerDataModel mBannerDataModel;
    Handler autoRefreshHandler = new Handler();


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_layout);

        activity = this;

        File cacheDir = StorageUtils.getCacheDirectory(this);
        ImageLoaderConfiguration config;
        config = new ImageLoaderConfiguration.Builder(this)
                .threadPoolSize(5) // default
                .denyCacheImageMultipleSizesInMemory()
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .build();
        ImageLoader.getInstance().init(config);

        MobileAds.initialize(this, GlobalConstants.ADMOB_APP_ID);

        initializeToolbarIcons();

        View autoRefreshView = findViewById(R.id.auto_refresh_progress_main);
        autoRefreshProgressBar = (ProgressBar) autoRefreshView.findViewById(R.id.auto_refresh_progress_bar);

//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            autoRefreshProgressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.circle_progress_bar_lower));
//        } else {
//            autoRefreshProgressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.progress_large_material, null));
//        }

        loadBottomNavigationItems();

        AppController.getInstance().setCurrentActivity(activity);
        autoRefresh();
        //gk setUpPullOptionHeader();
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

        linearLayoutToolbarText=(LinearLayout)mToolbar.findViewById(R.id.ll_toolbarText);
        textViewToolbatText1=(TextView)mToolbar.findViewById(R.id.textview_toolbar1);
        textViewToolbatText2=(TextView)mToolbar.findViewById(R.id.textview_toolbar2);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");
        Typeface faceNormal = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");
        textViewToolbatText2.setTypeface(face);
        textViewToolbatText1.setTypeface(faceNormal);
        imageViewSearch = (ImageView) mToolbar.findViewById(R.id.imageview_search);
        editTextSearch = (EditText) mToolbar.findViewById(R.id.editText_search);
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

     public void setUpPullOptionHeader() {
         final View pullView = findViewById(R.id.rl_pull_option);

         final SharedPreferences prefs = getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
         boolean isPullHeaderSeen = prefs.getBoolean(GlobalConstants.PREF_PULL_OPTION_HEADER, false);

         Button btnGotIt = (Button) pullView.findViewById(R.id.btn_got_it);

         btnGotIt.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 prefs.edit().putBoolean(GlobalConstants.PREF_PULL_OPTION_HEADER, true).commit();

                 Animation anim = AnimationUtils.loadAnimation(HomeScreen.this, R.anim.abc_fade_out);
                 pullView.setVisibility(View.GONE);
                 pullView.setAnimation(anim);
             }
         });

         if (isPullHeaderSeen) {
             pullView.setVisibility(View.GONE);
         }
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

            try {
                arrayListBanner.addAll(AppController.getInstance().getServiceManager().getVaultService().getAllTabBannerData());
                File imageFile;
                String url = "";
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

                            url = GlobalConstants.FEATURED_API_URL + "userId=" + AppController.getInstance().
                                    getModelFacade().getLocalModel().getUserId();
                            try {
                                arrayListFeatured.clear();
                                arrayListFeatured.addAll(AppController.getInstance().getServiceManager().getVaultService().
                                        getVideosListFromServer(url));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            VaultDatabaseHelper.getInstance(getApplicationContext()).insertVideosInDatabase(arrayListFeatured);

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
                            arrayListVideos.clear();
                        }

                    } else {
                        VaultDatabaseHelper.getInstance(getApplicationContext()).insertTabBannerData(bDTO);
                    }
                }


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

                }


                for (CatagoriesTabDao catagoriesTabDao : catagoriesListData) {

                    String playlistUrl = GlobalConstants.CATEGORIES_PLAYLIST_URL + "userid=" + userId + "&nav_tab_id="
                            + catagoriesTabDao.getCategoriesId();

                    playlistDtoArrayList.clear();
                    playlistDtoArrayList.addAll(AppController.getInstance().getServiceManager().
                            getVaultService().getPlaylistData(playlistUrl));

                    for (PlaylistDto playlistDto : playlistDtoArrayList) {
                        PlaylistDto localPlaylistDto = VaultDatabaseHelper.getInstance(getApplicationContext())
                                .getLocalPlaylistDataByPlaylistId(playlistDto.getPlaylistId());

                        if (localPlaylistDto != null) {
                            if (localPlaylistDto.getPlaylist_modified() != playlistDto.getPlaylist_modified()) {
                                VaultDatabaseHelper.getInstance(getApplicationContext()).
                                        insertPlaylistTabData(playlistDtoArrayList, catagoriesTabDao.getCategoriesId());

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
            Intent broadCastIntent = new Intent();
            broadCastIntent.setAction(HomeFragment.HomeResponseReceiver.ACTION_RESP);
            broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            sendBroadcast(broadCastIntent);

            Intent videoIntent = new Intent();
            videoIntent.setAction(VideoDetailFragment.VideoResponseReceiver.ACTION_RESP);
            videoIntent.addCategory(Intent.CATEGORY_DEFAULT);
            sendBroadcast(videoIntent);

            Intent playlistIntent = new Intent();
            playlistIntent.setAction(PlaylistFragment.PlaylistResponseReceiver.ACTION_RESP);
            playlistIntent.addCategory(Intent.CATEGORY_DEFAULT);
            sendBroadcast(playlistIntent);

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
        fragmentTransaction.addToBackStack(mNavigationPageList.get(0).getFragment().getClass().getName());
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

        try {

        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount > 1) {
            setFragmentIndicatorwithViews(backStackEntryCount);

        } else {
//            super.onBackPressed();
            finish();
        }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setFragmentIndicatorwithViews(int count) {
        Fragment fragment = null;
        String name = "";
        try {
//            if (count > 1) {
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().executePendingTransactions();
            name = getSupportFragmentManager().getBackStackEntryAt(count - 2).getName();
            fragment = getSupportFragmentManager().findFragmentByTag(name);
           /* } else {
                fragment=null;
//                name = getSupportFragmentManager().getBackStackEntryAt(count - 1).getName();
//                if (name.length() > 0) {
//                    fragment = getSupportFragmentManager().findFragmentByTag(name);
//                }
            }*/
            View view = findViewById(R.id.bottom_navigation);

            if (fragment instanceof HomeFragment) {
                LinearLayout ll1 = (LinearLayout) view.findViewById(R.id.linearLayoutBar1);
                mBottomNav.setView(ll1);
                for (int i = 1; i < count; i++) {
                    int backStackId = getSupportFragmentManager().getBackStackEntryAt(i).getId();
                    getSupportFragmentManager().popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }

            } else if (fragment instanceof CatagoriesFragment) {
                LinearLayout ll2 = (LinearLayout) view.findViewById(R.id.linearLayoutBar2);
                mBottomNav.setView(ll2);

            } else if (fragment instanceof SavedVideoFragment) {
                LinearLayout ll3 = (LinearLayout) view.findViewById(R.id.linearLayoutBar3);
                mBottomNav.setView(ll3);

            } else if (fragment instanceof ProfileFragment) {
                LinearLayout ll4 = (LinearLayout) view.findViewById(R.id.linearLayoutBar4);
                mBottomNav.setView(ll4);
            } else if (fragment instanceof PlaylistFragment) {
                LinearLayout ll2 = (LinearLayout) view.findViewById(R.id.linearLayoutBar2);
                mBottomNav.setView(ll2);
                int backStackId = getSupportFragmentManager().getBackStackEntryAt(count - 1).getId();
                getSupportFragmentManager().popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } else if (fragment instanceof VideoDetailFragment) {
                LinearLayout ll2 = (LinearLayout) view.findViewById(R.id.linearLayoutBar2);
                mBottomNav.setView(ll2);
                int backStackId = getSupportFragmentManager().getBackStackEntryAt(count - 1).getId();
                getSupportFragmentManager().popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
