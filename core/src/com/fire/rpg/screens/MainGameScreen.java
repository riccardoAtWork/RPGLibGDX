package com.fire.rpg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.fire.rpg.domain.Entity;
import com.fire.rpg.domain.PlayerController;
import com.fire.rpg.utils.MapManager;


public class MainGameScreen implements Screen {
    private static final String TAG = MainGameScreen.class.getSimpleName();

    private static class VIEWPORT {
        static float viewportWidth;
        static float viewportHeight;
        static float virtualWidth;
        static float virtualHeight;
        static float physicalWidth;
        static float physicalHeight;
        static float aspectRatio;
    }

    private PlayerController controller;
    private TextureRegion currentPlayerFrame;
    private Sprite currentPlayerSprite;

    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private static MapManager mapManager;

    public MainGameScreen() {
        mapManager = new MapManager();
    }

    private static Entity player;

    @Override
    public void show() {
        //camera setup
        setupViewport(10, 10);

        //get the current size
        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);

        mapRenderer = new OrthogonalTiledMapRenderer(mapManager.getCurrentMap(), MapManager.UNIT_SCALE);
        mapRenderer.setView(camera);

        Gdx.app.debug(TAG, "UnitScale value is: " + mapRenderer.getUnitScale());

        player = new Entity();
        Gdx.app.log(TAG, "entity created");
        player.init(mapManager.getPlayerStartUnitScaled().x, mapManager.getPlayerStartUnitScaled().y);
        Gdx.app.log(TAG, "entity init");

        currentPlayerSprite = player.getFrameSprite();

        controller = new PlayerController(player);
        Gdx.input.setInputProcessor(controller);

    }

    @Override
    public void hide() {
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Preferable to lock and center the camera to the player's position
        camera.position.set(currentPlayerSprite.getX(), currentPlayerSprite.getY(), 0f);
        //Gdx.app.log(TAG, String.format("%s %s", currentPlayerSprite.getX(), currentPlayerSprite.getY()));
        camera.update();

        player.update(delta);
        currentPlayerFrame = player.getFrame();

        updatePortalLayerActivation(player.boundingBox);

        if (!isCollisionWithMapLayer(player.boundingBox)) {
            player.setNextPositionToCurrent();
        }
        controller.update(delta);

        //mapRenderer.getBatch().enableBlending();
        //mapRenderer.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        mapRenderer.setView(camera);
        mapRenderer.render();

        mapRenderer.getBatch().begin();
        mapRenderer.getBatch().draw(currentPlayerFrame, currentPlayerSprite.getX(), currentPlayerSprite.getY(), 1, 1);
        mapRenderer.getBatch().end();
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
    public void dispose() {
        player.dispose();
        controller.dispose();
        Gdx.input.setInputProcessor(null);
        mapRenderer.dispose();

    }

    private void setupViewport(int width, int height) {
        //Make the viewport a percentage of the total display area
        VIEWPORT.virtualWidth = width;
        VIEWPORT.virtualHeight = height;

        //Current viewport dimensions
        VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
        VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;

        //pixel dimensions of display
        VIEWPORT.physicalWidth = Gdx.graphics.getWidth();
        VIEWPORT.physicalHeight = Gdx.graphics.getHeight();

        //aspect ratio for current viewport
        VIEWPORT.aspectRatio = (VIEWPORT.virtualWidth / VIEWPORT.virtualHeight);

        //update viewport if there could be skewing
        if (VIEWPORT.physicalWidth / VIEWPORT.physicalHeight >= VIEWPORT.aspectRatio) {
            //Letterbox left and right
            VIEWPORT.viewportWidth = VIEWPORT.viewportHeight * (VIEWPORT.physicalWidth / VIEWPORT.physicalHeight);
            VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;
        } else {
            //letterbox above and below
            VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
            VIEWPORT.viewportHeight = VIEWPORT.viewportWidth * (VIEWPORT.physicalHeight / VIEWPORT.physicalWidth);
        }

        Gdx.app.debug(TAG, "WorldRenderer: virtual: (" + VIEWPORT.virtualWidth + "," + VIEWPORT.virtualHeight + ")");
        Gdx.app.debug(TAG, "WorldRenderer: viewport: (" + VIEWPORT.viewportWidth + "," + VIEWPORT.viewportHeight + ")");
        Gdx.app.debug(TAG, "WorldRenderer: physical: (" + VIEWPORT.physicalWidth + "," + VIEWPORT.physicalHeight + ")");
    }

    private boolean isCollisionWithMapLayer(Rectangle boundingBox) {
        MapLayer mapCollisionLayer = mapManager.getCollisionLayer();

        if (mapCollisionLayer == null) {
            return false;
        }

        Rectangle rectangle = null;

        for (MapObject object : mapCollisionLayer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                rectangle = ((RectangleMapObject) object).getRectangle();
                //Gdx.app.debug(TAG, "Collision Rect (" + rectangle.x + "," + rectangle.y + ")");
                //Gdx.app.debug(TAG, "Player Rect (" + boundingBox.x + "," + boundingBox.y + ")");
                if (boundingBox.overlaps(rectangle)) {
                    //Gdx.app.debug(TAG, "Map Collision!");
                    return true;
                }
            }
        }

        return false;
    }

    private boolean updatePortalLayerActivation(Rectangle boundingBox) {
        MapLayer mapPortalLayer = mapManager.getPortalLayer();

        if (mapPortalLayer == null) {
            return false;
        }

        Rectangle rectangle = null;

        for (MapObject object : mapPortalLayer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                rectangle = ((RectangleMapObject) object).getRectangle();
                //Gdx.app.debug(TAG, "Collision Rect (" + rectangle.x + "," + rectangle.y + ")");
                //Gdx.app.debug(TAG, "Player Rect (" + boundingBox.x + "," + boundingBox.y + ")");
                if (boundingBox.overlaps(rectangle)) {
                    String mapName = object.getName();
                    if (mapName == null) {
                        return false;
                    }

                    mapManager.setClosestStartPositionFromScaledUnits(player.getCurrentPosition());
                    mapManager.loadMap(mapName);
                    player.init(mapManager.getPlayerStartUnitScaled().x, mapManager.getPlayerStartUnitScaled().y);
                    mapRenderer.setMap(mapManager.getCurrentMap());
                    Gdx.app.debug(TAG, "Portal Activated");
                    return true;
                }
            }
        }

        return false;
    }


}
