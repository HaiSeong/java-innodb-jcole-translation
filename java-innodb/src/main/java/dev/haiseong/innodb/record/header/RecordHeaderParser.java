package dev.haiseong.innodb.record.header;

import dev.haiseong.innodb.schema.Table;
import dev.haiseong.innodb.util.ByteCursor;

public interface RecordHeaderParser {

    RecordHeader parse(ByteCursor cursor, Table table);
}
