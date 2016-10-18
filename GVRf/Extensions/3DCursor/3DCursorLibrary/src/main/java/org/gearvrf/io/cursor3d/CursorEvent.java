/* Copyright 2015 Samsung Electronics Co., LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gearvrf.io.cursor3d;

import android.view.KeyEvent;
import android.view.MotionEvent;

import org.gearvrf.GVRBaseSensor;
import org.gearvrf.GVRSceneObject;
import org.joml.Vector3f;

import java.util.List;

/**
 * This class provides all the information corresponding to events generated by
 * a {@link Cursor}.
 * <p/>
 * Register a {@link CursorEventListener} with the {@link Cursor} object to receive {@link
 * CursorEvent}s generated by that cursor.
 * <p/>
 * Make sure that the app consumes the {@link CursorEvent} data within the
 * lifetime of the callback as these objects are recycled.
 */
public class CursorEvent {
    private static final String TAG = CursorEvent.class.getSimpleName();
    private boolean isActive;
    private boolean isColliding;
    private Vector3f hitPoint;
    private boolean isOver;
    private GVRSceneObject object;
    private Cursor cursor;
    private KeyEvent keyEvent;

    // We take a leaf out of the MotionEvent book to implement linked
    // recycling of objects.
    private static final int MAX_RECYCLED = 5;
    private static final Object recyclerLock = new Object();

    private static int recyclerUsed;
    private static CursorEvent recyclerTop;
    private CursorEvent next;
    private List<MotionEvent> motionEvents;

    CursorEvent(){
        hitPoint = new Vector3f();
    }

    /**
     * Set the active flag on the {@link CursorEvent}
     *
     * @param isActive The active flag value.
     */
    void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Set the active flag on the {@link CursorEvent}
     *
     * @param value The active flag value.
     */
    void setColliding(boolean value) {
        this.isColliding = value;
    }

