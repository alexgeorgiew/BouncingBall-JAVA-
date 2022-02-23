package com.example.ballproject;

import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

class Ball extends Pane {
    public int radius = 5;
    private int x,y,dx,dy,id;
    static int globalcount=0;
    private Circle circle;

    public Ball(int x,int y,int dx,int dy,Color color) {
        id=globalcount;
        globalcount++;
        circle=new Circle(x, y, radius);  //(pos x, pos y,radius)
        this.x=x;
        this.y=y;
        this.dx=dx;
        this.dy=dy;
        circle.setFill(color); //paint circle
    }

    public void move() {
        circle.setCenterX(x+dx);
        circle.setCenterY(y+dy);
        x += dx;
        y += dy;
    }

    public int Id()
    {
        return this.id;
    }

    public void setDx() {
        this.dx = -dx;
    }

    public void setDy() {
        this.dy =-dy;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public Shape getView()
    {
        return circle;
    }
}