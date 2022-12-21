package ch.epfl.cs107.play.game.icrogue.actor.enemies;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.actor.items.Bomb;
import ch.epfl.cs107.play.game.icrogue.actor.projectiles.Arrow;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.io.XMLTexts;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

public class Turret extends Enemy {
    private final List<Orientation> orientations;
    private final static float COOLDOWN = 4.f;
    private float time = 0.f;
    private final static String spriteName = "icrogue/static_npc";
    Animation animation;

    /**
     * Init all useful attributes
     * @param area (Area): owner Area
     * @param orientations (Orientation): orientation of the skeleton
     * @param position (DiscreteCoordinates): position of the entity on the map
     */
    public Turret(Area area, List<Orientation> orientations, DiscreteCoordinates position){
        super(area, Orientation.DOWN,position, 1f);
        this.orientations = orientations;
        Sprite[] sprites = Sprite.extractSprites("other/red_monster",4,1f,1f,this,16,16);
        animation = new Animation(4,sprites);
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public void die() {
        super.die();
        if (Math.random() > 0.5){
            new Bomb(getOwnerArea(),Orientation.DOWN,getCurrentMainCellCoordinates());
        }
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * Counter that set a cooldown between each shot of the turret
     * @param deltaTime (float): time step
     */
    public void count(float deltaTime){
        time += deltaTime;
        if (time >= COOLDOWN){
            for (Orientation orientation : orientations){
                shootArrow(orientation);
            }
            time = 0.f;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        animation.draw(canvas);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        animation.update(deltaTime);
        count(deltaTime);
    }

    private void shootArrow(Orientation orientation){
        speak(XMLTexts.getText("text-enemy-1"), true);
        new Arrow(getOwnerArea(),orientation,getCurrentMainCellCoordinates());
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler)v).interactWith(this , isCellInteraction);
    }
}
