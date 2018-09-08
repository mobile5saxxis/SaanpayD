package com.saxxis.saanpaydestributor.pattern;

import android.os.Bundle;

import java.util.List;

import me.zhanghai.android.patternlock.PatternView;

/**
 * Created by saxxis25 on 5/5/2017.
 */

public class SetPatternActivity extends me.zhanghai.android.patternlock.SetPatternActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // setTheme(R.style.AppTheme_Pattern);
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void onSetPattern(List<PatternView.Cell> pattern) {
        PatternLockUtils.setPattern(pattern, this);
    }
}
