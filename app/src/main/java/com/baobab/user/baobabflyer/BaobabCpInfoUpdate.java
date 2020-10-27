package com.baobab.user.baobabflyer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.util.LoadDialog;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;

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


public class BaobabCpInfoUpdate extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    Bitmap licenseBmp;

    EditText cpName;
    EditText cpPhone;
    TextView cpZipcode;
    TextView cpAddress;
    EditText cpAddrDetails;

    Spinner kind;
    Spinner type;

    EditText selfKind;
    EditText selfType;

    CheckBox mon;
    CheckBox tue;
    CheckBox wed;
    CheckBox thu;
    CheckBox fri;
    CheckBox sat;
    CheckBox sun;

    CheckBox theme1_1;
    CheckBox theme1_2;
    CheckBox theme1_3;
    CheckBox theme1_4;
    CheckBox theme1_5;
    CheckBox theme1_6;
    CheckBox theme1_7;
    CheckBox theme1_8;
    CheckBox theme1_9;
    EditText theme1_10;

    EditText parking;

    CheckBox theme2_1;
    CheckBox theme2_2;
    CheckBox theme2_3;
    CheckBox theme2_4;
    CheckBox theme2_5;
    CheckBox theme2_6;
    CheckBox theme2_7;
    CheckBox theme2_8;
    CheckBox theme2_9;
    EditText theme2_10;

    Spinner businessStartHour;
    Spinner businessStartMinute;
    Spinner businessEndHour;
    Spinner businessEndMinute;

    LinearLayout selfKindLayout;
    LinearLayout selfTypeLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_baobab_cp_info );

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent getKind = getIntent();

        if (!getKind.getStringExtra( "cpKind" ).equals( "음식점" )) {
            LinearLayout restaurant = findViewById( R.id.restaurant );
            restaurant.setVisibility( View.GONE );
        }

        kind = findViewById( R.id.kind );
        populateSpinners();

        type = findViewById( R.id.type );
        populteSubSpinners( R.array.type1 );

        kind.setOnItemSelectedListener( spinSelectedlistener );
        type.setOnItemSelectedListener( spinSelectedlistener2 );

        selfKindLayout = findViewById( R.id.selfKindLayout );
        selfTypeLayout = findViewById( R.id.selfTypeLayout );

        businessStartHour = findViewById( R.id.businessStartHour );
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( this, R.array.hour, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource( R.layout.spinner_cescum_layout );
        businessStartHour.setAdapter( adapter );

        businessStartMinute = findViewById( R.id.businessStartMinute );
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource( this, R.array.minute, android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource( R.layout.spinner_cescum_layout );
        businessStartMinute.setAdapter( adapter2 );

        businessEndHour = findViewById( R.id.businessEndHour );
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource( this, R.array.hour, android.R.layout.simple_spinner_dropdown_item);
        adapter3.setDropDownViewResource( R.layout.spinner_cescum_layout );
        businessEndHour.setAdapter( adapter3 );
        businessEndHour.setSelection(24);

        businessEndMinute = findViewById( R.id.businessEndMinute );
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource( this, R.array.minute, android.R.layout.simple_spinner_dropdown_item);
        adapter4.setDropDownViewResource( R.layout.spinner_cescum_layout );
        businessEndMinute.setAdapter( adapter4 );

        Button addressSearch = findViewById( R.id.addressSearth );
        addressSearch.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent as = new Intent( BaobabCpInfoUpdate.this, BaobabAddressSearch.class );
                as.putExtra("classKind", "being");
                startActivityForResult( as, 0 );

            }
        } );

        themeSetting();

        final Button[] weeksBtnArr = {findViewById(R.id.weeksBtn1), findViewById(R.id.weeksBtn2), findViewById(R.id.weeksBtn3), findViewById(R.id.weeksBtn4), findViewById(R.id.weeksBtn5), findViewById(R.id.weeksBtn6)};
        final LinearLayout[] weeksArr = {findViewById(R.id.weeks1), findViewById(R.id.weeks2), findViewById(R.id.weeks3), findViewById(R.id.weeks4), findViewById(R.id.weeks5), findViewById(R.id.weeks6)};

        for (int i = 0; i < weeksBtnArr.length; i++) {
            final int position = i;
            weeksBtnArr[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    weeksArr[position].setVisibility(View.VISIBLE);
                    v.setAlpha(0.5f);
                    for (int i = 0; i < weeksArr.length; i++) {
                        if (i != position) {
                            weeksArr[i].setVisibility(View.GONE);
                            weeksBtnArr[i].setAlpha(1);
                        }
                    }
                }
            });
        }

        for (int i = 0; i < weeksArr.length; i++) {
            final int weekPosition = i;

            for (int j = 0; j < ((LinearLayout) weeksArr[i].getChildAt(0)).getChildCount(); j++) {
                if (((LinearLayout) weeksArr[i].getChildAt(0)).getChildAt(j).getClass().getName().contains("CheckBox")) {
                    CheckBox checkBox = (CheckBox) ((LinearLayout) weeksArr[i].getChildAt(0)).getChildAt(j);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                buttonView.setBackground(getDrawable(R.drawable.ic_check_on));
                            } else {
                                buttonView.setBackground(getDrawable(R.drawable.ic_check_off));
                            }

                            if(weekPosition == 0){
                                setAllWeekCheckedAction(weeksArr, buttonView.getTag().toString(), isChecked);
                            }else {
                                setWeekCheckedAction(weeksArr, buttonView.getTag().toString());
                            }
                        }
                    });
                }
            }
        }

        Button joinComplete = findViewById( R.id.joinComplete );
        joinComplete.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setEnabled( false );
                final LoadDialog loading = new LoadDialog( BaobabCpInfoUpdate.this );
                loading.showDialog();

                cpName = findViewById( R.id.cpName );
                Call<String> dup = retroSingleTon.getInstance().getRetroInterface( getApplicationContext() ).cpNameDup( cpName.getText().toString() );
                dup.enqueue( new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                Log.d( "중복 확인 : ", "통신 완료" );
                                String result = response.body();
                                if (result.equals( "ok" )) {
                                    cpPhone = findViewById( R.id.cpPhone );
                                    cpZipcode = findViewById( R.id.cpZipcode );
                                    cpAddress = findViewById( R.id.cpAddress );
                                    cpAddrDetails = findViewById( R.id.cpAddrDetails );

                                    businessStartHour = findViewById( R.id.businessStartHour );
                                    businessStartMinute = findViewById( R.id.businessStartMinute );
                                    businessEndHour = findViewById( R.id.businessEndHour );
                                    businessEndMinute = findViewById( R.id.businessEndMinute );
                                    String startTime = businessStartHour.getSelectedItem().toString() + ":" + businessStartMinute.getSelectedItem().toString();
                                    String endTime = businessEndHour.getSelectedItem().toString() + ":" + businessEndMinute.getSelectedItem().toString();

                                    kind = findViewById( R.id.kind );
                                    selfKind = findViewById( R.id.selfKind );
                                    type = findViewById( R.id.type );
                                    selfType = findViewById( R.id.selfType );

                                    mon = findViewById( R.id.mon );
                                    tue = findViewById( R.id.tue );
                                    wed = findViewById( R.id.wed );
                                    thu = findViewById( R.id.thu );
                                    fri = findViewById( R.id.fri );
                                    sat = findViewById( R.id.sat );
                                    sun = findViewById( R.id.sun );

                                    theme1_1 = findViewById( R.id.theme1_1 );
                                    theme1_2 = findViewById( R.id.theme1_2 );
                                    theme1_3 = findViewById( R.id.theme1_3 );
                                    theme1_4 = findViewById( R.id.theme1_4 );
                                    theme1_5 = findViewById( R.id.theme1_5 );
                                    theme1_6 = findViewById( R.id.theme1_6 );
                                    theme1_7 = findViewById( R.id.theme1_7 );
                                    theme1_8 = findViewById( R.id.theme1_8 );
                                    theme1_9 = findViewById( R.id.theme1_9 );
                                    theme1_10 = findViewById( R.id.theme1_10 );

                                    parking = findViewById( R.id.parkingNum );

                                    theme2_1 = findViewById( R.id.theme2_1 );
                                    theme2_2 = findViewById( R.id.theme2_2 );
                                    theme2_3 = findViewById( R.id.theme2_3 );
                                    theme2_4 = findViewById( R.id.theme2_4 );
                                    theme2_5 = findViewById( R.id.theme2_5 );
                                    theme2_6 = findViewById( R.id.theme2_6 );
                                    theme2_7 = findViewById( R.id.theme2_7 );
                                    theme2_8 = findViewById( R.id.theme2_8 );
                                    theme2_9 = findViewById( R.id.theme2_9 );
                                    theme2_10 = findViewById( R.id.theme2_10 );

                                    EditText cpIntroEt = findViewById( R.id.cpIntro );
                                    String cpIntroStr = cpIntroEt.getText().toString();

                                    Intent intent = getIntent();

                                    final SharedPreferences spf = getSharedPreferences( "user", 0 );

                                    RequestBody licenseNum = RequestBody.create( MediaType.parse( "multipart/form-data" ), intent.getStringExtra( "licenseNum" ) );
                                    RequestBody userMail = RequestBody.create( MediaType.parse( "multipart/form-data" ), spf.getString( "email", "" ) );

                                    final RequestBody cpNameStr = RequestBody.create( MediaType.parse( "multipart/form-data" ), cpName.getText().toString() );
                                    RequestBody cpPhoneStr = RequestBody.create( MediaType.parse( "multipart/form-data" ), cpPhone.getText().toString() );
                                    RequestBody cpZipcodeStr = RequestBody.create( MediaType.parse( "multipart/form-data" ), cpZipcode.getText().toString() );
                                    RequestBody cpAddressStr = RequestBody.create( MediaType.parse( "multipart/form-data" ), cpAddress.getText().toString() );
                                    final RequestBody cpAddrDetailsStr = RequestBody.create( MediaType.parse( "multipart/form-data" ), cpAddrDetails.getText().toString() );

                                    RequestBody bStart = RequestBody.create( MediaType.parse( "multipart/form-data" ), startTime );
                                    RequestBody bEnd = RequestBody.create( MediaType.parse( "multipart/form-data" ), endTime );
                                    RequestBody cpIntro = RequestBody.create( MediaType.parse( "multipart/form-data" ), cpIntroStr );

                                    RequestBody kindStr;
                                    if (kind.getSelectedItem().toString().equals( "기타(직접입력)" )) {
                                        kindStr = RequestBody.create( MediaType.parse( "multipart/form-data" ), selfKind.getText().toString() );
                                    } else {
                                        kindStr = RequestBody.create( MediaType.parse( "multipart/form-data" ), kind.getSelectedItem().toString() );
                                    }
                                    RequestBody typeStr;
                                    if (type.getSelectedItem().toString().equals( "기타(직접입력)" )) {
                                        typeStr = RequestBody.create( MediaType.parse( "multipart/form-data" ), selfType.getText().toString() );
                                    } else {
                                        typeStr = RequestBody.create( MediaType.parse( "multipart/form-data" ), type.getSelectedItem().toString() );
                                    }

                                    RequestBody parkStr = RequestBody.create( MediaType.parse( "multipart/form-data" ), parking.getText().toString() );

                                    final RequestBody closeDay = RequestBody.create( MediaType.parse( "multipart/form-data" ), closeDayResult(weeksArr) );

                                    List<String> theme1List = new ArrayList<>();
                                    if (theme1_1.isChecked())
                                        theme1List.add( theme1_1.getTag().toString() );
                                    if (theme1_2.isChecked())
                                        theme1List.add( theme1_2.getTag().toString() );
                                    if (theme1_3.isChecked())
                                        theme1List.add( theme1_3.getTag().toString() );
                                    if (theme1_4.isChecked())
                                        theme1List.add( theme1_4.getTag().toString() );
                                    if (theme1_5.isChecked())
                                        theme1List.add( theme1_5.getTag().toString() );
                                    if (theme1_6.isChecked())
                                        theme1List.add( theme1_6.getTag().toString() );
                                    if (theme1_7.isChecked())
                                        theme1List.add( theme1_7.getTag().toString() );
                                    if (theme1_8.isChecked())
                                        theme1List.add( theme1_8.getTag().toString() );
                                    if (theme1_9.isChecked())
                                        theme1List.add( theme1_9.getTag().toString() );
                                    if (theme1_10.getText().toString().length() != 0)
                                        theme1List.add( theme1_10.getText().toString() );
                                    RequestBody theme1 = RequestBody.create( MediaType.parse( "multipart/form-data" ), themeSort(theme1List.toString()) );

                                    List<String> theme2List = new ArrayList<>();
                                    if (theme2_1.isChecked())
                                        theme2List.add( theme2_1.getTag().toString() );
                                    if (theme2_2.isChecked())
                                        theme2List.add( theme2_2.getTag().toString() );
                                    if (theme2_3.isChecked())
                                        theme2List.add( theme2_3.getTag().toString() );
                                    if (theme2_4.isChecked())
                                        theme2List.add( theme2_4.getTag().toString() );
                                    if (theme2_5.isChecked())
                                        theme2List.add( theme2_5.getTag().toString() );
                                    if (theme2_6.isChecked())
                                        theme2List.add( theme2_6.getTag().toString() );
                                    if (theme2_7.isChecked())
                                        theme2List.add( theme2_7.getTag().toString() );
                                    if (theme2_8.isChecked())
                                        theme2List.add( theme2_8.getTag().toString() );
                                    if (theme2_9.isChecked())
                                        theme2List.add( theme2_9.getTag().toString() );
                                    if (theme2_10.getText().toString().length() != 0)
                                        theme2List.add( theme2_10.getText().toString() );

                                    RequestBody theme2 = RequestBody.create( MediaType.parse( "multipart/form-data" ), themeSort(theme2List.toString()) );

                                    /* 비트맵 변환 */
                                    Uri licenseUri = intent.getParcelableExtra( "license" );
                                    try {
                                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                        MultipartBody.Part body = null;

                                        if(licenseUri != null){
                                            licenseBmp = MediaStore.Images.Media.getBitmap( getContentResolver(), licenseUri );
                                            licenseBmp.compress( Bitmap.CompressFormat.PNG, 10, bos );
                                            byte[] license = bos.toByteArray();
                                            File f = new File( getApplicationContext().getCacheDir(), licenseUri.toString().substring( licenseUri.toString().lastIndexOf( "/" ) ) );
                                            FileOutputStream fos = new FileOutputStream( f );
                                            fos.write( license );
                                            fos.flush();
                                            fos.close();
                                            RequestBody reqFile = RequestBody.create( MediaType.parse( "image/*" ), f );
                                            body = MultipartBody.Part.createFormData( "upload", f.getName(), reqFile );
                                        }

                                        RequestBody bank = RequestBody.create( MediaType.parse( "multipart/form-data" ), intent.getStringExtra( "bank" ) );
                                        RequestBody accountHolder = RequestBody.create( MediaType.parse( "multipart/form-data" ), intent.getStringExtra( "accountHolder" ) );
                                        RequestBody accountNumber = RequestBody.create( MediaType.parse( "multipart/form-data" ), intent.getStringExtra( "accountNumber" ) );
                                        RequestBody pushCheck = RequestBody.create( MediaType.parse( "multipart/form-data" ), spf.getString( "pushAgree", "" ) );
                                        RequestBody cpKind = RequestBody.create( MediaType.parse( "multipart/form-data" ), getIntent().getStringExtra("cpKind"));
                                        RequestBody closeEct = RequestBody.create(MediaType.parse("multipart/form-data"), ((EditText)findViewById(R.id.closeEct)).getText().toString());

                                        if (cpName.getText().toString().length() == 0) {
                                            Toast.makeText( BaobabCpInfoUpdate.this, "상호명을 입력해주세요.", Toast.LENGTH_SHORT ).show();
                                            cpName.requestFocus();
                                            v.setEnabled( true );
                                            loading.dialogCancel();
                                            return;
                                        } else if (cpPhone.getText().toString().length() == 0) {
                                            Toast.makeText( BaobabCpInfoUpdate.this, "가계전화번호를 입력해주세요.", Toast.LENGTH_SHORT ).show();
                                            cpPhone.requestFocus();
                                            v.setEnabled( true );
                                            loading.dialogCancel();
                                            return;
                                        } else if (cpAddress.getText().toString().length() == 0) {
                                            Toast.makeText( BaobabCpInfoUpdate.this, "주소찾기 버튼을 눌러주세요.", Toast.LENGTH_SHORT ).show();
                                            cpAddress.requestFocus();
                                            v.setEnabled( true );
                                            loading.dialogCancel();
                                            return;
                                        } else {
                                            Call<ResponseBody> join = retroSingleTon.getInstance().getCpJoinInterface( getApplicationContext() ).beingMall( userMail, cpNameStr, licenseNum, cpPhoneStr, cpZipcodeStr, cpAddressStr, cpAddrDetailsStr, kindStr, typeStr, theme1, theme2, parkStr,
                                                    closeDay, bStart, bEnd, body, cpIntro, bank, accountHolder, accountNumber, pushCheck, cpKind, closeEct);
                                            join.enqueue( new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    Log.d( "통신 완료", "완료" );
                                                    Log.d( "데이터 :: ", String.valueOf( cpNameStr ) );
                                                    loading.dialogCancel();
                                                    SharedPreferences.Editor editor = spf.edit();
                                                    editor.putString( "divCode", "c-01-01" );
                                                    editor.apply();

                                                    Toast.makeText( getApplicationContext(), "입점등록이 완료되었습니다.", Toast.LENGTH_SHORT ).show();

                                                    Intent putCPname = new Intent( BaobabCpInfoUpdate.this, BaobabAnterMain.class );
                                                    cpName = findViewById( R.id.cpName );
                                                    putCPname.putExtra( "joinCpName", cpName.getText().toString() );
                                                    startActivity( putCPname );
                                                    finish();
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    loading.dialogCancel();
                                                    v.setEnabled( true );
                                                    Log.d( "통신 실패", "실패3 : 통신 에러" + t.getLocalizedMessage() );
                                                    Toast.makeText( getApplicationContext(), "에러입니다. 다시 시도해주세요.", Toast.LENGTH_SHORT ).show();
                                                }
                                            } );
                                        }
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                        loading.dialogCancel();
                                        v.setEnabled( true );
                                        Toast.makeText( getApplicationContext(), "이미지 파일이 존재하지 않습니다.", Toast.LENGTH_SHORT ).show();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        loading.dialogCancel();
                                        v.setEnabled( true );
                                        Toast.makeText( getApplicationContext(), "사진을 가져오는중 에러발생", Toast.LENGTH_SHORT ).show();
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                        loading.dialogCancel();
                                        v.setEnabled( true );
                                        Toast.makeText( getApplicationContext(), "필수사항은 모두 입력해주세요.", Toast.LENGTH_SHORT ).show();
                                    }
                                } else {
                                    loading.dialogCancel();
                                    v.setEnabled( true );
                                    Toast.makeText( getApplicationContext(), "이미 등록된 업체명입니다.", Toast.LENGTH_SHORT ).show();
                                }
                            } else {
                                Log.d( "중복 확인 : ", "response 내용없음" );
                                loading.dialogCancel();
                                v.setEnabled( true );
                                Toast.makeText( getApplicationContext(), "에러입니다. 다시 시도해주세요.", Toast.LENGTH_SHORT ).show();
                            }
                        } else {
                            Log.d( "중복 확인 : ", "서버 에러" );
                            loading.dialogCancel();
                            v.setEnabled( true );
                            Toast.makeText( getApplicationContext(), "에러입니다. 다시 시도해주세요.", Toast.LENGTH_SHORT ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d( "중복 확인 : ", "통신 에러 : " + t.getLocalizedMessage() );
                        loading.dialogCancel();
                        Toast.makeText( getApplicationContext(), "에러입니다. 다시 시도해주세요.", Toast.LENGTH_SHORT ).show();
                    }
                } );
            }

        } );

        cpZipcode = findViewById( R.id.cpZipcode );
        cpAddress = findViewById( R.id.cpAddress );
        cpAddrDetails = findViewById( R.id.cpAddrDetails );
    }

    private String themeSort(String theme){
        if(theme.endsWith(",  ]")){
            return theme.replaceAll(",  ]", "]");
        }else {
            return theme;
        }
    }

    public void setAllWeekCheckedAction(LinearLayout[] weekArr, String chooseDay, boolean isChecked){
        for(int i=0;i<weekArr.length;i++){
            for(int j=0;j<((LinearLayout)weekArr[i].getChildAt(0)).getChildCount();j++){
                if(((LinearLayout)weekArr[i].getChildAt(0)).getChildAt(j).getTag() != null && ((LinearLayout)weekArr[i].getChildAt(0)).getChildAt(j).getTag().toString().equals(chooseDay)){
                    CheckBox checkBox = (CheckBox) ((LinearLayout)weekArr[i].getChildAt(0)).getChildAt(j);
                    if(isChecked){
                        checkBox.setChecked(true);
                    }else {
                        checkBox.setChecked(false);
                    }
                }
            }
        }
    }

    public void setWeekCheckedAction(LinearLayout[] weekArr, String chooseDay){
        CheckBox[] chooseArr = new CheckBox[5];

        for(int i=1;i<weekArr.length;i++){
            LinearLayout linearLayout = ((LinearLayout)weekArr[i].getChildAt(0));
            for(int j=0;j<linearLayout.getChildCount();j++){
                if(linearLayout.getChildAt(j).getTag() != null && linearLayout.getChildAt(j).getTag().toString().equals(chooseDay)){
                    chooseArr[i-1] = (CheckBox) linearLayout.getChildAt(j);
                }
            }
        }

        boolean allCheck = false;
        for(int i=0;i<chooseArr.length;i++){
            if(chooseArr[i].isChecked()){
                allCheck = true;
            }else {
                allCheck = false;
                break;
            }
        }

        if(allCheck){
            if(!choiceAllCheckBox(chooseDay).isChecked()){
                choiceAllCheckBox(chooseDay).setChecked(true);
            }
        }else {
            if(choiceAllCheckBox(chooseDay).isChecked()){
                choiceAllCheckBox(chooseDay).setChecked(false);
            }
        }
    }

    private CheckBox choiceAllCheckBox(String chooseDay){
        if(chooseDay.equals("월")){
            return findViewById(R.id.mon);
        }else if(chooseDay.equals("화")){
            return findViewById(R.id.tue);
        }else if(chooseDay.equals("일")){
            return findViewById(R.id.sun);
        }else if(chooseDay.equals("수")){
            return findViewById(R.id.wed);
        }else if(chooseDay.equals("목")){
            return findViewById(R.id.thu);
        }else if(chooseDay.equals("금")){
            return findViewById(R.id.fri);
        }else {
            return findViewById(R.id.sat);
        }
    }

    public void themeSetting(){
        CheckBox[] themes = new CheckBox[]{
                findViewById(R.id.theme1_1),
                findViewById(R.id.theme1_2),
                findViewById(R.id.theme1_3),
                findViewById(R.id.theme1_4),
                findViewById(R.id.theme1_5),
                findViewById(R.id.theme1_6),
                findViewById(R.id.theme1_7),
                findViewById(R.id.theme1_8),
                findViewById(R.id.theme1_9),
                findViewById(R.id.theme2_1),
                findViewById(R.id.theme2_2),
                findViewById(R.id.theme2_3),
                findViewById(R.id.theme2_4),
                findViewById(R.id.theme2_5),
                findViewById(R.id.theme2_6),
                findViewById(R.id.theme2_7),
                findViewById(R.id.theme2_8),
                findViewById(R.id.theme2_9)
        };

        for(int i=0;i<themes.length;i++){
            themes[i].setOnCheckedChangeListener(themeListener);
        }
    }

    CompoundButton.OnCheckedChangeListener themeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                buttonView.setBackground(getDrawable(R.drawable.ic_check_on));
            }else {
                buttonView.setBackground(getDrawable(R.drawable.ic_check_off));
            }
        }
    };

    public String closeDayResult(LinearLayout[] weeksArr) {
        String result = "";
        for (int i = 0; i < weeksArr.length; i++) {
            for (int j = 0; j < ((LinearLayout) weeksArr[i].getChildAt(0)).getChildCount(); j++) {
                if(((LinearLayout) weeksArr[i].getChildAt(0)).getChildAt(j).getClass().getName().contains("CheckBox")){
                    if(((CheckBox) ((LinearLayout) weeksArr[i].getChildAt(0)).getChildAt(j)).isChecked()){
                        result += ((LinearLayout) weeksArr[i].getChildAt(0)).getChildAt(j).getTag().toString() + ",";
                    }
                }
            }
            if(result.length() > 0){
                result = result.substring(0, result.length() - 1);
            }
            result += "~";
        }
        return result.substring(0, result.length() - 1);
    }

    private void populateSpinners() {
        ArrayAdapter<CharSequence> fAdapter;
        fAdapter = ArrayAdapter.createFromResource( this, R.array.storeType, android.R.layout.simple_spinner_dropdown_item);
        fAdapter.setDropDownViewResource( R.layout.spinner_cescum_layout );
        kind.setAdapter( fAdapter );
    }

    private void populteSubSpinners(int itemNum) {
        ArrayAdapter<CharSequence> fAdapter;
        fAdapter = ArrayAdapter.createFromResource( this, itemNum, android.R.layout.simple_spinner_dropdown_item);
        fAdapter.setDropDownViewResource( R.layout.spinner_cescum_layout );
        type.setAdapter( fAdapter );
    }

    private AdapterView.OnItemSelectedListener spinSelectedlistener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case (0):
                    populteSubSpinners( R.array.type1 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (1):
                    populteSubSpinners( R.array.type2 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (2):
                    populteSubSpinners( R.array.type3 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (3):
                    populteSubSpinners( R.array.type4 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (4):
                    populteSubSpinners( R.array.type5 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (5):
                    populteSubSpinners( R.array.type6 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (6):
                    populteSubSpinners( R.array.type7 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (7):
                    populteSubSpinners( R.array.type8 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (8):
                    populteSubSpinners( R.array.type9 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (9):
                    populteSubSpinners( R.array.type10 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (10):
                    populteSubSpinners( R.array.type11 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (11):
                    populteSubSpinners( R.array.type12 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (12):
                    populteSubSpinners( R.array.type13 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (13):
                    populteSubSpinners( R.array.type14 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (14):
                    populteSubSpinners( R.array.type15 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (15):
                    populteSubSpinners( R.array.type16 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (16):
                    populteSubSpinners( R.array.type17 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (17):
                    populteSubSpinners( R.array.type18 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (18):
                    populteSubSpinners( R.array.type19 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (19):
                    populteSubSpinners( R.array.type20 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (20):
                    populteSubSpinners( R.array.type21 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (21):
                    populteSubSpinners( R.array.type22 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (22):
                    populteSubSpinners( R.array.type23 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (23):
                    populteSubSpinners( R.array.type24 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (24):
                    populteSubSpinners( R.array.type25 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (25):
                    populteSubSpinners( R.array.type26 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (26):
                    populteSubSpinners( R.array.type27 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (27):
                    populteSubSpinners( R.array.type28 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (28):
                    populteSubSpinners( R.array.type29 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (29):
                    populteSubSpinners( R.array.type30 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (30):
                    populteSubSpinners( R.array.type31 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (31):
                    populteSubSpinners( R.array.type32 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.GONE );
                    break;
                case (32):
                    populteSubSpinners( R.array.type33 );
                    findViewById( R.id.selfKindLayout ).setVisibility( View.VISIBLE );
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener spinSelectedlistener2 = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (parent.getSelectedItem().toString().equals( "기타(직접입력)" )) {
                findViewById( R.id.selfTypeLayout ).setVisibility( View.VISIBLE );
            } else {
                findViewById( R.id.selfTypeLayout ).setVisibility( View.GONE );
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Intent intent = new Intent( BaobabCpInfoUpdate.this, BaobabBeingMall.class );
                intent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
                startActivity( intent );
                finish();
        }
        return false;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult( requestCode, resultCode, data );

        switch (resultCode) {

            case 1:

                String zipcode = data.getStringExtra( "zipcode" );
                String addr1 = data.getStringExtra( "address1" );
                String addr2 = data.getStringExtra( "address2" );
                if (zipcode != null || zipcode != "") {
                    cpZipcode.setText( zipcode );
                }
                if (addr1 != null || addr1 != "") {
                    cpAddress.setText( addr1 );
                }
                if (addr2 != null || addr2 != "") {
                    cpAddrDetails.setText( addr2 );
                }
                break;
            default:
                break;
        }

    }
}