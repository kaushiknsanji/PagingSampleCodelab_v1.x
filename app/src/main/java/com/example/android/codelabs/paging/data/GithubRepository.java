package com.example.android.codelabs.paging.data;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.util.Log;

import com.example.android.codelabs.paging.api.GithubService;
import com.example.android.codelabs.paging.db.GithubLocalCache;
import com.example.android.codelabs.paging.model.Repo;
import com.example.android.codelabs.paging.model.RepoSearchResult;

/**
 * Repository class that works with local and remote data sources.
 *
 * @author Kaushik N Sanji
 */
public class GithubRepository {
    //Constant used for logs
    private static final String LOG_TAG = GithubRepository.class.getSimpleName();
    //Constant for the number of items to be loaded at once from the DataSource by the PagedList
    private static final int DATABASE_PAGE_SIZE = 20;
    private GithubService githubService;
    private GithubLocalCache localCache;

    public GithubRepository(GithubService githubService, GithubLocalCache localCache) {
        this.githubService = githubService;
        this.localCache = localCache;
    }

    /**
     * Search repositories whose names match the query.
     */
    public RepoSearchResult search(String query) {
        Log.d(LOG_TAG, "search: New query: " + query);

        // Get data source factory from the local cache
        DataSource.Factory<Integer, Repo> reposByName = localCache.reposByName(query);

        // Construct the boundary callback
        RepoBoundaryCallback boundaryCallback = new RepoBoundaryCallback(query, githubService, localCache);
        LiveData<String> networkErrors = boundaryCallback.getNetworkErrors();

        // Set the Page size for the Paged list
        PagedList.Config pagedConfig = new PagedList.Config.Builder()
                .setPageSize(DATABASE_PAGE_SIZE)
                .build();

        // Get the Live Paged list
        LiveData<PagedList<Repo>> data = new LivePagedListBuilder<>(reposByName, pagedConfig)
                .setBoundaryCallback(boundaryCallback)
                .build();

        // Get the Search result with the network errors exposed by the boundary callback
        return new RepoSearchResult(data, networkErrors);
    }

}
