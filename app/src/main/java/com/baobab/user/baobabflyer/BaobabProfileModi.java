package com.baobab.user.baobabflyer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.ImageCompress;
import com.baobab.user.baobabflyer.server.util.ImageUtil;
import com.baobab.user.baobabflyer.server.util.LoadDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
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

public class BaobabProfileModi extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    ImageView profileIv;
    EditText nickNameEt;
    MultipartBody.Part imgFile = null;

    private int PICK_IMAGE_REQUEST = 1;

    String url;

    AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_profile_modi);

        profileIv = findViewById(R.id.profileImg);
        nickNameEt = findViewById(R.id.nickName);

        final SharedPreferences userSpf = getSharedPreferences("user", 0);
        String nickName = userSpf.getString("nickName", "");
        url = userSpf.getString("profile", "");

        alert = new AlertDialog.Builder(BaobabProfileModi.this)
                .setIcon(R.drawable.logo_and)
                .setTitle("프로필이미지")
                .setMessage("이미지를 삭제 하시겠습니까? 수정을 원하시면 수정을 눌러주세요.")
                .setPositiveButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        startActivityForResult(intent, PICK_IMAGE_REQUEST);
                    }
                })
                .setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).profileDelete(userSpf.getString("email", ""), url);
                        call.enqueue(new Callback<Integer>() {
                            @Override
                            public void onResponse(Call<Integer> call, Response<Integer> response) {
                                if(response.isSuccessful()){
                                    if(response.body() != null){
                                        int result = response.body();

                                        if(result > 0){
                                            Toast.makeText(getApplicationContext(), "이미지가 정상적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                            SharedPreferences.Editor editor = userSpf.edit();
                                            editor.putString("profile", "이미지없음");
                                            editor.apply();

                                            Intent intent = new Intent(BaobabProfileModi.this, BaobabProfileModi.class);
                                            startActivity(intent);
                                            finish();
                                        }else {
                                            Toast.makeText(getApplicationContext(), "이미지가 정상적으로 삭제되지 않았습니다. 다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
                                        }
                                    }else {
                                        Log.d("프로필 이미지 삭제", "response 내용없음");
                                        Toast.makeText(getApplicationContext(), "이미지가 정상적으로 삭제되지 않았습니다. 다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Log.d("프로필 이미지 삭제", "서버로그 확인 필요");
                                    Toast.makeText(getApplicationContext(), "이미지가 정상적으로 삭제되지 않았습니다. 다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Integer> call, Throwable t) {
                                Log.d("프로필 이미지 삭제", t.getLocalizedMessage());
                                Toast.makeText(getApplicationContext(), "이미지가 정상적으로 삭제되지 않았습니다. 다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).create();

        setLayout(url, nickName);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setLayout(String url, String nickName){
        nickNameEt.setText(nickName);

        Object selectUrl;
        if(url.equals("") || url.equals("이미지없음")){
            selectUrl = R.drawable.ic_profile_default;
        }else {
            selectUrl = url;
        }

        Glide.clear(profileIv);
        Glide.with(getApplicationContext()).load(selectUrl).asBitmap().override(120, 120).signature(new StringSignature(UUID.randomUUID().toString())).centerCrop().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                ImageUtil imageUtil = new ImageUtil();
                Bitmap bitmap = imageUtil.getCircularBitmap(resource);
                profileIv.setImageBitmap(bitmap);
            }
        });

        profileIv.setOnClickListener(pickImage);
        findViewById(R.id.saveBtn).setOnClickListener(save);
    }

    View.OnClickListener save = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final LoadDialog loading = new LoadDialog(BaobabProfileModi.this);
            loading.showDialog();
            RequestBody nickNameRb = RequestBody.create(MediaType.parse("multipart/form-data"), nickNameEt.getText().toString());
            RequestBody emailRb = RequestBody.create(MediaType.parse("multipart/form-data"), getSharedPreferences("user", 0).getString("email", ""));
            if(nickNameEt.getText().toString().isEmpty() || nickNameEt.getText().toString().length() == 0){
                Toast.makeText(getApplicationContext(), "닉네임을 입력 해주세요.", Toast.LENGTH_SHORT).show();
                loading.dialogCancel();
            }else {
                Call<String> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).setProfile(imgFile, nickNameRb, emailRb);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful()){
                            if(response.body() != null){
                                String result = response.body();

                                if(result.equals("dup")){
                                    Toast.makeText(getApplicationContext(), "중복된 닉네임이 존재합니다. 다른 닉네임을 사용하세요.", Toast.LENGTH_SHORT).show();
                                    loading.dialogCancel();
                                }else{
                                    Toast.makeText(getApplicationContext(), "프로필 변경 완료!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(BaobabProfileModi.this, BaobabAnterMain.class);
                                    SharedPreferences spf = getSharedPreferences("user", 0);
                                    SharedPreferences.Editor editor = spf.edit();
                                    editor.putString("nickName", nickNameEt.getText().toString());
                                    editor.putString("profile", result);
                                    editor.apply();
                                    loading.dialogCancel();
                                    startActivity(intent);
                                    finishAffinity();
                                }
                            }else {
                                Log.d("서버에러", "response 내용없음");
                                Toast.makeText(getApplicationContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                loading.dialogCancel();
                            }
                        }else {
                            Log.d("서버에러", "서버로그 확인 필요");
                            Toast.makeText(getApplicationContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            loading.dialogCancel();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("통신실패", t.getLocalizedMessage());
                        Toast.makeText(getApplicationContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        loading.dialogCancel();
                    }
                });
            }
        }
    };

    View.OnClickListener pickImage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!getSharedPreferences("user", 0).getString("profile", "").equals("이미지없음")){
                alert.show();
            }else {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        }
    };

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST) {
            try {
                if (resultCode == RESULT_OK && data != null) {
                    Uri uri = data.getData();

                    ImageCompress imageCompress = new ImageCompress();
                    String imgPath = getRealPath(uri);
                    final Bitmap bitmap = imageCompress.getCompressedBitmap(imgPath);
                    final ImageUtil imageUtil = new ImageUtil();
                    Glide.with(getApplicationContext()).load(uri).asBitmap().override(300, 300).centerCrop().into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            profileIv.setImageBitmap(imageUtil.getCircularBitmap(resource));
                        }
                    });

                    imgFile = makeMultiFile(bitmap, uri, "profileImg");
                } else {
                    Toast.makeText(this, "이미지를 선택하지 않으셨습니다.", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getRealPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor c = getContentResolver().query(uri, proj, null, null, null);
        int index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        c.moveToFirst();
        String path = c.getString(index);

        return path;
    }

    public MultipartBody.Part makeMultiFile(Bitmap bitmap, Uri uri, String imgName) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
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
