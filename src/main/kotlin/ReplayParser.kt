import com.google.gson.stream.JsonReader
import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecutor
import org.apache.commons.exec.ExecuteException
import org.apache.commons.exec.PumpStreamHandler
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException

class ReplayParser() {
    val types = arrayOf("gameevents", "messageevents", "trackerevents", "attributeevents", "header", "details", "initdata", "stats");
    /**
     *Uses the heroprotocol python library to parse the replay file and write it to file
     * - NOTE: In order to use this you MUST have a valid python v2.7 installation located in your PATH
     *@param replay the replay File
     *@param type what kind of parse to perform
     *@param output the stream that the JSON will be written to
     *@return empty string if success,  otherwise error information
     */
    fun parse(replay: File, type: String, output: FileOutputStream): String {
        var isValidType = false;
        for (ptype in types) {
            if (ptype.equals(type)) {
                isValidType = true;
                break;
            }
        }
        if (!isValidType) {
            return "Incorrect Type Parameter";
        }
        val script = File("dependencies\\heroprotocol\\heroprotocol.py");
        var cmdLine = CommandLine("python");
        cmdLine.addArgument(script.absolutePath);
        cmdLine.addArgument("--$type");
        cmdLine.addArgument(replay.absolutePath);
        var executor = DefaultExecutor();
        executor.streamHandler = PumpStreamHandler(output);
        executor.setExitValue(0);
        try {
            executor.execute(cmdLine);
            output.flush();
            return "";

        } catch(x: ExecuteException) {
            output.flush();
            return x.message!!;
        }
    }
}

fun main(args: Array<String>) {
    try {
        val folder = File("replays");
        var count = 0;
        val files = folder.listFiles();
        val rp = ReplayParser();
        for (file in files) {
            val out = FileOutputStream("replayJSONs\\output$count.txt");
            println(rp.parse(file, "trackerevents", out));
            out.close();
            count++;
        }
    } catch(x: IOException) {
        println(x.message);
    }
    var input = JsonReader(FileReader("output.txt"));
    while (input.hasNext()) {
        try {
            input.beginObject();
            input.isLenient = true;
            while (input.hasNext()) {
                val name = input.nextName();
                //System.out.print(name + " | ");
                if (name.equals("m_eventName")) {
                    val event = input.nextString();
                    if (event.equals("PlayerSpawned")) {
                        input.nextName();
                        input.skipValue();
                        input.nextName();
                        input.beginArray();
                        input.beginObject();
                        input.nextName();
                        input.skipValue();
                        input.nextName();
                        val playerId = input.nextInt();
                        input.endObject();
                        input.endArray();
                        input.nextName();
                        input.beginArray();
                        input.beginObject();
                        input.nextName();
                        input.skipValue();
                        input.nextName();
                        println(event + " | " + playerId + " | " + input.nextString());
                        input.endObject();
                        input.endArray();
                    }
                } else {
                    //System.out.println();
                    input.skipValue();
                }
            }
            //System.out.println("\n");
            input.endObject();
        } catch (y: IllegalStateException) {
            input.close();
            break;
        }
    }
}