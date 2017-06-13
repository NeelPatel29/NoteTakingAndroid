package com.example.neel.notetakingandroid;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by NEEL
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private List<NotesPojo> items;
    private Context context;

    public CustomAdapter(Context context, List<NotesPojo> items) {
        this.items = items;
        this.context = context;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvNote, tvLocation, tvLat, tvLon;
        ImageView img;


        public MyViewHolder(View view) {
            super(view);


            tvNote = (TextView) view.findViewById(R.id.textViewNote);
            tvLocation = (TextView) view.findViewById(R.id.textViewLocation);
            tvLat = (TextView) view.findViewById(R.id.textViewLat);
            tvLon = (TextView) view.findViewById(R.id.textViewLon);
            img = (ImageView) view.findViewById(R.id.imageView2);

        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_list, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        NotesPojo np = items.get(position);

        holder.tvNote.setText(np.getNote());
        holder.tvLocation.setText(np.getLocation());
        holder.tvLat.setText(np.getLat());
        holder.tvLon.setText(np.getLon());
        holder.img.setImageBitmap(BitmapFactory.decodeFile(np.getImage()));

    }
    @Override
    public int getItemCount() {
        return items.size();
    }


}
