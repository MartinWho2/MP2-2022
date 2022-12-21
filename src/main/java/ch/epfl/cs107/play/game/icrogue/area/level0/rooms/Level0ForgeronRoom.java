package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.actor.characters.Forgeron;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

public class Level0ForgeronRoom extends Level0ItemRoom{
    private Forgeron forgeron;

    /**
     * Init super
     * @param roomCoords (DiscreteCoordinates): room coordinates on the roomMap
     */
    public Level0ForgeronRoom(DiscreteCoordinates roomCoords){
        super(roomCoords);
    }

    @Override
    public void playerEnters() {
        super.playerEnters();
        if (forgeron != null) forgeron.resetDialog();
    }

    @Override
    public boolean challengeCompleted() {
        // challenge is completed when dialog finished
        return forgeron.getFinishedTalking();
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            forgeron = new Forgeron(this, Orientation.DOWN, new DiscreteCoordinates(5, 5));
            return true;
        }
        return false;
    }
}
