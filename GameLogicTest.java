import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Contains all of the unit tests for the GameLogic class
 *
 * @author : tjv26
 */

public class GameLogicTest {

    /**
     * Test which adds a new player to the game and checks
     * whether they were successfully added.
     * This is important as it is the first step, excluding
     * methods in DODServerGUI, to getting players connected
     * and on the map.
     */
    @Test
    public void testAddDoDPlayer() {
        GameLogic gameLogic = new GameLogic();
        gameLogic.addDoDPlayer(1);
        assertEquals(1, gameLogic.getNumPlayers());
    }

    /**
     * Test which checks if the generated spawn location
     * is inside the bounds of the map.
     * This is important as it is the first step, excluding
     * methods in DODServerGUI, to getting players connected
     * and on the map.
     */
    @Test
    public void testGetSpawnLocationX(){
        GameLogic gameLogic =  new GameLogic();
        int[] location = gameLogic.getSpawnLocation();
        assertTrue(location[0] >= 0 && location[0] <= gameLogic.getMap().getMapWidth());
    }

    /**
     * Test which checks if the generated spawn location
     * is inside the bounds of the map.
     * This is important as it is the first step, excluding
     * methods in DODServerGUI, to getting players connected
     * and on the map.
     */
    @Test
    public void testGetSpawnLocationY(){
        GameLogic gameLogic =  new GameLogic();
        int[] location = gameLogic.getSpawnLocation();
        assertTrue(location[1] >= 0 && location[1] <= gameLogic.getMap().getMapHeight());
    }

    /**
     * Test which checks the move command with a valid direction
     * is a success.
     * This is important the player requires the move command
     * in order to move around the map.
     */
    @Test
    public void testProcessCommandMovePositive() {
        GameLogic gameLogic = new GameLogic();
        int playerID = 1;
        gameLogic.addDoDPlayer(playerID);
        DoDPlayer player = gameLogic.getDoDPlayer(playerID);
        //Sets coordinates that are not next to a wall
        player.setXCoordinate(15);
        player.setYCoordinate(5);
        assertEquals("SUCCESS", gameLogic.processCommand("MOVE N", playerID));
    }

    /**
     * Test which checks the look command from a valid point on the map.
     * This is important as the player requires it to recieve their look
     * window.
     */
    @Test
    public void testProcessCommandLookPositive(){
        GameLogic gameLogic =  new GameLogic();
        int playerID = 1;
        gameLogic.addDoDPlayer(playerID);
        //Sets coordinates that are not next to a wall
        DoDPlayer player = gameLogic.getDoDPlayer(playerID);
        player.setXCoordinate(15);
        player.setYCoordinate(5);
        String lookWindow = "....." + "\n" + "....." + "\n" + "..1.." + "\n" + "....." + "\n" + ".....\n";
        assertEquals(lookWindow, gameLogic.processCommand("LOOK", playerID));
    }

    /**
     * Test which checks the pickup command from a valid point on the map
     * i.e. when the player is on top of a gold tile.
     * This is important as in order for the player to be able to pickup
     * gold, this command must work.
     */
    @Test
    public void testProcessCommandPickupPositive(){
        GameLogic gameLogic =  new GameLogic();
        int playerID = 1;
        gameLogic.addDoDPlayer(playerID);
        DoDPlayer player = gameLogic.getDoDPlayer(playerID);
        //Set coordinates to tile with gold on
        player.setXCoordinate(7);
        player.setYCoordinate(2);
        assertEquals("SUCCESS, GOLD COINS: 1", gameLogic.processCommand("PICKUP", playerID));
        assertEquals("GOLD: 0", gameLogic.processCommand("HELLO", playerID));
    }

