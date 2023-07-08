package sab.modloader;

import sab.game.Battle;
import sab.game.fighter.FighterType;

import java.util.List;

public class ModType {
    public String getLoadMessage() {
        return "Loaded mod: " + getClass().getName();
    }

    // Add music and other load tasks here
    public void load() {

    }

    // Make changes ever tick of the battle here, return false to freeze time
    public boolean updateBattle(Battle battle, boolean timeMoving) {
        return true;
    }

    public Class<? extends FighterType>[] getFighters() {
        return null;
    }
}
