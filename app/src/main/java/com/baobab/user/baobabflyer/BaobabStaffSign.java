package com.baobab.user.baobabflyer;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.LoadDialog;
import com.baobab.user.baobabflyer.server.vo.CpStaffVO;
import com.baobab.user.baobabflyer.server.vo.UserAllVO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabStaffSign extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    String gender;

    CpStaffVO vo;
    AlertDialog alert;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_staff_sign);

        ((TextView)findViewById(R.id.title)).setText("직원 등록");

        Spinner rankSp = findViewById(R.id.staff_rank);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.staff_rank, R.layout.spinner_cescum_layout);
        adapter.setDropDownViewResource(R.layout.spinner_cescum_layout);
        rankSp.setAdapter(adapter);

        RadioGroup radioGroup = findViewById(R.id.rGroup);
        radioGroup.setOnCheckedChangeListener(checked);

        findViewById(R.id.staff_save).setOnClickListener(staffSave);
        if(getIntent().getStringExtra("context").equals("find")){
            ((RadioButton)findViewById(R.id.male)).setChecked(true);
        }else{
            vo = (CpStaffVO) getIntent().getSerializableExtra("vo");
            makeUpdate(vo);

            alert = new AlertDialog.Builder(BaobabStaffSign.this)
                    .setIcon(R.drawable.logo_and)
                    .setTitle("직원등록")
                    .setMessage(vo.getStaffName() + "\n위 직원을 삭제하시겠습니까?")
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).staffDelete(vo.getEmail(), vo.getCpSeq());
                            call.enqueue(new Callback<Integer>() {
                                @Override
                                public void onResponse(Call<Integer> call, Response<Integer> response) {
                                    if(response.isSuccessful()){
                                        if(response.body() != null){
                                            int result = response.body();
                                            if(result == 3){
                                                Toast.makeText(getApplicationContext(), vo.getStaffName() + "직원을 삭제 하였습니다.", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(BaobabStaffSign.this, BaobabCpEmpManagement.class);
                                                intent.putExtra("cpSeq", vo.getCpSeq());
                                                startActivity(intent);
                                                finish();
                                            }else {
                                                Log.d("직원삭제 서버에러", "결과값 fail");
                                                Toast.makeText(getApplicationContext(), vo.getStaffName() + "직원 삭제가 정상적으로 이루어 지지 않았습니다. 고객센터에 문의 하십시오.", Toast.LENGTH_SHORT).show();
                                            }
                                        }else {
                                            Log.d("직원삭제 서버에러", "response 내용없음");
                                            Toast.makeText(getApplicationContext(), vo.getStaffName() + "직원 삭제가 정상적으로 이루어 지지 않았습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                        }
                                    }else {
                                        Log.d("직원삭제 서버에러", "서버로그 확인 필");
                                        Toast.makeText(getApplicationContext(), vo.getStaffName() + "직원 삭제가 정상적으로 이루어 지지 않았습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Integer> call, Throwable t) {
                                    Log.d("직원삭제 통신에러", t.getLocalizedMessage());
                                    Toast.makeText(getApplicationContext(), vo.getStaffName() + "직원 삭제가 정상적으로 이루어 지지 않았습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(BaobabStaffSign.this, "직원삭제가 취소되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }).create();

            findViewById(R.id.deleteBtn).setVisibility(View.VISIBLE);
            findViewById(R.id.deleteBtn).setOnClickListener(staffDelete);
        }

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void makeUpdate(CpStaffVO vo){
        EditText staffNameEt = findViewById(R.id.staff_name);
        staffNameEt.setText(vo.getStaffName());

        RadioGroup radioGroup = findViewById(R.id.rGroup);
        for(int i=0;i<radioGroup.getChildCount();i++){
            if(((RadioButton)radioGroup.getChildAt(i)).getText().toString().equals(vo.getStaffGender())){
                ((RadioButton)radioGroup.getChildAt(i)).setChecked(true);
                break;
            }
        }

        Spinner rankSp = findViewById(R.id.staff_rank);
        String[] rankArr = getResources().getStringArray(R.array.staff_rank);
        for(int i=0;i<rankArr.length;i++){
            if(rtnRank(vo.getDivCode()).equals(rankArr[i])){
                rankSp.setSelection(i);
                break;
            }
        }
    }

    public String rtnRank(String code){
        if(code.equals("c-02-02")){
            return "매니저";
        }else if(code.equals("c-02-03")){
            return "직원";
        }else {
            return "사장님";
        }
    }

    RadioGroup.OnCheckedChangeListener checked = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            gender = ((RadioButton)findViewById(checkedId)).getText().toString() + "자";
        }
    };

    View.OnClickListener staffSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText staffNameEt = findViewById(R.id.staff_name);
            String staffName = staffNameEt.getText().toString();

            Spinner staffRankSp = findViewById(R.id.staff_rank);
            String staffRank = staffRankSp.getSelectedItem().toString();
            String divCode = "";
            if(staffRank.equals("매니저")){
                divCode = "c-02-02";
            }else if(staffRank.equals("직원")){
                divCode = "c-02-03";
            }

            if(staffName.equals("") || staffName.isEmpty()){
                Toast.makeText(BaobabStaffSign.this, "직원 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            }else {
                final LoadDialog loading = new LoadDialog(BaobabStaffSign.this);
                loading.showDialog();

                if(getIntent().getStringExtra("context").equals("find")){
                    UserAllVO vo = (UserAllVO) getIntent().getSerializableExtra("vo");
                    final int cpSeq = getIntent().getIntExtra("cpSeq", 0);

                    Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).staffInfoInsert(vo.getEmail(), vo.getPhon_num(), staffName, gender, divCode, cpSeq);
                    call.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            if(response.isSuccessful()){
                                if(response.body() != null){
                                    int result = response.body();

                                    if(result == 3){
                                        Toast.makeText(BaobabStaffSign.this, "직원등록이 정상적으로 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(BaobabStaffSign.this, BaobabCpEmpManagement.class);
                                        intent.putExtra("cpSeq", cpSeq);
                                        loading.dialogCancel();
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        loading.dialogCancel();
                                        Log.d("직원 등록 ", "전체 로그 확인 필요");
                                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                        intent.putExtra("status", "오류");
                                        startActivity(intent);
                                        finish();
                                    }
                                }else {
                                    loading.dialogCancel();
                                    Log.d("직원 등록 ", "response 내용없음");
                                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                    intent.putExtra("status", "오류");
                                    startActivity(intent);
                                    finish();
                                }
                            }else {
                                loading.dialogCancel();
                                Log.d("직원 등록 ", "서버로그 확인 필요");
                                Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                intent.putExtra("status", "오류");
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            loading.dialogCancel();
                            Log.d("직원 등록 ", "통신에러 : " + t.getLocalizedMessage());
                            Intent intent = new Intent(getApplication(), BaobabInspection.class);
                            intent.putExtra("status", "오류");
                            startActivity(intent);
                            finish();
                        }
                    });
                }else {
                    CpStaffVO vo = (CpStaffVO) getIntent().getSerializableExtra("vo");
                    final int cpSeq = getIntent().getIntExtra("cpSeq", 0);

                    Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).updateStaff(vo.getSeqNum(), staffName, gender, divCode, vo.getEmail());
                    call.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            if(response.isSuccessful()){
                                if(response.body() != null){
                                    int result = response.body();
                                    if(result == 2){
                                        Toast.makeText(BaobabStaffSign.this, "직원 수정이 정상적으로 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(BaobabStaffSign.this, BaobabCpEmpManagement.class);
                                        intent.putExtra("cpSeq", cpSeq);
                                        loading.dialogCancel();
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        loading.dialogCancel();
                                        Log.d("직원 수정 ", "전체 로그 확인 필요");
                                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                        intent.putExtra("status", "오류");
                                        startActivity(intent);
                                        finish();
                                    }
                                }else {
                                    loading.dialogCancel();
                                    Log.d("직원 수정 ", "response 내용없음");
                                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                    intent.putExtra("status", "오류");
                                    startActivity(intent);
                                    finish();
                                }
                            }else {
                                loading.dialogCancel();
                                Log.d("직원 수정 ", "서버로그 확인 필요");
                                Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                intent.putExtra("status", "오류");
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            loading.dialogCancel();
                            Log.d("직원 수정 ", "통신에러 : " + t.getLocalizedMessage());
                            Intent intent = new Intent(getApplication(), BaobabInspection.class);
                            intent.putExtra("status", "오류");
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        }
    };

    View.OnClickListener staffDelete = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            alert.show();
        }
    };

    @Override
    public void onBackPressed(){
        Intent intent = new Intent( getApplicationContext(), BaobabCpEmpManagement.class );
        intent.putExtra("cpSeq", getIntent().getIntExtra("cpSeq", 0));
        startActivity( intent );
        finish();
    }
}
