package com.mob.tools.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;

public class MobRSA {
    private int keySize;

    public MobRSA(int keySize) {
        this.keySize = keySize;
    }

    public BigInteger[] genKeys() throws Throwable {
        SecureRandom random = new SecureRandom();
        return genKeys(BigInteger.probablePrime((this.keySize / 2) - 1, random), BigInteger.probablePrime((this.keySize / 2) - 1, random), BigInteger.probablePrime(this.keySize / 2, random));
    }

    public BigInteger[] genKeys(BigInteger p, BigInteger q, BigInteger e) throws Throwable {
        if (e.compareTo(BigInteger.ONE) <= 0) {
            throw new Throwable("e must be larger than 1");
        }
        BigInteger[] keys = new BigInteger[3];
        BigInteger n = p.multiply(q);
        BigInteger f = n.subtract(p).subtract(q).add(BigInteger.ONE);
        if (e.compareTo(f) >= 0) {
            throw new Throwable("e must be smaller than (p-1)*(q-1)");
        } else if (f.gcd(e).compareTo(BigInteger.ONE) != 0) {
            throw new Throwable("e must be coprime with (p-1)*(q-1)");
        } else {
            BigInteger d = e.modInverse(f);
            keys[0] = e;
            keys[1] = d;
            keys[2] = n;
            return keys;
        }
    }

    public byte[] encode(byte[] source, BigInteger key, BigInteger modulus) throws Throwable {
        int blockSize = this.keySize / 8;
        int inBlockSize = blockSize - 11;
        int offSet = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        while (source.length > offSet) {
            int inputLen = Math.min(source.length - offSet, inBlockSize);
            byte[] cache = encodeBlock(source, offSet, inputLen, key, modulus, blockSize);
            dos.writeInt(cache.length);
            dos.write(cache);
            offSet += inputLen;
        }
        dos.close();
        return baos.toByteArray();
    }

    private byte[] encodeBlock(byte[] bytes, int offset, int len, BigInteger key, BigInteger modulus, int blockSize) throws Throwable {
        byte[] source = bytes;
        if (!(bytes.length == len && offset == 0)) {
            source = new byte[len];
            System.arraycopy(bytes, offset, source, 0, len);
        }
        BigInteger message = new BigInteger(paddingBlock(source, blockSize));
        if (message.compareTo(modulus) <= 0) {
            return message.modPow(key, modulus).toByteArray();
        }
        throw new Throwable("the message must be smaller than the modulue");
    }

    private byte[] paddingBlock(byte[] source, int blockSize) throws Throwable {
        if (source.length > blockSize - 1) {
            throw new Throwable("Message too large");
        }
        byte[] padding = new byte[blockSize];
        padding[0] = (byte) 1;
        int len = source.length;
        padding[1] = (byte) (len >> 24);
        padding[2] = (byte) (len >> 16);
        padding[3] = (byte) (len >> 8);
        padding[4] = (byte) len;
        System.arraycopy(source, 0, padding, blockSize - len, len);
        return padding;
    }

    public byte[] decode(byte[] source, BigInteger key, BigInteger modulus) throws Throwable {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(source));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (dis.available() > 0) {
            byte[] en = new byte[dis.readInt()];
            dis.readFully(en);
            baos.write(decodeBlock(en, key, modulus));
        }
        dis.close();
        baos.close();
        return baos.toByteArray();
    }

    private byte[] decodeBlock(byte[] source, BigInteger key, BigInteger modulus) throws Throwable {
        return recoveryPaddingBlock(new BigInteger(source).modPow(key, modulus).toByteArray());
    }

    private byte[] recoveryPaddingBlock(byte[] padding) throws Throwable {
        if (padding[0] != (byte) 1) {
            throw new Throwable("Not RSA Block");
        }
        int len = ((((padding[1] & 255) << 24) + ((padding[2] & 255) << 16)) + ((padding[3] & 255) << 8)) + (padding[4] & 255);
        byte[] data = new byte[len];
        System.arraycopy(padding, padding.length - len, data, 0, len);
        return data;
    }
}
