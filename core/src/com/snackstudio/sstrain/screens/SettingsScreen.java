package com.fteams.sstrain.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fteams.sstrain.assets.Assets;
import com.fteams.sstrain.config.GlobalConfiguration;
import com.fteams.sstrain.util.SongUtils;

@SuppressWarnings("unchecked")
public class SettingsScreen extends ChangeListener implements Screen, InputProcessor {
    private Texture texture = Assets.mainMenuBackgroundTexture;
    private Image splashImage = new Image(texture);

    private Stage stage = new Stage();
    private Label titleLabel = new Label("Settings/設定", Assets.menuSkin, "default");

    private Label songVolumeLabel = new Label("Song Volume", Assets.menuSkin, "song_style_result");
    private Label feedbackVolumeLabel = new Label("Touch Feedback Volume", Assets.menuSkin, "song_style_result");
    private Label offsetLabel = new Label("Global offset", Assets.menuSkin, "song_style_result");
    private Label inputOffsetLabel = new Label("Input offset", Assets.menuSkin, "song_style_result");
    private Label noteSpeedLabel = new Label("Note Speed (Approach Rate)", Assets.menuSkin, "song_style_result");
    private Label pathToBeatmaps = new Label("Path to Beatmaps", Assets.menuSkin, "song_style_result");

    private TextButton volumeSettingsTabButton = new TextButton("Volume Settings", Assets.menuSkin, "item1");
    private TextButton offsetSettingsTabButton = new TextButton("Timing Settings", Assets.menuSkin, "item1");
    private TextButton otherSettingsTabButton = new TextButton("Other", Assets.menuSkin, "item1");

    private Table tabbedPane = new Table();
    private Stack content = new Stack();

    private Label songVolumeValueLabel;
    private Label feedbackVolumeValueLabel;
    private Label offsetValueLabel;
    private Label inputOffsetValueLabel;
    private Label noteSpeedValueLabel;

    private Slider songVolumeSlider;
    private Slider feedbackVolumeSlider;
    private Slider offsetSlider;
    private Slider inputOffsetSlider;
    private Slider noteSpeedSlider;

    private CheckBox playHintSoundCheckbox;
    private CheckBox syncModeCheckbox;
    private CheckBox sortingModeChooser;

    private String[] sortingModes = {"File Name", "Song Name", "Song Id", "Attribute", "Duration"};

    private final static boolean DEBUG = false;

    private Label pathValueLabel = new Label(GlobalConfiguration.pathToBeatmaps, Assets.menuSkin, "song_style_result");

    private TextButton returnButton = new TextButton("Save and Return", Assets.menuSkin, "item1");
    private TextButton reloadBeatmaps = new TextButton("Reload Beatmaps!", Assets.menuSkin, "item1");

    private Integer newVolume;
    private Integer newFeedbackVolume;
    private Integer newGlobalOffset;
    private Integer newInputOffset;
    private Boolean newHitSoundsSetting;
    private Integer newSortingMode;
    private Integer newSyncMode;
    private Integer newNoteSpeed;

