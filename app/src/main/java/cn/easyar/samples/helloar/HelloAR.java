//================================================================================================================================
//
//  Copyright (c) 2015-2017 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
//  EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
//  and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
//
//================================================================================================================================

package cn.easyar.samples.helloar;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import cn.easyar.CameraCalibration;
import cn.easyar.CameraDevice;
import cn.easyar.CameraDeviceFocusMode;
import cn.easyar.CameraDeviceType;
import cn.easyar.CameraFrameStreamer;
import cn.easyar.Frame;
import cn.easyar.FunctorOfVoidFromPointerOfTargetAndBool;
import cn.easyar.ImageTarget;
import cn.easyar.ImageTracker;
import cn.easyar.Matrix34F;
import cn.easyar.Matrix44F;
import cn.easyar.Renderer;
import cn.easyar.StorageType;
import cn.easyar.Target;
import cn.easyar.TargetInstance;
import cn.easyar.TargetStatus;
import cn.easyar.Vec2I;
import cn.easyar.Vec4I;

import static cn.easyar.engine.EasyAR.getApplicationContext;

public class HelloAR {
    public final float SCREEN_WIDTH = 1280.0F;
    public final float SCREEN_HEIGHT = 720.0F;

    private CameraDevice camera;
    private CameraFrameStreamer streamer;
    private ArrayList<ImageTracker> trackers;
    private Renderer videobg_renderer;
    private BoxRenderer box_renderer;
    private boolean viewport_changed = false;
    private Vec2I view_size = new Vec2I(0, 0);
    private int rotation = 0;
    private Vec4I viewport = new Vec4I(0, 0, 1280, 720);
    private Context context;

    private List<ArListener> listeners;

    public HelloAR(Context context) {
        this.context = context;
        trackers = new ArrayList<ImageTracker>();
        listeners = new ArrayList<>();
    }

    public void addListenr(ArListener listener) {
        listeners.add(listener);
    }

    private void loadFromImage(ImageTracker tracker, String path) {
        ImageTarget target = new ImageTarget();
        String jstr = "{\n"
                + "  \"images\" :\n"
                + "  [\n"
                + "    {\n"
                + "      \"image\" : \"" + path + "\",\n"
                + "      \"name\" : \"" + path.substring(0, path.indexOf(".")) + "\"\n"
                + "    }\n"
                + "  ]\n"
                + "}";
        target.setup(jstr, StorageType.Assets | StorageType.Json, "");
        tracker.loadTarget(target, new FunctorOfVoidFromPointerOfTargetAndBool() {
            @Override
            public void invoke(Target target, boolean status) {
                Log.i("HelloAR", String.format("load target (%b): %s (%d)", status, target.name(), target.runtimeID()));
            }
        });
    }

    private void loadFromJsonFile(ImageTracker tracker, String path, String targetname) {
        ImageTarget target = new ImageTarget();
        target.setup(path, StorageType.Assets, targetname);
        tracker.loadTarget(target, new FunctorOfVoidFromPointerOfTargetAndBool() {
            @Override
            public void invoke(Target target, boolean status) {
                Log.i("HelloAR", String.format("load target (%b): %s (%d)", status, target.name(), target.runtimeID()));
            }
        });
    }

    private void loadAllFromJsonFile(ImageTracker tracker, String path) {
        for (ImageTarget target : ImageTarget.setupAll(path, StorageType.Assets)) {
            tracker.loadTarget(target, new FunctorOfVoidFromPointerOfTargetAndBool() {
                @Override
                public void invoke(Target target, boolean status) {
                    Log.i("HelloAR", String.format("load target (%b): %s (%d)", status, target.name(), target.runtimeID()));
                }
            });
        }
    }

    public boolean initialize() {
        camera = new CameraDevice();
        streamer = new CameraFrameStreamer();
        streamer.attachCamera(camera);

        boolean status = true;
        status &= camera.open(CameraDeviceType.Default);
        camera.setSize(new Vec2I(1280, 720));

        if (!status) {
            return status;
        }
        ImageTracker tracker = new ImageTracker();
        tracker.attachStreamer(streamer);
        loadFromJsonFile(tracker, "targets.json", "argame");
        loadFromJsonFile(tracker, "targets.json", "idback");
        loadAllFromJsonFile(tracker, "targets2.json");
        loadFromImage(tracker, "namecard.jpg");
        trackers.add(tracker);

        return status;
    }

    public void dispose() {
        for (ImageTracker tracker : trackers) {
            tracker.dispose();
        }
        trackers.clear();
        box_renderer = null;
        if (videobg_renderer != null) {
            videobg_renderer.dispose();
            videobg_renderer = null;
        }
        if (streamer != null) {
            streamer.dispose();
            streamer = null;
        }
        if (camera != null) {
            camera.dispose();
            camera = null;
        }
    }

    public boolean start() {
        boolean status = true;
        status &= (camera != null) && camera.start();
        status &= (streamer != null) && streamer.start();
        camera.setFocusMode(CameraDeviceFocusMode.Continousauto);
        for (ImageTracker tracker : trackers) {
            status &= tracker.start();
        }

        return status;
    }

