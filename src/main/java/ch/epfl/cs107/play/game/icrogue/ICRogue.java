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
    private ICRoguePlayer player2;
    private Level0Room currentArea;
    Staff baton;
    Cherry cerise;
    private final String[] areas = {"icrogue/level000","icrogue/level010"};

    private int areaIndex;
    /**
     * Add all the areas
     */



    private void initLevel(){
        level0 = new Level0(this);
        DiscreteCoordinates coords = currentArea.getPlayerSpawnPosition();
        player = new ICRoguePlayer(currentArea, Orientation.DOWN, coords);
        player2= new ICRoguePlayer(currentArea, Orientation.DOWN, coords);
        player2.enterArea(area,new DiscreteCoordinates(2,2));
        player.enterArea(area, coords);
        baton = new Staff(currentArea, Orientation.DOWN, new DiscreteCoordinates(4, 3),"zelda/staff_water.icon",.5f);
        cerise = new Cherry(currentArea,Orientation.DOWN, new DiscreteCoordinates(6,3),"icrogue/cherry",0.6f);
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            initLevel();
            return true;
        }
        return false;
    }


    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Keyboard keyboard = currentArea.getKeyboard();
        if (keyboard.get(Keyboard.R).isPressed()){
            initLevel();
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

