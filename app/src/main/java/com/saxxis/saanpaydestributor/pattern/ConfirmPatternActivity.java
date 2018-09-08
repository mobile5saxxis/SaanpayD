package com.saxxis.saanpaydestributor.pattern;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.saxxis.saanpaydestributor.R;

import java.util.List;

import me.zhanghai.android.patternlock.PatternView;

/**
 * Created by saxxis25 on 5/5/2017.
 */

public class ConfirmPatternActivity extends me.zhanghai.android.patternlock.ConfirmPatternActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme_Pattern);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected boolean isStealthModeEnabled() {
        return !PreferenceUtils.getBoolean(PreferenceContract.KEY_PATTERN_VISIBLE,
                PreferenceContract.DEFAULT_PATTERN_VISIBLE, this);
    }

    @Override
    protected boolean isPatternCorrect(List<PatternView.Cell> pattern) {
        return PatternLockUtils.isPatternCorrect(pattern, this);
    }

    @Override
    protected void onForgotPassword() {

        new AlertDialog.Builder(this).setTitle("Reset Pattern ?").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PatternLockUtils.clearPattern(ConfirmPatternActivity.this);
                ToastUtils.show(R.string.pattern_reset, ConfirmPatternActivity.this);
                finish();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).show();


        // Finish with RESULT_FORGOT_PASSWORD.
//        super.onForgotPassword();
    }
}
