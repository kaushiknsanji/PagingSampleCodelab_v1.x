package com.example.android.codelabs.paging.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.android.codelabs.paging.api.GithubService;
import com.example.android.codelabs.paging.api.GithubServiceClient;
import com.example.android.codelabs.paging.db.GithubLocalCache;
import com.example.android.codelabs.paging.model.Repo;
import com.example.android.codelabs.paging.model.RepoSearchResult;

import java.util.List;

/**
 * Repository class that works with local and remote data sources.
 */
public class GithubRepository implements GithubServiceClient.ApiCallback {
    //TODO(Step-9): Move this callback implementation to RepoBoundaryCallback

    //Constant used for logs
    private static final String LOG_TAG = GithubRepository.class.getSimpleName();
    // Constant for the Number of items in a page to be requested from the Github API
    private static final int NETWORK_PAGE_SIZE = 50;
    private GithubService githubService;
    private GithubLocalCache localCache;
    //TODO(Step-9): Move these fields to RepoBoundaryCallback: START
    // Keep the last requested page. When the request is successful, increment the page number.
    private int lastRequestedPage = 1;
    // LiveData of network errors.
    private MutableLiveData<String> networkErrors = new MutableLiveData<>();
    // Avoid triggering multiple requests in the same time
    private boolean isRequestInProgress = false;
    //TODO(Step-9): Move these fields to RepoBoundaryCallback: END

    //TODO(Step-7): Define DATABASE_PAGE_SIZE constant and set it to 20

    public GithubRepository(GithubService githubService, GithubLocalCache localCache) {
        this.githubService = githubService;
        this.localCache = localCache;
    }

    /**
     * Search repositories whose names match the query.
     */
    public RepoSearchResult search(String query) {
        Log.d(LOG_TAG, "search: New query: " + query);

        //TODO(Step-7): Remove these lines: START
        //Resetting to first page for every new query
        lastRequestedPage = 1;
        //Requests data from Github for the query and saves the results
        requestAndSaveData(query);
        //TODO(Step-7): Remove these lines: END

        //TODO(Step-7): Create a new value to hold the DataSource.Factory from localCache.reposByName()
        // Get data from the local cache
        LiveData<List<Repo>> reposByName = localCache.reposByName(query);

        //TODO(Step-9): Construct the BoundaryCallback and get the Network errors

        //TODO(Step-7): Get the paged list constructed using LivePagedListBuilder
        //TODO(Step-9): Set the BoundaryCallback to the LivePagedListBuilder

        //TODO(Step-7): Pass the paged list data generated, to RepoSearchResult
        //TODO(Step-9): Pass the network errors exposed by the BoundaryCallback, to RepoSearchResult
        // Get the Search result with the network errors
        return new RepoSearchResult(reposByName, networkErrors);
    }

    //TODO(Step-9): Remove this method as it is no longer required since we are implementing BoundaryCallback

    /**
     * Called to retrieve more data from the API for the previously executed search query,
     * when the user scrolls almost to the end of the current list.
     *
     * @param query The query to use for retrieving the repositories from API
     */
    public void requestMore(String query) {
        requestAndSaveData(query);
    }

    //TODO(Step-9): Move this method to RepoBoundaryCallback

    /**
     * Method to request data from Github API for the given search query
     * and save the results.
     *
     * @param query The query to use for retrieving the repositories from API
     */
    private void requestAndSaveData(String query) {
        //Exiting if the request is in progress
        if (isRequestInProgress) return;

        //Set to true as we are starting the network request
        isRequestInProgress = true;

        //Calling the client API to retrieve the Repos for the given search query
        GithubServiceClient.searchRepos(githubService, query, lastRequestedPage, NETWORK_PAGE_SIZE, this);
    }

    //TODO(Step-9): Move this callback implementation to RepoBoundaryCallback

    /**
     * Callback invoked when the Search Repo API Call
     * completed successfully
     *
     * @param items The List of Repos retrieved for the Search done
     */
    @Override
    public void onSuccess(List<Repo> items) {
        //Inserting records in the database thread
        localCache.insert(items, () -> {
            //Updating the last requested page number when the request was successful
            //and the results were inserted successfully
            lastRequestedPage++;
            //Marking the request progress as completed
            isRequestInProgress = false;
        });
    }

    //TODO(Step-9): Move this callback implementation to RepoBoundaryCallback

    /**
     * Callback invoked when the Search Repo API Call failed
     *
     * @param errorMessage The Error message captured for the API Call failed
     */
    @Override
    public void onError(String errorMessage) {
        //Update the Network error to be shown
        networkErrors.postValue(errorMessage);
        //Mark the request progress as completed
        isRequestInProgress = false;
    }
}
