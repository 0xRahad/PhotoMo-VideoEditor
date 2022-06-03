package com.devsectech.photomo.customView;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.util.Log;

import com.devsectech.photomo.customView.beans.Ponto;
import com.devsectech.photomo.customView.beans.TrianguloBitmap;
import com.devsectech.photomo.customView.beans.Vertice;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Delaunay {
    public Bitmap imagemDelaunay;
    public List<Ponto> listaPontos = new CopyOnWriteArrayList();
    public List<TrianguloBitmap> listaTriangulos = new CopyOnWriteArrayList();

    public Delaunay(Bitmap bitmap) {
        this.imagemDelaunay = bitmap;
        this.listaTriangulos.addAll(TrianguloBitmap.cutInitialBitmap(bitmap));
    }

    private List<TrianguloBitmap> flipTriangulos(TrianguloBitmap trianguloBitmap, TrianguloBitmap trianguloBitmap2) {
        CopyOnWriteArrayList copyOnWriteArrayList = new CopyOnWriteArrayList();
        this.listaTriangulos.remove(trianguloBitmap);
        this.listaTriangulos.remove(trianguloBitmap2);
        trianguloBitmap.clear();
        trianguloBitmap2.clear();
        Vertice arestaComum = trianguloBitmap.getArestaComum(trianguloBitmap2);
        Ponto pontoForaVertice = trianguloBitmap.getPontoForaVertice(arestaComum);
        Ponto pontoForaVertice2 = trianguloBitmap2.getPontoForaVertice(arestaComum);
        TrianguloBitmap trianguloBitmap3 = new TrianguloBitmap(this.imagemDelaunay, pontoForaVertice, arestaComum.f153p1, pontoForaVertice2);
        TrianguloBitmap trianguloBitmap4 = new TrianguloBitmap(this.imagemDelaunay, pontoForaVertice, arestaComum.f154p2, pontoForaVertice2);
        this.listaTriangulos.add(trianguloBitmap3);
        this.listaTriangulos.add(trianguloBitmap4);
        copyOnWriteArrayList.add(trianguloBitmap3);
        copyOnWriteArrayList.add(trianguloBitmap4);
        return copyOnWriteArrayList;
    }

    private TrianguloBitmap getTrianguloVizinho(TrianguloBitmap trianguloBitmap, Vertice vertice) {
        for (TrianguloBitmap trianguloBitmap2 : this.listaTriangulos) {
            if (!trianguloBitmap2.equals(trianguloBitmap) && trianguloBitmap2.contemVertice(vertice.f153p1, vertice.f154p2)) {
                return trianguloBitmap2;
            }
        }
        return null;
    }

    private boolean isArestaIlegal(TrianguloBitmap trianguloBitmap, Ponto ponto) {
        return Boolean.valueOf(trianguloBitmap.pontoNoCircunscrito(ponto)).booleanValue();
    }

    private void legalizeAresta(Ponto ponto, TrianguloBitmap trianguloBitmap, Vertice vertice) {
        TrianguloBitmap trianguloVizinho = getTrianguloVizinho(trianguloBitmap, vertice);
        if (trianguloVizinho != null) {
            Ponto pontoForaVertice = trianguloVizinho.getPontoForaVertice(vertice);
            double anguloPontoOpostoAresta = trianguloVizinho.getAnguloPontoOpostoAresta(vertice) + trianguloBitmap.getAnguloPontoOpostoAresta(vertice);
            if (isArestaIlegal(trianguloBitmap, pontoForaVertice) || anguloPontoOpostoAresta > 3.141592653589793d) {
                List flipTriangulos = flipTriangulos(trianguloVizinho, trianguloBitmap);
                Vertice vertice2 = new Vertice(ponto, pontoForaVertice);
                Ponto pontoForaVertice2 = ((TrianguloBitmap) flipTriangulos.get(0)).getPontoForaVertice(vertice2);
                Ponto pontoForaVertice3 = ((TrianguloBitmap) flipTriangulos.get(1)).getPontoForaVertice(vertice2);
                legalizeAresta(ponto, (TrianguloBitmap) flipTriangulos.get(0), new Vertice(pontoForaVertice2, pontoForaVertice));
                legalizeAresta(ponto, (TrianguloBitmap) flipTriangulos.get(1), new Vertice(pontoForaVertice, pontoForaVertice3));
            }
        }
    }

    public void addPonto(Ponto ponto) {
        TrianguloBitmap trianguloBitmap;
        this.listaPontos.add(ponto);
        Iterator it = this.listaTriangulos.iterator();
        while (true) {
            if (!it.hasNext()) {
                trianguloBitmap = null;
                break;
            }
            trianguloBitmap = (TrianguloBitmap) it.next();
            if (trianguloBitmap != null && trianguloBitmap.contemPontoDentro(ponto)) {
                break;
            }
        }
        if (trianguloBitmap != null) {
            TrianguloBitmap trianguloBitmap2 = new TrianguloBitmap(this.imagemDelaunay, trianguloBitmap.getP1(), trianguloBitmap.getP2(), ponto);
            TrianguloBitmap trianguloBitmap3 = new TrianguloBitmap(this.imagemDelaunay, trianguloBitmap.getP2(), trianguloBitmap.getP3(), ponto);
            TrianguloBitmap trianguloBitmap4 = new TrianguloBitmap(this.imagemDelaunay, trianguloBitmap.getP3(), trianguloBitmap.getP1(), ponto);
            this.listaTriangulos.add(trianguloBitmap2);
            this.listaTriangulos.add(trianguloBitmap3);
            this.listaTriangulos.add(trianguloBitmap4);
            this.listaTriangulos.remove(trianguloBitmap);
            legalizeAresta(ponto, trianguloBitmap2, new Vertice(trianguloBitmap.getP1(), trianguloBitmap.getP2()));
            legalizeAresta(ponto, trianguloBitmap3, new Vertice(trianguloBitmap.getP2(), trianguloBitmap.getP3()));
            legalizeAresta(ponto, trianguloBitmap4, new Vertice(trianguloBitmap.getP3(), trianguloBitmap.getP1()));
        }
    }

    public void addPontoEstudo(Ponto ponto) {
        TrianguloBitmap trianguloBitmap;
        TrianguloBitmap trianguloBitmap2 = null;
        this.listaPontos.add(ponto);
        Iterator it = this.listaTriangulos.iterator();
        while (true) {
            if (!it.hasNext()) {
                trianguloBitmap = null;
                break;
            }
            trianguloBitmap = (TrianguloBitmap) it.next();
            if (trianguloBitmap.contemPontoDentro(ponto)) {
                break;
            }
        }
        if (trianguloBitmap != null) {
            CopyOnWriteArrayList copyOnWriteArrayList = new CopyOnWriteArrayList();
            TrianguloBitmap trianguloBitmap3 = new TrianguloBitmap(this.imagemDelaunay, trianguloBitmap.getP1(), trianguloBitmap.getP2(), ponto);
            TrianguloBitmap trianguloBitmap4 = new TrianguloBitmap(this.imagemDelaunay, trianguloBitmap.getP2(), trianguloBitmap.getP3(), ponto);
            TrianguloBitmap trianguloBitmap5 = new TrianguloBitmap(this.imagemDelaunay, trianguloBitmap.getP3(), trianguloBitmap.getP1(), ponto);
            copyOnWriteArrayList.add(trianguloBitmap3);
            copyOnWriteArrayList.add(trianguloBitmap4);
            copyOnWriteArrayList.add(trianguloBitmap5);
            this.listaTriangulos.add(trianguloBitmap3);
            this.listaTriangulos.add(trianguloBitmap4);
            this.listaTriangulos.add(trianguloBitmap5);
            this.listaTriangulos.remove(trianguloBitmap);
            trianguloBitmap.clear();
            for (TrianguloBitmap trianguloBitmap6 : this.listaTriangulos) {
                Iterator it2 = copyOnWriteArrayList.iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        break;
                    }
                    trianguloBitmap2 = (TrianguloBitmap) it2.next();
                    if (trianguloBitmap6 != trianguloBitmap2 && trianguloBitmap6.eVizinho(trianguloBitmap2)) {
                        Vertice arestaComum = trianguloBitmap6.getArestaComum(trianguloBitmap2);
                        trianguloBitmap6.getAnguloPontoOpostoAresta(arestaComum);
                        trianguloBitmap2.getAnguloPontoOpostoAresta(arestaComum);
                        Ponto pontoForaVertice = trianguloBitmap6.getPontoForaVertice(arestaComum);
                        if (trianguloBitmap6.pontoNoCircunscrito(trianguloBitmap2.getPontoForaVertice(arestaComum)) || trianguloBitmap2.pontoNoCircunscrito(pontoForaVertice)) {
                            List flipTriangulos = flipTriangulos(trianguloBitmap6, trianguloBitmap2);
                            copyOnWriteArrayList.remove(trianguloBitmap2);
                            copyOnWriteArrayList.addAll(flipTriangulos);
                        }
                    }
                }
                List flipTriangulos2 = flipTriangulos(trianguloBitmap6, trianguloBitmap2);
                copyOnWriteArrayList.remove(trianguloBitmap2);
                copyOnWriteArrayList.addAll(flipTriangulos2);
            }
        }
        StringBuilder outline24 = new StringBuilder();
        outline24.append(this.listaTriangulos.size());
        Log.i("INFO", outline24.toString());
    }


    public void atualizarTriangulos() {
        CopyOnWriteArrayList<Ponto> copyOnWriteArrayList = new CopyOnWriteArrayList<>(this.listaPontos);
        this.listaTriangulos.clear();
        this.listaPontos.clear();
        this.listaTriangulos.addAll(TrianguloBitmap.cutInitialBitmap(this.imagemDelaunay));
        for (Ponto addPonto : copyOnWriteArrayList) {
            addPonto(addPonto);
        }
    }

    public void clear() {
        for (TrianguloBitmap clear : this.listaTriangulos) {
            clear.clear();
        }
        System.gc();
    }

    public void deletePontos(List<Ponto> list) {
        this.listaPontos.removeAll(list);
        atualizarTriangulos();
    }

    public Bitmap getImagemDelaunay() {
        return this.imagemDelaunay;
    }

    public List<Ponto> getListaPontos() {
        return this.listaPontos;
    }

    public List<TrianguloBitmap> getListaTriangulos() {
        return this.listaTriangulos;
    }

    public Bitmap getTriangulosEstaticos(Config config) {
        Bitmap createBitmap = Bitmap.createBitmap(this.imagemDelaunay.getWidth(), this.imagemDelaunay.getHeight(), config);
        Canvas canvas = new Canvas(createBitmap);
        for (TrianguloBitmap trianguloBitmap : getListaTriangulos()) {
            if (trianguloBitmap.isEstatico()) {
                trianguloBitmap.desenhaDistorcao(canvas, config);
            }
        }
        return createBitmap;
    }

    public void reiniciarPontos() {
        for (Ponto ponto : getListaPontos()) {
            if (!ponto.isEstatico()) {
                ponto.setPosicaoAtualAnim(ponto.getXInit(), ponto.getYInit());
            }
        }
    }

    public void setImagemDelaunay(Bitmap bitmap) {
        this.imagemDelaunay = bitmap;
        for (TrianguloBitmap originalImage : this.listaTriangulos) {
            originalImage.setOriginalImage(bitmap);
        }
    }
}
