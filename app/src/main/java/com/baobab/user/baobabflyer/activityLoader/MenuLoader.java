package com.baobab.user.baobabflyer.activityLoader;

import android.app.Activity;
import android.content.Context;

import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.adapter.MenuEventAdapter;
import com.baobab.user.baobabflyer.server.adapter.MenuListAdapter;
import com.baobab.user.baobabflyer.server.adapter.MenuViewPagerAdapter;
import com.baobab.user.baobabflyer.server.adapter.ReviewsAdapter;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.CircleAnimIndicator;
import com.baobab.user.baobabflyer.server.util.ViewAnimationUtil;
import com.baobab.user.baobabflyer.server.vo.CPInfoVO;
import com.baobab.user.baobabflyer.server.vo.MenuActivityVO;
import com.baobab.user.baobabflyer.server.vo.MenuAdapterListVO;
import com.baobab.user.baobabflyer.server.vo.MenuNeedVO;
import com.baobab.user.baobabflyer.server.vo.MenuVO;
import com.baobab.user.baobabflyer.server.vo.ReviewsSelectVO;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

public class MenuLoader {
    RetroSingleTon retroSingleTon;

    private Context appContext;
    private Context activityContext;
    private Activity activity;
    private MenuNeedVO mainVO;
    private String cpDiv;
    private CPInfoVO cpVO;

    int mainImgCount;
    int sideImgCount;
    int ectImgCount;
    int drinkImgCount;

    CircleAnimIndicator circleAnimIndicator;

    public MenuLoader(Context appContext, Context activityContext, Activity activity, MenuNeedVO mainVO, String cpDiv, CPInfoVO cpVO) {
        this.appContext = appContext;
        this.activityContext = activityContext;
        this.activity = activity;
        this.cpDiv = cpDiv;
        this.mainVO = mainVO;
        this.cpVO = cpVO;

        allSetting();
    }

