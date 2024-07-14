package com.cupane;

import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {

        String RESET = "\u001B[0m";
        String BLUE = "\u001B[36m";
        String RED = "\u001B[31m";


        Options options = new Options();

        Option help = new Option("h", "help", false, "print this message");

        options.addOption(help);

        CommandLineParser parser = new DefaultParser();

//        args = new String[]{"Pippo", "fileozzo"};
        Cli cli = null;
        try {
            CommandLine commandLine = parser.parse(options, args);
            if (commandLine.getArgList().isEmpty()) throw new ParseException("No arguments given");
            if (commandLine.getArgList().size() < 2) throw new ParseException("Two arguments must be given");

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
                    if (line.contains(cli.getPattern())) System.out.println(lineNumber + " " + BLUE + line + RESET);
                }
            } catch (IOException e) {
                System.err.println(RED + "Could not read file: " + path.toString() + RESET);
            }
        }

    }
}