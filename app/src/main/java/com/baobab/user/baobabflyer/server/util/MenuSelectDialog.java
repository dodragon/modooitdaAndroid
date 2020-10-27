package com.baobab.user.baobabflyer.server.util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.Payment;
import com.baobab.user.baobabflyer.PaypleWebView;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.adapter.AccountPagerAdapter;
import com.baobab.user.baobabflyer.server.adapter.SelectEventListAdapter;
import com.baobab.user.baobabflyer.server.adapter.SelectedMenuAdapter;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.vo.EventCpMenuVO;
import com.baobab.user.baobabflyer.server.vo.EventCpVO;
import com.baobab.user.baobabflyer.server.vo.PaymentVO;
import com.baobab.user.baobabflyer.server.vo.PaypleAccountVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuSelectDialog extends DialogFragment implements View.OnClickListener, ViewPager.OnPageChangeListener{

    RetroSingleTon retroSingleTon;
    private static int cpSeq;
    private static String cpName;
    public static Context context;

    View view;

    private String payKind = "payple";

    private int acListSize = 0;
    List<PaypleAccountVO> accList;
    String payerId;
    String isSimple;

    private static boolean isSelected;
    private static String selectedE;
    private static String selectedO;
    private static int selectedM;

    public static MenuSelectDialog newInstance(int seqNum, Context acContext, String thisCpName, boolean menuChoice, String eventSerial, String optionSerial, int menuSeq) {
        Bundle bundle = new Bundle();
        bundle.putString("", "");

        cpSeq = seqNum;
        cpName = thisCpName;
        context = acContext;

        isSelected = menuChoice;
        selectedE = eventSerial;
        selectedO = optionSerial;
        selectedM = menuSeq;

        MenuSelectDialog fragment = new MenuSelectDialog();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.FullscreenDialog);
        view = getActivity().getLayoutInflater().inflate(R.layout.custom_dialog, null);

        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setCanceledOnTouchOutside(false);

        final LinearLayout eventBtn = view.findViewById(R.id.eventBtn);
        final LinearLayout optionBtn = view.findViewById(R.id.optionBtn);
        final LinearLayout menuBtn = view.findViewById(R.id.menuBtn);

        eventBtn.setOnClickListener(this);
        optionBtn.setOnClickListener(this);
        menuBtn.setOnClickListener(this);

        ((RadioGroup)view.findViewById(R.id.payKindGroup)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.payple){
                    payKind = "payple";
                    view.findViewById(R.id.pagerLayout).setVisibility(View.VISIBLE);
                }else {
                    payKind = "inicis";
                    view.findViewById(R.id.pagerLayout).setVisibility(View.GONE);
                }
            }
        });

        ((RadioButton)view.findViewById(R.id.payple)).setChecked(true);

        Call<List<EventCpVO>> call = retroSingleTon.getInstance().getRetroInterface(context.getApplicationContext()).cpEvent(cpSeq);
        call.enqueue(new Callback<List<EventCpVO>>() {
            @Override
            public void onResponse(Call<List<EventCpVO>> call, Response<List<EventCpVO>> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        List<EventCpVO> list = response.body();

                        if(list.isEmpty()){
                            Toast.makeText(context, "구매가능한 상품이 없습니다.", Toast.LENGTH_SHORT).show();
                            dismissDialog();
                        }else {
                            List<EventCpMenuVO> selectedList = new ArrayList<>();
                            Button sellBtn = view.findViewById(R.id.sellBtn);
                            sellBtn.setOnClickListener(sell);
                            sellBtn.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    View line = view.findViewById(R.id.payKindLine);
                                    if(s.toString().equals("구매하기")){
                                        line.setVisibility(View.GONE);
                                    }else {
                                        line.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void afterTextChanged(Editable s) {

                                }
                            });

                            RecyclerView selectedRecyclerView = view.findViewById(R.id.selectedRecyclerView);
                            selectedRecyclerView.setHasFixedSize(true);
                            RecyclerView.LayoutManager selectedLayoutManager = new LinearLayoutManager(context);
                            selectedRecyclerView.setLayoutManager(selectedLayoutManager);

                            SelectedMenuAdapter selectedAdapter = new SelectedMenuAdapter(selectedList, context, sellBtn, null);
                            selectedRecyclerView.setAdapter(selectedAdapter);

                            RecyclerView recyclerView = view.findViewById(R.id.eventRecyclerView);
                            recyclerView.setHasFixedSize(true);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                            recyclerView.setLayoutManager(layoutManager);

                            SelectEventListAdapter adapter = new SelectEventListAdapter(list, eventBtn, optionBtn, menuBtn, view.findViewById(R.id.optionLayout), view.findViewById(R.id.menuLayout),
                                    (TextView) view.findViewById(R.id.eventTv), (TextView)view.findViewById(R.id.optionTv), (RecyclerView) view.findViewById(R.id.optionRecyclerView),
                                    (RecyclerView) view.findViewById(R.id.menuRecyclerView), context, selectedRecyclerView, sellBtn, selectedList, selectedAdapter, isSelected, selectedE, selectedO, selectedM);

                            recyclerView.removeAllViewsInLayout();
                            recyclerView.setAdapter(adapter);
                        }
                    }else {
                        Log.d("selectDialog :::", "response 내용없음");
                        Toast.makeText(context, "구매가능한 상품이 없습니다.", Toast.LENGTH_SHORT).show();
                        dismissDialog();
                    }
                }else {
                    Log.d("selectDialog :::", "서버 로그 확인 필요");
                    Toast.makeText(context, "구매가능한 상품이 없습니다.", Toast.LENGTH_SHORT).show();
                    dismissDialog();
                }
            }

            @Override
            public void onFailure(Call<List<EventCpVO>> call, Throwable t) {
                Log.d("selectDialog :::", t.getLocalizedMessage());
                Toast.makeText(context, "구매가능한 상품이 없습니다.", Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });

        Call<List<PaypleAccountVO>> acCall = retroSingleTon.getInstance().getRetroInterface(context.getApplicationContext()).accountCheck(context.getSharedPreferences("user", 0).getString("email", ""));
        acCall.enqueue(new Callback<List<PaypleAccountVO>>() {
            @Override
            public void onResponse(Call<List<PaypleAccountVO>> call, Response<List<PaypleAccountVO>> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        accList = response.body();
                        acListSize = accList.size();

                        if(acListSize == 0){
                            payerId = "";
                            isSimple = "N";
                        }else {
                            payerId = accList.get(0).getPayerId();
                            isSimple = "Y";
                        }

                        makeViewPager(view, accList);
                    }else {
                        Log.d("등록계좌 조회", "response 내용 없음");
                        makeViewPager(view, new ArrayList<PaypleAccountVO>());
                    }
                }else {
                    Log.d("등록계좌 조회", "서버 로그 확인 필요");
                    makeViewPager(view, new ArrayList<PaypleAccountVO>());
                }
            }

            @Override
            public void onFailure(Call<List<PaypleAccountVO>> call, Throwable t) {
                Log.d("등록계좌 조회", t.getLocalizedMessage());
                makeViewPager(view, new ArrayList<PaypleAccountVO>());
            }
        });

        view.findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });

        return dialog;
    }

    public void dismissDialog() {
        this.dismiss();
    }

    public int returnRecyclerId(View btn){
        int resultId = 0;
        switch (btn.getId()){
            case R.id.eventBtn:
                resultId = R.id.eventRecyclerView;
                break;
            case R.id.optionBtn:
                resultId = R.id.optionRecyclerView;
                break;
            case R.id.menuBtn:
                resultId = R.id.menuRecyclerView;
                break;
        }
        return resultId;
    }

    private void makeViewPager(View view, List<PaypleAccountVO> list){
        ViewPager viewPager = view.findViewById(R.id.accountPager);
        viewPager.setClipToPadding(false);

        float density = getResources().getDisplayMetrics().density;
        int margin = (int)(60 * density);
        viewPager.setPadding(margin, 0, margin, 0);
        viewPager.setPageMargin(margin / 2);

        AccountPagerAdapter adapter = new AccountPagerAdapter(context, list, this);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(MenuSelectDialog.this);
    }

    @Override
    public void onClick(View v) {
        int recyclerId = returnRecyclerId(v);
        ViewAnimationUtil util = new ViewAnimationUtil();
        RecyclerView recyclerView = view.findViewById(recyclerId);
        ImageView imageView = ((ImageView)((LinearLayout)((LinearLayout)v).getChildAt(0)).getChildAt(2));

        if(recyclerView.getVisibility() == View.VISIBLE){
            util.collapse(recyclerView);
            imageView.setImageDrawable(context.getDrawable(R.drawable.ic_chevron_dropdown));
        }else {
            util.expand(recyclerView);
            imageView.setImageDrawable(context.getDrawable(R.drawable.ic_chevron_dropdown2));
        }
    }

    View.OnClickListener sell = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button thisBtn = (Button)v;
            if(thisBtn.getText().toString().equals("구매하기")){
                Toast.makeText(context, "선택된 메뉴가 없습니다.", Toast.LENGTH_SHORT).show();
            }else {
                List<EventCpMenuVO> list = SelectedMenuAdapter.list;
                PaymentVO vo = makePayVo(list, thisBtn);
                ArrayList<EventCpMenuVO> arrayList = new ArrayList<>();
                for(int i=0;i<list.size();i++){
                    arrayList.add(list.get(i));
                }

                Intent intent;

                if(payKind.equals("payple")){
                    intent = new Intent(context, PaypleWebView.class);
                    intent.putExtra("payerId", payerId);
                    intent.putExtra("isSimple", isSimple);
                    intent.putExtra("payWork", "PAY");
                }else {
                    intent = new Intent(context, Payment.class);
                }
                intent.putExtra("vo", vo);
                intent.putExtra("eventList", arrayList);
                startActivity(intent);
            }
        }
    };

    private PaymentVO makePayVo(List<EventCpMenuVO> list, Button button){
        PaymentVO vo = new PaymentVO();

        SharedPreferences spf = context.getSharedPreferences("user", 0);
        vo.setUserPhone(spf.getString("phone", ""));
        vo.setCpName(cpName);
        vo.setCpSeq(cpSeq);
        vo.setEmail(spf.getString("email", ""));
        vo.setOrderNum(makeOrderNum(vo.getUserPhone()));

        String goods = "";
        int totalPrice = 0;
        if(list.size() > 1){
            int allEa = 0;
            for(int i=0;i<list.size();i++){
                allEa += list.get(i).getSelectedEa();
            }
            goods = list.get(0).getMenuName() + " 외" + (allEa - 1) + "개";
        }else {
            goods = list.get(0).getMenuName();
        }

        for(int i=0;i<list.size();i++){
            totalPrice += list.get(i).getPrice();
        }

        vo.setTotalPrice(totalPrice);
        vo.setTotalDisPrice(Integer.parseInt(button.getText().toString().split(" ")[0].replaceAll(",", "").replaceAll("원", "")));
        vo.setGoods(goods);

        return vo;
    }

    private String makeOrderNum(String userPhone){
        MakeCertNumber certNumber = new MakeCertNumber();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return "P" + cpSeq + certNumber.numberGen(10, 1) + format.format(new Date()) + userPhone.substring(5, 8);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        if(i < acListSize){
            payerId = accList.get(i).getPayerId();
            isSimple = "Y";
        }else {
            payerId = "";
            isSimple = "N";
        }

        Log.d("심플", isSimple);
        Log.d("아이디", payerId);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
