
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class Gui extends Application {
	// UI elements and containers
	ArrayList<ImageView> dealerViewList, playerViewList;
	Button serverButton, clientButton, serverStartButton, clientStartButton, foldBtn, playBtn, dealBtn, newHandBtn;
	GridPane selectGrid, wagerGrid;
	HBox selectBox, dealerCardBox, playerCardBox, playerRow;
	ListView<String> serverLog;
	Spinner<Integer> pairPlusSpinner, anteSpinner;
	TextField clientPortField, serverPortField, pWagerField;
	Text winningsText, messageText;
	VBox playerHandArea;

	HashMap<String, Scene> sceneMap;

	Server serverConnection;
	Client clientConnection;



	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Three Card Poker!");

		sceneMap = new HashMap<>();

		sceneMap.put("select", createSelectScene());
		sceneMap.put("serverPort", createServerPortScene());
		sceneMap.put("clientPort", createClientPortScene());
		sceneMap.put("serverLog", createLogScene());
		sceneMap.put("playScene", createPlayScene());

		serverButton.setOnAction(e -> {
			primaryStage.setScene(sceneMap.get("serverPort"));
			primaryStage.setTitle("Server Port Selection");
		});

		clientButton.setOnAction(e -> {
			primaryStage.setScene(sceneMap.get("clientPort"));
			primaryStage.setTitle("Client Port Selection");
		});

		serverStartButton.setOnAction(e -> {
			primaryStage.setScene(sceneMap.get("serverLog"));
			primaryStage.setTitle("Server Log");
			
			serverConnection = new Server(Integer.parseInt(serverPortField.getText()), data -> {
				Platform.runLater(() -> {
					// serverLog.getItems().add(data.toString());
					updateServerUI(data.toString());
				});
			});
		});

		clientStartButton.setOnAction(e -> {
			primaryStage.setScene(sceneMap.get("playScene"));
			primaryStage.setTitle("Three Card Poker");

			clientConnection = new Client(this, Integer.parseInt(clientPortField.getText()), data -> {
				Platform.runLater(() -> {
					serverLog.getItems().add(data.toString());
				});
			});
			clientConnection.start();
		});

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});

		primaryStage.setScene(sceneMap.get("select"));
		primaryStage.show();

	}

	public Scene createSelectScene() {

		serverButton = new Button("Server");
		serverButton.setPrefSize(100, 120);

		clientButton = new Button("Client");
		clientButton.setPrefSize(100, 120);
		
		selectGrid = new GridPane();
		
		ColumnConstraints col0, col1, col2, col3;
		RowConstraints rowCon = new RowConstraints(250);

		col0 = new ColumnConstraints();
		col1 = new ColumnConstraints();
		col2 = new ColumnConstraints();
		col3 = new ColumnConstraints();
		col0.setPercentWidth(10);
		col1.setPercentWidth(40);
		col2.setPercentWidth(40);
		col3.setPercentWidth(10);

		selectGrid.getRowConstraints().add(rowCon);

		selectGrid.add(serverButton, 1, 0);
		selectGrid.add(clientButton, 2, 0);
		selectGrid.setAlignment(Pos.CENTER);
		GridPane.setMargin(serverButton, new Insets(50));
		GridPane.setMargin(clientButton, new Insets(50));

		selectBox = new HBox(selectGrid);
		selectBox.setAlignment(Pos.CENTER);

		return new Scene(selectBox, 800, 600);
	}

	public Scene createServerPortScene() {
		Text portText = new Text("Port Number");
		portText.setStyle("-fx-font-size: 36");

		serverPortField = new TextField();
		serverPortField.setPrefSize(150, 50);
		serverPortField.setMaxWidth(300);

		serverStartButton = new Button("Start Server");
		serverStartButton.setDefaultButton(true);
		serverStartButton.setPrefSize(150, 50);
		serverStartButton.setFont(new Font(20));
		
		VBox root = new VBox(10, portText, serverPortField, serverStartButton);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(0, 0, 0, 0));
		VBox.setMargin(serverStartButton, new Insets(62));
		root.setStyle("-fx-background-color: #f9707b");

		return new Scene(root, 400, 600);
	}

	public Scene createClientPortScene() {
		Label portLabel = new Label("Port Number");
		portLabel.setStyle("-fx-font-size: 36");

		clientPortField = new TextField();
		clientPortField.setPrefSize(150, 50);
		clientPortField.setMaxWidth(300);

		clientStartButton = new Button("Join Server");
		clientStartButton.setDefaultButton(true);
		clientStartButton.setPrefSize(150, 50);
		clientStartButton.setFont(new Font(20));
		
		VBox root = new VBox(10, portLabel, clientPortField, clientStartButton);
		root.setAlignment(Pos.CENTER);
		//root.setPadding(new Insets(0, 0, 0, 0));
		VBox.setMargin(clientStartButton, new Insets(62));
		//root.setStyle("-fx-background-color: #95FFD2");

		Scene scene = new Scene(root, 800, 600);
		scene.getStylesheets().add(getClass().getResource("/casino-style.css").toExternalForm());
		return scene;
	}
	
	public Scene createLogScene() {
		Text clientsConnectedText = new Text("Clients connected: ");
		// TODO: Display/update client count
		Button endServerButton = new Button("End Server");

		HBox topBox = new HBox(clientsConnectedText, endServerButton);
		// TODO: ListView is short. Fix that
		serverLog = new ListView<String>();

		VBox root = new VBox(topBox, serverLog);

		return new Scene(root, 800, 600);
	}

	public GridPane createwagerGrid() {
		GridPane grid;
		VBox anteBox, pairPlusBox;
		Text anteText, pairPlusText;

		// CSS Style strings
		// String boxStyle = "-fx-border-color: #000; -fx-border-width: 5";
		String textStyle = "-fx-font-size: 20; -fx-font-family: serif; -fx-text-fill: #f5e7c0;";	
		Color textColor = new Color(0.96, 0.906, 0.75, 1);

		// Ante box
		anteText = new Text("Ante/Play");
		anteText.setTextAlignment(TextAlignment.CENTER);
		anteText.setStyle(textStyle);
		anteText.setFill(textColor);

		anteSpinner = new Spinner<>(50, 10000, 50, 10);
		VBox.setMargin(anteSpinner, new Insets(0, 10, 0, 10));

		anteBox = new VBox(20, anteText, anteSpinner);
		anteBox.setAlignment(Pos.CENTER);
		anteBox.setPrefSize(120, 100);
		// anteBox.setStyle(boxStyle);

		// Pair+ box
		pairPlusText = new Text("Pair+");
		pairPlusText.setTextAlignment(TextAlignment.CENTER);
		pairPlusText.setStyle(textStyle);
		pairPlusText.setFill(textColor);

		pairPlusSpinner = new Spinner<>(0, 10000, 0, 10);
		// VBox.setMargin(pairPlusSpinner, new Insets(0, 10, 0, 10));

		pairPlusBox = new VBox(20, pairPlusText, pairPlusSpinner);
		pairPlusBox.setAlignment(Pos.CENTER);
		pairPlusBox.setPrefSize(120, 100);
		// pairPlusBox.setStyle(boxStyle);

		// Deal button
		dealBtn = new Button("Deal");
		dealBtn.setPrefSize(218, 100);
		dealBtn.setFont(new Font(20));

		dealBtn.setOnAction(e -> {
			dealBtn.setDisable(true);
			anteSpinner.setDisable(true);
			pairPlusSpinner.setDisable(true);
			clientConnection.sendWagers(pairPlusSpinner.getValue(), anteSpinner.getValue());
		});

		// New Hand button
		newHandBtn = new Button("New Hand");
		newHandBtn.setPrefSize(218, 100);
		newHandBtn.setFont(new Font(20));

		newHandBtn.setOnAction(e -> {
			clientConnection.newHand();
		});

		// Fold button
		foldBtn = new Button("Fold");
		foldBtn.setPrefSize(109, 100);
		foldBtn.setFont(new Font(20));
		foldBtn.setDisable(true);

		foldBtn.setOnAction(e -> {
			clientConnection.newHand();
		});

		// Play button
		playBtn = new Button("Play");
		playBtn.setPrefSize(109, 100);
		playBtn.setFont(new Font(20));
		playBtn.setDisable(true);

		playBtn.setOnAction(e -> {
			foldBtn.setDisable(true);
			playBtn.setDisable(true);
			clientConnection.playHand();
		});

		// Put em in a grid
		// TODO: not centered or aligned
		grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.addRow(0, anteBox, pairPlusBox);
		grid.add(dealBtn, 0, 1, 2, 1);
		grid.addRow(2, foldBtn, playBtn);
		grid.setStyle("-fx-border-color: #f5e7c0; -fx-border-width: 1px;");

		return grid;
	}

	public Scene createPlayScene() {
		
		// Menu bar stuff
		MenuItem exit = new MenuItem("Exit");
		Menu options = new Menu("Options");
		MenuBar menuBar = new MenuBar();

		Label dealerHandLabel, playerHandLabel;
		VBox dealerArea, messageBox;

		Color textColor = new Color(0.96, 0.906, 0.75, 1);
		
		// UI elements that will need to change
		// ImageView cardBack = new ImageView("cardBack.jpg");
		// cardBack.setPreserveRatio(true);
		// cardBack.setFitWidth(90);
		// cardBack.setSmooth(true);

		exit.setOnAction(e -> Platform.exit());

		options.getItems().add(exit);
		menuBar.getMenus().add(options);

		// Dealer hand area
		dealerArea = new VBox(8);

		dealerHandLabel = new Label("Dealer Hand");
		dealerHandLabel.setId("handLabel");
		dealerHandLabel.setTextAlignment(TextAlignment.CENTER);
		dealerHandLabel.setStyle("-fx-font-size: 48px");

		/* dealerHandText = new Text("Dealer Hand");
		dealerHandText.setStyle("-fx-font-size: 64");
		dealerHandText.setFill(textColor);
		dealerHandText.setTextAlignment(TextAlignment.CENTER); */

		// Container for Dealer's card images
		dealerViewList = new ArrayList<>(){
			{
				add(new ImageView("cardBack.jpg"));
				add(new ImageView("cardBack.jpg"));
				add(new ImageView("cardBack.jpg"));
			}
		};

		for (ImageView im : dealerViewList) {
			im.setPreserveRatio(true);
			im.setFitWidth(90);
			im.setSmooth(true);
		}

		dealerCardBox = new HBox(30);
		dealerCardBox.setAlignment(Pos.CENTER);
		dealerCardBox.getChildren().addAll(dealerViewList);

		displayDealerHand(null);

		dealerArea.getChildren().addAll(dealerHandLabel, dealerCardBox);
		dealerArea.setAlignment(Pos.CENTER);

		// Player Area - contains wager boxes, winning box, 
		wagerGrid = createwagerGrid();

		// Player hand
		playerHandArea = new VBox(8);
		playerHandArea.setAlignment(Pos.CENTER);

		playerHandLabel = new Label("Player Hand");
		playerHandLabel.setId("handLabel");
		playerHandLabel.setTextAlignment(TextAlignment.CENTER);
		
		// Hand List
		playerViewList = new ArrayList<>() {
			{
				add(new ImageView());
				add(new ImageView());
				add(new ImageView());
			}
		};
		
		for (ImageView im : playerViewList) {
			im.setPreserveRatio(true);
			im.setFitWidth(90);
			im.setSmooth(true);
		}
		
		playerCardBox = new HBox(30);
		playerCardBox.setAlignment(Pos.CENTER);
		playerCardBox.getChildren().addAll(playerViewList);
		
		winningsText = new Text("$1000");
		winningsText.setStyle("-fx-font-size: 32");
		winningsText.setFill(textColor);
		winningsText.setTextAlignment(TextAlignment.CENTER);
		
		displayPlayerHand(null);

		playerHandArea.getChildren().addAll(playerHandLabel, playerCardBox, winningsText);

		playerRow = new HBox(50, wagerGrid, playerHandArea);
		playerRow.setAlignment(Pos.CENTER);


		// Message area
		messageText = new Text("Welcome to Three Card Poker!\nTo start, place your ante wager and optional pair-plus wager, then click Deal.");
		messageText.setFill(textColor);
		messageText.setTextAlignment(TextAlignment.CENTER);
		
		messageBox = new VBox(messageText);
		messageBox.setId("messageBar");
		messageBox.setPrefSize(680, 130);
		messageBox.setAlignment(Pos.CENTER);

		VBox root = new VBox(20);
		root.getChildren().addAll(menuBar, dealerArea, messageBox, playerRow);

		Scene scene = new Scene(root, 800, 800);
		scene.getStylesheets().add(getClass().getResource("/casino-style.css").toExternalForm());
		return scene;
	}

	// Update functions
	void updateServerUI(Serializable data) {
		// TODO: this will eventually need to work with the data packet we send back and forth, and so will need to be changed
		// For now, just add data to log
		serverLog.getItems().add(data.toString());
	}

	// Updates Client thread's UI as needed, depending on the phase of the game.
	void updateClientUI(Serializable data) {
		GameState state = (GameState) data;

		winningsText.setText("$" + String.valueOf(state.currentFunds));
		if (state.phase.equals("newHand")) {
			//displayPlayerHand(null);
			//displayDealerHand(null);
			//anteSpinner = new Spinner<>(0, state.currentFunds, 50, 10);
			//pairPlusSpinner = new Spinner<>(0, state.currentFunds, 0, 10);
			//wagerGrid.add(dealBtn, 0, 1, 2, 1);

			wagerGrid = createwagerGrid();
			playerRow.getChildren().clear();
			playerRow.getChildren().addAll(wagerGrid, playerHandArea);
			displayPlayerHand(null);
			displayDealerHand(null);

			messageText.setText("New hand!");
		}

		else if (state.phase.equals("displayPlayer")) {
			messageText.setText("Fold, or bet a Play wager equal to your ante to play the hand out");
			displayPlayerHand(state.playerHand);
			foldBtn.setDisable(false);
			playBtn.setDisable(false);
		}

		else if (state.phase.equals("results")) {
			displayDealerHand(state.dealerHand);
			messageText.setText(state.resultString + "\nPayouts\nAnte: $" + String.valueOf(state.antePayout) + " | Play: $" + String.valueOf(state.playPayout) + " | Pair+: $" + String.valueOf(state.pairPlusPayout));

			wagerGrid.add(newHandBtn, 0, 1, 2, 1);
		}
		
		// TODO: Update Winnings text
	}

	void updateClientMessage(Serializable data) {
		messageText.setText(data.toString());
	}

	private Image getCardImage(Card card) {
		String url = "PNG-cards-1.3/" + card.cardString() + ".png";
		
		try {
			return new Image(url);
		} catch (Exception e) {
			System.out.println("Could not get card image at URL '" + url + "'.");
			e.printStackTrace();
		}

		return new Image("cardBack.jpg");
	}

	void displayPlayerHand(ArrayList<Card> hand) {
		if (hand == null) {
			for (ImageView i : playerViewList) {
				i.setImage(new Image("cardBack.jpg"));
			}
		} else {
				for (ImageView i : playerViewList) {
					i.setImage(getCardImage(hand.get(playerViewList.indexOf(i))));
				}
			}
		playerCardBox.getChildren().clear();
		playerCardBox.getChildren().addAll(playerViewList);
	}

	void displayDealerHand(ArrayList<Card> hand) {
		if (hand == null) {
			for (ImageView i : dealerViewList) {
				i.setImage(new Image("cardBack.jpg"));
			}
		} else {
			for (ImageView i : dealerViewList) {
				i.setImage(getCardImage(hand.get(dealerViewList.indexOf(i))));
			}
		}
		
		dealerCardBox.getChildren().clear();
		dealerCardBox.getChildren().addAll(dealerViewList);
	}

}
