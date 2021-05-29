package com.mibtech.aesthetic_am.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.model.Faq;

public class FaqAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // for load more
    public final int VIEW_TYPE_ITEM = 0;
    public final int VIEW_TYPE_LOADING = 1;
    final Activity activity;
    final ArrayList<Faq> faqs;
    public boolean isLoading;
    boolean visible;
    String id = "0";


    public FaqAdapter(Activity activity, ArrayList<Faq> faqs) {
        this.activity = activity;
        this.faqs = faqs;
        visible = false;
    }

    public void add(int position, Faq item) {
        faqs.add(position, item);
        notifyItemInserted(position);
    }

    public void setLoaded() {
        isLoading = false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.lyt_faq_list, parent, false);
            return new FaqHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_progressbar, parent, false);
            return new ViewHolderLoading(view);
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holderparent, final int position) {

        if (holderparent instanceof FaqHolder) {
            final FaqHolder holder = (FaqHolder) holderparent;
            final Faq faq = faqs.get(position);

            id = faq.getId();

            if (!faq.getQuestion().trim().isEmpty() && !faq.getAnswer().trim().isEmpty()) {
                holder.tvQue.setText(faq.getQuestion());
                holder.tvAns.setText(faq.getAnswer());
                holder.tvAns.setVisibility(View.GONE);
            } else {
                holder.mainLyt.setVisibility(View.GONE);
            }
            holder.mainLyt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (visible) {
                        visible = false;
                        holder.tvAns.setVisibility(View.GONE);
                    } else {
                        visible = true;
                        holder.tvAns.setVisibility(View.VISIBLE);
                    }
                }
            });

        } else if (holderparent instanceof ViewHolderLoading) {
            ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holderparent;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return faqs.size();
    }

    @Override
    public int getItemViewType(int position) {
        return faqs.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolderLoading extends RecyclerView.ViewHolder {
        public final ProgressBar progressBar;

        public ViewHolderLoading(View view) {
            super(view);
            progressBar = view.findViewById(R.id.itemProgressbar);
        }
    }

    class FaqHolder extends RecyclerView.ViewHolder {

        final TextView tvQue;
        final TextView tvAns;
        final RelativeLayout mainLyt;


        public FaqHolder(@NonNull View itemView) {
            super(itemView);

            tvQue = itemView.findViewById(R.id.tvQue);
            tvAns = itemView.findViewById(R.id.tvAns);
            mainLyt = itemView.findViewById(R.id.mainLyt);
        }
    }
}
