package com.fire.rpg.domain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.fire.rpg.MapManager;
import com.fire.rpg.utils.AssetLoader;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ric on 30/05/16.
 */
public class Entity {

    public enum Direction {UP, RIGHT, DOWN, LEFT}

    public enum AnimType {WALK_UP, WALK_RIGHT, WALK_DOWN, WALK_LEFT}

    public enum State {IDLE, WALKING}

    private final static String TAG = Entity.class.getSimpleName();
    private final static String DEFAULT_SPRITE_PATH = "sprites/Characters/Warrior.png";

    private Map<AnimType, Animation> animations = new HashMap<>();

    private Vector2 velocity;
    private String entityID;
    private Direction current = Direction.LEFT;
    private Direction previous = Direction.UP;

    protected Vector2 nextPosition;
    protected State state = State.IDLE;
    protected float frameTime;
    protected Sprite frameSprite;
    protected TextureRegion currentFrame;

    public final int FRAME_WIDTH = 16;
    public final int FRAME_HEIGHT = 16;
    public static Rectangle boundingBox;


    private TextureRegion currentPlayerFrame;


    public Entity() {
        init();
    }

    public void init() {


        entityID = UUID.randomUUID().toString();
        nextPosition = new Vector2();
        boundingBox = new Rectangle();
        velocity = new Vector2(2f, 2f);

        AssetLoader.instance().loadTextureAsset(DEFAULT_SPRITE_PATH);

        loadSpriteAndAnimations();

        setBoundingBoxSize(0f, 0.5f);



    }

    public void init(float startX, float startY) {

        frameSprite.setX(startX);
        frameSprite.setY(startY);

        this.nextPosition.x = startX;
        this.nextPosition.y = startY;
    }


    public void update(float dt) {
        frameTime = (frameTime + dt);
        updateBoundingBox();

    }



    private void setBoundingBoxSize(float percentageWidthReduced, float percentageHeightReduced) {

        boundingBox.width = FRAME_WIDTH;
        boundingBox.height = FRAME_HEIGHT;

        float widthReductionAmount = 1.0f - percentageWidthReduced; //.8f for 20% (1 - .20)
        float heightReductionAmount = 1.0f - percentageHeightReduced; //.8f for 20% (1 - .20)

        if (widthReductionAmount > 0 && widthReductionAmount < 1) {
            boundingBox.width *= widthReductionAmount;
        }

        if (heightReductionAmount > 0 && heightReductionAmount < 1) {
            boundingBox.height *= heightReductionAmount;
        }
    }


    private void updateBoundingBox() {

        //Need to account for the unitscale, since the map coordinates will be in pixels
        float x;
        float y;
        if (MapManager.UNIT_SCALE > 0) {
            x = nextPosition.x / MapManager.UNIT_SCALE;
            y = nextPosition.y / MapManager.UNIT_SCALE;
        } else {
            x = nextPosition.x;
            y = nextPosition.y;
        }

        boundingBox.setPosition(x, y);

    }


    private void loadSpriteAndAnimations() {
        //Walking animation
        Texture texture = AssetLoader.instance().getTextureAsset(DEFAULT_SPRITE_PATH);
        TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);
        frameSprite = new Sprite(textureFrames[0][0].getTexture(), 0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        frameSprite.setOriginCenter();
        currentFrame = textureFrames[0][0];

        Array<TextureRegion> walkDownFrames = new Array<>(4);
        Array<TextureRegion> walkLeftFrames = new Array<>(4);
        Array<TextureRegion> walkRightFrames = new Array<>(4);
        Array<TextureRegion> walkUpFrames = new Array<>(4);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                //Gdx.app.debug(TAG, "Got frame " + i + "," + j + " from " + sourceImage);
                TextureRegion region = textureFrames[i][j];
                if (region == null) {
                    Gdx.app.debug(TAG, "Got null animation frame " + i + "," + j);
                }
                switch (i) {
                    case 0:
                        walkDownFrames.insert(j, region);
                        break;
                    case 1:
                        walkLeftFrames.insert(j, region);
                        break;
                    case 2:
                        walkRightFrames.insert(j, region);
                        break;
                    case 3:
                        walkUpFrames.insert(j, region);
                        break;
                }
            }
        }

        animations.put(AnimType.WALK_UP, new Animation(0.25f, walkUpFrames, Animation.PlayMode.LOOP));
        animations.put(AnimType.WALK_RIGHT, new Animation(0.25f, walkRightFrames, Animation.PlayMode.LOOP));
        animations.put(AnimType.WALK_DOWN, new Animation(0.25f, walkDownFrames, Animation.PlayMode.LOOP));
        animations.put(AnimType.WALK_LEFT, new Animation(0.25f, walkLeftFrames, Animation.PlayMode.LOOP));


    }

    public void dispose() {
        AssetLoader.instance().unloadAsset(DEFAULT_SPRITE_PATH);
    }

    public void setState(State state) {
        this.state = state;
    }

    public Sprite getFrameSprite() {
        return frameSprite;
    }

    public TextureRegion getFrame() {

        return currentFrame;
    }

    public Vector2 getCurrentPosition() {

        return new Vector2(frameSprite.getX(), frameSprite.getY());
    }

    public void setCurrentPosition(float currentPositionX, float currentPositionY) {
        frameSprite.setX(currentPositionX);
        frameSprite.setY(currentPositionY);
    }



    public void setDirection(Direction direction) {
        previous = current;
        current = direction;
       // float time = frameTime;
        switch (current) {
            case DOWN:
                currentFrame = animations.get(AnimType.WALK_DOWN).getKeyFrame(frameTime);
                break;
            case LEFT:
                currentFrame = animations.get(AnimType.WALK_LEFT).getKeyFrame(frameTime);
                break;
            case UP:
                currentFrame = animations.get(AnimType.WALK_UP).getKeyFrame(frameTime);
                break;
            case RIGHT:
                currentFrame = animations.get(AnimType.WALK_RIGHT).getKeyFrame(frameTime);
                break;
            default:
                break;
        }
       // currentFrame = currentAnimation.getKeyFrame(time);
    }

    public void setNextPositionToCurrent() {
        setCurrentPosition(nextPosition.x, nextPosition.y);
    }


    public void calculateNextPosition(Direction currentDirection, float deltaTime) {
        float testX = frameSprite.getX();
        float testY = frameSprite.getY();

        velocity.scl(deltaTime);

        switch (currentDirection) {
            case LEFT:
                testX -= velocity.x;
                break;
            case RIGHT:
                testX += velocity.x;
                break;
            case UP:
                testY += velocity.y;
                break;
            case DOWN:
                testY -= velocity.y;
                break;
            default:
                break;
        }

        nextPosition.x = testX;
        nextPosition.y = testY;

        //velocity
        velocity.scl(1 / deltaTime);
    }


}



























