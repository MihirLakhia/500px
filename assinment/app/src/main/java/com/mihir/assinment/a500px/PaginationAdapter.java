package com.mihir.assinment.a500px;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.Contacts;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mihir.assinment.a500px.data.PxPhoto;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Suleiman on 19/10/16.
 */

public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<PxPhoto> photos;
    private Context context;
    private final Picasso mPicasso;
    private boolean isLoadingAdded = false;

    public PaginationAdapter(Context context) {
        this.context = context;
        this.photos = new ArrayList<>();
        mPicasso = Picasso.with(context);
    }

    public List<PxPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PxPhoto> photos) {
        this.photos = photos;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.px_frame, parent, false);
        viewHolder = new photos_view(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        PxPhoto photos = this.photos.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                photos_view photos_view = (photos_view) holder;
               final String url =photos.imageUrl;
                photos_view.txt_name.setText(photos.name);
                mPicasso.load(photos.imageUrl)
//                    .placeholder(R.drawable.placeholder)
                        .into(photos_view.image);
                photos_view.image.setContentDescription(photos.imageUrl);
                photos_view.txt_Description.setText(photos.description);
                photos_view.txt_name.setText(photos.name);
                photos_view.txt_date.setText(getdate(photos.date));
                photos_view.txt_vote.setText(photos.vote);
                photos_view.image.setLongClickable(true);
                photos_view.image.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Toast.makeText(context,"Downloading ",Toast.LENGTH_SHORT).show();
                        Log.d("Image url---------",url+"");
                        new DownloadFromURL().execute(url);
                        return false;
                    }
                });
                photos_view.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                break;
            case LOADING:
//                Do nothing
                break;
        }

    }

    @Override
    public int getItemCount() {
        return photos == null ? 0 : photos.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == photos.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(PxPhoto mc) {
        photos.add(mc);
        notifyItemInserted(photos.size() - 1);
    }

    public void addAll(List<PxPhoto> mcList) {
        for (PxPhoto mc : mcList) {
            add(mc);
        }
    }

    public void remove(PxPhoto city) {
        int position = photos.indexOf(city);
        if (position > -1) {
            photos.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new PxPhoto());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = photos.size() - 1;
        PxPhoto item = getItem(position);

        if (item != null) {
            photos.remove(position);
            notifyItemRemoved(position);
        }
    }

    public PxPhoto getItem(int position) {
        return photos.get(position);
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class photos_view extends RecyclerView.ViewHolder {
        public TextView txt_Description,txt_name,txt_date,txt_vote;
        public ImageView image;

        public photos_view(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            txt_Description  = (TextView) itemView.findViewById(R.id.txt_desc);
            txt_name  = (TextView) itemView.findViewById(R.id.txt_Name);
            txt_date  = (TextView) itemView.findViewById(R.id.txt_Date);
            txt_vote  = (TextView) itemView.findViewById(R.id.txt_vote);
        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

    final public String getdate(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value = null;
        if(dateString.isEmpty())
        {
            return "null";
        }
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


    class DownloadFromURL extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;
        File file;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Download Data...");
            progressDialog.setCancelable(false);
            progressDialog.show();


        }

        @Override
        protected String doInBackground(String... fileUrl) {
            int count;
            try {

                URL url = new URL(fileUrl[0]);
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"500px");
                  f.mkdirs();
                file = new File(f,"500px_img_"+timeStamp+".jpg");
              //  file = new File(root.getAbsolutePath()+"/500px_img_"+timeStamp+".jpg");
                InputStream in = new BufferedInputStream(url.openStream());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int n = 0;
                while (-1!=(n=in.read(buf)))
                {
                    out.write(buf, 0, n);
                }

                byte[] response = out.toByteArray();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(response);

                fos.close();
                out.close();
                in.close();




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
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            Toast.makeText(context,"Downloaded "+file.getPath(),Toast.LENGTH_LONG).show();
            Log.d("Image Path",file.getPath()+file.getTotalSpace());
            Log.d("Image url",file_url+"");

        }
    }
}
