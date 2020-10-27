package com.baobab.user.baobabflyer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabCpMenuInfo extends AppCompatActivity {

    RetroSingleTon retroSingleTon;
    private int PICK_IMAGE_REQUEST = 1;
    String cpName;
    int cpSeq;

    Uri imageUri;
    String[] menuOptionArr;
    ArrayAdapter<String> optionAdapter;

    int optionEa = 0;
    List<String> optionArr;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_cp_menu_info);

        optionArr = new ArrayList<>();

        cpName = getIntent().getStringExtra("getCpNameFromJoin");
        cpSeq = getIntent().getIntExtra("cpSeq", 0);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.image).setOnClickListener(pickImage);
        findViewById(R.id.image_pick_layout).setOnClickListener(pickImage);

        Spinner categorySp = findViewById(R.id.menu_category);
        categorySp.setBackground(null);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, R.layout.bank_spinner_normal, R.id.spinnerText, new String[] {"메인메뉴", "사이드메뉴", "기타메뉴", "주류/음료"});
        categoryAdapter.setDropDownViewResource(R.layout.bank_spinner_dropdown);
        categorySp.setAdapter(categoryAdapter);
        categorySp.setSelection(0);

        menuOptionArr = getResources().getStringArray(R.array.menuOption);
        optionAdapter = new ArrayAdapter<>(this, R.layout.bank_spinner_normal, R.id.spinnerText, menuOptionArr);
        optionAdapter.setDropDownViewResource(R.layout.bank_spinner_dropdown);

        findViewById(R.id.add_option).setOnClickListener(addOption);
        findViewById(R.id.save).setOnClickListener(save);

        findViewById(R.id.menu_default).setVisibility(View.GONE);
        findViewById(R.id.add_option).performClick();
    }

    View.OnClickListener pickImage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }
    };

    View.OnClickListener addOption = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addOptionLayout();
            optionArr.add(String.valueOf(optionEa));
            optionEa++;
        }
    };

    private void addOptionLayout(){
        LinearLayout fatherLayout = findViewById(R.id.option_father);

        LinearLayout firstLayout = new LinearLayout(this);
        LinearLayout.LayoutParams firstParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        firstParam.setMargins(dp(20), dp(9), dp(20), dp(9));
        firstLayout.setLayoutParams(firstParam);
        firstLayout.setBackground(getDrawable(R.drawable.all_round_non_border_white));
        firstLayout.setElevation(dp(4));
        firstLayout.setOrientation(LinearLayout.VERTICAL);
        firstLayout.setPadding(dp(20), dp(20), dp(20), dp(20));
        fatherLayout.addView(firstLayout);

        LinearLayout titleLayout = new LinearLayout(this);
        titleLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        titleLayout.setGravity(Gravity.CENTER_VERTICAL);
        firstLayout.addView(titleLayout);

        TextView titleTv = new TextView(this);
        titleTv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if(optionEa < 10){
            titleTv.setText("옵션 0" + (optionEa + 1));
        }else {
            titleTv.setText("옵션 " + (optionEa + 1));
        }
        titleTv.setTextColor(Color.parseColor("#333333"));
        titleTv.setTextSize(dp(6));
        titleTv.setTypeface(Typeface.DEFAULT_BOLD);
        titleTv.setLetterSpacing(-0.07f);
        titleLayout.addView(titleTv);

        titleLayout.addView(centerView());

        ImageView clearImg = new ImageView(this);
        clearImg.setId(optionEa);
        clearImg.setLayoutParams(new LinearLayout.LayoutParams(dp(18), dp(17)));
        clearImg.setImageDrawable(getDrawable(R.drawable.ic_delete));
        clearImg.setOnClickListener(clearBox);
        titleLayout.addView(clearImg);

        LinearLayout optionBox = new LinearLayout(this);
        LinearLayout.LayoutParams optionBoxParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        optionBoxParams.setMargins(0, dp(20), 0, 0);
        optionBox.setLayoutParams(optionBoxParams);
        optionBox.setPadding(dp(16), dp(20), dp(16), dp(20));
        optionBox.setBackground(getDrawable(R.drawable.all_round_non_border_skyblue));
        optionBox.setOrientation(LinearLayout.VERTICAL);
        firstLayout.addView(optionBox);

        TextView optionTagTv = new TextView(this);
        optionTagTv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        optionTagTv.setText("메뉴 옵션");
        optionTagTv.setTextSize(dp(5));
        optionTagTv.setTextColor(Color.parseColor("#9b9b9b"));
        optionTagTv.setLetterSpacing(-0.08f);
        optionTagTv.setLineSpacing(8f, 8f);
        optionBox.addView(optionTagTv);

        Spinner optionSp = new Spinner(this);
        optionSp.setBackground(null);
        optionSp.setId(100 + optionEa);
        optionSp.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(36)));
        optionSp.setAdapter(optionAdapter);
        optionSp.setSelection(0);
        optionBox.addView(optionSp);

        optionBox.addView(lineView());

        TextView priceTagTv = new TextView(this);
        LinearLayout.LayoutParams priceTagParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        priceTagParams.topMargin = dp(12);
        priceTagTv.setLayoutParams(priceTagParams);
        priceTagTv.setText("판매가 입력");
        priceTagTv.setTextSize(dp(5));
        priceTagTv.setTextColor(Color.parseColor("#9b9b9b"));
        priceTagTv.setLetterSpacing(-0.08f);
        priceTagTv.setLineSpacing(8f, 8f);
        optionBox.addView(priceTagTv);

        LinearLayout priceLayout = new LinearLayout(this);
        priceLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        priceLayout.setGravity(Gravity.CENTER_VERTICAL);
        priceLayout.setOrientation(LinearLayout.HORIZONTAL);
        optionBox.addView(priceLayout);

        EditText priceEt = new EditText(this);
        priceEt.setId(1000 + optionEa);
        priceEt.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(36), 1));
        priceEt.setBackground(null);
        priceEt.setGravity(Gravity.TOP);
        priceEt.setHint("판매가");
        priceEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        priceEt.setLetterSpacing(-0.07f);
        priceEt.setLines(1);
        priceEt.setSingleLine();
        priceEt.setTextColor(Color.parseColor("#515151"));
        setCursorDrawableColor(priceEt, Color.parseColor("#fc8441"));
        priceEt.setTextSize(dp(6));
        priceLayout.addView(priceEt);

        TextView wonTv = new TextView(this);
        LinearLayout.LayoutParams wonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        wonParams.setMargins(dp(8), dp(12), 0, 0);
        wonTv.setLayoutParams(wonParams);
        wonTv.setText("원");
        wonTv.setTextSize(dp(5));
        wonTv.setTextColor(Color.parseColor("#9b9b9b"));
        wonTv.setLetterSpacing(-0.08f);
        wonTv.setLineSpacing(8f, 8f);
        priceLayout.addView(wonTv);

        optionBox.addView(lineView());
    }

    public static void setCursorDrawableColor(EditText editText, int color) {
        try {
            Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            fCursorDrawableRes.setAccessible(true);
            int mCursorDrawableRes = fCursorDrawableRes.getInt(editText);
            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(editText);
            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);
            Drawable[] drawables = new Drawable[2];
            drawables[0] = editText.getContext().getResources().getDrawable(mCursorDrawableRes);
            drawables[1] = editText.getContext().getResources().getDrawable(mCursorDrawableRes);
            drawables[0].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            drawables[1].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            fCursorDrawable.set(editor, drawables);
        } catch (Throwable ignored) {
        }
    }

    private View lineView(){
        View view = new View(this);
        view.setLayoutParams(new LinearLayout.LayoutParams(0, dp(1), 0));
        view.setBackgroundColor(Color.parseColor("#515151"));
        return view;
    }

    private View centerView(){
        View view = new View(this);
        view.setLayoutParams(new LinearLayout.LayoutParams(0, 0, 1));
        return view;
    }

    public int dp(int dp) {
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        return Math.round(dp * dm.density);
    }

    View.OnClickListener clearBox = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LinearLayout fatherLayout = findViewById(R.id.option_father);
            fatherLayout.removeView((View) v.getParent().getParent());
            optionArr.remove(String.valueOf(v.getId()));
            optionEa--;
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
            }
        }
    }

    View.OnClickListener save = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(optionArr.size() > 0){
                ImageCompress imageCompress = new ImageCompress();
                String menuName = ((EditText)findViewById(R.id.menuName)).getText().toString();
                RequestBody menuNameRb = RequestBody.create(MediaType.parse("multipart/form-data"), menuName);
                RequestBody category = RequestBody.create(MediaType.parse("multipart/form-data"), ((Spinner)findViewById(R.id.menu_category)).getSelectedItem().toString());
                RequestBody menuIntro = RequestBody.create(MediaType.parse("multipart/form-data"), ((EditText)findViewById(R.id.menuIntro)).getText().toString());
                RequestBody cpNameRb = RequestBody.create(MediaType.parse("multipart/form-data"), cpName);
                RequestBody cpSeqRb = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(cpSeq));
                MultipartBody.Part imgFile = null;
                if(imageUri != null){
                    imgFile = makeMultiFile(resizeBitmapImg(imageCompress.getCompressedBitmap(getRealPath(imageUri)), 768), imageUri, menuName);
                }
                for(int i=0;i<optionArr.size();i++){
                    RequestBody optionRb = RequestBody.create(MediaType.parse("multipart/form-data"), ((Spinner)findViewById(100 + Integer.parseInt(optionArr.get(i)))).getSelectedItem().toString());
                    RequestBody priceRb = RequestBody.create(MediaType.parse("multipart/form-data"), ((EditText)findViewById(1000 + Integer.parseInt(optionArr.get(i)))).getText().toString());

                    saveMenu(menuNameRb, cpNameRb, category, optionRb, priceRb, imgFile, menuIntro, cpSeqRb, Integer.parseInt(optionArr.get(i)));

                    if(i == (optionEa - 1)){
                        Toast.makeText(getApplicationContext(), "메뉴가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }else {
                Toast.makeText(getApplicationContext(), "옵션을 추가해 옵션과 가격을 적어 주세요.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void saveMenu(RequestBody menuNameRb, RequestBody cpNameRb, RequestBody category, RequestBody optionRb, RequestBody priceRb, MultipartBody.Part imgFile, RequestBody menuIntro, RequestBody cpSeqRb, final int position){
        Call<ResponseBody> call = retroSingleTon.getInstance().menuJoinInterface(getApplicationContext()).setCPMenu(menuNameRb, category, cpNameRb, optionRb, priceRb, imgFile, menuIntro, cpSeqRb);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(position + "번째 옵션 : ", t.getLocalizedMessage());
                Toast.makeText(getApplicationContext(), "옵션명 : " + ((Spinner)findViewById(100 + position)).getSelectedItem().toString() + "이/가 저장되지 못했습니다. 메뉴 확인 후 다시 저장해 주십시오.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
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