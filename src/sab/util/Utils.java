package sab.util;

import java.util.List;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import sab.game.Game;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;

public class Utils {
    public static int loop(int old, int increment, int max, int min) {
        if (old + increment >= max) {
            return min + (max - (old + increment));
        }
        if (old + increment < min) {
            return max + min + old + increment;
        }
        return old + increment;
    }

    public static String textWrap(Seagraphics g, String text, float size, int maxLength) {
        return textWrap(g, Game.getDefaultFont(), text, size * Game.getDefaultFontScale(), maxLength);
    }

    public static String textWrap(Seagraphics g, BitmapFont font, String text, float size, int maxLength) {

        String reader = "";

        List<String> textLines = new ArrayList<>();

        String[] splitText = text.split(" ");

        if (splitText.length == 1) {
            textLines.add(splitText[0]);
        } else {
            for (String string : splitText) {
                reader += string + " ";
                if (g.getTextBounds(reader, Game.getDefaultFont(), -Game.game.window.resolutionX / 2 + 32, Game.game.window.resolutionY / 2 - 220, size, -1).width > maxLength || string.equals("\n")) {
                    if(!(reader.equals("") || text.equals(" ")) && !string.equals("\n")) textLines.add(reader);
                    if (!string.equals("\n")) reader = "";
                }
            }
            textLines.add(reader);
        }

        reader = "";

        for (String string : textLines) {
            reader += string + "\n";
        }

        return reader;
    }

    public static boolean aprilFools() {
        return LocalDateTime.now().getDayOfMonth() == 1 && LocalDateTime.now().getMonth() == Month.APRIL;
    }

    public static boolean christmas() {
        return (LocalDateTime.now().getDayOfMonth() == 24 || LocalDateTime.now().getDayOfMonth() == 25) && LocalDateTime.now().getMonth() == Month.DECEMBER;
    }

    public static Vector2 getNearestPointInRect(Vector2 target, Rectangle rect) {
        Vector2 nearest = new Vector2();
        nearest.x = Math.min(Math.max(target.x, rect.x), rect.x + rect.width);
        nearest.y = Math.min(Math.max(target.y, rect.y), rect.y + rect.height);

        return nearest;
    }

    public static Vector2 randomPointInRect(Rectangle rect) {
        return new Vector2(MathUtils.random(rect.x, rect.x + rect.width), MathUtils.random(rect.y, rect.y + rect.height));
    }

    public static Vector2 randomParticleVelocity(float magnitude) {
        return new Vector2(magnitude, 0).rotateDeg(MathUtils.random(0, 360));
    }

    public static String appendCostumeToIdentifier(String base, int costume, String fileFormat) {
        return base + (costume == 0 ? "" : "_alt_" + costume) + "." + fileFormat;
    }

    public static String applyCostumeToFilename(String base, int costume, String fileFormat) {
        return base.replace("." + fileFormat, "_alt_" + costume + "." + fileFormat);
    }
}