package com.xiaoxin.apktools;/*
 *     Copyright (c) 2015 GuDong
 *
 *     Permission is hereby granted, free of charge, to any person obtaining a copy
 *     of this software and associated documentation files (the "Software"), to deal
 *     in the Software without restriction, including without limitation the rights
 *     to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *     copies of the Software, and to permit persons to whom the Software is
 *     furnished to do so, subject to the following conditions:
 *
 *     The above copyright notice and this permission notice shall be included in all
 *     copies or substantial portions of the Software.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *     IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *     FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *     AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *     LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *     OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *     SOFTWARE.
 */


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoxin.apktools.Utils.ActionUtil;
import com.xiaoxin.apktools.Utils.FormatUtil;
import com.xiaoxin.apktools.Utils.NavigationManager;
import com.xiaoxin.apktools.Utils.Utils;
import com.xiaoxin.apktools.app.BaseActivity;
import com.xiaoxin.apktools.bean.AppEntity;


public class AppActivity extends BaseActivity implements View.OnClickListener {
    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";

    // View name of the header title. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_TITLE = "detail:header:title";

    public static final String EXTRA_APP_ENTITY = "APP_ENTITY";

    private ImageView mImageView;
    private TextView mTvAppName;
    private TextView mTvAppVersion;
    private TextView mTvAppVersionCode;
    private TextView mTvAppPackageName;
    private TextView mTvOpen;
    private TextView mTvShare;
    private TextView mTvExport;
    private TextView mTvDetail;

    private AppEntity mAppEntity;


    @Override
    protected int initLayout() {
        return R.layout.activity_app;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolBar(R.string.empty, true);
        setupView();
        fillData();
        addListener();
    }

    private void fillData() {
        mAppEntity = (AppEntity) getIntent().getParcelableExtra(EXTRA_APP_ENTITY);
        Bitmap bitmap = BitmapFactory.decodeByteArray(mAppEntity.getAppIconData(), 0, mAppEntity.getAppIconData().length);
        mImageView.setImageBitmap(bitmap);
        mTvAppName.setText(mAppEntity.getAppName());
        mTvAppVersion.setText(FormatUtil.formatVersionName(mAppEntity));
        mTvAppPackageName.setText(mAppEntity.getPackageName());
        mTvAppVersionCode.setText("VersionCode:"+mAppEntity.getVersionCode());
        if(Utils.isBriefMode(this)){
            mTvAppVersionCode.setVisibility(View.GONE);
        }
    }

    private void setupView() {
        mImageView = (ImageView) findViewById(R.id.iv_icon);
        mTvAppName = (TextView) findViewById(R.id.app_name);
        mTvAppVersion = (TextView) findViewById(R.id.tv_version);
        mTvAppVersionCode = (TextView) findViewById(R.id.tv_version_code);
        mTvAppPackageName = (TextView) findViewById(R.id.pacage_name);
        mTvOpen = (TextView) findViewById(R.id.tv_more);
        mTvExport = (TextView) findViewById(R.id.tv_export);
        mTvDetail = (TextView) findViewById(R.id.tv_detail);
        mTvShare = (TextView) findViewById(R.id.tv_share);

        ViewCompat.setTransitionName(mImageView, VIEW_NAME_HEADER_IMAGE);
    }

    private void addListener() {
        mTvOpen.setOnClickListener(this);
        mTvExport.setOnClickListener(this);
        mTvDetail.setOnClickListener(this);
        mTvShare.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_share:
                ActionUtil.shareApk(this,mAppEntity);
                break;
            case R.id.tv_export:
                ActionUtil.exportApk(this,mAppEntity);
                break;
            case R.id.tv_detail:
                NavigationManager.openAppDetail(this,mAppEntity.getPackageName());
                break;
            case R.id.tv_more:
                showMoreDialog();
                break;
        }
    }

    private void showMoreDialog() {
        new AlertDialog.Builder(this).
                setItems(R.array.more_action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                openApp();
                                break;
                            case 1:
                                uninstallApp();
                                break;
                            case 2:
                                NavigationManager.gotoMarket(AppActivity.this,mAppEntity.getPackageName());
                                break;
                        }
                    }
                })
                .create()
                .show();
    }

    private void uninstallApp() {
        if(Utils.isOwnApp(this,mAppEntity.getPackageName()))return;
        NavigationManager.uninstallApp(AppActivity.this,mAppEntity.getPackageName());
    }

    private void openApp() {
        if(Utils.isOwnApp(this,mAppEntity.getPackageName()))return;
        try {
            NavigationManager.openApp(AppActivity.this,mAppEntity.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(mTvShare,String.format(getString(R.string.fail_open_app),mAppEntity.getAppName()),Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == NavigationManager.UNINSTALL_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                onBackPressed();
            }else if(resultCode == RESULT_CANCELED){
//                Logger.i("cancel");
            }
        }
    }
}
