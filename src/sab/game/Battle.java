package sab.game;

import java.sql.Time;
import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import com.seagull_engine.graphics.SeagullCamera;

import sab.dialogue.Dialogue;
import sab.game.ai.BaseAI;
import sab.game.ass_ball.AssBall;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.fighter.Chain;
import sab.game.fighter.Fighter;
import sab.game.fighter.Marvin;
import sab.game.particle.Particle;
import sab.game.stage.LastLocation;
import sab.game.stage.Ledge;
import sab.game.stage.PassablePlatform;
import sab.game.stage.Platform;
import sab.game.stage.Stage;
import sab.game.stage.StageObject;
import sab.net.Keys;
import sab.net.VoidFunction;
import sab.replay.ReplayAI;
import sab.util.Utils;
import sab.util.SABRandom;

public class Battle {
    private long seed;
    private List<Player> players;
    private Player player1;
    private Player player2;
    private Dialogue currentDialogue;

    public Player winner;
    public Player loser;

    private Stage stage;

    private List<GameObject> gameObjects;
    private List<GameObject> hittableGameObjects;
    private List<Attack> attacks;
    private List<GameObject> miscGameObjects;
    private List<GameObject> newGameObjects;
    private List<GameObject> deadGameObjects;
    private List<AssBall> assBalls;
    private List<Particle> particles;
    private Map<Integer, GameObject> gameObjectsById;
    private Map<GameObject, Integer> idsByGameObject;
    private Vector2 cameraShakeVector;
    private int nextId;
    private int endGameTimer;
    private int cameraShake;
    private int slowdown;
    private int slowdownDuration;
    private int freezeFrames;
    private int assBallSpawnTime;
    private boolean zoomOnFreeze;
    private boolean freezeUpdate;
    private int screenShatter;

    // Pause game variables
    // Pausing should not be available on servers unless the server owner pauses the game
    private boolean paused;
    public boolean hasAssBalls;
    public boolean hasStageHazards;
    private boolean pauseOverlayHidden;
    public int pauseMenuIndex;
    private int battleTick;
    private int parryFlash;

    // Screen effect variables
    public boolean drawHitboxes;
    public boolean gameEnded;

    // Callbacks
    private VoidFunction<Particle> spawnParticleCallback;

    public Battle(long seed, Fighter fighter1, Fighter fighter2, int[] costumes, Stage stage, int player1Type, int player2Type, int lives, boolean hasAssBalls, boolean hasStageHazards) {
        SABRandom.createNewBattleRandom(seed);
        Game.controllerManager.setInGameState(true);
        this.stage = stage;
        stage.setBattle(this);
        stage.init();

        this.hasAssBalls = hasAssBalls;
        this.hasStageHazards = hasStageHazards;

        players = new ArrayList<>();

        player1 = new Player(fighter1, costumes[0], 0, lives, this);
        player1.setAI(player1Type == 0 ? null : player1.fighter.getAI(player1, player1Type));

        if (player1Type == -1) player1.setAI(new ReplayAI());

        player2 = new Player(fighter2, costumes[1], 1, lives, this);
        player2.setAI(player2Type == 0 ? null : player2.fighter.getAI(player2, player2Type));

        if (player2Type == -1) player2.setAI(new ReplayAI());

        player2.direction = -1;
        paused = false;
        pauseOverlayHidden = false;
        pauseMenuIndex = 0;
        gameEnded = false;
        endGameTimer = 0;
        cameraShakeVector = new Vector2();

        winner = null;
        loser = null;

        gameObjects = new ArrayList<>();
        hittableGameObjects = new ArrayList<>();
        attacks = new ArrayList<>();
        miscGameObjects = new ArrayList<>();
        newGameObjects = new ArrayList<>();
        particles = new ArrayList<>();
        deadGameObjects = new ArrayList<>();
        assBalls = new ArrayList<>();
        gameObjectsById = new HashMap<>();
        idsByGameObject = new HashMap<>();
        nextId = 0;

        assBallSpawnTime = SABRandom.random(1500, 3000);

        drawHitboxes = false;
        battleTick = 0;

        addGameObject(player1);
        addGameObject(player2);

        for (GameObject stageObject : stage.getStageObjects()) {
            addGameObject(stageObject);
        }

        spawnParticleCallback = (Particle p) -> {};
        addNewGameObjects();
    }

