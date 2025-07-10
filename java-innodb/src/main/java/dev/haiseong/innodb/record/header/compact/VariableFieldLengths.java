package dev.haiseong.innodb.record.header.compact;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class VariableFieldLengths {

    private final VariableFieldLength[] lengths;

    public VariableFieldLengths(VariableFieldLength[] lengths) {
        this.lengths = lengths;
    }
} 
