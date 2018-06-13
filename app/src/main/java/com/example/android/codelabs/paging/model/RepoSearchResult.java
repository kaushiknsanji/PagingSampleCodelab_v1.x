package com.example.android.codelabs.paging.model;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;

/**
 * RepoSearchResult from a search, which contains LiveData<PagedList<Repo>> holding query data,
 * and a LiveData<String> of network error state.
 *
 * @author Kaushik N Sanji
 */
public class RepoSearchResult {
    //LiveData for Search Results
    private final LiveData<PagedList<Repo>> data;
    //LiveData for the Network Errors
    private final LiveData<String> networkErrors;

    public RepoSearchResult(LiveData<PagedList<Repo>> data, LiveData<String> networkErrors) {
        this.data = data;
        this.networkErrors = networkErrors;
    }

    public LiveData<PagedList<Repo>> getData() {
        return data;
    }

    public LiveData<String> getNetworkErrors() {
        return networkErrors;
    }
}
