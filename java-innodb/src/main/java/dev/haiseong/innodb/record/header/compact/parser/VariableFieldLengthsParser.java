package dev.haiseong.innodb.record.header.compact.parser;

import dev.haiseong.innodb.record.header.compact.VariableFieldLength;
import dev.haiseong.innodb.record.header.compact.VariableFieldLengths;
import dev.haiseong.innodb.util.ByteCursor;

public class VariableFieldLengthsParser {

    private final VariableFieldLengthParser variableFieldLengthParser;

    public VariableFieldLengthsParser(VariableFieldLengthParser variableFieldLengthParser) {
        this.variableFieldLengthParser = variableFieldLengthParser;
    }

    public VariableFieldLengths parse(ByteCursor cursor, int variableFieldLengthsCount) {
        cursor.setDirection(ByteCursor.ReadDirection.BACKWARD);

        VariableFieldLength[] lengths = new VariableFieldLength[variableFieldLengthsCount];

        for (int i = 0; i < variableFieldLengthsCount; i++) {
            lengths[i] = variableFieldLengthParser.parse(cursor);
        }

        return new VariableFieldLengths(lengths);
    }

}
