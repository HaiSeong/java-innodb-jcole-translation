package dev.haiseong.innodb.schema;

import lombok.Getter;

@Getter
public class Column {

    private final String name;
    private final DataType type;
    private final boolean nullable;
    private final boolean primaryKey;

    public Column(String name, DataType type, boolean nullable, boolean primaryKey) {
        this.name = name;
        this.type = type;
        this.nullable = nullable;
        this.primaryKey = primaryKey;
    }

    public boolean isVariableLength() {
        return type.isVariableLength();
    }

    public int getMaxLengthInBytes() {
        return type.getMaxLengthInBytes();
    }
}
