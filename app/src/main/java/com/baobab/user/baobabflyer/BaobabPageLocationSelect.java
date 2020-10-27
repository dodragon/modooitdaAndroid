package com.baobab.user.baobabflyer;

import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baobab.user.baobabflyer.server.adapter.PageLocationAdapter;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.PageNeedVO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabPageLocationSelect extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    LinearLayout[] location;
    String bigLocation;
    String[] ourLocationArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_page_location_select);

        Call<List<String>> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).selectAddrGroup();
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        List<String> ourLocationList = response.body();
                        ourLocationArr = new String[ourLocationList.size() + 1];

                        for(int i=0;i<ourLocationArr.length;i++){
                            if(i == 0){
                                ourLocationArr[i] = "전체";
                            }else {
                                ourLocationArr[i] = ourLocationList.get(i - 1);
                            }
                        }

                        location = new LinearLayout[((LinearLayout)findViewById(R.id.locationMother)).getChildCount()];
                        for(int i=0;i<location.length;i++){
                            location[i] = (LinearLayout) ((LinearLayout)findViewById(R.id.locationMother)).getChildAt(i);
                            location[i].setOnClickListener(titleClick);
                        }

                        location[0].performClick();
                    }else {
                        Log.d("pageLocation", "response 내용없음");
                    }
                }else {
                    Log.d("pageLocation", "server error");
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.d("pageLocation", t.getLocalizedMessage());
            }
        });

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    View.OnClickListener titleClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for(int i=0;i<location.length;i++){
                ((TextView)location[i].getChildAt(0)).setTextColor(Color.parseColor("#b7b7b7"));
                location[i].getChildAt(1).setVisibility(View.GONE);
            }

            ((TextView)((LinearLayout)v).getChildAt(0)).setTextColor(Color.parseColor("#5c7cfa"));
            ((LinearLayout)v).getChildAt(1).setVisibility(View.VISIBLE);

            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BaobabPageLocationSelect.this);
            recyclerView.setLayoutManager(layoutManager);

            PageLocationAdapter adapter = new PageLocationAdapter(setLocationList(((TextView)((LinearLayout)v).getChildAt(0)).getText().toString()), (PageNeedVO) getIntent().getSerializableExtra("pageVO"),
                    BaobabPageLocationSelect.this);
            recyclerView.removeAllViewsInLayout();
            recyclerView.setAdapter(adapter);
        }
    };

    public String[] setLocationList(String location){
        String[] locationArr;
        switch (location){
            case "서울":
                locationArr = getResources().getStringArray(R.array.seoul);
                bigLocation = "서울특별시";
                break;
            case "경기":
                locationArr = getResources().getStringArray(R.array.gyeonggi);
                bigLocation = "경기도";
                break;
            case "인천":
                locationArr = getResources().getStringArray(R.array.incheon);
                bigLocation = "인천광역시";
                break;
            case "부산":
                locationArr = getResources().getStringArray(R.array.busan);
                bigLocation = "부산광역시";
                break;
            case "강원":
                locationArr = getResources().getStringArray(R.array.gangwon);
                bigLocation = "강원도";
                break;
            case "울산/경남":
                locationArr = getResources().getStringArray(R.array.ulsanAndgyeongnam);
                bigLocation = "울산광역시|경상남도";
                break;
            case "대구/경북":
                locationArr = getResources().getStringArray(R.array.daeguAndgyeongbuk);
                bigLocation = "대구광역시|경상북도";
                break;
            case "충청/대전":
                locationArr = getResources().getStringArray(R.array.choongchungs);
                bigLocation = "충청남도|충청북도|세종특별자치시|대전광역시";
                break;
            case "광주/전남":
                locationArr = getResources().getStringArray(R.array.gwangjuAndjeonam);
                bigLocation = "광주광역시|전라남도";
                break;
            case "전북":
                locationArr = getResources().getStringArray(R.array.jeonbuk);
                bigLocation = "전라북도";
                break;
            case "제주":
                locationArr = getResources().getStringArray(R.array.jeju);
                bigLocation = "제주특별자치도";
                break;
            default:
                locationArr = ourLocationArr;
                bigLocation = "전체";
                break;
        }
        return locationArr;
    }
}
