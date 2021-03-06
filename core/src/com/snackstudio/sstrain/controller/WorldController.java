package com.snackstudio.sstrain.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.snackstudio.sstrain.World;
import com.snackstudio.sstrain.assets.Assets;
import com.snackstudio.sstrain.config.GlobalConfiguration;
import com.snackstudio.sstrain.entities.Results;
import com.snackstudio.sstrain.objects.AccuracyMarker;
import com.snackstudio.sstrain.objects.AccuracyPopup;
import com.snackstudio.sstrain.objects.Circle;
import com.snackstudio.sstrain.objects.TapZone;
import com.snackstudio.sstrain.screens.ResultsScreen;
import com.snackstudio.sstrain.util.Accuracy;
import com.snackstudio.sstrain.util.SongUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldController implements Music.OnCompletionListener {
    private World world;

    private final Array<Circle> circles;
    private final Array<TapZone> tapZones;
    private final Array<AccuracyMarker> accuracyMarkers;
    private final Array<AccuracyPopup> accuracyPopups;

    public boolean done;
    private boolean hasMusic;

    public int combo;

    private int badCount;
    private int goodCount;
    private int greatCount;
    private int perfectCount;
    private int missCount;

    private int largestCombo;
    private List<Accuracy> accuracyList;

    Map<Integer, Integer> pointerToZoneId = new HashMap<>();
    Map<Integer, Vector2> pointerToCoordinates = new HashMap<>();

    Float aPosition;
    Float bPosition;

    float songStart;
    boolean songStarted;
    boolean isABRepeatMode;

    private Music theSong;
    private Integer syncMode;

    public WorldController(World world) {
        this.world = world;
        this.circles = world.getCircles();
        this.tapZones = world.getTapZones();
        this.accuracyMarkers = world.getAccuracyMarkers();
        this.combo = 0;
        this.badCount = 0;
        this.goodCount = 0;
        this.greatCount = 0;
        this.perfectCount = 0;
        this.missCount = 0;
        this.largestCombo = 0;
        this.accuracyList = new ArrayList<>();
        this.accuracyPopups = world.getAccuracyPopups();
        this.songStart = world.delay;
        this.songStarted = false;
        this.mtime = 0f;
        this.lastmtime = 0f;
        this.time = 0f;
        this.oldTime = 0f;
        this.timeSyncAcc = 0f;
        this.syncMode = GlobalConfiguration.syncMode;
        this.isABRepeatMode = GlobalConfiguration.playbackMode != null && GlobalConfiguration.playbackMode.equals(SongUtils.GAME_MODE_ABREPEAT);

        if (GlobalConfiguration.playbackRate == null || GlobalConfiguration.playbackRate.compareTo(1.0f) == 0) {
            theSong = SongLoader.loadSongFile();
        }
        if (isABRepeatMode) {
            aPosition = GlobalConfiguration.aTime;
            // set a buffer of 3 seconds previous to the fragment we're practicing
            aPosition = aPosition - 3f < 0.0f ? 0.0f : aPosition - 3f;
            bPosition = GlobalConfiguration.bTime;
            if (GlobalConfiguration.playbackRate != null && GlobalConfiguration.playbackRate.compareTo(1.0f) != 0)
                aPosition = aPosition / GlobalConfiguration.playbackRate;
            if (GlobalConfiguration.playbackRate != null && GlobalConfiguration.playbackRate.compareTo(1.0f) != 0)
                bPosition = bPosition / GlobalConfiguration.playbackRate;

            time = aPosition;
        }
        this.hasMusic = theSong != null;
    }

    private void resetMarks() {
        for (Circle circle : circles) {
            circle.accuracy = null;
            circle.visible = false;
            circle.holding = false;
            circle.soundPlayed = false;
            circle.miss = false;
            circle.processed = false;
            circle.position.y = 0f;
            circle.alpha = 1;
        }
        this.combo = 0;
        this.badCount = 0;
        this.goodCount = 0;
        this.greatCount = 0;
        this.perfectCount = 0;
        this.missCount = 0;
        this.largestCombo = 0;
        this.timeSyncAcc = 0f;
        accuracyMarkers.clear();
        accuracyPopups.clear();
    }


    @Override
    public void onCompletion(Music music) {
        if (isABRepeatMode && !done) {
            resetMarks();
            if (hasMusic) {
                theSong.pause();
                theSong.setPosition(aPosition);
                theSong.play();
                lastmtime = theSong.getPosition();
                time = lastmtime + world.delay;
                timeSyncAcc = 0;
            } else {
                time = aPosition;
            }
            return;

        }
        if (hasMusic) {
            music.dispose();
        }
        done = true;
        if (this.largestCombo < this.combo) {
            this.largestCombo = combo;
        }
        Results.bads = badCount;
        Results.goods = goodCount;
        Results.greats = greatCount;
        Results.perfects = perfectCount;
        Results.miss = missCount;

        Results.combo = largestCombo;
        Results.accuracy = calculateAccuracy();
        Results.normalizedAccuracy = calculateNormalizedAccuracy();
        accuracyMarkers.clear();
        accuracyPopups.clear();
        circles.clear();
        tapZones.clear();
        ((Game) Gdx.app.getApplicationListener()).setScreen(new ResultsScreen());
    }


    private float calculateNormalizedAccuracy() {
        float sum = 0f;
        for (Accuracy accuracy : accuracyList) {
            sum += Results.getAccuracyMultiplierForAccuracy(accuracy);
        }
        return sum / accuracyList.size();
    }

    public float mtime;
    public float time;
    public float lastmtime;
    public float oldTime;
    public float timeSyncAcc;


    public void update(float delta) {
        if (!world.started)
            return;

        if (world.paused)
            return;

        // some song files may start immediately and the beatmaps may have notes which start immediately with the songs
        // so, allow them to have a (note_speed + 0.5) seconds of pre-time
        if (!songStarted) {
            songStart -= delta;
            if (songStart <= 0) {
                songStarted = true;
                if (hasMusic) {
                    theSong.setLooping(false);
                    theSong.setOnCompletionListener(this);
                    theSong.setVolume(GlobalConfiguration.songVolume / 100f);
                    theSong.play();
                    if (aPosition != null)
                        theSong.setPosition(aPosition);
                    lastmtime = theSong.getPosition();
                    time = lastmtime + world.delay;
                    timeSyncAcc = 0;
                } else {
                    if (aPosition != null) {
                        lastmtime = aPosition;
                        time = lastmtime + world.delay;
                        timeSyncAcc = 0;
                    }
                }
            }
        }
        // sync music and beatmap if there's music
        sync(delta);

        for (Circle mark : circles) {
            mark.update(time);
        }
        for (AccuracyMarker marker : world.getAccuracyMarkers()) {
            marker.update(delta);
        }
        for (AccuracyPopup popup : world.getAccuracyPopups()) {
            popup.update(delta);
        }
        processInput();
    }

    private void sync(float delta) {
        float theTime = time;
        if (hasMusic) {
            switch (syncMode) {
                case 0: {
                    mtime = theSong.getPosition();
                    if (mtime <= 0f && !songStarted) {
                        time += delta;
                        // use the first 300 ms of the song to sync
                    } else if (songStarted && mtime < 0.3f) {
                        time = mtime + world.delay;
                        lastmtime = mtime;
                        // if we haven't synced in a while
                    } else if (timeSyncAcc > 0.5f) {
                        lastmtime = mtime;
                        time = mtime + world.delay;
                        timeSyncAcc = 0f;
                        // if the time didn't update we interpolate the delta
                    } else if (lastmtime == mtime) {
                        time += delta;
                        timeSyncAcc += delta;
                        // if the new reading is behind the previous one, we interpolate the delta
                    } else if (mtime < lastmtime) {
                        time = lastmtime + world.delay + delta;
                        lastmtime = lastmtime + delta;
                        timeSyncAcc += delta;
                        // if the new reading is way ahead, we interpolate the delta
                    } else if (mtime > oldTime + 2 * delta) {
                        time = lastmtime + world.delay + delta;
                        lastmtime = lastmtime + delta;
                        timeSyncAcc += delta;
                    } else {
                        lastmtime = mtime;
                        time = mtime + world.delay;
                        timeSyncAcc = 0f;
                    }
                    // smoothen transitions if the new time is ahead or behind of the time + delta
                    float theDiff = time - (theTime + delta);
                    time = theTime + delta + theDiff * 1 / Gdx.graphics.getFramesPerSecond();
                    break;
                }
                case 1: {
                    mtime = theSong.getPosition();
                    if (mtime <= lastmtime) {
                        time += delta;
                    } else {
                        time = mtime + world.delay;
                    }
                    break;
                }
                case 2: {
                    mtime = theSong.getPosition();
                    if (timeSyncAcc < 0.5f) {
                        if (mtime <= lastmtime) {
                            time += delta;
                        } else {
                            time = mtime + world.delay;
                            lastmtime = mtime;
                        }
                    } else {
                        time += delta;
                    }
                    timeSyncAcc += delta;
                    break;
                }
                default:
                    time += delta;
                    break;
            }

        } else
        // otherwise just play the beatmap
        {
            time += delta;
        }
        oldTime = time;
    }

    private float calculateAccuracy() {
        float sum = 0f;
        List<Float> high = new ArrayList<>();
        List<Float> low = new ArrayList<>();
        for (AccuracyMarker hit : world.getAccuracyMarkers()) {
            sum += hit.getTime();
        }
        float average = sum / world.getAccuracyMarkers().size;
        for (AccuracyMarker value : world.getAccuracyMarkers()) {
            if (value.getTime() >= average) {
                high.add(value.getTime());
            } else {
                low.add(value.getTime());
            }
        }

        Results.minAccuracy = calcAverage(low);
        Results.maxAccuracy = calcAverage(high);
        Results.unstableRating = 10 * calcDeviation(world.getAccuracyMarkers());

        return sum / world.getAccuracyMarkers().size;
    }

    private float calcAverage(List<Float> values) {
        float sum = 0;
        for (Float value : values) {
            sum += value;
        }
        return sum / values.size();
    }

    private float calcDeviation(Array<AccuracyMarker> values) {
        if (values.size == 0)
            return 0f;

        float sum = 0f;
        for (AccuracyMarker value : values) {
            sum += value.getTime();
        }

        float mean = sum / values.size;
        sum = 0f;
        for (AccuracyMarker value : values) {
            sum += (value.getTime() - mean) * (value.getTime() - mean);
        }
        return (float) Math.sqrt(sum / (values.size - 1));
    }


    private void processAccuracy(Accuracy accuracy, Accuracy accuracy2, boolean isHold) {
        if (!isHold) {
            if (accuracy == Accuracy.BAD) {
                badCount++;
                if (combo > largestCombo) {
                    largestCombo = combo;
                }
                combo = 0;
                world.combo = 0;
            } else if (accuracy == Accuracy.NICE) {
                goodCount++;
                if (combo > largestCombo) {
                    largestCombo = combo;
                }
                combo = 0;
                world.combo = 0;
            } else if (accuracy == Accuracy.GREAT) {
                greatCount++;
                combo++;
                world.combo = combo;
            } else if (accuracy == Accuracy.PERFECT) {
                perfectCount++;
                combo++;
                world.combo = combo;
            } else {
                missCount++;
                if (combo > largestCombo) {
                    largestCombo = combo;
                }
                combo = 0;
                world.combo = 0;
            }
        } else {
            // no combo break
            Accuracy lowest = accuracy.compareTo(accuracy2) >= 0 ? accuracy2 : accuracy;

            if (lowest == Accuracy.BAD) {
                badCount++;
            } else if (lowest == Accuracy.NICE) {
                goodCount++;
            } else if (lowest == Accuracy.GREAT) {
                greatCount++;
            } else if (lowest == Accuracy.PERFECT) {
                perfectCount++;
            } else {
                missCount++;
            }
            if (accuracy2.compareTo(Accuracy.NICE) > 0) {
                combo++;
                world.combo = combo;
            } else {
                if (combo > largestCombo) {
                    largestCombo = combo;
                }
                combo = 0;
                world.combo = 0;

            }

        }
    }

    private void playTapSoundForAccuracy(Accuracy accuracy) {
        if (accuracy == Accuracy.PERFECT) {
            Assets.perfectTapSound.play(GlobalConfiguration.feedbackVolume / 100f);
        }
        if (accuracy == Accuracy.GREAT) {
            Assets.greatTapSound.play(GlobalConfiguration.feedbackVolume / 100f);
        }
        if (accuracy == Accuracy.NICE) {
            Assets.niceTapSound.play(GlobalConfiguration.feedbackVolume / 100f);
        }
        // bad shares sound with nice
        if (accuracy == Accuracy.BAD) {
            Assets.niceTapSound.play(GlobalConfiguration.feedbackVolume / 100f);
        }
    }

    private void playSwipeSoundForAccuracy(Accuracy accuracy) {
        if (accuracy == Accuracy.PERFECT) {
            Assets.perfectSwipeSound.play(GlobalConfiguration.feedbackVolume / 100f);
        }
        if (accuracy == Accuracy.GREAT) {
            Assets.greatSwipeSound.play(GlobalConfiguration.feedbackVolume / 100f);
        }
        if (accuracy == Accuracy.NICE) {
            Assets.niceSwipeSound.play(GlobalConfiguration.feedbackVolume / 100f);
        }
        // bad shares sound with nice
        if (accuracy == Accuracy.BAD) {
            Assets.niceSwipeSound.play(GlobalConfiguration.feedbackVolume / 100f);
        }
    }

    public void pressed(int screenX, int screenY, int pointer, int button, float ppuX, float ppuY, int width, int height) {
        playMusicOnDemand();

        int matchedId = getTapZoneForCoordinates(screenX, screenY, ppuX, ppuY, width, height, pointer);

        if (matchedId == -1) {
            return;
        }

        hit(matchedId);
    }

    public void released(int screenX, int screenY, int pointer, int button, float ppuX, float ppuY, int width, int height) {
        int matchedId = -1;
        for (TapZone zone : tapZones) {
            if (zone.getId().equals(pointerToZoneId.get(pointer))) {
                matchedId = zone.getId();
                pointerToZoneId.remove(pointer);
                zone.pressed = false;
            }
            if (pointerToCoordinates.get(pointer) != null) {
                pointerToCoordinates.remove(pointer);
            }
        }

        if (matchedId == -1) {
            return;
        }
        release(matchedId);

    }

    private void playMusicOnDemand() {
        if (!world.started) {
            world.started = true;
        } else {
            if (world.paused) {
                world.paused = false;
                if (hasMusic) {
                    theSong.setPosition(lastmtime);
                    time = lastmtime + world.delay;
                    theSong.play();
                }
            }
        }
    }

    private int getTapZoneForCoordinates(int screenX, int screenY, float ppuX, float ppuY, int width, int height, int pointer) {
        float centerX = world.offsetX + width / 2;
        float centerY = world.offsetY + height * 0.20f;

        float relativeX = (screenX - centerX) / ppuX;
        float relativeY = (-screenY + centerY) / ppuY;

        float circleRadius = 400 * 0.065f;

        int matchedId = -1;
        for (TapZone zone : tapZones) {
            float x = zone.getPosition().x;
            if (x - 2 * circleRadius < relativeX && relativeX < x + 2 * circleRadius && relativeY < -125) {
                matchedId = zone.getId();
                zone.pressed = true;
                pointerToZoneId.put(pointer, matchedId);
                break;
            }
        }
        return matchedId;
    }

    private void hit(int matchedId) {
        boolean hit = false;
        for (Circle mark : circles) {
            if (!mark.waiting) {
                continue;
            }
            // swiped notes don't register on hit.
            if (!mark.note.status.equals(SongUtils.NOTE_NO_SWIPE) && mark.destination == matchedId) {
                break;
            }

            if (mark.destination == (matchedId)) {
                Accuracy accuracy = mark.hit();
                // if we tap too early, ignore this tap
                if (accuracy == Accuracy.NONE)
                    continue;

                hit = true;

                playTapSoundForAccuracy(accuracy);
                processAccuracy(accuracy, null, false);

                accuracyPopups.add(new AccuracyPopup(accuracy, mark.hitTime < 0));
                accuracyMarkers.add(new AccuracyMarker(mark.hitTime));
                accuracyList.add(accuracy);
                // 1 mark per tap
                break;
            }

        }
        if (!hit) {
            Assets.noHitTapSound.play(GlobalConfiguration.feedbackVolume / 100f);
        }
    }

    private void release(int matchedId) {
        for (Circle mark : circles) {
            if (mark.previousNote == null || !mark.previousNote.hold) {
                continue;
            }

            if (!mark.previousNote.holding) {
                continue;
            }

            if (matchedId == mark.destination) {
                Accuracy accuracy = mark.release();
                // releasing in the same zone as an upcoming hold can cause 'None' results
                if (accuracy == Accuracy.NONE)
                    continue;
                if (accuracy != Accuracy.MISS) {
                    playTapSoundForAccuracy(accuracy);
                }
                accuracyPopups.add(new AccuracyPopup(accuracy, mark.hitTime < 0));
                if (accuracy != Accuracy.MISS) {
                    accuracyMarkers.add(new AccuracyMarker(mark.hitTime));
                }
                processAccuracy(mark.accuracy, null, false);
                accuracyList.add(accuracy);
                // 1 mark per release
                break;
            }
        }
    }

    private void processInput() {
        boolean done = true;
        for (Circle mark : circles) {
            if (done && !mark.isDone()) {
                done = false;
            }
            if (!mark.processed && mark.isDone()) {
                mark.processed = true;
                if (mark.accuracy == Accuracy.MISS) {
                    accuracyPopups.add(new AccuracyPopup(Accuracy.MISS, mark.hitTime > 0));
                    processAccuracy(mark.accuracy, null, false);
                    accuracyList.add(mark.accuracy);
                }
            }
        }
        if (isABRepeatMode) {
            if (time + world.delay >= bPosition) {
                resetMarks();
                if (hasMusic) {
                    theSong.pause();
                    theSong.setPosition(aPosition);
                    theSong.play();
                    lastmtime = theSong.getPosition();
                    time = lastmtime + world.delay;
                    timeSyncAcc = 0;
                }
            }
        }
        if (done && !hasMusic) {
            this.onCompletion(null);
        }
        if (time > Assets.selectedBeatmap.metadata.duration / (GlobalConfiguration.playbackRate == null ? 1.0f : GlobalConfiguration.playbackRate) + world.delay) {
            if (!hasMusic)
                this.onCompletion(null);
        }
    }

    public void back() {
        if (world.started) {
            // if the game was paused and we pressed back again, we skip to the results screen
            if (world.paused) {
                this.done = true;
                this.onCompletion(theSong);
                return;
            }
            world.paused = true;
            if (hasMusic) {
                theSong.pause();
                lastmtime = theSong.getPosition();
                time = lastmtime + world.delay;
                timeSyncAcc = 0;
            }
        }
    }

    public void dragged(int screenX, int screenY, int pointer, float ppuX, float ppuY, int width, int height) {
        Vector2 coords = pointerToCoordinates.get(pointer);
        if (coords == null) {
            coords = new Vector2();
            coords.x = screenX;
            coords.y = screenY;
            pointerToCoordinates.put(pointer, coords);
            // first entry - just register the position.
            return;
        }
        float centerX = world.offsetX + width / 2;
        float centerY = world.offsetY + height * 0.20f;

        float relativeBeforeX = (coords.x - centerX) / ppuX;
        float relativeAfterX = (screenX - centerX) / ppuX;
        float relativeY = (-screenY + centerY) / ppuY;

        float circleRadius = 400 * 0.065f;


        for (TapZone zone : tapZones) {
            // /-----TAPZONE------\
            // |----|--------|----|
            //     LST      RST
            // LST: Left Swipe Trigger
            // RST: Right Swipe Trigger
            if (relativeBeforeX < zone.getPosition().x + circleRadius / 2
                    && zone.getPosition().x + circleRadius / 2 < relativeAfterX
                    && relativeY < -125) {
                swipeRight(zone.getId());
            } else if (relativeBeforeX > zone.getPosition().x - circleRadius / 2
                    && zone.getPosition().x - circleRadius / 2 > relativeAfterX
                    && relativeY < -125) {
                swipeLeft(zone.getId());
            }
            // else do nothing since we're swiping the same side
        }

        coords.x = screenX;
        coords.y = screenY;
    }

    private void swipeLeft(int matchedId) {
        for (Circle mark : circles) {
            if (!mark.waiting) {
                continue;
            }
            if (mark.destination == matchedId && (mark.note.status.equals(SongUtils.NOTE_NO_SWIPE) || mark.note.status.equals(SongUtils.NOTE_SWIPE_RIGHT))) {
                // we know the notes are in time order so, if the previous note was a tap and wasn't tapped, we ignore the swipe.
                // or there's a swipe in the other direction on the same lane
                break;
            }
            if (mark.note.status.equals(SongUtils.NOTE_SWIPE_LEFT)) {
                if (mark.destination == (matchedId)) {
                    Accuracy accuracy = mark.swipeLeft();
                    // if we swipe too early, ignore this tap
                    if (accuracy == Accuracy.NONE)
                        continue;

                    playSwipeSoundForAccuracy(accuracy);
                    processAccuracy(accuracy, null, false);

                    accuracyPopups.add(new AccuracyPopup(accuracy, mark.hitTime < 0));
                    accuracyMarkers.add(new AccuracyMarker(mark.hitTime));
                    accuracyList.add(accuracy);
                    // 1 mark per tap
                    break;
                }
            }
        }
    }

    private void swipeRight(int matchedId) {
        for (Circle mark : circles) {
            if (!mark.waiting) {
                continue;
            }
            if (mark.destination == matchedId && (mark.note.status.equals(SongUtils.NOTE_NO_SWIPE) || mark.note.status.equals(SongUtils.NOTE_SWIPE_LEFT))) {
                // we know the notes are in time order so, if the previous note was a tap and wasn't tapped, we ignore the swipe.
                // or there's a swipe in the other direction on the same lane
                break;
            }
            if (mark.note.status.equals(SongUtils.NOTE_SWIPE_RIGHT)) {
                if (mark.destination == (matchedId)) {
                    Accuracy accuracy = mark.swipeRight();
                    // if we swipe too early, ignore this tap
                    if (accuracy == Accuracy.NONE)
                        continue;

                    playSwipeSoundForAccuracy(accuracy);
                    processAccuracy(accuracy, null, false);

                    accuracyPopups.add(new AccuracyPopup(accuracy, mark.hitTime < 0));
                    accuracyMarkers.add(new AccuracyMarker(mark.hitTime));
                    accuracyList.add(accuracy);
                    // 1 mark per tap
                    break;
                }
            }
        }
    }
}
