import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

import javafx.application.Platform;



public class Client extends Thread{
	Socket socketClient;
	ObjectOutputStream out;
	ObjectInputStream in;
	int port;

	private Consumer<Serializable> callback;
	
	Gui gui;
	GameState state;

	Client(Gui gui, int portNum, Consumer<Serializable> call) {
		port = portNum;
		this.gui = gui;
		callback = call;
		state = new GameState();
	}

	public void run() {
		// Declare socket and I/O streams
		try {
			socketClient = new Socket("127.0.0.1", port);
			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());
			socketClient.setTcpNoDelay(true);
		} catch (Exception e) {
			System.out.println("Client socket did not launch");
			e.printStackTrace();
		}
		
		while (true) {
			// Receive hand. Display it
			try {
				state = (GameState) in.readObject();

				doPhase(state.phase);
				
			} catch (Exception e) {
				System.out.println("Could not display hand");
				e.printStackTrace();
				break;
			}
		}
	}

	private void doPhase(String phase) {
		if (phase.equals("displayPlayer")) {
			displayHand();
		} else if (phase.equals("results")) {
			displayResults();
		}
	}

	public void sendWagers(int pairPlus, int ante) {
		state.phase = "dealPlayer";
		state.pairPlus = pairPlus;
		state.ante = ante;
		state.currentFunds -= (pairPlus + ante);
		try {
			out.writeObject(state);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void displayHand() {
		Platform.runLater(() -> {
			gui.updateClientMessage("Fold or play!");
			gui.updateClientUI(state);
		});
	}

	public void playHand() {
		state.phase = "playHand";
		state.currentFunds -= (state.ante);
		try {
			out.writeObject(state);
		} catch (Exception e) {
			System.out.println("Error playing hand");
			e.printStackTrace();
		}
	}

	public void newHand() {
		state.phase = "newHand";
		gui.updateClientUI(state);
	}

	private void displayResults() {
		gui.updateClientUI(state);
	}
}
