package sab.game.screen;

import com.badlogic.gdx.Input;
import sab.game.Game;
import sab.net.Keys;
import sab.net.packet.CharacterSelectPacket;
import sab.net.packet.KeyEventPacket;
import sab.net.packet.Packet;
import sab.net.server.Server;
import sab.net.server.ServerListener;
import sab.screen.Screen;

public class HostedCharacterSelectScreen extends CharacterSelectScreen {
    private Server server;
    private int remoteClient;
    private ServerListener serverListener;

    public HostedCharacterSelectScreen(Server server, int remoteClient) {
        this.server = server;
        this.remoteClient = remoteClient;
        Game.controllerManager.setInGameState(true);

        serverListener = new ServerListener() {
            @Override
            public void connected(int connection) {

            }

            @Override
            public void disconnected(int connection) {

            }

            @Override
            public void received(int connection, Packet packet) {
                if (packet instanceof CharacterSelectPacket characterSelectPacket) {

                    if (!(characterSelectPacket.character < 0 || characterSelectPacket.costume < 0 || characterSelectPacket.character >= player2Fighters.size() || characterSelectPacket.costume >= player2Fighters.get(player2.index).costumes)) {
                        player2.setSelection(characterSelectPacket.character, characterSelectPacket.costume, player2Fighters);
                        player2.ready = characterSelectPacket.ready;
                    }
                }
            }
        };
        server.addServerListener(serverListener);

        update();
    }

    @Override
    public Screen keyPressed(int keyCode) {
        if (keyCode == Input.Keys.UP) {
            keyCode = Input.Keys.W;
        }
        if (keyCode == Input.Keys.DOWN) {
            keyCode = Input.Keys.S;
        }
        if (keyCode == Input.Keys.LEFT) {
            keyCode = Input.Keys.A;
        }
        if (keyCode == Input.Keys.RIGHT) {
            keyCode = Input.Keys.D;
        }
        if (keyCode == Input.Keys.M) {
            keyCode = Input.Keys.F;
        }

        Screen result = super.keyPressed(keyCode);
        server.send(remoteClient, new CharacterSelectPacket(player1.index, player1.costume, player1.ready));

        return result;
    }
}
