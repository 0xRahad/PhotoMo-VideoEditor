package com.devsectech.photomo.model;

public class EffectData {
    public String assetFolderName;
    public String effectFolderNameFPath;
    public int effectId;
    public String effectName;
    public int effectThumb;
    public boolean isSelected;

    public EffectData(int i, String str, String str2, boolean z, int i2, String str3) {
        this.effectId = i;
        this.effectName = str;
        this.effectFolderNameFPath = str2;
        this.isSelected = z;
        this.effectThumb = i2;
        this.assetFolderName = str3;
    }

    public String getAssetFolderName() {
        return this.assetFolderName;
    }

    public String getEffectFolderNameFPath() {
        return this.effectFolderNameFPath;
    }

    public String getEffectGIFPath() {
        return this.effectFolderNameFPath;
    }

    public int getEffectId() {
        return this.effectId;
    }

    public String getEffectName() {
        return this.effectName;
    }

    public int getEffectThumb() {
        return this.effectThumb;
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setAssetFolderName(String str) {
        this.assetFolderName = str;
    }

    public void setEffectFolderNameFPath(String str) {
        this.effectFolderNameFPath = str;
    }

    public void setEffectGIFPath(String str) {
        this.effectFolderNameFPath = str;
    }

    public void setEffectId(int i) {
        this.effectId = i;
    }

    public void setEffectName(String str) {
        this.effectName = str;
    }

    public void setEffectThumb(int i) {
        this.effectThumb = i;
    }

    public void setSelected(boolean z) {
        this.isSelected = z;
    }
}
