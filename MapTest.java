import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Contains all of the unit tests for the MapJNI class
 *
 * @author : tjv26
 */

public class MapTest {

    /**
     * Test which loads the example map and tests to see if its name
     * was correctly extracted and stored.
     * This test confirms that the program can correctly store map names.
     */
    @Test
    public void testGetMapNamePositive(){
        MapJNI map =  new MapJNI();
        map.loadMap("maps/example_map.txt");
        assertEquals("Very Small Labyrinth of Dooom", map.getMapName());
    }

    /**
     * Test which loads a map with a null name, i.e. 'name '
     * and tests if it was correctly se to null by the program.
     * This test confirms that the program can correctly handle null names.
     */
    @Test
    public void testGetMapNameNegativeTest(){
        MapJNI map =  new MapJNI();
        map.loadMap("maps/example_map_no_name.txt");
        assertEquals(null, map.getMapName());
    }

    /**
     * Test which loads the example map and tests to see if the
     * program correctly sets its width (19)
     * This test confirms that the program can correctly handle
     * maps of uniform width.
     */
    @Test
    public void testGetMapWidthPositive(){
        MapJNI map = new MapJNI();
        map.loadMap("maps/example_map.txt");
        assertEquals(19, map.getMapWidth());
    }

    /**
     * Test which loads a map where the first line is longer than
     * the others, and tests to see if the program correctly sets
     * the width (21).
     * This test confirms that the program can correctly handle
     * maps where the first line is longer that the others.
     *
     * Note, maps in this format will interfere with gameplay,
     * including the look command, however, as the functionality
     * of the given map.java file is being replicated, this was not fixed.
     */
    @Test
    public void testGetMapWidthNegative(){
        MapJNI map = new MapJNI();
        map.loadMap("maps/example_map_width_test.txt");
        assertEquals(21, map.getMapWidth());
    }

    /**
     * Test which loads the example map and tests to see if the
     * program correctly sets its height (9).
     * This test confirms that the program can correctly handle
     * maps of uniform height.
     */
    @Test
    public void testGetMapHeightPositive(){
        MapJNI map = new MapJNI();
        map.loadMap("maps/example_map.txt");
        assertEquals(9, map.getMapHeight());
    }


    /**
     * Test which loads a map where the first column is longer than
     * the others, and tests to see if the program correctly sets
     * the height (10)
     * This test confirms that the program can correctly handle
     * maps where the first column is longer that the others.
     *
     * Note, maps in this format will interfere with gameplay,
     * including the look command, however, as the functionality
     * of the given map.java file is being replicated, this was not fixed.
     */
    @Test
    public void testGetMapHeightNegative(){
        MapJNI map = new MapJNI();
        map.loadMap("maps/example_map_height_test.txt");
        assertEquals(10, map.getMapHeight());
    }

    /**
     * Test which loads the example map and tests to see if the
     * program correctly sets its win condition (get 1 gold).
     * This test confirms that the program can correctly handle
     * maps with a valid win condition.
     */
    @Test
    public void testGetGoldToWinPositive(){
        MapJNI map = new MapJNI();
        map.loadMap("maps/example_map.txt");
        assertEquals(1, map.getGoldToWin());
    }

    /**
     * Test which loads a map without a win condition, i.e. (win )
     * and tests to see if the program correctly sets it to the
     * error (-1).
     * This test confirms that the program can correctly handle
     * maps without valid win conditions.
     */
    @Test
    public void testGetGoldToWinNegative(){
        MapJNI map = new MapJNI();
        map.loadMap("maps/example_map_no_win.txt");
        //No win condition
        assertEquals(-1, map.getGoldToWin());
    }
	
	/**
     * Test which loads a map without a non integer win condition
	 * i.e. (win 1.1) and tests to see if the program correctly 
	 * floors the decimal.
     * This test confirms that the program can correctly handle
     * maps without valid win conditions.
     */
    @Test
    public void testGetGoldToWinDecimal(){
        MapJNI map = new MapJNI();
        map.loadMap("maps/example_map_float_win.txt");
        assertEquals(1, map.getGoldToWin());
    }

    /**
     * Test which loads the example map and tests to see if the
     * look method (C function) returns the correct look window.
     * This test confirms that the program returns the map tiles
     * in the correct format (5x5 window with the tiles from map file).
     */
    @Test
    public void testLookPositive(){
        MapJNI map = new MapJNI();
        map.loadMap("maps/example_map.txt");
        char[][] expectedLook = new char[][]{
                { '#', '#', '#', '#', '#'},
                { '#', '#', '#', '#', '#'},
                { '#', '#', '.', '.', '.'},
                { '#', '#', '.', '.', '.'},
                { '#', '#', '.', '.', '.'}
        };
        assertArrayEquals(expectedLook, map.look(1,1));
    }

