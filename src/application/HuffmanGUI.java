package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

public class HuffmanGUI extends Application {
    public enum OperationType {
        COMPRESSOR, DECOMPRESSOR
    }

    private TextArea resultArea;
    private String selectedFilePathForCompression = "";
    private String selectedFilePathForDecompression = "";
    private String compressedFilePath = "";
    FileCompressor compressor = new FileCompressor();
    long fileSize;
    long compressedFileSize;

    private OperationType operation;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Button compressButton = createStyledButton("Compress");
        Button decompressButton = createStyledButton("Decompress");
        Button browseButton = createStyledButton("Browse");
        Button startButton = createStyledButton("Start");
        Button statisticButton = createStyledButton("Statistic");
        Button huffmanButton = createStyledButton("Huffman");
        Button headerButton = createStyledButton("Header");

        browseButton.setVisible(false);
        statisticButton.setVisible(false);
        huffmanButton.setVisible(false);
        headerButton.setVisible(false);

        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Courier New", 16));
        resultArea.setStyle("-fx-background-color: #F6F6F6; " + "-fx-text-fill: #333333; " + "-fx-padding: 10; "
                + "-fx-border-radius: 5; -fx-background-radius: 5; "
                + "-fx-border-color: #298F93; -fx-border-width: 2;");
        resultArea.setPrefHeight(200);
        resultArea.setPrefWidth(500);

        HBox.setHgrow(resultArea, Priority.ALWAYS);
        VBox.setVgrow(resultArea, Priority.ALWAYS);

        compressButton.setOnAction(e -> showCompressButtons(browseButton, statisticButton, huffmanButton, headerButton));
        decompressButton.setOnAction(e -> showDecompressButton(browseButton, statisticButton, huffmanButton, headerButton));

        browseButton.setOnAction(event -> {
            resultArea.clear();
            if (operation == OperationType.DECOMPRESSOR) {
                selectedFilePathForDecompression = handleFileSelection(primaryStage, "Huffman Compressed Files (*.huff)", "*.huff", true);
            } else {
                selectedFilePathForCompression = handleFileSelection(primaryStage, "All Files", "*.*", false);
            }
        });

        huffmanButton.setOnAction(event -> {
            resultArea.clear();
            if (!selectedFilePathForCompression.isEmpty()) {
                resultArea.setText("HUFFMAN CODE:\n" + compressor.getHuffmanTree().inorderTraversal());
            } else {
                resultArea.setText("Error: Please select a file first using the Browse button.");
            }
        });

        startButton.setOnAction(event -> {
            resultArea.clear();
            if (operation == OperationType.DECOMPRESSOR) {
                if (selectedFilePathForDecompression != null) {
                    handleDecompression(primaryStage, selectedFilePathForDecompression);
                } else {
                    resultArea.setText("Error: No file selected.");
                }
            } else if (operation == OperationType.COMPRESSOR) {
                if (selectedFilePathForCompression != null) {
                    handleCompression(selectedFilePathForCompression, primaryStage);
                } else {
                    resultArea.setText("Error: No file selected.");
                }
            }
        });

        statisticButton.setOnAction(event -> {
            resultArea.clear();
            double compressionRatio = (double) compressedFileSize / fileSize;
            if (operation == OperationType.COMPRESSOR) {
                if (selectedFilePathForCompression != null) {
                    resultArea.setText("FILE SIZE BEFORE COMPRESSION: " + fileSize
                            + " Byte.\nFILE SIZE AFTER COMPRESSION: " + compressedFileSize + " Byte.\n"
                            + String.format("FILE COMPRESSION RATIO: %.2f%%\n", compressionRatio * 100));
                }
            } else {
                resultArea.setText("Error: Please select a file first using the Browse button.");
            }
        });

        headerButton.setOnAction(event -> {
            resultArea.clear();
            if (operation == OperationType.COMPRESSOR) {
                showHeaderInfo(compressedFilePath);
            } else {
                resultArea.setText("Error: Please select a file first using the Browse button.");
            }
        });

        HBox topRow = new HBox(20);
        topRow.getChildren().addAll(compressButton, decompressButton, browseButton);
        topRow.setStyle("-fx-alignment: center;");

        HBox bottomRow = new HBox(20);
        bottomRow.getChildren().addAll(statisticButton, huffmanButton, headerButton);
        bottomRow.setStyle("-fx-alignment: center;");

        VBox buttonLayout = new VBox(30);
        buttonLayout.setPadding(new Insets(30));
        buttonLayout.setStyle("-fx-background-color: #D6EEEC; -fx-alignment: center;");
        buttonLayout.getChildren().addAll(topRow, startButton, bottomRow);

        HBox mainLayout = new HBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.getChildren().addAll(buttonLayout, resultArea);

        HBox.setHgrow(resultArea, Priority.ALWAYS);
        VBox.setVgrow(mainLayout, Priority.ALWAYS);

