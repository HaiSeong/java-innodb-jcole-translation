package dev.haiseong.innodb.record.header.compact;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class VariableFieldLength {

    private final int length;
    private final boolean storedExternally;

    public VariableFieldLength(int length, boolean storedExternally) {
        this.length = length;
        this.storedExternally = storedExternally;
    }
} 
