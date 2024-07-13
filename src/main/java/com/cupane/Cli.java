package com.cupane;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.cli.CommandLine;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Cli {
    String pattern;
    List<Path> paths;

    public Cli(List<String> args) {
                pattern = args.get(0);
                paths = new ArrayList<>();
                args.stream()
                        .skip(1)
                        .forEach(path -> paths.add(FileSystems.getDefault().getPath(path)));

//                paths.forEach(
//                        path -> System.out.println(path.toAbsolutePath().toString())
//                );
    }
}
