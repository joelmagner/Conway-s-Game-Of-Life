package Test;
import Common.Grid;
import Common.Square;
import Server.GameManager;
import org.junit.Assert;
import org.junit.Assert.*;
import org.junit.Test;

public class JUnitTest {

    public Grid create10x10Grid(){
        return new Grid(10,10,0);
    }

    public Grid createInitGrid(){
        return new Grid(25,10,50);
    }

    @Test
    public void testNewGrid(){
        int gridSize = 25;
        Grid grid = createInitGrid();
        Assert.assertEquals(grid.getGrid().size(),gridSize*gridSize);
    }

    @Test
    public void testOneRound(){
        Grid grid = createInitGrid();
        Grid refGrid = grid;
        GameManager gm = new GameManager();
        grid = gm.round(grid);
        Assert.assertNotEquals(grid, refGrid);
    }

    @Test
    public void testGameLogic(){
        Grid grid = create10x10Grid();
        GameManager gm = new GameManager();
        for(Square s : grid.getGrid()){
            if(s.x == 4 && (s.y == 4 || s.y == 5 || s.y == 6)){ // vertical glider
                s.setSquareStatus(true);
            }
        }
        grid = gm.round(grid); // do round
        for(Square s : grid.getGrid()) {

            //only living cells
            if(s.x == 4 && s.y == 5){
                Assert.assertTrue(s.getSquareStatus());
                continue;
            }
            if(s.x == 3 && s.y == 5){
                Assert.assertTrue(s.getSquareStatus());
                continue;
            }
            if(s.x == 5 && s.y == 5){
                Assert.assertTrue(s.getSquareStatus());
                continue;
            }
            //the rest should be dead
            Assert.assertFalse(s.getSquareStatus());
        }

        grid = gm.round(grid);
        for(Square s : grid.getGrid()) {

            //only living cells
            if(s.x == 4 && s.y == 4){
                Assert.assertTrue(s.getSquareStatus());
                continue;
            }
            if(s.x == 4 && s.y == 5){
                Assert.assertTrue(s.getSquareStatus());
                continue;
            }
            if(s.x == 4 && s.y == 6){
                Assert.assertTrue(s.getSquareStatus());
                continue;
            }

            Assert.assertFalse(s.getSquareStatus());
        }
    }

}
