package com.snackstudio.sstrain.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.snackstudio.sstrain.assets.Assets;
import com.snackstudio.sstrain.config.GlobalConfiguration;

public class SongLoader {
    private static final String[] SONGFILE_PRIO = {".mp3", ".ogg", ".wav"};

    private static FileHandle loadFile(String path, boolean isDefault) {
        if(isDefault) return Gdx.files.internal(GlobalConfiguration.internalDataPath + path);
        else return Gdx.files.external(GlobalConfiguration.externalDataPath + path);
    }

    public static Music loadSongByName(String name, boolean isDefault) {
        try {
            // try loading the file
            FileHandle handle = loadFile(GlobalConfiguration.externalDataPath + name, isDefault);

            return Gdx.audio.newMusic(handle);
        } catch(Exception e) {
            // if it failed, try loading the file with a different extension (in case the extension was not specified)
            FileHandle handle = null;
            String path = GlobalConfiguration.externalDataPath + name.replaceAll("\\.[a-zA-Z0-9]+$","");

            for(String ext : SONGFILE_PRIO) {
                try {
                    handle = loadFile(path + ext, isDefault);
                    return Gdx.audio.newMusic(handle);
                } catch(Exception e2) {
                    continue;
                }
            }

            return null;
        }
    }

    public static Music loadSongFile() {
        Music result = null;

        if(Assets.selectedBeatmap.metadata.songFile != null)
            result = loadSongByName(Assets.selectedBeatmap.metadata.songFile, Assets.selectedGroup.isDefault);

        if(result == null)
            result = loadSongByName(Assets.selectedBeatmap.metadata.songName, Assets.selectedGroup.isDefault);

        return result;
    }
}
