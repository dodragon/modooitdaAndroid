package com.baobab.user.baobabflyer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.baobab.user.baobabflyer.activityLoader.AllActivityLoader;
import com.baobab.user.baobabflyer.activityLoader.PageLoader;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.PageNeedVO;

public class BaobabPage extends AppCompatActivity {

    RetroSingleTon retroSingleTon;

    AllActivityLoader allActivityLoader;
    PageLoader pageLoader;
    PageNeedVO mainVO;
    LinearLayout[] tabs;

    int topPageInt;
    int botPageInt;

    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_page);

        activity = this;

        allActivityLoader = new AllActivityLoader(this, getApplicationContext());
        allActivityLoader.profileSetting();
        allActivityLoader.drawerSetting();

        mainVO = (PageNeedVO) getIntent().getSerializableExtra("pageVO");
        topPageInt = mainVO.getTopPageInt();
        botPageInt = mainVO.getBotPageInt();

        ((TextView)findViewById(R.id.locationTv)).setText(getIntent().getStringExtra("userLocation"));
        pageLoader = new PageLoader(this);
        defaultSetting();
    }

    public void defaultSetting(){
        findViewById(R.id.location_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaobabPage.this, BaobabPageLocationSelect.class);
                intent.putExtra("pageVO", mainVO);
                startActivity(intent);
            }
        });

        ((ToggleButton)findViewById(R.id.sortBy_filter)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mainVO.setSortDiv("인기순");
                }else {
                    mainVO.setSortDiv("거리순");
                }
                mainVO.setBotPageInt(0);
                mainVO.setTopPageInt(0);
                pageLoader.search(mainVO);
            }
        });

        ((EditText)findViewById(R.id.searchBar)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId){
                    case EditorInfo.IME_ACTION_SEARCH:
                        mainVO.setSearchWord(((EditText)findViewById(R.id.searchBar)).getText().toString());
                        pageLoader.search(mainVO);
                        break;
                    default:
                        return false;
                }

                return true;
            }
        });

        if(mainVO.getTabDiv().equals("전체")){
            tabSetting(0);
        }else if(mainVO.getTabDiv().equals("안심")){
            tabSetting(1);
        }else {
            tabSetting(2);
        }

        ((EditText)findViewById(R.id.searchBar)).setText(mainVO.getSearchWord());

        findViewById(R.id.topMore).setOnClickListener(moreBtn);
        findViewById(R.id.botMore).setOnClickListener(moreBtn);
    }

    public void tabSetting(int tabPosition){
        tabs = new LinearLayout[]{
                findViewById(R.id.tab_all),
                findViewById(R.id.tab_safe),
                findViewById(R.id.tab_sale)
        };

        for(int i=0;i<tabs.length;i++){
            tabs[i].setOnClickListener(tabClick);
        }

        tabs[tabPosition].performClick();
    }

    View.OnClickListener tabClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for(int i=0;i<tabs.length;i++){
                ((TextView)tabs[i].getChildAt(0)).setTextColor(Color.parseColor("#b7b7b7"));
                tabs[i].getChildAt(1).setVisibility(View.GONE);
            }

            ((TextView)((LinearLayout)v).getChildAt(0)).setTextColor(Color.parseColor("#5c7cfa"));
            ((LinearLayout)v).getChildAt(1).setVisibility(View.VISIBLE);

            TextView mainTitle = findViewById(R.id.mainTitle);
            TextView subTitle = findViewById(R.id.subTitle);
            TextView mainTitle2 = findViewById(R.id.mainTitle2);
            TextView subTitle2 = findViewById(R.id.subTitle2);

            switch (v.getId()){
                case R.id.tab_all:
                    mainVO.setTabDiv("전체");
                    for(int i=0;i<mainVO.getTitleList().size();i++){
                        if(mainVO.getTitleList().get(i).getTitleDiv().equals("a1")){
                            mainTitle.setText(mainVO.getTitleList().get(i).getMainText());
                            subTitle.setText(mainVO.getTitleList().get(i).getSubText());
                        }else if(mainVO.getTitleList().get(i).getTitleDiv().equals("a2")){
                            mainTitle2.setText(mainVO.getTitleList().get(i).getMainText());
                            subTitle2.setText(mainVO.getTitleList().get(i).getSubText());
                        }
                    }
                    break;
                case R.id.tab_safe:
                    mainVO.setTabDiv("안심");
                    for(int i=0;i<mainVO.getTitleList().size();i++){
                        if(mainVO.getTitleList().get(i).getTitleDiv().equals("s1")){
                            mainTitle.setText(mainVO.getTitleList().get(i).getMainText());
                            subTitle.setText(mainVO.getTitleList().get(i).getSubText());
                        }else if(mainVO.getTitleList().get(i).getTitleDiv().equals("s2")){
                            mainTitle2.setText(mainVO.getTitleList().get(i).getMainText());
                            subTitle2.setText(mainVO.getTitleList().get(i).getSubText());
                        }
                    }
                    break;
                case R.id.tab_sale:
                    mainVO.setTabDiv("특가");
                    for(int i=0;i<mainVO.getTitleList().size();i++){
                        if(mainVO.getTitleList().get(i).getTitleDiv().equals("p1")){
                            mainTitle.setText(mainVO.getTitleList().get(i).getMainText());
                            subTitle.setText(mainVO.getTitleList().get(i).getSubText());
                        }else if(mainVO.getTitleList().get(i).getTitleDiv().equals("p2")){
                            mainTitle2.setText(mainVO.getTitleList().get(i).getMainText());
                            subTitle2.setText(mainVO.getTitleList().get(i).getSubText());
                        }
                    }
                    break;
            }

            mainVO.setBotPageInt(0);
            mainVO.setTopPageInt(0);

            pageLoader.search(mainVO);
        }
    };

    View.OnClickListener moreBtn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.topMore){
                mainVO.setTopPageInt(mainVO.getTopPageInt() + 10);
            }else{
                mainVO.setBotPageInt(mainVO.getBotPageInt() + 5);
            }

            pageLoader.search(mainVO);
        }
    };
}