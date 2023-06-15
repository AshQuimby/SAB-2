package sab.game.screen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.seagull_engine.Seagraphics;

import sab.error.SabError;
import sab.game.Game;
import sab.game.Player;
import sab.game.SABSounds;
import sab.game.fighter.Fighter;
import sab.game.fighter.FighterType;
import sab.game.fighter.Random;
import sab.game.screen.error.ErrorScreen;
import sab.modloader.ModLoader;
import sab.net.client.Client;
import sab.net.packet.CharacterSelectPacket;
import sab.net.packet.Packet;
import sab.net.packet.ScreenTransitionPacket;
import sab.net.server.Server;
import sab.screen.Screen;
import sab.util.SABReader;
import sab.util.Utils;
import sab.util.SABRandom;

public class CharacterSelectScreen extends NetScreen {
    protected static class CharacterSelection {
        public List<Fighter> availableFighters;

        public int index;
        public int costume;
        public int type;

        public boolean ready;
        public boolean sus;

        public CharacterSelection(int index, int costume, int type) {
            this.availableFighters = new ArrayList<>();
            this.index = index;
            this.costume = costume;
            this.type = type;
            this.ready = false;
            this.sus = false;
        }

        public void setSelection(int index, int costume) {
            this.index = index;
            this.costume = costume;

            sus = availableFighters.get(index).id.equals("gus") && costume == 2 && SABRandom.random() <= .01;
        }

        public void updateAvailableFighters() {
            availableFighters.clear();

            for (Class<? extends FighterType> fighter : Game.game.fighters) {
                Fighter f = new Fighter(ModLoader.getFighterType(fighter));
                availableFighters.add(f);
            }
            availableFighters.add(new Fighter(new Random()));
        }
    }

    protected final CharacterSelection player1;
    protected final CharacterSelection player2;
    
    private boolean updateCharacterList;

    private boolean disconnected;

    private boolean starting;

    public CharacterSelectScreen() {
        super();
        player1 = new CharacterSelection(0, 0, 0);
        player2 = new CharacterSelection(1, 0, 0);
        updateCharacterList = true;
    }

    public CharacterSelectScreen(Server server) {
        super(server);
        player1 = new CharacterSelection(0, 0, 0);
        player2 = new CharacterSelection(1, 0, 0);
        updateCharacterList = true;
    }

    public CharacterSelectScreen(Client client) {
        super(client);
        player1 = new CharacterSelection(0, 0, 0);
        player2 = new CharacterSelection(1, 0, 0);
        updateCharacterList = true;
    }

    @Override
    public Screen update() {
        if (updateCharacterList) {
            Game.controllerManager.setInGameState(true);
            player1.updateAvailableFighters();
            player2.updateAvailableFighters();
            updateCharacterList = false;
        }

        if (disconnected) {
            if (host) {
                return new ErrorScreen(new SabError("Client Disconnected", "Player 2 disconnected"));
            } else {
                return new ErrorScreen(new SabError("Lost Connection", "Lost connection to the server"));
            }
        }

        if (starting) {
            return new StageSelectScreen(
                    client,
                    player1.availableFighters.get(player1.index).copy(),
                    player2.availableFighters.get(player2.index).copy(),
                    player1.costume,
                    player2.costume,
                    player1.type,
                    player2.type);
        }

        return this;
    }

    public void start() {
        player1.ready = false;
        player2.ready = false;
    }

    private void changeCharacter(CharacterSelection selection, CharacterSelection otherSelection, int increment) {
        selection.availableFighters.get(selection.index).walkAnimation.reset();
        selection.setSelection(Utils.loop(selection.index, increment, selection.availableFighters.size(), 0), selection.costume);
        selection.costume = 0;
        if (selection.costume >= selection.availableFighters.get(selection.index).costumes) selection.costume = selection.availableFighters.get(selection.index).costumes - 1;
        if (selection.costume == otherSelection.costume && selection.index == otherSelection.index) selection.costume = Utils.loop(selection.costume, 1, otherSelection.availableFighters.get(otherSelection.index).costumes, 0);
        SABSounds.playSound(SABSounds.BLIP);

        if (host && selection == player1) {
            server.send(0, new CharacterSelectPacket(selection.index, selection.costume, selection.ready));
        } else if (!local && selection == player2) {
            client.send(new CharacterSelectPacket(selection.index, selection.costume, selection.ready));
        }
    }

