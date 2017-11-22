package com.ncsavault.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;

import applicationId.R;

import com.google.firebase.iid.FirebaseInstanceId;
import com.ncsavault.bottomnavigation.BottomNavigationBar;
import com.ncsavault.bottomnavigation.NavigationPage;
import com.ncsavault.controllers.AppController;
import com.ncsavault.database.VaultDatabaseHelper;
import com.ncsavault.dto.CategoriesTabDao;
import com.ncsavault.dto.PlaylistDto;
import com.ncsavault.dto.TabBannerDTO;
import com.ncsavault.dto.VideoDTO;
import com.ncsavault.fragments.views.CategoriesFragment;
import com.ncsavault.fragments.views.HomeFragment;
import com.ncsavault.fragments.views.PlaylistFragment;
import com.ncsavault.fragments.views.ProfileFragment;
import com.ncsavault.fragments.views.SavedVideoFragment;
import com.ncsavault.fragments.views.VideoDetailFragment;
import com.ncsavault.globalconstants.GlobalConstants;
import com.ncsavault.models.BannerDataModel;
import com.ncsavault.models.BaseModel;
import com.ncsavault.service.TrendingFeaturedVideoService;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 */

public class HomeScreen extends AppCompatActivity implements BottomNavigationBar.BottomNavigationMenuClickListener, AbstractView {
  private static final String SELECTED_ITEM = "arg_selected_item";
  private final String Tag = "HomeScreen";
  private final int mSelectedItem;
  public static int count = 10; //ViewPager items size
  // helper class for handling UI and click events of bottom-nav-bar
  private BottomNavigationBar mBottomNav;

  // list of Navigation pages to be shown
  private List<NavigationPage> mNavigationPageList = new ArrayList<>();
  /**
   * You shouldn't define first page = 0.
   * Let define first page = 'number viewpager size' to make endless carousel
   */
  public static int FIRST_PAGE = 10;
  public final static int LOOPS = 1000;

  @SuppressLint("StaticFieldLeak")
  public static Activity activity;
  public static int[] listItems = {R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4,
          R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4,
          R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4};

  private final String[] navigationPageArray = {"Home", "Categories", "Saved", "Settings"};

  private final Fragment[] navigationPageFragment = {HomeFragment.newInstance(this), CategoriesFragment.newInstance(this),
          SavedVideoFragment.newInstance(this), ProfileFragment.newInstance(this, 20, 20)};

  private final int[] bottomTabIcons = {R.drawable.tab_home, R.drawable.tab_category, R.drawable.tab_saved,
          R.drawable.tab_profile};

  private Animation animation;
  public ImageView imageViewSearch;
  public ImageView imageViewBackNavigation;
  public TextView textViewEdit;
  public LinearLayout linearLayoutToolbarText;
  public TextView textViewToolbarText1;
  public TextView textViewToolbarText2;
  private final BannerDataModel mBannerDataModel = new BannerDataModel();
  private ProgressBar autoRefreshProgressBar;
  private final Handler autoRefreshHandler = new Handler();
  private AsyncTask<Void, Void, Void> mRegisterTask;
  private String refreshedToken;

