package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Arrays;

import net.sf.saxon.expr.Component;
import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Jonny W.
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine m = readConfig();
        String curLine = _input.nextLine();
        if (curLine.charAt(0) == '*') {
            setUp(m, curLine.substring(2));
            if (!m.getRotor(0).reflecting()) {
                throw new EnigmaException("first rotor must be the reflector");
            }
            String rotName = m.getRotor(0).name();
            for (int index = 1; index < allRot; index++) {
                for (int index2 = index; index2 < allRot; index2++) {
                    if (allRotors.get(index2).name().equals(rotName)) {
                        throw new EnigmaException("duplicated rotors");
                    }
                }
                rotName = allRotors.get(index).name();
            }
        } else {
            throw new EnigmaException("must have a setting");
        }
        while (_input.hasNextLine()) {
            curLine = _input.nextLine();
            if (curLine.length() < 1) {
                _output.println(" ");
                continue;
            } else {
                if (curLine.charAt(0) == '*') {
                    setUp(m, curLine.substring(2));
                    if (!m.getRotor(0).reflecting()) {
                        throw new EnigmaException(
                                "first rotor must be the reflector");
                    }
                    String rotName = m.getRotor(0).name();
                    for (int index = 1; index < allRot; index++) {
                        for (int index2 = index; index2 < allRot; index2++) {
                            if (allRotors.get(index2).name().equals(rotName)) {
                                throw new EnigmaException("duplicated rotors");
                            }
                        }
                        rotName = allRotors.get(index).name();
                    }
                } else {
                    curLine = curLine.replaceAll(" ", "");
                    printMessageLine(m.convert(curLine));
                }
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            alph = new Alphabet(_config.nextLine());
            allRot = _config.nextInt();
            movRot = _config.nextInt();
            while (_config.hasNext()) {
                allRotors.add(readRotor());
            }
            return new Machine(alph, allRot, movRot, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String rotName = _config.next();
            String rotSet = _config.next();
            String perms = "";
            while (_config.hasNext("\\(.*\\)")) {
                perms += _config.next();
            }
            ArrayList<String> reFormatPerms = new ArrayList<String>(
                    Arrays.asList(perms.split("")));
            perms = "";
            for (String s: reFormatPerms) {
                if (s.equals(")")) {
                    perms += s + " ";
                } else {
                    perms += s;
                }
            }
            if (rotSet.charAt(0) == 'M') {
                return new MovingRotor(rotName,
                        new Permutation(perms, alph), rotSet.substring(1));
            } else if (rotSet.charAt(0) == 'N') {
                return new FixedRotor(rotName, new Permutation(perms, alph));
            } else {
                return new Reflector(rotName, new Permutation(perms, alph));
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        ArrayList<String> setList = new ArrayList<String>(
                Arrays.asList(settings.split(" ")));
        String[] curRotors = new String[allRot];
        for (int index = 0; index < curRotors.length; index++) {
            curRotors[index] = setList.get(index);
        }
        if (allRot >= setList.size()) {
            throw new EnigmaException("wrong number of rotors");
        }
        String curSetting = setList.get(allRot);
        char[] checkStr = curSetting.toCharArray();
        for (char c: checkStr) {
            if (!alph.contains(c)) {
                throw new EnigmaException("wrong rotor configurations");
            }
        }

        String curPlugboard = "";
        for (int index = allRot + 1; index < setList.size(); index++) {
            curPlugboard += (setList.get(index) + " ");
        }
        M.insertRotors(curRotors);
        int checkMRot = 0;
        for (int count = 0; count < allRot; count++) {
            if (M.getRotor(count).rotates()) {
                checkMRot++;
            }
        }
        if (checkMRot != movRot) {
            throw new EnigmaException("wrong number of moving rotors");
        }
        M.setRotors(curSetting);
        M.setPlugboard(new Permutation(curPlugboard, alph));
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        while (true) {
            if (msg.length() > 5) {
                _output.print(msg.substring(0, 5) + " ");
                msg = msg.substring(5);
            } else {
                _output.println(msg);
                break;
            }
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;

    /** An ArrayList containing all possible rotors. */
    private ArrayList<Rotor> allRotors = new ArrayList<Rotor>();

    /** Alphabets. */
    private Alphabet alph;

    /** number of rotors intended. */
    private int allRot;

    /** number of moving rotors intended. */
    private int movRot;
}
