package com.example.ballproject;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BallsController implements Initializable {
    boolean stop=false;  //variable for control on the balls movement
    ExecutorService threadPool= Executors.newCachedThreadPool();
    private ObservableList<Ball> arrayOfBalls = FXCollections.observableArrayList(); //here we store all created balls
    private Color[] colour={Color.TOMATO,Color.VIOLET,Color.SKYBLUE,Color.ORANGE,Color.NAVY,Color.LIME,Color.BLACK}; 
    @FXML
    Pane bounceArea;
    @FXML
    Slider slider;
    @FXML
    Rectangle segment;
    @FXML
    ChoiceBox choiceBox;

    
    public void clickstart(ActionEvent actionEvent) {
        int coordinants[]=randomPosition();
        Random rand =new Random();
        int speed=(int)slider.getValue()/10;
        createBall(coordinants[0],coordinants[1],randomSign()*(speed+rand.nextInt(1,3)),randomSign()*(speed+rand.nextInt(1,3)),Color.web((String) choiceBox.getValue()));
    }

    private void createBall(int startX,int startY,int dx,int dy,Color color) //this method creates ball
    {
        Ball ballofthread = new Ball(startX, startY, dx, dy, color);
        bounceArea.getChildren().add(ballofthread.getView());
        arrayOfBalls.add(ballofthread);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Runnable updater = new Runnable() {
                    @Override
                    public void run() {
                        if (!stop) {
                            int detect=detectCollision(ballofthread);
                            if(detect>-1) {                                                      //check if there is collision with another ball
                                if(ballofthread.getDx()*arrayOfBalls.get(detect).getDx()>0)  // balls move in same direction
                                {
                                    if(ballofthread.getX()<=arrayOfBalls.get(detect).getX())    // ballofthread is in left side of the other ball
                                    {
                                        if(ballofthread.getDx()>0)ballofthread.setDx();
                                        else arrayOfBalls.get(detect).setDx();
                                    }
                                    else                                                        // ballofthread thread is in right side of the other ball
                                    {
                                        if(ballofthread.getDx()>0)arrayOfBalls.get(detect).setDx();
                                        else ballofthread.setDx();;

                                    }
                                }
                                else
                                {
                                    ballofthread.setDx();
                                    arrayOfBalls.get(detect).setDx();
                                }
                                arrayOfBalls.get(detect).move();
                            }
                            else {
                                int result = wallCollision(ballofthread.getX(), ballofthread.getY());
                                if (result == 3) {     //ball is in one of the four corners of the pane
                                    ballofthread.setDx();
                                    ballofthread.setDy();
                                }
                                else {
                                    if (result == 1) ballofthread.setDx();
                                    if (result == 2) ballofthread.setDy();
                                    if (result == 0)
                                    {
                                        int segmenthit=segmentCollision(ballofthread.getX(), ballofthread.getY());
                                        if(segmenthit!=0) {
                                            if (segmenthit == 1) {
                                                ballofthread.setDx();
                                                ballofthread.setDy();
                                            }
                                            if (segmenthit == 2) ballofthread.setDy();
                                            if (segmenthit == 3) ballofthread.setDx();
                                        }
                                    }
                                }
                            }
                            ballofthread.move();
                        }
                    }
                };

                while (true) {
                    try {
                        Thread.sleep(35);
                    } catch (InterruptedException ex) {
                    }
                    Platform.runLater(updater);
                }
            }
        });
        threadPool.execute(thread);
    }
    private int wallCollision(int curx,int cury) //check if there is collision with borders of the pane and if there is collision it returns where it is
    {
        //0 for noll collision  //1 left bord or right bord //2 upper bord or lower bord //3 (1 and 2)
        if((curx<=5 || curx>=bounceArea.getPrefWidth()-5) && (cury<=5 || cury>=bounceArea.getPrefHeight()-5))return 3;
        if(curx<=5 || curx>=bounceArea.getPrefWidth()-5)return 1;
        if(cury<=5 || cury>=bounceArea.getPrefHeight()-5)return 2;
        return 0;
    }
    private int segmentCollision(int curx,int cury)  //check if there is collision with the segment in the center of the pane and if there is collision it returns where it is
    {
        int offset=6,segmentX=(int)segment.getLayoutX(),segmentY=(int)segment.getLayoutY(),segmentWidth=(int)segment.getWidth(),segmentHeight=(int)segment.getHeight();

        if( ( segmentX-offset<=curx && curx<=segmentX && segmentY-offset<=cury && cury<=segmentY )   //upper left corner
                || (segmentX-offset<=curx && curx<=segmentX && segmentY+segmentHeight<=cury && cury<=segmentHeight+offset+segmentY) //lower left corner
            ||(segmentX+segmentWidth<=curx && curx<=segmentX+segmentWidth+offset && segmentY+segmentHeight<=cury && cury<=segmentHeight+offset+segmentY)  //lower right corner
            ||(segmentX+segmentWidth<=curx && curx<=segmentWidth+offset+segmentX && segmentY-offset<=cury && cury<=segmentY))return 1;     //upper right corner;
        else if((segmentX<=curx && curx<=segmentX+segmentWidth && segmentY-6<=cury && cury<=segmentY) // upper edge
            ||  (segmentX<=curx && curx<=segmentX+segmentWidth && segmentY+segmentHeight<=cury && cury<=segmentY+segmentHeight+offset))return 2; //lower edge
        else if( (segmentX-offset<=curx && curx<=segmentX && segmentY<=cury && cury<=segmentY+segmentHeight) //left edge
            || segmentX+segmentWidth<=curx && curx<=segmentX+offset+segmentWidth && segmentY<=cury && cury<=segmentY+segmentHeight)return 3; //right edge
        else return 0; // there is no contact with segment;
    }
    private boolean ballCollision(Ball b1,Ball b2)  //check if there is collision between the given balls
    {
        if(Math.abs(b1.getX()-b2.getX())<11 && Math.abs(b1.getY()-b2.getY())<11 )return true;
        return false;
    }
    public void clickQuit(ActionEvent actionEvent)  //when pressing "X" to close application this method executes
    {
        threadPool.shutdown();
        Platform.exit();
    }

    private int detectCollision(Ball cur)  //check if there is collision between two balls
    {
       for(int i=0;i<arrayOfBalls.size();i++)
       {
           if(arrayOfBalls.get(i).Id()!=cur.Id())
           {
               if(ballCollision(cur, arrayOfBalls.get(i)))return i; //if there is collision returns index in global list of the ball that collides with "cur"
           }
       }
           return -1;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) //on start of scene  this executes
    {
        choiceBox.setValue("BLACK");
        choiceBox.setItems(FXCollections.observableArrayList( "TOMATO","VIOLET","SKYBLUE","ORANGE","NAVY","LIME","BLACK" ));
    }

    public void clickStop(ActionEvent actionEvent) // stop or resume balls movement
    {
        stop=!stop;
    }

    public void clickPane(MouseEvent mouseEvent) // create ball when there is a click in the pane
    {
        int mouseX=(int)mouseEvent.getX(),mouseY=(int)mouseEvent.getY();
        if(mouseX>10 && mouseX<bounceArea.getPrefWidth()-10 && mouseY>10 && mouseY<bounceArea.getPrefHeight()-10) // check if click is in the pane
        {
            if(detectCollision(new Ball(mouseX,mouseY,0,0,Color.BLACK))==-1) {       //check if the place is safe for new ball
                int Dxy = randomSpeed();
                createBall((int) mouseEvent.getX(), (int) mouseEvent.getY(), Dxy * randomSign(), Dxy * randomSign(), Color.web(randomColor()));
            }
        }
    }

    private String randomColor()  //gets name of random colour
    {
        Random rand =new Random();
        int index=rand.nextInt(0,choiceBox.getItems().size());
        Object randomChoice=choiceBox.getItems().get(index);
        return (String)randomChoice;
    }
    private int randomSpeed()     //random number for speed of a ball
    {
        Random rand =new Random();
        return rand.nextInt(2,6);
    }
    private int[] randomPosition()   //random position in pane
    {
        Random rand =new Random();
        int x=rand.nextInt(0,(int)bounceArea.getWidth()-20)+10;
        int y=rand.nextInt(0,(int)bounceArea.getHeight()-20)+10;
        while(detectCollision(new Ball(x,y,0,0,Color.BLACK))!=-1 && segmentCollision(x,y)!=0)
        {
            x=rand.nextInt(0,(int)bounceArea.getWidth()-20)+10;
            y=rand.nextInt(0,(int)bounceArea.getHeight()-20)+10;
        }
        int[] result={x,y};
        return result;
    }
    private int randomSign()      //return 1 or -1;
    {
        Random rand =new Random();
        if(rand.nextInt(0,2)==0)return 1;
        return -1;
    }
}