        Scene scene = new Scene(mainLayout, 900, 500);
        primaryStage.setTitle("Huffman Compression and Decompression");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Lucida Handwriting", 14));
        button.setStyle("-fx-background-color: #298F93; -fx-text-fill: white; -fx-font-weight: bold; "
                + "-fx-padding: 10 20; -fx-border-radius: 5; -fx-background-radius: 5;");
        button.setMinWidth(150);
        return button;
    }

    private String handleFileSelection(Stage primaryStage, String description, String extension, boolean isDecompression) {
        FileChooser fileChooser = setupFileChooser(description, extension);
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();

            if (isDecompression) {
                if (!filePath.endsWith(".huff")) {
                    resultArea.setText("Error: Only .huff files are supported for decompression.");
                    return null;
                }
                resultArea.setText("File selected for decompression: " + filePath);
            } else {
                if (filePath.endsWith(".huff")) {
                    resultArea.setText("Error: You cannot select a .huff file for compression.");
                    return null;
                }
                resultArea.setText("File selected for compression: " + filePath);
            }
            return filePath;
        }

        resultArea.setText("Error: No file selected.");
        return null;
    }

    private FileChooser setupFileChooser(String description, String extension) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(description, extension));
        return fileChooser;
    }

    private void handleDecompression(Stage primaryStage, String inputFilePath) {
        String compressedFilePath = inputFilePath != null ? inputFilePath : handleFileSelection(primaryStage, "Huffman Compressed Files (*.huff)", "*.huff", true);
        if (compressedFilePath == null) return;

        fileSize = getFileSize(inputFilePath);

        try (DataInputStream dis = new DataInputStream(new FileInputStream(compressedFilePath))) {
            String fileExtension = dis.readUTF();
            int headerSize = dis.readInt();
            BinaryTree huffmanTree = (BinaryTree) new ObjectInputStream(dis).readObject();

            byte[] compressedData = new byte[(int) (new File(compressedFilePath).length() - headerSize)];
            dis.readFully(compressedData);

            String decompressedFile = new File(compressedFilePath).getParent() + "/" + getFileNameWithoutExtension(new File(compressedFilePath).getName()) + "_decompressed." + fileExtension;

            FileDecompressor.decompress(compressedData, huffmanTree, decompressedFile);

            resultArea.setText("Decompression completed successfully!\n");
            resultArea.appendText("Decompressed file: " + decompressedFile + "\n");

        } catch (IOException | ClassNotFoundException e) {
            resultArea.setText("Error during decompression: " + e.getMessage());
        }
    }

    private void handleCompression(String inputFilePath, Stage primaryStage) {
        if (inputFilePath.isEmpty()) {
            resultArea.setText("Error: No file selected.");
            return;
        }

        compressedFilePath = new File(inputFilePath).getParent() + "/" + getFileNameWithoutExtension(new File(inputFilePath).getName()) + ".huff";
        fileSize = getFileSize(inputFilePath);

        try {
            int[] frequencyTable = new int[256];
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
                int ch;
                while ((ch = reader.read()) != -1) {
                    if (ch >= 0 && ch < 256) {
                        frequencyTable[ch]++;
                    }
                }
            }

            BinaryTree huffmanTree = FileCompressor.buildHuffmanTree(frequencyTable);
            compressor.setHuffmanTree(huffmanTree);

            byte[] compressedData = compressor.compress(inputFilePath, huffmanTree);

            try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(compressedFilePath))) {
                String fileExtension = getFileExtension(new File(inputFilePath));
                dos.writeUTF(fileExtension);
                dos.writeInt(compressedData.length);
                dos.write(compressedData);
            }

            compressedFileSize = new File(compressedFilePath).length();

            resultArea.appendText("Compression completed successfully!\n");
            resultArea.appendText("Compressed File: " + compressedFilePath + "\n");

        } catch (Exception e) {
            resultArea.setText("Error during file operations: " + e.getMessage());
        }
    }

    private void showHeaderInfo(String compressedFilePath) {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(compressedFilePath))) {
            String fileExtension = dis.readUTF();
            int headerSize = dis.readInt();

            resultArea.setText("Header Information:\n");
            resultArea.appendText("File Extension: " + fileExtension + "\n");
            resultArea.appendText("Header Size: " + headerSize + " bytes\n");
        } catch (IOException e) {
            resultArea.setText("Error reading header: " + e.getMessage());
        }
    }

    private String getFileNameWithoutExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");
        if (lastIndex == -1) {
            return fileName;
        } else {
            return fileName.substring(0, lastIndex);
        }
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int lastIndex = fileName.lastIndexOf(".");
        if (lastIndex == -1) {
            return "";
        } else {
            return fileName.substring(lastIndex + 1);
        }
    }

    private long getFileSize(String filePath) {
        return new File(filePath).length();
    }

    private void showCompressButtons(Button browseButton, Button statisticButton, Button huffmanButton, Button headerButton) {
        operation = OperationType.COMPRESSOR;
        browseButton.setVisible(true);
        statisticButton.setVisible(true);
        huffmanButton.setVisible(true);
        headerButton.setVisible(true);
    }

    private void showDecompressButton(Button browseButton, Button statisticButton, Button huffmanButton, Button headerButton) {
        operation = OperationType.DECOMPRESSOR;
        browseButton.setVisible(true);
        statisticButton.setVisible(false);
        huffmanButton.setVisible(false);
        headerButton.setVisible(false);
    }
}