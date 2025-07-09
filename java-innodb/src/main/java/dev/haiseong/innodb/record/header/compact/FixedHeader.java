package dev.haiseong.innodb.record.header.compact;

import lombok.Getter;
import lombok.ToString;

/**
 * InnoDB Compact 레코드의 고정 헤더 정보
 *
 * 고정 헤더는 항상 5바이트로 구성되며 다음 정보를 포함합니다:
 * - Next Record Offset (2 bytes)
 * - Record Type (3 bits) + Heap Number (13 bits) = 2 bytes
 * - N Owned (4 bits) + Info Flags (4 bits) = 1 byte
 */
@Getter
@ToString
public class FixedHeader {

    public final int nextRecordOffset;

    public final int recordType;

    public final int heapNumber;

    public final int nOwned;

    public final int infoFlags;

    public FixedHeader(int nextRecordOffset, int recordType, int heapNumber, int nOwned, int infoFlags) {
        this.nextRecordOffset = nextRecordOffset;
        this.recordType = recordType;
        this.heapNumber = heapNumber;
        this.nOwned = nOwned;
        this.infoFlags = infoFlags;
    }
} 
