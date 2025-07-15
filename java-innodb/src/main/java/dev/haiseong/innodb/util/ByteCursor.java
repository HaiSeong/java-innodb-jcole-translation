package dev.haiseong.innodb.util;

import java.util.Arrays;
import lombok.Getter;

@Getter
public class ByteCursor {

    private final byte[] data;
    private int position;
    private ReadDirection direction;

    public ByteCursor(byte[] data, int position) {
        this(data, position, ReadDirection.FORWARD);
    }

    public ByteCursor(byte[] data, int position, ReadDirection direction) {
        this.data = Arrays.copyOf(data, data.length);
        this.position = position;
        this.direction = direction;
    }

    public byte readByte() {
        checkBounds(1);

        // BACKWARD의 경우 읽기 전에 위치 이동
        if (direction == ReadDirection.BACKWARD) {
            position += direction.getDelta();
        }

        byte result = data[position];

        // FORWARD의 경우 읽기 후에 위치 이동
        if (direction == ReadDirection.FORWARD) {
            position += direction.getDelta();
        }

        return result;
    }

    public byte[] readBytes(int length) {
        checkBounds(length);
        byte[] result = new byte[length];

        if (direction == ReadDirection.FORWARD) {
            System.arraycopy(data, position, result, 0, length);
            position += direction.getDelta() * length;
        } else if (direction == ReadDirection.BACKWARD) {
            // BACKWARD: 현재 위치에서 length만큼 뒤로 가서 그 위치부터 순서대로 읽기
            int startPosition = position - length;
            System.arraycopy(data, startPosition, result, 0, length);
            position += direction.getDelta() * length;
        }
        return result;
    }

    public int readUnsignedByte() {
        checkBounds(1);

        // BACKWARD의 경우 읽기 전에 위치 이동
        if (direction == ReadDirection.BACKWARD) {
            position += direction.getDelta();
        }

        int result = data[position] & 0xFF;

        // FORWARD의 경우 읽기 후에 위치 이동
        if (direction == ReadDirection.FORWARD) {
            position += direction.getDelta();
        }

        return result;
    }

    public int readUnsignedShort() {
        return (int) readUnsignedInt(2);
    }

    public long readUnsignedInt() {
        return readUnsignedInt(4);
    }

    public long readUnsignedInt(int byteCount) {
        if (byteCount < 1 || byteCount > 8) {
            throw new IllegalArgumentException("Byte count must be between 1 and 8, got: " + byteCount);
        }

        checkBounds(byteCount);
        long result = 0;

        if (direction == ReadDirection.FORWARD) {
            // Big-endian 방식으로 읽기 (가장 상위 바이트부터)
            for (int i = 0; i < byteCount; i++) {
                result = (result << 8) | readUnsignedByte();
            }
        } else if (direction == ReadDirection.BACKWARD) {
            // BACKWARD에서는 읽은 바이트들을 역순으로 조합
            byte[] bytes = new byte[byteCount];
            for (int i = 0; i < byteCount; i++) {
                bytes[i] = (byte) readUnsignedByte();
            }
            // 역순으로 조합하여 올바른 big-endian 값 생성
            for (int i = byteCount - 1; i >= 0; i--) {
                result = (result << 8) | (bytes[i] & 0xFF);
            }
        }
        return result;
    }

    public void skip(int bytes) {
        setPosition(position + direction.getDelta() * bytes);
    }

    public int capacity() {
        return data.length;
    }

    public void setPosition(int newPosition) {
        if (newPosition < 0 || newPosition > data.length) {
            throw new IndexOutOfBoundsException(
                    String.format("Position %d is out of bounds [0, %d]", newPosition, data.length));
        }
        this.position = newPosition;
    }

    public void setDirection(ReadDirection direction) {
        this.direction = direction;
    }

    private void checkBounds(int bytesToRead) {
        int targetPosition = position + direction.getDelta() * bytesToRead;

        if (direction == ReadDirection.FORWARD) {
            if (targetPosition > data.length) {
                throw new IndexOutOfBoundsException(
                        String.format("Cannot read %d bytes forward from position %d (capacity: %d)",
                                bytesToRead, position, data.length));
            }
        } else {
            if (targetPosition < 0) {
                throw new IndexOutOfBoundsException(
                        String.format("Cannot read %d bytes backward from position %d", bytesToRead, position));
            }
        }
    }

    @Getter
    public enum ReadDirection {
        FORWARD(1),
        BACKWARD(-1);

        private final int delta;

        ReadDirection(int delta) {
            this.delta = delta;
        }

    }
} 
