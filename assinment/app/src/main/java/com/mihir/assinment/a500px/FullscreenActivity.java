package com.mihir.assinment.a500px;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mihir.assinment.a500px.data.PxPhoto;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FullscreenActivity extends AppCompatActivity {
    int position;
    @BindView(R.id.pager)
    ViewPager pager;
    List<PxPhoto> photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        position = b.getInt("position", 0);
        photos = (List<PxPhoto>) b.getSerializable("photo");
        Log.d("Position", position + "");
        Toast.makeText(getApplicationContext(), position + "", Toast.LENGTH_LONG).show();
        ButterKnife.bind(this);
        pager.setAdapter(new ImageAdapter(getApplicationContext(), photos));
        pager.setCurrentItem(position);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("MESSAGE", position);
        setResult(2, intent);
        finish();//finishing activity
    }

    private static class ImageAdapter extends PagerAdapter {


        private static final int ITEM = 0;
        private static final int LOADING = 1;
        private final Picasso mPicasso;
        private LayoutInflater inflater;
        private List<PxPhoto> photos;
        private boolean isLoadingAdded = false;


        ImageAdapter(Context context, List<PxPhoto> photos) {
            inflater = LayoutInflater.from(context);
            this.photos = photos;
            mPicasso = Picasso.with(context);

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
            assert imageLayout != null;
            ImageView imageView = imageLayout.findViewById(R.id.image);
            final ProgressBar spinner = imageLayout.findViewById(R.id.loading);
            TextView name = imageLayout.findViewById(R.id.pager_name);
            TextView desc = imageLayout.findViewById(R.id.pager_desc);


            PxPhoto photo = photos.get(position);
            final String url = photo.imageUrl;
            // photos_view.txt_name.setText(photo.name);
            mPicasso.load(photo.imageUrl)
//                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
            name.setText(photo.name);
            desc.setText(photo.description);
            spinner.setVisibility(View.GONE);
            view.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}
