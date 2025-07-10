package dev.haiseong.innodb.schema;

import lombok.Getter;

@Getter
public enum DataType {
    INT(4, false),
    BIGINT(8, false),
    VARCHAR(255, true),
    TEXT(65535, true),
    CHAR(50, true);

    private final int maxLengthInBytes;
    private final boolean variableLength;

    DataType(int maxLengthInBytes, boolean variableLength) {
        this.maxLengthInBytes = maxLengthInBytes;
        this.variableLength = variableLength;
    }

}
