package com.baobab.user.baobabflyer;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.adapter.ReviewGridViewAdapter;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.ImageCompress;
import com.baobab.user.baobabflyer.server.util.LoadDialog;
import com.baobab.user.baobabflyer.server.util.MakeCertNumber;
import com.baobab.user.baobabflyer.server.util.UserUsedTicketDetailDialog;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewInsert extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    private static final int PICK_IMG_REQUEST = 1;
    ReviewGridViewAdapter reviewGridViewAdapter;
    GridView gv;

    public List<Uri> uriList;
    public static Context context;

    int cpSeq;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_insert);

        context = this;

        cpSeq = getIntent().getIntExtra("cpSeq", 0);
        ((TextView)findViewById(R.id.cpName)).setText(getIntent().getStringExtra("cpName"));

        uriList = new ArrayList<>();
        reviewGridViewAdapter = new ReviewGridViewAdapter(uriList, this);
        gv = findViewById(R.id.gridview);
        gv.setAdapter(reviewGridViewAdapter);

        starScoreEvent();

        findViewById(R.id.insert_layout).setOnClickListener(insert);
    }

    public void starScoreEvent(){
        final ImageView[] stars = new ImageView[]{
                findViewById(R.id.star1),
                findViewById(R.id.star2),
                findViewById(R.id.star3),
                findViewById(R.id.star4),
                findViewById(R.id.star5)
        };

        View.OnClickListener starListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int starPosition = -1;
                for(int i=0;i<stars.length;i++){
                    if(stars[i].equals(v)){
                        starPosition = i;
                        break;
                    }
                }

                for(int i=0;i<=starPosition;i++){
                    Glide.with(ReviewInsert.this).load(R.drawable.star_p).into(stars[i]);
                }

                for(int i=starPosition+1;i<stars.length;i++){
                    Glide.with(ReviewInsert.this).load(R.drawable.star_n).into(stars[i]);
                }

                TextView starScoreTv = findViewById(R.id.starScore);
                starScoreTv.setText((starPosition + 1) + "점");
            }
        };

        for (int i=0;i<stars.length;i++){
            stars[i].setOnClickListener(starListener);
        }

        stars[4].performClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMG_REQUEST && resultCode == RESULT_OK){
            if(data != null){
                ClipData clipData = data.getClipData();
                if(clipData != null){
                    for(int i=0;i<clipData.getItemCount();i++){
                        uriList.add(clipData.getItemAt(i).getUri());
                    }
                }else {
                    uriList.add(data.getData());
                }

                if(uriList.size() > 9){
                    Toast.makeText(this, "사진은 최대 9장 까지 올릴 수 있습니다.", Toast.LENGTH_SHORT).show();

                    List<Uri> tempList = new ArrayList<>();

                    for (int i=0;i<9;i++){
                        tempList.add(uriList.get(i));
                    }

                    uriList = tempList;
                }

                reviewGridViewAdapter = new ReviewGridViewAdapter(uriList, this);
                gv.setAdapter(reviewGridViewAdapter);
            }
        }
    }

    View.OnClickListener insert = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText contentEt = findViewById(R.id.content);

            String email = getSharedPreferences("user", 0).getString("email", "");
            String revCode = makeRevCode(email);

            if(contentEt.getText().toString().equals("") || contentEt.getText() == null){
                Toast.makeText(ReviewInsert.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT);
            }else {
                LoadDialog loading = new LoadDialog(ReviewInsert.this);
                loading.showDialog();

                TextView starScoreTv = findViewById(R.id.starScore);
                int score = Integer.parseInt(starScoreTv.getText().toString().replaceAll("점", ""));

                insertReviewContent(score, contentEt.getText().toString(), email, revCode);

                if(uriList.size() > 0){
                    for(int i=0;i<uriList.size();i++){
                        insertReviewImages(uriList.get(i), email, revCode, i);
                        if(i == (uriList.size() - 1)){
                            Toast.makeText(context, "리뷰가 작성 되었습니다.", Toast.LENGTH_LONG).show();
                            loading.dialogCancel();
                            (UserUsedTicketDetailDialog.view).findViewById(R.id.review_insert).setVisibility(View.GONE);
                            finish();
                        }
                    }
                }else {
                    Toast.makeText(context, "리뷰가 작성 되었습니다.", Toast.LENGTH_LONG).show();
                    (UserUsedTicketDetailDialog.view).findViewById(R.id.review_insert).setVisibility(View.GONE);
                    finish();
                }
            }
        }
    };

    public void insertReviewContent(int score, String content, String email, String revCode){
        String nickName = getSharedPreferences("user", 0).getString("nickName", email);
        Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).revInsert(email, nickName, score, cpSeq, content, revCode, getIntent().getStringExtra("orderNum"));
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        int result = response.body();

                        if(result > 0){
                            Log.d("리뷰 content insert", "완료");
                        }else{
                            Toast.makeText(ReviewInsert.this, "리뷰 컨텐츠가 정상적으로 저장되지 않았습니다.", Toast.LENGTH_SHORT);
                            Log.d("리뷰 content insert", "결과값 이상");
                        }
                    }else {
                        Toast.makeText(ReviewInsert.this, "리뷰 컨텐츠가 정상적으로 저장되지 않았습니다.", Toast.LENGTH_SHORT);
                        Log.d("리뷰 content insert", "response 내용없음");
                    }
                }else {
                    Toast.makeText(ReviewInsert.this, "리뷰 컨텐츠가 정상적으로 저장되지 않았습니다.", Toast.LENGTH_SHORT);
                    Log.d("리뷰 content insert", "서버 로그 확인 필요");
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(ReviewInsert.this, "리뷰 컨텐츠가 정상적으로 저장되지 않았습니다.", Toast.LENGTH_SHORT);
                Log.d("리뷰 content insert", t.getLocalizedMessage());
            }
        });
    }

    public void insertReviewImages(Uri uri, String email, String revCode, int imgPosition){
        ImageCompress imageCompress = new ImageCompress();

        String imgName = imageName(email) + imgPosition + ".webp";

        MultipartBody.Part imgFile = makeMultiFile(imageCompress.getCompressedBitmap(getRealPath(uri)), uri, "revImg");
        RequestBody revCodeRb = RequestBody.create(MediaType.parse("multipart/form-data"), revCode);
        RequestBody imgNameRb = RequestBody.create(MediaType.parse("multipart/form-data"), imgName);

        Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).revImgInsert(imgFile, revCodeRb, imgNameRb);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        int result = response.body();

                        if(result > 0){
                            Log.d("리뷰 img insert", "완료");
                        }else{
                            Toast.makeText(ReviewInsert.this, "리뷰 이미지가 정상적으로 저장되지 않았습니다.", Toast.LENGTH_SHORT);
                            Log.d("리뷰 img insert", "결과값 이상");
                        }
                    }else{
                        Toast.makeText(ReviewInsert.this, "리뷰 이미지가 정상적으로 저장되지 않았습니다.", Toast.LENGTH_SHORT);
                        Log.d("리뷰 img insert", "response 내용없음");
                    }
                }else{
                    Toast.makeText(ReviewInsert.this, "리뷰 이미지가 정상적으로 저장되지 않았습니다.", Toast.LENGTH_SHORT);
                    Log.d("리뷰 img insert", "서버 로그 확인 필요");
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(ReviewInsert.this, "리뷰 이미지가 정상적으로 저장되지 않았습니다.", Toast.LENGTH_SHORT);
                Log.d("리뷰 img insert", t.getLocalizedMessage());
            }
        });
    }

    private String makeRevCode(String email) {
        MakeCertNumber rn = new MakeCertNumber();
        String ranNum = rn.numberGen(6, 1);
        return "rev" + email.split("@")[0] + ranNum + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    private String imageName(String email){
        MakeCertNumber rn = new MakeCertNumber();
        String ranNum = rn.numberGen(3, 1);
        return "rev" + email.split("@")[0] + ranNum + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
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
