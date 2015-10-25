package com.snackstudio.sstrain.entities;


import com.snackstudio.sstrain.config.GlobalConfiguration;
import com.snackstudio.sstrain.util.Accuracy;
import com.snackstudio.sstrain.util.SongUtils;

public class Results {
    public static Integer combo;
    public static float accuracy;
    public static int miss;
    public static int bads;
    public static int goods;
    public static int greats;
    public static int perfects;
    public static float maxAccuracy;
    public static float minAccuracy;
    public static float normalizedAccuracy;
    public static float unstableRating;

    public static void clear() {
        combo = 0;
        accuracy = 0;
        miss = 0;
        bads = 0;
        goods = 0;
        greats = 0;
        perfects = 0;
        maxAccuracy = 0;
        minAccuracy = 0;
        normalizedAccuracy = 0;
        unstableRating = 0;
    }


    public static float getAccuracyMultiplierForAccuracy(Accuracy accuracy) {
        if (accuracy == Accuracy.PERFECT) {
            return 1.0f;
        }
        if (accuracy == Accuracy.GREAT) {
            return 0.75f;
        }
        if (accuracy == Accuracy.NICE) {
            return 0.50f;
        }
        if (accuracy == Accuracy.BAD) {
            return 0.25f;
        }
        return 0f;
    }

    public static Accuracy getAccuracyFor(float timing) {
        // Perfect
        if (Math.abs(timing) < SongUtils.overallDiffPerfect[GlobalConfiguration.overallDifficulty] / 1000) {
            return Accuracy.PERFECT;
        }
        if (Math.abs(timing) < SongUtils.overallDiffGreat[GlobalConfiguration.overallDifficulty]/ 1000) {
            return Accuracy.GREAT;
        }
        if (Math.abs(timing) < SongUtils.overallDiffNice[GlobalConfiguration.overallDifficulty]/ 1000) {
            return Accuracy.NICE;
        }
        if (Math.abs(timing) < SongUtils.overallDiffBad[GlobalConfiguration.overallDifficulty]/ 1000) {
            return Accuracy.BAD;
        }
        return Accuracy.MISS;
    }

    // holds and swipes have bigger windows
    public static Accuracy getAccuracyForSwipesAndHolds(float timing) {
        return getAccuracyFor(timing * SWIPE_HOLD_MULTIPLIER);
    }

    public final static float SWIPE_HOLD_MULTIPLIER = 0.5f;
}
