package com.pymes.pymessaldo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private String[] dataSet;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public MyViewHolder(TextView tv) {
            super(tv);
            textView = tv;
        }
    }

    public MyAdapter (String[] myDataSet) {
        dataSet = myDataSet;
    }

    // Crear nueva vista
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder (ViewGroup parent, int viewtype) {
        // Crear nueva vista
        TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_saldos, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Reemplazar el contenido de la vista
    @Override
    public void onBindViewHolder (MyViewHolder holder, int position){
        holder.textView.setText(dataSet[position]);
    }

    // Retornar el tamanyo del dataset
    @Override
    public int getItemCount () {
        return dataSet.length;
    }
}
