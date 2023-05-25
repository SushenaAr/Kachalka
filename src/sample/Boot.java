import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.openqa.selenium.WebDriver;
import utils.Configurer;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Boot extends Application {
    static SimpleStringProperty titleFromSite = new SimpleStringProperty("0");
    static SimpleStringProperty titleFromRadio = new SimpleStringProperty("0");
    static Condition condition = new ReentrantLock().newCondition();
    static ArrayList<String> arrayList = new ArrayList<>();
    static WebDriver driver = Configurer.setUpDriver();
    static WebDriver driverDownload = Configurer.setUpDriver();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Button buttonGetTracksFromRadio = new Button("Радио");
        buttonGetTracksFromRadio.setOnMouseClicked(new EventHandler<>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                new Thread(() -> {
                    try {
                        DownloadFromZaycevNet.runGetTracks(driver, ParserFromRadioRecord.getSoundsForArtists(driver, new ArrayList<>()));
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        });
        TextField textField = new TextField();
        TextField textFieldCookie = new TextField();
        Button buttonGetTracksFromVK = new Button("Получить топ песен исполнителя");
        buttonGetTracksFromVK.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                new Thread(() -> {
                    try {
                        ArrayList<String> arrayList = new ArrayList<>(List.of(textField.getText().split(", ")));
                        DownloadFromZaycevNet.runGetTracks(driver, ParserFromTopVK.getSoundsForArtists(driver, arrayList, textFieldCookie.getText()));
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        });
        Button buttonReload = new Button("Обновить");
        buttonReload.setOnMouseClicked(new EventHandler<>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                String[] arrayElem = arrayList.get(0).split(";");
                titleFromRadio.setValue(arrayElem[0]);
                titleFromSite.setValue(arrayElem[1]);
            }
        });
        Button buttonDownload = new Button("Скачать");
        buttonDownload.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                String[] arrayElem = arrayList.get(0).split(";");
                new Thread(() -> {
                    DownloadFromZaycevNet.downloadTheTrack(driverDownload, arrayElem[2]);
                }).start();
                arrayList.remove(0);
                String[] arrayElem1 = arrayList.get(0).split(";");
                titleFromRadio.setValue(arrayElem1[0]);
                titleFromSite.setValue(arrayElem1[1]);
            }
        });
        Button buttonNotDownload = new Button("Не скачивать");
        buttonNotDownload.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                arrayList.remove(0);
                String[] arrayElem = arrayList.get(0).split(";");
                titleFromRadio.setValue(arrayElem[0]);
                titleFromSite.setValue(arrayElem[1]);
            }
        });
        HBox hBox = new HBox();
        Label labelFromRadio = new Label();
        labelFromRadio.textProperty().bind(titleFromRadio);
        Label labelFromSite = new Label();
        labelFromSite.textProperty().bind(titleFromSite);
        hBox.getChildren().addAll(buttonGetTracksFromRadio, textField, textFieldCookie, buttonGetTracksFromVK, buttonReload, buttonDownload,
                buttonNotDownload, labelFromRadio, labelFromSite);
        hBox.setSpacing(10);
        Group group = new Group();
        group.getChildren().addAll(hBox);
        Scene scene = new Scene(group, 1500, 50);
        primaryStage.setTitle("Качалка");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
