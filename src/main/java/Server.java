import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;
/*
 * Clicker: A: I really get it    B: No idea what you are talking about
 * C: kind of following
 */

public class Server {
	int numClients = 0;
	int portNum;
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	ListenerThread listener;
	private Consumer<Serializable> callback;

	Server(int portNum, Consumer<Serializable> call) {
		this.portNum = portNum; 
		callback = call;
		listener = new ListenerThread();
		listener.start();
	}

	public class ListenerThread extends Thread {
		public void run() {
			try (ServerSocket mySocket = new ServerSocket(portNum);) {
				callback.accept("Server is waiting for a client!");

				while (true) {
					ClientThread c = new ClientThread(mySocket.accept(), ++numClients);
					callback.accept("Client #" + numClients + " has connected to server");
					clients.add(c);
					c.start();
				} // End of while
			} catch (Exception e) {
				callback.accept("Server socket did not launch");
				e.printStackTrace();
			}
		}
	}

	class ClientThread extends Thread {
		Socket connection;
		int count;
		ObjectInputStream in;
		ObjectOutputStream out;
		GameState state;
		Dealer dealer;
		GameLogic logic;
		// GameLogic and Dealer go here probably

		ClientThread(Socket s, int count) {
			connection = s;
			this.count = count;
			// gameLogic = new GameLogic();
			dealer = new Dealer();
			logic = new GameLogic();
		}

		public void run() {
			try {
				in = new ObjectInputStream(connection.getInputStream());
				out = new ObjectOutputStream(connection.getOutputStream());
				connection.setTcpNoDelay(true);
			} catch (Exception e) {
				System.out.println("Streams not open");
				e.printStackTrace();
			}

			while (true) {
				try {
					state = (GameState) in.readObject();
					callback.accept("state.phase == " + state.phase);
					
					doPhase(state.phase);
					
				} catch (Exception e) {
					callback.accept("Client #" + count + " has disconnected");
					e.printStackTrace();
					clients.remove(this);
					break;
				}
			}
		}	// end run()

		private void doPhase(String phase) {

			if (phase.equals("dealPlayer")) {
				callback.accept("Player wagers...");
				callback.accept("Ante: $" + String.valueOf(state.ante) + " | Pair+: $" + String.valueOf(state.pairPlus));
				dealPlayerHand();
			} else if (phase.equals("playHand")) {
				playHand();
			}
		}

		private void playHand() {
			// Evaluate Player hand
			int playerRank = -1;
			try {
				playerRank = logic.evalHand(state.playerHand);
				state.pairPlusPayout = logic.pairPlusPayout(playerRank, state.pairPlus);
			} catch (Exception e) {
				callback.accept("Error evaluating Player hand");
				e.printStackTrace();
			}
			if (playerRank < 0) {
				throw new ArithmeticException("Player hand not evaluated");
			}

			try {
				dealDealerHand();
			} catch (Exception e) {
				callback.accept("Error dealing Dealer hand");
				e.printStackTrace();
			}

			// Evaluate Dealer's hand
			int dealerRank = logic.evalHand(state.dealerHand);

			// Must be Queen High or better
			if (dealerRank < 1 && state.dealerHand.get(0).value < 12) {
				state.whoWon = "No one (invalid Dealer hand)";

				state.antePayout = 2*state.ante;
				state.playPayout = 0;
			} else if (dealerRank < playerRank) {
				// Player wins
				state.whoWon = "Player";
				state.antePayout = 2*state.ante;
				state.playPayout = 2*state.ante;
			} else if (dealerRank > playerRank) {
				// Dealer wins
				state.whoWon = "Dealer";
				state.antePayout = 0;
				state.playPayout = 0;
			} else if (dealerRank == playerRank) {
				// Whoever has high card wins
				if (state.dealerHand.get(0).value > state.playerHand.get(0).value) {
					// Dealer has High
					state.whoWon = "Dealer";
					state.antePayout = 0;
					state.playPayout = 0;
				} else {
					state.whoWon = "Player";
					state.antePayout = 2*state.ante;
					state.playPayout = state.antePayout;
				}
			}

			state.currentFunds += (state.antePayout + state.playPayout + state.pairPlusPayout);
			state.phase = "results";
			state.resultString = "Finished hand! Winner: " + state.whoWon + "\nPlayer's hand rank: " + logic.handRanks.get(playerRank) + ". Dealer's hand rank: " + logic.handRanks.get(dealerRank) + ".";


			// send to Client
			try {
				out.writeObject(state);
				callback.accept(state.resultString);
				callback.accept("Payouts\n\tAnte: $" + String.valueOf(state.antePayout) + " | Play: $" + String.valueOf(state.playPayout) + " | Pair+: $" + String.valueOf(state.pairPlusPayout));
			} catch (Exception e) {
				callback.accept("Couldn't send results to client #" + count);
				e.printStackTrace();
			}
			// TODO: Client needs to receive results, display them for Player, then start new hand
		}	// End of playHand

		private void dealPlayerHand() {
			state.phase = "displayPlayer";
			try {
				dealer.shuffleDeck();
				state.playerHand = dealer.dealHand();
				callback.accept("Player's hand: " + state.playerHand.get(0).cardString() + " " + state.playerHand.get(1).cardString() + " " + state.playerHand.get(2).cardString());
			} catch (Exception e) {
				callback.accept("Could not deal hand to client #" + count);
				e.printStackTrace();
			}

			try {
				out.writeObject(state);
				callback.accept("Sent GameState back to client #" + count);
			} catch (Exception e) {
				callback.accept("Could not send GameState to client #" + count);
				e.printStackTrace();
			}
		}

		private void dealDealerHand() {
			//dealer.shuffleDeck();
			state.dealerHand = dealer.dealHand();
			
			callback.accept("Dealer's hand: " + state.dealerHand.get(0).cardString() + " " + state.dealerHand.get(1).cardString() + " " + state.dealerHand.get(2).cardString());
		}
	}

}