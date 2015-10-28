package com.snackstudio.sstrain.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.ExternalFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.snackstudio.sstrain.config.GlobalConfiguration;
import com.snackstudio.sstrain.entities.BaseMetadata;
import com.snackstudio.sstrain.entities.Beatmap;
import com.snackstudio.sstrain.entities.BeatmapGroup;
import com.snackstudio.sstrain.entities.Metadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Assets {

    public static AssetManager internalManager = new AssetManager(new InternalFileHandleResolver());
    public static AssetManager externalManager = new AssetManager(new ExternalFileHandleResolver());
    public static AssetManager defaultSongManager = new AssetManager(new InternalFileHandleResolver());

    static {
        externalManager.setLoader(List.class, new SimplifiedBeatmapLoader(new ExternalFileHandleResolver()));
        defaultSongManager.setLoader(List.class, new SimplifiedBeatmapLoader(new InternalFileHandleResolver()));
    }

    public static Beatmap selectedBeatmap;
    public static BeatmapGroup selectedGroup;

    public static TextureAtlas atlas;

    public static Skin menuSkin;
    public static Sound noHitTapSound;
    public static Sound niceTapSound;
    public static Sound greatTapSound;
    public static Sound perfectTapSound;
    public static Sound niceSwipeSound;
    public static Sound greatSwipeSound;
    public static Sound perfectSwipeSound;

    public static BitmapFont font;
    public static BitmapFont songFont;

    public static Texture mainMenuBackgroundTexture;
    public static Texture holdBG;

    public static Array<BeatmapGroup> songGroup;

    // In here we'll put everything that needs to be loaded in this format:
    // manager.load("file location in assets", fileType.class);
    //
    // libGDX AssetManager currently supports: Pixmap, Texture, BitmapFont,
    //     TextureAtlas, TiledAtlas, TiledMapRenderer, Music and Sound.
    public static void queueLoading() {
        internalManager.load("textures/textures.pack.atlas", TextureAtlas.class);
        internalManager.load("hitsounds/no_hit_tap.mp3", Sound.class);
        internalManager.load("hitsounds/tap_nice.mp3", Sound.class);
        internalManager.load("hitsounds/tap_great.mp3", Sound.class);
        internalManager.load("hitsounds/tap_perfect.mp3", Sound.class);
        internalManager.load("hitsounds/swipe_nice.mp3", Sound.class);
        internalManager.load("hitsounds/swipe_great.mp3", Sound.class);
        internalManager.load("hitsounds/swipe_perfect.mp3", Sound.class);
        internalManager.load("bigimages/main_menu_background.jpg", Texture.class);
        internalManager.load("images/hold_background.png", Texture.class);
        internalManager.load("fonts/combo-font.fnt", BitmapFont.class);
        internalManager.load("fonts/song-font.fnt", BitmapFont.class);
        reloadBeatmaps();
    }

    // thanks to libgdx, the manager will not actually load maps which were already loaded,
    // so if the same file comes again, it will be skipped
    public static void reloadBeatmaps() {
        // Default songs
        for (String fileName : Gdx.files.internal("default/").file().list()) {
            String fullPath = "default/" + fileName;
            if (Gdx.files.absolute(fullPath).isDirectory() || (!fileName.endsWith(".json")))
                continue;
            System.out.println("Default Load " + fullPath);
            defaultSongManager.load(fullPath, List.class);
        }

        // User added songs
        if (Gdx.files.absolute(GlobalConfiguration.pathToBeatmaps).exists()) {
            for (String fileName : Gdx.files.absolute(GlobalConfiguration.pathToBeatmaps).file().list()) {
                String fullPath = GlobalConfiguration.pathToBeatmaps + fileName;
                if (Gdx.files.absolute(fullPath).isDirectory() || (!fileName.endsWith(".json")))
                    continue;
                System.out.println("External Load " + fullPath);
                externalManager.load(GlobalConfiguration.beatmapPath + fileName, List.class);
            }
        } else {
            Gdx.files.absolute(GlobalConfiguration.pathToBeatmaps).mkdirs();
        }
    }

    // unlike the simple reload, in the hard reload we unload everything from the external manager
    // and force a reload of the beatmaps - this will cause .osz files which weren't extracted
    // to be processed, .osu files to be converted and music files within the .osz packages
    // to be copied over to the /beatmaps/soundfiles/ folder.
    public static void hardReloadBeatmaps() {
        selectedBeatmap = null;
        selectedGroup = null;
        externalManager.clear();
        defaultSongManager.clear();
        reloadBeatmaps();
    }

    //In here we'll create our skin, so we only have to create it once.
    public static void setMenuSkin() {
        if (menuSkin == null)
            menuSkin = new Skin(Gdx.files.internal("skins/menuSkin.json"), internalManager.get("textures/textures.pack.atlas", TextureAtlas.class));
    }

    public static void setTextures() {
        if (atlas == null)
            atlas = internalManager.get("textures/textures.pack.atlas");

        if (mainMenuBackgroundTexture == null)
            mainMenuBackgroundTexture = internalManager.get("bigimages/main_menu_background.jpg");

        if (holdBG == null)
            holdBG = internalManager.get("images/hold_background.png");
    }

    public static void setFonts() {
        if (font == null) {
            font = internalManager.get("fonts/combo-font.fnt");
        }
        if (songFont == null) {
            songFont= internalManager.get("fonts/song-font.fnt");
        }

    }

    public static void setHitsounds() {
        if (noHitTapSound == null)
            noHitTapSound = internalManager.get("hitsounds/no_hit_tap.mp3");
        if (niceTapSound == null)
            niceTapSound = internalManager.get("hitsounds/tap_nice.mp3");
        if (greatTapSound == null)
            greatTapSound = internalManager.get("hitsounds/tap_great.mp3");
        if (perfectTapSound == null)
            perfectTapSound = internalManager.get("hitsounds/tap_perfect.mp3");
        if (niceSwipeSound == null)
            niceSwipeSound = internalManager.get("hitsounds/swipe_nice.mp3");
        if (greatSwipeSound == null)
            greatSwipeSound = internalManager.get("hitsounds/swipe_great.mp3");
        if (perfectSwipeSound == null)
            perfectSwipeSound = internalManager.get("hitsounds/swipe_perfect.mp3");
    }

    @SuppressWarnings("unchecked")
    public static void setSongs() {
        if (songGroup == null) {
            songGroup = new Array<>();
        } else {
            songGroup.clear();
        }

        _setSongs(defaultSongManager, true);
        _setSongs(externalManager, false);
    }

    public static void _setSongs(AssetManager manager, boolean isDefault) {
        Array<String> assets = manager.getAssetNames();
        Map<Long, BeatmapGroup> groupMap = new HashMap<>();

        for (String string : assets) {
            System.out.println("asset name " + string);
            List<Beatmap> beatmaps = manager.get(string, List.class);
            if (!beatmaps.isEmpty()) {
                Metadata metadata = beatmaps.get(0).metadata;
                Long liveId = metadata.id;
                if (groupMap.get(liveId) == null) {
                    BeatmapGroup group = new BeatmapGroup();
                    group.metadata = new BaseMetadata();
                    group.metadata.id = metadata.id;
                    group.metadata.composer = metadata.composer;
                    group.metadata.lyricist = metadata.lyricist;
                    group.metadata.songFile = metadata.songFile;
                    group.metadata.songName = metadata.songName;
                    group.metadata.attribute = metadata.attribute;
                    group.metadata.duration = metadata.duration;
                    group.beatmaps = new Array<>();
                    group.isDefault = isDefault;
                    groupMap.put(liveId, group);
                }

                BeatmapGroup group = groupMap.get(liveId);
                for (Beatmap beatmap : beatmaps) {
                    group.beatmaps.add(beatmap);
                }
                group.beatmaps.sort();
            }
        }
        for (Long liveId : groupMap.keySet()) {
            songGroup.add(groupMap.get(liveId));
        }
        songGroup.sort();
    }

    public static boolean update() {
        return internalManager.update() && externalManager.update();
    }

    public static float getProgress() {
        return (internalManager.getProgress() + externalManager.getProgress()) / 2;
    }
}
