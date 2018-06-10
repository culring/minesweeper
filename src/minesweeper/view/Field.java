package minesweeper.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import minesweeper.view.exceptions.UnknownFieldTypeException;

public class Field extends ImageView{
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

    void set(int value){
        if(value < 1 || value > 8){
            throw new UnknownFieldTypeException("Tried to create a field with value: " + value);
        }
        setImage(valueFieldsImages[value-1]);
    }

    private static String path;
    private static Image[] valueFieldsImages;
    private static Image bombImage;
    private static Image bombDetonatedImage;
    private static Image markedImage;
    private static Image unmarkedImage;
    private static Image reversedImage;

    enum Type{
        MARKED,
        UNMARKED,
        BOMB_DETONATED,
        BOMB,
        REVERSED
    }
}
