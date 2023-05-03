package sab.game.screen.stage_select;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.seagull_engine.Seagraphics;

import sab.game.Battle;
import sab.game.Game;
import sab.game.SABSounds;
import sab.game.fighter.Fighter;
import sab.game.screen.battle.LocalBattleScreen;
import sab.game.stage.Stage;
import sab.game.stage.StageType;
import sab.modloader.ModLoader;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;

public class StageSelectScreen extends ScreenAdapter {
    private final List<Stage> stages;
    private int stageIndex;
    private final int player1Costume, player2Costume;
    private final Fighter player1, player2;
    private final int player1Type, player2Type;
    

    public StageSelectScreen(Fighter player1, Fighter player2, int player1Costume, int player2Costume, int player1Type, int player2Type) {
        stages = new ArrayList<>();
        stageIndex = 0;

        this.player1 = player1;
        this.player2 = player2;
        this.player1Costume = player1Costume;
        this.player2Costume = player2Costume;
        this.player1Type = player1Type;
        this.player2Type = player2Type;

        for (Class<? extends StageType> stage : Game.game.stages) {
            Stage drawnStage = new Stage(ModLoader.getStageType(stage));
            drawnStage.setBattle(new Battle());
            drawnStage.init();
            stages.add(drawnStage);
        }
    }

    @Override
    public void render(Seagraphics g) {
        g.scalableDraw(g.imageProvider.getImage(stages.get(stageIndex).background), -1280 / 2, -720 / 2, 1280 , 720);
        stages.get(stageIndex).renderDetails(g);
        stages.get(stageIndex).renderPlatforms(g);
        g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), -1280 / 2, -720 / 2, 1280 , 720, 0, 1, 0, false, false, new Color(0, 0, 0, 0.5f));
        g.drawText(stages.get(stageIndex).name, Game.getDefaultFont(), 0, 256, 2, Color.WHITE, 0);
    }

    @Override
    public Screen keyPressed(int keyCode) {
        if (keyCode == Input.Keys.D || keyCode == Input.Keys.RIGHT) stageIndex = sab.util.Utils.loop(stageIndex, 1, stages.size(), 0);
        if (keyCode == Input.Keys.A || keyCode == Input.Keys.LEFT) stageIndex = sab.util.Utils.loop(stageIndex, -1, stages.size(), 0);
        if (keyCode == Input.Keys.ENTER) return new LocalBattleScreen(player1, player2, new int[] {player1Costume, player2Costume}, stages.get(stageIndex), player1Type, player2Type, 3);
        if (keyCode == Input.Keys.ESCAPE) return Game.game.globalCharacterSelectScreen;
        SABSounds.playSound(SABSounds.BLIP);
        return this;
    }
}