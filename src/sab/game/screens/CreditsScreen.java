package sab.game.screens;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.seagull_engine.Seagraphics;

import sab.screen.Screen;
import sab.screen.ScreenAdapter;

public class CreditsScreen extends ScreenAdapter {
    private List<String> text;
    private List<Float> sizes;
    private float scrollDistance;
    private boolean speedUp;

    public CreditsScreen() {
        speedUp = false;
        text = new ArrayList<>();
        sizes = new ArrayList<>();
        scrollDistance = -704 / 2 - 64;
        FileHandle credits = Gdx.files.internal("assets/texts/credits.txt");
        File creditsFile = credits.file();
        try {
            Scanner reader = new Scanner(creditsFile);
            
            while (reader.hasNext()) {
                float size = Float.parseFloat(reader.next());
                String line = reader.nextLine();
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
        scrollDistance += 0.5f;
        if (speedUp) scrollDistance += 4.5f;
        g.scalableDraw(g.imageProvider.getImage("background.png"), -1152 / 2, -704 / 2, 1152, 704);
        float length = 0;
        for (int i = 0; i < text.size(); i++) {
            length += g.drawText(text.get(i), g.imageProvider.getFont("SAB_font"), 0, scrollDistance - length, sizes.get(i) / 25, Color.WHITE, 0).height + 4;
        } 
    }

    @Override
    public Screen keyPressed(int keyCode) {
        if (keyCode == Input.Keys.ENTER || keyCode == Input.Keys.ENTER) {
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
