package utils;

import org.apache.commons.cli.*;

/**
 * Parsea los argumentos obtenidos de la consola.
 * Source: <a href="https://opensource.com/article/21/8/java-commons-cli">Parse command options in Java with commons-cli</a>
 */
public class ConsoleParser {

    public static String dynamicFile;

    private static Options createOptions(){
        Options options = new Options();

        Option staticFile = Option.builder("N")
                .hasArg()
                .required(true)
                .desc("Cantidad de partículas").build();
        options.addOption(staticFile);

        Option dynamicFile = Option.builder("dynamicFile")
                .hasArg()
                .required(true)
                .desc("Path al archivo de valores dinámicos").build();
        options.addOption(dynamicFile);

        options.addOption("D",true, "Tamaño de la rejilla");
        options.addOption("out",true, "Nombre del archivo de salida");
        options.addOption("density",false, "Imprime la densidad de partículas en cada iteración");
        options.addOption("runs",true, "Cantidad de corridas");
        return options;
    }



    public static void parseArguments(String[] args, Properties properties) throws IllegalArgumentException {
        Options options = createOptions();
        CommandLine cmd;
        CommandLineParser parser = new BasicParser();
        HelpFormatter helper = new HelpFormatter();

        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("D")) {
                properties.setD(Integer.parseInt(cmd.getOptionValue("D")));
            } else
                properties.setD(50);
            if (cmd.hasOption("out")) {
                properties.setOutFileName(cmd.getOptionValue("out"));
            } else
                properties.setOutFileName("output.txt");

            if (cmd.hasOption("runs")) {
                properties.setRuns(Integer.parseInt(cmd.getOptionValue("runs")));
            } else
                properties.setRuns(1);

            properties.setPrintDensity(cmd.hasOption("density"));

            dynamicFile = cmd.getOptionValue("dynamicFile");
            properties.setN(Integer.parseInt(cmd.getOptionValue("N")));

        } catch (ParseException e) {
            printHelp(helper, e, options);
        }
    }

    public static int parseArgumentsFileGenerator(String[] args) throws IllegalArgumentException {

        Options options = new Options();
        Option dynamicFile = Option.builder("N")
                .hasArg()
                .required(true)
                .desc("Cantidad de partículas").build();
        options.addOption(dynamicFile);

        options.addOption("out",true, "Nombre del archivo de salida");

        CommandLine cmd;
        CommandLineParser parser = new BasicParser();
        HelpFormatter helper = new HelpFormatter();

        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("out")) {
                FileGenerator.outFileName = cmd.getOptionValue("out");
            } else
                FileGenerator.outFileName = "dynamicInput.txt";
            return Integer.parseInt(cmd.getOptionValue("N"));
        } catch (ParseException e) {
            printHelp(helper, e, options);
        }

        return 0;
    }

    static void printHelp(HelpFormatter helper, ParseException e, Options options) {
        System.out.println(e.getMessage());
        helper.printHelp("Modo de uso:", options);
        System.exit(0);
    }


}

