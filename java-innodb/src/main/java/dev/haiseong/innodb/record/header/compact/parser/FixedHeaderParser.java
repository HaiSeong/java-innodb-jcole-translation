package dev.haiseong.innodb.record.header.compact.parser;

import dev.haiseong.innodb.record.header.compact.FixedHeader;
import dev.haiseong.innodb.util.ByteCursor;
import dev.haiseong.innodb.util.ByteCursor.ReadDirection;

public class FixedHeaderParser {

    private static final int RECORD_TYPE_MASK = 0b0000000000000111;
    private static final int HEAP_NUMBER_MASK = 0b1111111111111000;
    private static final int N_OWNED_MASK = 0b00001111;
    private static final int INFO_FLAGS_MASK = 0b11110000;

    private static final int HEAP_NUMBER_SHIFT = 3;
    private static final int INFO_FLAGS_SHIFT = 4;


    public FixedHeader parse(ByteCursor cursor) {
        cursor.setDirection(ReadDirection.BACKWARD);

        int nextRecordOffset = cursor.readUnsignedShort();
        int typeAndOrder = cursor.readUnsignedShort();
        int ownedAndFlags = cursor.readUnsignedByte();

        return new FixedHeader(
                nextRecordOffset,
                extractRecordType(typeAndOrder),
                extractHeapNumber(typeAndOrder),
                extractNOwned(ownedAndFlags),
                extractInfoFlags(ownedAndFlags)
        );
    }

    private int extractRecordType(int typeAndOrder) {
        return typeAndOrder & RECORD_TYPE_MASK;
    }

    private int extractHeapNumber(int typeAndOrder) {
        return (typeAndOrder & HEAP_NUMBER_MASK) >>> HEAP_NUMBER_SHIFT;
    }

    private int extractNOwned(int ownedAndFlags) {
        return ownedAndFlags & N_OWNED_MASK;
    }

    private int extractInfoFlags(int ownedAndFlags) {
        return (ownedAndFlags & INFO_FLAGS_MASK) >>> INFO_FLAGS_SHIFT;
    }
} 
