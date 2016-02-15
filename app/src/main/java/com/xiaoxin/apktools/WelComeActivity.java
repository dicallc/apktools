/*
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

package com.xiaoxin.apktools;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;

import com.jaredrummler.android.processes.ProcessManager;
import com.xiaoxin.apktools.Utils.AppInfoEngine;
import com.xiaoxin.apktools.Utils.FileUtil;
import com.xiaoxin.apktools.Utils.NavigationManager;
import com.xiaoxin.apktools.Utils.Utils;
import com.xiaoxin.apktools.app.BaseActivity;
import com.xiaoxin.apktools.bean.AppEntity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WelComeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAndUpdateLocalDb();
        gotoMainActivity();
    }

    private void gotoMainActivity() {
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                NavigationManager.gotoMainActivityFromSplashView(WelComeActivity.this);
            }
        }, 1500);
    }

    /**
     * check running list should show AppPlus or not
     *
     * @param packagename
     * @return true if show else false
     */
    private static boolean isNotShowSelf(Context ctx, String packagename) {
        return !Utils.isShowSelf(ctx) && packagename.equals(ctx.getPackageName());
    }

    private void checkAndUpdateLocalDb() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Utils.list = AppInfoEngine.getInstance().getInstalledAppList();
                List<ActivityManager.RunningAppProcessInfo> runningList = ProcessManager.getRunningAppProcessInfo(WelComeActivity.this);
                List<AppEntity> list = new ArrayList<>();
                for (ActivityManager.RunningAppProcessInfo processInfo : runningList) {
                    String packageName = processInfo.processName;
                    if (isNotShowSelf(WelComeActivity.this, packageName)) continue;
                    for (AppEntity entity : Utils.list) {
                        if (packageName.equals(entity.getPackageName())) {
                            list.add(entity);
                        } else {
                            continue;
                        }
                    }

                }
                Utils.runningList=list;
            }
        }).start();
    }

    /**
     * check the directory which used to store export apk file has some file ,if the old dir has
     * apk file ,move all file to new dir folder.
     * this change is begin with version 3.0, and the new folder name is AppPlus
     */
    private void checkExportDirectoryIsChange() {
        if (!FileUtil.isSdCardOnMounted()) {
            return;
        }
        final File oldExportDir = new File(FileUtil.getSDPath(), FileUtil.KEY_EXPORT_DIR_OLDER);
        //user has not use older dir name,this condition is good , we need not deal
        if (!oldExportDir.exists()) {
            return;
        }
        final File[] files = oldExportDir.listFiles();
        if (files.length <= 0) {
            oldExportDir.delete();
            return;
        }
        final File nowExportDir = FileUtil.createDir(FileUtil.getSDPath(), FileUtil.KEY_EXPORT_DIR);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (File file : files) {
                    File dest = new File(nowExportDir, file.getName());
                    try {
                        FileUtil.copyFileUsingFileChannels(file, dest);
                        file.delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                oldExportDir.delete();
            }
        }).start();

    }

    @Override
    protected int initLayout() {
        return R.layout.activity_wel_come;
    }
}
