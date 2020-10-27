package com.baobab.user.baobabflyer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import com.baobab.user.baobabflyer.server.vo.CPInfoVO;
import com.baobab.user.baobabflyer.server.util.LoadDialog;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaobabCpJoin extends AppCompatActivity {
    RetroSingleTon retroSingleTon;

    Spinner kind;
    Spinner type;
    int typeSpinnerID;
    LinearLayout selfKindLayout;
    LinearLayout selfTypeLayout;

    int num;

    Spinner businessStartHour;
    Spinner businessStartMinute;
    Spinner businessEndHour;
    Spinner businessEndMinute;

    String cpKind;
    int seq;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_cp_info);

        kind = (Spinner) findViewById(R.id.kind);
        populateSpinners();

        type = (Spinner) findViewById(R.id.type);
        populteSubSpinners(R.array.type1);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        kind.setOnItemSelectedListener(spinSelectedlistener);
        type.setOnItemSelectedListener(spinSelectedlistener2);
        selfKindLayout = findViewById(R.id.selfKindLayout);
        selfTypeLayout = findViewById(R.id.selfTypeLayout);

        businessStartHour = (Spinner) findViewById(R.id.businessStartHour);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.hour, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.spinner_cescum_layout);
        businessStartHour.setAdapter(adapter);

        businessStartMinute = (Spinner) findViewById(R.id.businessStartMinute);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.minute, android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(R.layout.spinner_cescum_layout);
        businessStartMinute.setAdapter(adapter2);

        businessEndHour = (Spinner) findViewById(R.id.businessEndHour);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.hour, android.R.layout.simple_spinner_dropdown_item);
        adapter3.setDropDownViewResource(R.layout.spinner_cescum_layout);
        businessEndHour.setAdapter(adapter3);

        businessEndMinute = (Spinner) findViewById(R.id.businessEndMinute);
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this, R.array.minute, android.R.layout.simple_spinner_dropdown_item);
        adapter4.setDropDownViewResource(R.layout.spinner_cescum_layout);
        businessEndMinute.setAdapter(adapter4);

        Button addressSearch = (Button) findViewById(R.id.addressSearth);
        addressSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent as = new Intent(BaobabCpJoin.this, BaobabAddressSearch.class);
                as.putExtra("classKind", "update");
                startActivityForResult(as, 0);

            }
        });

        themeSetting();

        final Button[] weeksBtnArr = {findViewById(R.id.weeksBtn1), findViewById(R.id.weeksBtn2), findViewById(R.id.weeksBtn3), findViewById(R.id.weeksBtn4),
                findViewById(R.id.weeksBtn5), findViewById(R.id.weeksBtn6)};
        final LinearLayout[] weeksArr = {findViewById(R.id.weeks1), findViewById(R.id.weeks2), findViewById(R.id.weeks3), findViewById(R.id.weeks4), findViewById(R.id.weeks5),
                findViewById(R.id.weeks6)};


        Call<CPInfoVO> call = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).getUpdateData(getIntent().getIntExtra("cpSeq", 0));
        call.enqueue(new Callback<CPInfoVO>() {
            @Override
            public void onResponse(Call<CPInfoVO> call, Response<CPInfoVO> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("통신 완료(정보 호출)", "완료");

                        final CPInfoVO vo = response.body();

                        cpKind = vo.getCp_div();
                        seq = vo.getSeq_num();

                        if (!cpKind.equals("음식점")) {
                            LinearLayout restaurant = findViewById(R.id.restaurant);
                            restaurant.setVisibility(View.GONE);
                        }
                        num = vo.getSeq_num();

                        EditText cpName = findViewById(R.id.cpName);
                        cpName.setText(vo.getCP_name());
                        EditText cpPhone = findViewById(R.id.cpPhone);
                        cpPhone.setText(vo.getCP_phon());
                        TextView zipcode = findViewById(R.id.cpZipcode);
                        zipcode.setText(vo.getCP_zipcode());
                        TextView addr = findViewById(R.id.cpAddress);
                        addr.setText(vo.getCP_address());
                        EditText addrDetail = findViewById(R.id.cpAddrDetails);
                        addrDetail.setText(vo.getCP_addr_details());
                        EditText closeEct = findViewById(R.id.closeEct);
                        closeEct.setText(vo.getClose_ect());

                        businessStartHour.setSelection(getPosition(getResources().getStringArray(R.array.hour), vo.getBusiness_start().split(":")[0]));
                        businessStartMinute.setSelection(getPosition(getResources().getStringArray(R.array.minute), vo.getBusiness_start().split(":")[1]));
                        businessEndHour.setSelection(getPosition(getResources().getStringArray(R.array.hour), vo.getBusiness_end().split(":")[0]));
                        businessStartMinute.setSelection(getPosition(getResources().getStringArray(R.array.minute), vo.getBusiness_end().split(":")[1]));

                        kind.setSelection(getPosition(getResources().getStringArray(R.array.storeType), vo.getCP_kind()));

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                type.setSelection(getPosition(getResources().getStringArray(typeSpinnerID), vo.getCP_type()));

                                for (int i = 0; i < getResources().getStringArray(typeSpinnerID).length; i++) {
                                    Log.d("타입스피너 어레이", getResources().getStringArray(typeSpinnerID)[i]);
                                }

                                if (getPosition(getResources().getStringArray(typeSpinnerID), vo.getCP_type()) == getResources().getStringArray(typeSpinnerID).length - 1) {
                                    ((EditText) findViewById(R.id.selfType)).setText(vo.getCP_type());
                                }
                            }
                        }, 500);

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

                        checking("요일", vo.getClose_day());
                        checking("테마1", vo.getCP_Theme1());
                        checking("테마2", vo.getCP_Theme2());

                        EditText parkingNum = findViewById(R.id.parkingNum);
                        parkingNum.setText(vo.getParking());

                        EditText cpIntro = findViewById(R.id.cpIntro);
                        cpIntro.setText(vo.getCP_intro());
                    } else {
                        Log.d("통신 실패(정보 호출)", "실패1 : response내용없음");
                    }
                } else {
                    Log.d("통신 실패(정보 호출)", "실패2 : 서버 에러");
                }
            }

            @Override
            public void onFailure(Call<CPInfoVO> call, Throwable t) {
                Log.d("통신 실패(정보 호출)", "실패3 : 통신 에러" + t.getLocalizedMessage());
            }
        });

        Button updateBtn = findViewById(R.id.joinComplete);
        updateBtn.setOnClickListener(update);
        updateBtn.setText("수정하기");
    }

    public void themeSetting() {
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

        for (int i = 0; i < themes.length; i++) {
            themes[i].setOnCheckedChangeListener(themeListener);
        }
    }

    private void checkAndTextDiv(LinearLayout layout) {
        List<CheckBox> checkBox = new ArrayList<>();
        List<TextView> textView = new ArrayList<>();

        for (int i = 0; i < layout.getChildCount(); i++) {
            if (i % 2 == 0) {
                checkBox.add((CheckBox) layout.getChildAt(i));
            } else {
                textView.add((TextView) layout.getChildAt(i));
            }
        }

        for (int i = 0; i < checkBox.size(); i++) {
            checkBoxTextSetting(checkBox.get(i), textView.get(i));
        }
    }

    private void checkBoxTextSetting(final CheckBox checkBox, TextView textView) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.performClick();
            }
        });
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

    CompoundButton.OnCheckedChangeListener themeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                buttonView.setBackground(getDrawable(R.drawable.ic_check_on));
            } else {
                buttonView.setBackground(getDrawable(R.drawable.ic_check_off));
            }
        }
    };

    public String closeDayResult(LinearLayout[] weeksArr) {
        String result = "";
        for (int i = 0; i < weeksArr.length; i++) {
            for (int j = 0; j < ((LinearLayout) weeksArr[i].getChildAt(0)).getChildCount(); j++) {
                if (((LinearLayout) weeksArr[i].getChildAt(0)).getChildAt(j).getClass().getName().contains("CheckBox")) {
                    if (((CheckBox) ((LinearLayout) weeksArr[i].getChildAt(0)).getChildAt(j)).isChecked()) {
                        result += ((LinearLayout) weeksArr[i].getChildAt(0)).getChildAt(j).getTag().toString() + ",";
                    }
                }
            }
            if (result.length() > 0) {
                result = result.substring(0, result.length() - 1);
            }
            result += "~";
        }
        return result.substring(0, result.length() - 1);
    }

    public int getPosition(String[] array, String selectedItem) {
        int position = 0;
        for (int i = 0; i < array.length; i++) {
            if (selectedItem.equals(array[i])) {
                position = i;
                break;
            } else if (!selectedItem.equals(array[i]) && i == array.length - 1) {
                position = array.length - 1;
            }
        }
        Log.d("타입아이템", selectedItem);
        Log.d("타입어레이", array[0]);
        Log.d("타입포지션", String.valueOf(position));
        return position;
    }

    public void checking(String div, String value) {
        List<CheckBox> checkList = new ArrayList<>();
        if (div.equals("요일")) {
            dayChecking(value);
        } else if (div.equals("테마1")) {
            value = value.replace("[", "");
            value = value.replace("]", "");
            String[] valueArr = value.split(", ");
            checkList.add((CheckBox) findViewById(R.id.theme1_1));
            checkList.add((CheckBox) findViewById(R.id.theme1_2));
            checkList.add((CheckBox) findViewById(R.id.theme1_3));
            checkList.add((CheckBox) findViewById(R.id.theme1_4));
            checkList.add((CheckBox) findViewById(R.id.theme1_5));
            checkList.add((CheckBox) findViewById(R.id.theme1_6));
            checkList.add((CheckBox) findViewById(R.id.theme1_7));
            checkList.add((CheckBox) findViewById(R.id.theme1_8));
            checkList.add((CheckBox) findViewById(R.id.theme1_9));
            for (int i = 0; i < checkList.size(); i++) {
                checkAndTextDiv((LinearLayout) checkList.get(i).getParent());
                for (int j = 0; j < valueArr.length; j++) {
                    if (checkList.get(i).getTag().toString().equals(valueArr[j])) {
                        checkList.get(i).setChecked(true);
                        List<String> valueList = new ArrayList<>(Arrays.asList(valueArr));
                        valueList.remove(valueArr[j]);
                        valueArr = valueList.toArray(new String[valueList.size()]);
                    }
                }
            }
            if (valueArr.length > 0) {
                ((EditText) findViewById(R.id.theme1_10)).setText(valueArr[0]);
            }
        } else if (div.equals("테마2")) {
            value = value.replace("[", "");
            value = value.replace("]", "");
            String[] valueArr = value.split(", ");
            Log.d("테마 2 결과 : ", new ArrayList<>(Arrays.asList(valueArr)).toString());
            checkList.add((CheckBox) findViewById(R.id.theme2_1));
            checkList.add((CheckBox) findViewById(R.id.theme2_2));
            checkList.add((CheckBox) findViewById(R.id.theme2_3));
            checkList.add((CheckBox) findViewById(R.id.theme2_4));
            checkList.add((CheckBox) findViewById(R.id.theme2_5));
            checkList.add((CheckBox) findViewById(R.id.theme2_6));
            checkList.add((CheckBox) findViewById(R.id.theme2_7));
            checkList.add((CheckBox) findViewById(R.id.theme2_8));
            checkList.add((CheckBox) findViewById(R.id.theme2_9));
            for (int i = 0; i < checkList.size(); i++) {
                for (int j = 0; j < valueArr.length; j++) {
                    if (checkList.get(i).getTag().toString().equals(valueArr[j])) {
                        checkList.get(i).setChecked(true);
                        List<String> valueList = new ArrayList<>(Arrays.asList(valueArr));
                        valueList.remove(valueArr[j]);
                        valueArr = valueList.toArray(new String[valueList.size()]);
                    }
                }
            }
            if (valueArr.length > 0) {
                ((EditText) findViewById(R.id.theme2_10)).setText(valueArr[0]);
            }
        }
    }

    public void dayChecking(String value) {
        final LinearLayout[] weekLayouts = new LinearLayout[]{
                findViewById(R.id.weeks1),
                findViewById(R.id.weeks2),
                findViewById(R.id.weeks3),
                findViewById(R.id.weeks4),
                findViewById(R.id.weeks5),
                findViewById(R.id.weeks6)
        };

        for (int i = 0; i < weekLayouts.length; i++) {
            final int weekPosition = i;
            String thisWeekValue = value.split("~")[i];
            for (int j = 0; j < ((LinearLayout) weekLayouts[i].getChildAt(0)).getChildCount(); j++) {
                checkAndTextDiv((LinearLayout) weekLayouts[i].getChildAt(0));
                if (((LinearLayout) weekLayouts[i].getChildAt(0)).getChildAt(j).getClass().getName().contains("CheckBox")) {
                    CheckBox checkBox = (CheckBox) ((LinearLayout) weekLayouts[i].getChildAt(0)).getChildAt(j);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                buttonView.setBackground(getDrawable(R.drawable.ic_check_on));
                            } else {
                                buttonView.setBackground(getDrawable(R.drawable.ic_check_off));
                            }

                            if(weekPosition == 0){
                                setAllWeekCheckedAction(weekLayouts, buttonView.getTag().toString(), isChecked);
                            }else {
                                setWeekCheckedAction(weekLayouts, buttonView.getTag().toString());
                            }
                        }
                    });

                    if (thisWeekValue.contains(checkBox.getTag().toString())) {
                        checkBox.setChecked(true);
                    }
                }
            }
        }
    }

    View.OnClickListener update = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            v.setEnabled(false);
            final LoadDialog loading = new LoadDialog(BaobabCpJoin.this);
            loading.showDialog();

            final EditText cpName = findViewById(R.id.cpName);
            EditText cpPhone = findViewById(R.id.cpPhone);
            TextView zipcode = findViewById(R.id.cpZipcode);
            TextView addr = findViewById(R.id.cpAddress);
            EditText addrDetail = findViewById(R.id.cpAddrDetails);

            LinearLayout[] weeksArr = {findViewById(R.id.weeks1), findViewById(R.id.weeks2), findViewById(R.id.weeks3), findViewById(R.id.weeks4), findViewById(R.id.weeks5),
                    findViewById(R.id.weeks6)};

            Spinner kind = findViewById(R.id.kind);
            Spinner type = findViewById(R.id.type);
            EditText selfKind = findViewById(R.id.selfKind);
            EditText selfType = findViewById(R.id.selfType);

            CheckBox theme1_1 = findViewById(R.id.theme1_1);
            CheckBox theme1_2 = findViewById(R.id.theme1_2);
            CheckBox theme1_3 = findViewById(R.id.theme1_3);
            CheckBox theme1_4 = findViewById(R.id.theme1_4);
            CheckBox theme1_5 = findViewById(R.id.theme1_5);
            CheckBox theme1_6 = findViewById(R.id.theme1_6);
            CheckBox theme1_7 = findViewById(R.id.theme1_7);
            CheckBox theme1_8 = findViewById(R.id.theme1_8);
            CheckBox theme1_9 = findViewById(R.id.theme1_9);
            EditText theme1_10 = findViewById(R.id.theme1_10);

            EditText parkingNum = findViewById(R.id.parkingNum);

            CheckBox theme2_1 = findViewById(R.id.theme2_1);
            CheckBox theme2_2 = findViewById(R.id.theme2_2);
            CheckBox theme2_3 = findViewById(R.id.theme2_3);
            CheckBox theme2_4 = findViewById(R.id.theme2_4);
            CheckBox theme2_5 = findViewById(R.id.theme2_5);
            CheckBox theme2_6 = findViewById(R.id.theme2_6);
            CheckBox theme2_7 = findViewById(R.id.theme2_7);
            CheckBox theme2_8 = findViewById(R.id.theme2_8);
            CheckBox theme2_9 = findViewById(R.id.theme2_9);
            EditText theme2_10 = findViewById(R.id.theme2_10);

            EditText cpIntro = findViewById(R.id.cpIntro);

            businessStartHour = (Spinner) findViewById(R.id.businessStartHour);
            businessStartMinute = (Spinner) findViewById(R.id.businessStartMinute);
            businessEndHour = (Spinner) findViewById(R.id.businessEndHour);
            businessEndMinute = (Spinner) findViewById(R.id.businessEndMinute);
            String startTime = businessStartHour.getSelectedItem().toString() + ":" + businessStartMinute.getSelectedItem().toString();
            String endTime = businessEndHour.getSelectedItem().toString() + ":" + businessEndMinute.getSelectedItem().toString();

            List<String> theme1List = new ArrayList<>();
            if (theme1_1.isChecked())
                theme1List.add(theme1_1.getTag().toString());
            if (theme1_2.isChecked())
                theme1List.add(theme1_2.getTag().toString());
            if (theme1_3.isChecked())
                theme1List.add(theme1_3.getTag().toString());
            if (theme1_4.isChecked())
                theme1List.add(theme1_4.getTag().toString());
            if (theme1_5.isChecked())
                theme1List.add(theme1_5.getTag().toString());
            if (theme1_6.isChecked())
                theme1List.add(theme1_6.getTag().toString());
            if (theme1_7.isChecked())
                theme1List.add(theme1_7.getTag().toString());
            if (theme1_8.isChecked())
                theme1List.add(theme1_8.getTag().toString());
            if (theme1_9.isChecked())
                theme1List.add(theme1_9.getTag().toString());
            if (theme1_10.getText().toString().length() != 0)
                theme1List.add(theme1_10.getText().toString());

            List<String> theme2List = new ArrayList<>();
            if (theme2_1.isChecked())
                theme2List.add(theme2_1.getTag().toString());
            if (theme2_2.isChecked())
                theme2List.add(theme2_2.getTag().toString());
            if (theme2_3.isChecked())
                theme2List.add(theme2_3.getTag().toString());
            if (theme2_4.isChecked())
                theme2List.add(theme2_4.getTag().toString());
            if (theme2_5.isChecked())
                theme2List.add(theme2_5.getTag().toString());
            if (theme2_6.isChecked())
                theme2List.add(theme2_6.getTag().toString());
            if (theme2_7.isChecked())
                theme2List.add(theme2_7.getTag().toString());
            if (theme2_8.isChecked())
                theme2List.add(theme2_8.getTag().toString());
            if (theme2_9.isChecked())
                theme2List.add(theme2_9.getTag().toString());
            if (theme2_10.getText().toString().length() != 0)
                theme2List.add(theme2_10.getText().toString());

            final String cpNameStr = String.valueOf(cpName.getText());
            final String cpPhoneStr = String.valueOf(cpPhone.getText());
            final String zipcodeStr = String.valueOf(zipcode.getText());
            final String addrStr = String.valueOf(addr.getText());
            final String addrDetailStr = String.valueOf(addrDetail.getText());
            final String busyStartStr = startTime;
            final String busyEndStr = endTime;

            final String parkingNumStr = String.valueOf(parkingNum.getText());
            final String cpIntroStr = String.valueOf(cpIntro.getText());

            final String closeDay = closeDayResult(weeksArr);
            final String closeEct = ((EditText) findViewById(R.id.closeEct)).getText().toString();
            final String theme1 = themeSort(theme1List.toString());
            final String theme2 = themeSort(theme2List.toString());
            final String kindStr;
            if (kind.getSelectedItem().toString().equals("기타(직접입력)")) {
                kindStr = selfKind.getText().toString();
            } else {
                kindStr = kind.getSelectedItem().toString();
            }
            final String typeStr;
            if (type.getSelectedItem().toString().equals("기타(직접입력)")) {
                typeStr = selfType.getText().toString();
            } else {
                typeStr = type.getSelectedItem().toString();
            }

            Log.d("수정값들 :: ", cpNameStr + " " + cpPhoneStr + " " + zipcodeStr + " " + addrStr + " " + addrDetailStr + " " + busyStartStr + " " + busyEndStr + " " + parkingNumStr + " " + cpIntroStr + " " + closeDay + " " + theme1 +
                    " " + theme2 + " " + kindStr + " " + typeStr + " " + num);

            SharedPreferences spf = getSharedPreferences("user", 0);

            Call<ResponseBody> update = retroSingleTon.getInstance().getRetroInterface(getApplicationContext()).updateCPInfo(cpNameStr, cpPhoneStr, zipcodeStr, addrStr, addrDetailStr, busyStartStr, busyEndStr, parkingNumStr, cpIntroStr,
                    closeDay, theme1, theme2, kindStr, typeStr, num, spf.getString("email", ""), closeEct);
            update.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("통신 완료", "완료");
                    loading.dialogCancel();
                    Toast.makeText(getApplicationContext(), "수정완료", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BaobabCpJoin.this, BaobabMenuRevise.class);
                    intent.putExtra("activity", getIntent().getStringExtra("activity"));
                    CPInfoVO vo = new CPInfoVO();
                    vo.setCP_name(cpNameStr);
                    vo.setSeq_num(getIntent().getIntExtra("cpSeq", 0));
                    vo.setCp_div("음식점");
                    vo.setCP_phon(cpPhoneStr);
                    vo.setCP_zipcode(zipcodeStr);
                    vo.setCP_address(addrStr);
                    vo.setCP_addr_details(addrDetailStr);
                    vo.setBusiness_start(busyStartStr);
                    vo.setBusiness_end(busyEndStr);
                    vo.setParking(parkingNumStr);
                    vo.setCP_intro(cpIntroStr);
                    vo.setClose_day(closeDay);
                    vo.setCP_Theme1(theme1);
                    vo.setCP_Theme2(theme2);
                    vo.setCP_kind(kindStr);
                    vo.setCP_type(typeStr);
                    vo.setClose_ect(closeEct);
                    intent.putExtra("vo", vo);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    loading.dialogCancel();
                    v.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "에러입니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    Log.d("통신 실패", "실패3 : 통신 에러" + t.getLocalizedMessage());
                }
            });
        }
    };

    private String themeSort(String theme){
        if(theme.endsWith(",  ]")){
            return theme.replaceAll(",  ]", "]");
        }else {
            return theme;
        }
    }

    private void populateSpinners() {
        ArrayAdapter<CharSequence> fAdapter;
        fAdapter = ArrayAdapter.createFromResource(this, R.array.storeType, android.R.layout.simple_spinner_dropdown_item);
        fAdapter.setDropDownViewResource(R.layout.spinner_cescum_layout);
        kind.setAdapter(fAdapter);
    }

    private void populteSubSpinners(int itemNum) {
        typeSpinnerID = itemNum;
        ArrayAdapter<CharSequence> fAdapter;
        fAdapter = ArrayAdapter.createFromResource(this, itemNum, android.R.layout.simple_spinner_dropdown_item);
        fAdapter.setDropDownViewResource(R.layout.spinner_cescum_layout);
        type.setAdapter(fAdapter);
    }

    private AdapterView.OnItemSelectedListener spinSelectedlistener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case (0):
                    populteSubSpinners(R.array.type1);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (1):
                    populteSubSpinners(R.array.type2);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (2):
                    populteSubSpinners(R.array.type3);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (3):
                    populteSubSpinners(R.array.type4);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (4):
                    populteSubSpinners(R.array.type5);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (5):
                    populteSubSpinners(R.array.type6);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (6):
                    populteSubSpinners(R.array.type7);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (7):
                    populteSubSpinners(R.array.type8);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (8):
                    populteSubSpinners(R.array.type9);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (9):
                    populteSubSpinners(R.array.type10);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (10):
                    populteSubSpinners(R.array.type11);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (11):
                    populteSubSpinners(R.array.type12);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (12):
                    populteSubSpinners(R.array.type13);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (13):
                    populteSubSpinners(R.array.type14);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (14):
                    populteSubSpinners(R.array.type15);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (15):
                    populteSubSpinners(R.array.type16);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (16):
                    populteSubSpinners(R.array.type17);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (17):
                    populteSubSpinners(R.array.type18);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (18):
                    populteSubSpinners(R.array.type19);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (19):
                    populteSubSpinners(R.array.type20);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (20):
                    populteSubSpinners(R.array.type21);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (21):
                    populteSubSpinners(R.array.type22);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (22):
                    populteSubSpinners(R.array.type23);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (23):
                    populteSubSpinners(R.array.type24);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (24):
                    populteSubSpinners(R.array.type25);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (25):
                    populteSubSpinners(R.array.type26);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (26):
                    populteSubSpinners(R.array.type27);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (27):
                    populteSubSpinners(R.array.type28);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (28):
                    populteSubSpinners(R.array.type29);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (29):
                    populteSubSpinners(R.array.type30);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (30):
                    populteSubSpinners(R.array.type31);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (31):
                    populteSubSpinners(R.array.type32);
                    findViewById(R.id.selfKindLayout).setVisibility(View.GONE);
                    break;
                case (32):
                    populteSubSpinners(R.array.type33);
                    findViewById(R.id.selfKindLayout).setVisibility(View.VISIBLE);
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
            if (parent.getSelectedItem().toString().equals("기타(직접입력)")) {
                findViewById(R.id.selfTypeLayout).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.selfTypeLayout).setVisibility(View.GONE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TextView cpZipcode = findViewById(R.id.cpZipcode);
        TextView cpAddress = findViewById(R.id.cpAddress);
        EditText cpAddrDetails = findViewById(R.id.cpAddrDetails);
        switch (resultCode) {

            case 1:

                String zipcode = data.getStringExtra("zipcode");
                String addr1 = data.getStringExtra("address1");
                String addr2 = data.getStringExtra("address2");
                if (zipcode != null || zipcode != "") {
                    cpZipcode.setText(zipcode);
                }
                if (addr1 != null || addr1 != "") {
                    cpAddress.setText(addr1);
                }
                if (addr2 != null || addr2 != "") {
                    cpAddrDetails.setText(addr2);
                }
                break;


            default:

                break;

        }

    }
}