package com.baobab.user.baobabflyer;

import android.content.res.AssetManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.InputStream;

public class TextEntryAgreement extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_text_entry_agreement );

        AssetManager am = getResources().getAssets() ;
        InputStream is = null ;
        byte buf[] = new byte[200000] ;
        String text = "" ;

        try {
            is = am.open("entryagreement.txt") ;

            if (is.read(buf) > 0) {
                text = new String(buf) ;
            }

            TextView textView = (TextView) findViewById(R.id.text1) ;
            textView.setText(text) ;

            is.close() ;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (is != null) {
            try {
                is.close() ;
            } catch (Exception e) {
                e.printStackTrace() ;
            }

        }
    }
}