  public HomeScreen() {
    mSelectedItem = 0;
  }


  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.home_screen_layout);

    activity = this;

    MobileAds.initialize(this, GlobalConstants.ADMOB_APP_ID);

    initializeToolbarIcons();

    View autoRefreshView = findViewById(R.id.auto_refresh_progress_main);
    autoRefreshProgressBar = autoRefreshView.findViewById(R.id.auto_refresh_progress_bar);

    loadBottomNavigationItems();

    AppController.getInstance().setCurrentActivity(activity);
    autoRefresh();
    sendUserIdOnPushNotification();

  }

  /**
   * Method is used to show toolbar items
   */
  private void initializeToolbarIcons() {
    Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
    AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBar);
    appBarLayout.setExpanded(false, true);
    setSupportActionBar(mToolbar);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDisplayShowTitleEnabled(false);

    mToolbar.setTitle("");
    mToolbar.setSubtitle("");


    linearLayoutToolbarText = mToolbar.findViewById(R.id.ll_toolbarText);
    textViewToolbarText1 = mToolbar.findViewById(R.id.textview_toolbar1);
    textViewToolbarText2 = mToolbar.findViewById(R.id.textview_toolbar2);

    Typeface face = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");
    Typeface faceNormal = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");
    textViewToolbarText2.setTypeface(face);
    textViewToolbarText1.setTypeface(faceNormal);
    imageViewSearch = mToolbar.findViewById(R.id.image_view_search);
    textViewEdit =  findViewById(R.id.textview_edit);
    imageViewBackNavigation = findViewById(R.id.image_view_back);
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
    fragment.onActivityResult(requestCode, resultCode, data);
  }

  private void autoRefresh() {
    autoRefreshHandler.postDelayed(autoRefreshRunnable, GlobalConstants.AUTO_REFRESH_INTERVAL);

  }


  private final Runnable autoRefreshRunnable = new Runnable() {
    @Override
    public void run() {

      Log.d(Tag,"auto refresh time : " + Calendar.getInstance().getTime());
      loadAutoRefreshData();
    }
  };

  private final ArrayList<String> listUrl = new ArrayList<>();

  /**
   * Method is used load data after auto refresh
   */
  private void loadAutoRefreshData() {
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
      Log.d(Tag,"list URL " + listUrl.size());
      AutoRefreshData autoRefreshData = new AutoRefreshData();
      autoRefreshData.execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private final ArrayList<VideoDTO> arrayListFeatured = new ArrayList<>();
  private final ArrayList<PlaylistDto> playlistDtoArrayList = new ArrayList<>();
  private final ArrayList<VideoDTO> arrayListVideos = new ArrayList<>();

  /**
   * Async task is used for auto refresh function
   */
  private class AutoRefreshData extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
      ArrayList<TabBannerDTO> arrayListBanner = new ArrayList<>();

      try {
        arrayListBanner.addAll(AppController.getInstance().getServiceManager().getVaultService().getAllTabBannerData());
        @SuppressWarnings("UnusedAssignment") File imageFile;
        String url;
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

              arrayListVideos.clear();
            }

          } else {
            VaultDatabaseHelper.getInstance(getApplicationContext()).insertTabBannerData(bDTO);
          }
        }


        SharedPreferences pref = AppController.getInstance().getApplicationContext().
                getSharedPreferences(getResources().getString(R.string.pref_package_name), Context.MODE_PRIVATE);
        long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);

        ArrayList<CategoriesTabDao> categoriesListData = new ArrayList<>();
        String categoriesUrl = GlobalConstants.CATEGORIES_TAB_URL + "userid=" + userId;

        categoriesListData.addAll(AppController.getInstance().getServiceManager()
                .getVaultService().getCategoriesData(categoriesUrl));

        for (CategoriesTabDao categoriesTabDao : categoriesListData) {
          CategoriesTabDao localCategoriesData = VaultDatabaseHelper.getInstance(getApplicationContext())
                  .getLocalCategoriesDataByCategoriesId(categoriesTabDao.getCategoriesId());
          if (localCategoriesData != null) {
            if (localCategoriesData.getCategories_modified() != categoriesTabDao.getCategories_modified()) {
              VaultDatabaseHelper.getInstance(getApplicationContext()).updateCategoriesData(categoriesTabDao);
              try {

                String videoUrl = GlobalConstants.VIDEO_LIST_BY_CATEGORIES_ID_URL + "userid=" + userId +
                        "&nav_tab_id=" + categoriesTabDao.getCategoriesId();

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


        for (CategoriesTabDao categoriesTabDao : categoriesListData) {

          String playlistUrl = GlobalConstants.CATEGORIES_PLAYLIST_URL + "userid=" + userId + "&nav_tab_id="
                  + categoriesTabDao.getCategoriesId();

          playlistDtoArrayList.clear();
          playlistDtoArrayList.addAll(AppController.getInstance().getServiceManager().
                  getVaultService().getPlaylistData(playlistUrl));

          for (PlaylistDto playlistDto : playlistDtoArrayList) {
            PlaylistDto localPlaylistDto = VaultDatabaseHelper.getInstance(getApplicationContext())
                    .getLocalPlaylistDataByPlaylistId(playlistDto.getPlaylistId());

            if (localPlaylistDto != null) {
              if (localPlaylistDto.getPlaylist_modified() != playlistDto.getPlaylist_modified()) {
                VaultDatabaseHelper.getInstance(getApplicationContext()).
                        insertPlaylistTabData(playlistDtoArrayList, categoriesTabDao.getCategoriesId());

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


  /**
   * Method used to load all the bottom navigation pages.
   */
  private void loadBottomNavigationItems() {
    List<NavigationPage> navigationPages = new ArrayList<>();
    for (int i = 0; i < navigationPageArray.length; i++) {
      NavigationPage bottomNavigationPage = new NavigationPage(navigationPageArray[i],
              ContextCompat.getDrawable(this, bottomTabIcons[i]),
              navigationPageFragment[i]);
      navigationPages.add(bottomNavigationPage);
    }
    setupBottomBarHolderActivity(navigationPages);
  }

  /**
   * initializes the BottomBarHolderActivity with sent list of Navigation pages
   *
   * @param pages pages
   */
  private void setupBottomBarHolderActivity(List<NavigationPage> pages) {
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
   * @param menuType menuType
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
    Log.d("item", "item" + mSelectedItem);
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onBackPressed() {

    try {

      int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
      if (backStackEntryCount > 1) {
        setFragmentIndicatorWithViews(backStackEntryCount);

      } else {
        finish();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Method is used to show fragment indicator of view on bottom navigation on click of back button
   * On Click of back button, previous fragment is loaded and indicator set on bottom navigation
   * @param count set the value of number of fragment.
   */
  private void setFragmentIndicatorWithViews(int count) {
    Fragment fragment;
    String name;
    try {

      getSupportFragmentManager().popBackStack();
      getSupportFragmentManager().executePendingTransactions();
      name = getSupportFragmentManager().getBackStackEntryAt(count - 2).getName();
      fragment = getSupportFragmentManager().findFragmentByTag(name);

      View view = findViewById(R.id.bottom_navigation);

      if (fragment instanceof HomeFragment) {
        LinearLayout ll1 = view.findViewById(R.id.linearLayoutBar1);
        mBottomNav.setView(ll1);
        for (int i = 1; i < count; i++) {
          int backStackId = getSupportFragmentManager().getBackStackEntryAt(i).getId();
          getSupportFragmentManager().popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

      } else if (fragment instanceof CategoriesFragment) {
        LinearLayout ll2 = view.findViewById(R.id.linearLayoutBar2);
        mBottomNav.setView(ll2);

      } else if (fragment instanceof SavedVideoFragment) {
        LinearLayout ll3 = view.findViewById(R.id.linearLayoutBar3);
        mBottomNav.setView(ll3);

      } else if (fragment instanceof ProfileFragment) {
        LinearLayout ll4 = view.findViewById(R.id.linearLayoutBar4);
        mBottomNav.setView(ll4);
      } else if (fragment instanceof PlaylistFragment) {
        LinearLayout ll2 = view.findViewById(R.id.linearLayoutBar2);
        mBottomNav.setView(ll2);
        int backStackId = getSupportFragmentManager().getBackStackEntryAt(count - 1).getId();
        getSupportFragmentManager().popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
      } else if (fragment instanceof VideoDetailFragment) {
        LinearLayout ll2 = view.findViewById(R.id.linearLayoutBar2);
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

  /**
   * Method to show toast message
   * @param message set the message for show the toast.
   */
  @SuppressLint("PrivateResource")
  public void showToastMessage(String message) {
    View includedLayout = findViewById(R.id.llToast);

    final TextView text = includedLayout.findViewById(R.id.tv_toast_message);
    text.setText(message);

    animation = AnimationUtils.loadAnimation(this,
            R.anim.abc_fade_in);

    text.setAnimation(animation);
    text.setVisibility(View.VISIBLE);

    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @SuppressLint("PrivateResource")
      @Override
      public void run() {
        animation = AnimationUtils.loadAnimation(HomeScreen.this,
                R.anim.abc_fade_out);

        text.setAnimation(animation);
        text.setVisibility(View.GONE);
      }
    }, 2000);
  }


  /**
   * Method used for send the userId for particular user on server
   * to check push notification user
   */
  private void sendUserIdOnPushNotification()
  {
    refreshedToken = FirebaseInstanceId.getInstance().getToken();
    SharedPreferences pref = AppController.getInstance().
            getApplicationContext().getSharedPreferences(getResources().getString(R.string.pref_package_name), Context.MODE_PRIVATE);
    final long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);
    final boolean isAllowed = pref.getBoolean(GlobalConstants.PREF_IS_NOTIFICATION_ALLOW, false);

    mRegisterTask = new AsyncTask<Void, Void, Void>() {

      @Override
      protected Void doInBackground(Void... params) {
        Log.i("Utils", "Device Registration Id : = " + refreshedToken);
        @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String result = AppController.getInstance().getServiceManager().getVaultService().sendPushNotificationRegistration(GlobalConstants.PUSH_REGISTER_URL,
                refreshedToken, deviceId, isAllowed,userId);
        if (result != null) {
          Log.d("Response", "Response from server after registration : "
                  + result);
          if (result.toLowerCase().contains("success")) {


          }
        }
        return null;
      }

      @Override
      protected void onPostExecute(Void result) {
        mRegisterTask = null;
      }
    };

    mRegisterTask.execute();
  }


}
