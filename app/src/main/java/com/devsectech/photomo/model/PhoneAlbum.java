package com.devsectech.photomo.model;

import java.util.Vector;

public class PhoneAlbum {
    private Vector<PhonePhoto> albumPhotos;
    private String coverUri;
    private int id;
    private String name;

    public int getId() {
        return this.id;
    }

    public void setId(int i) {
        this.id = i;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getCoverUri() {
        return this.coverUri;
    }

    public void setCoverUri(String str) {
        this.coverUri = str;
    }

    public Vector<PhonePhoto> getAlbumPhotos() {
        if (this.albumPhotos == null) {
            this.albumPhotos = new Vector<>();
        }
        return this.albumPhotos;
    }

    public void setAlbumPhotos(Vector<PhonePhoto> vector) {
        this.albumPhotos = vector;
    }
}