    /**
     * Test which checks the pickup command from a point on the map
     * without a gold piece on.
     * This is important as the player has to only gain gold when they
     * are on a tile which contains gold.
     */
    @Test
    public void testProcessCommandPickupNegative(){
        GameLogic gameLogic =  new GameLogic();
        int playerID = 1;
        gameLogic.addDoDPlayer(playerID);
        //Sets coordinates that are not on a gold tile
        gameLogic.getDoDPlayer(playerID).setXCoordinate(5);
        gameLogic.getDoDPlayer(playerID).setYCoordinate(5);
        assertEquals("FAIL" + "\n" + "There is nothing to pick up...", gameLogic.processCommand("PICKUP", playerID));
    }

    /**
     * Test which checks the move command with invalid directions.
     * This is important as the player must enter a valid direction
     * when entering a move command.
     */
    @Test
    public void testProcessCommandMoveNegative(){
        GameLogic gameLogic =  new GameLogic();
        int playerID = 1;
        gameLogic.addDoDPlayer(playerID);
        //Sets coordinates that are not on a gold tile
        gameLogic.getDoDPlayer(playerID).setXCoordinate(5);
        gameLogic.getDoDPlayer(playerID).setYCoordinate(5);
        int invalidPlayerID = -1;
        assertEquals("FAIL", gameLogic.processCommand("MOVE", playerID));
        assertEquals("FAIL", gameLogic.processCommand("MOVE R", playerID));
        assertEquals("FAIL", gameLogic.processCommand("", playerID));
    }

    /**
     * Test which checks the move command with invalid directions.
     * This is important as the player must enter a valid direction
     * when entering a move command.
     */
    @Test
    public void testProcessCommandNull(){
        GameLogic gameLogic =  new GameLogic();
        int playerID = 1;
        gameLogic.addDoDPlayer(playerID);
        assertEquals("FAIL", gameLogic.processCommand("", playerID));
    }

    /**
     * Test which checks process command when an invalid player id
     * is passed through.
     * This is important as a move command must be sent from a valid
     * player who is actually in the game, i.e. not one who has recently quit.
     */
    @Test
    public void testProcessCommandIDNegative(){
        GameLogic gameLogic =  new GameLogic();
        int playerID = 1;
        gameLogic.addDoDPlayer(playerID);
        //Sets coordinates that are not on a gold tile
        gameLogic.getDoDPlayer(playerID).setXCoordinate(5);
        gameLogic.getDoDPlayer(playerID).setYCoordinate(5);
        int invalidPlayerID = -1;
        assertEquals("FAIL", gameLogic.processCommand("MOVE N", invalidPlayerID));
        assertEquals("FAIL", gameLogic.processCommand("", invalidPlayerID));
    }

    /**
     * Test which checks the move command with valid directions.
     * This is important as player must be able to move around the map
     * in order to play the game correctly.
     */
    @Test
    public void testMovePositive(){
        GameLogic gameLogic =  new GameLogic();
        gameLogic.addDoDPlayer(1);
        DoDPlayer player = gameLogic.getDoDPlayer(1);
        //Make sure the player doesn't move into a wall
        player.setXCoordinate(5);
        player.setYCoordinate(5);
        assertEquals("SUCCESS", gameLogic.move(player, 'N'));
        assertEquals("SUCCESS", gameLogic.move(player, 'E'));
        assertEquals("SUCCESS", gameLogic.move(player, 'S'));
        assertEquals("SUCCESS", gameLogic.move(player, 'W'));
    }

    /**
     * Test which checks the move command when the user is in a corner
     * of the map.
     * This is important as player must not be able to move through the
     * boundaries (the walls) of the map, as this would cause many errors.
     */
    @Test
    public void testMoveIntoWall(){
        GameLogic gameLogic =  new GameLogic();
        gameLogic.addDoDPlayer(1);
        DoDPlayer player = gameLogic.getDoDPlayer(1);
        //Make player spawn in corner of map
        player.setXCoordinate(1);
        player.setYCoordinate(1);
        //Cannot move into a wall
        assertEquals("FAIL", gameLogic.move(player, 'N'));
        assertEquals("FAIL", gameLogic.move(player, 'W'));
    }

