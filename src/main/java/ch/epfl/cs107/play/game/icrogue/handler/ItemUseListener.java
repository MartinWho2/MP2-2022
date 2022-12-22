package ch.epfl.cs107.play.game.icrogue.handler;

import ch.epfl.cs107.play.game.icrogue.actor.items.*;
public interface ItemUseListener {
    default void canUseItem(Bomb bomb){}
    default void canUseItem(Staff staff){}
    default void canUseItem(Sword sword){}
    default void canUseItem(Cherry cherry){}
    default void canUseItem(Diplome diplome){}
}
