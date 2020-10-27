package com.baobab.user.baobabflyer.activityLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baobab.user.baobabflyer.BaobabCpEvent;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.singleTons.RetroSingleTon;
import com.baobab.user.baobabflyer.server.util.MakeCertNumber;
import com.baobab.user.baobabflyer.server.vo.EventCpMenuVO;
import com.baobab.user.baobabflyer.server.vo.EventCpOptionVO;
import com.baobab.user.baobabflyer.server.vo.EventCpVO;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateEventLoader {

    RetroSingleTon retroSingleTon;

    Context context;
    EventCpVO mainVO;

    private int optionIndex = 0;
    private Map<Integer, Integer> menuIndexMap;
    private List<Integer> optionIndexList;
    DecimalFormat format;
    private boolean isMain = false;

    private View.OnClickListener addListener;
    private View.OnClickListener delListener;

    public UpdateEventLoader(Context context, EventCpVO vo, int mainSeq) {
        this.context = context;
        this.mainVO = vo;
        for(int i=0;i<vo.getOptionList().size();i++){
            for(int j=0;j<vo.getOptionList().get(i).getMenuList().size();j++){
                if(mainSeq == vo.getOptionList().get(i).getMenuList().get(j).getSeqNum()){
                    Toast.makeText(context, "대표메뉴가 포함된 이벤트 입니다. 수정시 대표메뉴를 다시 설정해 주셔야 합니다.", Toast.LENGTH_SHORT).show();
                    isMain = true;
                    break;
                }
            }
        }

        menuIndexMap = new HashMap<>();
        optionIndexList = new ArrayList<>();
        format = new DecimalFormat("###,###");

        LinearLayout addOptionBtn = ((Activity) context).findViewById(R.id.addOption);
        addOptionBtn.setOnClickListener(addOption);

        LinearLayout eventLayout = ((Activity) context).findViewById(R.id.eventLayout);
        for (int i = 0; i < vo.getOptionList().size(); i++) {
            eventLayout.addView(optionLayout(eventLayout, vo.getOptionList().get(i)));
        }

        ((EditText) ((Activity) context).findViewById(R.id.eventNameTv)).setText(vo.getEventName());

        ((Activity) context).findViewById(R.id.saveBtn).setOnClickListener(saveEvent);
    }

    public LinearLayout optionLayout(final LinearLayout mother, EventCpOptionVO vo) {
        optionIndex++;

        final int thisOptionIndex = optionIndex;
        optionIndexList.add(thisOptionIndex);

        LinearLayout optionLayout = new LinearLayout(context);
        LinearLayout.LayoutParams optionLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        optionLayoutParams.setMargins(dp(40), dp(20), dp(40), 0);
        optionLayout.setLayoutParams(optionLayoutParams);
        optionLayout.setBackground(context.getDrawable(R.drawable.menu_select_background));
        optionLayout.setElevation(dp(4));
        optionLayout.setOrientation(LinearLayout.VERTICAL);
        optionLayout.setPadding(dp(20), dp(20), dp(20), dp(20));

        LinearLayout optionNameLayout = new LinearLayout(context);
        LinearLayout.LayoutParams optionNameLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        optionNameLayout.setLayoutParams(optionNameLayoutParams);
        optionNameLayout.setGravity(Gravity.CENTER_VERTICAL);
        optionNameLayout.setOrientation(LinearLayout.HORIZONTAL);
        optionLayout.addView(optionNameLayout);

        EditText optionNameEt = new EditText(context);
        optionNameEt.setId(thisOptionIndex);
        LinearLayout.LayoutParams optionNameEtParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        optionNameEt.setLayoutParams(optionNameEtParams);
        optionNameEt.setBackground(null);
        optionNameEt.setHint("옵션 이름을 입력하세요");
        optionNameEt.setLetterSpacing(-0.07f);
        optionNameEt.setLines(1);
        optionNameEt.setTextColor(Color.BLACK);
        optionNameEt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        setCursorDrawableColor(optionNameEt, Color.rgb(252, 132, 73));
        optionNameEt.setText(vo.getOptionName());
        optionNameLayout.addView(optionNameEt);

        ImageView optionDelBtn = new ImageView(context);
        LinearLayout.LayoutParams optionDelBtnParams = new LinearLayout.LayoutParams(dp(16), dp(17));
        optionDelBtnParams.leftMargin = dp(8);
        optionDelBtn.setLayoutParams(optionDelBtnParams);
        optionDelBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_delete));
        optionDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mother.removeView((View) v.getParent().getParent());
                if (optionIndex > 0) {
                    int listIndex = 0;
                    for(int i=0;i<optionIndexList.size();i++){
                        if(thisOptionIndex == optionIndexList.get(i)){
                            listIndex = i;
                            break;
                        }
                    }
                    optionIndexList.remove(listIndex);
                    optionIndex--;
                }
            }
        });
        optionNameLayout.addView(optionDelBtn);

        for (int i = 0; i < vo.getMenuList().size(); i++) {
            if (i == 0) {
                optionLayout.addView(menuLayout(optionLayout, true, thisOptionIndex, vo.getMenuList().get(i)));
            } else {
                optionLayout.addView(menuLayout(optionLayout, false, thisOptionIndex, vo.getMenuList().get(i)));
            }
        }

        return optionLayout;
    }

    public LinearLayout menuLayout(final LinearLayout motherLayout, final boolean isFirst, final int thisOptionIndex, EventCpMenuVO vo) {
        final int thisMenuIndex;
        if (isFirst) {
            thisMenuIndex = 0;
        } else {
            thisMenuIndex = menuIndexMap.get(thisOptionIndex) + 1;
        }

        Log.d("생성될때 메뉴인덱스", String.valueOf(thisMenuIndex));

        menuIndexMap.put(thisOptionIndex, thisMenuIndex);

        final LinearLayout menuLayout = new LinearLayout(context);
        menuLayout.setTag(String.valueOf(thisMenuIndex));
        LinearLayout.LayoutParams menuLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        menuLayoutParams.topMargin = dp(10);
        menuLayout.setBackground(context.getDrawable(R.drawable.coupon_total_background));
        menuLayout.setLayoutParams(menuLayoutParams);
        menuLayout.setOrientation(LinearLayout.VERTICAL);
        menuLayout.setPadding(0, dp(15), 0, 0);

        EditText menuNameEt = new EditText(context);
        LinearLayout.LayoutParams menuNameParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(20));
        menuLayoutParams.bottomMargin = dp(22);
        menuNameEt.setLayoutParams(menuNameParams);
        menuNameEt.setBackground(null);
        menuNameEt.setHint("메뉴명을 입력하세요");
        menuNameEt.setLetterSpacing(-0.07f);
        menuNameEt.setPadding(dp(16), 0, dp(16), 0);
        menuNameEt.setTextColor(Color.BLACK);
        menuNameEt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        setCursorDrawableColor(menuNameEt, Color.rgb(252, 132, 73));
        menuNameEt.setText(vo.getMenuName());
        menuLayout.addView(menuNameEt);

        LinearLayout secondLayout = new LinearLayout(context);
        secondLayout.setId((thisOptionIndex * 10000) + thisMenuIndex);
        LinearLayout.LayoutParams secondLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        secondLayout.setLayoutParams(secondLayoutParams);
        secondLayout.setOrientation(LinearLayout.VERTICAL);
        secondLayout.setPadding(dp(16), 0, dp(16), 0);
        menuLayout.addView(secondLayout);

        LinearLayout priceLayout = new LinearLayout(context);
        LinearLayout.LayoutParams priceLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        priceLayoutParam.setMargins(0, dp(10), 0, dp(10));
        priceLayout.setLayoutParams(priceLayoutParam);
        priceLayout.setOrientation(LinearLayout.HORIZONTAL);
        secondLayout.addView(priceLayout);

        TextView priceTv = new TextView(context);
        LinearLayout.LayoutParams priceTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        priceTv.setGravity(Gravity.CENTER);
        priceTv.setLayoutParams(priceTvParams);
        priceTv.setLetterSpacing(-0.08f);
        priceTv.setTextColor(Color.rgb(155, 155, 155));
        priceTv.setText("판매가");
        priceTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        priceLayout.addView(priceTv);

        final EditText priceEt = new EditText(context);
        LinearLayout.LayoutParams priceEtParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        priceEtParams.leftMargin = dp(4);
        priceEt.setLayoutParams(priceEtParams);
        priceEt.setBackground(null);
        priceEt.setGravity(Gravity.END);
        priceEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        priceEt.setLines(1);
        priceEt.setText("");
        priceEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        priceEt.setHint("판매가(원가) 입력");
        priceEt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        setCursorDrawableColor(priceEt, Color.rgb(252, 132, 73));
        priceEt.setText(String.valueOf(vo.getPrice()));
        priceLayout.addView(priceEt);

        TextView priceIcTv = new TextView(context);
        LinearLayout.LayoutParams priceIcTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        priceIcTvParams.leftMargin = dp(3);
        priceIcTv.setLayoutParams(priceIcTvParams);
        priceIcTv.setGravity(Gravity.CENTER);
        priceIcTv.setLetterSpacing(-0.07f);
        priceIcTv.setTextColor(Color.rgb(81, 81, 81));
        priceIcTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        priceIcTv.setText("원");
        priceLayout.addView(priceIcTv);

        LinearLayout disPriceLayout = new LinearLayout(context);
        LinearLayout.LayoutParams disPriceLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        disPriceLayout.setLayoutParams(disPriceLayoutParams);
        disPriceLayout.setOrientation(LinearLayout.HORIZONTAL);
        secondLayout.addView(disPriceLayout);

        TextView disPriceTv = new TextView(context);
        LinearLayout.LayoutParams disPriceTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        disPriceTv.setGravity(Gravity.CENTER);
        disPriceTv.setLayoutParams(disPriceTvParams);
        disPriceTv.setLetterSpacing(-0.08f);
        disPriceTv.setTextColor(Color.rgb(155, 155, 155));
        disPriceTv.setText("할인가");
        disPriceTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        disPriceLayout.addView(disPriceTv);

        final EditText disPriceEt = new EditText(context);
        LinearLayout.LayoutParams disPriceParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        disPriceParams.leftMargin = dp(4);
        disPriceEt.setLayoutParams(disPriceParams);
        disPriceEt.setBackground(null);
        disPriceEt.setGravity(Gravity.END);
        disPriceEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        disPriceEt.setLines(1);
        disPriceEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        disPriceEt.setHint("할인된 가격 입력");
        disPriceEt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        setCursorDrawableColor(disPriceEt, Color.rgb(252, 132, 73));
        disPriceEt.setText(String.valueOf(vo.getDisPrice()));
        disPriceLayout.addView(disPriceEt);

        TextView disPriceIcTv = new TextView(context);
        LinearLayout.LayoutParams disPriceIcTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        disPriceIcTvParams.leftMargin = dp(3);
        disPriceIcTv.setLayoutParams(disPriceIcTvParams);
        disPriceIcTv.setGravity(Gravity.CENTER);
        disPriceIcTv.setLetterSpacing(-0.07f);
        disPriceIcTv.setTextColor(Color.rgb(81, 81, 81));
        disPriceIcTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        disPriceIcTv.setText("원");
        disPriceEt.setEnabled(false);
        disPriceLayout.addView(disPriceIcTv);

        LinearLayout percentLayout = new LinearLayout(context);
        LinearLayout.LayoutParams percentLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        percentLayoutParams.bottomMargin = dp(17);
        percentLayout.setLayoutParams(percentLayoutParams);
        percentLayout.setOrientation(LinearLayout.HORIZONTAL);
        secondLayout.addView(percentLayout);

        TextView percentTv = new TextView(context);
        LinearLayout.LayoutParams percentTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        percentTv.setGravity(Gravity.CENTER);
        percentTv.setLayoutParams(percentTvParams);
        percentTv.setLetterSpacing(-0.08f);
        percentTv.setTextColor(Color.rgb(155, 155, 155));
        percentTv.setText("할인율");
        percentTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        percentLayout.addView(percentTv);

        final EditText percentAgeEt = new EditText(context);
        LinearLayout.LayoutParams percentAgeEtParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        percentAgeEtParams.leftMargin = dp(4);
        percentAgeEt.setLayoutParams(percentAgeEtParams);
        percentAgeEt.setBackground(null);
        percentAgeEt.setGravity(Gravity.END);
        percentAgeEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        percentAgeEt.setLines(1);
        percentAgeEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        percentAgeEt.setHint("할인율 입력");
        percentAgeEt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        percentAgeEt.setEnabled(false);
        setCursorDrawableColor(percentAgeEt, Color.rgb(252, 132, 73));
        percentAgeEt.setText(String.valueOf(vo.getPercentAge()));
        percentLayout.addView(percentAgeEt);

        TextView percentIcTv = new TextView(context);
        LinearLayout.LayoutParams percentIcTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        percentIcTvParams.leftMargin = dp(3);
        percentIcTv.setLayoutParams(percentIcTvParams);
        percentIcTv.setGravity(Gravity.CENTER);
        percentIcTv.setLetterSpacing(-0.07f);
        percentIcTv.setTextColor(Color.rgb(81, 81, 81));
        percentIcTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        percentIcTv.setText("%");
        percentLayout.addView(percentIcTv);

        TextView infoTv = new TextView(context);
        LinearLayout.LayoutParams infoTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        infoTv.setGravity(Gravity.CENTER);
        infoTv.setLayoutParams(infoTvParams);
        infoTv.setLetterSpacing(-0.08f);
        infoTv.setTextColor(Color.rgb(155, 155, 155));
        infoTv.setText("상세정보");
        infoTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        secondLayout.addView(infoTv);

        final EditText infoEt = new EditText(context);
        LinearLayout.LayoutParams infoEtParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        infoEt.setLayoutParams(infoEtParams);
        infoEt.setBackground(null);
        infoEt.setHorizontallyScrolling(false);
        infoEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(500)});
        infoEt.setHint("상세정보 입력(500자)");
        infoEt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        setCursorDrawableColor(infoEt, Color.rgb(252, 132, 73));
        infoEt.setText(vo.getMenuInfo());
        secondLayout.addView(infoEt);

        LinearLayout btnsLayout = new LinearLayout(context);
        LinearLayout.LayoutParams btnsLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(44));
        btnsLayout.setLayoutParams(btnsLayoutParams);
        btnsLayout.setOrientation(LinearLayout.HORIZONTAL);
        btnsLayout.setGravity(Gravity.CENTER);
        menuLayout.addView(btnsLayout);

        final LinearLayout delBtnLayout = new LinearLayout(context);
        LinearLayout.LayoutParams delBtnLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        delBtnLayout.setLayoutParams(delBtnLayoutParams);
        delBtnLayout.setGravity(Gravity.CENTER);
        delBtnLayout.setBackground(context.getDrawable(R.drawable.bottom_left_round));

        TextView delTv = new TextView(context);
        LinearLayout.LayoutParams delTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        delTv.setLayoutParams(delTvParams);
        delTv.setLetterSpacing(-0.07f);
        delTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        delTv.setTypeface(Typeface.DEFAULT_BOLD);
        delTv.setTextColor(Color.WHITE);
        delTv.setText("메뉴 삭제");
        delBtnLayout.addView(delTv);

        LinearLayout addBtnLayout = new LinearLayout(context);
        LinearLayout.LayoutParams addBtnLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        addBtnLayout.setLayoutParams(addBtnLayoutParams);
        addBtnLayout.setGravity(Gravity.CENTER);
        addBtnLayout.setBackground(context.getDrawable(R.drawable.bottom_right_round));


        TextView addTv = new TextView(context);
        LinearLayout.LayoutParams addTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addTv.setLayoutParams(addTvParams);
        addTv.setLetterSpacing(-0.07f);
        addTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        addTv.setTypeface(Typeface.DEFAULT_BOLD);
        addTv.setTextColor(Color.WHITE);
        addTv.setText("메뉴 추가");
        addBtnLayout.addView(addTv);

        if (isFirst) {
            addBtnLayout.setBackground(context.getDrawable(R.drawable.bottom_round_background));
            btnsLayout.addView(addBtnLayout);
        } else {
            btnsLayout.addView(delBtnLayout);
            btnsLayout.addView(addBtnLayout);
        }

        addListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                motherLayout.addView(addMenuLayout(motherLayout, false, thisOptionIndex));
                if (!isFirst) {
                    v.setVisibility(View.GONE);
                    delBtnLayout.setBackground(context.getDrawable(R.drawable.bottom_round_background_del));
                }
            }
        };

        delListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuIndexMap.get(thisOptionIndex) == 0) {
                    Toast.makeText(context, "옵션당 1개의 메뉴가 존재해야 합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    ((LinearLayout) v.getParent().getParent().getParent()).removeView((LinearLayout) v.getParent().getParent());
                    int curIndex = menuIndexMap.get(thisOptionIndex);
                    if (curIndex > 0) {
                        menuIndexMap.put(thisOptionIndex, menuIndexMap.get(thisOptionIndex) - 1);
                    }
                }
            }
        };

        delBtnLayout.setOnClickListener(delListener);
        addBtnLayout.setOnClickListener(addListener);

        TextWatcher disPriceWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (disPriceEt.equals(((Activity) context).getCurrentFocus())) {
                    if (disPriceEt.getText().toString().length() == 0) {
                        percentAgeEt.setText(String.valueOf(0));
                    } else {
                        int price = Integer.parseInt(priceEt.getText().toString());

                        if (price != 0) {
                            int percent = (int) Math.floor(100 - ((Integer.parseInt(s.toString()) * 100) / price));
                            if (percent < 0) {
                                percent = 0;
                            }
                            percentAgeEt.setText(String.valueOf(percent));
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        TextWatcher percentWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (percentAgeEt.equals(((Activity) context).getCurrentFocus())) {
                    if (percentAgeEt.getText().toString().length() == 0) {
                        disPriceEt.setText(String.valueOf(0));
                    } else {
                        disPriceEt.setText(String.valueOf((int) Math.floor((Integer.parseInt(priceEt.getText().toString()) / 100) * (100 - Integer.parseInt(s.toString())))));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        TextWatcher priceWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    percentAgeEt.setEnabled(true);
                    disPriceEt.setEnabled(true);
                } else {
                    percentAgeEt.setEnabled(false);
                    disPriceEt.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        percentAgeEt.addTextChangedListener(percentWatcher);
        disPriceEt.addTextChangedListener(disPriceWatcher);
        priceEt.addTextChangedListener(priceWatcher);

        percentAgeEt.setEnabled(true);
        disPriceEt.setEnabled(true);

        return menuLayout;
    }

    public int dp(int dp) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return Math.round(dp * dm.density);
    }

    public static void setCursorDrawableColor(EditText editText, int color) {
        try {
            Field fCursorDrawableRes =
                    TextView.class.getDeclaredField("mCursorDrawableRes");
            fCursorDrawableRes.setAccessible(true);
            int mCursorDrawableRes = fCursorDrawableRes.getInt(editText);
            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(editText);
            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);

            Drawable[] drawables = new Drawable[2];
            Resources res = editText.getContext().getResources();
            drawables[0] = res.getDrawable(mCursorDrawableRes);
            drawables[1] = res.getDrawable(mCursorDrawableRes);
            drawables[0].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            drawables[1].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            fCursorDrawable.set(editor, drawables);
        } catch (final Throwable ignored) {
            ignored.printStackTrace();
        }
    }

    View.OnClickListener addOption = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            optionIndex++;
            LinearLayout eventLayout = ((Activity) context).findViewById(R.id.eventLayout);
            eventLayout.addView(addOptionLayout(eventLayout));
        }
    };

    public LinearLayout addMenuLayout(final LinearLayout motherLayout, final boolean isFirst, final int thisOptionIndex) {
        final int thisMenuIndex;
        if (isFirst) {
            thisMenuIndex = 0;
        } else {
            thisMenuIndex = menuIndexMap.get(thisOptionIndex) + 1;
        }

        Log.d("생성될때 메뉴인덱스", String.valueOf(thisMenuIndex));

        menuIndexMap.put(thisOptionIndex, thisMenuIndex);

        final LinearLayout menuLayout = new LinearLayout(context);
        menuLayout.setTag(String.valueOf(thisMenuIndex));
        LinearLayout.LayoutParams menuLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        menuLayoutParams.topMargin = dp(10);
        menuLayout.setBackground(context.getDrawable(R.drawable.coupon_total_background));
        menuLayout.setLayoutParams(menuLayoutParams);
        menuLayout.setOrientation(LinearLayout.VERTICAL);
        menuLayout.setPadding(0, dp(15), 0, 0);

        EditText menuNameEt = new EditText(context);
        LinearLayout.LayoutParams menuNameParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(20));
        menuLayoutParams.bottomMargin = dp(22);
        menuNameEt.setLayoutParams(menuNameParams);
        menuNameEt.setBackground(null);
        menuNameEt.setHint("메뉴명을 입력하세요");
        menuNameEt.setLetterSpacing(-0.07f);
        menuNameEt.setPadding(dp(16), 0, dp(16), 0);
        menuNameEt.setTextColor(Color.BLACK);
        menuNameEt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        setCursorDrawableColor(menuNameEt, Color.rgb(252, 132, 73));
        menuLayout.addView(menuNameEt);

        LinearLayout secondLayout = new LinearLayout(context);
        secondLayout.setId((thisOptionIndex * 10000) + thisMenuIndex);
        LinearLayout.LayoutParams secondLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        secondLayout.setLayoutParams(secondLayoutParams);
        secondLayout.setOrientation(LinearLayout.VERTICAL);
        secondLayout.setPadding(dp(16), 0, dp(16), 0);
        menuLayout.addView(secondLayout);

        LinearLayout priceLayout = new LinearLayout(context);
        LinearLayout.LayoutParams priceLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        priceLayoutParam.setMargins(0, dp(10), 0, dp(10));
        priceLayout.setLayoutParams(priceLayoutParam);
        priceLayout.setOrientation(LinearLayout.HORIZONTAL);
        secondLayout.addView(priceLayout);

        TextView priceTv = new TextView(context);
        LinearLayout.LayoutParams priceTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        priceTv.setGravity(Gravity.CENTER);
        priceTv.setLayoutParams(priceTvParams);
        priceTv.setLetterSpacing(-0.08f);
        priceTv.setTextColor(Color.rgb(155, 155, 155));
        priceTv.setText("판매가");
        priceTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        priceLayout.addView(priceTv);

        final EditText priceEt = new EditText(context);
        LinearLayout.LayoutParams priceEtParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        priceEtParams.leftMargin = dp(4);
        priceEt.setLayoutParams(priceEtParams);
        priceEt.setBackground(null);
        priceEt.setGravity(Gravity.END);
        priceEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        priceEt.setLines(1);
        priceEt.setText("");
        priceEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        priceEt.setHint("판매가(원가) 입력");
        priceEt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        setCursorDrawableColor(priceEt, Color.rgb(252, 132, 73));
        priceLayout.addView(priceEt);

        TextView priceIcTv = new TextView(context);
        LinearLayout.LayoutParams priceIcTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        priceIcTvParams.leftMargin = dp(3);
        priceIcTv.setLayoutParams(priceIcTvParams);
        priceIcTv.setGravity(Gravity.CENTER);
        priceIcTv.setLetterSpacing(-0.07f);
        priceIcTv.setTextColor(Color.rgb(81, 81, 81));
        priceIcTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        priceIcTv.setText("원");
        priceLayout.addView(priceIcTv);

        LinearLayout disPriceLayout = new LinearLayout(context);
        LinearLayout.LayoutParams disPriceLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        disPriceLayout.setLayoutParams(disPriceLayoutParams);
        disPriceLayout.setOrientation(LinearLayout.HORIZONTAL);
        secondLayout.addView(disPriceLayout);

        TextView disPriceTv = new TextView(context);
        LinearLayout.LayoutParams disPriceTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        disPriceTv.setGravity(Gravity.CENTER);
        disPriceTv.setLayoutParams(disPriceTvParams);
        disPriceTv.setLetterSpacing(-0.08f);
        disPriceTv.setTextColor(Color.rgb(155, 155, 155));
        disPriceTv.setText("할인가");
        disPriceTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        disPriceLayout.addView(disPriceTv);

        final EditText disPriceEt = new EditText(context);
        LinearLayout.LayoutParams disPriceParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        disPriceParams.leftMargin = dp(4);
        disPriceEt.setLayoutParams(disPriceParams);
        disPriceEt.setBackground(null);
        disPriceEt.setGravity(Gravity.END);
        disPriceEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        disPriceEt.setLines(1);
        disPriceEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        disPriceEt.setHint("할인된 가격 입력");
        disPriceEt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        setCursorDrawableColor(disPriceEt, Color.rgb(252, 132, 73));
        disPriceLayout.addView(disPriceEt);

        TextView disPriceIcTv = new TextView(context);
        LinearLayout.LayoutParams disPriceIcTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        disPriceIcTvParams.leftMargin = dp(3);
        disPriceIcTv.setLayoutParams(disPriceIcTvParams);
        disPriceIcTv.setGravity(Gravity.CENTER);
        disPriceIcTv.setLetterSpacing(-0.07f);
        disPriceIcTv.setTextColor(Color.rgb(81, 81, 81));
        disPriceIcTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        disPriceIcTv.setText("원");
        disPriceEt.setEnabled(false);
        disPriceLayout.addView(disPriceIcTv);

        LinearLayout percentLayout = new LinearLayout(context);
        LinearLayout.LayoutParams percentLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        percentLayoutParams.bottomMargin = dp(17);
        percentLayout.setLayoutParams(percentLayoutParams);
        percentLayout.setOrientation(LinearLayout.HORIZONTAL);
        secondLayout.addView(percentLayout);

        TextView percentTv = new TextView(context);
        LinearLayout.LayoutParams percentTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        percentTv.setGravity(Gravity.CENTER);
        percentTv.setLayoutParams(percentTvParams);
        percentTv.setLetterSpacing(-0.08f);
        percentTv.setTextColor(Color.rgb(155, 155, 155));
        percentTv.setText("할인율");
        percentTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        percentLayout.addView(percentTv);

        final EditText percentAgeEt = new EditText(context);
        LinearLayout.LayoutParams percentAgeEtParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        percentAgeEtParams.leftMargin = dp(4);
        percentAgeEt.setLayoutParams(percentAgeEtParams);
        percentAgeEt.setBackground(null);
        percentAgeEt.setGravity(Gravity.END);
        percentAgeEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        percentAgeEt.setLines(1);
        percentAgeEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        percentAgeEt.setHint("할인율 입력");
        percentAgeEt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        percentAgeEt.setEnabled(false);
        setCursorDrawableColor(percentAgeEt, Color.rgb(252, 132, 73));
        percentLayout.addView(percentAgeEt);

        TextView percentIcTv = new TextView(context);
        LinearLayout.LayoutParams percentIcTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        percentIcTvParams.leftMargin = dp(3);
        percentIcTv.setLayoutParams(percentIcTvParams);
        percentIcTv.setGravity(Gravity.CENTER);
        percentIcTv.setLetterSpacing(-0.07f);
        percentIcTv.setTextColor(Color.rgb(81, 81, 81));
        percentIcTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        percentIcTv.setText("%");
        percentLayout.addView(percentIcTv);

        LinearLayout btnsLayout = new LinearLayout(context);
        LinearLayout.LayoutParams btnsLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(44));
        btnsLayout.setLayoutParams(btnsLayoutParams);
        btnsLayout.setOrientation(LinearLayout.HORIZONTAL);
        btnsLayout.setGravity(Gravity.CENTER);
        menuLayout.addView(btnsLayout);

        TextView infoTv = new TextView(context);
        LinearLayout.LayoutParams infoTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        infoTv.setGravity(Gravity.CENTER);
        infoTv.setLayoutParams(infoTvParams);
        infoTv.setLetterSpacing(-0.08f);
        infoTv.setTextColor(Color.rgb(155, 155, 155));
        infoTv.setText("상세정보");
        infoTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        secondLayout.addView(infoTv);

        final EditText infoEt = new EditText(context);
        LinearLayout.LayoutParams infoEtParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        infoEt.setLayoutParams(infoEtParams);
        infoEt.setBackground(null);
        infoEt.setHorizontallyScrolling(false);
        infoEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(500)});
        infoEt.setHint("상세정보 입력(500자)");
        infoEt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        setCursorDrawableColor(infoEt, Color.rgb(252, 132, 73));
        secondLayout.addView(infoEt);

        final LinearLayout delBtnLayout = new LinearLayout(context);
        LinearLayout.LayoutParams delBtnLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        delBtnLayout.setLayoutParams(delBtnLayoutParams);
        delBtnLayout.setGravity(Gravity.CENTER);
        delBtnLayout.setBackground(context.getDrawable(R.drawable.bottom_left_round));

        TextView delTv = new TextView(context);
        LinearLayout.LayoutParams delTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        delTv.setLayoutParams(delTvParams);
        delTv.setLetterSpacing(-0.07f);
        delTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        delTv.setTypeface(Typeface.DEFAULT_BOLD);
        delTv.setTextColor(Color.WHITE);
        delTv.setText("메뉴 삭제");
        delBtnLayout.addView(delTv);

        LinearLayout addBtnLayout = new LinearLayout(context);
        LinearLayout.LayoutParams addBtnLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        addBtnLayout.setLayoutParams(addBtnLayoutParams);
        addBtnLayout.setGravity(Gravity.CENTER);
        addBtnLayout.setBackground(context.getDrawable(R.drawable.bottom_right_round));


        TextView addTv = new TextView(context);
        LinearLayout.LayoutParams addTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addTv.setLayoutParams(addTvParams);
        addTv.setLetterSpacing(-0.07f);
        addTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        addTv.setTypeface(Typeface.DEFAULT_BOLD);
        addTv.setTextColor(Color.WHITE);
        addTv.setText("메뉴 추가");
        addBtnLayout.addView(addTv);

        if (isFirst) {
            addBtnLayout.setBackground(context.getDrawable(R.drawable.bottom_round_background));
            btnsLayout.addView(addBtnLayout);
        } else {
            btnsLayout.addView(delBtnLayout);
            btnsLayout.addView(addBtnLayout);
        }

        addListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                motherLayout.addView(addMenuLayout(motherLayout, false, thisOptionIndex));
                if (!isFirst) {
                    v.setVisibility(View.GONE);
                    delBtnLayout.setBackground(context.getDrawable(R.drawable.bottom_round_background_del));
                }
            }
        };

        delListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuIndexMap.get(thisOptionIndex) == 0) {
                    Toast.makeText(context, "옵션당 1개의 메뉴가 존재해야 합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    ((LinearLayout) v.getParent().getParent().getParent()).removeView((LinearLayout) v.getParent().getParent());
                    int curIndex = menuIndexMap.get(thisOptionIndex);
                    if (curIndex > 0) {
                        menuIndexMap.put(thisOptionIndex, menuIndexMap.get(thisOptionIndex) - 1);
                    }
                }
            }
        };

        delBtnLayout.setOnClickListener(delListener);
        addBtnLayout.setOnClickListener(addListener);

        TextWatcher disPriceWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (disPriceEt.equals(((Activity) context).getCurrentFocus())) {
                    if (disPriceEt.getText().toString().length() == 0) {
                        percentAgeEt.setText(String.valueOf(0));
                    } else {
                        int price = Integer.parseInt(priceEt.getText().toString());

                        if (price != 0) {
                            int percent = (int) Math.floor(100 - ((Integer.parseInt(s.toString()) * 100) / price));
                            if (percent < 0) {
                                percent = 0;
                            }
                            percentAgeEt.setText(String.valueOf(percent));
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        TextWatcher percentWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (percentAgeEt.equals(((Activity) context).getCurrentFocus())) {
                    if (percentAgeEt.getText().toString().length() == 0) {
                        disPriceEt.setText(String.valueOf(0));
                    } else {
                        disPriceEt.setText(String.valueOf((int) Math.floor((Integer.parseInt(priceEt.getText().toString()) / 100) * (100 - Integer.parseInt(s.toString())))));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        TextWatcher priceWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    percentAgeEt.setEnabled(true);
                    disPriceEt.setEnabled(true);
                } else {
                    percentAgeEt.setEnabled(false);
                    disPriceEt.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        percentAgeEt.addTextChangedListener(percentWatcher);
        disPriceEt.addTextChangedListener(disPriceWatcher);
        priceEt.addTextChangedListener(priceWatcher);

        return menuLayout;
    }

    public LinearLayout addOptionLayout(final LinearLayout mother) {
        final int thisOptionIndex = optionIndex;
        optionIndexList.add(optionIndex);

        LinearLayout optionLayout = new LinearLayout(context);
        LinearLayout.LayoutParams optionLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        optionLayoutParams.setMargins(dp(40), dp(20), dp(40), 0);
        optionLayout.setLayoutParams(optionLayoutParams);
        optionLayout.setBackground(context.getDrawable(R.drawable.menu_select_background));
        optionLayout.setElevation(dp(4));
        optionLayout.setOrientation(LinearLayout.VERTICAL);
        optionLayout.setPadding(dp(20), dp(20), dp(20), dp(20));

        LinearLayout optionNameLayout = new LinearLayout(context);
        LinearLayout.LayoutParams optionNameLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        optionNameLayout.setLayoutParams(optionNameLayoutParams);
        optionNameLayout.setGravity(Gravity.CENTER_VERTICAL);
        optionNameLayout.setOrientation(LinearLayout.HORIZONTAL);
        optionLayout.addView(optionNameLayout);

        EditText optionNameEt = new EditText(context);
        optionNameEt.setId(thisOptionIndex);
        LinearLayout.LayoutParams optionNameEtParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        optionNameEt.setLayoutParams(optionNameEtParams);
        optionNameEt.setBackground(null);
        optionNameEt.setHint("옵션 이름을 입력하세요");
        optionNameEt.setLetterSpacing(-0.07f);
        optionNameEt.setLines(1);
        optionNameEt.setTextColor(Color.BLACK);
        optionNameEt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        setCursorDrawableColor(optionNameEt, Color.rgb(252, 132, 73));
        optionNameLayout.addView(optionNameEt);

        ImageView optionDelBtn = new ImageView(context);
        LinearLayout.LayoutParams optionDelBtnParams = new LinearLayout.LayoutParams(dp(16), dp(17));
        optionDelBtnParams.leftMargin = dp(8);
        optionDelBtn.setLayoutParams(optionDelBtnParams);
        optionDelBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_delete));
        optionDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mother.removeView((View) v.getParent().getParent());
                if (optionIndex > 0) {
                    int listIndex = 0;
                    for(int i=0;i<optionIndexList.size();i++){
                        if(thisOptionIndex == optionIndexList.get(i)){
                            listIndex = i;
                            break;
                        }
                    }
                    optionIndexList.remove(listIndex);
                    optionIndex--;
                }
            }
        });
        optionNameLayout.addView(optionDelBtn);

        optionLayout.addView(addMenuLayout(optionLayout, true, thisOptionIndex));

        return optionLayout;
    }

    private EventCpVO allDataMerge() {
        String serial = mainVO.getEventSerial();
        EventCpVO vo = new EventCpVO();
        vo.setEventName(((EditText) ((Activity) context).findViewById(R.id.eventNameTv)).getText().toString());

        List<EventCpOptionVO> optionList = new ArrayList<>();
        for (int i = 0; i < optionIndexList.size(); i++) {
            optionList.add(optionDataMerge(optionIndexList.get(i), serial));
        }

        Log.d("옵션리스트 들", optionList.toString());

        vo.setCpSeq(mainVO.getCpSeq());
        vo.setOptionList(optionList);
        vo.setEventSerial(serial);
        return vo;
    }

    private EventCpOptionVO optionDataMerge(int position, String evSerial) {
        Log.d("옵션번호", String.valueOf(position));
        String serial = makeSerial("op");
        EventCpOptionVO vo = new EventCpOptionVO();

        vo.setOptionName(((EditText) ((Activity) context).findViewById(position)).getText().toString());

        List<EventCpMenuVO> menuList = new ArrayList<>();
        for (int i = 0; i <= menuIndexMap.get(position); i++) {
            menuList.add(menuDataMerge(position, i, serial));
        }
        vo.setMenuList(menuList);
        vo.setOptionSerial(serial);
        vo.setEventSerial(evSerial);

        return vo;
    }

    private EventCpMenuVO menuDataMerge(int optionNum, int position, String opSerial) {
        try {
            LinearLayout secondLayout = ((Activity)context).findViewById((optionNum * 10000) + position);
            String menuName = ((EditText)((LinearLayout)secondLayout.getParent()).getChildAt(0)).getText().toString();
            int price = Integer.parseInt(((EditText)((LinearLayout)secondLayout.getChildAt(0)).getChildAt(1)).getText().toString());
            int disPrice = Integer.parseInt(((EditText)((LinearLayout)secondLayout.getChildAt(1)).getChildAt(1)).getText().toString());
            int percentAge = Integer.parseInt(((EditText)((LinearLayout)secondLayout.getChildAt(2)).getChildAt(1)).getText().toString());
            String info = ((EditText)secondLayout.getChildAt(4)).getText().toString();

            EventCpMenuVO vo = new EventCpMenuVO();
            vo.setMenuName(menuName);
            vo.setPrice(price);
            vo.setDisPrice(disPrice);
            vo.setPercentAge(percentAge);
            vo.setOptionSerial(opSerial);
            vo.setMenuInfo(info);
            return vo;
        } catch (Exception e) {
            Toast.makeText(context, "가격정보를 정확하게 입력해주세요.", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private String makeSerial(String numOption) {
        MakeCertNumber certNumber = new MakeCertNumber();
        return numOption + mainVO.getCpSeq() + certNumber.numberGen(10, 1);
    }

    View.OnClickListener saveEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final EventCpVO vo = allDataMerge();
            if (checkedString(vo)) {
                if (optionIndex > 0) {
                    Gson gson = new Gson();
                    String data = gson.toJson(vo);

                    Call<Integer> call = retroSingleTon.getInstance().getRetroInterface(context.getApplicationContext()).updateEvent(data, isMain);
                    call.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    int result = response.body();

                                    Log.d("수정 Result", String.valueOf(result));

                                    if (result != 0) {
                                        Toast.makeText(context, "이벤트 수정 완료!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(context, BaobabCpEvent.class);
                                        intent.putExtra("cpSeq", vo.getCpSeq());
                                        context.startActivity(intent);
                                        ((Activity)context).finish();
                                    } else {
                                        Toast.makeText(context, "이벤트 수정에 실패하였습니다. 다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
                                        ((Activity) context).onBackPressed();
                                    }
                                } else {
                                    Log.d("updateEvent ::", "response 내용없음");
                                    Toast.makeText(context, "이벤트 수정에 실패하였습니다. 다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
                                    ((Activity) context).onBackPressed();
                                }
                            } else {
                                Log.d("updateEvent ::", "서버로그 확인 필요");
                                Toast.makeText(context, "이벤트 수정에 실패하였습니다. 다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
                                ((Activity) context).onBackPressed();
                            }
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            Log.d("updateEvent ::", t.getLocalizedMessage());
                            Toast.makeText(context, "이벤트 수정에 실패하였습니다. 다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
                            ((Activity) context).onBackPressed();
                        }
                    });
                } else {
                    Toast.makeText(context, "옵션을 추가해 주십시오.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "데이터를 정확히 입력해 주십시오.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public boolean checkedString(EventCpVO vo) {
        try {
            boolean isChecked = true;
            if (vo.getEventName().length() <= 0) {
                Toast.makeText(context, "이벤트명을 입력하세요.", Toast.LENGTH_SHORT).show();
                isChecked = false;
            }

            for (int i = 0; i < vo.getOptionList().size(); i++) {
                if (vo.getOptionList().get(i).getOptionName().length() <= 0) {
                    isChecked = false;
                    Toast.makeText(context, "입력하지 않은 옵션 이름이 있습니다.", Toast.LENGTH_SHORT).show();
                    break;
                }

                for (int j = 0; j < vo.getOptionList().get(i).getMenuList().size(); j++) {
                    EventCpMenuVO menuVO = vo.getOptionList().get(i).getMenuList().get(j);
                    if (menuVO == null) {
                        isChecked = false;
                        Toast.makeText(context, "데이터를 정확히 입력해 주십시오. (" + (i+1) + "번 옵션의 " + (j+1) + "번째" + ")", Toast.LENGTH_SHORT).show();
                        break;
                    } else {
                        if (menuVO.getMenuName().length() <= 0) {
                            isChecked = false;
                            Toast.makeText(context, "입력하지 않은 메뉴 이름이 있습니다.", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
            }

            return isChecked;
        } catch (Exception e) {
            Toast.makeText(context, "데이터를 정확히 입력해 주십시오.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}