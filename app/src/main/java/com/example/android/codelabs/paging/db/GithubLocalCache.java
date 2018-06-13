package com.example.android.codelabs.paging.db;

import android.arch.paging.DataSource;
import android.util.Log;

import com.example.android.codelabs.paging.model.Repo;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Class that handles the DAO local data source. This ensures that methods are triggered on the
 * correct executor.
 *
 * @author Kaushik N Sanji
 */
public class GithubLocalCache {
    //Constant used for Logs
    private static final String LOG_TAG = GithubLocalCache.class.getSimpleName();

    //Dao for Repo Entity
    private RepoDao repoDao;
    //Single Thread Executor for database operations
    private Executor ioExecutor;

    public GithubLocalCache(RepoDao repoDao, Executor ioExecutor) {
        this.repoDao = repoDao;
        this.ioExecutor = ioExecutor;
    }

    /**
     * Insert a list of repos in the database, on a background thread.
     */
    public void insert(List<Repo> repos, InsertCallback callback) {
        ioExecutor.execute(() -> {
            Log.d(LOG_TAG, "insert: inserting " + repos.size() + " repos");
            repoDao.insert(repos);
            callback.insertFinished();
        });
    }

    /**
     * Request a DataSource.Factory<Integer, Repo> from the Dao, based on a repo name. If the name contains
     * multiple words separated by spaces, then we're emulating the GitHub API behavior and allow
     * any characters between the words.
     *
     * @param name repository name
     */
    public DataSource.Factory<Integer, Repo> reposByName(String name) {
        // appending '%' so we can allow other characters to be before and after the query string
        return repoDao.reposByName("%" + name.replace(' ', '%') + "%");
    }

    public interface InsertCallback {
        /**
         * Callback method invoked when the insert operation
         * completes.
         */
        void insertFinished();
    }
}