    /**
     * Test which loads the example map and tests to see if the
     * look method (C function) returns the correct look window.
     * The look command is passed coordinates which are out of
     * the bounds of the map.
     *
     * This test confirms that the program returns the map tiles
     * in the correct format (5x5 window with the tiles from map file)
     * even when the user is 'out of the map'. This is important for when
     * the user enters the look command in the corner of a map.
     */
    @Test
    public void testLookNegative(){
        MapJNI map = new MapJNI();
        map.loadMap("maps/example_map.txt");
        char[][] expectedLook = new char[][]{
                { '#', '#', '#', '#', '#'},
                { '#', '#', '#', '#', '#'},
                { '#', '#', '#', '#', '#'},
                { '#', '#', '#', '#', '#'},
                { '#', '#', '#', '#', '#'}
        };
        assertArrayEquals(expectedLook, map.look(20,20));
    }

    /**
     * Test which loads the example map and tests to see if individual
     * tiles can successfully be retrieved.
     * This test confirms that the program returns the correct individual
     * map tiles given their coordinates. This functionality is important
     * for many GameLogic methods.
     */
    @Test
    public void testGetTilePositive(){
        MapJNI map = new MapJNI();
        map.loadMap("maps/example_map.txt");
        assertEquals('#', map.getTile(0,0));
        assertEquals('.', map.getTile(5,5));
        assertEquals('E', map.getTile(3,4));
    }

    /**
     * Test which loads the example map and tests to see if an individual
     * tiles outside of the bounds of the map can be retrieved successfully.
     *
     * This test confirms that the program returns the correct individual
     * map tiles given their coordinates. This functionality is important
     * for some GameLogic methods such as getSpawnLocation() to check
     * the user does not spawn outside the boundaries of the map.
     */
    @Test
    public void testGetTileNegative(){
        MapJNI map = new MapJNI();
        map.loadMap("maps/example_map.txt");
        assertEquals('#', map.getTile(1000,1000));
    }

    /**
     * Test which loads the example map and tests to see if individual
     * tiles on the map can successfully be replaced.
     * This test confirms that the program can successfully replace tiles
     * on the map given their coordinates. This functionality is important
     * for many GameLogic methods.
     */
    @Test
    public void testReplaceTilePositive(){
        MapJNI map = new MapJNI();
        map.loadMap("maps/example_map.txt");
        map.replaceTile(5,5, 'G');
        assertEquals('G', map.getTile(5,5));

        map.replaceTile(3,3, '.');
        assertEquals('.', map.getTile(3,3));
    }

    /**
     * Test which loads the example map and tests to see if individual
     * tiles, which are outside the boundaries of the map, can
     * successfully be replaced.
     * This test confirms that the program can successfully replace tiles
     * on the map given their coordinates, even when out of bounds.
     * This is important for the robustness of the game.
     */
    @Test
    public void testReplaceTileNegative(){
        MapJNI map = new MapJNI();
        map.loadMap("maps/example_map.txt");
        map.replaceTile(100,100, 'G');
        assertEquals('#', map.getTile(100,100));
    }

    /**
     * Test which manually sets the win condition for the game, indepedent of
     * loading a map.
     * This test confirms that the game credentials is able to be set indepdent
     * of loading a map, which may be useful if the win condition were to be changed
     * during the game.
     */
    @Test
    public void testSetWinPositive(){
        MapJNI map = new MapJNI();
        String winLine = "win 4";
        map.setWin(winLine);
        assertEquals(4, map.getGoldToWin());
    }

    /**
     * Test which manually sets an invalid win condition for the game.
     * This test is important for the same reasons as above, as well as
     * making sure that if a map were to have an invalid win condition, it
     * would be caught.
     */
    @Test
    public void testSetWinNegative(){
        MapJNI map = new MapJNI();
        map.setWin("win ");
        assertEquals(-1, map.getGoldToWin());
    }

    /**
     * Test which manually sets a null win condition for the game.
     * This test is important for the same reasons as above, as well as
     * making sure that if a map were to have an invalid win condition,
     * i.e. a blank win condition, it would be caught.
     */
    @Test
    public void testSetWinNull(){
        MapJNI map = new MapJNI();
        map.setWin("");
        assertEquals(-1, map.getGoldToWin());
    }

    /**
     * Test which manually sets the name of a map without loading it.
     * This test is important to ensure that the setName method (C function)
     * can run independently of loadMap. Moreover, so that the name could
     * be changed in the duration of the game.
     */
    @Test
    public void testSetNamePositive(){
        MapJNI map = new MapJNI();
        map.setName("name Labyrinth of Dooom");
        assertEquals("Labyrinth of Dooom", map.getMapName());
    }

    /**
     * Test which manually sets an invalid name of the map.
     * This test is important for the same reasons as above,
     * but also to ensure that invalid names are caught, be it
     * in loading a map, or manually setting them.
     */
    @Test
    public void testSetNameNegative(){
        MapJNI map = new MapJNI();
        map.setName("name ");
        assertEquals(null, map.getMapName());
    }

    /**
     * Test which manually sets a null name for the map.
     * This test is important for the same reasons as above,
     * but also to ensure that invalid names are caught.
     * An example might be having a blank name in a map file.
     */
    @Test
    public void testSetNameNull(){
        MapJNI map = new MapJNI();
        map.setName("");
        assertEquals(null, map.getMapName());
    }
}
