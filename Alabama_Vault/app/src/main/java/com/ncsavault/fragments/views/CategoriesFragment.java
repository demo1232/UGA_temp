package com.ncsavault.fragments.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


import com.baoyz.widget.PullRefreshLayout;
import com.ncsavault.adapters.CategoriesAdapter;
import com.ncsavault.controllers.AppController;
import com.ncsavault.customviews.RecyclerViewDisable;
import com.ncsavault.database.VaultDatabaseHelper;
import com.ncsavault.dto.CategoriesTabDao;
import com.ncsavault.globalconstants.GlobalConstants;
import com.ncsavault.utils.Utils;
import com.ncsavault.views.HomeScreen;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import applicationId.R;

/**
 * Class using for show the list of categories item using categoriesAdapter
 * Also we are using pull to refresh functionality here.
 */

public class CategoriesFragment extends Fragment implements CategoriesAdapter.OnClickInterface {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private RecyclerView mRecyclerView;
    private final ArrayList<CategoriesTabDao> categoriesTabList = new ArrayList<>();
    private CategoriesAdapter mCategoriesAdapter;
    private PlaylistFragment playlistFragment;
    private PullRefreshLayout refreshLayout;
    private RecyclerView.OnItemTouchListener disable;

    /**
     * Method used for create a new instance of categories fragment.
     * @param context The reference of Context.
     * @return The value of fragment.
     */
    public static Fragment newInstance(Context context) {
        Fragment frag = new CategoriesFragment();
        mContext = context;
        return frag;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (refreshLayout != null) {
            refreshLayout.setEnabled(true);
            refreshLayout.setOnRefreshListener(refreshListener);
        }

    }

