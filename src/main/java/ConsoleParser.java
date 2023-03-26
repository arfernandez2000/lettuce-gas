import org.apache.commons.cli.*;

/**
 * Parsea los argumentos obtenidos de la consola.
 * Source: <a href="https://opensource.com/article/21/8/java-commons-cli">Parse command options in Java with commons-cli</a>
 */
public class ConsoleParser {

    public static String staticFile;
    public static String dynamicFile;

    private static Options createOptions(){
        Options options = new Options();

        Option staticFile = Option.builder("staticFile")
                .hasArg()
                .required(true)
                .desc("Path al archivo de valores estáticos").build();
        options.addOption(staticFile);

        Option dynamicFile = Option.builder("dynamicFile")
                .hasArg()
                .required(true)
                .desc("Path al archivo de valores dinámicos").build();
        options.addOption(dynamicFile);

        options.addOption("H",true, "Cantidad de celdas horizontales");
        options.addOption("V",true, "Cantidad de celdas verticales");
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
            }
            if (cmd.hasOption("V")) {
                properties.setV(Integer.parseInt(cmd.getOptionValue("V")));
            }
            dynamicFile = cmd.getOptionValue("dynamicFile");
            staticFile = cmd.getOptionValue("staticFile");

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            helper.printHelp("Modo de uso:", options);
            System.exit(0);
        }
    }
}

