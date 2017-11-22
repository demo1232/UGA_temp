package com.ncsavault.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ncsavault.globalconstants.GlobalConstants;
import com.ncsavault.utils.Utils;

import applicationId.R;

/**
 * Class used for user support if user want any help and suggestion from support.
 */
public class SupportActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSomethingWentWrong, buttonSuggestion, buttonHighlightRequest;
    private ImageView closeButton;
    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        initViews();
        setClickListeners();
    }

    /**
     * Method is used to set click listeners
     */
    private void setClickListeners() {
        buttonSomethingWentWrong.setOnClickListener(this);
        buttonSuggestion.setOnClickListener(this);
        buttonHighlightRequest.setOnClickListener(this);
        closeButton.setOnClickListener(this);
    }

    /**
     * Method is used to initialize views
     */
    private void initViews() {

        buttonSomethingWentWrong =  findViewById(R.id.button_somethingWentWrong);
        buttonHighlightRequest =  findViewById(R.id.button_highlight_request);
        buttonSuggestion =  findViewById(R.id.button_suggestion);
        closeButton =  findViewById(R.id.img_close);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_somethingWentWrong:
                if(Utils.isInternetAvailable(this))
               {
                startContactActivity(buttonSomethingWentWrong.getText().toString()
                        ,getResources().getString(R.string.support_tag_id));
               }else
               {
                   showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
               }

                break;
            case R.id.button_suggestion:
                if(Utils.isInternetAvailable(this))
                {
                    startContactActivity(buttonSuggestion.getText().toString()
                            ,getResources().getString(R.string.feedback_tag_id));
                }else
                {
                    showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                }

                break;
            case R.id.button_highlight_request:
                if(Utils.isInternetAvailable(this))
                {
                    startContactActivity(buttonHighlightRequest.getText().toString()
                            ,getResources().getString(R.string.clip_request_tag_id));
                }else
                {
                    showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                }

                break;
            case R.id.img_close:
                finish();
                break;
        }
    }

    /**
     * Method is used to start contact activity
     * @param buttonText set the button text string
     * @param tagId set the tag id.
     */
    private void startContactActivity(String buttonText,String tagId) {
        Intent intent = new Intent(this, ContactActivity.class);
        intent.putExtra("button_text", buttonText);
        intent.putExtra("tagId", tagId);
        startActivity(intent);
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
                animation = AnimationUtils.loadAnimation(SupportActivity.this,
                        R.anim.abc_fade_out);

                text.setAnimation(animation);
                text.setVisibility(View.GONE);
            }
        }, 2000);
    }


}
