package com.fteams.sstrain.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.fteams.sstrain.World;
import com.fteams.sstrain.controller.WorldController;
import com.fteams.sstrain.entities.Results;
import com.fteams.sstrain.renderer.WorldRenderer;

public class SongScreen implements Screen, InputProcessor {
    private World world;
    private WorldRenderer renderer;
    private WorldController controller;
    private int width;
    private int height;

    @Override
    public void show() {
        world = new World();
        Results.clear();
        renderer = new WorldRenderer(world);
        controller = new WorldController(world);
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        controller.update(delta);
        renderer.render();
    }

    @Override
    public void resize(int width, int height) {
        // use height as base and force a 3:2 ratio
        int originalWidth = width;
        int newWidth = height * 3 / 2;

        int originalHeight = height;
        int newHeight = width * 2 / 3;

        // check which side should be shortened
        if (newWidth > width)
        {
            height = newHeight;
        }
        else
        {
            width = newWidth;
        }

        renderer.setSize(width, height, (originalWidth - width) / 2, (originalHeight - height)/2);
        world.setSize(width, height, (originalWidth - width) /2, (originalHeight - height)/2);
        this.width = width;
        this.height = height;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public boolean keyDown(int keycode) {

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
            // do nothing
            controller.back();
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
        controller.pressed(screenX, screenY, pointer, button, renderer.ppuX, renderer.ppuY, width, height);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        controller.released(screenX, screenY, pointer, button, renderer.ppuX, renderer.ppuY, width, height);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        controller.dragged(screenX, screenY, pointer, renderer.ppuX, renderer.ppuY, width, height);
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
