package com.baobab.user.baobabflyer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.LoadDialog;
import com.baobab.user.baobabflyer.server.vo.CPUserDBVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabCpOption extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    private int PICK_IMAGE_LICENSE = 1;

    Bitmap licenseBm;
    Uri licenseUri;

    String email;

    public static Context context;

    boolean accountCert = true;
    String name;
    String accNum;
    String bank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_cp_option);

        context = this;

        email = getSharedPreferences("user", 0).getString("email", "");
        makeLayout(email);

        findViewById(R.id.save).setOnClickListener(save);
        findViewById(R.id.accountCertBtn).setOnClickListener(accCert);
        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void makeLayout(final String email){
        Call<CPUserDBVO> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).cpUserInfo(email);
        call.enqueue(new Callback<CPUserDBVO>() {
            @Override
            public void onResponse(Call<CPUserDBVO> call, Response<CPUserDBVO> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        final CPUserDBVO vo = response.body();
                        insertLayout(vo);

                        name = vo.getAccount_holder();
                        accNum = vo.getAccount_number();
                        bank = vo.getBank();

                        findViewById(R.id.cpPassword).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(BaobabCpOption.this, BaobabCpPwCert.class);
                                intent.putExtra("email", email);
                                intent.putExtra("pw", vo.getCpPw());
                                startActivity(intent);
                            }
                        });

                        Call<String> callStatus = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).getCpStatus(vo.getEmail(), vo.getCP_name());
                        callStatus.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if(response.isSuccessful()){
                                    if(response.body() != null){
                                        String result = response.body();

                                        Switch onOffSwitch = findViewById(R.id.onOffSwitch);
                                        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                String status;
                                                if(isChecked){
                                                    status = "open";
                                                    ((TextView)findViewById(R.id.onOffTv)).setText("on");
                                                }else {
                                                    status = "close";
                                                    ((TextView)findViewById(R.id.onOffTv)).setText("off");
                                                }
                                                setOnOff(vo.getEmail(), vo.getCP_name(), status);
                                            }
                                        });

                                        if(result.equals("open")){
                                            onOffSwitch.setChecked(true);
                                        }else {
                                            onOffSwitch.setChecked(false);
                                        }
                                    }else {
                                        Log.d("업체 status 불러오기", "response 내용없음");
                                    }
                                }else {
                                    Log.d("업체 status 불러오기", "서버 로그 확인 필요");
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.d("업체 status 불러오기", t.getLocalizedMessage());
                            }
                        });
                    }else {
                        Log.d("CPOption 서버에러", "response 내용없음");
                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                        intent.putExtra("status", "오류");
                        startActivity(intent);
                        finish();
                    }
                }else {
                    Log.d("CPOption 서버에러", "서버로그 확인 필요");
                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                    intent.putExtra("status", "오류");
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<CPUserDBVO> call, Throwable t) {
                Log.d("CPOption 통신 실패", "통신에러 : " + t.getLocalizedMessage());
                Intent intent = new Intent(getApplication(), BaobabInspection.class);
                intent.putExtra("status", "오류");
                startActivity(intent);
                finish();
            }
        });
    }

    public void insertLayout(CPUserDBVO vo){
        ImageView licenseImgV = findViewById(R.id.license);
        licenseImgV.setOnClickListener(pickImage);

        if(vo.getCP_license() != null){
            Glide.clear(licenseImgV);
            Glide.with(getApplicationContext()).load(vo.getCP_license()).asBitmap().override(110, 125).centerCrop().signature(new StringSignature(UUID.randomUUID().toString())).into(licenseImgV);
        }

        EditText blEt = findViewById(R.id.bl);
        blEt.setText(vo.getCP_license_num());

        EditText accNameEt = findViewById(R.id.accName);
        accNameEt.setText(vo.getAccount_holder());

        EditText accNumEt = findViewById(R.id.accNum);
        accNumEt.setText(vo.getAccount_number());


        Spinner banks = findViewById( R.id.accBank );
        ArrayAdapter<CharSequence> bankAdapter = ArrayAdapter.createFromResource( this, R.array.banks, R.layout.spinner_cescum_layout );
        bankAdapter.setDropDownViewResource( R.layout.spinner_cescum_layout );
        banks.setAdapter( bankAdapter );

        String[] bankArr = getResources().getStringArray(R.array.banks);
        int position = 0;
        for(int i=0;i<bankArr.length;i++){
            if(bankArr[i].equals(vo.getBank())){
                position = i;
                break;
            }
        }

        banks.setSelection(position);
    }

    public void setOnOff(String ownerEmail, String cpName, final String status){
        Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).cpOnOff(ownerEmail, cpName, status);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        int result = response.body();

                        if(result > 0){
                            if(status.equals("open")){
                                Toast.makeText(BaobabCpOption.this, "업체가 on 상태 입니다. 소비자에게 노출됩니다.", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(BaobabCpOption.this, "업체가 off 상태 입니다. 소비자에게 노출되지 않습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Log.d("업체 on/off", "결과 값 이상");
                            Toast.makeText(BaobabCpOption.this, "업체 on/off 실패\n다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Log.d("업체 on/off", "response 내용없음");
                        Toast.makeText(BaobabCpOption.this, "업체 on/off 실패\n다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Log.d("업체 on/off", "서버 로그 확인 필요");
                    Toast.makeText(BaobabCpOption.this, "업체 on/off 실패\n다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d("업체 on/off", t.getLocalizedMessage());
                Toast.makeText(BaobabCpOption.this, "업체 on/off 실패\n다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    View.OnClickListener accCert = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String bankNum = ((EditText)findViewById(R.id.accNum)).getText().toString();
            String bankStr = ((Spinner)findViewById(R.id.accBank)).getSelectedItem().toString();
            final String nameStr = ((EditText)findViewById(R.id.accName)).getText().toString();

            if(!accountCert || !bankNum.equals(accNum) || !bankStr.equals(bank) || !nameStr.equals(name)){
                final LoadDialog loading = new LoadDialog(BaobabCpOption.this);
                loading.showDialog();

                final TextView tv = findViewById(R.id.accountCertText);

                Call<String> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).accountCert(bankStr, bankNum);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful()){
                            if(response.body() != null){
                                String result = response.body();

                                if(result.equals(nameStr)){
                                    accountCert = true;
                                    tv.setText("계좌 인증 완료");
                                    tv.setTextColor(Color.parseColor("#fc8449"));
                                    Toast.makeText(BaobabCpOption.this, "인증이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    findViewById(R.id.accNum).setEnabled(false);
                                    findViewById(R.id.accBank).setEnabled(false);
                                    findViewById(R.id.accName).setEnabled(false);
                                    loading.dialogCancel();
                                }else {
                                    accountCert = false;
                                    tv.setText("계좌 인증을 해 주세요.");
                                    tv.setTextColor(Color.parseColor("#9b9b9b"));
                                    Toast.makeText(BaobabCpOption.this, "예금주명과 계좌정보를 정확하게 입력해주세요.", Toast.LENGTH_SHORT).show();
                                    loading.dialogCancel();
                                }
                            }else {
                                Log.d("계좌조회", "response 내용없음");
                                accountCert = false;
                                tv.setText("계좌 인증을 해 주세요.");
                                tv.setTextColor(Color.parseColor("#9b9b9b"));
                                Toast.makeText(BaobabCpOption.this, "인증에 실패하였습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                loading.dialogCancel();
                            }
                        }else {
                            Log.d("계좌조회", "서버 로그 확인 필요");
                            accountCert = false;
                            tv.setText("계좌 인증을 해 주세요.");
                            tv.setTextColor(Color.parseColor("#9b9b9b"));
                            Toast.makeText(BaobabCpOption.this, "인증에 실패하였습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            loading.dialogCancel();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("계좌조회", t.getLocalizedMessage());
                        accountCert = false;
                        tv.setText("계좌 인증을 해 주세요.");
                        tv.setTextColor(Color.parseColor("#9b9b9b"));
                        Toast.makeText(BaobabCpOption.this, "인증에 실패하였습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        loading.dialogCancel();
                    }
                });
            }else {
                Toast.makeText(BaobabCpOption.this, "이미 인증이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    View.OnClickListener save = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(accountCert){
                EditText blEt = findViewById(R.id.bl);
                EditText nameEt = findViewById(R.id.accName);
                Spinner bankSp = findViewById(R.id.accBank);
                EditText numEt = findViewById(R.id.accNum);

                if(blEt.getText().toString().length() > 10){
                    Toast.makeText(getApplicationContext(), "사업자등록번호를 정확히 입력해주세요", Toast.LENGTH_SHORT).show();
                    blEt.setFocusable(true);
                }else if(nameEt.getText().toString().length() <= 0){
                    Toast.makeText(getApplicationContext(), "예금주명을 정확히 입력해주세요", Toast.LENGTH_SHORT).show();
                    nameEt.setFocusable(true);
                }else if(bankSp.getSelectedItem().toString().equals("은행 선택")){
                    Toast.makeText(getApplicationContext(), "은행을 정확히 선택해주세요", Toast.LENGTH_SHORT).show();
                    bankSp.setFocusable(true);
                }else if(numEt.getText().toString().length() <= 0){
                    Toast.makeText(getApplicationContext(), "계좌번호를 정확히 입력해주세요", Toast.LENGTH_SHORT).show();
                    numEt.setFocusable(true);
                }else{
                    RequestBody emailRb = RequestBody.create(MediaType.parse("multipart/form-data"), email);
                    RequestBody licenseNumRb = RequestBody.create(MediaType.parse("multipart/form-data"), blEt.getText().toString());
                    RequestBody nameRb = RequestBody.create(MediaType.parse("multipart/form-data"), nameEt.getText().toString());
                    RequestBody bankRb = RequestBody.create(MediaType.parse("multipart/form-data"), bankSp.getSelectedItem().toString());
                    RequestBody accNumRb = RequestBody.create(MediaType.parse("multipart/form-data"), numEt.getText().toString());
                    MultipartBody.Part license = makeMultiFile(licenseBm, licenseUri, "license");

                    Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).changeCpUser(emailRb, licenseNumRb, nameRb, bankRb, accNumRb, license);
                    call.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            if(response.isSuccessful()){
                                if(response.body() != null){
                                    int result = response.body();
                                    if(result > 0){
                                        Toast.makeText(BaobabCpOption.this, "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }else {
                                        Toast.makeText(BaobabCpOption.this, "일치하는 계정 정보가 없습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Log.d("CPOption upload 서버에러", "response 내용없음");
                                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                    intent.putExtra("status", "오류");
                                    startActivity(intent);
                                    finish();
                                }
                            }else {
                                Log.d("CPOption upload 서버에러", "서버로그 확인 필요");
                                Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                intent.putExtra("status", "오류");
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            Log.d("CPOption upload 통신 실패", "통신에러 : " + t.getLocalizedMessage());
                            Intent intent = new Intent(getApplication(), BaobabInspection.class);
                            intent.putExtra("status", "오류");
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }else {
                Toast.makeText(BaobabCpOption.this, "계좌인증을 해야 합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    View.OnClickListener pickImage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, PICK_IMAGE_LICENSE);
        }
    };

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_LICENSE) {
            if (resultCode == RESULT_OK && data != null) {
                try {
                    licenseUri = data.getData();
                    licenseBm = MediaStore.Images.Media.getBitmap( getContentResolver(), licenseUri);

                    ImageView licenseImgV = findViewById(R.id.license);
                    Glide.clear(licenseImgV);
                    Glide.with(getApplicationContext()).load(licenseUri).asBitmap().override(110, 125).centerCrop().signature(new StringSignature(UUID.randomUUID().toString())).into(licenseImgV);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText( this, "이미지를 선택하지 않으셨습니다.", Toast.LENGTH_LONG ).show();
            }
        } else {
            Toast.makeText( this, "이미지를 선택하지 않으셨습니다.", Toast.LENGTH_LONG ).show();
        }
    }

    public MultipartBody.Part makeMultiFile(Bitmap bitmap, Uri uri, String imgName) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, bos);
            byte[] imgBit = bos.toByteArray();
            File file = new File(getApplicationContext().getCacheDir(), uri.toString().substring(uri.toString().lastIndexOf("/")));
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(imgBit);
            fos.flush();
            fos.close();
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part realMenuImg = MultipartBody.Part.createFormData(imgName, file.getName(), reqFile);
            return realMenuImg;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }
}
