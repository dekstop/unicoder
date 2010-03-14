/**
 * 
 */
package de.dekstop.unicoder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ibm.icu.text.Normalizer;
import com.ibm.icu.text.UTF16;

import de.dekstop.unicoder.util.UnicodeEnumeration;

/**
 * Builds a character map based on Unicode compatibility decomposition.
 */
public class CompatibilityEquivalence {

  /**
   * 
   * @param ue
   * @param n
   * @return
   */
  private static Map<Integer, Set<Integer>> generateCharacterMap(UnicodeEnumeration ue, Normalizer n) {
    Map<Integer, Set<Integer>> chars = new HashMap<Integer, Set<Integer>>();
    
    int idx = ue.getIndex();
    int codePoint = ue.currentCodePoint();
    for (; n.next()!=Normalizer.DONE;) {
      int normalizedCodePoint = n.current();

      if (normalizedCodePoint!=codePoint) {
        if (!chars.containsKey(normalizedCodePoint)) {
          chars.put(normalizedCodePoint, new HashSet<Integer>());
        }
        Set<Integer> targets = chars.get(normalizedCodePoint);
        targets.add(codePoint);
        if (ue.current() % 1000 == 0) {
          System.err.println(String.format("%d: %s -> %s (%d -> %d)", idx, UTF16.valueOf(codePoint), UTF16.valueOf(normalizedCodePoint), codePoint, normalizedCodePoint));
        }
      }

      // next
      idx = ue.getIndex();
      codePoint = ue.currentCodePoint();
    }
    return chars;
  }

  /**
   * 
   * @param chars
   * @param file
   * @throws FileNotFoundException
   * @throws IOException
   */
  private static void createCharacterMapFile(Map<Integer, Set<Integer>> chars, File file) throws FileNotFoundException,
    IOException {
    Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")));
    
    Integer[] sources = chars.keySet().toArray(new Integer[chars.size()]);
    Arrays.sort(sources);
    for (Integer source : sources) {
      out.write(UTF16.valueOf(source));
      out.write("=");
      Integer[] targets = chars.get(source).toArray(new Integer[]{});
      Arrays.sort(targets);
      for (int idx=0; idx<targets.length; idx++) {
        Integer target = targets[idx];
        out.write(UTF16.valueOf(target));
        if (idx<targets.length-1) {
          out.write(",");
        }
      }
      out.write("\n");
    }
    
    out.close();
  }

  /**
   * @param args
   */
  public static void main(String[] args) throws Exception {
    
    if (args.length!=1) {
      System.err.println("<output file>");
      System.exit(1);
    }
    
    String filename = args[0];

    UnicodeEnumeration ue = new UnicodeEnumeration();
    Normalizer n = new Normalizer(ue, Normalizer.NFKD, 0);

    Map<Integer, Set<Integer>> chars = generateCharacterMap(ue, n);
    createCharacterMapFile(chars, new File(filename));
  }

}