    public void allSetting() {
        Call<MenuActivityVO> call = retroSingleTon.getInstance().getRetroInterface(appContext).menu(mainVO.getCpName(), mainVO.getCpSeq());
        call.enqueue(new Callback<MenuActivityVO>() {
            @Override
            public void onResponse(Call<MenuActivityVO> call, Response<MenuActivityVO> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        MenuActivityVO vo = response.body();

                        menuSetting(vo.getMenuVOList());

                        String[] imgArr = new String[vo.getCPmainImgVOList().size()];
                        for(int i=0;i<vo.getCPmainImgVOList().size();i++){
                            imgArr[i] = vo.getCPmainImgVOList().get(i).getImg_url();
                        }

                        settingViewPager(imgArr);

                        RecyclerView eventRecyclerView = activity.findViewById(R.id.eventRecyclerView);
                        eventRecyclerView.setHasFixedSize(true);
                        LinearLayoutManager eventLayoutManager = new LinearLayoutManager(activityContext);
                        eventRecyclerView.setLayoutManager(eventLayoutManager);

                        MenuEventAdapter eventAdapter = new MenuEventAdapter(vo.getEventCpVOList(), activityContext, mainVO.getCpName());

                        eventRecyclerView.removeAllViewsInLayout();
                        eventRecyclerView.setAdapter(eventAdapter);

                        setReviewDefalutLayout(vo.getReviewsListVO());

                        if(vo.getReviewsListVO().size() == 0){
                            activity.findViewById(R.id.reviews_layout).setVisibility(GONE);
                        }else {
                            settingReview(vo.getReviewsListVO());
                        }

                    } else {
                        Log.d("MenuActivity", "response 내용없음");
                    }
                } else {
                    Log.d("MenuActivity", "서버로그 확인 필요");
                }
            }

            @Override
            public void onFailure(Call<MenuActivityVO> call, Throwable t) {
                Log.d("MenuActivity", t.getLocalizedMessage());
            }
        });
    }

    public void menuSetting(List<MenuVO> menuVOS){
        makeMenuList(menuVOS, "메인메뉴", "main", true, false);
        makeMenuList(menuVOS, "메인메뉴", "main", false, true);
        makeMenuList(menuVOS, "메인메뉴", "main", false, false);

        makeMenuList(menuVOS, "사이드메뉴", "side", true, false);
        makeMenuList(menuVOS, "사이드메뉴", "side", false, true);
        makeMenuList(menuVOS, "사이드메뉴", "side", false, false);

        makeMenuList(menuVOS, "기타메뉴", "ect", true, false);
        makeMenuList(menuVOS, "기타메뉴", "ect", false, true);
        makeMenuList(menuVOS, "기타메뉴", "ect", false, false);

        makeMenuList(menuVOS, "주류/음료", "drink", true, false);
        makeMenuList(menuVOS, "주류/음료", "drink", false, true);
        makeMenuList(menuVOS, "주류/음료", "drink", false, false);

        settingAnimation();
    }

    private void settingAnimation(){
        LinearLayout[] linearLayouts = new LinearLayout[]{
                activity.findViewById(R.id.mainBtn),
                activity.findViewById(R.id.sideBtn),
                activity.findViewById(R.id.ectBtn),
                activity.findViewById(R.id.drinkBtn),
        };

        for(int i=0;i<linearLayouts.length;i++){
            linearLayouts[i].setOnClickListener(animListener);
        }
    }

    View.OnClickListener animListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ViewAnimationUtil util = new ViewAnimationUtil();
            LinearLayout child = (LinearLayout) ((LinearLayout)v.getParent()).getChildAt(1);
            if(child.getVisibility() == View.VISIBLE){
                util.collapse(child);
                glideDrawableResource(appContext, R.drawable.ic_chevron_dropdown, 36, 36).into((ImageView)((LinearLayout)v).getChildAt(2));
            }else {
                util.expand(child);
                glideDrawableResource(appContext, R.drawable.ic_chevron_dropdown2, 36, 36).into((ImageView)((LinearLayout)v).getChildAt(2));
            }
        }
    };

    public DrawableRequestBuilder<Integer> glideDrawableResource(Context context, int resource, int width, int height){
        return  Glide.with(context).load(resource).override(width, height).signature(new StringSignature(UUID.randomUUID().toString())).centerCrop();
    }

    public void makeMenuList(List<MenuVO> list, String div, String divEng, boolean img, boolean isOdd){
        List<MenuAdapterListVO> adapterList;

        adapterList = imageMenu(menusDiv(list, div), img);

        int recyclerViewId;

        List<MenuAdapterListVO> realList;
        boolean hasLine;

        if(img){
            recyclerViewId = rtnRecyclerViewId(divEng, img, false);
            realList = adapterList;
            if (divEng.equals("main")){
                mainImgCount = realList.size();
            }else if (divEng.equals("side")){
                sideImgCount = realList.size();
            }else if (divEng.equals("ect")){
                ectImgCount = realList.size();
            }else if (divEng.equals("drink")){
                drinkImgCount = realList.size();
            }
            hasLine = false;
        }else {
            realList = nonImgListDiv(adapterList, isOdd);
            recyclerViewId = rtnRecyclerViewId(divEng, img, isOdd);
            hasLine = hasLine(divEng);
        }

        RecyclerView recyclerView = activity.findViewById(recyclerViewId);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activityContext);
        recyclerView.setLayoutManager(layoutManager);

        MenuListAdapter adapter = new MenuListAdapter(img, activityContext, appContext, realList, hasLine, cpVO.getCP_name(), cpVO.getSeq_num(), list);

        recyclerView.removeAllViewsInLayout();
        recyclerView.setAdapter(adapter);
    }

    public void hideLayout(int size, String div){
        Log.d(div, String.valueOf(size));
        if(size == 0){
            if(div.equals("메인메뉴")){
                activity.findViewById(R.id.mainMenu).setVisibility(GONE);
                activity.findViewById(R.id.mainBar).setVisibility(GONE);
            }else if(div.equals("사이드메뉴")){
                activity.findViewById(R.id.sideMenu).setVisibility(GONE);
                activity.findViewById(R.id.sideBar).setVisibility(GONE);
            }else if(div.equals("기타메뉴")){
                activity.findViewById(R.id.ectMenu).setVisibility(GONE);
                activity.findViewById(R.id.ectBar).setVisibility(GONE);
            }else if(div.equals("주류/음료")){
                activity.findViewById(R.id.drinkMenu).setVisibility(GONE);
                activity.findViewById(R.id.drinkBar).setVisibility(GONE);
            }
        }
    }

    public boolean hasLine(String div){
        if(div.equals("main") && mainImgCount != 0){
            return true;
        }else if(div.equals("side") && sideImgCount != 0){
            return true;
        }else if(div.equals("ect") && ectImgCount != 0){
            return true;
        }else if(div.equals("drink") && drinkImgCount != 0){
            return true;
        }else {
            return false;
        }
    }

    //메뉴 div 구분
    public List<MenuVO> menusDiv(List<MenuVO> list, String div) {
        List<MenuVO> newList = new ArrayList<>();
        for (int i=0;i<list.size();i++) {
            if (list.get(i).getMenu_div().equals(div)) {
                newList.add(list.get(i));
            }
        }

        hideLayout(newList.size(), div);
        return newList;
    }

    //메뉴 이미지 구분
    public List<MenuAdapterListVO> imageMenu(List<MenuVO> list, boolean img){
        String divString;

        if(img){
            divString = "http";
        }else {
            divString = "이미지";
        }

        List<MenuVO> newList = new ArrayList<>();
        for(MenuVO vo : list){
            if(vo.getMenu_img().startsWith(divString)){
                newList.add(vo);
            }
        }

        List<MenuAdapterListVO> resultList = new ArrayList<>();
        for(MenuVO vo : newList){
            MenuAdapterListVO listVo = new MenuAdapterListVO();
            listVo.setMenuName(vo.getMenu_name());
            listVo.setPrice(vo.getMenu_price());
            listVo.setMenuImg(vo.getMenu_img());
            listVo.setOption(vo.getMenu_option());
            listVo.setMenuDiv(vo.getMenu_div());

            resultList.add(listVo);
        }

        return resultList;
    }

    //이미지 없는 메뉴리스트 홀수 짝수 구분
    public List<MenuAdapterListVO> nonImgListDiv(List<MenuAdapterListVO> list, boolean isOdd){
        List<MenuAdapterListVO> newList = new ArrayList<>();
        int startingNum = 0;
        if(isOdd)
            startingNum = 1;
        for(int i=startingNum;i<list.size();i=i+2){
            newList.add(list.get(i));
        }
        return newList;
    }

    //return recycler view id
    public int rtnRecyclerViewId(String div, boolean img, boolean isOdd){
        int id = 0;
        if(img){
            switch (div){
                case "main":
                    id = R.id.main_menu_recyclerView;
                    break;
                case "side":
                    id = R.id.side_menu_recyclerView;
                    break;
                case "ect":
                    id = R.id.ect_menu_recyclerView;
                    break;
                case "drink":
                    id = R.id.drink_menu_recyclerView;
                    break;
            }
        }else {
            if(isOdd){
                switch (div){
                    case "main":
                        id = R.id.main_menu_nonImg_odd_recyclerView;
                        break;
                    case "side":
                        id = R.id.side_menu_nonImg_odd_recyclerView;
                        break;
                    case "ect":
                        id = R.id.ect_menu_nonImg_odd_recyclerView;
                        break;
                    case "drink":
                        id = R.id.drink_menu_nonImg_odd_recyclerView;
                        break;
                }
            }else {
                switch (div){
                    case "main":
                        id = R.id.main_menu_nonImg_recyclerView;
                        break;
                    case "side":
                        id = R.id.side_menu_nonImg_recyclerView;
                        break;
                    case "ect":
                        id = R.id.ect_menu_nonImg_recyclerView;
                        break;
                    case "drink":
                        id = R.id.drink_menu_nonImg_recyclerView;
                        break;
                }
            }
        }
        return id;
    }

    public String makeText(String str, String subStr) {
        if (str.contains(subStr)) {
            DecimalFormat decimalFormat = new DecimalFormat("###,###");
            str = str.replaceAll(",", "");
            return decimalFormat.format(Integer.parseInt(str.replaceAll(subStr, ""))) + subStr;
        } else {
            return str;
        }
    }

    private void settingReview(List<ReviewsSelectVO> list){
        RecyclerView reivewRecyclerView = activity.findViewById(R.id.rev_recyclerView);
        reivewRecyclerView.setHasFixedSize(true);
        LinearLayoutManager eventLayoutManager = new LinearLayoutManager(activityContext);
        reivewRecyclerView.setLayoutManager(eventLayoutManager);

        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(list, activityContext);

        reivewRecyclerView.removeAllViewsInLayout();
        reivewRecyclerView.setAdapter(reviewsAdapter);
    }

    private void settingViewPager(String[] imgArr){
        ViewPager viewPager = activity.findViewById(R.id.viewPager);
        viewPager.setClipToPadding(true);
        viewPager.setAdapter(new MenuViewPagerAdapter(activityContext, imgArr));

        circleAnimIndicator  = activity.findViewById(R.id.indicator);
        circleAnimIndicator.removeAllViews();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                circleAnimIndicator.selectDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        circleAnimIndicator.setItemMargin(4);
        circleAnimIndicator.createDotPanel(imgArr.length, R.drawable.circle_indicator, R.drawable.circle_indicator_none);
    }

    private void setReviewDefalutLayout(List<ReviewsSelectVO> list){
        ((TextView)activity.findViewById(R.id.rev_num)).setText(String.valueOf(list.size()));
        double revGrade = 0;
        for(int i=0;i<list.size();i++){
            revGrade += list.get(i).getScore();
        }
        revGrade = revGrade/list.size();
        revGrade = Math.round(revGrade*10)/10.0;
        ((TextView)activity.findViewById(R.id.review_grade)).setText(String.valueOf(revGrade));

        int revGradeCnt = 0;
        LinearLayout starLayout = activity.findViewById(R.id.starLayout);
        for(int i=0;i<revGrade;i++){
            revGradeCnt++;
            starLayout.addView(makeAllStar(R.drawable.star_trek));
        }

        if(revGrade - revGradeCnt > 0){
            starLayout.addView(makeAllStar(R.drawable.star_trek_2));
        }
    }

    private ImageView makeAllStar(int star){
        ImageView imageView = new ImageView(activityContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(30, 30);
        layoutParams.setMargins(0, 0, 6, 0);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageResource(star);

        return imageView;
    }
}