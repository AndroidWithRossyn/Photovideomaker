package com.androworld.musicvideo;

public interface OnProgressReceiver {
    void onImageProgressFrameUpdate(float f);

    void onProgressFinish(String str);

    void onVideoProgressFrameUpdate(float f);
}
