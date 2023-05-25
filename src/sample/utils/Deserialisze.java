package utils;

import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class Deserialisze {
    public ArrayList<Object> payload;

    public ArrayList<Object> getPayload() {
        return payload;
    }

    public void setPayload(ArrayList<Object> payload) {
        this.payload = payload;
    }

    public ArrayList<String> textFromWebElem = new ArrayList<>();

    public Deserialisze(List<WebElement> webElementList){
        for(WebElement elem : webElementList){
            textFromWebElem.add(elem.getText());
        }
    }

    public Deserialisze() {
    }
}
