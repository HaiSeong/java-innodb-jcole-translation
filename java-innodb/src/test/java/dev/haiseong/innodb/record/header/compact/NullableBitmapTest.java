package dev.haiseong.innodb.record.header.compact;

import static org.assertj.core.api.Assertions.assertThat;

import dev.haiseong.innodb.schema.Column;
import dev.haiseong.innodb.schema.DataType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class NullableBitmapTest {

    static Stream<Arguments> countNullVariableLengthColumnsTestCases() {
        return Stream.of(
                Arguments.of(
                        "모든 컬럼이 null이 아닌 경우",
                        new boolean[]{false, false, false},
                        Arrays.asList(
                                new Column("id", DataType.INT, true, true),
                                new Column("name", DataType.VARCHAR, true, false),
                                new Column("description", DataType.TEXT, true, false)
                        ),
                        0
                ),
                Arguments.of(
                        "모든 컬럼이 null인 경우",
                        new boolean[]{true, true, true},
                        Arrays.asList(
                                new Column("id", DataType.INT, true, true),
                                new Column("name", DataType.VARCHAR, true, false),
                                new Column("description", DataType.TEXT, true, false)
                        ),
                        2 // VARCHAR, TEXT만 가변 길이
                ),
                Arguments.of(
                        "가변 길이 컬럼만 null인 경우",
                        new boolean[]{false, true, true},
                        Arrays.asList(
                                new Column("id", DataType.INT, true, true),
                                new Column("name", DataType.VARCHAR, true, false),
                                new Column("description", DataType.TEXT, true, false)
                        ),
                        2 // VARCHAR, TEXT만 null이고 가변 길이
                ),
                Arguments.of(
                        "고정 길이 컬럼만 null인 경우",
                        new boolean[]{true, false, false},
                        Arrays.asList(
                                new Column("id", DataType.INT, true, true),
                                new Column("name", DataType.VARCHAR, true, false),
                                new Column("description", DataType.TEXT, true, false)
                        ),
                        0 // INT는 고정 길이이므로 카운트되지 않음
                ),
                Arguments.of(
                        "빈 리스트인 경우",
                        new boolean[]{},
                        List.of(),
                        0
                ),
                Arguments.of(
                        "CHAR 타입이 null인 경우",
                        new boolean[]{true, true, true},
                        Arrays.asList(
                                new Column("id", DataType.BIGINT, true, true),
                                new Column("name", DataType.VARCHAR, true, false),
                                new Column("code", DataType.CHAR, true, false)
                        ),
                        2 // VARCHAR, CHAR만 가변 길이
                ),
                Arguments.of(
                        "일부 가변 길이 컬럼만 null인 경우",
                        new boolean[]{false, true, false, true},
                        Arrays.asList(
                                new Column("id", DataType.INT, true, true),
                                new Column("name", DataType.VARCHAR, true, false),
                                new Column("age", DataType.BIGINT, true, false),
                                new Column("description", DataType.TEXT, true, false)
                        ),
                        2 // VARCHAR, TEXT 중 null인 것만 카운트
                )
        );
    }

    @DisplayName("null인 가변 길이 컬럼의 개수를 올바르게 계산한다")
    @ParameterizedTest(name = "{0}")
    @MethodSource("countNullVariableLengthColumnsTestCases")
    void countNullVariableLengthColumns(String testName, boolean[] bitmap, List<Column> nullableColumns, int expected) {
        NullableBitmap nullableBitmap = new NullableBitmap(bitmap);

        int actual = nullableBitmap.countNullVariableLengthColumns(nullableColumns);

        assertThat(actual).isEqualTo(expected);
    }
} 
