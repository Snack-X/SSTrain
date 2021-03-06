package com.snackstudio.sstrain.entities;

import com.badlogic.gdx.utils.Array;
import com.snackstudio.sstrain.config.GlobalConfiguration;
import com.snackstudio.sstrain.util.SongUtils;

public class BeatmapGroup implements Comparable<BeatmapGroup>{
    public BaseMetadata metadata;
    public Array<Beatmap> beatmaps;
    public boolean isDefault;

    public String toString()
    {
        return "["+SongUtils.getAttribute(metadata.attribute) + "] "+ metadata.songName.replaceAll("\\\\n", " ");
    }

    @Override
    public int compareTo(BeatmapGroup o) {
        if (GlobalConfiguration.sortOrder == SongUtils.SORTING_MODE_ASCENDING) {
            if (GlobalConfiguration.sortMode == SongUtils.SORTING_MODE_FILE_NAME)
                return metadata.songFile.compareTo(o.metadata.songFile);
            if (GlobalConfiguration.sortMode == SongUtils.SORTING_MODE_SONG_NAME)
                return metadata.songName.compareTo(o.metadata.songName);
            if (GlobalConfiguration.sortMode == SongUtils.SORTING_MODE_ATTRIBUTE)
                return metadata.attribute.compareTo(o.metadata.attribute);
            if (GlobalConfiguration.sortMode == SongUtils.SORTING_MODE_SONG_ID)
                return metadata.id.intValue() - o.metadata.id.intValue();
        }
        else if (GlobalConfiguration.sortOrder == SongUtils.SORTING_MODE_DESCENDING){
            if (GlobalConfiguration.sortMode == SongUtils.SORTING_MODE_FILE_NAME)
                return -metadata.songFile.compareTo(o.metadata.songFile);
            if (GlobalConfiguration.sortMode == SongUtils.SORTING_MODE_SONG_NAME)
                return -metadata.songName.compareTo(o.metadata.songName);
            if (GlobalConfiguration.sortMode == SongUtils.SORTING_MODE_ATTRIBUTE)
                return -metadata.attribute.compareTo(o.metadata.attribute);
            if (GlobalConfiguration.sortMode == SongUtils.SORTING_MODE_SONG_ID)
                return -metadata.id.intValue() - o.metadata.id.intValue();
            return -Double.compare(metadata.duration, o.metadata.duration);
        }
        return Double.compare(metadata.duration, o.metadata.duration);
    }
}