    private void changeCostume(CharacterSelection selection, CharacterSelection otherSelection, int increment) {
        int newCostume = Utils.loop(selection.costume, increment, selection.availableFighters.get(selection.index).costumes, 0);
        if (newCostume == otherSelection.costume && selection.index == otherSelection.index) newCostume = Utils.loop(newCostume, increment, selection.availableFighters.get(selection.index).costumes, 0);
        selection.setSelection(selection.index, newCostume);
        SABSounds.playSound(SABSounds.BLIP);

        if (host && selection == player1) {
            server.send(0, new CharacterSelectPacket(selection.index, selection.costume, selection.ready));
        } else if (!local && selection == player2) {
            client.send(new CharacterSelectPacket(selection.index, selection.costume, selection.ready));
        }
    }

    @Override
    public Screen keyPressed(int keyCode) {
        if (host) {
            if (keyCode == Input.Keys.LEFT) keyCode = Input.Keys.A;
            else if (keyCode == Input.Keys.RIGHT) keyCode = Input.Keys.D;
            else if (keyCode == Input.Keys.DOWN) keyCode = Input.Keys.S;
            else if (keyCode == Input.Keys.UP) keyCode = Input.Keys.W;
            else if (keyCode == Input.Keys.M) keyCode = Input.Keys.F;
            else if (keyCode == Input.Keys.N) keyCode = Input.Keys.T;
        } else if (!local) {
            if (keyCode == Input.Keys.A) keyCode = Input.Keys.LEFT;
            else if (keyCode == Input.Keys.D) keyCode = Input.Keys.RIGHT;
            else if (keyCode == Input.Keys.S) keyCode = Input.Keys.DOWN;
            else if (keyCode == Input.Keys.W) keyCode = Input.Keys.UP;
            else if (keyCode == Input.Keys.F) keyCode = Input.Keys.M;
            else if (keyCode == Input.Keys.T) keyCode = Input.Keys.N;
        }

        switch (keyCode) {
            // Player 1
            case Input.Keys.A -> { if (!player1.ready) changeCharacter(player1, player2, -1); }
            case Input.Keys.D -> { if (!player1.ready) changeCharacter(player1, player2, 1); }
            case Input.Keys.S -> changeCostume(player1, player2, -1);
            case Input.Keys.W -> changeCostume(player1, player2, 1);

            // Player 2
            case Input.Keys.LEFT -> { if (!player2.ready) changeCharacter(player2, player1, -1); }
            case Input.Keys.RIGHT -> { if (!player2.ready) changeCharacter(player2, player1, 1); }
            case Input.Keys.DOWN -> changeCostume(player2, player1, -1);
            case Input.Keys.UP -> changeCostume(player2, player1, 1);

            // Other
            case Input.Keys.ENTER -> {
                if (player1.ready && player2.ready && (local || host)) {
                    player1.availableFighters.get(player1.index).walkAnimation.reset();
                    player2.availableFighters.get(player2.index).walkAnimation.reset();
                    Fighter p1Fighter = player1.availableFighters.get(player1.index).copy();
                    if (p1Fighter.type instanceof Random) {
                        p1Fighter = player1.availableFighters.get(SABRandom.random(player1.availableFighters.size() - 2));
                        player1.costume = p1Fighter.getRandomCostume();
                    }
                    Fighter p2Fighter = player2.availableFighters.get(player2.index).copy();
                    if (p2Fighter.type instanceof Random) {
                        p2Fighter = player2.availableFighters.get(SABRandom.random(player2.availableFighters.size() - 2));
                        player2.costume = p2Fighter.getRandomCostume();
                    }
                    updateTimesPlayed();
                    updateCharacterList = true;
                    SABSounds.playSound(SABSounds.BLIP);
                    if (host) {
                        server.send(0, new ScreenTransitionPacket());
                        return new StageSelectScreen(
                                server,
                                p1Fighter,
                                p2Fighter,
                                player1.costume,
                                player2.costume,
                                player1.type,
                                player2.type);
                    }
                    return new StageSelectScreen(
                            p1Fighter,
                            p2Fighter,
                            player1.costume,
                            player2.costume,
                            player1.type,
                            player2.type);
                }
            }
            case Input.Keys.F -> {
                player1.ready = !player1.ready;
                if (player1.ready) SABSounds.playSound(SABSounds.SELECT); else SABSounds.playSound("deselect.mp3");

                if (host) server.send(0, new CharacterSelectPacket(player1.index, player1.costume, player1.ready));
            }
            case Input.Keys.M -> {
                player2.ready = !player2.ready;
                if (player2.ready) SABSounds.playSound(SABSounds.SELECT); else SABSounds.playSound("deselect.mp3");

                if (!local) client.send(new CharacterSelectPacket(player2.index, player2.costume, player2.ready));
            }
            case Input.Keys.SHIFT_LEFT -> player1.type = Utils.loop(player1.type, 1, 6, 0);
            case Input.Keys.SHIFT_RIGHT -> player2.type = Utils.loop(player2.type, 1, 6, 0);
            case Input.Keys.ESCAPE -> {
                updateCharacterList = true;
                Game.controllerManager.setInGameState(false);
                return new TitleScreen(false);
            }
        }

        return this;
    }

