package dev.haiseong.innodb.page.header;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Builder
@Getter
@ToString
public class FilTrailer {

    private final int oldStyleChecksum;
    private final int lsn;

}
