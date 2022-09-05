package sab.modloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sab.game.attacks.AttackType;
import sab.game.fighters.FighterType;
import sab.game.stages.StageType;

public class Mod {
    public final String displayName;
    public final String namespace;
    public final String version;
    public final String description;
    public final List<Class<? extends FighterType>> fighters;
    public final List<Class<? extends StageType>> stages;
    public final HashMap<String, Class<? extends AttackType>> attacks;

    public Mod(String displayName, String namespace, String version, String description) {
        this.displayName = displayName;
        this.namespace = namespace;
        this.version = version;
        this.description = description;
        fighters = new ArrayList<Class<? extends FighterType>>();
        stages = new ArrayList<Class<? extends StageType>>();
        attacks = new HashMap<String, Class<? extends AttackType>>();
    }

    public void addFighter(Class<? extends FighterType> fighter) {
        fighters.add(fighter);
    }

    public void addStage(Class<? extends StageType> stage) {
        stages.add(stage);
    }

    public void addAttack(String id, Class<? extends AttackType> attack) {
        attacks.put(id, attack);
    }

    public void addFighters(Class<? extends FighterType>[] fighters) {
        this.fighters.addAll(List.of(fighters));
    }

    public void addStages(Class<? extends StageType>[] stages) {
        this.stages.addAll(List.of(stages));
    }
}
