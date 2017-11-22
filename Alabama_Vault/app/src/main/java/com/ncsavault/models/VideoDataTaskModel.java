package com.ncsavault.models;

import android.os.AsyncTask;
import android.util.Log;

import com.ncsavault.controllers.AppController;
import com.ncsavault.dto.VideoDTO;
import com.ncsavault.globalconstants.GlobalConstants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class used for get the video detail from server.
 */

public class VideoDataTaskModel extends BaseModel {

    private ArrayList<VideoDTO> videoDTO;

    /**
     * Method used for get the all video detail from server.
     * @param hashMap set the value in hash map.
     */
    public void loadVideoData(HashMap<String, String> hashMap) {
        VideoDataTask videoDataTask = new VideoDataTask();
        videoDataTask.execute(hashMap);
    }

    /**
     * Async class used for get the video detail from server using
     * video id, and tab Id
     */
    private class VideoDataTask extends AsyncTask<HashMap, Void, ArrayList<VideoDTO>> {
        @Override
        protected ArrayList<VideoDTO> doInBackground(HashMap... params) {
            ArrayList<VideoDTO> videoList;
            try {
                videoList = AppController.getInstance().getServiceManager().getVaultService().
                        getVideosListFromServer(GlobalConstants.GET_VIDEO_DATA_FROM_BANNER + "?navTabId=" +
                                params[0].get("TabId").toString() + "&videoId=" + params[0].get("VideoId").toString()
                                + "&userId=" + AppController.getInstance().getModelFacade().getLocalModel().getUserId());
                Log.d("Video List","Video List Size from server : " + videoList.size());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            videoDTO = videoList;
            state = STATE_SUCCESS;
            informViews();
            return videoList;
        }
    }

    public ArrayList<VideoDTO> getVideoDTO() {
        return videoDTO;
    }
}
