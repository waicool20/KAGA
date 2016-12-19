package com.waicool20.kaga.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StreamGobbler {

    private Process process;

    public StreamGobbler(Process process) {
        this.process = process;
    }

    public void run() {
        Thread readOut = new Thread(() -> {
            InputStreamReader stdOut = new InputStreamReader(process.getInputStream());
            BufferedReader stdOutReader = new BufferedReader(stdOut);
            String line = null;
            try {
                while ((line = stdOutReader.readLine()) != null)
                    System.out.println(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Thread readErr = new Thread(() -> {
            InputStreamReader stdErr = new InputStreamReader(process.getErrorStream());
            BufferedReader stdErrReader = new BufferedReader(stdErr);
            String line = null;
            try {
                while ((line = stdErrReader.readLine()) != null)
                    System.out.println(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        readOut.start();
        readErr.start();
    }
}
