package dev.haiseong.innodb.record.header.compact.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import dev.haiseong.innodb.record.header.compact.CompactRecordHeader;
import dev.haiseong.innodb.schema.Column;
import dev.haiseong.innodb.schema.DataType;
import dev.haiseong.innodb.schema.Table;
import dev.haiseong.innodb.util.ByteCursor;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("CompactRecordHeaderParser 테스트")
class CompactRecordHeaderParserTest {

    private CompactRecordHeaderParser parser;

    @BeforeEach
    void setUp() {
        FixedHeaderParser fixedHeaderParser = new FixedHeaderParser();
        NullableBitmapParser nullableBitmapParser = new NullableBitmapParser();
        VariableFieldLengthsParser variableFieldLengthsParser
                = new VariableFieldLengthsParser(new VariableFieldLengthParser());

        parser = new CompactRecordHeaderParser(
                fixedHeaderParser,
                nullableBitmapParser,
                variableFieldLengthsParser
        );
    }

    @Nested
    @DisplayName("parse 테스트")
    class ParseTest {

        @Test
        @DisplayName("모든 필드에 값이 있는 레코드 헤더를 올바르게 파싱한다")
        void parseWithAllFields() {
            // id=1, age=25, name='A', description='Student', email='a@test.com'
            byte[] headerData = {0x0a, 0x07, 0x01, 0x00, 0x00, 0x00, 0x10, 0x00, 0x2f};
            Table expected = createTestTable();
            ByteCursor cursor = new ByteCursor(headerData, headerData.length);

            CompactRecordHeader actual = parser.parse(cursor, expected);

            assertAll(
                    () -> assertThat(actual.getRecordType()).isEqualTo(0),
                    () -> assertThat(actual.getHeapNumber()).isEqualTo(2),
                    () -> assertThat(actual.getNextRecordOffset()).isEqualTo(47),
                    () -> assertThat(actual.getNOwned()).isEqualTo(0),
                    () -> assertThat(actual.getInfoFlags()).isEqualTo(0),
                    () -> assertThat(actual.getNullBitmap()).isEqualTo(new boolean[]{false, false, false}),
                    () -> assertThat(actual.getVariableFieldLengths()).isEqualTo(new int[]{1, 7, 10})
            );
        }

        @Test
        @DisplayName("NULL 값이 포함된 레코드 헤더를 올바르게 파싱한다")
        void parseWithNullFields() {
            // id=2, age=NULL, name='Alice', description='Teacher at school', email=NULL
            byte[] headerData = {0x11, 0x05, 0x05, 0x00, 0x00, 0x18, 0x00, 0x2f};
            Table expected = createTestTable();
            ByteCursor cursor = new ByteCursor(headerData, headerData.length);

            CompactRecordHeader actual = parser.parse(cursor, expected);

            assertAll(
                    () -> assertThat(actual.getRecordType()).isEqualTo(0),
                    () -> assertThat(actual.getHeapNumber()).isEqualTo(3),
                    () -> assertThat(actual.getNextRecordOffset()).isEqualTo(47),
                    () -> assertThat(actual.getNOwned()).isEqualTo(0),
                    () -> assertThat(actual.getInfoFlags()).isEqualTo(0),
                    () -> assertThat(actual.getNullBitmap()).isEqualTo(new boolean[]{true, false, true}),
                    () -> assertThat(actual.getVariableFieldLengths()).isEqualTo(new int[]{5, 17})
            );
        }

        private Table createTestTable() {
            List<Column> columns = List.of(
                    new Column("id", DataType.INT, false, true),
                    new Column("age", DataType.INT, true, false),
                    new Column("name", DataType.VARCHAR, false, false),
                    new Column("description", DataType.VARCHAR, true, false),
                    new Column("email", DataType.VARCHAR, true, false)
            );
            return new Table("test_table", "COMPACT", columns);
        }
    }
} 
