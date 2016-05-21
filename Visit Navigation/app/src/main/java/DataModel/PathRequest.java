package DataModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nisan on 21/05/2016.
 */
public class PathRequest {
    List<Cell> cells;
    int BuildingId;

    public PathRequest() {
        cells = new ArrayList<Cell>();
        BuildingId = 1;
    }

    public PathRequest(List<Cell> listCells, int building) {
        cells = new ArrayList<Cell>();

        for (Cell c : listCells) {
            cells.add(c);
        }

        BuildingId = building;
    }

    public PathRequest(Cell src, Cell dest, int building) {
        cells = new ArrayList<Cell>();

        cells.add(src);
        cells.add(dest);

        BuildingId = building;
    }
}
