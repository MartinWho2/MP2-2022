package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.actor.items.Sword;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Level0SwordRoom extends Level0ItemRoom{

    /**
     * Init super
     * @param roomCoordinates (DiscreteCoordinates): room coordinates on the roomMap
     */
    public Level0SwordRoom(DiscreteCoordinates roomCoordinates) {
        super(roomCoordinates);
    }

    @Override
    protected void createArea() {
        super.createArea();
        items.add(new Sword(this, Orientation.DOWN, new DiscreteCoordinates(4,4)));
    }
}
