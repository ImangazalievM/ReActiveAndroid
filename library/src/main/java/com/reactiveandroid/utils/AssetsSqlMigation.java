
package com.reactiveandroid.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class AssetsSqlMigation {

    public final static int STATE_NONE = 0;
    public final static int STATE_STRING = 1;
    public final static int STATE_COMMENT = 2;
    public final static int STATE_COMMENT_BLOCK = 3;

    private void executeSqlScript(SQLiteDatabase db, Context context, String sqlFilePath) {
        try {
            List<String> commands = parse(context.getAssets().open(sqlFilePath));
            for (String command : commands) {
                db.execSQL(command);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("SQL file %s note found", sqlFilePath));
        }
    }

    private static List<String> parse(final InputStream stream) throws IOException {
        BufferedInputStream buffer = new BufferedInputStream(stream);
        List<String> commands = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        try {
            Tokenizer tokenizer = new Tokenizer(buffer);
            int state = STATE_NONE;

            while (tokenizer.hasNext()) {
                final char c = (char) tokenizer.next();

                if (state == STATE_COMMENT_BLOCK) {
                    if (tokenizer.skip("*/")) {
                        state = STATE_NONE;
                    }
                    continue;

                } else if (state == STATE_COMMENT) {
                    if (isNewLine(c)) {
                        state = STATE_NONE;
                    }
                    continue;

                } else if (state == STATE_NONE && tokenizer.skip("/*")) {
                    state = STATE_COMMENT_BLOCK;
                    continue;

                } else if (state == STATE_NONE && tokenizer.skip("--")) {
                    state = STATE_COMMENT;
                    continue;

                } else if (state == STATE_NONE && c == ';') {
                    final String command = sb.toString().trim();
                    commands.add(command);
                    sb.setLength(0);
                    continue;

                } else if (state == STATE_NONE && c == '\'') {
                    state = STATE_STRING;

                } else if (state == STATE_STRING && c == '\'') {
                    state = STATE_NONE;

                }

                if (state == STATE_NONE || state == STATE_STRING) {
                    if (state == STATE_NONE && isWhitespace(c)) {
                        if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ' ') {
                            sb.append(' ');
                        }
                    } else {
                        sb.append(c);
                    }
                }
            }

        } finally {
            buffer.close();
        }

        if (sb.length() > 0) {
            commands.add(sb.toString().trim());
        }

        return commands;
    }

    private static boolean isNewLine(final char c) {
        return c == '\r' || c == '\n';
    }

    private static boolean isWhitespace(final char c) {
        return c == '\r' || c == '\n' || c == '\t' || c == ' ';
    }

}
