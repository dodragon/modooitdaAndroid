package com.baobab.user.baobabflyer.activityLoader;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;

import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.adapter.PageBottomListAdapter;
import com.baobab.user.baobabflyer.server.adapter.PageTobListAdapter;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.LoadDialog;
import com.baobab.user.baobabflyer.server.vo.PageBotListVO;
import com.baobab.user.baobabflyer.server.vo.PageNeedVO;
import com.baobab.user.baobabflyer.server.vo.PageTopListVO;
import com.baobab.user.baobabflyer.server.vo.PageVO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PageLoader {
    private RetroSingleTon retroSingleTon;
    private Context context;
    private List<PageTopListVO> topList;
    private List<PageBotListVO> botList;

    public PageLoader(Context context) {
        this.context = context;
        this.topList = new ArrayList<>();
        this.botList = new ArrayList<>();
    }

    public void search(final PageNeedVO mainVO) {
        final LoadDialog loading = new LoadDialog(context);
        loading.showDialog();
        Call<PageVO> call = retroSingleTon.getInstance().getRetroInterface(context.getApplicationContext()).page(mainVO.getUser(), mainVO.getRoot(), mainVO.getService(), mainVO.getThirdPerson(), mainVO.getSearchWord(),
                mainVO.getCategoryDiv(), mainVO.getLocation(), mainVO.getLongitude(), mainVO.getLatitude(), mainVO.getTabDiv(), mainVO.getSortDiv(), mainVO.getTopPageInt(), mainVO.getBotPageInt());
        call.enqueue(new Callback<PageVO>() {
            @Override
            public void onResponse(Call<PageVO> call, Response<PageVO> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        PageVO pageVO = response.body();

                        if (mainVO.getTopPageInt() == 0) {
                            topList = pageVO.getTobList();
                        } else {
                            topList.addAll(pageVO.getTobList());
                        }

                        if (mainVO.getBotPageInt() == 0) {
                            botList = pageVO.getBotList();
                        } else {
                            botList.addAll(pageVO.getBotList());
                        }

                        //상단
                        RecyclerView couponRecyclerView = ((Activity) context).findViewById(R.id.tobRecyclerView);
                        couponRecyclerView.setHasFixedSize(true);
                        RecyclerView.LayoutManager couponLayoutManager = new LinearLayoutManager(context);
                        couponRecyclerView.setLayoutManager(couponLayoutManager);

                        PageTobListAdapter couponAdapter = new PageTobListAdapter(topList, context, mainVO.getTabDiv());

                        couponRecyclerView.removeAllViewsInLayout();
                        couponRecyclerView.setAdapter(couponAdapter);


                        //하단
                        RecyclerView recyclerView = ((Activity) context).findViewById(R.id.botRecyclerView);
                        recyclerView.setHasFixedSize(true);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                        recyclerView.setLayoutManager(layoutManager);

                        PageBottomListAdapter adapter = new PageBottomListAdapter(botList, context);
                        recyclerView.removeAllViewsInLayout();
                        recyclerView.setAdapter(adapter);

                        //검색
                        loading.dialogCancel();
                    } else {
                        Log.d("PageLoader", "response 내용없음");
                        loading.dialogCancel();
                    }
                } else {
                    Log.d("PageLoader", "서버 로그 확인 필요");
                    loading.dialogCancel();
                }
            }

            @Override
            public void onFailure(Call<PageVO> call, Throwable t) {
                Log.d("PageLoader", t.getLocalizedMessage());
                loading.dialogCancel();
            }
        });
    }
}
