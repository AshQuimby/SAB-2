package sab.game.screen.battle_adjacent;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.seagull_engine.Seagraphics;

import sab.error.SabError;
import sab.game.Battle;
import sab.game.Game;
import sab.game.SabSounds;
import sab.game.fighter.Fighter;
import sab.game.screen.BattleScreen;
import sab.game.screen.NetScreen;
import sab.game.screen.error.ErrorScreen;
import sab.game.settings.Settings;
import sab.game.stage.BattleConfig;
import sab.game.stage.Stage;
import sab.game.stage.StageType;
import sab.modloader.ModLoader;
import sab.net.client.Client;
import sab.net.packet.*;
import sab.net.server.Server;
import sab.screen.Screen;

public class StageSelectScreen extends NetScreen {
    private final List<Stage> stages;
    private int stageIndex;
    private boolean disconnected;
    private boolean starting;

    private long seed;
    private BattleConfig config;

    public StageSelectScreen(BattleConfig config) {
        super();
        stages = new ArrayList<>();
        stageIndex = 0;

        this.config = config;

        for (Class<? extends StageType> stage : Game.game.stages) {
            Stage drawnStage = new Stage(ModLoader.getStageType(stage));
            drawnStage.setBattle(new Battle(0, new BattleConfig())); // This Battle is a dummy
            drawnStage.init();
            stages.add(drawnStage);
        }
    }

    public StageSelectScreen(Server server, BattleConfig config) {
        super(server);
        stages = new ArrayList<>();
        stageIndex = 0;

        this.config = config;

        for (Class<? extends StageType> stage : Game.game.stages) {
            Stage drawnStage = new Stage(ModLoader.getStageType(stage));
            drawnStage.setBattle(new Battle(0, new BattleConfig())); // This Battle is a dummy
            drawnStage.init();
            stages.add(drawnStage);
        }
    }

    public StageSelectScreen(Client client, BattleConfig config) {
        super(client);
        stages = new ArrayList<>();
        stageIndex = 0;

        this.config = config;

        for (Class<? extends StageType> stage : Game.game.stages) {
            Stage drawnStage = new Stage(ModLoader.getStageType(stage));
            drawnStage.setBattle(new Battle(0, new BattleConfig())); // This Battle is a dummy
            drawnStage.init();
            stages.add(drawnStage);
        }
    }

    @Override
    protected void receive(Packet p) {
        if (p instanceof StageSelectPacket ssp) {
            stageIndex = ssp.stage;
        } else if (p instanceof BattleConfigPacket bcp) {
            seed = bcp.seed;
            config = bcp.config;
            stageIndex = config.stageIndex;
            starting = true;
        }
    }

    @Override
    protected void receive(int connection, Packet p) {
        if (p instanceof StageSelectPacket ssp) {
            if (ssp.stage < 0 || ssp.stage >= stages.size()) {
                server.send(0, new KickPacket("Attempted to select an invalid stage"));
            } else {
                stageIndex = ssp.stage;
            }
        }
    }

    @Override
    protected void disconnected() {
        disconnected = true;
    }

    @Override
    protected void disconnected(int connection) {
        disconnected = true;
    }

    @Override
    public Screen update() {
        if (disconnected) {
            if (host) {
                return new ErrorScreen(new SabError("Client Disconnected", "Player 2 disconnected"));
            } else {
                return new ErrorScreen(new SabError("Lost Connection", "Lost connection to the server"));
            }
        }

        if (starting) {
            config.stageIndex = stageIndex;
            return new BattleScreen(client, seed, config);
        }

        return this;
    }

    @Override
    public void render(Seagraphics g) {
        g.scalableDraw(g.imageProvider.getImage(stages.get(stageIndex).background), -1280 / 2, -720 / 2, 1280 , 720);
        Game.game.window.camera.update();
        g.useDynamicCamera();
        stages.get(stageIndex).renderBackground(g);
        stages.get(stageIndex).renderDetails(g);
        stages.get(stageIndex).renderPlatforms(g);
        g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), -1280 / 2, -720 / 2, 1280 , 720, 0, 1, 0, false, false, new Color(0, 0, 0, 0.5f));
        g.drawText(stages.get(stageIndex).name, Game.getDefaultFont(), 0, 256 + 32, 2 * Game.getDefaultFontScale(), Color.WHITE, 0);
    }

    @Override
    public Screen keyPressed(int keyCode) {
        if (keyCode == Input.Keys.D || keyCode == Input.Keys.RIGHT) {
            stageIndex = sab.util.Utils.loop(stageIndex, 1, stages.size(), 0);
            if (host) {
                server.send(0, new StageSelectPacket(stageIndex));
            } else if (!local) {
                client.send(new StageSelectPacket(stageIndex));
            }
        }
        if (keyCode == Input.Keys.A || keyCode == Input.Keys.LEFT) {
            stageIndex = sab.util.Utils.loop(stageIndex, -1, stages.size(), 0);
            if (host) {
                server.send(0, new StageSelectPacket(stageIndex));
            } else if (!local) {
                client.send(new StageSelectPacket(stageIndex));
            }
        }
        if (keyCode == Input.Keys.ENTER && (host || local)) {
            config.stageIndex = stageIndex;
            seed = System.currentTimeMillis();
            if (host) {
                server.send(0, new BattleConfigPacket(seed, config));
                return new BattleScreen(server, seed, config);
            }
            return new BattleScreen(seed, config);
        }
        if (keyCode == Input.Keys.ESCAPE) return Game.game.globalCharacterSelectScreen;
        SabSounds.playSound(SabSounds.BLIP);
        return this;
    }
}
