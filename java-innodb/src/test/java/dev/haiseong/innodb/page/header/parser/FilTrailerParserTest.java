package dev.haiseong.innodb.page.header.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import dev.haiseong.innodb.page.header.FilTrailer;
import dev.haiseong.innodb.util.ByteCursor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FilTrailerParserTest {

    @Test
    @DisplayName("file trailer를 올바르게 파싱한다")
    void parse() {
        byte[] data = {
                0x4f, (byte) 0xec, 0x4a, 0x70,
                0x02, 0x3e, 0x5c, (byte) 0xe0,
        };
        ByteCursor cursor = new ByteCursor(data, 8);
        FilTrailerParser parser = new FilTrailerParser();

        FilTrailer FilTrailer = parser.parse(cursor);

        assertAll(
                () -> assertThat(FilTrailer.getOldStyleChecksum()).isEqualTo(0x4fec4a70),
                () -> assertThat(FilTrailer.getLsn()).isEqualTo(0x023e5ce0)
        );
    }
}
