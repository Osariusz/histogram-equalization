package com.histogramequalization.histogramequalization.equalizer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class HistogramEqualizer {

    private BufferedImage image;

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    private void setImagePart(List<Integer> channelPixels, int start, int end, List<Integer> newPixelValue) {
        for (int i = start; i < end; ++i) {
            channelPixels.set(i, newPixelValue.get(channelPixels.get(i)));
        }
    }

    private void equalizeChannel(List<Integer> channelPixels) {
        int allPixels = image.getHeight() * image.getWidth();
        List<Integer> valueCount = new ArrayList<>(Collections.nCopies(256, 0));
        for (Integer value : channelPixels) {
            valueCount.set(value, valueCount.get(value) + 1);
        }
        for (int i = 1; i < valueCount.size(); ++i) {
            valueCount.set(i, valueCount.get(i) + valueCount.get(i - 1));
        }
        List<Integer> newPixelValue = new ArrayList<>(Collections.nCopies(256, 0));
        for (int i = 0; i < 256; ++i) {
            Integer count = valueCount.get(i);
            Integer newValue = (int) (((double) count / allPixels) * 255);
            newPixelValue.set(i, newValue);
        }

        int cores = Runtime.getRuntime().availableProcessors();
        List<Thread> threads = new ArrayList<>(cores);
        for (int i = 0; i < cores; ++i) {
            Integer threadStep = channelPixels.size() / cores;
            Integer start = threadStep * i;
            Thread thread = new Thread(() -> {
                Integer end = start + threadStep;
                if (end + threadStep >= channelPixels.size()) {
                    end = channelPixels.size();
                }
                setImagePart(channelPixels, start, end, newPixelValue);
            });
            thread.start();
            threads.add(thread);
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void equalizeHistogram() {
        long start = System.currentTimeMillis();
        List<Integer> red = new ArrayList<>(image.getWidth() * image.getHeight());
        List<Integer> green = new ArrayList<>(image.getWidth() * image.getHeight());
        List<Integer> blue = new ArrayList<>(image.getWidth() * image.getHeight());
        int[] allColors = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        for (int pixel : allColors) {
            Color pixelColor = new Color(pixel);
            red.add(pixelColor.getRed());
            green.add(pixelColor.getGreen());
            blue.add(pixelColor.getBlue());
        }
        equalizeChannel(red);
        equalizeChannel(green);
        equalizeChannel(blue);
        for (int i = 0; i < image.getWidth() * image.getHeight(); ++i) {
            int x = i % image.getWidth();
            int y = i / image.getWidth();
            image.setRGB(x, y, new Color(red.get(i), green.get(i), blue.get(i)).getRGB());
        }
        long end = System.currentTimeMillis();
        System.out.println(String.format("Equalization took: %d milliseconds", end - start));
    }
}
