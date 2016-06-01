package com.fire.rpg.domain;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;


import java.util.HashMap;
import java.util.Map;


public class PlayerController implements InputProcessor {

    private final static String TAG = PlayerController.class.getSimpleName();

    enum Keys {
        LEFT, RIGHT, UP, DOWN, QUIT
    }

    enum Mouse {
        SELECT, DO_ACTION
    }

    private Map<Keys, Boolean> keys = new HashMap<>();
    private  Map<Mouse, Boolean> mouseButtons = new HashMap<>();
    private Vector3 lastMouseCoordinates;


    private Entity player;

    public PlayerController(Entity player) {
        //Gdx.app.debug(TAG, "Construction" );
        keys.put(Keys.LEFT, false);
        keys.put(Keys.RIGHT, false);
        keys.put(Keys.UP, false);
        keys.put(Keys.DOWN, false);
        keys.put(Keys.QUIT, false);

        mouseButtons.put(Mouse.SELECT, false);
        mouseButtons.put(Mouse.DO_ACTION, false);

        this.lastMouseCoordinates = new Vector3();
        this.player = player;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
            this.leftPressed();
        }
        if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
            this.rightPressed();
        }
        if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            this.upPressed();
        }
        if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
            this.downPressed();
        }
        if (keycode == Input.Keys.Q) {
            this.quitPressed();
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
            this.leftReleased();
        }
        if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
            this.rightReleased();
        }
        if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            this.upReleased();
        }
        if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
            this.downReleased();
        }
        if (keycode == Input.Keys.Q) {
            this.quitReleased();
        }
        return true;

    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        //Gdx.app.debug(TAG, "GameScreen: MOUSE DOWN........: (" + screenX + "," + screenY + ")" );

        if (button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT) {
            this.setClickedMouseCoordinates(screenX, screenY);
        }

        //left is selection, right is context menu
        if (button == Input.Buttons.LEFT) {
            this.selectMouseButtonPressed(screenX, screenY);
        }
        if (button == Input.Buttons.RIGHT) {
            this.doActionMouseButtonPressed(screenX, screenY);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        //left is selection, right is context menu
        if (button == Input.Buttons.LEFT) {
            this.selectMouseButtonReleased(screenX, screenY);
        }
        if (button == Input.Buttons.RIGHT) {
            this.doActionMouseButtonReleased(screenX, screenY);
        }
        return true;
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

    public void dispose() {

    }

    //Key presses
    public void leftPressed() {
        keys.put(Keys.LEFT, true);
    }

    public void rightPressed() {
        keys.put(Keys.RIGHT, true);
    }

    public void upPressed() {
        keys.put(Keys.UP, true);
    }

    public void downPressed() {
        keys.put(Keys.DOWN, true);
    }

    public void quitPressed() {
        keys.put(Keys.QUIT, true);
    }

    public void setClickedMouseCoordinates(int x, int y) {
        lastMouseCoordinates.set(x, y, 0);
    }

    public void selectMouseButtonPressed(int x, int y) {
        mouseButtons.put(Mouse.SELECT, true);
    }

    public void doActionMouseButtonPressed(int x, int y) {
        mouseButtons.put(Mouse.DO_ACTION, true);
    }

    //Releases

    public void leftReleased() {
        keys.put(Keys.LEFT, false);
    }

    public void rightReleased() {
        keys.put(Keys.RIGHT, false);
    }

    public void upReleased() {
        keys.put(Keys.UP, false);
    }

    public void downReleased() {
        keys.put(Keys.DOWN, false);
    }

    public void quitReleased() {
        keys.put(Keys.QUIT, false);
    }

    public void selectMouseButtonReleased(int x, int y) {
        mouseButtons.put(Mouse.SELECT, false);
    }

    public void doActionMouseButtonReleased(int x, int y) {
        mouseButtons.put(Mouse.DO_ACTION, false);
    }


    public void update(float delta) {
        processInput(delta);
        //Gdx.app.debug(TAG, "update:: Next Position: (" + BludBourne.player.getNextPosition().x + "," + BludBourne.player.getNextPosition().y + ")" + "DELTA: " + delta);
    }

    public void hide() {
        keys.put(Keys.LEFT, false);
        keys.put(Keys.RIGHT, false);
        keys.put(Keys.UP, false);
        keys.put(Keys.DOWN, false);
        keys.put(Keys.QUIT, false);
    }

    private void processInput(float delta) {


        if (keys.get(Keys.LEFT)) {
            //Gdx.app.debug(TAG, "LEFT key");
            player.calculateNextPosition(Entity.Direction.LEFT, delta);
            player.setState(Entity.State.WALKING);
            player.setDirection(Entity.Direction.LEFT);
        } else if (keys.get(Keys.RIGHT)) {
            //Gdx.app.debug(TAG, "RIGHT key");
            player.calculateNextPosition(Entity.Direction.RIGHT, delta);
            player.setState(Entity.State.WALKING);
            player.setDirection(Entity.Direction.RIGHT);
        } else if (keys.get(Keys.UP)) {;
            player.calculateNextPosition(Entity.Direction.UP, delta);
            player.setState(Entity.State.WALKING);
            player.setDirection(Entity.Direction.UP);
        } else if (keys.get(Keys.DOWN)) {
            player.calculateNextPosition(Entity.Direction.DOWN, delta);
            player.setState(Entity.State.WALKING);
            player.setDirection(Entity.Direction.DOWN);
        } else if (keys.get(Keys.QUIT)) {
            Gdx.app.exit();
        } else {
            player.setState(Entity.State.IDLE);
        }


        if (mouseButtons.get(Mouse.SELECT)) {
            //Gdx.app.debug(TAG, "Mouse LEFT click at : (" + lastMouseCoordinates.x + "," + lastMouseCoordinates.y + ")" );
            mouseButtons.put(Mouse.SELECT, false);
        }

    }

}
