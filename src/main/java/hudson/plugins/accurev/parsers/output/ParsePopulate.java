package hudson.plugins.accurev.parsers.output;

import hudson.plugins.accurev.AccurevLauncher.ICmdOutputParser;
import hudson.plugins.accurev.AccurevLauncher.UnhandledAccurevCommandOutput;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Filters the output of the populate command and just shows a summary of the
 * output. Helps prevent build logs being clogged up with the checkout.
 */
public final class ParsePopulate implements ICmdOutputParser<Boolean, OutputStream> {
    public Boolean parse(InputStream cmdOutput, OutputStream streamToCopyOutputTo)
            throws UnhandledAccurevCommandOutput, IOException {
        final String lineStartDirectory = "Creating dir:";
        final String lineStartElement = "Populating element";
        int countOfDirectories = 0;
        int countOfElements = 0;
        final Reader stringReader = new InputStreamReader(cmdOutput, Charset.defaultCharset());
        final Writer stringWriter = new OutputStreamWriter(streamToCopyOutputTo, Charset.defaultCharset());
        final BufferedWriter lineWriter = new BufferedWriter(stringWriter);
        try (BufferedReader reader = new BufferedReader(stringReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(lineStartElement)) {
                    countOfElements++;
                } else if (line.startsWith(lineStartDirectory)) {
                    countOfDirectories++;
                } else {
                    lineWriter.write(line);
                    lineWriter.newLine();
                }
            }
            final String msg = "Populated " + countOfElements + " elements and " + countOfDirectories + " directories.";
            streamToCopyOutputTo.write(msg.getBytes(Charset.defaultCharset()));
        } finally {
            lineWriter.flush();
        }
        return Boolean.TRUE;
    }
}
