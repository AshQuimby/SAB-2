package sab.game;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import com.seagull_engine.graphics.SeagullCamera;

import sab.dialogue.Dialogue;
import sab.game.ai.pathfinding.Edge;
import sab.game.ai.pathfinding.Node;
import sab.game.ass_ball.AssBall;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.fighter.Chain;
import sab.game.fighter.Fighter;
import sab.game.fighter.Marvin;
import sab.game.particle.Particle;
import sab.game.settings.Settings;
import sab.game.stage.*;
import sab.modloader.Mod;
import sab.modloader.ModBattle;
import sab.net.Keys;
import sab.net.VoidFunction;
import sab.replay.ReplayAI;
import sab.util.SabRandom;

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
    private boolean freezeTime;

    // Screen effect variables
    public boolean drawHitboxes;
    public boolean drawPathfindingGraph;
    public boolean gameEnded;

    // Callbacks
    private VoidFunction<Particle> spawnParticleCallback;
    private List<ModBattle> modBattles;

    public Battle(long seed, Fighter fighter1, Fighter fighter2, int[] costumes, Stage stage, int player1Type, int player2Type, int lives, boolean hasAssBalls, boolean hasStageHazards) {
        SabRandom.createNewBattleRandom(seed);

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

        Game.controllerManager.setInGameState(true);
        this.stage = stage;
        stage.setBattle(this);
        stage.init();

        modBattles = new ArrayList<>();
        for (Mod mod : Game.game.mods.values()) {
            for (Class<? extends ModBattle> modBattle : mod.modBattles) {
                try {
                    modBattles.add((ModBattle) modBattle.getConstructors()[0].newInstance(this));
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        this.hasAssBalls = hasAssBalls;
        this.hasStageHazards = hasStageHazards;

        players = new ArrayList<>();

        if (Settings.localSettings.gameMode.asRawValue().equals("health")) {
            player1 = new Player(fighter1, costumes[0], 0, lives, this, 150);
            player2 = new Player(fighter2, costumes[1], 1, lives, this, 150);
        } else {
            player1 = new Player(fighter1, costumes[0], 0, lives, this);
            player2 = new Player(fighter2, costumes[1], 1, lives, this);
        }

        player1.setAI(player1Type == 0 ? null : player1.fighter.getAI(player1, player1Type));

        if (player1Type == -1) player1.setAI(new ReplayAI());

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

        nextId = 0;

        assBallSpawnTime = SabRandom.random(1500, 3000);

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

    public void start() {
        modBattles.forEach((modBattle) -> {
            modBattle.onStart();
        });
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
        if (Settings.localSettings.screenShake.value) {
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

    public List<StageObject> getSolidStageObjects() {
        List<StageObject> platforms = new ArrayList<>();
        for (StageObject stageObject : stage.getStageObjects()) {
            if (stageObject.isSolid()) platforms.add(stageObject);
        }
        return platforms;
    }

    public List<PassablePlatform> getPassablePlatforms() {
        List<PassablePlatform> platforms = new ArrayList<>();
        for (StageObject stageObject : stage.getStageObjects()) {
            if (stageObject instanceof PassablePlatform) platforms.add((PassablePlatform) stageObject);
        }
        return platforms;
    }

    public void updateCameraPosition() {
        SeagullCamera camera = Game.game.window.camera;

        if (Settings.localSettings.staticCamera.value) {
            camera.zoom = stage.maxZoomOut;
            camera.position.set(stage.getSafeBlastZone().getCenter(new Vector2()), 0);
            return;
        }


        if (zoomOnFreeze) {
            camera.targetZoom = 0.5f;
            camera.targetPosition = (player1.takingKnockback()) ? player1.getCenter() : player2.getCenter();
        } else {
            float playerDist = player1.getCenter().dst(player2.getCenter());
            if (Settings.localSettings.followAssBall.value) {
                if (assBalls.size() > 0) {
                    camera.targetPosition = player1.getCenter().cpy().add(player2.getCenter()).scl(0.5f);
                    camera.targetPosition = camera.targetPosition.add(assBalls.get(0).getCenter()).scl(0.25f);
                    playerDist = (playerDist + camera.getPosition().dst(assBalls.get(0).getCenter())) / 2;
                } else {
                    camera.targetPosition = player1.getCenter().cpy().add(player2.getCenter()).scl(0.5f);
                }
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

        if (badX && badX2) camera.targetPosition.x = stage.getSafeBlastZone().getCenter(new Vector2()).x;
        if (badY && badY2) camera.targetPosition.y = stage.getSafeBlastZone().getCenter(new Vector2()).y;

        if (slowdownDuration > 0 && !paused) camera.updateSeagullCamera(16 + slowdown * 2); else camera.updateSeagullCamera(8);
    }

    public void updateCameraEffects() {
        if (cameraShake > 0) {
            float effectiveCameraShake = Math.min(cameraShake / 2f, 6);

            if (effectiveCameraShake > 0) {
                float shakeX = SabRandom.random(-effectiveCameraShake * effectiveCameraShake / 2f, effectiveCameraShake * effectiveCameraShake / 2f);
                float shakeY = SabRandom.random(-effectiveCameraShake * effectiveCameraShake / 2f, effectiveCameraShake * effectiveCameraShake / 2f);

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
                Player player = (Player) newGameObject;
                players.add(player);
                modBattles.forEach((modBattle) -> {
                    modBattle.onPlayerSpawn(player);
                });
                misc = false;
            }

            if (newGameObject instanceof Attack) {
                Attack attack = (Attack) newGameObject;
                attacks.add(attack);
                misc = false;
                modBattles.forEach((modBattle) -> {
                    modBattle.onAttackSpawned(attack);
                });
            }

            if (newGameObject instanceof StageObject) {
                StageObject stageObject = (StageObject) newGameObject;
                stage.addStageObject(stageObject);
                modBattles.forEach((modBattle) -> {
                    modBattle.onStageObjectSpawned(stageObject);
                });
            }

            if (newGameObject instanceof AssBall) {
                AssBall assBall = (AssBall) newGameObject;
                assBalls.add(assBall);
                modBattles.forEach((modBattle) -> {
                    modBattle.onAssBallSpawn(assBall);
                });
            }


            if (misc) {
                miscGameObjects.add(newGameObject);
            }
        }
        newGameObjects.clear();
    }

    // Returns true if a tick passed, returns false if cut off
    public boolean update() {
        freezeTime = freezeFrames > 0 || paused || (slowdownDuration > 0 && Game.game.window.getTick() % slowdown != 0);
        if (currentDialogue != null) {
            continueDialogue();
            updatePlayerKeys();
            return false;
        }

        if (screenShatter > 0) {
            screenShatter--;
        }

        for (Mod mod : Game.game.mods.values()) {
            if (!mod.modType.updateBattle(this, freezeTime)) freezeTime = true;
            modBattles.forEach((modBattle) -> {
                modBattle.update(freezeTime);
            });
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
        if (freezeTime) {
            return false;
        }
        updateCameraEffects();
        battleTick++;

        addNewGameObjects();

        if (hasAssBalls) {
            if (getAssBalls().isEmpty()) {
                assBallSpawnTime--;
                if (assBallSpawnTime <= 0) {
                    spawnAssBall();
                    assBallSpawnTime = SabRandom.random(3000, 6000);
                }
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

            if (deadGameObject instanceof Player) {
                players.remove(deadGameObject);
                misc = false;
            }

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

        for (GameObject gameObject : gameObjects) {
            gameObject.lateUpdate();
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
        SabRandom.disposeBattleRandom();
    }

    public boolean preOnPlayerHit(DamageSource source, Player player) {
        boolean shouldTakeDamage = true;
        for (ModBattle modBattle : modBattles) {
            if (!modBattle.preOnPlayerHit(source, player))
                shouldTakeDamage = false;
        }
        return shouldTakeDamage;
    }

    public void onPlayerHit(DamageSource source, Player player) {
        modBattles.forEach((modBattle) -> {
            modBattle.onPlayerHit(source, player);
        });
    }

    public void onPlayerKilled(Player player) {
        modBattles.forEach((modBattle) -> {
            modBattle.onPlayerKilled(player);
        });
    }

    public void onSuccessfulParry(DamageSource source, Player player) {
        SabSounds.playSound("parry.mp3");
        parryFlash = 15;
        modBattles.forEach((modBattle) -> {
            modBattle.onParry(source, player);
        });
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
        SabSounds.playSound("final_death.mp3");
        SabSounds.stopMusic();
        this.winner = winner;
        this.loser = loser;
        endGameTimer = 1;
        modBattles.forEach((modBattle) -> {
            modBattle.onGameEnd();
        });
        slowdown(8, 600);
    }

    public void smashScreen() {
        freezeFrame(15, 4, 60, true);
        screenShatter = 90;
        shakeCamera(8);
        SabSounds.playSound("shatter.mp3");
        modBattles.forEach((modBattle) -> {
            modBattle.onScreenShatter();
        });
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

        modBattles.forEach((modBattle) -> {
            g.useStaticCamera();
            modBattle.renderBackground(g);
        });

        g.useDynamicCamera();

        for (GameObject misc : miscGameObjects) {
            misc.render(g);
            if (drawHitboxes) drawHitbox(misc, g);
        }

        stage.renderDetails(g);

        modBattles.forEach((modBattle) -> {
            g.useDynamicCamera();
            modBattle.renderBeforeAttacks(g);
        });

        for (Attack attack : attacks) {
            attack.render(g);
            if (drawHitboxes) drawHitbox(attack, g);
        }

        modBattles.forEach((modBattle) -> {
            g.useDynamicCamera();
            modBattle.renderBeforePlayers(g);
        });

        for (Player player : players) {
            player.render(g);
            if (drawHitboxes) drawHitbox(player, g);
        }

        modBattles.forEach((modBattle) -> {
            g.useDynamicCamera();
            modBattle.renderBeforePlatforms(g);
        });


        stage.renderPlatforms(g);

        modBattles.forEach((modBattle) -> {
            g.useDynamicCamera();
            modBattle.renderBeforeParticles(g);
        });

        for (Particle particle : particles) {
            particle.render(g);
        }

        for (AssBall assBall : assBalls) {
            assBall.render(g);
        }

        for (Attack attack : attacks) {
            attack.lateRender(g);
        }

        if (drawHitboxes) {
            for (Ledge ledge : stage.getLedges()) {
                g.shapeRenderer.setColor(new Color(0, 1, 1, 1));
                g.shapeRenderer.rect(ledge.grabBox.x, ledge.grabBox.y, ledge.grabBox.width, ledge.grabBox.height);
            }

            for (Slope slope : stage.getSlopes()) {
                g.shapeRenderer.setColor(1, 1, 0, 1);
                g.shapeRenderer.rect(slope.bounds.x, slope.bounds.y, slope.bounds.width, slope.bounds.height);
                g.shapeRenderer.setColor(0, 1, 0, 1);
                g.shapeRenderer.line(slope.start, slope.end);
                g.shapeRenderer.setColor(1, 0, 0, 1);
                Vector2 midPoint = slope.start.cpy().add(slope.end).scl(.5f);
                g.shapeRenderer.line(midPoint.x, midPoint.y, midPoint.x + slope.outerDirection * 10, midPoint.y);
            }
        }

        if (drawPathfindingGraph) {
            for (Node node : stage.graph.getNodes()) {
                switch (node.type) {
                    case GROUND -> g.shapeRenderer.setColor(0, 1, 0, 1);
                    case LEDGE -> g.shapeRenderer.setColor(0, 1, 1, 1);
                    case AIR -> g.shapeRenderer.setColor(1, 1, 1, 1);
                }
                g.shapeRenderer.circle(node.position.x, node.position.y, 8);
                for (Edge<Node> edge : stage.graph.getEdges(node)) {
                    g.shapeRenderer.line(edge.a.position, edge.b.position);
                }
            }
        }

        modBattles.forEach((modBattle) -> {
            g.useStaticCamera();
            modBattle.renderBeforeUI(g);
        });

        g.useStaticCamera();
        stage.renderOverlay(g);

        for (Player player : players) {
            if (player.getId() != -1) {
                player.renderUI( g);
            }
        }

        modBattles.forEach((modBattle) -> {
            g.useStaticCamera();
            modBattle.renderAfterUI(g);
        });

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
            g.drawText("GAME END", Game.getDefaultFont(), 0, 0, (2.5f - ((121f - endGameTimer) / 120) / 2) * Game.getDefaultFontScale(), Color.WHITE, 0);
        }

        modBattles.forEach((modBattle) -> {
            g.useStaticCamera();
            modBattle.renderAfterAll(g);
        });

        if (paused && !pauseOverlayHidden) {
            g.usefulDraw(g.imageProvider.getImage("pause_overlay.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY, pauseMenuIndex, 3, 0, false, false);
        }

        if (currentDialogue != null) {
            currentDialogue.render(g);
        }
    }
}