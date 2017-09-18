package com.ncsavault.alabamavault.fragments.views;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.baoyz.widget.PullRefreshLayout;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.adapters.CatagoriesAdapter;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.customviews.RecyclerViewDisabler;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.dto.CatagoriesTabDao;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.utils.Utils;
import com.ncsavault.alabamavault.views.HomeScreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by gauravkumar.singh on 8/4/2017.
 */

public class CatagoriesFragment extends Fragment implements CatagoriesAdapter.OnClickInterface {

    private static Context mContext;
    private RecyclerView mRecyclerView;
    public PlaylistFragment fragment = null;
    private ProgressBar progressBar;
    ArrayList<CatagoriesTabDao> catagoriesTabList = new ArrayList<>();
    CatagoriesAdapter mCatagoriesAdapter;
    PlaylistFragment playlistFragment;
    private PullRefreshLayout refreshLayout;
    PullRefreshTask pullTask;
    RecyclerView.OnItemTouchListener disable;

    public static Fragment newInstance(Context context) {
        Fragment frag = new CatagoriesFragment();
        mContext = context;
        Bundle args = new Bundle();
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

    PullRefreshLayout.OnRefreshListener refreshListener = new PullRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (!Utils.isInternetAvailable(mContext.getApplicationContext())) {
                refreshLayout.setEnabled(false);
                refreshLayout.setRefreshing(false);
            } else {
                refreshLayout.setEnabled(true);
                refreshLayout.setRefreshing(true);
                pullTask = new PullRefreshTask();
                pullTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    };

    public class PullRefreshTask extends AsyncTask<Void, Void, ArrayList<CatagoriesTabDao>> {

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
        protected ArrayList<CatagoriesTabDao> doInBackground(Void... params) {
            try {
                SharedPreferences pref = AppController.getInstance().getApplicationContext().
                        getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
                long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);
                String url = GlobalConstants.CATEGORIES_TAB_URL + "userid=" + userId;
                catagoriesTabList.clear();
                catagoriesTabList.addAll(AppController.getInstance().getServiceManager().getVaultService().getCategoriesData(url));

                Collections.sort(catagoriesTabList, new Comparator<CatagoriesTabDao>() {

                    @Override
                    public int compare(CatagoriesTabDao lhs, CatagoriesTabDao rhs) {
                        // TODO Auto-generated method stub
                        return Long.valueOf(lhs.getIndex_position())
                                .compareTo(Long.valueOf(rhs.getIndex_position()));
                    }
                });

                for (CatagoriesTabDao catagoriesTabDao : catagoriesTabList) {
                    CatagoriesTabDao localCatoriesData = VaultDatabaseHelper.getInstance(mContext)
                            .getLocalCategoriesDataByCategoriesId(catagoriesTabDao.getCategoriesId());
                    if (localCatoriesData != null) {
                        if (localCatoriesData.getCategories_modified() != catagoriesTabDao.getCategories_modified()) {

                            VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).removeAllCategoriesTabData();
                            VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).insertCategoriesTabData(catagoriesTabList);
                        }
                    }else
                    {
                        VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).insertCategoriesTabData(catagoriesTabList);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return catagoriesTabList;
        }

        @Override
        protected void onPostExecute(final ArrayList<CatagoriesTabDao> result) {
            super.onPostExecute(result);
            try {
                catagoriesTabList.clear();
                catagoriesTabList.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                        getAllLocalCategoriesTabData());

            if (mRecyclerView != null) {
                mCatagoriesAdapter = new CatagoriesAdapter(mContext, CatagoriesFragment.this, catagoriesTabList);
                mRecyclerView.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(mContext);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(llm);
                mRecyclerView.setAdapter(mCatagoriesAdapter);
                mRecyclerView.removeOnItemTouchListener(disable);
            }
               refreshLayout.setRefreshing(false);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void initViews(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.catagories_recycler_view);
        disable = new RecyclerViewDisabler();
        setToolbarIcons();
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);

        refreshLayout = (PullRefreshLayout) view.findViewById(R.id.refresh_layout);

        refreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_RING);
        refreshLayout.setEnabled(false);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.circle_progress_bar_lower));
        } else {
            System.out.println("progress bar not showing ");
            progressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.progress_large_material, null));
        }
    }

    private void setToolbarIcons() {
        ((HomeScreen)mContext).linearLayoutToolbarText.setVisibility(View.VISIBLE);
        ((HomeScreen) mContext).imageViewSearch.setVisibility(View.INVISIBLE);
        ((HomeScreen) mContext).textViewEdit.setVisibility(View.INVISIBLE);
        ((HomeScreen) mContext).imageViewBackNavigation.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onClick(CatagoriesAdapter.CatagoriesAdapterViewHolder v, final long tabPosition) {
        v.playlistImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistFragment = (PlaylistFragment) PlaylistFragment.newInstance(mContext, tabPosition);
                FragmentManager manager = ((HomeScreen) mContext).getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.container, playlistFragment, playlistFragment.getClass().getName());
                transaction.addToBackStack(playlistFragment.getClass().getName());
                transaction.commit();
            }
        });

    }

    private void getCatagoriesData() {
        final AsyncTask<Void, Void, ArrayList<CatagoriesTabDao>> mDbTask =
                new AsyncTask<Void, Void, ArrayList<CatagoriesTabDao>>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        if (progressBar != null) {
                            catagoriesTabList.clear();
                            if (catagoriesTabList.size() == 0) {
                                progressBar.setVisibility(View.VISIBLE);
                            } else {
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    protected ArrayList<CatagoriesTabDao> doInBackground(Void... params) {

                        try {
                            SharedPreferences pref = AppController.getInstance().getApplicationContext().
                                    getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
                            long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);
                            String url = GlobalConstants.CATEGORIES_TAB_URL + "userid=" + userId;
                            catagoriesTabList.clear();
                            catagoriesTabList.addAll(AppController.getInstance().getServiceManager().getVaultService().getCategoriesData(url));

                            Collections.sort(catagoriesTabList, new Comparator<CatagoriesTabDao>() {

                                @Override
                                public int compare(CatagoriesTabDao lhs, CatagoriesTabDao rhs) {
                                    // TODO Auto-generated method stub
                                    return Long.valueOf(lhs.getIndex_position())
                                            .compareTo(Long.valueOf(rhs.getIndex_position()));
                                }
                            });

                            VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).removeAllCategoriesTabData();
                            VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).insertCategoriesTabData(catagoriesTabList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return catagoriesTabList;
                    }

                    @Override
                    protected void onPostExecute(ArrayList<CatagoriesTabDao> result) {
                        super.onPostExecute(result);

                        if (progressBar != null) {
                            if (result.size() == 0) {
                                progressBar.setVisibility(View.VISIBLE);
                            } else {
                                progressBar.setVisibility(View.GONE);
                            }
                        }


                        if (mRecyclerView != null) {
                            mCatagoriesAdapter = new CatagoriesAdapter(mContext, CatagoriesFragment.this, result);
                            mRecyclerView.setHasFixedSize(true);
                            LinearLayoutManager llm = new LinearLayoutManager(mContext);
                            llm.setOrientation(LinearLayoutManager.VERTICAL);
                            mRecyclerView.setLayoutManager(llm);
                            mRecyclerView.setAdapter(mCatagoriesAdapter);
                        }
                        // ------- addBannerImage---------------------
                    }
                };

        mDbTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private void getCategoriesDateFromDatabase() {
        catagoriesTabList.clear();
        catagoriesTabList.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                getAllLocalCategoriesTabData());

        Collections.sort(catagoriesTabList, new Comparator<CatagoriesTabDao>() {
            @Override
            public int compare(CatagoriesTabDao lhs, CatagoriesTabDao rhs) {
                // TODO Auto-generated method stub
                return Long.valueOf(lhs.getIndex_position())
                        .compareTo(Long.valueOf(rhs.getIndex_position()));
            }
        });
        mCatagoriesAdapter = new CatagoriesAdapter(mContext, CatagoriesFragment.this, catagoriesTabList);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(mCatagoriesAdapter);
    }
    // }

}


