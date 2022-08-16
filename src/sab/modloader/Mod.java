package sab.modloader;

import java.util.ArrayList;
import java.util.List;

import sab.game.fighters.FighterType;

public class Mod {
    public final String displayName;
    public final String namespace;
    public final String version;
    public final String description;
    public final List<FighterType> fighters;

    public Mod(String displayName, String namespace, String version, String description) {
        this.displayName = displayName;
        this.namespace = namespace;
        this.version = version;
        this.description = description;
        fighters = new ArrayList<FighterType>();
    }

    public void addFighters(FighterType... fighters) {
        this.fighters.addAll(List.of(fighters));
    }
}
