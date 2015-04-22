// Copyright 2014 Google Inc. All Rights Reserved.
package android.support.v4.service;

import android.content.res.Configuration;
import android.support.v4.service.IMediaBrowserServiceCompatCallbacks;
import android.net.Uri;
import android.os.Bundle;

/**
 * Media API allows clients to browse through hierarchy of a user’s media collection,
 * playback a specific media entry and interact with the now playing queue.
 * @hide
 */
oneway interface IMediaBrowserServiceCompat {
    void connect(String pkg, in Bundle rootHints, IMediaBrowserServiceCompatCallbacks callbacks);
    void disconnect(IMediaBrowserServiceCompatCallbacks callbacks);
    void addSubscription(String uri, IMediaBrowserServiceCompatCallbacks callbacks);
    void removeSubscription(String uri, IMediaBrowserServiceCompatCallbacks callbacks);
}
