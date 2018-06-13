package com.example.android.codelabs.paging.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;
import android.support.annotation.Nullable;

import com.example.android.codelabs.paging.data.GithubRepository;
import com.example.android.codelabs.paging.model.Repo;
import com.example.android.codelabs.paging.model.RepoSearchResult;

/**
 * ViewModel for the {@link SearchRepositoriesActivity} screen.
 * The ViewModel works with the {@link com.example.android.codelabs.paging.data.GithubRepository} to get the data.
 *
 * @author Kaushik N Sanji
 */
public class SearchRepositoriesViewModel extends ViewModel {

    private GithubRepository githubRepository;
    private MutableLiveData<String> queryLiveData = new MutableLiveData<>();
    //Applying transformation to get RepoSearchResult for the given Search Query
    private LiveData<RepoSearchResult> repoResult = Transformations.map(queryLiveData,
            inputQuery -> githubRepository.search(inputQuery)
    );
    //Applying transformation to get Live PagedList<Repo> from the RepoSearchResult
    private LiveData<PagedList<Repo>> repos = Transformations.switchMap(repoResult,
            RepoSearchResult::getData
    );
    //Applying transformation to get Live Network Errors from the RepoSearchResult
    private LiveData<String> networkErrors = Transformations.switchMap(repoResult,
            RepoSearchResult::getNetworkErrors
    );

    public SearchRepositoriesViewModel(GithubRepository githubRepository) {
        this.githubRepository = githubRepository;
    }

    LiveData<PagedList<Repo>> getRepos() {
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

}
