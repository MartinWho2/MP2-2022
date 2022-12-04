package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.actor.items.Key;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;


public class Level0KeyRoom extends Level0ItemRoom{
    private final int KEY_ID;
    public Level0KeyRoom(DiscreteCoordinates roomCoordinates, int key_id) {
        super(roomCoordinates);
        KEY_ID = key_id;

    }

    @Override
    protected void createArea() {
        super.createArea();
        Key key = new Key(this, Orientation.DOWN, new DiscreteCoordinates(2, 2), KEY_ID);
        items.add(key);
    }
}