    @Test
    public void testMoveNegative(){
        GameLogic gameLogic =  new GameLogic();
        gameLogic.addDoDPlayer(1);
        DoDPlayer player = gameLogic.getDoDPlayer(1);
        //Make player spawn in corner of map
        player.setXCoordinate(5);
        player.setYCoordinate(5);
        //Invalid move characters
        assertEquals("FAIL", gameLogic.move(player, ' '));
        assertEquals("FAIL", gameLogic.move(player, 'R'));
    }

    /**
     * Test which checks the occupy tile command given a tile which
     * a player is on.
     * This is important as players must not be able to simultaneously
     * be on the same tiles.
     */
    @Test
    public void testOccupyTilePositive(){
        GameLogic gameLogic =  new GameLogic();
        gameLogic.addDoDPlayer(1);
        DoDPlayer player = gameLogic.getDoDPlayer(1);
        int playerX = player.getXCoordinate();
        int playerY = player.getYCoordinate();
        assertEquals(true, gameLogic.isAnotherPlayerOccupyingTile(playerX, playerY));
    }

    /**
     * Test which checks the occupy tile command given a tile which
     * no player currently occupies.
     * This is important as players must be able to move to any tile
     * which does not contain another player.
     */
    @Test
    public void testOccupyTileNegative(){
        GameLogic gameLogic =  new GameLogic();
        gameLogic.addDoDPlayer(1);
        assertEquals(false, gameLogic.isAnotherPlayerOccupyingTile(0, 0));
        assertEquals(false, gameLogic.isAnotherPlayerOccupyingTile(1000, 1000));
    }

    /**
     * Test which checks the pickup method correctly increments the
     * players collected gold when they are on a gold tile.
     * This is important as for a player to win the game, they
     * must meet a certain win condition specifiying the amount of
     * gold they must collect to exit.
     */
    @Test
    public void testPickupPositive(){
        GameLogic gameLogic =  new GameLogic();
        int playerID = 1;
        gameLogic.addDoDPlayer(playerID);
        DoDPlayer player = gameLogic.getDoDPlayer(playerID);
        //Sets coordinates to a tile containing gold
        player.setXCoordinate(7);
        player.setYCoordinate(2);
        assertEquals("SUCCESS, GOLD COINS: 1", gameLogic.pickup(player));
        player.setXCoordinate(11);
        player.setYCoordinate(5);
        assertEquals("SUCCESS, GOLD COINS: 2", gameLogic.pickup(player));
    }

    /**
     * Test which checks the pickup method correctly handles when
     * players attempt to collect gold when they are not on a gold tile.
     * This is important as player must only be able to collect gold when
     * on a specified tile.
     */
    @Test
    public void testPickupNegative(){
        GameLogic gameLogic =  new GameLogic();
        int playerID = 1;
        gameLogic.addDoDPlayer(playerID);
        DoDPlayer player = gameLogic.getDoDPlayer(playerID);
        //Tile with no gold on
        player.setXCoordinate(1);
        player.setYCoordinate(1);
        assertEquals("FAIL" + "\n" + "There is nothing to pick up...", gameLogic.pickup(player));
    }

