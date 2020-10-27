package com.baobab.user.baobabflyer.server.util;

import android.app.Dialog;
import androidx.fragment.app.DialogFragment;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.adapter.MenuDetailOptionListAdapter;
import com.baobab.user.baobabflyer.server.vo.MenuVO;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class MenuDetailDialog extends DialogFragment {

    List<MenuVO> menuList;

    public static MenuDetailDialog newInstance(String menuData) {
        Bundle bundle = new Bundle();
        bundle.putString("menuData", String.valueOf(menuData));

        MenuDetailDialog fragment = new MenuDetailDialog();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Gson gson = new Gson();

        if (getArguments() != null) {
            menuList = gson.fromJson(getArguments().getString("menuData"), new TypeToken<List<MenuVO>>(){}.getType());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.FullscreenDialog);
        View view = getActivity().getLayoutInflater().inflate(R.layout.activity_main_popup, null);

        MenuVO settingVo = makeSettingVo(menuList);

        ImageView imageView = view.findViewById(R.id.menuImg);
        if(settingVo.getMenu_img().startsWith("http")){
            Glide.with(view.getContext()).load(settingVo.getMenu_img()).centerCrop().into(imageView);
        }else {
            imageView.setVisibility(View.GONE);
        }

        ((TextView)view.findViewById(R.id.menuName)).setText(settingVo.getMenu_name());
        ((TextView)view.findViewById(R.id.menuIntro)).setText(settingVo.getMenu_intro());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        MenuDetailOptionListAdapter adapter = new MenuDetailOptionListAdapter(menuList);

        recyclerView.removeAllViewsInLayout();
        recyclerView.setAdapter(adapter);

        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    private MenuVO makeSettingVo(List<MenuVO> list){
        MenuVO vo = new MenuVO();

        vo.setMenu_name(list.get(0).getMenu_name());
        for(int i=0;i<list.size();i++){
            if(list.get(i).getMenu_img().startsWith("http")){
                vo.setMenu_img(list.get(i).getMenu_img());
                break;
            }else {
                vo.setMenu_img("이미지없음");
            }
        }
        for(int i=0;i<list.size();i++){
            if(stringCheck(list.get(i).getMenu_intro())){
                vo.setMenu_intro(list.get(i).getMenu_intro());
                break;
            }
        }

        return vo;
    }

    private boolean stringCheck(String str){
        if(str == null || str.equals("")){
            return false;
        }else{
            return true;
        }
    }

    private void dismissDialog() {
        this.dismiss();
    }
}