    @Override
    protected void receive(Packet p) {
        if (p instanceof CharacterSelectPacket csp) {
            player1.setSelection(csp.character, csp.costume);

            if (player1.ready && !csp.ready) SABSounds.playSound("deselect.mp3");
            else if (!player1.ready && csp.ready) SABSounds.playSound(SABSounds.SELECT);
            else SABSounds.playSound(SABSounds.BLIP);
            player1.ready = csp.ready;
        } else if (p instanceof ScreenTransitionPacket stp) {
            starting = true;
            SABSounds.playSound(SABSounds.BLIP);
        }
    }

    @Override
    protected void receive(int connection, Packet p) {
        if (p instanceof CharacterSelectPacket csp) {
            player2.setSelection(csp.character, csp.costume);

            if (player2.ready && !csp.ready) SABSounds.playSound("deselect.mp3");
            else if (!player2.ready && csp.ready) SABSounds.playSound(SABSounds.SELECT);
            else SABSounds.playSound(SABSounds.BLIP);
            player2.ready = csp.ready;
        }
    }

    @Override
    protected void disconnected() {
        disconnected = true;
    }

    @Override
    protected void disconnected(int connection) {
        disconnected = true;
    }

    private void updateTimesPlayed() {
        Path path = Paths.get("../saves/times_played.sab");
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException ignored) {
            // Directory already exists
        }

        File file = path.toFile();
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        file = new File("../saves/times_played.sab");
        HashMap<String, String> timesPlayed = SABReader.read(file);

        String id1 = player1.availableFighters.get(player1.index).id;
        String id2 = player2.availableFighters.get(player2.index).id;
        timesPlayed.put(id1, String.valueOf(Integer.parseInt(timesPlayed.getOrDefault(id1, "0")) + 1));
        timesPlayed.put(id2, String.valueOf(Integer.parseInt(timesPlayed.getOrDefault(id2, "0")) + 1));

        File legacyFile = new File("../saves/timesplayed.sab");
        if (legacyFile.exists()) {
            HashMap<String, String> legacyTimesPlayed = SABReader.read(legacyFile);
            for (String fighter : legacyTimesPlayed.keySet()) {
                String amount = legacyTimesPlayed.get(fighter);
                try {
                    Integer.parseInt(amount);
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing legacy times played value for " + fighter);
                    System.out.println("Could not parse " + amount);
                    continue;
                }
                timesPlayed.put(fighter, String.valueOf(Integer.parseInt(timesPlayed.getOrDefault(fighter, "0")) + Integer.parseInt(amount)));
            }
            legacyFile.delete();
        }

