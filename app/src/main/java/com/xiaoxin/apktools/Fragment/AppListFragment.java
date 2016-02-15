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

package com.xiaoxin.apktools.Fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.xiaoxin.apktools.AppActivity;
import com.xiaoxin.apktools.R;
import com.xiaoxin.apktools.Utils.ActionUtil;
import com.xiaoxin.apktools.Utils.NavigationManager;
import com.xiaoxin.apktools.Utils.RxBus;
import com.xiaoxin.apktools.Utils.Utils;
import com.xiaoxin.apktools.adapter.AppInfoListAdapter;
import com.xiaoxin.apktools.bean.AppEntity;
import com.xiaoxin.apktools.view.DividerItemDecoration;

import java.util.List;

import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import rx.Subscription;
import rx.functions.Action1;


/**
 * Created by mao on 15/7/8.
 */
public class AppListFragment extends Fragment implements AppInfoListAdapter.IClickPopupMenuItem, AppInfoListAdapter.IClickListItem {
    public static final String KEY_TYPE = "type";
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AppInfoListAdapter mAdapter;
    /**
     * Fragment列表的类型变量，小于0表示是搜索结果对应的列表Fragment，大于等于0，则是正常的用于显示App的列表Fragment
     **/
    private int mType = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.obj != null && msg.obj instanceof List) {
                List<AppEntity> result = (List<AppEntity>) msg.obj;
                loadingFinish();
                setData(result, msg.what);
            }
        }
    };
    private Subscription rxSubscription;
    ;

    public static AppListFragment getInstance(int type) {
        AppListFragment fragment = new AppListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getInt(KEY_TYPE);
// rxSubscription是一个Subscription的全局变量，这段代码可以在onCreate/onStart等生命周期内
        rxSubscription = RxBus.getDefault().toObserverable(AppEntity.class)
                .subscribe(new Action1<AppEntity>() {
                               @Override
                               public void call(AppEntity userEvent) {
                                   mSwipeRefreshLayout.post(new Runnable() {
                                       @Override
                                       public void run() {
                                           mSwipeRefreshLayout.setRefreshing(true);
                                       }
                                   });
                                   listener.onRefresh();
                               }
                           },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                // TODO: 处理异常
                            }
                        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(initLayout(), container, false);
        setupSwipeLayout(rootView);
        setupRecyclerView(rootView);
        return rootView;
    }

    private void setupRecyclerView(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new AppInfoListAdapter(getActivity(), Utils.isBriefMode(getActivity()));
        mAdapter.setClickPopupMenuItem(this);
        mAdapter.setClickListItem(this);

        SlideInBottomAnimationAdapter slideInLeftAdapter = new SlideInBottomAnimationAdapter(mAdapter);
        slideInLeftAdapter.setDuration(300);
        slideInLeftAdapter.setInterpolator(new AccelerateDecelerateInterpolator());

        mRecyclerView.setAdapter(slideInLeftAdapter);
    }

    SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
        public void onRefresh() {
            fillData();
        }
    };

    private void setupSwipeLayout(View rootView) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(Utils.getThemePrimaryColor(getActivity()));
        mSwipeRefreshLayout.setOnRefreshListener(listener);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        listener.onRefresh();
    }

    protected int initLayout() {
        return R.layout.fragment_app_list;
    }

    private synchronized void fillData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<AppEntity> list = null;
                switch (mType) {
                    case 0:
                        list = Utils.runningList;
                        break;
                    case 1:
                        list = Utils.list;
                        break;
                }
                mHandler.sendMessage(mHandler.obtainMessage(mType, list));
            }
        }).start();
    }


    public void hideRefresh() {
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }, 1000);
    }

    private void loadingFinish() {
        hideRefresh();
    }

    /**
     * 为列表设置数据
     */
    public void setData(List<AppEntity> result, int type) {
        if (result == null) {
            loadingDataError(getErrorInfo(type));
            return;
        }
        if (result.isEmpty()) {
            loadingDataEmpty(getEmptyInfo(type));
        }

        mAdapter.update(result);
    }

    private void loadingDataError(String errorInfo) {

    }

    private String getErrorInfo(int type) {
        if (type == 0) {
            return getString(R.string.app_list_error_recent);
        } else if (type == 1) {
            return getString(R.string.app_list_error_all);
        } else {
            return getString(R.string.app_list_error_all);
        }
    }

    private String getEmptyInfo(int type) {
        if (type == 0) {
            return getString(R.string.app_list_empty_recent);
        } else if (type == 1) {
            return getString(R.string.app_list_empty_all);
        } else {
            return getString(R.string.app_list_empty_search);
        }
    }

    private void loadingDataEmpty(String emptyInfo) {
        if (mType == 3) return;
        final Snackbar errorSnack = Snackbar.make(mRecyclerView, emptyInfo, Snackbar.LENGTH_LONG);
        errorSnack.setAction(R.string.action_retry, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorSnack.dismiss();
                fillData();
            }
        });
        errorSnack.show();
    }

    @Override
    public void onClickListItemIcon(View iconView, AppEntity entity) {
        ObjectAnimator animatorRotation = ObjectAnimator.ofFloat(iconView, "rotation", 0, 360);
        ObjectAnimator scaleRotationX = ObjectAnimator.ofFloat(iconView, "scaleX", 0, 1F);
        ObjectAnimator scaleRotationY = ObjectAnimator.ofFloat(iconView, "scaleY", 0, 1F);
        AnimatorSet animationSet = new AnimatorSet();
        animationSet.playTogether(animatorRotation, scaleRotationY, scaleRotationX);
        animationSet.setDuration(500);
        animationSet.start();
    }

    @Override
    public void onClickListItemContent(View view, AppEntity entity) {
        Intent intent = new Intent(getContext(), AppActivity.class);
        intent.putExtra(AppActivity.EXTRA_APP_ENTITY, entity);
        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(),
                new Pair<View, String>(view.findViewById(R.id.iv_icon),
                        AppActivity.VIEW_NAME_HEADER_IMAGE));
        ActivityCompat.startActivity(getActivity(), intent, activityOptions.toBundle());
    }

    @Override
    public void onClickMenuItem(int itemId, AppEntity entity) {
        switch (itemId) {
            case R.id.pop_share:
                ActionUtil.shareApk(getActivity(), entity);
                break;
            case R.id.pop_export:
                ActionUtil.exportApk(getActivity(), entity);
                break;
            case R.id.pop_detail:
                NavigationManager.openAppDetail(getActivity(), entity.getPackageName());
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


//    @Override
//    public void update(EEvent event, Bundle data) {{
//        List<AppEntity>list = mAdapter.getListData();
//        switch (event){
//            case UNINSTALL_APPLICATION_FROM_SYSTEM:
//                AppEntity uninstalledEntity = data.getParcelable("entity");
//                if(list.contains(uninstalledEntity)){
//                    list.remove(uninstalledEntity);
//                    mAdapter.update(list);
//                }else{
//                }
//                break;
//            case INSTALL_APPLICATION_FROM_SYSTEM:
//                AppEntity installedEntity = data.getParcelable("entity");
//                if(mType == 1 && !list.contains(installedEntity)){
//                    list.add(installedEntity);
//                    mAdapter.update(list);
//                }
//                break;
//            case PREPARE_FOR_ALL_INSTALLED_APP_FINISH:
//                mRecyclerView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        fillData();
//                    }
//                });
//                break;
//        }
//    }

//    }
}

