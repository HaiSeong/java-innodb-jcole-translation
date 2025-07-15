package dev.haiseong.innodb.page.header.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import dev.haiseong.innodb.page.header.FilHeader;
import dev.haiseong.innodb.util.ByteCursor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FilHeaderParserTest {


    @Test
    @DisplayName("file header를 올바르게 파싱한다")
    void parse() {
        byte[] data = {
                (byte) 0xb9, (byte) 0x8c, (byte) 0xea, 0x46,
                0x00, 0x00, 0x00, 0x04,
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                0x00, 0x00, 0x00, 0x00, 0x02, 0x2f, (byte) 0x9c, 0x25,
                0x45, (byte) 0xbf,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x42,
        };
        ByteCursor cursor = new ByteCursor(data, 0);
        FilHeaderParser parser = new FilHeaderParser();

        FilHeader filHeader = parser.parse(cursor);

        assertAll(
                () -> assertThat(filHeader.getChecksum()).isEqualTo(0xb98cea46),
                () -> assertThat(filHeader.getPageNumber()).isEqualTo(0x00000004),
                () -> assertThat(filHeader.getPreviousPageNumber()).isEqualTo(0xffffffff),
                () -> assertThat(filHeader.getNextPageNumber()).isEqualTo(0xffffffff),
                () -> assertThat(filHeader.getLsn()).isEqualTo(0x00000000022f9c25),
                () -> assertThat(filHeader.getPageType()).isEqualTo(0x45bf),
                () -> assertThat(filHeader.getFlushLsn()).isEqualTo(0x00),
                () -> assertThat(filHeader.getSpaceId()).isEqualTo(0x00000042)
        );
    }
}
