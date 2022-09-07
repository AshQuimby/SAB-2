package sab.game.screens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.seagull_engine.Seagraphics;

import sab.game.Game;
import sab.game.SABSounds;
import sab.game.fighters.Fighter;
import sab.game.stages.Stage;
import sab.game.stages.StageType;
import sab.modloader.ModLoader;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;

public class StageSelectScreen extends ScreenAdapter {
    
    private List<Stage> stages;
    private int stageIndex, player1Costume, player2Costume;
    private Fighter player1, player2;
    private int player1Type, player2Type;
    

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
            stages.add(new Stage(ModLoader.getStageType(stage)));
        }
    }

    @Override
    public void render(Seagraphics g) {
        g.scalableDraw(g.imageProvider.getImage(stages.get(stageIndex).background), -1152 / 2, -704 / 2, 1152, 704);
        stages.get(stageIndex).renderDetails(g);
        stages.get(stageIndex).renderPlatforms(g);
        g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), -1152 / 2, -704 / 2, 1152, 704, 0, 1, 0, false, false, new Color(0, 0, 0, 0.5f));
        g.drawText(stages.get(stageIndex).name, g.imageProvider.getFont("SAB_font"), 0, 256, 2, Color.WHITE, 0);
    }

    @Override
    public Screen keyPressed(int keyCode) {
        if (keyCode == Input.Keys.D || keyCode == Input.Keys.RIGHT) stageIndex = sab.util.Utils.loop(stageIndex, 1, stages.size(), 0);
        if (keyCode == Input.Keys.A || keyCode == Input.Keys.LEFT) stageIndex = sab.util.Utils.loop(stageIndex, -1, stages.size(), 0);
        if (keyCode == Input.Keys.ENTER) return new LocalBattleScreen(player1, player2, new int[]{player1Costume, player2Costume}, stages.get(stageIndex), player1Type, player2Type);
        if (keyCode == Input.Keys.ESCAPE) return Game.game.globalCharacterSelectScreen;
        SABSounds.playSound(SABSounds.BLIP);
        return this;
    }
}