        try {
            SABReader.write(timesPlayed, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(Seagraphics g) {
        if (updateCharacterList) {
            Game.controllerManager.setInGameState(true);
            player1.updateAvailableFighters();
            player2.updateAvailableFighters();
            updateCharacterList = false;
        }

        int fighterCount = Game.game.fighters.size() + 1;

        if (player1.costume >= player1.availableFighters.get(player1.index).costumes) player1.costume = player1.availableFighters.get(player1.index).costumes - 1;
        if (player2.costume >= player2.availableFighters.get(player2.index).costumes) player2.costume = player2.availableFighters.get(player2.index).costumes - 1;

        player1.availableFighters.get(player1.index).walkAnimation.stepLooping();
        player2.availableFighters.get(player2.index).walkAnimation.stepLooping();

        g.scalableDraw(g.imageProvider.getImage("character_selector_background_layer_1.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);

        String player1Costume = player1.costume == 0 ? "" : "_alt_" + player1.costume;
        String player2Costume = player2.costume == 0 ? "" : "_alt_" + player2.costume;

//        if (player1.availableFighters.get(player1.index).type instanceof Random) {
//            player1.costume = 1;
//        }
//        if (player2.availableFighters.get(player2.index).type instanceof Random) {
//            player2.costume = 0;
//        }

        if (player1.availableFighters.get(player1.index).id.equals("gus") && player1.costume == 2 && player1.sus) player1Costume += "_alt";
        if (player2.availableFighters.get(player2.index).id.equals("gus") && player2.costume == 2 && player2.sus) player2Costume += "_alt";

        Player player = new Player(player1.availableFighters.get(player1.index).copy());
        player.drawRect.x = -1280 / 2 + 256 - player1.availableFighters.get(player1.index).renderWidth / 2;
        player.drawRect.y = Game.game.window.resolutionY / 2 - 160;
        player.drawRect.width = player1.availableFighters.get(player1.index).renderWidth;
        player.drawRect.height = player1.availableFighters.get(player1.index).renderHeight;
        player.frame = player1.availableFighters.get(player1.index).walkAnimation.getFrame();
        player.fighter.id = player.fighter.id + player1Costume;
        player.direction = 1;
        player.costume = 0;
        player.fighter.render(player, g);

        g.usefulDraw(g.imageProvider.getImage(player1.availableFighters.get(player1.index).id + "_render" + player1Costume + ".png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, 512, 512, 0, 1, 0, true, false);

        g.usefulTintDraw(g.imageProvider.getImage(player1.availableFighters.get(Utils.loop(player1.index, 1, fighterCount, 0)).id + ".png"), -1280 / 2 + 376 - player1.availableFighters.get(Utils.loop(player1.index, 1, fighterCount, 0)).renderWidth / 2, Game.game.window.resolutionY / 2 - 172, player1.availableFighters.get(Utils.loop(player1.index, 1, fighterCount, 0)).renderWidth, player1.availableFighters.get(Utils.loop(player1.index, 1, fighterCount, 0)).renderHeight, 0, player1.availableFighters.get(Utils.loop(player1.index, 1, fighterCount, 0)).frames, 0, true, false, new Color(0.5f, 0.5f, 0.5f, 1f));
        g.usefulTintDraw(g.imageProvider.getImage(player1.availableFighters.get(Utils.loop(player1.index, -1, fighterCount, 0)).id + ".png"), -1280 / 2 + 132 - player1.availableFighters.get(Utils.loop(player1.index, -1, fighterCount, 0)).renderWidth / 2, Game.game.window.resolutionY / 2 - 172, player1.availableFighters.get(Utils.loop(player1.index, -1, fighterCount, 0)).renderWidth, player1.availableFighters.get(Utils.loop(player1.index, -1, fighterCount, 0)).renderHeight, 0, player1.availableFighters.get(Utils.loop(player1.index, -1, fighterCount, 0)).frames, 0, true, false, new Color(0.5f, 0.5f, 0.5f, 1f));

        player = new Player(player2.availableFighters.get(player2.index).copy());
        player.drawRect.x = 1280 / 2 - 256 - player2.availableFighters.get(player2.index).renderWidth / 2;
        player.drawRect.y = Game.game.window.resolutionY / 2 - 160;
        player.drawRect.width = player2.availableFighters.get(player2.index).renderWidth;
        player.drawRect.height = player2.availableFighters.get(player2.index).renderHeight;
        player.frame = player2.availableFighters.get(player2.index).walkAnimation.getFrame();
        player.fighter.id = player.fighter.id + player2Costume;
        player.costume = 0;
        player.fighter.render(player, g);

        g.usefulDraw(g.imageProvider.getImage(player2.availableFighters.get(player2.index).id + "_render" + player2Costume + ".png"), Game.game.window.resolutionX / 2 - 512, -Game.game.window.resolutionY / 2, 512, 512, 0, 1, 0, false, false);

        g.usefulTintDraw(g.imageProvider.getImage(player2.availableFighters.get(Utils.loop(player2.index, -1, fighterCount, 0)).id + ".png"), 1280 / 2 - 380 - player2.availableFighters.get(Utils.loop(player2.index, -1, fighterCount, 0)).renderWidth / 2, Game.game.window.resolutionY / 2 - 172, player2.availableFighters.get(Utils.loop(player2.index, -1, fighterCount, 0)).renderWidth, player2.availableFighters.get(Utils.loop(player2.index, -1, fighterCount, 0)).renderHeight, 0, player2.availableFighters.get(Utils.loop(player2.index, -1, fighterCount, 0)).frames, 0, false, false, new Color(0.5f, 0.5f, 0.5f, 1f));
        g.usefulTintDraw(g.imageProvider.getImage(player2.availableFighters.get(Utils.loop(player2.index, 1, fighterCount, 0)).id + ".png"), 1280 / 2 - 136 - player2.availableFighters.get(Utils.loop(player2.index, 1, fighterCount, 0)).renderWidth / 2, Game.game.window.resolutionY / 2 - 172, player2.availableFighters.get(Utils.loop(player2.index, 1, fighterCount, 0)).renderWidth, player2.availableFighters.get(Utils.loop(player2.index, 1, fighterCount, 0)).renderHeight, 0, player2.availableFighters.get(Utils.loop(player2.index, 1, fighterCount, 0)).frames, 0, false, false, new Color(0.5f, 0.5f, 0.5f, 1f));

        // 92 x 64

        // Don't draw type indicators in multiplayer (Both players must be humans).
        if (local) {
            g.usefulDraw(g.imageProvider.getImage("player_type_indicators.png"), 260 - 132, Game.game.window.resolutionY / 2 - 256, 132, 52, player2.type + 1, 8, 0, false, false);
            g.usefulDraw(g.imageProvider.getImage("player_type_indicators.png"), -260, Game.game.window.resolutionY / 2 - 256, 132, 52, player1.type == 0 ? 0 : player1.type + 1, 8, 0, false, false);
        }

        g.usefulDraw(g.imageProvider.getImage("fighter_selectors.png"), -Game.game.window.resolutionX / 2 + 8, -Game.game.window.resolutionY / 2 + 12, 512, 512, player1.ready ? 1 : 0, 4, 0, false, false);
        g.usefulDraw(g.imageProvider.getImage("fighter_selectors.png"), Game.game.window.resolutionX / 2 - 520, -Game.game.window.resolutionY / 2 + 12, 512, 512, player2.ready ? 3 : 2, 4, 0, false, false);

        g.scalableDraw(g.imageProvider.getImage("character_selector_background_layer_2.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);
        if (player1.ready && player2.ready) {
            g.usefulDraw(g.imageProvider.getImage("fight_button.png"), 0 - 320 / 2, Game.game.window.resolutionY / 2 -100 - 44, 320, 100, (Game.game.window.getTick() / 4) % 10, 11, 0, false, false);
        }
    }
}