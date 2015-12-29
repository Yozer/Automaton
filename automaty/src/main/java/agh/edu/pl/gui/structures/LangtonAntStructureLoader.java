package agh.edu.pl.gui.structures;

import agh.edu.pl.Main;
import agh.edu.pl.automaton.automata.langton.AntState;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Created by Dominik on 2015-12-26.
 */
public class LangtonAntStructureLoader {
//    public AntInfo loadAnt(StructureInfo structureInfo, Coords2D atPoint) throws IOException {
//        InputStreamReader streamReader = new InputStreamReader(Main.class.getResourceAsStream(structureInfo.getPath()), Charset.forName("UTF-8"));
//
//        try (BufferedReader reader = new BufferedReader(streamReader)) {
//            String line = reader.readLine();
//            if (line == null)
//                throw new IOException();
//            AntState antState = null;
//            switch (line) {
//                case "L":
//                    antState = AntState.WEST;
//                    break;
//                case "U":
//                    antState = AntState.NORTH;
//                    break;
//                case "D":
//                    antState = AntState.SOUTH;
//                    break;
//                case "R":
//                    antState = AntState.EAST;
//                    break;
//                default:
//                    throw new IllegalArgumentException("Invalid ant states");
//            }
//
//            return new AntInfo(antState, atPoint);
//        }
//    }

//    public static class AntInfo extends StructureInfo{
//        private final AntState antState;
//        private final Coords2D antCoords;
//
//        public AntInfo(AntState antState, Coords2D antCoords) {
//            this.antState = antState;
//            this.antCoords = antCoords;
//        }
//
//        public AntState getAntState() {
//            return antState;
//        }
//
//        public Coords2D getAntCoords() {
//            return antCoords;
//        }
//
//    }
}
