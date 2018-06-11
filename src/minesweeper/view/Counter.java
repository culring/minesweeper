package minesweeper.view;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import minesweeper.view.exceptions.CounterException;

class Counter {
    Counter(int numberOfSegments, int initialValue){
        if(numberOfSegments < 1 || initialValue < 0){
            throw new CounterException("Invalid parameters passed to a counter object constructor. NumberOfSegments = " +
            numberOfSegments + ", initialValue = " + initialValue);
        }

        this.numberOfSegments = numberOfSegments;
        hBox = new HBox();
        createSegments(numberOfSegments);
        setValue(initialValue);
    }

    static{
        String path = "minesweeper/view/resources/timer/";
        segmentsImages = new Image[11];
        for(int i = 0; i < 10; ++i){
            Counter.segmentsImages[i] = new Image(path + i + ".png", 30, 0,
                    true, true);
        }
        Counter.segmentsImages[10] = new Image(path + "empty.png", 30, 0,
                true, true);
    }

    Node getNode(){
        return hBox;
    }

    void setValue(int value){
        int[] digits = parseIntegerToDigitArray(value, numberOfSegments);
        for(int i = 0; i < digits.length; ++i){
            imageViews[i].setImage(segmentsImages[digits[i]]);
        }
    }

    private int[] parseIntegerToDigitArray(int value, int length){
        String strValue = Integer.toString(value);
        int[] digits = new int[strValue.length()];
        for(int i = 0; i < strValue.length(); ++i){
            digits[i] = Integer.parseInt(strValue.substring(i, i+1));
        }

        int[] trimmedDigits = new int[length];
        int pos = 0;
        if(length > digits.length){
            for(; pos < length - digits.length; ++pos){
                trimmedDigits[pos] = 10;
            }
        }
        if(length < digits.length){
            pos = digits.length - length;
        }
        System.arraycopy(digits, 0, trimmedDigits, pos, length - pos);

        return trimmedDigits;
    }

    private void createSegments(int numberOfSegments){
        imageViews = new ImageView[numberOfSegments];

        for(int i = 0; i < numberOfSegments; ++i){
            ImageView imageView = new ImageView(segmentsImages[10]);
            imageView.setCache(true);
            imageViews[i] = imageView;
            hBox.getChildren().add(imageView);
        }
    }

    private int numberOfSegments;
    private HBox hBox;
    private ImageView[] imageViews;
    private static Image[] segmentsImages;
}
