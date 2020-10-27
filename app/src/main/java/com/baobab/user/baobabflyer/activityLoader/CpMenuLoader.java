package com.baobab.user.baobabflyer.activityLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.BaobabCpEmpManagement;
import com.baobab.user.baobabflyer.BaobabCpEvent;
import com.baobab.user.baobabflyer.BaobabMenuRevise;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.adapter.MenuListAdapter;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.ViewAnimationUtil;
import com.baobab.user.baobabflyer.server.vo.CPInfoVO;
import com.baobab.user.baobabflyer.server.vo.CpMenuNeedVO;
import com.baobab.user.baobabflyer.server.vo.CpMenuVO;
import com.baobab.user.baobabflyer.server.vo.MenuAdapterListVO;
import com.baobab.user.baobabflyer.server.vo.MenuVO;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

public class CpMenuLoader {

    RetroSingleTon retroSingleTon;

    private Context appContext;
    private Context activityContext;
    private Activity activity;
    CpMenuNeedVO mainVO;

    int setImgCount;
    int mainImgCount;
    int sideImgCount;
    int ectImgCount;
    int drinkImgCount;

    public CpMenuLoader(Context appContext, Context activityContext, Activity activity, CpMenuNeedVO mainVO) {
        this.appContext = appContext;
        this.activityContext = activityContext;
        this.activity = activity;
        this.mainVO = mainVO;

        allSetting();
    }

    public void allSetting(){
        Call<CpMenuVO> call = retroSingleTon.getInstance().getRetroInterface(appContext).cpMenu(mainVO.getCpName(), mainVO.getCpSeq(), mainVO.getEmail());
        call.enqueue(new Callback<CpMenuVO>() {
            @Override
            public void onResponse(Call<CpMenuVO> call, Response<CpMenuVO> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        CpMenuVO vo = response.body();
                        defaultSetting(vo.getCpInfoVO(), vo.getMenuVOList());
                    }else {
                        Log.d("CpMenuActivity", "response 내용없음");
                    }
                }else {
                    Log.d("CpMenuActivity", "서버로그 확인 필요");
                }
            }

            @Override
            public void onFailure(Call<CpMenuVO> call, Throwable t) {
                Log.d("CpMenuActivity", t.getLocalizedMessage());
            }
        });
    }

    public void defaultSetting(CPInfoVO cpInfoVo, List<MenuVO> menuVOs){
        TextView mainText = activity.findViewById( R.id.mainText );
        final String cpName = mainVO.getCpName();
        mainText.setText(cpName);

        final SharedPreferences spf = activity.getSharedPreferences("user", 0);
        String email = spf.getString("email", "");
        final String divCode = spf.getString("divCode", "");

        TextView divText1 = activity.findViewById(R.id.userDivText1);
        TextView divText2 = activity.findViewById(R.id.userDivText2);

        if(divCode.equals("c-01-01")){
            divText1.setText("사장님");
            divText2.setText("사장님");
        }else if (divCode.equals("c-02-02")){
            divText1.setText("매니저님");
            divText2.setText("매니저님");
        }else {
            divText1.setText("직원님");
            divText2.setText("직원님");
        }

        if(email.equals("위대한올마이티")){
            cpInfoVo = (CPInfoVO) activity.getIntent().getSerializableExtra("vo");
        }

        final CPInfoVO finalCpInfoVo = cpInfoVo;
        activity.findViewById( R.id.btn_businessManagement ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activityContext, BaobabMenuRevise.class );
                intent.putExtra("activity", activity.getIntent().getStringExtra( "activity" ));
                intent.putExtra("vo", finalCpInfoVo);
                activity.startActivity( intent );
            }
        });

        activity.findViewById(R.id.btn_couponManagement).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(divCode.equals("c-01-01") || divCode.equals("c-02-02")){
                    Intent intent = new Intent(activityContext, BaobabCpEvent.class );
                    intent.putExtra("cpSeq", finalCpInfoVo.getSeq_num());
                    activity.startActivity( intent );
                }else {
                    Toast.makeText(activityContext, "권한이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        activity.findViewById(R.id.btn_employeeManagement).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(divCode.equals("c-01-01") || divCode.equals("c-02-02")) {
                    Intent intent = new Intent(activityContext, BaobabCpEmpManagement.class);
                    intent.putExtra("cpSeq", finalCpInfoVo.getSeq_num());
                    activity.startActivity(intent);
                }else {
                    Toast.makeText(activityContext, "권한이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        menuSetting(menuVOs);
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

        MenuListAdapter adapter = new MenuListAdapter(img, activityContext, appContext, realList, hasLine, mainVO.getCpName(), mainVO.getCpSeq(), list);

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
}
