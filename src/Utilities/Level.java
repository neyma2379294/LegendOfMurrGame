/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Utilities;

import Entities.Enemy;
import Entities.Item;
import Entities.Platform;
import Entities.Player;
import Entities.Wall;
import java.util.ArrayList;
import legendofmurrgame.DebugDrawJ2D;
import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.IViewportTransform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

/**
 *
 * @author Marieta
 */
public class Level {

    public final int LEVEL_ID;
    //PHYSICS
    Vec2 gravity;
    World world;
    float timeStep = 1.0f / 60.0f;
    int velocityIterations = 6;
    int positionInterations = 2;
    DebugDrawJ2D debugDraw;
    boolean debugMode;
    IViewportTransform viewportTransform;
    boolean worldPause;
    //Slick2D variables
    TiledMap tiledMap;
    int width, height;
    //Entities
    Player player;
    ArrayList<Item> itemBodies;
    ArrayList<Enemy> enemyBodies;
    ArrayList<Wall> wallBodies;
    ArrayList<Platform> platformBodies;

    public Level( int LEVEL_ID, GameContainer gc, Vec2 gravity, TiledMap tiledMap ){
        this.LEVEL_ID = LEVEL_ID;
        this.gravity = gravity;
        this.debugDraw = new DebugDrawJ2D(gc);
        this.tiledMap = tiledMap;

        world = new World(gravity);
        debugMode = true;
        viewportTransform = debugDraw.GetViewportTransform();
        viewportTransform.setCamera(0, 0, 10);
        world.setDebugDraw(debugDraw);
        worldPause = true;
        width = tiledMap.getWidth();
        height = tiledMap.getHeight();

        //ArrayLists
        itemBodies = new ArrayList();
        enemyBodies = new ArrayList();
        wallBodies = new ArrayList();
        platformBodies = new ArrayList();
    }

    public boolean GetDebugMode(){
        return debugMode;
    }

    public void SetDebugMode( boolean debugMode ){
        this.debugMode = debugMode;
    }

    public boolean GetWorldPause(){
        return worldPause;
    }

    public void SetWorldPause( boolean worldPause ){
        this.worldPause = worldPause;
    }

    public Player GetPlayer(){
        return player;
    }

    public void Update(){
        //PAUSE WORLD UPDATE
        if( !worldPause )
            world.step(timeStep, velocityIterations, positionInterations);
        else
            world.step(0, 0, 0);

        //DEBUG MODE UPDATE
        if ( !debugMode ) {
            debugDraw.clearFlags(DebugDraw.e_shapeBit);
        }
        else if ( debugMode ) {
            debugDraw.setFlags(DebugDraw.e_shapeBit);
        }

        player.UpdatePosition();
        System.out.println("Player x coord: " + player.GetPosition().x);
        System.out.println("Player y coord: " + player.GetPosition().y);

        //VIEWPORT TRANSFORM / CAMERA UPDATE
        if( player != null )
            viewportTransform.setCamera(player.GetPosition().x, player.GetPosition().y, CommonCode.SCALE);
        else
            viewportTransform.setCamera(0, 0, CommonCode.SCALE);
    }

    public void AddPlayer( float x, float y, float width, float height, String animPathName, int[] duration ) throws SlickException{
        if( player == null ){
            player = new Player(x, y, width, height, animPathName, duration, "player");
            player.CreateBodyInWorld(world);
        }
    }

    public void AddItemBody( float x, float y, float radius, String animPathName, String bodyUserData ) throws SlickException{
        Item tempItem = new Item( x, y, radius, animPathName, bodyUserData );
        tempItem.CreateBodyInWorld(world);
        itemBodies.add( tempItem );
    }

    public void AddEnemyBody( float x, float y, float width, float height, String animPathName, int[] duration, String bodyUserData ) throws SlickException{
        Enemy tempEnemy = new Enemy(x, y, width, height, animPathName, duration, bodyUserData);
        tempEnemy.CreateBodyInWorld(world);
        enemyBodies.add(tempEnemy);
    }

    public void AddPlatformBody( float x, float y, float width, float height, String imagePathName, float startX, float startY, float endX, float endY, float speed ) throws SlickException{
        Platform tempPlatform = new Platform(x, y, width, height, imagePathName, startX, startY, endX, endY, speed);
        tempPlatform.CreateBodyInWorld(world);
        platformBodies.add(tempPlatform);
    }

    public void AddWallBody( float x, float y, float width, float height, float friction ){
        Wall tempWall = new Wall(x, y, width, height, friction);
        tempWall.CreateBodyInWorld(world);
        wallBodies.add(tempWall);
    }

    public void Render(){
        debugDraw.drawCircle(new Vec2(0, 0), 2, Color3f.WHITE);
        debugDraw.drawString(new Vec2(0, 20), "fjkslejflesjfles", Color3f.WHITE);
        world.drawDebugData();
        float viewportXPos = viewportTransform.getExtents().x;
        float viewportYPos = viewportTransform.getExtents().y;
        if( player != null )
            player.Render();
        for( Item item: itemBodies ){
            if( item.GetPosition().x > viewportXPos && item.GetPosition().x < (viewportXPos + width) &&
                item.GetPosition().y > viewportYPos && item.GetPosition().y < (viewportYPos + height))
                item.Render();
        }
        for( Enemy enemy: enemyBodies ){
            if( enemy.GetPosition().x > viewportXPos && enemy.GetPosition().x < (viewportXPos + width) &&
                enemy.GetPosition().y > viewportYPos && enemy.GetPosition().y < (viewportYPos + height))
                enemy.Render();
        }
        for( Platform platform: platformBodies ){
            if( platform.GetPosition().x > viewportXPos && platform.GetPosition().x < (viewportXPos + width) &&
                platform.GetPosition().y > viewportYPos && platform.GetPosition().y < (viewportYPos + height))
                platform.Render();
        }
    }

}