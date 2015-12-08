package agh.edu.pl.automaton.cells.neighborhoods;

import agh.edu.pl.automaton.cells.coordinates.Coords2D;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Dominik on 2015-12-08.
 */
public class WrapCoordinatesHelper
{
    public static Set<Coords2D> fixCoords(Set<Coords2D> coords, boolean wrap, int width, int height)
    {
        for(Coords2D coordinates : new ArrayList<>(coords))
        {
            if (coordinates.getX() < 0 || coordinates.getY() < 0 || coordinates.getX() > width || coordinates.getY() > height)
            {
                coords.remove(coordinates);
                if (wrap)
                {
                    int newX = coordinates.getX();
                    int newY = coordinates.getY();
                    if (coordinates.getX() < 0 || coordinates.getX() > width )
                        newX = Math.floorMod(coordinates.getX(), width) + 1;
                    if(coordinates.getY() < 0 || coordinates.getY() > height)
                        newY = Math.floorMod(coordinates.getY(), height) + 1;

                    coords.add(new Coords2D(newX, newY));
                }
            }
        }

        return coords;
    }
}