    @Override
    public void show() {
        float fontScale = stage.getHeight() / GlobalConfiguration.BASE_HEIGHT;
        splashImage.setSize(stage.getWidth(), stage.getHeight());
        stage.addActor(splashImage);

        volumeSettingsTabButton.getLabel().setFontScale(fontScale * 0.7f);
        offsetSettingsTabButton.getLabel().setFontScale(fontScale * 0.7f);
        otherSettingsTabButton.getLabel().setFontScale(fontScale * 0.7f);
        returnButton.getLabel().setFontScale(fontScale * 0.7f);
        reloadBeatmaps.getLabel().setFontScale(fontScale * 0.7f);

        newVolume = GlobalConfiguration.songVolume;
        newFeedbackVolume = GlobalConfiguration.feedbackVolume;
        newGlobalOffset = GlobalConfiguration.offset;
        newInputOffset = GlobalConfiguration.inputOffset;
        newNoteSpeed = GlobalConfiguration.noteSpeed;
        newHitSoundsSetting = GlobalConfiguration.playHintSounds;
        newSortingMode = GlobalConfiguration.sortMode;
        newSyncMode = GlobalConfiguration.syncMode;

        ButtonGroup<TextButton> buttonGroup = new ButtonGroup<>();
        buttonGroup.add(volumeSettingsTabButton);
        buttonGroup.add(offsetSettingsTabButton);
        buttonGroup.add(otherSettingsTabButton);
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(1);
        buttonGroup.setUncheckLast(true);
        volumeSettingsTabButton.setChecked(true);

        // settings title
        titleLabel.setFontScale(fontScale * 0.6f);
        tabbedPane.add(titleLabel).padBottom(stage.getHeight() * 0.05f).colspan(2).fillX().row();

        /*
        buttons info:
        width: 0.3
        spawn between 0.1 and 0.4
         */
        // settings buttons
        tabbedPane.setDebug(DEBUG);
        tabbedPane.add(volumeSettingsTabButton).padTop(stage.getHeight() * 0.005f).padBottom(stage.getHeight() * 0.005f).width(stage.getWidth() * 0.25f).height(stage.getHeight() * 0.14f);
        tabbedPane.add().width(stage.getWidth() * 0.1f).row();
        tabbedPane.add(offsetSettingsTabButton).padTop(stage.getHeight() * 0.005f).padBottom(stage.getHeight() * 0.005f).width(stage.getWidth() * 0.25f).height(stage.getHeight() * 0.14f);
        tabbedPane.add().width(stage.getWidth() * 0.1f).row();
        tabbedPane.add(otherSettingsTabButton).padTop(stage.getHeight() * 0.005f).padBottom(stage.getHeight() * 0.005f).width(stage.getWidth() * 0.25f).height(stage.getHeight() * 0.14f);
        tabbedPane.add().width(stage.getWidth() * 0.1f).row();
        tabbedPane.setX(stage.getWidth() * 0.225f);
        tabbedPane.setY(stage.getHeight() / 2);

        tabbedPane.add(returnButton).padTop(stage.getHeight() * 0.005f).padBottom(stage.getHeight() * 0.005f).width(stage.getWidth() * 0.25f).height(stage.getHeight() * 0.14f);
        tabbedPane.add().width(stage.getWidth() * 0.1f).row();

        // volume block
        songVolumeLabel.setFontScale(fontScale);
        songVolumeValueLabel = new Label(Integer.toString(GlobalConfiguration.songVolume), Assets.menuSkin, "song_style_result");
        songVolumeValueLabel.setFontScale(fontScale);

        songVolumeSlider = new Slider(0, 100f, 1f, false, Assets.menuSkin);
        songVolumeSlider.setValue(GlobalConfiguration.songVolume);
        songVolumeSlider.addListener(this);

        feedbackVolumeLabel.setFontScale(fontScale);
        feedbackVolumeValueLabel = new Label(Integer.toString(GlobalConfiguration.feedbackVolume), Assets.menuSkin, "song_style_result");
        feedbackVolumeValueLabel.setFontScale(fontScale);

        feedbackVolumeSlider = new Slider(0, 100f, 1f, false, Assets.menuSkin);
        feedbackVolumeSlider.setValue(GlobalConfiguration.feedbackVolume);
        feedbackVolumeSlider.addListener(this);

        playHintSoundCheckbox = new CheckBox("Hint Sounds (" + (GlobalConfiguration.playHintSounds ? "X" : " ") + ")", Assets.menuSkin);
        playHintSoundCheckbox.getLabel().setFontScale(fontScale);
        playHintSoundCheckbox.getImageCell().width(0);
        playHintSoundCheckbox.setChecked(GlobalConfiguration.playHintSounds);
        playHintSoundCheckbox.addListener(this);

        final Table volumeTable = new Table();
        volumeTable.setHeight(stage.getHeight() * 0.7f);
        volumeTable.setWidth(stage.getWidth() * 0.6f);
        // song volume
        volumeTable.add(songVolumeLabel).width(stage.getWidth() * 0.3f).padTop(stage.getHeight() * 0.01f).padBottom(stage.getHeight() * 0.01f).left();
        volumeTable.add().width(stage.getWidth() * 0.25f);
        volumeTable.add(songVolumeValueLabel).width(stage.getWidth() * 0.05f).padTop(stage.getHeight() * 0.01f).padBottom(stage.getHeight() * 0.01f).right().row();
        volumeTable.add(songVolumeSlider).width(stage.getWidth() * 0.6f).height(songVolumeLabel.getHeight() * fontScale).padTop(stage.getHeight() * 0.01f).padBottom(stage.getHeight() * 0.01f).colspan(3).row();
        volumeTable.add().height(songVolumeLabel.getHeight()).row();

        // feedback volume
        volumeTable.add(feedbackVolumeLabel).width(stage.getWidth() * 0.3f).padTop(stage.getHeight() * 0.01f).padBottom(stage.getHeight() * 0.01f).left();
        volumeTable.add().width(stage.getWidth() * 0.25f);
        volumeTable.add(feedbackVolumeValueLabel).width(stage.getWidth() * 0.05f).padTop(stage.getHeight() * 0.01f).padBottom(stage.getHeight() * 0.01f).right().row();
        volumeTable.add(feedbackVolumeSlider).width(stage.getWidth() * 0.6f).height(songVolumeLabel.getHeight() * fontScale).padTop(stage.getHeight() * 0.01f).padBottom(stage.getHeight() * 0.01f).colspan(3).row();
        volumeTable.add().height(songVolumeLabel.getHeight()).row();

        volumeTable.add(playHintSoundCheckbox).height(songVolumeLabel.getHeight() * fontScale).colspan(3).left().row();
        volumeTable.add().expand().fill().row();

        volumeTable.setDebug(DEBUG);

        content.add(volumeTable);

        // timing block
        offsetLabel.setFontScale(fontScale);
        offsetValueLabel = new Label((GlobalConfiguration.offset > 0 ? "+" : "") + Integer.toString(GlobalConfiguration.offset) + " ms.", Assets.menuSkin, "song_style_result");
        offsetValueLabel.setFontScale(fontScale);

        offsetSlider = new Slider(-1000f, 1000f, 25f, false, Assets.menuSkin);
        offsetSlider.setValue(GlobalConfiguration.offset);
        offsetSlider.addListener(this);

        inputOffsetLabel.setFontScale(fontScale);
        inputOffsetValueLabel = new Label((GlobalConfiguration.inputOffset > 0 ? "+" : "") + Integer.toString(GlobalConfiguration.inputOffset) + " ms.", Assets.menuSkin, "song_style_result");
        inputOffsetValueLabel.setFontScale(fontScale);

        inputOffsetSlider = new Slider(-250f, 250f, 1f, false, Assets.menuSkin);
        inputOffsetSlider.setValue(GlobalConfiguration.inputOffset);
        inputOffsetSlider.addListener(this);

        // note speed
        noteSpeedSlider = new Slider(0, 10, 1, false, Assets.menuSkin);
        noteSpeedSlider.setValue(GlobalConfiguration.noteSpeed);
        noteSpeedSlider.addListener(this);

        noteSpeedLabel.setFontScale(fontScale);
        noteSpeedValueLabel = new Label(Integer.toString(GlobalConfiguration.noteSpeed) + "", Assets.menuSkin, "song_style_result");
        noteSpeedValueLabel.setFontScale(fontScale);

        syncModeCheckbox = new CheckBox("Sync Mode: " + SongUtils.syncModes[newSyncMode], Assets.menuSkin);
        syncModeCheckbox.getLabel().setFontScale(fontScale);
        syncModeCheckbox.getImageCell().width(0);
        syncModeCheckbox.addListener(this);


        final Table offsetTable = new Table();
        // global offset
        offsetTable.setHeight(stage.getHeight() * 0.7f);
        offsetTable.setWidth(stage.getWidth() * 0.6f);
        offsetTable.add(offsetLabel).width(stage.getWidth() * 0.3f).padTop(stage.getHeight() * 0.01f).padBottom(stage.getHeight() * 0.01f);
        offsetTable.add().width(stage.getWidth() * 0.20f);
        offsetTable.add(offsetValueLabel).width(stage.getWidth() * 0.10f).padTop(stage.getHeight() * 0.01f).padBottom(stage.getHeight() * 0.01f).right().row();
        offsetTable.add(offsetSlider).width(stage.getWidth() * 0.6f).height(offsetLabel.getHeight() * fontScale).padTop(stage.getHeight() * 0.01f).padBottom(stage.getHeight() * 0.01f).colspan(3).row();
        offsetTable.add().height(offsetValueLabel.getHeight()).row();

        // input offset
        offsetTable.add(inputOffsetLabel).width(stage.getWidth() * 0.3f).padTop(stage.getHeight() * 0.01f).padBottom(stage.getHeight() * 0.01f).fillX();
        offsetTable.add().width(stage.getWidth() * 0.20f);
        offsetTable.add(inputOffsetValueLabel).width(stage.getWidth() * 0.10f).padTop(stage.getHeight() * 0.01f).padBottom(stage.getHeight() * 0.01f).right().row();
        offsetTable.add(inputOffsetSlider).width(stage.getWidth() * 0.6f).height(inputOffsetLabel.getHeight() * fontScale).padTop(stage.getHeight() * 0.01f).padBottom(stage.getHeight() * 0.01f).colspan(3).row();
        offsetTable.add().height(offsetValueLabel.getHeight()).row();

        // note speed
        offsetTable.add(noteSpeedLabel).width(stage.getWidth() * 0.3f).padTop(stage.getHeight() * 0.01f).padBottom(stage.getHeight() * 0.01f).fillX();
        offsetTable.add().width(stage.getWidth() * 0.20f);
        offsetTable.add(noteSpeedValueLabel).width(stage.getWidth() * 0.10f).padTop(stage.getHeight() * 0.01f).padBottom(stage.getHeight() * 0.01f).right().row();
        offsetTable.add(noteSpeedSlider).width(stage.getWidth() * 0.6f).height(noteSpeedLabel.getHeight() * fontScale).padTop(stage.getHeight() * 0.01f).padBottom(stage.getHeight() * 0.01f).colspan(3).row();
        offsetTable.add().height(noteSpeedValueLabel.getHeight()).row();

        // sync mode setting
        offsetTable.add(syncModeCheckbox).height(inputOffsetLabel.getHeight() * fontScale).padTop(stage.getHeight() * 0.01f).padBottom(stage.getHeight() * 0.01f).left().colspan(3).row();

        offsetTable.add().expand().fill().row();
        offsetTable.setVisible(false);
        offsetTable.setDebug(DEBUG);

        content.add(offsetTable);


        // extras - path to beatmaps
        pathToBeatmaps.setFontScale(fontScale);
        pathValueLabel.setFontScale(fontScale);

        // extras - sorting mode
        sortingModeChooser = new CheckBox("Sorting Mode: " + sortingModes[GlobalConfiguration.sortMode], Assets.menuSkin);
        sortingModeChooser.getLabel().setFontScale(fontScale);
        sortingModeChooser.getImageCell().width(0);
        sortingModeChooser.addListener(this);

        // extras - random mode
        final Table otherTable = new Table();

        otherTable.setHeight(stage.getHeight() * 0.7f);
        otherTable.setWidth(stage.getWidth() * 0.6f);

        otherTable.add(pathToBeatmaps).width(stage.getWidth() * 0.6f).padTop(stage.getHeight() * 0.01f).padBottom(stage.getHeight() * 0.01f).fillX().left().row();
        otherTable.add(pathValueLabel).padTop(stage.getHeight() * 0.01f).padBottom(stage.getHeight() * 0.01f).fillX().left().padLeft(stage.getWidth() * 0.03f).row();
        otherTable.add(sortingModeChooser).padTop(stage.getHeight() * 0.01f).padBottom(stage.getHeight() * 0.01f).left().row();
        otherTable.add().expand().fill().row();
        otherTable.add(reloadBeatmaps).padTop(stage.getHeight() * 0.01f).padBottom(stage.getHeight() * 0.01f).left().row();

        otherTable.setVisible(false);
        otherTable.setDebug(DEBUG);
        content.add(otherTable);
        // fill in the UI

        content.setWidth(stage.getWidth() * 0.6f);
        content.setHeight(stage.getHeight() * 0.7f);
        content.setX(stage.getWidth() * 0.35f);
        content.setY(stage.getHeight() * 0.10f);

        stage.addActor(content);
        stage.addActor(tabbedPane);

        ChangeListener tabListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                volumeTable.setVisible(volumeSettingsTabButton.isChecked());
                offsetTable.setVisible(offsetSettingsTabButton.isChecked());
                otherTable.setVisible(otherSettingsTabButton.isChecked());
            }
        };
        volumeSettingsTabButton.addListener(tabListener);
        offsetSettingsTabButton.addListener(tabListener);
        otherSettingsTabButton.addListener(tabListener);

        reloadBeatmaps.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Assets.hardReloadBeatmaps();
                ((Game) Gdx.app.getApplicationListener()).setScreen(new BeatmapReloadScreen());
            }
        });

        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GlobalConfiguration.songVolume = newVolume;
                GlobalConfiguration.feedbackVolume = newFeedbackVolume;
                GlobalConfiguration.offset = newGlobalOffset;
                GlobalConfiguration.inputOffset = newInputOffset;
                GlobalConfiguration.noteSpeed = newNoteSpeed;
                GlobalConfiguration.playHintSounds = newHitSoundsSetting;
                GlobalConfiguration.sortMode = newSortingMode;
                GlobalConfiguration.syncMode = newSyncMode;
                GlobalConfiguration.storeConfiguration();
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());
            }
        });

        InputMultiplexer impx = new InputMultiplexer();
        impx.addProcessor(this);
        impx.addProcessor(stage);

        Gdx.input.setInputProcessor(impx);
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void changed(ChangeEvent event, Actor actor) {
        if (actor == songVolumeSlider) {
            newVolume = (int) ((Slider) actor).getValue();
            songVolumeValueLabel.setText(Integer.toString(newVolume));
        }
        if (actor == feedbackVolumeSlider) {
            newFeedbackVolume = (int) ((Slider) actor).getValue();
            feedbackVolumeValueLabel.setText(Integer.toString(newFeedbackVolume));
        }
        if (actor == offsetSlider) {
            newGlobalOffset = (int) ((Slider) actor).getValue();
            offsetValueLabel.setText((newGlobalOffset > 0 ? "+" : "") + Integer.toString(newGlobalOffset) + " ms.");
        }
        if (actor == inputOffsetSlider) {
            newInputOffset = (int) ((Slider) actor).getValue();
            inputOffsetValueLabel.setText((newInputOffset > 0 ? "+" : "") + Integer.toString(newInputOffset) + " ms.");
        }
        if (actor == noteSpeedSlider) {
            newNoteSpeed = (int) ((Slider) actor).getValue();
            noteSpeedValueLabel.setText(newNoteSpeed +"");
        }
        if (actor == playHintSoundCheckbox) {
            newHitSoundsSetting = playHintSoundCheckbox.isChecked();
            playHintSoundCheckbox.setText("Hint Sounds (" + (playHintSoundCheckbox.isChecked() ? "X" : " ") + ")");
        }
        if (actor == sortingModeChooser) {
            newSortingMode = (newSortingMode + 1) % sortingModes.length;
            sortingModeChooser.setText("Sorting Mode: " + sortingModes[newSortingMode]);
        }
        if (actor == syncModeCheckbox) {
            newSyncMode = (newSyncMode + 1) % 4;
            syncModeCheckbox.setText("Sync Mode: " + SongUtils.syncModes[newSyncMode]);
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
            // do nothing
            // Return without saving the changes
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());
            return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
