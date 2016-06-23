package core;

import java.awt.Polygon;
import java.awt.geom.Area;
import java.util.ArrayList;

public abstract class AbstractCarManager implements CarManager {
	Car car;
	World world;
	private Direction direction;
	Checkpoints checkpoints;
	int position;

	public AbstractCarManager(World w, Car car) {
		this.world = w;
		this.car = car;

		this.checkpoints = new Checkpoints();

		checkpoints.setTotalLaps(3);// default laps track
	}

	public void makeCheckPoint() {

		for (int i = 0; i < World.X_MATRIX_STRING; i++) {
			for (int j = 0; j < World.Y_MATRIX_STRING; j++) {

				if ("horizontal".equals(world.getMatrixString()[i][j])
						|| "vertical".equals(world.getMatrixString()[i][j])
						|| "curveleftdown".equals(world.getMatrixString()[i][j])
						|| "curveleftup".equals(world.getMatrixString()[i][j])
						|| "curverightup".equals(world.getMatrixString()[i][j])
						|| "curverightdown".equals(world.getMatrixString()[i][j])) {

					checkpoints.insertCheckPoint(i, j);
				}

			}
		}
	}

	public Checkpoints getCheckpoints() {
		return checkpoints;
	}

	@Override
	public void updateCar(ArrayList<CarManager> carManagerList, int i) {
		moving(true);
		check();
		moving(false);
		for (int j = i + 1; j < carManagerList.size(); j++) {
			if (collisionDetection(carManagerList.get(j).getCar())) {
				System.out.println(" CAR " + car.getID() + " CAR " + carManagerList.get(j).getCar().getID());
				applyCollision(carManagerList.get(j).getCar(), aheadCar(carManagerList.get(j)));
			}
		}
	}

	private void applyCollision(Car car2, int aheadCar) {
		if (aheadCar == 0) {
			car.setSpeed(car.getSpeed() - 0.05);
			car2.setSpeed(car.getSpeed() - 0.05);
		} else if (aheadCar == 1) {
			car2.setSpeed(car.getSpeed() - 0.05);
		} else if (aheadCar == 2) {
			car.setSpeed(car.getSpeed() - 0.05);
		}
	}

	private double findCenter(double firstVar, double secondVar) {
		return Math.abs((firstVar + secondVar) / 2);
	}

	private int aheadCar(CarManager carManager) {

		if (this.direction == Direction.UP) {
			if (carManager.getDirection() == Direction.DOWN)
				return 0;
			else if (carManager.getDirection() == Direction.UP) {
				if (findCenter(this.getCar().getY1rot(), this.getCar().getY4rot()) < findCenter(
						carManager.getCar().getY1rot(), carManager.getCar().getY4rot())) {
					return 1;
				} else
					return 2;
			}
		} else if (direction == Direction.DOWN) {
			if (carManager.getDirection() == Direction.UP)
				return 0;
			else if (carManager.getDirection() == Direction.DOWN) {
				if (findCenter(this.getCar().getY1rot(), this.getCar().getY4rot()) < findCenter(
						carManager.getCar().getY1rot(), carManager.getCar().getY4rot())) {
					return 2;
				} else
					return 1;
			}
		} else if (direction == Direction.LEFT) {
			if (carManager.getDirection() == Direction.RIGHT)
				return 0;
			else if (carManager.getDirection() == Direction.LEFT) {
				if (findCenter(this.getCar().getX1rot(), this.getCar().getX4rot()) < findCenter(
						carManager.getCar().getX1rot(), carManager.getCar().getX4rot())) {
					return 1;
				} else {
					return 2;
				}
			}
		} else if (direction == Direction.RIGHT) {
			if (carManager.getDirection() == Direction.LEFT)
				return 0;
			else if (carManager.getDirection() == Direction.RIGHT) {
				if (findCenter(this.getCar().getX1rot(), this.getCar().getX4rot()) < findCenter(
						carManager.getCar().getX1rot(), carManager.getCar().getX4rot())) {
					return 2;
				} else
					return 1;
			}
		}

		return -1;

	}

	private void moving(boolean condition) {
		if (condition) {
			totalMove();
			setDirection();
		} else {
			car.moveXYrot(world);

			if (car.isLEFT()) {
				if (car.getSpeed() != 0) {
					if (car.getSpeed() < 0) {
						car.setAngle(car.getAngle() + 0.011);
					} else {
						car.setAngle(car.getAngle() - 0.011);
					}
				}

			}
			if (car.isRIGHT()) {
				if (car.getSpeed() != 0) {
					if (car.getSpeed() < 0) {
						car.setAngle(car.getAngle() - 0.011);
					} else {
						car.setAngle(car.getAngle() + 0.011);
					}
				}
			}
			if (car.getAngle() < 0)
				car.setAngle(car.getAngle() + 6.283185307179586);
			if (car.getAngle() >= 6.283185307179586) {
				car.setAngle(0);
			}
		}
	}

	abstract void totalMove();