    // Relic of the past
    public Battle() {
        this(System.currentTimeMillis(), new Fighter(new Marvin()), new Fighter(new Chain()), new int[] {0, 0}, new Stage(new LastLocation()), 0, 0, 3, true, true);
    }

    public void setDialogue(Dialogue dialogue) {
        this.currentDialogue = dialogue;
    }

    public void onSpawnParticle(VoidFunction<Particle> callback) {
        spawnParticleCallback = callback;
    }

    public void addGameObject(GameObject gameObject) {
        newGameObjects.add(gameObject);
        int id = ++nextId;
        gameObjectsById.put(id, gameObject);
        idsByGameObject.put(gameObject, id);
    }

    public void addAttack(Attack attack, int[] data) {
        addGameObject(attack);
        attack.onSpawn(data);
    }

    // A slightly neater version of addAttack()
    public void createAttack(AttackType type, Player owner, int[] data) {
        addAttack(new Attack(type, owner), data);
    }

    // Do NOT call this method from a particle
    public void addParticle(Particle particle) {
        particles.add(particle);
        spawnParticleCallback.execute(particle);
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
        if (duration < 60 && players.size() > 2) {
            return;
        }
        freezeFrames = duration;
        slowdown(slowdown, slowdownDuration);
        zoomOnFreeze = zoomIn;
        freezeUpdate = true;
    }

    public void slowdown(int slowdown, int slowdownDuration) {
        this.slowdown = slowdown;
        this.slowdownDuration = slowdownDuration;
        this.zoomOnFreeze = false;
    }

    public void shakeCamera(int intensity) {
        if (Boolean.parseBoolean(Settings.toHashMap().get("screen_shake"))) {
            if (intensity > cameraShake) cameraShake = intensity;
            for (PlayerController playerController : Game.game.controllerManager.getControllers()) {
                playerController.startVibration(cameraShake, Math.min(10, cameraShake) / 10f);
            }
        }
    }

    public int getIdByGameObject(GameObject gameObject) {
        return idsByGameObject.get(gameObject);
    }

    public GameObject getGameObjectById(int id) {
        return gameObjectsById.get(id);
    }

    public List<GameObject> getPlatforms() {
        List<GameObject> platforms = new ArrayList<>();
        for (GameObject stageObject : stage.getStageObjects()) {
            if (stageObject instanceof Platform) platforms.add(stageObject);
        }
        return platforms;
    }

    public List<GameObject> getSolidStageObjects() {
        List<GameObject> platforms = new ArrayList<>();
        for (StageObject stageObject : stage.getStageObjects()) {
            if (stageObject.isSolid()) platforms.add(stageObject);
        }
        return platforms;
    }

