package sab.util;

import java.util.List;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import sab.game.Game;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;
import sab.game.settings.Settings;

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

    public static String[] textWrapArray(Seagraphics g, String text, float size, int maxLength) {
        String reader = "";

        List<String> textLines = new ArrayList<>();

        String[] splitText = text.split(" ");

        if (splitText.length == 1) {
            textLines.add(splitText[0]);
        } else {
            for (String string : splitText) {
                reader += string + " ";
                if (g.getTextBounds(reader, Game.getDefaultFont(), 0, 0, size, 0).width > maxLength || string.equals("\n")) {
                    if(!(reader.equals("") || text.equals(" ")) && !string.equals("\n")) textLines.add(reader);
                    if (!string.equals("\n")) reader = "";
                }
            }
            textLines.add(reader);
        }

        return textLines.toArray(new String[textLines.size()]);
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
                if (g.getTextBounds(reader, font, 0, 0, size, 0).width > maxLength || string.equals("\n")) {
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
        return new Vector2(SabRandom.random(rect.x, rect.x + rect.width), SabRandom.random(rect.y, rect.y + rect.height));
    }

    public static Vector2 randomParticleVelocity(float magnitude) {
        return new Vector2(magnitude, 0).rotateDeg(SabRandom.random(0, 360)).scl(SabRandom.random());
    }

    public static String appendCostumeToIdentifier(String base, int costume, String fileFormat) {
        return base + (costume == 0 ? "" : "_alt_" + costume) + "." + fileFormat;
    }

    public static String applyCostumeToFilename(String base, int costume, String fileFormat) {
        return base.replace("." + fileFormat, "_alt_" + costume + "." + fileFormat);
    }

    public static void drawButton(Seagraphics g, float x, float y, String text, float textSize, boolean highlighted) {
        drawButton(g, x, y, text, textSize, highlighted, 0);
    }

    public static Vector2 getRayIntersection(Vector2 start, Vector2 end, Rectangle... hitboxes) {
        // Handle vertical lines
        if (end.x - start.x == 0) {
            float x = start.x;
            for (Rectangle rect : hitboxes) {
                float y;
                if (Math.abs(start.y - rect.y) < Math.abs(start.y - (rect.y + rect.height))) y = rect.y;
                else y = rect.y + rect.height;
                if (rect.x < x && rect.x + rect.width > x) return new Vector2(x, y);
            }
            return null;
        }

        if (end.x < start.x) {
            Vector2 temp = end;
            end = start;
            start = temp;
        }
        Rectangle bounds = new Rectangle(start.x, Math.min(start.y, end.y), end.x - start.x, Math.max(start.y, end.y) - Math.min(start.y, end.y));

        float m = (end.y - start.y) / (end.x - start.x);
        float b = start.y - m * start.x;

        for (Rectangle rect : hitboxes) {
            float y = rect.y;
            float x = (y - b) / m;
            if (x >= rect.x && x <= rect.x + rect.width && bounds.contains(x, y)) return new Vector2(x, y);

            y = rect.y + rect.height;
            x = (y - b) / m;
            if (x >= rect.x && x <= rect.x + rect.width && bounds.contains(x, y)) return new Vector2(x, y);

            x = rect.x;
            y = m * x + b;
            if (y >= rect.y && y <= rect.y + rect.height && bounds.contains(x, y)) return new Vector2(x, y);

            x = rect.x + rect.width;
            y = m * x + b;
            if (y >= rect.y && y <= rect.y + rect.height && bounds.contains(x, y)) return new Vector2(x, y);
        }

        return null;
    }

    public static boolean raycast(Vector2 start, Vector2 end, Rectangle... hitboxes) {
        return getRayIntersection(start, end, hitboxes) != null;
    }

    public static boolean raycast(Vector2 position, float rotation, float distance, Rectangle... hitboxes) {
        return raycast(position, position.cpy().add(MathUtils.cosDeg(rotation) * distance, MathUtils.sinDeg(rotation) * distance), hitboxes);
    }

    public static void drawButton(Seagraphics g, float x, float y, String text, float textSize, boolean highlighted, int anchor) {
        Rectangle textBounds = g.getTextBounds(text, g.imageProvider.getFont(Settings.font.asRawValue()), x, y, textSize, anchor);
        textBounds.height -= 1;
        float a = textSize / Game.getDefaultFontScale() * 16;
        textBounds.width -= a;
        textBounds.height -= a;
        Texture image = g.imageProvider.getImage("button_patch" + (highlighted ? "_highlighted" : "") + ".png");

        Game.game.window.batch.draw(image, textBounds.x - a, textBounds.y - a, a, a, 0, 0, .4f, .4f);
        Game.game.window.batch.draw(image, textBounds.x + textBounds.width, textBounds.y - a, a, a, .6f, 0, 1, .4f);
        Game.game.window.batch.draw(image, textBounds.x - a, textBounds.y + textBounds.height, a, a, 0, .6f, .4f, 1);
        Game.game.window.batch.draw(image, textBounds.x + textBounds.width, textBounds.y + textBounds.height, a, a, .6f, .6f, 1, 1);

        Game.game.window.batch.draw(image, textBounds.x - a, textBounds.y, a, textBounds.height, 0, .4f, .4f, .6f);
        Game.game.window.batch.draw(image, textBounds.x + textBounds.width, textBounds.y, a, textBounds.height, .6f, .4f, 1f, .6f);
        Game.game.window.batch.draw(image, textBounds.x, textBounds.y - a, textBounds.width, a, .4f, 0f, .6f, .4f);
        Game.game.window.batch.draw(image, textBounds.x, textBounds.y + textBounds.height, textBounds.width, a, .4f, .6f, .6f, 1f);

        Game.game.window.batch.draw(image, textBounds.x, textBounds.y, textBounds.width, textBounds.height, .4f, .4f, .6f, .6f);
        g.drawText(text, g.imageProvider.getFont(Settings.font.asRawValue()), textBounds.x + textBounds.width / 2, textBounds.y + textBounds.height * 3.5f + 2, textSize, highlighted ? Color.WHITE : Color.LIGHT_GRAY, 0);
    }
}