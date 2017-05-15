package com.marielm.flickersearch;

import android.app.Application;

import dagger.ObjectGraph;

public class FlickerSearchApplication extends Application {

    static FlickerSearchApplication context;
    static ObjectGraph graph;

    public FlickerSearchApplication() {
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
