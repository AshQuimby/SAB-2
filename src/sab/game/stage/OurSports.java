package sab.game.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;

import com.seagull_engine.graphics.ParallaxBackground;
import sab.game.*;
import sab.game.particle.Particle;
import sab.game.settings.Settings;
import sab.util.SabRandom;

public class OurSports extends StageType {
    private Platform platform;
    private boolean stormy;
    private int stormTime;
    private int lightning;

    @Override
    public void init(Stage stage) {
        stage.name = "Our Sports Resort";
        stage.background = "our_sports_background.png";
        stage.id = "our_sports";
        stage.music = "our_sports.mp3";

        stage.parallaxBackground = new ParallaxBackground(Gdx.files.internal("assets/backgrounds/stage/our_sports"));

        stormy = false;
        stormTime = 0;
        lightning = 0;

        platform = new Platform(-256 / 2, -128, 256, 24, "our_sports_platform.png", stage, new StageObjectBehaviour() {
            @Override
            public void update(StageObject stageObject, Battle battle) {
                if (Settings.stageHazards.value && stormy) {
                    stageObject.velocity.y = -0.5f;
                }
            }
        });
        stage.addLedge(new Ledge(platform, new Vector2(256 / 2, -32), 24, 32, -1));
        stage.addLedge(new Ledge(platform, new Vector2(-256 / 2 - 24, -32), 24, 32, 1));

        stage.addStageObject(platform);

        stage.addStageObject(new StageObject(-256 / 2, -128 - Game.game.window.resolutionY + 24, 256, Game.game.window.resolutionY, "our_sports_platform_supports.png", stage, new StageObjectBehaviour() {
            @Override
            public void update(StageObject stageObject, Battle battle) {
                if (Settings.stageHazards.value && stormy) {
                    stageObject.velocity.y = -1f;
                }
            }
        }));
        stage.addStageObject(new StageObject(-256 / 2, -128 - Game.game.window.resolutionY + 24, 256, Game.game.window.resolutionY, "our_sports_platform_supports_solo.png", stage));

        platform = new Platform(-192 / 2, -128, 192, 24, "our_sports_platform_white_ring.png", stage, new StageObjectBehaviour() {
            @Override
            public void update(StageObject stageObject, Battle battle) {
                if (Settings.stageHazards.value && stormy) {
                    stageObject.velocity.y = -0.25f;
                }
            }
        });
        stage.addStageObject(platform, 0);
        
        platform = new Platform(-132 / 2, -128, 132, 24, "our_sports_platform_blue_ring.png", stage);
        stage.addStageObject(platform, 0);
    }

    @Override
    public void update(Battle battle, Stage stage) {
        if ((battle.getPlayer(0).getLives() <= 1 || battle.getPlayer(1).getLives() <= 1) && !stormy) {
            stormy = true;
            stage.background = "our_sports_stormy_background.png";
            stage.parallaxBackground = new ParallaxBackground(Gdx.files.internal("assets/backgrounds/stage/our_sports_stormy"));
            SabSounds.playMusic("our_sports_alt.mp3", true  );
        }

        if (stormy) {
            if (lightning > 0) {
                lightning--;
            }
            if (stormTime % 2 == 0) {
                battle.addParticle(new Particle(0, new Vector2(SabRandom.random(stage.getUnsafeBlastZone().x, stage.getUnsafeBlastZone().x + stage.getUnsafeBlastZone().width + Game.game.window.resolutionX/2), stage.getSafeBlastZone().height), new Vector2(-15, -15), 32, 32, 0, "rain.png"));
            }
            if (stormTime % 600 == 0) {
                lightning = 60;
            }
            if (lightning == 50) SabSounds.playSound("thunder.mp3");
            stormTime++;
        }
    }

    @Override
    public void renderOverlay(Stage stage, Seagraphics g) {
        if (lightning > 0) {
            g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY, 0, 1, 0, false, false, new Color(1, 1, 1, Math.min(1, lightning / 50f)));
        }
    }

    @Override
    public void onPlayerHit(Stage stage, Player player, DamageSource damageSource, boolean finalBlow) {
        if (stormy && finalBlow) {
            lightning = 30;
            SabSounds.playSound("thunder.mp3");
        }
    }
}