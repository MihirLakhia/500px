package com.mihir.assinment.a500px;

import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.mihir.assinment.a500px.data.PxPhoto;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

class PxListAdapter extends RecyclerView.Adapter<PxListAdapter.PxViewHolder> {
    private final LayoutInflater mInflater;
    private final Picasso mPicasso;
    private final List<PxPhoto> mPhotos;

    public PxListAdapter(Context context, List<PxPhoto> photos) {
        mInflater = LayoutInflater.from(context);
        mPicasso = Picasso.with(context);
        mPhotos = photos;
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    @Override
    public PxViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        final View view = mInflater.inflate(R.layout.px_frame, parent, false);
        return new PxViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PxViewHolder holder, int position) {
        final PxPhoto pxPhoto = mPhotos.get(position);
        mPicasso.load(pxPhoto.imageUrl)
//                    .placeholder(R.drawable.placeholder)
                .into(holder.image);
        holder.image.setContentDescription(pxPhoto.description);
        holder.txt_Description.setText(pxPhoto.description);
        holder.txt_name.setText(pxPhoto.name);
        holder.txt_date.setText(getdate(pxPhoto.date));
        holder.txt_vote.setText(pxPhoto.vote);
    }

    public static class PxViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_Description,txt_name,txt_date,txt_vote;
        public ImageView image;
        public PxViewHolder(View itemView) {
            super(itemView);
             image = (ImageView) itemView.findViewById(R.id.image);
             txt_Description  = (TextView) itemView.findViewById(R.id.txt_desc);
             txt_name  = (TextView) itemView.findViewById(R.id.txt_Name);
             txt_date  = (TextView) itemView.findViewById(R.id.txt_Date);
             txt_vote  = (TextView) itemView.findViewById(R.id.txt_vote);
                image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }
    }
    final public String getdate(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value = null;
        try {
            value = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM HH:mm");
        dateFormatter.setTimeZone(TimeZone.getDefault());
        String dt = dateFormatter.format(value);

        return dt;
    }
}
