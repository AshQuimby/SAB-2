package sab.modloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sab.game.attack.AttackType;
import sab.game.fighter.FighterType;
import sab.game.stage.StageType;

// Everything here is handled by the modloader, unless a mod dev really wants to understand how the game works, reading this file is optional

public class Mod {
    public final String displayName;
    public final String namespace;
    public final String version;
    public final String description;
    public final String icon;
    public final List<Class<? extends FighterType>> fighters;
    public final List<Class<? extends StageType>> stages;
    public final List<Class<? extends ModBattle>> modBattles;
    public ModType modType;
    public Mod(String displayName, String namespace, String version, String description, String icon) {
        this.displayName = displayName;
        this.namespace = namespace;
        this.version = version;
        this.description = description;
        this.icon = icon;
        fighters = new ArrayList<>();
        stages = new ArrayList<>();
        modBattles = new ArrayList<>();
    }

    public void addFighter(Class<? extends FighterType> fighter) {
        fighters.add(fighter);
    }

    public void addStage(Class<? extends StageType> stage) {
        stages.add(stage);
    }
    public void addModBattle(Class<? extends ModBattle> modBattle) {
        modBattles.add(modBattle);
    }

    public void addFighters(Class<? extends FighterType>[] fighters) {
        this.fighters.addAll(List.of(fighters));
    }

    public void addStages(Class<? extends StageType>[] stages) {
        this.stages.addAll(List.of(stages));
    }
}
