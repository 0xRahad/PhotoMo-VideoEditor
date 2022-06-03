package com.devsectech.photomo.customView.controllersapp.OpenGL;

import java.util.Vector;
import javax.microedition.khronos.opengles.GL10;

public class Group extends Mesh {
    public final Vector<Mesh> mChildren = new Vector<>();

    public void add(int i, Mesh mesh) {
        this.mChildren.add(i, mesh);
    }

    public void clear() {
        this.mChildren.clear();
    }

    public void draw(GL10 gl10) {
        int size = this.mChildren.size();
        for (int i = 0; i < size; i++) {
            ((Mesh) this.mChildren.get(i)).draw(gl10);
        }
    }

    public Mesh get(int i) {
        return (Mesh) this.mChildren.get(i);
    }

    public Mesh remove(int i) {
        return (Mesh) this.mChildren.remove(i);
    }

    public int size() {
        return this.mChildren.size();
    }

    public boolean add(Mesh mesh) {
        return this.mChildren.add(mesh);
    }

    public boolean remove(Object obj) {
        return this.mChildren.remove(obj);
    }
}
