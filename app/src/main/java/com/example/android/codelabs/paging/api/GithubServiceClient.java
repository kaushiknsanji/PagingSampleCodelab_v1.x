package com.example.android.codelabs.paging.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.codelabs.paging.model.Repo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * REST Client that makes API Calls
 */
public class GithubServiceClient {
    //Constant used for Logs
    private static final String LOG_TAG = GithubServiceClient.class.getSimpleName();

    //BASE URL for Github REST Calls
    private static final String BASE_URL = "https://api.github.com/";

    /**
     * Creates the Retrofit Service for Github API
     *
     * @return The {@link GithubService} instance
     */
    public static GithubService create() {
        //Initializing HttpLoggingInterceptor to receive the HTTP event logs
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        //Building the HTTPClient with the logger
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        //Returning the Retrofit service for the BASE_URL
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                //Using the HTTPClient setup
                .client(httpClient)
                //GSON converter to convert the JSON elements to a POJO
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                //Creating the service for the defined API Interface
                .create(GithubService.class);
    }

    /**
     * Method that invokes the {@link GithubService#searchRepos(String, int, int)} API for
     * retrieving the Repositories using the Search query executed.
     *
     * @param service      The {@link GithubService} instance for executing the API
     * @param query        The Search Query to execute
     * @param page         The Page index to show
     * @param itemsPerPage The Number of items to be shown per page
     * @param apiCallback  The {@link ApiCallback} interface for receiving the events of this
     *                     API Call
     */
    public static void searchRepos(GithubService service, String query, int page, int itemsPerPage, ApiCallback apiCallback) {
        Log.d(LOG_TAG, String.format("searchRepos: query: %s, page: %s, itemsPerPage: %s", query, page, itemsPerPage));

        //Framing the query to be searched only in the Name and description fields of the
        //Github repositories
        String apiQuery = query + "in:name,description";

        //Executing the API asynchronously
        service.searchRepos(apiQuery, page, itemsPerPage).enqueue(new Callback<RepoSearchResponse>() {
            //Called when the response is received
            @Override
            public void onResponse(@Nullable Call<RepoSearchResponse> call, @NonNull Response<RepoSearchResponse> response) {
                Log.d(LOG_TAG, "onResponse: Got response: " + response);
                if (response.isSuccessful()) {
                    //Retrieving the Repo Items when the response is successful
                    List<Repo> items;
                    if (response.body() != null) {
                        items = response.body().items;
                    } else {
                        items = new ArrayList<>();
                    }
                    //Pass the result to the callback
                    apiCallback.onSuccess(items);
                } else {
                    //When the response is unsuccessful
                    try {
                        //Pass the error to the callback
                        apiCallback.onError(response.errorBody() != null ?
                                response.errorBody().string() : "Unknown error");
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "onResponse: Failed while reading errorBody: ", e);
                    }
                }
            }

            //Called on Failure of the request
            @Override
            public void onFailure(@Nullable Call<RepoSearchResponse> call, @NonNull Throwable t) {
                Log.d(LOG_TAG, "onFailure: Failed to get data");
                //Pass the error to the callback
                apiCallback.onError(t.getMessage() != null ?
                        t.getMessage() : "Unknown error");
            }
        });
    }

    public interface ApiCallback {
        /**
         * Callback invoked when the Search Repo API Call
         * completed successfully
         *
         * @param items The List of Repos retrieved for the Search done
         */
        void onSuccess(List<Repo> items);

        /**
         * Callback invoked when the Search Repo API Call failed
         *
         * @param errorMessage The Error message captured for the API Call failed
         */
        void onError(String errorMessage);
    }
}
