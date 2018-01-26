package com.mihir.assinment.a500px;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mihir.assinment.a500px.data.PxPhoto;
import com.mihir.assinment.a500px.di.ApplicationComponent;
import com.mihir.assinment.a500px.di.DaggerApplicationComponent;
import com.mihir.assinment.a500px.di.PxServiceModule;
import com.mihir.assinment.a500px.service.SearchResults;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static android.os.Build.VERSION.SDK_INT;

public class FullscreenActivity extends AppCompatActivity {
    private static final String TAG = "fullscreen activity";
    int position;
    String Term;
    @BindView(R.id.pager)
    ViewPager pager;
    List<PxPhoto> photos;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        Intent i = getIntent();
        position = i.getIntExtra("position", 0);
        Term = i.getStringExtra("Term");
        Log.d("Position", position + "");
//        SearchResults results = (SearchResults) i.getSerializableExtra("Results");
//        Toast.makeText(getApplicationContext(), position + "", Toast.LENGTH_LONG).show();
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT > 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (SDK_INT > 19) {

                AppCompatActivity activity = FullscreenActivity.this;
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(activity.getResources().getColor(R.color.cardview_dark_background));
                // window.setStatusBarColor(Color.parseColor("#55565746"));
            }
        }
        loadFirstPage();
//        pager.setAdapter(new ImageAdapter(getApplicationContext(), photos));


    }

    private ApplicationComponent getComponent() {
        return DaggerApplicationComponent.builder()
                .pxServiceModule(new PxServiceModule(this))
                .build();
    }

    public void failure(String error) {
        Toast.makeText(this, "Failed to load photo list: " + error, Toast.LENGTH_LONG).show();
    }

    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ");
        getComponent().repository().getItems(Term)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SearchResults>() {
                    @Override
                    public void call(SearchResults searchResults) {
                        success(searchResults);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        failure(throwable.getMessage());
                    }
                });
    }

    public void success(SearchResults results) {
        ImageAdapter adapter = new ImageAdapter(this, results.photos);
//        adapter.addAll(results.photos);
        pager.setAdapter(adapter);
        pager.setCurrentItem(position);
    }
    @Override
    public void onBackPressed() {


        Intent intent = new Intent();
        intent.putExtra("position", pager.getCurrentItem());
        setResult(Activity.RESULT_OK, intent);
        this.finishActivity(1);
        // this.finishActivity(0);//finishing activity
        super.onBackPressed();
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

        public void addAll(List<PxPhoto> mcList) {
            for (PxPhoto mc : mcList) {
                add(mc);
            }
        }

        public void add(PxPhoto mc) {
            photos.add(mc);
            notifyAll();
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
