package agh.edu.pl.automaton.cells.neighborhoods;

import agh.edu.pl.automaton.cells.coordinates.Coords1D;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Dominik on 2015-12-08.
 */
public class WrapCoordinatesHelper
{
    private WrapCoordinatesHelper() {}
    public static Coords2D fixCoord(Coords2D coord, boolean wrap, int width, int height)
    {
        if (coord.getX() < 0 || coord.getY() < 0 || coord.getX() >= width || coord.getY() >= height)
        {
            if (wrap)
            {
                int newX = coord.getX();
                int newY = coord.getY();
                if (coord.getX() < 0 || coord.getX() >= width )
                    newX = Math.floorMod(coord.getX(), width);
                if(coord.getY() < 0 || coord.getY() >= height)
                    newY = Math.floorMod(coord.getY(), height) ;

                return new Coords2D(newX, newY);
            }
            return null;
        }
        return coord;
    }
    public static Coords1D fixCoord(Coords1D coord, boolean wrap, int width)
    {
        if (coord.getX() < 0 || coord.getX() >= width)
        {
            if (wrap)
            {
                int newX = coord.getX();
                if (coord.getX() < 0 || coord.getX() >= width )
                    newX = Math.floorMod(coord.getX(), width);

                return new Coords1D(newX);
            }
            return null;
        }
        return coord;
    }
}
