package a1;

public class Stone extends GameObject {
	
	private float speed;
	private boolean picked;
	

	public Stone(String stonefile) {
		setLocation();
		setObjectName(stonefile);
		speed = 0.0f;
		picked = false;
	}


	public float getSpeed() {
		return speed;
	}


	public void setSpeed(float speed) {
		this.speed = speed;
	}


	public boolean isPicked() {
		return picked;
	}


	public void setPicked(boolean picked) {
		this.picked = picked;
	}

}
