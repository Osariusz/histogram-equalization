package com.histogramequalization.histogramequalization;

import com.histogramequalization.histogramequalization.equalizer.HistogramEqualizer;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageController {

    @FXML
    private ImageView imageView;
    @FXML
    private AnchorPane anchorPane;

    public void initialize() {
        imageView.fitWidthProperty().bind(anchorPane.widthProperty());
        imageView.fitHeightProperty().bind(anchorPane.heightProperty());
        imageView.setPreserveRatio(true);
    }

    @FXML
    protected void onEqualizeButtonClick() {
        try {
            BufferedImage image = ImageIO.read(new File("src\\main\\resources\\in.png"));
            HistogramEqualizer histogramEqualizer = new HistogramEqualizer();
            histogramEqualizer.setImage(image);
            histogramEqualizer.equalizeHistogram();
            changeImage(SwingFXUtils.toFXImage(image, null));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void changeImage(Image image) {
        imageView.setImage(image);
    }
}