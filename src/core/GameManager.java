package core;

import java.util.ArrayList;
import java.util.HashMap;

public class GameManager {
	private boolean update;

	private World world;
	//ArrayList<CarManager> carManagerList;
	HashMap<Integer, CarManager> carManagerMap;

	public GameManager() {

		this.update = true;
		this.world = new World();
		world.makeWorld();

		// this.carManagerList = new ArrayList<>();
		this.carManagerMap = new HashMap<>();

		// carManagerList.add(new CarManagerHuman(world, world.getCar()));
		carManagerMap.put(world.getCar().getID(), new CarManagerHuman(world, world.getCar()));
		switch (World.carToBeCreated) {
		case 2:
			carManagerMap.put(world.getCar2().getID(), new CarManagerDummyAi(world, world.getCar2()));
			// carManagerList.add(new CarManagerDummyAi(world, world.getCar2(),
			// this));
			break;
		case 3:
			carManagerMap.put(world.getCar2().getID(), new CarManagerDummyAi(world, world.getCar2()));
			carManagerMap.put(world.getCar3().getID(), new CarManagerDummyAi(world, world.getCar3()));
			// carManagerList.add(new CarManagerDummyAi(world, world.getCar2(),
			// this));
			// carManagerList.add(new CarManagerDummyAi(world, world.getCar3(),
			// this));

			break;
		case 4:
			carManagerMap.put(world.getCar2().getID(), new CarManagerDummyAi(world, world.getCar2()));
			carManagerMap.put(world.getCar3().getID(), new CarManagerDummyAi(world, world.getCar3()));
			carManagerMap.put(world.getCar4().getID(), new CarManagerDummyAi(world, world.getCar4()));
			// carManagerList.add(new CarManagerDummyAi(world, world.getCar2(),
			// this));
			// carManagerList.add(new CarManagerDummyAi(world, world.getCar3(),
			// this));
			// carManagerList.add(new CarManagerDummyAi(world, world.getCar4(),
			// this));
			break;
		}
		update();
		threadSpeedCar();
	}

	// public ArrayList<CarManager> getCarManagerList() {
	// return carManagerList;
	// }

	public void init() {
		carManagerMap.forEach((k, v) -> v.makeCheckPoint());
		// for (CarManager c : carManagerList) {
		// c.makeCheckPoint();
		// }
	}

	public void azzeraCheckPoint() {
		// for (CarManager c : carManagerList) {
		// c.getCheckpoints().setActualLaps(0);
		// c.getCheckpoints().setFalseAllCheckPoint();
		// }
		carManagerMap.forEach((k, v) -> {
			v.getCheckpoints().setActualLaps(0);
			v.getCheckpoints().setFalseAllCheckPoint();
		});

	}

