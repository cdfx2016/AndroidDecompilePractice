package com.google.zxing.datamatrix.encoder;

import android.support.v4.media.TransportMediator;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import com.alibaba.fastjson.asm.Opcodes;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;

public final class ErrorCorrection {
    private static final int[] ALOG = new int[255];
    private static final int[][] FACTORS = new int[][]{new int[]{228, 48, 15, 111, 62}, new int[]{23, 68, 144, 134, PsExtractor.VIDEO_STREAM_MASK, 92, 254}, new int[]{28, 24, Opcodes.INVOKEINTERFACE, Opcodes.IF_ACMPNE, 223, 248, 116, 255, 110, 61}, new int[]{175, TsExtractor.TS_STREAM_TYPE_DTS, 205, 12, 194, 168, 39, 245, 60, 97, 120}, new int[]{41, Opcodes.IFEQ, Opcodes.IFLE, 91, 61, 42, 142, 213, 97, Opcodes.GETSTATIC, 100, 242}, new int[]{156, 97, 192, 252, 95, 9, 157, 119, TsExtractor.TS_STREAM_TYPE_DTS, 45, 18, 186, 83, Opcodes.INVOKEINTERFACE}, new int[]{83, 195, 100, 39, 188, 75, 66, 61, 241, 213, 109, TsExtractor.TS_STREAM_TYPE_AC3, 94, 254, 225, 48, 90, 188}, new int[]{15, 195, 244, 9, 233, 71, 168, 2, 188, Opcodes.IF_ICMPNE, Opcodes.IFEQ, 145, 253, 79, 108, 82, 27, 174, 186, 172}, new int[]{52, 190, 88, 205, 109, 39, 176, 21, 155, 197, 251, 223, 155, 21, 5, 172, 254, 124, 12, Opcodes.PUTFIELD, Opcodes.INVOKESTATIC, 96, 50, Opcodes.INSTANCEOF}, new int[]{211, 231, 43, 97, 71, 96, 103, 174, 37, Opcodes.DCMPL, 170, 53, 75, 34, 249, 121, 17, TsExtractor.TS_STREAM_TYPE_DTS, 110, 213, 141, 136, 120, Opcodes.DCMPL, 233, 168, 93, 255}, new int[]{245, TransportMediator.KEYCODE_MEDIA_PAUSE, 242, 218, 130, Callback.DEFAULT_SWIPE_ANIMATION_DURATION, Opcodes.IF_ICMPGE, Opcodes.PUTFIELD, 102, 120, 84, 179, 220, 251, 80, Opcodes.INVOKEVIRTUAL, 229, 18, 2, 4, 68, 33, 101, 137, 95, 119, 115, 44, 175, Opcodes.INVOKESTATIC, 59, 25, 225, 98, 81, 112}, new int[]{77, Opcodes.INSTANCEOF, 137, 31, 19, 38, 22, Opcodes.IFEQ, 247, 105, 122, 2, 245, 133, 242, 8, 175, 95, 100, 9, Opcodes.GOTO, 105, 214, 111, 57, 121, 21, 1, 253, 57, 54, 101, 248, 202, 69, 50, 150, Opcodes.RETURN, 226, 5, 9, 5}, new int[]{245, 132, 172, 223, 96, 32, 117, 22, 238, 133, 238, 231, 205, 188, 237, 87, 191, 106, 16, 147, 118, 23, 37, 90, 170, 205, 131, 88, 120, 100, 66, TsExtractor.TS_STREAM_TYPE_DTS, 186, PsExtractor.VIDEO_STREAM_MASK, 82, 44, 176, 87, Opcodes.NEW, 147, Opcodes.IF_ICMPNE, 175, 69, 213, 92, 253, 225, 19}, new int[]{175, 9, 223, 238, 12, 17, 220, 208, 100, 29, 175, 170, 230, 192, 215, 235, 150, Opcodes.IF_ICMPEQ, 36, 223, 38, 200, 132, 54, 228, 146, 218, 234, 117, 203, 29, 232, 144, 238, 22, 150, 201, 117, 62, 207, 164, 13, 137, 245, TransportMediator.KEYCODE_MEDIA_PAUSE, 67, 247, 28, 155, 43, 203, 107, 233, 53, 143, 46}, new int[]{242, 93, Opcodes.RET, 50, 144, 210, 39, 118, 202, 188, 201, PsExtractor.PRIVATE_STREAM_1, 143, 108, 196, 37, Opcodes.INVOKEINTERFACE, 112, 134, 230, 245, 63, 197, 190, Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 106, Opcodes.INVOKEINTERFACE, 221, 175, 64, 114, 71, Opcodes.IF_ICMPLT, 44, 147, 6, 27, 218, 51, 63, 87, 10, 40, 130, 188, 17, Opcodes.IF_ICMPGT, 31, 176, 170, 4, 107, 232, 7, 94, Opcodes.IF_ACMPNE, 224, 124, 86, 47, 11, 204}, new int[]{220, 228, 173, 89, 251, Opcodes.FCMPL, Opcodes.IF_ICMPEQ, 56, 89, 33, 147, 244, Opcodes.IFNE, 36, 73, TransportMediator.KEYCODE_MEDIA_PAUSE, 213, 136, 248, Opcodes.GETFIELD, 234, 197, Opcodes.IFLE, Opcodes.RETURN, 68, 122, 93, 213, 15, Opcodes.IF_ICMPNE, 227, 236, 66, 139, Opcodes.IFEQ, Opcodes.INVOKEINTERFACE, 202, Opcodes.GOTO, 179, 25, 220, 232, 96, 210, 231, 136, 223, 239, Opcodes.PUTFIELD, 241, 59, 52, 172, 25, 49, 232, 211, PsExtractor.PRIVATE_STREAM_1, 64, 54, 108, Opcodes.IFEQ, 132, 63, 96, 103, 82, 186}};
    private static final int[] FACTOR_SETS = new int[]{5, 7, 10, 11, 12, 14, 18, 20, 24, 28, 36, 42, 48, 56, 62, 68};
    private static final int[] LOG = new int[256];
    private static final int MODULO_VALUE = 301;

