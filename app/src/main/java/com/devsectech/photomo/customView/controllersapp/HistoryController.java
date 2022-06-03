package com.devsectech.photomo.customView.controllersapp;

import android.graphics.Bitmap;
import com.devsectech.photomo.customView.beans.Ponto;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

public class HistoryController {
    public static final int TAMANHO_MAX_PILHA_INIT = 30;
    public static HistoryController instance;
    public int idGroupAtual = 0;
    public int idGroupMax = 300;
    public Stack<ObjetoHistoria> pilhaDesfazer = new Stack<>();
    public Stack<ObjetoHistoria> pilhaRefazer = new Stack<>();

    public static class ObjetoHistoria {
        public boolean f3031d;
        public boolean f3032e = false;
        public int idGroup = -1;
        public Bitmap mask;
        public Ponto ponto;

        public ObjetoHistoria(int i, Ponto ponto2, boolean z) {
            this.idGroup = i;
            this.ponto = ponto2;
            this.f3031d = z;
        }

        public Bitmap getMask() {
            return this.mask;
        }

        public Ponto getPonto() {
            return this.ponto;
        }

        public boolean isMascara() {
            return this.mask != null;
        }

        public boolean mo6833a() {
            return this.f3032e;
        }

        public boolean mo6834b() {
            return this.f3031d;
        }

        public ObjetoHistoria(int i, Bitmap bitmap, boolean z) {
            this.mask = bitmap;
            this.f3031d = z;
            this.idGroup = i;
            this.f3032e = true;
        }
    }

    public HistoryController() {
        clear();
    }

    public static HistoryController getInstance() {
        if (instance == null) {
            instance = new HistoryController();
        }
        return instance;
    }

    private void verificarLimite() {
        int i = this.idGroupAtual;
        int i2 = this.idGroupMax;
        if (i >= i2) {
            this.idGroupMax = i2 + 1;
            if (this.pilhaDesfazer.size() <= 1) {
                do {
                } while (((ObjetoHistoria) this.pilhaDesfazer.get(0)).idGroup == ((ObjetoHistoria) this.pilhaDesfazer.remove(0)).idGroup);
            }
        }
    }

    public void addHistoria(Ponto ponto, boolean z) {
        this.pilhaDesfazer.push(new ObjetoHistoria(this.idGroupAtual, ponto, z));
        this.pilhaRefazer.clear();
        verificarLimite();
        this.idGroupAtual++;
    }

    public void clear() {
        this.idGroupAtual = 0;
        this.pilhaRefazer.clear();
        this.pilhaDesfazer.clear();
        this.idGroupMax = 300;
    }

    public List<ObjetoHistoria> desfazer() {
        ObjetoHistoria objetoHistoria;
        ObjetoHistoria objetoHistoria2;
        CopyOnWriteArrayList copyOnWriteArrayList = new CopyOnWriteArrayList();
        if (!this.pilhaDesfazer.isEmpty()) {
            do {
                objetoHistoria = null;
                objetoHistoria2 = (ObjetoHistoria) this.pilhaDesfazer.pop();
                copyOnWriteArrayList.add(objetoHistoria2);
                this.idGroupAtual = objetoHistoria2.idGroup;
                this.pilhaRefazer.push(objetoHistoria2);
                if (!this.pilhaDesfazer.isEmpty()) {
                    objetoHistoria = (ObjetoHistoria) this.pilhaDesfazer.peek();
                }
                if (objetoHistoria == null) {
                    break;
                }
            } while (objetoHistoria.idGroup == objetoHistoria2.idGroup);
        }
        return copyOnWriteArrayList;
    }

    public List<ObjetoHistoria> refazer() {
        ObjetoHistoria objetoHistoria;
        ObjetoHistoria objetoHistoria2;
        CopyOnWriteArrayList copyOnWriteArrayList = new CopyOnWriteArrayList();
        if (!this.pilhaRefazer.isEmpty()) {
            do {
                objetoHistoria = null;
                objetoHistoria2 = (ObjetoHistoria) this.pilhaRefazer.pop();
                copyOnWriteArrayList.add(objetoHistoria2);
                this.idGroupAtual = objetoHistoria2.idGroup + 1;
                this.pilhaDesfazer.push(objetoHistoria2);
                if (!this.pilhaRefazer.isEmpty()) {
                    objetoHistoria = (ObjetoHistoria) this.pilhaRefazer.peek();
                }
                if (objetoHistoria == null) {
                    break;
                }
            } while (objetoHistoria.idGroup == objetoHistoria2.idGroup);
        }
        return copyOnWriteArrayList;
    }

    public boolean temDesfazer() {
        return !this.pilhaDesfazer.isEmpty();
    }

    public boolean temRefazer() {
        return !this.pilhaRefazer.isEmpty();
    }

    public void addHistoria(List<Ponto> list, boolean z) {
        for (Ponto objetoHistoria : list) {
            this.pilhaDesfazer.push(new ObjetoHistoria(this.idGroupAtual, objetoHistoria, z));
        }
        this.pilhaRefazer.clear();
        verificarLimite();
        this.idGroupAtual++;
    }

    public void addHistoria(Bitmap bitmap, boolean z) {
        this.pilhaDesfazer.push(new ObjetoHistoria(this.idGroupAtual, bitmap, z));
        this.pilhaRefazer.clear();
        verificarLimite();
        this.idGroupAtual++;
    }
}
