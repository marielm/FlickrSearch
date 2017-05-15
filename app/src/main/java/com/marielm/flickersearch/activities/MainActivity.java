package com.marielm.flickersearch.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.marielm.flickersearch.FlickrSearchApplication;
import com.marielm.flickersearch.R;
import com.marielm.flickersearch.network.PhotoSearchService;
import com.marielm.flickersearch.network.SearchResult;
import com.marielm.flickersearch.network.TagsResponse;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.marielm.flickersearch.activities.SearchDialog.KEY_TAG;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SEARCH_TAG = 1;

    @BindView(R.id.progress_bar) View progressBar;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.empty_view) View emptyView;
    @BindView(R.id.empty_view_text) TextView emptyMessage;
    @BindView(R.id.empty_search_button) View emptyViewSearch;

    @Inject PhotoSearchService service;

    private SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        FlickrSearchApplication.getGraph().inject(this);

        adapter = new SearchAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            showEmpty("No results, try searching!");
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
            getSearchResults(tag);
        }
    }

    private void getSearchResults(String tag) {
        showProgress();
        service.getTaggedPhotos(tag).enqueue(new Callback<TagsResponse>() {
            @Override
            public void onResponse(Call<TagsResponse> call, Response<TagsResponse> response) {
                if (response.isSuccessful()) {
                    adapter.setData(response.body().photos.photo);
                    showResults();
                } else {
                    showEmpty("Error, give it another try!");
                }
            }

            @Override public void onFailure(Call<TagsResponse> call, Throwable t) {
                showEmpty("Error, give it another try!");
            }
        });
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);;
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
    }

    private void showEmpty(String message) {
        progressBar.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        emptyMessage.setText(message);
        recyclerView.setVisibility(View.GONE);
    }

    private void showResults() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private List<SearchResult> data;

        public void setData(List<SearchResult> data) {
            this.data = data;
        }

        @Override
        public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SearchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_result, parent, false));
        }

        @Override public void onBindViewHolder(SearchViewHolder holder, int position) {
            SearchResult item = data.get(position);
            holder.title.setText(item.title);
        }

        @Override public int getItemCount() {
            if (data != null) return data.size();

            return 0;
        }
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title) TextView title;

        public SearchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
