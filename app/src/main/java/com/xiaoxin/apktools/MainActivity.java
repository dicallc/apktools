package com.xiaoxin.apktools;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xiaoxin.apktools.Utils.DialogUtil;
import com.xiaoxin.apktools.Utils.Utils;
import com.xiaoxin.apktools.adapter.AppPageListAdapter;
import com.xiaoxin.apktools.app.BaseActivity;

public class MainActivity extends BaseActivity {
    TabLayout mTabLayout;
    ViewPager mViewPager;
    AppPageListAdapter mFragmentAdapter;
    RelativeLayout mLayoutMainRoot;
    private static final int[] TITLES = new int[]{R.string.tab_recent, R.string.tab_installed};

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutMainRoot = (RelativeLayout) findViewById(R.id.layoutMainRoot);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        versionCheck();
    }

    private void versionCheck() {
        String str = Utils.getStringPreference(MainActivity.this, "isFirst", "");
        if (TextUtils.isEmpty(str)){
            String htmlFileName = "changelog_ch.html";
            DialogUtil.showCustomDialogFillInWebView(MainActivity.this, getSupportFragmentManager(), getString(R.string.change_log),htmlFileName, "changelog");
            Utils.putStringPreference(MainActivity.this,"isFirst", "xiaoxin");
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        mFragmentAdapter = new AppPageListAdapter(getSupportFragmentManager(), this, TITLES);
        viewPager.setAdapter(mFragmentAdapter);
    }

    private long lastTime = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - lastTime < 2000) {
            super.onBackPressed();
        } else {
            lastTime = System.currentTimeMillis();
            Toast.makeText(MainActivity.this, getString(R.string.exit_point), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
