package dev.haiseong.innodb.record.header.compact.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import dev.haiseong.innodb.record.header.compact.VariableFieldLength;
import dev.haiseong.innodb.util.ByteCursor;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("VariableFieldLengthParser 테스트")
class VariableFieldLengthParserTest {

    private VariableFieldLengthParser parser;

    static Stream<Arguments> singleByteTestCases() {
        return Stream.of(
                Arguments.of(
                        "길이가 0인 경우",
                        new byte[]{0x00},
                        0,
                        false
                ),
                Arguments.of(
                        "길이가 1인 경우",
                        new byte[]{0x01},
                        1,
                        false
                ),
                Arguments.of(
                        "길이가 127인 경우 (최대 1바이트 길이)",
                        new byte[]{0x7F},
                        127,
                        false
                )
        );
    }

    static Stream<Arguments> multiByteTestCases() {
        return Stream.of(
                Arguments.of(
                        "2바이트 길이, 내부 저장, 길이 128",
                        new byte[]{(byte) 0x80, (byte) 0x80},
                        128,
                        false
                ),
                Arguments.of(
                        "2바이트 길이, 외부 저장, 길이 128",
                        new byte[]{(byte) 0x80, (byte) 0xC0},
                        128,
                        true
                ),
                Arguments.of(
                        "2바이트 길이, 내부 저장, 길이 255",
                        new byte[]{(byte) 0xFF, (byte) 0x80},
                        255,
                        false
                ),
                Arguments.of(
                        "2바이트 길이, 외부 저장, 길이 788 (0x03 << 8 + 0x14)",
                        new byte[]{(byte) 0x14, (byte) 0xC3},
                        788,
                        true
                ),
                Arguments.of(
                        "2바이트 길이, 내부 저장, 길이 512 (0x02 << 8 + 0x00)",
                        new byte[]{(byte) 0x00, (byte) 0x82},
                        512,
                        false
                )
        );
    }

    @BeforeEach
    void setUp() {
        parser = new VariableFieldLengthParser();
    }

    @DisplayName("1바이트 길이를 올바르게 파싱한다")
    @ParameterizedTest(name = "{0}")
    @MethodSource("singleByteTestCases")
    void parseSingleByteLength(String testName, byte[] data, int expectedLength, boolean expectedStoredExternally) {
        ByteCursor cursor = new ByteCursor(data, 1);

        VariableFieldLength actual = parser.parse(cursor);

        assertAll(
                () -> assertThat(actual.getLength()).isEqualTo(expectedLength),
                () -> assertThat(actual.isStoredExternally()).isEqualTo(expectedStoredExternally)
        );
    }

    @DisplayName("2바이트 길이를 올바르게 파싱한다")
    @ParameterizedTest(name = "{0}")
    @MethodSource("multiByteTestCases")
    void parseMultiByteLength(String testName, byte[] data, int expectedLength, boolean expectedStoredExternally) {
        ByteCursor cursor = new ByteCursor(data, 2);

        VariableFieldLength actual = parser.parse(cursor);

        assertAll(
                () -> assertThat(actual.getLength()).isEqualTo(expectedLength),
                () -> assertThat(actual.isStoredExternally()).isEqualTo(expectedStoredExternally)
        );
    }
} 