    public List<GameObject> getPassablePlatforms() {
        List<GameObject> platforms = new ArrayList<>();
        for (GameObject stageObject : stage.getStageObjects()) {
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


        if (zoomOnFreeze) {
            camera.targetZoom = 0.5f;
            camera.targetPosition = (player1.takingKnockback()) ? player1.getCenter() : player2.getCenter();
        } else {
            float playerDist = player1.getCenter().dst(player2.getCenter());
            if (assBalls.size() > 0) {
                camera.targetPosition = player1.getCenter().cpy().add(player2.getCenter()).scl(0.5f);
                camera.targetPosition = camera.targetPosition.add(assBalls.get(0).getCenter()).scl(0.5f);
                playerDist = (playerDist + camera.getPosition().dst(assBalls.get(0).getCenter())) / 2;
            } else {
                camera.targetPosition = player1.getCenter().cpy().add(player2.getCenter()).scl(0.5f);
            }

            camera.targetZoom = playerDist / 256;
            camera.targetZoom = Math.max(Math.min(stage.maxZoomOut, camera.targetZoom), slowdownDuration > 0 ? 0.5f : 0.75f);
        }

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

        if (slowdownDuration > 0) camera.updateSeagullCamera(16 + slowdown * 2); else camera.updateSeagullCamera(8);
    }

    public void updateCameraEffects() {
        if (cameraShake > 0) {
            float effectiveCameraShake = Math.min(cameraShake / 2f, 6);

            if (effectiveCameraShake > 0) {
                float shakeX = SABRandom.random(-effectiveCameraShake * effectiveCameraShake / 2f, effectiveCameraShake * effectiveCameraShake / 2f);
                float shakeY = SABRandom.random(-effectiveCameraShake * effectiveCameraShake / 2f, effectiveCameraShake * effectiveCameraShake / 2f);

                cameraShakeVector = new Vector2(shakeX, shakeY);
            }

            cameraShake--;
        }
    }
    
    public void updatePlayerKeys() {
        for (Player player : players) {
            player.keys.update();
        }
    }

    public void addNewGameObjects() {
        for (GameObject newGameObject : newGameObjects) {
            gameObjects.add(newGameObject);

            boolean misc = true;

            if (newGameObject instanceof Hittable) {
                hittableGameObjects.add(newGameObject);
                misc = false;
            }

            if (newGameObject instanceof Player) {
                players.add((Player) newGameObject);
                misc = false;
            }

            if (newGameObject instanceof Attack) {
                attacks.add((Attack) newGameObject);
                misc = false;
            }

            if (newGameObject instanceof StageObject) {
                stage.addStageObject((StageObject) newGameObject);
            }

            if (newGameObject instanceof AssBall) {
                assBalls.add((AssBall) newGameObject);
            }


            if (misc) {
                miscGameObjects.add(newGameObject);
            }
        }
        newGameObjects.clear();
    }

    // Returns true if a tick passed, returns false if cut off
    public boolean update() {
        if (currentDialogue != null) {
            continueDialogue();
            updatePlayerKeys();
            return false;
        }

        if (screenShatter > 0) {
            screenShatter--;
        }

        if (gameOver()) {
            paused = false;
            endGameTimer++;
            if (endGameTimer >= 120) {
                gameEnded = true;
            }
        }

        if (paused) {
            if (player1.keys.isJustPressed(Keys.ATTACK) || player2.keys.isJustPressed(Keys.ATTACK)) {
                triggerPauseMenu();
            }
            for (Player player : players) {
                player.keys.update();
            }
            return false;
        }

        for (PlayerController playerController : Game.controllerManager.getControllers()) {
            playerController.checkMacros(Gdx.input.getInputProcessor(), getPlayer(playerController.playerId));
        }

        if (freezeFrames > 0) {
            freezeFrames--;
            if (slowdownDuration > 0) {
                if (Game.game.window.getTick() % slowdown == 0) updateCameraEffects();
            }
            return false;
        } else {
            if (slowdownDuration > 0) {
                slowdownDuration--;
                if (Game.game.window.getTick() % slowdown != 0) {
                    for (Player player : players) {
                        if (player.ignoreSlowdowns) {
                            player.preUpdate();

                            for (Attack attack : attacks) {
                                if (attack.owner == player) {
                                    attack.preUpdate();
                                }
                            }
                        }
                    }
                    for (Player player : players) {
                        if (player.ignoreSlowdowns) {
                            player.lateUpdate();

                            for (Attack attack : attacks) {
                                if (attack.owner == player) {
                                    attack.lateUpdate();
                                }
                            }

                            player.keys.update();
                        }
                    }
                    return false;
                }
            } else {
                zoomOnFreeze = false;
            }
        }
        updateCameraEffects();
        battleTick++;

        if (hasAssBalls) {
            assBallSpawnTime--;
            if (assBallSpawnTime <= 0) {
                spawnAssBall();
                assBallSpawnTime = SABRandom.random(3000, 6000);
            }
        }

        if (winner == null && (player1.getLives() == 0 || player2.getLives() == 0)) {
            endGame(player1.getLives() > 0 ? player1 : (player2.getLives() == 0 ? null : player2), player1.getLives() > 0 ? player2 : (player2.getLives() == 0 ? null : player1));
        }

        // if (paused) {
        //     if (player1.keys.isJustPressed(Keys.UP) || player2.keys.isJustPressed(Keys.UP)) pauseMenuIndex++;
        //     if (player1.keys.isJustPressed(Keys.DOWN) || player2.keys.isJustPressed(Keys.DOWN)) pauseMenuIndex++;
        //     return;
        // }

        addNewGameObjects();

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

            if (deadGameObject instanceof AssBall) {
                assBalls.remove(deadGameObject);
                misc = false;
            }

            if (deadGameObject instanceof StageObject) {
                stage.getStageObjects().remove(deadGameObject);
            }

            if (misc) {
                miscGameObjects.remove(deadGameObject);
            }
        }
        
        deadGameObjects.removeAll(deadGameObjects);

        for (GameObject gameObject : gameObjects) {
            gameObject.preUpdate();
            if (gameObject instanceof Player) {
                Player player = (Player) gameObject;
                if (player.getId() == -1 && player.getLives() <= 0) {
                    deadGameObjects.add(player);
                }
            }
            if (gameObject instanceof Attack) {
                Attack attack = (Attack) gameObject;
                if (!attack.alive) {
                    attack.kill();
                    deadGameObjects.add(attack);
                }
            }
            if (gameObject instanceof AssBall) {
                AssBall assBall = (AssBall) gameObject;
                if (!assBall.isAlive()) {
                    deadGameObjects.add(assBall);
                }
            }
        }

        stage.update();

        for (GameObject gameObject : gameObjects) {
            gameObject.lateUpdate();
        }

        for (Player player : players) {
            player.keys.update();
        }

        return true;
    }

