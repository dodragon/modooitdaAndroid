package com.baobab.user.baobabflyer.server.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.appcompat.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.PaypleWebView;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.PaymentVO;
import com.baobab.user.baobabflyer.server.vo.PaypleAccountVO;
import com.bumptech.glide.Glide;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountPagerAdapter extends PagerAdapter {

    RetroSingleTon retroSingleTon;
    Context context;
    List<PaypleAccountVO> list;
    DialogFragment df;

    AlertDialog alert;

    public AccountPagerAdapter(Context context, List<PaypleAccountVO> list, DialogFragment df) {
        this.context = context;
        this.list = list;
        this.df = df;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.account_pager, null);

        Log.d("뷰페이저" + position, String.valueOf(list.size()));

        ImageView logo = view.findViewById(R.id.logo);
        ImageView deleteBtn = view.findViewById(R.id.deleteBtn);
        RelativeLayout layout = view.findViewById(R.id.layout);
        TextView holder = view.findViewById(R.id.accountHolder);
        TextView number = view.findViewById(R.id.accountNumber);

        if(position < list.size()){
            final PaypleAccountVO vo = list.get(position);
            int[] resource = selectBankResoruce(vo.getBankCode());

            if(vo.getBankCode().equals("011")){
                DisplayMetrics dm = context.getResources().getDisplayMetrics();
                logo.setLayoutParams(new LinearLayout.LayoutParams(Math.round(100 * dm.density), Math.round(28 * dm.density)));
            }else if(vo.getBankCode().equals("048") | vo.getBankCode().equals("007")){
                DisplayMetrics dm = context.getResources().getDisplayMetrics();
                logo.setLayoutParams(new LinearLayout.LayoutParams(Math.round(56 * dm.density), Math.round(28 * dm.density)));
            }

            Glide.with(context).load(resource[0]).override(86, 28).into(logo);
            layout.setTag(vo.getPayerId());
            layout.setBackground(context.getDrawable(resource[1]));
            holder.setText(vo.getAccountHolder());
            number.setText(vo.getAccountNumber());
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert = new AlertDialog.Builder(context)
                            .setIcon(R.drawable.logo_and)
                            .setTitle("등록계좌 해지")
                            .setMessage(vo.getBankName() + "\n" + vo.getAccountNumber() + "\n계좌를 해지할까요?")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Call<String> call = retroSingleTon.getInstance().getRetroInterface(context.getApplicationContext()).accountDelete(vo.getPayerId());
                                    call.enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {
                                            if(response.isSuccessful()){
                                                if(response.body() != null){
                                                    String result = response.body();

                                                    if(result.equals("success")){
                                                        Toast.makeText(context, vo.getBankName() + "\n" + vo.getAccountNumber() + "\n계좌가 해지되었습니다.", Toast.LENGTH_SHORT).show();
                                                        list.remove(position);
                                                        notifyDataSetChanged();
                                                    }else {
                                                        Log.d("등록계좌 해지", "result not success");
                                                        Toast.makeText(context, "다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }else {
                                                    Log.d("등록계좌 해지", "response is null");
                                                    Toast.makeText(context, "다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            }else {
                                                Log.d("등록계좌 해지", "check server logs");
                                                Toast.makeText(context, "다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {
                                            Log.d("등록계좌 해지", t.getLocalizedMessage());
                                            Toast.makeText(context, "다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create();
                    alert.show();
                }
            });
        }else {
            layout.setBackground(context.getDrawable(R.drawable.bank_back_none));
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PaypleWebView.class);
                    intent.putExtra("vo", new PaymentVO());
                    intent.putExtra("isSimple", "Y");
                    intent.putExtra("payWork", "AUTH");
                    context.startActivity(intent);
                    if(df != null){
                        df.dismiss();
                    }else {
                        ((Activity)context).finish();
                    }
                }
            });
            number.setText("새 계좌 등록");
            holder.setText("여기를 클릭하시면 계좌만 등록합니다.");
            deleteBtn.setVisibility(View.GONE);
        }

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return list.size() + 1;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public int getItemPosition(Object o){
        return POSITION_NONE;
    }

    private static int[] selectBankResoruce(String bankCode){
        if(bankCode.equals("004")){
            return new int[] {R.drawable.bank_gookmin, R.drawable.bank_back_gookmin};
        }else if(bankCode.equals("011")){
            return new int[] {R.drawable.bank_nonghyeop, R.drawable.bank_back_nonghyeop};
        }else if(bankCode.equals("088")){
            return new int[] {R.drawable.bank_sinhan, R.drawable.bank_back_sinhan};
        }else if(bankCode.equals("020")){
            return new int[] {R.drawable.bank_woori, R.drawable.bank_back_woori};
        }else if(bankCode.equals("003")){
            return new int[] {R.drawable.bank_giup, R.drawable.bank_back_giup};
        }else if(bankCode.equals("081")){
            return new int[] {R.drawable.bank_hana, R.drawable.bank_back_hana};
        }else if(bankCode.equals("071")){
            return new int[] {R.drawable.bank_woochegook, R.drawable.bank_back_woochegook};
        }else if(bankCode.equals("045")){
            return new int[] {R.drawable.bank_semaul, R.drawable.bank_back_semaul};
        }else if(bankCode.equals("031")){
            return new int[] {R.drawable.bank_deagu, R.drawable.bank_back_deagu};
        }else if(bankCode.equals("034")){
            return new int[] {R.drawable.bank_gwangju, R.drawable.bank_back_gwangju};
        }else if(bankCode.equals("039")){
            return new int[] {R.drawable.bank_gyeongnam, R.drawable.bank_back_gyeonnam};
        }else if(bankCode.equals("002")){
            return new int[] {R.drawable.bank_sanup, R.drawable.bank_back_sanup};
        }else if(bankCode.equals("048")){
            return new int[] {R.drawable.bank_sinhyeop, R.drawable.bank_back_sinhyeop};
        }else if(bankCode.equals("007")){
            return new int[] {R.drawable.bank_suhyeop, R.drawable.bank_back_suhyeop};
        }else if(bankCode.equals("032")){
            return new int[] {R.drawable.bank_busan, R.drawable.bank_back_busan};
        }else {
            return new int[] {0, 0};
        }
    }
}
