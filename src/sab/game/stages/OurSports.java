package sab.game.stages;


import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;

import sab.game.Battle;
import sab.game.DamageSource;
import sab.game.Player;
import sab.game.SABSounds;
import sab.game.particles.Particle;

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
        stormy = false;
        stormTime = 0;
        lightning = 0;

        platform = new Platform(-256 / 2, -128, 256, 24, "our_sports_platform.png", stage, new StageObjectBehaviour() {
            @Override
            public void update(StageObject stageObject, Battle battle) {
                if (stormy) {
                    stageObject.velocity.y = -0.5f;
                }
            }
        });
        stage.addLedge(new Ledge(platform, new Vector2(256 / 2, -32), 24, 32, -1));
        stage.addLedge(new Ledge(platform, new Vector2(-256 / 2 - 24, -32), 24, 32, 1));

        stage.addStageObject(platform);

        stage.addStageObject(new StageObject(-256 / 2, -128 - 704 + 24, 256, 704, "our_sports_platform_supports.png", stage, new StageObjectBehaviour() {
            @Override
            public void update(StageObject stageObject, Battle battle) {
                if (stormy) {
                    stageObject.velocity.y = -0.25f;
                }
            }
        }));
        stage.addStageObject(new StageObject(-256 / 2, -128 - 704 + 24, 256, 704, "our_sports_platform_supports_solo.png", stage));

        platform = new Platform(-192 / 2, -128, 192, 24, "our_sports_platform_white_ring.png", stage, new StageObjectBehaviour() {
            @Override
            public void update(StageObject stageObject, Battle battle) {
                if (stormy) {
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
        }

        if (stormy) {
            if (lightning > 0) {
                lightning--;
            }
            if (stormTime % 4 == 0) {
                battle.addParticle(new Particle(0, new Vector2(MathUtils.random(stage.getUnsafeBlastZone().x, stage.getUnsafeBlastZone().x + stage.getUnsafeBlastZone().width + 1152/2), stage.getSafeBlastZone().height), new Vector2(-15, -15), 25, 25, 0, "rain.png"));
            }
            if (stormTime % 600 == 0) {
                lightning = 30;
            }
            stormTime++;
        }
    }

    @Override
    public void renderOverlay(Stage stage, Seagraphics g) {
        if (lightning > 0) {
            if (lightning == 30) SABSounds.playSound("thunder.mp3");
            g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), -1152/2, -704/2, 1152, 704, 0, 1, 0, false, false, new Color(1, 1, 1, lightning / 30f));
        }
    }

    @Override
    public void onPlayerHit(Stage stage, Player player, DamageSource damageSource, boolean finalBlow) {
        // if (finalBlow) lightning = 30;
    }
}