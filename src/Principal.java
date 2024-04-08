import java.util.*;

class Grafo {
    private int V;
    private ArrayList<Integer>[] adjacente;

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
        
        // Inicializações
        boolean visitado[] = new boolean[V];
        int desc[] = new int[V];
        int baixo[] = new int[V];
        int pai[] = new int[V];
        List<List<Integer>> blocos = new ArrayList<>();
    
        // Marca todos os vértices como não visitados e inicializa os arrays desc e baixo
        for (int i = 0; i < V; ++i) {
            pai[i] = -1; // Nenhum vértice possui pai inicialmente
            visitado[i] = false; // Todos os vértices são marcados como não visitados
        }
    
        // Para cada vértice no grafo
        for (int i = 0; i < V; ++i) {
            // Se o vértice não foi visitado ainda
            if (!visitado[i]) {
                // Cria uma lista para armazenar os vértices do bloco
                List<Integer> bloco = new ArrayList<>();
                // Chama a função DFS para explorar o bloco conectado ao vértice atual
                DFS(i, visitado, -1, baixo, desc, bloco);
                // Adiciona o bloco encontrado à lista de blocos
                blocos.add(bloco);
            }
        }
    
        // Retorna a lista de blocos encontrados
        return blocos;
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
        long endTime = System.nanoTime();

        imprimirBlocos(blocos);
        long duration = (endTime - startTime);
        System.out.println("Tempo de execução: " + duration + " nanossegundos");
    }

    // Método para imprimir os blocos
    public static void imprimirBlocos(List<List<Integer>> blocos) {

        // Loop sobre cada bloco na lista de blocos
        for (int i = 0; i < blocos.size(); i++) {
            // Obtém a lista de vértices do bloco atual
            List<Integer> bloco = blocos.get(i);
        
            // Imprime informações sobre o bloco
            System.out.print("Bloco " + (i + 1) + ": composto");

            // Verifica se o bloco contém apenas um vértice
            if (bloco.size() == 1) {
                // Se sim, imprime uma mensagem indicando que o bloco possui apenas um vértice
                System.out.println(" apenas pelo vértice " + bloco.get(0) + ".");
            } else {
                // Se não, imprime uma mensagem indicando os vértices que compõem o bloco
                System.out.print(" pelos vértices ");
                for (int j = 0; j < bloco.size(); j++) {
                    System.out.print(bloco.get(j));
                    // Se não for o último vértice do bloco, imprime um traço como separador
                    if (j < bloco.size() - 1) {
                        System.out.print(" - ");
                    }
                }
                // Imprime uma nova linha para finalizar a mensagem do bloco
                System.out.println(".");
            }
        }
    }
}