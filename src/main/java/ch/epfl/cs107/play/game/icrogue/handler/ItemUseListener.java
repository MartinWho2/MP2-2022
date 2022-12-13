package ch.epfl.cs107.play.game.icrogue.handler;

import ch.epfl.cs107.play.game.icrogue.actor.items.Bomb;
import ch.epfl.cs107.play.game.icrogue.actor.items.Staff;
import ch.epfl.cs107.play.game.icrogue.actor.items.Sword;

public interface ItemUseListener {
    default void canUseItem(Bomb bomb){}
    default void canUseItem(Staff staff){}
    default void canUseItem(Sword sword){}
}
