package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Jonny W.
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        perm = new char[_alphabet.size()];
        char[] cyclesArray = cycles.toCharArray();
        if (cyclesArray.length > 0) {
            Character leftParenthesis = '(';
            Character rightParenthesis = ')';
            char temp = cyclesArray[1];
            int leftParCount = 0;
            int rightParCount = 0;
            for (Character c: cyclesArray) {
                if (c == leftParenthesis) {
                    leftParCount++;
                } else if (c == rightParenthesis) {
                    rightParCount++;
                }
            }
            if (leftParCount != rightParCount) {
                throw new EnigmaException("incomplete cycles");
            }
            for (int index = 1; index < cyclesArray.length - 1; index++) {
                if (((Character) cyclesArray[index]).equals(leftParenthesis)) {
                    temp = cyclesArray[index + 1];
                } else if (
                        ((Character)
                                cyclesArray[index + 1]).equals(
                                        rightParenthesis)) {
                    perm[_alphabet.toInt(cyclesArray[index])] = temp;
                    index += 2;
                } else {
                    perm[_alphabet.toInt(cyclesArray[index])] =
                            cyclesArray[index + 1];
                }
            }
        }
        for (int index = 0; index < _alphabet.size(); index++) {
            if (perm[index] == 0) {
                perm[index] = _alphabet.toChar(index);
            }
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        char[] cycleArray = cycle.toCharArray();
        Character leftParenthesis = '(';
        Character rightParenthesis = ')';
        char temp = cycleArray[1];
        for (int index = 1; index < cycleArray.length - 1; index++) {
            if (((Character) cycleArray[index]).equals(leftParenthesis)) {
                temp = cycleArray[index + 1];
            } else if ((
                    (Character) cycleArray[index + 1]).equals(
                            rightParenthesis)) {
                perm[_alphabet.toInt(cycleArray[index])] = temp;
                index += 2;
            } else {
                perm[_alphabet.toInt(cycleArray[index])]
                        = cycleArray[index + 1];
            }
        }
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return perm.length;
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        p = wrap(p);
        char temp = perm[p];
        int result = _alphabet.toInt(temp);
        return result;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        c = wrap(c);
        char temp = _alphabet.toChar(c);
        int result = new String(perm).indexOf(temp);
        return result;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int index = _alphabet.toInt(p);
        return perm[index];
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int index = new String(perm).indexOf(c);
        return _alphabet.toChar(index);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int index = 0; index < perm.length; index++) {
            int temp = new String(perm).indexOf(_alphabet.toChar(index));
            if (index == temp) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** An array containing _alphabet in the order of the permutation. */
    private char[] perm;
}
