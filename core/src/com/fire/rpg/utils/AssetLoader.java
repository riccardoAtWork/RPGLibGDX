package com.fire.rpg.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * Created by ric on 31/05/16.
 */
public class AssetLoader {

    private static AssetLoader instance = new AssetLoader();
    private InternalFileHandleResolver resolver = new InternalFileHandleResolver();
    private AssetManager manager = new AssetManager(resolver);
    private static final String TAG = AssetLoader.class.getSimpleName();

    public static AssetLoader instance() {
        return instance;
    }

    public void loadMap(String mapPath) {
        if (mapPath == null || mapPath.isEmpty()) {
            Gdx.app.debug(TAG, "null or empty Map path!");
            return;
        }
        if (!resolver.resolve(mapPath).exists()) {
            Gdx.app.debug(TAG, "Map doesn't exist!: " + mapPath);
            return;
        }
        manager.setLoader(TiledMap.class, new TmxMapLoader());
        manager.load(mapPath, TiledMap.class);
        manager.finishLoadingAsset(mapPath);
        Gdx.app.debug(TAG, "Map loaded!: " + mapPath);

    }
    public TiledMap getMap(String mapPath){
        TiledMap map = null;
        if( manager.isLoaded(mapPath) ){
            map = manager.get(mapPath,TiledMap.class);
        } else {
            Gdx.app.debug(TAG, "Map is not loaded: " + mapPath );
        }

        return map;
    }


    public void loadTextureAsset(String path) {
        if(path == null || path.isEmpty())
            return;
        System.out.println(path + resolver + Gdx.files);
        if(resolver.resolve(path).exists()) {

            manager.setLoader(Texture.class, new TextureLoader(resolver));
            manager.load(path, Texture.class);
            manager.finishLoadingAsset(path);

        } else {
            Gdx.app.debug(TAG, String.format("Texture %s does not exist", path));
        }

    }


    public Texture getTextureAsset(String path) {
        Texture texture = null;
        if( manager.isLoaded(path) ){
            texture = manager.get(path,Texture.class);
        } else {
            Gdx.app.debug(TAG, String.format("Texture %s is not loaded", path));
        }

        return texture;
    }




    public boolean isAssetLoaded(String fileName){
        return manager.isLoaded(fileName);

    }

    public void unloadAsset(String path) {
        if( manager.isLoaded(path) ){
            manager.unload(path);
        } else {
            Gdx.app.debug(TAG, String.format("Asset is not loaded; Nothing to unload: %s", path));
        }
    }


}
