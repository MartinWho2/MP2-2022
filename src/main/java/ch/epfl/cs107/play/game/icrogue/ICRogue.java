package ch.epfl.cs107.play.game.icrogue;

import ch.epfl.cs107.play.game.areagame.AreaGame;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.icrogue.actor.ICRoguePlayer;
import ch.epfl.cs107.play.game.icrogue.actor.items.Staff;
import ch.epfl.cs107.play.game.icrogue.actor.items.Cherry;
import ch.epfl.cs107.play.game.icrogue.actor.items.Staff;
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.game.icrogue.area.Level;
import ch.epfl.cs107.play.game.icrogue.area.level0.Level0;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.Level0ItemRoom;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.Level0KeyRoom;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.Level0Room;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;

public class ICRogue extends AreaGame{
    public final static float CAMERA_SCALE_FACTOR = 11.f;

    Level level0;
    private ICRoguePlayer player;
    private ICRogueRoom currentArea;

    /**
     * Add all the areas
     */



    private void initLevel(){
        level0 = new Level0(this);
        DiscreteCoordinates coords = currentArea.getPlayerSpawnPosition();
        player = new ICRoguePlayer(currentArea, Orientation.DOWN, coords);
        player.enterArea(currentArea,new DiscreteCoordinates(5,5));
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            initLevel();
            return true;
        }
        return false;
    }
    public void setCurrentAreaOfLevel(ICRogueRoom currentArea){
        this.currentArea = currentArea;
        setCurrentArea(currentArea.getTitle(),true);
        System.out.println(currentArea instanceof Level0KeyRoom);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Keyboard keyboard = currentArea.getKeyboard();
        if (keyboard.get(Keyboard.R).isPressed()){
            initLevel();
        }
        if (player.getIsChangingRoom()){

        }

    }

    @Override
    public void end() {
    }

    @Override
    public String getTitle() {
        return "ICRogue";
    }

    protected void switchArea() {
        player.leaveArea();
        //areaIndex = 0;
        // ICRogueRoom currentArea = (ICRogueRoom) setCurrentArea(areas[areaIndex], false);
        player.enterArea(currentArea, currentArea.getPlayerSpawnPosition());

        player.strengthen();
    }

}