    public void postUpdate() {
        if (!paused && currentDialogue == null) {
            for (Player player : players) {
                if (player.ignoreSlowdowns) player.physicsUpdate(freezeFrames > 0 ? 0 : 1);
                else player.physicsUpdate(freezeFrames > 0 ? 0 : slowdownDuration > 0 ? 1f / slowdown : 1f);
            }
        }

        for (Player player : players) {
            if (player.usedMacro) {
                player.keys.release(Keys.UP);
                player.keys.release(Keys.DOWN);
                player.keys.release(Keys.LEFT);
                player.keys.release(Keys.RIGHT);
                player.keys.release(Keys.ATTACK);
                player.keys.release(Keys.PARRY);
                player.usedMacro = false;
            }
        }
    }

    public void endBattle() {
        for (Player player : players) {
            player.onEndBattle();
        }
        SABRandom.disposeBattleRandom();
    }

    public void onSuccessfulParry() {
        SABSounds.playSound("parry.mp3");
        parryFlash = 15;
    }

    public void spawnAssBall() {
        addGameObject(new AssBall(new Vector2(0, 0), this));
    }

    public void continueDialogue() {
        currentDialogue.next();
        if (player1.keys.isJustPressed(Keys.ATTACK) || player2.keys.isJustPressed(Keys.ATTACK)) {
            if (currentDialogue.finished()) {
                currentDialogue = null;
            } else if (currentDialogue.finishedBlock()) {
                currentDialogue.nextBlock();
            } else {
                currentDialogue.toEnd();
            }
        }
    }

    public void onPressEnter() {
        if (currentDialogue != null) {
            continueDialogue();
        } else if (paused) {
            triggerPauseMenu();
        }
    }

    public boolean gameOver() {
        return endGameTimer > 0;
    }

    public void onSpawnParticle(Particle particle) {

    }

