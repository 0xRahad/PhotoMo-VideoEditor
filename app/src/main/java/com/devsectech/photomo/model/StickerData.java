package com.devsectech.photomo.model;

public class StickerData {
    public boolean isSelected;
    public String stickerFolderNameFPath;
    public int stickerId;
    public String stickerName;
    public int stickerThumb;

    public StickerData(int i, String str, String str2, boolean z, int i2) {
        this.stickerId = i;
        this.stickerName = str;
        this.stickerFolderNameFPath = str2;
        this.isSelected = z;
        this.stickerThumb = i2;
    }

    public String getStickerFolderNameFPath() {
        return this.stickerFolderNameFPath;
    }

    public int getStickerId() {
        return this.stickerId;
    }

    public String getStickerName() {
        return this.stickerName;
    }

    public int getStickerThumb() {
        return this.stickerThumb;
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean z) {
        this.isSelected = z;
    }

    public void setStickerFolderNameFPath(String str) {
        this.stickerFolderNameFPath = str;
    }

    public void setStickerId(int i) {
        this.stickerId = i;
    }

    public void setStickerName(String str) {
        this.stickerName = str;
    }

    public void setStickerThumb(int i) {
        this.stickerThumb = i;
    }
}
