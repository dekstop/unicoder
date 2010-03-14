package de.dekstop.unicoder.util;

import com.ibm.icu.text.UCharacterIterator;

/**
 * Class that enumerates all UTF-16 code points, including multibyte sequences of characters represented as surrogate 
 * pairs (a sequence of two characters from a high, then low surrogate range.)
 * 
 * Note that for simplicity's sake this also generates the 66 Unicode Non-characters ("U+FDD0..U+FDEF and any code 
 * point ending in the value FFFE or FFFF, i.e. U+FFFE, U+FFFF, U+1FFFE, U+1FFFF, ... U+10FFFE, U+10FFFF.")
 */
public class UnicodeEnumeration extends UCharacterIterator {

  private static final int FIRST_HIGH_SURROGATE_CODEPOINT = 0x0D800;
  private static final int FIRST_LOW_SURROGATE_CODEPOINT = 0x0DC00;
  private static final int FIRST_MULTIBYTE_CODEPOINT = 0x0E000;

  private static final int numSingleByteChars = FIRST_HIGH_SURROGATE_CODEPOINT;
  private static final int numHighSurrogateChars = FIRST_LOW_SURROGATE_CODEPOINT - FIRST_HIGH_SURROGATE_CODEPOINT;
  private static final int numLowSurrogateChars = FIRST_MULTIBYTE_CODEPOINT - FIRST_LOW_SURROGATE_CODEPOINT;
  private static final int numMultiByteChars =  numHighSurrogateChars * numLowSurrogateChars;
  //private static final int numChars = numSingleByteChars + numMultiByteChars; // hm. this is missing 0x02000 chars...?!

  private static final int numCharIndices = numSingleByteChars + 2 * numMultiByteChars;

  private int idx = 0; // index of enumeration; projects all Unicode codepoints to an uninterrupted sequence of code units
  private int charIdx = 0; // current code unit. Integer value of current character (either single-byte, or low surrogate)

  private boolean isInMultibyteRange = false;
  private boolean isHighSurrogateChar = false; // multi-byte code points are tuples of (high, low) surrogate chars
  private int highSurrogateCharIdx = 0; // current high-surrogate code unit. Integer value of high surrogate character

  public UnicodeEnumeration(){ }

  @Override
  public int current() {
    if (idx<0 || idx>=getLength()) {
      return DONE;
    }
    if (isInMultibyteRange) {
      if (isHighSurrogateChar) return FIRST_HIGH_SURROGATE_CODEPOINT + highSurrogateCharIdx;
      return FIRST_LOW_SURROGATE_CODEPOINT + charIdx;
    }
    return (char)charIdx;
  }

  @Override
  public int getIndex() {
    return Math.max(0, Math.min(idx, getLength()));
  }

  @Override
  public int getLength() {
    return numCharIndices;
  }

  @Override
  public int getText(char[] fillIn, int offset) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setIndex(int position) {
    idx = position;
    if (position >= FIRST_HIGH_SURROGATE_CODEPOINT) {
      isInMultibyteRange = true;
      isHighSurrogateChar = (((position - FIRST_HIGH_SURROGATE_CODEPOINT) & 1) == 0); // LSB is flag for high-surrogate chars
      int multiByteCharIdx = (position - FIRST_HIGH_SURROGATE_CODEPOINT) >> 1;
      charIdx = multiByteCharIdx % numHighSurrogateChars;
      highSurrogateCharIdx =  multiByteCharIdx / numHighSurrogateChars;
    }
    else {
      isInMultibyteRange = false;
      isHighSurrogateChar = false;
      charIdx = position;
      highSurrogateCharIdx = 0;
    }
  }

  @Override
  public int next() {
    int c = current();
    setIndex(getIndex() + 1);
    return c;
  }

  @Override
  public int previous() {
    setIndex(getIndex() - 1);
    return current();
  }

  public Object clone() {
    //return new UnicodeEnumeration(idx);

    return this; // Yes this is nasty. 
    // Unfortunately ICU's Normalizer doesn't expose a reference to its private character iterator,
    // and clones the one it's given. Our character map builders need access to the current input code 
    // point when iterating, so they can match input vs. output characters. Our workaround is to 
    // prevent cloning. 
    // (No hard feelings though, building character translation maps is not necessarily something ICU
    // devs are expected to cater to.)
  }
}