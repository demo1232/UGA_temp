package com.ncsavault.views;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import applicationId.R;
import com.ncsavault.controllers.AppController;
import com.ncsavault.globalconstants.GlobalConstants;
import com.ncsavault.models.BaseModel;
import com.ncsavault.models.CreateTaskOnAsanaModel;
import com.ncsavault.utils.CharacterCountErrorWatcher;
import com.ncsavault.utils.Utils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class used for contact with support
 * If user wants any help to support
 */
public class ContactActivity extends BaseActivity implements AbstractView {

    private TextView tvSubTitle,tvCharacterLimit;
    private ImageView tvClose;
    private Button tvSubmit;
    private EditText edMessage, edName, edEmail;
    private Animation animation;

    @SuppressWarnings("deprecation")
    private ProgressDialog pDialog;
    private String tagId = "";
    private AlertDialog alertDialog;
    private CreateTaskOnAsanaModel mCreateTaskOnAsanaModel;
    private LinearLayout linearLayoutWithoutLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_layout);

        initViews();
        initData();
        initListener();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Utils.getInstance().getHideKeyboard(this);
        overridePendingTransition(R.anim.nochange, R.anim.slidedown);
    }

    @Override
    public void initViews() {
        tvSubTitle = findViewById(R.id.tv_sub_title);
        tvClose = findViewById(R.id.img_close);
        tvSubmit = findViewById(R.id.tv_submit);
        tvCharacterLimit = findViewById(R.id.tv_character_limit);
        edMessage = findViewById(R.id.ed_message);
        Typeface faceNormal = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        edMessage.setTypeface(faceNormal);
        edName = findViewById(R.id.ed_name);
        edEmail = findViewById(R.id.ed_email);
        linearLayoutWithoutLogin = findViewById(R.id.linear_layout_without_login);
    }

    @Override
    public void initData() {
        long userID = AppController.getInstance().getModelFacade().getLocalModel().getUserId();
        if (userID == GlobalConstants.DEFAULT_USER_ID) {
            tvSubTitle.setText(getResources().getString(R.string.support_text_skip));
            edName.setVisibility(View.VISIBLE);
            edEmail.setVisibility(View.VISIBLE);
            linearLayoutWithoutLogin.setVisibility(View.VISIBLE);
            tagId=getResources().getString(R.string.no_login_tag_id);
        } else {

            String subTitle = getIntent().getStringExtra("button_text");
            tvSubTitle.setText(subTitle);
            tvSubTitle.setVisibility(View.VISIBLE);
            edName.setVisibility(View.GONE);
            edEmail.setVisibility(View.GONE);
            linearLayoutWithoutLogin.setVisibility(View.GONE);
            tagId = getIntent().getStringExtra("tagId");
        }

       
    }

    @Override
    public void initListener() {
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.getInstance().getHideKeyboard(ContactActivity.this);
                if (!edMessage.getText().toString().isEmpty()) {
                    showConfirmationDialog();
                } else {
                    onBackPressed();
                }
            }
        });

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.getInstance().getHideKeyboard(ContactActivity.this);
                boolean isChecked = true;
                long userId = AppController.getInstance().getModelFacade().getLocalModel().getUserId();
                String nameAndEmail = "";
                if (userId == GlobalConstants.DEFAULT_USER_ID) {
                    if (!isValidEmail(edEmail.getText().toString()))
                        isChecked = false;
                    if (!isValidText(edName.getText().toString())) {
                        isChecked = false;
                        edName.setError("Name should not be empty");
                    }
                    if (isChecked) {
                        nameAndEmail = edName.getText().toString() + " , " + edEmail.getText().toString();
                    }
                } else {
                    nameAndEmail = AppController.getInstance().getModelFacade().getLocalModel().getFName() + " " + AppController.getInstance().getModelFacade().getLocalModel().
                            getLName() + " , " + AppController.getInstance().getModelFacade().getLocalModel()
                            .getEmailAddress();
                }
                if (isChecked) {
                    edMessage.setText(edMessage.getText().toString().trim());
                    if (!edMessage.getText().toString().isEmpty()) {


                        //noinspection deprecation
                        pDialog = new ProgressDialog(ContactActivity.this, R.style.CustomDialogTheme);
                        pDialog.show();
                        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(ContactActivity.this));
                        pDialog.setCanceledOnTouchOutside(false);
                        pDialog.setCancelable(false);

                        if(mCreateTaskOnAsanaModel != null)
                        {
                            mCreateTaskOnAsanaModel.unRegisterView(ContactActivity.this);
                        }
                        mCreateTaskOnAsanaModel = AppController.getInstance().getModelFacade().getRemoteModel()
                                .getCreateTaskOnAsanaModel();
                        mCreateTaskOnAsanaModel.registerView(ContactActivity.this);
                        mCreateTaskOnAsanaModel.setProgressDialog(pDialog);
                        mCreateTaskOnAsanaModel.loadAsanaData(nameAndEmail, edMessage.getText().toString(), tagId);

                    } else {
                        showToastMessage("Please provide message");

                    }
                }
            }
        });

        edMessage.addTextChangedListener(new CharacterCountErrorWatcher(tvCharacterLimit,edMessage, 0, 250));
    }

    /**
     * Method is used to check email validation
     * @param email set the email address
     * @return the value of valid email true and false.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isValidEmail(String email) {
        if (email.length() == 0) {
            edEmail.setError("Email Not Entered!");
            return false;
        } else {
            String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                edEmail.setError("Invalid Email!");
                return false;
            } else
                return matcher.matches();
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isValidText(String text) {
        return text != null && text.length() > 0;
    }

    /**
     * Method is used to show confirmation dialog
     */
    @SuppressWarnings("deprecation")
    private void showConfirmationDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Do you want to discard this message?");
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setPositiveButton("Keep",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                    }
                });
        alertDialogBuilder.setNegativeButton("Discard",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                        finish();
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setAllCaps(false);
        positiveButton.setTextColor(getResources().getColor(R.color.app_theme_color));
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(getResources().getColor(R.color.app_theme_color));
        negativeButton.setAllCaps(false);
    }

    @Override
    public void update() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mCreateTaskOnAsanaModel != null && mCreateTaskOnAsanaModel.getState() == BaseModel.STATE_SUCCESS) {
                    if (mCreateTaskOnAsanaModel.getStatusResult()) {
                        showTicketSuccess(mCreateTaskOnAsanaModel.getTaskId());
                    } else {
                        showToastMessage(GlobalConstants.EMAIL_FAILURE_MESSAGE);
                    }

                    pDialog.dismiss();
                }
            }
        });


    }

    /**
     * Method is used to show successMessage after creating ticket
     * @param taskId set the task id.
     */
    @SuppressWarnings("deprecation")
    private void showTicketSuccess(String taskId) {
        try {
            String successMessage;
            long userID = AppController.getInstance().getModelFacade().getLocalModel().getUserId();
            if (userID == GlobalConstants.DEFAULT_USER_ID)
                successMessage = "Thank you. Ticket #" + taskId + " has been created. Someone from "
                        + AppController.getInstance().getApplicationContext().getResources()
                        .getString(R.string.app_full_name) + " will reply to you via your registered email,  "
                        + edEmail.getText().toString() + ". We appreciate you taking the time to contact us. -The "
                        +  AppController.getInstance().getApplicationContext().getResources()
                        .getString(R.string.app_full_name);
            else
                successMessage = "Thank you. Ticket #" + taskId + " has been created. Someone from "
                        +  AppController.getInstance().getApplicationContext().getResources()
                        .getString(R.string.app_full_name) + " will reply to you via your registered email,  "
                        + AppController.getInstance().getModelFacade().getLocalModel().getEmailAddress()
                        + ". We appreciate you taking the time to contact us. -The " +
                        AppController.getInstance().getApplicationContext().getResources()
                                .getString(R.string.app_full_name);
            AlertDialog alertDialog;
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ContactActivity.this);
            alertDialogBuilder.setMessage(successMessage);
            String title = AppController.getInstance().getApplicationContext().getResources()
                    .getString(R.string.app_full_name)+" v"+AppController.getInstance().getApplicationContext()
                    .getResources().getString(R.string.app_version);

            SpannableStringBuilder sb = new SpannableStringBuilder(title);
            StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
            sb.setSpan(bss, 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            alertDialogBuilder.setTitle(sb);

            alertDialogBuilder.setNegativeButton("Close",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    });

            alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
            Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positiveButton.setAllCaps(false);
            positiveButton.setTextColor(getResources().getColor(R.color.app_theme_color));
            Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            negativeButton.setTextColor(getResources().getColor(R.color.app_theme_color));
            negativeButton.setAllCaps(false);

        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Method is used to show toast message
     * @param message set the message which we want to show to user
     */
    @SuppressLint("PrivateResource")
    private void showToastMessage(String message) {

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
                animation = AnimationUtils.loadAnimation(ContactActivity.this,
                        R.anim.abc_fade_out);

                text.setAnimation(animation);
                text.setVisibility(View.GONE);
            }
        }, 3000);
    }
}
