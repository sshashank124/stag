package com.phaqlow.stag.util

object C {
    // Fragments
    const val FRAGMENT_HOME = "Home Fragment"
    const val FRAGMENT_PLAYLIST = "Playlist Fragment"

    // Tag - Entity
    const val TAG_MAX_NAME_LEN: Int = 20
    const val TAG_MAX_DESCRIPTION_LEN: Int = 200

    // Observable Operations
    const val RX_OP_ADD = 0
    const val RX_OP_REMOVE = 1
    const val RX_OP_SETALL = 2

    // Tabs
    const val TAB_TAGS = 0
    const val TAB_SONGS = 1

    // Detail Activity
    const val DETAIL_MODE_VIEW = 0
    const val DETAIL_MODE_EDIT = 1

    // Spotify Web API
    const val SPOTIFY_CLIENT_ID = "6ded709c4c584ceb9ad9ee911d50dace"
    const val SPOTIFY_LOGIN_REDIRECT_URI = "stag-login://callback"
    const val SPOTIFY_REQUEST_CODE = 1224

    // Shared Preferences
    const val PREF_IS_LAUNCHED_BEFORE = "IS_LAUNCHED_BEFORE"

    // Intent Extras
    const val EXTRA_SPOTIFY_ACCESS_TOKEN = "SPOTIFY_ACCESS_TOKEN"
    const val EXTRA_ITEM_ID = "ITEM_ID"

    // Logging
    const val LOG_TAG = "Stag"
}
