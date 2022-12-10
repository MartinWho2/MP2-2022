package ch.epfl.cs107.play.game.icrogue.handler;

import ch.epfl.cs107.play.game.icrogue.actor.items.Bomb;
import ch.epfl.cs107.play.game.icrogue.actor.items.Staff;

public interface ItemUseListener {
    default void canUseItem(Bomb bomb) {}
    default void canUseItem(Staff staff) {}
}
