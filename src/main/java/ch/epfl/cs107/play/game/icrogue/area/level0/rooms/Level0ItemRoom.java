package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.icrogue.actor.items.Item;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;
import java.util.List;

public abstract class Level0ItemRoom extends Level0Room {
    protected List<Item> items;

    public Level0ItemRoom(DiscreteCoordinates roomCoordinates) {
        super(roomCoordinates);
        items = new ArrayList<>();
    }

    @Override
    public boolean challengeCompleted() {
        for (Item item : items) {
            if (!item.isCollected()) return false;
        }
        return true;
    }

    @Override
    public void playerEnters() {
        this.hasPlayerEntered = true;
    }
}