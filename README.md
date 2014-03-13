ecg-dsp-tool
============

This is a tool to demonstrate processing of ECG signals to perform QRS or pulse detection.

An IntelliJ IDEA project file is included, as well as Ant build files to get the project running.

Building
--------

To build the project with Ant, switch to the project directory and use:

    ant -f ecg-dsp-tool.xml

A .jar file should be generated in ./out/artifacts which you can run with:

    java -jar out/artifacts/ecg_dsp_tool_jar/ecg-dsp-tool.jar

