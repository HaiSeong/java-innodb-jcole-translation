package dev.haiseong.innodb.record.header.compact;

import lombok.Getter;
import lombok.ToString;

/**
 *      +-------------------------------------------------------------+
 *      |                  Record Format - Header                     |
 *      +-------------------------------------------------------------+
 *      | Variable field lengths (1-2 bytes per var. field)           |
 *      +-------------------------------------------------------------+
 *      | Nullable field bitmap (1 bit per nullable field)            |
 *  N-5 +-------------------------------------------------------------+
 *      | Info Flags (4 bits)                                         |
 *      +-------------------------------------------------------------+
 *      | Number of Records Owned (4 bits)                            |
 *  N-4 +-------------------------------------------------------------+
 *      | Order (13 bits)                                             |
 *      +-------------------------------------------------------------+
 *      | Record Type (3 bits)                                        |
 *  N-2 +-------------------------------------------------------------+
 *      | Next Record Offset (2 bytes)                                |
 *  N   +-------------------------------------------------------------+
 */
@Getter
@ToString
public class CompactRecordHeader {

    private final FixedHeader fixedHeader;
    private final NullableBitmap nullableBitmap;
    private final VariableFieldLengths variableFieldLengths;

    public CompactRecordHeader(FixedHeader fixedHeader,
                               NullableBitmap nullableBitmap,
                               VariableFieldLengths variableFieldLengths
    ) {
        this.fixedHeader = fixedHeader;
        this.nullableBitmap = nullableBitmap;
        this.variableFieldLengths = variableFieldLengths;
    }

    public int getInfoFlags() {
        return fixedHeader.getInfoFlags();
    }

    public int getNOwned() {
        return fixedHeader.getNOwned();
    }

    public int getHeapNumber() {
        return fixedHeader.getHeapNumber();
    }

    public int getRecordType() {
        return fixedHeader.getRecordType();
    }

    public int getNextRecordOffset() {
        return fixedHeader.getNextRecordOffset();
    }

    public boolean[] getNullBitmap() {
        return nullableBitmap.getBitmap().clone();
    }

    public int[] getVariableFieldLengths() {
        VariableFieldLength[] lengths = variableFieldLengths.getLengths();
        int[] result = new int[lengths.length];
        for (int i = 0; i < lengths.length; i++) {
            result[i] = lengths[i].getLength();
        }
        return result;
    }
}
