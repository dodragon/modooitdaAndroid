package com.baobab.user.baobabflyer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.CPInfoVO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlmightyCpSearch extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_almighty_cp_search);

        findViewById(R.id.search_btn).setOnClickListener(search);
    }

    public LinearLayout makeList(final CPInfoVO vo){
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int size10 = Math.round( 10 * dm.density );

        LinearLayout layout = new LinearLayout(AlmightyCpSearch.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, size10, 0, size10);
        layout.setLayoutParams(params);
        layout.setBackgroundColor(Color.WHITE);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences spf = getSharedPreferences("user", 0);
                SharedPreferences.Editor editor = spf.edit();
                editor.putString("email", "위대한올마이티");
                editor.putString("divCode", "c-01-01");
                editor.apply();

                Intent cpMenu = new Intent( AlmightyCpSearch.this, BaobabCpMenu.class );
                cpMenu.putExtra( "activity", "almighty" );
                cpMenu.putExtra( "getCpNameFromJoin", vo.getCP_name() );
                cpMenu.putExtra("cpSeq", vo.getSeq_num());
                cpMenu.putExtra("vo", vo);
                startActivity( cpMenu );
            }
        });

        TextView cpNameTv = new TextView(AlmightyCpSearch.this);
        cpNameTv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        cpNameTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        cpNameTv.setTypeface(Typeface.DEFAULT_BOLD);
        cpNameTv.setTextColor(Color.BLACK);
        cpNameTv.setText(vo.getCP_name());
        layout.addView(cpNameTv);

        TextView addrTv = new TextView(AlmightyCpSearch.this);
        addrTv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addrTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        addrTv.setTypeface(Typeface.DEFAULT_BOLD);
        addrTv.setTextColor(Color.BLACK);
        addrTv.setText(vo.getCP_address() + " " + vo.getCP_addr_details());
        layout.addView(addrTv);

        return layout;
    }

    View.OnClickListener search = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String searchWord = ((EditText)findViewById(R.id.search_bar)).getText().toString();
            Call<List<CPInfoVO>> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).almightySearch(searchWord);
            call.enqueue(new Callback<List<CPInfoVO>>() {
                @Override
                public void onResponse(Call<List<CPInfoVO>> call, Response<List<CPInfoVO>> response) {
                    if(response.isSuccessful()){
                        if(response.body() != null){
                            List<CPInfoVO> list = response.body();
                            LinearLayout mother = findViewById(R.id.mother);
                            mother.removeAllViews();
                            for(int i=0;i<list.size();i++){
                                mother.addView(makeList(list.get(i)));
                            }
                        }else {
                            Log.d("올마이티", "response 내용없음");
                        }
                    }else {
                        Log.d("올마이티", "서버로그 확인 필요");
                    }
                }

                @Override
                public void onFailure(Call<List<CPInfoVO>> call, Throwable t) {
                    Log.d("올마이티", t.getLocalizedMessage());
                }
            });
        }
    };
}
