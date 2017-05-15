package com.marielm.flickersearch;

import android.app.Application;

import dagger.ObjectGraph;

public class FlickrSearchApplication extends Application {

    static FlickrSearchApplication context;
    static ObjectGraph graph;

    public FlickrSearchApplication() {
        context = this;
    }

    public static ObjectGraph getGraph() {
        if (graph == null) {
            graph = ObjectGraph.create(new FlickrSearchModule());
        }

        return graph;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
    }
}
