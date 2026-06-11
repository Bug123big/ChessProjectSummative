package AI;

import GameRole.Move;
import java.io.*;

public class ChessAI {

    private Process process;
    private BufferedReader reader;
    private BufferedWriter writer;
    private final Object engineLock = new Object();

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
        if (process == null || !process.isAlive()) {
            throw new IOException("Stockfish process is not running.");
        }

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

    public void setSkillLevel(int level) throws IOException {
        int skill;

        switch (level) {
            case 1:
                skill = 0;
                break;
            case 2:
                skill = 1;
                break;
            case 3:
                skill = 2;
                break;
            case 4:
                skill = 4;
                break;
            case 5:
                skill = 6;
                break;
            case 6:
                skill = 8;
                break;
            case 7:
                skill = 10;
                break;
            case 8:
                skill = 13;
                break;
            case 9:
                skill = 16;
                break;
            case 10:
                skill = 20;
                break;
            default:
                skill = 5;
        }

        sendCommand("setoption name Skill Level value " + skill);
        sendCommand("isready");
        waitFor("readyok");
    }

    public Move getBestMove(String fen, int moveTime) throws IOException {
        synchronized (engineLock) {
            sendCommand("stop");
            sendCommand("isready");
            waitFor("readyok");

            sendCommand("position fen " + fen);
            sendCommand("go movetime " + moveTime);

            String line;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("bestmove")) {
                    String[] parts = line.split(" ");

                    if (parts.length < 2 || parts[1].equals("(none)")) {
                        return null;
                    }

                    return convertUciToMove(parts[1]);
                }
            }

            return null;
        }
    }

    private Move convertUciToMove(String uci) {
        int fromCol = uci.charAt(0) - 'a';
        int fromRow = 8 - Character.getNumericValue(uci.charAt(1));

        int toCol = uci.charAt(2) - 'a';
        int toRow = 8 - Character.getNumericValue(uci.charAt(3));

        return new Move(fromRow, fromCol, toRow, toCol);
    }

    public String analyzePosition(String fen, int moveTime) throws IOException {
        synchronized (engineLock) {
            sendCommand("stop");
            sendCommand("isready");
            waitFor("readyok");

            sendCommand("position fen " + fen);
            sendCommand("go movetime " + moveTime);

            String line;
            String bestMove = "";
            String lastScore = "No score";

            while ((line = reader.readLine()) != null) {
                if (line.contains(" score cp ")) {
                    String[] parts = line.split(" ");
                    for (int i = 0; i < parts.length - 2; i++) {
                        if (parts[i].equals("score") && parts[i + 1].equals("cp")) {
                            int cp = Integer.parseInt(parts[i + 2]);
                            lastScore = "Evaluation: " + (cp / 100.0);
                        }
                    }
                }

                if (line.contains(" score mate ")) {
                    String[] parts = line.split(" ");
                    for (int i = 0; i < parts.length - 2; i++) {
                        if (parts[i].equals("score") && parts[i + 1].equals("mate")) {
                            lastScore = "Mate in " + parts[i + 2];
                        }
                    }
                }

                if (line.startsWith("bestmove")) {
                    String[] parts = line.split(" ");
                    if (parts.length >= 2) {
                        bestMove = parts[1];
                    }
                    break;
                }
            }

            return lastScore + "\nBest Move: " + bestMove;
        }
    }

    public void close() throws IOException {
        sendCommand("quit");
        process.destroy();
    }

    public void setChess960(boolean enabled) throws IOException {
        sendCommand("setoption name UCI_Chess960 value " + (enabled ? "true" : "false"));
        sendCommand("isready");
        waitFor("readyok");
    }
}
