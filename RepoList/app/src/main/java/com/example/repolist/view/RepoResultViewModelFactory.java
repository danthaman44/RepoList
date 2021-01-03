package com.example.repolist.view;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class RepoResultViewModelFactory implements ViewModelProvider.Factory {

    private Context mContext;

    public RepoResultViewModelFactory(Context context) {
        mContext = context;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new RepoResultViewModel(mContext);
    }

}
