package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Jonny W.
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        curSetting = 0;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return curSetting;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        curSetting = _permutation.wrap(posn);
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        curSetting = _permutation.alphabet().toInt(cposn);
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        int curPermInt = curSetting + p;
        int result = _permutation.permute(curPermInt);
        result -= curSetting;
        result = _permutation.wrap(result);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(result));
        }
        return result;
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        int curPermInt = curSetting + e;
        int result = _permutation.invert(curPermInt);
        result -= curSetting;
        result = _permutation.wrap(result);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(result));
        }
        return result;
    }

    /** Returns the positions of the notches, as a string giving the letters
     *  on the ring at which they occur. */
    String notches() {
        return "";
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        if (notches().length() != 0) {
            String curNotch =
                    Character.toString(
                            _permutation.alphabet().toChar(curSetting));
            if (notches().contains(curNotch)) {
                return true;
            }
        }
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    /** A char object that records the current setting of the rotor. */
    private int curSetting;

}
