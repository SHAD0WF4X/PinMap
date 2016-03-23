package robin.pinmapapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ListRowViewHolder> {
    private ArrayList<Transaction> listItems = new ArrayList<>();
    private Context c;



    public TransactionAdapter(ArrayList<Transaction> items, Context c){
        listItems = items;
        this.c = c;
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ListRowViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvDescription, tvBedrag, tvDateTime;
        public View container;

        public ListRowViewHolder(View view) {
            super(view);
            container = view.findViewById(R.id.container);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvDescription = (TextView) view.findViewById(R.id.tvDescription);
            tvBedrag = (TextView) view.findViewById(R.id.tvBedrag);
            tvDateTime = (TextView) view.findViewById(R.id.tvDateTime);
        }
    }



    @Override
    public TransactionAdapter.ListRowViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_transaction, null);
        final ListRowViewHolder holder = new ListRowViewHolder(v);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        Intent i = new Intent(c, TransactionActivity.class);
        i.putExtra("transaction", listItems.get(holder.getLayoutPosition()));
        c.startActivity(i);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ListRowViewHolder listRowViewHolder, int position) {

        Transaction item = listItems.get(position);
        listRowViewHolder.tvBedrag.setText(item.bedrag);
        listRowViewHolder.tvDateTime.setText(item.dateTime);
        listRowViewHolder.tvDescription.setText(item.description);
        listRowViewHolder.tvName.setText(item.name);

    }

    @Override
    public int getItemCount() {
        return (null != listItems ? listItems.size() : 0);
    }

    public void setItems(ArrayList<Transaction> items){
        listItems.addAll(items);
    }

    public void clear(){
        listItems.clear();
    }
}