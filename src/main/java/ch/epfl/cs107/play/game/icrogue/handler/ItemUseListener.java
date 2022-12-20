package ch.epfl.cs107.play.game.icrogue.handler;

import ch.epfl.cs107.play.game.icrogue.actor.items.Bomb;
import ch.epfl.cs107.play.game.icrogue.actor.items.Cherry;
import ch.epfl.cs107.play.game.icrogue.actor.items.Staff;
import ch.epfl.cs107.play.game.icrogue.actor.items.Sword;
//A METTRE DANS LE README
/**
 * This interface is used in this way:
 * When the player wants to use an object, it calls the function useCurrentItem in its inventory
 * The inventory then calls the method tryToUseItem on the selected item
 * And then the item uses the canUseItem method with giving "this" as parameter.
 * The nested class ItemHandler of player can then act differently given which item
 * was used ( mostly for animation purposes).
 * This has been done to prevent to make type tests and making the inventory use directly
 * the items without having to know what kind of item is being used
 */
public interface ItemUseListener {
    default void canUseItem(Bomb bomb){}
    default void canUseItem(Staff staff){}
    default void canUseItem(Sword sword){}
    default void canUseItem(Cherry cherry){}
}
