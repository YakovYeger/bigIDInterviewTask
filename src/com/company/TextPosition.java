package com.company;

public class TextPosition {

    private int charOffset;
    private int lineOffset;

    @Override
    public String toString() {
        return "[" +
                "charOffset=" + charOffset +
                ", lineOffset=" + lineOffset +
                ']';
    }

    public TextPosition(int lineOffset, int charOffset) {
        this.lineOffset = lineOffset;
        this.charOffset = charOffset;
    }

    public int getCharOffset() {
        return charOffset;
    }

    public int getLineOffset() {
        return lineOffset;
    }

    public void setCharOffset(int charOffset) {
        this.charOffset = charOffset;
    }

    public void setLineOffset(int lineOffset) {
        this.lineOffset = lineOffset;
    }
}
