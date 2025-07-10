package dev.haiseong.innodb.record.header.compact.parser;

import dev.haiseong.innodb.record.header.compact.VariableFieldLength;
import dev.haiseong.innodb.util.ByteCursor;

public class VariableFieldLengthParser {

    private static final int FIRST_BIT_MASK = 0b10000000;
    private static final int SECOND_BIT_MASK = 0b01000000;
    private static final int LOWER_6_BITS_MASK = 0b00111111;

    public VariableFieldLength parse(ByteCursor cursor) {
        cursor.setDirection(ByteCursor.ReadDirection.BACKWARD);

        int firstByte = cursor.readUnsignedByte();

        if (isSingleByteLength(firstByte)) {
            return new VariableFieldLength(firstByte, false);
        }

        boolean storedExternally = (firstByte & SECOND_BIT_MASK) != 0;

        int secondByte = cursor.readUnsignedByte();

        int upperBits = firstByte & LOWER_6_BITS_MASK;
        int length = (upperBits << 8) + secondByte;

        return new VariableFieldLength(length, storedExternally);
    }

    private boolean isSingleByteLength(int firstByte) {
        return (firstByte & FIRST_BIT_MASK) == 0;
    }
} 
