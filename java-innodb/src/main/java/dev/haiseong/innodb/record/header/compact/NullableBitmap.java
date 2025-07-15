package dev.haiseong.innodb.record.header.compact;

import dev.haiseong.innodb.schema.Column;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class NullableBitmap {

    public final boolean[] bitmap;

    public NullableBitmap(boolean[] bitmap) {
        this.bitmap = bitmap;
    }

    public int countNullVariableLengthColumns(List<Column> nullableColumns) {
        int nullableColumnIndex = 0;
        int nullVariableLengthCount = 0;

        for (Column column : nullableColumns) {
            if (column.isVariableLength() && bitmap[nullableColumnIndex]) {
                nullVariableLengthCount++;
            }
            nullableColumnIndex++;
        }

        return nullVariableLengthCount;
    }

} 