    @SuppressWarnings("UnusedAssignment")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.catagaroies_fragmnet_layout, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        getCategoriesDateFromDatabase();

    }

    /**
     * Method used for handle the call back of pull to refresh
     * And to call the PullRefreshTask.
     */
    private final PullRefreshLayout.OnRefreshListener refreshListener = new PullRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (!Utils.isInternetAvailable(mContext.getApplicationContext())) {
                refreshLayout.setEnabled(false);
                refreshLayout.setRefreshing(false);
            } else {
                refreshLayout.setEnabled(true);
                refreshLayout.setRefreshing(true);
                if (!categoriesTabList.isEmpty()) {
                    categoriesTabList.clear();
                }
                PullRefreshTask pullTask = new PullRefreshTask();
                pullTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    };

    /**
     * Class used to get the updated data from server using Async task
     * Used for to handle the pull to refresh functionality.
     */
    private class PullRefreshTask extends AsyncTask<Void, Void, ArrayList<CategoriesTabDao>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mRecyclerView != null) {
                mRecyclerView.setEnabled(false);
                mRecyclerView.addOnItemTouchListener(disable);
            }
            refreshLayout.setRefreshing(true);

        }

        @Override
        protected ArrayList<CategoriesTabDao> doInBackground(Void... params) {
            try {
                SharedPreferences pref = AppController.getInstance().getApplicationContext().
                        getSharedPreferences(mContext.getResources().getString(R.string.pref_package_name), Context.MODE_PRIVATE);
                long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);
                String url = GlobalConstants.CATEGORIES_TAB_URL + "userid=" + userId;
                categoriesTabList.addAll(AppController.getInstance().getServiceManager().getVaultService().getCategoriesData(url));

                Collections.sort(categoriesTabList, new Comparator<CategoriesTabDao>() {

                    @Override
                    public int compare(CategoriesTabDao lhs, CategoriesTabDao rhs) {
                        return Long.valueOf(lhs.getIndex_position())
                                .compareTo(rhs.getIndex_position());
                    }
                });

                for (CategoriesTabDao categoriesTabDao : categoriesTabList) {
                    CategoriesTabDao localCategoriesData = VaultDatabaseHelper.getInstance(mContext)
                            .getLocalCategoriesDataByCategoriesId(categoriesTabDao.getCategoriesId());
                    if (localCategoriesData != null) {
                        if (localCategoriesData.getCategories_modified() != categoriesTabDao.getCategories_modified()) {

                            VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).removeAllCategoriesTabData();
                            VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).insertCategoriesTabData(categoriesTabList);
                        }
                    } else {
                        VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).insertCategoriesTabData(categoriesTabList);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return categoriesTabList;
        }

        @Override
        protected void onPostExecute(final ArrayList<CategoriesTabDao> result) {
            super.onPostExecute(result);
            try {
                categoriesTabList.clear();
                categoriesTabList.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                        getAllLocalCategoriesTabData());

                Log.i("CategoriesFragment","Categories list : "+categoriesTabList.size());
                       mCategoriesAdapter = new CategoriesAdapter(CategoriesFragment.this,
                               categoriesTabList, getActivity());
                       mCategoriesAdapter.notifyDataSetChanged();
                       mRecyclerView.setHasFixedSize(true);
                       LinearLayoutManager llm = new LinearLayoutManager(mContext);
                       llm.setOrientation(LinearLayoutManager.VERTICAL);
                       mRecyclerView.setLayoutManager(llm);
                       mRecyclerView.setAdapter(mCategoriesAdapter);

                       mRecyclerView.removeOnItemTouchListener(disable);
                       refreshLayout.setRefreshing(false);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method used for initialize the view and layout.
     * @param view Reference of View
     */
    @SuppressWarnings("deprecation")
    private void initViews(View view) {
        mRecyclerView = view.findViewById(R.id.categories_recycler_view);
        disable = new RecyclerViewDisable();
        setToolbarIcons();
        ProgressBar progressBar = view.findViewById(R.id.progressbar);

        refreshLayout = view.findViewById(R.id.refresh_layout);

        refreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_RING);
        refreshLayout.setEnabled(false);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.circle_progress_bar_lower));
        } else {
            Log.d("progress bar","progress bar not showing ");
            progressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.progress_large_material, null));
        }
    }

    /**
     * Method used for set the toolbar icons and text in categories fragment.
     */
    private void setToolbarIcons() {
        ((HomeScreen) mContext).linearLayoutToolbarText.setVisibility(View.VISIBLE);
        ((HomeScreen) mContext).imageViewSearch.setVisibility(View.INVISIBLE);
        ((HomeScreen) mContext).textViewEdit.setVisibility(View.INVISIBLE);
        ((HomeScreen) mContext).imageViewBackNavigation.setVisibility(View.INVISIBLE);
        ((HomeScreen) mContext).textViewToolbarText1.setVisibility(View.VISIBLE);
        ((HomeScreen) mContext).textViewToolbarText2.setVisibility(View.VISIBLE);
        ((HomeScreen) mContext).textViewToolbarText1.setText(mContext.getResources().getString(R.string.app_name_text));
        ((HomeScreen) mContext).textViewToolbarText2.setText(mContext.getResources().getString(R.string.vault_text));
        Typeface faceNormal = Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-Bold.ttf");
        ((HomeScreen) getActivity()).textViewToolbarText2.setTypeface(faceNormal);
    }


    @Override
    public void onClick(CategoriesAdapter.CategoriesAdapterViewHolder v, final long tabPosition, final String categoriesTabName) {
        v.playlistImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistFragment = (PlaylistFragment) PlaylistFragment.newInstance(mContext, tabPosition, categoriesTabName);
                FragmentManager manager = ((HomeScreen) mContext).getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.container, playlistFragment, playlistFragment.getClass().getName());
                transaction.addToBackStack(playlistFragment.getClass().getName());
                transaction.commit();
            }
        });

    }

    /**
     * Method used for get the categories from data base.
     */
    private void getCategoriesDateFromDatabase() {
        categoriesTabList.clear();
        categoriesTabList.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                getAllLocalCategoriesTabData());

        Collections.sort(categoriesTabList, new Comparator<CategoriesTabDao>() {
            @Override
            public int compare(CategoriesTabDao lhs, CategoriesTabDao rhs) {

                return Long.valueOf(lhs.getIndex_position())
                        .compareTo(rhs.getIndex_position());
            }
        });
        mCategoriesAdapter = new CategoriesAdapter(CategoriesFragment.this, categoriesTabList, getActivity());
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(mCategoriesAdapter);
    }


}