	public World getWorld() {
		return world;
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(final boolean update) {
		this.update = update;
	}

	public void update() {

		new Thread() {

			@Override
			public void run() {
				while (true) {

					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (update) {
						for (int i = 1; i <= carManagerMap.size(); i++) {
							carManagerMap.get(i).updateCar(carManagerMap, i);
						}
						// for (int i = 0; i < carManagerList.size(); i++) {
						// carManagerList.get(i).updateCar(carManagerList, i);
						// }
						// carManagerMap.forEach((k, v) ->
						// v.updateCar(carManagerMap, k));
						// settingPositions();
					}
				}
			}

		}.start();

	}

	protected void settingPositions() {
		// IDS ARRAY IS SORTED BY WHICH CAR IS AHEAD.
		final ArrayList<Integer> ids = new ArrayList<Integer>();

		// ADD THE FIRST CAR.
		ids.add(1);

		// SFRUTTO LA CONOSCENZA CHE GLI ID DELLE CAR VANNO DA 1 A 4. PARTO
		// DALLA SECONDA E ARRIVO ALLA 4.
		for (int i = 2; i <= carManagerMap.size(); i++) {
			// LOOP ON THE IDS ARRAY.
			for (int j = 0; j < ids.size(); j++) {
				if (carManagerMap.get(i).getCheckpoints().getActualLaps() > carManagerMap.get(ids.get(j))
						.getCheckpoints().getActualLaps()) {
					System.out.println(i + " DIO BOIA" + j);
					ids.add(j, i);
				} else if (carManagerMap.get(i).getCheckpoints().getActualLaps() == carManagerMap.get(ids.get(j))
						.getCheckpoints().getActualLaps()) {
					if (carManagerMap.get(i).getCheckpoints().numberCheckpointsHit() > carManagerMap.get(ids.get(j))
							.getCheckpoints().numberCheckpointsHit()) {
						ids.add(j, i);
					} else if (carManagerMap.get(i).getCheckpoints().numberCheckpointsHit() == carManagerMap
							.get(ids.get(j)).getCheckpoints().numberCheckpointsHit()) {
						if (whichCarIsAhead(carManagerMap.get(i), carManagerMap.get(ids.get(j)))) {
							ids.add(j, i);
						} else if (j == ids.size() - 1) {
							ids.add(i);
						}
					}
				} else if (j == ids.size() - 1) {
					ids.add(i);
				}
			}
		}

		System.out.println("CAR: " + ids.get(0) + " pos 1");
		System.out.println("CAR: " + ids.get(1) + " pos 2");
		System.out.println("CAR: " + ids.get(2) + " pos 3");
		System.out.println("CAR: " + ids.get(3) + " pos 4");
	}

	private boolean whichCarIsAhead(final CarManager carManager, final CarManager carManager2) {
		// THE FIRST CAR IS THE ALREADY IN THE IDS ARRAY.
		// FIXME AFTER MIDDLEPOINT ON THE WORLD.
		if (world.getMatrixWorld().whereAmI(carManager.getCar()) instanceof BlockRoadCurveLeftDown) {

		} else if (world.getMatrixWorld().whereAmI(carManager.getCar()) instanceof BlockRoadCurveLeftUp) {

		} else if (world.getMatrixWorld().whereAmI(carManager.getCar()) instanceof BlockRoadCurveRightUp) {

		} else if (world.getMatrixWorld().whereAmI(carManager.getCar()) instanceof BlockRoadCurveRightDown) {

		} else if (world.getMatrixWorld().whereAmI(carManager.getCar()) instanceof BlockRoadHorizontal) {
			if (carManager.getDirection().equals(Direction.RIGHT)) {
				if (Math.max(carManager.getCar().getX3rot(), carManager.getCar().getX4rot()) > Math
						.max(carManager2.getCar().getX3rot(), carManager2.getCar().getX4rot())) {
					return false;
				}
			} else {
				if (Math.min(carManager.getCar().getX3rot(), carManager.getCar().getX4rot()) < Math
						.min(carManager2.getCar().getX3rot(), carManager2.getCar().getX4rot())) {
					return false;
				}
			}
		} else if (world.getMatrixWorld().whereAmI(carManager.getCar()) instanceof BlockRoadVertical) {
			if (carManager.getDirection().equals(Direction.DOWN)) {
				if (Math.max(carManager.getCar().getY3rot(), carManager.getCar().getY4rot()) > Math
						.max(carManager2.getCar().getY3rot(), carManager2.getCar().getY4rot())) {
					return false;
				}
			} else {
				if (Math.min(carManager.getCar().getY3rot(), carManager.getCar().getY4rot()) < Math
						.min(carManager2.getCar().getY3rot(), carManager2.getCar().getY4rot())) {
					return false;
				}
			}
		}
		return true;
	}

	public void threadSpeedCar() {

		new Thread() {

			@Override
			public void run() {
				while (true) {

					try {
						Thread.sleep(50);

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (update) {
						carManagerMap.forEach((k, v) -> v.speedHandler());
					}
				}
			}
		}.start();
	}

	public boolean endGame() {

		// if (carManagerHuman.getCheckpoints().getActualLaps() !=
		// carManagerHuman.getCheckpoints().getTotalLaps()) {
		// return false;
		// }
		if (carManagerMap.get(1).getCheckpoints().getActualLaps() != carManagerMap.get(1).getCheckpoints()
				.getTotalLaps()) {
			return false;
		}
		return true;

	}

	public HashMap<Integer, CarManager> getCarManagerMap() {
		return carManagerMap;
	}
}
