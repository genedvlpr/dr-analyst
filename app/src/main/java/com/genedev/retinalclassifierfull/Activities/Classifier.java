package com.genedev.retinalclassifierfull.Activities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.RectF;

import java.util.List;

// Generic interface for interacting with different recognition engines.

public interface Classifier {

    // An immutable result returned by a Classifier describing what was recognized.

    class Recognition {

        // A unique identifier for what has been recognized. Specific to the class, not the instance of the object.

        private final String id;

        // Display name for the recognition (Normal, Mild Non-Proliferative, Proliferative, Severe Non-Proliferative, Moderate Non-Proliferative).

        private final String title;

        // A sortable score for how good the recognition is relative to others. Higher should be better.

        private final Float confidence;

        // Optional location within the source image for the location of the recognized object.

        private RectF location;

        Recognition(
            final String id, final String title, final Float confidence, final RectF location) {

            this.id = id;
            this.title = title;
            this.confidence = confidence;
            this.location = location;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        Float getConfidence() {
            return confidence;
        }

        @SuppressLint("DefaultLocale")
        @Override
        public String toString() {

            String resultString = "";

            if (title != null) {
                // Diagnosis labels (Normal, Mild Non-Proliferative, Proliferative, Severe Non-Proliferative, Moderate Non-Proliferative).
                resultString += title + " ";
            }

            if (confidence != null) {
                // Confidence (Percentage).
                resultString += String.format("(%.1f%%) ", confidence * 100.0f);
            }

            if (location != null) {
                resultString += location + " ";
            }

            return resultString.trim();
        }
    }

    List<Recognition> recognizeImage(Bitmap bitmap);

    void enableStatLogging(final boolean debug);

    String getStatString();

    void close();
}

