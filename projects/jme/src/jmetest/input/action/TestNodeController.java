/*
 * Copyright (c) 2003-2006 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jmetest.input.action;

import com.jme.app.BaseGame;
import com.jme.input.InputHandler;
import com.jme.input.NodeHandler;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.CameraNode;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.Point;
import com.jme.scene.TriMesh;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.Timer;
import com.jme.util.geom.BufferUtils;

/**
 * <code>TestNodeController</code> provides a test for control of a node, in
 * this case a camera node.
 * @author Mark Powell
 * @version $Id: TestNodeController.java,v 1.13 2006/06/23 22:31:58 nca Exp $
 */
public class TestNodeController extends BaseGame {
    private Node scene;
    private CameraNode cameraNode;
    private Line l;
    private Point p;
    private TriMesh t;
    private TriMesh t2;
    private InputHandler input;
    /** High resolution timer for jME. */
    protected Timer timer;

    protected void update(float interpolation) {
        /** Recalculate the framerate. */
        timer.update();
          /** Update tpf to time per frame according to the Timer. */
        float tpf = timer.getTimePerFrame();
          /** Check for key/mouse updates. */
        input.update(tpf);
        scene.updateGeometricState(tpf, true);

    }

    /**
     * Render the scene
     * @see com.jme.app.SimpleGame#render()
     */
    protected void render(float interpolation) {
        display.getRenderer().clearBuffers();
        display.getRenderer().draw(scene);
    }

    /**
     * set up the display system and camera.
     * @see com.jme.app.SimpleGame#initSystem()
     */
    protected void initSystem() {
        Camera cam = null;
        try {
            display = DisplaySystem.getDisplaySystem( properties.getRenderer() );
            display.createWindow(
                    properties.getWidth(),
                    properties.getHeight(),
                    properties.getDepth(),
                    properties.getFreq(),
                    properties.getFullscreen() );
            cam =
                    display.getRenderer().createCamera(
                            properties.getWidth(),
                            properties.getHeight() );

        } catch ( JmeException e ) {
            e.printStackTrace();
            System.exit( 1 );
        }
        ColorRGBA blackColor = new ColorRGBA();
        blackColor.r = 0;
        blackColor.g = 0;
        blackColor.b = 0;
        display.getRenderer().setBackgroundColor( blackColor );
        cam.setFrustum( 1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f );
        Vector3f loc = new Vector3f( 4.0f, 0.0f, 0.0f );
        Vector3f left = new Vector3f( 0.0f, -1.0f, 0.0f );
        Vector3f up = new Vector3f( 0.0f, 0.0f, 1.0f );
        Vector3f dir = new Vector3f( -1.0f, 0f, 0.0f );
        cam.setFrame( loc, left, up, dir );
        cam.update();

        display.getRenderer().setCamera( cam );

        cameraNode = new CameraNode( "Camera Node", cam );
        cameraNode.setLocalTranslation( new Vector3f( 0, 0, -250 ) );
        cameraNode.updateWorldData( 0 );

        input = new NodeHandler( cameraNode, 50f, .5f );

        /** Get a high resolution timer for FPS updates. */
        timer = Timer.getTimer();


    }

