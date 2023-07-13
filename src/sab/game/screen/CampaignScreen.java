package sab.game.screen;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.seagull_engine.Seagraphics;
import sab.dialogue.Dialogues;
import sab.game.Game;
import sab.game.SabSounds;
import sab.game.fighter.*;
import sab.game.stage.*;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;

public class CampaignScreen extends ScreenAdapter {
    private static final int CARD_WIDTH = 136 * 4;
    private static final int CARD_HEIGHT = 161 * 4;

    private static class Level {
        private Stage stage;
        private Fighter opponent;
        private int difficulty;

        public Level(StageType stageType, FighterType fighterType, int difficulty) {
            stage = new Stage(stageType);
            opponent = new Fighter(fighterType);
            this.difficulty = difficulty;
        }
    }

    private int selection;
    private float animationTimer;

    private final Level[] levels = new Level[] {
            new Level(new Warzone(), new Chain(), 1),
            new Level(new LastLocation(), new Walouis(), 1),
            new Level(new Warzone(), new EmperorEvil(), 2),
            new Level(new LastLocation(), new Gus(), 2),
            new Level(new COBS(), new BigSeagull(), 5),
    };

    @Override
    public Screen keyPressed(int keyCode) {
        if (keyCode == Input.Keys.ESCAPE) {
            return new TitleScreen(false);
        }
        if (keyCode == Input.Keys.LEFT || keyCode == Input.Keys.A) {
            selection = getRelativeSelection(-1);
            animationTimer = -30;
            SabSounds.playSound(SabSounds.BLIP);
        }
        if (keyCode == Input.Keys.RIGHT || keyCode == Input.Keys.D) {
            selection = getRelativeSelection(1);
            animationTimer = 30;
            SabSounds.playSound(SabSounds.BLIP);
        }
        if (keyCode == Input.Keys.ENTER) {
            SabSounds.playSound(SabSounds.SELECT);
            BattleScreen battleScreen = new BattleScreen(0, new BattleConfig()); // FIXME: Make campaign levels actually work
            battleScreen.battle.setDialogue(Dialogues.getDialogue("chain_greeting.dlg"));
            return battleScreen;
        }
        return this;
    }

    @Override
    public Screen keyReleased(int keyCode) {
        return this;
    }

    @Override
    public Screen update() {
        animationTimer *= .8;
        return this;
    }

    private void drawCard(Seagraphics g, float offset, int selection) {
        float xOffset = MathUtils.cos(offset - MathUtils.HALF_PI) * 500;
        float cardSize = -MathUtils.sin(offset - MathUtils.HALF_PI);

        g.scalableDraw(
                g.imageProvider.getImage("campaign_selection.png"),
                xOffset - CARD_WIDTH * cardSize / 2,
                -CARD_HEIGHT * cardSize / 2,
                (int) (CARD_WIDTH * cardSize),
                (int) (CARD_HEIGHT * cardSize));
        g.scalableDraw(
                g.imageProvider.getImage(levels[selection].opponent.id + "_render.png"),
                xOffset - 512 * cardSize / 2,
                -(CARD_HEIGHT - 17 * 8) * cardSize / 2,
                (int) (512 * cardSize),
                (int) (512 * cardSize));
        if (cardSize * Game.getDefaultFontScale() > 0) {
            g.drawText(levels[selection].opponent.name, Game.getDefaultFont(), xOffset, (CARD_HEIGHT - 48) * cardSize / 2, cardSize * Game.getDefaultFontScale(), Color.WHITE, 0);
        }
    }

    private int getRelativeSelection(int steps) {
        if (steps < 0) steps += levels.length * -steps;
        return (selection + steps) % levels.length;
    }

    @Override
    public void render(Seagraphics g) {
        drawCard(g, MathUtils.sin(animationTimer * MathUtils.degreesToRadians - 60 * MathUtils.degreesToRadians) * MathUtils.HALF_PI, getRelativeSelection(-2));
        drawCard(g, MathUtils.sin(animationTimer * MathUtils.degreesToRadians + 60 * MathUtils.degreesToRadians) * MathUtils.HALF_PI, getRelativeSelection(2));
        drawCard(g, MathUtils.sin(animationTimer * MathUtils.degreesToRadians - 30 * MathUtils.degreesToRadians) * MathUtils.HALF_PI, getRelativeSelection(-1));
        drawCard(g, MathUtils.sin(animationTimer * MathUtils.degreesToRadians + 30 * MathUtils.degreesToRadians) * MathUtils.HALF_PI, getRelativeSelection(1));
        drawCard(g, MathUtils.sin(animationTimer * MathUtils.degreesToRadians) * MathUtils.HALF_PI, selection);
    }

    @Override
    public void close() {

    }
}
