package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.actor.items.Cherry;
import ch.epfl.cs107.play.game.icrogue.actor.items.Item;
import ch.epfl.cs107.play.game.icrogue.actor.items.Key;
import ch.epfl.cs107.play.game.icrogue.actor.items.Staff;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;

public class Level0StaffRoom extends Level0ItemRoom{

    /**
     * Init super
     * @param roomCoordinates (DiscreteCoordinates): room coordinates on the roomMap
     */
    public Level0StaffRoom(DiscreteCoordinates roomCoordinates) {
        super(roomCoordinates);
    }

    @Override
    protected void createArea() {
        super.createArea();
        items.add(new Staff(this, Orientation.DOWN, new DiscreteCoordinates(5, 5)));
        items.add(new Cherry(this, Orientation.DOWN, new DiscreteCoordinates(2,2)));
    }

}
