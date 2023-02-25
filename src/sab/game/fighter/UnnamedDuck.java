package sab.game.fighter;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import sab.game.Game;
import sab.game.Player;
import sab.game.PlayerAction;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.items.BigGun;
import sab.game.items.Item;
import sab.game.items.Knife;
import sab.game.items.Rake;
import sab.game.particle.Particle;
import sab.util.WeightedCollection;

public class UnnamedDuck extends FighterType {
    private Animation quackAnimation;
    private WeightedCollection<Item> items;

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
        fighter.jumps = 2;
        fighter.walkAnimation = new Animation(1, 4, 6, true);
        fighter.description = "This unruly duck is constantly causing mischief. Whether it's stealing somebody's gardening tools or committing a political assassination, this duck could be behind it. The strange thing is, nobody knows its name. Researchers have concluded that it does have one. One paper came to the conclusion of [REDACTED], but it is impossible to verify the claim.";
        fighter.debut = "No Name Duck Game";

        quackAnimation = new Animation(new int[] {5, 6}, 6, true);
        fighter.freefallAnimation = new Animation(new int[] {7}, 1, true);
        fighter.knockbackAnimation = new Animation(new int[] {8}, 1, true);
        fighter.knockbackAnimation = new Animation(new int[] {8}, 1, true);
        fighter.ledgeAnimation = new Animation(new int[] {9}, 1, true);
        fighter.costumes = 3;
        fighter.itemOffset = new Vector2(32, 2);
        initializeItemsList();
    }

    @Override
    public void update(Fighter fighter, Player player) {
        if (player.frame == 1 || player.frame == 2 || player.frame == 3 || player.frame == 4) {
            fighter.itemOffset = new Vector2(40, - 6);
        } else {
            fighter.itemOffset = new Vector2(32, 2);
        }
    }

    @Override
    public void neutralAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            
        }
    }

    @Override
    public void sideAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            
        }
    }

    @Override
    public void upAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            
        }
    }

    @Override
    public void downAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            if (player.hasItem()) {
                player.tossItem();
            } else {
                Item foundItem = getItem();
                foundItem.setDefaults();
                player.pickupItem(foundItem);
            }
        }
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
        items.add(new Rake(), 2);
        items.add(new Knife(), 2);
        items.add(new BigGun(), 2);
    }
}
