package com.example.android.codelabs.paging;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.android.codelabs.paging.api.GithubServiceClient;
import com.example.android.codelabs.paging.data.GithubRepository;
import com.example.android.codelabs.paging.db.GithubLocalCache;
import com.example.android.codelabs.paging.db.RepoDatabase;
import com.example.android.codelabs.paging.ui.ViewModelFactory;

import java.util.concurrent.Executors;

/**
 * Class that handles object creation.
 * <p>
 * Like this, objects can be passed as parameters in the constructors and then replaced for
 * testing, where needed.
 *
 * @author Kaushik N Sanji
 */
public class Injection {

    /**
     * Creates an instance of {@link GithubLocalCache} based on the database DAO.
     */
    @NonNull
    private static GithubLocalCache provideCache(Context context) {
        RepoDatabase repoDatabase = RepoDatabase.getInstance(context);
        return new GithubLocalCache(repoDatabase.reposDao(), Executors.newSingleThreadExecutor());
    }

    /**
     * Creates an instance of {@link GithubRepository} based on the
     * {@link com.example.android.codelabs.paging.api.GithubService} and a
     * {@link GithubLocalCache}
     */
    @NonNull
    private static GithubRepository provideGithubRepository(Context context) {
        return new GithubRepository(GithubServiceClient.create(), provideCache(context));
    }

    /**
     * Provides the {@link ViewModelFactory} that is then used to get a reference to
     * {@link com.example.android.codelabs.paging.ui.SearchRepositoriesViewModel} objects.
     */
    @NonNull
    public static ViewModelFactory provideViewModelFactory(Context context) {
        return new ViewModelFactory(provideGithubRepository(context));
    }
}
