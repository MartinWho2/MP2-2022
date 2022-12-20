package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.icrogue.actor.items.Item;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;
import java.util.List;

public abstract class Level0ItemRoom extends Level0Room {
    protected List<Item> items;

    /**
     * Init all useful class attributes
     * @param roomCoordinates (DiscreteCoordinates): room coordinates on the roomMap
     */
    public Level0ItemRoom(DiscreteCoordinates roomCoordinates) {
        super(roomCoordinates);
        items = new ArrayList<>();
    }

    @Override
    public boolean challengeCompleted() {
        // challenge is completed when all items are collected
        for (Item item : items) {
            if (!item.isCollected()) return false;
        }
        return true;
    }
}