	// Collision Detection.
	@Override
	public boolean collisionDetection(Car car2) {

		int[] xpoints = { (int) this.car.getX1rot(), (int) this.car.getX2rot(), (int) this.car.getX3rot(),
				(int) this.car.getX4rot() };
		int[] ypoints = { (int) this.car.getY1rot(), (int) this.car.getY2rot(), (int) this.car.getY3rot(),
				(int) this.car.getY4rot() };
		int npoints = 4;

		Area areaA = new Area(new Polygon(xpoints, ypoints, npoints));

		xpoints[0] = (int) car2.getX1rot();
		xpoints[1] = (int) car2.getX2rot();
		xpoints[2] = (int) car2.getX3rot();
		xpoints[3] = (int) car2.getX4rot();

		ypoints[0] = (int) car2.getY1rot();
		ypoints[1] = (int) car2.getY2rot();
		ypoints[2] = (int) car2.getY3rot();
		ypoints[3] = (int) car2.getY4rot();

		areaA.intersect(new Area(new Polygon(xpoints, ypoints, npoints)));

		return !areaA.isEmpty();
	}

	void check() {
		collisionGrass();
		collisionCheckPoint();
		collisionStart();
	}

	@Override
	public void collisionGrass() {

		if (checking_off_the_track())
			return;

		double cos = Math.cos(car.getAngle());
		double sin = Math.sin(car.getAngle());

		if ((world.getMatrixWorld().getValuePosition((int) (car.getY1rot() + (sin * car.getSpeed())),
				(int) (car.getX1rot() + (cos * car.getSpeed()))) == BlockRoadObject.GRASS)
				|| (world.getMatrixWorld().getValuePosition((int) (car.getY2rot() + (sin * car.getSpeed())),
						(int) (car.getX2rot() + (cos * car.getSpeed()))) == BlockRoadObject.GRASS)
				|| (world.getMatrixWorld().getValuePosition((int) (car.getY3rot() + (sin * car.getSpeed())),
						(int) (car.getX3rot() + (cos * car.getSpeed()))) == BlockRoadObject.GRASS)
				|| (world.getMatrixWorld().getValuePosition((int) (car.getY4rot() + (sin * car.getSpeed())),
						(int) (car.getX4rot() + (cos * car.getSpeed()))) == BlockRoadObject.GRASS)) {

			if (car.getSpeed() > 0.75)
				car.setSpeed(car.getSpeed() - 0.03);

			if (car.getSpeed() < 0)
				if (car.getSpeed() < -0.75)
					car.setSpeed(car.getSpeed() + 0.03);
		}
	}

	public void collisionCheckPoint() {
		if (checking_off_the_track())
			return;

		double cos = Math.cos(car.getAngle());
		double sin = Math.sin(car.getAngle());

		if (world.getMatrixWorld().getValuePosition((int) (car.getY1rot() + (sin * car.getSpeed())),
				(int) (car.getX1rot() + (cos * car.getSpeed()))) == BlockRoadObject.CHECKPOINT) {

			checkpoints.setCheckpoint((int) (car.getY1rot() + (sin * car.getSpeed())) / AbstractBlockRoadObject.SIZE,
					(int) (car.getX1rot() + (cos * car.getSpeed())) / AbstractBlockRoadObject.SIZE, true);

		}

		if (world.getMatrixWorld().getValuePosition((int) (car.getY2rot() + (sin * car.getSpeed())),
				(int) (car.getX2rot() + (cos * car.getSpeed()))) == BlockRoadObject.CHECKPOINT) {

			checkpoints.setCheckpoint((int) (car.getY2rot() + (sin * car.getSpeed())) / AbstractBlockRoadObject.SIZE,
					(int) (car.getX2rot() + (cos * car.getSpeed())) / AbstractBlockRoadObject.SIZE, true);

		}

		if (world.getMatrixWorld().getValuePosition((int) (car.getY3rot() + (sin * car.getSpeed())),
				(int) (car.getX3rot() + (cos * car.getSpeed()))) == BlockRoadObject.CHECKPOINT) {

			checkpoints.setCheckpoint((int) (car.getY3rot() + (sin * car.getSpeed())) / AbstractBlockRoadObject.SIZE,
					(int) (car.getX3rot() + (cos * car.getSpeed())) / AbstractBlockRoadObject.SIZE, true);

		}

		if (world.getMatrixWorld().getValuePosition((int) (car.getY4rot() + (sin * car.getSpeed())),
				(int) (car.getX4rot() + (cos * car.getSpeed()))) == BlockRoadObject.CHECKPOINT) {

			checkpoints.setCheckpoint((int) (car.getY4rot() + (sin * car.getSpeed())) / AbstractBlockRoadObject.SIZE,
					(int) (car.getX4rot() + (cos * car.getSpeed())) / AbstractBlockRoadObject.SIZE, true);

		}
	}

