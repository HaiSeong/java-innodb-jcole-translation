package dev.haiseong.innodb.record.header.compact;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class NullableBitmap {

    public final boolean[] bitmap;

    public NullableBitmap(boolean[] bitmap) {
        this.bitmap = bitmap;
    }

} 
