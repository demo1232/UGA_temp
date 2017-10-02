package com.ncsavault.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ncsavault.R;

public class SupportActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSomethingWentWrong, buttoSuggestion, buttonHighlightRequest;
    private TextView textViewToolbarTitle, textViewSupportYouNeed;
    private ImageView closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        initviews();
        setClickListeners();
    }

    private void setClickListeners() {
        buttonSomethingWentWrong.setOnClickListener(this);
        buttoSuggestion.setOnClickListener(this);
        buttonHighlightRequest.setOnClickListener(this);
    }

    private void initviews() {
        textViewToolbarTitle = (TextView) findViewById(R.id.textView_support);
        textViewSupportYouNeed = (TextView) findViewById(R.id.textView_supportYouNeed);
        buttonSomethingWentWrong = (Button) findViewById(R.id.button_somethingWentWrong);
        buttonHighlightRequest = (Button) findViewById(R.id.button_highlight_request);
        buttoSuggestion = (Button) findViewById(R.id.button_suggestion);
        closeButton = (ImageView) findViewById(R.id.img_close);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_somethingWentWrong:
                startContactActivity(buttonSomethingWentWrong.getText().toString());
                break;
            case R.id.button_suggestion:
                startContactActivity(buttoSuggestion.getText().toString());
                break;
            case R.id.button_highlight_request:
                startContactActivity(buttonHighlightRequest.getText().toString());
                break;
            case R.id.img_close:
                finish();
                break;
        }
    }

    private void startContactActivity(String buttonText) {
        Intent intent = new Intent(this, ContactActivity.class);
        intent.putExtra("button_text", buttonText);
        startActivity(intent);
    }


}
