package sab.game.screen.extras;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.seagull_engine.Seagraphics;

import sab.game.Game;
import sab.game.SabSounds;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;

public class CreditsScreen extends ScreenAdapter {
    private List<String> text;
    private List<Float> sizes;
    private float scrollDistance;
    private boolean speedUp;

    public CreditsScreen() {
        SabSounds.playMusic("credits.mp3", false);
        speedUp = false;
        text = new ArrayList<>();
        sizes = new ArrayList<>();
        scrollDistance = -Game.game.window.resolutionY / 2 - 8;
        FileHandle credits = Gdx.files.internal("assets/texts/credits.txt");
        File creditsFile = credits.file();
        try {
            Scanner reader = new Scanner(creditsFile);
            
            while (reader.hasNext()) {
                float size = reader.nextFloat();
                String line = reader.nextLine();
                if (line.startsWith(" ")) line = line.substring(1, line.length());
                text.add(line);
                sizes.add(size);
            }

            reader.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void render(Seagraphics g) {
        scrollDistance += 0.25f;
        if (speedUp) scrollDistance += 4.75f;
        g.scalableDraw(g.imageProvider.getImage("background.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);
        float length = 0;
        for (int i = 0; i < text.size(); i++) {
            Rectangle bounds = g.getTextBounds(text.get(i), Game.getDefaultFont(), 0, scrollDistance - length - 8 * i, sizes.get(i) / 25 * Game.getDefaultFontScale(), 0);

            if (!text.get(i).equals("")) { 
                g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), bounds.x - 4, bounds.y - 4, (int) bounds.width + 9, (int) bounds.height + 9, 1, 0, 0, false, false, new Color(0, 0, 0, 0.5f));

                g.drawText(text.get(i), Game.getDefaultFont(), 0, bounds.y + bounds.height, sizes.get(i) / 25 * Game.getDefaultFontScale(), Color.WHITE, 0);
            }

            length += bounds.height + 4;
        }
    }

    @Override
    public Screen keyPressed(int keyCode) {
        if (keyCode == Input.Keys.ENTER || keyCode == Input.Keys.ESCAPE) {
            return new ExtrasScreen();
        }
        if (keyCode == Input.Keys.SPACE) {
            speedUp = true;
        }
        return this;
    }

    @Override
    public Screen keyReleased(int keyCode) {
        if (keyCode == Input.Keys.SPACE) {
            speedUp = false;
        }
        return this;
    }
}
