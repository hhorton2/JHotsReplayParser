import com.google.gson.stream.JsonReader
import java.io.*

fun main(args: Array<String>) {
    var s = "";
    try {
        val folder = File("replays");
        val jython = File("dependencies\\jython-standalone-2.7.0.jar");
        val script = File("dependencies\\heroprotocol\\heroprotocol.py");
        val output = File("output.txt")
        for(file in folder.listFiles()){
            println("java -jar ${jython.absolutePath} ${script.absolutePath} --trackerevents \"${file.absolutePath}\"");
            var p = Runtime.getRuntime().exec("java -jar ${jython.absolutePath} ${script.absolutePath} --trackerevents \"${file.absolutePath}\"");

            var stdInput = BufferedReader(
                    InputStreamReader(p.inputStream));

            var stdError = BufferedReader(
                    InputStreamReader(p.errorStream));

            // read the output from the command
            println("Here is the standard output of the command:\n");
            stdInput.forEachLine {
                println(s);
            }

            // read any errors from the attempted command
            println("Here is the standard error of the command (if any):\n");
            stdError.forEachLine {
                println(s);
            }
        }

    } catch(x: IOException) {
        println(x.message);
    }
    var input = JsonReader(FileReader("output.txt"));
    while (input.hasNext()) {
        try {
            input.beginObject();
            input.setLenient(true);
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
                        System.out.println(event + " | " + playerId + " | " + input.nextString());
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