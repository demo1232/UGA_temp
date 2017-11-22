package com.ncsavault.models;

import android.os.AsyncTask;

import com.ncsavault.controllers.AppController;

import org.json.JSONObject;

import applicationId.R;

/**
 * Class used for create task on asana and also create tag that task.
 */

public class CreateTaskOnAsanaModel extends BaseModel {

    private boolean statusResult;
    private String taskId;


    /**
     * Constructor of the class
     * @param nameAndEmail set the name and email
     * @param taskMsg set the tag message
     * @param tagId set thr tag id.
     */
    public void loadAsanaData(String nameAndEmail,String taskMsg,String tagId)
    {
        AsanaTask asanaTask =  new AsanaTask();
        asanaTask.execute(nameAndEmail,taskMsg,tagId);
    }

    /**
     * Async class used for crate task on asana and generating ticket.
     */
    private class AsanaTask extends AsyncTask<String, Void, Void>
    {
        @Override
        protected Void doInBackground(String... params) {
            String task_id = "";
            boolean status = true;
            try {
                String result = AppController.getInstance().getServiceManager().getVaultService().createTaskOnAsana(params[0], params[1], params[2]);

                if (result != null) {
                    if (!result.isEmpty()) {
                        JSONObject jsonResult = new JSONObject(result);
                        if(jsonResult != null){
                            JSONObject jsonData = (JSONObject) jsonResult.get("data");
                            if(jsonData != null){
                                if(jsonData.getString("id") != null) {
                                    task_id = jsonData.getString("id");
                                    //Create tag for task type
                                    String tagResult;
                                    String[] tagValue = {params[2],AppController.getInstance()
                                            .getApplicationContext().getResources().getString(R.string.android_tag_id), AppController.getInstance()
                                            .getApplicationContext().getResources().getString(R.string.version_tag_id)};
                                    if(!params[2].isEmpty()) {

                                        for(int i = 0;i<tagValue.length;i++) {
                                            tagResult = AppController.getInstance().getServiceManager().getVaultService()
                                                    .createTagForAsanaTask(tagValue[i], task_id);

                                            status = tagResult.contains("\"data\":");
                                        }

                                    }

                                }
                            }
                        }
                    }
                }
                statusResult = status;
                taskId = task_id;
                state = STATE_SUCCESS;
                informViews();
            }catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Method used for get the result after task creation on asana.
     * @return the value of true and false.
     */
    public Boolean getStatusResult()
    {
        return statusResult;
    }

    /**
     * Method used for get the tag id.
     * @return the value of tag id.
     */
    public String getTaskId()
    {
        return taskId;
    }
}
