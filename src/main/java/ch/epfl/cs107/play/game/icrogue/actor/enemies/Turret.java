package ch.epfl.cs107.play.game.icrogue.actor.enemies;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.actor.projectiles.Arrow;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import java.util.List;

public class Turret extends Enemy {
    private final List<Orientation> orientations;
    private final static float COOLDOWN = 4.f;
    private float time = 0.f;

    public Turret(Area area, List<Orientation> orientations, DiscreteCoordinates position){
        super(area, Orientation.DOWN,position,"icrogue/static_npc",1f);
        this.orientations = orientations;

    }
    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

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
    public void update(float deltaTime) {
        super.update(deltaTime);
        count(deltaTime);
    }

    private void shootArrow(Orientation orientation){
        new Arrow(getOwnerArea(),orientation,getCurrentMainCellCoordinates());
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler)v).interactWith(this , isCellInteraction);
    }
}
