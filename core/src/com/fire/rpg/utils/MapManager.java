package com.fire.rpg.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ric on 31/05/16.
 */

public class MapManager {
    private TiledMap currentMap;
    private String currentMapName;
    private static final String TAG = MapManager.class.getSimpleName();
    public final static float UNIT_SCALE  = 1/16f;
    private final static String TOP_WORLD = "TOP_WORLD";
    private final static String TOWN = "TOWN";
    private final static String CASTLE_OF_DOOM = "CASTLE_OF_DOOM";
    private Map<String, String> mapTable = new HashMap<>();
    private Map<String, Vector2> playerStartLocations = new HashMap<>();
    private Vector2 playerStart;

    private MapLayer collisionLayer;
    private MapLayer portalLayer;
    private MapLayer spawnsLayer;
    private final static String MAP_COLLISION_LAYER = "MAP_COLLISION_LAYER";
    private final static String MAP_SPAWNS_LAYER = "MAP_SPAWNS_LAYER";
    private final static String MAP_PORTAL_LAYER = "MAP_PORTAL_LAYER";
    private final static String PLAYER_START = "PLAYER_START";
    private Vector2 playerStartPositionRect;
    private Vector2 closestPlayerStartPosition;

    public MapManager() {
        playerStart = new Vector2(0, 0);
        playerStartPositionRect = new Vector2(0, 0);
        closestPlayerStartPosition = new Vector2(0, 0);
        mapTable.put(TOP_WORLD, "maps/topworld.tmx");
        mapTable.put(TOWN, "maps/town.tmx");
        mapTable.put(CASTLE_OF_DOOM, "maps/castle_of_doom.tmx");
        playerStartLocations.put(TOP_WORLD, playerStart.cpy());
        playerStartLocations.put(TOWN, playerStart.cpy());
        playerStartLocations.put(CASTLE_OF_DOOM, playerStart.cpy());
    }


    public TiledMap getCurrentMap() {
        if( currentMap == null ) {
            currentMapName = TOWN;
            loadMap(currentMapName);
        }
        return currentMap;
    }

    public void loadMap(String mapName) {

        String mapFullPath = mapTable.get(mapName);

        if (mapFullPath == null || mapFullPath.isEmpty()) {
            Gdx.app.debug(TAG, "Map is invalid");
            return;
        }
        if (currentMap != null) {
            currentMap.dispose();
        }
        if(!AssetLoader.instance().isAssetLoaded(mapFullPath)) {
            AssetLoader.instance().loadMap(mapFullPath);
        }
        if(AssetLoader.instance().isAssetLoaded(mapFullPath)) {
            currentMap = AssetLoader.instance().getMap(mapFullPath);
            currentMapName = mapName;
        } else {
            Gdx.app.debug(TAG, "Map not loaded");
        }

        collisionLayer = currentMap.getLayers().get(MAP_COLLISION_LAYER);
        if (collisionLayer == null) {
            Gdx.app.debug(TAG, "No collision layer!");
        }

        portalLayer = currentMap.getLayers().get(MAP_PORTAL_LAYER);
        if (portalLayer == null) {
            Gdx.app.debug(TAG, "No portal layer!");
        }

        spawnsLayer = currentMap.getLayers().get(MAP_SPAWNS_LAYER);
        if (spawnsLayer == null) {
            Gdx.app.debug(TAG, "No spawn layer!");
        } else {
            Vector2 start = playerStartLocations.get(currentMapName);
            if (start.isZero()) {
                setClosestStartPosition(playerStart);
                start = playerStartLocations.get(currentMapName);
            }
            playerStart.set(start.x, start.y);
        }

        Gdx.app.debug(TAG, "Player Start: (" + playerStart.x + "," + playerStart.y + ")");

    }


    public Vector2 getPlayerStartUnitScaled() {
        Vector2 playerStart = this.playerStart.cpy();
        playerStart.set(this.playerStart.x * UNIT_SCALE, this.playerStart.y * UNIT_SCALE);
        //Gdx.app.debug(TAG, "getplayerstart" + this.playerStart.x * UNIT_SCALE + " " + this.playerStart.y * UNIT_SCALE);
        return playerStart;
    }

    private void setClosestStartPosition(Vector2 position) {
        Gdx.app.debug(TAG, "setClosestStartPosition INPUT: (" + position.x + "," + position.y + ") " + currentMapName);

        //Get last known position on this map
        playerStartPositionRect.set(0, 0);
        closestPlayerStartPosition.set(0, 0);
        float shortestDistance = 0f;

        //Go through all player start positions and choose closest to last known position
        for (MapObject object : spawnsLayer.getObjects()) {
            if (object.getName().equalsIgnoreCase(PLAYER_START)) {
                ((RectangleMapObject) object).getRectangle().getPosition(playerStartPositionRect);
                float distance = position.dst2(playerStartPositionRect);

                Gdx.app.debug(TAG, "distance: " + distance + " for " + currentMapName);

                if (distance < shortestDistance || shortestDistance == 0) {
                    closestPlayerStartPosition.set(playerStartPositionRect);
                    shortestDistance = distance;
                    Gdx.app.debug(TAG, "closest START is: (" + closestPlayerStartPosition.x + "," + closestPlayerStartPosition.y + ") " + currentMapName);
                }
            }
        }
        playerStartLocations.put(currentMapName, closestPlayerStartPosition.cpy());
    }



    public void setClosestStartPositionFromScaledUnits(Vector2 position) {
        if (UNIT_SCALE <= 0)
            return;
        Vector2 convertedUnits = new Vector2();
        convertedUnits.set(position.x / UNIT_SCALE, position.y / UNIT_SCALE);
        setClosestStartPosition(convertedUnits);
    }



    public MapLayer getCollisionLayer() {
        return collisionLayer;
    }

    public MapLayer getPortalLayer() {
        return portalLayer;
    }


}
