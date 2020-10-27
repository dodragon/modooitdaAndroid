package com.baobab.user.baobabflyer;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BaobabAllView extends AppCompatActivity {
    RetroSingleTon retroSingleTon;
    ArrayList<String> imgList;
    DisplayMetrics mMetrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_baobab_all_view );

        Intent getInfo = getIntent();

        imgList = getInfo.getStringArrayListExtra("imgList");

        makeDisplay();
    }

    public void makeDisplay() {
        makeImgView( imgList );
    }

    public void makeImgView(List<String> urlArr){
        Display display = ((WindowManager)getSystemService( WINDOW_SERVICE )).getDefaultDisplay();
        int displayWidth = display.getWidth();

        GridView gridView = findViewById( R.id.gridview );
        gridView.setAdapter( new ImageAdapter( BaobabAllView.this, displayWidth, urlArr ) );
        gridView.setOnItemClickListener( gridViewOnItemClickListener );

        mMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics( mMetrics );
    }

    private GridView.OnItemClickListener gridViewOnItemClickListener = new GridView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            Intent intent = new Intent( BaobabAllView.this, ImagedisplayLargeFragment.class );
            intent.putExtra( "url", imgList );
            Log.d( "이미지URL", arg0.getAdapter().getItem(arg2).toString() );
            intent.putExtra( "position", arg2 );
            Log.d( "순번", String.valueOf( arg2 ) );
            startActivity( intent );
        }
    };

    public class ImageAdapter extends BaseAdapter{
        private Context mContext;
        private int size;
        private List<String> imgUrlArr;

        public ImageAdapter(Context c, int width, List<String> imgUrl){
            mContext = c;
            size = width/3;
            imgUrlArr = imgUrl;
        }

        @Override
        public int getCount() {
            return imgUrlArr.size();
        }

        @Override
        public Object getItem(int position) {
            return imgUrlArr.get( position );
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int size2 = Math.round( 2 * dm.density );
            ImageView imageView;
            if (convertView == null){
                imageView = new ImageView( mContext );
                imageView.setLayoutParams( new GridView.LayoutParams( size-size2, size-size2 ) );
            }else {
                imageView = (ImageView) convertView;
            }

            imageView.setScaleType( ImageView.ScaleType.CENTER_CROP );
            Glide.clear( imageView );
            Glide.with( mContext ).load( imgUrlArr.get( position ) ).asBitmap().signature( new StringSignature( UUID.randomUUID().toString() ) ).into( imageView );
            return imageView;
        }
    }


}