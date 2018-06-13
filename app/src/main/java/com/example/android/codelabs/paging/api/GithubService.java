package com.example.android.codelabs.paging.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Github API communication setup via Retrofit.
 */
public interface GithubService {
    /**
     * Get repos ordered by stars.
     */
    @GET("search/repositories?sort=stars")
    Call<RepoSearchResponse> searchRepos(@Query("q") String query,
                                         @Query("page") int page,
                                         @Query("per_page") int itemsPerPage);
}

