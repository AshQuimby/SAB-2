package sab.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;

import sab.game.attacks.Attack;
import sab.game.fighters.Chain;
import sab.game.fighters.Fighter;
import sab.game.fighters.Marvin;
import sab.game.particles.Particle;
import sab.game.stages.LastLocation;
import sab.game.stages.Ledge;
import sab.game.stages.PassablePlatform;
import sab.game.stages.Platform;
import sab.game.stages.Stage;
import sab.game.stages.StageObject;

public class Battle {
    private List<Player> players;
    private Player player1;
    private Player player2;

    private Stage stage;

    private List<GameObject> gameObjects;
    private List<GameObject> hittableGameObjects;
    private List<GameObject> attacks;
    private List<GameObject> miscGameObjects;
    private List<GameObject> stageObjects;
    private List<GameObject> newGameObjects;
    private List<GameObject> deadGameObjects;
    private List<Particle> particles;
    private Map<Integer, GameObject> gameObjectsById;
    private Map<GameObject, Integer> idsByGameObject;
    private int nextId;

    // Pause game variables
    // Pausing should not be avaliable on servers unless the server owner pauses the game
    private boolean paused;
    private boolean pauseOverlayHidden;
    private int pauseMenuIndex;

    public boolean drawHitboxes;

    public Battle(Fighter fighter1, Fighter fighter2, int[] costumes, Stage stage) {
        players = new ArrayList<>();
        player1 = new Player(fighter1, costumes[0], 0, this);
        player2 = new Player(fighter2, costumes[1], 1, this);
        players.add(player1);
        players.add(player2);
        paused = false;
        pauseOverlayHidden = false;
        pauseMenuIndex = 0;

        this.stage = stage;

        gameObjects = new ArrayList<>();
        hittableGameObjects = new ArrayList<>();
        attacks = new ArrayList<>();
        miscGameObjects = new ArrayList<>();
        stageObjects = new ArrayList<>();
        newGameObjects = new ArrayList<>();
        particles = new ArrayList<>();
        deadGameObjects = new ArrayList<>();
        gameObjectsById = new HashMap<>();
        idsByGameObject = new HashMap<>();
        nextId = 0;

        addGameObject(player1);
        addGameObject(player2);

        drawHitboxes = true;

        for (GameObject stageObject : stage.getStageObjects()) {
            addGameObject(stageObject);
        }
    }

    public Battle() {
        this(new Fighter(new Marvin()), new Fighter(new Chain()), new int[]{0, 0}, new Stage(new LastLocation()));
    }

    public void reset() {
        players.clear();
        player1 = new Player(player1.fighter, player1.costume, 0, this);
        player2 = new Player(player1.fighter, player1.costume, 1, this);
        players.add(player1);
        players.add(player2);
        paused = false;

        this.stage = new Stage(new LastLocation());

        gameObjects.clear();
        hittableGameObjects.clear();
        attacks.clear();
        miscGameObjects.clear();
        stageObjects.clear();
        newGameObjects.clear();
        gameObjectsById.clear();
        idsByGameObject.clear();
        particles.clear();
        nextId = 0;

        addGameObject(player1);
        addGameObject(player2);

        for (GameObject stageObject : stage.getStageObjects()) {
            addGameObject(stageObject);
        }
    }

    public void addGameObject(GameObject gameObject) {
        newGameObjects.add(gameObject);
    }

