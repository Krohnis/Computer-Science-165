package a1;

public class Spear extends GameObject {

	private float speed;
	private boolean picked;
	
	public Spear(String spearfile) {
		setLocation();
		setObjectName(spearfile);
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
