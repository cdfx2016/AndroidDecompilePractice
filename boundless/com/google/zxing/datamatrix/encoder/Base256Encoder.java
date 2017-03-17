package com.google.zxing.datamatrix.encoder;

import android.support.v4.view.InputDeviceCompat;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import com.alibaba.fastjson.asm.Opcodes;

final class Base256Encoder implements Encoder {
    Base256Encoder() {
    }

    public int getEncodingMode() {
        return 5;
    }

    public void encode(EncoderContext context) {
        StringBuilder buffer = new StringBuilder();
        buffer.append('\u0000');
        while (context.hasMoreCharacters()) {
            buffer.append(context.getCurrentChar());
            context.pos++;
            int newMode = HighLevelEncoder.lookAheadTest(context.getMessage(), context.pos, getEncodingMode());
            if (newMode != getEncodingMode()) {
                context.signalEncoderChange(newMode);
                break;
            }
        }
        int dataCount = buffer.length() - 1;
        int currentSize = (context.getCodewordCount() + dataCount) + 1;
        context.updateSymbolInfo(currentSize);
        boolean mustPad;
        if (context.getSymbolInfo().getDataCapacity() - currentSize > 0) {
            mustPad = true;
        } else {
            mustPad = false;
        }
        if (context.hasMoreCharacters() || mustPad) {
            if (dataCount <= 249) {
                buffer.setCharAt(0, (char) dataCount);
            } else if (dataCount <= 249 || dataCount > 1555) {
                throw new IllegalStateException("Message length not in valid ranges: " + dataCount);
            } else {
                buffer.setCharAt(0, (char) ((dataCount / Callback.DEFAULT_SWIPE_ANIMATION_DURATION) + 249));
                buffer.insert(1, (char) (dataCount % Callback.DEFAULT_SWIPE_ANIMATION_DURATION));
            }
        }
        int c = buffer.length();
        for (int i = 0; i < c; i++) {
            context.writeCodeword(randomize255State(buffer.charAt(i), context.getCodewordCount() + 1));
        }
    }

    private static char randomize255State(char ch, int codewordPosition) {
        int tempVariable = ch + (((codewordPosition * Opcodes.FCMPL) % 255) + 1);
        if (tempVariable <= 255) {
            return (char) tempVariable;
        }
        return (char) (tempVariable + InputDeviceCompat.SOURCE_ANY);
    }
}
