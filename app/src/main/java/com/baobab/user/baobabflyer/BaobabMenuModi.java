package com.baobab.user.baobabflyer;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.ImageCompress;
import com.baobab.user.baobabflyer.server.util.ImageUtil;
import com.baobab.user.baobabflyer.server.vo.MenuVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabMenuModi extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    private static int PICK_IMAGE_REQUEST = 1;

    Uri imageUri;
    String imageUrl;

    int menuSeq = 0;

    String cpName;
    int cpSeq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_cp_menu_info);

        findViewById(R.id.add_option).setVisibility(View.GONE);

        ((TextView)findViewById(R.id.title)).setText("메뉴 수정");

        MenuVO vo = (MenuVO) getIntent().getSerializableExtra("vo");
        menuSeq = vo.getSeq_num();
        imageUrl = vo.getMenu_img();
        cpName = vo.getCp_name();
        cpSeq = getIntent().getIntExtra("cpSeq", 0);
        firstSetting(vo);

        findViewById(R.id.save).setOnClickListener(save);
        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void firstSetting(MenuVO vo){
        LinearLayout pickLayout = findViewById(R.id.image_pick_layout);
        final ImageView imageView = findViewById(R.id.image);

        pickLayout.setOnClickListener(pickImage);
        imageView.setOnClickListener(pickImage);

        if(!vo.getMenu_img().equals("이미지없음")){
            pickLayout.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(imageUrl).asBitmap().override(380, 150).centerCrop().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    ImageUtil util = new ImageUtil();
                    imageView.setImageBitmap(util.getRoundedCornerBitmap(resource, 16));
                }
            });
        }

        Spinner categorySp = findViewById(R.id.menu_category);
        categorySp.setBackground(null);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, R.layout.bank_spinner_normal, R.id.spinnerText, new String[] {"메인메뉴", "사이드메뉴", "기타메뉴", "주류/음료"});
        categoryAdapter.setDropDownViewResource(R.layout.bank_spinner_dropdown);
        categorySp.setAdapter(categoryAdapter);
        if(vo.getMenu_div().contains("메인")){
            categorySp.setSelection(0);
        }else if(vo.getMenu_div().contains("사이드")){
            categorySp.setSelection(1);
        }else if(vo.getMenu_div().contains("기타")){
            categorySp.setSelection(2);
        }else {
            categorySp.setSelection(3);
        }

        EditText menuName = findViewById(R.id.menuName);
        menuName.setText(vo.getMenu_name());

        EditText menuIntro = findViewById(R.id.menuIntro);
        menuIntro.setText(vo.getMenu_intro());

        String[] optionArr = getResources().getStringArray(R.array.menuOption);
        Spinner optionSp = findViewById(R.id.menu_option);
        optionSp.setBackground(null);
        ArrayAdapter<String> optionAdapter = new ArrayAdapter<>(this, R.layout.bank_spinner_normal, R.id.spinnerText, optionArr);
        optionAdapter.setDropDownViewResource(R.layout.bank_spinner_dropdown);
        optionSp.setAdapter(optionAdapter);
        for(int i=0;i<optionArr.length;i++){
            if(vo.getMenu_option().equals(optionArr[i])){
                optionSp.setSelection(i);
                break;
            }
        }

        EditText price = findViewById(R.id.price);
        price.setText(String.valueOf(vo.getMenu_price()));
    }

    View.OnClickListener pickImage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }
    };

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST) {
            if(data != null){
                imageUri = data.getData();
                findViewById(R.id.image_pick_layout).setVisibility(View.GONE);
                final ImageView imageView = findViewById(R.id.image);
                imageView.setVisibility(View.VISIBLE);

                Glide.with(getApplicationContext()).load(imageUri).asBitmap().override(380, 150).centerCrop().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        ImageUtil util = new ImageUtil();
                        imageView.setImageBitmap(util.getRoundedCornerBitmap(resource, 16));
                    }
                });
            }else {
                findViewById(R.id.image_pick_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.image).setVisibility(View.GONE);
                imageUrl = "이미지없음";
            }
        }
    }

    View.OnClickListener save = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ImageCompress imageCompress = new ImageCompress();
            String menuName = ((EditText)findViewById(R.id.menuName)).getText().toString();
            RequestBody menuNameRb = RequestBody.create(MediaType.parse("multipart/form-data"), menuName);
            RequestBody category = RequestBody.create(MediaType.parse("multipart/form-data"), ((Spinner)findViewById(R.id.menu_category)).getSelectedItem().toString());
            RequestBody menuIntro = RequestBody.create(MediaType.parse("multipart/form-data"), ((EditText)findViewById(R.id.menuIntro)).getText().toString());
            RequestBody menuSeqRb = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(menuSeq));
            RequestBody optionRb = RequestBody.create(MediaType.parse("multipart/form-data"), ((Spinner)findViewById(R.id.menu_option)).getSelectedItem().toString());
            RequestBody priceRb = RequestBody.create(MediaType.parse("multipart/form-data"), ((EditText)findViewById(R.id.price)).getText().toString());
            RequestBody cpNameRb = RequestBody.create(MediaType.parse("multipart/form-data"), cpName);
            RequestBody imageUrlRb = RequestBody.create(MediaType.parse("multipart/form-data"), imageUrl);

            MultipartBody.Part imgFile = null;
            if(imageUri != null){
                imgFile = makeMultiFile(resizeBitmapImg(imageCompress.getCompressedBitmap(getRealPath(imageUri)), 768), imageUri, menuName);
            }

            Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).menuModi(menuNameRb, optionRb, category, priceRb, menuIntro, imageUrlRb, menuSeqRb, imgFile, cpNameRb);
            call.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    if(response.isSuccessful()){
                        if(response.body() != null){
                            int result = response.body();

                            if(result > 0){
                                Toast.makeText(BaobabMenuModi.this, "메뉴가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                                ((Activity)BaobabMenuModified.context).finish();
                                Intent intent = new Intent(BaobabMenuModi.this, BaobabMenuModified.class);
                                intent.putExtra("getCpNameFromJoin", cpName);
                                intent.putExtra("cpSeq", cpSeq);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(BaobabMenuModi.this, "메뉴가 수정되지 않았습니다. 다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
                                Log.d("메뉴 수정", "result <= 0");
                            }
                        }else {
                            Log.d("메뉴 수정", "response 내용없음");
                        }
                    }else {
                        Log.d("메뉴 수정", "서버 로그 확인 필요");
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    Log.d("메뉴 수정", t.getLocalizedMessage());
                }
            });
        }
    };

    public String getRealPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor c = getContentResolver().query(uri, proj, null, null, null);
        int index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        c.moveToFirst();
        String path = c.getString(index);

        return path;
    }

    public Bitmap resizeBitmapImg(Bitmap source, int maxResolution){
        int width = source.getWidth();
        int height = source.getHeight();
        int newWidth = width;
        int newHeight = height;
        float rate = 0.0f;

        if(width > height){
            if(maxResolution < width){
                rate = maxResolution / (float) width;
                newHeight = (int) (height * rate);
                newWidth = maxResolution;
            }
        }else{
            if(maxResolution < height){
                rate = maxResolution / (float) height;
                newWidth = (int) (width * rate);
                newHeight = maxResolution;
            }
        }

        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
    }

    public MultipartBody.Part makeMultiFile(Bitmap bitmap, Uri uri, String imgName) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.WEBP, 100, bos);
            byte[] imgBit = bos.toByteArray();
            File file = new File(getApplicationContext().getCacheDir(), uri.toString().substring(uri.toString().lastIndexOf("/")));
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(imgBit);
            fos.flush();
            fos.close();
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part realMenuImg = MultipartBody.Part.createFormData(imgName, file.getName(), reqFile);
            return realMenuImg;
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }
}
