package com.example.android.codelabs.paging.model;

import android.arch.lifecycle.LiveData;

import java.util.List;

/**
 * RepoSearchResult from a search, which contains LiveData<List<Repo>> holding query data,
 * and a LiveData<String> of network error state.
 */
public class RepoSearchResult {
    //TODO(Step-5): Replace occurrences of List<Repo> with PagedList<Repo>

    //LiveData for Search Results
    private final LiveData<List<Repo>> data;
    //LiveData for the Network Errors
    private final LiveData<String> networkErrors;

    public RepoSearchResult(LiveData<List<Repo>> data, LiveData<String> networkErrors) {
        this.data = data;
        this.networkErrors = networkErrors;
    }

    public LiveData<List<Repo>> getData() {
        return data;
    }

    public LiveData<String> getNetworkErrors() {
        return networkErrors;
    }
}
