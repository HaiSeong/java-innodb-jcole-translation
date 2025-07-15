package dev.haiseong.innodb.page.header.parser;

import dev.haiseong.innodb.page.header.FilTrailer;
import dev.haiseong.innodb.util.ByteCursor;
import dev.haiseong.innodb.util.ByteCursor.ReadDirection;

public class FilTrailerParser {

    public FilTrailer parse(ByteCursor cursor) {
        cursor.setDirection(ReadDirection.BACKWARD);

        int lsn = (int) cursor.readUnsignedInt();
        int oldStyleChecksum = (int) cursor.readUnsignedInt();

        return FilTrailer.builder()
                .oldStyleChecksum(oldStyleChecksum)
                .lsn(lsn)
                .build();
    }
}
