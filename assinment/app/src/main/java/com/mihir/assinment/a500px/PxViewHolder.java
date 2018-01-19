package com.mihir.assinment.a500px;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PxViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.image)
    public ImageView image;
    @BindView(R.id.txt_desc)
    public TextView text;
    public PxViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
