package com.redomar.game.entities;

import java.util.List;

import com.redomar.game.entities.efx.Swim;
import com.redomar.game.gfx.Colours;
import com.redomar.game.gfx.Screen;
import com.redomar.game.level.LevelHandler;
import com.redomar.game.level.Node;

public class Vendor extends Mob {

	private int colour, shirtCol, faceCol; // = Colours.get(-1, 111, 240, 310);
	private int tickCount = 0;
	private int tick = 0;
	private double xa = 0;
	private double ya = 0;
	private double[] movement;
	private boolean[] swimType;
	private int[] swimColour;
	private static double speed = 0.75;
	private List<Node> path = null;
	private int time = 0;
	private static int[] collisionBoders = {0, 7, 0, 7};

	private Swim swim;

	public Vendor(LevelHandler level, String name, int x, int y, int shirtCol,
			int faceCol) {
		super(level, "h", x, y, speed, collisionBoders);
		this.faceCol = faceCol;
		this.shirtCol = shirtCol;
		this.colour = Colours.get(-1, 111, shirtCol, faceCol);
	}

	public void tick() {
		
		tick++;
		movement = randomMovementAI(x, y, xa, ya, tick);
		
		this.xa = movement[0];
		this.ya = movement[1];
		
		moveMob(xa, ya, this);
		
		setSwim(new Swim(level, (int) getX(), (int) getY()));
		swimType = getSwim().swimming(isSwimming, isMagma, isMuddy);
		isSwimming = swimType[0];
		isMagma = swimType[1];
		isMuddy = swimType[2];

		tickCount++;

	}

	public void render(Screen screen) {
		setTime(getTime() + 1);
		int xTile = 8;
		int yTile = 28;
		int walkingSpeed = 4;
		int flipTop = (numSteps >> walkingSpeed) & 1;
		int flipBottom = (numSteps >> walkingSpeed) & 1;

		if (movingDir == 1) {
			xTile += 2;
			if (!isMoving || swim.isActive(swimType)){
				yTile -= 2;
			}
		} else if (movingDir == 0 && !isMoving || movingDir == 0 && swim.isActive(swimType)) {
			yTile -= 2;
		} else if (movingDir > 1) {
			xTile += 4 + ((numSteps >> walkingSpeed) & 1) * 2;
			flipTop = (movingDir - 1) % 2;
			if(!isMoving){
				xTile = 4;
			}
		}

		int modifier = 8 * scale;
		int xOffset = (int) getX() - modifier / 2;
		int yOffset = (int) getY() - modifier / 2 - 4;

		if (isSwimming || isMagma || isMuddy) {
			swimColour = getSwim().waveCols(isSwimming, isMagma, isMuddy);
			
			int waterColour = 0;
			yOffset += 4;

			colour = Colours.get(-1, 111, -1, faceCol);

			if (tickCount % 60 < 15) {
				waterColour = Colours.get(-1, -1, swimColour[0], -1);
			} else if (15 <= tickCount % 60 && tickCount % 60 < 30) {
				yOffset--;
				waterColour = Colours.get(-1, swimColour[1], swimColour[2], -1);
			} else if (30 <= tickCount % 60 && tickCount % 60 < 45) {
				waterColour = Colours.get(-1, swimColour[2], -1, swimColour[1]);
			} else {
				yOffset--;
				waterColour = Colours.get(-1, -1, swimColour[1], swimColour[2]);
			}

			screen.render(xOffset, yOffset + 3, 31 + 31 * 32, waterColour,
					0x00, 1);
			screen.render(xOffset + 8, yOffset + 3, 31 + 31 * 32, waterColour,
					0x01, 1);
		}

		screen.render((xOffset + (modifier * flipTop)), yOffset,
				(xTile + yTile * 32), colour, flipTop, scale);
		screen.render((xOffset + modifier - (modifier * flipTop)), yOffset,
				((xTile + 1) + yTile * 32), colour, flipTop, scale);
		if (!isSwimming && !isMagma && !isMuddy) {
			screen.render((xOffset + (modifier * flipBottom)),
					(yOffset + modifier), (xTile + (yTile + 1) * 32), colour,
					flipBottom, scale);
			screen.render((xOffset + modifier - (modifier * flipBottom)),
					(yOffset + modifier), ((xTile + 1) + (yTile + 1) * 32),
					colour, flipBottom, scale);
			colour = Colours.get(-1, 111, shirtCol, faceCol);
		}
	}

	public Swim getSwim() {
		return swim;
	}

	public void setSwim(Swim swim) {
		this.swim = swim;
	}

	public List<Node> getPath() {
		return path;
	}

	public void setPath(List<Node> path) {
		this.path = path;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}
}
