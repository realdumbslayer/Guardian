package com.example.library.callback;

public interface DragStateListener {

    void onDragStart();

    void onDragEnd(boolean isMenuOpened);
}
