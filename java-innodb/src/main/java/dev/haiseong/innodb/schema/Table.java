package dev.haiseong.innodb.schema;

import java.util.List;
import lombok.Getter;

@Getter
public class Table {

    private final String name;
    private final String rowFormat;
    private final List<Column> columns;

    public Table(String name, String rowFormat, List<Column> columns) {
        this.name = name;
        this.rowFormat = rowFormat;
        this.columns = columns;
    }

    public List<Column> getNullableColumns() {
        return columns.stream()
                .filter(Column::isNullable)
                .toList();
    }

    public List<Column> getVariableLengthColumns() {
        return columns.stream()
                .filter(Column::isVariableLength)
                .toList();
    }
}
