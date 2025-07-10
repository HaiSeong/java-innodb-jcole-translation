package dev.haiseong.innodb.record.header.compact.parser;

import dev.haiseong.innodb.record.header.compact.NullableBitmap;
import dev.haiseong.innodb.util.ByteCursor;

public class NullableBitmapParser {

    private static final int BITS_PER_BYTE = 8;
    private static final int NULL_BIT_VALUE = 1;

    public NullableBitmap parse(ByteCursor cursor, int nullableColumnCount) {
        cursor.setDirection(ByteCursor.ReadDirection.BACKWARD);

        int bitmapSize = (int) ((nullableColumnCount + BITS_PER_BYTE - 1) / BITS_PER_BYTE);
        byte[] bitmapData = cursor.readBytes(bitmapSize);

        boolean[] bitmap = new boolean[(int) nullableColumnCount];
        for (int i = 0; i < nullableColumnCount; i++) {
            int byteIndex = i / BITS_PER_BYTE;
            int bitIndex = i % BITS_PER_BYTE;
            boolean isNullBit = (bitmapData[byteIndex] & (NULL_BIT_VALUE << bitIndex)) != 0;
            bitmap[i] = isNullBit;
        }

        return new NullableBitmap(bitmap);
    }
} 
