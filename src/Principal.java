import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Comparator;

class Grafo {
    private int V; // Número de vértices
    private ArrayList<ArrayList<Integer>> adj; // Lista de adjacências

    Grafo(int v) {
        V = v;
        adj = new ArrayList<>(V);
        for (int i = 0; i < V; ++i)
            adj.add(new ArrayList<>());
    }

    // Função para adicionar uma aresta ao grafo
    void adicionaAresta(int v, int w) {
        adj.get(v).add(w);
        adj.get(w).add(v);
    }

    // Função de utilidade para verificar se existe um ciclo
    // a partir do vértice v
    private void isCicloUtil(int v, boolean[] visitados, int pai, ArrayList<Integer> cicloAtual, HashSet<HashSet<Integer>> ciclos) {
        // Marca o vértice atual como visitado
        visitados[v] = true;
        cicloAtual.add(v);

        // Recurra para todos os vértices adjacentes ao vértice atual
        for (Integer i : adj.get(v)) {
            // Se o vértice adjacente não foi visitado, então o visite
            if (!visitados[i]) {
                isCicloUtil(i, visitados, v, cicloAtual, ciclos);
            }
            // Se o vértice adjacente é visitado e não é o pai do vértice atual,
            // então há um ciclo no grafo
            else if (i != pai) {
                // Encontrou um ciclo, adiciona ao HashSet de ciclos
                ArrayList<Integer> ciclo = new ArrayList<>(cicloAtual.subList(cicloAtual.indexOf(i), cicloAtual.size()));
                ciclo.add(v);
                ciclos.add(new HashSet<>(ciclo));
            }
        }

        cicloAtual.remove(cicloAtual.size() - 1);
        visitados[v] = false;
    }

    // Função principal para verificar se o grafo contém um ciclo
    ArrayList<ArrayList<Integer>> encontraCiclos() {
        // Marca todos os vértices como não visitados
        boolean[] visitados = new boolean[V];
        Arrays.fill(visitados, false);

        // HashSet para armazenar todos os ciclos encontrados
        HashSet<HashSet<Integer>> ciclosSet = new HashSet<>();

        // Lista temporária para armazenar o ciclo atual sendo explorado
        ArrayList<Integer> cicloAtual = new ArrayList<>();

        // Chama a função de utilidade recursiva para encontrar todos os ciclos
        for (int u = 0; u < V; u++)
            if (!visitados[u])
                isCicloUtil(u, visitados, -1, cicloAtual, ciclosSet);

        // Converter HashSet para TreeSet com comparador personalizado para garantir ordenação pela soma
        TreeSet<ArrayList<Integer>> ciclosOrdenados = new TreeSet<>(new Comparator<ArrayList<Integer>>() {
            @Override
            public int compare(ArrayList<Integer> ciclo1, ArrayList<Integer> ciclo2) {
                int sum1 = somaDoCiclo(ciclo1);
                int sum2 = somaDoCiclo(ciclo2);
                return Integer.compare(sum1, sum2);
            }
        });
        for (HashSet<Integer> cicloSet : ciclosSet) {
            ArrayList<Integer> ciclo = new ArrayList<>(cicloSet);
            ciclosOrdenados.add(ciclo);
        }

        // Converter TreeSet para ArrayList
        ArrayList<ArrayList<Integer>> ciclos = new ArrayList<>(ciclosOrdenados);

        return ciclos;
    }

    // Função para encontrar a soma dos vértices em um ciclo
    private int somaDoCiclo(ArrayList<Integer> ciclo) {
        int sum = 0;
        for (int vertice : ciclo) {
            sum += vertice;
        }
        return sum;
    }

    // Método de teste
    public static void main(String args[]) {
        Grafo g = new Grafo(7);
        g.adicionaAresta(0, 1);
        g.adicionaAresta(0, 2);
        g.adicionaAresta(0, 6);
        g.adicionaAresta(0, 5);
        g.adicionaAresta(0, 3);
        g.adicionaAresta(0, 4);
        g.adicionaAresta(1, 2);
        g.adicionaAresta(5, 6);
        g.adicionaAresta(3, 4);

        ArrayList<ArrayList<Integer>> ciclos = g.encontraCiclos();
        if (ciclos.isEmpty()) {
            System.out.println("O grafo não contém ciclos.");
        } else {
            System.out.println("Blocos encontrados:");
            for (ArrayList<Integer> ciclo : ciclos) {
                System.out.println(ciclo);
            }
        }
    }
}
