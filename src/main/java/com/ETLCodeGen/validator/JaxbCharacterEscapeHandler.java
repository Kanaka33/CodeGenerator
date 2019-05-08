package com.ETLCodeGen.validator;

import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;

import java.io.IOException;
import java.io.Writer;

public class JaxbCharacterEscapeHandler implements CharacterEscapeHandler {

    public void escape(char[] buf, int start, int len, boolean isAttValue,
                       Writer out) throws IOException {

        for (int i = start; i < start + len; i++) {
            char ch = buf[i];
            out.write(ch);
        }
    }
}
