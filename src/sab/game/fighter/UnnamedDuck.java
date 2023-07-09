package sab.game.fighter;

import com.badlogic.gdx.math.Vector2;

import com.seagull_engine.Seagraphics;
import sab.game.Game;
import sab.game.Player;
import sab.game.SabSounds;
import sab.game.ai.AI;
import sab.game.ai.BaseAI;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.unnamed_duck.*;
import sab.game.item.*;
import sab.game.particle.Particle;
import sab.util.WeightedCollection;
import sab.util.SabRandom;

public class UnnamedDuck extends FighterType {
    private Animation quackAnimation;
    private WeightedCollection<Item> items;
    private int defaultItemCoolDown;
    private int itemCoolDown;
    private Attack duckSign;

    @Override
    public void setDefaults(Fighter fighter) {
        fighter.id = "unnamed_duck";
        fighter.name = "Unnamed Duck";
        fighter.hitboxWidth = 48;
        fighter.hitboxHeight = 60;
        fighter.renderWidth = 80;
        fighter.renderHeight = 64;
        fighter.imageOffsetX = -4;
        fighter.imageOffsetY = 2;
        fighter.frames = 10;
        fighter.jumpHeight = 160;
        fighter.friction = .2f;
        fighter.mass = 5f;
        fighter.acceleration = .4f;
        fighter.airJumps = 2;
        fighter.walkAnimation = new Animation(1, 4, 6, true);
        fighter.description = "This unruly duck is constantly causing mischief. Whether it's stealing somebody's gardening tools or committing a political assassination, this duck could be behind it. The strange thing is, nobody knows its name. Researchers have concluded that it does have one. One paper came to the conclusion of [REDACTED], but it is impossible to verify the claim.";
        fighter.debut = "No Name Duck Game";
        fighter.airDodgeSpeed = 9;

        quackAnimation = new Animation(new int[] {5, 6}, 6, true);
        fighter.freefallAnimation = new Animation(new int[] {7}, 1, true);
        fighter.knockbackAnimation = new Animation(new int[] {8}, 1, true);
        fighter.knockbackAnimation = new Animation(new int[] {8}, 1, true);
        fighter.ledgeAnimation = new Animation(new int[] {9}, 1, true);
        fighter.costumes = 3;
        fighter.itemOffset = new Vector2(32, 8);
        initializeItemsList();
        itemCoolDown = 0;
        defaultItemCoolDown = 360;
    }

    @Override
    public AI getAI(Player player, int difficulty) {
        return new BaseAI(player, difficulty, 0) {
            @Override
            public void attack(Vector2 center, Player target, Vector2 targetPosition) {
                if (player.hasItem() && (player.getItem() instanceof Jerrycan || player.getItem() instanceof Match)) {
                    useSideAttack();
                    return;
                }

                if (isDirectlyHorizontal(target.hitbox) && isFacing(targetPosition.x)) {
                    float horizontalDistance = Math.abs(center.x - targetPosition.x);

                    if (horizontalDistance <= 60) {
                        useNeutralAttack();
                    } else if (player.hasItem() && SabRandom.random() * 20 < difficulty) {
                        useSideAttack();
                    }
                } else if (isDirectlyBelow(target.hitbox) && Math.abs(center.y - targetPosition.y) > 32 && SabRandom.random() * 20 < difficulty) {
                    useUpAttack();
                }

                UnnamedDuck duck = (UnnamedDuck) player.fighter.type;
                if (!player.hasItem() && duck.itemCoolDown == 0 && SabRandom.random() * 60 < .8f) {
                    useDownAttack();
                }
            }
        };
    }

    @Override
    public void update(Fighter fighter, Player player) {
        if (player.frame == 1 || player.frame == 2 || player.frame == 3 || player.frame == 4) {
            fighter.itemOffset = new Vector2(40, -6);
        } else {
            fighter.itemOffset = new Vector2(32, 8);
        }
        if (itemCoolDown > 0) {
            itemCoolDown--;
        }
    }

    @Override
    public void neutralAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            quackAnimation.reset();
            player.startAttack(new DuckGrab(), quackAnimation, 8, 8, false);
        }
    }

    @Override
    public void sideAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            quackAnimation.reset();
            player.startRepeatingAttack(new Quack(), quackAnimation, 8, 12, false, new int[0]);
        }
    }

    @Override
    public void upAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            player.velocity.y = 24;
            if (duckSign != null) duckSign.alive = false;
            duckSign = player.startAttack(new DuckSign(), new Animation(new int[]{ 7 }, 1, true), 1, 4, true);
            player.usedRecovery = true;
            SabSounds.playSound("sign.mp3");
        }
    }

    @Override
    public void downAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            if (player.hasItem()) {
                player.tossItem();
                itemCoolDown = (int) (itemCoolDown * 0.75f);
            } else {
                if (itemCoolDown > 0) {
                    for (int i = 0; i < 4 ; i++) {
                        player.battle.addParticle(new Particle(player.getCenter().add(new Vector2(fighter.itemOffset.x * player.direction, fighter.itemOffset.y)), new Vector2(4 * SabRandom.random(), 0).rotateDeg(SabRandom.random() * 360), 32, 32, 0, "smoke.png"));
                    }
                    SabSounds.playSound("sign.mp3");
                } else {
                    Item foundItem = getItem();
                    foundItem.setDefaults();
                    player.pickupItem(foundItem);
                    itemCoolDown = defaultItemCoolDown;
                }
            }
        }
    }

    @Override
    public boolean finalAss(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            player.battle.addAttack(new Attack(new MegaBell(), player), null);
            return true;
        }
        return false;
    }

    @Override
    public void chargeAttack(Fighter fighter, Player player, int charge) {
        
    }

    @Override
    public void charging(Fighter fighter, Player player, int charge) {
        
    }

    public Item getItem() {
        Item item = items.getAndRemove();
        if (items.isEmpty()) initializeItemsList();
        return item;
    }

    public void initializeItemsList() {
        items = new WeightedCollection<>();
        items.add(new Rake(), 1);
        items.add(new Knife(), 1.5f);
        items.add(new BigGun(), 1);
        items.add(new Jerrycan(), 0.5f);
        items.add(new Molotov(), 1);
        items.add(new DuckBomb(), 1);
        items.add(new Axe(), 1);
        items.add(new Plane(), 1.5f);
        items.add(new IceCube(), 1);
        items.add(new Flamethrower(), 0.25f);
        items.add(new RubbishLid(), 1);
    }

    @Override
    public void renderUI(Fighter fighter, Player player, Seagraphics g) {
        g.scalableDraw(g.imageProvider.getImage("duck_ui_back.png"), player.getId() == 0 ? -256 - 56 - 4 : 256 + 4, -256, 56, 56);
        int drawAmount = (int) (56 * (((float) defaultItemCoolDown - itemCoolDown) / defaultItemCoolDown));
        Game.game.window.batch.draw(g.imageProvider.getImage(itemCoolDown == 0 ? "duck_ui_front_full.png" : "duck_ui_front.png"), player.getId() == 0 ? -256 - 56 - 4 : 256 + 4, -256, 56, drawAmount / 4 * 4, 0, 14, 14, -drawAmount / 4, false, true);
    }
}
