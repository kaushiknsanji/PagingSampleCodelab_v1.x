package com.example.android.codelabs.paging.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.example.android.codelabs.paging.Injection;
import com.example.android.codelabs.paging.R;
import com.example.android.codelabs.paging.databinding.ActivitySearchRepositoriesBinding;

/**
 * The Main Activity that displays the list of Github Repositories
 * for the Search query executed. The query is searched only in the fields of Name and Description
 * of the Repository.
 */
public class SearchRepositoriesActivity extends AppCompatActivity {
    //Constant used for Logs
    private static final String LOG_TAG = SearchRepositoriesActivity.class.getSimpleName();

    //Bundle constant to save the last searched query
    private static final String LAST_SEARCH_QUERY = "last_search_query";
    //The default query to load
    private static final String DEFAULT_QUERY = "Android";

    private ActivitySearchRepositoriesBinding mDataBinding;
    private SearchRepositoriesViewModel mViewModel;
    private ReposAdapter mReposAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Uses DataBinding to set the content view
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_repositories);

        //Set the Empty text with emoji unicode
        mDataBinding.emptyList.setText(getString(R.string.no_results, "\uD83D\uDE13"));

        //Get the view model
        mViewModel = ViewModelProviders.of(this, Injection.provideViewModelFactory(this))
                .get(SearchRepositoriesViewModel.class);

        //Initialize RecyclerView
        initRecyclerView();

        //Get the query to search
        String query = DEFAULT_QUERY;
        if (savedInstanceState != null) {
            query = savedInstanceState.getString(LAST_SEARCH_QUERY, DEFAULT_QUERY);
        }

        //Post the query to be searched
        mViewModel.searchRepo(query);

        //Initialize the EditText for Search Actions
        initSearch(query);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(LAST_SEARCH_QUERY, mViewModel.lastQueryValue());
    }

    /**
     * Initializes the RecyclerView that loads the list of Repos
     */
    private void initRecyclerView() {
        //Add dividers between RecyclerView's row items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mDataBinding.list.addItemDecoration(dividerItemDecoration);

        //TODO(Step-9): Remove the custom scroll handler
        //Initializing Scroll Listener
        mDataBinding.list.addOnScrollListener(new RecyclerViewScrollListener(mViewModel));

        //Initializing Adapter
        initAdapter();
    }

    /**
     * Initializes the Adapter of RecyclerView which is {@link ReposAdapter}
     */
    private void initAdapter() {
        mReposAdapter = new ReposAdapter();
        mDataBinding.list.setAdapter(mReposAdapter);

        //TODO(Step-5): Replace occurrences of List<Repo> with PagedList<Repo>
        //This is not required here as we are using lambda expressions,
        //but do change the comment accordingly

        //Subscribing to receive the new List Repos
        mViewModel.getRepos().observe(this, repos -> {
            if (repos != null) {
                Log.d(LOG_TAG, "initAdapter: Repo List size: " + repos.size());
                showEmptyList(repos.size() == 0);
                mReposAdapter.submitList(repos);
            }
        });

        //Subscribing to receive the recent Network Errors if any
        mViewModel.getNetworkErrors().observe(this, errorMsg -> {
            Toast.makeText(this, "\uD83D\uDE28 Wooops " + errorMsg, Toast.LENGTH_LONG).show();
        });
    }

    /**
     * Shows the Empty view when the list is empty
     *
     * @param show Displays the empty view and hides the list when the boolean is <b>True</b>
     */
    private void showEmptyList(boolean show) {
        if (show) {
            mDataBinding.list.setVisibility(View.GONE);
            mDataBinding.emptyList.setVisibility(View.VISIBLE);
        } else {
            mDataBinding.list.setVisibility(View.VISIBLE);
            mDataBinding.emptyList.setVisibility(View.GONE);
        }
    }

    /**
     * Initializes the EditText for handling the Search actions
     *
     * @param query The query to be searched for in the Repositories
     */
    private void initSearch(String query) {
        mDataBinding.searchRepo.setText(query);

        mDataBinding.searchRepo.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput();
                return true;
            } else {
                return false;
            }
        });

        mDataBinding.searchRepo.setOnKeyListener((view, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput();
                return true;
            } else {
                return false;
            }
        });
    }

    /**
     * Updates the list with the new data when the User entered the query and hit 'enter'
     * or corresponding action to trigger the Search.
     */
    private void updateRepoListFromInput() {
        String queryEntered = mDataBinding.searchRepo.getText().toString().trim();
        if (!TextUtils.isEmpty(queryEntered)) {
            mDataBinding.list.scrollToPosition(0);
            mViewModel.searchRepo(queryEntered);
            mReposAdapter.submitList(null);
        }
    }

    //TODO(Step-9): Remove the Scroll Listener implementation

    /**
     * Scroll listener for RecyclerView to request more data whenever the scroll reaches the
     * end of the list
     */
    private static class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {
        private final SearchRepositoriesViewModel viewModel;

        RecyclerViewScrollListener(SearchRepositoriesViewModel viewModel) {
            this.viewModel = viewModel;
        }

        /**
         * Callback method to be invoked when the RecyclerView has been scrolled. This will be
         * called after the scroll has completed.
         * <p>
         * This callback will also be called if visible item range changes after a layout
         * calculation. In that case, dx and dy will be 0.
         *
         * @param recyclerView The RecyclerView which scrolled.
         * @param dx           The amount of horizontal scroll.
         * @param dy           The amount of vertical scroll.
         */
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy != 0) {
                //Scanning for vertical scrolls only

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();

                //Calling to request more data when required
                viewModel.listScrolled(visibleItemCount, lastVisibleItem, totalItemCount);
            }
        }
    }
}
