package com.example.repolist.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.repolist.R;
import com.example.repolist.databinding.ItemRepoBinding;
import com.example.repolist.models.Item;
import com.example.repolist.models.RepoResult;
import com.squareup.picasso.Picasso;

public class RepoListAdapter extends RecyclerView.Adapter<RepoListAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iconImageView;
        public TextView repoNameTextView;
        public TextView repoDescriptionTextView;
        public Button selectButton;
        public ItemRepoBinding mItemRepoBinding;

        public ViewHolder(View itemView, ItemRepoBinding itemRepoBinding) {
            super(itemView);
            mItemRepoBinding = itemRepoBinding;
            iconImageView = (ImageView) itemView.findViewById(R.id.icon);
            repoNameTextView = (TextView) itemView.findViewById(R.id.repo_name);
            repoDescriptionTextView = (TextView) itemView.findViewById(R.id.repo_description);
            selectButton = (Button) itemView.findViewById(R.id.select_button);
        }

        public void bind(Item repoItem) {
            mItemRepoBinding.setRepoItem(repoItem);
            mItemRepoBinding.executePendingBindings();
        }
    }

    private RepoResult mRepoResult;

    public RepoListAdapter()  {
        mRepoResult = new RepoResult();
    }

    @Override
    public RepoListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View repoView = inflater.inflate(R.layout.item_repo, parent, false);

        ItemRepoBinding itemRepoBinding = DataBindingUtil.inflate(inflater,
                R.layout.item_repo, parent, false);
        ViewHolder viewHolder = new ViewHolder(repoView, itemRepoBinding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RepoListAdapter.ViewHolder holder, int position) {
        Item item = mRepoResult.items.get(position);
        holder.repoNameTextView.setText(item.name);
        holder.repoDescriptionTextView.setText(item.description);
        Picasso.get().load(item.owner.avatar_url).into(holder.iconImageView);
        holder.selectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mRepoResult.items.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mRepoResult.items.size());
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("tag", "clicked");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRepoResult.items.size();
    }

    public void update(RepoResult repoResult) {
        mRepoResult = repoResult;
        notifyDataSetChanged();
    }

}
