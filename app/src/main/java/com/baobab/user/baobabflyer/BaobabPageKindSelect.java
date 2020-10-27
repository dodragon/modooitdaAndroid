package com.baobab.user.baobabflyer;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.baobab.user.baobabflyer.server.vo.PageNeedVO;

public class BaobabPageKindSelect extends AppCompatActivity {

    RadioButton[] toggleArr;
    PageNeedVO vo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_page_kind_select);

        toggleArr = new RadioButton[]{findViewById(R.id.toggleButton01),
                findViewById(R.id.toggleButton02),
                findViewById(R.id.toggleButton03),
                findViewById(R.id.toggleButton04),
                findViewById(R.id.toggleButton05),
                findViewById(R.id.toggleButton06),
                findViewById(R.id.toggleButton07),
                findViewById(R.id.toggleButton08),
                findViewById(R.id.toggleButton09),
                findViewById(R.id.toggleButton10),
                findViewById(R.id.toggleButton11),
                findViewById(R.id.toggleButton12)
        };

        vo = (PageNeedVO) getIntent().getSerializableExtra("vo");

        /*for(int i=0;i<toggleArr.length;i++){
            toggleArr[i].setOnCheckedChangeListener(toggleListener);
            if(vo.getMenu().equals(toggleArr[i].getTag().toString())){
                toggleArr[i].setChecked(true);
            }
        }*/

        findViewById(R.id.escBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        findViewById(R.id.submit).setOnClickListener(submit);
    }

    CompoundButton.OnCheckedChangeListener toggleListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            int resourceN = 0;
            int resourceP = 0;

            switch (compoundButton.getId()){
                case R.id.toggleButton01:
                    resourceN = R.drawable.m_ico_1n;
                    resourceP = R.drawable.m_ico_1p;
                    break;
                case R.id.toggleButton02:
                    resourceN = R.drawable.m_ico_2n;
                    resourceP = R.drawable.m_ico_2p;
                    break;
                case R.id.toggleButton03:
                    resourceN = R.drawable.m_ico_3n;
                    resourceP = R.drawable.m_ico_3p;
                    break;
                case R.id.toggleButton04:
                    resourceN = R.drawable.m_ico_4n;
                    resourceP = R.drawable.m_ico_4p;
                    break;
                case R.id.toggleButton05:
                    resourceN = R.drawable.m_ico_5n;
                    resourceP = R.drawable.m_ico_5p;
                    break;
                case R.id.toggleButton06:
                    resourceN = R.drawable.m_ico_6n;
                    resourceP = R.drawable.m_ico_6p;
                    break;
                case R.id.toggleButton07:
                    resourceN = R.drawable.m_ico_7n;
                    resourceP = R.drawable.m_ico_7p;
                    break;
                case R.id.toggleButton08:
                    resourceN = R.drawable.m_ico_8n;
                    resourceP = R.drawable.m_ico_8p;
                    break;
                case R.id.toggleButton09:
                    resourceN = R.drawable.m_ico_9n;
                    resourceP = R.drawable.m_ico_9p;
                    break;
                case R.id.toggleButton10:
                    resourceN = R.drawable.m_ico_10n;
                    resourceP = R.drawable.m_ico_10p;
                    break;
                case R.id.toggleButton11:
                    resourceN = R.drawable.m_ico_11n;
                    resourceP = R.drawable.m_ico_11p;
                    break;
                case R.id.toggleButton12:
                    resourceN = R.drawable.m_ico_12n;
                    resourceP = R.drawable.m_ico_12p;
                    break;
            }

            if (b) {
                compoundButton.setBackground(getResources().getDrawable(resourceP));

                for(int i=0;i<toggleArr.length;i++) {
                    if (!toggleArr[i].equals(compoundButton)) {
                        toggleArr[i].setChecked(false);
                    }
                }
            } else {
                compoundButton.setBackground(getResources().getDrawable(resourceN));
            }
        }
    };

    View.OnClickListener submit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String kind = "";
            for(int i=0;i<toggleArr.length;i++){
                if(toggleArr[i].isChecked()){
                    kind = toggleArr[i].getTag().toString();
                    break;
                }
            }

            Intent intent = new Intent(BaobabPageKindSelect.this, BaobabPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra( "menu", kind);
            /*intent.putExtra( "searchWord", vo.getSerchLoc());
            intent.putExtra( "selectedItem", vo.getSelectItem());*/
            intent.putExtra("location", vo.getLocation());
            intent.putExtra( "longitude", vo.getLongitude());
            intent.putExtra( "latitude", vo.getLatitude());
            intent.putExtra( "contentKind", "eatOut");
            startActivity(intent);
            finish();
        }
    };
}