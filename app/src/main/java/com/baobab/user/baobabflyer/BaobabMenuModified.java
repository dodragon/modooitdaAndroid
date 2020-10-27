package com.baobab.user.baobabflyer;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.baobab.user.baobabflyer.server.adapter.MenuUpdateListAdapter;
import com.baobab.user.baobabflyer.server.vo.MenuVO;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabMenuModified extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    List<MenuVO> list;

    Spinner mainSpinner;
    String cpName;
    int cpSeq;

    public static Context context;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_menu_modified);

        context = this;

        cpName = getIntent().getStringExtra("cpName");
        cpSeq = getIntent().getIntExtra("cpSeq", 0);

        mainSpinner = findViewById(R.id.mainSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.bank_spinner_normal, R.id.spinnerText, getResources().getStringArray(R.array.menuModified));
        adapter.setDropDownViewResource(R.layout.bank_spinner_dropdown);
        mainSpinner.setAdapter(adapter);
        mainSpinner.setOnItemSelectedListener(listener);
        mainSpinner.setSelection(0);
    }

    public void setRecyclerView(List<MenuVO> list){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BaobabMenuModified.this);
        recyclerView.setLayoutManager(layoutManager);
        MenuUpdateListAdapter adapter = new MenuUpdateListAdapter(list, BaobabMenuModified.this, (Button)findViewById(R.id.delMenu), cpName, cpSeq);
        recyclerView.removeAllViewsInLayout();
        recyclerView.setAdapter(adapter);
    }

    AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void onItemSelected(final AdapterView<?> parent, View view, int position, long id) {
            Call<List<MenuVO>> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).getMymenu(cpSeq);
            call.enqueue(new Callback<List<MenuVO>>() {
                @Override
                public void onResponse(Call<List<MenuVO>> call, Response<List<MenuVO>> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Log.d("통신완료 : ", "완료");
                            list = response.body();
                            setRecyclerView(optionDiv(list, parent.getSelectedItem().toString()));
                        } else {
                            Log.d("서버에러 : ", "response 내용없음");
                            Intent intent = new Intent(getApplication(), BaobabInspection.class);
                            intent.putExtra("status", "오류");
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Log.d("서버에러 : ", "서버 로그 확인 필요");
                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                        intent.putExtra("status", "오류");
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<List<MenuVO>> call, Throwable t) {
                    Log.d("통신에러 : ", t.getLocalizedMessage());
                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                    intent.putExtra("status", "오류");
                    startActivity(intent);
                    finish();
                }
            });
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private List<MenuVO> optionDiv(List<MenuVO> list, String option){
        List<MenuVO> newList = new ArrayList<>();
        if(option.equals("전체보기")){
            return list;
        }else if(option.contains("메인")){
            for(int i=0;i<list.size();i++){
                if(list.get(i).getMenu_div().contains("메인")){
                    newList.add(list.get(i));
                }
            }
            return newList;
        }else if(option.contains("사이드")){
            for(int i=0;i<list.size();i++){
                if(list.get(i).getMenu_div().contains("사이드")){
                    newList.add(list.get(i));
                }
            }
            return newList;
        }else if(option.contains("기타")){
            for(int i=0;i<list.size();i++){
                if(list.get(i).getMenu_div().contains("기타")){
                    newList.add(list.get(i));
                }
            }
            return newList;
        }else if(option.contains("주류")){
            for(int i=0;i<list.size();i++){
                if(list.get(i).getMenu_div().contains("주류")){
                    newList.add(list.get(i));
                }
            }
            return newList;
        }else {
            return list;
        }
    }
}