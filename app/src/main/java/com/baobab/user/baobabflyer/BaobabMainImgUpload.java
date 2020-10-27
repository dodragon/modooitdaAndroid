package com.baobab.user.baobabflyer;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.adapter.MainImgGridViewAdapter;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.ImageCompress;
import com.baobab.user.baobabflyer.server.util.LoadDialog;
import com.baobab.user.baobabflyer.server.vo.CPmainImgVO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabMainImgUpload extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;
    private int PICK_IMAGE_REQUEST_UP = 2;

    RetroSingleTon retroSingleTon;

    AlertDialog alert;

    public static String url;

    List<Uri> selectUriList;
    List<Uri> selectedUriList;
    List<String> urlList;
    List<Object> imgList;

    MainImgGridViewAdapter mainImgGridViewAdapter;
    GridView gv;
    int displayW;

    boolean isNeedSaved = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_main_img);

        urlList = new ArrayList<>();
        selectedUriList = new ArrayList<>();
        selectUriList = new ArrayList<>();
        gv = findViewById(R.id.gridview);
        displayW = (getApplicationContext().getResources().getDisplayMetrics().widthPixels / 3) - 2;

        findViewById(R.id.add_img).setOnClickListener(pickImage);

        makeDisplay(getIntent().getIntExtra("cpSeq", 0));

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        alert = new AlertDialog.Builder(BaobabMainImgUpload.this)
                .setIcon(R.drawable.logo_and)
                .setTitle("메인이미지")
                .setMessage("이미지를 삭제하시겠습니까? 수정하시겠습니까?")
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).delMainImg(url);
                        call.enqueue(new Callback<Integer>() {
                            @Override
                            public void onResponse(Call<Integer> call, Response<Integer> response) {
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        Log.d("메인이미지 삭제", "통신완료");
                                        int result = response.body();

                                        if (result >= 0) {
                                            Toast.makeText(BaobabMainImgUpload.this, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), BaobabMainImgUpload.class);
                                            intent.putExtra("cpName", getIntent().getStringExtra("cpName"));
                                            intent.putExtra("cpSeq", getIntent().getIntExtra("cpSeq", 0));
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(BaobabMainImgUpload.this, "삭제에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Log.d("메인이미지 삭제", "response 내용없음");
                                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                        intent.putExtra("status", "오류");
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    Log.d("메인이미지 삭제", "서버에러 (로그확인)");
                                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                    intent.putExtra("status", "오류");
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<Integer> call, Throwable t) {
                                Log.d("메인이미지 삭제", "통신에러 : " + t.getLocalizedMessage());
                                Intent intent = new Intent(getApplication(), BaobabInspection.class);
                                intent.putExtra("status", "오류");
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                })
                .setNegativeButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        startActivityForResult(intent, PICK_IMAGE_REQUEST_UP);
                    }
                }).create();

        findViewById(R.id.img_save).setOnClickListener(imgSave);
    }

    public void makeDisplay(int seq) {
        Call<List<CPmainImgVO>> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).getMainImg(seq);
        call.enqueue(new Callback<List<CPmainImgVO>>() {
            @Override
            public void onResponse(Call<List<CPmainImgVO>> call, Response<List<CPmainImgVO>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("메인이미지", "통신완료");
                        List<CPmainImgVO> imgVOList = response.body();

                        if (!imgVOList.isEmpty()) {
                            settingPage(imgVOList);
                        } else {
                            AlertDialog noImageAlert = new AlertDialog.Builder(BaobabMainImgUpload.this)
                                    .setIcon(R.drawable.logo_and)
                                    .setTitle("메인이미지")
                                    .setMessage("이미지가 없습니다. 이미지를 추가하시겠습니까?")
                                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                        @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            imgList = new ArrayList<>();
                                            selectedUriList = selectUriList;
                                            selectUriList = new ArrayList<>();

                                            Intent intent = new Intent(Intent.ACTION_PICK);
                                            intent.setType("image/*");
                                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                            startActivityForResult(intent, PICK_IMAGE_REQUEST);
                                        }
                                    })
                                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(BaobabMainImgUpload.this, "추가 하시려면 아래 이미지 추가 버튼을 눌러주세요.", Toast.LENGTH_SHORT).show();
                                        }
                                    }).create();
                            noImageAlert.show();
                        }
                    } else {
                        Log.d("메인이미지", "response 내용없음");
                        Intent intent = new Intent(getApplication(), BaobabInspection.class);
                        intent.putExtra("status", "오류");
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Log.d("메인이미지", "서버에러 (서버로그 확인)");
                    Intent intent = new Intent(getApplication(), BaobabInspection.class);
                    intent.putExtra("status", "오류");
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<CPmainImgVO>> call, Throwable t) {
                Log.d("메인이미지", "통신에러 : " + t.getLocalizedMessage());
                Intent intent = new Intent(getApplication(), BaobabInspection.class);
                intent.putExtra("status", "오류");
                startActivity(intent);
                finish();
            }
        });
    }

    private void settingPage(List<CPmainImgVO> list){
        List<Object> imgs = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            imgs.add(list.get(i).getImg_url());
            urlList.add(list.get(i).getImg_url());
        }

        mainImgGridViewAdapter = new MainImgGridViewAdapter(imgs, this, displayW, alert);
        gv.setAdapter(mainImgGridViewAdapter);
    }

    View.OnClickListener pickImage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            imgList = new ArrayList<>();
            selectedUriList = selectUriList;
            selectUriList = new ArrayList<>();

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }
    };

    View.OnClickListener imgSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LoadDialog loading = new LoadDialog(BaobabMainImgUpload.this);
            loading.showDialog();

            if(isNeedSaved){
                ImageCompress imageCompress = new ImageCompress();

                RequestBody cpNameRb = RequestBody.create(MediaType.parse("multipart/form-data"), getIntent().getStringExtra("cpName"));
                RequestBody cpSeqRb = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(getIntent().getIntExtra("cpSeq", 0)));

                for(int i=0;i<selectUriList.size();i++){
                    MultipartBody.Part imgFile = makeMultiFile(resizeBitmapImg(imageCompress.getCompressedBitmap(getRealPath(selectUriList.get(i))), 768), selectUriList.get(i), "mainImg");

                    imageInsert(imgFile, cpNameRb, cpSeqRb);

                    if(i == (selectUriList.size() - 1)){
                        isNeedSaved = false;
                        Toast.makeText(getApplicationContext(), "이미지가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        loading.dialogCancel();
                    }
                }
            }else {
                Toast.makeText(getApplicationContext(), "이미 저장되었거나 저장할 이미지가 없습니다.", Toast.LENGTH_SHORT).show();
                loading.dialogCancel();
            }
        }
    };

    public void imageInsert(MultipartBody.Part img, RequestBody cpName, RequestBody cpSeq){
        Call<ResponseBody> call = retroSingleTon.getInstance().mainImgUpload(getApplicationContext()).setMainImg(img, cpName, cpSeq);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST) {
            if(data != null){
                ClipData clipData = data.getClipData();
                if(clipData != null){
                    for(int i=0;i<clipData.getItemCount();i++){
                        selectUriList.add(clipData.getItemAt(i).getUri());
                    }
                }else {
                    selectUriList.add(data.getData());
                }

                try{
                    imgList.addAll(urlList);
                    imgList.addAll(selectedUriList);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                imgList.addAll(selectUriList);

                mainImgGridViewAdapter = new MainImgGridViewAdapter(imgList, BaobabMainImgUpload.this, displayW, alert);
                gv.setAdapter(mainImgGridViewAdapter);

                isNeedSaved = true;
            }
        } else {
            final LoadDialog loading = new LoadDialog(BaobabMainImgUpload.this);
            loading.showDialog();

            final Uri uri = data.getData();

            ImageCompress imageCompress = new ImageCompress();

            MultipartBody.Part imgFile = makeMultiFile(resizeBitmapImg(imageCompress.getCompressedBitmap(getRealPath(uri)), 768), uri, "mainImg");
            RequestBody urlRb = RequestBody.create(MediaType.parse("multipart/form-data"), url);

            Call<Integer> call = retroSingleTon.getInstance().mainImgUpload(getApplicationContext()).mainImgUpdate(imgFile, urlRb);
            call.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    if(response.isSuccessful()){
                        if(response.body() != null){
                            int result = response.body();

                            if(result > 0){
                                Toast.makeText(BaobabMainImgUpload.this, "이미지가 수정 되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), BaobabMainImgUpload.class);
                                intent.putExtra("cpName", getIntent().getStringExtra("cpName"));
                                intent.putExtra("cpSeq", getIntent().getIntExtra("cpSeq", 0));
                                loading.dialogCancel();
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(BaobabMainImgUpload.this, "저장되지 않은 이미지는 수정할 수 없습니다. 삭제 후 다시 이미지를 추가해 주세요.", Toast.LENGTH_SHORT).show();
                                loading.dialogCancel();
                            }
                        }else {
                            Toast.makeText(BaobabMainImgUpload.this, "이미지가 수정 되지 않았습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            Log.d("메인이미지 update", "response 내용없음");
                            loading.dialogCancel();
                        }
                    }else {
                        Toast.makeText(BaobabMainImgUpload.this, "이미지가 수정 되지 않았습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        Log.d("메인이미지 update", "서버 로그 확인 필요");
                        loading.dialogCancel();
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    Toast.makeText(BaobabMainImgUpload.this, "이미지가 수정 되지 않았습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                    Log.d("메인이미지 update", t.getLocalizedMessage());
                    loading.dialogCancel();
                }
            });
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