	public boolean checking_off_the_track() {
		double cos = Math.cos(car.getAngle());
		double sin = Math.sin(car.getAngle());

		if (((int) (car.getY1rot() + (sin * car.getSpeed())) < 0)
				|| ((int) (car.getX1rot() + (cos * car.getSpeed())) < 0)
				|| ((int) (car.getY1rot() + (sin * car.getSpeed())) >= (AbstractBlockRoadObject.getSize()
						* World.X_MATRIX_STRING))
				|| ((int) (car.getX1rot() + (cos * car.getSpeed())) >= (AbstractBlockRoadObject.getSize()
						* World.Y_MATRIX_STRING))
				||

				((int) (car.getY2rot() + (sin * car.getSpeed())) < 0)
				|| ((int) (car.getX2rot() + (cos * car.getSpeed())) < 0)
				|| ((int) (car.getY2rot() + (sin * car.getSpeed())) >= (AbstractBlockRoadObject.getSize()
						* World.X_MATRIX_STRING))
				|| ((int) (car.getX2rot() + (cos * car.getSpeed())) >= (AbstractBlockRoadObject.getSize()
						* World.Y_MATRIX_STRING))
				||

				((int) (car.getY3rot() + (sin * car.getSpeed())) < 0)
				|| ((int) (car.getX3rot() + (cos * car.getSpeed())) < 0)
				|| ((int) (car.getY3rot() + (sin * car.getSpeed())) >= (AbstractBlockRoadObject.getSize()
						* World.X_MATRIX_STRING))
				|| ((int) (car.getX3rot() + (cos * car.getSpeed())) >= (AbstractBlockRoadObject.getSize()
						* World.Y_MATRIX_STRING))
				||

				((int) (car.getY4rot() + (sin * car.getSpeed())) < 0)
				|| ((int) (car.getX4rot() + (cos * car.getSpeed())) < 0)
				|| ((int) (car.getY4rot() + (sin * car.getSpeed())) >= (AbstractBlockRoadObject.getSize()
						* World.X_MATRIX_STRING))
				|| ((int) (car.getX4rot() + (cos * car.getSpeed())) >= (AbstractBlockRoadObject.getSize()
						* World.Y_MATRIX_STRING))) {

			return true;
		}
		return false;
	}

	public void collisionStart() {
		if (checking_off_the_track())
			return;

		double cos = Math.cos(car.getAngle());
		double sin = Math.sin(car.getAngle());

		if ((world.getMatrixWorld().getValuePosition((int) (car.getY1rot() + (sin * car.getSpeed())),
				(int) (car.getX1rot() + (cos * car.getSpeed()))) == BlockRoadObject.START)
				|| (world.getMatrixWorld().getValuePosition((int) (car.getY2rot() + (sin * car.getSpeed())),
						(int) (car.getX2rot() + (cos * car.getSpeed()))) == BlockRoadObject.START)
				|| (world.getMatrixWorld().getValuePosition((int) (car.getY3rot() + (sin * car.getSpeed())),
						(int) (car.getX3rot() + (cos * car.getSpeed()))) == BlockRoadObject.START)
				|| (world.getMatrixWorld().getValuePosition((int) (car.getY4rot() + (sin * car.getSpeed())),
						(int) (car.getX4rot() + (cos * car.getSpeed()))) == BlockRoadObject.START)) {

			if (checkpoints.allCheckpointsPassed()) {
				checkpoints.setFalseAllCheckPoint();

				checkpoints.increaseLaps();

			}

		}
	}

	public Car getCar() {
		return car;
	}

	public void speedHandler() {

		// gestione velocita' per marcia avanti
		if (car.isUP()) {
			if (car.getSpeed() < 4)
				car.setSpeed(car.getSpeed() + 0.04);
		}
		if (!car.isUP()) {
			if (car.getSpeed() >= 0.05)
				car.setSpeed(car.getSpeed() - 0.05);
		}
		if (car.isDOWN()) {
			if (car.getSpeed() >= 0.15)
				car.setSpeed(car.getSpeed() - 0.15);
		}

		// gestione velocita' per marcia indietro
		if (car.isDOWN()) {
			if (car.getSpeed() > -2)
				car.setSpeed(car.getSpeed() - 0.04);
		}
		if (!car.isDOWN()) {
			if (car.getSpeed() <= -0.05)
				car.setSpeed(car.getSpeed() + 0.05);
		}
		if (car.isUP()) {
			if (car.getSpeed() <= -0.15)
				car.setSpeed(car.getSpeed() + 0.15);
		}

		if ((car.getSpeed() > -0.2) && (car.getSpeed() < 0.2) && (!car.isUP() && !car.isDOWN())) {
			car.setSpeed(0);
		}
	}

	@Override
	public Direction getDirection() {
		return direction;
	}

	@Override
	public void setDirection() {
		if (car.getAngle() >= Math.PI / 4 && car.getAngle() <= Math.PI - Math.PI / 4) {
			direction = Direction.DOWN;
		} else if (car.getAngle() >= Math.PI + Math.PI / 4 && car.getAngle() <= Math.PI * 2 - Math.PI / 4) {
			direction = Direction.UP;
		} else if (car.getAngle() > Math.PI - Math.PI / 4 && car.getAngle() < Math.PI + Math.PI / 4) {
			direction = Direction.LEFT;
		} else {
			direction = Direction.RIGHT;
		}
	}
}
