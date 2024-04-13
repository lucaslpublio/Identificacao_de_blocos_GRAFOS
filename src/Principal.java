import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.Set;

class Grafo {
    private int V; // Numero de vertices
    private ArrayList<ArrayList<Integer>> adj; // Lista de adjacencias

    Grafo(int v) {
        V = v;
        adj = new ArrayList<>(V);
        for (int i = 0; i < V; ++i)
            adj.add(new ArrayList<>());
    }

    // Funcao para adicionar uma aresta ao grafo
    void adicionaAresta(int v, int w) {
        adj.get(v).add(w);
        adj.get(w).add(v);
    }

    // Funcao de utilidade para verificar se existe um ciclo
    // a partir do vertice v
    private void isCicloUtil(int v, boolean[] visitados, int pai, ArrayList<Integer> cicloAtual, HashSet<HashSet<Integer>> ciclos) {
        // Marca o vertice atual como visitado
        visitados[v] = true;
        cicloAtual.add(v);

        // Recurra para todos os vertices adjacentes ao vertice atual
        for (Integer i : adj.get(v)) {
            // Se o vertice adjacente nao foi visitado, entao o visite
            if (!visitados[i]) {
                isCicloUtil(i, visitados, v, cicloAtual, ciclos);
            }
            // Se o vertice adjacente e visitado e nao e o pai do vertice atual,
            // entao ha um ciclo no grafo
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

    // Funcao principal para verificar se o grafo contem um ciclo
    ArrayList<ArrayList<Integer>> encontraCiclos() {
        // Marca todos os vertices como nao visitados
        boolean[] visitados = new boolean[V];
        Arrays.fill(visitados, false);

        // HashSet para armazenar todos os ciclos encontrados
        HashSet<HashSet<Integer>> ciclosSet = new HashSet<>();

        // Lista temporaria para armazenar o ciclo atual sendo explorado
        ArrayList<Integer> cicloAtual = new ArrayList<>();

        // Chama a funcao de utilidade recursiva para encontrar todos os ciclos
        for (int u = 0; u < V; u++)
            if (!visitados[u])
                isCicloUtil(u, visitados, -1, cicloAtual, ciclosSet);

        // Converter HashSet para TreeSet com comparador personalizado para garantir ordenacao pela soma
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

    // Funcao para encontrar a soma dos vertices em um ciclo
    private int somaDoCiclo(ArrayList<Integer> ciclo) {
        int sum = 0;
        for (int vertice : ciclo) {
            sum += vertice;
        }
        return sum;
    }
    
    // Funcao que encontra articulacoes
    void findArticulations() {
        boolean[] visited = new boolean[V];
        int[] disc = new int[V]; // Tempo de descoberta
        int[] low = new int[V]; // Menor tempo de descoberta alcancavel
        int[] parent = new int[V]; // Vertice pai na arvore DFS
        int time = 0;

        Arrays.fill(parent, -1);

        ArrayList<Integer> articulacoes = new ArrayList<>(); // Lista para armazenar as articulações

        for (int i = 0; i < V; i++) {
            if (!visited[i]) {
                dfs(i, visited, disc, low, parent, time, articulacoes); // Chamada recursiva do DFS
            }
        }

        // Impressão das articulações encontradas
        System.out.println("Articulações encontradas:");
        for (int articulacao : articulacoes) {
            System.out.println(articulacao);
        }

        // Verificar e imprimir blocos de articulações adjacentes
        verificarBlocosDeArticulacoes(articulacoes);
    }

    // Método para verificar e imprimir blocos de articulações adjacentes
    void verificarBlocosDeArticulacoes(ArrayList<Integer> articulacoes) {
        for (int i = 0; i < articulacoes.size() - 1; i++) {
            int art1 = articulacoes.get(i);
            for (int j = i + 1; j < articulacoes.size(); j++) {
                int art2 = articulacoes.get(j);
                // Verifica se as articulações são adjacentes
                if (adj.get(art1).contains(art2)) {
                    System.out.println("Bloco de articulações adjacentes encontradas:");
                    System.out.println(art1 + " - " + art2);
                }
            }
        }
    }

    void dfs(int u, boolean[] visited, int[] disc, int[] low, int[] parent, int time, ArrayList<Integer> articulacoes) {
        visited[u] = true;
        disc[u] = low[u] = ++time;

        int children = 0; // Contador de filhos na arvore DFS

        for (int v : adj.get(u)) {
            if (!visited[v]) {
                children++;
                parent[v] = u;
                dfs(v, visited, disc, low, parent, time, articulacoes);

                low[u] = Math.min(low[u], low[v]);

                // Verifica se u é uma articulacao
                if ((parent[u] == -1 && children > 1) || (parent[u] != -1 && low[v] >= disc[u])) {
                    articulacoes.add(u); // Adiciona a articulação encontrada à lista
                }
            } else if (v != parent[u]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }
    }
    
    //Funcao para encontrar blocos a partir dos ciclos
    ArrayList<ArrayList<Integer>> blocos(){
        ArrayList<ArrayList<Integer>> blocos = this.encontraCiclos(); //blocos inicialmente sao iguais aos ciclos
        
        //compara um ciclo ao proximo ciclo em loop unificando-os em blocos caso tenham dois ou mais vertices em comum ate que nao seja mais possivel
        for (int i = 0; i < blocos.size(); i++){
            ArrayList<Integer> bloco = blocos.get(i);
          for (int j = i+1; j < blocos.size(); j++){
              ArrayList<Integer> blocoAux = blocos.get(j);
            Set<Integer> intersecao = new HashSet<>(blocos.get(i));
            intersecao.retainAll(blocos.get(j)); //encontra a intersecao entre os ciclos
            if(intersecao.size() >= 2){
                Set<Integer> uniao = new HashSet<>(blocos.get(i));
                uniao.addAll(blocos.get(j));
                blocos.remove(bloco);
                blocos.remove(blocoAux);
                blocos.add(new ArrayList<>(uniao)); //remove do conjunto de blocos os ciclos sozinhos e adiciona a sua uniao caso tenham mais de dois vertices em comum
            }
         }
       }
       
       return blocos;
    }
    
    // Método para encontrar e imprimir blocos de articulações adjacentes
    void encontraBlocosDeArticulacoes() {
        ArrayList<Integer> articulacoes = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            if (adj.get(i).size() >= 2) {
                articulacoes.add(i);
            }
        }
        
        if (articulacoes.size() == 2) {
            int art1 = articulacoes.get(0);
            int art2 = articulacoes.get(1);
            
            // Verifica se as duas articulações são adjacentes
            if (adj.get(art1).contains(art2)) {
                System.out.println("Blocos de articulações adjacentes encontrados:");
                System.out.println("Articulação 1: " + art1);
                System.out.println("Articulação 2: " + art2);
            }
        }
    }

    // Metodo de teste
    public static void main(String args[]) {
        Grafo g = new Grafo(9);
        g.adicionaAresta(0, 1);
        g.adicionaAresta(1, 2);
        g.adicionaAresta(1, 3);
        g.adicionaAresta(2, 3);
        g.adicionaAresta(3, 0);
        g.adicionaAresta(0, 4);
        g.adicionaAresta(4, 5);
        g.adicionaAresta(5, 6);
        g.adicionaAresta(6, 7);
        g.adicionaAresta(7, 8);
        g.adicionaAresta(8, 5);

        // Encontra e imprime os ciclos e blocos
        ArrayList<ArrayList<Integer>> ciclos = g.encontraCiclos();
        ArrayList<ArrayList<Integer>> blocos = g.blocos();
        if (ciclos.isEmpty()) {
            System.out.println("O grafo nao contem ciclos.");
        } else {
            System.out.println("Ciclos encontrados:");
            for (ArrayList<Integer> ciclo : ciclos) {
                System.out.println(ciclo);
            }
            System.out.println("Blocos encontrados:");
            for (ArrayList<Integer> bloco : blocos) {
                System.out.println(bloco);
            }
        }

        // Encontra e imprime os pontos de articulacao
        g.findArticulations();

        // Encontra e imprime os blocos de articulações adjacentes
        g.encontraBlocosDeArticulacoes();
    }
}
