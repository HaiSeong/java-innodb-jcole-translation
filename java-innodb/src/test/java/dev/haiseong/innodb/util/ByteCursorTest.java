package dev.haiseong.innodb.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ByteCursorTest {

    private byte[] testData;

    @BeforeEach
    void setUp() {
        testData = new byte[]{0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x00};
    }

    @DisplayName("setDirection 테스트")
    @Test
    void setDirection() {
        ByteCursor cursor = new ByteCursor(testData, 0);

        cursor.setDirection(ByteCursor.ReadDirection.BACKWARD);

        assertThat(cursor.getDirection()).isEqualTo(ByteCursor.ReadDirection.BACKWARD);
    }

    @DisplayName("capacity 테스트")
    @Test
    void capacity() {
        ByteCursor cursor = new ByteCursor(testData, 0);

        int actual = cursor.capacity();

        assertThat(actual).isEqualTo(8);
    }

    @DisplayName("방향 변경 중 읽기 테스트")
    @Test
    void directionChange() {
        ByteCursor cursor = new ByteCursor(testData, 2);

        byte forwardRead = cursor.readByte();
        cursor.setDirection(ByteCursor.ReadDirection.BACKWARD);
        byte backwardRead = cursor.readByte();
        cursor.setDirection(ByteCursor.ReadDirection.FORWARD);
        byte forwardReadAgain = cursor.readByte();

        assertAll(
                () -> assertThat(forwardRead).isEqualTo((byte) 0x33),
                () -> assertThat(backwardRead).isEqualTo((byte) 0x33),
                () -> assertThat(forwardReadAgain).isEqualTo((byte) 0x33)
        );
    }

    @DisplayName("생성자 테스트")
    @Nested
    class ConstructorTest {

        @DisplayName("기본 생성자로 FORWARD 방향으로 초기화한다")
        @Test
        void constructor() {
            ByteCursor actual = new ByteCursor(testData, 0);

            assertAll(
                    () -> assertThat(actual.getPosition()).isEqualTo(0),
                    () -> assertThat(actual.getDirection()).isEqualTo(ByteCursor.ReadDirection.FORWARD)
            );
        }

        @DisplayName("방향을 지정하여 초기화한다")
        @Test
        void constructorWithDirection() {
            ByteCursor actual = new ByteCursor(testData, 2, ByteCursor.ReadDirection.BACKWARD);

            assertAll(
                    () -> assertThat(actual.getPosition()).isEqualTo(2),
                    () -> assertThat(actual.getDirection()).isEqualTo(ByteCursor.ReadDirection.BACKWARD)
            );
        }

        @DisplayName("원본 데이터를 복사하여 불변성을 보장한다")
        @Test
        void dataImmutability() {
            byte[] originalData = {0x11, 0x22};
            ByteCursor cursor = new ByteCursor(originalData, 0);

            originalData[0] = 0x00;

            byte actual = cursor.readByte();
            assertThat(actual).isEqualTo((byte) 0x11);
        }
    }

    @DisplayName("readByte 테스트")
    @Nested
    class ReadByteTest {

        @DisplayName("FORWARD 방향으로 바이트를 읽는다")
        @Test
        void readByteForward() {
            ByteCursor cursor = new ByteCursor(testData, 0);

            byte firstByte = cursor.readByte();
            byte secondByte = cursor.readByte();

            assertAll(
                    () -> assertThat(firstByte).isEqualTo((byte) 0x11),
                    () -> assertThat(secondByte).isEqualTo((byte) 0x22),
                    () -> assertThat(cursor.getPosition()).isEqualTo(2)
            );
        }

        @DisplayName("BACKWARD 방향으로 바이트를 읽는다")
        @Test
        void readByteBackward() {
            ByteCursor cursor = new ByteCursor(testData, 2, ByteCursor.ReadDirection.BACKWARD);

            byte firstByte = cursor.readByte();
            byte secondByte = cursor.readByte();

            assertAll(
                    () -> assertThat(firstByte).isEqualTo((byte) 0x22),
                    () -> assertThat(secondByte).isEqualTo((byte) 0x11),
                    () -> assertThat(cursor.getPosition()).isEqualTo(0)
            );
        }

        @DisplayName("범위를 벗어나면 예외를 발생시킨다")
        @Test
        void readByteOutOfBounds() {
            ByteCursor cursor = new ByteCursor(testData, 7);
            cursor.readByte();

            assertThatThrownBy(cursor::readByte)
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    @DisplayName("readUnsignedByte 테스트")
    @Nested
    class ReadUnsignedByteTest {

        @DisplayName("FORWARD 방향으로 unsigned 바이트를 읽는다")
        @Test
        void readUnsignedByteForward() {
            ByteCursor cursor = new ByteCursor(testData, 0);

            int first = cursor.readUnsignedByte();
            int second = cursor.readUnsignedByte();
            int third = cursor.readUnsignedByte();

            assertAll(
                    () -> assertThat(first).isEqualTo(0x11),
                    () -> assertThat(second).isEqualTo(0x22),
                    () -> assertThat(third).isEqualTo(0x33)
            );
        }

        @DisplayName("BACKWARD 방향으로 unsigned 바이트를 읽는다")
        @Test
        void readUnsignedByteBackward() {
            ByteCursor cursor = new ByteCursor(testData, 3, ByteCursor.ReadDirection.BACKWARD);

            int first = cursor.readUnsignedByte();
            int second = cursor.readUnsignedByte();
            int third = cursor.readUnsignedByte();

            assertAll(
                    () -> assertThat(first).isEqualTo(0x33),
                    () -> assertThat(second).isEqualTo(0x22),
                    () -> assertThat(third).isEqualTo(0x11)
            );
        }

        @DisplayName("음수 바이트를 양수로 변환한다")
        @Test
        void signedByteAsUnsigned() {
            byte[] data = {(byte) 0x88};
            ByteCursor cursor = new ByteCursor(data, 0);

            int actual = cursor.readUnsignedByte();
            int expected = 136;

            assertThat(actual).isEqualTo(expected);
        }
    }

    @DisplayName("readUnsignedShort 테스트")
    @Nested
    class ReadUnsignedShortTest {

        @DisplayName("FORWARD 방향으로 2바이트를 읽어 big-endian으로 조합한다")
        @Test
        void readUnsignedShortForward() {
            ByteCursor cursor = new ByteCursor(testData, 0);

            int first = cursor.readUnsignedShort();
            int second = cursor.readUnsignedShort();

            assertAll(
                    () -> assertThat(first).isEqualTo(0x1122),
                    () -> assertThat(second).isEqualTo(0x3344),
                    () -> assertThat(cursor.getPosition()).isEqualTo(4)
            );
        }

        @DisplayName("BACKWARD 방향으로 2바이트를 읽어 big-endian으로 조합한다")
        @Test
        void readUnsignedShortBackward() {
            ByteCursor cursor = new ByteCursor(testData, 4, ByteCursor.ReadDirection.BACKWARD);

            int first = cursor.readUnsignedShort();
            int second = cursor.readUnsignedShort();

            assertAll(
                    () -> assertThat(first).isEqualTo(0x3344),
                    () -> assertThat(second).isEqualTo(0x1122),
                    () -> assertThat(cursor.getPosition()).isEqualTo(0)
            );
        }
    }

    @DisplayName("readUnsignedInt 테스트")
    @Nested
    class ReadUnsignedIntTest {

        @DisplayName("FORWARD 방향으로 4바이트를 읽어 big-endian으로 조합한다")
        @Test
        void readUnsignedIntForward() {
            ByteCursor cursor = new ByteCursor(testData, 0);

            long actual = cursor.readUnsignedInt();
            long expected = 0x11223344L;

            assertAll(
                    () -> assertThat(actual).isEqualTo(expected),
                    () -> assertThat(cursor.getPosition()).isEqualTo(4)
            );
        }

        @DisplayName("BACKWARD 방향으로 4바이트를 읽어 big-endian으로 조합한다")
        @Test
        void readUnsignedIntBackward() {
            ByteCursor cursor = new ByteCursor(testData, 4, ByteCursor.ReadDirection.BACKWARD);

            long actual = cursor.readUnsignedInt();
            long expected = 0x11223344L;

            assertAll(
                    () -> assertThat(actual).isEqualTo(expected),
                    () -> assertThat(cursor.getPosition()).isEqualTo(0)
            );
        }

        @DisplayName("지정된 바이트 수만큼 읽어 조합한다")
        @Test
        void readUnsignedIntWithCustomByteCount() {
            ByteCursor cursor = new ByteCursor(testData, 0);

            long oneByte = cursor.readUnsignedInt(1);
            long threeBytes = cursor.readUnsignedInt(3);

            assertAll(
                    () -> assertThat(oneByte).isEqualTo(0x11),
                    () -> assertThat(threeBytes).isEqualTo(0x223344)
            );
        }

        @DisplayName("8바이트까지 읽을 수 있다")
        @Test
        void readUnsignedIntEightBytes() {
            ByteCursor cursor = new ByteCursor(testData, 0);

            long actual = cursor.readUnsignedInt(8);
            long expected = 0x1122334455667700L;

            assertThat(actual).isEqualTo(expected);
        }

        @DisplayName("잘못된 바이트 수를 입력하면 예외를 발생시킨다")
        @Test
        void readUnsignedIntInvalidByteCount() {
            ByteCursor cursor = new ByteCursor(testData, 0);

            assertAll(
                    () -> assertThatThrownBy(() -> cursor.readUnsignedInt(0))
                            .isInstanceOf(IllegalArgumentException.class),
                    () -> assertThatThrownBy(() -> cursor.readUnsignedInt(9))
                            .isInstanceOf(IllegalArgumentException.class)
            );
        }
    }

    @DisplayName("readBytes 테스트")
    @Nested
    class ReadBytesTest {

        @DisplayName("FORWARD 방향으로 지정된 길이만큼 바이트를 읽는다")
        @Test
        void readBytesForward() {
            ByteCursor cursor = new ByteCursor(testData, 0);

            byte[] actual = cursor.readBytes(4);
            byte[] expected = {0x11, 0x22, 0x33, 0x44};

            assertAll(
                    () -> assertThat(actual).isEqualTo(expected),
                    () -> assertThat(cursor.getPosition()).isEqualTo(4)
            );
        }

        @DisplayName("BACKWARD 방향으로 지정된 길이만큼 바이트를 읽는다")
        @Test
        void readBytesBackward() {
            ByteCursor cursor = new ByteCursor(testData, 4, ByteCursor.ReadDirection.BACKWARD);

            byte[] actual = cursor.readBytes(4);
            byte[] expected = {0x11, 0x22, 0x33, 0x44};

            assertAll(
                    () -> assertThat(actual).isEqualTo(expected),
                    () -> assertThat(cursor.getPosition()).isEqualTo(0)
            );
        }
    }

    @DisplayName("skip 테스트")
    @Nested
    class SkipTest {

        @DisplayName("FORWARD 방향으로 지정된 바이트만큼 건너뛴다")
        @Test
        void skipForward() {
            ByteCursor cursor = new ByteCursor(testData, 2);

            cursor.skip(3);

            assertThat(cursor.getPosition()).isEqualTo(5);
        }

        @DisplayName("BACKWARD 방향으로 지정된 바이트만큼 건너뛴다")
        @Test
        void skipBackward() {
            ByteCursor cursor = new ByteCursor(testData, 5, ByteCursor.ReadDirection.BACKWARD);

            cursor.skip(3);

            assertThat(cursor.getPosition()).isEqualTo(2);
        }
    }

    @DisplayName("setPosition 테스트")
    @Nested
    class SetPositionTest {

        @DisplayName("위치를 변경한다")
        @Test
        void setPosition() {
            ByteCursor cursor = new ByteCursor(testData, 0);

            cursor.setPosition(5);

            assertThat(cursor.getPosition()).isEqualTo(5);
        }

        @DisplayName("잘못된 위치를 설정하면 예외를 발생시킨다")
        @Test
        void setPositionInvalid() {
            ByteCursor cursor = new ByteCursor(testData, 0);

            assertAll(
                    () -> assertThatThrownBy(() -> cursor.setPosition(-1))
                            .isInstanceOf(IndexOutOfBoundsException.class),
                    () -> assertThatThrownBy(() -> cursor.setPosition(9))
                            .isInstanceOf(IndexOutOfBoundsException.class)
            );
        }
    }
} 
