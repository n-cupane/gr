package com.cupane;

import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {

        final String VERSION = "1.0.3";

        String RESET = "\u001B[0m";
        String BLUE = "\u001B[36m";
        String RED = "\u001B[31m";
        String GREEN = "\u001B[32m";

        Options options = new Options();
        HelpFormatter helpFormatter = new HelpFormatter();
        boolean showLineNumbers = false;
        long startTime = 0;
        boolean ignoreCase = false;

        Option help = new Option("h", "help", false, "print this message");
        Option version = new Option("v", "version", false, "print version information");
        Option displayLineNumber = new Option("n", "line-number", false, "print the line number");
        Option time = new Option("t", "time", false, "print running time");
        Option ignoreCaseOption = new Option("i", "ignore-case", false, "ignore case difference in pattern and data");

        options.addOption(help);
        options.addOption(version);
        options.addOption(displayLineNumber);
        options.addOption(time);
        options.addOption(ignoreCaseOption);

        CommandLineParser parser = new DefaultParser();

//        args = new String[]{"Pippo", "fileozzo"};
        Cli cli = null;
        try {
            CommandLine commandLine = parser.parse(options, args);
            if (commandLine.hasOption(help)) {
                helpFormatter.printHelp("gr [OPTION]... PATTERN [FILE]...", options);
                System.exit(0);
            } else if (commandLine.hasOption(version)) {
                System.out.println("gr version " + VERSION);
                System.exit(0);
            }
            else {
                if (commandLine.getArgList().isEmpty()) throw new ParseException("No arguments given");
                if (commandLine.getArgList().size() < 2) throw new ParseException("Two arguments must be given");
                if (commandLine.hasOption(displayLineNumber)) showLineNumbers = true;
                if (commandLine.hasOption(time)) startTime = System.currentTimeMillis();
                if (commandLine.hasOption(ignoreCaseOption)) ignoreCase = true;
            }

            cli = new Cli(commandLine.getArgList());

//            System.out.println("Args:");
//            System.out.println(commandLine.getArgList());
//            System.out.println("Options: ");
//            System.out.println(commandLine.getOptionValue("input"));
        } catch (ParseException e) {
            System.err.println(RED + "Parsing failed: " + e.getMessage() + RESET);
            System.exit(1);
        }

        for (Path path: cli.getPaths()) {
//                BufferedReader br = new BufferedReader(new FileReader(path.toAbsolutePath().toString()));
            try (BufferedReader br = Files.newBufferedReader(path)) {
                String line;
                int lineNumber = 0;
                while ((line = br.readLine()) != null) {
                    lineNumber++;

                    if (ignoreCase) {
                        cli.setPattern(cli.getPattern().toLowerCase());
                        line = line.toLowerCase();
                    }

                    if (line.contains(cli.getPattern())) {
                        if (showLineNumbers) System.out.print(GREEN + lineNumber + ": " + RESET);

                        String[] lineParts = line.split(cli.getPattern());

                        if (lineParts.length == 0) {
                            System.out.println(RED + line + RESET);
                        } else {
                            System.out.println(lineParts[0] + RED + cli.getPattern() + RESET + ((lineParts.length > 1) ? lineParts[1] : ""));
                        }

                    }
                }
                if (startTime != 0) System.out.println("Total running time: " + (System.currentTimeMillis() - startTime) + " milliseconds");
            } catch (IOException e) {
                System.err.println(RED + "Could not read file: " + path.toString() + RESET);
            }
        }

    }
}