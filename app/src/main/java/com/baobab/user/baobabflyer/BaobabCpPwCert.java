package com.baobab.user.baobabflyer;

import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baobab.user.baobabflyer.server.util.Sha256Util;

public class BaobabCpPwCert extends AppCompatActivity {

    EditText inputEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baobab_cp_pw_cert);

        inputEt = findViewById(R.id.inputPw);
        inputEt.addTextChangedListener(watcher);

        Intent getData = getIntent();
        final String email = getData.getStringExtra("email");
        final String pw = getData.getStringExtra("pw");

        findViewById(R.id.pwCert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPw(pw, email);
            }
        });

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void checkPw(String pw, String email){
        String inputPw = inputEt.getText().toString();
        if(new Sha256Util().sha256(inputPw).equals(pw)){
            Intent intent = new Intent(this, BaobabCpPwChange.class);
            intent.putExtra("email", email);
            intent.putExtra("pw", pw);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다. 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
            inputEt.setText("");
        }
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Button btn = findViewById(R.id.pwCert);
            if(inputEt.getText().toString().length() == 4){
                btn.setEnabled(true);
                btn.setBackgroundColor(Color.rgb(252, 132, 73));
            }else {
                btn.setEnabled(false);
                btn.setBackgroundColor(Color.rgb(216, 220, 229));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