    public void endGame(Player winner, Player loser) {
        Game.game.window.camera.viewportWidth = Game.game.window.resolutionX;
        Game.game.window.camera.viewportHeight = Game.game.window.resolutionY;
        Game.game.window.camera.position.x = 0;
        Game.game.window.camera.position.y = 0;
        if (winner == null) {
            winner = player1;
            winner.fighter.name = "Tie";
            winner.fighter.id = "tie";
            winner.costume = 0;
            loser = player2;
        }
        SABSounds.playSound("final_death.mp3");
        SABSounds.stopMusic();
        this.winner = winner;
        this.loser = loser;
        endGameTimer = 1;
        slowdown(8, 600);
    }

    public void smashScreen() {
        freezeFrame(15, 4, 60, true);
        screenShatter = 90;
        shakeCamera(8);
        SABSounds.playSound("shatter.mp3");
    }

    public void triggerPauseMenu() {
        switch (pauseMenuIndex) {
            case 1 :
                pauseOverlayHidden = !pauseOverlayHidden;
                break;
            case 2 :
                endGame(null, null);
            case 0 :
                unpause();
                break;
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

    public boolean isPaused() {
        return paused;
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

    public List<AssBall> getAssBalls() {
        return assBalls;
    }

    public int getBattleTick() {
        return battleTick;
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
        updateCameraPosition();

        g.getDynamicCamera().position.x += cameraShakeVector.x / (slowdown + 1);
        g.getDynamicCamera().position.y += cameraShakeVector.y / (slowdown + 1);
        g.useStaticCamera();
        g.scalableDraw(g.imageProvider.getImage(stage.background), -1280 / 2, -720 / 2, 1280, 720);
        g.useDynamicCamera();

        g.getDynamicCamera().position.add(cameraShakeVector.x, cameraShakeVector.y, 0);

        stage.renderBackground(g);

        for (GameObject misc : miscGameObjects) {
            misc.render(g);
            if (drawHitboxes) drawHitbox(misc, g);
        }

        stage.renderDetails(g);

        for (Attack attack : attacks) {
            attack.render(g);
            if (drawHitboxes) drawHitbox(attack, g);
        }

        for (Player player : players) {
            player.render(g);
            if (drawHitboxes) drawHitbox(player, g);
        }

        stage.renderPlatforms(g);

        for (Particle particle : particles) {
            particle.render(g);
        }

        for (AssBall assBall : assBalls) {
            assBall.render(g);
        }

        for (Attack attack : attacks) {
            attack.lateRender(g);
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

        g.drawText(player1.damage + "%", Game.getDefaultFont(), -256 + 116, -256 + 48, Game.getDefaultFontScale(), Color.WHITE, 1);
        g.drawText(player2.damage + "%", Game.getDefaultFont(), 256 - 128 + 116, -256 + 48, Game.getDefaultFontScale(), Color.WHITE, 1);

        for (Player player : players) {
            if (player.getId() != -1) {
                player.fighter.renderUI(player, g);
            }
        }

        if (parryFlash > 0) {
            g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), -1280 / 2, -720 / 2, 1280, 720, 0, 1, 0, false, false, new Color(1, 1, 1,
                    parryFlash / 30f));
            parryFlash--;
        }

        if (screenShatter > 0) {
            g.scalableDraw(g.imageProvider.getImage("screen_shatter.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);
        }

        if (gameOver()) {
            g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), -1280 / 2, -720 / 2, 1280, 720, 0, 1, 0, false, false, new Color(0, 0, 0, 1 - ((121f - endGameTimer) / 120)));
            g.drawText("GAME END", Game.getDefaultFont(), 0, 0, 2.5f - ((121f - endGameTimer) / 120) / 2 * Game.getDefaultFontScale(), Color.WHITE, 0);
        }

        if (paused && !pauseOverlayHidden) {
            g.usefulDraw(g.imageProvider.getImage("pause_overlay.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY, pauseMenuIndex, 3, 0, false, false);
        }

        if (currentDialogue != null) {
            currentDialogue.render(g);
        }
    }
}