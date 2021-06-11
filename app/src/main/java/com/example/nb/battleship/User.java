package com.example.nb.battleship;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.Serializable;


public class User implements Serializable {

    private String name;
    transient private Bitmap photo;

    public User(String name, Bitmap photo) {
        this.name = name;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    //Functions for serializable bitmap
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        photo.compress(Bitmap.CompressFormat.JPEG,50,out);
        out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        photo = BitmapFactory.decodeStream(in);
        in.defaultReadObject();
    }
}