    /**
     * Set the {@link Cursor} on the {@link CursorEvent}
     *
     * @param cursor The {@link Cursor} that created this event
     */
    void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }


    /**
     * Set the coordinates of the intersection between the cursor and the
     * affected object.
     *
     * This call returns the coordinates of the hit point with respect to the center of the affected object.
     *
     * @param hitX X co-ordinate of the hit point
     * @param hitY Y co-ordinate of the hit point
     * @param hitZ Z co-ordinate of the hit point
     */
    void setHitPoint(float hitX, float hitY, float hitZ) {
        hitPoint.set(hitX, hitY, hitZ);
    }

    /**
     * Set the key event associated with this {@link CursorEvent}(if
     * there is one).
     *
     * @param keyEvent the {@link KeyEvent} to be set
     */

    void setKeyEvent(KeyEvent keyEvent) {
        this.keyEvent = keyEvent;
    }

    /**
     * The {@link GVRSceneObject} that triggered this {@link CursorEvent}.
     *
     * @param object The affected object.
     */
    void setObject(GVRSceneObject object) {
        this.object = object;
    }

    /**
     * This flag denotes that the {@link Cursor} "is over" the
     * affected object.
     *
     * @param isOver The value of the "is over" flag.
     */
    void setOver(boolean isOver) {
        this.isOver = isOver;
    }

    /**
     * Use this call to retrieve the affected object.
     *
     * @return The affected {@link GVRSceneObject} that caused this
     * {@link CursorEvent} to be triggered.
     */
    public GVRSceneObject getObject() {
        return object;
    }

    /**
     * Use this flag to detect if the input {@link Cursor} "is over" the {@link GVRSceneObject}.
     * Note that the definition of this value varies based on the {@link CursorType}.
     *
     * @return <code>true</code> if the input is over the corresponding
     * {@link GVRSceneObject}. The library returns <code>true</code>true when the cursor “is over”
     * an object and for the first instance when the cursor is no longer “over” the object, the
     * library delivers an isOver=false event.
     */
    public boolean isOver() {
        return isOver;
    }

    /**
     * Returns true if the {@link Cursor} is colliding with the corresponding
     * {@link GVRSceneObject}.
     *
     * Use {@link #getCursor()} to get a reference to the {@link Cursor} and {@link #getObject()}
     * to get a reference to the {@link GVRSceneObject}.
     *
     * Note that in case of a {@link LaserCursor} this call will return the same result as
     * {@link #isOver()}.
     *
     * @return <code>true</code> if the cursor is colliding and <code>false</code> if not.
     */
    public boolean isColliding() {
        return isColliding;
    }

    /**
     * Get the X component of the hit point.
     *
     * The values returned by this call persist only for the duration of the
     * {@link CursorEventListener#onEvent(CursorEvent)} call. Make sure to make a copy of this
     * value if you wish to use it past its lifetime.
     *
     * @return 'X' component of the hit point.
     */
    public float getHitX() {
        return hitPoint.x;
    }

    /**
     * Get the 'Y' component of the hit point.
     *
     * The values returned by this call persist only for the duration of the
     * {@link CursorEventListener#onEvent(CursorEvent)} call. Make sure to make a copy of this
     * value if you wish to use it past its lifetime.
     *
     * @return 'Y' component of the hit point.
     */
    public float getHitY() {
        return hitPoint.y;
    }

    /**
     * Get the 'Z' component of the hit point.
     * The values returned by this call persist only for the duration of the
     * {@link CursorEventListener#onEvent(CursorEvent)} call. Make sure to make a copy of this
     * value if you wish to use it past its lifetime.
     *
     * @return 'Z' component of the hit point.
     */
    public float getHitZ() {
        return hitPoint.z;
    }

    /**
     * Returns the hit point of the cursor and the affected {@link GVRSceneObject}
     *
     * @return The coordinates where the cursor intersects with the (@link GVRSceneObject}. The
     * values reported are relative to the center of the affected object.
     * @deprecated Returns a new float array every call which may lead to
     * frequent GC cycles. Use the more efficient call to {{@link #getHitX()}},
     * {{@link #getHitY()}} and {{@link #getHitZ()}}.
     */
    public float[] getHitPoint() {
        return new float[]{hitPoint.x, hitPoint.y, hitPoint.z};
    }

    /**
     * Get the key event associated with this {@link CursorEvent}(if
     * there is one).
     *
     * Note that not all {@link Cursor} objects report {@link KeyEvent}s. Check for a null before
     * processing the KeyEvent.
     *
     * @return the {@link KeyEvent} or <code>null</code> if there isn't one.
     */
    public KeyEvent getKeyEvent() {
        return keyEvent;
    }

    /**
     * Returns the active status of the {@link CursorEvent}.
     *
     * @return <code>true</code> when the provided input has an active state and
     * <code>false</code> otherwise.
     * <p/>
     * This usually denotes a button press on a given input event. The
     * actual interaction that causes the active state is defined by the
     * input provided.
     *
     * To know more about the key that led to this active state make use {@link #getKeyEvent()}
     * call.
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Returns the {@link Cursor} that generated this event.
     *
     * @return the {@link Cursor} object.
     */
    public Cursor getCursor() {
        return cursor;
    }

    /**
     * Use this method to return a {@link CursorEvent} for use.
     *
     * @return the {@link CursorEvent} object.
     */
    static CursorEvent obtain() {
        final CursorEvent event;
        synchronized (recyclerLock) {
            event = recyclerTop;
            if (event == null) {
                return new CursorEvent();
            }
            recyclerTop = event.next;
            recyclerUsed -= 1;
        }
        event.next = null;
        return event;
    }

    /**
     * Recycle the {@link CursorEvent} object.
     * <p/>
     * Make sure that the object is not used after this call.
     */
    final void recycle() {
        synchronized (recyclerLock) {
            if (recyclerUsed < MAX_RECYCLED) {
                recyclerUsed++;
                next = recyclerTop;
                recyclerTop = this;
            }
        }
    }

    void setMotionEvents(List<MotionEvent> motionEvents) {
        this.motionEvents = motionEvents;
    }

    public List<MotionEvent> getMotionEvents() {
        return motionEvents;
    }
}