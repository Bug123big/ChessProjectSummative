package AI;

import GameRole.Move;
import java.io.*;

public class ChessAI {

    private Process process;
    private BufferedReader reader;
    private BufferedWriter writer;

    public ChessAI(String stockfishPath) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(stockfishPath);
        process = builder.start();

        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

        sendCommand("uci");
        waitFor("uciok");

        sendCommand("isready");
        waitFor("readyok");
    }

    private void sendCommand(String command) throws IOException {
        writer.write(command);
        writer.newLine();
        writer.flush();
    }

    private void waitFor(String target) throws IOException {
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.equals(target)) {
                break;
            }
        }
    }

    public Move getBestMove(String fen) throws IOException {
        sendCommand("position fen " + fen);
        sendCommand("go depth 10");

        String line;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("bestmove")) {
                String uciMove = line.split(" ")[1];
                return convertUciToMove(uciMove);
            }
        }

        return null;
    }

    private Move convertUciToMove(String uci) {
        int fromCol = uci.charAt(0) - 'a';
        int fromRow = 8 - Character.getNumericValue(uci.charAt(1));

        int toCol = uci.charAt(2) - 'a';
        int toRow = 8 - Character.getNumericValue(uci.charAt(3));

        return new Move(fromRow, fromCol, toRow, toCol);
    }

    public void close() throws IOException {
        sendCommand("quit");
        process.destroy();
    }
}