    public boolean stop() {
        boolean status = true;
        for (ImageTracker tracker : trackers) {
            status &= tracker.stop();
        }
        status &= (streamer != null) && streamer.stop();
        status &= (camera != null) && camera.stop();
        return status;
    }

    public void initGL() {
        if (videobg_renderer != null) {
            videobg_renderer.dispose();
        }
        videobg_renderer = new Renderer();
        box_renderer = new BoxRenderer();
        box_renderer.init();
    }

    public void resizeGL(int width, int height) {
        view_size = new Vec2I(width, height);
        viewport_changed = true;
    }

    private void updateViewport() {
        CameraCalibration calib = camera != null ? camera.cameraCalibration() : null;
        int rotation = calib != null ? calib.rotation() : 0;
        if (rotation != this.rotation) {
            this.rotation = rotation;
            viewport_changed = true;
        }
        if (viewport_changed) {
            Vec2I size = new Vec2I(1, 1);
            if ((camera != null) && camera.isOpened()) {
                size = camera.size();
            }
            if (rotation == 90 || rotation == 270) {
                size = new Vec2I(size.data[1], size.data[0]);
            }
            float scaleRatio = Math.max((float) view_size.data[0] / (float) size.data[0], (float) view_size.data[1] / (float) size.data[1]);
            Vec2I viewport_size = new Vec2I(Math.round(size.data[0] * scaleRatio), Math.round(size.data[1] * scaleRatio));
            viewport = new Vec4I((view_size.data[0] - viewport_size.data[0]) / 2, (view_size.data[1] - viewport_size.data[1]) / 2, viewport_size.data[0], viewport_size.data[1]);

            if ((camera != null) && camera.isOpened())
                viewport_changed = false;
        }
    }

    public void render() {
        GLES20.glClearColor(0.f, 0.f, 0.f, 0.f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        if (videobg_renderer != null) {
            Vec4I default_viewport = new Vec4I(0, 0, view_size.data[0], view_size.data[1]);
            GLES20.glViewport(default_viewport.data[0], default_viewport.data[1], default_viewport.data[2], default_viewport.data[3]);
            if (videobg_renderer.renderErrorMessage(default_viewport)) {
                return;
            }
        }

        if (streamer == null) {
            return;
        }
        Frame frame = streamer.peek();
        if (frame.targetInstances().size() != 0) {
            CharSequence text = "Image detected";

        }
        try {
            updateViewport();
            GLES20.glViewport(viewport.data[0], viewport.data[1], viewport.data[2], viewport.data[3]);

            if (videobg_renderer != null ) {
//                videobg_renderer.render(frame, viewport);
            }
            int amountTargetsTracked = frame.targetInstances().size();
//            Log.e("Target Instances Amount", amountTargetsTracked + "");

            boolean visible = amountTargetsTracked == 1;    //
            for (ArListener listener : listeners) {
                listener.updateVisibility(visible);
            }


            for (TargetInstance targetInstance : frame.targetInstances()) {
                int status = targetInstance.status();
//                Log.e("tracked?: ", "" + status);

                if (status == TargetStatus.Tracked) {
                    Target target = targetInstance.target();
                    ImageTarget imagetarget = target instanceof ImageTarget ? (ImageTarget) (target) : null;
                    if (imagetarget == null) {
                        continue;
                    }
                    Matrix34F positionInMatrix34 = targetInstance.pose();

                    showCoordMatrix(positionInMatrix34.data, true);
                    Vec2I position = transformCoordinates(positionInMatrix34.data);
                    for (ArListener listener : listeners) {
                        listener.updatePosition(position.data[0], position.data[1]);
                    }


                    if (box_renderer != null) {
                       // box_renderer.render(camera.projectionGL(0.2f, 500.f), targetInstance.poseGL(), imagetarget.size());
                    }
                }
            }
        } finally {
//            Log.e("tracked?: ", "disposing");
            frame.dispose();
        }
    }

    public void showCoordMatrix(float[] m, boolean type) {

        if (type) {
            Log.e("Zeilenweise 1", m[0] + " " + m[1] + " " + m[2] + " " + m[3]);
            Log.e("Zeilenweise 2", m[4] + " " + m[5] + " " + m[6] + " " + m[7]);
            Log.e("Zeilenweise 3", m[8] + " " + m[9] + " " + m[10] + " " + m[11]);
        } else {
            Log.e("Spaltenweise 1", m[0] + " " + m[3] + " " + m[6] + " " + m[9]);
            Log.e("Spaltenweise 2", m[1] + " " + m[4] + " " + m[7] + " " + m[10]);
            Log.e("Spaltenweise 3", m[2] + " " + m[5] + " " + m[8] + " " + m[11]);

        }
    }

    public Vec2I transformCoordinates(float[] data) {
        float xScreen, yScreen;
        float u, v;
        u = data[3] / data[11];
        v = data[7] / data[11];
        xScreen = u * SCREEN_WIDTH + SCREEN_WIDTH/2;
        yScreen = v * SCREEN_WIDTH + SCREEN_HEIGHT/2;
        return new Vec2I((int) xScreen, (int) yScreen);
    }
}
