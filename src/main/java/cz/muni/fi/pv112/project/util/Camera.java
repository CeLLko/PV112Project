package cz.muni.fi.pv112.project.util;


import cz.muni.fi.pv112.project.helpers.TerrainHelper;
import org.joml.Vector3f;

/**
 * SIMPLE PV112 CAMERA CLASS.
 * <p>
 * This is a VERY SIMPLE class that allows to very simply move with the camera.
 * It is not a perfect, brilliant, smart, or whatever implementation of a camera,
 * but it is sufficient for PV112 lectures.
 * <p>
 * Use left mouse button to change the point of view.
 * Use right mouse button to zoom in and zoom out.
 */
public class Camera {

    /// Constants that defines the behaviour of the camera
    ///		- Minimum elevation in radians
    private static final float MIN_ELEVATION = 0.1f;
    ///		- Maximum elevation in radians
    private static final float MAX_ELEVATION = 1.5f;
    ///		- Minimum distance from the point of interest
    private static final float MIN_DISTANCE = 25f;
    ///		- Maximum distance from the point of interest
    private static final float MAX_DISTANCE = 300f;
    ///		- Sensitivity of the mouse when changing elevation or direction angles
    private static final float ANGLE_SENSITIVITY = 0.008f;
    ///		- Sensitivity of the mouse when changing zoom
    private static final float ZOOM_SENSITIVITY = 0.003f;
    /// direction is an angle in which determines into which direction in xz plane I look.
    ///		- 0 degrees .. I look in -z direction
    ///		- 90 degrees .. I look in -x direction
    ///		- 180 degrees .. I look in +z direction
    ///		- 270 degrees .. I look in +x direction
    private float direction;
    /// elevation is an angle in which determines from which "height" I look.
    ///		- positive elevation .. I look from above the xz plane
    ///		- negative elevation .. I look from below the xz plane
    private float elevation;
    /// Distance from (0,0,0), the point at which I look
    private float distance;
    /// Final position of the eye in world space coordinates, for LookAt or shaders
    private Vector3f position;
    /// Last X and Y coordinates of the mouse cursor
    private int lastX;
    private int lastY;
    /// True or false if moused buttons are pressed and the user rotates/zooms the camera
    private boolean rotating;
    private boolean zooming;

    public void setTerrainHelper(TerrainHelper terrainHelper) {
        this.terrainHelper = terrainHelper;
    }

    private TerrainHelper terrainHelper;

    public Camera() {
        direction = 0.0f;
        elevation = 0.5f;
        distance = 100.0f;
        lastX = 0;
        lastY = 0;
        rotating = false;
        zooming = false;
        updateEyePosition();
    }

    /// Recomputes 'eye_position' from 'angle_direction', 'angle_elevation', and 'distance'
    private void updateEyePosition() {
        float x = (float) (distance * Math.cos(elevation) * -Math.sin(direction));
        float y = (float) (distance * Math.sin(elevation));
        float z = (float) (distance * Math.cos(elevation) * Math.cos(direction));
        position = new Vector3f(x, y, z);
       /* if(terrainHelper!= null)
        if(position.y > terrainHelper.getClosestVertex(position).y){
            position.y = terrainHelper.getClosestVertex(position).y;
        }*/
    }

    /// Called when the user presses or releases a mouse button (see MainWindow)
    public void updateMouseButton(Button button, boolean pressed, int x, int y) {
        // Left mouse button affects the angles
        if (button == Button.LEFT) {
            if (pressed) {
                lastX = x;
                lastY = y;
                rotating = true;
            } else {
                rotating = false;
            }
        }
        // Right mouse button affects the zoom
        if (button == Button.RIGHT) {
            if (pressed) {
                lastX = x;
                lastY = y;
                zooming = true;
            } else {
                zooming = false;
            }
        }
        // Middle mouse button affects the zoom
        if (button == Button.MIDDLE) {
            if (pressed) {
                lastX = x;
                lastY = y;
                zooming = true;
            } else {
                zooming = false;
            }
        }
    }

    /// Called when the user moves with the mouse cursor (see MainWindow)
    public void updateMousePosition(int x, int y) {
        float dx = (float) (x - lastX);
        float dy = (float) (y - lastY);
        lastX = x;
        lastY = y;

        if (rotating) {
            direction += dx * ANGLE_SENSITIVITY;
            elevation += dy * ANGLE_SENSITIVITY;

            // Clamp the results
            if (elevation > MAX_ELEVATION) {
                elevation = MAX_ELEVATION;
            }
            if (elevation < MIN_ELEVATION) {
                elevation = MIN_ELEVATION;
            }
        }
        if (zooming) {
            distance *= (1.0f + dy * ZOOM_SENSITIVITY);

            // Clamp the results
            if (distance < MIN_DISTANCE) {
                distance = MIN_DISTANCE;
            }
            if (distance > MAX_DISTANCE) {
                distance = MAX_DISTANCE;
            }
        }

        updateEyePosition();
    }

    /// Returns the position of the eye in world space coordinates
    public Vector3f getEyePosition() {
        return position;
    }

    public static enum Button {
        LEFT, RIGHT, MIDDLE;
    }

    public int getLastX() {
        return lastX;
    }

    public int getLastY() {
        return lastY;
    }
}
