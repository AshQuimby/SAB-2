package sab.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;

import sab.game.attacks.Attack;
import sab.game.fighters.Chain;
import sab.game.fighters.Marvin;
import sab.game.stages.Platform;

public class Battle {
    private List<Player> players;
    private Player player1;
    private Player player2;

    private List<GameObject> gameObjects;
    private List<GameObject> hittableGameObjects;
    private List<GameObject> newGameObjects;
    private List<GameObject> deadGameObjects;
    private Map<Integer, GameObject> gameObjectsById;
    private Map<GameObject, Integer> idsByGameObject;
    private int nextId;

    public Platform platform;

    public boolean drawHitboxes;

    public Battle() {
        players = new ArrayList<>();
        player1 = new Player(new Marvin(), this);
        player2 = new Player(new Chain(), this);
        players.add(player1);
        players.add(player2);

        gameObjects = new ArrayList<>();
        hittableGameObjects = new ArrayList<>();
        newGameObjects = new ArrayList<>();
        deadGameObjects = new ArrayList<>();
        gameObjectsById = new HashMap<>();
        idsByGameObject = new HashMap<>();
        nextId = 0;

        addGameObject(player1);
        addGameObject(player2);

        platform = new Platform(-320, -128, 640, 64, "last_location.png");
        addGameObject(platform);

        drawHitboxes = true;
    }

    public void reset() {
        players.clear();
        player1 = new Player(new Marvin(), this);
        player2 = new Player(new Chain(), this);
        players.add(player1);
        players.add(player2);

        gameObjects.clear();
        hittableGameObjects.clear();
        newGameObjects.clear();
        gameObjectsById.clear();
        idsByGameObject.clear();
        nextId = 0;

        addGameObject(player1);
        addGameObject(player2);

        platform = new Platform(-320, -128, 640, 64, "last_location.png");
        addGameObject(platform);
    }

    public void addGameObject(GameObject gameObject) {
        newGameObjects.add(gameObject);
    }

    public void removeGameObject(GameObject gameObject) {
        deadGameObjects.add(gameObject);
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public List<GameObject> getHittableGameObjects() {
        return hittableGameObjects;
    }

    public void update() {
        for (GameObject newGameObject : newGameObjects) {
            gameObjects.add(newGameObject);
            int id = ++nextId;
            gameObjectsById.put(id, newGameObject);
            idsByGameObject.put(newGameObject, id);

            if (newGameObject instanceof Hittable) {
                hittableGameObjects.add(newGameObject);
            }
        }

        newGameObjects.removeAll(newGameObjects);

        for (GameObject deadGameObject : deadGameObjects) {
            gameObjects.remove(deadGameObject);
            int id = idsByGameObject.get(deadGameObject);
            gameObjectsById.remove(id);
            idsByGameObject.remove(deadGameObject);

            if (deadGameObject instanceof Hittable) {
                hittableGameObjects.remove(deadGameObject);
            }
        }
        
        deadGameObjects.removeAll(deadGameObjects);

        for (GameObject gameObject : gameObjects) {
            gameObject.preUpdate();
        }
    }

    public Player getPlayer(int player) {
        return players.get(player);
    }

    public void render(Seagraphics g) {
        g.scalableDraw(g.imageProvider.getImage("background.png"), -1152 / 2, -704 / 2, 1152, 704);

        for (GameObject gameObject : gameObjects) {
            gameObject.render(g);

            if (drawHitboxes) {
                Rectangle hitbox = gameObject.hitbox;

                Game.game.window.shapeRenderer.setColor(1, 1, 0, 1);
                if (gameObject.getClass().isAssignableFrom(Player.class)) {
                    Game.game.window.shapeRenderer.setColor(0, 0, 1, 1);
                } else if (gameObject.getClass().isAssignableFrom(Platform.class)) {
                    Game.game.window.shapeRenderer.setColor(0, 1, 0, 1);
                } else if (gameObject.getClass().isAssignableFrom(Attack.class)) {
                    Game.game.window.shapeRenderer.setColor(1, 0, 1, 1);
                    Game.game.window.shapeRenderer.line(gameObject.hitbox.getCenter(new Vector2()), gameObject.hitbox.getCenter(new Vector2()).cpy().add(((Attack) gameObject).knockback));
                    Game.game.window.shapeRenderer.setColor(1, 0, 0, 1);
                }

                Game.game.window.shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
            }
        }

        g.scalableDraw(g.imageProvider.getImage("in_battle_hud.png"), 0, -256, 128, 96);

        for (int i = 0; i < player1.getLives(); i++) {
            g.scalableDraw(g.imageProvider.getImage("life_p1.png"), i * 24 + 32, -256, 20, 20);
        }
    }
}