
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;
import java.util.List;
import javafx.scene.control.ChoiceDialog;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableMap;
import javafx.collections.MapChangeListener;


/**
 * @author John Maginnes
 * @version 1
 */

public class MusicPlayer extends Application {
    private TextField filterField;
    private static TableView table;
    private static MediaPlayer mediaPlayer;
    @Override

    /**Override for Application
     * @param stage takes in a stage
     *
     */
    public void start(Stage stage) {

        Scene scene = new Scene(new Group());

        //Takes  mp3 from current directories and puts them in arraylist
        File file = new File(".");
        File[] myFiles = file.listFiles();
        String absPath = "file:" + file.getAbsolutePath();

        //Create an array of Media Objects
        ArrayList<Media> myMusic = new ArrayList<>();

        for (int i = 0; i < myFiles.length; i++) {
            if (myFiles[i].toString().contains("mp3")) {
                myMusic.add(new Media(myFiles[i].toURI().toString()));
            }
        }
        // Create ArrayList of Music

        ArrayList<Music> musicList = new ArrayList<>();
        for (int i = 0; i < myMusic.size(); i++) {
            String[] mySplit = myMusic.get(i).getSource().split("/");
            Music newSong = new Music(mySplit[mySplit.length - 1],
                new Media(myMusic.get(i).getSource()));
            musicList.add(newSong);
        }

        Map<String, Music> musicMap = new HashMap<>();
        for (Music song : musicList) {
            musicMap.put(song.getSongName(), song);
        }





        ObservableList<Music> myObservList =
            FXCollections.observableArrayList(musicList);
        table = createTable(myObservList, stage);
        Map<Media, MediaPlayer> mediaAndPlayer = new HashMap<>();
        for (Music song : musicList) {
            mediaAndPlayer
                .put(song.getMediaName(), new MediaPlayer(song.getMediaName()));
        }



        Button button1 = new Button("Play");
        Button button2 = new Button("Pause");
        button2.setDisable(true);
        Button button3 = new Button("Search Songs");
        Button button4 = new Button("Show all Songs");
        button4.setDisable(true);

        button1.setOnAction(e -> {
                Music mySong = (Music) table
                    .getSelectionModel().getSelectedItem();
                mediaPlayer = mediaAndPlayer
                    .get(mySong.getMediaName());
                if (!(mediaPlayer.getStatus()
                    .equals(MediaPlayer.Status.PLAYING))) {
                    mediaPlayer.play();
                    button1.setDisable(true);
                    button2.setDisable(false);

                }
            });

        button2.setOnAction(e -> {
                if (!(mediaPlayer.getStatus()
                    .equals(MediaPlayer.Status.PAUSED))) {
                    mediaPlayer.pause();
                    button1.setDisable(false);
                    button2.setDisable(true);
                }
            });

        TableView hello = new TableView();


        button3.setOnAction(e -> {
                List<String> choices = new ArrayList<>();
                choices.add("File Name");
                choices.add("Artist");
                choices.add("Title");
                choices.add("Album");

                button3.setDisable(true);
                button4.setDisable(false);

                ChoiceDialog<String> dialog =
                    new ChoiceDialog<>("File Name", choices);
                dialog.setTitle("Search Songs");
                dialog.setHeaderText("Search Songs");
                dialog.setContentText("Choose your category to search by: ");
                Optional<String> result = dialog.showAndWait();

                result.ifPresent(letter -> {
                        TextInputDialog search = new TextInputDialog("");
                        search.setTitle("Text Input Dialog");
                        search.setHeaderText("Look, a Text Input Dialog");
                        search.setContentText("Please enter your name:");

                        Optional<String> result2 = search.showAndWait();




                        if (letter.equals("File Name")) {
                            ArrayList<String> myFileNames = new ArrayList<>();
                            Map<Media, String> myMediaNames = new HashMap<>();
                            for (Music song : musicList)  {
                                myFileNames.add(song.getSongName());
                            }

                            result2.ifPresent(name -> {
                                    ArrayList<Music> finalList =
                                        new ArrayList<>();
                                    ArrayList<String> myNewList =
                                        getContained(myFileNames, name);

                                    for (String str :  myNewList) {
                                        if (musicMap.keySet().contains(str)) {
                                            finalList.add(musicMap.get(str));
                                        }



                                        for (int i = 0;
                                                i < table
                                                    .getItems().size(); i++) {
                                            table.getItems().clear();
                                        }
                                        Scene scene2 = scene;
                                        ObservableList<Music> mySearchList =
                                            FXCollections
                                                .observableArrayList(finalList);
                                        table =
                                            createTable(mySearchList, stage);
                                        HBox buttonBox = new HBox();
                                        buttonBox.getChildren()
                                            .addAll(button1, button2,
                                                button3, button4);

                                        Label label = new Label("Music Player");
                                        VBox vbox = new VBox();
                                        vbox.getChildren()
                                            .addAll(label, table, buttonBox);

                                        ((Group) scene2.getRoot()).getChildren()
                                            .addAll(vbox);

                                        stage.setScene(scene2);
                                        stage.show();

                                    }
                                });

                        }
                        if (letter.equals("Artist")) {

                            ArrayList<Music> nonNullArtist = new ArrayList<>();
                            for (Music song: musicList) {
                                if (song.getArtistName() != null) {
                                    nonNullArtist.add(song);
                                }
                            }

                            Map<String, ArrayList<Music>> artistMap =
                                new HashMap<>();
                            for (Music song : nonNullArtist) {
                                if (artistMap.keySet()
                                    .contains(song.getArtistName())) {
                                    ArrayList<Music> myList = artistMap
                                        .get(song.getArtistName());
                                    myList.add(song);
                                    artistMap.put(song.getArtistName(), myList);
                                } else {
                                    ArrayList<Music> newList =
                                        new ArrayList<>();
                                    newList.add(song);
                                    artistMap
                                        .put(song.getArtistName(), newList);
                                }
                            }

                            ArrayList<String> myFileNames = new ArrayList<>();
                            Map<Media, String> myMediaNames = new HashMap<>();
                            for (Music song : nonNullArtist)  {
                                myFileNames.add(song.getArtistName());
                            }

                            result2.ifPresent(name -> {
                                    ArrayList<Music> finalList =
                                        new ArrayList<>();
                                    ArrayList<String> myNewList =
                                        getContained(myFileNames, name);


                                    for (String str :  myNewList) {
                                        ArrayList<Music> myFinalArrayList =
                                            artistMap.get(str);
                                        for (Music mus : myFinalArrayList) {
                                            if (!(finalList.contains(mus))) {
                                                finalList.add(mus);
                                            }
                                        }
                                    }



                                    for (int i = 0;
                                        i < table.getItems().size(); i++) {
                                        table.getItems().clear();
                                    }
                                    Scene scene3 = scene;
                                    ObservableList<Music> mySearchList =
                                        FXCollections
                                        .observableArrayList(finalList);
                                    table = createTable(mySearchList, stage);
                                    HBox buttonBox = new HBox();
                                    buttonBox.getChildren()
                                        .addAll(button1,
                                            button2, button3, button4);

                                    Label label = new Label("Music Player");
                                    VBox vbox = new VBox();
                                    vbox.getChildren()
                                        .addAll(label, table, buttonBox);

                                    ((Group) scene3.getRoot()).getChildren()
                                        .addAll(vbox);

                                    stage.setScene(scene3);
                                    stage.show();


                                });

                        }

                        if (letter.equals("Title")) {

                            ArrayList<Music> nonNullTitle = new ArrayList<>();
                            for (Music song: musicList) {
                                if (song.getTitle() != null) {
                                    nonNullTitle.add(song);
                                }
                            }

                            Map<String, ArrayList<Music>> titleMap
                                = new HashMap<>();
                            for (Music song : nonNullTitle) {
                                if (titleMap.keySet()
                                    .contains(song.getTitle())) {
                                    ArrayList<Music> myList =
                                        titleMap.get(song.getTitle());
                                    myList.add(song);
                                    titleMap.put(song.getTitle(), myList);
                                } else {
                                    ArrayList<Music> newList =
                                        new ArrayList<>();
                                    newList.add(song);
                                    titleMap.put(song.getTitle(), newList);
                                }
                            }

                            ArrayList<String> myFileNames = new ArrayList<>();
                            Map<Media, String> myMediaNames = new HashMap<>();
                            for (Music song : nonNullTitle)  {
                                myFileNames.add(song.getTitle());
                            }

                            result2.ifPresent(name -> {
                                    ArrayList<Music> finalList =
                                        new ArrayList<>();
                                    ArrayList<String> myNewList =
                                        getContained(myFileNames, name);


                                    for (String str :  myNewList) {
                                        ArrayList<Music> myFinalArrayList =
                                            titleMap.get(str);
                                        for (Music mus : myFinalArrayList) {
                                            if (!(finalList.contains(mus))) {
                                                finalList.add(mus);
                                            }
                                        }
                                    }



                                    for (int i = 0; i < table.getItems()
                                        .size(); i++) {
                                        table.getItems().clear();
                                    }

                                    Scene scene3 = scene;
                                    ObservableList<Music> mySearchList =
                                        FXCollections
                                            .observableArrayList(finalList);
                                    table = createTable(mySearchList, stage);
                                    HBox buttonBox = new HBox();
                                    buttonBox.getChildren()
                                        .addAll(button1, button2,
                                            button3, button4);

                                    Label label = new Label("Music Player");
                                    VBox vbox = new VBox();
                                    vbox.getChildren()
                                        .addAll(label, table, buttonBox);

                                    ((Group) scene3.getRoot())
                                        .getChildren().addAll(vbox);

                                    stage.setScene(scene3);
                                    stage.show();


                                });


                        }

                        if (letter.equals("Album")) {

                            Map<String, ArrayList<Music>> albumMap =
                                new HashMap<>();
                            ArrayList<Music> nonNullAlbum = new ArrayList<>();
                            for (Music song: musicList) {
                                if (song.getAlbumName() != null) {
                                    nonNullAlbum.add(song);
                                }
                            }
                            for (Music song : nonNullAlbum) {
                                if (albumMap.keySet().contains(song
                                    .getAlbumName())) {
                                    ArrayList<Music> myList = albumMap
                                        .get(song.getAlbumName());
                                    myList.add(song);
                                    albumMap.put(song.getAlbumName(), myList);
                                } else {
                                    ArrayList<Music> newList =
                                        new ArrayList<>();
                                    newList.add(song);
                                    albumMap.put(song.getAlbumName(), newList);
                                }

                            }

                            ArrayList<String> myFileNames = new ArrayList<>();
                            Map<Media, String> myMediaNames = new HashMap<>();
                            for (Music song : nonNullAlbum)  {
                                myFileNames.add(song.getAlbumName());
                            }

                            result2.ifPresent(name -> {
                                    ArrayList<Music> finalList =
                                        new ArrayList<>();
                                    ArrayList<String> myNewList =
                                        getContained(myFileNames, name);


                                    for (String str :  myNewList) {
                                        ArrayList<Music> myFinalArrayList =
                                            albumMap.get(str);
                                        for (Music mus : myFinalArrayList) {
                                            if (!(finalList.contains(mus))) {
                                                finalList.add(mus);
                                            }
                                        }
                                    }


                                    for (int i = 0; i < table.getItems()
                                        .size(); i++) {
                                        table.getItems().clear();
                                    }
                                    Scene scene3 = scene;
                                    ObservableList<Music> mySearchList =
                                        FXCollections
                                            .observableArrayList(finalList);
                                    table = createTable(mySearchList, stage);
                                    HBox buttonBox = new HBox();
                                    buttonBox.getChildren()
                                        .addAll(button1, button2,
                                            button3, button4);

                                    Label label = new Label("Music Player");
                                    VBox vbox = new VBox();
                                    vbox.getChildren()
                                        .addAll(label, table, buttonBox);

                                    ((Group) scene3.getRoot())
                                        .getChildren().addAll(vbox);

                                    stage.setScene(scene3);
                                    stage.show();


                                });

                        }




                    // });
                    });
            });



        button4.setOnAction(e -> {

                button3.setDisable(false);
                button4.setDisable(true);

                ObservableList<Music> myObservList2 =
                    FXCollections.observableArrayList(musicList);


                table = createTable(myObservList2, stage);
                table.setItems(myObservList2);

                HBox buttonBox2 = new HBox();
                buttonBox2.getChildren()
                    .addAll(button1, button2, button3, button4);

                Label label2 = new Label("Music Player");
                VBox vbox2 = new VBox();
                vbox2.getChildren().addAll(label2, table, buttonBox2);

                ((Group) scene.getRoot()).getChildren().addAll(vbox2);

                stage.setScene(scene);
                stage.show();
            });

        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(button1, button2, button3, button4);

        Label label = new Label("Music Player");
        VBox vbox = new VBox();
        vbox.getChildren().addAll(label, table, buttonBox);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        stage.setScene(scene);
        stage.show();
        table.refresh();

    }
    /**
    * finds what words are contained in a list
     * @param str arraylist of strings
     * @param name what was searched
     * @return new ArrayList
     */
    public static
        ArrayList<String> getContained(ArrayList<String> str, String name) {
        ArrayList<String> emptList = new ArrayList<>();
        for (String string : str) {
            if (string.contains(name)) {
                emptList.add(string);
            }
        }

        return emptList;
    }

