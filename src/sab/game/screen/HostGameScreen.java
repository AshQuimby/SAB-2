package sab.game.screen;

import sab.screen.Screen;
import sab.screen.ScreenAdapter;

public class HostGameScreen extends ScreenAdapter {
    public HostGameScreen() {

    }
    
    @Override
    public Screen update() {
        return new HostedBattleScreen(25565);
    }
}