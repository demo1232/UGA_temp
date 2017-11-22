package com.ncsavault.utils;

import android.graphics.Color;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Class used for count of edit text character
 * And used this class in ContactActivity.
 */
 public class CharacterCountErrorWatcher implements TextWatcher {

    private final EditText  mTextInputLayout;
    private final ForegroundColorSpan mNormalTextAppearance;
    private final AlignmentSpan mAlignmentSpan = new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE);
    private final SpannableStringBuilder mErrorText = new SpannableStringBuilder();
    private final int mMinLen;
    private final int mMaxLen;
    private final TextView mCharacterLimit;

    /**
     * Constructor of the class
     * @param characterLimit depends on requirement.
     * @param textInputLayout Reference of Edit Text.
     * @param minLen Min length of character.
     * @param maxLen Max length of character.
     */
    public CharacterCountErrorWatcher(TextView characterLimit, EditText textInputLayout, int minLen, int maxLen)
    {
        mTextInputLayout = textInputLayout;
        mNormalTextAppearance = new ForegroundColorSpan(Color.GRAY);
        mMinLen = minLen;
        mMaxLen = maxLen;
        mCharacterLimit = characterLimit;
        updateErrorText();
    }

    /**
     * Method used for update the value of character count in text view.
     */
    private void updateErrorText()
    {
        mErrorText.clear();
        mErrorText.clearSpans();
        final int length = mTextInputLayout.getText().length();
        if(length >= 0){
            mErrorText.append(String.valueOf(length));
            mErrorText.append(" / ");
            mErrorText.append(String.valueOf(mMaxLen));
            mErrorText.setSpan(mAlignmentSpan, 0, mErrorText.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            if(hasValidLength()){
                mErrorText.setSpan(mNormalTextAppearance, 0, mErrorText.length(),
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
       mCharacterLimit.setText(mErrorText);
    }

    /**
     * Method used for check the valid length of edit text.
     * @return true or false
     */
    private boolean hasValidLength()
    {
        final int length = mTextInputLayout.getText().length();
        return (length >= mMinLen && length <= mMaxLen);
    }

    @Override
    public void afterTextChanged(Editable s)
    {
        updateErrorText();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {

    }
}