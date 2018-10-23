package myGame;

import ray.rage.scene.*;
import ray.rage.scene.controllers.*;
import ray.rml.*;

public class BounceController extends AbstractController
{
    private float bounceRate = 0.05f; // growth per second
    private float cycleTime = 2000.0f; // default cycle time
    private float totalTime = 0.0f;
    private float direction = 1.0f;

    @Override
    protected void updateImpl(float elapsedTimeMillis)
    { 
        totalTime += elapsedTimeMillis;
        if (direction == 1.0f) { bounceRate += 0.03f; }
        else { bounceRate -= 0.03f; }
        float amount = 1.0f * bounceRate;
        if (totalTime > cycleTime)
        { 
        	direction = -direction;
            totalTime = 0.0f;
        }
        for (Node n : super.controlledNodesList)
        {
            Vector3 curLoc = n.getLocalPosition();
            curLoc = Vector3f.createFrom(curLoc.x(), 1.0f * amount, curLoc.z());
            n.setLocalPosition(curLoc);
        }
    }
}