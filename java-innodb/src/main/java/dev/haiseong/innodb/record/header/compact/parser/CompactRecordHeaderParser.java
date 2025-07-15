package dev.haiseong.innodb.record.header.compact.parser;

import dev.haiseong.innodb.record.header.RecordHeaderParser;
import dev.haiseong.innodb.record.header.compact.CompactRecordHeader;
import dev.haiseong.innodb.record.header.compact.FixedHeader;
import dev.haiseong.innodb.record.header.compact.NullableBitmap;
import dev.haiseong.innodb.record.header.compact.VariableFieldLengths;
import dev.haiseong.innodb.schema.Column;
import dev.haiseong.innodb.schema.Table;
import dev.haiseong.innodb.util.ByteCursor;
import dev.haiseong.innodb.util.ByteCursor.ReadDirection;
import java.util.List;

public class CompactRecordHeaderParser implements RecordHeaderParser {

    private final FixedHeaderParser fixedHeaderParser;
    private final NullableBitmapParser nullableBitmapParser;
    private final VariableFieldLengthsParser variableFieldLengthsParser;

    public CompactRecordHeaderParser(FixedHeaderParser fixedHeaderParser,
                                     NullableBitmapParser nullableBitmapParser,
                                     VariableFieldLengthsParser variableFieldLengthsParser) {
        this.fixedHeaderParser = fixedHeaderParser;
        this.nullableBitmapParser = nullableBitmapParser;
        this.variableFieldLengthsParser = variableFieldLengthsParser;
    }

    public CompactRecordHeader parse(ByteCursor cursor, Table table) {
        cursor.setDirection(ReadDirection.BACKWARD);
        FixedHeader fixedHeader = fixedHeaderParser.parse(cursor);

        List<Column> nullableColumns = table.getNullableColumns();
        NullableBitmap nullableBitmap = nullableBitmapParser.parse(cursor, nullableColumns.size());

        List<Column> variableLengthColumns = table.getVariableLengthColumns();
        int variableFieldLengthsCount
                = calculateVariableFieldLengthsCount(variableLengthColumns, nullableBitmap, nullableColumns);
        VariableFieldLengths variableFieldLengths = variableFieldLengthsParser.parse(cursor, variableFieldLengthsCount);

        return new CompactRecordHeader(fixedHeader, nullableBitmap, variableFieldLengths);
    }

    private int calculateVariableFieldLengthsCount(List<Column> variableLengthColumns,
                                                   NullableBitmap nullableBitmap,
                                                   List<Column> nullableColumns) {
        int variableLengthColumnsSize = variableLengthColumns.size();
        int nullVariableLengthColumnsCount = nullableBitmap.countNullVariableLengthColumns(nullableColumns);

        return variableLengthColumnsSize - nullVariableLengthColumnsCount;
    }

}
