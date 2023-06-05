package processing;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.SocketException;
import java.nio.ByteBuffer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;

public class TCPExchanger {
    private static final int LENGTH_FIELD_SIZE = SerializationUtils.serialize(Integer.MAX_VALUE).length;

    public static void write(BufferedOutputStream bufferedOutputStream, Serializable object) throws IOException {
        byte[] objectBytes = SerializationUtils.serialize(object);
        bufferedOutputStream.write(objectBytes);
        bufferedOutputStream.flush();
    }

    public static Serializable read(BufferedInputStream bufferedInputStream) throws IOException {
        byte[] objectBytes = new byte[100000];
        int readCnt = bufferedInputStream.read(objectBytes);
        if (readCnt == -1) {
            throw new IOException(
                "(1) Cannot read object from socket"
            );
        }
        Serializable object = SerializationUtils.deserialize(objectBytes);
        return object;
    }



    /**
     * Writes one object to the channel.
     *
     * @param channel channel to write to
     * @param object object to be written
     * @throws IOException if failed to write to channel
     */
    public static void serverWrite(BufferedOutputStream bufferedOutputStream, Serializable object) throws SocketException, IOException {
        System.out.println("server started to read");
        byte[] objectBytes = SerializationUtils.serialize(object);
        int packages = (objectBytes.length + LENGTH_FIELD_SIZE - 1) / LENGTH_FIELD_SIZE;
        System.out.println("count of packages: " + packages);
        int objectBytesLength = objectBytes.length;
        byte[] objectPackagesCount = SerializationUtils.serialize(packages);
        bufferedOutputStream.write(objectPackagesCount);
        for (int i = 0; i < packages; i++) {
            byte[] bytePackage = new byte[Math.min(LENGTH_FIELD_SIZE, objectBytesLength - LENGTH_FIELD_SIZE * i)];
            System.arraycopy(objectBytes,LENGTH_FIELD_SIZE * i, bytePackage, 0, bytePackage.length);
            bufferedOutputStream.write(bytePackage);
        }
        bufferedOutputStream.flush();
        System.out.println("server write done");
    }

    /**
     * Reads one object from the channel and returns it.
     *
     * @param channel channel to read from
     * @return object read from the channel
     * @throws IOException if failed to read from channel
     */
    public static Serializable serverRead(BufferedInputStream bufferedInputStream) throws IOException {
        int readCnt;
        // ByteBuffer lengthBuffer = ByteBuffer.allocate(LENGTH_FIELD_SIZE);
        System.out.println("server started to read");
        byte[] lengthBytes = new byte[LENGTH_FIELD_SIZE];
        System.out.println(lengthBytes.length);
        readCnt = bufferedInputStream.read(lengthBytes);
        System.out.println("readCnt = " + readCnt);
        if (readCnt != LENGTH_FIELD_SIZE) {
            throw new IOException(
                "(1)Cannot read object from channel: " + readCnt + " != " + LENGTH_FIELD_SIZE
            );
        }
        int length = SerializationUtils.deserialize(lengthBytes);
        System.out.println("message length: " + length);
        // ByteBuffer objectBuffer = ByteBuffer.allocate(length);
        byte[] objectBytes = new byte[length];
        readCnt = bufferedInputStream.read(objectBytes);
        if (readCnt != length) {
            throw new IOException (
                "(2)Cannot read object from channel: " + readCnt + " != " + length
            );
        }
        Serializable object = SerializationUtils.deserialize(objectBytes);
        System.out.println("server read done");
        return object;
    }

    public static void clientWrite(BufferedOutputStream bufferedOutputStream, Serializable object) throws SocketException, IOException {
        byte[] objectBytes = SerializationUtils.serialize(object);
        System.out.println("object bytes = " + objectBytes.length);
        byte[] objectLengthBytes = SerializationUtils.serialize(objectBytes.length);
        System.out.println("object length bytes = " + objectLengthBytes.length);
        bufferedOutputStream.write(ArrayUtils.addAll(objectLengthBytes, objectBytes));
        bufferedOutputStream.flush();
        System.out.println("client write done");
    }

    public static Serializable clientRead(BufferedInputStream bufferedInputStream) throws IOException {
        int readCnt;
        byte[] objectBytes = {};
        // ByteBuffer packegesBuffer = ByteBuffer.allocate(LENGTH_FIELD_SIZE);
        byte[] packagesBytes = new byte[LENGTH_FIELD_SIZE];
        readCnt = bufferedInputStream.read(packagesBytes);
        if (readCnt == -1) {
            throw new IOException("Cannot read count of packages from server");
        }
        int packages = SerializationUtils.deserialize(packagesBytes);
        for (int i = 0; i < packages; i++) {
            ByteBuffer packageBuffer = ByteBuffer.allocate(LENGTH_FIELD_SIZE);
            readCnt = bufferedInputStream.read(packagesBytes);
            if (readCnt == -1) {
                throw new IOException(String.format("Cannot read package %s from server", i));
            }
            objectBytes = ArrayUtils.addAll(objectBytes, packageBuffer.array());
        }
        System.out.println("object bytes length = " + objectBytes.length);
        Serializable object = SerializationUtils.deserialize(objectBytes);
        System.out.println("client read done");
        return object;
    }
    
}
