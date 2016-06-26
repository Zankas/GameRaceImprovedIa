package core;

import java.util.ArrayList;
import java.util.HashMap;

public interface CarManager {
	void collisionGrass();

	void collisionCheckPoint();

	boolean checking_off_the_track();

	void collisionStart();

	void updateCar(ArrayList<CarManager> carManagerList, int i);

	Car getCar();

	Direction getDirection();

	void makeCheckPoint();

	void speedHandler();

	Checkpoints getCheckpoints();

	boolean collisionDetection(Car car);

	void setDirection();

	void updateCar(HashMap<Integer,CarManager> map, Integer k);

}
