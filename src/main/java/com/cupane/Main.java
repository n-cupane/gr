package com.cupane;

import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {

        final String VERSION = "1.0.1";

        String RESET = "\u001B[0m";
        String BLUE = "\u001B[36m";
        String RED = "\u001B[31m";

        Options options = new Options();
        HelpFormatter helpFormatter = new HelpFormatter();
        boolean showLineNumbers = false;

        Option help = new Option("h", "help", false, "print this message");
        Option version = new Option("v", "version", false, "print version information");
        Option displayLineNumber = new Option("n", "line-number", false, "print the line number");

        options.addOption(help);
        options.addOption(version);
        options.addOption(displayLineNumber);

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
                    if (line.contains(cli.getPattern())) {
                        if (showLineNumbers) System.out.print(lineNumber + " ");

                        String[] lineParts = line.split(cli.getPattern());

                        if (lineParts.length == 0) {
                            System.out.println(BLUE + line + RESET);
                        } else {
                            System.out.println(lineParts[0] + BLUE + cli.getPattern() + RESET + ((lineParts.length > 1) ? lineParts[1] : ""));
                        }

                    }
                }
            } catch (IOException e) {
                System.err.println(RED + "Could not read file: " + path.toString() + RESET);
            }
        }

    }
}