package minesweeper.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import minesweeper.view.exceptions.UnknownFieldTypeException;

/**
 * Class representing a field.
 */
public class Field extends ImageView{
    /**
     * Initializes an object.
     */
    Field(){
        set(Type.UNMARKED);
        setCache(true);
    }

    static{
        path = "/minesweeper/view/resources/game_board/";
        valueFieldsImages = new Image[]{
                new Image(Field.path + "1.png"),
                new Image(Field.path + "2.png"),
                new Image(Field.path + "3.png"),
                new Image(Field.path + "4.png"),
                new Image(Field.path + "5.png"),
                new Image(Field.path + "6.png"),
                new Image(Field.path + "7.png"),
                new Image(Field.path + "8.png"),
        };
        bombImage = new Image(Field.path + "bomb.jpg");
        bombDetonatedImage = new Image(Field.path + "bomb_detonated.jpg");
        markedImage = new Image(Field.path + "marked.png");
        unmarkedImage = new Image(Field.path + "unmarked.png");
        reversedImage = new Image(Field.path + "reversed.png");
    }

    /**
     * Changes an image of this field.
     * @param type Type of image to be displayed.
     */
    void set(Type type){
        switch (type){
            case MARKED:
                setImage(markedImage);
                break;
            case UNMARKED:
                setImage(unmarkedImage);
                break;
            case BOMB_DETONATED:
                setImage(bombDetonatedImage);
                break;
            case BOMB:
                setImage(bombImage);
                break;
            case REVERSED:
                setImage(reversedImage);
                break;
        }
    }

    /**
     * Changes an image of this field.
     * @param value Value on field.
     */
    void set(int value){
        if(value < 1 || value > 8){
            throw new UnknownFieldTypeException("Tried to create a field with value: " + value);
        }
        setImage(valueFieldsImages[value-1]);
    }

    /**
     * Path to resources.
     */
    private static String path;
    /**
     * All number images.
     */
    private static Image[] valueFieldsImages;
    /**
     * Bomb image.
     */
    private static Image bombImage;
    /**
     * Detonated bomb image.
     */
    private static Image bombDetonatedImage;
    /**
     * Marked field image.
     */
    private static Image markedImage;
    /**
     * "Unmarked" field image.
     */
    private static Image unmarkedImage;
    /**
     * Reversed field image.
     */
    private static Image reversedImage;

    /**
     * Types used to change current content of a field.
     */
    enum Type{
        MARKED,
        UNMARKED,
        BOMB_DETONATED,
        BOMB,
        REVERSED
    }
}
