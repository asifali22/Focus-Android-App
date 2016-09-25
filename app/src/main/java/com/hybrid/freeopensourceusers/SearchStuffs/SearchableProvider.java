package com.hybrid.freeopensourceusers.SearchStuffs;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by monster on 25/9/16.
 */

public class SearchableProvider extends SearchRecentSuggestionsProvider {

    public static final String AUTHORITY = "com.hybrid.freeopensourceusers.SearchStuffs.SearchableProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public SearchableProvider() {
    setupSuggestions(AUTHORITY, MODE);
    }
}