    /**
     * set up the scene
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void initGame() {
        Vector3f[] vertex = new Vector3f[1000];
        ColorRGBA[] color = new ColorRGBA[1000];
        for (int i = 0; i < 1000; i++) {
            vertex[i] = new Vector3f();
            vertex[i].x = (float) Math.random() * 50;
            vertex[i].y = (float) Math.random() * 50;
            vertex[i].z = (float) Math.random() * 50;
            color[i] = new ColorRGBA();
            color[i].r = (float) Math.random();
            color[i].g = (float) Math.random();
            color[i].b = (float) Math.random();
            color[i].a = 1.0f;
        }

        l = new Line("Line Group", vertex, null, color, null);
        l.setLocalTranslation(new Vector3f(-100.0f, -25, -25));

        Vector3f[] vertex2 = new Vector3f[1000];
        ColorRGBA[] color2 = new ColorRGBA[1000];
        for (int i = 0; i < 1000; i++) {
            vertex2[i] = new Vector3f();
            vertex2[i].x = (float) Math.random() * -50 + 25;
            vertex2[i].y = (float) Math.random() * 50 - 25;
            vertex2[i].z = (float) Math.random() * 50 - 25;

            color2[i] = new ColorRGBA();
            color2[i].r = (float) Math.random();
            color2[i].g = (float) Math.random();
            color2[i].b = (float) Math.random();
            color2[i].a = 1.0f;
        }

        p = new Point("Point Group", vertex2, null, color2, null);
        p.setLocalTranslation(new Vector3f(100f, 10, 10));
        p.setPointSize(5);
        p.setAntialiased(true);
        Node pointNode = new Node("Point Node");
        pointNode.attachChild(p);

        Vector3f[] verts = new Vector3f[3];
        ColorRGBA[] color3 = new ColorRGBA[3];

        verts[0] = new Vector3f();
        verts[0].x = -50;
        verts[0].y = 0;
        verts[0].z = 0;
        verts[1] = new Vector3f();
        verts[1].x = -50;
        verts[1].y = 25;
        verts[1].z = 25;
        verts[2] = new Vector3f();
        verts[2].x = -50;
        verts[2].y = 25;
        verts[2].z = 0;

        color3[0] = new ColorRGBA();
        color3[0].r = 1;
        color3[0].g = 0;
        color3[0].b = 0;
        color3[0].a = 1;
        color3[1] = new ColorRGBA();
        color3[1].r = 0;
        color3[1].g = 1;
        color3[1].b = 0;
        color3[1].a = 1;
        color3[2] = new ColorRGBA();
        color3[2].r = 0;
        color3[2].g = 0;
        color3[2].b = 1;
        color3[2].a = 1;
        int[] indices = { 0, 1, 2 };

        t = new TriMesh("Triangle 1", BufferUtils.createFloatBuffer(verts), null, BufferUtils.createFloatBuffer(color3), null, BufferUtils.createIntBuffer(indices));
        t.setLocalTranslation(new Vector3f(-100, 0, 0));

        pointNode.attachChild(t);
        pointNode.setLocalTranslation(new Vector3f(0, 0, 0));

        //should be culled:

        Vector3f[] verts2 = new Vector3f[3];
        ColorRGBA[] color4 = new ColorRGBA[3];

        verts2[0] = new Vector3f();
        verts2[0].x = -50;
        verts2[0].y = 0;
        verts2[0].z = 0;
        verts2[1] = new Vector3f();
        verts2[1].x = -50;
        verts2[1].y = 25;
        verts2[1].z = 25;
        verts2[2] = new Vector3f();
        verts2[2].x = -50;
        verts2[2].y = 25;
        verts2[2].z = 0;

        color4[0] = new ColorRGBA();
        color4[0].r = 1;
        color4[0].g = 0;
        color4[0].b = 0;
        color4[0].a = 1;
        color4[1] = new ColorRGBA();
        color4[1].r = 0;
        color4[1].g = 1;
        color4[1].b = 0;
        color4[1].a = 1;
        color4[2] = new ColorRGBA();
        color4[2].r = 0;
        color4[2].g = 0;
        color4[2].b = 1;
        color4[2].a = 1;
        int[] indices2 = { 0, 1, 2 };

        t2 = new TriMesh("Triangle 2", BufferUtils.createFloatBuffer(verts2), null, BufferUtils.createFloatBuffer(color4), null, BufferUtils.createIntBuffer(indices2));
        t2.setLocalTranslation(new Vector3f(100, 0, 0));

        scene = new Node("Scene graph Node");
        scene.attachChild(l);
        scene.attachChild(pointNode);
        scene.attachChild(t2);
		scene.attachChild(cameraNode);

        scene.updateGeometricState(0.0f, true);

    }

    /**
     * not used.
     * @see com.jme.app.SimpleGame#reinit()
     */
    protected void reinit() {
    }

    /**
     * not used.
     * @see com.jme.app.SimpleGame#cleanup()
     */
    protected void cleanup() {
    }

    public static void main(String[] args) {
        TestNodeController app = new TestNodeController();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }
}
