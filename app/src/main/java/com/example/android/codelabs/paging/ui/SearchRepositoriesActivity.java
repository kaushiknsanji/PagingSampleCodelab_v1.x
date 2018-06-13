package com.example.android.codelabs.paging.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
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
 *
 * @author Kaushik N Sanji
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

        //Initializing Adapter
        initAdapter();
    }

    /**
     * Initializes the Adapter of RecyclerView which is {@link ReposAdapter}
     */
    private void initAdapter() {
        mReposAdapter = new ReposAdapter();
        mDataBinding.list.setAdapter(mReposAdapter);

        //Subscribing to receive the new PagedList Repos
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
            //Posts the query to be searched
            mViewModel.searchRepo(queryEntered);
            //Resets the old list
            mReposAdapter.submitList(null);
        }
    }

}
