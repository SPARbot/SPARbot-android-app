/*
 * Copyright 2014 Google Inc. All Rights Reserved.

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

package com.rugveddighe.remotestream;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.EyeTransform;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;
import com.rugveddighe.remotestream.http.WaitingRequestQueue;
import com.rugveddighe.remotestream.mjpeg.MjpegInputStream;
import com.rugveddighe.remotestream.mjpeg.MjpegPlayer;
import com.sveder.remotestream.R;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * A Cardboard application that streams video from a RPi and sends pitch & yaw to the RPi.
 */
public class StreamActivity extends CardboardActivity implements CardboardView.StereoRenderer, View.OnTouchListener {

    private static final String TAG = "StreamActivity";

    private static final String BEGIN_MSG = "Pull the trigger when you're ready";
    private float[] mEulerAngles = new float[3];
    private float[] mInitEulerAngles = new float[3];

    private final double PRECISION = Math.PI / 450.0;
    private final double RANGE = Math.PI / 2.0;

    private Vibrator mVibrator;

    private CardboardOverlayView mOverlayView;

    private MjpegPlayer mp;

    private int i = 0;


    private String baseUrl = "http://";

    private WaitingRequestQueue mQueue;

    private boolean tracking = false;

    public StreamActivity() {
    }

    /**
     * Sets the view to our CardboardView and initializes the transformation matrices we will use
     * to render our scene.
     * //@param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.common_ui);
        CardboardView cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
        cardboardView.setRenderer(this);
        setCardboardView(cardboardView);
        cardboardView.setOnTouchListener(this);

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        Intent i = getIntent();
        baseUrl += i.getExtras().get("ip");
        mOverlayView = (CardboardOverlayView) findViewById(R.id.overlay);
        mOverlayView.show3DToast(BEGIN_MSG);
        startPlayer();

        mQueue = new WaitingRequestQueue(this, baseUrl + ":8080/move");
        mQueue.addRequest(0f, 0f);
    }

    private void startPlayer(){
        String URL = baseUrl + ":5000/stream/video.mjpeg";
        mp = new MjpegPlayer(mOverlayView);
        (new DoRead()).execute(URL);
    }

    @Override
    public void onRendererShutdown(){Log.i(TAG, "onRendererShutdown");
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Log.i(TAG, "onSurfaceChanged");

    }

    /**
     * Creates the buffers we use to store information about the 3D world. OpenGL doesn't use Java
     * arrays, but rather needs data in a format it can understand. Hence we use ByteBuffers.
     *
     * @param config The EGL configuration used when creating the surface.
     */
    @Override
    public void onSurfaceCreated(EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated");
    }

    /**
     * Prepares OpenGL ES before we draw a frame.
     *
     * @param headTransform The head transformation in the new frame.
     */
    @Override
    public void onNewFrame(HeadTransform headTransform) {
        headTransform.getEulerAngles(mEulerAngles, 0);
        if (i % 100 == 0) {
            Log.i(TAG, mEulerAngles[0] + " " + mEulerAngles[1] + " " + mEulerAngles[2]);
        }
        i++;
        if (tracking) {
            shift();
            mQueue.addRequest(mEulerAngles[0], mEulerAngles[1]);
        }
    }

    private void shift() {
        for (int i = 0; i < mEulerAngles.length; i++) {
            mEulerAngles[i] -= mInitEulerAngles[i];
        }
    }

    /**
     * Draws a frame for an eye. The transformation for that eye (from the camera) is passed in as
     * a parameter.
     *
     * @param transform The transformations to apply to render this eye.
     */
    @Override
    public void onDrawEye(EyeTransform transform) {
    }

    @Override
    public void onFinishFrame(Viewport viewport) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!tracking) {
                Log.i(TAG, "starting tracking");
                mInitEulerAngles = mEulerAngles.clone();
                tracking = true;
                mOverlayView.fade3DToast();
                mQueue.start();
            } else {
                Log.i(TAG, "stopping tracking");
                tracking = false;
                mOverlayView.show3DToast(BEGIN_MSG);
                mQueue.stopAndRecenter();
            }
        }
        return true;
    }

    class DoRead extends AsyncTask<String, Void, MjpegInputStream> {

        @Override
        protected MjpegInputStream doInBackground(String... params) {
            return MjpegInputStream.read(params[0]);
        }

        protected void onPostExecute(MjpegInputStream result) {
            if (result == null){
                throw new RuntimeException("stream is null!!!");


            }
            mp.setSource(result);
            Log.i(TAG, "running mjpeg input stream");
        }
    }
}