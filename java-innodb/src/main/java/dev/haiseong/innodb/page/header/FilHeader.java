package dev.haiseong.innodb.page.header;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Builder
@Getter
@ToString
public class FilHeader {

    private final int checksum;
    private final int pageNumber;
    private final int previousPageNumber;
    private final int nextPageNumber;
    private final long lsn;
    private final int pageType;
    private final long flushLsn;
    private final int spaceId;

}
