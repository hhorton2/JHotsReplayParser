import com.google.gson.stream.JsonReader
import org.python.core.PyInteger
import org.python.core.PyString
import org.python.core.PySystemState
import org.python.util.PythonInterpreter
import java.io.*

fun main(args: Array<String>) {
    var s = "";
    try {
        val folder = File("replays");
        val script = File("dependencies\\heroprotocol\\heroprotocol.py");
        val lib = File("dependencies\\heroprotocol\\mpyq")
        val output = File("output.txt")
        for (file in folder.listFiles()) {
            var state = PySystemState();
            state.argv.clear ();
            state.argv.append (PyString ("--trackerevents"));
            state.argv.append (PyString (file.absolutePath));
            val interpreter = PythonInterpreter(null, state);
            interpreter.execfile(script.absolutePath);
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