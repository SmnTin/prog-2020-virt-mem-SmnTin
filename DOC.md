# Documentation
## Building and execution
The project uses Gradle as a build system.
You can build a __jar__ file with __shadowJar__ task.
Or execute the code with __runJar__ task
inside your IDE or in the terminal with __gradlew__.

## Usage
### Analysis mode
Main mode that performs analysis of each cache strategy with given data.

You need to specify input file containing sequence of pages indices. You also have to specify the output file.

These files are specified with flags alongside other parameters:
```
-if Input file
-of Output file
-m Number of frames
```
For more detailed description consult `--help`.

Consider following example as a quick start:
```bash
java -jar runnable-1.0-SNAPSHOT.jar analysis -m 50 -if input.txt -of output.txt
```

### Generator mode
A useful tool for quick tests. Generates sequence of pages of specified length.

Every parameter is specified via flag.
```
-n Number of different pages
-l Length of result sequence
-of Output file
```

Consider following example as a quick start:
```bash
java -jar runnable-1.0-SNAPSHOT.jar generate -n 500 -l 1000 -of input.txt
```

## Files format description
Examples of input and output files can be found in the ```data``` subdirectory.

### Input file
Put the sequence of pages indices in one line. The pages are 0-indexed.
```
0 4 3 5 2...
```

If you split the sequence in several lines they are considered as
several tests each of which will be analyzed separately, and the output 
will be written to a separate file. Each output file ends with 
`_<number of line>` suffix added to the original filename.

### Output file
The result is a list of pairs on a separate line each in the following format.
```
<cache name> <number of pages replacements>
```
The pairs are sorted in the descending order by the number.