    /**
     * Test which checks to see if the look window of a player
     * contains any opponents, along with their icons.
     * This is important as a player needs to know where
     * the other players are on the map relative to them.
     */
    @Test
    public void testGetVisibleOpponentsInWindow(){
        GameLogic gameLogic =  new GameLogic();
        gameLogic.addDoDPlayer(0);
        DoDPlayer player1 = gameLogic.getDoDPlayer(0);
        player1.setXCoordinate(1);
        player1.setYCoordinate(1);
        gameLogic.addDoDPlayer(1);
        DoDPlayer player2 = gameLogic.getDoDPlayer(1);
        player2.setXCoordinate(2);
        player2.setYCoordinate(1);

        //Each index stores the i'th character of each column
        char[][] lookP1 = new char[][]{
                { '#', '#', '#', '#', '#'},
                { '#', '#', '#', '#', '#'},
                { '#', '#', '0', '.', '.'},
                { '#', '#', '.', '.', '.'},
                { '#', '#', '.', '.', '.'}
        };

        char[][] lookP2 = new char[][]{
                { '#', '#', '#', '#', '#'},
                { '#', '#', '.', '.', '.'},
                { '#', '#', '1', '.', '.'},
                { '#', '#', '.', '.', '.'},
                { '#', '#', '.', '.', '.'}
        };

        char[][] expectedLookP1 = new char[][]{
                { '#', '#', '#', '#', '#'},
                { '#', '#', '#', '#', '#'},
                { '#', '#', '0', '.', '.'},
                { '#', '#', '1', '.', '.'},
                { '#', '#', '.', '.', '.'}
        };

        char[][] expectedLookP2 = new char[][]{
                { '#', '#', '#', '#', '#'},
                { '#', '#', '0', '.', '.'},
                { '#', '#', '1', '.', '.'},
                { '#', '#', '.', '.', '.'},
                { '#', '#', '.', '.', '.'}
        };

        assertArrayEquals(expectedLookP1, gameLogic.getVisibleOpponents(lookP1, player1));
        assertArrayEquals(expectedLookP2, gameLogic.getVisibleOpponents(lookP2, player2));
    }

    /**
     * Test which checks to see if the look window of a player
     * still contains an opponent even if they are not in a 2
     * unit radius of the player.
     * This is important as a player needs to know where
     * the other players are on the map relative to them.
     */
    @Test
    public void testGetVisibleOpponentsOutWindow(){
        GameLogic gameLogic =  new GameLogic();

        gameLogic.addDoDPlayer(0);
        DoDPlayer player1 = gameLogic.getDoDPlayer(0);
        player1.setXCoordinate(1);
        player1.setYCoordinate(1);
        gameLogic.addDoDPlayer(1);

        DoDPlayer player2 = gameLogic.getDoDPlayer(1);
        player2.setXCoordinate(4);
        player2.setYCoordinate(1);

        //Each index stores the i'th character of each column
        char[][] newLookP1 = new char[][]{
                { '#', '#', '#', '#', '#'},
                { '#', '#', '#', '#', '#'},
                { '#', '#', '0', '.', '.'},
                { '#', '#', '.', '.', '.'},
                { '#', '#', '.', '.', '.'}
        };

        char[][] newExpectedLookP1 = new char[][]{
                { '#', '#', '#', '#', '#'},
                { '#', '#', '#', '#', '#'},
                { '#', '#', '0', '.', '.'},
                { '#', '#', '.', '.', '.'},
                { '#', '#', '.', '.', '.'}
        };


        char[][] newLookP2 = new char[][]{
                { '#', '#', '.', '.', '.'},
                { '#', '#', '.', '.', '.'},
                { '#', '#', '1', '.', '.'},
                { '#', '#', '.', '.', '.'},
                { '#', '#', '.', '.', '.'}
        };

        char[][] newExpectedLookP2 = new char[][]{
                { '#', '#', '.', '.', '.'},
                { '#', '#', '.', '.', '.'},
                { '#', '#', '1', '.', '.'},
                { '#', '#', '.', '.', '.'},
                { '#', '#', '.', '.', '.'}
        };
        assertArrayEquals(newExpectedLookP1, gameLogic.getVisibleOpponents(newLookP1, player1));
        assertArrayEquals(newExpectedLookP2, gameLogic.getVisibleOpponents(newLookP2, player2));
    }

    /**
     * Test to see if the quit command removes a player from the game.
     * This is important as if the player wants to quit the game, they
     * need to be removed from the list of player.
     */
    @Test
    public void testQuit(){
        GameLogic gameLogic =  new GameLogic();
        int playerID = 1;
        gameLogic.addDoDPlayer(playerID);
        DoDPlayer player = gameLogic.getDoDPlayer(playerID);
        gameLogic.quitGame(player);
        assertEquals(0, gameLogic.getNumPlayers());
    }

}
