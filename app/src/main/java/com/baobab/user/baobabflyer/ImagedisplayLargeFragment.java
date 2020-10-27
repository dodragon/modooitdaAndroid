package com.baobab.user.baobabflyer;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class ImagedisplayLargeFragment extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_imagedisplay_large_fragment );
        Intent getImg = getIntent();
        List<String> imgList = (List<String>) getImg.getSerializableExtra( "url" );
        int listPos = getImg.getIntExtra( "position", 0 );

        ViewPager viewPager = findViewById(R.id.viewPager);
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentAdapter);
        for (int i = 0; i < imgList.size(); i++) {
            ImageFragment imageFragment = new ImageFragment();
            Bundle bundle = new Bundle();
            bundle.putString("url", imgList.get(i));
            imageFragment.setArguments(bundle);
            fragmentAdapter.addItem(imageFragment);
        }
        viewPager.setCurrentItem( listPos );
        fragmentAdapter.notifyDataSetChanged();
    }

    class FragmentAdapter extends FragmentPagerAdapter {

        // ViewPager에 들어갈 Fragment들을 담을 리스트
        private ArrayList<Fragment> fragments = new ArrayList<>();

        // 필수 생성자
        FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        // List에 Fragment를 담을 함수
        void addItem(Fragment fragment) {
            fragments.add(fragment);
        }
    }
}