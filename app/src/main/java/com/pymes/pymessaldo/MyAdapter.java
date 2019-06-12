package com.pymes.pymessaldo;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<ListData> listData;
    private Context context;

    public MyAdapter(List<ListData> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.data_list, viewGroup, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final ListData data = listData.get(i);

        myViewHolder.tvDescr.setText(data.getDescr());
        myViewHolder.tvFecha.setText(data.getFecha());

        myViewHolder.linearLayout_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentInfo = new Intent (context, ItemInfo.class);

                intentInfo.putExtra("INGRESODATA", data.getIngreso());
                intentInfo.putExtra("GASTODATA", data.getGasto());
                intentInfo.putExtra("ID", data.getId());

                context.startActivity(intentInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvDescr;
        public TextView tvFecha;
        public LinearLayout linearLayout_data;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDescr = itemView.findViewById(R.id.tvListDescr);
            tvFecha = itemView.findViewById(R.id.tvListFecha);
            linearLayout_data = itemView.findViewById(R.id.linearLayout_data);
        }
    }


}
