
/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import com.sun.awt.SecurityWarning;

public class Breakout extends GraphicsProgram {

	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 500;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

	/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

	/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

	/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

	/** Separation between bricks */
	private static final int BRICK_SEP = 4;

	/** Width of a brick */
	private static final int BRICK_WIDTH = (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

	/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

	/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

	/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

	/** Number of turns */
	private static final int NTURNS = 3;

	private GRect paddle = null;
	private GOval ball = null;
	private boolean gameIsOn = false;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx = 0, vy = 0;
	private int BRICKS_NUM = NBRICKS_PER_ROW * NBRICK_ROWS;
	private long colisionTime = 0;

	/** Runs the Breakout program. */
	public void run() {

		addMouseListeners();
		setBricks();
		for (int i = NTURNS; i > 0; i--) {
			{

				setGame();

				waitForClick();
				playGame();
				if (BRICKS_NUM == 0) {
					break;
				}
			}
		}
		finishGame();
	}

	/**
	 * This method sets Paddle and Ball on their places.
	 */
	private void setGame() {
		setPaddle();
		setBall();
	}

	/**
	 * This method sets paddle in the middle of the canvas (on the x axis)
	 */
	private void setPaddle() {

		paddle = new GRect(getWidth() / 2 - PADDLE_WIDTH / 2, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH,
				PADDLE_HEIGHT);
		paddle.setColor(Color.BLACK);
		paddle.setFillColor(Color.BLACK);
		paddle.setFilled(true);
		add(paddle);
	}

	/**
	 * This method draws all bricks.
	 */
	private void setBricks() {

		int yCoordinate = BRICK_Y_OFFSET;
		for (int i = 1; i <= NBRICK_ROWS; i++) {
			drawRow(i, yCoordinate);
			yCoordinate += BRICK_SEP + BRICK_HEIGHT;
		}
	}

	/**
	 * This method draws one row of bricks with given row number and y coordinate.
	 */
	private void drawRow(int i, int yCoordinate) {

		int xCoordinate = 0;
		Color col = getColor(i);

		for (int j = 1; j <= NBRICKS_PER_ROW; j++) {
			drawBrick(yCoordinate, xCoordinate, col);
			xCoordinate += BRICK_WIDTH + BRICK_SEP;
		}

	}

	/**
	 * This method gets colour for each row of bricks with given row number.
	 */
	private Color getColor(int row) {

		if (row <= NBRICK_ROWS / 5) {
			return Color.RED;
		} else if (row <= 2 * NBRICK_ROWS / 5) {
			return Color.ORANGE;
		} else if (row <= 3 * NBRICK_ROWS / 5) {
			return Color.YELLOW;
		} else if (row <= 4 * NBRICK_ROWS / 5) {
			return Color.GREEN;
		} else if (row <= 5 * NBRICK_ROWS / 5) {
			return Color.CYAN;
		}
		return Color.black;
	}

	/**
	 * This method draws a rectangle (a brick) with given sizes and colour/
	 */
	private void drawBrick(double d, double e, Color col) {

		GRect rect = new GRect(e, d, BRICK_WIDTH, BRICK_HEIGHT);
		rect.setColor(col);
		rect.setFilled(true);
		rect.setFillColor(col);
		add(rect);
	}

	/**
	 * This method creates GOval (ball) and fills it.
	 */
	private void setBall() {
		ball = new GOval(getWidth() / 2 - BALL_RADIUS, getHeight() / 2 - BALL_RADIUS, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setColor(Color.BLACK);
		ball.setFillColor(Color.BLACK);
		ball.setFilled(true);
		add(ball);
	}

	/**
	 * This method moves ball until game is finished.
	 */
	private void playGame() {
		while (!gameFinished()) {
			ball.move(vx, vy);
			updateGame();
			pause(15);
		}

		gameIsOn = false;
		remove(paddle);
		remove(ball);

	}

	/**
	 * This method starts game upon pressing mouse.
	 */
	public void mousePressed(MouseEvent e) {
		if (!gameIsOn) {
			setVelocity();
		}
		gameIsOn = true;
	}

	/**
	 * This method tracks mouse to set paddle's exact location.
	 */
	public void mouseMoved(MouseEvent e) {
		if (gameIsOn) {
			if (e.getX() - PADDLE_WIDTH / 2 >= 0 && e.getX() + PADDLE_WIDTH / 2 <= getWidth()) {
				paddle.setLocation(e.getX() - PADDLE_WIDTH / 2, getHeight() - PADDLE_Y_OFFSET);
			} else if (e.getX() - PADDLE_WIDTH / 2 < 0) {
				paddle.setLocation(0, getHeight() - PADDLE_Y_OFFSET);
			} else if (e.getX() + PADDLE_WIDTH / 2 > getWidth()) {
				paddle.setLocation(getWidth() - PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET);
			}
		}
	}

	/**
	 * This method sets velocity to ball.
	 */
	private void setVelocity() {
		if (vx == 0) {
			vx = rgen.nextDouble(1, 3);
			if (rgen.nextBoolean(0.5))
				vx = -vx;
			vy = 3;
		}
	}

	/**
	 * This method checks if anything is going to collide ball including walls,
	 * bricks and paddle.
	 * 
	 */
	private void updateGame() {
		checkWallCollision();
		checkInGameCollision();

	}

	/**
	 * This method checks if ball is colliding bricks or paddle. If brick is hit, it
	 * removes brick, updates total number of bricks and updates ball velocity. If
	 * paddle is hit, it updates paddle Velocity if paddle was NOT hit 2 seconds ago
	 * or less.
	 */
	private void checkInGameCollision() {
		int angleCollision = checkColision();
		if (angleCollision != -1) {
			GObject obj = getColidingObj(angleCollision);

			if (obj != paddle && obj != null) {
				changeVelocity(angleCollision);
				remove(obj);
				BRICKS_NUM--;
			} else if (obj == paddle) {
				long temp = System.currentTimeMillis();
				if (colisionTime + 300 <= temp) {
					colisionTime = temp;
					changeVelocity(angleCollision);
				}
			}
		}
	}

	/**
	 * This method changes velocity depending on where was ball hit using angle.
	 */
	private void changeVelocity(int angle) {
		if (angle != 0 && angle != 180) {
			vy = -vy;
		}
		if (angle != 90 && angle != 270) {
			if (angle > 90 && angle < 270) {
				vx = Math.abs(vx);
			} else {
				vx = -Math.abs(vx);
			}
		}
	}

	/**
	 * This method checks if ball is going to hit any object.
	 */
	private int checkColision() {
		double x = 0;
		double y = 0;
		for (int i = 0; i < 360; i += 30) {
			x = ball.getX() + Math.cos(Math.toRadians(i)) + BALL_RADIUS * (1 + Math.cos(Math.toRadians(i)));
			y = ball.getY() - Math.sin(Math.toRadians(i)) + BALL_RADIUS * (1 - Math.sin(Math.toRadians(i)));
			if (getElementAt(x, y) != null) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * This method gets colliding object near ball.
	 */
	private GObject getColidingObj(int i) {
		double x = ball.getX() + Math.cos(Math.toRadians(i)) + BALL_RADIUS * (1 + Math.cos(Math.toRadians(i)));
		double y = ball.getY() - Math.sin(Math.toRadians(i)) + BALL_RADIUS * (1 - Math.sin(Math.toRadians(i)));
		GObject object = getElementAt(x, y);
		return object;
	}

	/**
	 * This method prevents ball to going through walls and makes it bounce from
	 * them.
	 */
	private void checkWallCollision() {
		if (ball.getX() <= 0 || ball.getX() + 2 * BALL_RADIUS >= WIDTH) {
			vx = -vx;
		}
		if (ball.getY() <= 0) {
			vy = -vy;
		}
	}

	/**
	 * This method (as it seems) checks if game is finished, depending on ball
	 * location and number of bricks left.
	 */
	private boolean gameFinished() {
		return BRICKS_NUM == 0 || ball.getY() > HEIGHT;
	}

	/**
	 * This method finishes game and prints out users result.
	 */
	private void finishGame() {
		GLabel lab = null;
		if (BRICKS_NUM == 0) {
			lab = new GLabel("CONGRATULATIONS< YOU WON");
		} else {
			lab = new GLabel("BETTER LUCK NEXT TIME");
		}
		lab.setLocation(WIDTH / 2 - lab.getWidth() / 2, HEIGHT / 2);
		add(lab);
	}

}
