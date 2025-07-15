package dev.haiseong.innodb.page.header.parser;

import dev.haiseong.innodb.page.header.FilHeader;
import dev.haiseong.innodb.util.ByteCursor;
import dev.haiseong.innodb.util.ByteCursor.ReadDirection;

public class FilHeaderParser {

    public FilHeader parse(ByteCursor cursor) {
        cursor.setDirection(ReadDirection.FORWARD);

        int checksum = (int) cursor.readUnsignedInt();
        int pageNumber = (int) cursor.readUnsignedInt();
        int previousPageNumber = (int) cursor.readUnsignedInt();
        int nextPageNumber = (int) cursor.readUnsignedInt();
        long lsn = cursor.readUnsignedInt(8);
        int pageType = cursor.readUnsignedShort();
        long flushLsn = cursor.readUnsignedInt(8);
        int spaceId = (int) cursor.readUnsignedInt();

        return FilHeader.builder()
                .checksum(checksum)
                .pageNumber(pageNumber)
                .previousPageNumber(previousPageNumber)
                .nextPageNumber(nextPageNumber)
                .lsn(lsn)
                .pageType(pageType)
                .flushLsn(flushLsn)
                .spaceId(spaceId)
                .build();
    }
}
