package com.example.android.codelabs.paging.api;

import com.example.android.codelabs.paging.model.Repo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Class to hold repo responses from searchRepo API calls.
 */
public class RepoSearchResponse {
    @SerializedName("total_count")
    public int total;

    @SerializedName("items")
    public List<Repo> items;

    public int nextPage;
}
