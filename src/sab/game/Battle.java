package sab.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import com.seagull_engine.graphics.SeagullCamera;

import sab.game.ai.AI;
import sab.game.ai.BaseAI;
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

    public Player winner;
    public Player loser;

    private Stage stage;

    private List<GameObject> gameObjects;
    private List<GameObject> hittableGameObjects;
    private List<Attack> attacks;
    private List<GameObject> miscGameObjects;
    private List<GameObject> stageObjects;
    private List<GameObject> newGameObjects;
    private List<GameObject> deadGameObjects;
    private List<Particle> particles;
    private Map<Integer, GameObject> gameObjectsById;
    private Map<GameObject, Integer> idsByGameObject;
    private int nextId;
    private int endGameTimer;
    private int cameraShake;
    private int slowdown;
    private int slowdownDuration;
    private int freezeFrames;
    private boolean zoomOnFreeze;
    private boolean freezeUpdate;
    private int screenShatter;

    // Pause game variables
    // Pausing should not be avaliable on servers unless the server owner pauses the game
    private boolean paused;
    private boolean pauseOverlayHidden;
    private int pauseMenuIndex;

    //Screen effect variables

    public boolean drawHitboxes;
    public boolean gameEnded;

    public Battle(Fighter fighter1, Fighter fighter2, int[] costumes, Stage stage, int player1Type, int player2Type) {
        this.stage = stage;

        players = new ArrayList<>();
        player1 = new Player(fighter1, costumes[0], 0, this);
        player1.setAI(player1Type == 0 ? null : new BaseAI(player1, 60 - 10 * player1Type));
        player2 = new Player(fighter2, costumes[1], 1, this);
        player2.setAI(player2Type == 0 ? null : new BaseAI(player2, 60 - 10 * player2Type));
        players.add(player1);
        players.add(player2);
        paused = false;
        pauseOverlayHidden = false;
        pauseMenuIndex = 0;
        gameEnded = false;
        endGameTimer = 0;

        winner = null;
        loser = null;

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

        drawHitboxes = false;

        for (GameObject stageObject : stage.getStageObjects()) {
            addGameObject(stageObject);
        }
    }

    public Battle() {
        this(new Fighter(new Marvin()), new Fighter(new Chain()), new int[]{0, 0}, new Stage(new LastLocation()), 0, 0);
    }

    public void addGameObject(GameObject gameObject) {
        newGameObjects.add(gameObject);
    }

    public void addAttack(Attack attack, int[] data) {
        addGameObject(attack);
        attack.onSpawn(data);
    }

    // Do NOT call this method from a particle
    public void addParticle(Particle particle) {
        particles.add(particle);
    }

    public void removeGameObject(GameObject gameObject) {
        if (!deadGameObjects.contains(gameObject)) deadGameObjects.add(gameObject);
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

    public void freezeFrame(int duration, int slowdown, int slowdownDuration, boolean zoomIn) {
        freezeFrames = duration;
        slowdown(slowdown, slowdownDuration);
        zoomOnFreeze = zoomIn;
        freezeUpdate = true;
    }

    public void slowdown(int slowdown, int slowdownDuration) {
        this.slowdown = slowdown;
        this.slowdownDuration = slowdownDuration;
    }

    public void shakeCamera(int intensity) {
        if (intensity > cameraShake) cameraShake = intensity;
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

    public void updateCameraPosition() {
        SeagullCamera camera = Game.game.window.camera;

        if (Settings.getStaticCamera()) {
            camera.zoom = stage.maxZoomOut;
            camera.position.set(stage.getSafeBlastZone().getCenter(new Vector2()), 0);
            return;
        }


        if (freezeFrames > 0 && zoomOnFreeze) {
            camera.targetZoom = 0.5f;
            camera.targetPosition = (player1.takingKnockback()) ? player1.hitbox.getCenter(new Vector2()) : player2.hitbox.getCenter(new Vector2());
            camera.updateSeagullCamera(1);
        }

        camera.targetPosition = player1.hitbox.getCenter(new Vector2()).cpy().add(player2.hitbox.getCenter(new Vector2())).scl(0.5f);
        float playerDist = player1.hitbox.getCenter(new Vector2()).dst(player2.hitbox.getCenter(new Vector2()));
        camera.targetZoom = playerDist / 256;

        camera.targetZoom = Math.max(Math.min(stage.maxZoomOut, camera.targetZoom), slowdownDuration > 0 ? 0.5f : 0.75f);

        boolean badX = false;
        boolean badY = false;
        boolean badX2 = false;
        boolean badY2 = false;

        if ((camera.targetPosition.x - camera.viewportWidth * camera.zoom / 2) < stage.getSafeBlastZone().x) {
            camera.targetPosition.x = stage.getSafeBlastZone().x + camera.viewportWidth * camera.zoom / 2;
            badX = true;
        }
        if ((camera.targetPosition.y - camera.viewportHeight * camera.zoom / 2) < stage.getSafeBlastZone().y) {
            camera.targetPosition.y = stage.getSafeBlastZone().y + camera.viewportHeight * camera.zoom / 2;
            badY = true;
        }
        if ((camera.targetPosition.x + camera.viewportWidth * camera.zoom / 2) > stage.getSafeBlastZone().x + stage.getSafeBlastZone().width) {
            camera.targetPosition.x = stage.getSafeBlastZone().x + stage.getSafeBlastZone().width - camera.viewportWidth * camera.zoom / 2;
            badX2 = true;
        }
        if ((camera.targetPosition.y + camera.viewportHeight * camera.zoom / 2) > stage.getSafeBlastZone().y + stage.getSafeBlastZone().height) {
            camera.targetPosition.y = stage.getSafeBlastZone().y + stage.getSafeBlastZone().height - camera.viewportHeight * camera.zoom / 2;
            badY2 = true;
        }

        if (badX && badX2) camera.targetPosition.x = stage.getSafeBlastZone().x + stage.getSafeBlastZone().width / 2;
        if (badY && badY2) camera.targetPosition.y = stage.getSafeBlastZone().y + stage.getSafeBlastZone().height / 2;

        if (slowdownDuration > 0) camera.updateSeagullCamera(32 + slowdown * 4); else camera.updateSeagullCamera(8);
    }

    public void updateCameraEffects() {
        float shakeX = MathUtils.random(-cameraShake * cameraShake / 2f, cameraShake * cameraShake / 2f);
        float shakeY = MathUtils.random(-cameraShake * cameraShake / 2f, cameraShake * cameraShake / 2f);

        Game.game.window.camera.position.add(shakeX, shakeY, 0);

        if (cameraShake > 0) cameraShake--;
    }

    public void update() {
        if (endGameTimer > 0) {
            endGameTimer++;
            if (endGameTimer >= 120) {
                gameEnded = true;
            }
        }

        if (paused) return;

        if (freezeFrames > 0) {
            freezeFrames--;
            if (!freezeUpdate) {
                return;
            } else {
                freezeUpdate = false;
            }
        } else {
            if (slowdownDuration > 0) {
                slowdownDuration--;
                if (Game.game.window.getTick() % slowdown != 0) return;
            }
        }

        if (winner == null) {
            if (player1.getLives() == 0 && player2.getLives() == 0) {
                winner = player1;
                winner.fighter.name = "Tie";
                winner.fighter.id = "tie";
                winner.costume = 0;
                loser = player2;
                SABSounds.playSound("final_death.mp3");
                SABSounds.stopMusic();
                endGameTimer = 1;
            }else if (player1.getLives() <= 0) {
                winner = player2;
                loser = player1;
                SABSounds.playSound("final_death.mp3");
                SABSounds.stopMusic();
                endGameTimer = 1;
            } else if (player2.getLives() <= 0){
                winner = player1;
                loser = player2;
                SABSounds.playSound("final_death.mp3");
                SABSounds.stopMusic();
                endGameTimer = 1;
            }
        }

        if (endGameTimer > 0 && Game.game.window.getTick() % 4 != 0) return;

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
                attacks.add((Attack) newGameObject);
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
        
        updateCameraEffects();

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

        stage.update(this);

        for (Player player : players) {
            player.keys.update();
        }
    }

    public boolean gameOver() {
        return endGameTimer > 0;
    }

    public void endGame() {
        Game.game.window.camera.viewportWidth = 1152;
        Game.game.window.camera.viewportHeight = 704;
        Game.game.window.camera.position.x = 0;
        Game.game.window.camera.position.y = 0;
        gameEnded = true;
    }

    public void smashScreen() {
        freezeFrame(15, 8, 60, true);
        screenShatter = 75;
        SABSounds.playSound("shatter.mp3");
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

    public List<Player> getPlayers() {
        return players;
    }

    public List<Attack> getAttacks() {
        return attacks;
    }

    private void drawHitbox(GameObject gameObject, Seagraphics g) {
        Rectangle hitbox = gameObject.hitbox;

        g.shapeRenderer.setColor(1, 1, 0, 1);
        if (gameObject.getClass().isAssignableFrom(Player.class)) {
            g.shapeRenderer.setColor(0, 0, 1, 1);
        } else if (gameObject.getClass().isAssignableFrom(Platform.class)) {
            g.shapeRenderer.setColor(0, 1, 0, 1);
        } else if (gameObject.getClass().isAssignableFrom(Attack.class)) {
            g.shapeRenderer.setColor(1, 0, 1, 1);
            g.shapeRenderer.line(gameObject.hitbox.getCenter(new Vector2()),
                    gameObject.hitbox.getCenter(new Vector2()).cpy().add(((Attack) gameObject).knockback));
                    g.shapeRenderer.setColor(1, 0, 0, 1);
        }

        g.shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    public void render(Seagraphics g) {
        g.useStaticCamera();
        g.scalableDraw(g.imageProvider.getImage(stage.background), -1152 / 2, -704 / 2, 1152, 704);
        g.useDynamicCamera();

        updateCameraPosition();

        stage.renderBackground(g);
        
        for (GameObject misc : miscGameObjects) {
            misc.render(g);
            if (drawHitboxes) drawHitbox(misc, g);
        }

        stage.renderDetails(g);

        for (GameObject attack : attacks) {
            attack.render(g);
            if (drawHitboxes) drawHitbox(attack, g);
        }

        for (GameObject player : players) {
            player.render(g);
            if (drawHitboxes) drawHitbox(player, g);
        }

        stage.renderPlatforms(g);

        for (Particle particle : particles) {
            particle.render(g);
        }

        for (Ledge ledge : stage.getLedges()) {
            if (drawHitboxes) {
                g.shapeRenderer.setColor(new Color(0, 1, 1, 1));
                g.shapeRenderer.rect(ledge.grabBox.x, ledge.grabBox.y, ledge.grabBox.width, ledge.grabBox.height);
            }
        }

        g.useStaticCamera();
        stage.renderOverlay(g);
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

        if (endGameTimer > 0) {
            g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), -1152 / 2, -704 / 2, 1152, 704, 0, 1, 0, false, false, new Color(0, 0, 0, 1 - ((121f - endGameTimer) / 120)));
            g.drawText("GAME END", g.imageProvider.getFont("SAB_font"), 0, 0, 2.5f - ((121f - endGameTimer) / 120) / 2, Color.WHITE, 0);
        }

        if (paused && !pauseOverlayHidden) {
            g.usefulDraw(g.imageProvider.getImage("pause_overlay.png"), -1152 / 2, -704 / 2, 1152, 704, pauseMenuIndex, 3, 0, false, false);
        }
        if (screenShatter > 0) {
            g.scalableDraw(g.imageProvider.getImage("screen_shatter.png"), -1152 / 2, -704 / 2, 1152, 704);
            screenShatter--;
        }
    }
}