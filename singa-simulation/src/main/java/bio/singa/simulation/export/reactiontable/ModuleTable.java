package bio.singa.simulation.export.reactiontable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class ModuleTable {

    private List<ModuleTableRow> rows;
    static int appendCount = 1;

    public ModuleTable() {
        rows = new ArrayList<>();
    }

    public List<ModuleTableRow> getRows() {
        return rows;
    }

    public void setRows(List<ModuleTableRow> rows) {
        this.rows = rows;
    }

    public void addRow(ModuleTableRow row) {
        rows.add(row);
    }

    public String toTex() {
        return rows.stream()
                .map(ModuleTableRow::toRow)
                .collect(Collectors.joining(ModuleTableRow.fullSpace));
    }

}
