package enigma;

import java.util.ArrayList;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Jonny W.
 */
class Alphabet {

    /** Alphabet in a form of an Arraylist of chars. */
    private ArrayList<Character> alphabets = new ArrayList<Character>();

    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        for (int index = 0; index < chars.length(); index++) {
            alphabets.add(chars.charAt(index));
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return alphabets.size();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        return alphabets.contains(ch);
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        return alphabets.get(index);
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        return alphabets.indexOf(ch);
    }

}
