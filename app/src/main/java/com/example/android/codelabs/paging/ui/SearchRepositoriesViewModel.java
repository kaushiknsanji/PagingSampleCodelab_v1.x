package com.example.android.codelabs.paging.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.example.android.codelabs.paging.data.GithubRepository;
import com.example.android.codelabs.paging.model.Repo;
import com.example.android.codelabs.paging.model.RepoSearchResult;

import java.util.List;

/**
 * ViewModel for the {@link SearchRepositoriesActivity} screen.
 * The ViewModel works with the {@link com.example.android.codelabs.paging.data.GithubRepository} to get the data.
 */
public class SearchRepositoriesViewModel extends ViewModel {
    //Constant used like a Prefetch distance while loading the next set of data
    private static final int VISIBLE_THRESHOLD = 5;

    private GithubRepository githubRepository;
    private MutableLiveData<String> queryLiveData = new MutableLiveData<>();
    //Applying transformation to get RepoSearchResult for the given Search Query
    private LiveData<RepoSearchResult> repoResult = Transformations.map(queryLiveData,
            inputQuery -> githubRepository.search(inputQuery)
    );
    //Applying transformation to get Live List<Repo> from the RepoSearchResult
    private LiveData<List<Repo>> repos = Transformations.switchMap(repoResult,
            RepoSearchResult::getData
    );

    //TODO(Step-5): Replace occurrences of List<Repo> with PagedList<Repo>
    //Applying transformation to get Live Network Errors from the RepoSearchResult
    private LiveData<String> networkErrors = Transformations.switchMap(repoResult,
            RepoSearchResult::getNetworkErrors
    );

    public SearchRepositoriesViewModel(GithubRepository githubRepository) {
        this.githubRepository = githubRepository;
    }

    //TODO(Step-5): Replace occurrences of List<Repo> with PagedList<Repo>
    LiveData<List<Repo>> getRepos() {
        return repos;
    }

    LiveData<String> getNetworkErrors() {
        return networkErrors;
    }

    /**
     * Search a repository based on a query string.
     */
    void searchRepo(String queryString) {
        queryLiveData.postValue(queryString);
    }

    /**
     * Get the last query value.
     */
    @Nullable
    String lastQueryValue() {
        return queryLiveData.getValue();
    }

    //TODO(Step-9): Remove the method invoked by the Scroll Listener

    /**
     * Method that requests more data to be loaded when the scroll is about to reach the end
     */
    void listScrolled(int visibleItemCount, int lastVisibleItemPosition, int totalItemCount) {
        if (visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount) {
            String immutableQuery = lastQueryValue();
            if (immutableQuery != null) {
                githubRepository.requestMore(immutableQuery);
            }
        }
    }
}
