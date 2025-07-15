package dev.haiseong.innodb.record.header.compact.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import dev.haiseong.innodb.record.header.compact.NullableBitmap;
import dev.haiseong.innodb.util.ByteCursor;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class NullableBitmapParserTest {

    private NullableBitmapParser parser;

    static Stream<Arguments> nullableBitmapTestCases() {
        return Stream.of(
                Arguments.of(
                        "nullable 컬럼이 0개인 경우",
                        0,
                        new byte[]{0x01, 0x02}, // 임의의 데이터, 읽히지 않음
                        new boolean[]{}
                ),
                Arguments.of(
                        "nullable 컬럼이 3개일 때 1바이트를 읽는다",
                        3,
                        new byte[]{0x05}, // 0b00000101
                        new boolean[]{true, false, true}
                ),
                Arguments.of(
                        "nullable 컬럼이 9개일 때 2바이트를 읽는다",
                        9,
                        new byte[]{0x55, 0x01}, // 0b01010101, 0b00000001
                        new boolean[]{true, false, true, false, true, false, true, false, true}
                ),
                Arguments.of(
                        "nullable 컬럼 2개가 모두 NULL인 경우",
                        2,
                        new byte[]{0x03}, // 0b00000011
                        new boolean[]{true, true}
                ),
                Arguments.of(
                        "nullable 컬럼 2개가 모두 NOT NULL인 경우",
                        2,
                        new byte[]{0x00}, // 0b00000000
                        new boolean[]{false, false}
                ),
                Arguments.of(
                        "nullable 컬럼이 8개일 때 정확히 1바이트를 읽는다",
                        8,
                        new byte[]{(byte) 0xFF}, // 0b11111111
                        new boolean[]{true, true, true, true, true, true, true, true}
                )
        );
    }

    @BeforeEach
    void setUp() {
        parser = new NullableBitmapParser();
    }

    @DisplayName("nullable 비트맵을 파싱한다")
    @ParameterizedTest(name = "{0}")
    @MethodSource("nullableBitmapTestCases")
    void parseNullableBitmap(String testName, int nullableColumnCount, byte[] inputData, boolean[] expected) {
        int position = inputData.length;
        ByteCursor cursor = new ByteCursor(inputData, position);

        NullableBitmap actual = parser.parse(cursor, nullableColumnCount);

        assertAll(
                () -> assertThat(actual.getBitmap()).hasSize(expected.length),
                () -> assertThat(actual.getBitmap()).isEqualTo(expected)
        );
    }
} 