    /**
    * constructs a table
     * @param myList observ list of music
     * @param stage takes in a stage
     * @return new table
     */

    public static
        TableView createTable(ObservableList<Music> myList, Stage stage) {
        // Creating Table

        Scene scene2 = new Scene(new Group());

        TableView table2 = new TableView();
        table2.setItems(myList);

        TableColumn fileName = new TableColumn("File Name");
        fileName.setMinWidth(500);
        TableColumn attributes = new TableColumn("Attributes");
        attributes.setMinWidth(500);

        TableColumn artistName = new TableColumn("Artist");
        artistName.setMinWidth(300);

        TableColumn title = new TableColumn("Title");
        title.setMinWidth(300);

        TableColumn albumName = new TableColumn("Album");
        albumName.setMinWidth(300);

        fileName.setCellValueFactory(
            new PropertyValueFactory<Music, String>("songName"));

        artistName.setCellValueFactory(new PropertyValueFactory<Music, String>(
            "artistName"));
        title.setCellValueFactory(new PropertyValueFactory<Music, String>(
            "title"));
        albumName.setCellValueFactory(new PropertyValueFactory<Music, String>(
            "albumName"));





        //Sets Columns and Sub Columns
        table2.getColumns().addAll(fileName, attributes);
        attributes.getColumns().addAll(artistName, title, albumName);
        table2.setEditable(true);
        stage.setScene(scene2);
        stage.show();
        table2.refresh();


        return table2;
    }

