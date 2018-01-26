package com.mihir.assinment.a500px;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mihir.assinment.a500px.data.PxPhoto;
import com.mihir.assinment.a500px.di.ApplicationComponent;
import com.mihir.assinment.a500px.di.DaggerApplicationComponent;
import com.mihir.assinment.a500px.di.PxServiceModule;
import com.mihir.assinment.a500px.service.SearchResults;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static android.os.Build.VERSION.SDK_INT;

public class MainActivity extends AppCompatActivity {
    public static final int progressType = 0;
    private static final int NUM_COLUMNS = 2;
    private static final String TAG = "Main Activity Error";
    private static final int PAGE_START = 0;
    public ProgressDialog progressDialog;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.px_list)
    RecyclerView mPxListView;
    PaginationAdapter adapter;
    List<PxPhoto> photos;
    String Term = "500px";
    SearchResults results;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 3;
    private int currentPage = PAGE_START;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        this.toolbar.setTitle("");
        if (SDK_INT > 19) {

            AppCompatActivity activity = MainActivity.this;
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
            // window.setStatusBarColor(Color.parseColor("#55565746"));
        }
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mPxListView.setClickable(true);
        mPxListView.setHasFixedSize(false);
        GridLayoutManager ly = new GridLayoutManager(this, 1,
                GridLayoutManager.VERTICAL, false);
        mPxListView.setLayoutManager(ly);
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                0);


            }
        }
        mPxListView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ImageView imageView = view.findViewById(R.id.image);
                String url = imageView.getContentDescription().toString();
                Toast.makeText(MainActivity.this,"Url = "+url,Toast.LENGTH_LONG).show();
                //new DownloadFromURL().execute(url);
                return false;
            }
        });
        photos = new ArrayList<>();

        adapter = new PaginationAdapter(this, Term);

        mPxListView.setItemAnimator(new DefaultItemAnimator());

        mPxListView.setAdapter(adapter);

        mPxListView.addOnScrollListener(new PaginationScrollListener(ly) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        // mocking network delay for API call
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadFirstPage();
            }
        }, 1000);

        mPxListView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int position = rv.getChildAdapterPosition(child);
                    Intent i = new Intent(getApplicationContext(), FullscreenActivity.class);
                    i.putExtra("Term", Term);
                    i.putExtra("position", position);
//Bundle b = new Bundle();
//b.putSerializable("Results", (Serializable) results);
                    startActivityForResult(i, 1);

                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {


            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        //---
    }

    public void success(SearchResults results) {
        adapter = new PaginationAdapter(this, Term);
        adapter.addAll(results.photos);
        mPxListView.setAdapter(adapter);
    }

    public void failure(String error) {
        Toast.makeText(this, "Failed to load photo list: " + error, Toast.LENGTH_LONG).show();
    }

    private ApplicationComponent getComponent() {
        return DaggerApplicationComponent.builder()
                .pxServiceModule(new PxServiceModule(this))
                .build();
    }




    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ");
        getComponent().repository().getItems(Term)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SearchResults>() {
                    @Override
                    public void call(SearchResults searchResults) {
                        results = searchResults;
                        success(searchResults);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        failure(throwable.getMessage());
                    }
                });
        if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
        else isLastPage = true;

    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);
        getComponent().repository().getItems(Term)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SearchResults>() {
                    @Override
                    public void call(SearchResults searchResults) {
                        //success(searchResults);
                        results = searchResults;
                        adapter.removeLoadingFooter();
                        adapter.addAll(searchResults.photos);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        failure(throwable.getMessage());
                    }
                });

        //List<Photos> photos = Photos.createMovies(adapter.getItemCount());


        isLoading = false;

       // adapter.addAll(photos);

        if (currentPage != TOTAL_PAGES+1) adapter.addLoadingFooter();
        else isLastPage = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

            getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_registration) {
              Search();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void Search() {

        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Photo type")
                .setView(R.layout.dialog_term);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Dialog f = (Dialog) dialog;
                EditText description;
                description = f.findViewById(R.id.et_desc);

                Term = (description.getText().toString());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadFirstPage();
                    }
                }, 1000);
            }
        });

        alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                int position = data.getIntExtra("position", 0);
                mPxListView.scrollToPosition(position);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

   class DownloadFromURL extends AsyncTask<String, String, String> {
      private ProgressDialog progressDialog;

      @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Send Data...");
            progressDialog.setCancelable(false);
            progressDialog.show();


        }

        @Override
        protected String doInBackground(String... fileUrl) {
            int count;
            try {
                URL url = new URL(fileUrl[0]);
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                // show progress bar 0-100%
                int fileLength = urlConnection.getContentLength();
                InputStream inputStream = new BufferedInputStream(url.openStream(), 8192);
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                OutputStream outputStream = new FileOutputStream("/sdcard/500px_img_"+timeStamp+".jpg");

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = inputStream.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / fileLength));
                    outputStream.write(data, 0, count);
                }
                // flushing output
                outputStream.flush();
                // closing streams
                outputStream.close();
                inputStream.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }

        // progress bar Updating

        protected void onProgressUpdate(String... progress) {
            // progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String file_url) {
            progressDialog.dismiss();
            String imagePath = Environment.getExternalStorageDirectory().toString() + "/downloadedfile.jpg";
           // imageView.setImageDrawable(Drawable.createFromPath(imagePath));
        }
    }
}