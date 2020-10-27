package com.baobab.user.baobabflyer.server.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baobab.user.baobabflyer.BaobabUserTicketList;
import com.baobab.user.baobabflyer.R;
import com.baobab.user.baobabflyer.server.util.ImageUtil;
import com.baobab.user.baobabflyer.server.util.UserTicketDetailDialog;
import com.baobab.user.baobabflyer.server.vo.UserTicketVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.List;

public class UserCouponListAdapter extends RecyclerView.Adapter<UserCouponListAdapter.ItemViewHolder>{

    List<UserTicketVO> list;
    Context context;
    String selectedSerial;

    public UserCouponListAdapter(List<UserTicketVO> list, Context context, String selectedSerial) {
        this.list = list;
        this.context = context;
        this.selectedSerial = selectedSerial;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.coupon_user_list, viewGroup, false);
        return new UserCouponListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder itemViewHolder, int i) {
        final UserTicketVO vo = list.get(i);

        itemViewHolder.ticketTitle.setText(vo.getTicketTitle());
        itemViewHolder.cpName.setText(vo.getCpName());
        itemViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserTicketDetailDialog dialog = UserTicketDetailDialog.newInstance(vo, context);
                dialog.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light);
                dialog.show(((BaobabUserTicketList)(context)).getSupportFragmentManager(), "");
            }
        });
        final ImageUtil util = new ImageUtil();
        Glide.with(context.getApplicationContext()).load(vo.getImgUrl()).asBitmap().override(150, 150).centerCrop().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                itemViewHolder.cpImg.setImageBitmap(util.getCircularBitmap(resource));
            }
        });

        if(!selectedSerial.equals("") && vo.getTicketSerial().equals(selectedSerial)){
            itemViewHolder.layout.performClick();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        LinearLayout layout;
        TextView ticketTitle;
        TextView cpName;
        ImageView cpImg;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout);
            ticketTitle = itemView.findViewById(R.id.ticketTitle);
            cpName = itemView.findViewById(R.id.cpName);
            cpImg = itemView.findViewById(R.id.cpImg);
        }
    }
}
