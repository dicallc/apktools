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

package com.xiaoxin.apktools.bean;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * app信息实体
 * Created by mao on 15/7/8.
 */
 public class AppEntity implements Parcelable{

protected long id;

 private String appName="";
  private String packageName="";
 private String versionName="";
 private int versionCode=0;
 private byte[] appIconData=null;
 private String srcPath;
 private int uid;
    public AppEntity() {
    }

    public AppEntity(String packageName) {
        this.packageName = packageName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public byte[] getAppIconData() {
        return appIconData;
    }

    public void setAppIconData(byte[] appIconData) {
        this.appIconData = appIconData;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppEntity entity = (AppEntity) o;

        if (versionCode != entity.versionCode) return false;
        if (appName != null ? !appName.equals(entity.appName) : entity.appName != null)
            return false;
        if (packageName != null ? !packageName.equals(entity.packageName) : entity.packageName != null)
            return false;
        if (versionName != null ? !versionName.equals(entity.versionName) : entity.versionName != null)
            return false;
        if (appIconData != null ? !(appIconData.length == entity.appIconData.length) : entity.appIconData != null)
            return false;
        return srcPath != null ? srcPath.equals(entity.srcPath) : entity.srcPath == null;

    }

    @Override
    public int hashCode() {
        return packageName != null ? packageName.hashCode() : 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.appName);
        dest.writeString(this.packageName);
        dest.writeString(this.versionName);
        dest.writeInt(this.versionCode);
        dest.writeByteArray(this.appIconData);
        dest.writeString(this.srcPath);
        dest.writeInt(this.uid);
    }

    private AppEntity(Parcel in) {
        this.id = in.readLong();
        this.appName = in.readString();
        this.packageName = in.readString();
        this.versionName = in.readString();
        this.versionCode = in.readInt();
        this.appIconData = in.createByteArray();
        this.srcPath = in.readString();
        this.uid = in.readInt();
    }

    public static final Creator<AppEntity> CREATOR = new Creator<AppEntity>() {
        public AppEntity createFromParcel(Parcel source) {
            return new AppEntity(source);
        }

        public AppEntity[] newArray(int size) {
            return new AppEntity[size];
        }
    };

    @Override
    public String toString() {
        return "AppEntity{" +
                "appIconData size =" + appIconData.length +
                ", id=" + id +
                ", appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                ", srcPath='" + srcPath + '\'' +
                ", uid=" + uid +
                '}';
    }
}
