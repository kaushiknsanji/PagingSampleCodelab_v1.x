package com.example.android.codelabs.paging.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.android.codelabs.paging.data.GithubRepository;

import java.lang.reflect.InvocationTargetException;

/**
 * Factory for ViewModels
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    private GithubRepository githubRepository;

    public ViewModelFactory(GithubRepository githubRepository) {
        this.githubRepository = githubRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (SearchRepositoriesViewModel.class.isAssignableFrom(modelClass)) {
            try {
                return modelClass.getConstructor(GithubRepository.class).newInstance(githubRepository);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            } catch (InstantiationException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            }
        }
        throw new IllegalArgumentException("Unknown ViewModel class " + modelClass);
    }
}
