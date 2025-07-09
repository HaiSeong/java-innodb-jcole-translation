package dev.haiseong.innodb.record.header.compact.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import dev.haiseong.innodb.record.header.compact.FixedHeader;
import dev.haiseong.innodb.util.ByteCursor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("FixedHeaderParser 테스트")
class FixedHeaderParserTest {


    @Test
    @DisplayName("infimum 시스템 레코드 헤더를 올바르게 파싱한다")
    void parseInfimumRecord() {
        byte[] data = {
                0x01,
                0x00, 0x02,
                0x00, 0x1f
        };
        ByteCursor cursor = new ByteCursor(data, 5);
        FixedHeaderParser parser = new FixedHeaderParser();

        FixedHeader actual = parser.parse(cursor);

        assertAll(
                () -> assertThat(actual.getNextRecordOffset()).isEqualTo(31),
                () -> assertThat(actual.getRecordType()).isEqualTo(2),
                () -> assertThat(actual.getHeapNumber()).isEqualTo(0),
                () -> assertThat(actual.getNOwned()).isEqualTo(1),
                () -> assertThat(actual.getInfoFlags()).isEqualTo(0)
        );
    }

    @Test
    @DisplayName("supremum 시스템 레코드 헤더를 올바르게 파싱한다")
    void parseSupremumRecord() {
        byte[] data = {
                0x04,
                0x00, 0x0b,
                0x00, 0x00
        };
        ByteCursor cursor = new ByteCursor(data, 5);
        FixedHeaderParser parser = new FixedHeaderParser();

        FixedHeader actual = parser.parse(cursor);

        assertAll(
                () -> assertThat(actual.getNextRecordOffset()).isEqualTo(0),
                () -> assertThat(actual.getRecordType()).isEqualTo(3),
                () -> assertThat(actual.getHeapNumber()).isEqualTo(1),
                () -> assertThat(actual.getNOwned()).isEqualTo(4),
                () -> assertThat(actual.getInfoFlags()).isEqualTo(0)
        );
    }

    @Test
    @DisplayName("일반 사용자 레코드 헤더를 올바르게 파싱한다")
    void parseRegularUserRecord() {
        byte[] data = {
                0x00,
                0x00, 0x10,
                0x00, 0x2d
        };
        ByteCursor cursor = new ByteCursor(data, 5);
        FixedHeaderParser parser = new FixedHeaderParser();

        FixedHeader actual = parser.parse(cursor);

        assertAll(
                () -> assertThat(actual.getNextRecordOffset()).isEqualTo(45),
                () -> assertThat(actual.getRecordType()).isEqualTo(0),
                () -> assertThat(actual.getHeapNumber()).isEqualTo(2),
                () -> assertThat(actual.getNOwned()).isEqualTo(0),
                () -> assertThat(actual.getInfoFlags()).isEqualTo(0)
        );
    }
}
