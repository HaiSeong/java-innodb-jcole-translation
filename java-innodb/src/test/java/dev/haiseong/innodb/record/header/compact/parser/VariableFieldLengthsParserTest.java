package dev.haiseong.innodb.record.header.compact.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import dev.haiseong.innodb.record.header.compact.VariableFieldLength;
import dev.haiseong.innodb.record.header.compact.VariableFieldLengths;
import dev.haiseong.innodb.util.ByteCursor;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("VariableFieldLengthsParser 테스트")
class VariableFieldLengthsParserTest {

    private VariableFieldLengthsParser parser;

    static Stream<Arguments> variableFieldLengthsTestCases() {
        return Stream.of(
                Arguments.of(
                        "길이 0개를 파싱하는 경우",
                        0,
                        new byte[]{0x01, 0x02}, // 임의의 데이터, 읽히지 않음
                        new VariableFieldLength[]{}
                ),
                Arguments.of(
                        "길이 1개를 파싱하는 경우 (1바이트 길이)",
                        1,
                        new byte[]{0x0A}, // 길이 10
                        new VariableFieldLength[]{
                                new VariableFieldLength(10, false)
                        }
                ),
                Arguments.of(
                        "길이 1개를 파싱하는 경우 (2바이트 길이)",
                        1,
                        new byte[]{(byte) 0xFF, (byte) 0x80}, // 길이 255, 내부 저장
                        new VariableFieldLength[]{
                                new VariableFieldLength(255, false)
                        }
                ),
                Arguments.of(
                        "길이 3개를 파싱하는 경우 (역순으로 읽힘)",
                        3,
                        new byte[]{
                                0x05,                              // 첫 번째 길이: 5
                                0x14,                              // 두 번째 길이: 20  
                                (byte) 0x00, (byte) 0xC1          // 세 번째 길이: 256, 외부 저장
                        },
                        new VariableFieldLength[]{
                                new VariableFieldLength(256, true),  // 마지막부터 읽음
                                new VariableFieldLength(20, false),
                                new VariableFieldLength(5, false)
                        }
                ),
                Arguments.of(
                        "길이 2개를 파싱하는 경우 (혼합 바이트 길이)",
                        2,
                        new byte[]{
                                0x08,  // 첫 번째 길이: 8 (1바이트)
                                0x1A   // 두 번째 길이: 26 (1바이트)
                        },
                        new VariableFieldLength[]{
                                new VariableFieldLength(26, false),
                                new VariableFieldLength(8, false)
                        }
                )
        );
    }

    @BeforeEach
    void setUp() {
        VariableFieldLengthParser fieldLengthParser = new VariableFieldLengthParser();
        parser = new VariableFieldLengthsParser(fieldLengthParser);
    }

    @DisplayName("variable field lengths를 올바르게 파싱한다")
    @ParameterizedTest(name = "{0}")
    @MethodSource("variableFieldLengthsTestCases")
    void parseVariableFieldLengths(String testName, int lengthsCount, byte[] data,
                                   VariableFieldLength[] expectedLengths) {
        ByteCursor cursor = new ByteCursor(data, data.length);

        VariableFieldLengths actual = parser.parse(cursor, lengthsCount);

        assertAll(
                () -> assertThat(actual.getLengths()).hasSize(expectedLengths.length),
                () -> {
                    for (int i = 0; i < expectedLengths.length; i++) {
                        int index = i;
                        assertAll(
                                () -> assertThat(actual.getLengths()[index].getLength())
                                        .isEqualTo(expectedLengths[index].getLength()),
                                () -> assertThat(actual.getLengths()[index].isStoredExternally())
                                        .isEqualTo(expectedLengths[index].isStoredExternally())
                        );
                    }
                }
        );
    }
} 
