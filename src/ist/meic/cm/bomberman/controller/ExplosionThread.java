package ist.meic.cm.bomberman.controller;

import ist.meic.cm.bomberman.status.BombStatus;
import ist.meic.cm.bomberman.status.BombermanStatus;
import ist.meic.cm.bomberman.status.GhostStatus;
import ist.meic.cm.bomberman.status.Status;

import java.io.Serializable;
import java.util.LinkedList;

public class ExplosionThread extends Thread implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -390850487009272286L;
	private long EXPLOSION_DURATION;
	private long EXPLOSION_TIMEOUT;
	private int EXPLOSION_RANGE;
	private MapController mapController;
	private int position;
	private BombStatus bombStatus;
	private char[] array;
	protected final static int OTHER_LINE_STEP = 21;
	private static final long ADJUST = 1000;

	public ExplosionThread(int position, BombStatus bombStatus,
			MapController mapController) {
		this.mapController = mapController;
		this.position = position;
		this.bombStatus = bombStatus;

		EXPLOSION_DURATION = (long) (MapController.getExplosionDuration() * ADJUST);
		EXPLOSION_TIMEOUT = (long) (MapController.getExplosionTimeout() * ADJUST);
		EXPLOSION_RANGE = MapController.getExplosionRange();
	}

	@Override
	public void run() {
		try {
			Thread.sleep(EXPLOSION_DURATION);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		deleteBomb();
		ExplodingThread et = new ExplodingThread(bombStatus, mapController,
				array);
		et.start();

		try {
			Thread.sleep(EXPLOSION_TIMEOUT);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		et.setRunning(false);
		exploded();
	}

	//
	public void bombExplode() {

		char[] mapArray = mapController.getMap().toCharArray();
		mapArray[position] = 'E';

		int len = mapArray.length;
		int pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0;
		boolean pos_1 = true, pos_2 = true, pos_3 = true, pos_4 = true;

		for (int i = 1; i <= EXPLOSION_RANGE
				&& (pos_1 && pos1 >= 0 || pos_2 && pos2 < len || pos_3
						&& pos3 >= 0 || pos_4 && pos4 < len); i++) {

			if (pos_1) {
				pos1 = position - i;
				if (pos1 >= 0)
					if (mapArray[pos1] == 'W')
						pos_1 = false;
					else if (mapArray[pos1] != 'W')
						mapArray[pos1] = 'E';
			}
			if (pos_2) {
				pos2 = position + i;
				if (pos2 < len)
					if (mapArray[pos2] == 'W')
						pos_2 = false;
					else if (mapArray[pos2] != 'W')
						mapArray[pos2] = 'E';
			}
			if (pos_3) {
				pos3 = position - (i * OTHER_LINE_STEP);
				if (pos3 >= 0)
					if (mapArray[pos3] == 'W')
						pos_3 = false;
					else if (mapArray[pos3] != 'W')
						mapArray[pos3] = 'E';
			}
			if (pos_4) {
				pos4 = position + (i * OTHER_LINE_STEP);
				if (pos4 < len)
					if (mapArray[pos4] == 'W')
						pos_4 = false;
					else if (mapArray[pos4] != 'W')
						mapArray[pos4] = 'E';
			}
		}

		mapController.setMap(new String(array = mapArray));
	}

	//
	public void deleteBomb() {
		LinkedList<GhostStatus> ghostsStatus = mapController.getGhostsStatus();
		LinkedList<BombermanStatus> bombermanStatus = mapController
				.getBombermansStatus();
		//
		bombStatus.die();
		bombStatus.getBomberman().setCanBomb(true);
		//
		bombExplode();

		for (Status ghost : ghostsStatus)
			if (!ghost.isDead()) {
				if (checkDeathPos(ghost.getI())) {
					ghost.die(); // remove this ghost from the list of ghosts
					// Statuses, no longer exists
					mapController
							.killedGhost(bombStatus.getBomberman().getId()); // TODO
				}
			}
		for (Status bomberman : bombermanStatus)
			if (!bomberman.isDead()) {
				if (checkDeathPos(bomberman.getI())) {
					bomberman.die();
					if (!bomberman.equals(bombStatus.getBomberman()))
						mapController.killedBomberman(bombStatus.getBomberman()
								.getId());
				}
			}
	}

	//
	private boolean checkDeathPos(int currentPos) {
		return array[currentPos] == 'E';
	}

	public void exploded() {
		char[] mapArray = array;

		mapArray[position] = '-';

		int len = mapArray.length;
		int pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0;
		boolean pos_1 = true, pos_2 = true, pos_3 = true, pos_4 = true;

		for (int i = 1; i <= EXPLOSION_RANGE
				&& (pos_1 && pos1 >= 0 || pos_2 && pos2 < len || pos_3
						&& pos3 >= 0 || pos_4 && pos4 < len); i++) {

			if (pos_1) {
				pos1 = position - i;
				if (pos1 >= 0)
					if (mapArray[pos1] != 'E')
						pos_1 = false;
					else if (mapArray[pos1] == 'E')
						mapArray[pos1] = '-';
			}
			if (pos_2) {
				pos2 = position + i;
				if (pos2 < len)
					if (mapArray[pos2] != 'E')
						pos_2 = false;
					else if (mapArray[pos2] == 'E')
						mapArray[pos2] = '-';
			}
			if (pos_3) {
				pos3 = position - (i * OTHER_LINE_STEP);
				if (pos3 >= 0)
					if (mapArray[pos3] != 'E')
						pos_3 = false;
					else if (mapArray[pos3] == 'E')
						mapArray[pos3] = '-';
			}
			if (pos_4) {
				pos4 = position + (i * OTHER_LINE_STEP);
				if (pos4 < len)
					if (mapArray[pos4] != 'E')
						pos_4 = false;
					else if (mapArray[pos4] == 'E')
						mapArray[pos4] = '-';
			}
		}

		mapController.setMap(new String(mapArray));
	}

}
