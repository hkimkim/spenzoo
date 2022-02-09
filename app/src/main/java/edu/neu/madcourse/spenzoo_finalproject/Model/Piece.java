package edu.neu.madcourse.spenzoo_finalproject.Model;

abstract public class Piece {
    public String type;
    public Float xPosition;
    public Float yPosition;
    public Integer imageSource;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Float getxPosition() {
        return xPosition;
    }

    public void setxPosition(Float xPosition) {
        this.xPosition = xPosition;
    }

    public Float getyPosition() {
        return yPosition;
    }

    public void setyPosition(Float yPosition) {
        this.yPosition = yPosition;
    }

    public Integer getImageSource() {
        return imageSource;
    }

    public void setImageSource(Integer imageSource) {
        this.imageSource = imageSource;
    }
}
