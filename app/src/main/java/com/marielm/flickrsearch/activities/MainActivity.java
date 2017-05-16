package com.marielm.flickrsearch.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.common.collect.Lists;
import com.marielm.flickrsearch.FlickrSearchApplication;
import com.marielm.flickrsearch.R;
import com.marielm.flickrsearch.network.PhotoSearchService;
import com.marielm.flickrsearch.network.SearchResult;
import com.marielm.flickrsearch.network.TagsResponse;
import com.marielm.flickrsearch.util.ImageUrlUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.marielm.flickrsearch.activities.SearchDialog.KEY_FILTER;
import static com.marielm.flickrsearch.activities.SearchDialog.KEY_FILTER_ENTRY;
import static com.marielm.flickrsearch.activities.SearchDialog.KEY_TAG;
import static com.marielm.flickrsearch.activities.SearchDialog.KEY_TAG_ENTRY;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SEARCH_TAG = 1;
    private static final int REQUEST_SEARCH_FILTER = 2;

    @BindView(R.id.progress_bar) View progressBar;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.empty_view) View emptyView;
    @BindView(R.id.empty_view_text) TextView emptyMessage;

    @Inject PhotoSearchService service;
    @Inject SharedPreferences sharedPreferences;

    private SearchAdapter adapter;
    private boolean showFilter;

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

    }

    @OnClick(R.id.search_fab)
    public void onSearchClick(View view) {
        startActivityForResult(SearchDialog.create(MainActivity.this, KEY_TAG), REQUEST_SEARCH_TAG);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_SEARCH_TAG && data.hasExtra(KEY_TAG)) {
            String tag = data.getStringExtra(KEY_TAG);

            sharedPreferences.edit().putString(KEY_TAG_ENTRY, tag).apply();
            sharedPreferences.edit().remove(KEY_FILTER_ENTRY).apply();
            getSearchResults(tag);
        } else if (requestCode == REQUEST_SEARCH_FILTER && data.hasExtra(KEY_FILTER)) {
            String filterText = data.getStringExtra(KEY_FILTER);

            sharedPreferences.edit().putString(KEY_FILTER_ENTRY, filterText).apply();
            handleFilterResults(filterText);
        }
    }

    private void handleFilterResults(String filterText) {
        adapter.filterResults(filterText);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.filter).setVisible(showFilter);

        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                startActivityForResult(SearchDialog.create(MainActivity.this, KEY_FILTER), REQUEST_SEARCH_FILTER);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getSearchResults(String tag) {
        showProgress();
        service.getTaggedPhotos(tag).enqueue(new Callback<TagsResponse>() {
            @Override
            public void onResponse(Call<TagsResponse> call, Response<TagsResponse> response) {
                if (response.isSuccessful()) {

                    showFilter = true;
                    invalidateOptionsMenu();

                    adapter.setData(response.body().photos.photo);
                    showResults();
                } else {
                    showFilter = false;
                    invalidateOptionsMenu();

                    showEmpty("Error, give it another try!");
                }
            }

            @Override public void onFailure(Call<TagsResponse> call, Throwable t) {
                showEmpty("Error, give it another try!");
                showFilter = false;
                invalidateOptionsMenu();
            }
        });
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
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
        private List<SearchResult> total;

        public void setData(List<SearchResult> data) {
            this.data = data;
            this.total = data;
        }

        public void filterResults(final String text) {
            final List<String> words = Lists.newArrayList(text.split("\\s"));

            Set<SearchResult> filtered = new HashSet<>();

            for (String word : words) {
                for (SearchResult item : total) {
                    String title = item.title.toLowerCase();
                    if (title.contains(word)) filtered.add(item);
                }
            }

            List<SearchResult> results = Lists.newArrayList(filtered);

            if (results.size() > 0) {
                data = results;
                notifyDataSetChanged();
            } else {
                Toast.makeText(MainActivity.this, "No results on filter", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SearchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_result, parent, false));
        }

        @Override public void onBindViewHolder(final SearchViewHolder holder, int position) {
            final SearchResult item = data.get(position);

            Glide.with(MainActivity.this)
                    .load(ImageUrlUtil.getUrl(item.farm, item.server, item.id, item.secret))
                    .crossFade()
                    .centerCrop()
                    .into(holder.photo);


            holder.photo.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    Snackbar.make(holder.photo, item.title, BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            });
        }

        @Override public int getItemCount() {
            if (data != null) return data.size();

            return 0;
        }
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image) ImageView photo;

        public SearchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
