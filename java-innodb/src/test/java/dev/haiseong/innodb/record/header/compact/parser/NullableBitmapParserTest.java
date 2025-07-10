package dev.haiseong.innodb.record.header.compact.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import dev.haiseong.innodb.record.header.compact.NullableBitmap;
import dev.haiseong.innodb.schema.Column;
import dev.haiseong.innodb.schema.DataType;
import dev.haiseong.innodb.schema.Table;
import dev.haiseong.innodb.util.ByteCursor;
import java.util.List;
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
                        "nullable 컬럼이 없는 경우",
                        List.of(
                                new Column("id", DataType.INT, false, true),
                                new Column("name", DataType.VARCHAR, false, false)
                        ),
                        new byte[]{0x01, 0x02},
                        new boolean[]{}
                ),
                Arguments.of(
                        "3개의 nullable 컬럼이 있을 때 1바이트를 읽는다",
                        List.of(
                                new Column("id", DataType.INT, false, true),
                                new Column("name", DataType.VARCHAR, true, false),
                                new Column("age", DataType.INT, true, false),
                                new Column("email", DataType.VARCHAR, true, false)
                        ),
                        new byte[]{0x05},
                        new boolean[]{true, false, true}
                ),
                Arguments.of(
                        "9개의 nullable 컬럼이 있을 때 2바이트를 읽는다",
                        createColumnsForNineNullableTest(),
                        new byte[]{0x55, 0x01}, // 0b01010101, 0b00000001
                        new boolean[]{true, false, true, false, true, false, true, false, true}
                ),
                Arguments.of(
                        "모든 nullable 컬럼이 NULL인 경우",
                        List.of(
                                new Column("id", DataType.INT, false, true),
                                new Column("name", DataType.VARCHAR, true, false),
                                new Column("age", DataType.INT, true, false)
                        ),
                        new byte[]{0x03},
                        new boolean[]{true, true}
                ),
                Arguments.of(
                        "모든 nullable 컬럼이 NOT NULL인 경우",
                        List.of(
                                new Column("id", DataType.INT, false, true),
                                new Column("name", DataType.VARCHAR, true, false),
                                new Column("age", DataType.INT, true, false)
                        ),
                        new byte[]{0x00},
                        new boolean[]{false, false}
                )
        );
    }

    private static List<Column> createColumnsForNineNullableTest() {
        Column[] columns = new Column[10];
        columns[0] = new Column("id", DataType.INT, false, true); // NOT NULL
        for (int i = 1; i < 10; i++) {
            columns[i] = new Column("col" + i, DataType.VARCHAR, true, false); // NULLABLE
        }
        return List.of(columns);
    }

    @BeforeEach
    void setUp() {
        parser = new NullableBitmapParser();
    }

    @DisplayName("nullable 비트맵을 파싱한다")
    @ParameterizedTest(name = "{0}")
    @MethodSource("nullableBitmapTestCases")
    void parseNullableBitmap(String testName, List<Column> columns, byte[] inputData, boolean[] expected) {
        Table table = new Table("test_table", "compact", columns);
        int position = inputData.length;
        ByteCursor cursor = new ByteCursor(inputData, position);

        NullableBitmap actual = parser.parse(cursor, table);

        assertAll(
                () -> assertThat(actual.getBitmap()).hasSize(expected.length),
                () -> assertThat(actual.getBitmap()).isEqualTo(expected)
        );
    }
} 
