package com.jellyworkz.arstud;

import android.net.Uri;
import android.util.Log;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.lang.ref.WeakReference;

/**
 * Created by BRcJU
 *
 * @since 27.05.2019
 */
public class ModelLoader {
    private final WeakReference<MainActivity> owner;
    private static final String TAG = "ModelLoader";

    ModelLoader(WeakReference<MainActivity> owner) {
        this.owner = owner;
    }

    void loadModel(Anchor anchor, Uri uri) {
        if (owner.get() == null) {
            Log.d(TAG, "Activity is null.  Cannot load model.");
            return;
        }
        ModelRenderable.builder()
                .setSource(owner.get(), uri)
                .build()
                .handle((renderable, throwable) -> {
                    MainActivity activity = owner.get();
                    if (activity == null) {
                        return null;
                    } else if (throwable != null) {
                        activity.onException(throwable);
                    } else {
                        activity.addNodeToScene(anchor, renderable);
                    }
                    return null;
                });

        return;
    }
}