package com.mihir.assinment.a500px;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.mihir.assinment.a500px.di.ApplicationComponent;
import com.mihir.assinment.a500px.di.DaggerApplicationComponent;
import com.mihir.assinment.a500px.di.PxServiceModule;
import com.mihir.assinment.a500px.service.SearchResults;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static android.os.Build.VERSION.SDK_INT;

public class MainActivity extends AppCompatActivity {
    private static final int NUM_COLUMNS = 2;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.px_list)
    RecyclerView mPxListView;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (SDK_INT > 19) {

            AppCompatActivity activity = MainActivity.this;
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
            // window.setStatusBarColor(Color.parseColor("#55565746"));
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mPxListView.setClickable(true);
        mPxListView.setHasFixedSize(false);
        mPxListView.setLayoutManager(new GridLayoutManager(this, 1,
                GridLayoutManager.VERTICAL, false));
//        mPxListView.setLayoutManager(new GridLayoutManager(this, NUM_COLUMNS,
//                GridLayoutManager.VERTICAL, false));
//        mPxListView.setLayoutManager(new StaggeredGridLayoutManager(2,
//                StaggeredGridLayoutManager.VERTICAL));

        getComponent().repository().getItems("fresh_today")
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
        mPxListView.setAdapter(new PxListAdapter(this, results.photos));
    }

    public void failure(String error) {
        Toast.makeText(this, "Failed to load photo list: " + error, Toast.LENGTH_LONG).show();
    }

    private ApplicationComponent getComponent() {
        return DaggerApplicationComponent.builder()
                .pxServiceModule(new PxServiceModule(this))
                .build();
    }
}