    /**
    * Music Class
     */

    public static class Music {
        private StringProperty songName;
        private Media mediaName;
        private String artistName;
        private String title;
        private String albumName;
        private Media song2;

        /**
        * constructor for MusicPlayer
         * @param songName takes in song name
         * @param mediaName takes in media
         */

        public Music(String songName, Media mediaName) {
            this.songName = new SimpleStringProperty(songName);
            this.mediaName = mediaName;
            ObservableMap<String, Object> mapData = mediaName.getMetadata();
            mapData.addListener(new MapChangeListener<String, Object>() {
                public void onChanged(MapChangeListener.Change<? extends String,
                    ? extends Object> change) {
                    artistName = (String) mapData.get("artist");
                    title = (String) mapData.get("title");
                    albumName = (String) mapData.get("album");
                    if (artistName != null) {
                        setArtistName(artistName);
                    }

                    table.refresh();

                }
            });

        }

        /**
        *  getter for mediaName
         * @return mediaName
         */
        public Media getMediaName() {
            return this.mediaName;
        }

        /**
        *  setter for songName
         * @param songName takes in new songName
         */

        public void setSongName(String songName) {
            this.songName = new SimpleStringProperty(songName);
        }

        /**
        *  getter for songName
         * @return songName
         */

        public String getSongName() {
            return songName.get();
        }

        /**
        *  setter for artistName
         * @param artistName takes in new artistName
         */

        public void setArtistName(String artistName) {
            this.artistName = artistName;
        }

        /**
        *  getter for artistName
         * @return artistName
         */

        public String getArtistName() {
            return this.artistName;
        }

        /**
        *  setter for title
         * @param title takes in new title
         */

        public void setTitle(String title) {
            this.title = title;
        }

        /**
        *  getter for titleName
         * @return titleName
         */

        public String getTitle() {
            return this.title;
        }

        /**
        *  setter for albumName
         * @param albumName takes in new albumName
         */

        public void setAlbumName(String albumName) {
            this.albumName = albumName;
        }

        /**
        *  getter for albumName
         * @return albumName
         */

        public String getAlbumName() {
            return this.albumName;
        }
    }
}
