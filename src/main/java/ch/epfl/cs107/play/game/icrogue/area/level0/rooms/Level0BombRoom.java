package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.actor.items.Bomb;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Level0BombRoom extends Level0ItemRoom {
    public Level0BombRoom(DiscreteCoordinates roomCoordinates) {
        super(roomCoordinates);
    }

    @Override
    protected void createArea() {
        super.createArea();
        items.add(new Bomb(this, Orientation.DOWN, getRoomCoordinates()));
    }
}
