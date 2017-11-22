package com.ncsavault.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import applicationId.R;
import com.ncsavault.controllers.AppController;
import com.ncsavault.database.VaultDatabaseHelper;
import com.ncsavault.dto.VideoDTO;
import com.ncsavault.globalconstants.GlobalConstants;
import com.ncsavault.utils.Utils;
import com.ncsavault.views.HomeScreen;
import com.ncsavault.views.VideoInfoActivity;

import java.util.ArrayList;

/**
 * This class used to play the video when user click on push notification and social sharing
 */

@SuppressWarnings("deprecation")
public class VideoPlayTask extends AsyncTask<String, Void, VideoDTO> {

    private final Activity mActivity;
    private ProgressDialog pDialog;
    private final String videoCategory;

    public VideoPlayTask(Activity mActivity, ProgressDialog pDialog) {
        this.mActivity = mActivity;
        this.pDialog = pDialog;
        this.videoCategory = GlobalConstants.FEATURED;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(mActivity, R.style.CustomDialogTheme);
        pDialog.show();
        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(mActivity));
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
    }

    @Override
    protected VideoDTO doInBackground(String... params) {

        SharedPreferences pref = AppController.getInstance().getApplication()
                .getSharedPreferences(mActivity.getResources().getString(R.string.pref_package_name), Context.MODE_PRIVATE);
        long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);
        VideoDTO videoData;
        try {
            videoData = AppController.getInstance().getServiceManager().getVaultService().
                    getVideosDataFromServer(GlobalConstants.GET_VIDEO_DATA + "?videoid=" + params[0] + "&userid=" + userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return videoData;
    }


    @Override
    protected void onPostExecute(VideoDTO videoDTOs) {
        super.onPostExecute(videoDTOs);
        if (videoDTOs != null) {
            ArrayList<VideoDTO> videoDTOArrayList = new ArrayList<>();
            videoDTOArrayList.add(videoDTOs);
            VaultDatabaseHelper.getInstance(mActivity.getApplicationContext()).insertVideosInDatabase(videoDTOArrayList);
            if (Utils.isInternetAvailable(mActivity)) {
                if (videoDTOs.getVideoLongUrl() != null) {
                    if (videoDTOs.getVideoLongUrl().length() > 0 && !videoDTOs.getVideoLongUrl().toLowerCase().equals("none")) {
                        Intent intent = new Intent(mActivity,
                                VideoInfoActivity.class);
                        intent.putExtra(GlobalConstants.KEY_CATEGORY, videoCategory);
                        intent.putExtra(GlobalConstants.PLAYLIST_REF_ID, videoDTOs.getPlaylistReferenceId());
                        intent.putExtra(GlobalConstants.VIDEO_OBJ, videoDTOs);
                        mActivity.startActivity(intent);
                        mActivity.overridePendingTransition(R.anim.slide_up_video_info, R.anim.nochange);
                    } else {
                        ((HomeScreen) mActivity).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                    }
                } else {
                    ((HomeScreen) mActivity).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                }
            } else {
                ((HomeScreen) mActivity).showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
            }

        } else {
            ((HomeScreen) mActivity).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
        }
        pDialog.dismiss();
    }
}
