package sab.game;

import com.badlogic.gdx.controllers.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ControllerManager implements ControllerListener {

    public Map<Controller, PlayerController> controllerMap;

    public ControllerManager() {
        controllerMap = new HashMap<>();
    }

    public void update() {
        for (PlayerController controller : getControllers()) {
            controller.update();
        }
    }

    public void setInGameState(boolean value) {
        for (PlayerController controller : getControllers()) {
            controller.setInGame(value);
        }
    }

    public void checkController(Controller controller) {
        if (!controllerMap.containsKey(controller)) {
            controllerMap.put(controller, new PlayerController(controller, controllerMap.size()));
        }
    }

    public Collection<PlayerController> getControllers() {
        return controllerMap.values();
    }

    @Override
    public void connected(Controller controller) {
        checkController(controller);
    }

    @Override
    public void disconnected(Controller controller) {
        controllerMap.remove(controller);
    }

    @Override
    public boolean buttonDown(Controller controller, int i) {
        System.out.println(i);
        checkController(controller);
        return true;
    }

    @Override
    public boolean buttonUp(Controller controller, int i) {
        checkController(controller);
        return true;
    }

    @Override
    public boolean axisMoved(Controller controller, int i, float v) {
        checkController(controller);
        return true;
    }
}
