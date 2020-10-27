package com.baobab.user.baobabflyer;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.adapter.EmpUpdateListAdapter;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.LoadDialog;
import com.baobab.user.baobabflyer.server.vo.CpStaffVO;
import com.baobab.user.baobabflyer.server.vo.UserAllVO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabCpEmpManagement extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    RelativeLayout[] mainBtns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_cp_emp_management);

        Call<List<CpStaffVO>> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).getStaffs(getIntent().getIntExtra("cpSeq", 0));
        call.enqueue(new Callback<List<CpStaffVO>>() {
            @Override
            public void onResponse(Call<List<CpStaffVO>> call, Response<List<CpStaffVO>> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        List<CpStaffVO> list = response.body();

                        RecyclerView recyclerView = findViewById(R.id.recyclerView);
                        recyclerView.setHasFixedSize(true);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BaobabCpEmpManagement.this);
                        recyclerView.setLayoutManager(layoutManager);

                        EmpUpdateListAdapter adapter = new EmpUpdateListAdapter(list, BaobabCpEmpManagement.this);
                        recyclerView.removeAllViewsInLayout();
                        recyclerView.setAdapter(adapter);
                    }else {
                        Log.d("직원 관리 ", "response 내용없음");
                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                        intent.putExtra("status", "오류");
                        startActivity(intent);
                        finish();
                    }
                }else {
                    Log.d("직원 관리 ", "전체 로그 확인 필요");
                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                    intent.putExtra("status", "오류");
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<CpStaffVO>> call, Throwable t) {
                Log.d("직원 관리 ", "통신에러 : " + t.getLocalizedMessage());
                Intent intent = new Intent(getApplication(), BaobabInspection.class);
                intent.putExtra("status", "오류");
                startActivity(intent);
                finish();
            }
        });

        mainBtns = new RelativeLayout[]{findViewById(R.id.updateStaff), findViewById(R.id.findStaff)};
        for(int i=0;i<mainBtns.length;i++){
            mainBtns[i].setOnClickListener(mainBtnListener);
        }
        mainBtns[0].performClick();

        EditText searchBar = findViewById(R.id.searchBar);
        searchBar.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    search();
                }
                return false;
            }
        });

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    View.OnClickListener mainBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.equals(mainBtns[0])){
                findViewById(R.id.update_staff).setVisibility(View.VISIBLE);
                findViewById(R.id.find_staff).setVisibility(View.GONE);

                TextView updateTv = findViewById(R.id.updateTv);
                updateTv.setTextColor(Color.parseColor("#5c7cfa"));
                updateTv.setTypeface(Typeface.DEFAULT_BOLD);
                findViewById(R.id.updateBar).setVisibility(View.VISIBLE);

                TextView findTv = findViewById(R.id.findTv);
                findTv.setTextColor(Color.parseColor("#333333"));
                findTv.setTypeface(Typeface.DEFAULT);
                findViewById(R.id.findBar).setVisibility(View.GONE);

                findViewById(R.id.contentLayout).setBackgroundColor(Color.parseColor("#f5f5f5"));
            }else if(v.equals(mainBtns[1])){
                findViewById(R.id.update_staff).setVisibility(View.GONE);
                findViewById(R.id.find_staff).setVisibility(View.VISIBLE);

                TextView findTv = findViewById(R.id.findTv);
                findTv.setTextColor(Color.parseColor("#5c7cfa"));
                findTv.setTypeface(Typeface.DEFAULT_BOLD);
                findViewById(R.id.findBar).setVisibility(View.VISIBLE);

                TextView updateTv = findViewById(R.id.updateTv);
                updateTv.setTextColor(Color.parseColor("#333333"));
                updateTv.setTypeface(Typeface.DEFAULT);
                findViewById(R.id.updateBar).setVisibility(View.GONE);
                findViewById(R.id.contentLayout).setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        }
    };

    private void search(){
        LinearLayout mother = findViewById(R.id.mother);
        final LoadDialog loading = new LoadDialog(BaobabCpEmpManagement.this);
        loading.showDialog();

        mother.removeAllViews();

        final EditText searchBarEt = findViewById(R.id.searchBar);
        String searchWord = searchBarEt.getText().toString();
        Call<List<UserAllVO>> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).staffSearch(searchWord, getIntent().getIntExtra("cpSeq", 0));
        call.enqueue(new Callback<List<UserAllVO>>() {
            @Override
            public void onResponse(Call<List<UserAllVO>> call, Response<List<UserAllVO>> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        List<UserAllVO> userList = response.body();
                        LinearLayout mother = findViewById(R.id.mother);

                        for(int i=0;i<userList.size();i++){
                            mother.addView(makeLayout(userList.get(i)));
                        }

                        loading.dialogCancel();
                    }else {
                        searchBarEt.setText("");
                        loading.dialogCancel();
                        Toast.makeText(BaobabCpEmpManagement.this, "찾을 수 없는 검색어 입니다. 다시 시도 해주세요,", Toast.LENGTH_SHORT).show();
                        Log.d("스태프검색", "response 내용없음");
                    }
                }else {
                    searchBarEt.setText("");
                    loading.dialogCancel();
                    Toast.makeText(BaobabCpEmpManagement.this, "찾을 수 없는 검색어 입니다. 다시 시도 해주세요,", Toast.LENGTH_SHORT).show();
                    Log.d("스태프검색", "서버로그 확인 필요");
                }
            }

            @Override
            public void onFailure(Call<List<UserAllVO>> call, Throwable t) {
                searchBarEt.setText("");
                loading.dialogCancel();
                Toast.makeText(BaobabCpEmpManagement.this, "찾을 수 없는 검색어 입니다. 다시 시도 해주세요,", Toast.LENGTH_SHORT).show();
                Log.d("스태프검색", "통신에러 : " + t.getLocalizedMessage());
            }
        });
    }

    public LinearLayout makeLayout(final UserAllVO vo){
        final AlertDialog alert = new AlertDialog.Builder(BaobabCpEmpManagement.this)
                .setIcon(R.drawable.logo_and)
                .setTitle("직원등록")
                .setMessage(vo.getEmail() + "\n위 회원을 직원으로 등록 하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(BaobabCpEmpManagement.this, BaobabStaffSign.class);
                        intent.putExtra("cpSeq", getIntent().getIntExtra("cpSeq", 0));
                        intent.putExtra("context", "find");
                        intent.putExtra("vo", vo);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(BaobabCpEmpManagement.this, "직원등록이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }).create();

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int size5 = Math.round(5 * dm.density);
        int size10 = Math.round(10 * dm.density);

        LinearLayout firstLayout = new LinearLayout(BaobabCpEmpManagement.this);
        LinearLayout.LayoutParams firstParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        firstParam.setMargins(size5, size5, size5, size5);
        firstLayout.setLayoutParams(firstParam);
        firstLayout.setBackgroundColor(Color.WHITE);
        firstLayout.setWeightSum(2);
        firstLayout.setPadding(size10, size10, size10, size10);
        firstLayout.setOrientation(LinearLayout.HORIZONTAL);
        firstLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.show();
            }
        });

        TextView emailTv = new TextView(BaobabCpEmpManagement.this);
        LinearLayout.LayoutParams emailParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        emailTv.setLayoutParams(emailParam);
        emailTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        emailTv.setTextColor(Color.BLACK);
        emailTv.setText(vo.getEmail());
        firstLayout.addView(emailTv);

        TextView insertTv = new TextView(BaobabCpEmpManagement.this);
        LinearLayout.LayoutParams insertParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        insertTv.setLayoutParams(insertParam);
        insertTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        insertTv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        insertTv.setTextColor(Color.parseColor("#5c7cfa"));
        insertTv.setPaintFlags(insertTv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        insertTv.setText("등록하기");
        firstLayout.addView(insertTv);

        return firstLayout;
    }
}