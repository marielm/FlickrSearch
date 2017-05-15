package com.marielm.flickersearch.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.marielm.flickersearch.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.marielm.flickersearch.activities.SearchDialog.KEY_TAG;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SEARCH_TAG = 1;

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

        emptyViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, SearchDialog.class), REQUEST_SEARCH_TAG);
            }
        });
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_SEARCH_TAG && data.hasExtra(KEY_TAG)) {
            String tag = data.getStringExtra(KEY_TAG);
            Toast.makeText(MainActivity.this, "tag: " + tag, Toast.LENGTH_SHORT).show();
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