    static {
        int p = 1;
        for (int i = 0; i < 255; i++) {
            ALOG[i] = p;
            LOG[p] = i;
            p <<= 1;
            if (p >= 256) {
                p ^= MODULO_VALUE;
            }
        }
    }

    private ErrorCorrection() {
    }

    public static String encodeECC200(String codewords, SymbolInfo symbolInfo) {
        if (codewords.length() != symbolInfo.getDataCapacity()) {
            throw new IllegalArgumentException("The number of codewords does not match the selected symbol");
        }
        StringBuilder sb = new StringBuilder(symbolInfo.getDataCapacity() + symbolInfo.getErrorCodewords());
        sb.append(codewords);
        int blockCount = symbolInfo.getInterleavedBlockCount();
        if (blockCount == 1) {
            sb.append(createECCBlock(codewords, symbolInfo.getErrorCodewords()));
        } else {
            sb.setLength(sb.capacity());
            int[] dataSizes = new int[blockCount];
            int[] errorSizes = new int[blockCount];
            int[] startPos = new int[blockCount];
            for (int i = 0; i < blockCount; i++) {
                dataSizes[i] = symbolInfo.getDataLengthForInterleavedBlock(i + 1);
                errorSizes[i] = symbolInfo.getErrorLengthForInterleavedBlock(i + 1);
                startPos[i] = 0;
                if (i > 0) {
                    startPos[i] = startPos[i - 1] + dataSizes[i];
                }
            }
            for (int block = 0; block < blockCount; block++) {
                StringBuilder temp = new StringBuilder(dataSizes[block]);
                for (int d = block; d < symbolInfo.getDataCapacity(); d += blockCount) {
                    temp.append(codewords.charAt(d));
                }
                String ecc = createECCBlock(temp.toString(), errorSizes[block]);
                int pos = 0;
                int e = block;
                while (e < errorSizes[block] * blockCount) {
                    int pos2 = pos + 1;
                    sb.setCharAt(symbolInfo.getDataCapacity() + e, ecc.charAt(pos));
                    e += blockCount;
                    pos = pos2;
                }
            }
        }
        return sb.toString();
    }

    private static String createECCBlock(CharSequence codewords, int numECWords) {
        return createECCBlock(codewords, 0, codewords.length(), numECWords);
    }

    private static String createECCBlock(CharSequence codewords, int start, int len, int numECWords) {
        int i;
        int table = -1;
        for (i = 0; i < FACTOR_SETS.length; i++) {
            if (FACTOR_SETS[i] == numECWords) {
                table = i;
                break;
            }
        }
        if (table < 0) {
            throw new IllegalArgumentException("Illegal number of error correction codewords specified: " + numECWords);
        }
        int[] poly = FACTORS[table];
        char[] ecc = new char[numECWords];
        for (i = 0; i < numECWords; i++) {
            ecc[i] = '\u0000';
        }
        for (i = start; i < start + len; i++) {
            int m = ecc[numECWords - 1] ^ codewords.charAt(i);
            int k = numECWords - 1;
            while (k > 0) {
                if (m == 0 || poly[k] == 0) {
                    ecc[k] = ecc[k - 1];
                } else {
                    ecc[k] = (char) (ecc[k - 1] ^ ALOG[(LOG[m] + LOG[poly[k]]) % 255]);
                }
                k--;
            }
            if (m == 0 || poly[0] == 0) {
                ecc[0] = '\u0000';
            } else {
                ecc[0] = (char) ALOG[(LOG[m] + LOG[poly[0]]) % 255];
            }
        }
        char[] eccReversed = new char[numECWords];
        for (i = 0; i < numECWords; i++) {
            eccReversed[i] = ecc[(numECWords - i) - 1];
        }
        return String.valueOf(eccReversed);
    }
}
