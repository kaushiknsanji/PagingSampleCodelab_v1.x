package com.example.android.codelabs.paging.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.codelabs.paging.R;
import com.example.android.codelabs.paging.databinding.RepoViewItemBinding;
import com.example.android.codelabs.paging.model.Repo;

/**
 * Adapter for the list of repositories.
 */
public class ReposAdapter extends ListAdapter<Repo, ReposAdapter.RepoViewHolder> {
    /**
     * DiffUtil to compare the Repo data (old and new)
     * for issuing notify commands suitably to update the list
     */
    private static DiffUtil.ItemCallback<Repo> REPO_COMPARATOR
            = new DiffUtil.ItemCallback<Repo>() {
        @Override
        public boolean areItemsTheSame(Repo oldItem, Repo newItem) {
            return oldItem.fullName.equals(newItem.fullName);
        }

        @Override
        public boolean areContentsTheSame(Repo oldItem, Repo newItem) {
            return oldItem.equals(newItem);
        }
    };

    /**
     * TODO(Step-8): Update ReposAdapter to work with a PagedList by replacing
     * ListAdapter with PagedListAdapter
     */

    ReposAdapter() {
        super(REPO_COMPARATOR);
    }

    @NonNull
    @Override
    public RepoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Uses DataBinding to inflate the Item View
        RepoViewItemBinding itemBinding = RepoViewItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RepoViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RepoViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    /**
     * View Holder for a {@link Repo} RecyclerView list item.
     */
    public class RepoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final RepoViewItemBinding mDataBinding;

        RepoViewHolder(RepoViewItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.mDataBinding = itemBinding;

            View itemView = itemBinding.getRoot();
            itemView.setOnClickListener(this);
        }

        void bind(Repo repo) {
            if (repo == null) {
                //Binding the elements in the code when the Repo is null
                Resources resources = mDataBinding.getRoot().getContext().getResources();
                mDataBinding.repoName.setText(resources.getString(R.string.loading));
                mDataBinding.repoDescription.setVisibility(View.GONE);
                mDataBinding.repoLanguage.setVisibility(View.GONE);
                mDataBinding.repoStars.setText(resources.getString(R.string.unknown));
                mDataBinding.repoForks.setText(resources.getString(R.string.unknown));
            } else {
                //When Repo is not null, data binding will be automatically done in the layout
                mDataBinding.setRepo(repo);
                //For Immediate Binding
                mDataBinding.executePendingBindings();
            }
        }

        /**
         * Called when a view has been clicked.
         *
         * @param view The view that was clicked.
         */
        @Override
        public void onClick(View view) {
            if (getAdapterPosition() > RecyclerView.NO_POSITION) {
                Repo repo = getItem(getAdapterPosition());
                if (repo != null && !TextUtils.isEmpty(repo.url)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(repo.url));
                    view.getContext().startActivity(intent);
                }
            }
        }
    }
}
