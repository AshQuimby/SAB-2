package sab.game.screen;

import com.badlogic.gdx.Input;
import sab.game.Game;
import sab.net.client.Client;
import sab.net.client.ClientListener;
import sab.net.packet.CharacterSelectPacket;
import sab.net.packet.KeyEventPacket;
import sab.net.packet.Packet;
import sab.screen.Screen;

public class JoinedCharacterSelectScreen extends CharacterSelectScreen {
    private Client client;
    private ClientListener listener;

    public JoinedCharacterSelectScreen(Client client) {
        this.client = client;
        Game.controllerManager.setInGameState(true);

        listener = new ClientListener() {
            @Override
            public void received(Packet packet) {
                if (packet instanceof CharacterSelectPacket csp) {
                    player1.setSelection(csp.character, csp.costume, player1Fighters);
                    player1.ready = csp.ready;
                }
            }

            @Override
            public void disconnected() {

            }
        };

        client.addClientListener(listener);

        update();
    }

    @Override
    public Screen keyPressed(int keyCode) {
        if (keyCode == Input.Keys.W) {
            keyCode = Input.Keys.UP;
        }
        if (keyCode == Input.Keys.S) {
            keyCode = Input.Keys.DOWN;
        }
        if (keyCode == Input.Keys.A) {
            keyCode = Input.Keys.LEFT;
        }
        if (keyCode == Input.Keys.D) {
            keyCode = Input.Keys.RIGHT;
        }
        if (keyCode == Input.Keys.F) {
            keyCode = Input.Keys.M;
        }
        if (keyCode == Input.Keys.ENTER) {
            return this;
        }

        super.keyPressed(keyCode);
        client.send(new CharacterSelectPacket(player2.index, player2.costume, player2.ready));

        return this;
    }
}
