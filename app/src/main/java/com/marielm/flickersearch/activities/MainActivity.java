package com.marielm.flickersearch.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.marielm.flickersearch.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.empty_view) View emptyView;
    @BindView(R.id.empty_search_button) View emptyViewSearch;

    private SearchAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        adapter = new SearchAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(adapter);


        if (adapter.getItemCount() == 0) {
            showEmpty();
        } else {
            showResults();
        }

    }

    private void showEmpty() {
        emptyView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void showResults() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);

    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {

        @Override
        public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override public void onBindViewHolder(SearchViewHolder holder, int position) {

        }

        @Override public int getItemCount() {
            return 0;
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder {
        public SearchViewHolder(View itemView) {
            super(itemView);
        }
    }
}
