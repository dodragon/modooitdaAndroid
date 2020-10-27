package com.baobab.user.baobabflyer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.adapter.BankSpinnerAdapter;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.ImageUtil;
import com.baobab.user.baobabflyer.server.util.LoadDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabBeingMall extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    private int PICK_IMAGE_REQUEST = 1;

    String tagResult;

    Uri licenceUri = null;
    Bitmap bitmap;

    boolean accountCert = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_baobab_being_mall );

        Spinner banks = findViewById( R.id.banks );
        BankSpinnerAdapter bankSpinnerAdapter = new BankSpinnerAdapter(getResources().getStringArray(R.array.banks), this);
        banks.setAdapter(bankSpinnerAdapter);

        Spinner cpKind = findViewById( R.id.cpKind );
        BankSpinnerAdapter kindSpinnerAdapter = new BankSpinnerAdapter(getResources().getStringArray(R.array.cpKind), this);
        cpKind.setAdapter(kindSpinnerAdapter);

        LinearLayout license = findViewById( R.id.license_img_add );
        license.setTag( "1" );
        license.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType( "image/*" );
                intent.setAction( Intent.ACTION_GET_CONTENT );
                startActivityForResult( Intent.createChooser( intent, "Select Picture" ), PICK_IMAGE_REQUEST );
                tagResult = v.getTag().toString();
            }
        } );

        Button nextBtn = findViewById( R.id.commitBtn );
        nextBtn.setOnClickListener(next);

        findViewById(R.id.license_img_clear).setOnClickListener(clearImg);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        EditText nameEt = findViewById(R.id.accountHolder);
        EditText licenseEt = findViewById(R.id.licenseNum);
        EditText accountNumEt = findViewById(R.id.accountNumber);

        nameEt.addTextChangedListener(watcher);
        licenseEt.addTextChangedListener(watcher);
        accountNumEt.addTextChangedListener(watcher);
        findViewById(R.id.accountCertBtn).setOnClickListener(accountConfirm);
    }

    View.OnClickListener clearImg = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            findViewById(R.id.license_img_add).setVisibility(View.VISIBLE);
            findViewById(R.id.license_img_clear).setVisibility(View.GONE);

            licenceUri = null;
            bitmap = null;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        try {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
                ImageUtil imageUtil = new ImageUtil();
                ImageView imageView;

                licenceUri = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap( getContentResolver(), licenceUri );

                imageView = findViewById(R.id.license_img);
                imageView.setImageBitmap(imageUtil.getRoundedCornerBitmap(bitmap, 16));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                findViewById(R.id.license_img_add).setVisibility(View.GONE);
                findViewById(R.id.license_img_clear).setVisibility(View.VISIBLE);
            } else {
                Toast.makeText( this, "이미지를 선택하지 않으셨습니다.", Toast.LENGTH_LONG ).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String name = ((EditText)findViewById(R.id.accountHolder)).getText().toString();
            String licenseNum = ((EditText)findViewById(R.id.licenseNum)).getText().toString();
            String bankNum = ((EditText)findViewById(R.id.accountNumber)).getText().toString();

            Button nextBtn = findViewById( R.id.commitBtn );

            if(name.length() > 0 & licenseNum.length() > 0 & bankNum.length() > 0){
                nextBtn.setBackgroundColor(Color.rgb(92, 124, 250));
            }else {
                nextBtn.setBackgroundColor(Color.rgb(216, 220, 229));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    View.OnClickListener accountConfirm = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!accountCert){
                final LoadDialog loading = new LoadDialog(BaobabBeingMall.this);
                loading.showDialog();

                String bankNum = ((EditText)findViewById(R.id.accountNumber)).getText().toString();
                String bank = ((Spinner)findViewById(R.id.banks)).getSelectedItem().toString();
                final TextView tv = findViewById(R.id.accountCertText);

                Call<String> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).accountCert(bank, bankNum);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful()){
                            if(response.body() != null){
                                String result = response.body();

                                if(result.equals(((EditText)findViewById(R.id.accountHolder)).getText().toString())){
                                    accountCert = true;
                                    tv.setText("계좌 인증 완료");
                                    tv.setTextColor(Color.parseColor("#5c7cfa"));
                                    Toast.makeText(BaobabBeingMall.this, "인증이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    findViewById(R.id.accountNumber).setEnabled(false);
                                    findViewById(R.id.banks).setEnabled(false);
                                    findViewById(R.id.accountHolder).setEnabled(false);
                                    loading.dialogCancel();
                                }else {
                                    accountCert = false;
                                    tv.setText("계좌 인증을 해 주세요.");
                                    tv.setTextColor(Color.parseColor("#b7b7b7"));
                                    Toast.makeText(BaobabBeingMall.this, "예금주명과 계좌정보를 정확하게 입력해주세요.", Toast.LENGTH_SHORT).show();
                                    loading.dialogCancel();
                                }
                            }else {
                                Log.d("계좌조회", "response 내용없음");
                                accountCert = false;
                                tv.setText("계좌 인증을 해 주세요.");
                                tv.setTextColor(Color.parseColor("#b7b7b7"));
                                Toast.makeText(BaobabBeingMall.this, "인증에 실패하였습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                loading.dialogCancel();
                            }
                        }else {
                            Log.d("계좌조회", "서버 로그 확인 필요");
                            accountCert = false;
                            tv.setText("계좌 인증을 해 주세요.");
                            tv.setTextColor(Color.parseColor("#b7b7b7"));
                            Toast.makeText(BaobabBeingMall.this, "인증에 실패하였습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            loading.dialogCancel();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("계좌조회", t.getLocalizedMessage());
                        accountCert = false;
                        tv.setText("계좌 인증을 해 주세요.");
                        tv.setTextColor(Color.parseColor("#b7b7b7"));
                        Toast.makeText(BaobabBeingMall.this, "인증에 실패하였습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        loading.dialogCancel();
                    }
                });
            }else {
                Toast.makeText(BaobabBeingMall.this, "이미 인증이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    View.OnClickListener next = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (accountCert) {
                String name = ((EditText)findViewById(R.id.accountHolder)).getText().toString();
                String licenseNum = ((EditText)findViewById(R.id.licenseNum)).getText().toString();
                String bankNum = ((EditText)findViewById(R.id.accountNumber)).getText().toString();
                String bank = ((Spinner)findViewById(R.id.banks)).getSelectedItem().toString();
                String cpKind = ((Spinner)findViewById(R.id.cpKind)).getSelectedItem().toString();

                if(name.length() == 0){
                    Toast.makeText(BaobabBeingMall.this, "성함을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if(licenseNum.length() != 10){
                    Toast.makeText(BaobabBeingMall.this, "사업자 등록번호를 정확히 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if(bank.equals("은행 선택")){
                    Toast.makeText(BaobabBeingMall.this, "은행을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }else if(bankNum.length() == 0){
                    Toast.makeText(BaobabBeingMall.this, "계좌번호를 정확히 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(BaobabBeingMall.this, BaobabCpInfoUpdate.class);
                    intent.putExtra("accountHolder", name);
                    intent.putExtra("licenseNum", licenseNum);
                    intent.putExtra("bank", bank);
                    intent.putExtra("accountNumber", bankNum);
                    intent.putExtra("cpKind", cpKind);
                    intent.putExtra("license", licenceUri);
                    startActivity(intent);
                    finish();
                }
            }else {
                Toast.makeText(BaobabBeingMall.this, "계좌인증을 해야 합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
