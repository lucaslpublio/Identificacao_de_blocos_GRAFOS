import java.util.*;

class Grafo {
    private int V;
    private ArrayList<Integer>[] adjacente;
    Set<Integer> articulacoes = new HashSet<>();

    Grafo(int v) {
        V = v;
        adjacente = new ArrayList[v];
        for (int i = 0; i < v; ++i)
            adjacente[i] = new ArrayList<Integer>();
    }

    void adicionaAresta(int v, int w) {
        adjacente[v].add(w);
        adjacente[w].add(v);
    }

    void DFS(int u, boolean visitado[], int pai, int baixo[], int desc[], List<Integer> bloco) {
        visitado[u] = true;
        bloco.add(u);

        for (Integer v : adjacente[u]) {
            if (!visitado[v]) {
                DFS(v, visitado, u, baixo, desc, bloco);
            }
        }
    }

    List<List<Integer>> encontrarBlocos() {
        boolean visitado[] = new boolean[V];
        int desc[] = new int[V];
        int baixo[] = new int[V];
        int pai[] = new int[V];
        List<List<Integer>> blocos = new ArrayList<>();

        for (int i = 0; i < V; ++i) {
            pai[i] = -1;
            visitado[i] = false;
        }

        for (int i = 0; i < V; ++i) {
            if (!visitado[i]) {
                List<Integer> bloco = new ArrayList<>();
                DFS(i, visitado, -1, baixo, desc, bloco);
                blocos.add(bloco);
            }
        }

        return blocos;
    }

    void encontrarArticulacoes() {
        boolean visitado[] = new boolean[V];
        int desc[] = new int[V];
        int baixo[] = new int[V];
        int pai[] = new int[V];
        int tempo = 0;

        for (int i = 0; i < V; ++i) {
            pai[i] = -1;
            visitado[i] = false;
        }

        for (int i = 0; i < V; ++i) {
            if (!visitado[i]) {
                encontrarArticulacoesUtil(i, visitado, pai, baixo, desc, tempo);
            }
        }
    }

    void encontrarArticulacoesUtil(int u, boolean visitado[], int pai[], int baixo[], int desc[], int tempo) {
        int filhos = 0;
        visitado[u] = true;
        desc[u] = baixo[u] = ++tempo;

        for (Integer v : adjacente[u]) {
            if (!visitado[v]) {
                filhos++;
                pai[v] = u;
                encontrarArticulacoesUtil(v, visitado, pai, baixo, desc, tempo);

                baixo[u] = Math.min(baixo[u], baixo[v]);

                if (pai[u] == -1 && filhos > 1) {
                    articulacoes.add(u);
                }
                if (pai[u] != -1 && baixo[v] >= desc[u]) {
                    articulacoes.add(u);
                }
            } else if (v != pai[u]) {
                baixo[u] = Math.min(baixo[u], desc[v]);
            }
        }
    }
}

public class Principal {
    public static void main(String args[]) {
        Grafo g = new Grafo(12);
        g.adicionaAresta(0, 1);
        g.adicionaAresta(1, 2);
        g.adicionaAresta(2, 0);
        g.adicionaAresta(1, 3);
        g.adicionaAresta(3, 4);
        g.adicionaAresta(4, 5);
        g.adicionaAresta(5, 3);
        g.adicionaAresta(6, 7);
        g.adicionaAresta(7, 8);
        g.adicionaAresta(8, 6);
        g.adicionaAresta(9, 10);
        g.adicionaAresta(10, 11);
        g.adicionaAresta(11, 9);
        g.adicionaAresta(1, 9);

        long startTime = System.nanoTime();
        List<List<Integer>> blocos = g.encontrarBlocos();
        g.encontrarArticulacoes();
        long endTime = System.nanoTime();

        imprimirBlocos(blocos);
        System.out.println("Articulações encontradas: " + g.articulacoes);
        long duration = (endTime - startTime);
        System.out.println("Tempo de execução: " + duration + " nanossegundos");
    }

    public static void imprimirBlocos(List<List<Integer>> blocos) {
        for (int i = 0; i < blocos.size(); i++) {
            List<Integer> bloco = blocos.get(i);
            System.out.print("Bloco " + (i + 1) + ": composto");

            if (bloco.size() == 1) {
                System.out.println(" apenas pelo vértice " + bloco.get(0) + ".");
            } else {
                System.out.print(" pelos vértices ");
                for (int j = 0; j < bloco.size(); j++) {
                    System.out.print(bloco.get(j));
                    if (j < bloco.size() - 1) {
                        System.out.print(" - ");
                    }
                }
                System.out.println(".");
            }
        }
    }
}
