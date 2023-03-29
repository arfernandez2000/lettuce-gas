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

        options.addOption("H",true, "Cantidad de celdas horizontales");
        options.addOption("V",true, "Cantidad de celdas verticales");
        options.addOption("D",true, "Tamaño de la rejilla");
        return options;
    }



    public static void parseArguments(String[] args, Properties properties) throws IllegalArgumentException {
        Options options = createOptions();
        CommandLine cmd;
        CommandLineParser parser = new BasicParser();
        HelpFormatter helper = new HelpFormatter();

        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("H")) {
                properties.setH(Integer.parseInt(cmd.getOptionValue("H")));
            } else
                properties.setH(200);
            if (cmd.hasOption("V")) {
                properties.setV(Integer.parseInt(cmd.getOptionValue("V")));
            } else
                properties.setV(200);
            if (cmd.hasOption("D")) {
                properties.setV(Integer.parseInt(cmd.getOptionValue("D")));
            } else
                properties.setD(50);
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

        CommandLine cmd;
        CommandLineParser parser = new BasicParser();
        HelpFormatter helper = new HelpFormatter();

        try {
            cmd = parser.parse(options, args);
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