    // Do NOT call this method from a particle
    public void addParticle(Particle particle) {
        particles.add(particle);
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

    public Stage getStage() {
        return stage;
    }

    public int getIdByGameObject(GameObject gameObject) {
        return idsByGameObject.get(gameObject);
    }

    public GameObject getGameObjectById(int id) {
        return gameObjectsById.get(id);
    }

    public List<GameObject> getPlatforms() {
        List<GameObject> platforms = new ArrayList<>();
        for (GameObject stageObject : stageObjects) {
            if (((StageObject) stageObject).isSolid()) platforms.add(stageObject);
        }
        return platforms;
    }

    public List<GameObject> getPassablePlatforms() {
        List<GameObject> platforms = new ArrayList<>();
        for (GameObject stageObject : stageObjects) {
            if (stageObject instanceof PassablePlatform) platforms.add(stageObject);
        }
        return platforms;
    }

    // // If this returns true then it returns to the character select screen
    // public boolean onPauseSelect() {
    //     return paused && pauseMenuIndex == 2;
    // }

    public void update() {

        if (paused) return;

        // if (paused) {
        //     if (player1.keys.isJustPressed(Keys.UP) || player2.keys.isJustPressed(Keys.UP)) pauseMenuIndex++;
        //     if (player1.keys.isJustPressed(Keys.DOWN) || player2.keys.isJustPressed(Keys.DOWN)) pauseMenuIndex++;
        //     return;
        // }

        for (GameObject newGameObject : newGameObjects) {
            gameObjects.add(newGameObject);
            int id = ++nextId;
            gameObjectsById.put(id, newGameObject);
            idsByGameObject.put(newGameObject, id);

            boolean misc = true;

            if (newGameObject instanceof Hittable) {
                hittableGameObjects.add(newGameObject);
                misc = false;
            }

            if (newGameObject instanceof Attack) {
                attacks.add(newGameObject);
                misc = false;
            }

            if (newGameObject instanceof StageObject) {
                stageObjects.add(newGameObject);
            }

            if (misc) {
                miscGameObjects.add(newGameObject);
            }
        }

        newGameObjects.removeAll(newGameObjects);

        List<Particle> deadParticles = new ArrayList<>();
        for (Particle particle : particles) {
            particle.preUpdate();
            if (!particle.alive) deadParticles.add(particle);
        }
        particles.removeAll(deadParticles);
        

        for (GameObject deadGameObject : deadGameObjects) {
            gameObjects.remove(deadGameObject);
            int id = idsByGameObject.get(deadGameObject);
            gameObjectsById.remove(id);
            idsByGameObject.remove(deadGameObject);

            boolean misc = true;

            if (deadGameObject instanceof Hittable) {
                hittableGameObjects.remove(deadGameObject);
                misc = false;
            }

            if (deadGameObject instanceof Attack) {
                attacks.remove(deadGameObject);
                misc = false;
            }

            if (deadGameObject instanceof StageObject) {
                stageObjects.remove(deadGameObject);
            }

            if (misc) {
                miscGameObjects.remove(deadGameObject);
            }
        }
        
        deadGameObjects.removeAll(deadGameObjects);

        for (GameObject gameObject : gameObjects) {
            gameObject.preUpdate();
        }
    }

    public void pause() {
        paused = true;
    }

    public void unpause() {
        paused = false;
    }

    public void togglePause() {
        paused = !paused;
    }

    public Player getPlayer(int player) {
        return players.get(player);
    }

    private void drawHitbox(GameObject gameObject) {
        Rectangle hitbox = gameObject.hitbox;

        Game.game.window.shapeRenderer.setColor(1, 1, 0, 1);
        if (gameObject.getClass().isAssignableFrom(Player.class)) {
            Game.game.window.shapeRenderer.setColor(0, 0, 1, 1);
        } else if (gameObject.getClass().isAssignableFrom(Platform.class)) {
            Game.game.window.shapeRenderer.setColor(0, 1, 0, 1);
        } else if (gameObject.getClass().isAssignableFrom(Attack.class)) {
            Game.game.window.shapeRenderer.setColor(1, 0, 1, 1);
            Game.game.window.shapeRenderer.line(gameObject.hitbox.getCenter(new Vector2()),
                    gameObject.hitbox.getCenter(new Vector2()).cpy().add(((Attack) gameObject).knockback));
            Game.game.window.shapeRenderer.setColor(1, 0, 0, 1);
        }

        Game.game.window.shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    public void render(Seagraphics g) {
        g.scalableDraw(g.imageProvider.getImage(stage.background), -1152 / 2, -704 / 2, 1152, 704);

        for (GameObject misc : miscGameObjects) {
            misc.render(g);
            if (drawHitboxes) drawHitbox(misc);
        }

        for (GameObject attack : attacks) {
            attack.render(g);
            if (drawHitboxes) drawHitbox(attack);
        }

        for (GameObject player : players) {
            player.render(g);
            if (drawHitboxes) drawHitbox(player);
        }

        stage.render(g);

        for (Particle particle : particles) {
            particle.render(g);
        }

        for (Ledge ledge : stage.getLedges()) {
            if (drawHitboxes) {
                Game.game.window.shapeRenderer.setColor(new Color(0, 1, 1, 1));
                Game.game.window.shapeRenderer.rect(ledge.grabBox.x, ledge.grabBox.y, ledge.grabBox.width, ledge.grabBox.height);
            }
        }

        g.scalableDraw(g.imageProvider.getImage("in_battle_hud_p1.png"), -256, -256 - 64, 128, 128);

        g.scalableDraw(g.imageProvider.getImage("in_battle_hud_p2.png"), 256 - 128, -256 - 64, 128, 128);

        for (int i = 0; i < player1.getLives(); i++) {
            g.scalableDraw(g.imageProvider.getImage("life_p1.png"), -256 + 48 + 24 * i, -256 - 12, 20, 20);
        }
        for (int i = 0; i < player2.getLives(); i++) {
            g.scalableDraw(g.imageProvider.getImage("life_p2.png"), 256 - 128 + 48 + 24 * i, -256 - 12, 20, 20);
        }

        g.drawText(player1.damage + "%", g.imageProvider.getFont("SAB_font"), -256 + 116, -256 + 48, 1, Color.WHITE, 1);
        g.drawText(player2.damage + "%", g.imageProvider.getFont("SAB_font"), 256 - 128 + 116, -256 + 48, 1, Color.WHITE, 1);

        if (paused && !pauseOverlayHidden) {
            g.usefulDraw(g.imageProvider.getImage("pause_overlay.png"), -1152 / 2, -704 / 2, 1152, 704, pauseMenuIndex, 3, 0, false, false);
        }
    }
}