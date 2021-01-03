package com.example.repolist.view;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.repolist.R;
import com.example.repolist.models.RepoResult;

public class RepoListFragment extends Fragment {

    private RecyclerView mRepoList;
    private SwipeRefreshLayout mSwipeContainer;
    private EditText mTextInput;
    private RepoResultViewModel mViewModel;
    private RepoListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(
                this,
                new RepoResultViewModelFactory(getContext())).get(RepoResultViewModel.class);
        mAdapter = new RepoListAdapter();

        mViewModel.errorData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean visibility) {
                setErrorVisibility(visibility);
            }
        });
        mViewModel.loadingData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean isLoading) {
                showLoading(isLoading);
            }
        });
        mViewModel.repoData.observe(this, new Observer<RepoResult>() {
            @Override
            public void onChanged(@Nullable final RepoResult newResult) {
                mAdapter.update(newResult);
            }
        });
        mViewModel.searchInput.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String searchText) {
                mViewModel.loadingData.setValue(true);
                mViewModel.fetchRepositories(searchText);
                dismissKeyboard();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_fragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initializeUI(view);
        mViewModel.fetchRepositories(mTextInput.getText().toString());
    }

    @Override
    public void onStop() {
        mViewModel.clearAllRepositories();
        super.onStop();
    }

    private void initializeUI(View view) {
        mTextInput = (EditText) view.findViewById(R.id.text_input);
        mTextInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchText = mTextInput.getText().toString();
                    mViewModel.searchInput.setValue(searchText);
                    return true;
                }
                return false;
            }
        });

        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
             @Override
             public void onRefresh() {
                 String searchText = mTextInput.getText().toString();
                 mViewModel.searchInput.setValue(searchText);
             }
         }
        );

        mRepoList = (RecyclerView) view.findViewById(R.id.repo_list);
        mRepoList.setAdapter(mAdapter);
        mRepoList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRepoList.setItemAnimator(new DefaultItemAnimator());
    }

    private void showLoading(boolean isLoading) {
        mSwipeContainer.setRefreshing(isLoading);
    }

    private void setErrorVisibility(boolean shouldShow) {
        if (shouldShow) {
            mRepoList.setVisibility(View.GONE);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder
                .setTitle(R.string.error_title)
                .setMessage(R.string.error_message)
                .setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert))
                .setPositiveButton(R.string.error_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // No-op
                    }
                })
                .show();
        } else {
            mRepoList.setVisibility(View.VISIBLE);
        }
    }

    private void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mTextInput.getWindowToken(), 0);
    }

}
