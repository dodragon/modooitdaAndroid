package com.baobab.user.baobabflyer;

import android.content.Context;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.adapter.MakeEventAdapter;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.EventOptionDialog;
import com.baobab.user.baobabflyer.server.vo.EventCpVO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabCpEvent extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    RetroSingleTon retroSingleTon;
    int cpSeq;
    public static int mainSeq = 0;

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_cp_event);

        context = this;

        cpSeq = getIntent().getIntExtra("cpSeq", 0);

        Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).getMainEvent(cpSeq);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        mainSeq = response.body();
                        if(mainSeq == 0){
                            Toast.makeText(BaobabCpEvent.this, "메인메뉴 등록시 메인화면과 검색화면 첫화면에 등록됩니다.(현재 미등록)", Toast.LENGTH_SHORT).show();
                        }
                        setAll();
                    }else {
                        Log.d("메인메뉴조회", "response 내용없음");
                        setAll();
                    }
                }else {
                    Log.d("메인메뉴조회", "서버로그확인 필요");
                    setAll();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d("메인메뉴조회", t.getLocalizedMessage());
                setAll();
            }
        });
    }

    private void setAll(){
        if(cpSeq != 0){
            callList(cpSeq, mainSeq);
            setOptionBtn();
            setBackBtn();
        }else {
            onBackPressed();
            Toast.makeText(this, "고객센터로 문의 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void callList(final int cpSeq, final int mainSeq){
        Call<List<EventCpVO>> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).cpAllEvent(cpSeq);
        call.enqueue(new Callback<List<EventCpVO>>() {
            @Override
            public void onResponse(Call<List<EventCpVO>> call, Response<List<EventCpVO>> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        List<EventCpVO> list = response.body();

                        ViewPager viewPager = findViewById(R.id.event_viewPager);
                        viewPager.setClipToPadding(false);

                        float density = getResources().getDisplayMetrics().density;
                        int margin = (int)(40 * density);
                        viewPager.setPadding(margin, 0, margin, 0);
                        viewPager.setPageMargin(margin/2);

                        MakeEventAdapter adapter = new MakeEventAdapter(list, BaobabCpEvent.this, mainSeq);
                        viewPager.setAdapter(adapter);
                        viewPager.setOnPageChangeListener(BaobabCpEvent.this);

                        ((TextView)findViewById(R.id.allEa)).setText(String.valueOf(adapter.getCount()));
                        if(list.isEmpty()){
                            ((TextView)findViewById(R.id.currentCount)).setText("0");
                        }else {
                            ((TextView)findViewById(R.id.currentCount)).setText("1");
                        }
                    }else {
                        Log.d("업체 이벤트 조회", "response 내용없음");
                        onBackPressed();
                        Toast.makeText(BaobabCpEvent.this, "고객센터로 문의 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Log.d("업체 이벤트 조회", "서버로그 확인 필요");
                    onBackPressed();
                    Toast.makeText(BaobabCpEvent.this, "고객센터로 문의 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<EventCpVO>> call, Throwable t) {
                Log.d("업체 이벤트 조회", t.getLocalizedMessage());
            }
        });
    }

    public void setOptionBtn(){
        ImageView optionBtn = findViewById(R.id.eventOption);

        optionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("메인쪽에서", String.valueOf(mainSeq));
                EventOptionDialog dialog = EventOptionDialog.newInstance(cpSeq, mainSeq);
                dialog.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light);
                dialog.show(getSupportFragmentManager(), "noTag");
            }
        });
    }

    public void setBackBtn(){
        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        ((TextView)findViewById(R.id.currentCount)).setText(String.valueOf(i + 1));
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onResume() {
        super.onResume();
        context = this;

        cpSeq = getIntent().getIntExtra("cpSeq", 0);
        Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).getMainEvent(cpSeq);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        mainSeq = response.body();
                        if(mainSeq == 0){
                            Toast.makeText(BaobabCpEvent.this, "메인메뉴 등록시 메인화면과 검색화면 첫화면에 등록됩니다.(현재 미등록)", Toast.LENGTH_SHORT).show();
                        }
                        setAll();
                    }else {
                        Log.d("메인메뉴조회", "response 내용없음");
                        setAll();
                    }
                }else {
                    Log.d("메인메뉴조회", "서버로그확인 필요");
                    setAll();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d("메인메뉴조회", t.getLocalizedMessage());
                setAll();
            }
        });
    }
}
