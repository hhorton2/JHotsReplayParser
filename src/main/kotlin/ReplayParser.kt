import com.google.gson.stream.JsonReader
import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecutor
import org.apache.commons.exec.ExecuteException
import org.apache.commons.exec.PumpStreamHandler
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException

fun main(args: Array<String>) {
    var s = "";
    try {
        val folder = File("replays");
        val script = File("dependencies\\heroprotocol\\heroprotocol.py");
        var count = 0;
        val files = folder.listFiles();
        for(file in files){
            println(files.size)
            try {
                println("Loop #$count")
                val output = File("replayJSONs\\output$count.txt")
                var cmdLine = CommandLine("python");
                cmdLine.addArgument(script.absolutePath);
                cmdLine.addArgument("--trackerevents");
                cmdLine.addArgument(file.absolutePath);
                var out = FileOutputStream(output);
                var executor = DefaultExecutor();
                executor.streamHandler = PumpStreamHandler(out);
                executor.setExitValue(0);
                try {
                    var exitValue = executor.execute(cmdLine);
                    println("Something Went Right")
                    println(exitValue);
                    out.flush();
                    out.close();

                }catch(x: ExecuteException){
                    println("Something Failed")
                    out.flush();
                    out.close();
                }
            } finally {
                count++;
            }
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