package enigma;

import java.util.ArrayList;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Jonny W.
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        return curRotors.get(k);
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        if (curRotors.size() > 0) {
            curRotors = new ArrayList<Rotor>();
        }
        for (int index = 0; index < rotors.length; index++) {
            boolean rotAdded = false;
            for (Rotor r : _allRotors) {
                if (rotors[index].equals(r.name())) {
                    curRotors.add(r);
                    rotAdded = true;
                    break;
                }
            }
            if (!rotAdded) {
                throw new EnigmaException("no such rotor");
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int index = 1; index < curRotors.size(); index++) {
            curRotors.get(index).set(setting.charAt(index - 1));
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        boolean[] doubStep = new boolean[numRotors()];
        for (int index = 1; index < numRotors() - 1; index++) {
            if (getRotor(index).rotates() && getRotor(index + 1).atNotch()) {
                doubStep[index] = doubStep[index + 1] = true;
            }
        }
        doubStep[numRotors() - 1] = true;
        for (int index = 1; index < numRotors(); index++) {
            if (doubStep[index]) {
                getRotor(index).advance();
            }
        }
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        for (int index = numRotors() - 1; index >= 0; index--) {
            c = getRotor(index).convertForward(c);
        }
        for (int index = 1; index < numRotors(); index++) {
            c = getRotor(index).convertBackward(c);
        }
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        char[] cvtMsg = msg.toCharArray();
        int[] intMsg = new int[cvtMsg.length];
        String result = "";
        for (int index = 0; index < cvtMsg.length; index++) {
            intMsg[index] = alphabet().toInt(cvtMsg[index]);
            intMsg[index] = convert(intMsg[index]);
            result += alphabet().toChar(intMsg[index]);
        }
        return result;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotors in this machine. */
    private int _numRotors;

    /** Number of pawls in this machine. */
    private int _pawls;

    /** A list of all rotors available. */
    private Collection<Rotor> _allRotors;

    /** An ArrayList of all the rotors in this machine. */
    private ArrayList<Rotor> curRotors = new ArrayList<Rotor>();

    /** The plugboard of this machine. */
    private Permutation _plugboard;

}
